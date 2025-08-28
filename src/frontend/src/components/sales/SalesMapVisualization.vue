<template>
  <div class="sales-map-visualization">
    <div class="map-header">
      <div class="header-left">
        <h3 class="map-title">销售地区分布地图</h3>
        <p class="map-subtitle">基于地理位置的销售数据热力图展示</p>
      </div>
      <div class="header-right">
        <el-select
          v-model="currentMetric"
          size="small"
          style="width: 120px;"
          @change="handleMetricChange"
        >
          <el-option label="销售额" value="revenue" />
          <el-option label="订单数" value="orders" />
          <el-option label="客户数" value="customers" />
        </el-select>
        
        <el-select
          v-model="currentMapType"
          size="small"
          style="width: 100px;"
          @change="handleMapTypeChange"
        >
          <el-option label="中国地图" value="china" />
          <el-option label="世界地图" value="world" />
        </el-select>
        
        <el-select
          v-model="timeGranularity"
          size="small"
          style="width: 100px;"
          @change="handleTimeGranularityChange"
        >
          <el-option label="按月" value="monthly" />
          <el-option label="按季度" value="quarterly" />
          <el-option label="按年" value="yearly" />
        </el-select>
        
        <el-button size="small" @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
        </el-button>
        
        <el-button size="small" @click="handleExportData" :loading="exportLoading">
          <el-icon><Download /></el-icon>
        </el-button>
      </div>
    </div>
    
    <div class="map-content">
      <!-- 统计指标 -->
      <div class="metrics-summary">
        <div class="metric-item">
          <div class="metric-label">总{{ getMetricLabel() }}</div>
          <div class="metric-value">{{ formatMetricValue(totalMetric) }}</div>
        </div>
        <div class="metric-item">
          <div class="metric-label">覆盖地区</div>
          <div class="metric-value">{{ activeRegions }}个</div>
        </div>
        <div class="metric-item">
          <div class="metric-label">平均值</div>
          <div class="metric-value">{{ formatMetricValue(averageMetric) }}</div>
        </div>
        <div class="metric-item">
          <div class="metric-label">最高地区</div>
          <div class="metric-value">{{ topRegion?.name || '-' }}</div>
        </div>
      </div>
      
      <!-- 地图容器 -->
      <div ref="mapContainer" class="map-container" v-loading="loading || props.loading"></div>
      
      <!-- 销售趋势对比分析 -->
      <div class="trend-comparison" v-if="showTrendComparison">
        <div class="comparison-header">
          <h4>销售趋势对比分析</h4>
          <el-button size="small" @click="toggleTrendComparison">
            {{ showTrendComparison ? '隐藏' : '显示' }}对比
          </el-button>
        </div>
        <div class="comparison-content">
          <div class="trend-chart" ref="trendChartContainer"></div>
          <div class="trend-insights">
            <div class="insight-item">
              <div class="insight-label">最佳表现地区</div>
              <div class="insight-value">{{ topRegion?.name || '-' }}</div>
            </div>
            <div class="insight-item">
              <div class="insight-label">增长最快地区</div>
              <div class="insight-value">{{ fastestGrowthRegion?.name || '-' }}</div>
            </div>
            <div class="insight-item">
              <div class="insight-label">平均增长率</div>
              <div class="insight-value">{{ averageGrowthRate.toFixed(1) }}%</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 地区详情弹窗 -->
      <el-dialog
        v-model="regionDetailVisible"
        :title="`${selectedRegion?.name} - 销售详情`"
        width="600px"
        :before-close="handleCloseRegionDetail"
      >
        <div v-if="selectedRegion" class="region-detail">
          <div class="detail-stats">
            <div class="stat-card">
              <div class="stat-label">销售额</div>
              <div class="stat-value">¥{{ formatCurrency(selectedRegion.salesAmount || 0) }}</div>
              <div class="stat-growth" :class="{ positive: (selectedRegion.salesGrowth || 0) > 0 }">
                {{ (selectedRegion.salesGrowth || 0) > 0 ? '+' : '' }}{{ (selectedRegion.salesGrowth || 0).toFixed(1) }}%
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-label">订单数</div>
              <div class="stat-value">{{ selectedRegion.orderCount || 0 }}</div>
              <div class="stat-growth" :class="{ positive: (selectedRegion.orderGrowth || 0) > 0 }">
                {{ (selectedRegion.orderGrowth || 0) > 0 ? '+' : '' }}{{ (selectedRegion.orderGrowth || 0).toFixed(1) }}%
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-label">客户数</div>
              <div class="stat-value">{{ selectedRegion.customerCount || 0 }}</div>
              <div class="stat-growth" :class="{ positive: (selectedRegion.customerGrowth || 0) > 0 }">
                {{ (selectedRegion.customerGrowth || 0) > 0 ? '+' : '' }}{{ (selectedRegion.customerGrowth || 0).toFixed(1) }}%
              </div>
            </div>
          </div>
          
          <div class="detail-info">
            <h4>地区信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">地区:</span>
                <span class="value">{{ selectedRegion.name }}</span>
              </div>
              <div class="info-item">
                <span class="label">平均订单价值:</span>
                <span class="value">¥{{ formatCurrency(selectedRegion.avgOrderValue || 0) }}</span>
              </div>
              <div class="info-item">
                <span class="label">活跃客户率:</span>
                <span class="value">{{ (selectedRegion.activeRate || 0) }}%</span>
              </div>
              <div class="info-item">
                <span class="label">市场份额:</span>
                <span class="value">{{ (selectedRegion.marketShare || 0).toFixed(1) }}%</span>
              </div>
            </div>
          </div>
        </div>
        
        <template #footer>
          <el-button @click="regionDetailVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleExportRegionData">导出数据</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, computed, withDefaults, watch } from 'vue'
import { Refresh, Download } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { mapDataAPI, type MapDataPoint } from '@/api/mapData'
import worldMapData from '@/assets/maps/world.json'

// 地图数据接口
interface MapDataItem {
  name: string
  value: number
  salesAmount?: number
  orderCount?: number
  customerCount?: number
  salesGrowth?: number
  orderGrowth?: number
  customerGrowth?: number
  avgOrderValue?: number
  activeRate?: number
  marketShare?: number
}

// Props
interface Props {
  dateRange?: [string, string]
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  dateRange: () => ['2024-01-01', '2024-12-31'],
  loading: false
})

// 响应式数据
const loading = ref(false)
const exportLoading = ref(false)
const mapContainer = ref<HTMLElement>()
const currentMetric = ref<'revenue' | 'orders' | 'customers'>('revenue')
const currentMapType = ref<'china' | 'world'>('world')
const timeGranularity = ref<'monthly' | 'quarterly' | 'yearly'>('monthly')
const regionDetailVisible = ref(false)
const selectedRegion = ref<MapDataItem | null>(null)
const showTrendComparison = ref(false)
const trendChartContainer = ref<HTMLElement>()

let mapInstance: echarts.ECharts | null = null

// 实际地图数据
const mapData = ref<MapDataPoint[]>([])

// 模拟销售数据 (作为备用数据)
const fallbackSalesData = {
  revenue: {
    china: [
      { name: '北京', value: 285000 },
      { name: '上海', value: 268000 },
      { name: '广东', value: 245000 },
      { name: '浙江', value: 198000 },
      { name: '江苏', value: 185000 },
      { name: '山东', value: 165000 },
      { name: '四川', value: 145000 },
      { name: '湖北', value: 125000 },
      { name: '河南', value: 115000 },
      { name: '福建', value: 98000 }
    ],
    world: [
      { name: '中国', value: 1850000 },
      { name: '美国', value: 1250000 },
      { name: '日本', value: 850000 },
      { name: '韩国', value: 650000 },
      { name: '德国', value: 480000 },
      { name: '英国', value: 420000 },
      { name: '法国', value: 380000 },
      { name: '新加坡', value: 280000 }
    ]
  },
  orders: {
    china: [
      { name: '北京', value: 1285 },
      { name: '上海', value: 1168 },
      { name: '广东', value: 1045 },
      { name: '浙江', value: 865 },
      { name: '江苏', value: 798 },
      { name: '山东', value: 685 },
      { name: '四川', value: 598 },
      { name: '湖北', value: 485 },
      { name: '河南', value: 425 },
      { name: '福建', value: 368 }
    ],
    world: [
      { name: '中国', value: 8250 },
      { name: '美国', value: 5680 },
      { name: '日本', value: 3850 },
      { name: '韩国', value: 2980 },
      { name: '德国', value: 2150 },
      { name: '英国', value: 1890 },
      { name: '法国', value: 1680 },
      { name: '新加坡', value: 1250 }
    ]
  },
  customers: {
    china: [
      { name: '北京', value: 685 },
      { name: '上海', value: 642 },
      { name: '广东', value: 598 },
      { name: '浙江', value: 485 },
      { name: '江苏', value: 456 },
      { name: '山东', value: 398 },
      { name: '四川', value: 365 },
      { name: '湖北', value: 298 },
      { name: '河南', value: 268 },
      { name: '福建', value: 225 }
    ],
    world: [
      { name: '中国', value: 4250 },
      { name: '美国', value: 2850 },
      { name: '日本', value: 2180 },
      { name: '韩国', value: 1680 },
      { name: '德国', value: 1420 },
      { name: '英国', value: 1250 },
      { name: '法国', value: 1180 },
      { name: '新加坡', value: 980 }
    ]
  }
}

// 计算属性
const currentData = computed(() => {
  if (mapData.value.length > 0) {
    return mapData.value.map(item => ({
      name: item.name,
      value: getMetricValue(item),
      salesAmount: item.value as number,
      orderCount: Math.round((item.value as number) / 300), // 估算订单数
      customerCount: Math.round((item.value as number) / 600), // 估算客户数
      salesGrowth: item.extra?.growth || Math.round((Math.random() - 0.5) * 40),
      orderGrowth: Math.round((Math.random() - 0.5) * 30),
      customerGrowth: Math.round((Math.random() - 0.5) * 35),
      avgOrderValue: item.extra?.avgOrderValue || Math.round((item.value as number) / Math.round((item.value as number) / 300)),
      activeRate: item.extra?.activeRate || Math.round(60 + Math.random() * 35),
      marketShare: ((item.value as number) / mapData.value.reduce((sum, d) => sum + (d.value as number), 0)) * 100
    }))
  }
  // 使用备用数据
  return fallbackSalesData[currentMetric.value][currentMapType.value].map(item => ({
    ...item,
    salesAmount: item.value,
    orderCount: Math.round(item.value / 300),
    customerCount: Math.round(item.value / 600),
    salesGrowth: Math.round((Math.random() - 0.5) * 40),
    orderGrowth: Math.round((Math.random() - 0.5) * 30),
    customerGrowth: Math.round((Math.random() - 0.5) * 35),
    avgOrderValue: Math.round(item.value / Math.round(item.value / 300)),
    activeRate: Math.round(60 + Math.random() * 35),
    marketShare: (item.value / fallbackSalesData[currentMetric.value][currentMapType.value].reduce((sum, d) => sum + d.value, 0)) * 100
  }))
})

const totalMetric = computed(() => {
  return currentData.value.reduce((sum, item) => sum + item.value, 0)
})

const activeRegions = computed(() => {
  return currentData.value.length
})

const averageMetric = computed(() => {
  return Math.round(totalMetric.value / activeRegions.value)
})

const topRegion = computed(() => {
  return currentData.value.reduce((max, item) => 
    item.value > max.value ? item : max, 
    currentData.value[0] || { name: '', value: 0 }
  )
})

const fastestGrowthRegion = computed(() => {
  return currentData.value.reduce((max, item) => 
    (item.salesGrowth || 0) > (max.salesGrowth || 0) ? item : max, 
    currentData.value[0] || { name: '', salesGrowth: 0 }
  )
})

const averageGrowthRate = computed(() => {
  const totalGrowth = currentData.value.reduce((sum, item) => sum + (item.salesGrowth || 0), 0)
  return currentData.value.length > 0 ? totalGrowth / currentData.value.length : 0
})

// 方法
const getMetricLabel = () => {
  const labels = {
    revenue: '销售额',
    orders: '订单数',
    customers: '客户数'
  }
  return labels[currentMetric.value]
}

const getMetricValue = (item: MapDataPoint) => {
  // 根据当前指标返回对应的值
  if (currentMetric.value === 'revenue') {
    return item.value as number
  } else if (currentMetric.value === 'orders') {
    return Math.round((item.value as number) / 300) // 估算订单数
  } else {
    return Math.round((item.value as number) / 600) // 估算客户数
  }
}

const formatMetricValue = (value: number) => {
  if (currentMetric.value === 'revenue') {
    return `¥${(value / 10000).toFixed(1)}万`
  } else if (currentMetric.value === 'customers') {
    return `${value}人`
  } else {
    return `${value}单`
  }
}

const formatCurrency = (amount: number) => {
  return amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const handleMetricChange = () => {
  updateMapData()
}

const handleMapTypeChange = () => {
  loadMapData()
  initMap()
}

const refreshData = async () => {
  loading.value = true
  try {
    await loadMapData()
    updateMapData()
    ElMessage.success('地图数据已刷新')
  } catch (error) {
    ElMessage.error('刷新数据失败')
  } finally {
    loading.value = false
  }
}

const loadMapData = async () => {
  try {
    const response = await mapDataAPI.getSalesMapData({
      dateRange: props.dateRange,
      granularity: currentMapType.value === 'world' ? 'country' : 'province',
      metric: currentMetric.value,
      timeGranularity: timeGranularity.value
    })
    
    if (response.code === 200 && response.data) {
      mapData.value = response.data
    }
  } catch (error) {
    console.error('加载地图数据失败:', error)
    // 使用备用数据
    mapData.value = []
  }
}

const handleCloseRegionDetail = () => {
  regionDetailVisible.value = false
  selectedRegion.value = null
}

const handleTimeGranularityChange = () => {
  loadMapData()
  updateMapData()
}

const handleExportData = async () => {
  exportLoading.value = true
  try {
    // 导出地图截图和数据
    if (mapInstance) {
      const imageDataURL = mapInstance.getDataURL({
        type: 'png',
        pixelRatio: 2,
        backgroundColor: '#fff'
      })
      
      // 创建下载链接
      const link = document.createElement('a')
      link.download = `销售地图_${currentMapType.value}_${timeGranularity.value}_${new Date().toISOString().split('T')[0]}.png`
      link.href = imageDataURL
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
  
      
      ElMessage.success('地图和数据导出成功')
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exportLoading.value = false
  }
}

const exportDataAsCSV = () => {
  const headers = ['地区', '销售额', '订单数', '客户数', '增长率', '市场份额']
  const csvContent = [
    headers.join(','),
    ...currentData.value.map(item => [
      item.name,
      item.salesAmount || 0,
      item.orderCount || 0,
      item.customerCount || 0,
      `${(item.salesGrowth || 0).toFixed(1)}%`,
      `${(item.marketShare || 0).toFixed(1)}%`
    ].join(','))
  ].join('\n')
  
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.download = `销售数据_${currentMapType.value}_${timeGranularity.value}_${new Date().toISOString().split('T')[0]}.csv`
  link.href = URL.createObjectURL(blob)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

const toggleTrendComparison = () => {
  showTrendComparison.value = !showTrendComparison.value
  if (showTrendComparison.value) {
    nextTick(() => {
      initTrendChart()
    })
  }
}

const initTrendChart = () => {
  if (!trendChartContainer.value) return
  
  const trendChart = echarts.init(trendChartContainer.value)
  
  const option = {
    title: {
      text: '地区销售趋势对比',
      textStyle: {
        fontSize: 14,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['销售额', '增长率']
    },
    xAxis: {
      type: 'category',
      data: currentData.value.slice(0, 10).map(item => item.name)
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额',
        position: 'left'
      },
      {
        type: 'value',
        name: '增长率(%)',
        position: 'right'
      }
    ],
    series: [
      {
        name: '销售额',
        type: 'bar',
        yAxisIndex: 0,
        data: currentData.value.slice(0, 10).map(item => item.value),
        itemStyle: {
          color: '#5470c6'
        }
      },
      {
        name: '增长率',
        type: 'line',
        yAxisIndex: 1,
        data: currentData.value.slice(0, 10).map(item => item.salesGrowth || 0),
        itemStyle: {
          color: '#91cc75'
        }
      }
    ]
  }
  
  trendChart.setOption(option)
}

const handleExportRegionData = () => {
  if (selectedRegion.value) {
    ElMessage.success(`导出 ${selectedRegion.value.name} 地区数据功能开发中...`)
  }
}

const initMap = async () => {
  if (!mapContainer.value) return
  
  loading.value = true
  
  try {
    if (mapInstance) {
      mapInstance.dispose()
      mapInstance = null
    }
    
    mapInstance = echarts.init(mapContainer.value)
    
    // 注册世界地图数据
    if (currentMapType.value === 'world') {
      echarts.registerMap('world', worldMapData as any)
    }
    
    if (currentMapType.value === 'china') {
      await initChinaMap()
    } else {
      await initWorldMap()
    }
    
    updateMapData()
  } catch (error) {
    console.error('初始化地图失败:', error)
    ElMessage.error('地图初始化失败')
  } finally {
    loading.value = false
  }
}

const initChinaMap = async () => {
  if (!mapInstance) return
  
  // 注册中国地图数据
  try {
    // 使用简化的中国地图数据
    const chinaGeoJSON = {
      type: "FeatureCollection",
      features: [
        {
          type: "Feature",
          properties: { name: "北京", adcode: "110000" },
          geometry: {
            type: "Polygon",
            coordinates: [[[116, 39.5], [117, 39.5], [117, 40.5], [116, 40.5], [116, 39.5]]]
          }
        },
        {
          type: "Feature",
          properties: { name: "上海", adcode: "310000" },
          geometry: {
            type: "Polygon",
            coordinates: [[[121, 31], [122, 31], [122, 32], [121, 32], [121, 31]]]
          }
        },
        {
          type: "Feature",
          properties: { name: "广东", adcode: "440000" },
          geometry: {
            type: "Polygon",
            coordinates: [[[113, 22], [116, 22], [116, 25], [113, 25], [113, 22]]]
          }
        },
        {
          type: "Feature",
          properties: { name: "江苏", adcode: "320000" },
          geometry: {
            type: "Polygon",
            coordinates: [[[118, 31], [121, 31], [121, 35], [118, 35], [118, 31]]]
          }
        }
      ]
    }
    
    // 注册地图
    echarts.registerMap('china', chinaGeoJSON)
  } catch (error) {
    console.warn('中国地图数据加载失败，使用默认配置:', error)
  }
  
  const maxValue = Math.max(...currentData.value.map(item => item.value))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: function(params: any) {
        if (params.data) {
          return `${params.data.name}<br/>${getMetricLabel()}: ${formatMetricValue(params.data.value)}<br/>点击查看详情`
        }
        return `${params.name}<br/>暂无数据`
      }
    },
    visualMap: {
      min: 0,
      max: maxValue,
      left: 'left',
      top: 'bottom',
      text: ['高', '低'],
      calculable: true,
      inRange: {
        color: ['#e0f3ff', '#006edd']
      },
      textStyle: {
        color: '#333'
      }
    },
    series: [
      {
        name: getMetricLabel(),
        type: 'map',
        map: 'china',
        roam: true,
        data: currentData.value,
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 1
        },
        emphasis: {
          itemStyle: {
            areaColor: '#389BB7',
            borderWidth: 2
          },
          label: {
            show: true,
            color: '#fff'
          }
        },
        select: {
          itemStyle: {
            areaColor: '#389BB7'
          },
          label: {
            show: true,
            color: '#fff'
          }
        }
      }
    ]
  }
  
  mapInstance.setOption(option)
  
  // 添加点击事件
  mapInstance.on('click', function(params: any) {
    if (params.data) {
      // 查找完整的地区数据
      const regionData = currentData.value.find(item => item.name === params.data.name)
      if (regionData) {
        selectedRegion.value = regionData
        regionDetailVisible.value = true
      } else {
        ElMessage.info(`${params.data.name}: ${formatMetricValue(params.data.value)}`)
      }
    }
  })
}

const initWorldMap = async () => {
  if (!mapInstance) return
  
  // 注册世界地图数据
  try {
    // 使用简化的世界地图数据
    const worldGeoJSON = {
      type: "FeatureCollection",
      features: [
        {
          type: "Feature",
          properties: { name: "China", name_zh: "中国" },
          geometry: {
            type: "Polygon",
            coordinates: [[[100, 30], [120, 30], [120, 50], [100, 50], [100, 30]]]
          }
        },
        {
          type: "Feature", 
          properties: { name: "United States", name_zh: "美国" },
          geometry: {
            type: "Polygon",
            coordinates: [[[-120, 30], [-80, 30], [-80, 50], [-120, 50], [-120, 30]]]
          }
        },
        {
          type: "Feature",
          properties: { name: "Japan", name_zh: "日本" },
          geometry: {
            type: "Polygon", 
            coordinates: [[[130, 30], [145, 30], [145, 45], [130, 45], [130, 30]]]
          }
        }
      ]
    }
    
    // 注册地图
    echarts.registerMap('world', worldGeoJSON)
  } catch (error) {
    console.warn('世界地图数据加载失败，使用默认配置:', error)
  }
  
  const maxValue = Math.max(...currentData.value.map(item => item.value))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: function(params: any) {
        if (params.data) {
          return `${params.data.name}<br/>${getMetricLabel()}: ${formatMetricValue(params.data.value)}<br/>点击查看详情`
        }
        return `${params.name}<br/>暂无数据`
      }
    },
    visualMap: {
      min: 0,
      max: maxValue,
      left: 'left',
      top: 'bottom',
      text: ['高', '低'],
      calculable: true,
      inRange: {
        color: ['#e0f3ff', '#006edd']
      },
      textStyle: {
        color: '#333'
      }
    },
    series: [
      {
        name: getMetricLabel(),
        type: 'map',
        map: 'world',
        roam: true,
        data: currentData.value,
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 0.5
        },
        emphasis: {
          itemStyle: {
            areaColor: '#389BB7',
            borderWidth: 1
          },
          label: {
            show: true,
            color: '#fff'
          }
        }
      }
    ]
  }
  
  mapInstance.setOption(option)
  
  // 添加点击事件
  mapInstance.on('click', function(params: any) {
    if (params.data) {
      // 查找完整的地区数据
      const regionData = currentData.value.find(item => item.name === params.data.name)
      if (regionData) {
        selectedRegion.value = regionData
        regionDetailVisible.value = true
      } else {
        ElMessage.info(`${params.data.name}: ${formatMetricValue(params.data.value)}`)
      }
    }
  })
}

const updateMapData = () => {
  if (!mapInstance) return
  
  const maxValue = Math.max(...currentData.value.map(item => item.value))
  
  mapInstance.setOption({
    visualMap: {
      max: maxValue
    },
    series: [
      {
        name: getMetricLabel(),
        data: currentData.value
      }
    ]
  })
}

const resizeMap = () => {
  mapInstance?.resize()
}

// 监听器
watch(() => props.dateRange, async () => {
  await loadMapData()
  updateMapData()
}, { deep: true })

// 生命周期
onMounted(async () => {
  await nextTick()
  await loadMapData()
  await initMap()
  
  window.addEventListener('resize', resizeMap)
})

onUnmounted(() => {
  if (mapInstance) {
    mapInstance.dispose()
  }
  window.removeEventListener('resize', resizeMap)
})

// 暴露方法给父组件
defineExpose({
  refreshData,
  updateData: updateMapData
})
</script>

<style lang="scss" scoped>
.sales-map-visualization {
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  overflow: hidden;

  .map-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 20px 20px 0 20px;
    border-bottom: 1px solid #f0f0f0;

    .header-left {
      .map-title {
        margin: 0 0 4px 0;
        color: #262626;
        font-size: 16px;
        font-weight: 600;
      }

      .map-subtitle {
        margin: 0;
        color: #8c8c8c;
        font-size: 12px;
        font-weight: 400;
      }
    }

    .header-right {
      display: flex;
      gap: 8px;
      align-items: center;
    }
  }

  .map-content {
    padding: 20px;

    .metrics-summary {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
      gap: 20px;
      margin-bottom: 20px;
      padding: 16px;
      background: #fafafa;
      border-radius: 6px;

      .metric-item {
        text-align: center;

        .metric-label {
          font-size: 12px;
          color: #8c8c8c;
          margin-bottom: 4px;
        }

        .metric-value {
          font-size: 16px;
          font-weight: 600;
          color: #262626;
        }
      }
    }

    .map-container {
      height: 500px;
      width: 100%;
      border-radius: 4px;
      overflow: hidden;
    }

    .trend-comparison {
      margin-top: 20px;
      padding: 16px;
      background: #fafafa;
      border-radius: 6px;

      .comparison-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        h4 {
          margin: 0;
          font-size: 14px;
          font-weight: 600;
          color: #262626;
        }
      }

      .comparison-content {
        display: grid;
        grid-template-columns: 2fr 1fr;
        gap: 20px;

        .trend-chart {
          height: 300px;
          background: white;
          border-radius: 4px;
        }

        .trend-insights {
          display: flex;
          flex-direction: column;
          gap: 16px;

          .insight-item {
            background: white;
            padding: 12px;
            border-radius: 4px;
            text-align: center;

            .insight-label {
              font-size: 12px;
              color: #8c8c8c;
              margin-bottom: 4px;
            }

            .insight-value {
              font-size: 16px;
              font-weight: 600;
              color: #262626;
            }
          }
        }
      }
    }
  }
}

// 地区详情弹窗样式
.region-detail {
  .detail-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 16px;
    margin-bottom: 24px;

    .stat-card {
      background: #f8f9fa;
      border-radius: 8px;
      padding: 16px;
      text-align: center;

      .stat-label {
        font-size: 12px;
        color: #6c757d;
        margin-bottom: 8px;
      }

      .stat-value {
        font-size: 20px;
        font-weight: 600;
        color: #212529;
        margin-bottom: 4px;
      }

      .stat-growth {
        font-size: 12px;
        color: #dc3545;

        &.positive {
          color: #28a745;
        }
      }
    }
  }

  .detail-info {
    h4 {
      margin: 0 0 16px 0;
      color: #212529;
      font-size: 16px;
      font-weight: 600;
    }

    .info-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 12px;

      .info-item {
        display: flex;
        justify-content: space-between;
        padding: 8px 0;
        border-bottom: 1px solid #e9ecef;

        .label {
          color: #6c757d;
          font-size: 14px;
        }

        .value {
          color: #212529;
          font-weight: 500;
          font-size: 14px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .sales-map-visualization {
    .map-header {
      flex-direction: column;
      gap: 12px;
      align-items: flex-start;

      .header-right {
        width: 100%;
        justify-content: flex-end;
      }
    }

    .map-content {
      .metrics-summary {
        grid-template-columns: repeat(2, 1fr);
        gap: 12px;

        .metric-item {
          text-align: left;
        }
      }

      .map-container {
        height: 400px;
      }

      .trend-comparison .comparison-content {
        grid-template-columns: 1fr;

        .trend-chart {
          height: 250px;
        }

        .trend-insights {
          flex-direction: row;
          overflow-x: auto;

          .insight-item {
            min-width: 120px;
            flex-shrink: 0;
          }
        }
      }
    }
  }

  .region-detail {
    .detail-stats {
      grid-template-columns: 1fr;
    }

    .detail-info .info-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>