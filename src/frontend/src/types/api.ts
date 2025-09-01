// API响应相关类型定义 - 统一的API响应格式

// 基础API响应接口
export interface BaseApiResponse {
  code: number
  message: string
  timestamp?: string
  traceId?: string
}

// 成功响应接口
export interface ApiSuccessResponse<T = any> extends BaseApiResponse {
  code: 200
  data: T
}

// 错误响应接口
export interface ApiErrorResponse extends BaseApiResponse {
  code: number // 非200状态码
  errorCode?: string
  errorDetails?: any
  data?: null
}

// 统一API响应类型
export type ApiResponse<T = any> = ApiSuccessResponse<T> | ApiErrorResponse

// 分页数据接口
export interface PaginationData {
  page: number
  pageSize: number
  total: number
  totalPages?: number
  hasNext?: boolean
  hasPrev?: boolean
}

// 分页响应接口
export interface PaginatedApiResponse<T> extends BaseApiResponse {
  data: {
    list: T[]
    pagination: PaginationData
    isEmpty?: boolean
  }
}

// 列表响应接口（简化版分页）
export interface ListApiResponse<T> extends BaseApiResponse {
  data: {
    list: T[]
    total: number
    page: number
    pageSize: number
    isEmpty?: boolean
  }
}

// 文件上传响应接口
export interface FileUploadResponse extends BaseApiResponse {
  data: {
    fileId: string
    fileName: string
    fileUrl: string
    fileSize: number
    mimeType: string
    uploadTime: string
  }
}

// 批量操作响应接口
export interface BatchOperationResponse extends BaseApiResponse {
  data: {
    successCount: number
    failureCount: number
    totalCount: number
    successIds: (string | number)[]
    failureIds: (string | number)[]
    errors?: Array<{
      id: string | number
      error: string
    }>
  }
}

// 统计数据响应接口
export interface StatsApiResponse<T> extends BaseApiResponse {
  data: T & {
    generatedAt: string
    cacheExpiry?: string
  }
}

// 搜索建议响应接口
export interface SearchSuggestionResponse<T> extends BaseApiResponse {
  data: {
    suggestions: T[]
    total: number
    searchTime: number
    hasMore: boolean
  }
}

// 导出任务响应接口
export interface ExportTaskResponse extends BaseApiResponse {
  data: {
    taskId: string
    status: 'pending' | 'processing' | 'completed' | 'failed'
    progress: number
    downloadUrl?: string
    createdAt: string
    estimatedTime?: number
  }
}

// 验证响应接口
export interface ValidationResponse extends BaseApiResponse {
  data: {
    isValid: boolean
    errors?: Array<{
      field: string
      message: string
      code?: string
    }>
    warnings?: Array<{
      field: string
      message: string
    }>
  }
}

// 选项数据接口
export interface OptionItem {
  label: string
  value: string | number
  disabled?: boolean
  description?: string
  count?: number
  icon?: string
}

// 选项响应接口
export interface OptionsApiResponse extends BaseApiResponse {
  data: {
    options: OptionItem[]
    categories?: Record<string, OptionItem[]>
  }
}

// 健康检查响应接口
export interface HealthCheckResponse extends BaseApiResponse {
  data: {
    status: 'healthy' | 'unhealthy' | 'degraded'
    version: string
    uptime: number
    services: Record<string, {
      status: 'up' | 'down' | 'degraded'
      responseTime?: number
      lastCheck: string
    }>
  }
}

// 权限检查响应接口
export interface PermissionResponse extends BaseApiResponse {
  data: {
    hasPermission: boolean
    permissions: string[]
    roles: string[]
    restrictions?: Record<string, any>
  }
}

// 配置响应接口
export interface ConfigResponse extends BaseApiResponse {
  data: {
    config: Record<string, any>
    version: string
    lastUpdated: string
  }
}

// 日志响应接口
export interface LogResponse extends BaseApiResponse {
  data: {
    logs: Array<{
      timestamp: string
      level: 'debug' | 'info' | 'warn' | 'error'
      message: string
      context?: Record<string, any>
    }>
    total: number
    hasMore: boolean
  }
}

// 通用错误码枚举
export enum ApiErrorCode {
  // 通用错误
  UNKNOWN_ERROR = 'UNKNOWN_ERROR',
  INVALID_REQUEST = 'INVALID_REQUEST',
  VALIDATION_ERROR = 'VALIDATION_ERROR',
  
  // 认证授权错误
  UNAUTHORIZED = 'UNAUTHORIZED',
  FORBIDDEN = 'FORBIDDEN',
  TOKEN_EXPIRED = 'TOKEN_EXPIRED',
  
  // 资源错误
  NOT_FOUND = 'NOT_FOUND',
  RESOURCE_EXISTS = 'RESOURCE_EXISTS',
  RESOURCE_LOCKED = 'RESOURCE_LOCKED',
  
  // 业务错误
  BUSINESS_ERROR = 'BUSINESS_ERROR',
  OPERATION_FAILED = 'OPERATION_FAILED',
  CONSTRAINT_VIOLATION = 'CONSTRAINT_VIOLATION',
  
  // 系统错误
  INTERNAL_ERROR = 'INTERNAL_ERROR',
  SERVICE_UNAVAILABLE = 'SERVICE_UNAVAILABLE',
  TIMEOUT = 'TIMEOUT',
  
  // 限流错误
  RATE_LIMIT_EXCEEDED = 'RATE_LIMIT_EXCEEDED',
  QUOTA_EXCEEDED = 'QUOTA_EXCEEDED'
}

// HTTP状态码枚举
export enum HttpStatusCode {
  OK = 200,
  CREATED = 201,
  NO_CONTENT = 204,
  BAD_REQUEST = 400,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  METHOD_NOT_ALLOWED = 405,
  CONFLICT = 409,
  UNPROCESSABLE_ENTITY = 422,
  TOO_MANY_REQUESTS = 429,
  INTERNAL_SERVER_ERROR = 500,
  BAD_GATEWAY = 502,
  SERVICE_UNAVAILABLE = 503,
  GATEWAY_TIMEOUT = 504
}

// 请求配置接口
export interface RequestConfig {
  timeout?: number
  retries?: number
  retryDelay?: number
  cache?: boolean
  cacheTime?: number
  headers?: Record<string, string>
  params?: Record<string, any>
}

// 响应拦截器配置
export interface ResponseInterceptorConfig {
  transformResponse?: boolean
  validateStatus?: (status: number) => boolean
  errorHandler?: (error: any) => void
  successHandler?: (response: any) => void
}