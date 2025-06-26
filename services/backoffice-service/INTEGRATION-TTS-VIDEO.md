# Intégration TTS et Génération Vidéo

## Vue d'ensemble

Le service backoffice MaâtCore intègre maintenant deux solutions pour la génération de contenu multimédia :

- **ElevenLabs** : Synthèse vocale de haute qualité
- **HeyGen** : Génération de vidéos avec avatars IA

## Configuration

### Variables d'environnement

```env
# ElevenLabs Configuration
TTS_SERVICE_URL=https://api.elevenlabs.io/v1/text-to-speech
TTS_API_KEY=your-elevenlabs-api-key
TTS_DEFAULT_VOICE=21m00Tcm4TlvDq8ikWAM
TTS_MODEL=eleven_multilingual_v2

# HeyGen Configuration
HEYGEN_API_KEY=your-heygen-api-key
HEYGEN_API_URL=https://api.heygen.com/v2
HEYGEN_DEFAULT_AVATAR=your-default-avatar-id
HEYGEN_DEFAULT_VOICE=your-default-voice-id
HEYGEN_VOICE_FR=french-voice-id
HEYGEN_VOICE_EN=english-voice-id
HEYGEN_VOICE_ES=spanish-voice-id
HEYGEN_VOICE_DE=german-voice-id
HEYGEN_VOICE_IT=italian-voice-id
HEYGEN_DEFAULT_QUALITY=medium
HEYGEN_WEBHOOK_URL=https://your-domain.com/api/heygen/webhook
```

## Utilisation

### Génération Audio (ElevenLabs)

```typescript
import { generateMedia, getDefaultTTSConfig } from '../utils/tts';

// Configuration par défaut
const config = getDefaultTTSConfig('elevenlabs');

// Génération audio
const result = await generateMedia(text, config);
console.log('Audio URL:', result.audioUrl);
```

### Génération Vidéo (HeyGen)

```typescript
import { generateMedia, getDefaultVideoConfig } from '../utils/tts';

// Configuration vidéo
const videoConfig = getDefaultVideoConfig();
const config = {
  provider: 'heygen',
  voiceId: videoConfig.voiceId,
  model: 'heygen_v1',
  language: 'fr',
  avatarId: videoConfig.avatarId,
  videoQuality: 'medium',
  backgroundId: videoConfig.backgroundId,
  enableGestures: true
};

// Génération vidéo
const result = await generateMedia(text, config);
console.log('Video URL:', result.videoUrl);
console.log('Thumbnail URL:', result.thumbnailUrl);
```

## API Endpoints

### Génération Audio pour Diffusion

```http
POST /api/news/broadcasts/:id/generate-audio
Content-Type: application/json
Authorization: Bearer <token>

{
  "voiceId": "21m00Tcm4TlvDq8ikWAM",
  "model": "eleven_multilingual_v2",
  "stability": 0.5,
  "clarity": 0.75,
  "speed": 1.0
}
```

### Génération Vidéo pour Diffusion

```http
POST /api/news/broadcasts/:id/generate-video
Content-Type: application/json
Authorization: Bearer <token>

{
  "avatarId": "your-avatar-id",
  "voiceId": "your-voice-id",
  "quality": "medium",
  "aspectRatio": "16:9",
  "backgroundId": "your-background-id",
  "enableGestures": true
}
```

## Interfaces TypeScript

### Configuration TTS

```typescript
interface TTSConfig {
  provider: 'elevenlabs' | 'azure' | 'google' | 'heygen';
  voiceId: string;
  model: string;
  language: string;
  stability?: number;
  clarity?: number;
  speed?: number;
  pitch?: number;
  // HeyGen specific
  avatarId?: string;
  videoQuality?: 'low' | 'medium' | 'high' | 'ultra';
  backgroundId?: string;
  enableGestures?: boolean;
}
```

### Configuration Vidéo

```typescript
interface VideoConfig {
  avatarId: string;
  voiceId: string;
  quality: 'low' | 'medium' | 'high' | 'ultra';
  aspectRatio: '16:9' | '9:16' | '1:1';
  backgroundId?: string;
  enableGestures: boolean;
  duration?: number;
}
```

### Résultats

```typescript
interface TTSResult {
  audioUrl: string;
  duration: number;
  textLength: number;
  voiceId: string;
}

interface VideoResult {
  videoUrl: string;
  audioUrl: string;
  thumbnailUrl: string;
  duration: number;
  jobId: string;
  status: 'pending' | 'processing' | 'completed' | 'failed';
  error?: string;
}

type MediaResult = TTSResult | VideoResult;
```

## Workflow de Génération

### 1. Préparation du Contenu

```typescript
// Formatage du texte pour TTS
const formattedText = formatTextForTTS(originalText);

// Calcul de la durée estimée
const estimatedDuration = calculateDuration(formattedText);
```

### 2. Génération Asynchrone

```typescript
// Mise à jour du statut
await prisma.newsBroadcast.update({
  where: { id: broadcastId },
  data: { status: 'PREPARING' }
});

// Génération du média
const result = await generateMedia(text, config);

// Mise à jour avec les résultats
await prisma.newsBroadcast.update({
  where: { id: broadcastId },
  data: {
    status: 'READY',
    audioUrl: result.audioUrl,
    videoUrl: result.videoUrl, // Si vidéo
    thumbnailUrl: result.thumbnailUrl, // Si vidéo
    duration: result.duration
  }
});
```

### 3. Gestion des Erreurs

```typescript
try {
  const result = await generateMedia(text, config);
  // Traitement du succès
} catch (error) {
  logger.error('Media generation failed:', error);
  await prisma.newsBroadcast.update({
    where: { id: broadcastId },
    data: { status: 'FAILED' }
  });
}
```

## Validation

### Configuration TTS

```typescript
import { validateTTSConfig } from '../utils/tts';

const validation = validateTTSConfig(config);
if (!validation.valid) {
  console.error('Configuration errors:', validation.errors);
}
```

### Configuration Vidéo

```typescript
import { validateVideoConfig } from '../utils/tts';

const validation = validateVideoConfig(videoConfig);
if (!validation.valid) {
  console.error('Video config errors:', validation.errors);
}
```

## Voix Disponibles

### ElevenLabs

- **Français** : `21m00Tcm4TlvDq8ikWAM` (Agent 21st - Voix par défaut)
- **Anglais** : Configuré via `TTS_DEFAULT_VOICE`
- **Autres langues** : Configurables via les variables d'environnement

### HeyGen

- **Français** : Configuré via `HEYGEN_VOICE_FR`
- **Anglais** : Configuré via `HEYGEN_VOICE_EN`
- **Espagnol** : Configuré via `HEYGEN_VOICE_ES`
- **Allemand** : Configuré via `HEYGEN_VOICE_DE`
- **Italien** : Configuré via `HEYGEN_VOICE_IT`

## Qualités Vidéo

- **low** : 480p, traitement rapide
- **medium** : 720p, équilibre qualité/vitesse (par défaut)
- **high** : 1080p, haute qualité
- **ultra** : 4K, qualité maximale

## Formats de Sortie

### Audio (ElevenLabs)
- Format : MP3
- Qualité : 44.1kHz, 128kbps
- Stockage : Cloud storage avec URL publique

### Vidéo (HeyGen)
- Format : MP4
- Codec : H.264
- Audio : AAC
- Résolutions : 480p à 4K selon la qualité
- Ratios : 16:9, 9:16, 1:1

## Monitoring et Logs

```typescript
// Logs de génération
logger.info(`Starting ${mediaType} generation for broadcast ${broadcastId}`);
logger.info(`${mediaType} generation completed for broadcast ${broadcastId}`);
logger.error(`${mediaType} generation failed for broadcast ${broadcastId}:`, error);

// Métriques
- Durée de génération
- Taille du fichier
- Qualité de sortie
- Taux de succès/échec
```

## Sécurité

- Clés API stockées dans les variables d'environnement
- Validation des entrées utilisateur
- Limitation du taux de requêtes
- Authentification requise pour tous les endpoints
- Rôles requis : `BACKOFFICE_MODERATOR` ou `PLATFORM_ADMIN`

## Limitations

### ElevenLabs
- Limite de caractères par requête
- Quota mensuel selon l'abonnement
- Langues supportées limitées

### HeyGen
- Durée maximale de vidéo : 5 minutes
- Temps de traitement plus long pour les vidéos
- Coût plus élevé que l'audio seul
- Avatars disponibles selon l'abonnement

## Dépannage

### Erreurs Communes

1. **API Key invalide**
   ```
   Error: Unauthorized - Check your API key
   ```
   Solution : Vérifier les variables d'environnement

2. **Quota dépassé**
   ```
   Error: Rate limit exceeded
   ```
   Solution : Attendre ou upgrader l'abonnement

3. **Texte trop long**
   ```
   Error: Text exceeds maximum length
   ```
   Solution : Diviser le texte en segments plus courts

4. **Avatar non disponible**
   ```
   Error: Avatar not found or not accessible
   ```
   Solution : Vérifier l'ID de l'avatar et les permissions

### Debug

```typescript
// Activer les logs détaillés
process.env.DEBUG = 'tts:*,heygen:*';

// Tester la configuration
const config = getDefaultTTSConfig('elevenlabs');
console.log('TTS Config:', config);

const videoConfig = getDefaultVideoConfig();
console.log('Video Config:', videoConfig);
```