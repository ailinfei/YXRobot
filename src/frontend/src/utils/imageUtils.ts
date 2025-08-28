/**
 * 图片工具函数
 * 提供图片处理、加载、压缩等功能
 */

import { stateImages, lazyLoadConfig, uploadConfig } from '@/assets/images'

/**
 * 获取图片URL，支持相对路径和绝对路径
 */
export function getImageUrl(path: string): string {
  if (!path) return stateImages.noData
  
  // 如果是完整的URL，直接返回
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }
  
  // 如果是相对路径，添加基础路径
  if (path.startsWith('/')) {
    return path
  }
  
  return `/${path}`
}

/**
 * 生成占位图片URL
 */
export function generatePlaceholder(width: number, height: number, text?: string): string {
  const baseUrl = 'https://via.placeholder.com'
  const textParam = text ? `?text=${encodeURIComponent(text)}` : ''
  return `${baseUrl}/${width}x${height}/E5E7EB/9CA3AF${textParam}`
}

/**
 * 生成用户头像
 */
export function generateAvatar(name: string, size: number = 60): string {
  if (!name) return stateImages.noData
  
  const colors = ['FF5A5F', '8B5CF6', 'EC4899', 'EF4444', 'F59E0B', '10B981', '06B6D4', '3B82F6']
  const colorIndex = Math.abs(name.split('').reduce((a, b) => a + b.charCodeAt(0), 0)) % colors.length
  const color = colors[colorIndex]
  const initial = name.charAt(0).toUpperCase()
  
  return `https://via.placeholder.com/${size}x${size}/${color}/FFFFFF?text=${initial}`
}

/**
 * 图片预加载
 */
export function preloadImage(src: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => resolve()
    img.onerror = () => reject(new Error(`Failed to load image: ${src}`))
    img.src = src
  })
}

/**
 * 批量预加载图片
 */
export async function preloadImages(urls: string[]): Promise<void> {
  try {
    await Promise.all(urls.map(url => preloadImage(url)))
  } catch (error) {
    console.warn('Some images failed to preload:', error)
  }
}

/**
 * 图片压缩
 */
export function compressImage(
  file: File,
  options: {
    quality?: number
    maxWidth?: number
    maxHeight?: number
  } = {}
): Promise<Blob> {
  return new Promise((resolve, reject) => {
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')
    const img = new Image()
    
    img.onload = () => {
      const { quality = uploadConfig.quality, maxWidth = uploadConfig.maxWidth, maxHeight = uploadConfig.maxHeight } = options
      
      // 计算新尺寸
      let { width, height } = img
      if (width > maxWidth || height > maxHeight) {
        const ratio = Math.min(maxWidth / width, maxHeight / height)
        width *= ratio
        height *= ratio
      }
      
      canvas.width = width
      canvas.height = height
      
      // 绘制图片
      ctx?.drawImage(img, 0, 0, width, height)
      
      // 转换为Blob
      canvas.toBlob(
        (blob) => {
          if (blob) {
            resolve(blob)
          } else {
            reject(new Error('Failed to compress image'))
          }
        },
        file.type,
        quality
      )
    }
    
    img.onerror = () => reject(new Error('Failed to load image for compression'))
    img.src = URL.createObjectURL(file)
  })
}

/**
 * 验证图片文件
 */
export function validateImageFile(file: File): { valid: boolean; error?: string } {
  // 检查文件类型
  if (!uploadConfig.acceptedTypes.includes(file.type)) {
    return {
      valid: false,
      error: `不支持的文件类型。支持的格式：${uploadConfig.acceptedTypes.join(', ')}`
    }
  }
  
  // 检查文件大小
  if (file.size > uploadConfig.maxSize) {
    const maxSizeMB = uploadConfig.maxSize / (1024 * 1024)
    return {
      valid: false,
      error: `文件大小超出限制。最大允许 ${maxSizeMB}MB`
    }
  }
  
  return { valid: true }
}

/**
 * 获取图片信息
 */
export function getImageInfo(file: File): Promise<{
  width: number
  height: number
  size: number
  type: string
}> {
  return new Promise((resolve, reject) => {
    const img = new Image()
    
    img.onload = () => {
      resolve({
        width: img.naturalWidth,
        height: img.naturalHeight,
        size: file.size,
        type: file.type
      })
    }
    
    img.onerror = () => reject(new Error('Failed to load image'))
    img.src = URL.createObjectURL(file)
  })
}

/**
 * 图片懒加载指令
 */
export const lazyLoadDirective = {
  mounted(el: HTMLImageElement, binding: { value: string }) {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const img = entry.target as HTMLImageElement
          img.src = binding.value
          img.onload = () => {
            img.classList.add('loaded')
          }
          img.onerror = () => {
            img.src = lazyLoadConfig.error
          }
          observer.unobserve(img)
        }
      })
    })
    
    el.src = lazyLoadConfig.placeholder
    observer.observe(el)
  }
}

/**
 * 图片错误处理
 */
export function handleImageError(event: Event) {
  const img = event.target as HTMLImageElement
  if (img.src !== stateImages.noData) {
    img.src = stateImages.noData
  }
}

/**
 * 图片加载状态处理
 */
export function handleImageLoad(event: Event) {
  const img = event.target as HTMLImageElement
  img.classList.add('loaded')
}