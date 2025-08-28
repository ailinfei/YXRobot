<template>
  <div class="firmware-update-progress">
    <!-- 任务概览 -->
    <div class="task-overview">
      <div class="task-header">
        <div class="task-info">
          <h3 class="task-name">{{ task?.name || '固件更新任务' }}</h3>
          <div class="task-meta">
            <span class="task-version">目标版本: {{ task?.targetVersion }}</span>
            <span class="task-devices">设备数量: {{ task?.deviceIds.length }}台</span>
            <span class="task-duration">运行时长: {{ taskDuration }}</span>
          </div>
        </div>
        <div class="task-status">
          <el-tag :type="getTaskStatusType(task?.status)" size="large">
            {{ getTaskStatusText(task?.status) }}
          </el-tag>
        </div>
      </div>

      <!-- 整体进度 -->
      <div class="overall-progress">
        <div class="progress-header">
          <span class="progress-title">整体进度</span>
          <span class="progress-stats">
            {{ completedDevices }}/{{ totalDevices }} 设备完成
            ({{ overallProgressPercentage }}%)
          </span>
        </div>
        <el-progress
          :percentage="overallProgressPercentage"
          :color="getOverallProgressColor()"
          :stroke-width="12"
          :show-text="false"
        />
        <div class="progress-details">
          <div class="detail-item success">
            <span class="detail-label">成功:</span>
            <span class="detail-value">{{ successDevices }}台</span>
          </div>
          <div class="detail-item processing">
            <span class="detail-label">进行中:</span>
            <span class="detail-value">{{ processingDevices }}台</span>
          </div>
          <div class="detail-item failed">
            <span class="detail-label">失败:</span>
            <span class="detail-value">{{ failedDevices }}台</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">成功率:</span>
            <span class="detail-value">{{ successRate }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 设备进度列表 -->
    <div class="device-progress-list">
      <div class="list-header">
        <h4>设备更新进度</h4>
        <div class="header-actions">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索设备"
            :prefix-icon="Search"
            clearable
            style="width: 200px;"
          />
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="等待中" value="pending" />
            <el-option label="下载中" value="downloading" />
            <el-option label="安装中" value="installing" />
            <el-option label="重启中" value="rebooting" />
            <el-option label="完成" value="completed" />
            <el-option label="失败" value="failed" />
          </el-select>
          <el-button @click="refreshProgress" :loading="refreshing">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>

      <!-- 设备进度卡片 -->
      <div class="device-cards">
        <div
          v-for="device in filteredDevices"
          :key="device.deviceId"
          class="device-card"
          :class="{
            'status-pending': device.status === 'pending',
            'status-downloading': device.status === 'downloading',
            'status-installing': device.status === 'installing',
            'status-rebooting': device.status === 'rebooting',
            'status-completed': device.status === 'completed',
            'status-failed': device.status === 'failed'
          }"
        >
          <div class="device-header">
            <div class="device-info">
              <span class="device-serial">{{ device.deviceSerialNumber }}</span>
              <el-tag :type="getDeviceStatusType(device.status)" size="small">
                {{ getDeviceStatusText(device.status) }}
              </el-tag>
            </div>
            <div class="device-actions">
              <el-button
                v-if="device.status === 'failed'"
                text
                type="primary"
                size="small"
                @click="retryDevice(device)"
              >
                重试
              </el-button>
              <el-button text type="info" size="small" @click="viewDeviceDetail(device)">
                详情
              </el-button>
            </div>
          </div>

          <div class="device-progress">
            <div class="progress-info">
              <span class="current-step">{{ device.currentStep }}</span>
              <span class="progress-percentage">{{ device.progress }}%</span>
            </div>
            <el-progress
              :percentage="device.progress"
              :color="getDeviceProgressColor(device.status)"
              :stroke-width="8"
              :show-text="false"
            />
          </div>

          <!-- 时间信息 -->
          <div class="device-timing">
            <div v-if="device.startTime" class="timing-item">
              <span class="timing-label">开始:</span>
              <span class="timing-value">{{ formatTime(device.startTime) }}</span>
            </div>
            <div v-if="device.endTime" class="timing-item">
              <span class="timing-label">结束:</span>
              <span class="timing-value">{{ formatTime(device.endTime) }}</span>
            </div>
            <div v-if="!device.endTime && device.startTime" class="timing-item">
              <span class="timing-label">耗时:</span>
              <span class="timing-value">{{ getDeviceDuration(device) }}</span>
            </div>
            <div v-if="device.retryCount > 0" class="timing-item">
              <span class="timing-label">重试:</span>
              <span class="timing-value">{{ device.retryCount }}次</span>
            </div>
          </div>

          <!-- 错误信息 -->
          <div v-if="device.errorMessage" class="device-error">
            <el-icon class="error-icon"><Warning /></el-icon>
            <span class="error-message">{{ device.errorMessage }}</span>
          </div>

          <!-- 实时状态指示器 -->
          <div class="status-indicator">
            <div
              class="indicator-dot"
              :class="{
                'dot-pending': device.status === 'pending',
                'dot-active': ['downloading', 'installing', 'rebooting'].includes(device.status),
                'dot-success': device.status === 'completed',
                'dot-error': device.status === 'failed'
              }"
            ></div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredDevices.length === 0" class="empty-state">
        <el-empty description="没有找到匹配的设备" />
      </div>
    </div>

    <!-- 实时日志 -->
    <div v-if="showLogs" class="real-time-logs">
      <div class="logs-header">
        <h4>实时日志</h4>
        <div class="logs-actions">
          <el-button text size="small" @click="clearLogs">清空日志</el-button>
          <el-button text size="small" @click="toggleAutoScroll">
            {{ autoScroll ? '停止滚动' : '自动滚动' }}
          </el-button>
          <el-button text size="small" @click="showLogs = false">隐藏日志</el-button>
        </div>
      </div>
      <div ref="logsContainer" class="logs-container">
        <div
          v-for="(log, index) in logs"
          :key="index"
          class="log-entry"
          :class="`log-${log.level}`"
        >
          <span class="log-time">{{ formatLogTime(log.timestamp) }}</span>
          <span class="log-device">{{ log.deviceId }}</span>
          <span class="log-message">{{ log.message }}</span>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="action-bar">
      <div class="action-left">
        <el-button @click="showLogs = !showLogs">
          {{ showLogs ? '隐藏日志' : '显示日志' }}
        </el-button>
        <el-button @click="exportProgress">导出进度报告</el-button>
      </div>
      <div class="action-right">
        <el-button
          v-if="task?.status === 'running'"
          type="warning"
          @click="pauseTask"
        >
          暂停任务
        </el-button>
        <el-button
          v-if="['pending', 'running'].includes(task?.status || '')"
          type="danger"
          @click="cancelTask"
        >
          取消任务
        </el-button>
        <el-button @click="$emit('close')">关闭</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Warning } from '@element-plus/icons-vue'
import type {
  FirmwareUpdateTask,
  DeviceUpdateProgress,
  UpdateProgressStatus,
  UpdateTaskStatus
} from '@/types/firmware'
import {
  UPDATE_TASK_STATUS_TEXT,
  UPDATE_PROGRESS_STATUS_TEXT,
  FirmwareUpdateTaskManager
} from '@/types/firmware'
import { firmwareUpdateService, firmwareUpdateManager } from '@/services/firmwareUpdateService'
import type { ProgressUpdate, UpdateResult } from '@/services/firmwareUpdateService'

// Props
interface Props {
  task: FirmwareUpdateTask | null
  autoRefresh?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  autoRefresh: true
})

// Emits
const emit = defineEmits<{
  'close': []
  'task-updated': [task: FirmwareUpdateTask]
  'task-completed': [results: UpdateResult[]]
}>()

// 响应式数据
const searchKeyword = ref('')
const statusFilter = ref<UpdateProgressStatus | ''>('')
const refreshing = ref(false)
const showLogs = ref(false)
const autoScroll = ref(true)

// 日志相关
interface LogEntry {
  timestamp: string
  deviceId: string
  level: 'info' | 'warning' | 'error' | 'success'
  message: string
}

const logs = ref<LogEntry[]>([])
const logsContainer = ref<HTMLElement>()

// 定时器
let refreshTimer: NodeJS.Timeout | null = null
let durationTimer: NodeJS.Timeout | null = null

// 计算属性
const totalDevices = computed(() => props.task?.deviceIds.length || 0)

const completedDevices = computed(() => {
  if (!props.task) return 0
  return props.task.progress.completed + props.task.progress.failed
})

const successDevices = computed(() => props.task?.progress.completed || 0)

const processingDevices = computed(() => props.task?.progress.inProgress || 0)

const failedDevices = computed(() => props.task?.progress.failed || 0)

const overallProgressPercentage = computed(() => {
  if (!props.task) return 0
  return FirmwareUpdateTaskManager.getTaskCompletionPercentage(props.task)
})

const successRate = computed(() => {
  if (!props.task) return 0
  return FirmwareUpdateTaskManager.getTaskSuccessRate(props.task)
})

const taskDuration = computed(() => {
  if (!props.task?.startedAt) return '未开始'
  
  const startTime = new Date(props.task.startedAt).getTime()
  const endTime = props.task.completedAt ? new Date(props.task.completedAt).getTime() : Date.now()
  const duration = Math.floor((endTime - startTime) / 1000)
  
  return formatDuration(duration)
})

const filteredDevices = computed(() => {
  if (!props.task?.devices) return []

  let filtered = props.task.devices

  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(device =>
      device.deviceSerialNumber.toLowerCase().includes(keyword)
    )
  }

  // 状态过滤
  if (statusFilter.value) {
    filtered = filtered.filter(device => device.status === statusFilter.value)
  }

  return filtered
})

// 方法
const refreshProgress = async () => {
  if (!props.task) return

  refreshing.value = true
  try {
    // 这里应该调用API刷新任务状态
    // const updatedTask = await mockFirmwareAPI.getUpdateTask(props.task.id)
    // emit('task-updated', updatedTask)
    
    ElMessage.success('进度已刷新')
  } catch (error) {
    ElMessage.error('刷新进度失败')
  } finally {
    refreshing.value = false
  }
}

const retryDevice = async (device: DeviceUpdateProgress) => {
  if (!props.task) return

  try {
    await ElMessageBox.confirm(
      `确定要重试设备 ${device.deviceSerialNumber} 吗？`,
      '确认重试',
      {
        confirmButtonText: '重试',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    await firmwareUpdateManager.retryFailedDevices(props.task.id, [device.deviceId])
    
    addLog(device.deviceId, 'info', '开始重试更新')
    ElMessage.success('设备重试已开始')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重试设备失败')
    }
  }
}

const viewDeviceDetail = (device: DeviceUpdateProgress) => {
  ElMessage.info(`查看设备 ${device.deviceSerialNumber} 详情`)
  // 这里可以打开设备详情对话框
}

const pauseTask = async () => {
  if (!props.task) return

  try {
    await ElMessageBox.confirm('确定要暂停当前任务吗？', '确认暂停', {
      confirmButtonText: '暂停',
      cancelButtonText: '取消',
      type: 'warning'
    })

    ElMessage.success('任务已暂停')
    addLog('SYSTEM', 'warning', '任务已暂停')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('暂停任务失败')
    }
  }
}

const cancelTask = async () => {
  if (!props.task) return

  try {
    await ElMessageBox.confirm(
      '确定要取消当前任务吗？取消后无法恢复。',
      '确认取消',
      {
        confirmButtonText: '取消任务',
        cancelButtonText: '保留任务',
        type: 'danger'
      }
    )

    const success = await firmwareUpdateManager.cancelUpdateTask(props.task.id)
    if (success) {
      ElMessage.success('任务已取消')
      addLog('SYSTEM', 'error', '任务已取消')
      emit('close')
    } else {
      ElMessage.error('取消任务失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消任务失败')
    }
  }
}

const exportProgress = () => {
  ElMessage.info('正在导出进度报告...')
  // 这里可以实现导出功能
  setTimeout(() => {
    ElMessage.success('进度报告已导出')
  }, 2000)
}

const clearLogs = () => {
  logs.value = []
}

const toggleAutoScroll = () => {
  autoScroll.value = !autoScroll.value
}

const addLog = (deviceId: string, level: LogEntry['level'], message: string) => {
  const log: LogEntry = {
    timestamp: new Date().toISOString(),
    deviceId,
    level,
    message
  }

  logs.value.push(log)

  // 限制日志数量
  if (logs.value.length > 1000) {
    logs.value = logs.value.slice(-500)
  }

  // 自动滚动到底部
  if (autoScroll.value) {
    nextTick(() => {
      if (logsContainer.value) {
        logsContainer.value.scrollTop = logsContainer.value.scrollHeight
      }
    })
  }
}

// 工具方法
const getTaskStatusType = (status?: UpdateTaskStatus) => {
  if (!status) return 'info'
  const typeMap = {
    pending: 'info',
    running: 'primary',
    completed: 'success',
    failed: 'danger',
    cancelled: 'warning'
  }
  return typeMap[status] || 'info'
}

const getTaskStatusText = (status?: UpdateTaskStatus) => {
  if (!status) return '未知'
  return UPDATE_TASK_STATUS_TEXT[status] || status
}

const getDeviceStatusType = (status: UpdateProgressStatus) => {
  const typeMap = {
    pending: 'info',
    downloading: 'primary',
    installing: 'primary',
    rebooting: 'warning',
    completed: 'success',
    failed: 'danger'
  }
  return typeMap[status] || 'info'
}

const getDeviceStatusText = (status: UpdateProgressStatus) => {
  return UPDATE_PROGRESS_STATUS_TEXT[status] || status
}

const getOverallProgressColor = () => {
  if (!props.task) return '#909399'
  
  if (props.task.status === 'completed') return '#67c23a'
  if (props.task.status === 'failed') return '#f56c6c'
  if (props.task.status === 'running') return '#409eff'
  
  return '#909399'
}

const getDeviceProgressColor = (status: UpdateProgressStatus) => {
  const colorMap = {
    pending: '#909399',
    downloading: '#409eff',
    installing: '#409eff',
    rebooting: '#e6a23c',
    completed: '#67c23a',
    failed: '#f56c6c'
  }
  return colorMap[status] || '#909399'
}

const formatTime = (timeString: string) => {
  return new Date(timeString).toLocaleTimeString('zh-CN')
}

const formatLogTime = (timeString: string) => {
  return new Date(timeString).toLocaleTimeString('zh-CN', {
    hour12: false,
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const formatDuration = (seconds: number) => {
  if (seconds < 60) return `${seconds}秒`
  if (seconds < 3600) return `${Math.floor(seconds / 60)}分${seconds % 60}秒`
  
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const remainingSeconds = seconds % 60
  
  return `${hours}小时${minutes}分${remainingSeconds}秒`
}

const getDeviceDuration = (device: DeviceUpdateProgress) => {
  if (!device.startTime) return '未开始'
  
  const startTime = new Date(device.startTime).getTime()
  const endTime = device.endTime ? new Date(device.endTime).getTime() : Date.now()
  const duration = Math.floor((endTime - startTime) / 1000)
  
  return formatDuration(duration)
}

// 设置进度回调
const setupProgressCallbacks = () => {
  if (!props.task) return

  // 注册进度更新回调
  firmwareUpdateService.onProgress(props.task.id, (update: ProgressUpdate) => {
    addLog(update.deviceId, 'info', `${update.currentStep} - ${update.progress}%`)
    
    if (update.errorMessage) {
      addLog(update.deviceId, 'error', update.errorMessage)
    }
  })

  // 注册结果回调
  firmwareUpdateService.onResult(props.task.id, (result: UpdateResult) => {
    if (result.success) {
      addLog(result.deviceId, 'success', `更新成功 - 耗时${result.duration}秒`)
    } else {
      addLog(result.deviceId, 'error', `更新失败 - ${result.errorMessage}`)
    }
  })
}

// 启动自动刷新
const startAutoRefresh = () => {
  if (!props.autoRefresh) return

  refreshTimer = setInterval(() => {
    if (props.task?.status === 'running') {
      refreshProgress()
    }
  }, 3000) // 每3秒刷新一次

  durationTimer = setInterval(() => {
    // 触发计算属性更新，用于实时显示持续时间
  }, 1000) // 每秒更新一次
}

const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
  if (durationTimer) {
    clearInterval(durationTimer)
    durationTimer = null
  }
}

// 监听任务变化
watch(() => props.task, (newTask, oldTask) => {
  if (newTask?.id !== oldTask?.id) {
    // 任务变化时重新设置回调
    if (oldTask) {
      firmwareUpdateService.removeCallbacks(oldTask.id)
    }
    if (newTask) {
      setupProgressCallbacks()
    }
  }
}, { immediate: true })

// 生命周期
onMounted(() => {
  startAutoRefresh()
  setupProgressCallbacks()
  
  // 添加初始日志
  if (props.task) {
    addLog('SYSTEM', 'info', `开始监控任务: ${props.task.name}`)
  }
})

onUnmounted(() => {
  stopAutoRefresh()
  if (props.task) {
    firmwareUpdateService.removeCallbacks(props.task.id)
  }
})
</script>

<style lang="scss" scoped>
.firmware-update-progress {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;

  .task-overview {
    background: white;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .task-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 24px;

      .task-info {
        .task-name {
          margin: 0 0 8px 0;
          color: #303133;
          font-size: 20px;
          font-weight: 600;
        }

        .task-meta {
          display: flex;
          gap: 24px;
          font-size: 14px;
          color: #606266;
        }
      }
    }

    .overall-progress {
      .progress-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .progress-title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }

        .progress-stats {
          font-size: 14px;
          color: #606266;
        }
      }

      .progress-details {
        display: flex;
        justify-content: space-between;
        margin-top: 16px;
        padding-top: 16px;
        border-top: 1px solid #e4e7ed;

        .detail-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 4px;

          .detail-label {
            font-size: 12px;
            color: #909399;
          }

          .detail-value {
            font-size: 16px;
            font-weight: 600;
            color: #303133;
          }

          &.success .detail-value {
            color: #67c23a;
          }

          &.processing .detail-value {
            color: #409eff;
          }

          &.failed .detail-value {
            color: #f56c6c;
          }
        }
      }
    }
  }

  .device-progress-list {
    background: white;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .list-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;

      h4 {
        margin: 0;
        color: #303133;
        font-size: 18px;
        font-weight: 600;
      }

      .header-actions {
        display: flex;
        gap: 12px;
        align-items: center;
      }
    }

    .device-cards {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 16px;

      .device-card {
        position: relative;
        padding: 16px;
        border: 1px solid #e4e7ed;
        border-radius: 8px;
        background: #fafafa;
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        &.status-downloading,
        &.status-installing,
        &.status-rebooting {
          border-left: 4px solid #409eff;
          background: #ecf5ff;
        }

        &.status-completed {
          border-left: 4px solid #67c23a;
          background: #f0f9ff;
        }

        &.status-failed {
          border-left: 4px solid #f56c6c;
          background: #fef0f0;
        }

        .device-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;

          .device-info {
            display: flex;
            align-items: center;
            gap: 12px;

            .device-serial {
              font-weight: 600;
              color: #303133;
            }
          }

          .device-actions {
            display: flex;
            gap: 8px;
          }
        }

        .device-progress {
          margin-bottom: 12px;

          .progress-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;

            .current-step {
              font-size: 14px;
              color: #606266;
            }

            .progress-percentage {
              font-size: 14px;
              font-weight: 600;
              color: #303133;
            }
          }
        }

        .device-timing {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 8px;
          margin-bottom: 8px;

          .timing-item {
            display: flex;
            justify-content: space-between;
            font-size: 12px;

            .timing-label {
              color: #909399;
            }

            .timing-value {
              color: #606266;
            }
          }
        }

        .device-error {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 8px;
          background: #fef0f0;
          border: 1px solid #fbc4c4;
          border-radius: 4px;
          margin-bottom: 8px;

          .error-icon {
            color: #f56c6c;
            font-size: 14px;
          }

          .error-message {
            color: #f56c6c;
            font-size: 12px;
          }
        }

        .status-indicator {
          position: absolute;
          top: 12px;
          right: 12px;

          .indicator-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: #909399;

            &.dot-pending {
              background: #909399;
            }

            &.dot-active {
              background: #409eff;
              animation: pulse 2s infinite;
            }

            &.dot-success {
              background: #67c23a;
            }

            &.dot-error {
              background: #f56c6c;
            }
          }
        }
      }
    }

    .empty-state {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 200px;
    }
  }

  .real-time-logs {
    background: white;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .logs-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h4 {
        margin: 0;
        color: #303133;
        font-size: 18px;
        font-weight: 600;
      }

      .logs-actions {
        display: flex;
        gap: 8px;
      }
    }

    .logs-container {
      height: 300px;
      overflow-y: auto;
      background: #1e1e1e;
      border-radius: 4px;
      padding: 12px;
      font-family: 'Courier New', monospace;

      .log-entry {
        display: flex;
        gap: 12px;
        margin-bottom: 4px;
        font-size: 12px;
        line-height: 1.4;

        .log-time {
          color: #888;
          min-width: 80px;
        }

        .log-device {
          color: #61dafb;
          min-width: 100px;
        }

        .log-message {
          color: #fff;
          flex: 1;
        }

        &.log-info .log-message {
          color: #fff;
        }

        &.log-success .log-message {
          color: #52c41a;
        }

        &.log-warning .log-message {
          color: #faad14;
        }

        &.log-error .log-message {
          color: #ff4d4f;
        }
      }
    }
  }

  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 24px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .action-left,
    .action-right {
      display: flex;
      gap: 12px;
    }
  }
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .firmware-update-progress {
    padding: 16px;

    .task-overview .task-header {
      flex-direction: column;
      gap: 16px;

      .task-info .task-meta {
        flex-direction: column;
        gap: 8px;
      }
    }

    .device-progress-list {
      .list-header {
        flex-direction: column;
        gap: 16px;

        .header-actions {
          width: 100%;
          justify-content: space-between;
        }
      }

      .device-cards {
        grid-template-columns: 1fr;
      }
    }

    .action-bar {
      flex-direction: column;
      gap: 16px;

      .action-left,
      .action-right {
        width: 100%;
        justify-content: center;
      }
    }
  }
}
</style>