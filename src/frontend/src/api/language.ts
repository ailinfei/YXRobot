import { request } from '@/utils/request'

// 多语言管理API接口
export const languageApi = {
  // =====================================================
  // 语言配置管理
  // =====================================================
  
  /**
   * 获取所有支持的语言列表
   */
  async getLanguages() {
    return request.get('/api/admin/languages')
  },

  /**
   * 添加新语言
   */
  async createLanguage(data: {
    languageCode: string
    languageName: string
    nativeName: string
    flagIcon?: string
    sortOrder?: number
    isEnabled?: boolean
  }) {
    return request.post('/api/admin/languages', data)
  },

  /**
   * 更新语言配置
   */
  async updateLanguage(id: number, data: any) {
    return request.put(`/api/admin/languages/${id}`, data)
  },

  /**
   * 删除语言
   */
  async deleteLanguage(id: number) {
    return request.delete(`/api/admin/languages/${id}`)
  },

  /**
   * 获取翻译进度统计
   */
  async getTranslationProgress() {
    return request.get('/api/admin/languages/translation-progress')
  },

  // =====================================================
  // 内容管理
  // =====================================================

  /**
   * 获取多语言内容列表
   */
  async getContentList(params: {
    contentType: 'website' | 'robot_ui'
    page?: number
    pageSize?: number
    keyword?: string
    category?: string
    status?: string
  }) {
    return request.get('/api/admin/content', { params })
  },

  /**
   * 创建新内容
   */
  async createContent(data: {
    contentKey: string
    contentType: 'website' | 'robot_ui'
    category: string
    defaultContent: string
  }) {
    return request.post('/api/admin/content', data)
  },

  /**
   * 更新内容
   */
  async updateContent(id: number, data: any) {
    return request.put(`/api/admin/content/${id}`, data)
  },

  /**
   * 删除内容
   */
  async deleteContent(id: number) {
    return request.delete(`/api/admin/content/${id}`)
  },

  // =====================================================
  // 翻译管理
  // =====================================================

  /**
   * 获取内容的所有翻译
   */
  async getContentTranslations(contentId: number) {
    return request.get(`/api/admin/content/${contentId}/translations`)
  },

  /**
   * 更新翻译内容
   */
  async updateTranslation(data: {
    contentId: number
    languageCode: string
    title?: string
    content: string
    metaData?: any
  }) {
    return request.put('/api/admin/translations', data)
  },

  /**
   * 批量更新翻译
   */
  async batchUpdateTranslations(contentId: number, translations: Record<string, string>) {
    return request.put(`/api/admin/content/${contentId}/translations/batch`, { translations })
  },

  /**
   * 更新翻译状态
   */
  async updateTranslationStatus(data: {
    contentId: number
    languageCode: string
    status: 'pending' | 'translated' | 'reviewed' | 'published'
  }) {
    return request.patch('/api/admin/translations/status', data)
  },

  /**
   * 发布翻译
   */
  async publishTranslation(contentId: number, languageCode: string) {
    return request.post('/api/admin/translations/publish', {
      contentId,
      languageCode
    })
  },

  // =====================================================
  // 前端API - 获取翻译内容
  // =====================================================

  /**
   * 获取官网指定语言的内容
   */
  async getWebsiteContent(languageCode: string, category?: string) {
    return request.get('/api/website/content', {
      params: { languageCode, category }
    })
  },

  /**
   * 获取机器人界面指定语言的文字
   */
  async getRobotUITexts(languageCode: string, category?: string) {
    return request.get('/api/robot/ui-texts', {
      params: { languageCode, category }
    })
  },

  /**
   * 获取通用多语言内容
   */
  async getCommonTexts(languageCode: string) {
    return request.get('/api/common/texts', {
      params: { languageCode }
    })
  },

  // =====================================================
  // 内容分类管理
  // =====================================================

  /**
   * 获取内容分类列表
   */
  async getContentCategories(contentType: 'website' | 'robot_ui') {
    return request.get('/api/admin/content/categories', {
      params: { contentType }
    })
  },

  /**
   * 创建新分类
   */
  async createCategory(data: {
    contentType: 'website' | 'robot_ui'
    category: string
    description?: string
  }) {
    return request.post('/api/admin/content/categories', data)
  },

  // =====================================================
  // 导入导出功能
  // =====================================================

  /**
   * 导出翻译内容
   */
  async exportTranslations(params: {
    contentType?: 'website' | 'robot_ui'
    languageCode?: string
    format?: 'json' | 'excel' | 'csv'
  }) {
    return request.get('/api/admin/translations/export', {
      params,
      responseType: 'blob'
    })
  },

  /**
   * 导入翻译内容
   */
  async importTranslations(file: File, options: {
    contentType: 'website' | 'robot_ui'
    languageCode: string
    overwrite?: boolean
  }) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('contentType', options.contentType)
    formData.append('languageCode', options.languageCode)
    if (options.overwrite) {
      formData.append('overwrite', 'true')
    }

    return request.post('/api/admin/translations/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

// 类型定义
export interface Language {
  id: number
  languageCode: string
  languageName: string
  nativeName: string
  isEnabled: boolean
  isDefault: boolean
  sortOrder: number
  flagIcon?: string
  createdAt: string
  updatedAt: string
}

export interface ContentMaster {
  id: number
  contentKey: string
  contentType: 'website' | 'robot_ui' | 'course' | 'news' | 'common'
  category: string
  defaultLanguage: string
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface ContentTranslation {
  id: number
  contentId: number
  languageCode: string
  title?: string
  content: string
  metaData?: any
  translationStatus: 'pending' | 'translated' | 'reviewed' | 'published'
  translator?: string
  reviewer?: string
  translatedAt?: string
  reviewedAt?: string
  createdAt: string
  updatedAt: string
}

export interface TranslationProgress {
  languageCode: string
  languageName: string
  nativeName: string
  contentType: string
  totalContent: number
  translatedContent: number
  publishedContent: number
  translationPercentage: number
  publishedPercentage: number
}

export interface ContentListItem {
  id: number
  contentKey: string
  category: string
  defaultContent: string
  translations: Record<string, string>
  translationStatus: Record<string, string>
}