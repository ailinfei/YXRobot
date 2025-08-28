/**
 * 地图数据处理工具函数
 * 提供坐标转换、地理编码、数据格式化等功能
 */

// 地图数据点接口
export interface MapDataPoint {
  name: string
  value: number | number[]
  coordinates?: [number, number]
  category?: string
  extra?: Record<string, any>
}

// 地理区域映射
export const REGION_COORDINATES: Record<string, [number, number]> = {
  // 主要国家坐标
  '中国': [104.195397, 35.86166],
  '美国': [-95.712891, 37.09024],
  '日本': [138.252924, 36.204824],
  '韩国': [127.766922, 35.907757],
  '德国': [10.451526, 51.165691],
  '英国': [-3.435973, 55.378051],
  '法国': [2.213749, 46.227638],
  '意大利': [12.567380, 41.871940],
  '加拿大': [-106.346771, 56.130366],
  '澳大利亚': [133.775136, -25.274398],
  '巴西': [-51.92528, -14.235004],
  '印度': [78.96288, 20.593684],
  '俄罗斯': [105.318756, 61.52401],
  '新加坡': [103.819836, 1.352083],
  '泰国': [100.992541, 15.870032],
  '马来西亚': [101.975766, 4.210484],
  '印度尼西亚': [113.921327, -0.789275],
  '菲律宾': [121.774017, 12.879721],
  '越南': [108.277199, 14.058324],
  
  // 中国主要城市坐标
  '北京': [116.407526, 39.90403],
  '上海': [121.473701, 31.230416],
  '广州': [113.280637, 23.125178],
  '深圳': [114.085947, 22.547],
  '杭州': [120.153576, 30.287459],
  '南京': [118.767413, 32.041544],
  '武汉': [114.298572, 30.584355],
  '成都': [104.065735, 30.659462],
  '西安': [108.948024, 34.263161],
  '重庆': [106.504962, 29.533155]
}

/**
 * 坐标转换 - 将地名转换为坐标
 */
export function getCoordinatesByName(name: string): [number, number] | null {
  const coordinates = REGION_COORDINATES[name]
  if (coordinates) {
    return coordinates
  }
  
  // 如果没有找到精确匹配，尝试模糊匹配
  const fuzzyMatch = Object.keys(REGION_COORDINATES).find(key => 
    key.includes(name) || name.includes(key)
  )
  
  if (fuzzyMatch) {
    return REGION_COORDINATES[fuzzyMatch]
  }
  
  return null
}

/**
 * 数据格式化 - 将原始数据转换为地图组件需要的格式
 */
export function formatMapData(
  rawData: Array<{ name: string; value: number | number[]; [key: string]: any }>,
  options: {
    addCoordinates?: boolean
    valueIndex?: number
    categoryField?: string
  } = {}
): MapDataPoint[] {
  const { addCoordinates = true, valueIndex = 0, categoryField } = options
  
  return rawData.map(item => {
    const mapPoint: MapDataPoint = {
      name: item.name,
      value: item.value,
      extra: { ...item }
    }
    
    // 添加坐标信息
    if (addCoordinates) {
      const coordinates = getCoordinatesByName(item.name)
      if (coordinates) {
        mapPoint.coordinates = coordinates
      }
    }
    
    // 添加分类信息
    if (categoryField && item[categoryField]) {
      mapPoint.category = item[categoryField]
    }
    
    // 处理数组值
    if (Array.isArray(item.value) && valueIndex < item.value.length) {
      mapPoint.value = item.value[valueIndex]
    }
    
    return mapPoint
  })
}

/**
 * 数据聚合 - 按地区聚合数据
 */
export function aggregateDataByRegion(
  data: MapDataPoint[],
  aggregateType: 'sum' | 'average' | 'count' = 'sum'
): MapDataPoint[] {
  const regionMap = new Map<string, number[]>()
  
  // 按地区分组
  data.forEach(item => {
    const region = item.name
    const value = Array.isArray(item.value) ? item.value[0] : item.value
    
    if (!regionMap.has(region)) {
      regionMap.set(region, [])
    }
    regionMap.get(region)!.push(value)
  })
  
  // 聚合计算
  const result: MapDataPoint[] = []
  regionMap.forEach((values, region) => {
    let aggregatedValue: number
    
    switch (aggregateType) {
      case 'sum':
        aggregatedValue = values.reduce((sum, val) => sum + val, 0)
        break
      case 'average':
        aggregatedValue = values.reduce((sum, val) => sum + val, 0) / values.length
        break
      case 'count':
        aggregatedValue = values.length
        break
      default:
        aggregatedValue = values.reduce((sum, val) => sum + val, 0)
    }
    
    result.push({
      name: region,
      value: aggregatedValue,
      coordinates: getCoordinatesByName(region),
      extra: {
        count: values.length,
        originalValues: values
      }
    })
  })
  
  return result
}

/**
 * 数据范围计算
 */
export function calculateDataRange(data: MapDataPoint[]): {
  min: number
  max: number
  range: number
} {
  if (data.length === 0) {
    return { min: 0, max: 100, range: 100 }
  }
  
  const values = data.map(item => 
    Array.isArray(item.value) ? Math.max(...item.value) : item.value
  )
  
  const min = Math.min(...values)
  const max = Math.max(...values)
  const range = max - min
  
  return { min, max, range }
}

/**
 * 颜色方案生成
 */
export function generateColorScheme(
  type: 'heat' | 'cool' | 'rainbow' | 'business' = 'heat',
  steps: number = 11
): string[] {
  const schemes = {
    heat: [
      '#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8',
      '#ffffcc', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026'
    ],
    cool: [
      '#f7fcfd', '#e5f5f9', '#ccece6', '#99d8c9', '#66c2a4',
      '#41ae76', '#238b45', '#006d2c', '#00441b'
    ],
    rainbow: [
      '#9e0142', '#d53e4f', '#f46d43', '#fdae61', '#fee08b',
      '#ffffbf', '#e6f598', '#abdda4', '#66c2a5', '#3288bd', '#5e4fa2'
    ],
    business: [
      '#FF5A5F', '#FF7A7F', '#FF9A9F', '#FFBABF', '#FFDADF',
      '#E5F3FF', '#CCE7FF', '#B3DBFF', '#99CFFF', '#80C3FF', '#66B7FF'
    ]
  }
  
  const baseScheme = schemes[type] || schemes.heat
  
  if (steps === baseScheme.length) {
    return baseScheme
  }
  
  // 如果需要的步数不同，进行插值
  const result: string[] = []
  const stepSize = (baseScheme.length - 1) / (steps - 1)
  
  for (let i = 0; i < steps; i++) {
    const index = Math.round(i * stepSize)
    result.push(baseScheme[Math.min(index, baseScheme.length - 1)])
  }
  
  return result
}

/**
 * 地理编码 - 根据地址获取坐标（模拟实现）
 */
export async function geocodeAddress(address: string): Promise<[number, number] | null> {
  // 这里可以集成真实的地理编码服务，如高德地图API、百度地图API等
  // 目前返回预设的坐标
  return getCoordinatesByName(address)
}

/**
 * 反向地理编码 - 根据坐标获取地址（模拟实现）
 */
export async function reverseGeocode(coordinates: [number, number]): Promise<string | null> {
  const [lng, lat] = coordinates
  
  // 简单的反向查找
  for (const [name, coords] of Object.entries(REGION_COORDINATES)) {
    const [regionLng, regionLat] = coords
    const distance = Math.sqrt(
      Math.pow(lng - regionLng, 2) + Math.pow(lat - regionLat, 2)
    )
    
    // 如果距离小于1度，认为匹配
    if (distance < 1) {
      return name
    }
  }
  
  return null
}

/**
 * 数据验证
 */
export function validateMapData(data: any[]): {
  isValid: boolean
  errors: string[]
  validData: MapDataPoint[]
} {
  const errors: string[] = []
  const validData: MapDataPoint[] = []
  
  if (!Array.isArray(data)) {
    return {
      isValid: false,
      errors: ['数据必须是数组格式'],
      validData: []
    }
  }
  
  data.forEach((item, index) => {
    if (!item.name) {
      errors.push(`第${index + 1}项缺少name字段`)
      return
    }
    
    if (item.value === undefined || item.value === null) {
      errors.push(`第${index + 1}项缺少value字段`)
      return
    }
    
    if (typeof item.value !== 'number' && !Array.isArray(item.value)) {
      errors.push(`第${index + 1}项的value字段必须是数字或数字数组`)
      return
    }
    
    validData.push({
      name: item.name,
      value: item.value,
      coordinates: item.coordinates,
      category: item.category,
      extra: item.extra || {}
    })
  })
  
  return {
    isValid: errors.length === 0,
    errors,
    validData
  }
}

/**
 * 导出地图数据为JSON
 */
export function exportMapData(data: MapDataPoint[], filename: string = 'map-data.json'): void {
  const jsonStr = JSON.stringify(data, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  URL.revokeObjectURL(url)
}

/**
 * 从CSV导入地图数据
 */
export function importMapDataFromCSV(csvText: string): MapDataPoint[] {
  const lines = csvText.trim().split('\n')
  if (lines.length < 2) {
    throw new Error('CSV文件格式不正确')
  }
  
  const headers = lines[0].split(',').map(h => h.trim())
  const nameIndex = headers.findIndex(h => h.toLowerCase().includes('name'))
  const valueIndex = headers.findIndex(h => h.toLowerCase().includes('value'))
  
  if (nameIndex === -1 || valueIndex === -1) {
    throw new Error('CSV文件必须包含name和value列')
  }
  
  const data: MapDataPoint[] = []
  
  for (let i = 1; i < lines.length; i++) {
    const values = lines[i].split(',').map(v => v.trim())
    if (values.length >= Math.max(nameIndex, valueIndex) + 1) {
      const name = values[nameIndex]
      const value = parseFloat(values[valueIndex])
      
      if (name && !isNaN(value)) {
        data.push({
          name,
          value,
          coordinates: getCoordinatesByName(name),
          extra: {}
        })
      }
    }
  }
  
  return data
}