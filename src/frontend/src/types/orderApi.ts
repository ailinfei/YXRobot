/**
 * 订单API相关类型定义
 * 专门用于API请求和响应的类型定义
 * 任务18：创建前端TypeScript接口定义
 */

import type { 
  Order, 
  OrderStats, 
  OrderItem, 
  ShippingInfo, 
  OrderLog,
  CreateOrderData,
  UpdateOrderData,
  OrderQueryParams,
  OrderBatchOperation,
  OrderBatchOperationResponse,
  OrderExportParams,
  OrderExportResponse
} from './order'

import type { ApiResponse, PaginatedApiResponse, ListApiResponse } from './api'

// 订单列表API响应接口
export interface OrderListApiResponse extends ListApiResponse<Order> {
  data: {
    list: Order[]
    total: number
    page: number
    pageSize: number
    stats: OrderStats
    isEmpty?: boolean
  }
}

// 订单详情API响应接口
export interface OrderDetailApiResponse extends ApiResponse<Order> {
  data: Order & {
    items: OrderItem[]
    shippingInfo?: ShippingInfo
    logs?: OrderLog[]
    permissions?: {
      canEdit: boolean
      canDelete: boolean
      canUpdateStatus: boolean
      canExport: boolean
    }
  }
}

// 订单统计API响应接口
export interface OrderStatsApiResponse extends ApiResponse<OrderStats> {
  data: OrderStats & {
    generatedAt: string
    period?: {
      startDate: string
      endDate: string
    }
    trends?: {
      totalChange: number
      revenueChange: number
      completedChange: number
    }
  }
}

// 订单创建API请求接口
export interface CreateOrderApiRequest {
  data: CreateOrderData
  options?: {
    validateOnly?: boolean
    sendNotification?: boolean
    autoConfirm?: boolean
  }
}

// 订单创建API响应接口
export interface CreateOrderApiResponse extends ApiResponse<Order> {
  data: Order & {
    validationResult?: {
      isValid: boolean
      warnings?: string[]
    }
    nextActions?: string[]
  }
}

// 订单更新API请求接口
export interface UpdateOrderApiRequest {
  id: string | number
  data: UpdateOrderData
  options?: {
    validateOnly?: boolean
    sendNotification?: boolean
    logChanges?: boolean
  }
}

// 订单更新API响应接口
export interface UpdateOrderApiResponse extends ApiResponse<Order> {
  data: Order & {
    changes?: Array<{
      field: string
      oldValue: any
      newValue: any
    }>
    validationResult?: {
      isValid: boolean
      warnings?: string[]
    }
  }
}

// 订单状态更新API请求接口
export interface UpdateOrderStatusApiRequest {
  id: string | number
  status: Order['status']
  options?: {
    reason?: string
    notes?: string
    sendNotification?: boolean
    validateTransition?: boolean
  }
}

// 订单状态更新API响应接口
export interface UpdateOrderStatusApiResponse extends ApiResponse<Order> {
  data: Order & {
    statusHistory?: Array<{
      from: string
      to: string
      changedAt: string
      changedBy: string
      reason?: string
    }>
    nextAllowedStatuses?: string[]
  }
}

// 批量订单状态更新API请求接口
export interface BatchUpdateOrderStatusApiRequest {
  orderIds: (string | number)[]
  status: Order['status']
  options?: {
    reason?: string
    notes?: string
    sendNotification?: boolean
    validateTransition?: boolean
    continueOnError?: boolean
  }
}

// 批量订单状态更新API响应接口
export interface BatchUpdateOrderStatusApiResponse extends ApiResponse<OrderBatchOperationResponse> {
  data: OrderBatchOperationResponse & {
    summary: {
      totalRequested: number
      successCount: number
      failureCount: number
      skippedCount: number
    }
    details: Array<{
      orderId: string | number
      status: 'success' | 'failed' | 'skipped'
      error?: string
      newStatus?: string
    }>
  }
}

// 订单删除API响应接口
export interface DeleteOrderApiResponse extends ApiResponse<{ deleted: boolean }> {
  data: {
    deleted: boolean
    orderId: string | number
    deletedAt: string
    canRestore?: boolean
    restoreDeadline?: string
  }
}

// 批量订单删除API请求接口
export interface BatchDeleteOrderApiRequest {
  orderIds: (string | number)[]
  options?: {
    permanent?: boolean
    reason?: string
    sendNotification?: boolean
  }
}

// 批量订单删除API响应接口
export interface BatchDeleteOrderApiResponse extends ApiResponse<OrderBatchOperationResponse> {
  data: OrderBatchOperationResponse & {
    deletedAt: string
    canRestore?: boolean
    restoreDeadline?: string
  }
}

// 订单导出API响应接口
export interface OrderExportApiResponse extends ApiResponse<OrderExportResponse> {
  data: OrderExportResponse & {
    taskId: string
    estimatedTime?: number
    progress?: number
  }
}

// 订单导入API请求接口
export interface OrderImportApiRequest {
  file: File
  options?: {
    format: 'excel' | 'csv'
    skipValidation?: boolean
    updateExisting?: boolean
    dryRun?: boolean
  }
}

// 订单导入API响应接口
export interface OrderImportApiResponse extends ApiResponse<{
  taskId: string
  totalRows: number
  validRows: number
  invalidRows: number
  duplicateRows: number
  preview?: Order[]
  errors?: Array<{
    row: number
    field: string
    error: string
  }>
}> {}

// 订单搜索API请求接口
export interface OrderSearchApiRequest {
  query: string
  filters?: {
    type?: string[]
    status?: string[]
    dateRange?: [string, string]
    amountRange?: [number, number]
  }
  options?: {
    limit?: number
    includeItems?: boolean
    includeLogs?: boolean
    fuzzyMatch?: boolean
  }
}

// 订单搜索API响应接口
export interface OrderSearchApiResponse extends ApiResponse<{
  orders: Order[]
  total: number
  searchTime: number
  suggestions?: string[]
  facets?: Record<string, Array<{ value: string; count: number }>>
}> {}

// 订单验证API请求接口
export interface OrderValidationApiRequest {
  data: CreateOrderData | UpdateOrderData
  options?: {
    checkInventory?: boolean
    checkCustomer?: boolean
    checkPricing?: boolean
    strictMode?: boolean
  }
}

// 订单验证API响应接口
export interface OrderValidationApiResponse extends ApiResponse<{
  isValid: boolean
  errors: Array<{
    field: string
    code: string
    message: string
    severity: 'error' | 'warning'
  }>
  suggestions?: Array<{
    field: string
    suggestion: string
    reason: string
  }>
}> {}

// 订单物流跟踪API响应接口
export interface OrderTrackingApiResponse extends ApiResponse<{
  orderId: string | number
  trackingNumber?: string
  carrier?: string
  status: string
  events: Array<{
    timestamp: string
    status: string
    location?: string
    description: string
  }>
  estimatedDelivery?: string
  actualDelivery?: string
}> {}

// 订单物流更新API请求接口
export interface UpdateOrderTrackingApiRequest {
  id: string | number
  trackingInfo: {
    trackingNumber: string
    carrier?: string
    shippedAt?: string
    estimatedDelivery?: string
  }
}

// 订单报表API请求接口
export interface OrderReportApiRequest {
  type: 'summary' | 'detailed' | 'trend' | 'comparison'
  period: {
    startDate: string
    endDate: string
  }
  filters?: {
    type?: string[]
    status?: string[]
    salesPerson?: string[]
    customer?: string[]
  }
  groupBy?: 'day' | 'week' | 'month' | 'quarter' | 'year'
  metrics?: string[]
}

// 订单报表API响应接口
export interface OrderReportApiResponse extends ApiResponse<{
  reportType: string
  period: { startDate: string; endDate: string }
  summary: {
    totalOrders: number
    totalRevenue: number
    averageOrderValue: number
    completionRate: number
  }
  data: Array<{
    period: string
    orders: number
    revenue: number
    averageValue: number
  }>
  charts?: Array<{
    type: 'line' | 'bar' | 'pie'
    title: string
    data: any[]
  }>
  generatedAt: string
}> {}

// 订单权限检查API响应接口
export interface OrderPermissionApiResponse extends ApiResponse<{
  orderId: string | number
  permissions: {
    canView: boolean
    canEdit: boolean
    canDelete: boolean
    canUpdateStatus: boolean
    canExport: boolean
    canComment: boolean
  }
  restrictions?: {
    editableFields?: string[]
    allowedStatuses?: string[]
    maxAmount?: number
  }
  reason?: string
}> {}

// 订单配置API响应接口
export interface OrderConfigApiResponse extends ApiResponse<{
  statusFlow: Array<{
    from: string
    to: string[]
    conditions?: string[]
    permissions?: string[]
  }>
  defaultValues: {
    currency: string
    shippingFee: number
    paymentMethod: string
    salesPerson: string
  }
  validation: {
    rules: Record<string, any>
    messages: Record<string, string>
  }
  features: {
    enableRental: boolean
    enableBatch: boolean
    enableExport: boolean
    enableImport: boolean
  }
}> {}

// API错误响应接口
export interface OrderApiError {
  code: number
  message: string
  errorCode?: string
  field?: string
  details?: any
  timestamp: string
  traceId?: string
}

// API请求配置接口
export interface OrderApiConfig {
  baseURL?: string
  timeout?: number
  retries?: number
  headers?: Record<string, string>
  interceptors?: {
    request?: (config: any) => any
    response?: (response: any) => any
    error?: (error: any) => any
  }
}