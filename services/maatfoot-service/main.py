from fastapi import FastAPI, HTTPException, Depends
from sqlalchemy import create_engine, Column, Integer, String, Text, DateTime, Boolean
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from pydantic import BaseModel
from datetime import datetime
from typing import List, Optional
import uvicorn

# Configuration de la base de données
SQLALCHEMY_DATABASE_URL = "sqlite:///./maatfoot.db"
engine = create_engine(SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# Modèle de base de données pour les matchs
class Match(Base):
    __tablename__ = "matches"
    
    id = Column(Integer, primary_key=True, index=True)
    home_team = Column(String, index=True)
    away_team = Column(String, index=True)
    home_score = Column(Integer, default=0)
    away_score = Column(Integer, default=0)
    match_date = Column(DateTime)
    status = Column(String, default="scheduled")  # scheduled, live, finished
    league = Column(String)
    stadium = Column(String)
    is_live = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

# Créer les tables
Base.metadata.create_all(bind=engine)

# Modèles Pydantic
class MatchDto(BaseModel):
    id: int
    home_team: str
    away_team: str
    home_score: int
    away_score: int
    match_date: datetime
    status: str
    league: str
    stadium: Optional[str] = None
    is_live: bool
    
    class Config:
        from_attributes = True

class MatchCreate(BaseModel):
    home_team: str
    away_team: str
    home_score: Optional[int] = 0
    away_score: Optional[int] = 0
    match_date: datetime
    status: Optional[str] = "scheduled"
    league: str
    stadium: Optional[str] = None
    is_live: Optional[bool] = False

# Application FastAPI
app = FastAPI(title="MaâtFoot Service", version="1.0.0")

# Dépendance pour obtenir la session de base de données
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# Endpoints
@app.get("/health")
def health_check():
    return {"status": "healthy", "service": "maatfoot"}

@app.get("/maqfoot/matches", response_model=List[MatchDto])
def get_matches(skip: int = 0, limit: int = 20, db: Session = Depends(get_db)):
    """Récupérer la liste des matchs (endpoint utilisé par l'app Android)"""
    matches = db.query(Match).offset(skip).limit(limit).all()
    return matches

@app.get("/matches", response_model=List[MatchDto])
def get_all_matches(skip: int = 0, limit: int = 20, db: Session = Depends(get_db)):
    """Récupérer la liste des matchs (endpoint alternatif)"""
    matches = db.query(Match).offset(skip).limit(limit).all()
    return matches

@app.get("/matches/{match_id}", response_model=MatchDto)
def get_match(match_id: int, db: Session = Depends(get_db)):
    """Récupérer un match par son ID"""
    match = db.query(Match).filter(Match.id == match_id).first()
    if not match:
        raise HTTPException(status_code=404, detail="Match non trouvé")
    return match

@app.get("/matches/live", response_model=List[MatchDto])
def get_live_matches(db: Session = Depends(get_db)):
    """Récupérer les matchs en direct"""
    matches = db.query(Match).filter(Match.is_live == True).all()
    return matches

@app.get("/matches/league/{league_name}", response_model=List[MatchDto])
def get_matches_by_league(league_name: str, db: Session = Depends(get_db)):
    """Récupérer les matchs par championnat"""
    matches = db.query(Match).filter(Match.league == league_name).all()
    return matches

@app.post("/matches", response_model=MatchDto)
def create_match(match: MatchCreate, db: Session = Depends(get_db)):
    """Créer un nouveau match"""
    db_match = Match(**match.dict())
    db.add(db_match)
    db.commit()
    db.refresh(db_match)
    return db_match

@app.put("/matches/{match_id}", response_model=MatchDto)
def update_match(match_id: int, match_update: MatchCreate, db: Session = Depends(get_db)):
    """Mettre à jour un match"""
    match = db.query(Match).filter(Match.id == match_id).first()
    if not match:
        raise HTTPException(status_code=404, detail="Match non trouvé")
    
    for key, value in match_update.dict().items():
        setattr(match, key, value)
    
    match.updated_at = datetime.utcnow()
    db.commit()
    db.refresh(match)
    return match

@app.delete("/matches/{match_id}")
def delete_match(match_id: int, db: Session = Depends(get_db)):
    """Supprimer un match"""
    match = db.query(Match).filter(Match.id == match_id).first()
    if not match:
        raise HTTPException(status_code=404, detail="Match non trouvé")
    
    db.delete(match)
    db.commit()
    return {"message": "Match supprimé avec succès"}

# Données de test
@app.post("/matches/seed")
def seed_matches(db: Session = Depends(get_db)):
    """Ajouter des matchs de test"""
    test_matches = [
        {
            "home_team": "Sénégal",
            "away_team": "Nigeria",
            "home_score": 2,
            "away_score": 1,
            "match_date": datetime(2024, 1, 15, 20, 0),
            "status": "finished",
            "league": "CAN 2024",
            "stadium": "Stade Abdoulaye Wade",
            "is_live": False
        },
        {
            "home_team": "Maroc",
            "away_team": "Côte d'Ivoire",
            "home_score": 0,
            "away_score": 0,
            "match_date": datetime(2024, 1, 20, 18, 0),
            "status": "live",
            "league": "CAN 2024",
            "stadium": "Stade Olympique d'Ebimpé",
            "is_live": True
        },
        {
            "home_team": "Cameroun",
            "away_team": "Ghana",
            "home_score": 0,
            "away_score": 0,
            "match_date": datetime(2024, 1, 25, 21, 0),
            "status": "scheduled",
            "league": "CAN 2024",
            "stadium": "Stade de Yamoussoukro",
            "is_live": False
        },
        {
            "home_team": "Al Ahly",
            "away_team": "Wydad Casablanca",
            "home_score": 1,
            "away_score": 0,
            "match_date": datetime(2024, 1, 18, 19, 0),
            "status": "finished",
            "league": "Ligue des Champions CAF",
            "stadium": "Stade International du Caire",
            "is_live": False
        }
    ]
    
    for match_data in test_matches:
        # Vérifier si le match existe déjà
        existing = db.query(Match).filter(
            Match.home_team == match_data["home_team"],
            Match.away_team == match_data["away_team"],
            Match.match_date == match_data["match_date"]
        ).first()
        if not existing:
            db_match = Match(**match_data)
            db.add(db_match)
    
    db.commit()
    return {"message": "Matchs de test ajoutés"}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8004)