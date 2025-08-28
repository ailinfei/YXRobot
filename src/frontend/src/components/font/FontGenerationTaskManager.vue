<template>
  <div class="font-generation-task-manager">
    <!-- 任务概览 -->
    <div class="task-overview">
      <div class="overview-cards">
        <div class="overview-card running">
          <div class="card-icon">
            <el-icon><Loading /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ runningTasks.length }}</div>
            <div class="card-label">正在生成</div>
          </div>
        </div>
        
        <div class="overview-card pending">
          <div class="card-icon">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ pendingTasks.length }}</div>
            <div class="card-label">等待中</div>
          </div>
        </div>
        
        <div class="overview-card completed">
          <div class="card-icon">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ completedTasks.length }}</div>
            <div class="card-label">已完成</div>
          </div>
        </div>
        
        <div class="overview-card failed">
          <div class="card-icon">
            <el-icon><CircleClose /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ failedTasks.length }}</div>
            <div class="card-label">失败</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 任务列表 -->
    <div class="task-list">
      <div class="list-header">
        <h3>生成任务列表</h3>
        <div class="header-actions">
          <el-select v-model="statusFilter" placeholder="状态筛选" @change="handleFilter">
            <el-option label="全部状态" value="" />
            <el-option label="等待中" value="pending" />
            <el-option label="正在生成" value="running" />
            <el-option label="已完成" value="completed" />
            <el-option label="失败" value="failed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
          <el-button @click="refreshTasks" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>

      <div class="task-items" v-loading="loading">
        <div
          v-for="task in filteredTasks"
          :key="task.id"
          class="task-item"
          :class="task.status"
        >
          <div class="task-header">
            <div class="task-info">
              <div class="task-title">
                <h4>{{ task.fontPackageName }}</h4>
                <el-tag :type="getStatusTagType(task.status)" size="small">
                  {{ getStatusText(task.status) }}
                </el-tag>
              </div>
              <div class="task-meta">
                <span>任务ID: {{ task.id }}</span>
                <span>创建时间: {{ formatDateTime(task.createdAt) }}</span>
                <span v-if="task.startedAt">开始时间: {{ formatDateTime(task.startedAt) }}</span>
              </div>
            </div>
            
            <div class="task-actions">
              <el-button 
                v-if="task.status === 'running'" 
                size="small" 
                type="warning"
                @click="pauseTask(task.id)"
              >
                暂停
              </el-button>
              <el-button 
                v-if="task.status === 'paused'" 
                size="small" 
                type="primary"
                @click="resumeTask(task.id)"
              >
                继续
              </el-button>
              <el-button 
                v-if="['pending', 'running', 'paused'].includes(task.status)" 
                size="small" 
                type="danger"
                @click="cancelTask(task.id)"
              >
                取消
              </el-button>
              <el-button 
                v-if="task.status === 'failed'" 
                size="small" 
                type="primary"
                @click="retryTask(task.id)"
              >
                重试
              </el-button>
              <el-button 
                size="small" 
                @click="viewTaskDetail(task)"
              >
                详情
              </el-button>
            </div>
          </div>

          <div class="task-content">
            <!-- 进度信息 -->
            <div class="progress-section">
              <div class="progress-info">
                <span>进度: {{ task.progress }}%</span>
                <span>已生成: {{ task.completedCount }}/{{ task.totalCount }}</span>
                <span v-if="task.status === 'running'">
                  预计剩余: {{ getEstimatedTime(task) }}
                </span>
              </div>
              <el-progress
                :percentage="task.progress"
                :stroke-width="8"
                :show-text="false"
                :color="getProgressColor(task.status)"
              />
            </div>

            <!-- 任务详情 -->
            <div class="task-details">
              <div class="detail-item">
                <label>字体类型:</label>
                <span>{{ task.fontType }}</span>
              </div>
              <div class="detail-item">
                <label>样本数量:</label>
                <span>{{ task.sampleCount }}个</span>
              </div>
              <div class="detail-item">
                <label>目标字符:</label>
                <span>{{ task.targetCharacterCount }}个</span>
              </div>
              <div class="detail-item" v-if="task.errorMessage">
                <label>错误信息:</label>
                <span class="error-message">{{ task.errorMessage }}</span>
              </div>
            </div>

            <!-- 实时日志 -->
            <div v-if="task.status === 'running' && task.realtimeLogs" class="realtime-logs">
              <div class="logs-header">
                <span>实时日志</span>
                <el-button size="small" text @click="toggleLogs(task.id)">
                  {{ expandedLogs.includes(task.id) ? '收起' : '展开' }}
                </el-button>
              </div>
              <div v-if="expandedLogs.includes(task.id)" class="logs-content">
                <div
                  v-for="log in task.realtimeLogs.slice(-10)"
                  :key="log.timestamp"
                  class="log-item"
                  :class="log.level"
                >
                  <span class="log-time">{{ formatTime(log.timestamp) }}</span>
                  <span class="log-message">{{ log.message }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-if="filteredTasks.length === 0" description="暂无生成任务" />
      </div>
    </div>

    <!-- 任务详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`任务详情 - ${currentTask?.fontPackageName}`"
      width="800px"
    >
      <div v-if="currentTask" class="task-detail-content">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="任务ID">
                {{ currentTask.id }}
              </el-descriptions-item>
              <el-descriptions-item label="字体包名称">
                {{ currentTask.fontPackageName }}
              </el-descriptions-item>
              <el-descriptions-item label="字体类型">
                {{ currentTask.fontType }}
              </el-descriptions-item>
              <el-descriptions-item label="任务状态">
                <el-tag :type="getStatusTagType(currentTask.status)">
                  {{ getStatusText(currentTask.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">
                {{ formatDateTime(currentTask.createdAt) }}
              </el-descriptions-item>
              <el-descriptions-item label="开始时间">
                {{ currentTask.startedAt ? formatDateTime(currentTask.startedAt) : '未开始' }}
              </el-descriptions-item>
              <el-descriptions-item label="完成时间">
                {{ currentTask.completedAt ? formatDateTime(currentTask.completedAt) : '未完成' }}
              </el-descriptions-item>
              <el-descriptions-item label="耗时">
                {{ getTaskDuration(currentTask) }}
              </el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>

          <el-tab-pane label="生成统计" name="stats">
            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-label">总字符数</div>
                <div class="stat-value">{{ currentTask.totalCount }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">已完成</div>
                <div class="stat-value">{{ currentTask.completedCount }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">成功率</div>
                <div class="stat-value">{{ getSuccessRate(currentTask) }}%</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">平均耗时</div>
                <div class="stat-value">{{ getAverageTime(currentTask) }}s</div>
              </div>
            </div>

            <div class="progress-chart">
              <h4>生成进度图表</h4>
              <div ref="progressChartRef" style="height: 300px;"></div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="错误日志" name="errors">
            <FontGenerationErrorLog :task-id="currentTask.id" />
          </el-tab-pane>

          <el-tab-pane label="生成日志" name="logs">
            <div class="generation-logs">
              <div class="logs-toolbar">
                <el-select v-model="logLevel" placeholder="日志级别" size="small">
                  <el-option label="全部" value="" />
                  <el-option label="信息" value="info" />
                  <el-option label="警告" value="warning" />
                  <el-option label="错误" value="error" />
                </el-select>
                <el-button size="small" @click="exportLogs">导出日志</el-button>
                <el-button size="small" @click="clearLogs">清空日志</el-button>
              </div>
              
              <div class="logs-container">
                <div
                  v-for="log in filteredLogs"
                  :key="log.id"
                  class="log-entry"
                  :class="log.level"
                >
                  <div class="log-header">
                    <span class="log-time">{{ formatDateTime(log.timestamp) }}</span>
                    <el-tag :type="getLogLevelType(log.level)" size="small">
                      {{ log.level.toUpperCase() }}
                    </el-tag>
                  </div>
                  <div class="log-content">{{ log.message }}</div>
                  <div v-if="log.details" class="log-details">
                    <pre>{{ JSON.stringify(log.details, null, 2) }}</pre>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Loading,
  Clock,
  CircleCheck,
  CircleClose,
  Refresh
} from '@element-plus/icons-vue'
import FontGenerationErrorLog from './FontGenerationErrorLog.vue'
import * as echarts from 'echarts'

// 接口定义
interface GenerationTask {
  id: string
  fontPackageId: string
  fontPackageName: string
  fontType: string
  status: 'pending' | 'running' | 'paused' | 'completed' | 'failed' | 'cancelled'
  progress: number
  totalCount: number
  completedCount: number
  sampleCount: number
  targetCharacterCount: number
  createdAt: string
  startedAt?: string
  completedAt?: string
  errorMessage?: string
  realtimeLogs?: LogEntry[]
  generationStats?: {
    successCount: number
    failureCount: number
    averageTime: number
    totalTime: number
  }
}

interface LogEntry {
  id: string
  timestamp: string
  level: 'info' | 'warning' | 'error'
  message: string
  details?: any
}

// 响应式数据
const loading = ref(false)
const statusFilter = ref('')
const expandedLogs = ref<string[]>([])

// 任务数据
const tasks = ref<GenerationTask[]>([])

// 对话框状态
const detailDialogVisible = ref(false)
const currentTask = ref<GenerationTask | null>(null)
const activeTab = ref('basic')
const logLevel = ref('')

// 图表引用
const progressChartRef = ref<HTMLElement>()
let progressChart: echarts.ECharts | null = null

// WebSocket连接
let ws: WebSocket | null = null

// 计算属性
const runningTasks = computed(() => tasks.value.filter(task => task.status === 'running'))
const pendingTasks = computed(() => tasks.value.filter(task => task.status === 'pending'))
const completedTasks = computed(() => tasks.value.filter(task => task.status === 'completed'))
const failedTasks = computed(() => tasks.value.filter(task => task.status === 'failed'))

const filteredTasks = computed(() => {
  if (!statusFilter.value) return tasks.value
  return tasks.value.filter(task => task.status === statusFilter.value)
})

const filteredLogs = computed(() => {
  if (!currentTask.value?.realtimeLogs) return []
  if (!logLevel.value) return currentTask.value.realtimeLogs
  return currentTask.value.realtimeLogs.filter(log => log.level === logLevel.value)
})

// 方法
const loadTasks = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟任务数据
    tasks.value = [
      {
        id: 'task-001',
        fontPackageId: 'font-001',
        fontPackageName: '楷书字体包',
        fontType: 'kaishu',
        status: 'running',
        progress: 65,
        totalCount: 3500,
        completedCount: 2275,
        sampleCount: 150,
        targetCharacterCount: 3500,
        createdAt: '2024-01-20 10:00:00',
        startedAt: '2024-01-20 10:05:00',
        realtimeLogs: [
          {
            id: 'log-001',
            timestamp: '2024-01-20 14:30:00',
            level: 'info',
            message: '正在生成字符: 中'
          },
          {
            id: 'log-002',
            timestamp: '2024-01-20 14:30:05',
            level: 'info',
            message: '字符生成完成: 中, 耗时: 2.3s'
          }
        ],
        generationStats: {
          successCount: 2275,
          failureCount: 25,
          averageTime: 1.8,
          totalTime: 4095
        }
      },
      {
        id: 'task-002',
        fontPackageId: 'font-002',
        fontPackageName: '行书字体包',
        fontType: 'xingshu',
        status: 'pending',
        progress: 0,
        totalCount: 2800,
        completedCount: 0,
        sampleCount: 120,
        targetCharacterCount: 2800,
        createdAt: '2024-01-20 11:00:00'
      },
      {
        id: 'task-003',
        fontPackageId: 'font-003',
        fontPackageName: '隶书字体包',
        fontType: 'lishu',
        status: 'completed',
        progress: 100,
        totalCount: 3000,
        completedCount: 3000,
        sampleCount: 180,
        targetCharacterCount: 3000,
        createdAt: '2024-01-19 09:00:00',
        startedAt: '2024-01-19 09:05:00',
        completedAt: '2024-01-19 15:30:00',
        generationStats: {
          successCount: 2985,
          failureCount: 15,
          averageTime: 2.1,
          totalTime: 6300
        }
      },
      {
        id: 'task-004',
        fontPackageId: 'font-004',
        fontPackageName: '草书字体包',
        fontType: 'caoshu',
        status: 'failed',
        progress: 35,
        totalCount: 2500,
        completedCount: 875,
        sampleCount: 100,
        targetCharacterCount: 2500,
        createdAt: '2024-01-18 14:00:00',
        startedAt: '2024-01-18 14:05:00',
        errorMessage: '样本质量不足，无法继续生成',
        generationStats: {
          successCount: 850,
          failureCount: 25,
          averageTime: 2.5,
          totalTime: 2187
        }
      }
    ]
  } catch (error) {
    ElMessage.error('加载任务列表失败')
  } finally {
    loading.value = false
  }
}

const refreshTasks = () => {
  loadTasks()
}

const handleFilter = () => {
  // 筛选逻辑已在计算属性中处理
}

const pauseTask = async (taskId: string) => {
  try {
    await ElMessageBox.confirm('确定要暂停这个生成任务吗？', '确认暂停')
    
    // 模拟API调用
    const task = tasks.value.find(t => t.id === taskId)
    if (task) {
      task.status = 'paused'
      ElMessage.success('任务已暂停')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('暂停任务失败')
    }
  }
}

const resumeTask = async (taskId: string) => {
  try {
    // 模拟API调用
    const task = tasks.value.find(t => t.id === taskId)
    if (task) {
      task.status = 'running'
      ElMessage.success('任务已继续')
    }
  } catch (error) {
    ElMessage.error('继续任务失败')
  }
}

const cancelTask = async (taskId: string) => {
  try {
    await ElMessageBox.confirm('确定要取消这个生成任务吗？取消后无法恢复。', '确认取消', {
      type: 'warning'
    })
    
    // 模拟API调用
    const task = tasks.value.find(t => t.id === taskId)
    if (task) {
      task.status = 'cancelled'
      ElMessage.success('任务已取消')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消任务失败')
    }
  }
}

const retryTask = async (taskId: string) => {
  try {
    await ElMessageBox.confirm('确定要重试这个生成任务吗？', '确认重试')
    
    // 模拟API调用
    const task = tasks.value.find(t => t.id === taskId)
    if (task) {
      task.status = 'pending'
      task.progress = 0
      task.completedCount = 0
      task.errorMessage = undefined
      ElMessage.success('任务已重新提交')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重试任务失败')
    }
  }
}

const viewTaskDetail = (task: GenerationTask) => {
  currentTask.value = task
  detailDialogVisible.value = true
  activeTab.value = 'basic'
  
  // 初始化进度图表
  setTimeout(() => {
    initProgressChart()
  }, 100)
}

const toggleLogs = (taskId: string) => {
  const index = expandedLogs.value.indexOf(taskId)
  if (index > -1) {
    expandedLogs.value.splice(index, 1)
  } else {
    expandedLogs.value.push(taskId)
  }
}

const exportLogs = () => {
  ElMessage.info('导出日志功能开发中...')
}

const clearLogs = async () => {
  try {
    await ElMessageBox.confirm('确定要清空日志吗？', '确认清空')
    ElMessage.success('日志已清空')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空日志失败')
    }
  }
}

// 工具方法
const getStatusTagType = (status: string) => {
  const typeMap = {
    pending: 'info',
    running: 'warning',
    paused: 'primary',
    completed: 'success',
    failed: 'danger',
    cancelled: 'info'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

const getStatusText = (status: string) => {
  const textMap = {
    pending: '等待中',
    running: '正在生成',
    paused: '已暂停',
    completed: '已完成',
    failed: '失败',
    cancelled: '已取消'
  }
  return textMap[status as keyof typeof textMap] || status
}

const getProgressColor = (status: string) => {
  const colorMap = {
    pending: '#909399',
    running: '#409EFF',
    paused: '#E6A23C',
    completed: '#67C23A',
    failed: '#F56C6C',
    cancelled: '#909399'
  }
  return colorMap[status as keyof typeof colorMap] || '#409EFF'
}

const getLogLevelType = (level: string) => {
  const typeMap = {
    info: 'primary',
    warning: 'warning',
    error: 'danger'
  }
  return typeMap[level as keyof typeof typeMap] || 'primary'
}

const getEstimatedTime = (task: GenerationTask) => {
  if (task.status !== 'running' || !task.generationStats) return '未知'
  
  const remaining = task.totalCount - task.completedCount
  const avgTime = task.generationStats.averageTime
  const totalSeconds = remaining * avgTime
  
  const hours = Math.floor(totalSeconds / 3600)
  const minutes = Math.floor((totalSeconds % 3600) / 60)
  
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  } else {
    return `${minutes}分钟`
  }
}

const getTaskDuration = (task: GenerationTask) => {
  if (!task.startedAt) return '未开始'
  
  const start = new Date(task.startedAt).getTime()
  const end = task.completedAt ? new Date(task.completedAt).getTime() : Date.now()
  const duration = Math.floor((end - start) / 1000)
  
  const hours = Math.floor(duration / 3600)
  const minutes = Math.floor((duration % 3600) / 60)
  const seconds = duration % 60
  
  if (hours > 0) {
    return `${hours}小时${minutes}分钟${seconds}秒`
  } else if (minutes > 0) {
    return `${minutes}分钟${seconds}秒`
  } else {
    return `${seconds}秒`
  }
}

const getSuccessRate = (task: GenerationTask) => {
  if (!task.generationStats) return 0
  const total = task.generationStats.successCount + task.generationStats.failureCount
  if (total === 0) return 0
  return Math.round((task.generationStats.successCount / total) * 100)
}

const getAverageTime = (task: GenerationTask) => {
  return task.generationStats?.averageTime?.toFixed(1) || '0'
}

const formatDateTime = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

const formatTime = (dateStr: string) => {
  return new Date(dateStr).toLocaleTimeString('zh-CN')
}

// 图表初始化
const initProgressChart = () => {
  if (!progressChartRef.value || !currentTask.value) return
  
  progressChart = echarts.init(progressChartRef.value)
  
  // 模拟进度数据
  const hours = []
  const progressData = []
  const now = new Date()
  
  for (let i = 23; i >= 0; i--) {
    const time = new Date(now.getTime() - i * 60 * 60 * 1000)
    hours.push(time.getHours() + ':00')
    
    // 模拟进度增长
    const baseProgress = Math.max(0, currentTask.value.progress - (i * 2.5))
    progressData.push(Math.min(100, baseProgress + Math.random() * 5))
  }
  
  const option = {
    title: {
      text: '24小时生成进度',
      left: 'center',
      textStyle: {
        fontSize: 14
      }
    },
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}%'
    },
    xAxis: {
      type: 'category',
      data: hours,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '进度',
        type: 'line',
        data: progressData,
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        },
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
  
  progressChart.setOption(option)
}

// WebSocket连接
const connectWebSocket = () => {
  if (ws) return
  
  try {
    ws = new WebSocket('ws://localhost:8080/ws/font-generation')
    
    ws.onopen = () => {
      console.log('WebSocket连接已建立')
    }
    
    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        handleWebSocketMessage(data)
      } catch (error) {
        console.error('解析WebSocket消息失败:', error)
      }
    }
    
    ws.onclose = () => {
      console.log('WebSocket连接已关闭')
      ws = null
      // 5秒后重连
      setTimeout(connectWebSocket, 5000)
    }
    
    ws.onerror = (error) => {
      console.error('WebSocket连接错误:', error)
    }
  } catch (error) {
    console.error('创建WebSocket连接失败:', error)
  }
}

const handleWebSocketMessage = (data: any) => {
  const { type, taskId, payload } = data
  
  switch (type) {
    case 'task_progress':
      updateTaskProgress(taskId, payload)
      break
    case 'task_status':
      updateTaskStatus(taskId, payload.status)
      break
    case 'task_log':
      addTaskLog(taskId, payload)
      break
    case 'task_error':
      handleTaskError(taskId, payload)
      break
  }
}

const updateTaskProgress = (taskId: string, payload: any) => {
  const task = tasks.value.find(t => t.id === taskId)
  if (task) {
    task.progress = payload.progress
    task.completedCount = payload.completedCount
    
    if (task.generationStats) {
      Object.assign(task.generationStats, payload.stats)
    }
  }
}

const updateTaskStatus = (taskId: string, status: string) => {
  const task = tasks.value.find(t => t.id === taskId)
  if (task) {
    task.status = status as any
    
    if (status === 'running' && !task.startedAt) {
      task.startedAt = new Date().toISOString()
    } else if (['completed', 'failed', 'cancelled'].includes(status)) {
      task.completedAt = new Date().toISOString()
    }
  }
}

const addTaskLog = (taskId: string, logEntry: LogEntry) => {
  const task = tasks.value.find(t => t.id === taskId)
  if (task) {
    if (!task.realtimeLogs) {
      task.realtimeLogs = []
    }
    task.realtimeLogs.push(logEntry)
    
    // 保持最新的100条日志
    if (task.realtimeLogs.length > 100) {
      task.realtimeLogs = task.realtimeLogs.slice(-100)
    }
  }
}

const handleTaskError = (taskId: string, error: any) => {
  const task = tasks.value.find(t => t.id === taskId)
  if (task) {
    task.status = 'failed'
    task.errorMessage = error.message
    task.completedAt = new Date().toISOString()
  }
  
  ElMessage.error(`任务 ${taskId} 生成失败: ${error.message}`)
}

// 生命周期
onMounted(() => {
  loadTasks()
  connectWebSocket()
})

onUnmounted(() => {
  if (ws) {
    ws.close()
    ws = null
  }
  
  if (progressChart) {
    progressChart.dispose()
  }
})
</script>

<style lang="scss" scoped>
.font-generation-task-manager {
  .task-overview {
    margin-bottom: 24px;

    .overview-cards {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;

      .overview-card {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 20px;
        background: white;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

        .card-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;
          color: white;

          &.running .card-icon {
            background: linear-gradient(135deg, #E6A23C, #EEBE77);
          }

          &.pending .card-icon {
            background: linear-gradient(135deg, #909399, #B1B3B8);
          }

          &.completed .card-icon {
            background: linear-gradient(135deg, #67C23A, #85CE61);
          }

          &.failed .card-icon {
            background: linear-gradient(135deg, #F56C6C, #F78989);
          }
        }

        .card-content {
          .card-value {
            font-size: 24px;
            font-weight: 700;
            color: #303133;
            line-height: 1.2;
          }

          .card-label {
            font-size: 12px;
            color: #909399;
            margin-top: 4px;
          }
        }

        &.running .card-icon {
          background: linear-gradient(135deg, #E6A23C, #EEBE77);
        }

        &.pending .card-icon {
          background: linear-gradient(135deg, #909399, #B1B3B8);
        }

        &.completed .card-icon {
          background: linear-gradient(135deg, #67C23A, #85CE61);
        }

        &.failed .card-icon {
          background: linear-gradient(135deg, #F56C6C, #F78989);
        }
      }
    }
  }

  .task-list {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .list-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      border-bottom: 1px solid #f0f0f0;

      h3 {
        margin: 0;
        color: #303133;
        font-size: 16px;
        font-weight: 600;
      }

      .header-actions {
        display: flex;
        gap: 12px;
        align-items: center;
      }
    }

    .task-items {
      .task-item {
        padding: 20px;
        border-bottom: 1px solid #f0f0f0;
        transition: all 0.3s ease;

        &:hover {
          background: #f8f9fa;
        }

        &:last-child {
          border-bottom: none;
        }

        &.running {
          border-left: 4px solid #E6A23C;
        }

        &.completed {
          border-left: 4px solid #67C23A;
        }

        &.failed {
          border-left: 4px solid #F56C6C;
        }

        .task-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 16px;

          .task-info {
            flex: 1;

            .task-title {
              display: flex;
              align-items: center;
              gap: 12px;
              margin-bottom: 8px;

              h4 {
                margin: 0;
                color: #303133;
                font-size: 16px;
                font-weight: 600;
              }
            }

            .task-meta {
              display: flex;
              gap: 16px;
              font-size: 12px;
              color: #909399;

              span {
                display: flex;
                align-items: center;
              }
            }
          }

          .task-actions {
            display: flex;
            gap: 8px;
          }
        }

        .task-content {
          .progress-section {
            margin-bottom: 16px;

            .progress-info {
              display: flex;
              justify-content: space-between;
              align-items: center;
              margin-bottom: 8px;
              font-size: 12px;
              color: #606266;
            }
          }

          .task-details {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 12px;
            margin-bottom: 16px;

            .detail-item {
              display: flex;
              gap: 8px;
              font-size: 12px;

              label {
                color: #909399;
                min-width: 60px;
              }

              span {
                color: #606266;

                &.error-message {
                  color: #F56C6C;
                }
              }
            }
          }

          .realtime-logs {
            .logs-header {
              display: flex;
              justify-content: space-between;
              align-items: center;
              margin-bottom: 8px;
              font-size: 12px;
              color: #606266;
            }

            .logs-content {
              background: #f8f9fa;
              border-radius: 4px;
              padding: 8px;
              max-height: 200px;
              overflow-y: auto;

              .log-item {
                display: flex;
                gap: 8px;
                margin-bottom: 4px;
                font-size: 11px;

                .log-time {
                  color: #909399;
                  min-width: 60px;
                }

                .log-message {
                  color: #606266;
                }

                &.error {
                  .log-message {
                    color: #F56C6C;
                  }
                }

                &.warning {
                  .log-message {
                    color: #E6A23C;
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  .task-detail-content {
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
      margin-bottom: 20px;

      .stat-item {
        text-align: center;
        padding: 16px;
        background: #f8f9fa;
        border-radius: 8px;

        .stat-label {
          font-size: 12px;
          color: #909399;
          margin-bottom: 8px;
        }

        .stat-value {
          font-size: 20px;
          font-weight: 600;
          color: #303133;
        }
      }
    }

    .progress-chart {
      h4 {
        margin: 0 0 16px 0;
        color: #303133;
        font-size: 14px;
        font-weight: 600;
      }
    }

    .generation-logs {
      .logs-toolbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
      }

      .logs-container {
        max-height: 400px;
        overflow-y: auto;
        border: 1px solid #e4e7ed;
        border-radius: 4px;

        .log-entry {
          padding: 12px;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .log-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;

            .log-time {
              font-size: 12px;
              color: #909399;
            }
          }

          .log-content {
            font-size: 13px;
            color: #606266;
            line-height: 1.5;
          }

          .log-details {
            margin-top: 8px;
            padding: 8px;
            background: #f8f9fa;
            border-radius: 4px;

            pre {
              margin: 0;
              font-size: 11px;
              color: #606266;
              white-space: pre-wrap;
            }
          }

          &.error {
            background: #fef0f0;
            border-left: 4px solid #F56C6C;
          }

          &.warning {
            background: #fdf6ec;
            border-left: 4px solid #E6A23C;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .font-generation-task-manager {
    .task-overview {
      .overview-cards {
        grid-template-columns: repeat(2, 1fr);
      }
    }

    .task-list {
      .list-header {
        flex-direction: column;
        gap: 12px;
        align-items: stretch;
      }

      .task-items {
        .task-item {
          .task-header {
            flex-direction: column;
            gap: 12px;
            align-items: stretch;
          }

          .task-content {
            .task-details {
              grid-template-columns: 1fr;
            }
          }
        }
      }
    }

    .task-detail-content {
      .stats-grid {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  }
}
</style>