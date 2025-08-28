/**
 * 平台链接管理Mock数据
 * 提供电商平台和租赁平台链接管理的模拟数据
 */

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
  priority: number
  clickCount: number
  conversionCount: number
  conversionRate: number
  lastCheckedAt: string
  linkStatus: 'active' | 'inactive' | 'error' | 'checking'
  errorMessage?: string
  createdAt: string
  updatedAt: string
  createdBy: string
  tags: string[]
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
    name: string
    clicks: number
    conversions: number
  }>
  regionStats: Array<{
    region: string
    linkCount: number
    clicks: number
    conversions: number
  }>
  languageStats: Array<{
    language: string
    linkCount: number
    clicks: number
    conversions: number
  }>
}

export interface RegionConfig {
  region: string
  countries: Array<{
    name: string
    code: string
  }>
  languages: Array<{
    name: string
    code: string
  }>
}

export interface LinkAnalytics {
  linkId: number
  date: string
  clicks: number
  conversions: number
  revenue: number
  conversionRate: number
  averageOrderValue: number
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
export class PlatformLinkMockService {
  private static ecommercePlatforms = [
    { name: '淘宝', platform: 'taobao', baseUrl: 'https://item.taobao.com' },
    { name: '天猫', platform: 'tmall', baseUrl: 'https://detail.tmall.com' },
    { name: '京东', platform: 'jd', baseUrl: 'https://item.jd.com' },
    { name: 'Amazon US', platform: 'amazon-us', baseUrl: 'https://www.amazon.com' },
    { name: 'Amazon Japan', platform: 'amazon-jp', baseUrl: 'https://www.amazon.co.jp' },
    { name: 'Amazon Germany', platform: 'amazon-de', baseUrl: 'https://www.amazon.de' },
    { name: 'eBay', platform: 'ebay', baseUrl: 'https://www.ebay.com' },
    { name: '拼多多', platform: 'pdd', baseUrl: 'https://mobile.yangkeduo.com' },
    { name: '苏宁易购', platform: 'suning', baseUrl: 'https://product.suning.com' },
    { name: '国美在线', platform: 'gome', baseUrl: 'https://item.gome.com.cn' }
  ]

  private static rentalPlatforms = [
    { name: '租赁宝', platform: 'zulinbao', baseUrl: 'https://www.zulinbao.com' },
    { name: '设备租', platform: 'shebeizu', baseUrl: 'https://www.shebeizu.com' },
    { name: '智能租赁', platform: 'smartrental', baseUrl: 'https://www.smartrental.com' },
    { name: '机器人租赁平台', platform: 'robotrental', baseUrl: 'https://www.robotrental.com' },
    { name: '科技设备租', platform: 'techrental', baseUrl: 'https://www.techrental.com' },
    { name: '教育设备租赁', platform: 'edurental', baseUrl: 'https://www.edurental.com' }
  ]

  private static regions = [
    { name: '中国大陆', code: 'CN', country: '中国', countryCode: 'CN', language: '简体中文', languageCode: 'zh-CN' },
    { name: '中国台湾', code: 'TW', country: '中国台湾', countryCode: 'TW', language: '繁体中文', languageCode: 'zh-TW' },
    { name: '美国', code: 'US', country: '美国', countryCode: 'US', language: '英语', languageCode: 'en-US' },
    { name: '日本', code: 'JP', country: '日本', countryCode: 'JP', language: '日语', languageCode: 'ja-JP' },
    { name: '韩国', code: 'KR', country: '韩国', countryCode: 'KR', language: '韩语', languageCode: 'ko-KR' },
    { name: '德国', code: 'DE', country: '德国', countryCode: 'DE', language: '德语', languageCode: 'de-DE' },
    { name: '法国', code: 'FR', country: '法国', countryCode: 'FR', language: '法语', languageCode: 'fr-FR' },
    { name: '英国', code: 'GB', country: '英国', countryCode: 'GB', language: '英语', languageCode: 'en-GB' },
    { name: '新加坡', code: 'SG', country: '新加坡', countryCode: 'SG', language: '英语', languageCode: 'en-SG' },
    { name: '澳大利亚', code: 'AU', country: '澳大利亚', countryCode: 'AU', language: '英语', languageCode: 'en-AU' }
  ]

  private static creators = [
    '张运营', '李市场', '王商务', '陈推广', '刘合作'
  ]

  private static tags = [
    '热门', '推荐', '新增', '高转化', '重点', '测试', '优质', '合作'
  ]

  private static statuses: ('active' | 'inactive' | 'error' | 'checking')[] = ['active', 'inactive', 'error', 'checking']

  // 生成平台链接数据
  static generatePlatformLinks(count: number = 80): PlatformLink[] {
    const links: PlatformLink[] = []

    // 预定义一些高质量的测试数据
    const predefinedLinks = [
      {
        platformName: '淘宝 - 中国大陆',
        platformType: 'ecommerce' as const,
        region: '中国大陆',
        country: '中国',
        languageCode: 'zh-CN',
        languageName: '简体中文',
        clickCount: 15680,
        conversionCount: 1245,
        linkStatus: 'active' as const,
        priority: 10,
        tags: ['热门', '推荐', '高转化']
      },
      {
        platformName: 'Amazon US - 美国',
        platformType: 'ecommerce' as const,
        region: '美国',
        country: '美国',
        languageCode: 'en-US',
        languageName: '英语',
        clickCount: 12450,
        conversionCount: 890,
        linkStatus: 'active' as const,
        priority: 9,
        tags: ['推荐', '优质']
      },
      {
        platformName: '京东 - 中国大陆',
        platformType: 'ecommerce' as const,
        region: '中国大陆',
        country: '中国',
        languageCode: 'zh-CN',
        languageName: '简体中文',
        clickCount: 9870,
        conversionCount: 756,
        linkStatus: 'active' as const,
        priority: 8,
        tags: ['热门', '重点']
      },
      {
        platformName: 'Amazon Japan - 日本',
        platformType: 'ecommerce' as const,
        region: '日本',
        country: '日本',
        languageCode: 'ja-JP',
        languageName: '日语',
        clickCount: 8920,
        conversionCount: 623,
        linkStatus: 'active' as const,
        priority: 8,
        tags: ['推荐', '新增']
      },
      {
        platformName: '教育设备租赁 - 中国大陆',
        platformType: 'rental' as const,
        region: '中国大陆',
        country: '中国',
        languageCode: 'zh-CN',
        languageName: '简体中文',
        clickCount: 5680,
        conversionCount: 445,
        linkStatus: 'active' as const,
        priority: 7,
        tags: ['租赁', '教育', '合作']
      },
      {
        platformName: 'EduRent - 美国',
        platformType: 'rental' as const,
        region: '美国',
        country: '美国',
        languageCode: 'en-US',
        languageName: '英语',
        clickCount: 4320,
        conversionCount: 298,
        linkStatus: 'active' as const,
        priority: 6,
        tags: ['租赁', '教育']
      }
    ]

    // 添加预定义的高质量数据
    predefinedLinks.forEach((predefined, index) => {
      const platform = this.ecommercePlatforms.find(p => predefined.platformName.includes(p.name)) ||
                      this.rentalPlatforms.find(p => predefined.platformName.includes(p.name)) ||
                      this.ecommercePlatforms[0]
      
      links.push({
        id: index + 1,
        platformName: predefined.platformName,
        platformType: predefined.platformType,
        linkUrl: `${platform.baseUrl}/product/${randomBetween(100000, 999999)}`,
        region: predefined.region,
        country: predefined.country,
        languageCode: predefined.languageCode,
        languageName: predefined.languageName,
        isEnabled: true,
        priority: predefined.priority,
        clickCount: predefined.clickCount,
        conversionCount: predefined.conversionCount,
        conversionRate: randomFloat(5, 12),
        lastCheckedAt: generateRandomDate(3),
        linkStatus: predefined.linkStatus,
        errorMessage: undefined,
        createdAt: generateRandomDate(180),
        updatedAt: generateRandomDate(7),
        createdBy: this.creators[randomBetween(0, this.creators.length - 1)],
        tags: predefined.tags
      })
    })

    // 生成其余的随机数据
    for (let i = predefinedLinks.length + 1; i <= count; i++) {
      const isEcommerce = Math.random() > 0.35
      const platforms = isEcommerce ? this.ecommercePlatforms : this.rentalPlatforms
      const platform = platforms[randomBetween(0, platforms.length - 1)]
      const region = this.regions[randomBetween(0, this.regions.length - 1)]
      
      // 80%的链接是活跃状态，提高数据质量
      const statusWeights = [
        { status: 'active', weight: 0.75 },
        { status: 'inactive', weight: 0.15 },
        { status: 'checking', weight: 0.08 },
        { status: 'error', weight: 0.02 }
      ]
      
      let random = Math.random()
      let status: 'active' | 'inactive' | 'error' | 'checking' = 'active'
      
      for (const { status: s, weight } of statusWeights) {
        if (random < weight) {
          status = s as any
          break
        }
        random -= weight
      }
      
      const tagCount = randomBetween(1, 3)
      const selectedTags: string[] = []
      for (let j = 0; j < tagCount; j++) {
        const tag = this.tags[randomBetween(0, this.tags.length - 1)]
        if (!selectedTags.includes(tag)) {
          selectedTags.push(tag)
        }
      }

      const clickCount = randomBetween(50, 8000)
      const conversionCount = Math.floor(clickCount * randomFloat(0.02, 0.12))
      const conversionRate = randomFloat(2, 10)

      links.push({
        id: i,
        platformName: `${platform.name} - ${region.name}`,
        platformType: isEcommerce ? 'ecommerce' : 'rental',
        linkUrl: `${platform.baseUrl}/product/${randomBetween(100000, 999999)}`,
        region: region.name,
        country: region.country,
        languageCode: region.languageCode,
        languageName: region.language,
        isEnabled: Math.random() > 0.15, // 85%的链接是启用的
        priority: randomBetween(1, 10),
        clickCount,
        conversionCount,
        conversionRate,
        lastCheckedAt: generateRandomDate(14),
        linkStatus: status,
        errorMessage: status === 'error' ? '链接无法访问，返回404错误' : undefined,
        createdAt: generateRandomDate(365),
        updatedAt: generateRandomDate(30),
        createdBy: this.creators[randomBetween(0, this.creators.length - 1)],
        tags: selectedTags
      })
    }

    return links.sort((a, b) => b.priority - a.priority)
  }

  // 生成平台链接统计数据
  static generatePlatformLinkStats(): PlatformLinkStats {
    const links = this.generatePlatformLinks(80)
    const totalLinks = links.length
    const activeLinks = links.filter(link => link.linkStatus === 'active' && link.isEnabled).length
    const totalClicks = links.reduce((sum, link) => sum + link.clickCount, 0)
    const totalConversions = links.reduce((sum, link) => sum + link.conversionCount, 0)
    const conversionRate = totalClicks > 0 ? (totalConversions / totalClicks) * 100 : 0

    // 生成顶级表现链接
    const topPerformingLinks = links
      .sort((a, b) => b.conversionCount - a.conversionCount)
      .slice(0, 5)
      .map(link => ({
        id: link.id,
        name: link.platformName,
        clicks: link.clickCount,
        conversions: link.conversionCount
      }))

    // 生成地区统计
    const regionMap = new Map<string, { linkCount: number; clicks: number; conversions: number }>()
    links.forEach(link => {
      const existing = regionMap.get(link.region) || { linkCount: 0, clicks: 0, conversions: 0 }
      regionMap.set(link.region, {
        linkCount: existing.linkCount + 1,
        clicks: existing.clicks + link.clickCount,
        conversions: existing.conversions + link.conversionCount
      })
    })

    const regionStats = Array.from(regionMap.entries()).map(([region, stats]) => ({
      region,
      ...stats
    }))

    // 生成语言统计
    const languageMap = new Map<string, { linkCount: number; clicks: number; conversions: number }>()
    links.forEach(link => {
      const existing = languageMap.get(link.languageName) || { linkCount: 0, clicks: 0, conversions: 0 }
      languageMap.set(link.languageName, {
        linkCount: existing.linkCount + 1,
        clicks: existing.clicks + link.clickCount,
        conversions: existing.conversions + link.conversionCount
      })
    })

    const languageStats = Array.from(languageMap.entries()).map(([language, stats]) => ({
      language,
      ...stats
    }))

    return {
      totalLinks,
      activeLinks,
      inactiveLinks: totalLinks - activeLinks,
      totalClicks,
      totalConversions,
      conversionRate: Math.round(conversionRate * 100) / 100,
      topPerformingLinks,
      regionStats,
      languageStats
    }
  }

  // 生成地区配置数据
  static generateRegionConfigs(): RegionConfig[] {
    const regionGroups = [
      {
        region: '亚太地区',
        countries: [
          { name: '中国', code: 'CN' },
          { name: '日本', code: 'JP' },
          { name: '韩国', code: 'KR' },
          { name: '新加坡', code: 'SG' },
          { name: '澳大利亚', code: 'AU' }
        ],
        languages: [
          { name: '简体中文', code: 'zh-CN' },
          { name: '繁体中文', code: 'zh-TW' },
          { name: '日语', code: 'ja-JP' },
          { name: '韩语', code: 'ko-KR' },
          { name: '英语', code: 'en' }
        ]
      },
      {
        region: '欧洲地区',
        countries: [
          { name: '德国', code: 'DE' },
          { name: '法国', code: 'FR' },
          { name: '英国', code: 'GB' },
          { name: '意大利', code: 'IT' },
          { name: '西班牙', code: 'ES' }
        ],
        languages: [
          { name: '德语', code: 'de-DE' },
          { name: '法语', code: 'fr-FR' },
          { name: '英语', code: 'en-GB' },
          { name: '意大利语', code: 'it-IT' },
          { name: '西班牙语', code: 'es-ES' }
        ]
      },
      {
        region: '北美地区',
        countries: [
          { name: '美国', code: 'US' },
          { name: '加拿大', code: 'CA' },
          { name: '墨西哥', code: 'MX' }
        ],
        languages: [
          { name: '英语', code: 'en-US' },
          { name: '法语', code: 'fr-CA' },
          { name: '西班牙语', code: 'es-MX' }
        ]
      }
    ]

    return regionGroups
  }

  // 生成链接分析数据
  static generateLinkAnalytics(linkId: number, days: number = 30): LinkAnalytics[] {
    const analytics: LinkAnalytics[] = []
    const today = new Date()

    for (let i = days - 1; i >= 0; i--) {
      const date = new Date(today)
      date.setDate(date.getDate() - i)
      
      const clicks = randomBetween(10, 200)
      const conversions = Math.floor(clicks * randomFloat(0.01, 0.08))
      const averageOrderValue = randomFloat(5000, 15000)
      const revenue = conversions * averageOrderValue
      const conversionRate = conversions / clicks * 100

      analytics.push({
        linkId,
        date: date.toISOString().slice(0, 10),
        clicks,
        conversions,
        revenue: Math.round(revenue),
        conversionRate: Math.round(conversionRate * 100) / 100,
        averageOrderValue: Math.round(averageOrderValue)
      })
    }

    return analytics
  }
}

// 模拟API延迟
const mockDelay = (ms: number = 200): Promise<void> => {
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
export const mockPlatformLinkAPI = {
  // 获取平台链接列表
  async getPlatformLinks(params: any) {
    console.log('🔗 Mock API: 获取平台链接列表', params)
    await mockDelay()
    let data = PlatformLinkMockService.generatePlatformLinks(80)
    
    // 应用筛选
    if (params?.platformType) {
      data = data.filter(item => item.platformType === params.platformType)
    }
    
    if (params?.region) {
      data = data.filter(item => item.region === params.region)
    }
    
    if (params?.languageCode) {
      data = data.filter(item => item.languageCode === params.languageCode)
    }
    
    if (params?.linkStatus) {
      data = data.filter(item => item.linkStatus === params.linkStatus)
    }
    
    if (params?.isEnabled !== undefined) {
      data = data.filter(item => item.isEnabled === params.isEnabled)
    }
    
    if (params?.search) {
      data = data.filter(item => 
        item.platformName.includes(params.search) ||
        item.linkUrl.includes(params.search) ||
        item.tags.some(tag => tag.includes(params.search))
      )
    }
    
    // 排序
    if (params?.sortBy) {
      data.sort((a, b) => {
        const aVal = a[params.sortBy as keyof PlatformLink]
        const bVal = b[params.sortBy as keyof PlatformLink]
        
        if (typeof aVal === 'number' && typeof bVal === 'number') {
          return params.sortOrder === 'asc' ? aVal - bVal : bVal - aVal
        }
        
        if (typeof aVal === 'string' && typeof bVal === 'string') {
          return params.sortOrder === 'asc' ? 
            aVal.localeCompare(bVal) : bVal.localeCompare(aVal)
        }
        
        return 0
      })
    }
    
    // 分页
    const page = params?.page || 1
    const pageSize = params?.pageSize || 20
    const total = data.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = data.slice(start, end)
    
    console.log('🔗 Mock API: 返回数据', { total, page, pageSize, listLength: list.length })
    
    return createMockResponse({
      list,
      total,
      page,
      pageSize,
      totalPages: Math.ceil(total / pageSize)
    })
  },

  // 获取平台链接统计数据
  async getPlatformLinkStats() {
    console.log('📊 Mock API: 获取平台链接统计数据')
    await mockDelay()
    const stats = PlatformLinkMockService.generatePlatformLinkStats()
    console.log('📊 Mock API: 统计数据', stats)
    return createMockResponse(stats)
  },

  // 获取地区配置
  async getRegionConfigs() {
    console.log('🌍 Mock API: 获取地区配置')
    await mockDelay()
    const configs = PlatformLinkMockService.generateRegionConfigs()
    console.log('🌍 Mock API: 地区配置', configs)
    return createMockResponse(configs)
  },

  // 获取平台链接详情
  async getPlatformLinkDetail(params: any) {
    const linkId = parseInt(params.id || params.linkId)
    console.log('🔍 Mock API: 获取平台链接详情', linkId)
    await mockDelay()
    const links = PlatformLinkMockService.generatePlatformLinks(80)
    const link = links.find(l => l.id === linkId)
    
    if (!link) {
      return createMockResponse(null, 404, '链接不存在')
    }
    
    return createMockResponse(link)
  },

  // 创建平台链接
  async createPlatformLink(params: any) {
    console.log('➕ Mock API: 创建平台链接', params)
    await mockDelay()
    const newLink = {
      id: Date.now(),
      ...params,
      clickCount: 0,
      conversionCount: 0,
      conversionRate: 0,
      lastCheckedAt: new Date().toISOString(),
      linkStatus: 'checking' as const,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      createdBy: '当前用户',
      tags: []
    }
    return createMockResponse(newLink)
  },

  // 更新平台链接
  async updatePlatformLink(params: any) {
    const linkId = parseInt(params.id || params.linkId)
    console.log('✏️ Mock API: 更新平台链接', linkId, params)
    await mockDelay()
    const updatedLink = {
      id: linkId,
      ...params,
      updatedAt: new Date().toISOString()
    }
    return createMockResponse(updatedLink)
  },

  // 删除平台链接
  async deletePlatformLink(params: any) {
    const linkId = parseInt(params.id || params.linkId)
    console.log('🗑️ Mock API: 删除平台链接', linkId)
    await mockDelay()
    return createMockResponse({ deleted: true })
  },

  // 批量删除平台链接
  async batchDeletePlatformLinks(params: any) {
    const linkIds = params.ids || params.linkIds || []
    console.log('🗑️ Mock API: 批量删除平台链接', linkIds)
    await mockDelay()
    return createMockResponse({
      deletedCount: linkIds.length,
      deletedIds: linkIds
    })
  },

  // 批量更新平台链接
  async batchUpdatePlatformLinks(params: any) {
    const linkIds = params.linkIds || []
    console.log('✏️ Mock API: 批量更新平台链接', params)
    await mockDelay()
    return createMockResponse({
      updatedCount: linkIds.length,
      updatedIds: linkIds
    })
  },

  // 验证平台链接
  async validatePlatformLink(params: any) {
    const linkId = parseInt(params.id || params.linkId)
    console.log('✅ Mock API: 验证平台链接', linkId)
    await mockDelay(1000) // 模拟检查耗时
    const isValid = Math.random() > 0.2
    return createMockResponse({
      linkId,
      isValid,
      linkStatus: isValid ? 'active' : 'error',
      errorMessage: isValid ? undefined : '链接无法访问，返回404错误',
      checkedAt: new Date().toISOString()
    })
  },

  // 批量验证平台链接
  async batchValidatePlatformLinks(params: any) {
    const linkIds = params.ids || params.linkIds || []
    console.log('✅ Mock API: 批量验证平台链接', linkIds)
    await mockDelay(2000) // 模拟批量检查耗时
    const results = linkIds.map((linkId: number) => ({
      linkId,
      isValid: Math.random() > 0.2,
      linkStatus: Math.random() > 0.2 ? 'active' : 'error',
      errorMessage: Math.random() > 0.2 ? undefined : '链接无法访问',
      checkedAt: new Date().toISOString()
    }))
    
    return createMockResponse(results)
  },

  // 切换平台链接状态
  async togglePlatformLink(params: any) {
    const linkId = parseInt(params.id || params.linkId)
    const isEnabled = params.isEnabled
    console.log('🔄 Mock API: 切换平台链接状态', linkId, isEnabled)
    await mockDelay()
    return createMockResponse({
      linkId,
      isEnabled,
      updatedAt: new Date().toISOString()
    })
  },

  // 获取链接分析数据
  async getLinkAnalytics(params: any) {
    const linkId = parseInt(params.id || params.linkId)
    const days = params?.days || 30
    console.log('📈 Mock API: 获取链接分析数据', linkId, days)
    await mockDelay()
    const analytics = PlatformLinkMockService.generateLinkAnalytics(linkId, days)
    return createMockResponse(analytics)
  }
}