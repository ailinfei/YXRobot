<template>
  <el-dialog
    v-model="visible"
    :title="`设备日志 - ${device?.deviceSerialNumber || ''}`"
    width="900px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div v-if="device" class="device-logs-dialog">
      <!-- 设备信息 -->
      <div class="device-info-section">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="设备序列号">{{ device.deviceSerialNumber }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getDeviceStatusType(device.status)" size="small">
              {{ getDeviceStatusText(device.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="当前进度">{{ device.progress }}%</el-descriptions-item>
          <el-descriptions-item label="当前步骤">{{ device.currentStep }}</el-descriptions-item>
          <el-descriptions-item label="重试次数">{{ device.retryCount }}次</el-descriptions-item>
          <el-descriptions-item label="开始时间">
            {{ device.startTime ? formatDateTime(device.startTime) : '未开始' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 日志筛选 -->
      <div class="log-filters">
        <div class="filters-left">
          <el-select v-model="logLevelFilter" placeholder="日志级别" clearable style="width: 120px;">
            <el-option label="全部" value="" />
            <el-option label="调试" value="debug" />
            <el-option label="信息" value="info" />
            <el-option label="警告" value="warning" />
            <el-option label="错误" value="error" />
          </el-select>
          <el-input
            v-model="logSearchKeyword"
            placeholder="搜索日志内容"
            :prefix-icon="Search"
            clearable
            style="width: 200px;"
          />
          <el-date-picker
            v-model="logTimeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 300px;"
          />
        </div>
        <div class="filters-right">
          <el-button @click="refreshLogs" :loading="logsLoading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button @click="clearLogs">
            <el-icon><Delete /></el-icon>
            清空
          </el-button>
          <el-button @click="exportLogs">
            <el-icon><Download /></el-icon>
            导出
          </el-button>
          <el-button @click="toggleAutoScroll">
            <el-icon><Position /></el-icon>
            {{ autoScroll ? '停止滚动' : '自动滚动' }}
          </el-button>
        </div>
      </div>

      <!-- 日志内容 -->
      <div class="logs-content">
        <div ref="logsContainer" class="logs-container" :class="{ 'auto-scroll': autoScroll }">
          <div
            v-for="(log, index) in filteredLogs"
            :key="index"
            class="log-entry"
            :class="`log-${log.level}`"
          >
            <div class="log-header">
              <span class="log-timestamp">{{ formatLogTime(log.timestamp) }}</span>
              <el-tag :type="getLogLevelType(log.level)" size="small">
                {{ getLogLevelText(log.level) }}
              </el-tag>
              <span v-if="log.component" class="log-component">[{{ log.component }}]</span>
            </div>
            <div class="log-message">{{ log.message }}</div>
            <div v-if="log.details" class="log-details">
              <el-collapse>
                <el-collapse-item title="详细信息" name="details">
                  <pre class="log-details-content">{{ formatLogDetails(log.details) }}</pre>
                </el-collapse-item>
              </el-collapse>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="filteredLogs.length === 0 && !logsLoading" class="empty-logs">
            <el-empty description="暂无日志数据" />
          </div>

          <!-- 加载状态 -->
          <div v-if="logsLoading" class="loading-logs">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载日志中...</span>
          </div>
        </div>
      </div>

      <!-- 实时状态指示器 -->
      <div class="status-indicators">
        <div class="indicator-group">
          <div class="indicator-title">连接状态</div>
          <div class="indicator-item">
            <div class="indicator-dot" :class="{ 'dot-active': device.isOnline }"></div>
            <span>{{ device.isOnline ? '在线' : '离线' }}</span>
          </div>
        </div>

        <div class="indicator-group">
          <div class="indicator-title">更新状态</div>
          <div class="indicator-item">
            <div
              class="indicator-dot"
              :class="{
                'dot-pending': device.status === 'pending',
                'dot-active': ['downloading', 'installing', 'rebooting'].includes(device.status),
                'dot-success': device.status === 'completed',
                'dot-error': device.status === 'failed'
              }"
            ></div>
            <span>{{ getDeviceStatusText(device.status) }}</span>
          </div>
        </div>

        <div v-if="device.signalStrength !== undefined" class="indicator-group">
          <div class="indicator-title">信号强度</div>
          <div class="indicator-item">
            <el-progress
              :percentage="device.signalStrength"
              :color="getSignalColor(device.signalStrength)"
              :stroke-width="6"
              :show-text="false"
              style="width: 80px;"
            />
            <span>{{ device.signalStrength }}%</span>
          </div>
        </div>

        <div class="indicator-group">
          <div class="indicator-title">更新进度</div>
          <div class="indicator-item">
            <el-progress
              :percentage="device.progress"
              :color="getProgressColor(device.status)"
              :stroke-width="6"
              :show-text="false"
              style="width: 100px;"
            />
            <span>{{ device.progress }}%</span>
          </div>
        </div>
      </div>

      <!-- 错误信息 -->
      <div v-if="device.errorMessage" class="error-section">
        <el-alert
          :title="device.errorMessage"
          type="error"
          :closable="false"
          show-icon
        >
          <template #default>
            <div class="error-details">
              <div v-if="device.errorCode" class="error-code">
                错误代码: {{ device.errorCode }}
              </div>
              <div class="error-time">
                发生时间: {{ formatDateTime(device.endTime || new Date().toISOString()) }}
              </div>
              <div class="error-actions">
                <el-button text type="primary" size="small" @click="viewErrorDetails">
                  查看详细错误信息
                </el-button>
                <el-button text type="primary" size="small" @click="reportError">
                  报告错误
                </el-button>
              </div>
            </div>
          </template>
        </el-alert>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button v-if="device?.status === 'failed'" type="primary" @click="retryDevice">
          重试更新
        </el-button>
        <el-button type="info" @click="downloadLogs">
          下载日志
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Delete,
  Download,
  Position,
  Loading
} from '@element-plus/icons-vue'
import type {
  DeviceUpdateProgress,
  UpdateProgressStatus
} from '@/types/firmware'
import {
  UPDATE_PROGRESS_STATUS_TEXT
} from '@/types/firmware'

// Props
interface Props {
  modelValue: boolean
  device: (DeviceUpdateProgress & { isOnline?: boolean; signalStrength?: number; errorCode?: string }) | null
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'retry-device': [device: DeviceUpdateProgress]
}>()

// 日志条目接口
interface LogEntry {
  timestamp: string
  level: 'debug' | 'info' | 'warning' | 'error'
  component?: string
  message: string
  details?: any
}

// 响应式数据
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const logsLoading = ref(false)
const autoScroll = ref(true)
const logLevelFilter = ref<string>('')
const logSearchKeyword = ref('')
const logTimeRange = ref<[string, string] | null>(null)

const logs = ref<LogEntry[]>([])
const logsContainer = ref<HTMLElement>()

// 定时器
let refreshTimer: NodeJS.Timeout | null = null

// 计算属性
const filteredLogs = computed(() => {
  let filtered = logs.value

  // 级别过滤
  if (logLevelFilter.value) {
    filtered = filtered.filter(log => log.level === logLevelFilter.value)
  }

  // 关键词搜索
  if (logSearchKeyword.value) {
    const keyword = logSearchKeyword.value.toLowerCase()
    filtered = filtered.filter(log =>
      log.message.toLowerCase().includes(keyword) ||
      (log.component && log.component.toLowerCase().includes(keyword))
    )
  }

  // 时间范围过滤
  if (logTimeRange.value && logTimeRange.value.length === 2) {
    const [startTime, endTime] = logTimeRange.value
    filtered = filtered.filter(log => {
      const logTime = new Date(log.timestamp).getTime()
      return logTime >= new Date(startTime).getTime() && logTime <= new Date(endTime).getTime()
    })
  }

  return filtered.sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime())
})

// 方法
const handleClose = () => {
  visible.value = false
  stopAutoRefresh()
}

const refreshLogs = async () => {
  if (!props.device) return

  logsLoading.value = true
  try {
    // 模拟加载日志数据
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 生成模拟日志
    const newLogs = generateMockLogs(props.device)
    logs.value = newLogs
    
    ElMessage.success('日志已刷新')
    
    // 自动滚动到底部
    if (autoScroll.value) {
      scrollToBottom()
    }
  } catch (error) {
    ElMessage.error('刷新日志失败')
  } finally {
    logsLoading.value = false
  }
}

const clearLogs = () => {
  logs.value = []
  ElMessage.success('日志已清空')
}

const exportLogs = () => {
  if (filteredLogs.value.length === 0) {
    ElMessage.warning('没有日志可导出')
    return
  }

  // 生成日志文本
  const logText = filteredLogs.value.map(log => {
    let line = `[${log.timestamp}] [${log.level.toUpperCase()}]`
    if (log.component) {
      line += ` [${log.component}]`
    }
    line += ` ${log.message}`
    if (log.details) {
      line += `\n${JSON.stringify(log.details, null, 2)}`
    }
    return line
  }).join('\n')

  // 创建下载链接
  const blob = new Blob([logText], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `device-${props.device?.deviceSerialNumber}-logs-${new Date().toISOString().slice(0, 10)}.txt`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)

  ElMessage.success('日志已导出')
}

const toggleAutoScroll = () => {
  autoScroll.value = !autoScroll.value
  if (autoScroll.value) {
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (logsContainer.value) {
      logsContainer.value.scrollTop = logsContainer.value.scrollHeight
    }
  })
}

const retryDevice = () => {
  if (props.device) {
    emit('retry-device', props.device)
    handleClose()
  }
}

const downloadLogs = () => {
  exportLogs()
}

const viewErrorDetails = () => {
  if (!props.device?.errorMessage) return

  ElMessageBox.alert(
    props.device.errorMessage,
    '详细错误信息',
    {
      confirmButtonText: '确定',
      type: 'error'
    }
  )
}

const reportError = () => {
  ElMessage.info('错误报告功能开发中...')
}

// 工具方法
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

const getLogLevelType = (level: string) => {
  const typeMap = {
    debug: 'info',
    info: 'primary',
    warning: 'warning',
    error: 'danger'
  }
  return typeMap[level] || 'info'
}

const getLogLevelText = (level: string) => {
  const textMap = {
    debug: '调试',
    info: '信息',
    warning: '警告',
    error: '错误'
  }
  return textMap[level] || level
}

const getSignalColor = (strength: number) => {
  if (strength >= 70) return '#67c23a'
  if (strength >= 40) return '#e6a23c'
  return '#f56c6c'
}

const getProgressColor = (status: UpdateProgressStatus) => {
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

const formatDateTime = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const formatLogTime = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

const formatLogDetails = (details: any) => {
  if (typeof details === 'string') return details
  return JSON.stringify(details, null, 2)
}

// 生成模拟日志数据
const generateMockLogs = (device: DeviceUpdateProgress): LogEntry[] => {
  const logs: LogEntry[] = []
  const baseTime = device.startTime ? new Date(device.startTime).getTime() : Date.now() - 300000

  // 根据设备状态生成相应的日志
  const statusLogs = {
    pending: [
      { level: 'info' as const, component: 'SYSTEM', message: '设备更新任务已创建' },
      { level: 'info' as const, component: 'SYSTEM', message: '等待设备响应...' }
    ],
    downloading: [
      { level: 'info' as const, component: 'SYSTEM', message: '设备更新任务已创建' },
      { level: 'info' as const, component: 'NETWORK', message: '开始连接设备...' },
      { level: 'info' as const, component: 'NETWORK', message: '设备连接成功' },
      { level: 'info' as const, component: 'DOWNLOAD', message: '开始下载固件文件' },
      { level: 'info' as const, component: 'DOWNLOAD', message: `下载进度: ${device.progress}%` }
    ],
    installing: [
      { level: 'info' as const, component: 'DOWNLOAD', message: '固件下载完成' },
      { level: 'info' as const, component: 'VERIFY', message: '验证固件文件完整性...' },
      { level: 'info' as const, component: 'VERIFY', message: '固件文件验证通过' },
      { level: 'info' as const, component: 'INSTALL', message: '开始安装固件' },
      { level: 'info' as const, component: 'INSTALL', message: `安装进度: ${device.progress}%` }
    ],
    rebooting: [
      { level: 'info' as const, component: 'INSTALL', message: '固件安装完成' },
      { level: 'info' as const, component: 'SYSTEM', message: '准备重启设备...' },
      { level: 'warning' as const, component: 'SYSTEM', message: '设备正在重启，请稍候...' }
    ],
    completed: [
      { level: 'info' as const, component: 'SYSTEM', message: '设备重启完成' },
      { level: 'info' as const, component: 'VERIFY', message: '验证固件版本...' },
      { level: 'info' as const, component: 'VERIFY', message: '固件版本验证通过' },
      { level: 'info' as const, component: 'SYSTEM', message: '固件更新成功完成' }
    ],
    failed: [
      { level: 'error' as const, component: 'SYSTEM', message: '固件更新失败' },
      { level: 'error' as const, component: 'ERROR', message: device.errorMessage || '未知错误' }
    ]
  }

  const deviceLogs = statusLogs[device.status] || []
  
  deviceLogs.forEach((log, index) => {
    logs.push({
      timestamp: new Date(baseTime + index * 30000).toISOString(),
      level: log.level,
      component: log.component,
      message: log.message,
      details: index === deviceLogs.length - 1 && device.status === 'failed' ? {
        errorCode: device.errorCode || 'UNKNOWN_ERROR',
        deviceId: device.deviceId,
        retryCount: device.retryCount
      } : undefined
    })
  })

  // 添加一些调试日志
  if (device.status !== 'pending') {
    logs.splice(1, 0, {
      timestamp: new Date(baseTime + 5000).toISOString(),
      level: 'debug',
      component: 'NETWORK',
      message: `设备IP: 192.168.1.${Math.floor(Math.random() * 254) + 1}`,
      details: {
        deviceId: device.deviceId,
        serialNumber: device.deviceSerialNumber,
        networkInterface: 'WiFi'
      }
    })
  }

  return logs
}

// 启动自动刷新
const startAutoRefresh = () => {
  refreshTimer = setInterval(() => {
    if (props.device && ['downloading', 'installing', 'rebooting'].includes(props.device.status)) {
      // 添加新的日志条目
      const newLog: LogEntry = {
        timestamp: new Date().toISOString(),
        level: 'info',
        component: props.device.status.toUpperCase(),
        message: `${props.device.currentStep} - ${props.device.progress}%`
      }
      
      logs.value.push(newLog)
      
      // 限制日志数量
      if (logs.value.length > 500) {
        logs.value = logs.value.slice(-300)
      }
      
      // 自动滚动
      if (autoScroll.value) {
        scrollToBottom()
      }
    }
  }, 5000) // 每5秒添加一条日志
}

const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

// 监听设备变化
watch(() => props.device, (newDevice) => {
  if (newDevice && visible.value) {
    refreshLogs()
  }
}, { immediate: true })

watch(visible, (newVisible) => {
  if (newVisible) {
    refreshLogs()
    startAutoRefresh()
  } else {
    stopAutoRefresh()
  }
})

// 生命周期
onMounted(() => {
  if (visible.value) {
    refreshLogs()
    startAutoRefresh()
  }
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style lang="scss" scoped>
.device-logs-dialog {
  .device-info-section {
    margin-bottom: 24px;
  }

  .log-filters {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 6px;

    .filters-left {
      display: flex;
      gap: 12px;
      align-items: center;
    }

    .filters-right {
      display: flex;
      gap: 8px;
    }
  }

  .logs-content {
    margin-bottom: 24px;

    .logs-container {
      height: 400px;
      overflow-y: auto;
      background: #1e1e1e;
      border-radius: 6px;
      padding: 16px;
      font-family: 'Courier New', monospace;

      &.auto-scroll {
        scroll-behavior: smooth;
      }

      .log-entry {
        margin-bottom: 12px;
        padding: 8px;
        border-radius: 4px;
        background: rgba(255, 255, 255, 0.05);

        &.log-debug {
          border-left: 3px solid #909399;
        }

        &.log-info {
          border-left: 3px solid #409eff;
        }

        &.log-warning {
          border-left: 3px solid #e6a23c;
        }

        &.log-error {
          border-left: 3px solid #f56c6c;
        }

        .log-header {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 4px;

          .log-timestamp {
            color: #888;
            font-size: 12px;
          }

          .log-component {
            color: #61dafb;
            font-size: 12px;
            font-weight: 600;
          }
        }

        .log-message {
          color: #fff;
          font-size: 14px;
          line-height: 1.4;
          margin-bottom: 8px;
        }

        .log-details {
          .log-details-content {
            background: rgba(0, 0, 0, 0.3);
            padding: 8px;
            border-radius: 4px;
            color: #ccc;
            font-size: 12px;
            margin: 0;
            white-space: pre-wrap;
            word-break: break-all;
          }
        }
      }

      .empty-logs,
      .loading-logs {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 200px;
        color: #888;

        .is-loading {
          font-size: 24px;
          margin-bottom: 8px;
        }
      }
    }
  }

  .status-indicators {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    margin-bottom: 24px;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 6px;

    .indicator-group {
      .indicator-title {
        font-size: 12px;
        color: #909399;
        margin-bottom: 8px;
      }

      .indicator-item {
        display: flex;
        align-items: center;
        gap: 8px;

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

        span {
          font-size: 14px;
          color: #303133;
        }
      }
    }
  }

  .error-section {
    margin-bottom: 24px;

    .error-details {
      .error-code,
      .error-time {
        margin-bottom: 8px;
        font-size: 14px;
        color: #606266;
      }

      .error-actions {
        display: flex;
        gap: 8px;
        margin-top: 12px;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
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
  .device-logs-dialog {
    .log-filters {
      flex-direction: column;
      gap: 16px;

      .filters-left {
        flex-direction: column;
        width: 100%;
        
        .el-select,
        .el-input,
        .el-date-picker {
          width: 100% !important;
        }
      }

      .filters-right {
        width: 100%;
        justify-content: center;
      }
    }

    .status-indicators {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}
</style>