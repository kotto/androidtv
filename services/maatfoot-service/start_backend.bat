@echo off
REM Script de lancement du backend MaâtFoot
echo Installation des dépendances...
pip install -r requirements.txt
echo Démarrage du serveur MaâtFoot...
uvicorn main:app --host 0.0.0.0 --port 8004 --reload
pause