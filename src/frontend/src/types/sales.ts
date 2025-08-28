/**
 * 销售数据模块 TypeScript 接口定义
 * 与后端DTO保持完全一致的字段映射
 */

// 销售记录接口
export interface SalesRecord {
  id: number
  orderNumber: string
  customerId: number
  productId: number
  salesStaffId: number
  salesAmount: number
  quantity: number
  unitPrice: number
  discountAmount: number
  orderDate: string
  deliveryDate?: string
  status: SalesStatus
  paymentStatus: PaymentStatus
  paymentMethod?: string
  region?: string
  channel?: string
  notes?: string
  createdAt: string
  updatedAt: string
  
  // 关联数据（查询时包含，匹配后端DTO）
  customerName: string      // 客户姓名
  customerPhone: string     // 客户电话（新增）
  productName: string       // 产品名称
  staffName: string         // 销售人员姓名（修复字段名）
}

// 客户接口
export interface Customer {
  id: number
  customerName: string
  customerType: CustomerType
  contactPerson?: string
  phone?: string
  email?: string
  address?: string
  region?: string
  industry?: string
  creditLevel: CreditLevel
  isActive: boolean
  createdAt: string
  updatedAt: string
  
  // 统计信息（可选）
  totalOrders?: number
  totalSalesAmount?: number
  lastOrderDate?: string
}

// 销售产品接口
export interface SalesProduct {
  id: number
  productName: string
  productCode: string
  category: string
  brand?: string
  model?: string
  unitPrice: number
  costPrice?: number
  stockQuantity: number
  unit: string
  description?: string
  specifications?: any
  isActive: boolean
  createdAt: string
  updatedAt: string
}

// 销售人员接口
export interface SalesStaff {
  id: number
  staffName: string
  staffCode: string
  department?: string
  position?: string
  phone?: string
  email?: string
  hireDate?: string
  salesTarget?: number
  commissionRate?: number
  isActive: boolean
  createdAt: string
  updatedAt: string
  
  // 业绩统计（可选）
  currentMonthSales?: number
  currentMonthOrders?: number
  totalSalesAmount?: number
  totalOrders?: number
  customerCount?: number
  targetCompletionRate?: number
}

// 销售统计接口
export interface SalesStats {
  totalSalesAmount: number
  totalOrders: number
  avgOrderAmount: number
  totalQuantity: number
  newCustomers: number
  activeCustomers: number
  growthRate?: number
}

// 图表数据接口
export interface SalesChartData {
  categories: string[]
  series: ChartSeries[]
}

export interface ChartSeries {
  name: string
  type: string
  data: number[]
  color?: string
}

// 枚举类型
export enum SalesStatus {
  PENDING = 'pending',
  CONFIRMED = 'confirmed', 
  DELIVERED = 'delivered',
  COMPLETED = 'completed',
  CANCELLED = 'cancelled'
}

export enum PaymentStatus {
  UNPAID = 'unpaid',
  PARTIAL = 'partial',
  PAID = 'paid',
  REFUNDED = 'refunded'
}

export enum CustomerType {
  INDIVIDUAL = 'individual',
  ENTERPRISE = 'enterprise'
}

export enum CreditLevel {
  A = 'A',
  B = 'B', 
  C = 'C',
  D = 'D'
}

// 查询参数接口
export interface SalesRecordQuery {
  page?: number
  pageSize?: number
  startDate?: string
  endDate?: string
  customerId?: number
  productId?: number
  staffId?: number
  status?: SalesStatus
  keyword?: string
}

export interface CustomerQuery {
  page?: number
  pageSize?: number
  keyword?: string
  type?: CustomerType
  region?: string
  industry?: string
  creditLevel?: CreditLevel
  active?: boolean
}

export interface ProductQuery {
  page?: number
  pageSize?: number
  keyword?: string
  category?: string
  brand?: string
  active?: boolean
}

export interface StaffQuery {
  page?: number
  pageSize?: number
  keyword?: string
  department?: string
  position?: string
  active?: boolean
}

// 表单数据接口
export interface SalesRecordForm {
  orderNumber: string
  customerId: number
  productId: number
  salesStaffId: number
  salesAmount: number
  quantity: number
  unitPrice: number
  discountAmount?: number
  orderDate: string
  deliveryDate?: string
  status: SalesStatus
  paymentStatus: PaymentStatus
  paymentMethod?: string
  region?: string
  channel?: string
  notes?: string
}

// API响应接口
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface PageResponse<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
  isEmpty: boolean
}

// 批量操作接口
export interface BatchOperation {
  ids: number[]
  operation: string
  params?: Record<string, any>
}