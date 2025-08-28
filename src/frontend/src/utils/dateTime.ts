/**
 * 日期时间工具函数
 * 提供多时区支持、格式化、相对时间计算等功能
 */

// 时区类型
export type TimeZone = 'UTC' | 'Asia/Shanghai' | 'America/New_York' | 'Europe/London' | 'Asia/Tokyo'

// 日期格式类型
export type DateFormat = 'YYYY-MM-DD' | 'YYYY-MM-DD HH:mm:ss' | 'MM/DD/YYYY' | 'DD/MM/YYYY' | 'relative'

/**
 * 格式化日期
 */
export function formatDate(
  date: Date | string | number,
  format: DateFormat = 'YYYY-MM-DD HH:mm:ss',
  timezone?: TimeZone
): string {
  const dateObj = new Date(date)
  
  if (isNaN(dateObj.getTime())) {
    return 'Invalid Date'
  }

  // 处理时区
  let targetDate = dateObj
  if (timezone && timezone !== 'UTC') {
    targetDate = new Date(dateObj.toLocaleString('en-US', { timeZone: timezone }))
  }

  // 相对时间格式
  if (format === 'relative') {
    return getRelativeTime(dateObj)
  }

  const year = targetDate.getFullYear()
  const month = String(targetDate.getMonth() + 1).padStart(2, '0')
  const day = String(targetDate.getDate()).padStart(2, '0')
  const hours = String(targetDate.getHours()).padStart(2, '0')
  const minutes = String(targetDate.getMinutes()).padStart(2, '0')
  const seconds = String(targetDate.getSeconds()).padStart(2, '0')

  switch (format) {
    case 'YYYY-MM-DD':
      return `${year}-${month}-${day}`
    case 'YYYY-MM-DD HH:mm:ss':
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    case 'MM/DD/YYYY':
      return `${month}/${day}/${year}`
    case 'DD/MM/YYYY':
      return `${day}/${month}/${year}`
    default:
      return targetDate.toISOString()
  }
}

/**
 * 获取相对时间
 */
export function getRelativeTime(date: Date | string | number): string {
  const now = new Date()
  const targetDate = new Date(date)
  const diffMs = now.getTime() - targetDate.getTime()
  const diffSeconds = Math.floor(diffMs / 1000)
  const diffMinutes = Math.floor(diffSeconds / 60)
  const diffHours = Math.floor(diffMinutes / 60)
  const diffDays = Math.floor(diffHours / 24)
  const diffWeeks = Math.floor(diffDays / 7)
  const diffMonths = Math.floor(diffDays / 30)
  const diffYears = Math.floor(diffDays / 365)

  if (diffSeconds < 60) {
    return '刚刚'
  } else if (diffMinutes < 60) {
    return `${diffMinutes}分钟前`
  } else if (diffHours < 24) {
    return `${diffHours}小时前`
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else if (diffWeeks < 4) {
    return `${diffWeeks}周前`
  } else if (diffMonths < 12) {
    return `${diffMonths}个月前`
  } else {
    return `${diffYears}年前`
  }
}

/**
 * 解析日期字符串
 */
export function parseDate(dateString: string): Date | null {
  // 尝试多种日期格式
  const formats = [
    /^\d{4}-\d{2}-\d{2}$/, // YYYY-MM-DD
    /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/, // YYYY-MM-DD HH:mm:ss
    /^\d{2}\/\d{2}\/\d{4}$/, // MM/DD/YYYY
    /^\d{2}\/\d{2}\/\d{4}$/, // DD/MM/YYYY
  ]

  const date = new Date(dateString)
  if (!isNaN(date.getTime())) {
    return date
  }

  return null
}

/**
 * 获取日期范围
 */
export function getDateRange(
  type: 'today' | 'yesterday' | 'thisWeek' | 'lastWeek' | 'thisMonth' | 'lastMonth' | 'thisYear' | 'lastYear',
  timezone?: TimeZone
): { start: Date; end: Date } {
  const now = new Date()
  let start: Date
  let end: Date

  switch (type) {
    case 'today':
      start = new Date(now.getFullYear(), now.getMonth(), now.getDate())
      end = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59)
      break
    case 'yesterday':
      start = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1)
      end = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1, 23, 59, 59)
      break
    case 'thisWeek':
      const dayOfWeek = now.getDay()
      start = new Date(now.getFullYear(), now.getMonth(), now.getDate() - dayOfWeek)
      end = new Date(now.getFullYear(), now.getMonth(), now.getDate() + (6 - dayOfWeek), 23, 59, 59)
      break
    case 'lastWeek':
      const lastWeekStart = new Date(now.getFullYear(), now.getMonth(), now.getDate() - now.getDay() - 7)
      start = lastWeekStart
      end = new Date(lastWeekStart.getFullYear(), lastWeekStart.getMonth(), lastWeekStart.getDate() + 6, 23, 59, 59)
      break
    case 'thisMonth':
      start = new Date(now.getFullYear(), now.getMonth(), 1)
      end = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59)
      break
    case 'lastMonth':
      start = new Date(now.getFullYear(), now.getMonth() - 1, 1)
      end = new Date(now.getFullYear(), now.getMonth(), 0, 23, 59, 59)
      break
    case 'thisYear':
      start = new Date(now.getFullYear(), 0, 1)
      end = new Date(now.getFullYear(), 11, 31, 23, 59, 59)
      break
    case 'lastYear':
      start = new Date(now.getFullYear() - 1, 0, 1)
      end = new Date(now.getFullYear() - 1, 11, 31, 23, 59, 59)
      break
    default:
      start = now
      end = now
  }

  return { start, end }
}

/**
 * 计算两个日期之间的差异
 */
export function getDateDiff(
  date1: Date | string | number,
  date2: Date | string | number,
  unit: 'days' | 'hours' | 'minutes' | 'seconds' = 'days'
): number {
  const d1 = new Date(date1)
  const d2 = new Date(date2)
  const diffMs = Math.abs(d2.getTime() - d1.getTime())

  switch (unit) {
    case 'seconds':
      return Math.floor(diffMs / 1000)
    case 'minutes':
      return Math.floor(diffMs / (1000 * 60))
    case 'hours':
      return Math.floor(diffMs / (1000 * 60 * 60))
    case 'days':
      return Math.floor(diffMs / (1000 * 60 * 60 * 24))
    default:
      return diffMs
  }
}

/**
 * 添加时间
 */
export function addTime(
  date: Date | string | number,
  amount: number,
  unit: 'years' | 'months' | 'days' | 'hours' | 'minutes' | 'seconds'
): Date {
  const result = new Date(date)

  switch (unit) {
    case 'years':
      result.setFullYear(result.getFullYear() + amount)
      break
    case 'months':
      result.setMonth(result.getMonth() + amount)
      break
    case 'days':
      result.setDate(result.getDate() + amount)
      break
    case 'hours':
      result.setHours(result.getHours() + amount)
      break
    case 'minutes':
      result.setMinutes(result.getMinutes() + amount)
      break
    case 'seconds':
      result.setSeconds(result.getSeconds() + amount)
      break
  }

  return result
}

/**
 * 判断是否为同一天
 */
export function isSameDay(date1: Date | string | number, date2: Date | string | number): boolean {
  const d1 = new Date(date1)
  const d2 = new Date(date2)
  
  return d1.getFullYear() === d2.getFullYear() &&
         d1.getMonth() === d2.getMonth() &&
         d1.getDate() === d2.getDate()
}

/**
 * 判断是否为工作日
 */
export function isWeekday(date: Date | string | number): boolean {
  const d = new Date(date)
  const day = d.getDay()
  return day >= 1 && day <= 5
}

/**
 * 获取月份的天数
 */
export function getDaysInMonth(year: number, month: number): number {
  return new Date(year, month, 0).getDate()
}

/**
 * 获取时区偏移
 */
export function getTimezoneOffset(timezone: TimeZone): number {
  const date = new Date()
  const utc = date.getTime() + (date.getTimezoneOffset() * 60000)
  
  switch (timezone) {
    case 'UTC':
      return 0
    case 'Asia/Shanghai':
      return 8
    case 'America/New_York':
      return -5 // 标准时间，夏令时为-4
    case 'Europe/London':
      return 0 // 标准时间，夏令时为+1
    case 'Asia/Tokyo':
      return 9
    default:
      return 0
  }
}

/**
 * 转换时区
 */
export function convertTimezone(
  date: Date | string | number,
  fromTimezone: TimeZone,
  toTimezone: TimeZone
): Date {
  const d = new Date(date)
  const fromOffset = getTimezoneOffset(fromTimezone)
  const toOffset = getTimezoneOffset(toTimezone)
  const diffHours = toOffset - fromOffset
  
  return addTime(d, diffHours, 'hours')
}

/**
 * 格式化持续时间
 */
export function formatDuration(milliseconds: number): string {
  const seconds = Math.floor(milliseconds / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (days > 0) {
    return `${days}天 ${hours % 24}小时 ${minutes % 60}分钟`
  } else if (hours > 0) {
    return `${hours}小时 ${minutes % 60}分钟`
  } else if (minutes > 0) {
    return `${minutes}分钟 ${seconds % 60}秒`
  } else {
    return `${seconds}秒`
  }
}

/**
 * 创建日期选择器的快捷选项
 */
export function createDatePickerShortcuts() {
  return [
    {
      text: '今天',
      value: () => {
        const { start, end } = getDateRange('today')
        return [start, end]
      }
    },
    {
      text: '昨天',
      value: () => {
        const { start, end } = getDateRange('yesterday')
        return [start, end]
      }
    },
    {
      text: '本周',
      value: () => {
        const { start, end } = getDateRange('thisWeek')
        return [start, end]
      }
    },
    {
      text: '上周',
      value: () => {
        const { start, end } = getDateRange('lastWeek')
        return [start, end]
      }
    },
    {
      text: '本月',
      value: () => {
        const { start, end } = getDateRange('thisMonth')
        return [start, end]
      }
    },
    {
      text: '上月',
      value: () => {
        const { start, end } = getDateRange('lastMonth')
        return [start, end]
      }
    }
  ]
}