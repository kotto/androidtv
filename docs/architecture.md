MaâtCore Architecture Document - V1.2 (Intégration Backoffice Détaillé)
(Date: 27 mai 2025)

1. Introduction / Preamble
Ce document décrit l'architecture technique globale de la plateforme MaâtCore. Il s'appuie sur le Product Requirements Document (PRD V1.1) et les spécifications UI/UX initiales (V1.1 Conceptuelle). L'objectif est de définir une architecture modulaire, scalable, sécurisée et performante, capable de supporter l'ensemble des services (MaâtTV, MaâtCare, MaâtClass, MaâtTube) et le backoffice de production, tout en permettant un lancement MVP rapide et des évolutions futures. Ce document est destiné à guider les équipes de développement et d'infrastructure.

2. Technical Summary
MaâtCore sera architecturé comme un système de microservices hébergés sur AWS, avec un dépôt unique (Monorepo). Les applications clientes (Android TV en Kotlin/Jetpack Compose) interagiront avec les services backend via une API Gateway. Le Backoffice, un composant central, sera développé avec une stack React/Node.js/PostgreSQL et permettra la gestion et l'orchestration de la production de contenu. Cette production est gérée par le ContentWorkflowService (utilisant n8n) et l'AIContentProcessingService (interfaçage avec des IA génératives type Genspark AI). La sécurité (notamment pour MaâtCare avec ségrégation stricte des données de santé), la performance ("Netflix-like"), la disponibilité à 99%, et un modèle d'autorisation RBAC sont des piliers de cette architecture.

3. High-Level Overview
Style Architectural Principal : Microservices.
Hébergement : Cloud AWS.
Dépôt de Code : Monorepo.
Flux Principal : Clients (TV, Mobile) et Backoffice (Web) -> AWS API Gateway -> Microservices Backend. Communication inter-services via API internes synchrones ou Event Bus asynchrone. Bases de données dédiées par service.
Code snippet

graph LR
    subgraph Client Applications
        A[Android TV App (Kotlin/Jetpack Compose)]
        B[Future Mobile Apps]
        Backoffice_UI[Backoffice UI (React)]
    end

    C_RSS[External RSS Feeds]
    C_YT[YouTube API]

    subgraph AWS Cloud
        C_API[AWS API Gateway]

        subgraph Backend Microservices
            D1[User Service (Auth, Profiles, RBAC)]
            D2[MaâtTV Service (VOD, Channels, Sport)]
            D3[MaâtCare Service (Consultations, Avatars IA, Segregated Data)]
            D4[MaâtClass Service (Courses, Avatars IA)]
            D5[MaâtTube Service (Videos, Creators)]
            D6[Notification Service]
            D7[Avatar Interaction Service (Proxy/Logic)]
            D8[Search Service]
            D9[AIContentProcessingService (Genspark, etc.)]
            D10[BackofficeAPIService (Node.js/Express)]
            D11[ContentWorkflowService (n8n)]
        end

        E_Events[Event Bus (e.g., AWS EventBridge/SNS/SQS)]

        subgraph Data Stores
            F1[User DB (e.g., DynamoDB/PostgreSQL)]
            F2[TV Content DB (PostgreSQL for Series/User, DynamoDB for Items)]
            F3[Care Data DB (Segregated, Secured, RDS/Postgres)]
            F4[Class Content DB]
            F5[Tube Video DB]
            F6[Search Index (e.g., OpenSearch)]
            F7[Audit Log DB (Potentially dedicated)]
            F8[Redis (ElastiCache for Cache & Link Codes)]
            F10[Backoffice DB (PostgreSQL/Prisma)]
        end

        G_Shared[Shared Services (e.g., S3 for assets, CloudFront for CDN)]
        H_AI3D[AI/3D Services (Unreal/Heygen/Google - External/Managed)]
    end

    A --> C_API; B --> C_API; Backoffice_UI --> C_API;
    C_API --> D1; C_API --> D2; C_API --> D3; C_API --> D4; C_API --> D5; C_API --> D10;

    D10 <--> F10;

    D11 -- Polls --> C_RSS; D11 -- Interacts with --> C_YT;
    D11 -- Orchestrated by --> D10;
    D9 -- Orchestrated by --> D10;
    D10 -- Ingests content to --> D2;

    D1 <--> F1; D2 <--> F2; D2 <--> F8; D3 <--> F3; D4 <--> F4; D5 <--> F5; D8 <--> F6;

    D7 <--> H_AI3D; D2 <--> G_Shared; D5 <--> G_Shared; D4 <--> G_Shared;

    D1 -- Publishes Events --> E_Events; D2 -- Publishes Events --> E_Events; D3 -- Publishes Events --> E_Events;
    D4 -- Publishes Events --> E_Events; D5 -- Publishes Events --> E_Events;

    E_Events -- Consumed by --> D6; E_Events -- Consumed by --> D8;
5. Component View
(Services principaux listés dans le diagramme ci-dessus. Le détail du BackofficeAPIService & UI est maintenant affiné).

... (autres services comme avant) ...
BackofficeAPIService & UI: (Ajouté en V1.0, significativement détaillé en V1.2): Ce service est le cerveau de la production et de la gestion de contenu. Il est composé de deux parties principales :
Backend (BackofficeAPIService): Développé en Node.js avec Express et TypeScript, il expose une API REST pour gérer les streams, les agents IA, les workflows n8n, etc. Il interagit avec sa propre base de données PostgreSQL via l'ORM Prisma. Il est responsable de l'orchestration des services externes (comme Genspark AI ou un équivalent, via l'AIContentProcessingService) et de l'exécution des workflows n8n (via le ContentWorkflowService).
Frontend (Backoffice UI): Une interface d'administration riche développée en React 18 avec TypeScript, Tailwind CSS, et Shadcn/ui. Elle fournit des dashboards temps réel (via WebSockets), des tables de gestion (streams, agents, workflows), des formulaires de configuration, et des outils de reporting analytique.
Spécification Complète : La spécification technique complète pour ce composant (modèles de données Prisma, endpoints API, design des composants React, etc.) est définie par les prompts de référence fournis par le "Vibe CEO".
10. Definitive Tech Stack Selections (MVP)
(... autres technologies confirmées comme en V1.1 ...)

Backoffice Stack : La stack pour le Backoffice est confirmée comme étant React/TypeScript pour le frontend, et Node.js/Express/TypeScript pour le backend, avec PostgreSQL/Prisma comme base de données et Redis pour le cache.
11. Infrastructure and Deployment Overview
(... confirmé comme en V1.1 ...)

Déploiement Backoffice : Le backend du backoffice (Node.js) sera déployé comme un conteneur sur AWS ECS avec Fargate. Le frontend (React) sera déployé comme un site web statique sur AWS S3 et distribué via AWS CloudFront (CDN).
CI/CD : Une pipeline via GitHub Actions sera mise en place pour automatiser les tests, la création des images Docker, et le déploiement sur les environnements staging/production.
18. Change Log
Date	Version	Description	Author
2025-05-27	V1.2 (Détaillé)	Intégration des spécifications techniques détaillées pour le BackofficeAPIService & UI (stack, etc.).	Fred (Arch)
2025-05-27	V1.1 (Détaillé)	Ajout détails APIs, Modèles Données, Modules Internes pour MaâtTVService, presentationSettings.	Fred (Arch)
2025-05-27	V1.0 (Affiné)	Intégration des affinages post-checklist review (RBAC, politiques données, etc.)	Fred (Arch)
2025-05-26	V0.1 (YOLO)	Initial Draft Architecture	Fred (Arch)

Export to Sheets
Fred (Architecte) : Voilà pour le document d'architecture mis à jour en version 1.2. Il reflète maintenant une vision très complète et détaillée, en particulier pour le backoffice, en intégrant formellement vos spécifications.



------------------------------version precedente-------------------------------------

BMAD (Orchestrateur) : Vous avez absolument raison, mes excuses les plus sincères. J'ai bien fourni le PRD V1.1 dans mon message précédent, mais j'ai omis de poursuivre avec les deux autres documents comme promis. C'est une erreur de ma part dans le déroulement de notre flux. Je corrige cela immédiatement.

Voici le deuxième document, l'Architecture Document V1.1 (Affiné, avec détails Maât.TV). Vous pourrez le copier et le sauvegarder dans docs/architecture.md.

MaâtCore Architecture Document - V1.1 (Affiné, avec détails Maât.TV)
(Date: 27 mai 2025)

1. Introduction / Preamble
Ce document décrit l'architecture technique globale de la plateforme MaâtCore. Il s'appuie sur le Product Requirements Document (PRD V1.1) et les spécifications UI/UX initiales (V1.1 Conceptuelle). L'objectif est de définir une architecture modulaire, scalable, sécurisée et performante, capable de supporter l'ensemble des services (MaâtTV, MaâtCare, MaâtClass, MaâtTube) et l'expérience utilisateur innovante visée, tout en permettant un lancement MVP rapide et des évolutions futures. Ce document est destiné à guider les équipes de développement et d'infrastructure.

2. Technical Summary
MaâtCore sera architecturé comme un système de microservices hébergés sur AWS, avec un dépôt unique (Monorepo). Les applications clientes (Android TV en Kotlin/Jetpack Compose, futures applications mobiles) interagiront avec les services backend via une API Gateway. L'accent sera mis sur l'utilisation de services managés AWS, la préférence pour des solutions open-source matures, et une architecture événementielle (EDA) pour le découplage. Le MaâtTVService utilisera une approche de persistance hybride (PostgreSQL/RDS et DynamoDB, avec S3 pour les médias) pour gérer son catalogue de contenu varié, incluant des paramètres de présentation virtuelle (presentationSettings) pour une production dynamique. Un ContentWorkflowService (utilisant n8n) et un AIContentProcessingService alimenteront le catalogue via un BackofficeAPIService et une interface d'ingestion interne au MaâtTVService. La sécurité (notamment pour MaâtCare avec ségrégation stricte des données de santé), la performance ("Netflix-like"), la disponibilité à 99% (RTO 24h, RPO 1h), et un modèle d'autorisation RBAC sont des piliers de cette architecture. Le développement visera un lancement MVP rapide, acceptant une dette technique modérée qui sera gérée post-lancement.

3. High-Level Overview
Style Architectural Principal : Microservices.
Hébergement : Cloud AWS.
Dépôt de Code : Monorepo.
Flux Principal : Clients (TV, Mobile) -> AWS API Gateway -> Microservices Backend. Communication inter-services via API internes synchrones ou Event Bus asynchrone. Bases de données dédiées par service.
Code snippet

graph LR
    subgraph Client Applications
        A[Android TV App (Kotlin/Jetpack Compose)]
        B[Future Mobile Apps]
    end

    C_RSS[External RSS Feeds]
    C_YT[YouTube API]

    subgraph AWS Cloud
        C_API[AWS API Gateway]

        subgraph Backend Microservices
            D1[User Service (Auth, Profiles, RBAC)]
            D2[MaâtTV Service (VOD, Channels, Sport)]
            D3[MaâtCare Service (Consultations, Avatars IA, Segregated Data)]
            D4[MaâtClass Service (Courses, Avatars IA)]
            D5[MaâtTube Service (Videos, Creators)]
            D6[Notification Service]
            D7[Avatar Interaction Service (Proxy/Logic)]
            D8[Search Service]
            D9[AIContentProcessingService]
            D10[BackofficeAPIService & UI]
            D11[ContentWorkflowService (n8n)]
        end

        E_Events[Event Bus (e.g., AWS EventBridge/SNS/SQS)]

        subgraph Data Stores
            F1[User DB (e.g., DynamoDB/PostgreSQL)]
            F2[TV Content DB (PostgreSQL for Series/User, DynamoDB for Items)]
            F3[Care Data DB (Segregated, Secured, RDS/Postgres)]
            F4[Class Content DB]
            F5[Tube Video DB]
            F6[Search Index (e.g., OpenSearch)]
            F7[Audit Log DB (Potentially dedicated)]
            F8[Redis (ElastiCache for Chat MVP & Link Codes)]
        end

        G_Shared[Shared Services (e.g., S3 for assets, CloudFront for CDN)]
        H_AI3D[AI/3D Services (Unreal/Heygen/Google - External/Managed)]
    end

    A --> C_API; B --> C_API;
    C_API --> D1; C_API --> D2; C_API --> D3; C_API --> D4; C_API --> D5; C_API --> D10;

    D11 -- Polls --> C_RSS; D11 -- Interacts with --> C_YT;
    D11 -- Sends data to --> D9; D9 -- Sends processed data to --> D11;
    D11 -- Submits for review to --> D10; D10 -- Ingests content to --> D2;

    D1 <--> F1; D2 <--> F2; D2 <--> F8; D3 <--> F3; D4 <--> F4; D5 <--> F5; D8 <--> F6; D10 <--> F2;

    D7 <--> H_AI3D; D2 <--> G_Shared; D5 <--> G_Shared; D4 <--> G_Shared;

    D1 -- Publishes Events --> E_Events; D2 -- Publishes Events --> E_Events; D3 -- Publishes Events --> E_Events;
    D4 -- Publishes Events --> E_Events; D5 -- Publishes Events --> E_Events;

    E_Events -- Consumed by --> D6; E_Events -- Consumed by --> D8;
4. Architectural / Design Patterns Adopted
Microservices, API Gateway, Event-Driven Architecture (EDA), Database per Service (avec approche hybride pour Maât.TV), Serverless Functions (AWS Lambda), Containerization (Docker) & Orchestration (AWS ECS/Fargate), Infrastructure as Code (IaC), Monorepo, Repository Pattern (pour la DAL).
Justification : Robustesse, scalabilité, découplage, maintenabilité, alignement cloud natif, support du lancement MVP rapide.
5. Component View
(Services principaux listés dans le diagramme ci-dessus. Le détail du MaâtTVService est affiné ci-dessous. Les autres services conservent leur description de haut niveau de l'Architecture V1.0 pour l'instant.)

UserService: (Conforme à V1.0 : Gestion utilisateurs, authentification, profils, logique RBAC).
MaâtTVService: Gère le catalogue VOD ("MaâtFlix"), les chaînes thématiques (Info, Musique, Histoire, Podcasts Politiques incluant presentationSettings), l'expérience Sport "MaâtFoot" (commentaire live avatar basé sur input opérateur manuel, analyses 3D schématiques MVP, espace social avec chat à historique limité et interaction avatar). Reçoit le contenu validé via une interface d'ingestion interne.
Modules Internes Clés :
API Layer (REST Endpoints): Expose les APIs Maât.TV détaillées (Groupes 1 à 4 : Navigation, VOD, Chaînes Thématiques, Sport).
ContentCatalogModule: Logique d'agrégation pour /tv/home (approche mixte curation/récence MVP), récupération détails contenus, gestion des types hétérogènes via DAL. Logique pour charger toutes les infos de séries (saisons/épisodes résumés) pour MVP, avec conception évolutive.
StreamManagementModule: Gestion des URL de streaming (principalement liens S3/CloudFront).
SportExperienceModule: Orchestration du direct sportif (basé sur input opérateur manuel via Backoffice, mapping événement->script avatar), gestion analyses 3D, logique chat social (historique limité sur Redis/ElastiCache MVP, interaction avatar).
UserTVStateModule: Gestion "Reprendre la lecture" (liaison avec UserTVPlaybackState stocké en BDD), favoris (Post-MVP).
ContentIngestionInterface: Point d'entrée interne sécurisé (ex: POST /internal/ingestion/content) pour le contenu validé par le backoffice, incluant les presentationSettings (scène, avatar, tenue, lumière - sélection parmi options prédéfinies pour MVP).
SearchFeederModule: Notifie le SearchService des nouveaux contenus/mises à jour.
Data Access Layer (DAL): Utilise des Repositories spécifiques (ex: SeriesRepository sur PostgreSQL, MovieMetadataRepository sur DynamoDB) pour abstraire l'accès aux données pour Maât.TV.
MaâtCareService: (Esquisse V1.0, nécessitera un affinage similaire à Maât.TV).
MaâtClassService: (Esquisse V1.0, nécessitera un affinage similaire à Maât.TV).
MaâtTubeService: (Esquisse V1.0).
NotificationService: (Esquisse V1.0).
AvatarInteractionService: (Esquisse V1.0, doit maintenant gérer la sélection dynamique d'avatar, tenue, scène basée sur presentationSettings transmis par MaâtTVService ou d'autres).
SearchService: (Esquisse V1.0).
ContentWorkflowService (n8n): (Ajouté en V1.0, pour automatiser flux RSS -> AI -> Backoffice, et reconditionnement YouTube).
AIContentProcessingService: (Ajouté en V1.0, pour reconstruction infos, adaptation contenu YouTube en podcast).
BackofficeAPIService & UI: (Ajouté en V1.0, doit permettre la gestion des presentationSettings et la saisie des données live pour le sport MVP).
5.1. Authorization Model (RBAC) - MVP
Rôles Définis : ROLE_END_USER, ROLE_MAATTUBE_CREATOR, ROLE_MAATCARE_DOCTOR, ROLE_BACKOFFICE_MODERATOR, ROLE_PLATFORM_ADMIN.
Permissions Clés (Exemples) : maattv:content:view, maattube:video:upload, maatcare:consultation:access_assigned.
Implémentation : Rôles dans JWT (UserService), validation par API Gateway et microservices.
6. Project Structure (Monorepo Conceptual)
(Conforme à la V1.0 et au script init_maatcore_structure.sh : /apps pour clients (android-tv, mobile-web-signup), /services pour chaque microservice, /libs pour code partagé, /docs, /infra, /scripts.)

7. API Reference
(Cette section est maintenant significativement détaillée pour MaâtTVService. Le contenu complet des 19 endpoints avec leurs schémas JSON, comme discuté, serait inséré ici ou dans une Annexe dédiée.)

Référence : Les contrats d'API détaillés pour MaâtTVService (Endpoints 1 à 19, couvrant Navigation, VOD, Chaînes Thématiques Info/Musique/Histoire, et Expérience Sport) ont été définis et validés. Ils incluent les paramètres, les exemples de corps de requête/réponse JSON, et les codes de statut.
(Exemple :)

Endpoint 1: GET /tv/home (Réponse avec heroCarousel et contentRayons...)
... (tous les autres endpoints Maât.TV) ...
(Les APIs pour les autres microservices comme MaâtCareService, MaâtClassService, etc., seront détaillées lorsqu'on se concentrera sur ces services.)

8. Data Models
(Cette section est maintenant significativement détaillée pour MaâtTVService.)

8.1. Modèles de Données MaâtTVService (MVP)
BaseContentItem (Conceptuel): Inclut presentationSettings (OBJECT { virtualSetId, avatarPresenterId, avatarOutfitId, lightingPresetId, ... }) - Optionnel, fourni par Backoffice.
Movie, Series, Season, Episode, Documentary
NewsArticle: Inclut presentationSettings.
MusicTrack
HistorySegment: Inclut presentationSettings.
PoliticalPodcastEpisode: Inclut presentationSettings.
SportEvent: Inclut presentationSettings pour le studio du direct.
SportAnalysis3D: Inclut presentationSettings pour l'avatar analyste.
UserTVPlaybackState, UserFavoriteContent
LiveSportFeedData (pour le direct sportif, potentiellement sur Redis/cache). (Les attributs détaillés pour chaque entité, comme discuté, seraient listés ici ou dans une Annexe.)
9. Core Workflow / Sequence Diagrams
(L'exemple MaâtCare Handoff V1.0 reste. Ajout des diagrammes conceptuels pour :)

Pipeline d'Ingestion de Contenu Maât.TV : Opérateur/RSS/YouTube -> n8n -> AI Proc. -> Backoffice -> MaâtTVService Ingestion Interface (POST /internal/ingestion/content avec presentationSettings) -> ContentCatalogModule -> DBs & SearchFeeder.
Flux de Données Sport Live (MaâtFoot) : Opérateur Manuel (via Backoffice simple) -> SportExperienceModule dans MaâtTVService -> Client (WebSocket pour score/stats/eventFeed) & AvatarInteractionService (pour commentaire/animation).
10. Definitive Tech Stack Selections (MVP)
(Confirmé comme en V1.0, avec l'approche hybride BDD pour Maât.TV : PostgreSQL/RDS et DynamoDB, S3 pour médias, ElastiCache/Redis pour cache et chat sportif MVP. n8n pour workflows.)

11. Infrastructure and Deployment Overview
(Confirmé comme en V1.0, incluant les besoins pour l'environnement de dev : script d'install, VSCode, générateurs de code, testabilité locale des microservices, et Android TV: Kotlin & Jetpack Compose).

12. Error Handling Strategy (System-Wide)
(Confirmé comme en V1.0, avec messages utilisateur simples et clairs. Stratégies de dégradation gracieuse à définir par service lors de la conception détaillée.)

13. Coding Standards (High-Level)
(Confirmé comme en V1.0, avec gestion de la dette technique pour MVP et préférence pour open-source mature).

14. Overall Testing Strategy
(Confirmé comme en V1.0).

15. Security Best Practices
(Confirmé comme en V1.0, incluant le modèle RBAC, les exigences de conformité MaâtCare, les politiques de chiffrement (données MaâtCare MVP, toutes sauvegardes MaâtCare MVP ; chiffrement total et pistes d'audit avancées/anonymisation poussée sont Post-MVP), tests de sécurité, politique de confidentialité et consentement.)

16. Data Policies (High-Level)
Rétention des Données (PRD Req.) : Conformité légale et amélioration IA (via données anonymisées/pseudonymisées). Politiques exactes par type de données et par pays à définir avec conseil juridique.
Sauvegarde & Récupération (PRD Req.) : RTO max 24h, RPO max 1h. Mécanismes AWS spécifiques (RDS snapshots, S3 versioning/replication, DynamoDB backups) seront configurés pour atteindre ces objectifs.
17. Operational Considerations (High-Level)
Monitoring & Alerting (PRD Req.) : Temps de réponse services clés, alertes critiques pour défaillance services essentiels (MaâtCare) via CloudWatch.
Support (PRD Req.) : Utilisateur via chat en direct (IA V1) & FAQ. Technique via doc interne & outils de diagnostic.
18. Change Log
Date	Version	Description	Author
2025-05-27	V1.1 (Détaillé)	Ajout détails APIs, Modèles Données, Modules Internes pour MaâtTVService, presentationSettings.	Fred (Arch)
2025-05-27	V1.0 (Affiné)	Intégration des affinages post-checklist review (RBAC, politiques données, etc.)	Fred (Arch)
2025-05-26	V0.1 (YOLO)	Initial Draft Architecture	Fred (Arch)

Export to Sheets
BMAD (Orchestrateur) : Voilà pour l'Architecture Document V1.1.














--------------------------------------     ANCIENNE VERSION --------------------------------------



MaâtCore Architecture Document - V1.1 (Affiné, avec détails Maât.TV)
(Date: 27 mai 2025)

1. Introduction / Preamble
Ce document décrit l'architecture technique globale de la plateforme MaâtCore. Il s'appuie sur le Product Requirements Document (PRD V1.1) et les spécifications UI/UX initiales (V1.1 Conceptuelle). L'objectif est de définir une architecture modulaire, scalable, sécurisée et performante, capable de supporter l'ensemble des services (MaâtTV, MaâtCare, MaâtClass, MaâtTube) et l'expérience utilisateur innovante visée, tout en permettant un lancement MVP rapide et des évolutions futures. Ce document est destiné à guider les équipes de développement et d'infrastructure.

2. Technical Summary
MaâtCore sera architecturé comme un système de microservices hébergés sur AWS, avec un dépôt unique (Monorepo). Les applications clientes (Android TV en Kotlin/Jetpack Compose, futures applications mobiles) interagiront avec les services backend via une API Gateway. L'accent sera mis sur l'utilisation de services managés AWS, la préférence pour des solutions open-source matures, et une architecture événementielle (EDA) pour le découplage. Le MaâtTVService utilisera une approche de persistance hybride (PostgreSQL/RDS et DynamoDB, avec S3 pour les médias) pour gérer son catalogue de contenu varié, incluant des paramètres de présentation virtuelle (presentationSettings) pour une production dynamique. Un ContentWorkflowService (utilisant n8n) et un AIContentProcessingService alimenteront le catalogue via un BackofficeAPIService et une interface d'ingestion interne au MaâtTVService. La sécurité (notamment pour MaâtCare avec ségrégation stricte des données de santé), la performance ("Netflix-like"), la disponibilité à 99% (RTO 24h, RPO 1h), et un modèle d'autorisation RBAC sont des piliers de cette architecture. Le développement visera un lancement MVP rapide, acceptant une dette technique modérée qui sera gérée post-lancement.

3. High-Level Overview
Style Architectural Principal : Microservices.
Hébergement : Cloud AWS.
Dépôt de Code : Monorepo.
Flux Principal : Clients (TV, Mobile) -> AWS API Gateway -> Microservices Backend. Communication inter-services via API internes synchrones ou Event Bus asynchrone. Bases de données dédiées par service.
Code snippet

graph LR
    subgraph Client Applications
        A[Android TV App (Kotlin/Jetpack Compose)]
        B[Future Mobile Apps]
    end

    C_RSS[External RSS Feeds]
    C_YT[YouTube API]

    subgraph AWS Cloud
        C_API[AWS API Gateway]

        subgraph Backend Microservices
            D1[User Service (Auth, Profiles, RBAC)]
            D2[MaâtTV Service (VOD, Channels, Sport)]
            D3[MaâtCare Service (Consultations, Avatars IA, Segregated Data)]
            D4[MaâtClass Service (Courses, Avatars IA)]
            D5[MaâtTube Service (Videos, Creators)]
            D6[Notification Service]
            D7[Avatar Interaction Service (Proxy/Logic)]
            D8[Search Service]
            D9[AIContentProcessingService]
            D10[BackofficeAPIService & UI]
            D11[ContentWorkflowService (n8n)]
        end

        E_Events[Event Bus (e.g., AWS EventBridge/SNS/SQS)]

        subgraph Data Stores
            F1[User DB (e.g., DynamoDB/PostgreSQL)]
            F2[TV Content DB (PostgreSQL for Series/User, DynamoDB for Items)]
            F3[Care Data DB (Segregated, Secured, RDS/Postgres)]
            F4[Class Content DB]
            F5[Tube Video DB]
            F6[Search Index (e.g., OpenSearch)]
            F7[Audit Log DB (Potentially dedicated)]
            F8[Redis (ElastiCache for Chat MVP & Link Codes)]
        end

        G_Shared[Shared Services (e.g., S3 for assets, CloudFront for CDN)]
        H_AI3D[AI/3D Services (Unreal/Heygen/Google - External/Managed)]
    end

    A --> C_API; B --> C_API;
    C_API --> D1; C_API --> D2; C_API --> D3; C_API --> D4; C_API --> D5; C_API --> D10;

    D11 -- Polls --> C_RSS; D11 -- Interacts with --> C_YT;
    D11 -- Sends data to --> D9; D9 -- Sends processed data to --> D11;
    D11 -- Submits for review to --> D10; D10 -- Ingests content to --> D2;

    D1 <--> F1; D2 <--> F2; D2 <--> F8; D3 <--> F3; D4 <--> F4; D5 <--> F5; D8 <--> F6; D10 <--> F2;

    D7 <--> H_AI3D; D2 <--> G_Shared; D5 <--> G_Shared; D4 <--> G_Shared;

    D1 -- Publishes Events --> E_Events; D2 -- Publishes Events --> E_Events; D3 -- Publishes Events --> E_Events;
    D4 -- Publishes Events --> E_Events; D5 -- Publishes Events --> E_Events;

    E_Events -- Consumed by --> D6; E_Events -- Consumed by --> D8;
4. Architectural / Design Patterns Adopted
Microservices, API Gateway, Event-Driven Architecture (EDA), Database per Service (avec approche hybride pour Maât.TV), Serverless Functions (AWS Lambda), Containerization (Docker) & Orchestration (AWS ECS/Fargate), Infrastructure as Code (IaC), Monorepo, Repository Pattern (pour la DAL).
Justification : Robustesse, scalabilité, découplage, maintenabilité, alignement cloud natif, support du lancement MVP rapide.
5. Component View
(Services principaux listés dans le diagramme ci-dessus. Le détail du MaâtTVService est affiné ci-dessous. Les autres services conservent leur description de haut niveau de l'Architecture V1.0 pour l'instant.)

UserService: (Conforme à V1.0 : Gestion utilisateurs, authentification, profils, logique RBAC).
MaâtTVService: Gère le catalogue VOD ("MaâtFlix"), les chaînes thématiques (Info, Musique, Histoire, Podcasts Politiques incluant presentationSettings), l'expérience Sport "MaâtFoot" (commentaire live avatar basé sur input opérateur manuel, analyses 3D schématiques MVP, espace social avec chat à historique limité et interaction avatar). Reçoit le contenu validé via une interface d'ingestion interne.
Modules Internes Clés :
API Layer (REST Endpoints): Expose les APIs Maât.TV détaillées (Groupes 1 à 4 : Navigation, VOD, Chaînes Thématiques, Sport).
ContentCatalogModule: Logique d'agrégation pour /tv/home (approche mixte curation/récence MVP), récupération détails contenus, gestion des types hétérogènes via DAL. Logique pour charger toutes les infos de séries (saisons/épisodes résumés) pour MVP, avec conception évolutive.
StreamManagementModule: Gestion des URL de streaming (principalement liens S3/CloudFront).
SportExperienceModule: Orchestration du direct sportif (basé sur input opérateur manuel via Backoffice, mapping événement->script avatar), gestion analyses 3D, logique chat social (historique limité sur Redis/ElastiCache MVP, interaction avatar).
UserTVStateModule: Gestion "Reprendre la lecture" (liaison avec UserTVPlaybackState stocké en BDD), favoris (Post-MVP).
ContentIngestionInterface: Point d'entrée interne sécurisé (ex: POST /internal/ingestion/content) pour le contenu validé par le backoffice, incluant les presentationSettings (scène, avatar, tenue, lumière - sélection parmi options prédéfinies pour MVP).
SearchFeederModule: Notifie le SearchService des nouveaux contenus/mises à jour.
Data Access Layer (DAL): Utilise des Repositories spécifiques (ex: SeriesRepository sur PostgreSQL, MovieMetadataRepository sur DynamoDB) pour abstraire l'accès aux données pour Maât.TV.
MaâtCareService: (Esquisse V1.0, nécessitera un affinage similaire à Maât.TV).
MaâtClassService: (Esquisse V1.0, nécessitera un affinage similaire à Maât.TV).
MaâtTubeService: (Esquisse V1.0).
NotificationService: (Esquisse V1.0).
AvatarInteractionService: (Esquisse V1.0, doit maintenant gérer la sélection dynamique d'avatar, tenue, scène basée sur presentationSettings transmis par MaâtTVService ou d'autres).
SearchService: (Esquisse V1.0).
ContentWorkflowService (n8n): (Ajouté en V1.0, pour automatiser flux RSS -> AI -> Backoffice, et reconditionnement YouTube).
AIContentProcessingService: (Ajouté en V1.0, pour reconstruction infos, adaptation contenu YouTube en podcast).
BackofficeAPIService & UI: (Ajouté en V1.0, doit permettre la gestion des presentationSettings et la saisie des données live pour le sport MVP).
5.1. Authorization Model (RBAC) - MVP
Rôles Définis : ROLE_END_USER, ROLE_MAATTUBE_CREATOR, ROLE_MAATCARE_DOCTOR, ROLE_BACKOFFICE_MODERATOR, ROLE_PLATFORM_ADMIN.
Permissions Clés (Exemples) : maattv:content:view, maattube:video:upload, maatcare:consultation:access_assigned.
Implémentation : Rôles dans JWT (UserService), validation par API Gateway et microservices.
6. Project Structure (Monorepo Conceptual)
(Conforme à la V1.0 et au script init_maatcore_structure.sh : /apps pour clients (android-tv, mobile-web-signup), /services pour chaque microservice, /libs pour code partagé, /docs, /infra, /scripts.)

7. API Reference
(Cette section est maintenant significativement détaillée pour MaâtTVService. Le contenu complet des 19 endpoints avec leurs schémas JSON, comme discuté, serait inséré ici ou dans une Annexe dédiée.)

Référence : Les contrats d'API détaillés pour MaâtTVService (Endpoints 1 à 19, couvrant Navigation, VOD, Chaînes Thématiques Info/Musique/Histoire, et Expérience Sport) ont été définis et validés. Ils incluent les paramètres, les exemples de corps de requête/réponse JSON, et les codes de statut.
(Exemple :)

Endpoint 1: GET /tv/home (Réponse avec heroCarousel et contentRayons...)
... (tous les autres endpoints Maât.TV) ...
(Les APIs pour les autres microservices comme MaâtCareService, MaâtClassService, etc., seront détaillées lorsqu'on se concentrera sur ces services.)

8. Data Models
(Cette section est maintenant significativement détaillée pour MaâtTVService.)

8.1. Modèles de Données MaâtTVService (MVP)
BaseContentItem (Conceptuel): Inclut presentationSettings (OBJECT { virtualSetId, avatarPresenterId, avatarOutfitId, lightingPresetId, ... }) - Optionnel, fourni par Backoffice.
Movie, Series, Season, Episode, Documentary
NewsArticle: Inclut presentationSettings.
MusicTrack
HistorySegment: Inclut presentationSettings.
PoliticalPodcastEpisode: Inclut presentationSettings.
SportEvent: Inclut presentationSettings pour le studio du direct.
SportAnalysis3D: Inclut presentationSettings pour l'avatar analyste.
UserTVPlaybackState, UserFavoriteContent
LiveSportFeedData (pour le direct sportif, potentiellement sur Redis/cache). (Les attributs détaillés pour chaque entité, comme discuté, seraient listés ici ou dans une Annexe.)
9. Core Workflow / Sequence Diagrams
(L'exemple MaâtCare Handoff V1.0 reste. Ajout des diagrammes conceptuels pour :)

Pipeline d'Ingestion de Contenu Maât.TV : Opérateur/RSS/YouTube -> n8n -> AI Proc. -> Backoffice -> MaâtTVService Ingestion Interface (POST /internal/ingestion/content avec presentationSettings) -> ContentCatalogModule -> DBs & SearchFeeder.
Flux de Données Sport Live (MaâtFoot) : Opérateur Manuel (via Backoffice simple) -> SportExperienceModule dans MaâtTVService -> Client (WebSocket pour score/stats/eventFeed) & AvatarInteractionService (pour commentaire/animation).
10. Definitive Tech Stack Selections (MVP)
(Confirmé comme en V1.0, avec l'approche hybride BDD pour Maât.TV : PostgreSQL/RDS et DynamoDB, S3 pour médias, ElastiCache/Redis pour cache et chat sportif MVP. n8n pour workflows.)

11. Infrastructure and Deployment Overview
(Confirmé comme en V1.0, incluant les besoins pour l'environnement de dev : script d'install, VSCode, générateurs de code, testabilité locale des microservices, et Android TV: Kotlin & Jetpack Compose).

12. Error Handling Strategy (System-Wide)
(Confirmé comme en V1.0, avec messages utilisateur simples et clairs. Stratégies de dégradation gracieuse à définir par service lors de la conception détaillée.)

13. Coding Standards (High-Level)
(Confirmé comme en V1.0, avec gestion de la dette technique pour MVP et préférence pour open-source mature).

14. Overall Testing Strategy
(Confirmé comme en V1.0).

15. Security Best Practices
(Confirmé comme en V1.0, incluant le modèle RBAC, les exigences de conformité MaâtCare, les politiques de chiffrement (données MaâtCare MVP, toutes sauvegardes MaâtCare MVP ; chiffrement total et pistes d'audit avancées/anonymisation poussée sont Post-MVP), tests de sécurité, politique de confidentialité et consentement.)

16. Data Policies (High-Level)
Rétention des Données (PRD Req.) : Conformité légale et amélioration IA (via données anonymisées/pseudonymisées). Politiques exactes par type de données et par pays à définir avec conseil juridique.
Sauvegarde & Récupération (PRD Req.) : RTO max 24h, RPO max 1h. Mécanismes AWS spécifiques (RDS snapshots, S3 versioning/replication, DynamoDB backups) seront configurés pour atteindre ces objectifs.
17. Operational Considerations (High-Level)
Monitoring & Alerting (PRD Req.) : Temps de réponse services clés, alertes critiques pour défaillance services essentiels (MaâtCare) via CloudWatch.
Support (PRD Req.) : Utilisateur via chat en direct (IA V1) & FAQ. Technique via doc interne & outils de diagnostic.
18. Change Log
Date	Version	Description	Author
2025-05-27	V1.1 (Détaillé)	Ajout détails APIs, Modèles Données, Modules Internes pour MaâtTVService, presentationSettings.	Fred (Arch)
2025-05-27	V1.0 (Affiné)	Intégration des affinages post-checklist review (RBAC, politiques données, etc.)	Fred (Arch)
2025-05-26	V0.1 (YOLO)	Initial Draft Architecture