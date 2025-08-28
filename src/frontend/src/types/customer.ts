// 客户相关类型定义

export interface Address {
  province: string
  city: string
  detail: string
}

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
}

export interface DeviceUsageStats {
  totalUsageHours: number
  dailyAverageUsage: number
  lastUsedAt: string
  coursesCompleted: number
  charactersWritten: number
}

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
}

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

export interface ServiceAttachment {
  id: string
  name: string
  url: string
  size: number
  type: string
}

export interface Customer {
  id: string
  name: string
  level: 'regular' | 'vip' | 'premium'
  phone: string
  email: string
  company?: string
  status: 'active' | 'inactive' | 'suspended'
  address?: Address
  tags?: string[]
  notes?: string
  avatar?: string
  deviceCount?: {
    total: number
    purchased: number
    rental: number
  }
  totalSpent: number
  customerValue: number
  devices?: CustomerDevice[]
  orders?: CustomerOrder[]
  serviceRecords?: CustomerServiceRecord[]
  registeredAt: string
  lastActiveAt?: string
}

// API 请求/响应类型
export interface CreateCustomerData {
  name: string
  level: string
  phone: string
  email: string
  company?: string
  status: string
  address?: Address
  tags?: string[]
  notes?: string
}

export interface UpdateCustomerData extends Partial<CreateCustomerData> {
  id?: string
}

export interface CustomerQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  level?: string
  status?: string
  deviceType?: string
  region?: string
}

export interface CustomerListResponse {
  list: Customer[]
  total: number
  page: number
  pageSize: number
}

// 设备相关类型
export interface AddDeviceData {
  customerId: string
  serialNumber: string
  model: string
  type: 'purchased' | 'rental'
  status: string
  activatedAt?: string
  notes?: string
}

// 服务记录相关类型
export interface AddServiceRecordData {
  customerId: string
  type: string
  subject: string
  description: string
  deviceId?: string
  serviceStaff: string
  cost?: number
  status: string
  attachments?: Array<{
    name: string
    url: string
    size: number
  }>
}