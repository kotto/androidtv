@echo off
REM Script de lancement du backend MaâtTube
echo Installation des dépendances...
pip install -r requirements.txt
echo Démarrage du serveur MaâtTube...
uvicorn main:app --host 0.0.0.0 --port 8003 --reload
pause