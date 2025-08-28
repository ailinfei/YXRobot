/**
 * 管理后台仪表板Mock数据
 * 提供管理后台所有模块的综合统计数据
 */

export interface DashboardStats {
  // 业务概览
  business: {
    totalRevenue: number
    monthlyRevenue: number
    revenueGrowth: number
    totalOrders: number
    monthlyOrders: number
    orderGrowth: number
    activeDevices: number
    deviceUtilization: number
    totalCustomers: number
    activeCustomers: number
    customerGrowth: number
    averageOrderValue: number
    conversionRate: number
  }
  
  // 设备状态
  devices: {
    total: number
    online: number
    offline: number
    maintenance: number
    error: number
    updating: number
    averageBatteryLevel: number
    averageSignalStrength: number
    alertCount: number
    healthScore: number
  }
  
  // 销售和租赁统计
  sales: {
    totalSalesRevenue: number
    monthlySalesRevenue: number
    salesGrowth: number
    totalSalesOrders: number
    monthlySalesOrders: number
    averageSalesOrderValue: number
    topSellingProduct: string
    salesConversionRate: number
  }
  
  rental: {
    totalRentalRevenue: number
    monthlyRentalRevenue: number
    rentalGrowth: number
    totalRentalOrders: number
    monthlyRentalOrders: number
    averageRentalPeriod: number
    deviceUtilizationRate: number
    rentalConversionRate: number
  }
  
  // 课程统计
  courses: {
    total: number
    active: number
    draft: number
    archived: number
    totalCharacters: number
    completedLessons: number
    averageRating: number
    popularCourse: string
  }
  
  // AI字体包统计
  fontPackages: {
    total: number
    published: number
    training: number
    completed: number
    failed: number
    totalSamples: number
    averageAccuracy: number
    totalDownloads: number
  }
  
  // 多语言统计
  languages: {
    supported: number
    enabled: number
    averageCompleteness: number
    totalTranslations: number
    robotInterfaceTexts: number
    websiteContents: number
    pendingTranslations: number
  }
  
  // 产品管理统计
  products: {
    total: number
    published: number
    draft: number
    archived: number
    totalReviews: number
    averageRating: number
    totalSales: number
    stockAlerts: number
  }
  
  // 公益项目统计
  charity: {
    totalProjects: number
    activeProjects: number
    totalBeneficiaries: number
    totalDonations: number
    totalVolunteers: number
    activeVolunteers: number
    totalInstitutions: number
    thisMonthActivities: number
  }
  
  // 平台链接统计
  platformLinks: {
    total: number
    active: number
    inactive: number
    error: number
    totalClicks: number
    totalConversions: number
    averageConversionRate: number
    topPerformingPlatform: string
  }
  
  // 客户管理统计
  customers: {
    total: number
    active: number
    inactive: number
    vip: number
    newThisMonth: number
    churnRate: number
    averageLifetimeValue: number
    satisfactionScore: number
  }
}

export interface RecentActivity {
  id: number
  type: 'order' | 'device' | 'course' | 'fontPackage' | 'translation'
  title: string
  description: string
  timestamp: string
  status: 'success' | 'warning' | 'error' | 'info'
  user?: string
}

export interface TrendData {
  date: string
  revenue: number
  orders: number
  devices: number
  customers: number
}

export interface TopPerformers {
  devices: Array<{
    id: string
    model: string
    utilization: number
    revenue: number
  }>
  
  courses: Array<{
    id: number
    name: string
    completions: number
    rating: number
  }>
  
  customers: Array<{
    id: number
    name: string
    totalSpent: number
    orderCount: number
  }>
}

// 生成随机数据的工具函数
const randomBetween = (min: number, max: number): number => {
  return Math.floor(Math.random() * (max - min + 1)) + min
}

const randomFloat = (min: number, max: number, decimals: number = 2): number => {
  return parseFloat((Math.random() * (max - min) + min).toFixed(decimals))
}

const generateRandomDate = (daysAgo: number): string => {
  const date = new Date()
  date.setDate(date.getDate() - randomBetween(0, daysAgo))
  return date.toISOString()
}

// Mock数据生成器
export class DashboardMockService {
  // 生成仪表板统计数据
  static generateDashboardStats(): DashboardStats {
    const totalRevenue = randomBetween(8000000, 15000000)
    const monthlyRevenue = randomBetween(600000, 1200000)
    const totalOrders = randomBetween(3000, 8000)
    const monthlyOrders = randomBetween(200, 600)
    const totalDevices = randomBetween(500, 1200)
    const onlineDevices = randomBetween(300, totalDevices - 100)
    const totalCustomers = randomBetween(1000, 3000)
    const activeCustomers = randomBetween(600, totalCustomers - 200)
    
    // 销售数据
    const totalSalesRevenue = Math.floor(totalRevenue * 0.7)
    const monthlySalesRevenue = Math.floor(monthlyRevenue * 0.7)
    const totalSalesOrders = Math.floor(totalOrders * 0.6)
    const monthlySalesOrders = Math.floor(monthlyOrders * 0.6)
    
    // 租赁数据
    const totalRentalRevenue = totalRevenue - totalSalesRevenue
    const monthlyRentalRevenue = monthlyRevenue - monthlySalesRevenue
    const totalRentalOrders = totalOrders - totalSalesOrders
    const monthlyRentalOrders = monthlyOrders - monthlySalesOrders
    
    // 课程数据
    const totalCourses = randomBetween(80, 150)
    const activeCourses = Math.floor(totalCourses * 0.7)
    const draftCourses = Math.floor(totalCourses * 0.2)
    const archivedCourses = totalCourses - activeCourses - draftCourses
    
    // 字体包数据
    const totalFontPackages = randomBetween(40, 80)
    const publishedFontPackages = Math.floor(totalFontPackages * 0.6)
    const trainingFontPackages = Math.floor(totalFontPackages * 0.15)
    const completedFontPackages = Math.floor(totalFontPackages * 0.2)
    const failedFontPackages = totalFontPackages - publishedFontPackages - trainingFontPackages - completedFontPackages
    
    // 产品数据
    const totalProducts = randomBetween(15, 30)
    const publishedProducts = Math.floor(totalProducts * 0.8)
    const draftProducts = Math.floor(totalProducts * 0.15)
    const archivedProducts = totalProducts - publishedProducts - draftProducts
    
    // 平台链接数据
    const totalPlatformLinks = randomBetween(40, 80)
    const activePlatformLinks = Math.floor(totalPlatformLinks * 0.8)
    const inactivePlatformLinks = Math.floor(totalPlatformLinks * 0.15)
    const errorPlatformLinks = totalPlatformLinks - activePlatformLinks - inactivePlatformLinks
    
    return {
      business: {
        totalRevenue,
        monthlyRevenue,
        revenueGrowth: randomFloat(-8, 35),
        totalOrders,
        monthlyOrders,
        orderGrowth: randomFloat(-5, 28),
        activeDevices: onlineDevices,
        deviceUtilization: randomFloat(68, 88),
        totalCustomers,
        activeCustomers,
        customerGrowth: randomFloat(5, 25),
        averageOrderValue: Math.round(totalRevenue / totalOrders),
        conversionRate: randomFloat(3.5, 8.2)
      },
      
      devices: {
        total: totalDevices,
        online: onlineDevices,
        offline: randomBetween(50, 150),
        maintenance: randomBetween(20, 60),
        error: randomBetween(10, 40),
        updating: randomBetween(5, 25),
        averageBatteryLevel: randomBetween(65, 85),
        averageSignalStrength: randomBetween(70, 90),
        alertCount: randomBetween(15, 50),
        healthScore: randomBetween(75, 95)
      },
      
      sales: {
        totalSalesRevenue,
        monthlySalesRevenue,
        salesGrowth: randomFloat(-5, 30),
        totalSalesOrders,
        monthlySalesOrders,
        averageSalesOrderValue: Math.round(totalSalesRevenue / totalSalesOrders),
        topSellingProduct: 'YX机器人专业版',
        salesConversionRate: randomFloat(4.2, 9.1)
      },
      
      rental: {
        totalRentalRevenue,
        monthlyRentalRevenue,
        rentalGrowth: randomFloat(-3, 25),
        totalRentalOrders,
        monthlyRentalOrders,
        averageRentalPeriod: randomBetween(25, 45),
        deviceUtilizationRate: randomFloat(72, 89),
        rentalConversionRate: randomFloat(2.8, 6.5)
      },
      
      courses: {
        total: totalCourses,
        active: activeCourses,
        draft: draftCourses,
        archived: archivedCourses,
        totalCharacters: randomBetween(3000, 8000),
        completedLessons: randomBetween(8000, 25000),
        averageRating: randomFloat(4.2, 4.8, 1),
        popularCourse: '基础汉字入门课程'
      },
      
      fontPackages: {
        total: totalFontPackages,
        published: publishedFontPackages,
        training: trainingFontPackages,
        completed: completedFontPackages,
        failed: failedFontPackages,
        totalSamples: randomBetween(2000, 8000),
        averageAccuracy: randomFloat(88, 96),
        totalDownloads: randomBetween(5000, 20000)
      },
      
      languages: {
        supported: 12,
        enabled: randomBetween(8, 11),
        averageCompleteness: randomFloat(75, 92),
        totalTranslations: randomBetween(3000, 10000),
        robotInterfaceTexts: randomBetween(150, 300),
        websiteContents: randomBetween(200, 400),
        pendingTranslations: randomBetween(50, 200)
      },
      
      products: {
        total: totalProducts,
        published: publishedProducts,
        draft: draftProducts,
        archived: archivedProducts,
        totalReviews: randomBetween(500, 2000),
        averageRating: randomFloat(4.3, 4.9, 1),
        totalSales: totalSalesOrders,
        stockAlerts: randomBetween(2, 8)
      },
      
      charity: {
        totalProjects: randomBetween(25, 40),
        activeProjects: randomBetween(8, 15),
        totalBeneficiaries: randomBetween(8000, 20000),
        totalDonations: randomBetween(2000000, 6000000),
        totalVolunteers: randomBetween(80, 150),
        activeVolunteers: randomBetween(40, 80),
        totalInstitutions: randomBetween(60, 120),
        thisMonthActivities: randomBetween(8, 20)
      },
      
      platformLinks: {
        total: totalPlatformLinks,
        active: activePlatformLinks,
        inactive: inactivePlatformLinks,
        error: errorPlatformLinks,
        totalClicks: randomBetween(50000, 200000),
        totalConversions: randomBetween(2000, 8000),
        averageConversionRate: randomFloat(3.5, 7.2),
        topPerformingPlatform: '淘宝'
      },
      
      customers: {
        total: totalCustomers,
        active: activeCustomers,
        inactive: totalCustomers - activeCustomers,
        vip: Math.floor(totalCustomers * 0.15),
        newThisMonth: randomBetween(50, 200),
        churnRate: randomFloat(2.5, 8.5),
        averageLifetimeValue: randomBetween(8000, 25000),
        satisfactionScore: randomFloat(4.1, 4.7, 1)
      }
    }
  }
  
  // 生成最近活动数据
  static generateRecentActivities(count: number = 20): RecentActivity[] {
    const activities: RecentActivity[] = []
    const types: ('order' | 'device' | 'course' | 'fontPackage' | 'translation')[] = 
      ['order', 'device', 'course', 'fontPackage', 'translation']
    const statuses: ('success' | 'warning' | 'error' | 'info')[] = 
      ['success', 'warning', 'error', 'info']
    const users = ['张管理员', '李操作员', '王技术员', '陈编辑', '刘审核员']
    
    const activityTemplates = {
      order: [
        '新订单创建成功',
        '订单状态更新',
        '订单支付完成',
        '订单取消处理',
        '订单退款申请'
      ],
      device: [
        '设备上线运行',
        '设备进入维护模式',
        '设备状态异常',
        '设备固件更新',
        '设备离线告警'
      ],
      course: [
        '新课程发布',
        '课程内容更新',
        '课程汉字添加',
        '课程状态变更',
        '课程删除操作'
      ],
      fontPackage: [
        'AI字体包训练开始',
        '字体包生成完成',
        '字体包发布上线',
        '字体样本上传',
        '字体包审核通过'
      ],
      translation: [
        '多语言翻译更新',
        '新语言支持添加',
        '翻译内容审核',
        '批量翻译完成',
        '翻译文件导入'
      ]
    }
    
    for (let i = 1; i <= count; i++) {
      const type = types[randomBetween(0, types.length - 1)]
      const templates = activityTemplates[type]
      const title = templates[randomBetween(0, templates.length - 1)]
      
      activities.push({
        id: i,
        type,
        title,
        description: `${title} - 详细描述信息 ${i}`,
        timestamp: generateRandomDate(7),
        status: statuses[randomBetween(0, statuses.length - 1)],
        user: users[randomBetween(0, users.length - 1)]
      })
    }
    
    return activities.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())
  }
  
  // 生成趋势数据
  static generateTrendData(days: number = 30): TrendData[] {
    const trends: TrendData[] = []
    const today = new Date()
    
    for (let i = days - 1; i >= 0; i--) {
      const date = new Date(today)
      date.setDate(date.getDate() - i)
      
      trends.push({
        date: date.toISOString().slice(0, 10),
        revenue: randomBetween(5000, 15000),
        orders: randomBetween(10, 30),
        devices: randomBetween(50, 100),
        customers: randomBetween(20, 50)
      })
    }
    
    return trends
  }
  
  // 生成顶级表现者数据
  static generateTopPerformers(): TopPerformers {
    const deviceModels = ['YX-Robot-Pro', 'YX-Robot-Standard', 'YX-Robot-Lite']
    const courseNames = [
      '基础汉字入门', '常用汉字练习', '小学一年级汉字', '商务汉字课程',
      '日常生活汉字', '汉字笔画学习', '部首认识课程', '传统汉字艺术'
    ]
    const customerNames = [
      '张伟', '王芳', '李娜', '刘强', '陈静', '杨洋', '赵敏', '黄磊',
      '周杰', '吴亮', '徐丽', '孙勇', '马超', '朱琳', '胡斌', '郭敏'
    ]
    
    return {
      devices: Array.from({ length: 5 }, (_, i) => ({
        id: `YX-${String(i + 1).padStart(4, '0')}`,
        model: deviceModels[randomBetween(0, deviceModels.length - 1)],
        utilization: randomFloat(80, 95),
        revenue: randomBetween(50000, 150000)
      })).sort((a, b) => b.utilization - a.utilization),
      
      courses: Array.from({ length: 5 }, (_, i) => ({
        id: i + 1,
        name: courseNames[randomBetween(0, courseNames.length - 1)],
        completions: randomBetween(100, 500),
        rating: randomFloat(4.0, 5.0, 1)
      })).sort((a, b) => b.completions - a.completions),
      
      customers: Array.from({ length: 5 }, (_, i) => ({
        id: i + 1,
        name: customerNames[randomBetween(0, customerNames.length - 1)],
        totalSpent: randomBetween(10000, 50000),
        orderCount: randomBetween(5, 25)
      })).sort((a, b) => b.totalSpent - a.totalSpent)
    }
  }
}

// 模拟API延迟
const mockDelay = (ms: number = 500): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// Mock API响应格式
const createMockResponse = <T>(data: T, code: number = 200, message: string = 'success') => {
  return {
    code,
    message,
    data,
    timestamp: Date.now()
  }
}

// Mock API函数
export const mockDashboardAPI = {
  // 获取仪表板统计数据
  async getDashboardStats() {
    await mockDelay()
    return createMockResponse(DashboardMockService.generateDashboardStats())
  },
  
  // 获取最近活动
  async getRecentActivities(params: any) {
    await mockDelay()
    let data = DashboardMockService.generateRecentActivities(50)
    
    // 应用筛选
    if (params?.type) {
      data = data.filter(item => item.type === params.type)
    }
    
    if (params?.status) {
      data = data.filter(item => item.status === params.status)
    }
    
    // 分页
    const page = params?.page || 1
    const pageSize = params?.pageSize || 10
    const total = data.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = data.slice(start, end)
    
    return createMockResponse({
      list,
      total,
      page,
      pageSize,
      totalPages: Math.ceil(total / pageSize)
    })
  },
  
  // 获取趋势数据
  async getTrendData(params: any) {
    await mockDelay()
    const days = params?.days || 30
    const data = DashboardMockService.generateTrendData(days)
    return createMockResponse(data)
  },
  
  // 获取顶级表现者数据
  async getTopPerformers() {
    await mockDelay()
    return createMockResponse(DashboardMockService.generateTopPerformers())
  }
}