from sqlalchemy import Column, Integer, String, Text
from database import Base

class ContentItem(Base):
    __tablename__ = "content_items"
    id = Column(Integer, primary_key=True, index=True)
    imdb_id = Column(String, unique=True, index=True)
    title = Column(String, index=True)
    year = Column(String)
    type = Column(String)  # "movie" ou "series"
    image = Column(String)
    crew = Column(String)
    rating = Column(String)
    description = Column(Text)
