from sqlalchemy.orm import Session
from models import ContentItem

def get_content(db: Session, skip: int = 0, limit: int = 20):
    return db.query(ContentItem).offset(skip).limit(limit).all()

def get_content_by_id(db: Session, item_id: int):
    return db.query(ContentItem).filter(ContentItem.id == item_id).first()

def get_content_by_imdb(db: Session, imdb_id: str):
    return db.query(ContentItem).filter(ContentItem.imdb_id == imdb_id).first()

def create_content(db: Session, item: dict):
    db_item = ContentItem(**item)
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item
