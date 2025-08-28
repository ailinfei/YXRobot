import { ref, computed, watch } from 'vue'
import { languageApi, type Language, type ContentListItem, type TranslationProgress } from '@/api/language'

/**
 * 多语言管理组合式函数
 * 提供多语言内容的管理和操作功能
 */
export function useMultiLanguage() {
    // 响应式数据
    const languages = ref<Language[]>([])
    const currentLanguage = ref('zh')
    const loading = ref(false)
    const error = ref<string | null>(null)

    // 计算属性
    const enabledLanguages = computed(() => {
        return languages.value.filter(lang => lang.isEnabled)
    })

    const defaultLanguage = computed(() => {
        return languages.value.find(lang => lang.isDefault) || languages.value[0]
    })

    const currentLanguageInfo = computed(() => {
        return languages.value.find(lang => lang.languageCode === currentLanguage.value)
    })

    // 方法
    const loadLanguages = async () => {
        loading.value = true
        error.value = null
        try {
            const response = await languageApi.getLanguages()
            languages.value = response.data
        } catch (err) {
            error.value = err instanceof Error ? err.message : '加载语言列表失败'
            console.error('加载语言列表失败:', err)
        } finally {
            loading.value = false
        }
    }

    const addLanguage = async (languageData: {
        languageCode: string
        languageName: string
        nativeName: string
        flagIcon?: string
        sortOrder?: number
        isEnabled?: boolean
    }) => {
        try {
            const response = await languageApi.createLanguage(languageData)
            languages.value.push(response.data)
            return response.data
        } catch (err) {
            error.value = err instanceof Error ? err.message : '添加语言失败'
            throw err
        }
    }

    const updateLanguage = async (id: number, data: Partial<Language>) => {
        try {
            const response = await languageApi.updateLanguage(id, data)
            const index = languages.value.findIndex(lang => lang.id === id)
            if (index !== -1) {
                languages.value[index] = response.data
            }
            return response.data
        } catch (err) {
            error.value = err instanceof Error ? err.message : '更新语言失败'
            throw err
        }
    }

    const deleteLanguage = async (id: number) => {
        try {
            await languageApi.deleteLanguage(id)
            const index = languages.value.findIndex(lang => lang.id === id)
            if (index !== -1) {
                languages.value.splice(index, 1)
            }
        } catch (err) {
            error.value = err instanceof Error ? err.message : '删除语言失败'
            throw err
        }
    }

    const switchLanguage = (languageCode: string) => {
        if (languages.value.some(lang => lang.languageCode === languageCode)) {
            currentLanguage.value = languageCode
            // 可以在这里添加语言切换的副作用，比如更新localStorage
            localStorage.setItem('currentLanguage', languageCode)
        }
    }

    // 初始化当前语言
    const initCurrentLanguage = () => {
        const savedLanguage = localStorage.getItem('currentLanguage')
        if (savedLanguage && languages.value.some(lang => lang.languageCode === savedLanguage)) {
            currentLanguage.value = savedLanguage
        } else if (defaultLanguage.value) {
            currentLanguage.value = defaultLanguage.value.languageCode
        }
    }

    // 监听语言列表变化，初始化当前语言
    watch(languages, () => {
        if (languages.value.length > 0 && !currentLanguage.value) {
            initCurrentLanguage()
        }
    }, { immediate: true })

    return {
        // 响应式数据
        languages,
        currentLanguage,
        loading,
        error,

        // 计算属性
        enabledLanguages,
        defaultLanguage,
        currentLanguageInfo,

        // 方法
        loadLanguages,
        addLanguage,
        updateLanguage,
        deleteLanguage,
        switchLanguage,
        initCurrentLanguage
    }
}

/**
 * 多语言内容管理组合式函数
 * 提供内容的CRUD操作和翻译管理
 */
export function useMultiLanguageContent(contentType: 'website' | 'robot_ui') {
    // 响应式数据
    const contentList = ref<ContentListItem[]>([])
    const loading = ref(false)
    const error = ref<string | null>(null)
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(20)

    // 筛选条件
    const filters = ref({
        keyword: '',
        category: '',
        status: ''
    })

    // 方法
    const loadContentList = async () => {
        loading.value = true
        error.value = null
        try {
            const response = await languageApi.getContentList({
                contentType,
                page: currentPage.value,
                pageSize: pageSize.value,
                ...filters.value
            })
            contentList.value = response.data.list
            total.value = response.data.total
        } catch (err) {
            error.value = err instanceof Error ? err.message : '加载内容列表失败'
            console.error('加载内容列表失败:', err)
        } finally {
            loading.value = false
        }
    }

    const addContent = async (data: {
        contentKey: string
        category: string
        defaultContent: string
    }) => {
        try {
            const response = await languageApi.createContent({
                ...data,
                contentType
            })
            await loadContentList() // 重新加载列表
            return response.data
        } catch (err) {
            error.value = err instanceof Error ? err.message : '添加内容失败'
            throw err
        }
    }

    const updateContent = async (id: number, data: any) => {
        try {
            const response = await languageApi.updateContent(id, data)
            await loadContentList() // 重新加载列表
            return response.data
        } catch (err) {
            error.value = err instanceof Error ? err.message : '更新内容失败'
            throw err
        }
    }

    const deleteContent = async (id: number) => {
        try {
            await languageApi.deleteContent(id)
            await loadContentList() // 重新加载列表
        } catch (err) {
            error.value = err instanceof Error ? err.message : '删除内容失败'
            throw err
        }
    }

    const updateTranslation = async (data: {
        contentId: number
        languageCode: string
        content: string
        title?: string
    }) => {
        try {
            const response = await languageApi.updateTranslation(data)
            // 更新本地数据
            const contentItem = contentList.value.find(item => item.id === data.contentId)
            if (contentItem) {
                contentItem.translations[data.languageCode] = data.content
                contentItem.translationStatus[data.languageCode] = 'translated'
            }
            return response.data
        } catch (err) {
            error.value = err instanceof Error ? err.message : '更新翻译失败'
            throw err
        }
    }

    const publishTranslation = async (contentId: number, languageCode: string) => {
        try {
            await languageApi.publishTranslation(contentId, languageCode)
            // 更新本地状态
            const contentItem = contentList.value.find(item => item.id === contentId)
            if (contentItem) {
                contentItem.translationStatus[languageCode] = 'published'
            }
        } catch (err) {
            error.value = err instanceof Error ? err.message : '发布翻译失败'
            throw err
        }
    }

    const updateFilters = (newFilters: Partial<typeof filters.value>) => {
        Object.assign(filters.value, newFilters)
        currentPage.value = 1 // 重置到第一页
        loadContentList()
    }

    const changePage = (page: number) => {
        currentPage.value = page
        loadContentList()
    }

    const changePageSize = (size: number) => {
        pageSize.value = size
        currentPage.value = 1
        loadContentList()
    }

    return {
        // 响应式数据
        contentList,
        loading,
        error,
        total,
        currentPage,
        pageSize,
        filters,

        // 方法
        loadContentList,
        addContent,
        updateContent,
        deleteContent,
        updateTranslation,
        publishTranslation,
        updateFilters,
        changePage,
        changePageSize
    }
}

/**
 * 翻译进度统计组合式函数
 */
export function useTranslationProgress() {
    const progressData = ref<TranslationProgress[]>([])
    const loading = ref(false)
    const error = ref<string | null>(null)

    const loadProgress = async () => {
        loading.value = true
        error.value = null
        try {
            const response = await languageApi.getTranslationProgress()
            progressData.value = response.data
        } catch (err) {
            error.value = err instanceof Error ? err.message : '加载翻译进度失败'
            console.error('加载翻译进度失败:', err)
        } finally {
            loading.value = false
        }
    }

    const getLanguageProgress = (languageCode: string) => {
        return progressData.value.filter(item => item.languageCode === languageCode)
    }

    const getContentTypeProgress = (contentType: string) => {
        return progressData.value.filter(item => item.contentType === contentType)
    }

    return {
        progressData,
        loading,
        error,
        loadProgress,
        getLanguageProgress,
        getContentTypeProgress
    }
}