import os
import requests
from sqlalchemy.orm import Session
from models import ContentItem
from dotenv import load_dotenv

# Charger la clé API depuis le fichier .env
load_dotenv()
IMDB_API_KEY = os.getenv("IMDB_API_KEY")

def import_top_movies(db: Session):
    if not IMDB_API_KEY:
        raise ValueError("La clé IMDB_API_KEY n'est pas définie dans le fichier .env")
    
    # Récupérer plusieurs pages de résultats
    all_movies = []
    total_pages = 3  # Importer 3 pages (environ 30 films)
    
    for page in range(1, total_pages + 1):
        search_url = f"http://www.omdbapi.com/?s=action&type=movie&page={page}&apikey={IMDB_API_KEY}"
        search_response = requests.get(search_url)
        search_data = search_response.json()
        
        if search_data.get("Response") != "True":
            print(f"Erreur page {page}: {search_data.get('Error', 'Unknown error')}")
            continue
            
        page_movies = search_data.get("Search", [])
        print(f"Page {page}: {len(page_movies)} films trouvés")
        all_movies.extend(page_movies)
    
    if not all_movies:
        print("Aucun film trouvé")
        return
    
    # Importer les films
    print(f"Total films à importer: {len(all_movies)}")
    
    for movie in all_movies:
        imdb_id = movie.get("imdbID")
        if not imdb_id:
            print("Film sans ID IMDB, ignoré")
            continue
            
        # Vérifier si le film existe déjà
        if db.query(ContentItem).filter_by(imdb_id=imdb_id).first():
            print(f"Film {imdb_id} existe déjà, ignoré")
            continue
            
        # Vérifier si le film existe déjà
        if db.query(ContentItem).filter_by(imdb_id=imdb_id).first():
            continue
            
        # Obtenir les détails complets du film
        detail_url = f"http://www.omdbapi.com/?i={imdb_id}&apikey={IMDB_API_KEY}"
        detail_response = requests.get(detail_url)
        detail_data = detail_response.json()
        
        if detail_data.get("Response") != "True":
            continue
            
        db_item = ContentItem(
            imdb_id=detail_data["imdbID"],
            title=detail_data["Title"],
            year=detail_data["Year"],
            type="movie",
            image=detail_data["Poster"],
            crew=detail_data["Director"],
            rating=detail_data["imdbRating"],
            description=detail_data["Plot"]
        )
        db.add(db_item)
    
    db.commit()
