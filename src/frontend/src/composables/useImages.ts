import { ref, computed } from 'vue'
import { 
  productImages, 
  charityImages, 
  avatarImages, 
  stateImages,
  validateImageUrl,
  getSafeImageUrl 
} from '@/assets/images'

export const useImages = () => {
  const imageCache = ref<Map<string, string>>(new Map())
  const loadingImages = ref<Set<string>>(new Set())

  // 获取产品图片
  const getProductImage = async (key: keyof typeof productImages): Promise<string> => {
    const url = productImages[key]
    if (imageCache.value.has(url)) {
      return imageCache.value.get(url)!
    }

    loadingImages.value.add(url)
    try {
      const validUrl = await getSafeImageUrl(url)
      imageCache.value.set(url, validUrl)
      return validUrl
    } finally {
      loadingImages.value.delete(url)
    }
  }

  // 获取公益图片
  const getCharityImage = async (key: keyof typeof charityImages): Promise<string> => {
    const url = charityImages[key]
    if (imageCache.value.has(url)) {
      return imageCache.value.get(url)!
    }

    loadingImages.value.add(url)
    try {
      const validUrl = await getSafeImageUrl(url)
      imageCache.value.set(url, validUrl)
      return validUrl
    } finally {
      loadingImages.value.delete(url)
    }
  }

  // 获取头像图片
  const getAvatarImage = async (key: keyof typeof avatarImages): Promise<string> => {
    const url = avatarImages[key]
    if (imageCache.value.has(url)) {
      return imageCache.value.get(url)!
    }

    loadingImages.value.add(url)
    try {
      const validUrl = await getSafeImageUrl(url, avatarImages.default)
      imageCache.value.set(url, validUrl)
      return validUrl
    } finally {
      loadingImages.value.delete(url)
    }
  }

  // 获取任意图片URL
  const getImageUrl = async (url: string, fallback?: string): Promise<string> => {
    if (!url) return fallback || stateImages.noData

    if (imageCache.value.has(url)) {
      return imageCache.value.get(url)!
    }

    loadingImages.value.add(url)
    try {
      const validUrl = await getSafeImageUrl(url, fallback)
      imageCache.value.set(url, validUrl)
      return validUrl
    } finally {
      loadingImages.value.delete(url)
    }
  }

  // 预加载图片
  const preloadImages = async (urls: string[]): Promise<void> => {
    const promises = urls.map(url => getImageUrl(url))
    await Promise.all(promises)
  }

  // 清除缓存
  const clearCache = (): void => {
    imageCache.value.clear()
  }

  // 检查图片是否正在加载
  const isImageLoading = computed(() => (url: string) => {
    return loadingImages.value.has(url)
  })

  // 检查图片是否已缓存
  const isImageCached = computed(() => (url: string) => {
    return imageCache.value.has(url)
  })

  return {
    getProductImage,
    getCharityImage,
    getAvatarImage,
    getImageUrl,
    preloadImages,
    clearCache,
    isImageLoading,
    isImageCached,
    // 直接导出图片对象供模板使用
    productImages,
    charityImages,
    avatarImages,
    stateImages
  }
}

// 全局图片管理实例
export const globalImageManager = useImages()