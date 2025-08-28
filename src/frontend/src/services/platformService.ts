/**
 * 平台推荐服务
 * 实现智能平台推荐算法，根据地理位置和语言偏好推荐合适的购买/租赁平台
 */

import { mockApiData } from '@/utils/mockData'

// 平台链接接口定义
export interface PlatformLink {
  id: number
  platformName: string
  platformUrl: string
  region: string
  language: string
  currency: string
  price?: number
  dailyRate?: number
  monthlyRate?: number
  isActive: boolean
  priority: number
  description: string
}

// 用户地理位置和语言偏好接口
export interface UserPreference {
  region?: string
  language?: string
  userAgent?: string
}

// 平台推荐结果接口
export interface PlatformRecommendation {
  recommended: PlatformLink[]
  alternatives: PlatformLink[]
  userPreference: UserPreference
}

/**
 * 智能平台推荐服务类
 */
export class PlatformService {
  
  /**
   * 获取用户地理位置和语言偏好
   */
  static async getUserPreference(): Promise<UserPreference> {
    const preference: UserPreference = {}
    
    // 1. 检测浏览器语言偏好
    const browserLanguage = navigator.language || navigator.languages?.[0] || 'zh-CN'
    preference.language = this.mapBrowserLanguageToCode(browserLanguage)
    
    // 2. 尝试通过IP获取地理位置（模拟实现）
    try {
      preference.region = await this.detectRegionByIP()
    } catch (error) {
      // 如果IP检测失败，根据语言推断地区
      preference.region = this.inferRegionFromLanguage(preference.language)
    }
    
    // 3. 获取用户代理信息
    preference.userAgent = navigator.userAgent
    
    return preference
  }
  
  /**
   * 将浏览器语言代码映射为系统语言代码
   */
  private static mapBrowserLanguageToCode(browserLang: string): string {
    const langMap: Record<string, string> = {
      'zh': 'zh',
      'zh-CN': 'zh',
      'zh-TW': 'zh',
      'zh-HK': 'zh',
      'en': 'en',
      'en-US': 'en',
      'en-GB': 'en',
      'ja': 'ja',
      'ja-JP': 'ja',
      'ko': 'ko',
      'ko-KR': 'ko',
      'es': 'es',
      'es-ES': 'es',
      'es-MX': 'es'
    }
    
    const langCode = browserLang.toLowerCase()
    return langMap[langCode] || langMap[langCode.split('-')[0]] || 'zh'
  }
  
  /**
   * 通过IP检测用户地理位置（模拟实现）
   */
  private static async detectRegionByIP(): Promise<string> {
    // 在实际项目中，这里会调用IP地理位置API
    // 这里使用模拟数据
    return new Promise((resolve) => {
      setTimeout(() => {
        // 模拟不同地区的IP检测结果
        const regions = ['CN', 'US', 'JP', 'KR', 'ES']
        const randomRegion = regions[Math.floor(Math.random() * regions.length)]
        resolve(randomRegion)
      }, 100)
    })
  }
  
  /**
   * 根据语言推断地区
   */
  private static inferRegionFromLanguage(language: string): string {
    const regionMap: Record<string, string> = {
      'zh': 'CN',
      'en': 'US',
      'ja': 'JP',
      'ko': 'KR',
      'es': 'ES'
    }
    
    return regionMap[language] || 'CN'
  }
  
  /**
   * 获取购买平台推荐
   */
  static async getPurchasePlatformRecommendation(): Promise<PlatformRecommendation> {
    const userPreference = await this.getUserPreference()
    const allPlatforms = mockApiData.platformLinks.purchase.filter(p => p.isActive)
    
    // 智能推荐算法
    const scored = allPlatforms.map(platform => ({
      platform,
      score: this.calculatePlatformScore(platform, userPreference)
    }))
    
    // 按分数排序
    scored.sort((a, b) => b.score - a.score)
    
    // 分离推荐和备选平台
    const recommended = scored.slice(0, 2).map(s => s.platform)
    const alternatives = scored.slice(2).map(s => s.platform)
    
    return {
      recommended,
      alternatives,
      userPreference
    }
  }
  
  /**
   * 获取租赁平台推荐
   */
  static async getRentalPlatformRecommendation(): Promise<PlatformRecommendation> {
    const userPreference = await this.getUserPreference()
    const allPlatforms = mockApiData.platformLinks.rental.filter(p => p.isActive)
    
    // 智能推荐算法
    const scored = allPlatforms.map(platform => ({
      platform,
      score: this.calculatePlatformScore(platform, userPreference)
    }))
    
    // 按分数排序
    scored.sort((a, b) => b.score - a.score)
    
    // 分离推荐和备选平台
    const recommended = scored.slice(0, 2).map(s => s.platform)
    const alternatives = scored.slice(2).map(s => s.platform)
    
    return {
      recommended,
      alternatives,
      userPreference
    }
  }
  
  /**
   * 计算平台推荐分数
   */
  private static calculatePlatformScore(platform: PlatformLink, userPreference: UserPreference): number {
    let score = 0
    
    // 1. 地区匹配权重 (40%)
    if (platform.region === userPreference.region) {
      score += 40
    } else {
      // 地区不匹配时的降权
      score += 10
    }
    
    // 2. 语言匹配权重 (30%)
    if (platform.language === userPreference.language) {
      score += 30
    } else {
      // 语言不匹配时的降权
      score += 5
    }
    
    // 3. 平台优先级权重 (20%)
    score += (4 - platform.priority) * 5 // priority越小分数越高
    
    // 4. 平台可用性权重 (10%)
    if (platform.isActive) {
      score += 10
    }
    
    return score
  }
  
  /**
   * 验证平台链接有效性
   */
  static async validatePlatformLink(platformUrl: string): Promise<boolean> {
    try {
      // 在实际项目中，这里会发送HEAD请求检查链接有效性
      // 由于跨域限制，这里使用模拟实现
      return new Promise((resolve) => {
        setTimeout(() => {
          // 模拟链接验证结果，90%的链接有效
          resolve(Math.random() > 0.1)
        }, 200)
      })
    } catch (error) {
      console.error('Platform link validation failed:', error)
      return false
    }
  }
  
  /**
   * 跳转到推荐平台
   */
  static async redirectToPlatform(platform: PlatformLink): Promise<void> {
    // 验证链接有效性
    const isValid = await this.validatePlatformLink(platform.platformUrl)
    
    if (!isValid) {
      throw new Error(`平台链接无效: ${platform.platformName}`)
    }
    
    // 记录用户点击行为（用于统计分析）
    this.trackPlatformClick(platform)
    
    // 在新窗口打开平台链接
    window.open(platform.platformUrl, '_blank', 'noopener,noreferrer')
  }
  
  /**
   * 记录平台点击行为
   */
  private static trackPlatformClick(platform: PlatformLink): void {
    // 在实际项目中，这里会发送统计数据到后端
    console.log('Platform click tracked:', {
      platformId: platform.id,
      platformName: platform.platformName,
      region: platform.region,
      language: platform.language,
      timestamp: new Date().toISOString()
    })
  }
  
  /**
   * 格式化价格显示
   */
  static formatPrice(price: number, currency: string): string {
    const formatters: Record<string, Intl.NumberFormat> = {
      'CNY': new Intl.NumberFormat('zh-CN', { style: 'currency', currency: 'CNY' }),
      'USD': new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }),
      'JPY': new Intl.NumberFormat('ja-JP', { style: 'currency', currency: 'JPY' }),
      'KRW': new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }),
      'EUR': new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' })
    }
    
    const formatter = formatters[currency] || formatters['CNY']
    return formatter.format(price)
  }
  
  /**
   * 格式化租赁价格显示
   */
  static formatRentalPrice(dailyRate: number, monthlyRate: number, currency: string): string {
    const daily = this.formatPrice(dailyRate, currency)
    const monthly = this.formatPrice(monthlyRate, currency)
    return `${daily}/天 或 ${monthly}/月`
  }
}