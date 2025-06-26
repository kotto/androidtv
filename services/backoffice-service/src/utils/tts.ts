import axios from 'axios';
import { logger } from './logger';

/**
 * TTS Configuration interface
 */
export interface TTSConfig {
  provider: 'elevenlabs' | 'azure' | 'google' | 'heygen';
  voiceId: string;
  model: string;
  language: string;
  speed?: number;
  pitch?: number;
  stability?: number;
  clarity?: number;
  // HeyGen specific options
  avatarId?: string;
  videoQuality?: 'low' | 'medium' | 'high' | 'ultra';
  backgroundId?: string;
  enableGestures?: boolean;
}

/**
 * Video Generation Configuration for HeyGen
 */
export interface VideoConfig {
  avatarId: string;
  voiceId: string;
  backgroundId?: string;
  quality: 'low' | 'medium' | 'high' | 'ultra';
  enableGestures: boolean;
  aspectRatio: '16:9' | '9:16' | '1:1';
  duration?: number;
}

/**
 * TTS Generation Result
 */
export interface TTSResult {
  success: boolean;
  audioUrl?: string;
  duration?: number;
  error?: string;
}

/**
 * Video Generation Result for HeyGen
 */
export interface VideoResult {
  success: boolean;
  videoUrl?: string;
  thumbnailUrl?: string;
  duration?: number;
  jobId?: string;
  status?: 'pending' | 'processing' | 'completed' | 'failed';
  error?: string;
}

/**
 * Combined Media Result (Audio + Video)
 */
export interface MediaResult {
  success: boolean;
  type: 'audio' | 'video' | 'both';
  audioUrl?: string;
  videoUrl?: string;
  thumbnailUrl?: string;
  duration?: number;
  error?: string;
}

/**
 * Format text for optimal TTS output
 * @param text - Raw text content
 * @param language - Target language (default: 'fr')
 * @returns Formatted text optimized for TTS
 */
export const formatTextForTTS = async (text: string, language: string = 'fr'): Promise<string> => {
  try {
    let formattedText = text;

    // Remove HTML tags
    formattedText = formattedText.replace(/<[^>]*>/g, '');

    // Remove special characters that cause TTS issues
    formattedText = formattedText.replace(/[\u2018\u2019]/g, "'"); // Smart quotes
    formattedText = formattedText.replace(/[\u201C\u201D]/g, '"'); // Smart double quotes
    formattedText = formattedText.replace(/[\u2013\u2014]/g, '-'); // Em/en dashes
    formattedText = formattedText.replace(/[\u2026]/g, '...'); // Ellipsis

    // Handle French-specific formatting
    if (language === 'fr') {
      // Add pronunciation guides for common abbreviations
      formattedText = formattedText.replace(/\bM\./g, 'Monsieur');
      formattedText = formattedText.replace(/\bMme\./g, 'Madame');
      formattedText = formattedText.replace(/\bMlle\./g, 'Mademoiselle');
      formattedText = formattedText.replace(/\bDr\./g, 'Docteur');
      formattedText = formattedText.replace(/\bPr\./g, 'Professeur');
      formattedText = formattedText.replace(/\betc\./g, 'et cetera');
      formattedText = formattedText.replace(/\bc\.à\.d\./g, "c'est-à-dire");
      
      // Handle common French acronyms
      formattedText = formattedText.replace(/\bUE\b/g, 'Union Européenne');
      formattedText = formattedText.replace(/\bONU\b/g, 'Organisation des Nations Unies');
      formattedText = formattedText.replace(/\bOMS\b/g, 'Organisation Mondiale de la Santé');
      formattedText = formattedText.replace(/\bPIB\b/g, 'Produit Intérieur Brut');
      formattedText = formattedText.replace(/\bTVA\b/g, 'Taxe sur la Valeur Ajoutée');
    }

    // Handle numbers and dates
    formattedText = await formatNumbersAndDates(formattedText, language);

    // Add natural pauses
    formattedText = formattedText.replace(/\. /g, '. <break time="0.5s"/> ');
    formattedText = formattedText.replace(/\, /g, ', <break time="0.2s"/> ');
    formattedText = formattedText.replace(/\; /g, '; <break time="0.3s"/> ');
    formattedText = formattedText.replace(/\: /g, ': <break time="0.3s"/> ');

    // Clean up extra whitespace
    formattedText = formattedText.replace(/\s+/g, ' ').trim();

    return formattedText;
  } catch (error) {
    logger.error('Error formatting text for TTS:', error);
    return text; // Return original text if formatting fails
  }
};

/**
 * Format numbers and dates for TTS
 * @param text - Text containing numbers and dates
 * @param language - Target language
 * @returns Text with formatted numbers and dates
 */
const formatNumbersAndDates = async (text: string, language: string): Promise<string> => {
  let formattedText = text;

  if (language === 'fr') {
    // Format large numbers
    formattedText = formattedText.replace(/\b(\d{1,3}(?:\s\d{3})+)\b/g, (match) => {
      const number = parseInt(match.replace(/\s/g, ''));
      return numberToFrenchWords(number);
    });

    // Format percentages
    formattedText = formattedText.replace(/(\d+(?:,\d+)?)\s*%/g, '$1 pour cent');

    // Format dates (DD/MM/YYYY)
    formattedText = formattedText.replace(/(\d{1,2})\/(\d{1,2})\/(\d{4})/g, (match, day, month, year) => {
      const monthNames = [
        'janvier', 'février', 'mars', 'avril', 'mai', 'juin',
        'juillet', 'août', 'septembre', 'octobre', 'novembre', 'décembre'
      ];
      const monthName = monthNames[parseInt(month) - 1] || month;
      return `${day} ${monthName} ${year}`;
    });

    // Format time (HH:MM)
    formattedText = formattedText.replace(/(\d{1,2}):(\d{2})/g, '$1 heures $2');
    formattedText = formattedText.replace(/(\d{1,2}) heures 00/g, '$1 heures');
  }

  return formattedText;
};

/**
 * Convert number to French words (simplified version)
 * @param num - Number to convert
 * @returns French word representation
 */
const numberToFrenchWords = (num: number): string => {
  if (num < 1000) return num.toString();
  
  if (num < 1000000) {
    const thousands = Math.floor(num / 1000);
    const remainder = num % 1000;
    if (remainder === 0) {
      return `${thousands} mille`;
    }
    return `${thousands} mille ${remainder}`;
  }
  
  if (num < 1000000000) {
    const millions = Math.floor(num / 1000000);
    const remainder = num % 1000000;
    if (remainder === 0) {
      return `${millions} ${millions === 1 ? 'million' : 'millions'}`;
    }
    return `${millions} ${millions === 1 ? 'million' : 'millions'} ${numberToFrenchWords(remainder)}`;
  }
  
  return num.toString(); // Fallback for very large numbers
};

/**
 * Calculate estimated duration for TTS audio
 * @param text - Text content
 * @param wordsPerMinute - Speaking rate (default: 150 WPM)
 * @returns Estimated duration in seconds
 */
export const calculateDuration = (text: string, wordsPerMinute: number = 150): number => {
  try {
    // Remove SSML tags for word counting
    const cleanText = text.replace(/<[^>]*>/g, '');
    
    // Count words
    const words = cleanText.trim().split(/\s+/).length;
    
    // Calculate duration in seconds
    const durationMinutes = words / wordsPerMinute;
    const durationSeconds = Math.ceil(durationMinutes * 60);
    
    // Add buffer for pauses and natural speech patterns
    return Math.ceil(durationSeconds * 1.2);
  } catch (error) {
    logger.error('Error calculating TTS duration:', error);
    return 60; // Default fallback duration
  }
};

/**
 * Generate TTS audio using various providers
 * @param text - Text to convert to speech
 * @param config - TTS configuration
 * @returns TTS generation result
 */
export const generateTTSAudio = async (text: string, config: TTSConfig): Promise<TTSResult> => {
  try {
    if (config.provider === 'elevenlabs') {
      const apiKey = process.env.TTS_API_KEY || process.env.ELEVENLABS_API_KEY;
      const baseUrl = process.env.TTS_SERVICE_URL || 'https://api.elevenlabs.io/v1';
      
      if (!apiKey) {
        throw new Error('ElevenLabs API key not configured');
      }
      
      return await generateElevenLabsAudio(text, config, apiKey, baseUrl);
    }
    
    if (config.provider === 'heygen') {
      // For HeyGen, we generate video instead of just audio
      throw new Error('Use generateHeyGenVideo for HeyGen provider');
    }
    
    // Add other providers here (Azure, Google, etc.)
    throw new Error(`TTS provider '${config.provider}' not implemented`);
  } catch (error) {
    logger.error('Error generating TTS audio:', error);
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Unknown error'
    };
  }
};

/**
 * Generate video with HeyGen API
 * @param text - Text to convert to video
 * @param config - Video configuration
 * @returns Video generation result
 */
export const generateHeyGenVideo = async (text: string, config: VideoConfig): Promise<VideoResult> => {
  try {
    const apiKey = process.env.HEYGEN_API_KEY;
    const baseUrl = process.env.HEYGEN_API_URL || 'https://api.heygen.com/v2';
    
    if (!apiKey) {
      throw new Error('HeyGen API key not configured');
    }

    const response = await axios.post(
      `${baseUrl}/video/generate`,
      {
        video_inputs: [{
          character: {
            type: 'avatar',
            avatar_id: config.avatarId,
            avatar_style: 'normal'
          },
          voice: {
            type: 'text',
            input_text: text,
            voice_id: config.voiceId
          },
          background: config.backgroundId ? {
            type: 'image',
            url: config.backgroundId
          } : undefined
        }],
        dimension: {
          width: config.aspectRatio === '16:9' ? 1920 : config.aspectRatio === '9:16' ? 1080 : 1080,
          height: config.aspectRatio === '16:9' ? 1080 : config.aspectRatio === '9:16' ? 1920 : 1080
        },
        aspect_ratio: config.aspectRatio,
        quality: config.quality
      },
      {
        headers: {
          'X-API-Key': apiKey,
          'Content-Type': 'application/json'
        }
      }
    );

    const jobId = response.data.data.video_id;
    
    logger.info('HeyGen video generation started', {
      jobId,
      textLength: text.length,
      avatarId: config.avatarId
    });

    return {
      success: true,
      jobId,
      status: 'pending',
      duration: calculateDuration(text)
    };
  } catch (error) {
    logger.error('HeyGen video generation failed:', error);
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Unknown error'
    };
  }
};

/**
 * Check HeyGen video generation status
 * @param jobId - Video generation job ID
 * @returns Video status and URL if completed
 */
export const checkHeyGenVideoStatus = async (jobId: string): Promise<VideoResult> => {
  try {
    const apiKey = process.env.HEYGEN_API_KEY;
    const baseUrl = process.env.HEYGEN_API_URL || 'https://api.heygen.com/v2';
    
    if (!apiKey) {
      throw new Error('HeyGen API key not configured');
    }

    const response = await axios.get(
      `${baseUrl}/video/${jobId}`,
      {
        headers: {
          'X-API-Key': apiKey
        }
      }
    );

    const data = response.data.data;
    
    return {
      success: true,
      jobId,
      status: data.status,
      videoUrl: data.status === 'completed' ? data.video_url : undefined,
      thumbnailUrl: data.thumbnail_url,
      duration: data.duration
    };
  } catch (error) {
    logger.error('Error checking HeyGen video status:', error);
    return {
      success: false,
      error: error instanceof Error ? error.message : 'Unknown error'
    };
  }
};

/**
 * Generate media (audio or video) based on provider
 * @param text - Text content
 * @param config - TTS/Video configuration
 * @returns Media generation result
 */
export const generateMedia = async (text: string, config: TTSConfig): Promise<MediaResult> => {
  try {
    if (config.provider === 'heygen') {
      if (!config.avatarId) {
        throw new Error('Avatar ID required for HeyGen video generation');
      }
      
      const videoConfig: VideoConfig = {
        avatarId: config.avatarId,
        voiceId: config.voiceId,
        backgroundId: config.backgroundId,
        quality: config.videoQuality || 'medium',
        enableGestures: config.enableGestures || true,
        aspectRatio: '16:9'
      };
      
      const result = await generateHeyGenVideo(text, videoConfig);
      
      return {
        success: result.success,
        type: 'video',
        videoUrl: result.videoUrl,
        thumbnailUrl: result.thumbnailUrl,
        duration: result.duration,
        error: result.error
      };
    } else {
      // Generate audio for other providers
      const result = await generateTTSAudio(text, config);
      
      return {
        success: result.success,
        type: 'audio',
        audioUrl: result.audioUrl,
        duration: result.duration,
        error: result.error
      };
    }
  } catch (error) {
    logger.error('Error generating media:', error);
    return {
      success: false,
      type: 'audio',
      error: error instanceof Error ? error.message : 'Unknown error'
    };
  }
};

/**
 * Generate audio using ElevenLabs API
 * @param text - Text to convert
 * @param config - TTS configuration
 * @param apiKey - API key
 * @param baseUrl - Base URL for API
 * @returns TTS result
 */
const generateElevenLabsAudio = async (
  text: string,
  config: TTSConfig,
  apiKey: string,
  baseUrl: string
): Promise<TTSResult> => {
  try {
    const response = await axios.post(
      `${baseUrl}/text-to-speech/${config.voiceId}`,
      {
        text,
        model_id: config.model || 'eleven_multilingual_v2',
        voice_settings: {
          stability: config.stability || 0.5,
          similarity_boost: config.clarity || 0.8,
          style: 0.0,
          use_speaker_boost: true
        }
      },
      {
        headers: {
          'Accept': 'audio/mpeg',
          'Content-Type': 'application/json',
          'xi-api-key': apiKey
        },
        responseType: 'arraybuffer'
      }
    );

    // In a real implementation, you would:
    // 1. Save the audio buffer to a file
    // 2. Upload to cloud storage (AWS S3, etc.)
    // 3. Return the public URL
    
    // For now, return a placeholder
    const audioUrl = `${process.env.BROADCAST_CDN_URL}/audio/${Date.now()}.mp3`;
    const duration = calculateDuration(text);

    logger.info('TTS audio generated successfully', {
      textLength: text.length,
      duration,
      voiceId: config.voiceId
    });

    return {
      success: true,
      audioUrl,
      duration
    };
  } catch (error) {
    logger.error('ElevenLabs TTS generation failed:', error);
    throw error;
  }
};

/**
 * Get default TTS configuration
 * @param language - Target language
 * @param provider - TTS provider (default: 'elevenlabs')
 * @returns Default TTS configuration
 */
export const getDefaultTTSConfig = (language: string = 'fr', provider: 'elevenlabs' | 'heygen' = 'elevenlabs'): TTSConfig => {
  const voiceMap: Record<string, string> = {
    'fr': process.env.TTS_DEFAULT_VOICE || '21m00Tcm4TlvDq8ikWAM',
    'en': 'EXAVITQu4vr4xnSDxMaL',
    'es': 'VR6AewLTigWG4xSOukaG',
    'de': 'ErXwobaYiN019PkySvjV',
    'it': 'AZnzlk1XvdvUeBnXmlld'
  };

  if (provider === 'heygen') {
    return {
      provider: 'heygen',
      voiceId: process.env.HEYGEN_DEFAULT_VOICE || 'default_voice',
      model: 'heygen_v2',
      language,
      avatarId: process.env.HEYGEN_DEFAULT_AVATAR,
      videoQuality: 'medium',
      enableGestures: true
    };
  }

  return {
    provider: 'elevenlabs',
    voiceId: voiceMap[language] || voiceMap['fr'],
    model: process.env.TTS_MODEL || 'eleven_multilingual_v2',
    language,
    stability: 0.5,
    clarity: 0.8
  };
};

/**
 * Get default video configuration for HeyGen
 * @param language - Target language
 * @param avatarId - Avatar ID (optional)
 * @returns Default video configuration
 */
export const getDefaultVideoConfig = (language: string = 'fr', avatarId?: string): VideoConfig => {
  const voiceMap: Record<string, string> = {
    'fr': process.env.HEYGEN_VOICE_FR || 'french_voice',
    'en': process.env.HEYGEN_VOICE_EN || 'english_voice',
    'es': process.env.HEYGEN_VOICE_ES || 'spanish_voice',
    'de': process.env.HEYGEN_VOICE_DE || 'german_voice',
    'it': process.env.HEYGEN_VOICE_IT || 'italian_voice'
  };

  return {
    avatarId: avatarId || process.env.HEYGEN_DEFAULT_AVATAR || 'default_avatar',
    voiceId: voiceMap[language] || voiceMap['fr'],
    quality: 'medium',
    enableGestures: true,
    aspectRatio: '16:9'
  };
};

/**
 * Validate TTS configuration
 * @param config - TTS configuration to validate
 * @returns Validation result
 */
export const validateTTSConfig = (config: TTSConfig): { valid: boolean; errors: string[] } => {
  const errors: string[] = [];

  if (!config.provider) {
    errors.push('TTS provider is required');
  }

  if (!['elevenlabs', 'azure', 'google', 'heygen'].includes(config.provider)) {
    errors.push('Invalid TTS provider');
  }

  if (!config.voiceId) {
    errors.push('Voice ID is required');
  }

  if (!config.model) {
    errors.push('TTS model is required');
  }

  if (!config.language) {
    errors.push('Language is required');
  }

  // HeyGen specific validations
  if (config.provider === 'heygen') {
    if (!config.avatarId) {
      errors.push('Avatar ID is required for HeyGen');
    }
    
    if (config.videoQuality && !['low', 'medium', 'high', 'ultra'].includes(config.videoQuality)) {
      errors.push('Invalid video quality for HeyGen');
    }
  }

  // ElevenLabs specific validations
  if (config.provider === 'elevenlabs') {
    if (config.stability !== undefined && (config.stability < 0 || config.stability > 1)) {
      errors.push('Stability must be between 0 and 1');
    }

    if (config.clarity !== undefined && (config.clarity < 0 || config.clarity > 1)) {
      errors.push('Clarity must be between 0 and 1');
    }
  }

  if (config.speed !== undefined && (config.speed < 0.25 || config.speed > 4.0)) {
    errors.push('Speed must be between 0.25 and 4.0');
  }

  if (config.pitch !== undefined && (config.pitch < -20 || config.pitch > 20)) {
    errors.push('Pitch must be between -20 and 20');
  }

  return {
    valid: errors.length === 0,
    errors
  };
};

/**
 * Validate video configuration
 * @param config - Video configuration to validate
 * @returns Validation result
 */
export const validateVideoConfig = (config: VideoConfig): { valid: boolean; errors: string[] } => {
  const errors: string[] = [];

  if (!config.avatarId) {
    errors.push('Avatar ID is required');
  }

  if (!config.voiceId) {
    errors.push('Voice ID is required');
  }

  if (!['low', 'medium', 'high', 'ultra'].includes(config.quality)) {
    errors.push('Invalid video quality');
  }

  if (!['16:9', '9:16', '1:1'].includes(config.aspectRatio)) {
    errors.push('Invalid aspect ratio');
  }

  if (config.duration !== undefined && (config.duration < 1 || config.duration > 300)) {
    errors.push('Duration must be between 1 and 300 seconds');
  }

  return {
    valid: errors.length === 0,
    errors
  };
};