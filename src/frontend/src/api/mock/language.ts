/**
 * 多语言管理Mock数据
 * 提供多语言相关的模拟数据，用于前端开发和测试
 */

export interface Language {
  id: number
  code: string
  name: string
  nativeName: string
  isEnabled: boolean
  isDefault: boolean
  flag: string
  direction: 'ltr' | 'rtl'
  completeness: number // 翻译完成度百分比
  lastUpdated: string
  createdAt: string
  updatedAt: string
}

export interface RobotInterfaceText {
  id: number
  key: string
  category: string
  description: string
  defaultText: string
  translations: { [languageCode: string]: string }
  isRequired: boolean
  maxLength?: number
  context: string
  lastUpdated: string
  updatedBy: string
}

export interface WebsiteContent {
  id: number
  page: string
  section: string
  key: string
  contentType: 'text' | 'html' | 'markdown'
  defaultContent: string
  translations: { [languageCode: string]: string }
  isPublished: boolean
  priority: 'high' | 'medium' | 'low'
  lastUpdated: string
  updatedBy: string
  seoTitle?: { [languageCode: string]: string }
  seoDescription?: { [languageCode: string]: string }
}

export interface TranslationProgress {
  languageCode: string
  languageName: string
  robotInterface: {
    total: number
    translated: number
    percentage: number
  }
  websiteContent: {
    total: number
    translated: number
    percentage: number
  }
  overall: {
    total: number
    translated: number
    percentage: number
  }
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
export class LanguageMockService {
  private static languages = [
    { code: 'zh-CN', name: '简体中文', nativeName: '简体中文', flag: '🇨🇳', direction: 'ltr' as const, isDefault: true },
    { code: 'zh-TW', name: '繁体中文', nativeName: '繁體中文', flag: '🇹🇼', direction: 'ltr' as const, isDefault: false },
    { code: 'en', name: '英语', nativeName: 'English', flag: '🇺🇸', direction: 'ltr' as const, isDefault: false },
    { code: 'ja', name: '日语', nativeName: '日本語', flag: '🇯🇵', direction: 'ltr' as const, isDefault: false },
    { code: 'ko', name: '韩语', nativeName: '한국어', flag: '🇰🇷', direction: 'ltr' as const, isDefault: false },
    { code: 'fr', name: '法语', nativeName: 'Français', flag: '🇫🇷', direction: 'ltr' as const, isDefault: false },
    { code: 'de', name: '德语', nativeName: 'Deutsch', flag: '🇩🇪', direction: 'ltr' as const, isDefault: false },
    { code: 'es', name: '西班牙语', nativeName: 'Español', flag: '🇪🇸', direction: 'ltr' as const, isDefault: false },
    { code: 'ru', name: '俄语', nativeName: 'Русский', flag: '🇷🇺', direction: 'ltr' as const, isDefault: false },
    { code: 'ar', name: '阿拉伯语', nativeName: 'العربية', flag: '🇸🇦', direction: 'rtl' as const, isDefault: false },
    { code: 'pt', name: '葡萄牙语', nativeName: 'Português', flag: '🇵🇹', direction: 'ltr' as const, isDefault: false },
    { code: 'it', name: '意大利语', nativeName: 'Italiano', flag: '🇮🇹', direction: 'ltr' as const, isDefault: false }
  ]

  private static robotInterfaceCategories = [
    '系统消息', '用户交互', '错误提示', '操作指引', '状态显示',
    '菜单导航', '按钮文本', '表单标签', '提示信息', '确认对话'
  ]

  private static websitePages = [
    '首页', '产品介绍', '功能特色', '价格方案', '客户案例',
    '新闻资讯', '帮助中心', '关于我们', '联系我们', '用户协议'
  ]

  private static websiteSections = [
    'header', 'hero', 'features', 'pricing', 'testimonials',
    'news', 'footer', 'sidebar', 'navigation', 'content'
  ]

  private static editors = [
    '张编辑', '李翻译', '王校对', '陈审核', '刘管理',
    '杨编辑', '赵翻译', '黄校对', '周审核', '吴管理'
  ]

  // 生成语言配置数据
  static generateLanguages(): Language[] {
    return this.languages.map((lang, index) => ({
      id: index + 1,
      code: lang.code,
      name: lang.name,
      nativeName: lang.nativeName,
      isEnabled: index < 6 || Math.random() > 0.3, // 前6种语言默认启用
      isDefault: lang.isDefault,
      flag: lang.flag,
      direction: lang.direction,
      completeness: lang.isDefault ? 100 : randomFloat(60, 95),
      lastUpdated: generateRandomDate(30),
      createdAt: generateRandomDate(365),
      updatedAt: generateRandomDate(7)
    }))
  }

  // 生成机器人界面文字数据
  static generateRobotInterfaceTexts(count: number = 100): RobotInterfaceText[] {
    const texts: RobotInterfaceText[] = []
    const enabledLanguages = this.generateLanguages().filter(lang => lang.isEnabled)

    const textKeys = [
      'welcome_message', 'goodbye_message', 'error_occurred', 'please_wait',
      'operation_success', 'operation_failed', 'confirm_action', 'cancel_action',
      'start_learning', 'pause_learning', 'resume_learning', 'finish_learning',
      'next_character', 'previous_character', 'repeat_character', 'skip_character',
      'writing_correct', 'writing_incorrect', 'try_again', 'well_done',
      'battery_low', 'charging_required', 'system_update', 'maintenance_mode',
      'voice_recognition_on', 'voice_recognition_off', 'volume_up', 'volume_down',
      'brightness_up', 'brightness_down', 'wifi_connected', 'wifi_disconnected'
    ]

    const defaultTexts: { [key: string]: string } = {
      'welcome_message': '欢迎使用YX机器人！',
      'goodbye_message': '再见，期待下次见面！',
      'error_occurred': '发生错误，请稍后重试',
      'please_wait': '请稍等...',
      'operation_success': '操作成功',
      'operation_failed': '操作失败',
      'confirm_action': '确认操作',
      'cancel_action': '取消操作',
      'start_learning': '开始学习',
      'pause_learning': '暂停学习',
      'resume_learning': '继续学习',
      'finish_learning': '完成学习',
      'next_character': '下一个字',
      'previous_character': '上一个字',
      'repeat_character': '重复这个字',
      'skip_character': '跳过这个字',
      'writing_correct': '写得很好！',
      'writing_incorrect': '再试一次',
      'try_again': '请重试',
      'well_done': '做得很棒！',
      'battery_low': '电量不足',
      'charging_required': '需要充电',
      'system_update': '系统更新',
      'maintenance_mode': '维护模式',
      'voice_recognition_on': '语音识别已开启',
      'voice_recognition_off': '语音识别已关闭',
      'volume_up': '音量增大',
      'volume_down': '音量减小',
      'brightness_up': '亮度增加',
      'brightness_down': '亮度降低',
      'wifi_connected': 'WiFi已连接',
      'wifi_disconnected': 'WiFi已断开'
    }

    textKeys.forEach((key, index) => {
      const translations: { [languageCode: string]: string } = {}
      
      enabledLanguages.forEach(lang => {
        if (lang.code === 'zh-CN') {
          translations[lang.code] = defaultTexts[key] || `默认文本 ${index + 1}`
        } else {
          // 模拟翻译（实际项目中应该是真实的翻译）
          if (Math.random() > 0.2) { // 80%的概率有翻译
            translations[lang.code] = this.generateMockTranslation(defaultTexts[key] || `默认文本 ${index + 1}`, lang.code)
          }
        }
      })

      texts.push({
        id: index + 1,
        key,
        category: this.robotInterfaceCategories[randomBetween(0, this.robotInterfaceCategories.length - 1)],
        description: `机器人界面文字：${defaultTexts[key] || key}`,
        defaultText: defaultTexts[key] || `默认文本 ${index + 1}`,
        translations,
        isRequired: Math.random() > 0.3,
        maxLength: randomBetween(10, 100),
        context: `在${this.robotInterfaceCategories[randomBetween(0, this.robotInterfaceCategories.length - 1)]}场景中使用`,
        lastUpdated: generateRandomDate(30),
        updatedBy: this.editors[randomBetween(0, this.editors.length - 1)]
      })
    })

    return texts
  }

  // 生成官网多语言内容数据
  static generateWebsiteContents(count: number = 80): WebsiteContent[] {
    const contents: WebsiteContent[] = []
    const enabledLanguages = this.generateLanguages().filter(lang => lang.isEnabled)
    const contentTypes: ('text' | 'html' | 'markdown')[] = ['text', 'html', 'markdown']
    const priorities: ('high' | 'medium' | 'low')[] = ['high', 'medium', 'low']

    const contentKeys = [
      'site_title', 'site_description', 'hero_title', 'hero_subtitle',
      'feature_title_1', 'feature_desc_1', 'feature_title_2', 'feature_desc_2',
      'pricing_title', 'pricing_desc', 'contact_title', 'contact_desc',
      'about_title', 'about_content', 'news_title', 'news_desc',
      'footer_copyright', 'footer_links', 'privacy_policy', 'terms_of_service'
    ]

    const defaultContents: { [key: string]: string } = {
      'site_title': 'YX机器人 - 智能汉字学习助手',
      'site_description': '专业的AI驱动汉字学习机器人，让汉字学习更有趣、更高效',
      'hero_title': '智能汉字学习的未来',
      'hero_subtitle': '通过AI技术和机器人交互，让每个人都能轻松掌握汉字书写',
      'feature_title_1': '智能识别',
      'feature_desc_1': '先进的AI算法，精准识别汉字笔画和结构',
      'feature_title_2': '个性化学习',
      'feature_desc_2': '根据学习者水平，提供定制化的学习方案',
      'pricing_title': '选择适合您的方案',
      'pricing_desc': '灵活的租赁方案，满足不同需求',
      'contact_title': '联系我们',
      'contact_desc': '有任何问题，随时与我们联系',
      'about_title': '关于YX机器人',
      'about_content': '我们致力于通过技术创新，让汉字学习变得更加简单有趣。',
      'news_title': '最新资讯',
      'news_desc': '了解YX机器人的最新动态和产品更新',
      'footer_copyright': '© 2024 YX机器人. 保留所有权利.',
      'footer_links': '隐私政策 | 服务条款 | 联系我们',
      'privacy_policy': '隐私政策内容...',
      'terms_of_service': '服务条款内容...'
    }

    contentKeys.forEach((key, index) => {
      const translations: { [languageCode: string]: string } = {}
      const seoTitle: { [languageCode: string]: string } = {}
      const seoDescription: { [languageCode: string]: string } = {}
      
      enabledLanguages.forEach(lang => {
        if (lang.code === 'zh-CN') {
          translations[lang.code] = defaultContents[key] || `默认内容 ${index + 1}`
          seoTitle[lang.code] = `SEO标题 - ${defaultContents[key] || key}`
          seoDescription[lang.code] = `SEO描述 - ${defaultContents[key] || key}`
        } else {
          // 模拟翻译
          if (Math.random() > 0.25) { // 75%的概率有翻译
            translations[lang.code] = this.generateMockTranslation(defaultContents[key] || `默认内容 ${index + 1}`, lang.code)
            seoTitle[lang.code] = this.generateMockTranslation(`SEO标题 - ${defaultContents[key] || key}`, lang.code)
            seoDescription[lang.code] = this.generateMockTranslation(`SEO描述 - ${defaultContents[key] || key}`, lang.code)
          }
        }
      })

      contents.push({
        id: index + 1,
        page: this.websitePages[randomBetween(0, this.websitePages.length - 1)],
        section: this.websiteSections[randomBetween(0, this.websiteSections.length - 1)],
        key,
        contentType: contentTypes[randomBetween(0, contentTypes.length - 1)],
        defaultContent: defaultContents[key] || `默认内容 ${index + 1}`,
        translations,
        isPublished: Math.random() > 0.2,
        priority: priorities[randomBetween(0, priorities.length - 1)],
        lastUpdated: generateRandomDate(30),
        updatedBy: this.editors[randomBetween(0, this.editors.length - 1)],
        seoTitle,
        seoDescription
      })
    })

    return contents
  }

  // 生成翻译进度数据
  static generateTranslationProgress(): TranslationProgress[] {
    const languages = this.generateLanguages().filter(lang => lang.isEnabled && !lang.isDefault)
    const robotTexts = this.generateRobotInterfaceTexts()
    const websiteContents = this.generateWebsiteContents()

    return languages.map(lang => {
      const robotTranslated = robotTexts.filter(text => text.translations[lang.code]).length
      const websiteTranslated = websiteContents.filter(content => content.translations[lang.code]).length
      const totalTranslated = robotTranslated + websiteTranslated
      const totalItems = robotTexts.length + websiteContents.length

      return {
        languageCode: lang.code,
        languageName: lang.name,
        robotInterface: {
          total: robotTexts.length,
          translated: robotTranslated,
          percentage: Math.round((robotTranslated / robotTexts.length) * 100)
        },
        websiteContent: {
          total: websiteContents.length,
          translated: websiteTranslated,
          percentage: Math.round((websiteTranslated / websiteContents.length) * 100)
        },
        overall: {
          total: totalItems,
          translated: totalTranslated,
          percentage: Math.round((totalTranslated / totalItems) * 100)
        }
      }
    })
  }

  // 生成模拟翻译
  private static generateMockTranslation(text: string, languageCode: string): string {
    const mockTranslations: { [key: string]: { [key: string]: string } } = {
      'en': {
        '欢迎使用YX机器人！': 'Welcome to YX Robot!',
        '再见，期待下次见面！': 'Goodbye, see you next time!',
        '发生错误，请稍后重试': 'An error occurred, please try again later',
        '请稍等...': 'Please wait...',
        '操作成功': 'Operation successful',
        '操作失败': 'Operation failed',
        '开始学习': 'Start Learning',
        '暂停学习': 'Pause Learning',
        '智能汉字学习的未来': 'The Future of Intelligent Chinese Character Learning',
        'YX机器人 - 智能汉字学习助手': 'YX Robot - Intelligent Chinese Character Learning Assistant'
      },
      'ja': {
        '欢迎使用YX机器人！': 'YXロボットへようこそ！',
        '再见，期待下次见面！': 'さようなら、また次回お会いしましょう！',
        '发生错误，请稍后重试': 'エラーが発生しました。後でもう一度お試しください',
        '请稍等...': 'お待ちください...',
        '操作成功': '操作成功',
        '操作失败': '操作失敗',
        '开始学习': '学習開始',
        '暂停学习': '学習一時停止',
        '智能汉字学习的未来': 'インテリジェント漢字学習の未来',
        'YX机器人 - 智能汉字学习助手': 'YXロボット - インテリジェント漢字学習アシスタント'
      }
    }

    const langTranslations = mockTranslations[languageCode]
    if (langTranslations && langTranslations[text]) {
      return langTranslations[text]
    }

    // 如果没有预定义翻译，返回模拟翻译
    return `[${languageCode.toUpperCase()}] ${text}`
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
export const mockLanguageAPI = {
  // 获取语言配置列表
  async getLanguages() {
    await mockDelay()
    const languages = LanguageMockService.generateLanguages()
    return createMockResponse(languages)
  },

  // 更新语言配置
  async updateLanguage(languageId: number, languageData: any) {
    await mockDelay()
    const updatedLanguage = {
      id: languageId,
      ...languageData,
      updatedAt: new Date().toISOString()
    }
    return createMockResponse(updatedLanguage)
  },

  // 获取机器人界面文字列表
  async getRobotInterfaceTexts(params: any) {
    await mockDelay()
    let data = LanguageMockService.generateRobotInterfaceTexts(50)
    
    // 应用筛选
    if (params?.category) {
      data = data.filter(item => item.category === params.category)
    }
    
    if (params?.isRequired !== undefined) {
      data = data.filter(item => item.isRequired === params.isRequired)
    }
    
    if (params?.keyword) {
      data = data.filter(item => 
        item.key.includes(params.keyword) ||
        item.defaultText.includes(params.keyword) ||
        item.description.includes(params.keyword)
      )
    }
    
    // 分页
    const page = params?.page || 1
    const pageSize = params?.pageSize || 20
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

  // 更新机器人界面文字
  async updateRobotInterfaceText(textId: number, textData: any) {
    await mockDelay()
    const updatedText = {
      id: textId,
      ...textData,
      lastUpdated: new Date().toISOString()
    }
    return createMockResponse(updatedText)
  },

  // 获取官网多语言内容列表
  async getWebsiteContents(params: any) {
    await mockDelay()
    let data = LanguageMockService.generateWebsiteContents(100)
    
    // 应用筛选
    if (params?.page) {
      data = data.filter(item => item.page === params.page)
    }
    
    if (params?.section) {
      data = data.filter(item => item.section === params.section)
    }
    
    if (params?.contentType) {
      data = data.filter(item => item.contentType === params.contentType)
    }
    
    if (params?.isPublished !== undefined) {
      data = data.filter(item => item.isPublished === params.isPublished)
    }
    
    if (params?.priority) {
      data = data.filter(item => item.priority === params.priority)
    }
    
    if (params?.keyword) {
      data = data.filter(item => 
        item.key.includes(params.keyword) ||
        item.defaultContent.includes(params.keyword)
      )
    }
    
    // 分页
    const page = params?.page || 1
    const pageSize = params?.pageSize || 20
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

  // 更新官网多语言内容
  async updateWebsiteContent(contentId: number, contentData: any) {
    await mockDelay()
    const updatedContent = {
      id: contentId,
      ...contentData,
      lastUpdated: new Date().toISOString()
    }
    return createMockResponse(updatedContent)
  },

  // 获取翻译进度
  async getTranslationProgress() {
    await mockDelay()
    const progress = LanguageMockService.generateTranslationProgress()
    return createMockResponse(progress)
  },

  // 批量翻译
  async batchTranslate(translateData: any) {
    await mockDelay(2000) // 模拟较长的翻译时间
    return createMockResponse({
      translatedCount: translateData.items?.length || 0,
      successCount: Math.floor((translateData.items?.length || 0) * 0.9),
      failedCount: Math.ceil((translateData.items?.length || 0) * 0.1)
    })
  },

  // 导出翻译文件
  async exportTranslations(exportData: any) {
    await mockDelay(1000)
    return createMockResponse({
      downloadUrl: `/api/export/translations/${Date.now()}.json`,
      expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString()
    })
  },

  // 导入翻译文件
  async importTranslations(importData: any) {
    await mockDelay(1500)
    return createMockResponse({
      importedCount: randomBetween(50, 200),
      updatedCount: randomBetween(20, 100),
      errorCount: randomBetween(0, 10)
    })
  }
}