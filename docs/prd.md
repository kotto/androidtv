MaâtCore Product Requirements Document (PRD) - V1.1 (Affiné et Finalisé)
(Date: 27 mai 2025)

1. Goal, Objective and Context (But, Objectif et Contexte)
Goal (But) : Lancer MaâtCore, un écosystème numérique intégré et innovant, pour transformer l'accès à l'information, au divertissement, à la santé et à l'éducation, initialement en Afrique avec une vision globale.
Objective (Objectif) : Atteindre une adoption significative dès la première année (1M abonnés TV, 100k Care, 100k Class) en offrant des services fiables, pertinents et engageants via une interface intuitive "Netflix-like" enrichie par l'IA et la 3D.
Context (Contexte) : MaâtCore répond à un besoin critique de contenu localisé, d'informations fiables, d'accès facilité aux soins et à l'éducation, et d'une plateforme pour les créateurs, le tout dans un environnement numérique où l'innovation technologique et l'expérience utilisateur ludique sont des leviers clés d'adoption.
2. Functional Requirements (MVP) (Exigences Fonctionnelles pour le MVP)
2.1. Maât.TV - Navigation & Découverte :
[ ] L'utilisateur doit pouvoir naviguer dans les contenus (chaînes, VOD) via une interface visuelle de type "Netflix".
[ ] L'utilisateur doit pouvoir rechercher des contenus (films, séries, etc.) en utilisant une fonction de recherche vocale basée sur l'IA.
[ ] L'utilisateur doit pouvoir parcourir des catégories (films, séries, docs, musique, sport, info).
2.2. Maât.TV - VOD (Films, Séries, Docs, Histoire) :
[ ] L'utilisateur doit pouvoir sélectionner un contenu VOD pour voir sa fiche descriptive.
[ ] L'utilisateur doit pouvoir lancer la lecture d'un contenu VOD.
[ ] L'utilisateur doit pouvoir mettre en pause, reprendre la lecture, avancer et reculer rapidement (contrôles de lecture MVP minimum).
2.3. Maât.TV - Chaînes Thématiques Interactives :
Info : L'utilisateur doit pouvoir visionner la chaîne d'information où un avatar IA lit les actualités, avec visuels de studio dynamiques, écran 3D pour vidéos, et playlist de news déployable. (Comparaison de sources détaillée Post-MVP).
Musique : L'utilisateur doit pouvoir sélectionner des styles musicaux (focus Afrique). Un flux audio démarre. L'interface affiche infos piste et avatar IA DJ. Interactions MVP avec avatar DJ : boutons "Piste Suivante/Précédente/J'aime" ; commandes vocales "Quel titre/artiste ?", "Piste suivante/précédente", "J'aime cette chanson".
Histoire : L'utilisateur voit un avatar IA narrateur présenter des segments d'histoire avec illustrations/visuels synchronisés. Comparaison de sources/narrations (MVP simple avec avatar désignant les sources).
2.4. Maât.TV - Sport (MaâtFoot) :
[ ] L'utilisateur doit pouvoir écouter des commentaires audio de matchs en direct, incarnés par un avatar IA expressif dans un studio thématisé "MaâtFoot", avec affichage dynamique du scoreboard, stats MVP (possession, tirs cadrés), fil d'événements discret, et ambiance lumineuse dynamique.
[ ] L'utilisateur doit pouvoir accéder à des analyses 3D post-matchs (vue schématique 3D sur écran du studio, avatar analyste, contrôles simples Play/Pause/Rejouer, angle de caméra unique pour MVP).
[ ] L'utilisateur doit pouvoir rejoindre un espace de discussion social virtuel (panneau de chat latéral/inférieur semi-transparent) pour commenter le sport, avec interaction de l'avatar commentateur.
2.5. MaâtCare - Parcours de Consultation :
[ ] L'utilisateur doit pouvoir initier une session en décrivant ses symptômes via reconnaissance vocale.
[ ] L'avatar médecin IA pose des questions d'orientation basées sur les inputs.
[ ] L'avatar IA propose une mise en relation avec un médecin réel si nécessaire (ou si demandé par l'utilisateur). L'utilisateur peut accepter/refuser.
[ ] Si accepté, l'utilisateur est connecté à un médecin réel via appel audio/vidéo (vidéo utilisateur activable MVP), et reçoit un résumé textuel des recommandations du médecin à la fin (MVP).
2.6. MaâtCare - "Soigner par les Plantes" :
[ ] Accès à une section informative sur les plantes (celles ayant fait l'objet d'études PubMed pour crédibilité).
[ ] Recherche par nom de plante et par symptôme de bien-être (MVP).
[ ] Fiches plantes avec nom, image, usages bien-être, précautions, avertissement légal, et 1-2 liens PubMed (MVP). Lecture par avatar.
2.7. MaâtCare - Paramètres Vitaux :
[ ] Collecte automatique via Bluetooth pour tensiomètre et oxymètre (fournis dans un kit).
[ ] Saisie déclarative pour autres paramètres (ex: température) ou en cas d'échec Bluetooth.
[ ] Affichage clair de la source des données (auto vs. déclarée).
2.8. MaâtClass - Sélection & Visionnage de Cours :
[ ] L'utilisateur peut filtrer les cours par Localisation, Niveau Scolaire, et Matière.
[ ] L'utilisateur peut visionner des leçons où un avatar IA présente le contenu avec illustrations/animations 2D/3D simples synchronisées.
[ ] Contrôles de lecture (Play/Pause, barre de progression) et navigation "Leçon Précédente/Suivante".
2.9. MaâtClass - Suivi de Progression :
[ ] Suivi de progression basique "vu/non vu" par leçon/module.
[ ] Indicateur de progression global par cours.
[ ] Proposition de "Reprendre la lecture" à la dernière leçon non terminée.
2.10. MaâtTube - Pour les Créateurs (MVP Essentiel) :
[ ] Inscription créateur & création de chaîne MaâtTube.
[ ] Téléversement vidéos (formats courants) avec titre, description, miniature, tags.
[ ] Visualisation et suppression de ses propres vidéos.
2.11. MaâtTube - Pour les Spectateurs :
[ ] Navigation/découverte via interface "Netflix-like" et recherche IA.
[ ] Lecture vidéos (contrôles standard).
[ ] Affichage nombre de vues. (Likes, Commentaires, Abonnements sont Post-MVP).
2.12. Plateforme Cœur (Fondation - Epic 1) :
[ ] Interface utilisateur "Netflix-like" cohérente.
[ ] Recherche IA (vocale/texte) unifiée ou contextuelle.
[ ] Gestion des profils utilisateurs (Inscription/Connexion facilitée via smartphone pour TV ; profil minimaliste avec e-mail/pseudo ; déconnexion).
2.13. MVP Validation Strategy and Learning Objectives :
Mécanismes de Feedback Utilisateur (MVP) : Enquêtes pop-up post-utilisation ; point de contact clair (e-mail/formulaire).
Objectifs d'Apprentissage Clés du MVP : Valider engagement "audio-avatar" sport ; évaluer attraction MaâtTube pour créateurs ; (Suggéré) évaluer acceptation avatars IA pour MaâtCare ; (Suggéré) déterminer efficacité perçue cours MaâtClass.
Critères pour Évoluer au-delà du MVP (Phase 2) : Atteinte de 100 000 abonnés actifs MaâtCore ; niveau de satisfaction utilisateur élevé (NPS cible à définir) et retours qualitatifs positifs.
3. Non Functional Requirements (MVP) (Exigences Non Fonctionnelles pour le MVP)
3.1. Performance : Haute réactivité ("Netflix-like") ; démarrage vidéo < 3s ; latence minimisée pour direct audio.
3.2. Scalability : Supporte 50 000 utilisateurs simultanés (An 1) ; architecture pour montée en charge rapide.
3.3. Security :
Bonnes pratiques web standard (HTTPS, etc.).
Conformité MaâtCare : Conception et opération en stricte conformité avec réglementations données de santé applicables (territoires Afrique Francophone/Anglophone ciblés) et standards internationaux. Analyse de conformité détaillée requise.
Tests de Sécurité : Scans de vulnérabilités automatisés (CI/CD) ; Pentests ciblés (MaâtCare, AuthN) avant lancement MVP.
Politique de Confidentialité : Détaillée, expliquant collecte, usage, protection, droits des données ; accessible avant lancement.
Consentement Utilisateur : Mécanismes de consentement explicite pour collecte/usage données personnelles (surtout MaâtCare et amélioration IA).
Dispositif anti-copie de base (TBD).
3.4. Availability / Reliability :
Taux de disponibilité visé : 99%.
Fiabilité des sources d'information pour Maât.TV News.
Objectif de Temps de Récupération (RTO) maximal de 24 heures et Objectif de Point de Récupération (RPO) maximal de 1 heure en cas d'incident majeur.
3.5. Accessibility : Prévue Post-MVP.
3.6. Internationalization/Localization (MVP) : Supporte le Français et l'Anglais (interfaces et contenus de base).
3.7. Monitoring & Alerting (MVP) : Temps de réponse services clés monitorés ; alertes critiques pour défaillance services essentiels (MaâtCare) ou dégradation performances.
4. User Interaction and Design Goals (Objectifs d'Interaction Utilisateur et de Design)
4.1. Philosophie d'Interface : Netflix-like, fluide, moderne, colorée, vibrante, touche africaine.
4.2. Aspect Ludique (MVP) : Transitions animées, micro-animations, design sonore qualitatif.
4.3. Rôle des Avatars : Point d'entrée visuel principal (Care, Class, Info, Sport, Musique), animés (IA), expressifs, incarnent l'aspect humain.
4.4. Objectif "Wow" : Première expérience utilisateur mémorable soulignant l'innovation.
4.5. Gestion des Erreurs (Expérience Utilisateur MVP) : Messages simples, clairs, rassurants, sans jargon technique. (Détails par Design Architect).
5. Technical Assumptions (Hypothèses Techniques)
5.1. Délais & Budget : Forte exigence de mise sur le marché rapide (MVP) ; pas de contrainte budgétaire principale.
5.2. Plateforme & Architecture : Cloud AWS ; approche Microservices ; dépôt Monorepo.
5.3. Technologies Clés (Préférences & Options) :
Moteurs 3D/Avatars : Évaluer Unreal Engine, Heygen, et technologies Google. Avatars à rendu humain réaliste (style HeyGen) sont une exigence MVP.
Développement Android TV : Kotlin et Jetpack Compose privilégiés (exploiter composants TV spécifiques).
L'équipe technique proposera les autres technologies.
5.4. Development Philosophy & Guiding Principles :
Choix Technologiques : Privilégier les solutions open-source matures et largement supportées.
Gestion de la Dette Technique (MVP) : Lancement rapide prioritaire ; dette technique modérée acceptée pour MVP, avec plan de résolution Post-MVP.
5.5. Development Environment (MVP) :
Script d'installation clair pour environnement local.
IDE standardisé : Visual Studio Code (VSCode).
Encouragement à l'utilisation de générateurs de code pertinents.
L'équipe technique gère la compatibilité des versions.
L'environnement local devra permettre de lancer/tester les microservices.
6. Epic Overview (Vue d'ensemble des Epics)
(Les User Stories détaillées pour chaque Epic (1 à 5) ont été définies et validées dans nos échanges précédents et sont considérées comme "Draft Approuvé" et prêtes pour développement. Elles incluent les ACs, Tâches, et Directives Techniques pour chaque story.)

Epic 1 : Fondation MaâtCore & Profil Utilisateur
Goal : Structure technique, UI coquille, inscription/connexion (smartphone), profil minimaliste.
Stories 1.1 à 1.4
Epic 2 : Maât.TV - Expérience de Visionnage MVP
Goal : Naviguer, rechercher, visionner VOD et chaînes thématiques (Info, Musique, Histoire) via interface "Netflix-like" et recherche vocale.
Stories 2.1 à 2.5
Epic 3 : Maât.TV - Expérience Sport MVP
Goal : Offrir expérience unique (commentaire audio live avatar, analyses 3D, espace social).
Stories 3.1 à 3.3
Epic 4 : MaâtCare - Consultation Initiale MVP
Goal : Pré-consultation vocale avatar IA, handoff médecin réel, infos médecine plantes, collecte paramètres vitaux (Bluetooth pour tensiomètre/oxymètre, déclaratif pour autres).
Stories 4.1 à 4.6
Epic 5 : MaâtClass - Accès aux Cours MVP
Goal : Trouver, sélectionner, suivre cours (programmes scolaires officiels) par avatars avec illustrations, suivi progression basique.
Stories 5.1 à 5.3
7. Key Reference Documents (Documents de Référence Clés)
Project Brief MaâtCore V1
MaâtCore Architecture Document V1.1 (Affiné, avec détails Maât.TV)
MaâtCore UI/UX Specification V1.1 (Conceptual, avec détails Maât.TV) (Cette section sera complétée avec d'autres documents techniques et de design détaillés au fur et à mesure de leur production.)
8. Out of Scope Ideas Post MVP (Idées hors périmètre pour l'après-MVP)
Maât.TV Ultra-Immersif : Watch Parties 3D, Fictions Interactives IA, Documentaires Explorables, Chaînes Personnalisées IA.
MaâtCare Proactif : Intégration Objets Connectés (au-delà tensiomètre/oxymètre MVP, haute priorité post-MVP), Bien-être Mental, Parcours Spécialisés, Pharmacie Virtuelle. Pistes d'audit immuables et détaillées pour MaâtCare, mécanismes d'anonymisation/pseudonymisation robustes pour IA avec données MaâtCare.
MaâtClass Continu & Collaboratif : Formation Pro, Salles Virtuelles, Apprentissage Adaptatif, Certifications. Reprise de lecture à un point précis d'une leçon. Approche Socratique d'apprentissage.
MaâtTube Pro & Social : Outils de Création 3D, Monétisation Avancée, Live Streaming, Commentaires, Likes, Abonnements.
Nouveaux Services d'Écosystème : MaâtSapp (Visio TV familiale), MaâtCash (Transfert d'argent).
Transverse & Écosystème (suite) : Couche Sociale Approfondie, Marketplace, VR/AR, Assistant Personnel IA.
Fonctionnalités TV/VOD Avancées : Tri et filtres avancés pour catalogues, bandes-annonces sur fiches détail, gestion de "Ma Liste / Mes Favoris".
Sécurité & Conformité Avancées : Chiffrement systématique de toutes les sauvegardes de données (au-delà de MaâtCare), chiffrement au repos par défaut de toutes les données persistantes, pistes d'audit généralisées pour toutes actions critiques (hors MaâtCare).
Lecteur VOD (Overlay) : Aperçu par miniature vidéo pendant le scrubbing, contrôle de volume in-player, accès direct aux options de langues/sous-titres.
9. Change Log (Journal des Modifications)
Date	Version	Description	Auteur
2025-05-27	V1.1 (Affiné)	Version initiale post-affinage PM Checklist et finalisation User Stories pour tous les Epics MVP. Ajout sections 2.13 et 10 (Support).	John (PM) / Sarah (PO) / BMad (Orchest.)

Export to Sheets
Checklist Results Report (Rapport des Résultats du Checklist PM) - Statut Révisé
Final Decision (Décision Finale) - Révisée
READY FOR ARCHITECT & DESIGN ARCHITECT REVIEW / DEVELOPMENT PREPARATION (PRÊT POUR REVUE ARCHITECTE & DESIGN ARCHITECT / PRÉPARATION AU DÉVELOPPELENT)

Le PRD MaâtCore V1.1, suite aux affinages et à la complétion de toutes les User Stories pour les 5 Epics MVP, est maintenant considéré comme une base solide et complète. Les déficiences initialement identifiées par le pm-checklist ont été adressées au niveau des politiques, des exigences, et de la stratégie MVP. Ce document est prêt à être utilisé par l'Architecte (Fred) et la Design Architect (Jane) pour leurs travaux de conception détaillée et par le Product Owner (Sarah) pour la gestion du backlog en vue du développement.

Prompt for Design Architect (UI/UX Specification Mode)
Objective: Élaborer sur les aspects UI/UX du produit MaâtCore défini dans ce PRD V1.1.
Mode: UI/UX Specification Mode
Input: Ce document PRD MaâtCore V1.1 complet. Utiliser également la MaâtCore UI/UX Specification V1.1 (Conceptual) comme base de départ et l'enrichir.
Key Tasks:

Examiner les buts du produit, les user stories des 5 Epics MVP, et toutes les notes relatives à l'UI/UX ici présentes.
Pour les services MaâtCare, MaâtClass, et MaâtTube, définir collaborativement les flux utilisateurs détaillés, les wireframes (conceptuels), et les maquettes/descriptions des écrans clés, en s'inspirant du niveau de détail atteint pour Maât.TV.
Spécifier les exigences d'utilisabilité et anticiper les considérations d'accessibilité (Post-MVP).
Consolider tout cela dans une version V1.2 (ou V2.0) de la MaâtCore UI/UX Specification. Veuillez guider l'utilisateur à travers ce processus.
Initial Architect Prompt
Basé sur nos discussions et ce PRD V1.1 complet pour MaâtCore, et en utilisant l'Architecture Document V1.1 (Affiné, avec détails Maât.TV) que vous avez produit :

Veuillez maintenant vous préparer à :

Revoir ce PRD V1.1 pour identifier tout impact sur l'Architecture Document V1.1 qui n'aurait pas encore été pris en compte.
Commencer la conception architecturale détaillée pour les autres microservices critiques du MVP, notamment le MaâtCareService, le MaâtClassService, et le UserService (pour les aspects non encore totalement couverts par la Story 1.1, comme la gestion de profil complète, etc.), en suivant une approche similaire à celle utilisée pour Maât.TV (APIs, modèles de données, modules internes, interactions).
Assurer la cohérence globale de l'architecture.








-----------------------------    ANCIENNE VERSION -----------------------------------------









MaâtCore Product Requirements Document (PRD) - V1.1 (Affiné et Finalisé)
(Date: 27 mai 2025)

1. Goal, Objective and Context (But, Objectif et Contexte)
Goal (But) : Lancer MaâtCore, un écosystème numérique intégré et innovant, pour transformer l'accès à l'information, au divertissement, à la santé et à l'éducation, initialement en Afrique avec une vision globale.
Objective (Objectif) : Atteindre une adoption significative dès la première année (1M abonnés TV, 100k Care, 100k Class) en offrant des services fiables, pertinents et engageants via une interface intuitive "Netflix-like" enrichie par l'IA et la 3D.
Context (Contexte) : MaâtCore répond à un besoin critique de contenu localisé, d'informations fiables, d'accès facilité aux soins et à l'éducation, et d'une plateforme pour les créateurs, le tout dans un environnement numérique où l'innovation technologique et l'expérience utilisateur ludique sont des leviers clés d'adoption.
2. Functional Requirements (MVP) (Exigences Fonctionnelles pour le MVP)
2.1. Maât.TV - Navigation & Découverte :
[ ] L'utilisateur doit pouvoir naviguer dans les contenus (chaînes, VOD) via une interface visuelle de type "Netflix".
[ ] L'utilisateur doit pouvoir rechercher des contenus (films, séries, etc.) en utilisant une fonction de recherche vocale basée sur l'IA.
[ ] L'utilisateur doit pouvoir parcourir des catégories (films, séries, docs, musique, sport, info).
2.2. Maât.TV - VOD (Films, Séries, Docs, Histoire) :
[ ] L'utilisateur doit pouvoir sélectionner un contenu VOD pour voir sa fiche descriptive.
[ ] L'utilisateur doit pouvoir lancer la lecture d'un contenu VOD.
[ ] L'utilisateur doit pouvoir mettre en pause, reprendre la lecture, avancer et reculer rapidement (contrôles de lecture MVP minimum).
2.3. Maât.TV - Chaînes Thématiques Interactives :
Info : L'utilisateur doit pouvoir visionner la chaîne d'information où un avatar IA lit les actualités, avec visuels de studio dynamiques, écran 3D pour vidéos, et playlist de news déployable. (Comparaison de sources détaillée Post-MVP).
Musique : L'utilisateur doit pouvoir sélectionner des styles musicaux (focus Afrique). Un flux audio démarre. L'interface affiche infos piste et avatar IA DJ. Interactions MVP avec avatar DJ : boutons "Piste Suivante/Précédente/J'aime" ; commandes vocales "Quel titre/artiste ?", "Piste suivante/précédente", "J'aime cette chanson".
Histoire : L'utilisateur voit un avatar IA narrateur présenter des segments d'histoire avec illustrations/visuels synchronisés. Comparaison de sources/narrations (MVP simple avec avatar désignant les sources).
2.4. Maât.TV - Sport (MaâtFoot) :
[ ] L'utilisateur doit pouvoir écouter des commentaires audio de matchs en direct, incarnés par un avatar IA expressif dans un studio thématisé "MaâtFoot", avec affichage dynamique du scoreboard, stats MVP (possession, tirs cadrés), fil d'événements discret, et ambiance lumineuse dynamique.
[ ] L'utilisateur doit pouvoir accéder à des analyses 3D post-matchs (vue schématique 3D sur écran du studio, avatar analyste, contrôles simples Play/Pause/Rejouer, angle de caméra unique pour MVP).
[ ] L'utilisateur doit pouvoir rejoindre un espace de discussion social virtuel (panneau de chat latéral/inférieur semi-transparent) pour commenter le sport, avec interaction de l'avatar commentateur.
2.5. MaâtCare - Parcours de Consultation :
[ ] L'utilisateur doit pouvoir initier une session en décrivant ses symptômes via reconnaissance vocale.
[ ] L'avatar médecin IA pose des questions d'orientation basées sur les inputs.
[ ] L'avatar IA propose une mise en relation avec un médecin réel si nécessaire (ou si demandé par l'utilisateur). L'utilisateur peut accepter/refuser.
[ ] Si accepté, l'utilisateur est connecté à un médecin réel via appel audio/vidéo (vidéo utilisateur activable MVP), et reçoit un résumé textuel des recommandations du médecin à la fin (MVP).
2.6. MaâtCare - "Soigner par les Plantes" :
[ ] Accès à une section informative sur les plantes (celles ayant fait l'objet d'études PubMed pour crédibilité).
[ ] Recherche par nom de plante et par symptôme de bien-être (MVP).
[ ] Fiches plantes avec nom, image, usages bien-être, précautions, avertissement légal, et 1-2 liens PubMed (MVP). Lecture par avatar.
2.7. MaâtCare - Paramètres Vitaux :
[ ] Collecte automatique via Bluetooth pour tensiomètre et oxymètre (fournis dans un kit).
[ ] Saisie déclarative pour autres paramètres (ex: température) ou en cas d'échec Bluetooth.
[ ] Affichage clair de la source des données (auto vs. déclarée).
2.8. MaâtClass - Sélection & Visionnage de Cours :
[ ] L'utilisateur peut filtrer les cours par Localisation, Niveau Scolaire, et Matière.
[ ] L'utilisateur peut visionner des leçons où un avatar IA présente le contenu avec illustrations/animations 2D/3D simples synchronisées.
[ ] Contrôles de lecture (Play/Pause, barre de progression) et navigation "Leçon Précédente/Suivante".
2.9. MaâtClass - Suivi de Progression :
[ ] Suivi de progression basique "vu/non vu" par leçon/module.
[ ] Indicateur de progression global par cours.
[ ] Proposition de "Reprendre la lecture" à la dernière leçon non terminée.
2.10. MaâtTube - Pour les Créateurs (MVP Essentiel) :
[ ] Inscription créateur & création de chaîne MaâtTube.
[ ] Téléversement vidéos (formats courants) avec titre, description, miniature, tags.
[ ] Visualisation et suppression de ses propres vidéos.
2.11. MaâtTube - Pour les Spectateurs :
[ ] Navigation/découverte via interface "Netflix-like" et recherche IA.
[ ] Lecture vidéos (contrôles standard).
[ ] Affichage nombre de vues. (Likes, Commentaires, Abonnements sont Post-MVP).
2.12. Plateforme Cœur (Fondation - Epic 1) :
[ ] Interface utilisateur "Netflix-like" cohérente.
[ ] Recherche IA (vocale/texte) unifiée ou contextuelle.
[ ] Gestion des profils utilisateurs (Inscription/Connexion facilitée via smartphone pour TV ; profil minimaliste avec e-mail/pseudo ; déconnexion).
2.13. MVP Validation Strategy and Learning Objectives :
Mécanismes de Feedback Utilisateur (MVP) : Enquêtes pop-up post-utilisation ; point de contact clair (e-mail/formulaire).
Objectifs d'Apprentissage Clés du MVP : Valider engagement "audio-avatar" sport ; évaluer attraction MaâtTube pour créateurs ; (Suggéré) évaluer acceptation avatars IA pour MaâtCare ; (Suggéré) déterminer efficacité perçue cours MaâtClass.
Critères pour Évoluer au-delà du MVP (Phase 2) : Atteinte de 100 000 abonnés actifs MaâtCore ; niveau de satisfaction utilisateur élevé (NPS cible à définir) et retours qualitatifs positifs.
3. Non Functional Requirements (MVP) (Exigences Non Fonctionnelles pour le MVP)
3.1. Performance : Haute réactivité ("Netflix-like") ; démarrage vidéo < 3s ; latence minimisée pour direct audio.
3.2. Scalability : Supporte 50 000 utilisateurs simultanés (An 1) ; architecture pour montée en charge rapide.
3.3. Security :
Bonnes pratiques web standard (HTTPS, etc.).
Conformité MaâtCare : Conception et opération en stricte conformité avec réglementations données de santé applicables (territoires Afrique Francophone/Anglophone ciblés) et standards internationaux. Analyse de conformité détaillée requise.
Tests de Sécurité : Scans de vulnérabilités automatisés (CI/CD) ; Pentests ciblés (MaâtCare, AuthN) avant lancement MVP.
Politique de Confidentialité : Détaillée, expliquant collecte, usage, protection, droits des données ; accessible avant lancement.
Consentement Utilisateur : Mécanismes de consentement explicite pour collecte/usage données personnelles (surtout MaâtCare et amélioration IA).
Dispositif anti-copie de base (TBD).
3.4. Availability / Reliability :
Taux de disponibilité visé : 99%.
Fiabilité des sources d'information pour Maât.TV News.
Objectif de Temps de Récupération (RTO) maximal de 24 heures et Objectif de Point de Récupération (RPO) maximal de 1 heure en cas d'incident majeur.
3.5. Accessibility : Prévue Post-MVP.
3.6. Internationalization/Localization (MVP) : Supporte le Français et l'Anglais (interfaces et contenus de base).
3.7. Monitoring & Alerting (MVP) : Temps de réponse services clés monitorés ; alertes critiques pour défaillance services essentiels (MaâtCare) ou dégradation performances.
4. User Interaction and Design Goals (Objectifs d'Interaction Utilisateur et de Design)
4.1. Philosophie d'Interface : Netflix-like, fluide, moderne, colorée, vibrante, touche africaine.
4.2. Aspect Ludique (MVP) : Transitions animées, micro-animations, design sonore qualitatif.
4.3. Rôle des Avatars : Point d'entrée visuel principal (Care, Class, Info, Sport, Musique), animés (IA), expressifs, incarnent l'aspect humain.
4.4. Objectif "Wow" : Première expérience utilisateur mémorable soulignant l'innovation.
4.5. Gestion des Erreurs (Expérience Utilisateur MVP) : Messages simples, clairs, rassurants, sans jargon technique. (Détails par Design Architect).
5. Technical Assumptions (Hypothèses Techniques)
5.1. Délais & Budget : Forte exigence de mise sur le marché rapide (MVP) ; pas de contrainte budgétaire principale.
5.2. Plateforme & Architecture : Cloud AWS ; approche Microservices ; dépôt Monorepo.
5.3. Technologies Clés (Préférences & Options) :
Moteurs 3D/Avatars : Évaluer Unreal Engine, Heygen, et technologies Google.
Développement Android TV : Kotlin et Jetpack Compose privilégiés (exploiter composants TV spécifiques).
L'équipe technique proposera les autres technologies.
5.4. Development Philosophy & Guiding Principles :
Choix Technologiques : Privilégier les solutions open-source matures et largement supportées.
Gestion de la Dette Technique (MVP) : Lancement rapide prioritaire ; dette technique modérée acceptée pour MVP, avec plan de résolution Post-MVP.
5.5. Development Environment (MVP) :
Script d'installation clair pour environnement local.
IDE standardisé : Visual Studio Code (VSCode).
Encouragement à l'utilisation de générateurs de code pertinents.
L'équipe technique gère la compatibilité des versions.
L'environnement local devra permettre de lancer/tester les microservices.
6. Epic Overview (Vue d'ensemble des Epics)
(Les User Stories détaillées pour chaque Epic (1 à 5) ont été définies et validées dans nos échanges précédents et sont considérées comme "Draft Approuvé" et prêtes pour développement. Elles incluent les ACs, Tâches, et Directives Techniques pour chaque story.)

Epic 1 : Fondation MaâtCore & Profil Utilisateur
Goal : Structure technique, UI coquille, inscription/connexion (smartphone), profil minimaliste.
Stories 1.1 à 1.4
Epic 2 : Maât.TV - Expérience de Visionnage MVP
Goal : Naviguer, rechercher, visionner VOD et chaînes thématiques (Info, Musique, Histoire) via interface "Netflix-like" et recherche vocale.
Stories 2.1 à 2.5
Epic 3 : Maât.TV - Expérience Sport MVP
Goal : Offrir expérience unique (commentaire audio live avatar, analyses 3D, espace social).
Stories 3.1 à 3.3
Epic 4 : MaâtCare - Consultation Initiale MVP
Goal : Pré-consultation vocale avatar IA, handoff médecin réel, infos médecine plantes, collecte paramètres vitaux (Bluetooth pour tensiomètre/oxymètre, déclaratif pour autres).
Stories 4.1 à 4.6
Epic 5 : MaâtClass - Accès aux Cours MVP
Goal : Trouver, sélectionner, suivre cours (programmes scolaires officiels) par avatars avec illustrations, suivi progression basique.
Stories 5.1 à 5.3
7. Key Reference Documents (Documents de Référence Clés)
Project Brief MaâtCore V1
MaâtCore Architecture Document V1.1 (Affiné, avec détails Maât.TV)
MaâtCore UI/UX Specification V1.1 (Conceptual, avec détails Maât.TV) (Cette section sera complétée avec d'autres documents techniques et de design détaillés au fur et à mesure de leur production.)
8. Out of Scope Ideas Post MVP (Idées hors périmètre pour l'après-MVP)
Maât.TV Ultra-Immersif : Watch Parties 3D, Fictions Interactives IA, Documentaires Explorables, Chaînes Personnalisées IA.
MaâtCare Proactif : Intégration Objets Connectés (au-delà tensiomètre/oxymètre MVP, haute priorité post-MVP), Bien-être Mental, Parcours Spécialisés, Pharmacie Virtuelle. Pistes d'audit immuables et détaillées pour MaâtCare, mécanismes d'anonymisation/pseudonymisation robustes pour IA avec données MaâtCare.
MaâtClass Continu & Collaboratif : Formation Pro, Salles Virtuelles, Apprentissage Adaptatif, Certifications. Reprise de lecture à un point précis d'une leçon.
MaâtTube Pro & Social : Outils de Création 3D, Monétisation Avancée, Live Streaming, Commentaires, Likes, Abonnements.
Nouveaux Services d'Écosystème : MaâtSapp (Visio TV familiale), MaâtCash (Transfert d'argent).
Transverse & Écosystème (suite) : Couche Sociale Approfondie, Marketplace, VR/AR, Assistant Personnel IA.
Fonctionnalités TV/VOD Avancées : Tri et filtres avancés pour catalogues, bandes-annonces sur fiches détail, gestion de "Ma Liste / Mes Favoris".
Sécurité & Conformité Avancées : Chiffrement systématique de toutes les sauvegardes de données (au-delà de MaâtCare), chiffrement au repos par défaut de toutes les données persistantes, pistes d'audit généralisées pour toutes actions critiques (hors MaâtCare).
Lecteur VOD (Overlay) : Aperçu par miniature vidéo pendant le scrubbing, contrôle de volume in-player, accès direct aux options de langues/sous-titres.
9. Change Log (Journal des Modifications)
Date	Version	Description	Auteur
2025-05-27	V1.1 (Affiné)	Version initiale post-affinage PM Checklist et finalisation User Stories pour tous les Epics MVP. Ajout sections 2.13 et 10 (Support).	John (PM) / Sarah (PO) / BMad (Orchest.)

Export to Sheets
Checklist Results Report (Rapport des Résultats du Checklist PM) - Statut Révisé
Final Decision (Décision Finale) - Révisée
READY FOR ARCHITECT & DESIGN ARCHITECT REVIEW / DEVELOPMENT PREPARATION (PRÊT POUR REVUE ARCHITECTE & DESIGN ARCHITECT / PRÉPARATION AU DÉVELOPPELENT)

Le PRD MaâtCore V1.1, suite aux affinages et à la complétion de toutes les User Stories pour les 5 Epics MVP, est maintenant considéré comme une base solide et complète. Les déficiences initialement identifiées par le pm-checklist ont été adressées au niveau des politiques, des exigences, et de la stratégie MVP. Ce document est prêt à être utilisé par l'Architecte (Fred) et la Design Architect (Jane) pour leurs travaux de conception détaillée et par le Product Owner (Sarah) pour la gestion du backlog en vue du développement.

Prompt for Design Architect (UI/UX Specification Mode)
Objective: Élaborer sur les aspects UI/UX du produit MaâtCore défini dans ce PRD V1.1.
Mode: UI/UX Specification Mode
Input: Ce document PRD MaâtCore V1.1 complet. Utiliser également la MaâtCore UI/UX Specification V1.1 (Conceptual) comme base de départ et l'enrichir.
Key Tasks:

Examiner les buts du produit, les user stories des 5 Epics MVP, et toutes les notes relatives à l'UI/UX ici présentes.
Pour les services MaâtCare, MaâtClass, et MaâtTube, définir collaborativement les flux utilisateurs détaillés, les wireframes (conceptuels), et les maquettes/descriptions des écrans clés, en s'inspirant du niveau de détail atteint pour Maât.TV.
Spécifier les exigences d'utilisabilité et anticiper les considérations d'accessibilité (Post-MVP).
Consolider tout cela dans une version V1.2 (ou V2.0) de la MaâtCore UI/UX Specification. Veuillez guider l'utilisateur à travers ce processus.
Initial Architect Prompt
Basé sur nos discussions et ce PRD V1.1 complet pour MaâtCore, et en utilisant l'Architecture Document V1.1 (Affiné, avec détails Maât.TV) que vous avez produit :

Veuillez maintenant vous préparer à :

Revoir ce PRD V1.1 pour identifier tout impact sur l'Architecture Document V1.1 qui n'aurait pas encore été pris en compte.
Commencer la conception architecturale détaillée pour les autres microservices critiques du MVP, notamment le MaâtCareService, le MaâtClassService, et le UserService (pour les aspects non encore totalement couverts par la Story 1.1, comme la gestion de profil complète, etc.), en suivant une approche similaire à celle utilisée pour Maât.TV (APIs, modèles de données, modules internes, interactions).
Assurer la cohérence globale de l'architecture.