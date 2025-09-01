<template>
  <el-dialog
    v-model="visible"
    title="设备详情"
    width="800px"
    :before-close="handleClose"
    class="device-detail-dialog"
  >
    <div v-if="deviceData" class="device-detail-content">
      <!-- 设备基本信息 -->
      <div class="detail-section">
        <h3 class="section-title">基本信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">设备ID:</span>
            <span class="value">{{ deviceData.deviceId }}</span>
          </div>
          <div class="info-item">
            <span class="label">设备型号:</span>
            <el-tag :type="getModelTagType(deviceData.deviceModel)" size="small">
              {{ deviceData.deviceModel }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="label">当前状态:</span>
            <el-tag :type="getStatusTagType(deviceData.currentStatus)" size="small">
              {{ getStatusText(deviceData.currentStatus) }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="label">利用率:</span>
            <span class="value">{{ deviceData.utilizationRate }}%</span>
          </div>
          <div class="info-item">
            <span class="label">租赁天数:</span>
            <span class="value">{{ deviceData.totalRentalDays }}天</span>
          </div>
          <div class="info-item">
            <span class="label">可用天数:</span>
            <span class="value">{{ deviceData.totalAvailableDays }}天</span>
          </div>
          <div class="info-item">
            <span class="label">最后租赁:</span>
            <span class="value">{{ deviceData.lastRentalDate || '暂无' }}</span>
          </div>
          <div class="info-item">
            <span class="label">所在地区:</span>
            <span class="value">{{ deviceData.region || '未知' }}</span>
          </div>
          <div v-if="deviceData.networkType" class="info-item">
            <span class="label">网络类型:</span>
            <el-tag :type="getNetworkTagType(deviceData.networkType)" size="small">
              {{ deviceData.networkType }}
            </el-tag>
          </div>
          <div v-if="deviceData.lastMaintenanceDate" class="info-item">
            <span class="label">最后维护:</span>
            <span class="value">{{ deviceData.lastMaintenanceDate }}</span>
          </div>
          <div v-if="deviceData.nextMaintenanceDate" class="info-item">
            <span class="label">下次维护:</span>
            <span class="value">{{ deviceData.nextMaintenanceDate }}</span>
          </div>
          <div v-if="deviceData.predictedFailureRisk !== undefined" class="info-item">
            <span class="label">故障风险:</span>
            <span class="value" :class="getRiskClass(deviceData.predictedFailureRisk)">
              {{ deviceData.predictedFailureRisk }}%
            </span>
          </div>
        </div>
      </div>

      <!-- 性能指标 -->
      <div class="detail-section">
        <h3 class="section-title">性能指标</h3>
        <div class="metrics-grid">
          <div class="metric-card">
            <div class="metric-title">设备健康度</div>
            <div class="metric-value">
              <el-progress
                :percentage="deviceData.performanceScore || 85"
                :color="getHealthColor(deviceData.performanceScore || 85)"
                :stroke-width="8"
              />
              <span class="metric-text">{{ deviceData.performanceScore || 85 }}分</span>
            </div>
          </div>
          <div class="metric-card">
            <div class="metric-title">信号强度</div>
            <div class="metric-value">
              <el-progress
                :percentage="deviceData.signalStrength || 78"
                :color="getSignalColor(deviceData.signalStrength || 78)"
                :stroke-width="8"
              />
              <span class="metric-text">{{ deviceData.signalStrength || 78 }}%</span>
            </div>
          </div>
          <div class="metric-card">
            <div class="metric-title">维护状态</div>
            <div class="metric-value">
              <el-tag :type="getMaintenanceTagType(deviceData.maintenanceStatus || 'normal')" size="large">
                {{ getMaintenanceText(deviceData.maintenanceStatus || 'normal') }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 利用率趋势图 -->
      <div class="detail-section">
        <h3 class="section-title">利用率趋势（最近30天）</h3>
        <div ref="utilizationTrendChart" class="trend-chart"></div>
      </div>

      <!-- 租赁历史 -->
      <div class="detail-section">
        <h3 class="section-title">最近租赁记录</h3>
        <el-table :data="rentalHistory" size="small" max-height="200">
          <el-table-column prop="rentalDate" label="租赁日期" width="100" />
          <el-table-column prop="customerName" label="客户" width="120" />
          <el-table-column prop="rentalDays" label="租赁天数" width="80" align="center" />
          <el-table-column prop="revenue" label="收入" width="100" align="right">
            <template #default="{ row }">
              ¥{{ row.revenue.toLocaleString() }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === '已完成' ? 'success' : 'warning'" size="small">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleExport">导出详情</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import type { DeviceUtilizationData, NetworkType } from '@/types/rental'

interface Props {
  modelValue: boolean
  deviceData: DeviceUtilizationData | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 图表相关
const utilizationTrendChart = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

// 租赁历史数据
const rentalHistory = ref([
  {
    rentalDate: '2024-01-15',
    customerName: '张三',
    rentalDays: 7,
    revenue: 2800,
    status: '已完成'
  },
  {
    rentalDate: '2024-01-08',
    customerName: '李四',
    rentalDays: 14,
    revenue: 5600,
    status: '已完成'
  },
  {
    rentalDate: '2024-01-01',
    customerName: '王五',
    rentalDays: 3,
    revenue: 1200,
    status: '已完成'
  },
  {
    rentalDate: '2023-12-28',
    customerName: '赵六',
    rentalDays: 10,
    revenue: 4000,
    status: '已完成'
  }
])

// 工具函数
const getModelTagType = (model: string) => {
  const types: Record<string, any> = {
    'YX-Robot-Pro': 'danger',
    'YX-Robot-Standard': 'primary',
    'YX-Robot-Lite': 'success',
    'YX-Robot-Mini': 'warning'
  }
  return types[model] || 'info'
}

const getStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    active: 'success',
    idle: 'warning',
    maintenance: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    active: '运行中',
    idle: '空闲',
    maintenance: '维护中'
  }
  return texts[status] || status
}

const getMaintenanceTagType = (status: string) => {
  const types: Record<string, any> = {
    normal: 'success',
    warning: 'warning',
    urgent: 'danger'
  }
  return types[status] || 'info'
}

const getMaintenanceText = (status: string) => {
  const texts: Record<string, string> = {
    normal: '正常',
    warning: '需关注',
    urgent: '需维护'
  }
  return texts[status] || status
}

const getHealthColor = (score: number) => {
  if (score >= 90) return '#67c23a'
  if (score >= 70) return '#e6a23c'
  if (score >= 50) return '#f56c6c'
  return '#909399'
}

const getSignalColor = (strength: number) => {
  if (strength >= 80) return '#67c23a'
  if (strength >= 60) return '#e6a23c'
  if (strength >= 40) return '#f56c6c'
  return '#909399'
}

const getNetworkTagType = (networkType: string) => {
  const types: Record<string, any> = {
    '5G': 'danger',
    '4G': 'primary',
    'WiFi': 'success',
    'Ethernet': 'warning'
  }
  return types[networkType] || 'info'
}

const getRiskClass = (risk: number) => {
  if (risk >= 80) return 'risk-high'
  if (risk >= 50) return 'risk-medium'
  return 'risk-low'
}

// 初始化利用率趋势图
const initTrendChart = async () => {
  await nextTick()
  if (!utilizationTrendChart.value) return

  chartInstance = echarts.init(utilizationTrendChart.value)
  
  // 生成最近30天的模拟数据
  const dates = []
  const utilizations = []
  const today = new Date()
  
  for (let i = 29; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
    
    // 基于当前利用率生成有波动的趋势数据
    const baseRate = props.deviceData?.utilizationRate || 75
    const variation = (Math.random() - 0.5) * 20 // ±10%的波动
    const rate = Math.max(0, Math.min(100, baseRate + variation))
    utilizations.push(Math.round(rate))
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}%'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        fontSize: 11
      }
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '利用率',
        type: 'line',
        data: utilizations,
        smooth: true,
        itemStyle: { color: '#409eff' },
        lineStyle: { width: 2 },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ]
          }
        }
      }
    ]
  }

  chartInstance.setOption(option)
}

// 监听弹窗打开
watch(visible, (newVal) => {
  if (newVal && props.deviceData) {
    nextTick(() => {
      initTrendChart()
    })
  }
})

// 关闭弹窗
const handleClose = () => {
  visible.value = false
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
}

// 导出详情
const handleExport = () => {
  console.log('导出设备详情:', props.deviceData)
  // 这里可以实现导出功能
}

// 组件卸载时清理图表
onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style lang="scss" scoped>
.device-detail-dialog {
  .device-detail-content {
    max-height: 600px;
    overflow-y: auto;
  }

  .detail-section {
    margin-bottom: 24px;

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 16px;
      padding-bottom: 8px;
      border-bottom: 2px solid #e4e7ed;
    }
  }

  .info-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .info-item {
      display: flex;
      align-items: center;

      .label {
        font-weight: 500;
        color: #606266;
        min-width: 80px;
        margin-right: 8px;
      }

      .value {
        color: #303133;
        font-weight: 500;

        &.risk-low {
          color: #67c23a;
        }

        &.risk-medium {
          color: #e6a23c;
        }

        &.risk-high {
          color: #f56c6c;
        }
      }
    }
  }

  .metrics-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;

    .metric-card {
      padding: 16px;
      background: #f8f9fa;
      border-radius: 8px;
      text-align: center;

      .metric-title {
        font-size: 14px;
        color: #606266;
        margin-bottom: 12px;
      }

      .metric-value {
        .metric-text {
          display: block;
          margin-top: 8px;
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
      }
    }
  }

  .trend-chart {
    height: 200px;
    width: 100%;
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

:deep(.el-dialog__body) {
  padding: 20px;
}
</style>