<template>
  <div class="wizard-step-progress">
    <div class="step-header">
      <h3>{{ $t('fontPackage.wizard.progress.title') }}</h3>
      <p class="step-description">{{ $t('fontPackage.wizard.progress.description') }}</p>
    </div>

    <div class="progress-content">
      <!-- 训练状态卡片 -->
      <div class="training-status-card">
        <div class="status-header">
          <div class="status-info">
            <h4>{{ fontPackageName }}</h4>
            <div class="status-badges">
              <el-tag :type="getStatusType(trainingStatus)" size="large">
                {{ getStatusText(trainingStatus) }}
              </el-tag>
              <el-tag v-if="isConnected" type="success" size="small">
                <el-icon><Link /></el-icon>
                实时监控
              </el-tag>
              <el-tag v-else type="warning" size="small">
                <el-icon><Warning /></el-icon>
                离线模式
              </el-tag>
            </div>
          </div>
          
          <div class="action-buttons">
            <el-button 
              v-if="trainingStatus === 'pending'"
              type="primary"
              @click="startTraining"
              :loading="isStarting"
            >
              <el-icon><VideoPlay /></el-icon>
              开始训练
            </el-button>
            
            <el-button 
              v-if="trainingStatus === 'training'"
              type="warning"
              @click="pauseTraining"
              :loading="isPausing"
            >
              <el-icon><VideoPause /></el-icon>
              暂停训练
            </el-button>
            
            <el-button 
              v-if="trainingStatus === 'paused'"
              type="primary"
              @click="resumeTraining"
              :loading="isResuming"
            >
              <el-icon><VideoPlay /></el-icon>
              继续训练
            </el-button>
            
            <el-button 
              v-if="['training', 'paused'].includes(trainingStatus)"
              type="danger"
              @click="stopTraining"
              :loading="isStopping"
            >
              <el-icon><Close /></el-icon>
              停止训练
            </el-button>
            
            <el-dropdown v-if="trainingStatus === 'completed'" @command="handleAction">
              <el-button type="success">
                训练完成
                <el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="download">
                    <el-icon><Download /></el-icon>
                    下载字体包
                  </el-dropdown-item>
                  <el-dropdown-item command="preview">
                    <el-icon><View /></el-icon>
                    预览效果
                  </el-dropdown-item>
                  <el-dropdown-item command="publish">
                    <el-icon><Upload /></el-icon>
                    发布字体包
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>

      <!-- 进度监控组件 -->
      <div class="progress-monitor-container">
        <VisualProgressMonitor
          :package-id="packageId"
          :auto-start="trainingStatus === 'training'"
          @monitoring-started="handleMonitoringStarted"
          @monitoring-stopped="handleMonitoringStopped"
          @anomaly-resolved="handleAnomalyResolved"
        />
      </div>

      <!-- 训练日志 -->
      <div class="training-logs" v-if="showLogs">
        <div class="logs-header">
          <h4>训练日志</h4>
          <div class="logs-actions">
            <el-button size="small" @click="clearLogs">清空日志</el-button>
            <el-button size="small" @click="downloadLogs">下载日志</el-button>
            <el-button 
              size="small" 
              :type="autoScroll ? 'primary' : 'default'"
              @click="toggleAutoScroll"
            >
              {{ autoScroll ? '停止滚动' : '自动滚动' }}
            </el-button>
          </div>
        </div>
        
        <div class="logs-content" ref="logsContainer">
          <div 
            v-for="log in trainingLogs" 
            :key="log.id"
            class="log-entry"
            :class="`log-${log.level}`"
          >
            <span class="log-time">{{ formatLogTime(log.timestamp) }}</span>
            <span class="log-level">{{ log.level.toUpperCase() }}</span>
            <span class="log-message">{{ log.message }}</span>
            <el-button 
              v-if="log.details"
              size="small"
              text
              @click="showLogDetails(log)"
            >
              详情
            </el-button>
          </div>
          
          <div v-if="trainingLogs.length === 0" class="no-logs">
            暂无训练日志
          </div>
        </div>
      </div>
    </div>

    <!-- 步骤导航 -->
    <div class="step-navigation">
      <el-button @click="$emit('previous')" :disabled="!canGoBack">
        <el-icon><ArrowLeft /></el-icon>
        上一步
      </el-button>
      
      <div class="nav-center">
        <el-button 
          type="info" 
          text 
          @click="showLogs = !showLogs"
        >
          {{ showLogs ? '隐藏日志' : '显示日志' }}
        </el-button>
      </div>
      
      <el-button 
        type="primary"
        @click="$emit('next')"
        :disabled="!canGoNext"
      >
        下一步
        <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Link, Warning, VideoPlay, VideoPause, Close, ArrowDown, 
  Download, View, Upload, ArrowLeft, ArrowRight 
} from '@element-plus/icons-vue'
import VisualProgressMonitor from '../VisualProgressMonitor.vue'
import type { WizardData, StepValidation, TrainingLog } from '@/types/fontPackage'

interface Props {
  modelValue: WizardData
  validation?: StepValidation
  packageId?: number
}

interface Emits {
  (e: 'update:modelValue', value: WizardData): void
  (e: 'validate', validation: StepValidation): void
  (e: 'next'): void
  (e: 'previous'): void
}

const props = withDefaults(defineProps<Props>(), {
  validation: () => ({ isValid: false, errors: [], warnings: [] })
})

const emit = defineEmits<Emits>()

// 响应式数据
const trainingStatus = ref<'pending' | 'training' | 'paused' | 'completed' | 'failed'>('pending')
const isConnected = ref(false)
const isStarting = ref(false)
const isPausing = ref(false)
const isResuming = ref(false)
const isStopping = ref(false)
const showLogs = ref(false)
const autoScroll = ref(true)
const trainingLogs = ref<TrainingLog[]>([])
const logsContainer = ref<HTMLElement>()

// 计算属性
const fontPackageName = computed(() => {
  return props.modelValue.basicInfo?.name || '未命名字体包'
})

const canGoBack = computed(() => {
  return trainingStatus.value !== 'training'
})

const canGoNext = computed(() => {
  return trainingStatus.value === 'completed'
})

// 方法
const getStatusType = (status: string) => {
  const typeMap = {
    pending: 'info',
    training: 'warning',
    paused: 'warning',
    completed: 'success',
    failed: 'danger'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

const getStatusText = (status: string) => {
  const textMap = {
    pending: '等待开始',
    training: '训练中',
    paused: '已暂停',
    completed: '训练完成',
    failed: '训练失败'
  }
  return textMap[status as keyof typeof textMap] || status
}

const startTraining = async () => {
  try {
    await ElMessageBox.confirm(
      '确认开始训练？训练过程可能需要较长时间。',
      '开始训练',
      {
        confirmButtonText: '开始',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    isStarting.value = true
    
    // 模拟启动训练
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    trainingStatus.value = 'training'
    addLog('info', '训练已开始')
    
    ElMessage.success('训练已开始')
  } catch {
    // 用户取消
  } finally {
    isStarting.value = false
  }
}

const pauseTraining = async () => {
  try {
    await ElMessageBox.confirm(
      '确认暂停训练？',
      '暂停训练',
      {
        confirmButtonText: '暂停',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    isPausing.value = true
    
    // 模拟暂停训练
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    trainingStatus.value = 'paused'
    addLog('warning', '训练已暂停')
    
    ElMessage.success('训练已暂停')
  } catch {
    // 用户取消
  } finally {
    isPausing.value = false
  }
}

const resumeTraining = async () => {
  isResuming.value = true
  
  try {
    // 模拟恢复训练
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    trainingStatus.value = 'training'
    addLog('info', '训练已恢复')
    
    ElMessage.success('训练已恢复')
  } catch (error) {
    ElMessage.error('恢复训练失败')
  } finally {
    isResuming.value = false
  }
}

const stopTraining = async () => {
  try {
    await ElMessageBox.confirm(
      '确认停止训练？停止后将无法恢复当前进度。',
      '停止训练',
      {
        confirmButtonText: '停止',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    isStopping.value = true
    
    // 模拟停止训练
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    trainingStatus.value = 'failed'
    addLog('error', '训练已停止')
    
    ElMessage.success('训练已停止')
  } catch {
    // 用户取消
  } finally {
    isStopping.value = false
  }
}

const handleAction = (command: string) => {
  switch (command) {
    case 'download':
      downloadFontPackage()
      break
    case 'preview':
      previewFontPackage()
      break
    case 'publish':
      publishFontPackage()
      break
  }
}

const downloadFontPackage = () => {
  ElMessage.success('字体包下载功能开发中')
}

const previewFontPackage = () => {
  ElMessage.success('字体包预览功能开发中')
}

const publishFontPackage = () => {
  ElMessage.success('字体包发布功能开发中')
}

const handleMonitoringStarted = (packageId: number) => {
  isConnected.value = true
  addLog('info', `开始监控字体包 ${packageId}`)
}

const handleMonitoringStopped = () => {
  isConnected.value = false
  addLog('info', '停止监控')
}

const handleAnomalyResolved = (anomaly: any) => {
  addLog('info', `异常已解决: ${anomaly.message}`)
}

// 日志管理
const addLog = (level: 'info' | 'warning' | 'error', message: string, details?: any) => {
  const log: TrainingLog = {
    id: Date.now().toString(),
    timestamp: new Date().toISOString(),
    level,
    message,
    details
  }
  
  trainingLogs.value.push(log)
  
  // 限制日志数量
  if (trainingLogs.value.length > 1000) {
    trainingLogs.value = trainingLogs.value.slice(-500)
  }
  
  // 自动滚动到底部
  if (autoScroll.value) {
    nextTick(() => {
      scrollToBottom()
    })
  }
}

const clearLogs = () => {
  trainingLogs.value = []
  ElMessage.success('日志已清空')
}

const downloadLogs = () => {
  const logsText = trainingLogs.value
    .map(log => `[${formatLogTime(log.timestamp)}] ${log.level.toUpperCase()}: ${log.message}`)
    .join('\n')
  
  const blob = new Blob([logsText], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `training-logs-${Date.now()}.txt`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
  
  ElMessage.success('日志已下载')
}

const toggleAutoScroll = () => {
  autoScroll.value = !autoScroll.value
  if (autoScroll.value) {
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  if (logsContainer.value) {
    logsContainer.value.scrollTop = logsContainer.value.scrollHeight
  }
}

const formatLogTime = (timestamp: string) => {
  return new Date(timestamp).toLocaleTimeString()
}

const showLogDetails = (log: TrainingLog) => {
  ElMessageBox.alert(
    JSON.stringify(log.details, null, 2),
    '日志详情',
    {
      confirmButtonText: '确定'
    }
  )
}

// 验证步骤
const validateStep = (): StepValidation => {
  const validation: StepValidation = {
    isValid: true,
    errors: [],
    warnings: []
  }
  
  if (trainingStatus.value === 'training') {
    validation.warnings.push('训练正在进行中')
  }
  
  if (trainingStatus.value === 'failed') {
    validation.errors.push('训练失败，请重新开始')
    validation.isValid = false
  }
  
  return validation
}

// 模拟训练进度更新
let progressTimer: NodeJS.Timeout | null = null

const startProgressSimulation = () => {
  if (progressTimer) return
  
  progressTimer = setInterval(() => {
    if (trainingStatus.value === 'training') {
      // 随机生成日志
      if (Math.random() > 0.8) {
        const messages = [
          '正在处理字符样本',
          '模型训练中',
          '生成字符图像',
          '质量检查通过',
          '保存训练检查点'
        ]
        const message = messages[Math.floor(Math.random() * messages.length)]
        addLog('info', message)
      }
      
      // 模拟训练完成
      if (Math.random() > 0.98) {
        trainingStatus.value = 'completed'
        addLog('info', '训练完成！')
        ElMessage.success('字体包训练完成')
        stopProgressSimulation()
      }
    }
  }, 3000)
}

const stopProgressSimulation = () => {
  if (progressTimer) {
    clearInterval(progressTimer)
    progressTimer = null
  }
}

// 监听器
watch(trainingStatus, (newStatus) => {
  if (newStatus === 'training') {
    startProgressSimulation()
  } else {
    stopProgressSimulation()
  }
  
  // 发射验证事件
  emit('validate', validateStep())
})

// 生命周期
onMounted(() => {
  // 初始验证
  emit('validate', validateStep())
  
  // 添加初始日志
  addLog('info', '进入训练监控页面')
})

onUnmounted(() => {
  stopProgressSimulation()
})
</script>

<style scoped lang="scss">
.wizard-step-progress {
  .step-header {
    margin-bottom: 24px;
    text-align: center;

    h3 {
      margin: 0 0 8px 0;
      color: #303133;
      font-size: 20px;
      font-weight: 600;
    }

    .step-description {
      margin: 0;
      color: #606266;
      font-size: 14px;
    }
  }

  .progress-content {
    .training-status-card {
      margin-bottom: 24px;
      padding: 20px;
      background: #fff;
      border: 1px solid #e4e7ed;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

      .status-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;

        .status-info {
          h4 {
            margin: 0 0 12px 0;
            color: #303133;
            font-size: 18px;
            font-weight: 600;
          }

          .status-badges {
            display: flex;
            gap: 8px;
            align-items: center;
          }
        }

        .action-buttons {
          display: flex;
          gap: 8px;
        }
      }
    }

    .progress-monitor-container {
      margin-bottom: 24px;
    }

    .training-logs {
      border: 1px solid #e4e7ed;
      border-radius: 8px;
      overflow: hidden;

      .logs-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 16px;
        background: #f5f7fa;
        border-bottom: 1px solid #e4e7ed;

        h4 {
          margin: 0;
          color: #303133;
          font-size: 14px;
          font-weight: 600;
        }

        .logs-actions {
          display: flex;
          gap: 8px;
        }
      }

      .logs-content {
        height: 300px;
        overflow-y: auto;
        background: #fafafa;

        .log-entry {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 8px 16px;
          border-bottom: 1px solid #f0f0f0;
          font-family: 'Courier New', monospace;
          font-size: 12px;

          &:hover {
            background: #f0f0f0;
          }

          .log-time {
            color: #909399;
            min-width: 80px;
          }

          .log-level {
            min-width: 60px;
            font-weight: 600;
          }

          .log-message {
            flex: 1;
            color: #303133;
          }

          &.log-info .log-level {
            color: #409eff;
          }

          &.log-warning .log-level {
            color: #e6a23c;
          }

          &.log-error .log-level {
            color: #f56c6c;
          }
        }

        .no-logs {
          display: flex;
          justify-content: center;
          align-items: center;
          height: 100%;
          color: #909399;
          font-size: 14px;
        }
      }
    }
  }

  .step-navigation {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 32px;
    padding-top: 20px;
    border-top: 1px solid #e4e7ed;

    .nav-center {
      display: flex;
      gap: 12px;
    }
  }
}
</style>