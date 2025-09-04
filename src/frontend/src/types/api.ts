/**
 * API 通用类型定义
 * 用于所有模块的API响应格式
 */

// API统一响应格式接口
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp?: string
}

// 分页响应接口
export interface PaginatedResponse<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
  totalPages?: number
  isEmpty?: boolean
}

// 批量操作响应接口
export interface BatchOperationResponse {
  code: number
  message: string
  data: {
    successCount: number
    failureCount: number
    errors?: string[]
    processedIds: string[]
  }
}

// 文件上传响应接口
export interface FileUploadResponse {
  url: string
  filename: string
  size: number
  type: string
}

// 导出响应接口
export interface ExportResponse {
  downloadUrl: string
  filename: string
  fileSize?: number
  expiresAt?: string
}

// 操作结果接口
export interface OperationResult {
  success: boolean
  message: string
  data?: any
}

// 错误响应接口
export interface ErrorResponse {
  code: number
  message: string
  details?: string
  timestamp: string
}

// 选项接口（用于下拉选择等）
export interface Option {
  label: string
  value: string | number
  disabled?: boolean
}

// 统计数据接口
export interface StatsData {
  [key: string]: number | string
}