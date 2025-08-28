// 订单类型定义
export interface OrderItem {
  id?: string
  productId: string
  productName: string
  quantity: number
  unitPrice: number
  totalPrice: number
  notes?: string
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

export interface Order {
  id: string
  orderNumber: string
  type: 'sales' | 'rental'
  status: 'pending' | 'confirmed' | 'processing' | 'shipped' | 'delivered' | 'completed' | 'cancelled'
  
  // 客户信息
  customerId?: string
  customerName: string
  customerPhone: string
  customerEmail?: string
  deliveryAddress: string
  
  // 商品信息
  items: OrderItem[]
  
  // 金额信息
  subtotal?: number
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
  
  // 操作日志
  logs?: OrderLog[]
}

export interface OrderStats {
  total: number
  pending: number
  processing: number
  completed: number
  cancelled: number
  totalRevenue: number
  averageOrderValue: number
  salesOrders: number
  rentalOrders: number
}

export interface OrderQuery {
  page?: number
  pageSize?: number
  keyword?: string
  type?: 'sales' | 'rental'
  status?: Order['status']
  dateRange?: [string, string]
  customerId?: string
  salesPerson?: string
}

export interface OrderListResponse {
  list: Order[]
  total: number
  stats: OrderStats
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