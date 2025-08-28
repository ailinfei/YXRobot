import request from '@/utils/request'
import type {
  News,
  NewsForm,
  NewsCategory,
  NewsTag,
  NewsStats,
  NewsListParams,
  NewsListResponse,
  NewsBatchOperationParams,
  NewsStatusUpdateParams,
  ApiResponse,
  FileUploadResponse
} from '@/types/news'

// 重新导出类型以保持向后兼容
export type NewsItem = News

// ==================== 新闻管理API ====================

/**
 * 获取新闻列表
 */
export const getNewsList = (params: NewsListParams = {}) => {
  return request.get<ApiResponse<NewsListResponse>>('/api/news', params)
}

/**
 * 获取新闻详情
 */
export const getNewsDetail = (id: number) => {
  return request.get<ApiResponse<News>>(`/api/news/${id}`)
}

/**
 * 创建新闻
 */
export const createNews = (data: NewsForm) => {
  return request.post<ApiResponse<News>>('/api/news', data)
}

/**
 * 更新新闻
 */
export const updateNews = (id: number, data: Partial<NewsForm>) => {
  return request.put<ApiResponse<News>>(`/api/news/${id}`, data)
}

/**
 * 删除新闻
 */
export const deleteNews = (id: number) => {
  return request.delete<ApiResponse<void>>(`/api/news/${id}`)
}

// ==================== 新闻状态管理API ====================

/**
 * 发布新闻
 */
export const publishNews = (id: number, publishTime?: string) => {
  return request.post<ApiResponse<News>>(`/api/news/${id}/publish`, {
    publishTime
  })
}

/**
 * 下线新闻
 */
export const offlineNews = (id: number, reason?: string) => {
  return request.post<ApiResponse<News>>(`/api/news/${id}/offline`, {
    reason
  })
}

/**
 * 批量操作新闻
 */
export const batchOperateNews = (params: NewsBatchOperationParams) => {
  return request.post<ApiResponse<void>>('/api/news/batch', params)
}

/**
 * 更新新闻状态
 */
export const updateNewsStatus = (params: NewsStatusUpdateParams) => {
  return request.post<ApiResponse<News>>(`/api/news/${params.id}/status`, {
    status: params.status,
    publishTime: params.publishTime,
    reason: params.reason
  })
}

// ==================== 新闻分类和标签API ====================

/**
 * 获取新闻分类列表
 */
export const getNewsCategories = () => {
  return request.get<ApiResponse<NewsCategory[]>>('/api/news/categories')
}

/**
 * 获取新闻标签列表
 */
export const getNewsTags = () => {
  return request.get<ApiResponse<NewsTag[]>>('/api/news/tags')
}

/**
 * 创建新闻标签
 */
export const createNewsTag = (data: Partial<NewsTag>) => {
  return request.post<ApiResponse<NewsTag>>('/api/news/tags', data)
}

// ==================== 新闻统计和分析API ====================

/**
 * 获取新闻统计数据
 */
export const getNewsStats = () => {
  return request.get<ApiResponse<NewsStats>>('/api/news/stats')
}

/**
 * 获取热门新闻
 */
export const getHotNews = (limit: number = 10) => {
  return request.get<ApiResponse<News[]>>('/api/news/hot', { limit })
}

/**
 * 获取相关新闻
 */
export const getRelatedNews = (id: number, limit: number = 5) => {
  return request.get<ApiResponse<News[]>>(`/api/news/${id}/related`, { limit })
}

// ==================== 新闻互动API ====================

/**
 * 记录新闻浏览
 */
export const recordNewsView = (id: number) => {
  return request.post<ApiResponse<void>>(`/api/news/${id}/view`)
}

/**
 * 点赞新闻
 */
export const likeNews = (id: number) => {
  return request.post<ApiResponse<void>>(`/api/news/${id}/like`)
}

/**
 * 收藏新闻
 */
export const collectNews = (id: number) => {
  return request.post<ApiResponse<void>>(`/api/news/${id}/collect`)
}

// ==================== 文件上传API ====================

/**
 * 上传新闻图片
 */
export const uploadNewsImage = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)

  return request.post<ApiResponse<FileUploadResponse>>('/api/news/upload-image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传新闻附件
 */
export const uploadNewsAttachment = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)

  return request.post<ApiResponse<FileUploadResponse>>('/api/news/upload-attachment', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// ==================== 向后兼容的别名 ====================

/**
 * @deprecated 使用 recordNewsView 替代
 */
export const increaseNewsViews = recordNewsView