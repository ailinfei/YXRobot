<template>
  <div class="chart-container">
    <!-- 图表头部 -->
    <div class="chart-header" v-if="showHeader">
      <div class="header-left">
        <h3 class="chart-title">{{ title }}</h3>
        <p class="chart-subtitle" v-if="subtitle">{{ subtitle }}</p>
      </div>
      <div class="header-right">
        <slot name="header-actions">
          <el-dropdown v-if="showActions" @command="handleAction">
            <el-button text :icon="MoreFilled" />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="export">导出图片</el-dropdown-item>
                <el-dropdown-item command="fullscreen">全屏显示</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </slot>
      </div>
    </div>

    <!-- 图表内容 -->
    <div class="chart-content">
      <!-- 加载状态 -->
      <div v-if="loading" class="chart-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>数据加载中...</span>
      </div>
      
      <!-- 错误状态 -->
      <div v-else-if="error" class="chart-error">
        <el-icon><Warning /></el-icon>
        <span>{{ error }}</span>
        <el-button type="primary" size="small" @click="handleRetry">重试</el-button>
      </div>
      
      <!-- 空数据状态 -->
      <div v-else-if="isEmpty" class="chart-empty">
        <el-icon><DocumentRemove /></el-icon>
        <span>暂无数据</span>
      </div>
      
      <!-- 图表主体 -->
      <div 
        v-else
        ref="chartRef" 
        class="chart-main"
        :style="{ width: width, height: height }"
      ></div>
    </div>

    <!-- 图表底部信息 -->
    <div class="chart-footer" v-if="showFooter">
      <slot name="footer">
        <div class="chart-info">
          <span class="update-time" v-if="updateTime">
            更新时间: {{ formatTime(updateTime) }}
          </span>
          <span class="data-source" v-if="dataSource">
            数据来源: {{ dataSource }}
          </span>
        </div>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import {
  MoreFilled,
  Loading,
  Warning,
  DocumentRemove
} from '@element-plus/icons-vue'

// 定义接口
interface ChartProps {
  title?: string
  subtitle?: string
  width?: string
  height?: string
  option: any
  loading?: boolean
  error?: string
  isEmpty?: boolean
  showHeader?: boolean
  showFooter?: boolean
  showActions?: boolean
  updateTime?: string | Date
  dataSource?: string
  theme?: string
  autoResize?: boolean
}

// Props定义
const props = withDefaults(defineProps<ChartProps>(), {
  title: '',
  subtitle: '',
  width: '100%',
  height: '400px',
  option: () => ({}),
  loading: false,
  error: '',
  isEmpty: false,
  showHeader: true,
  showFooter: false,
  showActions: true,
  updateTime: '',
  dataSource: '',
  theme: 'default',
  autoResize: true
})

// Emits定义
const emit = defineEmits([
  'refresh',
  'export',
  'fullscreen',
  'retry',
  'chart-ready',
  'chart-click',
  'chart-hover'
])

// 响应式数据
const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

// 初始化图表
const initChart = () => {
  if (!chartRef.value) {
    console.warn('ChartContainer: chartRef is not available')
    return
  }
  
  // 销毁已存在的实例
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  try {
    // 创建新实例
    chartInstance = echarts.init(chartRef.value, props.theme)
    console.log('ChartContainer: ECharts instance created', chartInstance)
    
    // 设置配置项
    if (props.option && Object.keys(props.option).length > 0) {
      chartInstance.setOption(props.option, true)
      console.log('ChartContainer: Chart option set', props.option)
    } else {
      console.warn('ChartContainer: No chart option provided')
    }
    
    // 绑定事件
    if (chartInstance) {
      chartInstance.on('click', (params) => {
        emit('chart-click', params)
      })
      
      chartInstance.on('mouseover', (params) => {
        emit('chart-hover', params)
      })
      
      // 自动调整大小
      if (props.autoResize) {
        window.addEventListener('resize', handleResize)
      }
      
      emit('chart-ready', chartInstance)
    }
  } catch (error) {
    console.error('ChartContainer: Failed to initialize chart', error)
  }
}

// 更新图表
const updateChart = () => {
  if (chartInstance && props.option) {
    chartInstance.setOption(props.option, true)
  }
}

// 调整图表大小
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 导出图片
const exportImage = (type = 'png') => {
  if (!chartInstance) return
  
  const url = chartInstance.getDataURL({
    type: type,
    pixelRatio: 2,
    backgroundColor: '#fff'
  })
  
  // 创建下载链接
  const link = document.createElement('a')
  link.download = `chart_${Date.now()}.${type}`
  link.href = url
  link.click()
  
  ElMessage.success('图表已导出')
}

// 全屏显示
const toggleFullscreen = () => {
  const element = chartRef.value?.parentElement
  if (!element) return
  
  if (document.fullscreenElement) {
    document.exitFullscreen()
  } else {
    element.requestFullscreen()
  }
}

// 格式化时间
const formatTime = (time: string | Date) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 事件处理
const handleAction = (command: string) => {
  switch (command) {
    case 'refresh':
      emit('refresh')
      break
    case 'export':
      exportImage()
      emit('export')
      break
    case 'fullscreen':
      toggleFullscreen()
      emit('fullscreen')
      break
  }
}

const handleRetry = () => {
  emit('retry')
}

// 监听配置变化
watch(() => props.option, (newOption) => {
  console.log('ChartContainer: Option changed', newOption)
  if (chartInstance && newOption && Object.keys(newOption).length > 0) {
    chartInstance.setOption(newOption, true)
  }
}, { deep: true, immediate: true })

watch(() => props.loading, (newVal) => {
  if (chartInstance) {
    if (newVal) {
      chartInstance.showLoading('default', {
        text: '加载中...',
        color: '#FF5A5F',
        textColor: '#000',
        maskColor: 'rgba(255, 255, 255, 0.8)',
        zlevel: 0
      })
    } else {
      chartInstance.hideLoading()
    }
  }
})

// 生命周期
onMounted(() => {
  nextTick(() => {
    console.log('ChartContainer mounted, props.option:', props.option)
    initChart()
  })
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  
  if (props.autoResize) {
    window.removeEventListener('resize', handleResize)
  }
})

// 暴露方法给父组件
defineExpose({
  getChartInstance: () => chartInstance,
  resize: handleResize,
  exportImage,
  toggleFullscreen
})
</script>

<style lang="scss" scoped>
.chart-container {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px 24px 16px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-secondary);
  
  .header-left {
    .chart-title {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0 0 4px 0;
    }
    
    .chart-subtitle {
      font-size: 14px;
      color: var(--text-secondary);
      margin: 0;
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.chart-content {
  position: relative;
  
  .chart-loading,
  .chart-error,
  .chart-empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 300px;
    color: var(--text-secondary);
    gap: 12px;
    
    .el-icon {
      font-size: 48px;
      color: var(--text-light);
    }
    
    span {
      font-size: 16px;
    }
  }
  
  .chart-error {
    .el-icon {
      color: var(--el-color-warning);
    }
  }
  
  .chart-main {
    width: 100%;
    height: 400px;
    min-height: 300px;
  }
}

.chart-footer {
  padding: 12px 24px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-tertiary);
  
  .chart-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: var(--text-light);
    
    .update-time,
    .data-source {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}

// 全屏样式
:global(.chart-container:fullscreen) {
  .chart-main {
    height: calc(100vh - 120px) !important;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .chart-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
    
    .header-right {
      justify-content: flex-end;
    }
  }
  
  .chart-footer {
    .chart-info {
      flex-direction: column;
      gap: 8px;
      align-items: flex-start;
    }
  }
}
</style>