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
(Cette section contient tous les détails affinés pour Maât.TV que nous avons discutés : Chaîne d'Info avec studio 3D, Expérience Sport MaâtFoot avec avatar commentateur/analyses 3D/chat, Fiche Détail VOD, et Contrôles de Lecture VOD MVP).

7. Interface Utilisateur Spécifique : MaâtCare (Concepts Affinés)
Ambiance Visuelle : Thème sombre MaâtCore, rehaussé d'une couleur d'accent vert santé (code couleur exact à fournir par l'utilisateur).
Avatar Médecin IA : Style âgé, en blouse blanche, pour inspirer confiance et expérience. Apparence humaine réaliste (style HeyGen/Google) est une exigence MVP.
Écran d'Accueil : Carte proéminente "Démarrer une consultation" avec l'avatar médecin. Accès à "Soigner par les Plantes" (focus PubMed).
Interface de Consultation IA : Décor de studio virtuel sobre, rassurant, et beau (créé avec Unreal Engine). Dialogue via bulles épurées. Illustrations 3D schématiques et animées (pré-conçues pour MVP) des symptômes/concepts de santé apparaissent dans un panneau flottant.
Interface Consultation Médecin Réel : Ambiance studio MaâtCare conservée. Indicateur d'attente avec musique douce. Interface vidéo avec vidéo utilisateur activable (MVP). Affichage simple du résumé de consultation à la fin (MVP).
Interface "Soigner par les Plantes" : Recherche par nom/symptôme bien-être. Fiches détaillées avec avatar lecteur et liens PubMed.
Interface Paramètres Vitaux : Flux guidé pour appairage Bluetooth (tensiomètre, oxymètre MVP). Interface de collecte mixte (automatisée et déclarative).
8. Interface Utilisateur Spécifique : MaâtClass (Concept Affiné)
Ambiance Visuelle : Thème sombre MaâtCore, couleur d'accent bleu lumineux vibrant (ex: céruléen).
Avatar Professeur IA : Plusieurs avatars professeurs distincts pour varier les matières/niveaux, avec un style professionnel et bienveillant (tenue moderne, pas de blouse).
Sélection Cours : Filtres via boutons horizontaux activant des menus déroulants stylisés (Pays/Programme, Niveau, Matière). Grille de cartes de cours (image thématique, titre, matière, niveau, indicateur de progression discret).
Interface Lecteur de Cours : Avatar dans un fond 2D stylisé, abstrait et apaisant (MVP). Zone principale pour supports visuels synchronisés (illustrations, animations 2D/3D simples). Panneau latéral "Sommaire du Cours" se déploie via glissement fluide. Contrôles de lecture MVP.
Suivi de Progression : Indicateurs visuels clairs (barre de progression sur carte, coche ✔️ sur leçons vues).
(Note : Transcription/sous-titres et méthode socratique interactive sont Post-MVP).
9. Interface Utilisateur Spécifique : MaâtTube (Concept Initial - À Affiner)
Créateurs : Interface d'upload simple, gestion des métadonnées, page de chaîne basique.
Spectateurs : Découverte via interface "Netflix-like", lecteur vidéo standard, affichage nombre de vues.
10. Branding & Style Guide Reference (Bases pour MVP)
Palette de Couleurs : Thème de base sombre. Accents globaux (doré/cuivre). Variations par service : Maât.TV/MaâtFoot (orange vif ou jaune solaire), MaâtCare (vert santé), MaâtClass (bleu lumineux céruléen).
Typographie : Style "Netflix" : famille sans-serif moderne, très lisible (TV/mobile), variété de graisses.
Iconographie : Style "Netflix" : subtile, simple, élégante, moderne, minimaliste, reconnaissance immédiate.
(Autres points conformes aux versions précédentes)
11. Accessibility (AX) Requirements
MVP : Bases (contraste, navigation clavier éléments natifs).
Post-MVP : Audit et implémentation WCAG 2.1 AA.
12. Responsiveness
Cibles : Android TV, Mobile Android.
Stratégie : "Adaptive Design" avec layouts et interactions optimisés par plateforme.
13. Change Log
Date	Version	Description	Auteur
2025-05-27	V1.1 (Conceptual)	Consolidation affinages Maât.TV, MaâtCare, et MaâtClass.	Jane (DA)


                                  VERSION PRECEDENTE
__________________________________________________________________________________________

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
Layout Général & Ambiance : Inspiré du studio 3D (STUDIO-TV.jpg comme référence). Thème moderne, technologique, éclairages bleus dominants, touches de couleurs chaudes. Bureau brandé "Maât.TV".
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
Zone Inférieure / "Détails" : Synopsis, infos complémentaires (réalisateur/casting pour films ; créateur/casting/saisons pour séries). Pour séries : sélection saisons (onglets/déroulant), liste épisodes (miniature, numéro, titre, durée, bouton lire/reprendre par épisode).
Post-MVP : Boutons "Bande-Annonce", "Ajouter à Ma Liste".
6.4. Contrôles de Lecture VOD (MaâtFlix - MVP Fonctionnel Minimum)
Overlay : Apparaît/disparaît en fondu (3-5s inactivité). Bandeau horizontal bas, fond noir semi-transparent.
Éléments (MVP) : Barre de progression (temps écoulé/total, scrubbable sans preview miniature), boutons Retour Rapide (-10s), Play/Pause (central, dynamique), Avance Rapide (+10s).
Focus & Feedback : Clairement indiqué, animations fluides.
Post-MVP : Preview miniature scrubbing, volume in-player, accès langues/sous-titres.
7. Interface Utilisateur Spécifique : MaâtCare (Concepts Affinés)
7.1. Écran d'Accueil MaâtCare (MVP)
Ambiance Visuelle : Thème sombre MaâtCore, rehaussé d'une couleur d'accent vert santé (code couleur exact à fournir par l'utilisateur).
Éléments Principaux :
Appel à l'Action Principal : Carte proéminente "Démarrer une consultation" (style affiche de film) avec avatar médecin IA (style âgé, blouse blanche, rassurant).
Accès à "Soigner par les Plantes" : Autre point d'entrée clair (carte ou item de menu). Le libellé est "Soigner par les Plantes" (contenu focus PubMed : seules les plantes avec études PubMed référencées).
Navigation : Accès depuis menu principal MaâtCore. Navigation télécommande simple entre options.
7.2. Interface d'Initiation de la Consultation Vocale (MaâtCare - Story 4.1 PRD)
Transition & Accueil Avatar : Transition fluide. Avatar médecin (âgé, blouse blanche) proéminent, décor médical stylisé apaisant ("sobre, rassurant et beau"). Message vocal de bienvenue.
Invitation à Parler : Icône micro animée, message "Je vous écoute...". Avatar en posture d'écoute.
Confirmation de Compréhension : Résumé textuel des symptômes affiché. Avatar lit le résumé. Illustration animée 3D schématique et pédagogique du symptôme principal s'affiche en synchronisation (pour MVP si animation simple disponible, pré-conçue et téléchargée).
Boutons : "Oui" et "Non / Recommencer".
7.3. Interface d'Interaction Guidée avec l'Avatar Médecin IA (MaâtCare - Story 4.2 PRD - Affinée)
Layout Général : Avatar d'un côté, zone de dialogue de l'autre.
Dialogue : Questions (avatar) et transcriptions des réponses (utilisateur) s'affichent sous forme de bulles de dialogue épurées.
Zone d'Illustration 3D : Animations 3D schématiques (pré-conçues pour MVP) s'affichent dans un panneau flottant qui apparaît contextuellement pour illustrer les explications de l'avatar.
Navigation : Bouton discret mais accessible "Parler à un médecin réel" reste disponible.
7.4. Interface de Mise en Relation & Consultation avec Médecin Réel (Stories 4.3 & 4.4 PRD - Affinée)
Attente : Message clair ("Recherche en cours..."), indicateur visuel apaisant, boucle musicale douce.
Consultation Vidéo : Ambiance du studio virtuel MaâtCare conservée. Vidéo médecin principale, vidéo utilisateur en incrustation (activable MVP). Infos médecin visibles. Contrôles d'appel simples (Mute, Caméra Off, Terminer) sur overlay discret.
Résumé : Écran de texte simple et lisible avec le résumé du médecin à la fin, et un bouton "Fermer".
7.5. Interface "Soigner par les Plantes" (MaâtCare - Story 4.5 PRD - Affinée)
Accueil Section : Barre de recherche (nom de plante et symptôme de bien-être MVP), navigation par catégories de bien-être.
Fiche Plante : Layout partagé (image de la plante à gauche, texte scrollable à droite). Avatar médecin IA comme lecteur. Section "Recherche Scientifique" avec titre et lien direct pour 1-2 études PubMed. Animations 3D schématiques via panneau flottant. Avertissement légal proéminent.
7.6. Interface de Collecte des Paramètres Vitaux (MaâtCare - Story 4.6 PRD - Affinée)
Appairage Bluetooth (Tensiomètre, Oxymètre - MVP) : Proposé contextuellement (première fois que nécessaire) ou via un menu "Mes Appareils Médicaux". Processus guidé étape par étape (instructions, scan, sélection, confirmation).
Collecte de Données :
Automatisée : L'interface guide l'utilisateur pour prendre la mesure sur son appareil externe. Les données reçues via Bluetooth sont affichées pour confirmation.
Déclarative (pour autres paramètres comme la température, ou en cas d'échec Bluetooth) : Interface de saisie numérique simple et claire, optimisée pour la télécommande ou la voix.
La source des données (automatique vs. déclarée) est visuellement indiquée.
8. Interface Utilisateur Spécifique : MaâtClass (Concept Initial Affiné)
Accès & Ambiance : Accès via menu principal. Couleur d'accent bleu lumineux vibrant (ex: céruléen).
Sélection Cours (Filtres) :
Mécanisme : Trois boutons horizontaux ("Pays/Programme", "Niveau", "Matière") activant des menus déroulants stylisés en superposition.
Affichage Résultats : Grille de cartes de cours scrollable.
Carte de Cours (MVP) : Titre, Matière, Niveau, Image thématique, Indicateur de progression discret.
Visionnage de Cours (Interface Lecteur) :
Layout : Avatar professeur IA (style professionnel, bienveillant) sur un côté, zone principale pour supports visuels (illustrations, diagrammes, animations 2D/3D simples).
Navigation : Panneau latéral "Sommaire du Cours" déployable, listant toutes les leçons (avec indicateur "vue" pour progression). Boutons "Leçon Précédente/Suivante".
Contrôles : Overlay discret avec Play/Pause, barre de progression pour la leçon en cours.
(Concept Post-MVP) Apprentissage Socratique : L'interface pourra évoluer pour intégrer des phases de questions/réponses interactives.
9. Interface Utilisateur Spécifique : MaâtTube (Concept Initial - À Affiner)
Créateurs : Interface d'upload simple, gestion des métadonnées, page de chaîne basique.
Spectateurs : Découverte via interface "Netflix-like", lecteur vidéo standard, affichage nombre de vues.
10. Branding & Style Guide Reference (Bases pour MVP)
Palette de Couleurs : Thème de base sombre. Accents globaux (doré/cuivre). Variations par service : Maât.TV/MaâtFoot (orange vif ou jaune solaire), MaâtCare (vert santé - code exact à fournir), MaâtClass (bleu lumineux céruléen).
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
2025-05-27	V1.1 (Conceptual)	Consolidation affinages Maât.TV (News, Sport, VOD), et affinages MaâtCare (Accueil, Consultation, Plantes, Vitals) & MaâtClass (Accueil).	Jane (DA)
2025-05-26	V1.0 (Conceptual)	Intégration ébauches et affinages initiaux pour Maât.TV.	Jane (DA)

Export to Sheets












---------------------------------------------ANCIENNE VERSION ------------------------------------------------






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