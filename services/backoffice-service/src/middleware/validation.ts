import { Request, Response, NextFunction } from 'express';
import Joi from 'joi';
import { logger } from '../utils/logger';

/**
 * Validation middleware factory
 */
export const validate = (schema: {
  body?: Joi.ObjectSchema;
  query?: Joi.ObjectSchema;
  params?: Joi.ObjectSchema;
}) => {
  return (req: Request, res: Response, next: NextFunction) => {
    const errors: any = {};

    // Validate request body
    if (schema.body) {
      const { error } = schema.body.validate(req.body, { abortEarly: false });
      if (error) {
        errors.body = error.details.map(detail => ({
          field: detail.path.join('.'),
          message: detail.message,
          value: detail.context?.value
        }));
      }
    }

    // Validate query parameters
    if (schema.query) {
      const { error } = schema.query.validate(req.query, { abortEarly: false });
      if (error) {
        errors.query = error.details.map(detail => ({
          field: detail.path.join('.'),
          message: detail.message,
          value: detail.context?.value
        }));
      }
    }

    // Validate route parameters
    if (schema.params) {
      const { error } = schema.params.validate(req.params, { abortEarly: false });
      if (error) {
        errors.params = error.details.map(detail => ({
          field: detail.path.join('.'),
          message: detail.message,
          value: detail.context?.value
        }));
      }
    }

    // If there are validation errors, return them
    if (Object.keys(errors).length > 0) {
      logger.warn('Validation failed', {
        path: req.path,
        method: req.method,
        errors,
        userId: req.user?.id
      });

      return res.status(400).json({
        success: false,
        message: 'Validation failed',
        errors
      });
    }

    next();
  };
};

// Common validation schemas
export const commonSchemas = {
  // ID parameter validation
  id: Joi.object({
    id: Joi.string().uuid().required().messages({
      'string.guid': 'ID must be a valid UUID',
      'any.required': 'ID is required'
    })
  }),

  // Pagination query validation
  pagination: Joi.object({
    page: Joi.number().integer().min(1).default(1),
    limit: Joi.number().integer().min(1).max(100).default(10),
    sortBy: Joi.string().optional(),
    sortOrder: Joi.string().valid('asc', 'desc').default('desc'),
    search: Joi.string().optional().allow(''),
    filter: Joi.string().optional().allow('')
  }),

  // File upload validation
  fileUpload: Joi.object({
    filename: Joi.string().required(),
    mimetype: Joi.string().required(),
    size: Joi.number().max(10 * 1024 * 1024) // 10MB max
  })
};

// Avatar validation schemas
export const avatarSchemas = {
  create: {
    body: Joi.object({
      name: Joi.string().min(2).max(100).required(),
      description: Joi.string().max(500).optional(),
      type: Joi.string().valid('DOCTOR', 'COACH', 'TEACHER', 'PRESENTER', 'CUSTOM').required(),
      personality: Joi.object({
        traits: Joi.array().items(Joi.string()).optional(),
        tone: Joi.string().valid('PROFESSIONAL', 'FRIENDLY', 'CASUAL', 'FORMAL').optional(),
        expertise: Joi.array().items(Joi.string()).optional()
      }).optional(),
      appearance: Joi.object({
        gender: Joi.string().valid('MALE', 'FEMALE', 'NON_BINARY').optional(),
        age: Joi.string().valid('YOUNG', 'MIDDLE_AGED', 'SENIOR').optional(),
        ethnicity: Joi.string().optional(),
        style: Joi.string().optional()
      }).optional(),
      voice: Joi.object({
        provider: Joi.string().valid('ELEVENLABS', 'AZURE', 'AWS', 'GOOGLE').optional(),
        voiceId: Joi.string().optional(),
        language: Joi.string().optional(),
        accent: Joi.string().optional()
      }).optional(),
      capabilities: Joi.array().items(
        Joi.string().valid('TEXT_TO_SPEECH', 'SPEECH_TO_TEXT', 'CONVERSATION', 'PRESENTATION', 'ANALYSIS')
      ).optional(),
      isActive: Joi.boolean().default(true),
      tags: Joi.array().items(Joi.string()).optional()
    })
  },

  update: {
    params: commonSchemas.id,
    body: Joi.object({
      name: Joi.string().min(2).max(100).optional(),
      description: Joi.string().max(500).optional(),
      type: Joi.string().valid('DOCTOR', 'COACH', 'TEACHER', 'PRESENTER', 'CUSTOM').optional(),
      personality: Joi.object({
        traits: Joi.array().items(Joi.string()).optional(),
        tone: Joi.string().valid('PROFESSIONAL', 'FRIENDLY', 'CASUAL', 'FORMAL').optional(),
        expertise: Joi.array().items(Joi.string()).optional()
      }).optional(),
      appearance: Joi.object({
        gender: Joi.string().valid('MALE', 'FEMALE', 'NON_BINARY').optional(),
        age: Joi.string().valid('YOUNG', 'MIDDLE_AGED', 'SENIOR').optional(),
        ethnicity: Joi.string().optional(),
        style: Joi.string().optional()
      }).optional(),
      voice: Joi.object({
        provider: Joi.string().valid('ELEVENLABS', 'AZURE', 'AWS', 'GOOGLE').optional(),
        voiceId: Joi.string().optional(),
        language: Joi.string().optional(),
        accent: Joi.string().optional()
      }).optional(),
      capabilities: Joi.array().items(
        Joi.string().valid('TEXT_TO_SPEECH', 'SPEECH_TO_TEXT', 'CONVERSATION', 'PRESENTATION', 'ANALYSIS')
      ).optional(),
      isActive: Joi.boolean().optional(),
      tags: Joi.array().items(Joi.string()).optional()
    })
  },

  list: {
    query: commonSchemas.pagination.keys({
      type: Joi.string().valid('DOCTOR', 'COACH', 'TEACHER', 'PRESENTER', 'CUSTOM').optional(),
      isActive: Joi.boolean().optional(),
      capabilities: Joi.string().optional()
    })
  }
};

// Studio validation schemas
export const studioSchemas = {
  create: {
    body: Joi.object({
      name: Joi.string().min(2).max(100).required(),
      description: Joi.string().max(500).optional(),
      type: Joi.string().valid('MEDICAL', 'SPORTS', 'EDUCATION', 'NEWS', 'CUSTOM').required(),
      unrealEngineConfig: Joi.object({
        projectPath: Joi.string().required(),
        version: Joi.string().required(),
        renderSettings: Joi.object().optional(),
        lightingSetup: Joi.object().optional(),
        cameraSettings: Joi.object().optional()
      }).required(),
      environment: Joi.object({
        skybox: Joi.string().optional(),
        lighting: Joi.string().valid('NATURAL', 'STUDIO', 'DRAMATIC', 'SOFT').optional(),
        atmosphere: Joi.string().optional(),
        props: Joi.array().items(Joi.string()).optional()
      }).optional(),
      assets: Joi.object({
        models: Joi.array().items(Joi.string()).optional(),
        textures: Joi.array().items(Joi.string()).optional(),
        animations: Joi.array().items(Joi.string()).optional(),
        sounds: Joi.array().items(Joi.string()).optional()
      }).optional(),
      isActive: Joi.boolean().default(true),
      tags: Joi.array().items(Joi.string()).optional()
    })
  },

  update: {
    params: commonSchemas.id,
    body: Joi.object({
      name: Joi.string().min(2).max(100).optional(),
      description: Joi.string().max(500).optional(),
      type: Joi.string().valid('MEDICAL', 'SPORTS', 'EDUCATION', 'NEWS', 'CUSTOM').optional(),
      unrealEngineConfig: Joi.object({
        projectPath: Joi.string().optional(),
        version: Joi.string().optional(),
        renderSettings: Joi.object().optional(),
        lightingSetup: Joi.object().optional(),
        cameraSettings: Joi.object().optional()
      }).optional(),
      environment: Joi.object({
        skybox: Joi.string().optional(),
        lighting: Joi.string().valid('NATURAL', 'STUDIO', 'DRAMATIC', 'SOFT').optional(),
        atmosphere: Joi.string().optional(),
        props: Joi.array().items(Joi.string()).optional()
      }).optional(),
      assets: Joi.object({
        models: Joi.array().items(Joi.string()).optional(),
        textures: Joi.array().items(Joi.string()).optional(),
        animations: Joi.array().items(Joi.string()).optional(),
        sounds: Joi.array().items(Joi.string()).optional()
      }).optional(),
      isActive: Joi.boolean().optional(),
      tags: Joi.array().items(Joi.string()).optional()
    })
  },

  list: {
    query: commonSchemas.pagination.keys({
      type: Joi.string().valid('MEDICAL', 'SPORTS', 'EDUCATION', 'NEWS', 'CUSTOM').optional(),
      isActive: Joi.boolean().optional()
    })
  }
};

// RSS validation schemas
export const rssSchemas = {
  create: {
    body: Joi.object({
      name: Joi.string().min(2).max(100).required(),
      url: Joi.string().uri().required(),
      category: Joi.string().valid('HEALTH', 'SPORTS', 'EDUCATION', 'NEWS', 'TECHNOLOGY', 'OTHER').required(),
      language: Joi.string().length(2).required(), // ISO 639-1 language code
      updateFrequency: Joi.number().integer().min(1).max(1440).default(60), // minutes
      isActive: Joi.boolean().default(true),
      factCheckEnabled: Joi.boolean().default(true),
      tags: Joi.array().items(Joi.string()).optional(),
      filters: Joi.object({
        keywords: Joi.array().items(Joi.string()).optional(),
        excludeKeywords: Joi.array().items(Joi.string()).optional(),
        minWordCount: Joi.number().integer().min(0).optional(),
        maxAge: Joi.number().integer().min(0).optional() // hours
      }).optional()
    })
  },

  update: {
    params: commonSchemas.id,
    body: Joi.object({
      name: Joi.string().min(2).max(100).optional(),
      url: Joi.string().uri().optional(),
      category: Joi.string().valid('HEALTH', 'SPORTS', 'EDUCATION', 'NEWS', 'TECHNOLOGY', 'OTHER').optional(),
      language: Joi.string().length(2).optional(),
      updateFrequency: Joi.number().integer().min(1).max(1440).optional(),
      isActive: Joi.boolean().optional(),
      factCheckEnabled: Joi.boolean().optional(),
      tags: Joi.array().items(Joi.string()).optional(),
      filters: Joi.object({
        keywords: Joi.array().items(Joi.string()).optional(),
        excludeKeywords: Joi.array().items(Joi.string()).optional(),
        minWordCount: Joi.number().integer().min(0).optional(),
        maxAge: Joi.number().integer().min(0).optional()
      }).optional()
    })
  },

  list: {
    query: commonSchemas.pagination.keys({
      category: Joi.string().valid('HEALTH', 'SPORTS', 'EDUCATION', 'NEWS', 'TECHNOLOGY', 'OTHER').optional(),
      language: Joi.string().length(2).optional(),
      isActive: Joi.boolean().optional(),
      factCheckEnabled: Joi.boolean().optional()
    })
  }
};

// Content validation schemas
export const contentSchemas = {
  create: {
    body: Joi.object({
      title: Joi.string().min(2).max(200).required(),
      content: Joi.string().min(10).required(),
      type: Joi.string().valid('ARTICLE', 'VIDEO', 'PODCAST', 'COURSE', 'LIVE_STREAM').required(),
      category: Joi.string().valid('HEALTH', 'SPORTS', 'EDUCATION', 'NEWS', 'ENTERTAINMENT').required(),
      language: Joi.string().length(2).required(),
      metadata: Joi.object({
        author: Joi.string().optional(),
        source: Joi.string().optional(),
        publishedAt: Joi.date().optional(),
        duration: Joi.number().optional(), // seconds
        thumbnailUrl: Joi.string().uri().optional(),
        tags: Joi.array().items(Joi.string()).optional()
      }).optional(),
      avatarId: Joi.string().uuid().optional(),
      studioId: Joi.string().uuid().optional(),
      isPublished: Joi.boolean().default(false),
      scheduledAt: Joi.date().optional()
    })
  },

  update: {
    params: commonSchemas.id,
    body: Joi.object({
      title: Joi.string().min(2).max(200).optional(),
      content: Joi.string().min(10).optional(),
      type: Joi.string().valid('ARTICLE', 'VIDEO', 'PODCAST', 'COURSE', 'LIVE_STREAM').optional(),
      category: Joi.string().valid('HEALTH', 'SPORTS', 'EDUCATION', 'NEWS', 'ENTERTAINMENT').optional(),
      language: Joi.string().length(2).optional(),
      metadata: Joi.object({
        author: Joi.string().optional(),
        source: Joi.string().optional(),
        publishedAt: Joi.date().optional(),
        duration: Joi.number().optional(),
        thumbnailUrl: Joi.string().uri().optional(),
        tags: Joi.array().items(Joi.string()).optional()
      }).optional(),
      avatarId: Joi.string().uuid().optional(),
      studioId: Joi.string().uuid().optional(),
      isPublished: Joi.boolean().optional(),
      scheduledAt: Joi.date().optional()
    })
  },

  list: {
    query: commonSchemas.pagination.keys({
      type: Joi.string().valid('ARTICLE', 'VIDEO', 'PODCAST', 'COURSE', 'LIVE_STREAM').optional(),
      category: Joi.string().valid('HEALTH', 'SPORTS', 'EDUCATION', 'NEWS', 'ENTERTAINMENT').optional(),
      language: Joi.string().length(2).optional(),
      isPublished: Joi.boolean().optional(),
      avatarId: Joi.string().uuid().optional(),
      studioId: Joi.string().uuid().optional()
    })
  }
};

// Workflow validation schemas
export const workflowSchemas = {
  create: {
    body: Joi.object({
      name: Joi.string().min(2).max(100).required(),
      description: Joi.string().max(500).optional(),
      type: Joi.string().valid('CONTENT_CREATION', 'FACT_CHECK', 'RSS_PROCESSING', 'AVATAR_TRAINING').required(),
      steps: Joi.array().items(
        Joi.object({
          name: Joi.string().required(),
          type: Joi.string().required(),
          config: Joi.object().required(),
          order: Joi.number().integer().min(0).required()
        })
      ).min(1).required(),
      triggers: Joi.array().items(
        Joi.object({
          type: Joi.string().valid('MANUAL', 'SCHEDULED', 'EVENT', 'WEBHOOK').required(),
          config: Joi.object().required()
        })
      ).optional(),
      isActive: Joi.boolean().default(true)
    })
  },

  update: {
    params: commonSchemas.id,
    body: Joi.object({
      name: Joi.string().min(2).max(100).optional(),
      description: Joi.string().max(500).optional(),
      type: Joi.string().valid('CONTENT_CREATION', 'FACT_CHECK', 'RSS_PROCESSING', 'AVATAR_TRAINING').optional(),
      steps: Joi.array().items(
        Joi.object({
          name: Joi.string().required(),
          type: Joi.string().required(),
          config: Joi.object().required(),
          order: Joi.number().integer().min(0).required()
        })
      ).min(1).optional(),
      triggers: Joi.array().items(
        Joi.object({
          type: Joi.string().valid('MANUAL', 'SCHEDULED', 'EVENT', 'WEBHOOK').required(),
          config: Joi.object().required()
        })
      ).optional(),
      isActive: Joi.boolean().optional()
    })
  },

  execute: {
    params: commonSchemas.id,
    body: Joi.object({
      input: Joi.object().optional(),
      context: Joi.object().optional()
    })
  },

  list: {
    query: commonSchemas.pagination.keys({
      type: Joi.string().valid('CONTENT_CREATION', 'FACT_CHECK', 'RSS_PROCESSING', 'AVATAR_TRAINING').optional(),
      isActive: Joi.boolean().optional()
    })
  }
};
