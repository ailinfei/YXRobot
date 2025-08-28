/**
 * 管理后台综合Mock数据
 * 整合所有模块的Mock数据，提供统一的管理后台数据接口
 */

import { DashboardMockService } from './dashboard'
import { SalesMockService } from './sales'
import { RentalMockService } from './rental'
import { CustomerMockService } from './customer'
import { DeviceMockService } from './device'
import { ProductMockService } from './product'
import { CharityMockService } from './charity'
import { PlatformLinkMockService } from './platformLink'
import { CourseMockService } from './course'
// import { FontPackageMockService } from './fontPackage'
import { LanguageMockService } from './language'

export interface AdminDashboardData {
  // 数据看板数据
  dashboardStats: ReturnType<typeof DashboardMockService.generateDashboardStats>
  recentActivities: ReturnType<typeof DashboardMockService.generateRecentActivities>
  trendData: ReturnType<typeof DashboardMockService.generateTrendData>
  topPerformers: ReturnType<typeof DashboardMockService.generateTopPerformers>

  // 业务管理数据
  salesData: {
    stats: ReturnType<typeof SalesMockService.generateSalesStats>
    orders: ReturnType<typeof SalesMockService.generateSalesOrders>
    regionData: ReturnType<typeof SalesMockService.generateRegionSalesData>
    channelData: ReturnType<typeof SalesMockService.generateChannelSalesData>
    trendData: ReturnType<typeof SalesMockService.generateSalesTrendData>
    productRanking: ReturnType<typeof SalesMockService.generateProductSalesRanking>
  }

  rentalData: {
    stats: ReturnType<typeof RentalMockService.generateRentalStats>
    orders: ReturnType<typeof RentalMockService.generateRentalOrders>
    deviceUtilization: ReturnType<typeof RentalMockService.generateDeviceUtilizationData>
    trendData: ReturnType<typeof RentalMockService.generateRentalTrendData>
    revenueAnalysis: ReturnType<typeof RentalMockService.generateRentalRevenueAnalysis>
  }

  customerData: {
    customers: ReturnType<typeof CustomerMockService.generateCustomers>
    sampleDevices: ReturnType<typeof CustomerMockService.generateCustomerDevices>
    sampleOrders: ReturnType<typeof CustomerMockService.generateCustomerOrders>
  }

  deviceData: {
    devices: ReturnType<typeof DeviceMockService.generateDevices>
    stats: ReturnType<typeof DeviceMockService.generateDeviceStats>
    alerts: ReturnType<typeof DeviceMockService.generateDeviceAlerts>
    firmwareVersions: ReturnType<typeof DeviceMockService.generateFirmwareVersions>
    sampleUsageData: ReturnType<typeof DeviceMockService.generateDeviceUsageData>
  }

  // 内容管理数据
  productData: {
    products: ReturnType<typeof ProductMockService.generateProducts>
    categories: ReturnType<typeof ProductMockService.generateProductCategories>
    sampleReviews: ReturnType<typeof ProductMockService.generateProductReviews>
  }

  charityData: {
    stats: ReturnType<typeof CharityMockService.generateCharityStats>
    projects: ReturnType<typeof CharityMockService.generateCharityProjects>
    institutions: ReturnType<typeof CharityMockService.generateCharityInstitutions>
    activities: ReturnType<typeof CharityMockService.generateCharityActivities>
    donations: ReturnType<typeof CharityMockService.generateCharityDonations>
    volunteers: ReturnType<typeof CharityMockService.generateCharityVolunteers>
  }

  platformLinkData: {
    links: ReturnType<typeof PlatformLinkMockService.generatePlatformLinks>
    regions: ReturnType<typeof PlatformLinkMockService.generateRegionConfigs>
    statsSummary: ReturnType<typeof PlatformLinkMockService.generatePlatformLinkStats>
    sampleAnalytics: ReturnType<typeof PlatformLinkMockService.generateLinkAnalytics>
  }

  // 系统管理数据
  courseData: {
    courses: ReturnType<typeof CourseMockService.generateCourses>
    sampleCharacters: ReturnType<typeof CourseMockService.generateCharacters>
    sampleTranslations: ReturnType<typeof CourseMockService.generateCharacterTranslations>
  }

  // fontPackageData: {
  //   packages: ReturnType<typeof FontPackageMockService.generateFontPackages>
  //   sampleSamples: ReturnType<typeof FontPackageMockService.generateFontSamples>
  //   sampleTasks: ReturnType<typeof FontPackageMockService.generateGenerationTasks>
  //   samplePreviews: ReturnType<typeof FontPackageMockService.generateFontPreviews>
  // }

  languageData: {
    languages: ReturnType<typeof LanguageMockService.generateLanguages>
    robotTexts: ReturnType<typeof LanguageMockService.generateRobotInterfaceTexts>
    websiteContents: ReturnType<typeof LanguageMockService.generateWebsiteContents>
    translationProgress: ReturnType<typeof LanguageMockService.generateTranslationProgress>
  }
}

// 生成完整的管理后台Mock数据
export class AdminDashboardMockService {
  static generateCompleteAdminData(): AdminDashboardData {
    console.log('🚀 开始生成完整的管理后台Mock数据...')
    const startTime = Date.now()

    // 生成数据看板数据
    const dashboardStats = DashboardMockService.generateDashboardStats()
    const recentActivities = DashboardMockService.generateRecentActivities(30)
    const trendData = DashboardMockService.generateTrendData(30)
    const topPerformers = DashboardMockService.generateTopPerformers()

    // 生成业务管理数据
    const salesStats = SalesMockService.generateSalesStats()
    const salesOrders = SalesMockService.generateSalesOrders(500)
    const regionData = SalesMockService.generateRegionSalesData()
    const channelData = SalesMockService.generateChannelSalesData()
    const salesTrendData = SalesMockService.generateSalesTrendData(365)
    const productRanking = SalesMockService.generateProductSalesRanking()

    const rentalStats = RentalMockService.generateRentalStats()
    const rentalOrders = RentalMockService.generateRentalOrders(200)
    const deviceUtilization = RentalMockService.generateDeviceUtilizationData()
    const rentalTrendData = RentalMockService.generateRentalTrendData('monthly', 90)
    const revenueAnalysis = RentalMockService.generateRentalRevenueAnalysis()

    const customers = CustomerMockService.generateCustomers(200)
    const sampleCustomerDevices = CustomerMockService.generateCustomerDevices(1, 5)
    const sampleCustomerOrders = CustomerMockService.generateCustomerOrders(1, 10)

    const devices = DeviceMockService.generateDevices(150)
    const deviceStats = DeviceMockService.generateDeviceStats()
    const deviceAlerts = DeviceMockService.generateDeviceAlerts(50)
    const firmwareVersions = DeviceMockService.generateFirmwareVersions()
    const sampleUsageData = DeviceMockService.generateDeviceUsageData('YX-000001', 30)

    // 生成内容管理数据
    const products = ProductMockService.generateProducts(30)
    const productCategories = ProductMockService.generateProductCategories()
    const sampleProductReviews = ProductMockService.generateProductReviews(1, 20)

    const charityStats = CharityMockService.generateCharityStats()
    const charityProjects = CharityMockService.generateCharityProjects(50)
    const charityInstitutions = CharityMockService.generateCharityInstitutions(80)
    const charityActivities = CharityMockService.generateCharityActivities(150)
    const charityDonations = CharityMockService.generateCharityDonations(120)
    const charityVolunteers = CharityMockService.generateCharityVolunteers(100)

    const platformLinks = PlatformLinkMockService.generatePlatformLinks(60)
    const platformRegions = PlatformLinkMockService.generateRegionConfigs()
    const linkStatsSummary = PlatformLinkMockService.generatePlatformLinkStats()
    const sampleLinkAnalytics = PlatformLinkMockService.generateLinkAnalytics(1, 30)

    // 生成系统管理数据
    const courses = CourseMockService.generateCourses(100)
    const sampleCharacters = CourseMockService.generateCharacters(1, 20)
    const sampleTranslations = CourseMockService.generateCharacterTranslations(1)

    // const fontPackages = FontPackageMockService.generateFontPackages(50)
    // const sampleFontSamples = FontPackageMockService.generateFontSamples(1, 50)
    // const sampleGenerationTasks = FontPackageMockService.generateGenerationTasks(1, 5)
    // const sampleFontPreviews = FontPackageMockService.generateFontPreviews(1, 20)

    const languages = LanguageMockService.generateLanguages()
    const robotTexts = LanguageMockService.generateRobotInterfaceTexts(50)
    const websiteContents = LanguageMockService.generateWebsiteContents(100)
    const translationProgress = LanguageMockService.generateTranslationProgress()

    const endTime = Date.now()
    const duration = endTime - startTime

    console.log(`✅ 管理后台Mock数据生成完成！耗时: ${duration}ms`)
    console.log('📊 数据统计:')
    console.log(`  - 数据看板: 统计数据 + ${recentActivities.length} 条活动 + ${trendData.length} 天趋势`)
    console.log(`  - 销售管理: ${salesOrders.length} 个订单 + ${regionData.length} 个地区 + ${channelData.length} 个渠道`)
    console.log(`  - 租赁管理: ${rentalOrders.length} 个订单 + ${deviceUtilization.length} 台设备`)
    console.log(`  - 客户管理: ${customers.length} 个客户`)
    console.log(`  - 设备管理: ${devices.length} 台设备 + ${deviceAlerts.length} 条告警`)
    console.log(`  - 产品管理: ${products.length} 个产品 + ${productCategories.length} 个分类`)
    console.log(`  - 公益管理: ${charityProjects.length} 个项目 + ${charityInstitutions.length} 家机构`)
    console.log(`  - 平台链接: ${platformLinks.length} 个链接 + ${platformRegions.length} 个地区`)
    console.log(`  - 课程管理: ${courses.length} 门课程`)
    // console.log(`  - 字体包管理: ${fontPackages.length} 个字体包`)
    console.log(`  - 多语言管理: ${languages.length} 种语言 + ${robotTexts.length} 条机器人文字`)

    return {
      dashboardStats,
      recentActivities,
      trendData,
      topPerformers,

      salesData: {
        stats: salesStats,
        orders: salesOrders,
        regionData,
        channelData,
        trendData: salesTrendData,
        productRanking
      },

      rentalData: {
        stats: rentalStats,
        orders: rentalOrders,
        deviceUtilization,
        trendData: rentalTrendData,
        revenueAnalysis
      },

      customerData: {
        customers,
        sampleDevices: sampleCustomerDevices,
        sampleOrders: sampleCustomerOrders
      },

      deviceData: {
        devices,
        stats: deviceStats,
        alerts: deviceAlerts,
        firmwareVersions,
        sampleUsageData
      },

      productData: {
        products,
        categories: productCategories,
        sampleReviews: sampleProductReviews
      },

      charityData: {
        stats: charityStats,
        projects: charityProjects,
        institutions: charityInstitutions,
        activities: charityActivities,
        donations: charityDonations,
        volunteers: charityVolunteers
      },

      platformLinkData: {
        links: platformLinks,
        regions: platformRegions,
        statsSummary: linkStatsSummary,
        sampleAnalytics: sampleLinkAnalytics
      },

      courseData: {
        courses,
        sampleCharacters,
        sampleTranslations
      },

      // fontPackageData: {
      //   packages: fontPackages,
      //   sampleSamples: sampleFontSamples,
      //   sampleTasks: sampleGenerationTasks,
      //   samplePreviews: sampleFontPreviews
      // },

      languageData: {
        languages,
        robotTexts,
        websiteContents,
        translationProgress
      }
    }
  }

  // 生成数据看板专用的汇总数据
  static generateDashboardSummary() {
    const completeData = this.generateCompleteAdminData()

    return {
      // 核心业务指标
      coreMetrics: {
        totalRevenue: completeData.dashboardStats.business.totalRevenue,
        monthlyRevenue: completeData.dashboardStats.business.monthlyRevenue,
        totalOrders: completeData.dashboardStats.business.totalOrders,
        totalCustomers: completeData.dashboardStats.business.totalCustomers,
        activeDevices: completeData.dashboardStats.devices.online,
        deviceUtilization: completeData.dashboardStats.business.deviceUtilization
      },

      // 各模块数据量统计
      dataVolume: {
        sales: completeData.salesData.orders.length,
        rental: completeData.rentalData.orders.length,
        customers: completeData.customerData.customers.length,
        devices: completeData.deviceData.devices.length,
        products: completeData.productData.products.length,
        charity: completeData.charityData.projects.length,
        platformLinks: completeData.platformLinkData.links.length,
        courses: completeData.courseData.courses.length,
        fontPackages: 0, // completeData.fontPackageData.packages.length,
        languages: completeData.languageData.languages.length
      },

      // 系统健康状态
      systemHealth: {
        deviceOnlineRate: (completeData.dashboardStats.devices.online / completeData.dashboardStats.devices.total * 100).toFixed(1),
        averageDeviceHealth: completeData.dashboardStats.devices.healthScore,
        alertCount: completeData.dashboardStats.devices.alertCount,
        systemUptime: '99.8%',
        dataFreshness: 'Real-time'
      },

      // 业务增长趋势
      growthTrends: {
        revenueGrowth: completeData.dashboardStats.business.revenueGrowth,
        customerGrowth: completeData.dashboardStats.business.customerGrowth,
        orderGrowth: completeData.dashboardStats.business.orderGrowth,
        salesGrowth: completeData.dashboardStats.sales.salesGrowth,
        rentalGrowth: completeData.dashboardStats.rental.rentalGrowth
      }
    }
  }

  // 获取各模块的快速统计
  static getModuleQuickStats() {
    return {
      dashboard: { apis: 4, dataPoints: 100 },
      sales: { apis: 6, orders: 500, regions: 20 },
      rental: { apis: 5, orders: 200, devices: 150 },
      customers: { apis: 6, customers: 200, segments: 4 },
      devices: { apis: 8, devices: 150, alerts: 50 },
      products: { apis: 8, products: 30, categories: 7 },
      charity: { apis: 6, projects: 50, institutions: 80 },
      platformLinks: { apis: 7, links: 60, regions: 10 },
      courses: { apis: 8, courses: 100, characters: 2000 },
      fontPackages: { apis: 8, packages: 50, samples: 2500 },
      languages: { apis: 6, languages: 12, texts: 150 }
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

// 管理后台综合Mock API
export const mockAdminDashboardAPI = {
  // 获取完整的管理后台数据
  async getCompleteAdminData() {
    await mockDelay(1000) // 较长延迟，因为数据量大
    const data = AdminDashboardMockService.generateCompleteAdminData()
    return createMockResponse(data)
  },

  // 获取数据看板汇总
  async getDashboardSummary() {
    await mockDelay()
    const summary = AdminDashboardMockService.generateDashboardSummary()
    return createMockResponse(summary)
  },

  // 获取各模块快速统计
  async getModuleQuickStats() {
    await mockDelay(200)
    const stats = AdminDashboardMockService.getModuleQuickStats()
    return createMockResponse(stats)
  },

  // 获取系统概览数据（用于首页展示）
  async getSystemOverview() {
    await mockDelay()
    const completeData = AdminDashboardMockService.generateCompleteAdminData()

    const overview = {
      businessMetrics: {
        totalRevenue: completeData.dashboardStats.business.totalRevenue,
        monthlyRevenue: completeData.dashboardStats.business.monthlyRevenue,
        revenueGrowth: completeData.dashboardStats.business.revenueGrowth,
        totalOrders: completeData.dashboardStats.business.totalOrders,
        orderGrowth: completeData.dashboardStats.business.orderGrowth
      },

      operationalMetrics: {
        totalDevices: completeData.dashboardStats.devices.total,
        onlineDevices: completeData.dashboardStats.devices.online,
        deviceUtilization: completeData.dashboardStats.business.deviceUtilization,
        totalCustomers: completeData.dashboardStats.business.totalCustomers,
        activeCustomers: completeData.dashboardStats.business.activeCustomers
      },

      contentMetrics: {
        totalProducts: completeData.dashboardStats.products.total,
        publishedProducts: completeData.dashboardStats.products.published,
        totalCourses: completeData.dashboardStats.courses.total,
        activeCourses: completeData.dashboardStats.courses.active,
        totalFontPackages: completeData.dashboardStats.fontPackages.total,
        publishedFontPackages: completeData.dashboardStats.fontPackages.published
      },

      systemMetrics: {
        supportedLanguages: completeData.dashboardStats.languages.supported,
        enabledLanguages: completeData.dashboardStats.languages.enabled,
        totalTranslations: completeData.dashboardStats.languages.totalTranslations,
        platformLinks: completeData.dashboardStats.platformLinks.total,
        activePlatformLinks: completeData.dashboardStats.platformLinks.active
      },

      recentActivities: completeData.recentActivities.slice(0, 10),
      trendData: completeData.trendData.slice(-7) // 最近7天
    }

    return createMockResponse(overview)
  }
}