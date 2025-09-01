/**
 * 客户管理模块 TypeScript 接口定义
 * 与后端DTO保持完全一致的字段映射
 * 任务16：创建前端TypeScript接口定义
 */

// 基础地址接口 - 匹配后端AddressInfo
export interface Address {
  province: string
  city: string
  detail: string
  district?: string
  postalCode?: string
}

// 设备使用统计接口
export interface DeviceUsageStats {
  totalUsageHours: number
  dailyAverageUsage: number
  lastUsedAt: string
  coursesCompleted: number
  charactersWritten: number
}

// 客户设备接口 - 匹配后端CustomerDeviceDTO
export interface CustomerDevice {
  id: string
  serialNumber: string
  model: string
  type: 'purchased' | 'rental'
  status: 'pending' | 'active' | 'offline' | 'maintenance' | 'retired'
  activatedAt?: string
  lastOnlineAt?: string
  firmwareVersion: string
  healthScore: number
  usageStats?: DeviceUsageStats
  notes?: string
  
  // 租赁相关信息
  rentalInfo?: RentalInfo
  
  // 购买相关信息
  purchaseInfo?: PurchaseInfo
}

// 租赁信息接口 - 匹配后端RentalInfoDTO
export interface RentalInfo {
  startDate: string
  endDate: string
  dailyRentalFee: number
  totalRentalFee: number
}

// 购买信息接口 - 匹配后端PurchaseInfoDTO
export interface PurchaseInfo {
  purchaseDate: string
  purchasePrice: number
  warrantyEndDate: string
}

// 客户订单接口 - 匹配后端CustomerOrderDTO
export interface CustomerOrder {
  id: string
  orderNumber: string
  type: 'sales' | 'rental'
  productName: string
  productModel: string
  quantity: number
  amount: number
  status: 'pending' | 'processing' | 'completed' | 'cancelled'
  createdAt: string
  updatedAt?: string
  
  // 租赁订单特有字段
  rentalDays?: number
  rentalStartDate?: string
  rentalEndDate?: string
  notes?: string
  
  // 订单详细信息
  orderDetails?: OrderDetails
}

// 订单详情接口 - 匹配后端OrderDetailsDTO
export interface OrderDetails {
  totalAmount: number
  discountAmount: number
  finalAmount: number
  paymentMethod: string
  paymentStatus: string
  paymentTime?: string
  deliveryAddress: string
  deliveryStatus: string
  deliveryTime?: string
}

// 服务记录附件接口
export interface ServiceAttachment {
  id: string
  name: string
  url: string
  size: number
  type: string
}

// 客户服务记录接口 - 匹配后端ServiceRecordDTO
export interface CustomerServiceRecord {
  id: string
  type: 'maintenance' | 'upgrade' | 'consultation' | 'complaint'
  subject: string
  description: string
  deviceId?: string
  serviceStaff: string
  cost?: number
  status: 'in_progress' | 'completed' | 'cancelled'
  attachments?: ServiceAttachment[]
  createdAt: string
  updatedAt?: string
}

// 设备数量统计接口 - 匹配后端DeviceCountInfo
export interface DeviceCount {
  total: number
  purchased: number
  rental: number
}

// 客户等级分布接口 - 匹配后端LevelDistributionDTO
export interface LevelDistribution {
  regularPercentage: number
  vipPercentage: number
  premiumPercentage: number
}

// 客户创建请求接口 - 匹配后端CustomerCreateDTO
export interface CustomerCreateRequest {
  name: string
  level: 'REGULAR' | 'VIP' | 'PREMIUM'
  phone: string
  email: string
  company?: string
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED'
  address?: Address
  tags?: string[]
  notes?: string
  avatar?: string
}

// 客户更新请求接口 - 匹配后端CustomerUpdateDTO
export interface CustomerUpdateRequest extends Partial<CustomerCreateRequest> {
  id: string | number
}

// 客户查询请求接口 - 匹配后端CustomerQueryDTO
export interface CustomerQueryRequest {
  page?: number
  pageSize?: number
  keyword?: string
  level?: string
  status?: string
  deviceType?: string
  region?: string
  sortBy?: string
  sortOrder?: 'ASC' | 'DESC'
  minCustomerValue?: number
  maxCustomerValue?: number
  minSpent?: number
  maxSpent?: number
  registeredStartDate?: string
  registeredEndDate?: string
  lastActiveStartDate?: string
  lastActiveEndDate?: string
  tags?: string
}

// 客户统计接口 - 完全匹配后端CustomerStatsDTO
export interface CustomerStats {
  // 基础统计字段 - 对应前端统计卡片
  total: number
  regular: number
  vip: number
  premium: number
  
  // 客户状态统计
  active?: number
  inactive?: number
  suspended?: number
  
  // 设备统计 - 对应前端设备统计卡片
  totalDevices?: number
  activeDevices: number
  purchasedDevices?: number
  rentalDevices?: number
  
  // 财务统计 - 对应前端收入统计卡片
  totalRevenue: number
  monthlyRevenue?: number
  averageOrderValue?: number
  totalSpent?: number
  
  // 订单统计
  totalOrders?: number
  monthlyOrders?: number
  completedOrders?: number
  pendingOrders?: number
  
  // 时间统计 - 对应前端新增客户统计
  newThisMonth: number
  newCustomersThisWeek?: number
  newCustomersToday?: number
  
  // 地区和行业统计
  topRegion?: string
  topRegionCount?: number
  topIndustry?: string
  topIndustryCount?: number
  
  // 客户等级分布 - 对应前端图表数据
  levelDistribution?: LevelDistribution
  
  // 统计时间
  statisticsDate?: string
  updatedAt?: string
}

// 主要客户接口 - 完全匹配后端CustomerDTO
export interface Customer {
  id: string | number
  
  // 基本信息字段 - 映射到前端
  name: string                    // 对应后端customerName字段
  level: 'regular' | 'vip' | 'premium'  // 对应后端customerLevel字段
  status: 'active' | 'inactive' | 'suspended'  // 对应后端customerStatus字段
  company?: string                // 对应后端contactPerson字段
  
  phone: string
  email: string
  avatar?: string                 // 对应后端avatarUrl字段
  tags?: string[]                 // 客户标签
  notes?: string                  // 备注信息
  
  // 地址信息 - 支持前端地址对象结构
  address?: Address
  
  // 统计信息字段 - 映射到前端
  totalSpent: number
  customerValue: number
  
  // 设备统计信息 - 支持前端设备数量显示
  deviceCount?: DeviceCount
  
  // 时间字段
  registeredAt: string
  lastActiveAt?: string
  
  // 关联数据（可选）
  devices?: CustomerDevice[]
  orders?: CustomerOrder[]
  serviceRecords?: CustomerServiceRecord[]
  
  // 保持现有字段兼容性
  customerType?: string
  region?: string
  industry?: string
  creditLevel?: string
  isActive?: boolean
  createdAt?: string
  updatedAt?: string
  
  // 统计信息（保持现有字段）
  totalOrders?: number
  totalSalesAmount?: number
  lastOrderDate?: string
}

// API 请求/响应类型 - 完全匹配后端DTO

// 客户创建数据接口 - 匹配后端CustomerCreateDTO
export interface CreateCustomerData {
  name: string                    // 对应后端name字段
  level: 'REGULAR' | 'VIP' | 'PREMIUM'  // 对应后端level字段
  phone: string                   // 对应后端phone字段
  email: string                   // 对应后端email字段
  company?: string                // 对应后端company字段
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED'  // 对应后端status字段
  address?: Address               // 对应后端address字段
  tags?: string[]                 // 对应后端tags字段
  notes?: string                  // 对应后端notes字段
  avatar?: string                 // 对应后端avatar字段
}

// 客户更新数据接口 - 匹配后端CustomerUpdateDTO
export interface UpdateCustomerData extends Partial<CreateCustomerData> {
  id?: string | number
}

// 客户查询参数接口 - 匹配后端CustomerQueryDTO
export interface CustomerQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  level?: string
  status?: string
  deviceType?: string
  region?: string
  sortBy?: string
  sortOrder?: 'ASC' | 'DESC'
  minCustomerValue?: string
  maxCustomerValue?: string
  minSpent?: string
  maxSpent?: string
  registeredStartDate?: string
  registeredEndDate?: string
  lastActiveStartDate?: string
  lastActiveEndDate?: string
  tags?: string
}

// 客户列表响应接口 - 匹配后端API响应格式
export interface CustomerListResponse {
  list: Customer[]
  total: number
  page: number
  pageSize: number
  isEmpty?: boolean
}

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

// 设备相关类型 - 匹配后端设备管理DTO
export interface AddDeviceData {
  customerId: string | number
  serialNumber: string
  model: string
  type: 'purchased' | 'rental'
  status: 'pending' | 'active' | 'offline' | 'maintenance' | 'retired'
  activatedAt?: string
  notes?: string
  firmwareVersion?: string
  healthScore?: number
}

// 服务记录相关类型 - 匹配后端服务记录DTO
export interface AddServiceRecordData {
  customerId: string | number
  type: 'maintenance' | 'upgrade' | 'consultation' | 'complaint'
  subject: string
  description: string
  deviceId?: string
  serviceStaff: string
  cost?: number
  status: 'in_progress' | 'completed' | 'cancelled'
  attachments?: ServiceAttachment[]
}

// 客户搜索建议接口
export interface CustomerSearchSuggestion {
  id: string | number
  name: string
  phone: string
  email?: string
  level: string
}

// 筛选选项接口
export interface FilterOption {
  label: string
  value: string
  count?: number
}

// 客户筛选选项接口
export interface CustomerFilterOptions {
  levels: FilterOption[]
  regions: FilterOption[]
  industries: FilterOption[]
  deviceTypes: FilterOption[]
  statuses: FilterOption[]
}

// 客户导出参数接口
export interface CustomerExportParams extends CustomerQueryParams {
  format?: 'excel' | 'csv'
  fields?: string[]
}

// 客户批量操作接口
export interface CustomerBatchOperation {
  customerIds: (string | number)[]
  operation: 'delete' | 'updateLevel' | 'updateStatus' | 'addTags' | 'removeTags'
  data?: {
    level?: string
    status?: string
    tags?: string[]
  }
}

// 客户详情扩展信息接口
export interface CustomerDetailInfo extends Customer {
  deviceHistory?: CustomerDevice[]
  orderHistory?: CustomerOrder[]
  serviceHistory?: CustomerServiceRecord[]
  statisticsInfo?: {
    totalDevices: number
    totalOrders: number
    totalServiceRecords: number
    averageOrderValue: number
    lastActivityDate: string
  }
}

// 客户等级枚举
export enum CustomerLevel {
  REGULAR = 'REGULAR',
  VIP = 'VIP',
  PREMIUM = 'PREMIUM'
}

// 客户状态枚举
export enum CustomerStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED'
}

// 设备类型枚举
export enum DeviceType {
  PURCHASED = 'purchased',
  RENTAL = 'rental'
}

// 服务记录类型枚举
export enum ServiceRecordType {
  MAINTENANCE = 'maintenance',
  UPGRADE = 'upgrade',
  CONSULTATION = 'consultation',
  COMPLAINT = 'complaint'
}

// 服务记录状态枚举
export enum ServiceRecordStatus {
  IN_PROGRESS = 'in_progress',
  COMPLETED = 'completed',
  CANCELLED = 'cancelled'
}

// 设备状态枚举
export enum DeviceStatus {
  PENDING = 'pending',
  ACTIVE = 'active',
  OFFLINE = 'offline',
  MAINTENANCE = 'maintenance',
  RETIRED = 'retired'
}

// 订单状态枚举
export enum OrderStatus {
  PENDING = 'pending',
  PROCESSING = 'processing',
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
  UNPAID = 'unpaid',
  PARTIAL = 'partial',
  PAID = 'paid',
  REFUNDED = 'refunded'
}

// 配送状态枚举
export enum DeliveryStatus {
  PENDING = 'pending',
  SHIPPED = 'shipped',
  DELIVERED = 'delivered',
  CANCELLED = 'cancelled'
}

// 客户详情扩展信息接口
export interface CustomerDetailExtended extends Customer {
  deviceHistory?: CustomerDevice[]
  orderHistory?: CustomerOrder[]
  serviceHistory?: CustomerServiceRecord[]
  statisticsInfo?: CustomerStatisticsInfo
}

// 客户统计信息接口
export interface CustomerStatisticsInfo {
  totalDevices: number
  totalOrders: number
  totalServiceRecords: number
  averageOrderValue: number
  lastActivityDate: string
}

// 客户搜索建议响应接口
export interface CustomerSearchSuggestionResponse {
  suggestions: CustomerSearchSuggestion[]
  total: number
}

// 客户筛选选项响应接口
export interface CustomerFilterOptionsResponse {
  levels: FilterOption[]
  regions: FilterOption[]
  industries: FilterOption[]
  deviceTypes: FilterOption[]
  statuses: FilterOption[]
}

// 客户导出响应接口
export interface CustomerExportResponse {
  downloadUrl: string
  fileName: string
  fileSize: number
  expiresAt: string
}

// 客户批量操作响应接口
export interface CustomerBatchOperationResponse {
  successCount: number
  failureCount: number
  errors?: string[]
  processedIds: (string | number)[]
}