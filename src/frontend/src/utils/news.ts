/**
 * 新闻管理相关工具函数
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

import type { News, NewsForm, NewsStats } from '@/types/news'
import { NewsStatus } from '@/types/news'

// ==================== 状态处理函数 ====================

/**
 * 获取新闻状态的显示文本
 */
export const getNewsStatusText = (status: NewsStatus): string => {
    const statusMap = {
        [NewsStatus.DRAFT]: '草稿',
        [NewsStatus.PUBLISHED]: '已发布',
        [NewsStatus.OFFLINE]: '已下线'
    }
    return statusMap[status] || '未知'
}

/**
 * 获取新闻状态的颜色类型
 */
export const getNewsStatusType = (status: NewsStatus): string => {
    const typeMap = {
        [NewsStatus.DRAFT]: 'info',
        [NewsStatus.PUBLISHED]: 'success',
        [NewsStatus.OFFLINE]: 'danger'
    }
    return typeMap[status] || 'info'
}

/**
 * 获取新闻状态的颜色值
 */
export const getNewsStatusColor = (status: NewsStatus): string => {
    const colorMap = {
        [NewsStatus.DRAFT]: '#909399',
        [NewsStatus.PUBLISHED]: '#67C23A',
        [NewsStatus.OFFLINE]: '#F56C6C'
    }
    return colorMap[status] || '#909399'
}

/**
 * 检查新闻状态是否可以转换
 */
export const canChangeNewsStatus = (currentStatus: NewsStatus, targetStatus: NewsStatus): boolean => {
    // 草稿可以发布或下线
    if (currentStatus === NewsStatus.DRAFT) {
        return targetStatus === NewsStatus.PUBLISHED || targetStatus === NewsStatus.OFFLINE
    }

    // 已发布可以下线
    if (currentStatus === NewsStatus.PUBLISHED) {
        return targetStatus === NewsStatus.OFFLINE
    }

    // 已下线可以重新发布
    if (currentStatus === NewsStatus.OFFLINE) {
        return targetStatus === NewsStatus.PUBLISHED
    }

    return false
}

// ==================== 数据格式化函数 ====================

/**
 * 格式化新闻浏览量
 */
export const formatNewsViews = (views: number): string => {
    if (views >= 10000) {
        return `${(views / 10000).toFixed(1)}万`
    }
    if (views >= 1000) {
        return `${(views / 1000).toFixed(1)}k`
    }
    return views.toString()
}

/**
 * 格式化新闻发布时间
 */
export const formatNewsPublishTime = (publishTime: string | undefined): string => {
    if (!publishTime) return '未发布'

    const date = new Date(publishTime)
    const now = new Date()
    const diff = now.getTime() - date.getTime()

    // 小于1分钟
    if (diff < 60 * 1000) {
        return '刚刚'
    }

    // 小于1小时
    if (diff < 60 * 60 * 1000) {
        const minutes = Math.floor(diff / (60 * 1000))
        return `${minutes}分钟前`
    }

    // 小于1天
    if (diff < 24 * 60 * 60 * 1000) {
        const hours = Math.floor(diff / (60 * 60 * 1000))
        return `${hours}小时前`
    }

    // 小于7天
    if (diff < 7 * 24 * 60 * 60 * 1000) {
        const days = Math.floor(diff / (24 * 60 * 60 * 1000))
        return `${days}天前`
    }

    // 超过7天显示具体日期
    return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    })
}

/**
 * 格式化新闻创建时间
 */
export const formatNewsCreatedAt = (createdAt: string): string => {
    const date = new Date(createdAt)
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    })
}

/**
 * 截断新闻标题
 */
export const truncateNewsTitle = (title: string, maxLength: number = 50): string => {
    if (title.length <= maxLength) return title
    return title.substring(0, maxLength) + '...'
}

/**
 * 截断新闻摘要
 */
export const truncateNewsExcerpt = (excerpt: string | undefined, maxLength: number = 100): string => {
    if (!excerpt) return ''
    if (excerpt.length <= maxLength) return excerpt
    return excerpt.substring(0, maxLength) + '...'
}

// ==================== 数据验证函数 ====================

/**
 * 验证新闻标题
 */
export const validateNewsTitle = (title: string): { valid: boolean; message?: string } => {
    if (!title || title.trim().length === 0) {
        return { valid: false, message: '新闻标题不能为空' }
    }

    if (title.length < 5) {
        return { valid: false, message: '新闻标题长度不能少于5个字符' }
    }

    if (title.length > 200) {
        return { valid: false, message: '新闻标题长度不能超过200个字符' }
    }

    return { valid: true }
}

/**
 * 验证新闻内容
 */
export const validateNewsContent = (content: string): { valid: boolean; message?: string } => {
    if (!content || content.trim().length === 0) {
        return { valid: false, message: '新闻内容不能为空' }
    }

    // 移除HTML标签后检查长度
    const textContent = content.replace(/<[^>]*>/g, '').trim()
    if (textContent.length < 50) {
        return { valid: false, message: '新闻内容长度不能少于50个字符' }
    }

    return { valid: true }
}

/**
 * 验证新闻摘要
 */
export const validateNewsExcerpt = (excerpt: string | undefined): { valid: boolean; message?: string } => {
    if (!excerpt) return { valid: true }

    if (excerpt.length < 10) {
        return { valid: false, message: '新闻摘要长度不能少于10个字符' }
    }

    if (excerpt.length > 500) {
        return { valid: false, message: '新闻摘要长度不能超过500个字符' }
    }

    return { valid: true }
}

/**
 * 验证新闻表单
 */
export const validateNewsForm = (form: NewsForm): { valid: boolean; errors: string[] } => {
    const errors: string[] = []

    // 验证标题
    const titleValidation = validateNewsTitle(form.title)
    if (!titleValidation.valid && titleValidation.message) {
        errors.push(titleValidation.message)
    }

    // 验证内容
    const contentValidation = validateNewsContent(form.content)
    if (!contentValidation.valid && contentValidation.message) {
        errors.push(contentValidation.message)
    }

    // 验证摘要
    const excerptValidation = validateNewsExcerpt(form.excerpt)
    if (!excerptValidation.valid && excerptValidation.message) {
        errors.push(excerptValidation.message)
    }

    // 验证分类
    if (!form.categoryId) {
        errors.push('请选择新闻分类')
    }

    // 验证作者
    if (!form.author || form.author.trim().length === 0) {
        errors.push('作者不能为空')
    } else if (form.author.length > 100) {
        errors.push('作者姓名不能超过100个字符')
    }

    return {
        valid: errors.length === 0,
        errors
    }
}

// ==================== 数据转换函数 ====================

/**
 * 将新闻数据转换为表单数据
 */
export const newsToForm = (news: News): NewsForm => {
    return {
        id: news.id,
        title: news.title,
        excerpt: news.excerpt,
        content: news.content,
        categoryId: news.categoryId,
        author: news.author,
        status: news.status,
        coverImage: news.coverImage,
        publishTime: news.publishTime,
        isFeatured: news.isFeatured,
        sortOrder: news.sortOrder,
        tagIds: news.tagIds || []
    }
}

/**
 * 生成新闻摘要（从内容中提取）
 */
export const generateNewsExcerpt = (content: string, maxLength: number = 200): string => {
    // 移除HTML标签
    const textContent = content.replace(/<[^>]*>/g, '').trim()

    // 截取指定长度
    if (textContent.length <= maxLength) {
        return textContent
    }

    // 在句号、感叹号、问号处截断
    const sentences = textContent.substring(0, maxLength).split(/[。！？]/)
    if (sentences.length > 1) {
        sentences.pop() // 移除最后一个不完整的句子
        return sentences.join('。') + '。'
    }

    return textContent.substring(0, maxLength) + '...'
}

// ==================== 统计数据处理函数 ====================

/**
 * 计算新闻发布率
 */
export const calculatePublishRate = (stats: NewsStats): number => {
    if (stats.totalNews === 0) return 0
    return Math.round((stats.publishedNews / stats.totalNews) * 100)
}

/**
 * 计算平均浏览量
 */
export const calculateAverageViews = (stats: NewsStats): number => {
    if (stats.publishedNews === 0) return 0
    return Math.round(stats.totalViews / stats.publishedNews)
}

/**
 * 格式化统计数字
 */
export const formatStatsNumber = (num: number): string => {
    if (num >= 100000000) {
        return `${(num / 100000000).toFixed(1)}亿`
    }
    if (num >= 10000) {
        return `${(num / 10000).toFixed(1)}万`
    }
    if (num >= 1000) {
        return `${(num / 1000).toFixed(1)}k`
    }
    return num.toString()
}

// ==================== 搜索和筛选函数 ====================

/**
 * 检查新闻是否匹配搜索关键词
 */
export const matchesSearchKeyword = (news: News, keyword: string): boolean => {
    if (!keyword) return true

    const searchText = keyword.toLowerCase()
    return (
        news.title.toLowerCase().includes(searchText) ||
        (news.excerpt?.toLowerCase().includes(searchText) ?? false) ||
        news.author.toLowerCase().includes(searchText) ||
        (news.categoryName?.toLowerCase().includes(searchText) ?? false)
    )
}

/**
 * 根据条件筛选新闻列表
 */
export const filterNewsList = (
    newsList: News[],
    filters: {
        keyword?: string
        status?: NewsStatus
        categoryId?: number
        isFeatured?: boolean
        author?: string
    }
): News[] => {
    return newsList.filter(news => {
        // 关键词筛选
        if (filters.keyword && !matchesSearchKeyword(news, filters.keyword)) {
            return false
        }

        // 状态筛选
        if (filters.status && news.status !== filters.status) {
            return false
        }

        // 分类筛选
        if (filters.categoryId && news.categoryId !== filters.categoryId) {
            return false
        }

        // 推荐筛选
        if (filters.isFeatured !== undefined && news.isFeatured !== filters.isFeatured) {
            return false
        }

        // 作者筛选
        if (filters.author && !news.author.toLowerCase().includes(filters.author.toLowerCase())) {
            return false
        }

        return true
    })
}

// ==================== 排序函数 ====================

/**
 * 新闻列表排序
 */
export const sortNewsList = (
    newsList: News[],
    sortBy: string,
    sortOrder: 'asc' | 'desc' = 'desc'
): News[] => {
    return [...newsList].sort((a, b) => {
        let aValue: any
        let bValue: any

        switch (sortBy) {
            case 'title':
                aValue = a.title
                bValue = b.title
                break
            case 'author':
                aValue = a.author
                bValue = b.author
                break
            case 'views':
                aValue = a.views
                bValue = b.views
                break
            case 'likes':
                aValue = a.likes
                bValue = b.likes
                break
            case 'comments':
                aValue = a.comments
                bValue = b.comments
                break
            case 'publishTime':
                aValue = a.publishTime ? new Date(a.publishTime).getTime() : 0
                bValue = b.publishTime ? new Date(b.publishTime).getTime() : 0
                break
            case 'createdAt':
                aValue = new Date(a.createdAt).getTime()
                bValue = new Date(b.createdAt).getTime()
                break
            case 'sortOrder':
                aValue = a.sortOrder
                bValue = b.sortOrder
                break
            default:
                aValue = new Date(a.createdAt).getTime()
                bValue = new Date(b.createdAt).getTime()
        }

        if (typeof aValue === 'string') {
            aValue = aValue.toLowerCase()
            bValue = bValue.toLowerCase()
        }

        if (sortOrder === 'asc') {
            return aValue > bValue ? 1 : aValue < bValue ? -1 : 0
        } else {
            return aValue < bValue ? 1 : aValue > bValue ? -1 : 0
        }
    })
}