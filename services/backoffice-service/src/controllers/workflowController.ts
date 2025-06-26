import { Request, Response } from 'express';
import { prisma } from '../utils/database';
import { redisClient, CacheKeys } from '../utils/redis';
import { logger, logHelpers } from '../utils/logger';
import {
  AppError,
  NotFoundError,
  ConflictError,
  ValidationError,
  ExternalServiceError,
  asyncHandler,
  throwIfNotFound,
  handleDatabaseError,
  handleExternalServiceError
} from '../utils/errors';
import axios from 'axios';

/**
 * Get all workflows with pagination and filtering
 */
export const getWorkflows = asyncHandler(async (req: Request, res: Response) => {
  const {
    page = 1,
    limit = 10,
    search,
    category,
    status = 'ACTIVE',
    triggerType
  } = req.query;

  const pageNum = parseInt(page as string);
  const limitNum = parseInt(limit as string);

  // Build where clause
  const where: any = {
    deletedAt: null
  };

  if (status !== 'ALL') {
    where.isActive = status === 'ACTIVE';
  }

  if (category) {
    where.category = category;
  }

  if (triggerType) {
    where.triggerType = triggerType;
  }

  if (search) {
    where.OR = [
      { name: { contains: search, mode: 'insensitive' } },
      { description: { contains: search, mode: 'insensitive' } }
    ];
  }

  try {
    const result = await prisma.findManyWithPagination('workflow', {
      page: pageNum,
      limit: limitNum,
      where,
      orderBy: { createdAt: 'desc' },
      include: {
        executions: {
          orderBy: { startedAt: 'desc' },
          take: 3,
          select: {
            id: true,
            status: true,
            startedAt: true,
            finishedAt: true,
            error: true
          }
        },
        _count: {
          select: {
            executions: true
          }
        }
      }
    });

    logHelpers.logBusiness('Workflows retrieved', {
      count: result.data.length,
      total: result.pagination.total,
      filters: { search, category, status, triggerType }
    }, req.user?.id);

    res.json({
      success: true,
      data: result.data,
      pagination: result.pagination
    });
  } catch (error) {
    throw handleDatabaseError(error);
  }
});

/**
 * Get workflow by ID
 */
export const getWorkflowById = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    const workflow = await prisma.workflow.findFirst({
      where: {
        id,
        deletedAt: null
      },
      include: {
        executions: {
          orderBy: { startedAt: 'desc' },
          take: 20,
          select: {
            id: true,
            status: true,
            startedAt: true,
            finishedAt: true,
            error: true,
            inputData: true,
            outputData: true
          }
        },
        _count: {
          select: {
            executions: true
          }
        }
      }
    });

    throwIfNotFound(workflow, 'Workflow');

    // Get workflow details from n8n if n8nWorkflowId exists
    let n8nWorkflow = null;
    if (workflow!.n8nWorkflowId) {
      try {
        n8nWorkflow = await getN8nWorkflow(workflow!.n8nWorkflowId);
      } catch (error) {
        logger.warn('Failed to fetch n8n workflow details', {
          workflowId: id,
          n8nWorkflowId: workflow!.n8nWorkflowId,
          error: error instanceof Error ? error.message : 'Unknown error'
        });
      }
    }

    logHelpers.logBusiness('Workflow retrieved', { workflowId: id }, req.user?.id);

    res.json({
      success: true,
      data: {
        ...workflow,
        n8nWorkflow
      }
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Create new workflow
 */
export const createWorkflow = asyncHandler(async (req: Request, res: Response) => {
  const {
    name,
    description,
    category,
    triggerType,
    triggerConfig,
    workflowDefinition,
    isActive = true
  } = req.body;

  try {
    // Check if workflow with same name exists
    const existingWorkflow = await prisma.workflow.findFirst({
      where: {
        name,
        deletedAt: null
      }
    });

    if (existingWorkflow) {
      throw new ConflictError(`Workflow with name '${name}' already exists`);
    }

    // Create workflow in n8n first
    const n8nWorkflow = await createN8nWorkflow({
      name,
      nodes: workflowDefinition.nodes,
      connections: workflowDefinition.connections,
      active: isActive
    });

    const workflow = await prisma.workflow.create({
      data: {
        name,
        description,
        category,
        triggerType,
        triggerConfig: triggerConfig || {},
        workflowDefinition,
        n8nWorkflowId: n8nWorkflow.id,
        isActive,
        createdBy: req.user!.id,
        updatedBy: req.user!.id
      },
      include: {
        _count: {
          select: {
            executions: true
          }
        }
      }
    });

    logHelpers.logBusiness('Workflow created', {
      workflowId: workflow.id,
      name: workflow.name,
      category: workflow.category,
      n8nWorkflowId: n8nWorkflow.id
    }, req.user?.id);

    res.status(201).json({
      success: true,
      data: workflow,
      message: 'Workflow created successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Update workflow
 */
export const updateWorkflow = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const updateData = { ...req.body };
  delete updateData.id;
  updateData.updatedBy = req.user!.id;

  try {
    // Check if workflow exists
    const existingWorkflow = await prisma.workflow.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(existingWorkflow, 'Workflow');

    // Check if name is being changed and if it conflicts
    if (updateData.name && updateData.name !== existingWorkflow!.name) {
      const nameConflict = await prisma.workflow.findFirst({
        where: {
          name: updateData.name,
          id: { not: id },
          deletedAt: null
        }
      });

      if (nameConflict) {
        throw new ConflictError(`Workflow with name '${updateData.name}' already exists`);
      }
    }

    // Update workflow in n8n if definition or status changed
    if (existingWorkflow!.n8nWorkflowId && 
        (updateData.workflowDefinition || updateData.isActive !== undefined)) {
      await updateN8nWorkflow(existingWorkflow!.n8nWorkflowId, {
        name: updateData.name || existingWorkflow!.name,
        nodes: updateData.workflowDefinition?.nodes || existingWorkflow!.workflowDefinition.nodes,
        connections: updateData.workflowDefinition?.connections || existingWorkflow!.workflowDefinition.connections,
        active: updateData.isActive !== undefined ? updateData.isActive : existingWorkflow!.isActive
      });
    }

    const workflow = await prisma.workflow.update({
      where: { id },
      data: updateData,
      include: {
        executions: {
          orderBy: { startedAt: 'desc' },
          take: 5
        },
        _count: {
          select: {
            executions: true
          }
        }
      }
    });

    logHelpers.logBusiness('Workflow updated', {
      workflowId: id,
      changes: Object.keys(updateData)
    }, req.user?.id);

    res.json({
      success: true,
      data: workflow,
      message: 'Workflow updated successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Delete workflow (soft delete)
 */
export const deleteWorkflow = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;

  try {
    // Check if workflow exists
    const existingWorkflow = await prisma.workflow.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(existingWorkflow, 'Workflow');

    // Deactivate workflow in n8n first
    if (existingWorkflow!.n8nWorkflowId) {
      try {
        await deactivateN8nWorkflow(existingWorkflow!.n8nWorkflowId);
      } catch (error) {
        logger.warn('Failed to deactivate n8n workflow', {
          workflowId: id,
          n8nWorkflowId: existingWorkflow!.n8nWorkflowId,
          error: error instanceof Error ? error.message : 'Unknown error'
        });
      }
    }

    await prisma.softDelete('workflow', { id });

    logHelpers.logBusiness('Workflow deleted', {
      workflowId: id,
      name: existingWorkflow!.name
    }, req.user?.id);

    res.json({
      success: true,
      message: 'Workflow deleted successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Execute workflow manually
 */
export const executeWorkflow = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const { inputData = {} } = req.body;

  try {
    // Check if workflow exists
    const workflow = await prisma.workflow.findFirst({
      where: {
        id,
        deletedAt: null
      }
    });

    throwIfNotFound(workflow, 'Workflow');

    if (!workflow!.isActive) {
      throw new ValidationError('Cannot execute inactive workflow');
    }

    if (!workflow!.n8nWorkflowId) {
      throw new ValidationError('Workflow is not linked to n8n');
    }

    // Create execution record
    const execution = await prisma.workflowExecution.create({
      data: {
        workflowId: id,
        status: 'RUNNING',
        inputData,
        startedAt: new Date(),
        triggeredBy: req.user!.id
      }
    });

    // Execute workflow in n8n
    const n8nExecution = await executeN8nWorkflow(workflow!.n8nWorkflowId, inputData);

    // Update execution with n8n execution ID
    await prisma.workflowExecution.update({
      where: { id: execution.id },
      data: {
        n8nExecutionId: n8nExecution.id
      }
    });

    logHelpers.logBusiness('Workflow executed manually', {
      workflowId: id,
      executionId: execution.id,
      n8nExecutionId: n8nExecution.id
    }, req.user?.id);

    res.json({
      success: true,
      data: {
        execution: {
          id: execution.id,
          status: execution.status,
          startedAt: execution.startedAt
        },
        n8nExecution: {
          id: n8nExecution.id,
          status: n8nExecution.status
        }
      },
      message: 'Workflow execution started'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get workflow executions
 */
export const getWorkflowExecutions = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const {
    page = 1,
    limit = 10,
    status,
    startDate,
    endDate
  } = req.query;

  const pageNum = parseInt(page as string);
  const limitNum = parseInt(limit as string);

  // Build where clause
  const where: any = {
    workflowId: id
  };

  if (status) {
    where.status = status;
  }

  if (startDate || endDate) {
    where.startedAt = {};
    if (startDate) where.startedAt.gte = new Date(startDate as string);
    if (endDate) where.startedAt.lte = new Date(endDate as string);
  }

  try {
    // Check if workflow exists
    const workflow = await prisma.workflow.findFirst({
      where: { id, deletedAt: null }
    });

    throwIfNotFound(workflow, 'Workflow');

    const result = await prisma.findManyWithPagination('workflowExecution', {
      page: pageNum,
      limit: limitNum,
      where,
      orderBy: { startedAt: 'desc' }
    });

    res.json({
      success: true,
      data: result.data,
      pagination: result.pagination
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get workflow execution by ID
 */
export const getWorkflowExecutionById = asyncHandler(async (req: Request, res: Response) => {
  const { id, executionId } = req.params;

  try {
    const execution = await prisma.workflowExecution.findFirst({
      where: {
        id: executionId,
        workflowId: id
      },
      include: {
        workflow: {
          select: {
            id: true,
            name: true,
            category: true
          }
        }
      }
    });

    throwIfNotFound(execution, 'Workflow Execution');

    // Get execution details from n8n if available
    let n8nExecution = null;
    if (execution!.n8nExecutionId) {
      try {
        n8nExecution = await getN8nExecution(execution!.n8nExecutionId);
      } catch (error) {
        logger.warn('Failed to fetch n8n execution details', {
          executionId,
          n8nExecutionId: execution!.n8nExecutionId,
          error: error instanceof Error ? error.message : 'Unknown error'
        });
      }
    }

    res.json({
      success: true,
      data: {
        ...execution,
        n8nExecution
      }
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Get workflow analytics
 */
export const getWorkflowAnalytics = asyncHandler(async (req: Request, res: Response) => {
  const { id } = req.params;
  const { period = '30d' } = req.query;

  try {
    // Check if workflow exists
    const workflow = await prisma.workflow.findFirst({
      where: { id, deletedAt: null }
    });

    throwIfNotFound(workflow, 'Workflow');

    // Calculate date range
    const now = new Date();
    let startDate: Date;
    
    switch (period) {
      case '7d':
        startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
        break;
      case '30d':
        startDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
        break;
      case '90d':
        startDate = new Date(now.getTime() - 90 * 24 * 60 * 60 * 1000);
        break;
      default:
        startDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
    }

    // Get analytics data
    const [totalExecutions, successfulExecutions, failedExecutions, statusStats] = await Promise.all([
      // Total executions
      prisma.workflowExecution.count({
        where: {
          workflowId: id,
          startedAt: { gte: startDate }
        }
      }),
      
      // Successful executions
      prisma.workflowExecution.count({
        where: {
          workflowId: id,
          status: 'SUCCESS',
          startedAt: { gte: startDate }
        }
      }),
      
      // Failed executions
      prisma.workflowExecution.count({
        where: {
          workflowId: id,
          status: 'FAILED',
          startedAt: { gte: startDate }
        }
      }),
      
      // Status distribution
      prisma.workflowExecution.groupBy({
        by: ['status'],
        where: {
          workflowId: id,
          startedAt: { gte: startDate }
        },
        _count: true
      })
    ]);

    // Get daily execution counts for chart
    const dailyExecutions = await prisma.$queryRaw`
      SELECT 
        DATE(started_at) as date,
        COUNT(*) as execution_count,
        SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as success_count,
        SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as failed_count
      FROM workflow_executions 
      WHERE workflow_id = ${id} 
        AND started_at >= ${startDate}
      GROUP BY DATE(started_at)
      ORDER BY date ASC
    `;

    const analytics = {
      summary: {
        totalExecutions,
        successfulExecutions,
        failedExecutions,
        successRate: totalExecutions > 0 ? (successfulExecutions / totalExecutions * 100).toFixed(2) : 0,
        failureRate: totalExecutions > 0 ? (failedExecutions / totalExecutions * 100).toFixed(2) : 0
      },
      statusStats: statusStats.reduce((acc: any, stat: any) => {
        acc[stat.status] = stat._count;
        return acc;
      }, {}),
      chart: {
        dailyExecutions
      },
      period,
      workflow: {
        id: workflow.id,
        name: workflow.name,
        category: workflow.category
      }
    };

    res.json({
      success: true,
      data: analytics
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Sync workflow execution status from n8n
 */
export const syncExecutionStatus = asyncHandler(async (req: Request, res: Response) => {
  const { id, executionId } = req.params;

  try {
    const execution = await prisma.workflowExecution.findFirst({
      where: {
        id: executionId,
        workflowId: id
      }
    });

    throwIfNotFound(execution, 'Workflow Execution');

    if (!execution!.n8nExecutionId) {
      throw new ValidationError('Execution is not linked to n8n');
    }

    // Get execution status from n8n
    const n8nExecution = await getN8nExecution(execution!.n8nExecutionId);

    // Update execution status
    const updatedExecution = await prisma.workflowExecution.update({
      where: { id: executionId },
      data: {
        status: n8nExecution.status,
        outputData: n8nExecution.data,
        error: n8nExecution.error,
        finishedAt: n8nExecution.finishedAt ? new Date(n8nExecution.finishedAt) : null
      }
    });

    logHelpers.logBusiness('Execution status synced', {
      executionId,
      workflowId: id,
      status: updatedExecution.status
    }, req.user?.id);

    res.json({
      success: true,
      data: updatedExecution,
      message: 'Execution status synced successfully'
    });
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw handleDatabaseError(error);
  }
});

/**
 * Helper functions for n8n integration
 */
async function getN8nWorkflow(workflowId: string): Promise<any> {
  const n8nApiUrl = process.env.N8N_API_URL;
  if (!n8nApiUrl) {
    throw new ExternalServiceError('n8n', 'API URL not configured');
  }

  try {
    const response = await axios.get(`${n8nApiUrl}/workflows/${workflowId}`, {
      headers: {
        'X-N8N-API-KEY': process.env.N8N_API_KEY,
        'Content-Type': 'application/json'
      },
      timeout: 10000
    });

    return response.data;
  } catch (error) {
    throw handleExternalServiceError('n8n API', error);
  }
}

async function createN8nWorkflow(workflowData: any): Promise<any> {
  const n8nApiUrl = process.env.N8N_API_URL;
  if (!n8nApiUrl) {
    throw new ExternalServiceError('n8n', 'API URL not configured');
  }

  try {
    const response = await axios.post(`${n8nApiUrl}/workflows`, workflowData, {
      headers: {
        'X-N8N-API-KEY': process.env.N8N_API_KEY,
        'Content-Type': 'application/json'
      },
      timeout: 30000
    });

    return response.data;
  } catch (error) {
    throw handleExternalServiceError('n8n API', error);
  }
}

async function updateN8nWorkflow(workflowId: string, workflowData: any): Promise<any> {
  const n8nApiUrl = process.env.N8N_API_URL;
  if (!n8nApiUrl) {
    throw new ExternalServiceError('n8n', 'API URL not configured');
  }

  try {
    const response = await axios.put(`${n8nApiUrl}/workflows/${workflowId}`, workflowData, {
      headers: {
        'X-N8N-API-KEY': process.env.N8N_API_KEY,
        'Content-Type': 'application/json'
      },
      timeout: 30000
    });

    return response.data;
  } catch (error) {
    throw handleExternalServiceError('n8n API', error);
  }
}

async function deactivateN8nWorkflow(workflowId: string): Promise<void> {
  const n8nApiUrl = process.env.N8N_API_URL;
  if (!n8nApiUrl) {
    throw new ExternalServiceError('n8n', 'API URL not configured');
  }

  try {
    await axios.patch(`${n8nApiUrl}/workflows/${workflowId}/activate`, 
      { active: false },
      {
        headers: {
          'X-N8N-API-KEY': process.env.N8N_API_KEY,
          'Content-Type': 'application/json'
        },
        timeout: 10000
      }
    );
  } catch (error) {
    throw handleExternalServiceError('n8n API', error);
  }
}

async function executeN8nWorkflow(workflowId: string, inputData: any): Promise<any> {
  const n8nApiUrl = process.env.N8N_API_URL;
  if (!n8nApiUrl) {
    throw new ExternalServiceError('n8n', 'API URL not configured');
  }

  try {
    const response = await axios.post(`${n8nApiUrl}/workflows/${workflowId}/execute`, 
      { data: inputData },
      {
        headers: {
          'X-N8N-API-KEY': process.env.N8N_API_KEY,
          'Content-Type': 'application/json'
        },
        timeout: 60000
      }
    );

    return response.data;
  } catch (error) {
    throw handleExternalServiceError('n8n API', error);
  }
}

async function getN8nExecution(executionId: string): Promise<any> {
  const n8nApiUrl = process.env.N8N_API_URL;
  if (!n8nApiUrl) {
    throw new ExternalServiceError('n8n', 'API URL not configured');
  }

  try {
    const response = await axios.get(`${n8nApiUrl}/executions/${executionId}`, {
      headers: {
        'X-N8N-API-KEY': process.env.N8N_API_KEY,
        'Content-Type': 'application/json'
      },
      timeout: 10000
    });

    return response.data;
  } catch (error) {
    throw handleExternalServiceError('n8n API', error);
  }
}