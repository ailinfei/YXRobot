/**
 * API类型定义
 * 定义所有API接口的请求和响应类型
 */

// 通用响应格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 分页响应格式
export interface PageResponse<T = any> {
  list: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}

// 数据看板相关类型
export interface DashboardStats {
  totalRevenue: number
  totalOrders: number
  newProducts: number
  newCustomers: number
  revenueGrowth: number
  ordersGrowth: number
  customersGrowth: number
}

export interface ChartData {
  name: string
  value: number
  date?: string
}

// 产品相关类型
export interface Product {
  id: number
  name: string
  model: string
  description: string
  price: number
  coverImage: string
  status: 'draft' | 'published' | 'archived'
  createdAt: string
  updatedAt: string
}

// 订单相关类型
export interface Order {
  id: number
  orderNumber: string
  customerId: number
  customerName: string
  productModel: string
  quantity: number
  totalAmount: number
  status: 'pending' | 'paid' | 'shipped' | 'delivered' | 'cancelled'
  orderDate: string
  shippedDate?: string
}

// 客户相关类型
export interface Customer {
  id: number
  name: string
  email: string
  phone: string
  address: string
  country: string
  region: string
  city: string
  customerLevel: 'normal' | 'vip'
  languagePreference: string
  createdAt: string
  updatedAt: string
}

// 设备相关类型
export interface Device {
  id: number
  deviceId: string
  model: string
  firmwareVersion: string
  status: 'pending' | 'online' | 'offline' | 'fault' | 'maintenance'
  lastOnlineAt?: string
  activatedAt?: string
  createdAt: string
  updatedAt: string
}

// 公益项目相关类型
export interface CharityProject {
  id: number
  totalDonatedCourses: number
  totalBeneficiaries: number
  totalInstitutions: number
  totalRobotsSold: number
  donationRatio: number
  schoolsCount: number
  communityCentersCount: number
  careCentersCount: number
  welfareHomesCount: number
  elderlyUniversitiesCount: number
  updatedAt: string
}

export interface CharityLocation {
  id: number
  name: string
  locationType: 'school' | 'community_center' | 'care_center' | 'welfare_home' | 'elderly_university'
  address: string
  contactPerson: string
  contactPhone: string
  beneficiaryCount: number
  coursesCount: number
  imageUrls: string[]
  latitude: number
  longitude: number
  isActive: boolean
  createdAt: string
  updatedAt: string
}

// 课程相关类型
export interface Course {
  id: number
  name: string
  difficultyLevel: number
  gradeLevel: string
  description: string
  coverUrl: string
  characterCount: number
  status: 'draft' | 'published' | 'archived'
  createdAt: string
  updatedAt: string
}

// 字体包相关类型
export interface FontPackage {
  id: number
  name: string
  fontType: 'kaishu' | 'xingshu' | 'lishu' | 'zhuanshu' | 'caoshu'
  description: string
  status: 'sample_uploading' | 'ai_generating' | 'generated' | 'publishing' | 'published' | 'generation_failed'
  characterCount: number
  createdAt: string
  updatedAt: string
}

// 平台链接相关类型
export interface PlatformLink {
  id: number
  platformName: string
  platformType: 'ecommerce' | 'rental'
  linkUrl: string
  region: string
  country: string
  languageCode: string
  languageName: string
  isEnabled: boolean
  linkStatus: 'active' | 'inactive' | 'checking' | 'error'
  lastCheckedAt?: string
  clickCount: number
  conversionCount: number
  createdAt: string
  updatedAt: string
}

export interface PlatformLinkForm {
  platformName: string
  platformType: 'ecommerce' | 'rental'
  linkUrl: string
  region: string
  country: string
  languageCode: string
  isEnabled: boolean
}

export interface LinkValidationResult {
  id: number
  isValid: boolean
  statusCode?: number
  responseTime?: number
  errorMessage?: string
  checkedAt: string
}

export interface PlatformLinkStats {
  totalLinks: number
  activeLinks: number
  inactiveLinks: number
  totalClicks: number
  totalConversions: number
  conversionRate: number
  topPerformingLinks: Array<{
    id: number
    platformName: string
    region: string
    clickCount: number
    conversionCount: number
    conversionRate: number
  }>
  regionStats: Array<{
    region: string
    linkCount: number
    clickCount: number
    conversionCount: number
  }>
  languageStats: Array<{
    languageCode: string
    languageName: string
    linkCount: number
    clickCount: number
    conversionCount: number
  }>
}

export interface RegionConfig {
  region: string
  country: string
  languages: Array<{
    code: string
    name: string
  }>
}

export interface BatchUpdateRequest {
  linkIds: number[]
  updates: Partial<PlatformLinkForm>
}

// 租赁相关类型
export interface RentalOrder {
  id: number
  orderNumber: string
  customerId: number
  customerName: string
  customerPhone: string
  deviceModel: string
  deviceId: string
  rentalPeriod: number
  dailyRate: number
  totalAmount: number
  platform: string
  status: 'pending' | 'active' | 'expired' | 'returned' | 'cancelled'
  startDate: string
  endDate: string
  createdAt: string
  updatedAt: string
}

export interface RentalStats {
  totalRentalRevenue: number
  totalRentalDevices: number
  activeRentalDevices: number
  deviceUtilizationRate: number
  averageRentalPeriod: number
  totalRentalOrders: number
  revenueGrowthRate: number
  deviceGrowthRate: number
}

export interface RentalTrendData {
  date: string
  revenue: number
  orderCount: number
  deviceCount: number
  utilizationRate: number
}

export interface DeviceUtilizationData {
  deviceId: string
  deviceModel: string
  utilizationRate: number
  totalRentalDays: number
  totalAvailableDays: number
  currentStatus: 'active' | 'idle' | 'maintenance'
  lastRentalDate?: string
}

// 新闻管理相关类型
export interface NewsItem {
  id: number
  title: string
  excerpt?: string
  content: string
  categoryId: number
  categoryName?: string
  author: string
  status: 'DRAFT' | 'PUBLISHED' | 'OFFLINE'
  statusText?: string
  coverImage?: string
  publishTime?: string
  views: number
  comments: number
  likes: number
  isFeatured: boolean
  sortOrder: number
  createdAt: string
  updatedAt: string
  tags?: NewsTagItem[]
  tagIds?: number[]
}

export interface NewsFormData {
  id?: number
  title: string
  excerpt?: string
  content: string
  categoryId: number
  author: string
  status: 'DRAFT' | 'PUBLISHED' | 'OFFLINE'
  coverImage?: string
  publishTime?: string
  isFeatured: boolean
  sortOrder: number
  tagIds?: number[]
}

export interface NewsCategoryItem {
  id: number
  name: string
  description?: string
  sortOrder: number
  isEnabled: boolean
  newsCount?: number
  createdAt: string
  updatedAt: string
}

export interface NewsTagItem {
  id: number
  name: string
  color?: string
  usageCount?: number
  createdAt: string
  updatedAt: string
}

export interface NewsStatsData {
  totalNews: number
  publishedNews: number
  draftNews: number
  offlineNews: number
  totalViews: number
  totalComments: number
  totalLikes: number
  todayNews: number
  todayViews: number
  weekNews: number
  monthNews: number
  featuredNews: number
  publishRate?: number
  averageViews?: number
}