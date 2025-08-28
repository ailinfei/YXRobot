/**
 * 图片优化工具
 * 提供图片路径验证、优化和回退机制
 */

import { stateImages } from '@/assets/images'

// 图片格式映射
const IMAGE_FORMATS = {
  jpg: 'image/jpeg',
  jpeg: 'image/jpeg',
  png: 'image/png',
  gif: 'image/gif',
  svg: 'image/svg+xml',
  webp: 'image/webp'
}

// 检查图片是否存在
export const checkImageExists = (url: string): Promise<boolean> => {
  return new Promise((resolve) => {
    const img = new Image()
    img.onload = () => resolve(true)
    img.onerror = () => resolve(false)
    img.src = url
  })
}

// 获取图片尺寸
export const getImageDimensions = (url: string): Promise<{ width: number; height: number }> => {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => {
      resolve({ width: img.naturalWidth, height: img.naturalHeight })
    }
    img.onerror = () => reject(new Error('Failed to load image'))
    img.src = url
  })
}

// 生成响应式图片URL
export const generateResponsiveImageUrl = (baseUrl: string, width: number, quality = 80): string => {
  // 如果是本地图片，直接返回
  if (baseUrl.startsWith('/')) {
    return baseUrl
  }
  
  // 如果是外部图片，可以添加参数优化
  const url = new URL(baseUrl)
  url.searchParams.set('w', width.toString())
  url.searchParams.set('q', quality.toString())
  return url.toString()
}

// 获取图片格式
export const getImageFormat = (url: string): string | null => {
  const extension = url.split('.').pop()?.toLowerCase()
  return extension ? IMAGE_FORMATS[extension as keyof typeof IMAGE_FORMATS] || null : null
}

// 图片预加载
export const preloadImage = (url: string): Promise<void> => {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => resolve()
    img.onerror = () => reject(new Error(`Failed to preload image: ${url}`))
    img.src = url
  })
}

// 批量预加载图片
export const preloadImages = async (urls: string[]): Promise<void> => {
  const promises = urls.map(url => preloadImage(url).catch(() => {
    console.warn(`Failed to preload image: ${url}`)
  }))
  await Promise.all(promises)
}

// 获取安全的图片URL（带回退）
export const getSafeImageUrl = async (url: string, fallback?: string): Promise<string> => {
  if (!url) {
    return fallback || stateImages.noData
  }

  try {
    const exists = await checkImageExists(url)
    if (exists) {
      return url
    } else {
      console.warn(`Image not found: ${url}, using fallback`)
      return fallback || stateImages.noData
    }
  } catch (error) {
    console.error(`Error checking image: ${url}`, error)
    return fallback || stateImages.noData
  }
}

// 图片懒加载观察器
export const createImageObserver = (callback: (entry: IntersectionObserverEntry) => void) => {
  const options = {
    root: null,
    rootMargin: '50px',
    threshold: 0.1
  }

  return new IntersectionObserver((entries) => {
    entries.forEach(callback)
  }, options)
}

// 图片压缩（用于上传）
export const compressImage = (
  file: File, 
  maxWidth = 1920, 
  maxHeight = 1080, 
  quality = 0.8
): Promise<Blob> => {
  return new Promise((resolve) => {
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')!
    const img = new Image()

    img.onload = () => {
      // 计算新尺寸
      let { width, height } = img
      
      if (width > maxWidth) {
        height = (height * maxWidth) / width
        width = maxWidth
      }
      
      if (height > maxHeight) {
        width = (width * maxHeight) / height
        height = maxHeight
      }

      canvas.width = width
      canvas.height = height

      // 绘制并压缩
      ctx.drawImage(img, 0, 0, width, height)
      canvas.toBlob(resolve, 'image/jpeg', quality)
    }

    img.src = URL.createObjectURL(file)
  })
}

// 验证图片文件
export const validateImageFile = (file: File): { valid: boolean; error?: string } => {
  const maxSize = 5 * 1024 * 1024 // 5MB
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/svg+xml', 'image/webp']

  if (!allowedTypes.includes(file.type)) {
    return { valid: false, error: '不支持的图片格式' }
  }

  if (file.size > maxSize) {
    return { valid: false, error: '图片文件过大，请选择小于5MB的图片' }
  }

  return { valid: true }
}