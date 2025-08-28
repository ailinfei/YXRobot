/**
 * 数据处理工具函数
 * 提供深拷贝、对象合并、数组去重等功能
 */

/**
 * 深拷贝对象
 */
export function deepClone<T>(obj: T): T {
  if (obj === null || typeof obj !== 'object') {
    return obj
  }

  if (obj instanceof Date) {
    return new Date(obj.getTime()) as unknown as T
  }

  if (obj instanceof Array) {
    return obj.map(item => deepClone(item)) as unknown as T
  }

  if (obj instanceof RegExp) {
    return new RegExp(obj) as unknown as T
  }

  if (typeof obj === 'object') {
    const clonedObj = {} as T
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }

  return obj
}

/**
 * 深度合并对象
 */
export function deepMerge<T extends Record<string, any>>(target: T, ...sources: Partial<T>[]): T {
  if (!sources.length) return target

  const source = sources.shift()
  if (!source) return target

  if (isObject(target) && isObject(source)) {
    for (const key in source) {
      if (isObject(source[key])) {
        if (!target[key]) Object.assign(target, { [key]: {} })
        deepMerge(target[key], source[key])
      } else {
        Object.assign(target, { [key]: source[key] })
      }
    }
  }

  return deepMerge(target, ...sources)
}

/**
 * 判断是否为对象
 */
function isObject(item: any): boolean {
  return item && typeof item === 'object' && !Array.isArray(item)
}

/**
 * 数组去重
 */
export function uniqueArray<T>(array: T[], key?: keyof T): T[] {
  if (!key) {
    return [...new Set(array)]
  }

  const seen = new Set()
  return array.filter(item => {
    const value = item[key]
    if (seen.has(value)) {
      return false
    }
    seen.add(value)
    return true
  })
}

/**
 * 数组分组
 */
export function groupBy<T>(array: T[], key: keyof T | ((item: T) => string)): Record<string, T[]> {
  return array.reduce((groups, item) => {
    const groupKey = typeof key === 'function' ? key(item) : String(item[key])
    if (!groups[groupKey]) {
      groups[groupKey] = []
    }
    groups[groupKey].push(item)
    return groups
  }, {} as Record<string, T[]>)
}

/**
 * 数组排序
 */
export function sortBy<T>(
  array: T[],
  key: keyof T | ((item: T) => any),
  order: 'asc' | 'desc' = 'asc'
): T[] {
  return [...array].sort((a, b) => {
    const aValue = typeof key === 'function' ? key(a) : a[key]
    const bValue = typeof key === 'function' ? key(b) : b[key]

    if (aValue < bValue) {
      return order === 'asc' ? -1 : 1
    }
    if (aValue > bValue) {
      return order === 'asc' ? 1 : -1
    }
    return 0
  })
}

/**
 * 数组分页
 */
export function paginate<T>(array: T[], page: number, pageSize: number): {
  data: T[]
  total: number
  totalPages: number
  currentPage: number
  hasNext: boolean
  hasPrev: boolean
} {
  const total = array.length
  const totalPages = Math.ceil(total / pageSize)
  const startIndex = (page - 1) * pageSize
  const endIndex = startIndex + pageSize
  const data = array.slice(startIndex, endIndex)

  return {
    data,
    total,
    totalPages,
    currentPage: page,
    hasNext: page < totalPages,
    hasPrev: page > 1
  }
}

/**
 * 数组求和
 */
export function sum<T>(array: T[], key?: keyof T | ((item: T) => number)): number {
  return array.reduce((total, item) => {
    const value = key 
      ? (typeof key === 'function' ? key(item) : Number(item[key]))
      : Number(item)
    return total + (isNaN(value) ? 0 : value)
  }, 0)
}

/**
 * 数组平均值
 */
export function average<T>(array: T[], key?: keyof T | ((item: T) => number)): number {
  if (array.length === 0) return 0
  return sum(array, key) / array.length
}

/**
 * 数组最大值
 */
export function max<T>(array: T[], key?: keyof T | ((item: T) => number)): number {
  if (array.length === 0) return 0
  
  const values = array.map(item => {
    const value = key 
      ? (typeof key === 'function' ? key(item) : Number(item[key]))
      : Number(item)
    return isNaN(value) ? -Infinity : value
  })
  
  return Math.max(...values)
}

/**
 * 数组最小值
 */
export function min<T>(array: T[], key?: keyof T | ((item: T) => number)): number {
  if (array.length === 0) return 0
  
  const values = array.map(item => {
    const value = key 
      ? (typeof key === 'function' ? key(item) : Number(item[key]))
      : Number(item)
    return isNaN(value) ? Infinity : value
  })
  
  return Math.min(...values)
}

/**
 * 获取嵌套对象的值
 */
export function getNestedValue(obj: any, path: string, defaultValue?: any): any {
  const keys = path.split('.')
  let result = obj

  for (const key of keys) {
    if (result === null || result === undefined) {
      return defaultValue
    }
    result = result[key]
  }

  return result !== undefined ? result : defaultValue
}

/**
 * 设置嵌套对象的值
 */
export function setNestedValue(obj: any, path: string, value: any): void {
  const keys = path.split('.')
  const lastKey = keys.pop()!
  let current = obj

  for (const key of keys) {
    if (!(key in current) || typeof current[key] !== 'object') {
      current[key] = {}
    }
    current = current[key]
  }

  current[lastKey] = value
}

/**
 * 删除对象中的空值
 */
export function removeEmptyValues<T extends Record<string, any>>(obj: T): Partial<T> {
  const result: Partial<T> = {}

  for (const [key, value] of Object.entries(obj)) {
    if (value !== null && value !== undefined && value !== '') {
      if (Array.isArray(value) && value.length > 0) {
        result[key as keyof T] = value
      } else if (typeof value === 'object' && !Array.isArray(value)) {
        const cleaned = removeEmptyValues(value)
        if (Object.keys(cleaned).length > 0) {
          result[key as keyof T] = cleaned as T[keyof T]
        }
      } else if (typeof value !== 'object') {
        result[key as keyof T] = value
      }
    }
  }

  return result
}

/**
 * 对象键值转换
 */
export function transformKeys<T extends Record<string, any>>(
  obj: T,
  transformer: (key: string) => string
): Record<string, any> {
  const result: Record<string, any> = {}

  for (const [key, value] of Object.entries(obj)) {
    const newKey = transformer(key)
    if (typeof value === 'object' && value !== null && !Array.isArray(value)) {
      result[newKey] = transformKeys(value, transformer)
    } else {
      result[newKey] = value
    }
  }

  return result
}

/**
 * 驼峰转下划线
 */
export function camelToSnake(str: string): string {
  return str.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`)
}

/**
 * 下划线转驼峰
 */
export function snakeToCamel(str: string): string {
  return str.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase())
}

/**
 * 对象扁平化
 */
export function flattenObject(obj: Record<string, any>, prefix = ''): Record<string, any> {
  const result: Record<string, any> = {}

  for (const [key, value] of Object.entries(obj)) {
    const newKey = prefix ? `${prefix}.${key}` : key

    if (typeof value === 'object' && value !== null && !Array.isArray(value)) {
      Object.assign(result, flattenObject(value, newKey))
    } else {
      result[newKey] = value
    }
  }

  return result
}

/**
 * 对象反扁平化
 */
export function unflattenObject(obj: Record<string, any>): Record<string, any> {
  const result: Record<string, any> = {}

  for (const [key, value] of Object.entries(obj)) {
    setNestedValue(result, key, value)
  }

  return result
}

/**
 * 数组转树形结构
 */
export function arrayToTree<T extends { id: any; parentId?: any }>(
  array: T[],
  options: {
    idKey?: keyof T
    parentIdKey?: keyof T
    childrenKey?: string
  } = {}
): (T & { children?: T[] })[] {
  const {
    idKey = 'id' as keyof T,
    parentIdKey = 'parentId' as keyof T,
    childrenKey = 'children'
  } = options

  const map = new Map()
  const roots: (T & { children?: T[] })[] = []

  // 创建映射
  array.forEach(item => {
    map.set(item[idKey], { ...item, [childrenKey]: [] })
  })

  // 构建树形结构
  array.forEach(item => {
    const node = map.get(item[idKey])
    const parentId = item[parentIdKey]

    if (parentId && map.has(parentId)) {
      map.get(parentId)[childrenKey].push(node)
    } else {
      roots.push(node)
    }
  })

  return roots
}

/**
 * 树形结构转数组
 */
export function treeToArray<T extends { children?: T[] }>(
  tree: T[],
  childrenKey = 'children'
): Omit<T, 'children'>[] {
  const result: Omit<T, 'children'>[] = []

  function traverse(nodes: T[]) {
    nodes.forEach(node => {
      const { [childrenKey]: children, ...rest } = node as any
      result.push(rest)
      
      if (children && Array.isArray(children)) {
        traverse(children)
      }
    })
  }

  traverse(tree)
  return result
}

/**
 * 生成随机ID
 */
export function generateId(length = 8): string {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return result
}

/**
 * 生成UUID
 */
export function generateUUID(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}