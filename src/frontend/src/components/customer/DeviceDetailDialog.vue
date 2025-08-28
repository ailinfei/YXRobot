<template>
  <el-dialog
    v-model="visible"
    title="设备详情"
    width="900px"
    :before-close="handleClose"
    destroy-on-close
  >
    <div v-if="device" class="device-detail">
      <!-- 设备基本信息 -->
      <div class="detail-section">
        <div class="section-header">
          <h4>基本信息</h4>
          <div class="device-status">
            <el-tag :type="getDeviceStatusTagType(device.status)" size="large">
              {{ getDeviceStatusText(device.status) }}
            </el-tag>
            <div class="online-indicator">
              <el-icon :class="getOnlineStatusClass(device.lastOnlineAt)">
                <CircleCheckFilled />
              </el-icon>
              <span>{{ formatRelativeTime(device.lastOnlineAt) }}</span>
            </div>
          </div>
        </div>
        
        <div class="info-grid">
          <div class="info-item">
            <label>设备序列号</label>
            <span class="serial-number">{{ device.serialNumber }}</span>
          </div>
          <div class="info-item">
            <label>设备型号</label>
            <span>{{ device.model }}</span>
          </div>
          <div class="info-item">
            <label>设备类型</label>
            <el-tag :type="device.type === 'purchased' ? 'success' : 'warning'" size="small">
              {{ device.type === 'purchased' ? '购买' : '租赁' }}
            </el-tag>
          </div>
          <div class="info-item">
            <label>固件版本</label>
            <span>{{ device.firmwareVersion }}</span>
          </div>
          <div class="info-item">
            <label>激活时间</label>
            <span>{{ formatDate(device.activatedAt) }}</span>
          </div>
          <div class="info-item">
            <label>最后在线</label>
            <span>{{ formatDate(device.lastOnlineAt) }}</span>
          </div>
        </div>
      </div>

      <!-- 设备健康状况 -->
      <div class="detail-section">
        <h4>设备健康状况</h4>
        <div class="health-overview">
          <div class="health-score">
            <div class="score-circle">
              <el-progress
                type="circle"
                :percentage="device.healthScore"
                :color="getHealthColor(device.healthScore)"
                :width="120"
                :stroke-width="8"
              >
                <template #default="{ percentage }">
                  <span class="score-text">{{ percentage }}%</span>
                  <span class="score-label">健康度</span>
                </template>
              </el-progress>
            </div>
          </div>
          
          <div class="health-details">
            <div class="health-item">
              <div class="health-label">运行状态</div>
              <div class="health-value good">正常</div>
            </div>
            <div class="health-item">
              <div class="health-label">硬件状态</div>
              <div class="health-value good">良好</div>
            </div>
            <div class="health-item">
              <div class="health-label">网络连接</div>
              <div class="health-value" :class="device.lastOnlineAt ? 'good' : 'warning'">
                {{ device.lastOnlineAt ? '正常' : '离线' }}
              </div>
            </div>
            <div class="health-item">
              <div class="health-label">存储空间</div>
              <div class="health-value good">充足</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 使用统计 -->
      <div class="detail-section" v-if="device.usageStats">
        <h4>使用统计</h4>
        <div class="usage-stats">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ device.usageStats.totalUsageHours }}h</div>
              <div class="stat-label">总使用时长</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ device.usageStats.dailyAverageUsage }}h</div>
              <div class="stat-label">日均使用</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ device.usageStats.coursesCompleted }}</div>
              <div class="stat-label">完成课程</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><Edit /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ device.usageStats.charactersWritten }}</div>
              <div class="stat-label">练习字数</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 设备操作 -->
      <div class="detail-section">
        <h4>设备操作</h4>
        <div class="device-actions">
          <el-button type="primary" @click="handleRemoteControl">
            <el-icon><VideoPlay /></el-icon>
            远程控制
          </el-button>
          <el-button @click="handleRestart">
            <el-icon><RefreshRight /></el-icon>
            重启设备
          </el-button>
          <el-button @click="handleUpdateFirmware">
            <el-icon><Upload /></el-icon>
            固件更新
          </el-button>
          <el-button @click="handleViewLogs">
            <el-icon><Document /></el-icon>
            查看日志
          </el-button>
          <el-button type="warning" @click="handleMaintenance">
            <el-icon><Tools /></el-icon>
            维护模式
          </el-button>
        </div>
      </div>

      <!-- 设备日志 -->
      <div class="detail-section">
        <h4>最近日志</h4>
        <div class="device-logs">
          <el-timeline>
            <el-timeline-item
              v-for="log in deviceLogs"
              :key="log.id"
              :timestamp="formatDate(log.timestamp)"
              :type="getLogType(log.level)"
            >
              <div class="log-content">
                <div class="log-message">{{ log.message }}</div>
                <div class="log-details" v-if="log.details">{{ log.details }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">编辑设备</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  CircleCheckFilled,
  Clock,
  TrendCharts,
  Document,
  Edit,
  VideoPlay,
  RefreshRight,
  Upload,
  Tools
} from '@element-plus/icons-vue'
import type { CustomerDevice } from '@/types/customer'

// Props
interface Props {
  modelValue: boolean
  device?: CustomerDevice | null
}

const props = withDefaults(defineProps<Props>(), {
  device: null
})

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'edit': [device: CustomerDevice]
}>()

// 响应式数据
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 模拟设备日志数据
const deviceLogs = ref([
  {
    id: '1',
    timestamp: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
    level: 'info',
    message: '设备正常启动',
    details: '所有系统组件初始化完成'
  },
  {
    id: '2',
    timestamp: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString(),
    level: 'info',
    message: '用户开始练字课程',
    details: '课程：楷书基础练习'
  },
  {
    id: '3',
    timestamp: new Date(Date.now() - 1000 * 60 * 60 * 4).toISOString(),
    level: 'warning',
    message: '网络连接不稳定',
    details: '检测到间歇性网络中断'
  },
  {
    id: '4',
    timestamp: new Date(Date.now() - 1000 * 60 * 60 * 6).toISOString(),
    level: 'info',
    message: '固件更新完成',
    details: '版本更新至 v2.1.0'
  }
])

// 方法
const handleClose = () => {
  visible.value = false
}

const handleEdit = () => {
  if (props.device) {
    emit('edit', props.device)
    handleClose()
  }
}

const handleRemoteControl = () => {
  ElMessage.info('远程控制功能开发中...')
}

const handleRestart = () => {
  ElMessage.info('设备重启指令已发送')
}

const handleUpdateFirmware = () => {
  ElMessage.info('固件更新功能开发中...')
}

const handleViewLogs = () => {
  ElMessage.info('查看完整日志功能开发中...')
}

const handleMaintenance = () => {
  ElMessage.info('维护模式功能开发中...')
}



// 工具方法
const getDeviceStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    pending: 'info',
    active: 'success',
    offline: 'warning',
    maintenance: 'primary',
    retired: 'danger'
  }
  return types[status] || 'info'
}

const getDeviceStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待激活',
    active: '使用中',
    offline: '离线',
    maintenance: '维护中',
    retired: '已退役'
  }
  return texts[status] || status
}

const getOnlineStatusClass = (lastOnlineAt?: string) => {
  if (!lastOnlineAt) return 'offline'
  
  const now = new Date()
  const lastOnline = new Date(lastOnlineAt)
  const diffMinutes = (now.getTime() - lastOnline.getTime()) / (1000 * 60)
  
  if (diffMinutes < 5) return 'online'
  if (diffMinutes < 60) return 'warning'
  return 'offline'
}

const getHealthColor = (score: number) => {
  if (score >= 80) return '#67C23A'
  if (score >= 60) return '#E6A23C'
  return '#F56C6C'
}

const getLogType = (level: string) => {
  const types: Record<string, any> = {
    info: 'primary',
    warning: 'warning',
    error: 'danger',
    success: 'success'
  }
  return types[level] || 'primary'
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const formatRelativeTime = (dateStr?: string) => {
  if (!dateStr) return '从未在线'
  
  const now = new Date()
  const date = new Date(dateStr)
  const diffMs = now.getTime() - date.getTime()
  const diffMinutes = Math.floor(diffMs / (1000 * 60))
  
  if (diffMinutes < 1) return '刚刚在线'
  if (diffMinutes < 5) return '在线'
  if (diffMinutes < 60) return `${diffMinutes}分钟前`
  
  const diffHours = Math.floor(diffMinutes / 60)
  if (diffHours < 24) return `${diffHours}小时前`
  
  const diffDays = Math.floor(diffHours / 24)
  return `${diffDays}天前`
}
</script>

<style lang="scss" scoped>
.device-detail {
  .detail-section {
    margin-bottom: 32px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      
      h4 {
        margin: 0;
        color: #303133;
        font-size: 16px;
        font-weight: 600;
      }
      
      .device-status {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .online-indicator {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 14px;
          color: #606266;
          
          .el-icon {
            font-size: 8px;
            
            &.online {
              color: #67C23A;
            }
            
            &.warning {
              color: #E6A23C;
            }
            
            &.offline {
              color: #F56C6C;
            }
          }
        }
      }
    }
    
    h4 {
      margin: 0 0 20px 0;
      color: #303133;
      font-size: 16px;
      font-weight: 600;
      border-bottom: 1px solid #EBEEF5;
      padding-bottom: 8px;
    }
    
    .info-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 20px;
      
      .info-item {
        display: flex;
        flex-direction: column;
        gap: 8px;
        
        label {
          font-size: 14px;
          font-weight: 500;
          color: #606266;
        }
        
        span {
          font-size: 14px;
          color: #303133;
          
          &.serial-number {
            font-family: 'Courier New', monospace;
            font-weight: 600;
            color: #409EFF;
          }
        }
      }
    }
    
    .health-overview {
      display: flex;
      gap: 40px;
      align-items: center;
      
      .health-score {
        .score-circle {
          .score-text {
            font-size: 24px;
            font-weight: 700;
            color: #303133;
            display: block;
            line-height: 1;
          }
          
          .score-label {
            font-size: 12px;
            color: #909399;
            display: block;
            margin-top: 4px;
          }
        }
      }
      
      .health-details {
        flex: 1;
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;
        
        .health-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px;
          background: #F5F7FA;
          border-radius: 6px;
          
          .health-label {
            font-size: 14px;
            color: #606266;
          }
          
          .health-value {
            font-size: 14px;
            font-weight: 500;
            
            &.good {
              color: #67C23A;
            }
            
            &.warning {
              color: #E6A23C;
            }
            
            &.error {
              color: #F56C6C;
            }
          }
        }
      }
    }
    
    .usage-stats {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;
      
      .stat-card {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 20px;
        background: #F8F9FA;
        border-radius: 8px;
        border: 1px solid #EBEEF5;
        
        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
          background: linear-gradient(135deg, #409EFF, #66B1FF);
          color: white;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;
        }
        
        .stat-content {
          flex: 1;
          
          .stat-value {
            font-size: 20px;
            font-weight: 700;
            color: #303133;
            line-height: 1.2;
            margin-bottom: 4px;
          }
          
          .stat-label {
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }
    
    .device-actions {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
    }
    
    .device-logs {
      max-height: 300px;
      overflow-y: auto;
      
      .log-content {
        .log-message {
          font-size: 14px;
          color: #303133;
          margin-bottom: 4px;
        }
        
        .log-details {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 1200px) {
  .device-detail {
    .detail-section {
      .info-grid {
        grid-template-columns: repeat(2, 1fr);
      }
      
      .usage-stats {
        grid-template-columns: repeat(2, 1fr);
      }
      
      .health-overview {
        flex-direction: column;
        gap: 20px;
        
        .health-details {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .device-detail {
    .detail-section {
      .info-grid {
        grid-template-columns: 1fr;
      }
      
      .usage-stats {
        grid-template-columns: 1fr;
      }
      
      .device-actions {
        flex-direction: column;
      }
    }
  }
}
</style>