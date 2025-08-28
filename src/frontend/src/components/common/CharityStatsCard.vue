<template>
  <div class="charity-stats-card" :class="cardClass">
    <div class="stats-icon" :class="iconClass">
      <el-icon>
        <component :is="iconComponent" />
      </el-icon>
    </div>
    
    <div class="stats-content">
      <div class="stats-value">
        <CountUp 
          :value="Number(value)" 
          :duration="animationDuration"
          :decimals="decimals"
          :suffix="unit"
          class="value-number" 
        />
      </div>
      
      <div class="stats-title">{{ title }}</div>
      
      <div class="stats-change" v-if="change !== undefined">
        <el-icon :class="changeClass">
          <component :is="changeIcon" />
        </el-icon>
        <span :class="changeClass">{{ formatChange(change) }}</span>
        <span class="change-period">{{ changePeriod }}</span>
      </div>
    </div>
    
    <div class="stats-trend" v-if="showTrend && trendData.length > 0">
      <div ref="trendChart" class="trend-chart"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { 
  User, 
  OfficeBuilding, 
  Trophy, 
  Money,
  ArrowUp,
  ArrowDown,
  Minus
} from '@element-plus/icons-vue'
import CountUp from './CountUp.vue'
import * as echarts from 'echarts'

interface CharityStatsCardProps {
  title: string
  value: string | number
  unit?: string
  change?: number
  changePeriod?: string
  theme?: 'primary' | 'success' | 'warning' | 'info' | 'danger'
  icon?: 'user' | 'building' | 'trophy' | 'money'
  decimals?: number
  animationDuration?: number
  showTrend?: boolean
  trendData?: number[]
}

const props = withDefaults(defineProps<CharityStatsCardProps>(), {
  unit: '',
  change: undefined,
  changePeriod: '较上月',
  theme: 'primary',
  icon: 'user',
  decimals: 0,
  animationDuration: 2000,
  showTrend: false,
  trendData: () => []
})

const trendChart = ref<HTMLElement>()
let trendChartInstance: echarts.ECharts | null = null

// 计算属性
const cardClass = computed(() => {
  return [`stats-card-${props.theme}`]
})

const iconClass = computed(() => {
  return [`icon-${props.theme}`]
})

const iconComponent = computed(() => {
  const iconMap = {
    user: User,
    building: OfficeBuilding,
    trophy: Trophy,
    money: Money
  }
  return iconMap[props.icon] || User
})

const changeClass = computed(() => {
  if (props.change === undefined) return ''
  
  if (props.change > 0) {
    return 'change-positive'
  } else if (props.change < 0) {
    return 'change-negative'
  } else {
    return 'change-neutral'
  }
})

const changeIcon = computed(() => {
  if (props.change === undefined) return Minus
  
  if (props.change > 0) {
    return ArrowUp
  } else if (props.change < 0) {
    return ArrowDown
  } else {
    return Minus
  }
})

// 方法
const formatChange = (change: number) => {
  const absChange = Math.abs(change)
  const prefix = change > 0 ? '+' : change < 0 ? '-' : ''
  return `${prefix}${absChange}%`
}

const initTrendChart = () => {
  if (!trendChart.value || !props.showTrend || props.trendData.length === 0) return
  
  if (trendChartInstance) {
    trendChartInstance.dispose()
  }
  
  trendChartInstance = echarts.init(trendChart.value)
  
  const option = {
    grid: {
      left: 0,
      right: 0,
      top: 0,
      bottom: 0
    },
    xAxis: {
      type: 'category',
      show: false,
      data: props.trendData.map((_, index) => index)
    },
    yAxis: {
      type: 'value',
      show: false
    },
    series: [
      {
        type: 'line',
        data: props.trendData,
        smooth: true,
        symbol: 'none',
        lineStyle: {
          width: 2,
          color: getThemeColor()
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: getThemeColor(0.3) },
              { offset: 1, color: getThemeColor(0.05) }
            ]
          }
        }
      }
    ]
  }
  
  trendChartInstance.setOption(option)
}

const getThemeColor = (opacity = 1) => {
  const colorMap = {
    primary: `rgba(64, 158, 255, ${opacity})`,
    success: `rgba(103, 194, 58, ${opacity})`,
    warning: `rgba(230, 162, 60, ${opacity})`,
    info: `rgba(144, 147, 153, ${opacity})`,
    danger: `rgba(245, 108, 108, ${opacity})`
  }
  return colorMap[props.theme] || colorMap.primary
}

// 监听趋势数据变化
watch(() => props.trendData, () => {
  if (props.showTrend) {
    nextTick(() => {
      initTrendChart()
    })
  }
}, { deep: true })

onMounted(() => {
  if (props.showTrend) {
    nextTick(() => {
      initTrendChart()
    })
  }
})
</script>

<style lang="scss" scoped>
.charity-stats-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  }
  
  &.stats-card-primary {
    border-left: 4px solid #409EFF;
    
    .stats-icon.icon-primary {
      background: linear-gradient(135deg, #409EFF, #66B1FF);
      color: white;
    }
  }
  
  &.stats-card-success {
    border-left: 4px solid #67C23A;
    
    .stats-icon.icon-success {
      background: linear-gradient(135deg, #67C23A, #85CE61);
      color: white;
    }
  }
  
  &.stats-card-warning {
    border-left: 4px solid #E6A23C;
    
    .stats-icon.icon-warning {
      background: linear-gradient(135deg, #E6A23C, #EDBF73);
      color: white;
    }
  }
  
  &.stats-card-info {
    border-left: 4px solid #909399;
    
    .stats-icon.icon-info {
      background: linear-gradient(135deg, #909399, #A6A9AD);
      color: white;
    }
  }
  
  &.stats-card-danger {
    border-left: 4px solid #F56C6C;
    
    .stats-icon.icon-danger {
      background: linear-gradient(135deg, #F56C6C, #F78989);
      color: white;
    }
  }
}

.stats-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stats-content {
  .stats-value {
    .value-number {
      font-size: 32px;
      font-weight: 700;
      color: #303133;
      line-height: 1.2;
    }
  }
  
  .stats-title {
    font-size: 14px;
    color: #606266;
    margin: 8px 0 12px 0;
    font-weight: 500;
  }
  
  .stats-change {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    
    &.change-positive {
      color: #67C23A;
    }
    
    &.change-negative {
      color: #F56C6C;
    }
    
    &.change-neutral {
      color: #909399;
    }
    
    .el-icon {
      font-size: 12px;
    }
    
    .change-period {
      color: #909399;
      margin-left: 4px;
    }
  }
}

.stats-trend {
  position: absolute;
  right: 0;
  top: 0;
  width: 80px;
  height: 100%;
  
  .trend-chart {
    width: 100%;
    height: 100%;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .charity-stats-card {
    padding: 20px;
    
    .stats-icon {
      width: 48px;
      height: 48px;
      font-size: 20px;
      margin-bottom: 12px;
    }
    
    .stats-content {
      .stats-value {
        .value-number {
          font-size: 28px;
        }
      }
      
      .stats-title {
        font-size: 13px;
      }
    }
    
    .stats-trend {
      width: 60px;
    }
  }
}
</style>