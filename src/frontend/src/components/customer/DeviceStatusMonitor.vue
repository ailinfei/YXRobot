<template>
  <div class="device-status-monitor">
    <div class="monitor-header">
      <h4>设备状态监控</h4>
      <div class="monitor-controls">
        <el-switch
          v-model="autoRefresh"
          active-text="自动刷新"
          inactive-text="手动刷新"
          @change="handleAutoRefreshChange"
        />
        <el-button 
          size="small" 
          :icon="Refresh"
          @click="refreshDeviceStatus"
          :loading="refreshing"
        >
          刷新状态
        </el-button>
      </div>
    </div>

    <div class="devices-monitor-grid">
      <div 
        v-for="device in devices" 
        :key="device.id"
        class="device-monitor-card"
        :class="{ 
          online: device.status === 'active',
          offline: device.status === 'offline',
          maintenance: device.status === 'maintenance'
        }"
      >
        <!-- 设备基本信息 -->
        <div class="device-header">
          <div class="device-info">
            <h5>{{ device.model }}</h5>
            <div class="device-serial">{{ device.serialNumber }}</div>
          </div>
          <div class="device-status-indicator">
            <div class="status-dot" :class="device.status"></div>
            <span class="status-text">{{ getStatusText(device.status) }}</span>
          </div>
        </div>

        <!-- 实时状态信息 -->
        <div class="device-realtime-info">
          <div class="info-row">
            <span class="label">在线状态:</span>
            <span class="value" :class="getOnlineStatusClass(device.lastOnlineAt)">
              {{ getOnlineStatusText(device.lastOnlineAt) }}
            </span>
          </div>
          <div class="info-row">
            <span class="label">健康评分:</span>
            <span class="value">
              <el-progress 
                :percentage="device.healthScore" 
                :stroke-width="4"
                :show-text="false"
                :color="getHealthColor(device.healthScore)"
                style="width: 60px; margin-right: 8px;"
              />
              {{ device.healthScore }}%
            </span>
          </div>
          <div class="info-row">
            <span class="label">固件版本:</span>
            <span class="value">
              {{ device.firmwareVersion }}
              <el-tag 
                v-if="hasNewFirmware(device.firmwareVersion)"
                size="small"
                type="warning"
              >
                有更新
              </el-tag>
            </span>
          </div>
          <div class="info-row">
            <span class="label">使用时长:</span>
            <span class="value">{{ device.usageStats.totalUsageHours }}小时</span>
          </div>
        </div>

        <!-- 设备操作 -->
        <div class="device-actions">
          <el-dropdown @command="(command) => handleDeviceAction(command, device)">
            <el-button size="small" type="primary">
              远程操作
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item 
                  command="restart"
                  :disabled="device.status !== 'active'"
                >
                  <el-icon><RefreshRight /></el-icon>
                  重启设备
                </el-dropdown-item>
                <el-dropdown-item 
                  command="update"
                  :disabled="device.status !== 'active'"
                >
                  <el-icon><Upload /></el-icon>
                  固件更新
                </el-dropdown-item>
                <el-dropdown-item 
                  command="settings"
                  :disabled="device.status !== 'active'"
                >
                  <el-icon><Setting /></el-icon>
                  参数设置
                </el-dropdown-item>
                <el-dropdown-item 
                  command="logs"
                >
                  <el-icon><Document /></el-icon>
                  查看日志
                </el-dropdown-item>
                <el-dropdown-item 
                  command="maintenance"
                  divided
                >
                  <el-icon><Tools /></el-icon>
                  维护模式
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          
          <el-button 
            size="small" 
            @click="showDeviceDetail(device)"
          >
            详细信息
          </el-button>
        </div>

        <!-- 连接状态指示器 -->
        <div class="connection-indicator" v-if="device.status === 'active'">
          <div class="signal-bars">
            <div class="bar" :class="{ active: getSignalStrength(device) >= 1 }"></div>
            <div class="bar" :class="{ active: getSignalStrength(device) >= 2 }"></div>
            <div class="bar" :class="{ active: getSignalStrength(device) >= 3 }"></div>
            <div class="bar" :class="{ active: getSignalStrength(device) >= 4 }"></div>
          </div>
          <span class="signal-text">信号强度</span>
        </div>
      </div>
    </div>

    <!-- 设备详情对话框 -->
    <el-dialog
      v-model="deviceDetailVisible"
      :title="`设备详情 - ${selectedDevice?.serialNumber}`"
      width="70%"
    >
      <div v-if="selectedDevice" class="device-detail-content">
        <!-- 设备基本信息 -->
        <div class="detail-section">
          <h4>基本信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="设备型号">{{ selectedDevice.model }}</el-descriptions-item>
            <el-descriptions-item label="序列号">{{ selectedDevice.serialNumber }}</el-descriptions-item>
            <el-descriptions-item label="设备类型">
              <el-tag :type="selectedDevice.type === 'purchased' ? 'success' : 'warning'">
                {{ selectedDevice.type === 'purchased' ? '购买' : '租赁' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="当前状态">
              <el-tag :type="getDeviceStatusType(selectedDevice.status)">
                {{ getStatusText(selectedDevice.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="激活时间">
              {{ formatDate(selectedDevice.activatedAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="最后在线">
              {{ formatDate(selectedDevice.lastOnlineAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="固件版本">{{ selectedDevice.firmwareVersion }}</el-descriptions-item>
            <el-descriptions-item label="健康评分">
              <el-progress 
                :percentage="selectedDevice.healthScore" 
                :color="getHealthColor(selectedDevice.healthScore)"
              />
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 使用统计 -->
        <div class="detail-section">
          <h4>使用统计</h4>
          <div class="usage-stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ selectedDevice.usageStats.totalUsageHours }}</div>
              <div class="stat-label">总使用时长(小时)</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ selectedDevice.usageStats.dailyAverageUsage }}</div>
              <div class="stat-label">日均使用(小时)</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ selectedDevice.usageStats.coursesCompleted }}</div>
              <div class="stat-label">完成课程数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ selectedDevice.usageStats.charactersWritten }}</div>
              <div class="stat-label">练习字数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ selectedDevice.usageStats.learningProgress }}%</div>
              <div class="stat-label">学习进度</div>
            </div>
          </div>
        </div>

        <!-- 实时数据图表 -->
        <div class="detail-section">
          <h4>使用趋势</h4>
          <div class="chart-container">
            <div class="chart-placeholder">
              <el-icon size="48"><TrendCharts /></el-icon>
              <p>使用趋势图表 (开发中)</p>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Refresh, 
  ArrowDown, 
  RefreshRight, 
  Upload, 
  Setting, 
  Document, 
  Tools,
  TrendCharts
} from '@element-plus/icons-vue'
import type { CustomerDevice } from '@/types/customer'
import { formatDate } from '@/utils/dateTime'

interface Props {
  devices: CustomerDevice[]
  customerId?: string
}

interface Emits {
  (e: 'device-action', action: string, device: CustomerDevice): void
  (e: 'refresh'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式数据
const autoRefresh = ref(true)
const refreshing = ref(false)
const deviceDetailVisible = ref(false)
const selectedDevice = ref<CustomerDevice | null>(null)
let refreshTimer: NodeJS.Timeout | null = null

// 生命周期
onMounted(() => {
  if (autoRefresh.value) {
    startAutoRefresh()
  }
})

onUnmounted(() => {
  stopAutoRefresh()
})

// 方法
const getStatusText = (status: string): string => {
  const statusMap = {
    active: '在线',
    offline: '离线',
    maintenance: '维护中',
    pending: '待激活',
    retired: '已退役'
  }
  return statusMap[status as keyof typeof statusMap] || status
}

const getDeviceStatusType = (status: string): string => {
  const typeMap = {
    active: 'success',
    offline: 'warning',
    maintenance: 'danger',
    pending: 'info',
    retired: 'info'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

const getOnlineStatusText = (lastOnlineAt?: string): string => {
  if (!lastOnlineAt) return '从未在线'
  
  const lastOnline = new Date(lastOnlineAt)
  const now = new Date()
  const diffMinutes = Math.floor((now.getTime() - lastOnline.getTime()) / (1000 * 60))
  
  if (diffMinutes < 5) return '在线'
  if (diffMinutes < 60) return `${diffMinutes}分钟前在线`
  if (diffMinutes < 1440) return `${Math.floor(diffMinutes / 60)}小时前在线`
  return `${Math.floor(diffMinutes / 1440)}天前在线`
}

const getOnlineStatusClass = (lastOnlineAt?: string): string => {
  if (!lastOnlineAt) return 'offline'
  
  const lastOnline = new Date(lastOnlineAt)
  const now = new Date()
  const diffMinutes = Math.floor((now.getTime() - lastOnline.getTime()) / (1000 * 60))
  
  if (diffMinutes < 5) return 'online'
  if (diffMinutes < 30) return 'recent'
  return 'offline'
}

const getHealthColor = (score: number): string => {
  if (score >= 90) return '#67c23a'
  if (score >= 70) return '#e6a23c'
  return '#f56c6c'
}



const getSignalStrength = (device: CustomerDevice): number => {
  // 根据设备健康评分和在线状态计算信号强度
  if (device.status !== 'active') return 0
  
  const healthScore = device.healthScore
  if (healthScore >= 90) return 4
  if (healthScore >= 70) return 3
  if (healthScore >= 50) return 2
  return 1
}

const handleAutoRefreshChange = (value: boolean) => {
  if (value) {
    startAutoRefresh()
  } else {
    stopAutoRefresh()
  }
}

const startAutoRefresh = () => {
  refreshTimer = setInterval(() => {
    refreshDeviceStatus()
  }, 30000) // 30秒刷新一次
}

const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

const refreshDeviceStatus = async () => {
  try {
    refreshing.value = true
    // 模拟刷新设备状态
    await new Promise(resolve => setTimeout(resolve, 1000))
    emit('refresh')
  } catch (error) {
    ElMessage.error('刷新设备状态失败')
  } finally {
    refreshing.value = false
  }
}

const handleDeviceAction = async (action: string, device: CustomerDevice) => {
  try {
    let confirmMessage = ''
    let successMessage = ''
    
    switch (action) {
      case 'restart':
        confirmMessage = `确定要重启设备 ${device.serialNumber} 吗？`
        successMessage = '设备重启指令已发送'
        break
      case 'update':
        confirmMessage = `确定要更新设备 ${device.serialNumber} 的固件吗？`
        successMessage = '固件更新指令已发送'
        break
      case 'settings':
        ElMessage.info('参数设置功能开发中...')
        return
      case 'logs':
        ElMessage.info('查看日志功能开发中...')
        return
      case 'maintenance':
        confirmMessage = `确定要将设备 ${device.serialNumber} 设置为维护模式吗？`
        successMessage = '设备已设置为维护模式'
        break
      default:
        return
    }
    
    if (confirmMessage) {
      await ElMessageBox.confirm(confirmMessage, '操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
    }
    
    // 模拟远程操作
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    ElMessage.success(successMessage)
    emit('device-action', action, device)
    
    // 刷新设备状态
    setTimeout(() => {
      refreshDeviceStatus()
    }, 2000)
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

const showDeviceDetail = (device: CustomerDevice) => {
  selectedDevice.value = device
  deviceDetailVisible.value = true
}
</script>

<style lang="scss" scoped>
.device-status-monitor {
  .monitor-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h4 {
      margin: 0;
      color: var(--text-primary);
    }
    
    .monitor-controls {
      display: flex;
      align-items: center;
      gap: 16px;
    }
  }
  
  .devices-monitor-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 20px;
    
    .device-monitor-card {
      background: white;
      border: 2px solid var(--border-color);
      border-radius: var(--radius-lg);
      padding: 20px;
      transition: all 0.3s ease;
      position: relative;
      
      &.online {
        border-color: var(--success-color);
        box-shadow: 0 0 0 2px rgba(var(--success-color-rgb), 0.1);
      }
      
      &.offline {
        border-color: var(--warning-color);
        box-shadow: 0 0 0 2px rgba(var(--warning-color-rgb), 0.1);
      }
      
      &.maintenance {
        border-color: var(--danger-color);
        box-shadow: 0 0 0 2px rgba(var(--danger-color-rgb), 0.1);
      }
      
      .device-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;
        
        .device-info {
          h5 {
            margin: 0 0 4px 0;
            color: var(--text-primary);
            font-size: 16px;
          }
          
          .device-serial {
            font-size: 12px;
            color: var(--text-light);
            font-family: monospace;
          }
        }
        
        .device-status-indicator {
          display: flex;
          align-items: center;
          gap: 8px;
          
          .status-dot {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            
            &.active {
              background: var(--success-color);
              box-shadow: 0 0 0 2px rgba(var(--success-color-rgb), 0.3);
              animation: pulse 2s infinite;
            }
            
            &.offline {
              background: var(--warning-color);
            }
            
            &.maintenance {
              background: var(--danger-color);
            }
            
            &.pending {
              background: var(--info-color);
            }
          }
          
          .status-text {
            font-size: 12px;
            font-weight: 600;
            color: var(--text-secondary);
          }
        }
      }
      
      .device-realtime-info {
        margin-bottom: 16px;
        
        .info-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
          font-size: 13px;
          
          .label {
            color: var(--text-secondary);
          }
          
          .value {
            color: var(--text-primary);
            font-weight: 500;
            display: flex;
            align-items: center;
            
            &.online {
              color: var(--success-color);
            }
            
            &.recent {
              color: var(--warning-color);
            }
            
            &.offline {
              color: var(--text-light);
            }
          }
        }
      }
      
      .device-actions {
        display: flex;
        gap: 8px;
        margin-bottom: 16px;
        
        .el-button {
          flex: 1;
        }
      }
      
      .connection-indicator {
        position: absolute;
        top: 16px;
        right: 16px;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;
        
        .signal-bars {
          display: flex;
          align-items: end;
          gap: 2px;
          
          .bar {
            width: 3px;
            background: var(--border-color);
            border-radius: 1px;
            transition: background-color 0.3s ease;
            
            &:nth-child(1) { height: 6px; }
            &:nth-child(2) { height: 9px; }
            &:nth-child(3) { height: 12px; }
            &:nth-child(4) { height: 15px; }
            
            &.active {
              background: var(--success-color);
            }
          }
        }
        
        .signal-text {
          font-size: 10px;
          color: var(--text-light);
        }
      }
    }
  }
  
  .device-detail-content {
    .detail-section {
      margin-bottom: 32px;
      
      h4 {
        margin: 0 0 16px 0;
        color: var(--text-primary);
        font-size: 16px;
        border-bottom: 1px solid var(--border-color);
        padding-bottom: 8px;
      }
      
      .usage-stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
        gap: 16px;
        
        .stat-item {
          text-align: center;
          padding: 16px;
          background: var(--bg-secondary);
          border-radius: var(--radius-md);
          
          .stat-value {
            font-size: 24px;
            font-weight: 700;
            color: var(--primary-color);
            line-height: 1;
          }
          
          .stat-label {
            font-size: 12px;
            color: var(--text-secondary);
            margin-top: 4px;
          }
        }
      }
      
      .chart-container {
        height: 200px;
        background: var(--bg-secondary);
        border-radius: var(--radius-lg);
        display: flex;
        align-items: center;
        justify-content: center;
        
        .chart-placeholder {
          text-align: center;
          color: var(--text-light);
          
          p {
            margin: 8px 0 0 0;
          }
        }
      }
    }
  }
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 2px rgba(var(--success-color-rgb), 0.3);
  }
  50% {
    box-shadow: 0 0 0 6px rgba(var(--success-color-rgb), 0.1);
  }
  100% {
    box-shadow: 0 0 0 2px rgba(var(--success-color-rgb), 0.3);
  }
}

// 响应式适配
@media (max-width: 768px) {
  .device-status-monitor {
    .monitor-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }
    
    .devices-monitor-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>