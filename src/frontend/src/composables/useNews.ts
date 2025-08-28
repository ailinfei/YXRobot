/**
 * 新闻管理组合式API
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type {
  News,
  NewsForm,
  NewsCategory,
  NewsTag,
  NewsStats,
  NewsListParams,
  NewsListResponse,
  NewsBatchOperationParams,
  NewsStatus
} from '@/types/news'
import {
  getNewsList,
  getNewsDetail,
  createNews,
  updateNews,
  deleteNews,
  publishNews,
  offlineNews,
  batchOperateNews,
  getNewsCategories,
  getNewsTags,
  getNewsStats,
  uploadNewsImage
} from '@/api/news'
import {
  validateNewsForm,
  formatNewsViews,
  formatNewsPublishTime,
  getNewsStatusText,
  getNewsStatusType
} from '@/utils/news'
import { DEFAULT_NEWS_FORM, DEFAULT_PAGE_CONFIG } from '@/constants/news'

/**
 * 新闻列表管理
 */
export function useNewsList() {
  // 响应式数据
  const loading = ref(false)
  const newsList = ref<News[]>([])
  const total = ref(0)
  const searchParams = reactive<NewsListParams>({
    page: DEFAULT_PAGE_CONFIG.page,
    pageSize: DEFAULT_PAGE_CONFIG.pageSize
  })

  // 计算属性
  const hasData = computed(() => newsList.value.length > 0)
  const isEmpty = computed(() => !loading.value && newsList.value.length === 0)

  // 加载新闻列表
  const loadNewsList = async (params?: Partial<NewsListParams>) => {
    try {
      loading.value = true
      
      // 合并参数
      const requestParams = { ...searchParams, ...params }
      
      const response = await getNewsList(requestParams)
      if (response.data.code === 200 && response.data.data) {
        newsList.value = response.data.data.list || []
        total.value = response.data.data.total || 0
        
        // 更新搜索参数
        Object.assign(searchParams, {
          page: response.data.data.page || 1,
          pageSize: response.data.data.pageSize || DEFAULT_PAGE_CONFIG.pageSize
        })
      } else {
        ElMessage.error(response.data.message || '获取新闻列表失败')
      }
    } catch (error) {
      console.error('加载新闻列表失败:', error)
      ElMessage.error('加载新闻列表失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }

  // 搜索新闻
  const searchNews = (keyword: string) => {
    searchParams.keyword = keyword
    searchParams.page = 1
    loadNewsList()
  }

  // 筛选新闻
  const filterNews = (filters: Partial<NewsListParams>) => {
    Object.assign(searchParams, filters)
    searchParams.page = 1
    loadNewsList()
  }

  // 重置筛选
  const resetFilters = () => {
    Object.assign(searchParams, {
      page: 1,
      pageSize: DEFAULT_PAGE_CONFIG.pageSize,
      keyword: undefined,
      categoryId: undefined,
      status: undefined,
      author: undefined,
      isFeatured: undefined,
      startDate: undefined,
      endDate: undefined
    })
    loadNewsList()
  }

  // 分页处理
  const handlePageChange = (page: number) => {
    searchParams.page = page
    loadNewsList()
  }

  const handleSizeChange = (size: number) => {
    searchParams.pageSize = size
    searchParams.page = 1
    loadNewsList()
  }

  // 刷新列表
  const refreshList = () => {
    loadNewsList()
  }

  return {
    // 响应式数据
    loading,
    newsList,
    total,
    searchParams,
    
    // 计算属性
    hasData,
    isEmpty,
    
    // 方法
    loadNewsList,
    searchNews,
    filterNews,
    resetFilters,
    handlePageChange,
    handleSizeChange,
    refreshList
  }
}

/**
 * 新闻表单管理
 */
export function useNewsForm() {
  // 响应式数据
  const loading = ref(false)
  const formData = reactive<NewsForm>({ ...DEFAULT_NEWS_FORM })
  const formErrors = ref<string[]>([])

  // 计算属性
  const isValid = computed(() => formErrors.value.length === 0)
  const isEditing = computed(() => !!formData.id)

  // 验证表单
  const validateForm = () => {
    const validation = validateNewsForm(formData)
    formErrors.value = validation.errors
    return validation.valid
  }

  // 重置表单
  const resetForm = () => {
    Object.assign(formData, DEFAULT_NEWS_FORM)
    formErrors.value = []
  }

  // 加载新闻数据到表单
  const loadNewsToForm = async (id: number) => {
    try {
      loading.value = true
      const response = await getNewsDetail(id)
      
      if (response.data.code === 200 && response.data.data) {
        const news = response.data.data
        Object.assign(formData, {
          id: news.id,
          title: news.title || '',
          excerpt: news.excerpt || '',
          content: news.content || '',
          categoryId: news.categoryId,
          author: news.author || '',
          status: news.status,
          coverImage: news.coverImage || '',
          publishTime: news.publishTime,
          isFeatured: news.isFeatured || false,
          sortOrder: news.sortOrder || 0,
          tagIds: news.tagIds || []
        })
      } else {
        ElMessage.error(response.data.message || '获取新闻详情失败')
      }
    } catch (error) {
      console.error('加载新闻详情失败:', error)
      ElMessage.error('加载新闻详情失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }

  // 保存新闻
  const saveNews = async () => {
    if (!validateForm()) {
      ElMessage.error('请检查表单数据')
      return false
    }

    try {
      loading.value = true
      
      let response
      if (isEditing.value) {
        response = await updateNews(formData.id!, formData)
      } else {
        response = await createNews(formData)
      }

      if (response.data.code === 200) {
        ElMessage.success(isEditing.value ? '更新成功' : '创建成功')
        return true
      } else {
        ElMessage.error(response.data.message || '保存失败')
        return false
      }
    } catch (error) {
      console.error('保存新闻失败:', error)
      ElMessage.error('保存失败，请稍后重试')
      return false
    } finally {
      loading.value = false
    }
  }

  return {
    // 响应式数据
    loading,
    formData,
    formErrors,
    
    // 计算属性
    isValid,
    isEditing,
    
    // 方法
    validateForm,
    resetForm,
    loadNewsToForm,
    saveNews
  }
}

/**
 * 新闻操作管理
 */
export function useNewsActions() {
  // 响应式数据
  const loading = ref(false)

  // 删除新闻
  const deleteNewsItem = async (id: number, title: string) => {
    try {
      await ElMessageBox.confirm(
        `确定要删除新闻"${title}"吗？删除后无法恢复。`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      loading.value = true
      const response = await deleteNews(id)

      if (response.data.code === 200) {
        ElMessage.success('删除成功')
        return true
      } else {
        ElMessage.error(response.data.message || '删除失败')
        return false
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除新闻失败:', error)
        ElMessage.error('删除失败，请稍后重试')
      }
      return false
    } finally {
      loading.value = false
    }
  }

  // 发布新闻
  const publishNewsItem = async (id: number, title: string, publishTime?: string) => {
    try {
      loading.value = true
      const response = await publishNews(id, publishTime)

      if (response.data.code === 200) {
        ElMessage.success(`新闻"${title}"发布成功`)
        return true
      } else {
        ElMessage.error(response.data.message || '发布失败')
        return false
      }
    } catch (error) {
      console.error('发布新闻失败:', error)
      ElMessage.error('发布失败，请稍后重试')
      return false
    } finally {
      loading.value = false
    }
  }

  // 下线新闻
  const offlineNewsItem = async (id: number, title: string, reason?: string) => {
    try {
      await ElMessageBox.confirm(
        `确定要下线新闻"${title}"吗？下线后用户将无法访问。`,
        '确认下线',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      loading.value = true
      const response = await offlineNews(id, reason)

      if (response.data.code === 200) {
        ElMessage.success(`新闻"${title}"已下线`)
        return true
      } else {
        ElMessage.error(response.data.message || '下线失败')
        return false
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('下线新闻失败:', error)
        ElMessage.error('下线失败，请稍后重试')
      }
      return false
    } finally {
      loading.value = false
    }
  }

  // 批量操作
  const batchOperation = async (params: NewsBatchOperationParams) => {
    if (params.ids.length === 0) {
      ElMessage.warning('请选择要操作的新闻')
      return false
    }

    const operationText = {
      publish: '发布',
      offline: '下线',
      delete: '删除',
      feature: '设为推荐',
      unfeature: '取消推荐'
    }[params.operation]

    try {
      await ElMessageBox.confirm(
        `确定要${operationText}选中的 ${params.ids.length} 条新闻吗？`,
        `确认${operationText}`,
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      loading.value = true
      const response = await batchOperateNews(params)

      if (response.data.code === 200) {
        ElMessage.success(`批量${operationText}成功`)
        return true
      } else {
        ElMessage.error(response.data.message || `批量${operationText}失败`)
        return false
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('批量操作失败:', error)
        ElMessage.error(`批量${operationText}失败，请稍后重试`)
      }
      return false
    } finally {
      loading.value = false
    }
  }

  return {
    // 响应式数据
    loading,
    
    // 方法
    deleteNewsItem,
    publishNewsItem,
    offlineNewsItem,
    batchOperation
  }
}

/**
 * 新闻分类和标签管理
 */
export function useNewsMetadata() {
  // 响应式数据
  const loading = ref(false)
  const categories = ref<NewsCategory[]>([])
  const tags = ref<NewsTag[]>([])

  // 加载分类列表
  const loadCategories = async () => {
    try {
      loading.value = true
      const response = await getNewsCategories()

      if (response.data.code === 200 && response.data.data) {
        categories.value = response.data.data
      } else {
        ElMessage.error(response.data.message || '获取分类列表失败')
      }
    } catch (error) {
      console.error('加载分类列表失败:', error)
      ElMessage.error('加载分类列表失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }

  // 加载标签列表
  const loadTags = async () => {
    try {
      loading.value = true
      const response = await getNewsTags()

      if (response.data.code === 200 && response.data.data) {
        tags.value = response.data.data
      } else {
        ElMessage.error(response.data.message || '获取标签列表失败')
      }
    } catch (error) {
      console.error('加载标签列表失败:', error)
      ElMessage.error('加载标签列表失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }

  // 获取分类名称
  const getCategoryName = (categoryId: number) => {
    const category = categories.value.find(c => c.id === categoryId)
    return category?.name || '未知分类'
  }

  // 获取标签名称列表
  const getTagNames = (tagIds: number[]) => {
    return tagIds.map(id => {
      const tag = tags.value.find(t => t.id === id)
      return tag?.name || '未知标签'
    })
  }

  return {
    // 响应式数据
    loading,
    categories,
    tags,
    
    // 方法
    loadCategories,
    loadTags,
    getCategoryName,
    getTagNames
  }
}

/**
 * 新闻统计管理
 */
export function useNewsStats() {
  // 响应式数据
  const loading = ref(false)
  const stats = ref<NewsStats | null>(null)

  // 计算属性
  const hasStats = computed(() => stats.value !== null)

  // 加载统计数据
  const loadStats = async () => {
    try {
      loading.value = true
      const response = await getNewsStats()

      if (response.data.code === 200 && response.data.data) {
        stats.value = response.data.data
      } else {
        ElMessage.error(response.data.message || '获取统计数据失败')
      }
    } catch (error) {
      console.error('加载统计数据失败:', error)
      ElMessage.error('加载统计数据失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }

  return {
    // 响应式数据
    loading,
    stats,
    
    // 计算属性
    hasStats,
    
    // 方法
    loadStats
  }
}

/**
 * 文件上传管理
 */
export function useFileUpload() {
  // 响应式数据
  const uploading = ref(false)
  const uploadProgress = ref(0)

  // 上传图片
  const uploadImage = async (file: File) => {
    try {
      uploading.value = true
      uploadProgress.value = 0

      const response = await uploadNewsImage(file)

      if (response.data.code === 200 && response.data.data) {
        ElMessage.success('图片上传成功')
        return response.data.data.url
      } else {
        ElMessage.error(response.data.message || '图片上传失败')
        return null
      }
    } catch (error) {
      console.error('上传图片失败:', error)
      ElMessage.error('图片上传失败，请稍后重试')
      return null
    } finally {
      uploading.value = false
      uploadProgress.value = 0
    }
  }

  // 验证图片文件
  const validateImageFile = (file: File) => {
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
    const maxSize = 5 * 1024 * 1024 // 5MB

    if (!allowedTypes.includes(file.type)) {
      ElMessage.error('只支持 JPG、PNG、GIF、WebP 格式的图片')
      return false
    }

    if (file.size > maxSize) {
      ElMessage.error('图片大小不能超过 5MB')
      return false
    }

    return true
  }

  return {
    // 响应式数据
    uploading,
    uploadProgress,
    
    // 方法
    uploadImage,
    validateImageFile
  }
}

/**
 * 新闻工具函数
 */
export function useNewsUtils() {
  return {
    formatNewsViews,
    formatNewsPublishTime,
    getNewsStatusText,
    getNewsStatusType
  }
}