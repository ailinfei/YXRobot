/**
 * 数据统计表
 * 提供YXRobot系统的完整数据统计和分析功能
 */

import { mockUtils } from './index'

// 数据统计接口定义
export interface AllStatistics {
    overview: SystemOverview
    business: BusinessStatistics
    content: ContentStatistics
    systemManagement: SystemManagementStatistics
    website: WebsiteStatistics
    performance: PerformanceStatistics
    trends: TrendStatistics
}

export interface SystemOverview {
    totalUsers: number
    totalDevices: number
    totalRevenue: number
    totalOrders: number
    activeCustomers: number
    onlineDevices: number
    monthlyGrowth: number
    satisfactionRate: number
    uptime: number
    lastUpdated: string
}

export interface BusinessStatistics {
    sales: SalesStatistics
    rental: RentalStatistics
    customers: CustomerStatistics
    revenue: RevenueStatistics
}

export interface ContentStatistics {
    products: ProductContentStats
    charity: CharityContentStats
    platforms: PlatformContentStats
    media: MediaContentStats
}

export interface SystemManagementStatistics {
    courses: CourseStatistics
    fontPackages: FontPackageStatistics
    languages: LanguageStatistics
    devices: DeviceStatistics
    infrastructure: InfrastructureStatistics
}

export interface WebsiteStatistics {
    traffic: TrafficStatistics
    engagement: EngagementStatistics
    conversion: ConversionStatistics
    content: WebsiteContentStatistics
}

export interface PerformanceStatistics {
    system: SystemPerformanceStats
    api: APIPerformanceStats
    database: DatabasePerformanceStats
    frontend: FrontendPerformanceStats
}

export interface TrendStatistics {
    daily: DailyTrendData[]
    weekly: WeeklyTrendData[]
    monthly: MonthlyTrendData[]
    quarterly: QuarterlyTrendData[]
    yearly: YearlyTrendData[]
    forecasts: ForecastData[]
    seasonality: SeasonalityData[]
}

export interface SalesStatistics {
    totalOrders: number
    totalRevenue: number
    averageOrderValue: number
    conversionRate: number
    topProducts: ProductSalesRank[]
    regionDistribution: RegionSalesData[]
    channelPerformance: ChannelPerformanceData[]
    monthlyTrends: MonthlySalesData[]
    quarterlyComparison: QuarterlySalesData[]
}

export interface ProductSalesRank {
    productId: number
    productName: string
    salesCount: number
    revenue: number
    growthRate: number
}

export interface RegionSalesData {
    region: string
    salesCount: number
    revenue: number
    growthRate: number
}

export interface ChannelPerformanceData {
    channel: string
    salesCount: number
    revenue: number
    conversionRate: number
}

export interface MonthlySalesData {
    month: string
    salesCount: number
    revenue: number
    growthRate: number
}

export interface QuarterlySalesData {
    quarter: string
    salesCount: number
    revenue: number
    growthRate: number
}

export interface RentalStatistics {
    totalOrders: number
    totalRevenue: number
    averageRentalPeriod: number
    deviceUtilization: number
    topRentalProducts: ProductRentalRank[]
    regionDistribution: RegionRentalData[]
    utilizationTrends: UtilizationTrendData[]
    revenueAnalysis: RentalRevenueData[]
}

export interface ProductRentalRank {
    productId: number
    productName: string
    rentalCount: number
    revenue: number
    utilizationRate: number
}

export interface RegionRentalData {
    region: string
    rentalCount: number
    revenue: number
    utilizationRate: number
}

export interface UtilizationTrendData {
    date: string
    utilizationRate: number
    activeDevices: number
    totalDevices: number
}

export interface RentalRevenueData {
    period: string
    revenue: number
    growthRate: number
    averageRentalValue: number
}

export interface CustomerStatistics {
    totalCustomers: number
    activeCustomers: number
    newCustomers: number
    customerRetention: number
    customerSegments: CustomerSegmentData[]
    satisfactionScores: SatisfactionData[]
    lifetimeValue: LifetimeValueData[]
    churnAnalysis: ChurnAnalysisData[]
}

export interface CustomerSegmentData {
    segment: string
    customerCount: number
    revenue: number
    averageOrderValue: number
}

export interface SatisfactionData {
    period: string
    score: number
    responseCount: number
    trend: number
}

export interface LifetimeValueData {
    segment: string
    averageValue: number
    retentionRate: number
    churnRate: number
}

export interface ChurnAnalysisData {
    period: string
    churnRate: number
    churnedCustomers: number
    retainedCustomers: number
}

export interface RevenueStatistics {
    totalRevenue: number
    recurringRevenue: number
    oneTimeRevenue: number
    revenueGrowth: number
    revenueBySource: RevenueSourceData[]
    monthlyRecurring: MonthlyRecurringData[]
    forecastData: RevenueForecastData[]
    profitMargins: ProfitMarginData[]
}

export interface RevenueSourceData {
    source: string
    revenue: number
    percentage: number
    growthRate: number
}

export interface MonthlyRecurringData {
    month: string
    recurringRevenue: number
    newRevenue: number
    churnedRevenue: number
}

export interface RevenueForecastData {
    period: string
    forecastRevenue: number
    confidence: number
    actualRevenue?: number
}

export interface ProfitMarginData {
    category: string
    revenue: number
    cost: number
    profit: number
    margin: number
}

export interface ContentStatistics {
    products: ProductContentStats
    charity: CharityContentStats
    platforms: PlatformContentStats
    media: MediaContentStats
}

export interface ProductContentStats {
    totalProducts: number
    publishedProducts: number
    draftProducts: number
    archivedProducts: number
    totalReviews: number
    averageRating: number
    mediaFiles: number
    contentUpdates: number
}

export interface CharityContentStats {
    totalProjects: number
    activeProjects: number
    totalInstitutions: number
    totalActivities: number
    totalVolunteers: number
    totalDonations: number
    beneficiaries: number
    mediaContent: number
}

export interface PlatformContentStats {
    totalLinks: number
    activeLinks: number
    inactiveLinks: number
    totalClicks: number
    conversionRate: number
    regionCoverage: number
    languageSupport: number
    performanceScore: number
}

export interface MediaContentStats {
    totalImages: number
    totalVideos: number
    totalDocuments: number
    storageUsed: number
    bandwidth: number
    compressionRatio: number
    deliverySpeed: number
    cacheHitRate: number
}

export interface SystemManagementStatistics {
    courses: CourseStatistics
    fontPackages: FontPackageStatistics
    languages: LanguageStatistics
    devices: DeviceStatistics
    infrastructure: InfrastructureStatistics
}

export interface CourseStatistics {
    totalCourses: number
    publishedCourses: number
    totalCharacters: number
    totalTranslations: number
    completionRate: number
    averageProgress: number
    popularCourses: PopularCourseData[]
    difficultyDistribution: DifficultyDistributionData[]
}

export interface PopularCourseData {
    courseId: number
    courseName: string
    enrollments: number
    completionRate: number
    rating: number
}

export interface DifficultyDistributionData {
    difficulty: string
    courseCount: number
    enrollmentCount: number
    completionRate: number
}

export interface FontPackageStatistics {
    totalPackages: number
    completedPackages: number
    inProgressPackages: number
    totalSamples: number
    averageAccuracy: number
    generationTime: number
    storageUsed: number
    downloadCount: number
}

export interface LanguageStatistics {
    supportedLanguages: number
    totalTexts: number
    translatedTexts: number
    translationCompleteness: number
    qualityScore: number
    pendingTranslations: number
    recentUpdates: number
}

export interface DeviceStatistics {
    totalDevices: number
    onlineDevices: number
    offlineDevices: number
    maintenanceDevices: number
    errorDevices: number
    averageUptime: number
    totalAlerts: number
    resolvedAlerts: number
    firmwareVersions: FirmwareVersionData[]
}

export interface FirmwareVersionData {
    version: string
    deviceCount: number
    releaseDate: string
    updateRate: number
}

export interface InfrastructureStatistics {
    serverUptime: number
    databaseSize: number
    apiResponseTime: number
    errorRate: number
    throughput: number
    memoryUsage: number
    cpuUsage: number
    diskUsage: number
}

export interface WebsiteStatistics {
    traffic: TrafficStatistics
    engagement: EngagementStatistics
    conversion: ConversionStatistics
    content: WebsiteContentStatistics
}

export interface TrafficStatistics {
    totalVisitors: number
    uniqueVisitors: number
    pageViews: number
    bounceRate: number
    averageSessionDuration: number
    trafficSources: TrafficSourceData[]
    deviceTypes: DeviceTypeData[]
    geographicData: GeographicData[]
}

export interface TrafficSourceData {
    source: string
    visitors: number
    percentage: number
    conversionRate: number
}

export interface DeviceTypeData {
    device: string
    visitors: number
    percentage: number
    bounceRate: number
}

export interface GeographicData {
    country: string
    visitors: number
    percentage: number
    averageSessionDuration: number
}

export interface EngagementStatistics {
    averageTimeOnSite: number
    pagesPerSession: number
    returnVisitorRate: number
    socialShares: number
    commentCount: number
    newsletterSignups: number
    downloadCount: number
    contactFormSubmissions: number
}

export interface ConversionStatistics {
    overallConversionRate: number
    goalCompletions: number
    ecommerceConversions: number
    leadGeneration: number
    conversionFunnels: ConversionFunnelData[]
    abTestResults: ABTestResultData[]
}

export interface ConversionFunnelData {
    step: string
    visitors: number
    conversionRate: number
    dropOffRate: number
}

export interface ABTestResultData {
    testName: string
    variant: string
    conversionRate: number
    confidence: number
    winner: boolean
}

export interface WebsiteContentStatistics {
    totalPages: number
    publishedPages: number
    blogPosts: number
    mediaFiles: number
    searchQueries: number
    popularContent: PopularContentData[]
    contentPerformance: ContentPerformanceData[]
}

export interface PopularContentData {
    title: string
    url: string
    views: number
    shares: number
    engagementRate: number
}

export interface ContentPerformanceData {
    contentType: string
    totalCount: number
    averageViews: number
    engagementRate: number
}

export interface PerformanceStatistics {
    system: SystemPerformanceStats
    api: APIPerformanceStats
    database: DatabasePerformanceStats
    frontend: FrontendPerformanceStats
}

export interface SystemPerformanceStats {
    uptime: number
    responseTime: number
    throughput: number
    errorRate: number
    memoryUsage: number
    cpuUsage: number
    diskUsage: number
    networkLatency: number
}

export interface APIPerformanceStats {
    totalRequests: number
    averageResponseTime: number
    errorRate: number
    slowestEndpoints: SlowEndpointData[]
    requestsByEndpoint: EndpointRequestData[]
    statusCodeDistribution: StatusCodeData[]
}

export interface SlowEndpointData {
    endpoint: string
    averageResponseTime: number
    requestCount: number
    errorRate: number
}

export interface EndpointRequestData {
    endpoint: string
    requestCount: number
    averageResponseTime: number
    successRate: number
}

export interface StatusCodeData {
    statusCode: number
    count: number
    percentage: number
}

export interface DatabasePerformanceStats {
    queryCount: number
    averageQueryTime: number
    slowQueries: number
    connectionCount: number
    cacheHitRate: number
    indexEfficiency: number
    storageUsed: number
    backupStatus: string
}

export interface FrontendPerformanceStats {
    pageLoadTime: number
    firstContentfulPaint: number
    largestContentfulPaint: number
    cumulativeLayoutShift: number
    firstInputDelay: number
    jsErrorRate: number
    resourceLoadTime: number
    cacheEfficiency: number
}

export interface TrendStatistics {
    daily: DailyTrendData[]
    weekly: WeeklyTrendData[]
    monthly: MonthlyTrendData[]
    quarterly: QuarterlyTrendData[]
    yearly: YearlyTrendData[]
    forecasts: ForecastData[]
    seasonality: SeasonalityData[]
}

export interface DailyTrendData {
    date: string
    users: number
    revenue: number
    orders: number
    devices: number
    satisfaction: number
}

export interface WeeklyTrendData {
    week: string
    users: number
    revenue: number
    orders: number
    growthRate: number
    comparison: number
}

export interface MonthlyTrendData {
    month: string
    users: number
    revenue: number
    orders: number
    growthRate: number
    yearOverYear: number
}

export interface QuarterlyTrendData {
    quarter: string
    users: number
    revenue: number
    orders: number
    growthRate: number
    yearOverYear: number
}

export interface YearlyTrendData {
    year: string
    users: number
    revenue: number
    orders: number
    growthRate: number
    marketShare: number
}

export interface ForecastData {
    period: string
    metric: string
    forecast: number
    confidence: number
    trend: 'up' | 'down' | 'stable'
}

export interface SeasonalityData {
    period: string
    metric: string
    seasonalIndex: number
    trend: number
    cyclical: number
}

// 数据统计生成器
export class DataStatisticsGenerator {
    
    // 生成系统概览统计
    static generateSystemOverview(): SystemOverview {
        return {
            totalUsers: mockUtils.randomBetween(50000, 100000),
            totalDevices: mockUtils.randomBetween(5000, 15000),
            totalRevenue: mockUtils.randomBetween(10000000, 50000000),
            totalOrders: mockUtils.randomBetween(20000, 80000),
            activeCustomers: mockUtils.randomBetween(15000, 35000),
            onlineDevices: mockUtils.randomBetween(4000, 12000),
            monthlyGrowth: mockUtils.randomFloat(5, 25),
            satisfactionRate: mockUtils.randomFloat(85, 98),
            uptime: mockUtils.randomFloat(99.5, 99.99),
            lastUpdated: new Date().toISOString()
        }
    }
    
    // 生成业务统计数据
    static generateBusinessStatistics(): BusinessStatistics {
        return {
            sales: this.generateSalesStatistics(),
            rental: this.generateRentalStatistics(),
            customers: this.generateCustomerStatistics(),
            revenue: this.generateRevenueStatistics()
        }
    }
    
    // 生成销售统计
    static generateSalesStatistics(): SalesStatistics {
        const products = ['YX-Pro-2024', 'YX-Standard-2024', 'YX-Education-2024', 'YX-Home-2024', 'YX-Lite-2024']
        const regions = ['华北', '华东', '华南', '华中', '西南', '西北', '东北']
        const channels = ['官网直销', '电商平台', '代理商', '线下门店', '企业直销']
        
        return {
            totalOrders: mockUtils.randomBetween(15000, 25000),
            totalRevenue: mockUtils.randomBetween(80000000, 150000000),
            averageOrderValue: mockUtils.randomBetween(5000, 12000),
            conversionRate: mockUtils.randomFloat(3, 8),
            topProducts: products.map((product, index) => ({
                productId: index + 1,
                productName: product,
                salesCount: mockUtils.randomBetween(1000, 5000),
                revenue: mockUtils.randomBetween(8000000, 25000000),
                growthRate: mockUtils.randomFloat(-5, 30)
            })),
            regionDistribution: regions.map(region => ({
                region,
                salesCount: mockUtils.randomBetween(1500, 4000),
                revenue: mockUtils.randomBetween(10000000, 25000000),
                growthRate: mockUtils.randomFloat(-2, 25)
            })),
            channelPerformance: channels.map(channel => ({
                channel,
                salesCount: mockUtils.randomBetween(2000, 6000),
                revenue: mockUtils.randomBetween(15000000, 35000000),
                conversionRate: mockUtils.randomFloat(2, 10)
            })),
            monthlyTrends: Array.from({ length: 12 }, (_, i) => ({
                month: `2024-${String(i + 1).padStart(2, '0')}`,
                salesCount: mockUtils.randomBetween(1000, 3000),
                revenue: mockUtils.randomBetween(8000000, 15000000),
                growthRate: mockUtils.randomFloat(-5, 20)
            })),
            quarterlyComparison: ['Q1', 'Q2', 'Q3', 'Q4'].map(quarter => ({
                quarter,
                salesCount: mockUtils.randomBetween(3000, 8000),
                revenue: mockUtils.randomBetween(25000000, 45000000),
                growthRate: mockUtils.randomFloat(0, 25)
            }))
        }
    }
    
    // 生成租赁统计
    static generateRentalStatistics(): RentalStatistics {
        const products = ['YX-Pro-2024', 'YX-Standard-2024', 'YX-Education-2024']
        const regions = ['华北', '华东', '华南', '华中', '西南']
        
        return {
            totalOrders: mockUtils.randomBetween(8000, 15000),
            totalRevenue: mockUtils.randomBetween(25000000, 45000000),
            averageRentalPeriod: mockUtils.randomBetween(30, 180),
            deviceUtilization: mockUtils.randomFloat(65, 85),
            topRentalProducts: products.map((product, index) => ({
                productId: index + 1,
                productName: product,
                rentalCount: mockUtils.randomBetween(800, 2500),
                revenue: mockUtils.randomBetween(5000000, 15000000),
                utilizationRate: mockUtils.randomFloat(60, 90)
            })),
            regionDistribution: regions.map(region => ({
                region,
                rentalCount: mockUtils.randomBetween(1000, 3000),
                revenue: mockUtils.randomBetween(4000000, 10000000),
                utilizationRate: mockUtils.randomFloat(55, 85)
            })),
            utilizationTrends: Array.from({ length: 30 }, (_, i) => ({
                date: new Date(Date.now() - (29 - i) * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
                utilizationRate: mockUtils.randomFloat(60, 85),
                activeDevices: mockUtils.randomBetween(3000, 8000),
                totalDevices: mockUtils.randomBetween(8000, 12000)
            })),
            revenueAnalysis: Array.from({ length: 12 }, (_, i) => ({
                period: `2024-${String(i + 1).padStart(2, '0')}`,
                revenue: mockUtils.randomBetween(2000000, 4000000),
                growthRate: mockUtils.randomFloat(-3, 15),
                averageRentalValue: mockUtils.randomBetween(2500, 4500)
            }))
        }
    }
    
    // 生成客户统计
    static generateCustomerStatistics(): CustomerStatistics {
        const segments = ['企业客户', '教育机构', '个人用户', '政府机构']
        
        return {
            totalCustomers: mockUtils.randomBetween(25000, 45000),
            activeCustomers: mockUtils.randomBetween(18000, 32000),
            newCustomers: mockUtils.randomBetween(2000, 5000),
            customerRetention: mockUtils.randomFloat(75, 90),
            customerSegments: segments.map(segment => ({
                segment,
                customerCount: mockUtils.randomBetween(3000, 12000),
                revenue: mockUtils.randomBetween(8000000, 25000000),
                averageOrderValue: mockUtils.randomBetween(4000, 10000)
            })),
            satisfactionScores: Array.from({ length: 12 }, (_, i) => ({
                period: `2024-${String(i + 1).padStart(2, '0')}`,
                score: mockUtils.randomFloat(4.2, 4.8),
                responseCount: mockUtils.randomBetween(500, 1500),
                trend: mockUtils.randomFloat(-0.2, 0.3)
            })),
            lifetimeValue: segments.map(segment => ({
                segment,
                averageValue: mockUtils.randomBetween(15000, 45000),
                retentionRate: mockUtils.randomFloat(70, 95),
                churnRate: mockUtils.randomFloat(5, 30)
            })),
            churnAnalysis: Array.from({ length: 12 }, (_, i) => ({
                period: `2024-${String(i + 1).padStart(2, '0')}`,
                churnRate: mockUtils.randomFloat(3, 12),
                churnedCustomers: mockUtils.randomBetween(200, 800),
                retainedCustomers: mockUtils.randomBetween(2000, 5000)
            }))
        }
    }
    
    // 生成收入统计
    static generateRevenueStatistics(): RevenueStatistics {
        const sources = ['产品销售', '设备租赁', '技术服务', '培训服务', '维护服务']
        
        return {
            totalRevenue: mockUtils.randomBetween(120000000, 200000000),
            recurringRevenue: mockUtils.randomBetween(40000000, 80000000),
            oneTimeRevenue: mockUtils.randomBetween(80000000, 120000000),
            revenueGrowth: mockUtils.randomFloat(8, 25),
            revenueBySource: sources.map(source => ({
                source,
                revenue: mockUtils.randomBetween(15000000, 50000000),
                percentage: mockUtils.randomFloat(15, 35),
                growthRate: mockUtils.randomFloat(5, 30)
            })),
            monthlyRecurring: Array.from({ length: 12 }, (_, i) => ({
                month: `2024-${String(i + 1).padStart(2, '0')}`,
                recurringRevenue: mockUtils.randomBetween(3000000, 7000000),
                newRevenue: mockUtils.randomBetween(500000, 1500000),
                churnedRevenue: mockUtils.randomBetween(200000, 800000)
            })),
            forecastData: Array.from({ length: 6 }, (_, i) => ({
                period: `2024-${String(new Date().getMonth() + i + 2).padStart(2, '0')}`,
                forecastRevenue: mockUtils.randomBetween(12000000, 18000000),
                confidence: mockUtils.randomFloat(75, 95),
                actualRevenue: i < 2 ? mockUtils.randomBetween(11000000, 17000000) : undefined
            })),
            profitMargins: sources.map(category => ({
                category,
                revenue: mockUtils.randomBetween(15000000, 40000000),
                cost: mockUtils.randomBetween(8000000, 25000000),
                profit: mockUtils.randomBetween(5000000, 18000000),
                margin: mockUtils.randomFloat(25, 45)
            }))
        }
    }
    
    // 生成完整统计数据
    static generateAllStatistics(): AllStatistics {
        return {
            overview: this.generateSystemOverview(),
            business: this.generateBusinessStatistics(),
            content: {
                products: {
                    totalProducts: mockUtils.randomBetween(25, 35),
                    publishedProducts: mockUtils.randomBetween(20, 30),
                    draftProducts: mockUtils.randomBetween(3, 8),
                    archivedProducts: mockUtils.randomBetween(2, 5),
                    totalReviews: mockUtils.randomBetween(5000, 15000),
                    averageRating: mockUtils.randomFloat(4.2, 4.8),
                    mediaFiles: mockUtils.randomBetween(500, 1200),
                    contentUpdates: mockUtils.randomBetween(50, 150)
                },
                charity: {
                    totalProjects: mockUtils.randomBetween(40, 60),
                    activeProjects: mockUtils.randomBetween(35, 50),
                    totalInstitutions: mockUtils.randomBetween(800, 1200),
                    totalActivities: mockUtils.randomBetween(200, 400),
                    totalVolunteers: mockUtils.randomBetween(500, 1000),
                    totalDonations: mockUtils.randomBetween(50000, 100000),
                    beneficiaries: mockUtils.randomBetween(150000, 250000),
                    mediaContent: mockUtils.randomBetween(2000, 5000)
                },
                platforms: {
                    totalLinks: mockUtils.randomBetween(50, 80),
                    activeLinks: mockUtils.randomBetween(40, 65),
                    inactiveLinks: mockUtils.randomBetween(5, 15),
                    totalClicks: mockUtils.randomBetween(100000, 500000),
                    conversionRate: mockUtils.randomFloat(3, 8),
                    regionCoverage: mockUtils.randomBetween(15, 25),
                    languageSupport: mockUtils.randomBetween(8, 15),
                    performanceScore: mockUtils.randomFloat(85, 95)
                },
                media: {
                    totalImages: mockUtils.randomBetween(5000, 15000),
                    totalVideos: mockUtils.randomBetween(200, 800),
                    totalDocuments: mockUtils.randomBetween(1000, 3000),
                    storageUsed: mockUtils.randomBetween(500, 2000),
                    bandwidth: mockUtils.randomBetween(1000, 5000),
                    compressionRatio: mockUtils.randomFloat(60, 85),
                    deliverySpeed: mockUtils.randomFloat(95, 99),
                    cacheHitRate: mockUtils.randomFloat(85, 95)
                }
            },
            systemManagement: {
                courses: {
                    totalCourses: mockUtils.randomBetween(80, 120),
                    publishedCourses: mockUtils.randomBetween(70, 100),
                    totalCharacters: mockUtils.randomBetween(5000, 8000),
                    totalTranslations: mockUtils.randomBetween(25000, 45000),
                    completionRate: mockUtils.randomFloat(75, 90),
                    averageProgress: mockUtils.randomFloat(65, 85),
                    popularCourses: Array.from({ length: 5 }, (_, i) => ({
                        courseId: i + 1,
                        courseName: `热门课程${i + 1}`,
                        enrollments: mockUtils.randomBetween(1000, 5000),
                        completionRate: mockUtils.randomFloat(70, 95),
                        rating: mockUtils.randomFloat(4.2, 4.9)
                    })),
                    difficultyDistribution: ['初级', '中级', '高级', '专家级'].map(difficulty => ({
                        difficulty,
                        courseCount: mockUtils.randomBetween(15, 35),
                        enrollmentCount: mockUtils.randomBetween(2000, 8000),
                        completionRate: mockUtils.randomFloat(60, 90)
                    }))
                },
                fontPackages: {
                    totalPackages: mockUtils.randomBetween(40, 60),
                    completedPackages: mockUtils.randomBetween(35, 50),
                    inProgressPackages: mockUtils.randomBetween(3, 8),
                    totalSamples: mockUtils.randomBetween(2000, 5000),
                    averageAccuracy: mockUtils.randomFloat(88, 96),
                    generationTime: mockUtils.randomBetween(120, 300),
                    storageUsed: mockUtils.randomBetween(100, 500),
                    downloadCount: mockUtils.randomBetween(10000, 50000)
                },
                languages: {
                    supportedLanguages: mockUtils.randomBetween(10, 15),
                    totalTexts: mockUtils.randomBetween(5000, 10000),
                    translatedTexts: mockUtils.randomBetween(4000, 8500),
                    translationCompleteness: mockUtils.randomFloat(75, 92),
                    qualityScore: mockUtils.randomFloat(85, 95),
                    pendingTranslations: mockUtils.randomBetween(200, 800),
                    recentUpdates: mockUtils.randomBetween(50, 200)
                },
                devices: {
                    totalDevices: mockUtils.randomBetween(8000, 15000),
                    onlineDevices: mockUtils.randomBetween(6000, 12000),
                    offlineDevices: mockUtils.randomBetween(1000, 2500),
                    maintenanceDevices: mockUtils.randomBetween(200, 800),
                    errorDevices: mockUtils.randomBetween(50, 300),
                    averageUptime: mockUtils.randomFloat(95, 99),
                    totalAlerts: mockUtils.randomBetween(500, 2000),
                    resolvedAlerts: mockUtils.randomBetween(400, 1800),
                    firmwareVersions: Array.from({ length: 5 }, (_, i) => ({
                        version: `v${i + 1}.${mockUtils.randomBetween(0, 9)}.${mockUtils.randomBetween(0, 9)}`,
                        deviceCount: mockUtils.randomBetween(500, 3000),
                        releaseDate: mockUtils.generateRandomDate(365),
                        updateRate: mockUtils.randomFloat(60, 95)
                    }))
                },
                infrastructure: {
                    serverUptime: mockUtils.randomFloat(99.5, 99.99),
                    databaseSize: mockUtils.randomBetween(500, 2000),
                    apiResponseTime: mockUtils.randomBetween(50, 200),
                    errorRate: mockUtils.randomFloat(0.1, 2),
                    throughput: mockUtils.randomBetween(1000, 5000),
                    memoryUsage: mockUtils.randomFloat(60, 85),
                    cpuUsage: mockUtils.randomFloat(40, 75),
                    diskUsage: mockUtils.randomFloat(50, 80)
                }
            },
            website: {
                traffic: {
                    totalVisitors: mockUtils.randomBetween(50000, 150000),
                    uniqueVisitors: mockUtils.randomBetween(35000, 100000),
                    pageViews: mockUtils.randomBetween(200000, 600000),
                    bounceRate: mockUtils.randomFloat(25, 45),
                    averageSessionDuration: mockUtils.randomBetween(180, 480),
                    trafficSources: ['直接访问', '搜索引擎', '社交媒体', '推荐链接', '广告投放'].map(source => ({
                        source,
                        visitors: mockUtils.randomBetween(5000, 25000),
                        percentage: mockUtils.randomFloat(10, 35),
                        conversionRate: mockUtils.randomFloat(2, 8)
                    })),
                    deviceTypes: ['桌面电脑', '移动设备', '平板电脑'].map(device => ({
                        device,
                        visitors: mockUtils.randomBetween(10000, 50000),
                        percentage: mockUtils.randomFloat(20, 60),
                        bounceRate: mockUtils.randomFloat(20, 50)
                    })),
                    geographicData: ['中国', '美国', '日本', '韩国', '德国'].map(country => ({
                        country,
                        visitors: mockUtils.randomBetween(5000, 30000),
                        percentage: mockUtils.randomFloat(8, 40),
                        averageSessionDuration: mockUtils.randomBetween(150, 400)
                    }))
                },
                engagement: {
                    averageTimeOnSite: mockUtils.randomBetween(240, 420),
                    pagesPerSession: mockUtils.randomFloat(2.5, 5.2),
                    returnVisitorRate: mockUtils.randomFloat(35, 55),
                    socialShares: mockUtils.randomBetween(2000, 8000),
                    commentCount: mockUtils.randomBetween(500, 2000),
                    newsletterSignups: mockUtils.randomBetween(1000, 5000),
                    downloadCount: mockUtils.randomBetween(3000, 12000),
                    contactFormSubmissions: mockUtils.randomBetween(800, 3000)
                },
                conversion: {
                    overallConversionRate: mockUtils.randomFloat(3, 8),
                    goalCompletions: mockUtils.randomBetween(2000, 8000),
                    ecommerceConversions: mockUtils.randomBetween(1500, 6000),
                    leadGeneration: mockUtils.randomBetween(1000, 4000),
                    conversionFunnels: ['访问首页', '浏览产品', '添加购物车', '结算支付'].map((step, index) => ({
                        step,
                        visitors: mockUtils.randomBetween(10000 - index * 2000, 50000 - index * 8000),
                        conversionRate: mockUtils.randomFloat(15 - index * 3, 85 - index * 15),
                        dropOffRate: mockUtils.randomFloat(10 + index * 5, 40 + index * 10)
                    })),
                    abTestResults: Array.from({ length: 3 }, (_, i) => ({
                        testName: `A/B测试${i + 1}`,
                        variant: Math.random() > 0.5 ? 'A' : 'B',
                        conversionRate: mockUtils.randomFloat(3, 8),
                        confidence: mockUtils.randomFloat(85, 99),
                        winner: Math.random() > 0.5
                    }))
                },
                content: {
                    totalPages: mockUtils.randomBetween(100, 300),
                    publishedPages: mockUtils.randomBetween(80, 250),
                    blogPosts: mockUtils.randomBetween(50, 150),
                    mediaFiles: mockUtils.randomBetween(1000, 3000),
                    searchQueries: mockUtils.randomBetween(5000, 20000),
                    popularContent: Array.from({ length: 5 }, (_, i) => ({
                        title: `热门内容${i + 1}`,
                        url: `/content/${i + 1}`,
                        views: mockUtils.randomBetween(5000, 25000),
                        shares: mockUtils.randomBetween(200, 1500),
                        engagementRate: mockUtils.randomFloat(15, 35)
                    })),
                    contentPerformance: ['产品介绍', '技术文档', '用户案例', '新闻资讯'].map(contentType => ({
                        contentType,
                        totalCount: mockUtils.randomBetween(20, 80),
                        averageViews: mockUtils.randomBetween(1000, 8000),
                        engagementRate: mockUtils.randomFloat(10, 30)
                    }))
                }
            },
            performance: {
                system: {
                    uptime: mockUtils.randomFloat(99.5, 99.99),
                    responseTime: mockUtils.randomBetween(50, 200),
                    throughput: mockUtils.randomBetween(1000, 5000),
                    errorRate: mockUtils.randomFloat(0.1, 2),
                    memoryUsage: mockUtils.randomFloat(60, 85),
                    cpuUsage: mockUtils.randomFloat(40, 75),
                    diskUsage: mockUtils.randomFloat(50, 80),
                    networkLatency: mockUtils.randomBetween(10, 50)
                },
                api: {
                    totalRequests: mockUtils.randomBetween(1000000, 5000000),
                    averageResponseTime: mockUtils.randomBetween(80, 250),
                    errorRate: mockUtils.randomFloat(0.5, 3),
                    slowestEndpoints: Array.from({ length: 5 }, (_, i) => ({
                        endpoint: `/api/endpoint${i + 1}`,
                        averageResponseTime: mockUtils.randomBetween(200, 800),
                        requestCount: mockUtils.randomBetween(10000, 100000),
                        errorRate: mockUtils.randomFloat(1, 8)
                    })),
                    requestsByEndpoint: Array.from({ length: 10 }, (_, i) => ({
                        endpoint: `/api/endpoint${i + 1}`,
                        requestCount: mockUtils.randomBetween(50000, 500000),
                        averageResponseTime: mockUtils.randomBetween(50, 300),
                        successRate: mockUtils.randomFloat(92, 99)
                    })),
                    statusCodeDistribution: [
                        { statusCode: 200, count: mockUtils.randomBetween(800000, 4000000), percentage: mockUtils.randomFloat(80, 95) },
                        { statusCode: 404, count: mockUtils.randomBetween(10000, 100000), percentage: mockUtils.randomFloat(2, 8) },
                        { statusCode: 500, count: mockUtils.randomBetween(5000, 50000), percentage: mockUtils.randomFloat(1, 5) },
                        { statusCode: 401, count: mockUtils.randomBetween(3000, 30000), percentage: mockUtils.randomFloat(0.5, 3) }
                    ]
                },
                database: {
                    queryCount: mockUtils.randomBetween(5000000, 20000000),
                    averageQueryTime: mockUtils.randomBetween(10, 100),
                    slowQueries: mockUtils.randomBetween(100, 1000),
                    connectionCount: mockUtils.randomBetween(50, 200),
                    cacheHitRate: mockUtils.randomFloat(85, 95),
                    indexEfficiency: mockUtils.randomFloat(90, 98),
                    storageUsed: mockUtils.randomBetween(500, 2000),
                    backupStatus: '正常'
                },
                frontend: {
                    pageLoadTime: mockUtils.randomBetween(800, 2500),
                    firstContentfulPaint: mockUtils.randomBetween(500, 1500),
                    largestContentfulPaint: mockUtils.randomBetween(1000, 3000),
                    cumulativeLayoutShift: mockUtils.randomFloat(0.05, 0.25),
                    firstInputDelay: mockUtils.randomBetween(50, 200),
                    jsErrorRate: mockUtils.randomFloat(0.1, 2),
                    resourceLoadTime: mockUtils.randomBetween(200, 800),
                    cacheEfficiency: mockUtils.randomFloat(80, 95)
                }
            },
            trends: {
                daily: Array.from({ length: 30 }, (_, i) => ({
                    date: new Date(Date.now() - (29 - i) * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
                    users: mockUtils.randomBetween(1000, 5000),
                    revenue: mockUtils.randomBetween(100000, 500000),
                    orders: mockUtils.randomBetween(50, 300),
                    devices: mockUtils.randomBetween(8000, 12000),
                    satisfaction: mockUtils.randomFloat(4.2, 4.8)
                })),
                weekly: Array.from({ length: 12 }, (_, i) => ({
                    week: `2024-W${String(i + 1).padStart(2, '0')}`,
                    users: mockUtils.randomBetween(8000, 25000),
                    revenue: mockUtils.randomBetween(800000, 2500000),
                    orders: mockUtils.randomBetween(400, 1500),
                    growthRate: mockUtils.randomFloat(-5, 20),
                    comparison: mockUtils.randomFloat(-10, 25)
                })),
                monthly: Array.from({ length: 12 }, (_, i) => ({
                    month: `2024-${String(i + 1).padStart(2, '0')}`,
                    users: mockUtils.randomBetween(25000, 80000),
                    revenue: mockUtils.randomBetween(3000000, 12000000),
                    orders: mockUtils.randomBetween(1500, 6000),
                    growthRate: mockUtils.randomFloat(-3, 25),
                    yearOverYear: mockUtils.randomFloat(5, 35)
                })),
                quarterly: ['Q1', 'Q2', 'Q3', 'Q4'].map(quarter => ({
                    quarter,
                    users: mockUtils.randomBetween(80000, 200000),
                    revenue: mockUtils.randomBetween(12000000, 35000000),
                    orders: mockUtils.randomBetween(5000, 18000),
                    growthRate: mockUtils.randomFloat(0, 30),
                    yearOverYear: mockUtils.randomFloat(8, 40)
                })),
                yearly: Array.from({ length: 3 }, (_, i) => ({
                    year: String(2022 + i),
                    users: mockUtils.randomBetween(200000, 600000),
                    revenue: mockUtils.randomBetween(50000000, 150000000),
                    orders: mockUtils.randomBetween(20000, 80000),
                    growthRate: mockUtils.randomFloat(10, 50),
                    marketShare: mockUtils.randomFloat(5, 25)
                })),
                forecasts: Array.from({ length: 6 }, (_, i) => ({
                    period: `2024-${String(new Date().getMonth() + i + 2).padStart(2, '0')}`,
                    metric: ['用户数', '收入', '订单数'][i % 3],
                    forecast: mockUtils.randomBetween(50000, 200000),
                    confidence: mockUtils.randomFloat(75, 95),
                    trend: ['up', 'down', 'stable'][mockUtils.randomBetween(0, 2)] as 'up' | 'down' | 'stable'
                })),
                seasonality: Array.from({ length: 12 }, (_, i) => ({
                    period: `${i + 1}月`,
                    metric: '销售额',
                    seasonalIndex: mockUtils.randomFloat(0.8, 1.3),
                    trend: mockUtils.randomFloat(0.95, 1.1),
                    cyclical: mockUtils.randomFloat(0.9, 1.15)
                }))
            }
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
export const mockDataStatisticsAPI = {
    // 获取系统概览统计
    async getSystemOverview() {
        await mockDelay()
        const overview = DataStatisticsGenerator.generateSystemOverview()
        return createMockResponse(overview)
    },

    // 获取业务统计数据
    async getBusinessStatistics() {
        await mockDelay()
        const business = DataStatisticsGenerator.generateBusinessStatistics()
        return createMockResponse(business)
    },

    // 获取内容统计数据
    async getContentStatistics() {
        await mockDelay()
        const allStats = DataStatisticsGenerator.generateAllStatistics()
        return createMockResponse(allStats.content)
    },

    // 获取系统管理统计数据
    async getSystemStatistics() {
        await mockDelay()
        const allStats = DataStatisticsGenerator.generateAllStatistics()
        return createMockResponse(allStats.systemManagement)
    },

    // 获取网站统计数据
    async getWebsiteStatistics() {
        await mockDelay()
        const allStats = DataStatisticsGenerator.generateAllStatistics()
        return createMockResponse(allStats.website)
    },

    // 获取性能统计数据
    async getPerformanceStatistics() {
        await mockDelay()
        const allStats = DataStatisticsGenerator.generateAllStatistics()
        return createMockResponse(allStats.performance)
    },

    // 获取趋势统计数据
    async getTrendStatistics() {
        await mockDelay()
        const allStats = DataStatisticsGenerator.generateAllStatistics()
        return createMockResponse(allStats.trends)
    },

    // 获取所有统计数据
    async getAllStatistics() {
        await mockDelay()
        const allStats = DataStatisticsGenerator.generateAllStatistics()
        return createMockResponse(allStats)
    }
}