/**
 * 租赁业务相关TypeScript接口定义
 * 与后端Java实体类和DTO保持完全一致
 * 
 * @author Kiro
 * @date 2025-01-28
 */

// ==================== 基础枚举类型 ====================

/**
 * 设备状态枚举
 * 对应后端 DeviceStatus 枚举
 */
export enum DeviceStatus {
  ACTIVE = 'active',           // 运行中
  IDLE = 'idle',              // 空闲
  MAINTENANCE = 'maintenance', // 维护中
  RENTING = 'renting',        // 租赁中
  RETIRED = 'retired'         // 已退役
}

/**
 * 租赁状态枚举
 * 对应后端 RentalStatus 枚举
 */
export enum RentalStatus {
  PENDING = 'pending',         // 待确认
  ACTIVE = 'active',          // 进行中
  EXPIRED = 'expired',        // 已过期
  RETURNED = 'returned',      // 已归还
  CANCELLED = 'cancelled'     // 已取消
}

/**
 * 付款状态枚举
 * 对应后端 PaymentStatus 枚举
 */
export enum PaymentStatus {
  UNPAID = 'unpaid',          // 未付款
  PARTIAL = 'partial',        // 部分付款
  PAID = 'paid',             // 已付款
  REFUNDED = 'refunded'      // 已退款
}

/**
 * 客户类型枚举
 * 对应后端 CustomerType 枚举
 */
export enum CustomerType {
  INDIVIDUAL = 'individual',   // 个人
  ENTERPRISE = 'enterprise',   // 企业
  INSTITUTION = 'institution'  // 机构
}

/**
 * 维护状态枚举
 * 对应后端 MaintenanceStatus 枚举
 */
export enum MaintenanceStatus {
  NORMAL = 'normal',          // 正常
  WARNING = 'warning',        // 需关注
  URGENT = 'urgent'          // 需维护
}

/**
 * 网络类型枚举
 */
export enum NetworkType {
  WIFI = 'WiFi',
  ETHERNET = 'Ethernet',
  MOBILE_4G = '4G',
  MOBILE_5G = '5G'
}

// ==================== 核心业务接口 ====================

/**
 * 租赁统计数据接口
 * 对应后端 RentalStatsDTO
 */
export interface RentalStats {
  /** 总租赁收入 */
  totalRentalRevenue: number
  /** 租赁设备总数 */
  totalRentalDevices: number
  /** 活跃租赁设备数 */
  activeRentalDevices: number
  /** 设备利用率 */
  deviceUtilizationRate: number
  /** 平均租赁周期（天） */
  averageRentalPeriod: number
  /** 租赁订单总数 */
  totalRentalOrders: number
  /** 收入增长率 */
  revenueGrowthRate: number
  /** 设备增长率 */
  deviceGrowthRate: number
}

/**
 * 租赁趋势数据接口
 * 对应后端 RentalTrendDTO
 */
export interface RentalTrendData {
  /** 日期 */
  date: string
  /** 收入 */
  revenue: number
  /** 订单数量 */
  orderCount: number
  /** 设备数量 */
  deviceCount: number
  /** 利用率 */
  utilizationRate: number
}

/**
 * 设备利用率数据接口
 * 对应后端 DeviceUtilizationDTO
 */
export interface DeviceUtilizationData {
  /** 设备ID */
  deviceId: string
  /** 设备型号 */
  deviceModel: string
  /** 利用率 */
  utilizationRate: number
  /** 租赁天数 */
  totalRentalDays: number
  /** 可用天数 */
  totalAvailableDays: number
  /** 当前状态 */
  currentStatus: DeviceStatus
  /** 最后租赁日期 */
  lastRentalDate?: string
  /** 所在地区 */
  region?: string
  /** 性能评分 (0-100) */
  performanceScore?: number
  /** 信号强度 (0-100) */
  signalStrength?: number
  /** 维护状态 */
  maintenanceStatus?: MaintenanceStatus
  /** 预测故障风险 (0-100) */
  predictedFailureRisk?: number
  /** 网络类型 */
  networkType?: NetworkType
  /** 最后维护时间 */
  lastMaintenanceDate?: string
  /** 下次维护时间 */
  nextMaintenanceDate?: string
}

/**
 * 租赁记录接口
 * 对应后端 RentalRecord 实体类
 */
export interface RentalRecord {
  /** 记录ID */
  id: number
  /** 租赁订单号 */
  rentalOrderNumber: string
  /** 设备ID */
  deviceId: number
  /** 客户ID */
  customerId: number
  /** 租赁开始日期 */
  rentalStartDate: string
  /** 租赁结束日期 */
  rentalEndDate?: string
  /** 计划结束日期 */
  plannedEndDate: string
  /** 租赁期间（天数） */
  rentalPeriod: number
  /** 日租金 */
  dailyRentalFee: number
  /** 总租金 */
  totalRentalFee: number
  /** 押金金额 */
  depositAmount: number
  /** 实际支付金额 */
  actualPayment: number
  /** 付款状态 */
  paymentStatus: PaymentStatus
  /** 租赁状态 */
  rentalStatus: RentalStatus
  /** 交付方式 */
  deliveryMethod?: string
  /** 交付地址 */
  deliveryAddress?: string
  /** 实际归还日期 */
  returnDate?: string
  /** 归还状态 */
  returnCondition?: string
  /** 备注信息 */
  notes?: string
  /** 创建时间 */
  createdAt: string
  /** 更新时间 */
  updatedAt: string
  /** 是否删除 */
  isDeleted: boolean
}

/**
 * 租赁设备接口
 * 对应后端 RentalDevice 实体类
 */
export interface RentalDevice {
  /** 设备ID */
  id: number
  /** 设备编号 */
  deviceId: string
  /** 设备型号 */
  deviceModel: string
  /** 设备名称 */
  deviceName: string
  /** 设备类别 */
  deviceCategory: string
  /** 序列号 */
  serialNumber?: string
  /** 采购日期 */
  purchaseDate?: string
  /** 采购价格 */
  purchasePrice?: number
  /** 日租金价格 */
  dailyRentalPrice: number
  /** 当前状态 */
  currentStatus: DeviceStatus
  /** 设备位置 */
  location?: string
  /** 所在地区 */
  region?: string
  /** 性能评分（0-100） */
  performanceScore: number
  /** 信号强度（0-100） */
  signalStrength: number
  /** 维护状态 */
  maintenanceStatus: MaintenanceStatus
  /** 最后维护日期 */
  lastMaintenanceDate?: string
  /** 下次维护日期 */
  nextMaintenanceDate?: string
  /** 累计租赁天数 */
  totalRentalDays: number
  /** 累计可用天数 */
  totalAvailableDays: number
  /** 利用率 */
  utilizationRate: number
  /** 最后租赁日期 */
  lastRentalDate?: string
  /** 是否启用 */
  isActive: boolean
  /** 创建时间 */
  createdAt: string
  /** 更新时间 */
  updatedAt: string
  /** 是否删除 */
  isDeleted: boolean
}

/**
 * 租赁客户接口
 * 对应后端 RentalCustomer 实体类
 */
export interface RentalCustomer {
  /** 客户ID */
  id: number
  /** 客户名称 */
  customerName: string
  /** 客户类型 */
  customerType: CustomerType
  /** 联系人 */
  contactPerson?: string
  /** 联系电话 */
  phone?: string
  /** 邮箱地址 */
  email?: string
  /** 地址 */
  address?: string
  /** 所在地区 */
  region?: string
  /** 所属行业 */
  industry?: string
  /** 信用等级 */
  creditLevel?: string
  /** 累计租赁金额 */
  totalRentalAmount: number
  /** 累计租赁天数 */
  totalRentalDays: number
  /** 最后租赁日期 */
  lastRentalDate?: string
  /** 是否活跃 */
  isActive: boolean
  /** 创建时间 */
  createdAt: string
  /** 更新时间 */
  updatedAt: string
  /** 是否删除 */
  isDeleted: boolean
}

// ==================== 分析和报表接口 ====================

/**
 * 租赁收入分析数据接口
 */
export interface RentalRevenueAnalysis {
  /** 分析周期 */
  period: string
  /** 总收入 */
  totalRevenue: number
  /** 订单数量 */
  orderCount: number
  /** 平均订单价值 */
  averageOrderValue: number
  /** 表现最佳的设备 */
  topPerformingDevices: Array<{
    deviceModel: string
    revenue: number
    orderCount: number
    utilizationRate: number
  }>
  /** 地区分布 */
  regionDistribution: Array<{
    region: string
    revenue: number
    orderCount: number
    deviceCount: number
  }>
}

/**
 * 设备状态统计接口
 */
export interface DeviceStatusStats {
  /** 运行中设备数 */
  active: number
  /** 空闲设备数 */
  idle: number
  /** 维护中设备数 */
  maintenance: number
  /** 租赁中设备数 */
  renting?: number
  /** 已退役设备数 */
  retired?: number
}

/**
 * 今日统计数据接口
 */
export interface TodayStats {
  /** 今日收入 */
  revenue: number
  /** 新增订单 */
  orders: number
  /** 活跃设备 */
  activeDevices: number
  /** 平均利用率 */
  avgUtilization: number
}

// ==================== 图表数据接口 ====================

/**
 * 图表数据点接口
 */
export interface ChartDataPoint {
  /** 标签 */
  label: string
  /** 数值 */
  value: number
  /** 额外数据 */
  extra?: Record<string, any>
}

/**
 * 图表数据系列接口
 */
export interface ChartSeries {
  /** 系列名称 */
  name: string
  /** 数据点 */
  data: ChartDataPoint[]
  /** 系列类型 */
  type?: 'line' | 'bar' | 'pie' | 'area'
  /** 颜色 */
  color?: string
}

/**
 * 图表配置接口
 */
export interface ChartConfig {
  /** 图表标题 */
  title?: string
  /** X轴标签 */
  xAxisLabel?: string
  /** Y轴标签 */
  yAxisLabel?: string
  /** 数据系列 */
  series: ChartSeries[]
  /** 图表类型 */
  type: 'line' | 'bar' | 'pie' | 'area' | 'mixed'
}

// ==================== API请求和响应接口 ====================

/**
 * 分页查询参数接口
 */
export interface PaginationParams {
  /** 页码 */
  page?: number
  /** 每页大小 */
  pageSize?: number
}

/**
 * 排序参数接口
 */
export interface SortParams {
  /** 排序字段 */
  sortBy?: string
  /** 排序方向 */
  sortOrder?: 'asc' | 'desc'
}

/**
 * 日期范围参数接口
 */
export interface DateRangeParams {
  /** 开始日期 */
  startDate?: string
  /** 结束日期 */
  endDate?: string
}

/**
 * 设备查询参数接口
 */
export interface DeviceQueryParams extends PaginationParams, SortParams, DateRangeParams {
  /** 设备型号 */
  deviceModel?: string
  /** 设备状态 */
  status?: DeviceStatus
  /** 地区 */
  region?: string
  /** 关键词搜索 */
  keyword?: string
  /** 最小利用率 */
  minUtilization?: number
  /** 最大利用率 */
  maxUtilization?: number
}

/**
 * 租赁记录查询参数接口
 */
export interface RentalRecordQueryParams extends PaginationParams, SortParams, DateRangeParams {
  /** 租赁状态 */
  status?: RentalStatus
  /** 客户ID */
  customerId?: number
  /** 设备型号 */
  deviceModel?: string
  /** 关键词搜索 */
  keyword?: string
}

/**
 * 趋势数据查询参数接口
 */
export interface TrendQueryParams extends DateRangeParams {
  /** 时间周期 */
  period: 'daily' | 'weekly' | 'monthly' | 'quarterly' | 'yearly'
}

/**
 * 收入分析查询参数接口
 */
export interface RevenueAnalysisParams {
  /** 分析周期 */
  period: 'monthly' | 'quarterly' | 'yearly'
  /** 年份 */
  year?: number
  /** 季度 */
  quarter?: number
  /** 月份 */
  month?: number
}

// ==================== 表单数据接口 ====================

/**
 * 租赁记录表单数据接口
 */
export interface RentalRecordFormData {
  /** 设备ID */
  deviceId: number
  /** 客户ID */
  customerId: number
  /** 租赁开始日期 */
  rentalStartDate: string
  /** 计划结束日期 */
  plannedEndDate: string
  /** 租赁期间（天数） */
  rentalPeriod: number
  /** 日租金 */
  dailyRentalFee: number
  /** 押金金额 */
  depositAmount?: number
  /** 交付方式 */
  deliveryMethod?: string
  /** 交付地址 */
  deliveryAddress?: string
  /** 备注信息 */
  notes?: string
}

/**
 * 设备表单数据接口
 */
export interface RentalDeviceFormData {
  /** 设备编号 */
  deviceId: string
  /** 设备型号 */
  deviceModel: string
  /** 设备名称 */
  deviceName: string
  /** 设备类别 */
  deviceCategory: string
  /** 序列号 */
  serialNumber?: string
  /** 采购日期 */
  purchaseDate?: string
  /** 采购价格 */
  purchasePrice?: number
  /** 日租金价格 */
  dailyRentalPrice: number
  /** 设备位置 */
  location?: string
  /** 所在地区 */
  region?: string
}

/**
 * 客户表单数据接口
 */
export interface RentalCustomerFormData {
  /** 客户名称 */
  customerName: string
  /** 客户类型 */
  customerType: CustomerType
  /** 联系人 */
  contactPerson?: string
  /** 联系电话 */
  phone?: string
  /** 邮箱地址 */
  email?: string
  /** 地址 */
  address?: string
  /** 所在地区 */
  region?: string
  /** 所属行业 */
  industry?: string
}

// ==================== 导出和报表接口 ====================

/**
 * 导出参数接口
 */
export interface ExportParams {
  /** 导出类型 */
  type: 'orders' | 'revenue' | 'utilization' | 'devices' | 'customers'
  /** 导出格式 */
  format: 'excel' | 'pdf' | 'csv'
  /** 开始日期 */
  startDate?: string
  /** 结束日期 */
  endDate?: string
  /** 筛选条件 */
  filters?: Record<string, any>
}

/**
 * 报表配置接口
 */
export interface ReportConfig {
  /** 报表标题 */
  title: string
  /** 报表类型 */
  type: 'summary' | 'detail' | 'analysis'
  /** 包含的数据字段 */
  fields: string[]
  /** 筛选条件 */
  filters?: Record<string, any>
  /** 排序配置 */
  sort?: SortParams
}

// ==================== 验证和错误处理接口 ====================

/**
 * 表单验证错误接口
 */
export interface ValidationError {
  /** 字段名 */
  field: string
  /** 错误消息 */
  message: string
  /** 错误代码 */
  code?: string
}

/**
 * API错误响应接口
 */
export interface ApiError {
  /** 错误代码 */
  code: number
  /** 错误消息 */
  message: string
  /** 详细错误信息 */
  details?: string
  /** 验证错误列表 */
  validationErrors?: ValidationError[]
  /** 时间戳 */
  timestamp: number
}

// ==================== 工具类型 ====================

/**
 * 可选字段类型
 */
export type Optional<T, K extends keyof T> = Omit<T, K> & Partial<Pick<T, K>>

/**
 * 必需字段类型
 */
export type Required<T, K extends keyof T> = T & Required<Pick<T, K>>

/**
 * 创建时的数据类型（排除ID和时间戳）
 */
export type CreateData<T> = Omit<T, 'id' | 'createdAt' | 'updatedAt' | 'isDeleted'>

/**
 * 更新时的数据类型（排除创建时间）
 */
export type UpdateData<T> = Omit<Partial<T>, 'id' | 'createdAt' | 'isDeleted'>