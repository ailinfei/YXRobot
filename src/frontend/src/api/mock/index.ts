/**
 * Mock数据统一入口
 * 导出所有模块的Mock API和数据生成器
 */

// 导出所有Mock API
export { mockRentalAPI, RentalMockService } from './rental'
export { mockCustomerAPI, CustomerMockService } from './customer'
export { mockOrderAPI, MockOrderAPI } from './order'
export { mockCourseAPI, CourseMockService } from './course'
// export { mockFontPackageAPI, FontPackageMockService } from './fontPackage'
export { mockLanguageAPI, LanguageMockService } from './language'
export { mockDashboardAPI, DashboardMockService } from './dashboard'
export { mockCharityAPI, CharityMockService } from './charity'
export { mockSalesAPI, SalesMockService } from './sales'
export { mockProductAPI, ProductMockService } from './product'
export { mockPlatformLinkAPI, PlatformLinkMockService } from './platformLink'
export { mockDeviceAPI, DeviceMockService } from './device'
export { mockAdminDashboardAPI, AdminDashboardMockService } from './adminDashboard'
export { mockWebsiteAPI, WebsiteMockService } from './website'
export { TestDataGenerator, generateTestData, initializeTestData, TEST_DATA_CONFIG } from './testData'
export { mockDataStatisticsAPI, DataStatisticsGenerator } from './dataStatistics'
export { mockMapDataAPI, MapDataMockService } from './mapData'

// 导出类型定义
export type {
  RentalStats,
  RentalTrendData,
  DeviceUtilizationData,
  RentalOrder,
  RentalRevenueAnalysis
} from '../rental'

export type {
  Customer,
  CustomerDevice,
  CustomerOrder
} from './customer'

export type {
  Order,
  OrderItem,
  OrderStats,
  OrderQuery,
  OrderListResponse
} from '../../types/order'

export type {
  Course,
  Character,
  CharacterTranslation
} from './course'

export type {
  FontPackage,
  FontSample,
  GenerationTask,
  FontPreview
} from './fontPackage'

export type {
  Language,
  RobotInterfaceText,
  WebsiteContent,
  TranslationProgress
} from './language'

export type {
  DashboardStats,
  RecentActivity,
  TrendData,
  TopPerformers
} from './dashboard'

export type {
  CharityProject,
  CharityInstitution,
  CharityActivity,
  CharityDonation,
  CharityVolunteer,
  CharityStats
} from './charity'

export type {
  SalesStats,
  SalesOrder,
  RegionSalesData,
  ChannelSalesData,
  SalesTrendData,
  ProductSalesRanking
} from './sales'

export type {
  Product,
  ProductSpecification,
  ProductReview,
  ProductCategory
} from './product'

export type {
  PlatformLink,
  PlatformLinkStats,
  RegionConfig,
  LinkAnalytics
} from './platformLink'

export type {
  Device,
  DeviceAlert,
  DeviceStats,
  DeviceUsageData,
  FirmwareVersion
} from './device'

export type {
  AdminDashboardData
} from './adminDashboard'

export type {
  WebsiteProduct,
  WebsiteTestimonial,
  CharityLocation,
  StudentWork,
  VolunteerActivity,
  WebsitePlatformLink,
  WebsiteStats,
  MultiLanguageContent
} from './website'

export type {
  AllStatistics,
  SystemOverview,
  BusinessStatistics,
  ContentStatistics,
  WebsiteStatistics,
  PerformanceStatistics,
  TrendStatistics,
  SalesStatistics,
  RentalStatistics,
  CustomerStatistics,
  RevenueStatistics
} from './dataStatistics'

export type {
  MapDataPoint,
  MapDataParams,
  MapConfig
} from '../mapData'

// 统一的Mock配置
export const mockConfig = {
  // 是否启用Mock数据
  enabled: true,
  
  // API延迟配置（毫秒）
  delay: {
    default: 500,
    short: 200,
    long: 1000,
    upload: 2000
  },
  
  // 分页配置
  pagination: {
    defaultPageSize: 10,
    maxPageSize: 100
  },
  
  // 数据生成配置
  generation: {
    customers: 200,
    courses: 100,
    fontPackages: 50,
    robotTexts: 50,
    websiteContents: 100,
    products: 30,
    platformLinks: 60,
    devices: 150,
    charityProjects: 50,
    salesOrders: 500,
    rentalOrders: 200,
    // 官网数据配置
    websiteProducts: 5,
    testimonials: 30,
    charityLocations: 60,
    studentWorks: 120,
    volunteerActivities: 40,
    websitePlatformLinks: 10
  }
}

// 通用Mock工具函数
export const mockUtils = {
  // 生成随机数
  randomBetween: (min: number, max: number): number => {
    return Math.floor(Math.random() * (max - min + 1)) + min
  },
  
  // 生成随机浮点数
  randomFloat: (min: number, max: number, decimals: number = 2): number => {
    return parseFloat((Math.random() * (max - min) + min).toFixed(decimals))
  },
  
  // 生成随机日期
  generateRandomDate: (daysAgo: number): string => {
    const date = new Date()
    date.setDate(date.getDate() - mockUtils.randomBetween(0, daysAgo))
    return date.toISOString()
  },
  
  // 模拟API延迟
  mockDelay: (ms: number = mockConfig.delay.default): Promise<void> => {
    return new Promise(resolve => setTimeout(resolve, ms))
  },
  
  // 创建Mock响应格式
  createMockResponse: <T>(data: T, code: number = 200, message: string = 'success') => {
    return {
      code,
      message,
      data,
      timestamp: Date.now()
    }
  },
  
  // 应用分页
  applyPagination: <T>(data: T[], page: number = 1, pageSize: number = mockConfig.pagination.defaultPageSize) => {
    const total = data.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = data.slice(start, end)
    
    return {
      list,
      total,
      page,
      pageSize,
      totalPages: Math.ceil(total / pageSize)
    }
  },
  
  // 应用关键词筛选
  applyKeywordFilter: <T>(data: T[], keyword: string, fields: (keyof T)[]): T[] => {
    if (!keyword) return data
    
    return data.filter(item => 
      fields.some(field => {
        const value = item[field]
        if (typeof value === 'string') {
          return value.includes(keyword)
        }
        if (Array.isArray(value)) {
          return value.some(v => typeof v === 'string' && v.includes(keyword))
        }
        return false
      })
    )
  }
}

// 初始化Mock数据
export const initializeMockData = () => {
  console.log('🚀 YXRobot完整Mock数据系统已初始化')
  console.log('📊 可用的Mock API模块：')
  console.log('  【管理后台】')
  console.log('  - 数据看板 (mockDashboardAPI)')
  console.log('  - 销售管理 (mockSalesAPI)')
  console.log('  - 租赁管理 (mockRentalAPI)')
  console.log('  - 客户管理 (mockCustomerAPI)')
  console.log('  - 设备管理 (mockDeviceAPI)')
  console.log('  - 产品管理 (mockProductAPI)')
  console.log('  - 公益管理 (mockCharityAPI)')
  console.log('  - 平台链接管理 (mockPlatformLinkAPI)')
  console.log('  - 课程管理 (mockCourseAPI)')
  console.log('  - AI字体包管理 (mockFontPackageAPI)')
  console.log('  - 多语言管理 (mockLanguageAPI)')
  console.log('  - 综合管理后台 (mockAdminDashboardAPI)')
  console.log('  【官网前端】')
  console.log('  - 官网展示 (mockWebsiteAPI)')
  console.log('  【数据统计】')
  console.log('  - 完整数据统计表 (mockDataStatisticsAPI)')
  
  if (mockConfig.enabled) {
    console.log('✅ Mock数据已启用')
    console.log('📈 数据生成配置:', mockConfig.generation)
    console.log('📊 数据统计表已集成，包含以下统计模块：')
    console.log('    - 系统概览统计')
    console.log('    - 业务数据统计（销售、租赁、客户、收入）')
    console.log('    - 内容管理统计（产品、公益、平台、媒体）')
    console.log('    - 系统管理统计（课程、字体包、语言、设备、基础设施）')
    console.log('    - 网站流量统计（访问、参与、转化、内容）')
    console.log('    - 性能监控统计（系统、API、数据库、前端）')
    console.log('    - 趋势分析统计（日、周、月、季、年度趋势及预测）')
  } else {
    console.log('❌ Mock数据已禁用')
  }
}

// 获取所有Mock数据统计
export const getMockDataStats = () => {
  return {
    // 数据看板统计
    dashboard: {
      totalModules: 12,
      totalAPIs: 60,
      totalDataPoints: 1200
    },
    
    // 业务管理数据
    business: {
      sales: {
        orders: mockConfig.generation.salesOrders,
        revenue: '8M-15M',
        regions: 20,
        channels: 8
      },
      rental: {
        orders: mockConfig.generation.rentalOrders,
        devices: mockConfig.generation.devices,
        utilization: '68-88%'
      },
      customers: {
        total: mockConfig.generation.customers,
        segments: 4,
        levels: 4
      }
    },
    
    // 内容管理数据
    content: {
      products: {
        total: mockConfig.generation.products,
        categories: 7,
        reviews: '10-50 per product'
      },
      charity: {
        projects: mockConfig.generation.charityProjects,
        institutions: 80,
        activities: 150,
        volunteers: 120
      },
      platformLinks: {
        total: mockConfig.generation.platformLinks,
        regions: 10,
        platforms: 16
      }
    },
    
    // 系统管理数据
    system: {
      courses: {
        total: mockConfig.generation.courses,
        characters: '15-50 per course',
        translations: '3-6 languages per character'
      },
      fontPackages: {
        total: mockConfig.generation.fontPackages,
        samples: '30-100 per package',
        accuracy: '88-96%'
      },
      languages: {
        supported: 12,
        robotTexts: mockConfig.generation.robotTexts,
        websiteContents: mockConfig.generation.websiteContents,
        completeness: '75-92%'
      },
      devices: {
        total: mockConfig.generation.devices,
        statuses: 5,
        alerts: '15-50',
        firmwareVersions: 10
      }
    },
    
    // 官网前端数据
    website: {
      products: {
        total: mockConfig.generation.websiteProducts,
        categories: 5,
        features: '6-9 per product'
      },
      testimonials: {
        total: mockConfig.generation.testimonials,
        verified: '70-80%',
        international: '30%'
      },
      charity: {
        locations: mockConfig.generation.charityLocations,
        studentWorks: mockConfig.generation.studentWorks,
        volunteerActivities: mockConfig.generation.volunteerActivities,
        countries: 8
      },
      platformLinks: {
        total: mockConfig.generation.websitePlatformLinks,
        purchase: 6,
        rental: 4,
        regions: 6
      },
      multiLanguage: {
        languages: 5,
        contentKeys: 15,
        completeness: '100%'
      }
    },
    
    // 数据统计表模块
    dataStatistics: {
      totalStatistics: 7,
      systemOverview: '10+ 核心指标',
      businessStats: '4大业务模块统计',
      contentStats: '4大内容模块统计',
      systemStats: '5大系统模块统计',
      websiteStats: '4大网站模块统计',
      performanceStats: '4大性能模块统计',
      trendStats: '5个时间维度趋势分析',
      realTimeStats: '实时数据监控',
      predictiveAnalytics: '智能预测分析'
    },
    
    // 总计统计
    totals: {
      dataRecords: Object.values(mockConfig.generation).reduce((sum, count) => sum + count, 0),
      apiEndpoints: 70, // 增加了数据统计API
      mockServices: 13, // 增加了数据统计服务
      dataCategories: 6, // 增加了数据统计分类
      statisticsModules: 7, // 新增统计模块数量
      totalDataPoints: 2000 // 包含统计表的总数据点
    }
  }
}