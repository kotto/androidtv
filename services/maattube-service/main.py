from fastapi import FastAPI, HTTPException, Depends
from sqlalchemy import create_engine, Column, Integer, String, Text, DateTime
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from pydantic import BaseModel
from datetime import datetime
from typing import List, Optional
import uvicorn

# Configuration de la base de données
SQLALCHEMY_DATABASE_URL = "sqlite:///./maattube.db"
engine = create_engine(SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# Modèle de base de données
class Video(Base):
    __tablename__ = "videos"
    
    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, index=True)
    description = Column(Text)
    channel = Column(String)
    thumbnail_url = Column(String)
    video_url = Column(String)
    duration = Column(Integer)  # en secondes
    views = Column(Integer, default=0)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

# Créer les tables
Base.metadata.create_all(bind=engine)

# Modèles Pydantic
class VideoBase(BaseModel):
    title: str
    description: Optional[str] = None
    channel: str
    thumbnail_url: Optional[str] = None
    video_url: str
    duration: Optional[int] = None

class VideoCreate(VideoBase):
    pass

class VideoResponse(VideoBase):
    id: int
    views: int
    created_at: datetime
    updated_at: datetime
    
    class Config:
        from_attributes = True

# Application FastAPI
app = FastAPI(title="MaâtTube Service", version="1.0.0")

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
    return {"status": "healthy", "service": "maattube"}

@app.get("/videos", response_model=List[VideoResponse])
def get_videos(skip: int = 0, limit: int = 20, db: Session = Depends(get_db)):
    """Récupérer la liste des vidéos"""
    videos = db.query(Video).offset(skip).limit(limit).all()
    return videos

@app.get("/videos/{video_id}", response_model=VideoResponse)
def get_video(video_id: int, db: Session = Depends(get_db)):
    """Récupérer une vidéo par son ID"""
    video = db.query(Video).filter(Video.id == video_id).first()
    if not video:
        raise HTTPException(status_code=404, detail="Vidéo non trouvée")
    
    # Incrémenter le nombre de vues
    video.views += 1
    db.commit()
    db.refresh(video)
    
    return video

@app.post("/videos", response_model=VideoResponse)
def create_video(video: VideoCreate, db: Session = Depends(get_db)):
    """Créer une nouvelle vidéo"""
    db_video = Video(**video.dict())
    db.add(db_video)
    db.commit()
    db.refresh(db_video)
    return db_video

@app.delete("/videos/{video_id}")
def delete_video(video_id: int, db: Session = Depends(get_db)):
    """Supprimer une vidéo"""
    video = db.query(Video).filter(Video.id == video_id).first()
    if not video:
        raise HTTPException(status_code=404, detail="Vidéo non trouvée")
    
    db.delete(video)
    db.commit()
    return {"message": "Vidéo supprimée avec succès"}

@app.get("/videos/search/{query}")
def search_videos(query: str, db: Session = Depends(get_db)):
    """Rechercher des vidéos par titre ou description"""
    videos = db.query(Video).filter(
        Video.title.contains(query) | Video.description.contains(query)
    ).all()
    return videos

# Données de test
@app.post("/videos/seed")
def seed_videos(db: Session = Depends(get_db)):
    """Ajouter des vidéos de test"""
    test_videos = [
        {
            "title": "Les Secrets de l'Afrique Ancienne",
            "description": "Documentaire sur les civilisations africaines anciennes",
            "channel": "HistoireTV",
            "thumbnail_url": "https://example.com/thumb1.jpg",
            "video_url": "https://example.com/video1.mp4",
            "duration": 3600
        },
        {
            "title": "Concert Exclusif Burna Boy",
            "description": "Concert live de Burna Boy à Lagos",
            "channel": "MaatMusic",
            "thumbnail_url": "https://example.com/thumb2.jpg",
            "video_url": "https://example.com/video2.mp4",
            "duration": 5400
        },
        {
            "title": "Cuisine Africaine Moderne",
            "description": "Recettes traditionnelles revisitées",
            "channel": "MaatCuisine",
            "thumbnail_url": "https://example.com/thumb3.jpg",
            "video_url": "https://example.com/video3.mp4",
            "duration": 1800
        }
    ]
    
    for video_data in test_videos:
        # Vérifier si la vidéo existe déjà
        existing = db.query(Video).filter(Video.title == video_data["title"]).first()
        if not existing:
            db_video = Video(**video_data)
            db.add(db_video)
    
    db.commit()
    return {"message": "Vidéos de test ajoutées"}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8003)