@echo off
REM Script de lancement du backend MaâtTV
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8001
pause
