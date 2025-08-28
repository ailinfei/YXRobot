/**
 * 工具函数统一导出
 * 提供所有工具函数的统一入口
 */

// 地图数据处理工具
export * from './mapUtils'

// 数据导出工具
export * from './exportUtils'

// 表单验证工具
export * from './validation'

// 日期时间工具
export * from './dateTime'

// 数据处理工具
export * from './dataUtils'

// API请求工具
export * from './request'

/**
 * 常用工具函数集合
 */
export const Utils = {
  // 格式化文件大小
  formatFileSize: (bytes: number): string => {
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  },

  // 格式化数字
  formatNumber: (num: number, decimals = 2): string => {
    return num.toLocaleString('zh-CN', {
      minimumFractionDigits: decimals,
      maximumFractionDigits: decimals
    })
  },

  // 格式化百分比
  formatPercent: (num: number, decimals = 1): string => {
    return (num * 100).toFixed(decimals) + '%'
  },

  // 格式化货币
  formatCurrency: (amount: number, currency = 'CNY'): string => {
    const symbols: Record<string, string> = {
      CNY: '¥',
      USD: '$',
      EUR: '€',
      JPY: '¥',
      GBP: '£'
    }
    
    const symbol = symbols[currency] || currency
    return symbol + amount.toLocaleString('zh-CN', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    })
  },

  // 截断文本
  truncateText: (text: string, maxLength: number, suffix = '...'): string => {
    if (text.length <= maxLength) return text
    return text.substring(0, maxLength - suffix.length) + suffix
  },

  // 首字母大写
  capitalize: (str: string): string => {
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase()
  },

  // 驼峰转短横线
  camelToKebab: (str: string): string => {
    return str.replace(/[A-Z]/g, letter => `-${letter.toLowerCase()}`)
  },

  // 短横线转驼峰
  kebabToCamel: (str: string): string => {
    return str.replace(/-([a-z])/g, (_, letter) => letter.toUpperCase())
  },

  // 生成颜色
  generateColor: (str: string): string => {
    let hash = 0
    for (let i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash)
    }
    const color = Math.abs(hash).toString(16).substring(0, 6)
    return '#' + '000000'.substring(0, 6 - color.length) + color
  },

  // 获取随机颜色
  getRandomColor: (): string => {
    const colors = [
      '#FF5A5F', '#FF7A7F', '#FF9A9F', '#FFBABF',
      '#4CAF50', '#66BB6A', '#81C784', '#A5D6A7',
      '#2196F3', '#42A5F5', '#64B5F6', '#90CAF9',
      '#FF9800', '#FFB74D', '#FFCC02', '#FFE082',
      '#9C27B0', '#BA68C8', '#CE93D8', '#E1BEE7'
    ]
    return colors[Math.floor(Math.random() * colors.length)]
  },

  // 防抖函数
  debounce: <T extends (...args: any[]) => any>(
    func: T,
    wait: number,
    immediate = false
  ): ((...args: Parameters<T>) => void) => {
    let timeout: NodeJS.Timeout | null = null
    
    return function executedFunction(...args: Parameters<T>) {
      const later = () => {
        timeout = null
        if (!immediate) func(...args)
      }
      
      const callNow = immediate && !timeout
      
      if (timeout) clearTimeout(timeout)
      timeout = setTimeout(later, wait)
      
      if (callNow) func(...args)
    }
  },

  // 节流函数
  throttle: <T extends (...args: any[]) => any>(
    func: T,
    limit: number
  ): ((...args: Parameters<T>) => void) => {
    let inThrottle: boolean
    
    return function executedFunction(...args: Parameters<T>) {
      if (!inThrottle) {
        func(...args)
        inThrottle = true
        setTimeout(() => inThrottle = false, limit)
      }
    }
  },

  // 延迟执行
  delay: (ms: number): Promise<void> => {
    return new Promise(resolve => setTimeout(resolve, ms))
  },

  // 重试函数
  retry: async <T>(
    fn: () => Promise<T>,
    maxAttempts = 3,
    delayMs = 1000
  ): Promise<T> => {
    let lastError: any
    
    for (let attempt = 1; attempt <= maxAttempts; attempt++) {
      try {
        return await fn()
      } catch (error) {
        lastError = error
        if (attempt < maxAttempts) {
          await Utils.delay(delayMs * attempt)
        }
      }
    }
    
    throw lastError
  },

  // 检查是否为移动设备
  isMobile: (): boolean => {
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
      navigator.userAgent
    )
  },

  // 检查是否为开发环境
  isDev: (): boolean => {
    return import.meta.env.DEV
  },

  // 检查是否为生产环境
  isProd: (): boolean => {
    return import.meta.env.PROD
  },

  // 获取浏览器信息
  getBrowserInfo: () => {
    const ua = navigator.userAgent
    let browser = 'Unknown'
    let version = 'Unknown'

    if (ua.includes('Chrome')) {
      browser = 'Chrome'
      version = ua.match(/Chrome\/(\d+)/)?.[1] || 'Unknown'
    } else if (ua.includes('Firefox')) {
      browser = 'Firefox'
      version = ua.match(/Firefox\/(\d+)/)?.[1] || 'Unknown'
    } else if (ua.includes('Safari')) {
      browser = 'Safari'
      version = ua.match(/Version\/(\d+)/)?.[1] || 'Unknown'
    } else if (ua.includes('Edge')) {
      browser = 'Edge'
      version = ua.match(/Edge\/(\d+)/)?.[1] || 'Unknown'
    }

    return { browser, version }
  },

  // 复制到剪贴板
  copyToClipboard: async (text: string): Promise<boolean> => {
    try {
      if (navigator.clipboard && window.isSecureContext) {
        await navigator.clipboard.writeText(text)
        return true
      } else {
        // 降级方案
        const textArea = document.createElement('textarea')
        textArea.value = text
        textArea.style.position = 'fixed'
        textArea.style.left = '-999999px'
        textArea.style.top = '-999999px'
        document.body.appendChild(textArea)
        textArea.focus()
        textArea.select()
        const result = document.execCommand('copy')
        document.body.removeChild(textArea)
        return result
      }
    } catch (error) {
      console.error('复制失败:', error)
      return false
    }
  },

  // 下载文件
  downloadFile: (url: string, filename?: string): void => {
    const link = document.createElement('a')
    link.href = url
    if (filename) {
      link.download = filename
    }
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  },

  // 获取URL参数
  getUrlParams: (url?: string): Record<string, string> => {
    const urlObj = new URL(url || window.location.href)
    const params: Record<string, string> = {}
    
    urlObj.searchParams.forEach((value, key) => {
      params[key] = value
    })
    
    return params
  },

  // 设置URL参数
  setUrlParams: (params: Record<string, string>, url?: string): string => {
    const urlObj = new URL(url || window.location.href)
    
    Object.entries(params).forEach(([key, value]) => {
      if (value) {
        urlObj.searchParams.set(key, value)
      } else {
        urlObj.searchParams.delete(key)
      }
    })
    
    return urlObj.toString()
  }
}

// 默认导出
export default Utils