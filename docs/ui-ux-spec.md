MaâtCore UI/UX Specification - V1.1 (Conceptual)
(Date: 27 mai 2025)

1. Introduction
Le présent document définit les spécifications de l'expérience utilisateur (UX) et de l'interface utilisateur (UI) pour la plateforme MaâtCore. Il s'appuie sur le Product Requirements Document (PRD V1.1) et vise à guider la conception et le développement d'une expérience utilisateur intuitive, engageante, moderne, et en accord avec l'identité "Netflix-like" souhaitée, enrichie d'une touche africaine vibrante et d'interactions ludiques. Le logo et la marque pour le service de télévision seront spécifiquement "Maât.TV", avec une sous-marque "MaâtFoot" pour le football.

Link to Main PRD (REQUIRED): MaâtCore PRD V1.1
Link to Primary Design Files: {Placeholder: Figma/Sketch/Adobe XD URL - à créer}
Link to Deployed Storybook / Design System: {Placeholder: URL - pour plus tard}
2. Overall UX Goals & Principles
Target User Personas (basé sur PRD).
Usability Goals: Intuitivité maximale, engagement fort, accessibilité (Post-MVP), sentiment de confiance.
Design Principles: Clarté & Simplicité ("Netflix-like"), Engagement Ludique, Humanité via Avatars, Modernité Africaine Vibrante, Cohérence Globale.
3. Information Architecture (IA)
Site Map / Screen Inventory (MVP High-Level) : (Conforme au diagramme Mermaid de la V0.3 de ce document et aux services définis dans le PRD)
Navigation Structure :
Principale : Menu vertical animé et fluide à gauche.
Secondaire : Navigation par "rayons" ou "carrousels" (style Netflix).
Avatars comme Points d'Entrée (MaâtCare, MaâtClass).
4. Splash Screen MaâtCore (Concept)
Visuel : Fond noir profond texturé/dégradé. Animation centrale d'un motif géométrique d'inspiration africaine modernisée (ocre doré, rouge terre, orange solaire). Logo "MaâtCore" apparaît en fondu.
Son : Jingle court (2-3s), moderne avec touche instrumentale africaine stylisée, synchro avec logo.
Indicateur de Chargement : Fin, lumineux, ou via complétion du motif animé.
Transition : Fondu rapide vers authentification ou page d'accueil.
5. Page d'Accueil Principale (Tableau de Bord) & Header
Layout Général : Thème sombre ("écran noir avec ses subtilités"). Menu vertical animé à gauche.
Header Dynamique (Maât.TV / MaâtFoot par défaut à l'atterrissage) :
Zone Immersive : En haut, carrousel dynamique et fluide d'images/courtes vidéos en boucle (contenu phare Maât.TV/MaâtFoot). Navigation simple via télécommande.
Floutage Artistique : Partie gauche du header floutée/assombrie pour intégration harmonieuse du menu.
Informations Superposées : Titre du contenu en vedette (typographie "style Netflix"), courte tagline, appel à l'action ("Regarder").
Contenu sous le Header (pour Maât.TV / Accueil Général) :
"Rayons" de vignettes/cartes (style affiches de films avec titres sur l'image). Pour MVP : "Nouveautés" et "Catégories Principales". Ordre éditorial. Pas de rayon "Reprendre la lecture" sur l'accueil pour MVP.
Le header se met à jour pour refléter l'item en focus dans les rayons ("liste immersive").
Option "Voir tout >" par rayon.
6. Interface Utilisateur Spécifique : Maât.TV / MaâtFlix / MaâtFoot
6.1. Chaîne d'Information Maât.TV (MVP)
Layout Général & Ambiance : Inspiré du studio 3D (votre image STUDIO-TV.jpg). Thème moderne, technologique, éclairages bleus dominants, touches de couleurs chaudes. Bureau brandé "Maât.TV".
Avatar IA Présentateur : Incrusté sur la droite de l'écran, à côté/derrière le bureau "Maât.TV".
Zone d'Information Visuelle (Écrans du Studio) :
Contenu par Défaut : Animation en boucle d'un globe terrestre stylisé et dynamique.
Contenu Spécifique News : Titre, images, infographies, cartes.
Comparaison de Sources (MVP simple) : Affichage discret de logos de sources ou citations clés sur un des écrans. L'avatar "désigne" gestuellement les sources.
Lecture de Vidéos d'Actualité : Nouvel écran 3D virtuel au design moderne apparaît dynamiquement dans le studio.
Navigation (Playlist) : Panneau vertical gauche semi-transparent, se déploie à la demande, listant les news (miniature/titre). Item en cours surligné.
6.2. Expérience Sport MaâtFoot (MVP)
Interface du Commentaire Audio "Live" Avatar :
Thématisation Studio "MaâtFoot" : Basé sur studio Maât.TV, logo "MaâtFoot", couleur d'accent énergique (proposition : orange vif ou jaune solaire dynamique), motifs de ballon de football stylisés en fond.
Avatar Commentateur : Style vestimentaire moderne décontracté africain (tenues multiples Post-MVP). Expressif.
Affichage Infos Match :
Scoreboard : Élément 3D stylisé intégré au décor (ex: coin supérieur ou écran secondaire), affichant noms/logos équipes, score, temps de jeu.
Statistiques Clés (MVP) : Possession de balle et tirs cadrés (infographies simples sur écran secondaire, mises à jour périodiques).
Fil d'Événements Textuels (MVP) : Bandeau fin et discret en bas de l'écran principal du studio ou petite zone dédiée.
Ambiance Lumineuse Dynamique (MVP) : Changements subtils selon intensité (ex: flash couleur accent pour but).
Accès au "Live" : Rayon dédié "En Direct / MaâtFoot" sur l'accueil de Maât.TV (cartes "style affiche de match").
Interface des Analyses 3D Post-Match (MVP) :
Accès : Section "Replays & Analyses" MaâtFoot ou depuis fiche match terminé.
Layout : Avatar analyste (style "expert") dans studio MaâtFoot. Un écran du studio devient une "fenêtre 3D tactique" (terrain schématique, joueurs/ballon stylisés animés, angle caméra principal fixe MVP).
Synchronisation : Avatar commente, éléments graphiques (flèches, cercles) apparaissent sur vue 3D en synchro.
Contrôles MVP : Play/Pause (synchro avatar/3D), "Rejouer", barre de progression simple.
Interface de l'Espace Social Sportif (MVP) :
Accès : Bouton "Fan Zone" depuis interface live/analyse.
Présentation : Panneau de chat latéral (droit) ou inférieur, semi-transparent, s'ouvrant par glissement fluide.
Messages : Chronologiques (récents en bas), pseudo (avatar MaâtCore simple si MVP). Messages de l'avatar commentateur ou modérateurs visuellement distincts.
Interaction : Défilement télécommande. Champ de saisie pour clavier virtuel TV. Bouton "Envoyer".
Interaction Avatar Commentateur (MVP) : Peut occasionnellement afficher/mentionner un message/thème du chat.
Fermeture : Facile (touche "Retour" ou icône "X").
6.3. Fiche Détail du Contenu VOD (MaâtFlix - MVP Cœur)
Layout : Thème sombre immersif. Bouton "Retour".
Zone Supérieure / "Hero" : Grande image de fond (mainImageUrl), titre, ligne d'info (année, durée, classement), bouton "Lire"/"Reprendre" proéminent.
Zone Inférieure / "Détails" : Synopsis, infos complémentaires (réalisateur/casting pour films ; créateur/casting/saisons pour séries). Pour séries : sélection saisons (onglets/déroulant), liste épisodes (miniature, numéro, titre, durée, bouton lire/reprendre).
Post-MVP : Boutons "Bande-Annonce", "Ajouter à Ma Liste".
6.4. Contrôles de Lecture VOD (MaâtFlix - MVP Fonctionnel Minimum)
Overlay : Apparaît/disparaît en fondu (3-5s inactivité). Bandeau horizontal bas, fond noir semi-transparent.
Éléments (MVP) : Barre de progression (temps écoulé/total, scrubbable sans preview miniature), boutons Retour Rapide (-10s), Play/Pause (central, dynamique), Avance Rapide (+10s).
Focus & Feedback : Clairement indiqué, animations fluides.
Post-MVP : Preview miniature scrubbing, volume in-player, accès langues/sous-titres.
7. Interface Utilisateur Spécifique : MaâtCare (Concepts Initiaux Affinés)
7.1. Écran d'Accueil MaâtCare (MVP)
Ambiance Visuelle : Thème sombre MaâtCore, rehaussé d'une couleur d'accent vert santé (code couleur exact à fournir par l'utilisateur).
Éléments Principaux :
Appel à l'Action Principal : Carte proéminente "Démarrer une consultation" (style affiche de film) avec avatar médecin IA (style âgé, blouse blanche, rassurant).
Accès à "Soigner par les Plantes" : Autre point d'entrée clair. Le libellé est "Soigner par les Plantes" (contenu focus PubMed : seules les plantes avec études PubMed référencées).
Navigation : Accès depuis menu principal MaâtCore. Navigation télécommande simple.
7.2. Interface d'Initiation de la Consultation Vocale (MaâtCare - Story 4.1 PRD)
Transition & Accueil Avatar : Transition fluide. Avatar médecin (âgé, blouse blanche) proéminent, décor médical stylisé apaisant. Message vocal de bienvenue.
Invitation à Parler : Icône micro animée, message "Je vous écoute...". Avatar en posture d'écoute.
Confirmation de Compréhension : Résumé textuel des symptômes affiché. Avatar lit le résumé. Illustration animée 3D schématique et pédagogique du symptôme principal s'affiche en synchronisation (pour MVP si animation simple disponible). Boutons "Oui" et "Non / Recommencer".
7.3. Interface d'Interaction Guidée avec l'Avatar Médecin IA (MaâtCare - Story 4.2 PRD - À Affiner)
Conversationnel : Avatar pose questions (audio + texte optionnel), utilisateur répond vocalement.
Espace pour afficher les animations 3D schématiques illustrant les explications de l'avatar (MVP simple).
Option claire pour demander à parler à un médecin réel ou arrêter.
7.4. Interface de Consultation avec Médecin Réel (MaâtCare - Story 4.4 PRD - À Affiner)
Affichage vidéo du médecin, vidéo de l'utilisateur (activable MVP).
Infos médecin. Contrôles d'appel simples (Mute, Caméra Off, Terminer).
Affichage du résumé de consultation à la fin (MVP).
7.5. Interface "Soigner par les Plantes" (MaâtCare - Story 4.5 PRD - À Affiner)
Navigation/recherche (par nom de plante et symptôme de bien-être MVP).
Fiches plantes claires : nom, image, usages bien-être, précautions, avertissement légal, 1-2 liens PubMed (MVP). Lecture par avatar. Illustrations animées 3D schématiques possibles.
7.6. Interface de Saisie des Paramètres Vitaux (MaâtCare - Story 4.6 PRD - À Affiner)
Guidage pour appairage Bluetooth (tensiomètre, oxymètre MVP).
Affichage des données collectées automatiquement avec confirmation.
Interface de saisie déclarative pour autres paramètres (température) ou en cas d'échec Bluetooth. Indication claire de la source (auto vs. déclarée).
8. Interface Utilisateur Spécifique : MaâtClass (Concept Initial - À Affiner)
Navigation via filtres (Localisation, Niveau, Matière) et cartes "Avatar Professeur".
Interface de cours : avatar IA présentant, zone pour illustrations/animations 2D/3D simples synchronisées.
Contrôles de lecture et navigation inter-leçons.
Tableau de bord de suivi de progression simple.
9. Interface Utilisateur Spécifique : MaâtTube (Concept Initial - À Affiner)
Créateurs : Interface d'upload simple, gestion des métadonnées, page de chaîne basique.
Spectateurs : Découverte via interface "Netflix-like", lecteur vidéo standard, affichage nombre de vues.
10. Branding & Style Guide Reference (Bases pour MVP)
Palette de Couleurs : Thème de base sombre. Accents globaux (doré/cuivre). Variations par service : Maât.TV/MaâtFoot (orange vif ou jaune solaire), MaâtCare (vert santé - code exact à fournir).
Typographie : Style "Netflix" : famille sans-serif moderne, très lisible (TV/mobile), variété de graisses.
Iconographie : Style "Netflix" : subtile, simple, élégante, moderne, minimaliste, reconnaissance immédiate.
Imagerie & Illustrations : Refléter diversité culturelle africaine contemporaine.
"Touche Africaine" : Subtile (motifs géométriques discrets, textures, iconographie).
Animations & Transitions : Fluides, engageantes, pour l'aspect ludique et "wow". Design sonore discret et qualitatif.
11. Accessibility (AX) Requirements
MVP : Bases (contraste, navigation clavier éléments natifs).
Post-MVP : Audit et implémentation WCAG 2.1 AA.
12. Responsiveness
Cibles : Android TV, Mobile Android.
Stratégie : "Adaptive Design" avec layouts et interactions optimisés par plateforme.
13. Change Log
Date	Version	Description	Auteur
2025-05-27	V1.1 (Conceptual)	Consolidation affinages Maât.TV (News, Sport), esquisse accueil MaâtCare.	Jane (DA)
2025-05-26	V1.0 (Conceptual)	Intégration ébauches et affinages pour Maât.TV	Jane (DA)