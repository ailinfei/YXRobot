<template>
  <div class="map-visualization" :style="{ height }">
    <div class="map-controls" v-if="showControls">
      <el-select
        v-model="currentMapType"
        @change="handleMapTypeChange"
        class="map-type-selector"
        size="small"
      >
        <el-option label="世界地图" value="world" />
        <el-option label="中国地图" value="china" />
      </el-select>
      
      <el-select
        v-model="currentVisualType"
        @change="handleVisualTypeChange"
        class="visual-type-selector"
        size="small"
      >
        <el-option label="热力图" value="heatmap" />
        <el-option label="散点图" value="scatter" />
        <el-option label="气泡图" value="bubble" />
      </el-select>
    </div>

    <v-chart
      ref="chartRef"
      :option="chartOption"
      :loading="loading"
      :autoresize="true"
      @click="handleMapClick"
      class="map-chart"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { MapChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  VisualMapComponent,
  GeoComponent
} from 'echarts/components'
import VChart from 'vue-echarts'

// 注册ECharts组件
use([
  CanvasRenderer,
  MapChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  VisualMapComponent,
  GeoComponent
])

// 定义接口
interface MapDataPoint {
  name: string
  value: number | number[]
  coordinates?: [number, number]
  category?: string
  extra?: Record<string, any>
}

interface MapVisualizationProps {
  data: MapDataPoint[]
  mapType?: 'world' | 'china' | 'custom'
  visualType?: 'heatmap' | 'scatter' | 'bubble'
  colorScheme?: string[]
  showTooltip?: boolean
  showLegend?: boolean
  showControls?: boolean
  height?: string
  loading?: boolean
  title?: string
}

// Props定义
const props = withDefaults(defineProps<MapVisualizationProps>(), {
  data: () => [],
  mapType: 'world',
  visualType: 'heatmap',
  colorScheme: () => ['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8', '#ffffcc', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026'],
  showTooltip: true,
  showLegend: true,
  showControls: true,
  height: '400px',
  loading: false,
  title: ''
})

// Emits定义
const emit = defineEmits<{
  mapClick: [data: { name: string; value: any; coordinates?: [number, number] }]
  mapTypeChange: [mapType: string]
  visualTypeChange: [visualType: string]
}>()

// 响应式数据
const chartRef = ref()
const currentMapType = ref(props.mapType)
const currentVisualType = ref(props.visualType)

// 计算属性 - 图表配置
const chartOption = computed(() => {
  const baseOption = {
    title: {
      text: props.title,
      left: 'center',
      textStyle: {
        color: '#333',
        fontSize: 16,
        fontWeight: 'bold'
      }
    },
    tooltip: {
      show: props.showTooltip,
      trigger: 'item',
      formatter: (params: any) => {
        if (params.data) {
          const { name, value } = params.data
          return `${name}<br/>数值: ${Array.isArray(value) ? value.join(', ') : value}`
        }
        return params.name
      }
    },
    visualMap: {
      show: props.showLegend && currentVisualType.value === 'heatmap',
      left: 'left',
      min: 0,
      max: getMaxValue(),
      inRange: {
        color: props.colorScheme
      },
      text: ['高', '低'],
      calculable: true
    }
  }

  // 根据地图类型和可视化类型生成配置
  if (currentVisualType.value === 'heatmap') {
    return {
      ...baseOption,
      series: [{
        name: '数据分布',
        type: 'map',
        map: currentMapType.value === 'world' ? 'world' : 'china',
        roam: true,
        data: props.data.map(item => ({
          name: item.name,
          value: Array.isArray(item.value) ? item.value[0] : item.value,
          ...item.extra
        })),
        emphasis: {
          label: {
            show: true
          },
          itemStyle: {
            areaColor: '#ffeaa7'
          }
        }
      }]
    }
  } else if (currentVisualType.value === 'scatter') {
    return {
      ...baseOption,
      geo: {
        map: currentMapType.value === 'world' ? 'world' : 'china',
        roam: true,
        itemStyle: {
          areaColor: '#f3f3f3',
          borderColor: '#999'
        }
      },
      series: [{
        name: '散点数据',
        type: 'scatter',
        coordinateSystem: 'geo',
        data: props.data.filter(item => item.coordinates).map(item => ({
          name: item.name,
          value: [...(item.coordinates || [0, 0]), Array.isArray(item.value) ? item.value[0] : item.value],
          ...item.extra
        })),
        symbolSize: (val: number[]) => Math.max(val[2] / 10, 4),
        itemStyle: {
          color: '#c0392b'
        }
      }]
    }
  } else {
    // bubble 气泡图
    return {
      ...baseOption,
      geo: {
        map: currentMapType.value === 'world' ? 'world' : 'china',
        roam: true,
        itemStyle: {
          areaColor: '#f3f3f3',
          borderColor: '#999'
        }
      },
      series: [{
        name: '气泡数据',
        type: 'scatter',
        coordinateSystem: 'geo',
        data: props.data.filter(item => item.coordinates).map(item => ({
          name: item.name,
          value: [...(item.coordinates || [0, 0]), Array.isArray(item.value) ? item.value[0] : item.value],
          ...item.extra
        })),
        symbolSize: (val: number[]) => Math.sqrt(val[2]) * 2,
        itemStyle: {
          color: 'rgba(255, 69, 0, 0.6)'
        }
      }]
    }
  }
})

// 方法
const getMaxValue = () => {
  if (!props.data.length) return 100
  return Math.max(...props.data.map(item => 
    Array.isArray(item.value) ? Math.max(...item.value) : item.value
  ))
}

const handleMapClick = (params: any) => {
  if (params.data) {
    emit('mapClick', {
      name: params.data.name,
      value: params.data.value,
      coordinates: params.data.coordinates
    })
  }
}

const handleMapTypeChange = (mapType: string) => {
  currentMapType.value = mapType as 'world' | 'china'
  emit('mapTypeChange', mapType)
}

const handleVisualTypeChange = (visualType: string) => {
  currentVisualType.value = visualType as 'heatmap' | 'scatter' | 'bubble'
  emit('visualTypeChange', visualType)
}

// 加载地图数据
const loadMapData = async () => {
  try {
    if (currentMapType.value === 'world') {
      // 加载世界地图数据
      const worldMapData = await import('@/assets/maps/world.json')
      const echarts = await import('echarts')
      echarts.registerMap('world', worldMapData.default)
    } else if (currentMapType.value === 'china') {
      // 加载中国地图数据
      const chinaMapData = await import('@/assets/maps/china.json')
      const echarts = await import('echarts')
      echarts.registerMap('china', chinaMapData.default)
    }
  } catch (error) {
    console.warn('地图数据加载失败，使用默认配置:', error)
  }
}

// 监听地图类型变化
watch(currentMapType, async () => {
  await loadMapData()
  await nextTick()
  chartRef.value?.resize()
})

// 组件挂载
onMounted(async () => {
  await loadMapData()
})

// 暴露方法
defineExpose({
  resize: () => chartRef.value?.resize(),
  getChart: () => chartRef.value?.getChart()
})
</script>

<style lang="scss" scoped>
.map-visualization {
  position: relative;
  width: 100%;
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;

  .map-controls {
    position: absolute;
    top: 16px;
    right: 16px;
    z-index: 10;
    display: flex;
    gap: 8px;
    background: rgba(255, 255, 255, 0.9);
    padding: 8px;
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-sm);

    .map-type-selector,
    .visual-type-selector {
      width: 100px;
    }
  }

  .map-chart {
    width: 100%;
    height: 100%;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .map-visualization {
    .map-controls {
      position: static;
      justify-content: center;
      margin-bottom: 8px;
      background: white;
      border-bottom: 1px solid var(--border-color);
    }
  }
}
</style>