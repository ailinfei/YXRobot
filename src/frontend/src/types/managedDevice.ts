/**
 * 设备管理模块专用 TypeScript 接口定义
 * 与后端ManagedDevice实体类完全匹配
 * 任务20：创建前端TypeScript接口定义
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

// 设备型号枚举 - 匹配后端DeviceModel枚举
export enum DeviceModel {
  YX_EDU_2024 = 'YX-EDU-2024',
  YX_HOME_2024 = 'YX-HOME-2024', 
  YX_PRO_2024 = 'YX-PRO-2024'
}

// 设备状态枚举 - 匹配后端DeviceStatus枚举
export enum DeviceStatus {
  ONLINE = 'online',
  OFFLINE = 'offline',
  ERROR = 'error',
  MAINTENANCE = 'maintenance'
}

// 维护类型枚举 - 匹配后端MaintenanceType枚举
export enum MaintenanceType {
  REPAIR = 'repair',
  UPGRADE = 'upgrade',
  INSPECTION = 'inspection',
  REPLACEMENT = 'replacement'
}

// 维护状态枚举 - 匹配后端MaintenanceStatus枚举
export enum MaintenanceStatus {
  PENDING = 'pending',
  IN_PROGRESS = 'in_progress',
  COMPLETED = 'completed',
  CANCELLED = 'cancelled'
}

// 日志级别枚举 - 匹配后端LogLevel枚举
export enum LogLevel {
  INFO = 'info',
  WARNING = 'warning',
  ERROR = 'error',
  DEBUG = 'debug'
}

// 日志分类枚举 - 匹配后端LogCategory枚举
export enum LogCategory {
  SYSTEM = 'system',
  USER = 'user',
  NETWORK = 'network',
  HARDWARE = 'hardware',
  SOFTWARE = 'software'
}

// 客户接口 - 匹配后端Customer实体
export interface Customer {
  id: string
  name: string
  phone: string
  email?: string
  address?: string
  company?: string
  contactPerson?: string
  notes?: string
  createdAt?: string
  updatedAt?: string
  isDeleted?: boolean
}

// 设备技术参数接口 - 匹配后端ManagedDeviceSpecification实体
export interface ManagedDeviceSpecification {
  id?: string
  deviceId?: string
  cpu: string
  memory: string
  storage: string
  display: string
  battery: string
  connectivity: string[]
  createdAt?: string
  updatedAt?: string
}

// 设备使用统计接口 - 匹配后端ManagedDeviceUsageStats实体
export interface ManagedDeviceUsageStats {
  id?: string
  deviceId?: string
  totalRuntime: number        // 总运行时间（分钟）
  usageCount: number          // 使用次数
  lastUsedAt?: string         // 最后使用时间
  averageSessionTime: number  // 平均使用时长（分钟）
  createdAt?: string
  updatedAt?: string
}

// 设备配置接口 - 匹配后端ManagedDeviceConfiguration实体
export interface ManagedDeviceConfiguration {
  id?: string
  deviceId?: string
  language: string
  timezone: string
  autoUpdate: boolean
  debugMode: boolean
  customSettings: Record<string, any>
  createdAt?: string
  updatedAt?: string
}

// 设备位置信息接口 - 匹配后端ManagedDeviceLocation实体
export interface ManagedDeviceLocation {
  id?: string
  deviceId?: string
  latitude: number
  longitude: number
  address: string
  lastUpdated: string
  createdAt?: string
  updatedAt?: string
}

// 设备维护记录接口 - 匹配后端ManagedDeviceMaintenanceRecord实体
export interface ManagedDeviceMaintenanceRecord {
  id: string
  deviceId: string
  type: MaintenanceType
  description: string
  technician: string
  startTime: string
  endTime?: string
  status: MaintenanceStatus
  cost?: number
  parts?: string[]
  notes?: string
  createdAt?: string
  updatedAt?: string
}

// 设备日志接口 - 匹配后端ManagedDeviceLog实体
export interface ManagedDeviceLog {
  id: string
  deviceId: string
  timestamp: string
  level: LogLevel
  category: LogCategory
  message: string
  details?: Record<string, any>
  createdAt?: string
}

// 主设备接口 - 匹配后端ManagedDevice实体
export interface ManagedDevice {
  id: string
  serialNumber: string
  model: DeviceModel
  status: DeviceStatus
  firmwareVersion: string
  customerId: string
  customerName: string
  customerPhone: string
  lastOnlineAt?: string
  activatedAt?: string
  createdAt: string
  updatedAt?: string
  createdBy?: string
  notes?: string
  isDeleted?: boolean
  
  // 关联对象
  specifications?: ManagedDeviceSpecification
  usageStats?: ManagedDeviceUsageStats
  configuration?: ManagedDeviceConfiguration
  location?: ManagedDeviceLocation
  maintenanceRecords?: ManagedDeviceMaintenanceRecord[]
}

// 设备统计接口 - 匹配后端DeviceStatsDTO
export interface ManagedDeviceStats {
  total: number
  online: number
  offline: number
  error: number
  maintenance: number
  
  // 按型号统计
  modelStats?: {
    [DeviceModel.YX_EDU_2024]: number
    [DeviceModel.YX_HOME_2024]: number
    [DeviceModel.YX_PRO_2024]: number
  }
  
  // 时间范围统计
  dateRange?: {
    startDate: string
    endDate: string
  }
  
  // 统计时间
  statisticsDate?: string
  updatedAt?: string
}

// API 请求/响应类型 - 匹配后端DTO

// 创建设备数据接口 - 匹配后端CreateManagedDeviceDTO
export interface CreateManagedDeviceData {
  serialNumber: string
  model: DeviceModel
  customerId: string
  firmwareVersion?: string
  specifications?: Partial<ManagedDeviceSpecification>
  configuration?: Partial<ManagedDeviceConfiguration>
  location?: Partial<ManagedDeviceLocation>
  notes?: string
}

// 更新设备数据接口 - 匹配后端UpdateManagedDeviceDTO
export interface UpdateManagedDeviceData extends Partial<CreateManagedDeviceData> {
  status?: DeviceStatus
  customerName?: string
  customerPhone?: string
}

// 设备查询参数接口 - 匹配后端ManagedDeviceQueryDTO
export interface ManagedDeviceQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  status?: DeviceStatus
  model?: DeviceModel
  customerId?: string
  dateRange?: [string, string]
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

// 设备列表响应接口 - 匹配后端API响应格式
export interface ManagedDeviceListResponse {
  list: ManagedDevice[]
  total: number
  page: number
  pageSize: number
  stats?: ManagedDeviceStats
}

// 设备日志查询参数接口
export interface DeviceLogQueryParams {
  page?: number
  pageSize?: number
  level?: LogLevel
  category?: LogCategory
  dateRange?: [string, string]
}

// 设备日志列表响应接口
export interface DeviceLogListResponse {
  list: ManagedDeviceLog[]
  total: number
  page: number
  pageSize: number
}

// 维护记录查询参数接口
export interface MaintenanceRecordQueryParams {
  page?: number
  pageSize?: number
  type?: MaintenanceType
  status?: MaintenanceStatus
  dateRange?: [string, string]
}

// 维护记录列表响应接口
export interface MaintenanceRecordListResponse {
  list: ManagedDeviceMaintenanceRecord[]
  total: number
  page: number
  pageSize: number
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

// API统一响应格式接口
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp?: string
}

// 客户选项接口
export interface CustomerOption {
  id: string
  name: string
  phone?: string
}

// 设备操作结果接口
export interface DeviceOperationResult {
  id: string
  status?: string
  message?: string
  updatedAt?: string
  activatedAt?: string
  version?: string
}

// 设备导出参数接口
export interface DeviceExportParams extends ManagedDeviceQueryParams {
  format?: 'excel' | 'csv'
  fields?: string[]
}

// 设备导出响应接口
export interface DeviceExportResponse {
  downloadUrl: string
  filename: string
  fileSize?: number
  expiresAt?: string
}

// 固件推送参数接口
export interface FirmwarePushParams {
  deviceId?: string
  deviceIds?: string[]
  firmwareVersion: string
  forceUpdate?: boolean
  scheduleTime?: string
}

// 固件推送响应接口
export interface FirmwarePushResponse {
  taskId: string
  status: 'pending' | 'in_progress' | 'completed' | 'failed'
  message: string
  affectedDevices: number
  startTime?: string
  completedTime?: string
}

// 设备重启参数接口
export interface DeviceRebootParams {
  deviceId?: string
  deviceIds?: string[]
  scheduleTime?: string
  reason?: string
}

// 设备激活参数接口
export interface DeviceActivateParams {
  deviceId: string
  activationCode?: string
  customerId?: string
}

// 设备状态更新参数接口
export interface DeviceStatusUpdateParams {
  deviceId: string
  status: DeviceStatus
  reason?: string
}

// 设备搜索建议接口
export interface DeviceSearchSuggestion {
  type: 'serialNumber' | 'customerName' | 'model'
  value: string
  label: string
  count?: number
}

// 设备统计详情接口
export interface DeviceStatsDetail {
  stats: ManagedDeviceStats
  trends: {
    totalChange: number
    onlineChange: number
    offlineChange: number
    errorChange: number
  }
  chartData: {
    labels: string[]
    datasets: {
      label: string
      data: number[]
      backgroundColor?: string
      borderColor?: string
    }[]
  }
}

// 文本映射常量

// 设备状态文本映射
export const DEVICE_STATUS_TEXT = {
  [DeviceStatus.ONLINE]: '在线',
  [DeviceStatus.OFFLINE]: '离线',
  [DeviceStatus.ERROR]: '故障',
  [DeviceStatus.MAINTENANCE]: '维护中'
} as const

// 设备型号文本映射
export const DEVICE_MODEL_TEXT = {
  [DeviceModel.YX_EDU_2024]: '教育版练字机器人',
  [DeviceModel.YX_HOME_2024]: '家庭版练字机器人',
  [DeviceModel.YX_PRO_2024]: '专业版练字机器人'
} as const

// 维护类型文本映射
export const MAINTENANCE_TYPE_TEXT = {
  [MaintenanceType.REPAIR]: '维修',
  [MaintenanceType.UPGRADE]: '升级',
  [MaintenanceType.INSPECTION]: '检查',
  [MaintenanceType.REPLACEMENT]: '更换'
} as const

// 维护状态文本映射
export const MAINTENANCE_STATUS_TEXT = {
  [MaintenanceStatus.PENDING]: '待处理',
  [MaintenanceStatus.IN_PROGRESS]: '进行中',
  [MaintenanceStatus.COMPLETED]: '已完成',
  [MaintenanceStatus.CANCELLED]: '已取消'
} as const

// 日志级别文本映射
export const LOG_LEVEL_TEXT = {
  [LogLevel.INFO]: '信息',
  [LogLevel.WARNING]: '警告',
  [LogLevel.ERROR]: '错误',
  [LogLevel.DEBUG]: '调试'
} as const

// 日志分类文本映射
export const LOG_CATEGORY_TEXT = {
  [LogCategory.SYSTEM]: '系统',
  [LogCategory.USER]: '用户',
  [LogCategory.NETWORK]: '网络',
  [LogCategory.HARDWARE]: '硬件',
  [LogCategory.SOFTWARE]: '软件'
} as const

// 颜色映射常量

// 设备状态颜色映射
export const DEVICE_STATUS_COLOR = {
  [DeviceStatus.ONLINE]: 'success',
  [DeviceStatus.OFFLINE]: 'info',
  [DeviceStatus.ERROR]: 'danger',
  [DeviceStatus.MAINTENANCE]: 'warning'
} as const

// 维护状态颜色映射
export const MAINTENANCE_STATUS_COLOR = {
  [MaintenanceStatus.PENDING]: 'warning',
  [MaintenanceStatus.IN_PROGRESS]: 'primary',
  [MaintenanceStatus.COMPLETED]: 'success',
  [MaintenanceStatus.CANCELLED]: 'info'
} as const

// 日志级别颜色映射
export const LOG_LEVEL_COLOR = {
  [LogLevel.INFO]: 'primary',
  [LogLevel.WARNING]: 'warning',
  [LogLevel.ERROR]: 'danger',
  [LogLevel.DEBUG]: 'info'
} as const

// 设备型号颜色映射
export const DEVICE_MODEL_COLOR = {
  [DeviceModel.YX_EDU_2024]: 'primary',
  [DeviceModel.YX_HOME_2024]: 'success',
  [DeviceModel.YX_PRO_2024]: 'warning'
} as const

// 类型守卫函数

export function isValidDeviceStatus(status: string): status is DeviceStatus {
  return Object.values(DeviceStatus).includes(status as DeviceStatus)
}

export function isValidDeviceModel(model: string): model is DeviceModel {
  return Object.values(DeviceModel).includes(model as DeviceModel)
}

export function isValidMaintenanceType(type: string): type is MaintenanceType {
  return Object.values(MaintenanceType).includes(type as MaintenanceType)
}

export function isValidLogLevel(level: string): level is LogLevel {
  return Object.values(LogLevel).includes(level as LogLevel)
}

export function isValidLogCategory(category: string): category is LogCategory {
  return Object.values(LogCategory).includes(category as LogCategory)
}

// 工具函数

/**
 * 获取设备状态显示文本
 */
export function getDeviceStatusText(status: DeviceStatus): string {
  return DEVICE_STATUS_TEXT[status] || status
}

/**
 * 获取设备型号显示文本
 */
export function getDeviceModelText(model: DeviceModel): string {
  return DEVICE_MODEL_TEXT[model] || model
}

/**
 * 获取维护类型显示文本
 */
export function getMaintenanceTypeText(type: MaintenanceType): string {
  return MAINTENANCE_TYPE_TEXT[type] || type
}

/**
 * 获取维护状态显示文本
 */
export function getMaintenanceStatusText(status: MaintenanceStatus): string {
  return MAINTENANCE_STATUS_TEXT[status] || status
}

/**
 * 获取日志级别显示文本
 */
export function getLogLevelText(level: LogLevel): string {
  return LOG_LEVEL_TEXT[level] || level
}

/**
 * 获取日志分类显示文本
 */
export function getLogCategoryText(category: LogCategory): string {
  return LOG_CATEGORY_TEXT[category] || category
}

/**
 * 格式化运行时间（分钟转换为可读格式）
 */
export function formatRuntime(minutes: number): string {
  if (minutes < 60) {
    return `${minutes}分钟`
  }
  const hours = Math.floor(minutes / 60)
  const remainingMinutes = minutes % 60
  return remainingMinutes > 0 ? `${hours}小时${remainingMinutes}分钟` : `${hours}小时`
}

/**
 * 格式化设备序列号显示
 */
export function formatSerialNumber(serialNumber: string): string {
  if (serialNumber.length <= 8) {
    return serialNumber
  }
  return `${serialNumber.substring(0, 4)}...${serialNumber.substring(serialNumber.length - 4)}`
}

/**
 * 检查设备是否在线
 */
export function isDeviceOnline(device: ManagedDevice): boolean {
  return device.status === DeviceStatus.ONLINE
}

/**
 * 检查设备是否需要维护
 */
export function isDeviceNeedsMaintenance(device: ManagedDevice): boolean {
  return device.status === DeviceStatus.ERROR || device.status === DeviceStatus.MAINTENANCE
}

/**
 * 获取设备最后活跃时间
 */
export function getDeviceLastActiveTime(device: ManagedDevice): string {
  return device.lastOnlineAt || device.updatedAt || device.createdAt
}