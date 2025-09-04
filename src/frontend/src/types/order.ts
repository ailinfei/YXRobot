// 订单商品明细接口 - 完全匹配后端OrderItemDTO
export interface OrderItem {
  id?: string | number
  orderId?: string | number
  productId: string | number
  productName: string
  productModel?: string
  quantity: number
  unitPrice: number
  totalPrice: number
  notes?: string
  createdAt?: string
  updatedAt?: string
}

export interface ShippingInfo {
  company?: string
  trackingNumber?: string
  shippedAt?: string
  deliveredAt?: string
}

export interface OrderLog {
  id: string
  action: string
  operator: string
  notes?: string
  createdAt: string
}

// 主要订单接口 - 完全匹配后端OrderDTO
export interface Order {
  id: string | number
  orderNumber: string
  type: 'sales' | 'rental'
  status: 'pending' | 'confirmed' | 'processing' | 'shipped' | 'delivered' | 'completed' | 'cancelled'
  
  // 客户信息
  customerId: string | number
  customerName: string
  customerPhone: string
  customerEmail?: string
  deliveryAddress: string
  
  // 商品信息 - 匹配后端orderItems字段
  items: OrderItem[]
  orderItems?: OrderItem[]  // 兼容后端字段名
  
  // 金额信息
  subtotal: number
  shippingFee?: number
  discount?: number
  totalAmount: number
  currency?: string
  
  // 支付信息
  paymentStatus?: 'pending' | 'paid' | 'failed' | 'refunded'
  paymentMethod?: string
  paymentTime?: string
  
  // 物流信息
  shippingInfo?: ShippingInfo
  
  // 租赁信息 (仅租赁订单)
  rentalStartDate?: string
  rentalEndDate?: string
  rentalDays?: number
  rentalNotes?: string
  
  // 其他信息
  expectedDeliveryDate?: string
  salesPerson?: string
  notes?: string
  
  // 系统信息
  createdAt: string
  updatedAt?: string
  createdBy?: string
  
  // 操作日志 - 匹配后端orderLogs字段
  logs?: OrderLog[]
  orderLogs?: OrderLog[]  // 兼容后端字段名
}

// 订单统计接口 - 完全匹配后端OrderStatsDTO
export interface OrderStats {
  total: number
  pending: number
  confirmed?: number
  processing: number
  shipped?: number
  delivered?: number
  completed: number
  cancelled: number
  totalRevenue: number
  averageOrderValue: number
  salesOrders: number
  rentalOrders: number
}

// 物流信息接口 - 匹配后端ShippingInfoDTO
export interface ShippingInfoDTO {
  id?: string
  orderId: string
  company?: string
  trackingNumber?: string
  shippedAt?: string
  deliveredAt?: string
  estimatedDeliveryDate?: string
  actualDeliveryDate?: string
  deliveryAddress: string
  recipientName: string
  recipientPhone: string
  notes?: string
  status: 'pending' | 'shipped' | 'in_transit' | 'delivered' | 'failed'
  createdAt?: string
  updatedAt?: string
}

// 订单日志接口 - 匹配后端OrderLogDTO
export interface OrderLogDTO {
  id: string
  orderId: string
  action: string
  operator: string
  operatorId?: string
  notes?: string
  oldValue?: string
  newValue?: string
  ipAddress?: string
  userAgent?: string
  createdAt: string
}

export interface OrderQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  type?: 'sales' | 'rental'
  status?: Order['status']
  dateRange?: [string, string]
  customerId?: string
  salesPerson?: string
}

// 订单创建数据接口 - 匹配后端创建订单需求
export interface CreateOrderData {
  orderNumber: string
  type: 'sales' | 'rental'
  status?: 'pending' | 'confirmed' | 'processing' | 'shipped' | 'delivered' | 'completed' | 'cancelled'
  customerId: string | number
  customerName: string
  customerPhone: string
  customerEmail?: string
  deliveryAddress: string
  items: Omit<OrderItem, 'id' | 'orderId' | 'createdAt' | 'updatedAt'>[]
  subtotal: number
  shippingFee?: number
  discount?: number
  totalAmount: number
  currency?: string
  paymentStatus?: 'pending' | 'paid' | 'failed' | 'refunded'
  paymentMethod?: string
  paymentTime?: string
  expectedDeliveryDate?: string
  salesPerson?: string
  notes?: string
  // 租赁相关字段
  rentalStartDate?: string
  rentalEndDate?: string
  rentalDays?: number
  rentalNotes?: string
}

// 订单更新数据接口 - 匹配后端更新订单需求
export interface UpdateOrderData extends Partial<CreateOrderData> {
  id?: string | number
}

// 订单查询数据接口 - 匹配后端OrderQueryDTO
export interface OrderQueryData {
  page?: number
  pageSize?: number
  keyword?: string
  type?: 'sales' | 'rental'
  status?: Order['status']
  customerId?: string | number
  salesPerson?: string
  startDate?: string
  endDate?: string
  minAmount?: number
  maxAmount?: number
  paymentStatus?: string
  sortBy?: string
  sortOrder?: 'ASC' | 'DESC'
}

export interface OrderListResponse {
  list: Order[]
  total: number
  stats: OrderStats
}

// API请求和响应类型定义

// 订单创建请求接口 - 匹配后端CreateOrderRequest
export interface CreateOrderRequest {
  orderNumber?: string
  type: 'sales' | 'rental'
  
  // 客户信息
  customerId?: string
  customerName: string
  customerPhone: string
  customerEmail?: string
  deliveryAddress: string
  
  // 商品信息
  items: Array<{
    productId: string
    productName: string
    quantity: number
    unitPrice: number
    totalPrice: number
    notes?: string
  }>
  
  // 金额信息
  subtotal?: number
  shippingFee?: number
  discount?: number
  totalAmount: number
  currency?: string
  
  // 支付信息
  paymentMethod?: string
  
  // 租赁信息 (仅租赁订单)
  rentalStartDate?: string
  rentalEndDate?: string
  rentalDays?: number
  rentalNotes?: string
  
  // 其他信息
  expectedDeliveryDate?: string
  salesPerson?: string
  notes?: string
}

// 订单更新请求接口 - 匹配后端UpdateOrderRequest
export interface UpdateOrderRequest extends Partial<CreateOrderRequest> {
  id?: string
  status?: Order['status']
  paymentStatus?: 'pending' | 'paid' | 'failed' | 'refunded'
}

// 订单状态更新请求接口
export interface UpdateOrderStatusRequest {
  status: Order['status']
  notes?: string
  operator?: string
}

// 批量订单状态更新请求接口
export interface BatchUpdateOrderStatusRequest {
  orderIds: string[]
  status: Order['status']
  notes?: string
  operator?: string
}

// 批量删除订单请求接口
export interface BatchDeleteOrdersRequest {
  orderIds: string[]
  reason?: string
  operator?: string
}

// 订单导出请求接口
export interface ExportOrdersRequest extends OrderQueryParams {
  format?: 'excel' | 'csv' | 'pdf'
  fields?: string[]
  includeItems?: boolean
  includeCustomerInfo?: boolean
  includeShippingInfo?: boolean
}

// 订单搜索建议接口
export interface OrderSearchSuggestion {
  id: string
  orderNumber: string
  customerName: string
  totalAmount: number
  status: string
  type: string
  createdAt: string
}

// 订单筛选选项接口
export interface OrderFilterOptions {
  types: Array<{ label: string; value: string; count?: number }>
  statuses: Array<{ label: string; value: string; count?: number }>
  paymentStatuses: Array<{ label: string; value: string; count?: number }>
  salesPersons: Array<{ label: string; value: string; count?: number }>
  customers: Array<{ label: string; value: string; count?: number }>
}

// 订单详情扩展接口 - 包含完整的关联信息
export interface OrderDetailExtended extends Order {
  // 客户完整信息
  customer?: {
    id: string
    name: string
    phone: string
    email?: string
    level: string
    company?: string
    address?: string
  }
  
  // 产品完整信息
  itemsWithDetails?: Array<OrderItem & {
    product?: {
      id: string
      name: string
      model: string
      description?: string
      image?: string
      specifications?: Record<string, string>
    }
  }>
  
  // 完整的物流信息
  shippingInfoDetail?: ShippingInfoDTO
  
  // 完整的操作日志
  operationLogs?: OrderLogDTO[]
  
  // 相关订单
  relatedOrders?: Array<{
    id: string
    orderNumber: string
    type: string
    status: string
    totalAmount: number
    createdAt: string
  }>
}

// 订单统计详情接口
export interface OrderStatsDetail extends OrderStats {
  // 按时间统计
  todayOrders: number
  weekOrders: number
  monthOrders: number
  yearOrders: number
  
  // 按金额统计
  todayRevenue: number
  weekRevenue: number
  monthRevenue: number
  yearRevenue: number
  
  // 按类型统计
  salesOrdersRevenue: number
  rentalOrdersRevenue: number
  
  // 按状态统计
  pendingRevenue: number
  processingRevenue: number
  completedRevenue: number
  
  // 增长率统计
  orderGrowthRate: number
  revenueGrowthRate: number
  
  // 客户统计
  totalCustomers: number
  newCustomers: number
  returningCustomers: number
  
  // 产品统计
  totalProducts: number
  topSellingProducts: Array<{
    productId: string
    productName: string
    quantity: number
    revenue: number
  }>
}

// API响应格式接口
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp?: string
  success?: boolean
}

// 分页响应接口
export interface PaginatedResponse<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
  totalPages?: number
  hasNext?: boolean
  hasPrev?: boolean
}

// 订单操作结果接口
export interface OrderOperationResult {
  success: boolean
  message: string
  affectedCount?: number
  errors?: string[]
  data?: any
}

// 订单状态文本映射
export const ORDER_STATUS_TEXT = {
  pending: '待确认',
  confirmed: '已确认',
  processing: '处理中',
  shipped: '已发货',
  delivered: '已送达',
  completed: '已完成',
  cancelled: '已取消'
} as const

// 订单类型文本映射
export const ORDER_TYPE_TEXT = {
  sales: '销售订单',
  rental: '租赁订单'
} as const

// 支付状态文本映射
export const PAYMENT_STATUS_TEXT = {
  pending: '待支付',
  paid: '已支付',
  failed: '支付失败',
  refunded: '已退款'
} as const

// 订单状态枚举
export enum OrderStatus {
  PENDING = 'pending',
  CONFIRMED = 'confirmed',
  PROCESSING = 'processing',
  SHIPPED = 'shipped',
  DELIVERED = 'delivered',
  COMPLETED = 'completed',
  CANCELLED = 'cancelled'
}

// 订单类型枚举
export enum OrderType {
  SALES = 'sales',
  RENTAL = 'rental'
}

// 支付状态枚举
export enum PaymentStatus {
  PENDING = 'pending',
  PAID = 'paid',
  FAILED = 'failed',
  REFUNDED = 'refunded'
}

// 批量操作接口
export interface OrderBatchOperation {
  orderIds: (string | number)[]
  operation: 'updateStatus' | 'delete' | 'export'
  data?: {
    status?: Order['status']
    notes?: string
  }
}

// 批量操作响应接口
export interface OrderBatchOperationResponse {
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

// 订单导出参数接口
export interface OrderExportParams extends OrderQueryParams {
  format?: 'excel' | 'csv' | 'pdf'
  fields?: string[]
  includeItems?: boolean
  includeLogs?: boolean
}

// 订单导出响应接口
export interface OrderExportResponse {
  downloadUrl: string
  fileName: string
  fileSize: number
  expiresAt: string
  taskId?: string
}

// 订单搜索建议接口
export interface OrderSearchSuggestion {
  id: string | number
  orderNumber: string
  customerName: string
  type: OrderType
  status: OrderStatus
  totalAmount: number
  createdAt: string
}

// 订单筛选选项接口
export interface OrderFilterOptions {
  types: Array<{ label: string; value: string; count?: number }>
  statuses: Array<{ label: string; value: string; count?: number }>
  paymentStatuses: Array<{ label: string; value: string; count?: number }>
  salesPersons: Array<{ label: string; value: string; count?: number }>
  customers: Array<{ label: string; value: string; count?: number }>
}

// 订单验证响应接口
export interface OrderValidationResponse {
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

// 订单状态流转规则接口
export interface OrderStatusTransition {
  from: OrderStatus
  to: OrderStatus[]
  conditions?: string[]
  permissions?: string[]
}

// 订单操作权限接口
export interface OrderPermissions {
  canView: boolean
  canCreate: boolean
  canEdit: boolean
  canDelete: boolean
  canUpdateStatus: boolean
  canExport: boolean
  canBatchOperate: boolean
  allowedStatuses?: OrderStatus[]
  restrictions?: Record<string, any>
}

// 订单管理中使用的简化Customer接口
export interface CustomerForOrder {
  id: string
  name: string
  phone: string
  email?: string
  company?: string
  address?: string
  level?: 'regular' | 'vip' | 'premium'
  status?: 'active' | 'inactive' | 'suspended'
}

// 订单管理中使用的简化Product接口
export interface ProductForOrder {
  id: string
  name: string
  model: string
  price: number
  originalPrice?: number
  description?: string
  image?: string
  coverImageUrl?: string
  status: 'draft' | 'published' | 'archived'
  stock?: number
  specifications?: Record<string, string>
  features?: string[]
}

// 订单商品选择接口
export interface ProductSelection {
  product: ProductForOrder
  quantity: number
  unitPrice: number
  totalPrice: number
  notes?: string
}

// 客户选择接口
export interface CustomerSelection {
  customer: CustomerForOrder
  deliveryAddress?: string
  contactPhone?: string
  contactEmail?: string
}

// 订单表单数据接口
export interface OrderFormData {
  // 基本信息
  type: 'sales' | 'rental'
  orderNumber?: string
  
  // 客户信息
  customerSelection?: CustomerSelection
  customerId?: string
  customerName: string
  customerPhone: string
  customerEmail?: string
  deliveryAddress: string
  
  // 商品信息
  productSelections: ProductSelection[]
  items: OrderItem[]
  
  // 金额信息
  subtotal: number
  shippingFee: number
  discount: number
  totalAmount: number
  currency: string
  
  // 租赁信息
  rentalStartDate?: string
  rentalEndDate?: string
  rentalDays?: number
  rentalNotes?: string
  
  // 其他信息
  expectedDeliveryDate?: string
  salesPerson?: string
  notes?: string
  paymentMethod?: string
}

// 订单验证结果接口
export interface OrderValidationResult {
  isValid: boolean
  errors: Array<{
    field: string
    message: string
    code?: string
  }>
  warnings?: Array<{
    field: string
    message: string
    code?: string
  }>
}

// 订单计算结果接口
export interface OrderCalculationResult {
  subtotal: number
  shippingFee: number
  discount: number
  totalAmount: number
  taxAmount?: number
  finalAmount: number
  savings?: number
}

// 订单预览接口
export interface OrderPreview {
  orderData: OrderFormData
  calculation: OrderCalculationResult
  validation: OrderValidationResult
  estimatedDeliveryDate?: string
  availablePaymentMethods?: string[]
  recommendedProducts?: ProductForOrder[]
}