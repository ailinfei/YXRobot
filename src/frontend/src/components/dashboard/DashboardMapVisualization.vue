<template>
  <div class="dashboard-map-visualization">
    <div class="chart-header">
      <div class="header-left">
        <h3 class="chart-title">业务分布地图</h3>
        <p class="chart-subtitle">全球销售、客户、设备分布可视化</p>
      </div>
      <div class="header-right">
        <el-select
          v-model="currentDataType"
          size="small"
          style="width: 120px;"
          @change="handleDataTypeChange"
        >
          <el-option label="销售分布" value="sales" />
          <el-option label="客户分布" value="customers" />
          <el-option label="设备分布" value="devices" />
          <el-option label="租赁分布" value="rentals" />
        </el-select>
        
        <el-select
          v-model="currentMapType"
          size="small"
          style="width: 100px;"
          @change="handleMapTypeChange"
        >
          <el-option label="世界地图" value="world" />
          <el-option label="中国地图" value="china" />
        </el-select>
        
        <el-button size="small" @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
        </el-button>
      </div>
    </div>
    
    <div class="chart-content">
      <!-- 地图容器 -->
      <div ref="mapContainer" class="map-container" v-loading="loading"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, computed, watch } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

// 响应式数据
const loading = ref(false)
const mapContainer = ref<HTMLElement>()
const currentDataType = ref<'sales' | 'customers' | 'devices' | 'rentals'>('sales')
const currentMapType = ref<'world' | 'china'>('world')

let mapInstance: echarts.ECharts | null = null

// 示例数据生成函数
const generateSampleData = () => {
  const baseData = {
    world: {
      sales: [
        { name: '中国', value: 15000000, coordinates: [104.195, 35.86] },
        { name: '美国', value: 12000000, coordinates: [-95.7, 37.1] },
        { name: '日本', value: 8000000, coordinates: [138.25, 36.2] },
        { name: '德国', value: 6000000, coordinates: [10.4, 51.1] },
        { name: '英国', value: 5000000, coordinates: [-0.1, 51.5] },
        { name: '法国', value: 4500000, coordinates: [2.3, 46.2] },
        { name: '韩国', value: 3500000, coordinates: [127.8, 35.9] },
        { name: '加拿大', value: 3000000, coordinates: [-106.3, 56.1] },
        { name: '澳大利亚', value: 2800000, coordinates: [133.8, -25.3] },
        { name: '巴西', value: 2500000, coordinates: [-47.9, -15.8] },
        { name: '印度', value: 2200000, coordinates: [78.9, 20.6] },
        { name: '俄罗斯', value: 2000000, coordinates: [105.3, 61.5] }
      ],
      customers: [
        { name: '中国', value: 50000, coordinates: [104.195, 35.86] },
        { name: '美国', value: 35000, coordinates: [-95.7, 37.1] },
        { name: '日本', value: 28000, coordinates: [138.25, 36.2] },
        { name: '德国', value: 22000, coordinates: [10.4, 51.1] },
        { name: '英国', value: 18000, coordinates: [-0.1, 51.5] },
        { name: '法国', value: 15000, coordinates: [2.3, 46.2] },
        { name: '韩国', value: 12000, coordinates: [127.8, 35.9] },
        { name: '加拿大', value: 10000, coordinates: [-106.3, 56.1] },
        { name: '澳大利亚', value: 8500, coordinates: [133.8, -25.3] },
        { name: '巴西', value: 7200, coordinates: [-47.9, -15.8] }
      ],
      devices: [
        { name: '中国', value: 8500, coordinates: [104.195, 35.86] },
        { name: '美国', value: 6200, coordinates: [-95.7, 37.1] },
        { name: '日本', value: 4800, coordinates: [138.25, 36.2] },
        { name: '德国', value: 3600, coordinates: [10.4, 51.1] },
        { name: '英国', value: 2900, coordinates: [-0.1, 51.5] },
        { name: '法国', value: 2400, coordinates: [2.3, 46.2] },
        { name: '韩国', value: 2100, coordinates: [127.8, 35.9] },
        { name: '加拿大', value: 1800, coordinates: [-106.3, 56.1] }
      ],
      rentals: [
        { name: '中国', value: 8500000, coordinates: [104.195, 35.86] },
        { name: '美国', value: 6800000, coordinates: [-95.7, 37.1] },
        { name: '日本', value: 4200000, coordinates: [138.25, 36.2] },
        { name: '德国', value: 3100000, coordinates: [10.4, 51.1] },
        { name: '英国', value: 2600000, coordinates: [-0.1, 51.5] },
        { name: '法国', value: 2200000, coordinates: [2.3, 46.2] },
        { name: '韩国', value: 1800000, coordinates: [127.8, 35.9] },
        { name: '加拿大', value: 1500000, coordinates: [-106.3, 56.1] }
      ]
    },
    china: {
      sales: [
        { name: '北京', value: 15000000, coordinates: [116.4, 39.9] },
        { name: '上海', value: 12000000, coordinates: [121.5, 31.2] },
        { name: '广东', value: 10000000, coordinates: [113.3, 23.1] },
        { name: '江苏', value: 8000000, coordinates: [118.8, 32.0] },
        { name: '浙江', value: 7000000, coordinates: [120.2, 30.3] },
        { name: '山东', value: 6000000, coordinates: [117.0, 36.7] },
        { name: '河南', value: 5000000, coordinates: [113.6, 34.8] },
        { name: '四川', value: 4500000, coordinates: [104.1, 30.7] },
        { name: '湖北', value: 4000000, coordinates: [114.3, 30.6] },
        { name: '湖南', value: 3800000, coordinates: [113.0, 28.2] },
        { name: '河北', value: 3500000, coordinates: [114.5, 38.0] },
        { name: '福建', value: 3200000, coordinates: [119.3, 26.1] }
      ],
      customers: [
        { name: '北京', value: 45000, coordinates: [116.4, 39.9] },
        { name: '上海', value: 38000, coordinates: [121.5, 31.2] },
        { name: '广东', value: 32000, coordinates: [113.3, 23.1] },
        { name: '江苏', value: 28000, coordinates: [118.8, 32.0] },
        { name: '浙江', value: 25000, coordinates: [120.2, 30.3] },
        { name: '山东', value: 22000, coordinates: [117.0, 36.7] },
        { name: '河南', value: 18000, coordinates: [113.6, 34.8] },
        { name: '四川', value: 16000, coordinates: [104.1, 30.7] },
        { name: '湖北', value: 14000, coordinates: [114.3, 30.6] },
        { name: '湖南', value: 12000, coordinates: [113.0, 28.2] }
      ],
      devices: [
        { name: '北京', value: 7500, coordinates: [116.4, 39.9] },
        { name: '上海', value: 6200, coordinates: [121.5, 31.2] },
        { name: '广东', value: 5800, coordinates: [113.3, 23.1] },
        { name: '江苏', value: 4900, coordinates: [118.8, 32.0] },
        { name: '浙江', value: 4200, coordinates: [120.2, 30.3] },
        { name: '山东', value: 3800, coordinates: [117.0, 36.7] },
        { name: '河南', value: 3200, coordinates: [113.6, 34.8] },
        { name: '四川', value: 2800, coordinates: [104.1, 30.7] },
        { name: '湖北', value: 2500, coordinates: [114.3, 30.6] },
        { name: '湖南', value: 2200, coordinates: [113.0, 28.2] }
      ],
      rentals: [
        { name: '北京', value: 8500000, coordinates: [116.4, 39.9] },
        { name: '上海', value: 7200000, coordinates: [121.5, 31.2] },
        { name: '广东', value: 6800000, coordinates: [113.3, 23.1] },
        { name: '江苏', value: 5500000, coordinates: [118.8, 32.0] },
        { name: '浙江', value: 4800000, coordinates: [120.2, 30.3] },
        { name: '山东', value: 4200000, coordinates: [117.0, 36.7] },
        { name: '河南', value: 3600000, coordinates: [113.6, 34.8] },
        { name: '四川', value: 3200000, coordinates: [104.1, 30.7] },
        { name: '湖北', value: 2800000, coordinates: [114.3, 30.6] },
        { name: '湖南', value: 2500000, coordinates: [113.0, 28.2] }
      ]
    }
  }
  
  return baseData[currentMapType.value][currentDataType.value] || baseData.world.sales
}

// 计算属性
const currentData = computed(() => generateSampleData())

const totalValue = computed(() => {
  return currentData.value.reduce((sum, item) => sum + item.value, 0)
})

const activeRegions = computed(() => {
  return currentData.value.length
})

const growthRate = computed(() => {
  const rates = {
    sales: 12.5,
    customers: 8.3,
    devices: 15.2,
    rentals: 6.8
  }
  return rates[currentDataType.value]
})



// 方法
const getDataTypeLabel = () => {
  const labels = {
    sales: '销售额',
    customers: '客户数',
    devices: '设备数',
    rentals: '租赁收入'
  }
  return labels[currentDataType.value]
}

const formatMetricValue = (value: number) => {
  if (currentDataType.value === 'sales' || currentDataType.value === 'rentals') {
    return `¥${(value / 10000).toFixed(1)}万`
  } else if (currentDataType.value === 'customers') {
    return `${value.toLocaleString()}人`
  } else {
    return `${value.toLocaleString()}台`
  }
}



const getColorByValue = (value: number, maxValue: number): string => {
  if (!value || !maxValue || maxValue === 0) return '#E5E7EB'
  
  const ratio = Math.max(0, Math.min(1, value / maxValue))
  
  if (ratio > 0.8) return '#EF4444'      // 红色 - 高值
  if (ratio > 0.6) return '#F97316'      // 橙色
  if (ratio > 0.4) return '#EAB308'      // 黄色 - 中值
  if (ratio > 0.2) return '#22C55E'      // 绿色
  return '#6B7280'                       // 灰色 - 低值
}

const handleDataTypeChange = () => {
  updateChart()
}

const handleMapTypeChange = () => {
  updateChart()
}

const refreshData = () => {
  loading.value = true
  setTimeout(() => {
    updateChart()
    loading.value = false
    ElMessage.success('数据已刷新')
  }, 500)
}

const updateChart = async () => {
  if (!mapInstance) {
    console.warn('ECharts实例不存在，无法更新图表')
    return
  }
  
  const data = currentData.value
  console.log('当前数据:', data)
  
  if (!data || data.length === 0) {
    console.warn('数据为空')
    return
  }
  
  const maxValue = Math.max(...data.map(item => item.value))
  console.log('最大值:', maxValue)
  
  // 准备散点数据 - 在坐标系上显示数据点
  const scatterData = data.map(item => ({
    name: item.name,
    value: [...item.coordinates, item.value],
    itemStyle: {
      color: getColorByValue(item.value, maxValue),
      borderColor: '#fff',
      borderWidth: 2
    }
  }))
  
  console.log('散点数据:', scatterData)

  const option = {
    backgroundColor: '#ffffff',
    title: {
      text: currentMapType.value === 'world' ? '全球业务分布' : '中国业务分布',
      left: 'center',
      top: 20,
      textStyle: {
        color: '#374151',
        fontSize: 16,
        fontWeight: 'bold'
      }
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff',
        fontSize: 12
      },
      formatter: function(params: any) {
        const value = Array.isArray(params.data.value) ? params.data.value[2] : params.data.value
        return `<div style="padding: 8px;">
          <div style="font-weight: bold; margin-bottom: 4px;">${params.data.name}</div>
          <div>${getDataTypeLabel()}: ${formatMetricValue(value)}</div>
        </div>`
      }
    },
    grid: {
      left: '5%',
      right: '5%',
      top: '10%',
      bottom: '5%',
      backgroundColor: '#ffffff',
      borderColor: 'transparent',
      borderWidth: 0,
      show: false
    },
    xAxis: {
      type: 'value',
      scale: true,
      min: currentMapType.value === 'world' ? -180 : 70,
      max: currentMapType.value === 'world' ? 180 : 140,
      show: false,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { show: false },
      splitLine: { show: false }
    },
    yAxis: {
      type: 'value',
      scale: true,
      min: currentMapType.value === 'world' ? -60 : 15,
      max: currentMapType.value === 'world' ? 80 : 55,
      show: false,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { show: false },
      splitLine: { show: false }
    },
    series: [
      // 海洋背景
      {
        name: '海洋',
        type: 'scatter',
        data: [],
        symbolSize: 0,
        itemStyle: {
          color: 'transparent'
        },
        markArea: {
          silent: true,
          itemStyle: {
            color: '#e6f3ff',
            borderWidth: 0
          },
          data: [[
            {
              coord: [currentMapType.value === 'world' ? -180 : 70, currentMapType.value === 'world' ? -60 : 15]
            },
            {
              coord: [currentMapType.value === 'world' ? 180 : 140, currentMapType.value === 'world' ? 80 : 55]
            }
          ]]
        }
      },
      // 大陆背景（填充区域）
      {
        name: '大陆',
        type: 'custom',
        renderItem: function (params: any, api: any) {
          const continents = currentMapType.value === 'world' ? [
            // 北美洲
            [[-130, 60], [-60, 60], [-60, 20], [-130, 20]],
            // 南美洲
            [[-80, 10], [-35, 10], [-35, -55], [-80, -55]],
            // 欧洲
            [[-10, 70], [40, 70], [40, 35], [-10, 35]],
            // 非洲
            [[10, 35], [50, 35], [50, -35], [10, -35]],
            // 亚洲
            [[60, 80], [180, 80], [180, 10], [60, 10]],
            // 澳洲
            [[110, -10], [155, -10], [155, -45], [110, -45]]
          ] : [
            // 中国
            [[73, 53], [135, 53], [135, 18], [73, 18]]
          ]
          
          return {
            type: 'group',
            children: continents.map(continent => ({
              type: 'polygon',
              shape: {
                points: continent.map(point => api.coord(point))
              },
              style: {
                fill: '#f5f5f5',
                stroke: '#d1d5db',
                lineWidth: 1
              }
            }))
          }
        },
        data: [0]
      },
      // 国家边界线
      {
        name: '国家边界',
        type: 'lines',
        coordinateSystem: 'geo',
        data: currentMapType.value === 'world' ? [
          // 主要国家边界
          { coords: [[-95.7, 49], [-95.7, 25]] }, // 美国中线
          { coords: [[-60, 5], [-60, -30]] }, // 巴西中线
          { coords: [[2, 51], [2, 42]] }, // 法国中线
          { coords: [[10, 54], [10, 47]] }, // 德国中线
          { coords: [[104, 53], [104, 18]] }, // 中国中线
          { coords: [[138, 45], [138, 30]] }, // 日本中线
          { coords: [[77, 37], [77, 8]] }, // 印度中线
          { coords: [[37, 82], [37, 41]] }, // 俄罗斯中线
          { coords: [[133, -10], [133, -44]] } // 澳大利亚中线
        ] : [
          // 中国省份边界
          { coords: [[116, 53], [116, 18]] }, // 中国东部线
          { coords: [[104, 53], [104, 18]] }, // 中国中部线
          { coords: [[87, 53], [87, 18]] }, // 中国西部线
          { coords: [[73, 40], [135, 40]] }, // 中国中部横线
          { coords: [[73, 30], [135, 30]] } // 中国南部横线
        ],
        lineStyle: {
          color: '#cbd5e1',
          width: 0.5,
          type: 'dashed'
        },
        silent: true
      },
      // 散点系列（数据点）
      {
        name: getDataTypeLabel(),
        type: 'scatter',
        data: scatterData,
        symbolSize: function(data: any) {
          const value = Array.isArray(data) ? data[2] : (data || 0)
          const size = Math.max(20, Math.min(60, (value / maxValue) * 50))
          return size
        },
        itemStyle: {
          shadowBlur: 15,
          shadowColor: 'rgba(0, 0, 0, 0.3)',
          shadowOffsetY: 3,
          borderWidth: 3,
          borderColor: '#fff'
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 20,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
            borderWidth: 4
          },
          scale: 1.3
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{b}',
          color: '#374151',
          fontSize: 11,
          fontWeight: 'bold',
          distance: 10
        }
      }
    ],
    animation: true,
    animationDuration: 1500,
    animationEasing: 'cubicOut'
  }
  
  console.log('设置图表选项:', option)
  mapInstance.setOption(option, true)
  console.log('图表更新完成')
}





const initChart = async () => {
  if (!mapContainer.value) {
    console.warn('地图容器未找到')
    return
  }
  
  try {
    // 销毁现有实例
    if (mapInstance) {
      mapInstance.dispose()
      mapInstance = null
    }
    
    // 等待DOM更新
    await nextTick()
    
    // 确保容器有尺寸
    const containerRect = mapContainer.value.getBoundingClientRect()
    if (containerRect.width === 0 || containerRect.height === 0) {
      console.warn('地图容器尺寸为0，延迟初始化')
      setTimeout(() => initChart(), 100)
      return
    }
    
    // 创建新实例
    mapInstance = echarts.init(mapContainer.value, null, {
      renderer: 'canvas',
      useDirtyRect: false
    })
    
    console.log('ECharts实例创建成功')
    
    // 初始化图表
    await updateChart()
    
    // 监听窗口大小变化
    const resizeHandler = () => {
      if (mapInstance) {
        mapInstance.resize()
      }
    }
    
    window.addEventListener('resize', resizeHandler)
    
    // 在组件卸载时移除监听器
    onUnmounted(() => {
      window.removeEventListener('resize', resizeHandler)
    })
    
  } catch (error) {
    console.error('初始化图表失败:', error)
    ElMessage.error('图表初始化失败')
  }
}

// 监听数据变化
watch([currentDataType, currentMapType], () => {
  updateChart()
}, { deep: true })

// 生命周期
onMounted(async () => {
  await nextTick()
  await initChart()
})

onUnmounted(() => {
  if (mapInstance) {
    mapInstance.dispose()
    mapInstance = null
  }
})

// 暴露方法给父组件
defineExpose({
  refreshData
})
</script>

<style lang="scss" scoped>
.dashboard-map-visualization {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #E5E7EB;
  overflow: hidden;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 24px 24px 0 24px;
    border-bottom: 1px solid #F3F4F6;
    margin-bottom: 0;

    .header-left {
      .chart-title {
        margin: 0 0 6px 0;
        color: #111827;
        font-size: 18px;
        font-weight: 700;
        letter-spacing: -0.025em;
      }

      .chart-subtitle {
        margin: 0;
        color: #6B7280;
        font-size: 14px;
        font-weight: 400;
      }
    }

    .header-right {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }

  .chart-content {
    padding: 24px;

    .metrics-row {
      display: flex;
      gap: 24px;
      margin-bottom: 24px;
      padding: 20px;
      background: linear-gradient(135deg, #F9FAFB 0%, #F3F4F6 100%);
      border-radius: 8px;
      border: 1px solid #E5E7EB;

      .metric-item {
        flex: 1;
        text-align: center;
        position: relative;

        &:not(:last-child)::after {
          content: '';
          position: absolute;
          right: -12px;
          top: 50%;
          transform: translateY(-50%);
          width: 1px;
          height: 40px;
          background: #D1D5DB;
        }

        .metric-label {
          font-size: 13px;
          color: #6B7280;
          margin-bottom: 6px;
          font-weight: 500;
        }

        .metric-value {
          font-size: 20px;
          font-weight: 700;
          color: #111827;
          letter-spacing: -0.025em;

          &.growth-positive {
            color: #059669;
          }

          &.growth-negative {
            color: #DC2626;
          }
        }
      }
    }

    .map-container {
      height: 500px;
      width: 100%;
      margin-bottom: 0;
      border-radius: 8px;
      overflow: hidden;
      border: 1px solid #E5E7EB;
      background: #FAFAFA;
    }


  }
}



@media (max-width: 768px) {
  .dashboard-map-visualization {
    .chart-header {
      flex-direction: column;
      gap: 16px;
      align-items: flex-start;

      .header-right {
        width: 100%;
        justify-content: flex-end;
      }
    }

    .chart-content {
      padding: 16px;

      .metrics-row {
        flex-direction: column;
        gap: 16px;

        .metric-item {
          text-align: left;

          &:not(:last-child)::after {
            display: none;
          }
        }
      }

      .map-container {
        height: 400px;
      }


    }
  }
}
</style>