from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from database import SessionLocal, engine, Base
import models, crud, imdb_importer
from pydantic import BaseModel  # Ajout de Pydantic pour les modèles de réponse

Base.metadata.create_all(bind=engine)

app = FastAPI()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# Modèle Pydantic pour la réponse
class ContentItemResponse(BaseModel):
    id: int
    title: str
    imageUrl: str  # Mappé depuis le champ 'image' du modèle SQLAlchemy
    year: str
    type: str
    crew: str
    rating: str
    description: str

@app.get("/tv/vod/list", response_model=list[ContentItemResponse])
def list_content(skip: int = 0, limit: int = 20, db: Session = Depends(get_db)):
    items = crud.get_content(db, skip=skip, limit=limit)
    return [ContentItemResponse(
        id=item.id,
        title=item.title,
        imageUrl=item.image,  # Mappe le champ 'image' vers 'imageUrl'
        year=item.year,
        type=item.type,
        crew=item.crew,
        rating=item.rating,
        description=item.description
    ) for item in items]

@app.get("/tv/vod/item/{item_id}", response_model=ContentItemResponse)
def get_content(item_id: int, db: Session = Depends(get_db)):
    item = crud.get_content_by_id(db, item_id)
    if not item:
        raise HTTPException(status_code=404, detail="Not found")
    return ContentItemResponse(
        id=item.id,
        title=item.title,
        imageUrl=item.image,  # Mappe le champ 'image' vers 'imageUrl'
        year=item.year,
        type=item.type,
        crew=item.crew,
        rating=item.rating,
        description=item.description
    )

@app.post("/tv/vod/import")
def import_content(db: Session = Depends(get_db)):
    imdb_importer.import_top_movies(db)
    return {"status": "imported"}

@app.get("/tv/api-key/imdb")
def get_imdb_api_key():
    api_key = imdb_importer.IMDB_API_KEY
    if not api_key:
        raise HTTPException(status_code=404, detail="IMDB API key not found")
    return {"api_key": api_key}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8002)
