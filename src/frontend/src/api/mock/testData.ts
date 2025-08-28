/**
 * 测试数据初始化
 * 为YXRobot项目生成完整的测试数据集
 */

import { CharityMockService } from './charity'
import { CustomerMockService } from './customer'
import { CourseMockService } from './course'
// import { FontPackageMockService } from './fontPackage'
import { LanguageMockService } from './language'
import { DashboardMockService } from './dashboard'
import { RentalMockService } from './rental'
import { SalesMockService } from './sales'

// 测试数据配置
export const TEST_DATA_CONFIG = {
  // 公益慈善数据
  charity: {
    projects: 50,
    institutions: 80,
    activities: 150,
    donations: 200,
    volunteers: 120
  },
  
  // 客户管理数据
  customers: {
    count: 300,
    devicesPerCustomer: 5,
    ordersPerCustomer: 15
  },
  
  // 课程管理数据
  courses: {
    count: 100,
    charactersPerCourse: 30,
    translationsPerCharacter: 6
  },
  
  // AI字体包数据
  fontPackages: {
    count: 60,
    samplesPerPackage: 80,
    previewsPerPackage: 40,
    tasksPerPackage: 8
  },
  
  // 多语言数据
  languages: {
    supported: 15,
    robotTexts: 80,
    websiteContents: 120
  },
  
  // 租赁数据
  rental: {
    orders: 200,
    devices: 100,
    trendDays: 90
  },
  
  // 官网数据
  website: {
    products: 5,
    testimonials: 20,
    charityLocations: 50,
    studentWorks: 100,
    volunteerActivities: 30,
    platformLinks: 20
  },
  
  // 销售数据
  sales: {
    orders: 500,
    regions: 20,
    channels: 8,
    trendDays: 365
  }
}

// 生成完整测试数据集
export class TestDataGenerator {
  // 生成公益慈善完整数据
  static generateCharityTestData() {
    const config = TEST_DATA_CONFIG.charity
    
    return {
      stats: CharityMockService.generateCharityStats(),
      projects: CharityMockService.generateCharityProjects(config.projects),
      institutions: CharityMockService.generateCharityInstitutions(config.institutions),
      activities: CharityMockService.generateCharityActivities(config.activities),
      donations: CharityMockService.generateCharityDonations(config.donations),
      volunteers: CharityMockService.generateCharityVolunteers(config.volunteers)
    }
  }
  
  // 生成客户管理完整数据
  static generateCustomerTestData() {
    const config = TEST_DATA_CONFIG.customers
    const customers = CustomerMockService.generateCustomers(config.count)
    
    const customerDevices = customers.map(customer => 
      CustomerMockService.generateCustomerDevices(customer.id, config.devicesPerCustomer)
    ).flat()
    
    const customerOrders = customers.map(customer => 
      CustomerMockService.generateCustomerOrders(customer.id, config.ordersPerCustomer)
    ).flat()
    
    return {
      customers,
      devices: customerDevices,
      orders: customerOrders
    }
  }
  
  // 生成课程管理完整数据
  static generateCourseTestData() {
    const config = TEST_DATA_CONFIG.courses
    const courses = CourseMockService.generateCourses(config.count)
    
    const courseCharacters = courses.map(course => 
      CourseMockService.generateCharacters(course.id, config.charactersPerCourse)
    ).flat()
    
    const characterTranslations = courseCharacters.map(character => 
      CourseMockService.generateCharacterTranslations(character.id)
    ).flat()
    
    return {
      courses,
      characters: courseCharacters,
      translations: characterTranslations
    }
  }
  
  // 生成AI字体包完整数据
  static generateFontPackageTestData() {
    // const config = TEST_DATA_CONFIG.fontPackages
    // const fontPackages = FontPackageMockService.generateFontPackages(config.count)
    
    // const fontSamples = fontPackages.map(pkg => 
    //   FontPackageMockService.generateFontSamples(pkg.id, config.samplesPerPackage)
    // ).flat()
    
    // const fontPreviews = fontPackages.map(pkg => 
    //   FontPackageMockService.generateFontPreviews(pkg.id, config.previewsPerPackage)
    // ).flat()
    
    // const generationTasks = fontPackages.map(pkg => 
    //   FontPackageMockService.generateGenerationTasks(pkg.id, config.tasksPerPackage)
    // ).flat()
    
    return {
      packages: [],
      samples: [],
      previews: [],
      tasks: []
    }
  }
  
  // 生成多语言完整数据
  static generateLanguageTestData() {
    const config = TEST_DATA_CONFIG.languages
    
    return {
      languages: LanguageMockService.generateLanguages(),
      robotTexts: LanguageMockService.generateRobotInterfaceTexts(config.robotTexts),
      websiteContents: LanguageMockService.generateWebsiteContents(config.websiteContents),
      translationProgress: LanguageMockService.generateTranslationProgress()
    }
  }
  
  // 生成租赁管理完整数据
  static generateRentalTestData() {
    const config = TEST_DATA_CONFIG.rental
    
    return {
      stats: RentalMockService.generateRentalStats(),
      orders: RentalMockService.generateRentalOrders(config.orders),
      devices: RentalMockService.generateDeviceUtilizationData(),
      trends: RentalMockService.generateRentalTrendData('monthly', config.trendDays),
      analysis: RentalMockService.generateRentalRevenueAnalysis()
    }
  }
  
  // 生成仪表板完整数据
  static generateDashboardTestData() {
    return {
      stats: DashboardMockService.generateDashboardStats(),
      activities: DashboardMockService.generateRecentActivities(30),
      trends: DashboardMockService.generateTrendData(30),
      performers: DashboardMockService.generateTopPerformers()
    }
  }
  
  // 生成销售管理完整数据
  static generateSalesTestData() {
    const config = TEST_DATA_CONFIG.sales
    
    return {
      stats: SalesMockService.generateSalesStats(),
      orders: SalesMockService.generateSalesOrders(config.orders),
      regions: SalesMockService.generateRegionSalesData(),
      channels: SalesMockService.generateChannelSalesData(),
      trends: SalesMockService.generateSalesTrendData(config.trendDays),
      products: SalesMockService.generateProductSalesRanking()
    }
  }
  
  // 生成所有测试数据
  static generateAllTestData() {
    console.log('🚀 开始生成YXRobot完整测试数据集...')
    
    const startTime = Date.now()
    
    const testData = {
      charity: this.generateCharityTestData(),
      customers: this.generateCustomerTestData(),
      courses: this.generateCourseTestData(),
      fontPackages: this.generateFontPackageTestData(),
      languages: this.generateLanguageTestData(),
      rental: this.generateRentalTestData(),
      sales: this.generateSalesTestData(),
      dashboard: this.generateDashboardTestData()
    }
    
    const endTime = Date.now()
    const duration = endTime - startTime
    
    console.log(`✅ 测试数据生成完成！耗时: ${duration}ms`)
    console.log('📊 数据统计:')
    console.log(`  - 公益项目: ${testData.charity.projects.length} 个`)
    console.log(`  - 合作机构: ${testData.charity.institutions.length} 家`)
    console.log(`  - 公益活动: ${testData.charity.activities.length} 次`)
    console.log(`  - 捐赠记录: ${testData.charity.donations.length} 条`)
    console.log(`  - 志愿者: ${testData.charity.volunteers.length} 人`)
    console.log(`  - 客户: ${testData.customers.customers.length} 个`)
    console.log(`  - 客户设备: ${testData.customers.devices.length} 台`)
    console.log(`  - 客户订单: ${testData.customers.orders.length} 个`)
    console.log(`  - 课程: ${testData.courses.courses.length} 门`)
    console.log(`  - 汉字: ${testData.courses.characters.length} 个`)
    console.log(`  - 翻译: ${testData.courses.translations.length} 条`)
    console.log(`  - 字体包: ${testData.fontPackages.packages.length} 个`)
    console.log(`  - 字体样本: ${testData.fontPackages.samples.length} 个`)
    console.log(`  - 语言支持: ${testData.languages.languages.length} 种`)
    console.log(`  - 租赁订单: ${testData.rental.orders.length} 个`)
    
    return testData
  }
  
  // 获取数据统计信息
  static getDataStatistics() {
    const config = TEST_DATA_CONFIG
    
    return {
      totalRecords: Object.values(config).reduce((total, moduleConfig) => {
        if (typeof moduleConfig === 'object') {
          return total + Object.values(moduleConfig).reduce((sum: number, count) => sum + (count as number), 0)
        }
        return total + (moduleConfig as number)
      }, 0),
      modules: {
        charity: Object.values(config.charity).reduce((sum, count) => sum + count, 0),
        customers: config.customers.count + 
                   (config.customers.count * config.customers.devicesPerCustomer) + 
                   (config.customers.count * config.customers.ordersPerCustomer),
        courses: config.courses.count + 
                (config.courses.count * config.courses.charactersPerCourse) + 
                (config.courses.count * config.courses.charactersPerCourse * config.courses.translationsPerCharacter),
        fontPackages: config.fontPackages.count + 
                     (config.fontPackages.count * config.fontPackages.samplesPerPackage) + 
                     (config.fontPackages.count * config.fontPackages.previewsPerPackage) + 
                     (config.fontPackages.count * config.fontPackages.tasksPerPackage),
        languages: config.languages.supported + config.languages.robotTexts + config.languages.websiteContents,
        rental: config.rental.orders + config.rental.devices + config.rental.trendDays
      }
    }
  }
}

// 预定义的示例数据集
export const SAMPLE_DATA_SETS = {
  // 小型数据集（用于快速测试）
  small: {
    charity: { projects: 10, institutions: 15, activities: 25, donations: 30, volunteers: 20 },
    customers: { count: 50, devicesPerCustomer: 3, ordersPerCustomer: 8 },
    courses: { count: 20, charactersPerCourse: 15, translationsPerCharacter: 4 },
    fontPackages: { count: 15, samplesPerPackage: 30, previewsPerPackage: 20, tasksPerPackage: 5 },
    languages: { supported: 8, robotTexts: 40, websiteContents: 60 },
    rental: { orders: 80, devices: 40, trendDays: 30 }
  },
  
  // 中型数据集（用于功能测试）
  medium: {
    charity: { projects: 30, institutions: 50, activities: 80, donations: 100, volunteers: 60 },
    customers: { count: 150, devicesPerCustomer: 4, ordersPerCustomer: 12 },
    courses: { count: 60, charactersPerCourse: 25, translationsPerCharacter: 5 },
    fontPackages: { count: 35, samplesPerPackage: 50, previewsPerPackage: 30, tasksPerPackage: 6 },
    languages: { supported: 12, robotTexts: 60, websiteContents: 90 },
    rental: { orders: 120, devices: 60, trendDays: 60 }
  },
  
  // 大型数据集（用于性能测试）
  large: {
    charity: { projects: 100, institutions: 150, activities: 300, donations: 500, volunteers: 200 },
    customers: { count: 500, devicesPerCustomer: 6, ordersPerCustomer: 20 },
    courses: { count: 200, charactersPerCourse: 40, translationsPerCharacter: 8 },
    fontPackages: { count: 100, samplesPerPackage: 100, previewsPerPackage: 60, tasksPerPackage: 10 },
    languages: { supported: 20, robotTexts: 120, websiteContents: 200 },
    rental: { orders: 400, devices: 200, trendDays: 180 }
  }
}

// 导出便捷函数
export const generateTestData = (size: 'small' | 'medium' | 'large' = 'medium') => {
  const originalConfig = { ...TEST_DATA_CONFIG }
  
  // 临时替换配置
  Object.assign(TEST_DATA_CONFIG, SAMPLE_DATA_SETS[size])
  
  const data = TestDataGenerator.generateAllTestData()
  
  // 恢复原始配置
  Object.assign(TEST_DATA_CONFIG, originalConfig)
  
  return data
}

// 初始化测试数据
export const initializeTestData = () => {
  console.log('🎯 初始化YXRobot测试数据系统')
  console.log('📈 数据统计:', TestDataGenerator.getDataStatistics())
  
  return TestDataGenerator.generateAllTestData()
}