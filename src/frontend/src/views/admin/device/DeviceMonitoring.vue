<!--
  设备监控页面
  实时监控设备状态、性能指标和告警信息
-->
<template>
  <div class="device-monitoring">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2>设备监控</h2>
        <p class="header-subtitle">实时监控设备状态与性能 · 练字机器人管理系统</p>
      </div>
    </div>

    <!-- 实时统计 -->
    <div class="monitoring-stats">
      <div class="stat-card online">
        <div class="stat-icon">
          <el-icon><CircleCheckFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ monitoringStats.online }}</div>
          <div class="stat-label">在线设备</div>
          <div class="stat-trend">
            <span :class="['trend', getTrendClass(monitoringStats.onlineTrend)]">
              {{ formatTrend(monitoringStats.onlineTrend) }}
            </span>
          </div>
        </div>
      </div>
      
      <div class="stat-card offline">
        <div class="stat-icon">
          <el-icon><CircleCloseFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ monitoringStats.offline }}</div>
          <div class="stat-label">离线设备</div>
          <div class="stat-trend">
            <span :class="['trend', getTrendClass(monitoringStats.offlineTrend)]">
              {{ formatTrend(monitoringStats.offlineTrend) }}
            </span>
          </div>
        </div>
      </div>
      
      <div class="stat-card error">
        <div class="stat-icon">
          <el-icon><WarningFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ monitoringStats.error }}</div>
          <div class="stat-label">故障设备</div>
          <div class="stat-trend">
            <span :class="['trend', getTrendClass(monitoringStats.errorTrend)]">
              {{ formatTrend(monitoringStats.errorTrend) }}
            </span>
          </div>
        </div>
      </div>
      
      <div class="stat-card performance">
        <div class="stat-icon">
          <el-icon><DataLine /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ monitoringStats.avgPerformance }}%</div>
          <div class="stat-label">平均性能</div>
          <div class="stat-trend">
            <span :class="['trend', getTrendClass(monitoringStats.performanceTrend)]">
              {{ formatTrend(monitoringStats.performanceTrend) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 监控内容 -->
    <el-row :gutter="24">
      <!-- 设备状态地图 -->
      <el-col :span="16">
        <el-card class="monitoring-card">
          <template #header>
            <div class="card-header">
              <span>设备分布地图</span>
              <el-button text @click="refreshMap">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div class="device-map" ref="mapContainer" style="height: 400px;">
            <!-- 这里可以集成地图组件显示设备分布 -->
            <div class="map-placeholder">
              <el-icon size="48"><Location /></el-icon>
              <p>设备分布地图</p>
              <p class="map-info">显示全球设备实时状态分布</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 实时告警 -->
      <el-col :span="8">
        <el-card class="monitoring-card">
          <template #header>
            <div class="card-header">
              <span>实时告警</span>
              <el-badge :value="alerts.length" :max="99" type="danger">
                <el-icon><Bell /></el-icon>
              </el-badge>
            </div>
          </template>
          <div class="alerts-list">
            <div
              v-for="alert in alerts.slice(0, 10)"
              :key="alert.id"
              :class="['alert-item', `alert-${alert.level}`]"
            >
              <div class="alert-icon">
                <el-icon>
                  <WarningFilled v-if="alert.level === 'error'" />
                  <InfoFilled v-else-if="alert.level === 'warning'" />
                  <CircleCheckFilled v-else />
                </el-icon>
              </div>
              <div class="alert-content">
                <div class="alert-message">{{ alert.message }}</div>
                <div class="alert-time">{{ formatTime(alert.timestamp) }}</div>
              </div>
            </div>
            <div v-if="alerts.length === 0" class="no-alerts">
              <el-icon><CircleCheckFilled /></el-icon>
              <p>暂无告警信息</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 性能监控图表 -->
    <el-row :gutter="24" style="margin-top: 24px;">
      <el-col :span="12">
        <el-card class="monitoring-card">
          <template #header>
            <span>设备性能趋势</span>
          </template>
          <div class="chart-container" ref="performanceChart" style="height: 300px;">
            <!-- 性能趋势图表 -->
            <div class="chart-placeholder">
              <el-icon size="48"><DataLine /></el-icon>
              <p>设备性能趋势图</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="monitoring-card">
          <template #header>
            <span>网络连接状态</span>
          </template>
          <div class="chart-container" ref="networkChart" style="height: 300px;">
            <!-- 网络状态图表 -->
            <div class="chart-placeholder">
              <el-icon size="48"><Link /></el-icon>
              <p>网络连接状态图</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 设备列表 -->
    <el-card class="monitoring-card" style="margin-top: 24px;">
      <template #header>
        <div class="card-header">
          <span>设备实时状态</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索设备"
              :prefix-icon="Search"
              clearable
              style="width: 200px; margin-right: 12px;"
            />
            <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 120px;">
              <el-option label="全部" value="" />
              <el-option label="在线" value="online" />
              <el-option label="离线" value="offline" />
              <el-option label="故障" value="error" />
            </el-select>
          </div>
        </div>
      </template>
      
      <el-table :data="filteredDevices" v-loading="tableLoading" stripe>
        <el-table-column prop="serialNumber" label="设备序列号" min-width="160" />
        <el-table-column prop="customerName" label="客户" min-width="120" />
        <el-table-column prop="status" label="状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              <el-icon style="margin-right: 4px;">
                <CircleCheckFilled v-if="row.status === 'online'" />
                <CircleCloseFilled v-else-if="row.status === 'offline'" />
                <WarningFilled v-else />
              </el-icon>
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="性能指标" min-width="280">
          <template #default="{ row }">
            <div class="performance-metrics">
              <div class="metric-item">
                <span class="metric-label">CPU:</span>
                <el-progress
                  :percentage="row.performance.cpu"
                  :stroke-width="6"
                  :show-text="false"
                  :color="getPerformanceColor(row.performance.cpu)"
                />
                <span class="metric-value">{{ row.performance.cpu }}%</span>
              </div>
              <div class="metric-item">
                <span class="metric-label">内存:</span>
                <el-progress
                  :percentage="row.performance.memory"
                  :stroke-width="6"
                  :show-text="false"
                  :color="getPerformanceColor(row.performance.memory)"
                />
                <span class="metric-value">{{ row.performance.memory }}%</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="lastOnlineAt" label="最后在线时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.lastOnlineAt) }}
          </template>
        </el-table-column>
        <el-table-column label="设备信息" min-width="200">
          <template #default="{ row }">
            <div class="device-info-detail">
              <div class="info-row">
                <span class="info-label">型号:</span>
                <span class="info-value">{{ row.model || 'YX-EDU-2024' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">固件:</span>
                <span class="info-value">{{ row.firmwareVersion || 'v2.1.0' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="网络状态" min-width="160">
          <template #default="{ row }">
            <div class="network-status">
              <div class="network-item">
                <span class="network-label">信号强度:</span>
                <el-progress
                  :percentage="Math.floor(Math.random() * 40) + 60"
                  :stroke-width="4"
                  :show-text="false"
                  color="#67c23a"
                />
              </div>
              <div class="network-type">
                <el-tag size="small" type="info">{{ ['4G', '5G', 'WiFi'][Math.floor(Math.random() * 3)] }}</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="viewDeviceDetail(row)">
              查看详情
            </el-button>
            <el-button text type="success" size="small" @click="remoteControl(row)" v-if="row.status === 'online'">
              远程控制
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
<script
 setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  CircleCheckFilled,
  CircleCloseFilled,
  WarningFilled,
  DataLine,
  Location,
  Bell,
  InfoFilled,
  Link,
  Search
} from '@element-plus/icons-vue'
import type { Device } from '@/types/device'
import { mockDeviceAPI } from '@/api/mock/device'

// 响应式数据
const tableLoading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')

// 监控统计数据
const monitoringStats = ref({
  online: 0,
  offline: 0,
  error: 0,
  avgPerformance: 0,
  onlineTrend: 0,
  offlineTrend: 0,
  errorTrend: 0,
  performanceTrend: 0
})

// 设备列表
const deviceList = ref<(Device & { performance: { cpu: number; memory: number } })[]>([])

// 告警列表
const alerts = ref<Array<{
  id: string
  level: 'error' | 'warning' | 'info'
  message: string
  timestamp: string
}>>([])



// 计算属性
const filteredDevices = computed(() => {
  let filtered = deviceList.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(device =>
      device.serialNumber.toLowerCase().includes(keyword) ||
      device.customerName.toLowerCase().includes(keyword)
    )
  }

  if (statusFilter.value) {
    filtered = filtered.filter(device => device.status === statusFilter.value)
  }

  return filtered
})

// 方法
const loadMonitoringData = async () => {
  tableLoading.value = true
  try {
    // 加载设备数据
    const response = await mockDeviceAPI.getDevices({ pageSize: 100 })
    const devices = response.data.list

    // 为每个设备生成性能数据
    deviceList.value = devices.map(device => ({
      ...device,
      performance: {
        cpu: Math.floor(Math.random() * 100),
        memory: Math.floor(Math.random() * 100)
      }
    }))

    // 计算统计数据
    const stats = response.data.stats
    monitoringStats.value = {
      online: stats.online,
      offline: stats.offline,
      error: stats.error,
      avgPerformance: Math.floor(Math.random() * 30) + 70,
      onlineTrend: Math.floor(Math.random() * 20) - 10,
      offlineTrend: Math.floor(Math.random() * 10) - 5,
      errorTrend: Math.floor(Math.random() * 6) - 3,
      performanceTrend: Math.floor(Math.random() * 10) - 5
    }

    // 生成告警数据
    generateAlerts()
  } catch (error) {
    console.error('加载监控数据失败:', error)
    ElMessage.error('加载监控数据失败')
  } finally {
    tableLoading.value = false
  }
}

const generateAlerts = () => {
  const alertMessages = [
    { level: 'error' as const, message: '设备 EDU-001234 连接超时' },
    { level: 'warning' as const, message: '设备 HOME-005678 电池电量低于20%' },
    { level: 'error' as const, message: '设备 PRO-009876 硬件故障' },
    { level: 'warning' as const, message: '设备 EDU-002468 内存使用率过高' },
    { level: 'info' as const, message: '设备 HOME-001357 固件更新完成' }
  ]

  alerts.value = alertMessages.map((alert, index) => ({
    id: (index + 1).toString(),
    ...alert,
    timestamp: new Date(Date.now() - Math.random() * 3600000).toISOString()
  }))
}



const refreshMap = () => {
  ElMessage.info('地图数据已刷新')
}



const viewDeviceDetail = (device: Device) => {
  ElMessage.info(`查看设备详情：${device.serialNumber}`)
}

const remoteControl = (device: Device) => {
  ElMessage.info(`远程控制设备：${device.serialNumber}`)
}

// 工具方法
const getTrendClass = (trend: number) => {
  if (trend > 0) return 'trend-up'
  if (trend < 0) return 'trend-down'
  return 'trend-stable'
}

const formatTrend = (trend: number) => {
  if (trend > 0) return `+${trend}`
  return trend.toString()
}

const getStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    online: 'success',
    offline: 'info',
    error: 'danger',
    maintenance: 'warning'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    online: '在线',
    offline: '离线',
    error: '故障',
    maintenance: '维护中'
  }
  return texts[status] || status
}

const getPerformanceColor = (value: number) => {
  if (value >= 80) return '#f56c6c'
  if (value >= 60) return '#e6a23c'
  return '#67c23a'
}

const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return '无'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const formatTime = (dateStr: string) => {
  const now = new Date()
  const time = new Date(dateStr)
  const diff = now.getTime() - time.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return time.toLocaleDateString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadMonitoringData()
})
</script>

<style lang="scss" scoped>
.device-monitoring {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
      h2 {
        margin: 0 0 4px 0;
        color: #262626;
        font-size: 24px;
        font-weight: 600;
      }
      
      .header-subtitle {
        margin: 0;
        color: #8c8c8c;
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

  .monitoring-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 24px;

    .stat-card {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
      }

      &.online .stat-icon {
        background: linear-gradient(135deg, #67C23A, #85CE61);
        color: white;
      }

      &.offline .stat-icon {
        background: linear-gradient(135deg, #909399, #B1B3B8);
        color: white;
      }

      &.error .stat-icon {
        background: linear-gradient(135deg, #F56C6C, #F78989);
        color: white;
      }

      &.performance .stat-icon {
        background: linear-gradient(135deg, #409EFF, #66B1FF);
        color: white;
      }

      .stat-content {
        flex: 1;

        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #303133;
          line-height: 1.2;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #606266;
          margin-bottom: 4px;
        }

        .stat-trend {
          .trend {
            font-size: 12px;
            font-weight: 500;

            &.trend-up {
              color: #67c23a;
            }

            &.trend-down {
              color: #f56c6c;
            }

            &.trend-stable {
              color: #909399;
            }
          }
        }
      }
    }
  }

  .monitoring-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        align-items: center;
      }
    }

    .device-map {
      .map-placeholder {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100%;
        color: #909399;

        .map-info {
          margin: 8px 0 0 0;
          font-size: 12px;
          color: #c0c4cc;
        }
      }
    }

    .alerts-list {
      max-height: 400px;
      overflow-y: auto;

      .alert-item {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        padding: 12px;
        border-radius: 6px;
        margin-bottom: 8px;
        transition: background-color 0.2s;

        &:hover {
          background-color: #f8f9fa;
        }

        &.alert-error {
          border-left: 4px solid #f56c6c;
        }

        &.alert-warning {
          border-left: 4px solid #e6a23c;
        }

        &.alert-info {
          border-left: 4px solid #409eff;
        }

        .alert-icon {
          color: #f56c6c;
          margin-top: 2px;
        }

        .alert-content {
          flex: 1;

          .alert-message {
            font-size: 14px;
            color: #262626;
            margin-bottom: 4px;
          }

          .alert-time {
            font-size: 12px;
            color: #8c8c8c;
          }
        }
      }

      .no-alerts {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 40px;
        color: #67c23a;

        p {
          margin: 8px 0 0 0;
          color: #909399;
        }
      }
    }

    .chart-container {
      .chart-placeholder {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100%;
        color: #909399;
      }
    }

    .performance-metrics {
      .metric-item {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;

        &:last-child {
          margin-bottom: 0;
        }

        .metric-label {
          font-size: 12px;
          color: #666;
          width: 40px;
        }

        .metric-value {
          font-size: 12px;
          color: #262626;
          font-weight: 500;
          width: 35px;
          text-align: right;
        }

        .el-progress {
          flex: 1;
        }
      }
    }

    // 设备信息详情样式
    .device-info-detail {
      .info-row {
        display: flex;
        align-items: center;
        margin-bottom: 4px;

        &:last-child {
          margin-bottom: 0;
        }

        .info-label {
          font-size: 12px;
          color: #666;
          min-width: 40px;
          margin-right: 8px;
        }

        .info-value {
          font-size: 12px;
          color: #262626;
          font-weight: 500;
        }
      }
    }

    // 网络状态样式
    .network-status {
      .network-item {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;

        .network-label {
          font-size: 12px;
          color: #666;
          min-width: 60px;
        }

        .el-progress {
          flex: 1;
        }
      }

      .network-type {
        text-align: center;
      }
    }
  }
}

@media (max-width: 1200px) {
  .device-monitoring {
    .monitoring-stats {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}

@media (max-width: 768px) {
  .device-monitoring {
    padding: 16px;

    .page-header {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;

      .header-right {
        justify-content: center;
      }
    }

    .monitoring-stats {
      grid-template-columns: 1fr;
      gap: 16px;
    }
  }
}
</style>