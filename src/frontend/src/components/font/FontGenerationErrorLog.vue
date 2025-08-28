<template>
  <div class="font-generation-error-log">
    <!-- 错误统计 -->
    <div class="error-stats">
      <div class="stat-card total-errors">
        <div class="stat-icon">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ errorStats.totalErrors }}</div>
          <div class="stat-label">总错误数</div>
        </div>
      </div>
      
      <div class="stat-card critical-errors">
        <div class="stat-icon">
          <el-icon><CircleClose /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ errorStats.criticalErrors }}</div>
          <div class="stat-label">严重错误</div>
        </div>
      </div>
      
      <div class="stat-card resolved-errors">
        <div class="stat-icon">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ errorStats.resolvedErrors }}</div>
          <div class="stat-label">已解决</div>
        </div>
      </div>
      
      <div class="stat-card error-rate">
        <div class="stat-icon">
          <el-icon><DataLine /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ errorStats.errorRate.toFixed(1) }}%</div>
          <div class="stat-label">错误率</div>
        </div>
      </div>
    </div>

    <!-- 错误筛选 -->
    <div class="error-filters">
      <div class="filter-controls">
        <el-select v-model="severityFilter" placeholder="错误级别" @change="handleFilter">
          <el-option label="全部级别" value="" />
          <el-option label="严重" value="critical" />
          <el-option label="错误" value="error" />
          <el-option label="警告" value="warning" />
          <el-option label="信息" value="info" />
        </el-select>
        
        <el-select v-model="categoryFilter" placeholder="错误类型" @change="handleFilter">
          <el-option label="全部类型" value="" />
          <el-option label="样本质量" value="sample_quality" />
          <el-option label="模型训练" value="model_training" />
          <el-option label="字符生成" value="character_generation" />
          <el-option label="文件处理" value="file_processing" />
          <el-option label="系统错误" value="system_error" />
        </el-select>
        
        <el-select v-model="statusFilter" placeholder="处理状态" @change="handleFilter">
          <el-option label="全部状态" value="" />
          <el-option label="未处理" value="unresolved" />
          <el-option label="处理中" value="investigating" />
          <el-option label="已解决" value="resolved" />
          <el-option label="已忽略" value="ignored" />
        </el-select>
        
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleFilter"
        />
      </div>
      
      <div class="filter-actions">
        <el-button @click="resetFilters" :icon="Refresh">重置</el-button>
        <el-button @click="exportErrors" :icon="Download">导出</el-button>
        <el-button @click="refreshErrors" :loading="loading" :icon="Refresh">刷新</el-button>
      </div>
    </div>

    <!-- 错误列表 -->
    <div class="error-list" v-loading="loading">
      <div
        v-for="error in filteredErrors"
        :key="error.id"
        class="error-item"
        :class="[error.severity, error.status]"
      >
        <div class="error-header">
          <div class="error-info">
            <div class="error-title">
              <div class="severity-badge" :class="error.severity">
                <el-icon v-if="error.severity === 'critical'"><CircleClose /></el-icon>
                <el-icon v-else-if="error.severity === 'error'"><Close /></el-icon>
                <el-icon v-else-if="error.severity === 'warning'"><Warning /></el-icon>
                <el-icon v-else><InfoFilled /></el-icon>
              </div>
              <h4>{{ error.title }}</h4>
              <el-tag :type="getCategoryTagType(error.category)" size="small">
                {{ getCategoryText(error.category) }}
              </el-tag>
            </div>
            <div class="error-meta">
              <span>错误ID: {{ error.id }}</span>
              <span>发生时间: {{ formatDateTime(error.timestamp) }}</span>
              <span v-if="error.characterId">字符: {{ error.characterId }}</span>
              <span v-if="error.retryCount > 0">重试次数: {{ error.retryCount }}</span>
            </div>
          </div>
          
          <div class="error-actions">
            <el-tag :type="getStatusTagType(error.status)" size="small">
              {{ getStatusText(error.status) }}
            </el-tag>
            <el-button size="small" @click="viewErrorDetail(error)">
              详情
            </el-button>
            <el-dropdown @command="(cmd) => handleErrorAction(cmd, error)">
              <el-button size="small" :icon="MoreFilled" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="retry" :disabled="!canRetry(error)">
                    重试
                  </el-dropdown-item>
                  <el-dropdown-item command="resolve">
                    标记已解决
                  </el-dropdown-item>
                  <el-dropdown-item command="ignore">
                    忽略
                  </el-dropdown-item>
                  <el-dropdown-item command="report" divided>
                    报告问题
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <div class="error-content">
          <div class="error-message">
            <p>{{ error.message }}</p>
          </div>
          
          <div v-if="error.context" class="error-context">
            <h5>错误上下文</h5>
            <div class="context-grid">
              <div v-for="(value, key) in error.context" :key="key" class="context-item">
                <label>{{ formatContextKey(key) }}:</label>
                <span>{{ formatContextValue(value) }}</span>
              </div>
            </div>
          </div>
          
          <div v-if="error.stackTrace" class="error-stack">
            <div class="stack-header">
              <h5>堆栈跟踪</h5>
              <el-button size="small" text @click="toggleStack(error.id)">
                {{ expandedStacks.includes(error.id) ? '收起' : '展开' }}
              </el-button>
            </div>
            <div v-if="expandedStacks.includes(error.id)" class="stack-content">
              <pre>{{ error.stackTrace }}</pre>
            </div>
          </div>
          
          <div v-if="error.suggestions && error.suggestions.length > 0" class="error-suggestions">
            <h5>解决建议</h5>
            <ul>
              <li v-for="suggestion in error.suggestions" :key="suggestion">
                {{ suggestion }}
              </li>
            </ul>
          </div>
          
          <div v-if="error.relatedErrors && error.relatedErrors.length > 0" class="related-errors">
            <h5>相关错误</h5>
            <div class="related-list">
              <el-link
                v-for="relatedId in error.relatedErrors"
                :key="relatedId"
                type="primary"
                @click="jumpToError(relatedId)"
              >
                {{ relatedId }}
              </el-link>
            </div>
          </div>
        </div>

        <div v-if="error.resolution" class="error-resolution">
          <div class="resolution-header">
            <h5>解决方案</h5>
            <span class="resolution-time">
              解决时间: {{ formatDateTime(error.resolution.timestamp) }}
            </span>
          </div>
          <div class="resolution-content">
            <p>{{ error.resolution.description }}</p>
            <div v-if="error.resolution.actions" class="resolution-actions">
              <h6>执行的操作:</h6>
              <ul>
                <li v-for="action in error.resolution.actions" :key="action">
                  {{ action }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="filteredErrors.length === 0" description="暂无错误记录" />
    </div>

    <!-- 错误详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`错误详情 - ${currentError?.id}`"
      width="800px"
    >
      <div v-if="currentError" class="error-detail-content">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="错误ID">
                {{ currentError.id }}
              </el-descriptions-item>
              <el-descriptions-item label="错误级别">
                <el-tag :type="getSeverityTagType(currentError.severity)">
                  {{ getSeverityText(currentError.severity) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="错误类型">
                <el-tag :type="getCategoryTagType(currentError.category)">
                  {{ getCategoryText(currentError.category) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="处理状态">
                <el-tag :type="getStatusTagType(currentError.status)">
                  {{ getStatusText(currentError.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="发生时间">
                {{ formatDateTime(currentError.timestamp) }}
              </el-descriptions-item>
              <el-descriptions-item label="字符ID">
                {{ currentError.characterId || '无' }}
              </el-descriptions-item>
              <el-descriptions-item label="重试次数">
                {{ currentError.retryCount }}
              </el-descriptions-item>
              <el-descriptions-item label="影响范围">
                {{ currentError.impact || '未知' }}
              </el-descriptions-item>
            </el-descriptions>
            
            <div class="error-message-section">
              <h4>错误消息</h4>
              <div class="message-content">
                {{ currentError.message }}
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="技术详情" name="technical">
            <div class="technical-details">
              <div v-if="currentError.context" class="context-section">
                <h4>错误上下文</h4>
                <el-table :data="formatContextForTable(currentError.context)" size="small">
                  <el-table-column prop="key" label="属性" width="150" />
                  <el-table-column prop="value" label="值" />
                </el-table>
              </div>
              
              <div v-if="currentError.stackTrace" class="stack-section">
                <h4>堆栈跟踪</h4>
                <div class="stack-trace">
                  <pre>{{ currentError.stackTrace }}</pre>
                </div>
              </div>
              
              <div v-if="currentError.systemInfo" class="system-section">
                <h4>系统信息</h4>
                <el-descriptions :column="2" border size="small">
                  <el-descriptions-item
                    v-for="(value, key) in currentError.systemInfo"
                    :key="key"
                    :label="formatContextKey(key)"
                  >
                    {{ value }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="解决方案" name="solution">
            <div class="solution-content">
              <div v-if="currentError.suggestions" class="suggestions-section">
                <h4>系统建议</h4>
                <ul class="suggestions-list">
                  <li v-for="suggestion in currentError.suggestions" :key="suggestion">
                    {{ suggestion }}
                  </li>
                </ul>
              </div>
              
              <div v-if="currentError.resolution" class="resolution-section">
                <h4>已执行的解决方案</h4>
                <div class="resolution-info">
                  <p><strong>解决时间:</strong> {{ formatDateTime(currentError.resolution.timestamp) }}</p>
                  <p><strong>解决描述:</strong> {{ currentError.resolution.description }}</p>
                  <div v-if="currentError.resolution.actions">
                    <p><strong>执行的操作:</strong></p>
                    <ul>
                      <li v-for="action in currentError.resolution.actions" :key="action">
                        {{ action }}
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
              
              <div class="manual-resolution">
                <h4>手动解决</h4>
                <el-form :model="resolutionForm" label-width="100px">
                  <el-form-item label="解决描述">
                    <el-input
                      v-model="resolutionForm.description"
                      type="textarea"
                      :rows="3"
                      placeholder="请描述解决方案"
                    />
                  </el-form-item>
                  <el-form-item label="执行操作">
                    <el-input
                      v-model="resolutionForm.actions"
                      type="textarea"
                      :rows="2"
                      placeholder="请描述执行的操作，每行一个"
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="resolveError">
                      标记为已解决
                    </el-button>
                    <el-button @click="ignoreError">
                      忽略此错误
                    </el-button>
                  </el-form-item>
                </el-form>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="相关信息" name="related">
            <div class="related-content">
              <div v-if="currentError.relatedErrors" class="related-errors-section">
                <h4>相关错误</h4>
                <div class="related-errors-list">
                  <div
                    v-for="relatedId in currentError.relatedErrors"
                    :key="relatedId"
                    class="related-error-item"
                  >
                    <el-link type="primary" @click="jumpToError(relatedId)">
                      {{ relatedId }}
                    </el-link>
                  </div>
                </div>
              </div>
              
              <div class="error-frequency">
                <h4>错误频率分析</h4>
                <div ref="frequencyChartRef" style="height: 300px;"></div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Warning,
  CircleClose,
  CircleCheck,
  DataLine,
  Close,
  InfoFilled,
  MoreFilled,
  Refresh,
  Download
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 接口定义
interface ErrorLog {
  id: string
  taskId: string
  title: string
  message: string
  severity: 'critical' | 'error' | 'warning' | 'info'
  category: 'sample_quality' | 'model_training' | 'character_generation' | 'file_processing' | 'system_error'
  status: 'unresolved' | 'investigating' | 'resolved' | 'ignored'
  timestamp: string
  characterId?: string
  retryCount: number
  impact?: string
  context?: Record<string, any>
  stackTrace?: string
  suggestions?: string[]
  relatedErrors?: string[]
  systemInfo?: Record<string, any>
  resolution?: {
    timestamp: string
    description: string
    actions?: string[]
  }
}

interface ErrorStats {
  totalErrors: number
  criticalErrors: number
  resolvedErrors: number
  errorRate: number
}

// Props
interface FontGenerationErrorLogProps {
  taskId?: string
}

const props = defineProps<FontGenerationErrorLogProps>()

// 响应式数据
const loading = ref(false)
const severityFilter = ref('')
const categoryFilter = ref('')
const statusFilter = ref('')
const dateRange = ref<[string, string] | null>(null)
const expandedStacks = ref<string[]>([])

// 错误数据
const errors = ref<ErrorLog[]>([])
const errorStats = ref<ErrorStats>({
  totalErrors: 0,
  criticalErrors: 0,
  resolvedErrors: 0,
  errorRate: 0
})

// 对话框状态
const detailDialogVisible = ref(false)
const currentError = ref<ErrorLog | null>(null)
const activeTab = ref('basic')

// 解决方案表单
const resolutionForm = reactive({
  description: '',
  actions: ''
})

// 图表引用
const frequencyChartRef = ref<HTMLElement>()

// 计算属性
const filteredErrors = computed(() => {
  let result = [...errors.value]
  
  if (severityFilter.value) {
    result = result.filter(error => error.severity === severityFilter.value)
  }
  
  if (categoryFilter.value) {
    result = result.filter(error => error.category === categoryFilter.value)
  }
  
  if (statusFilter.value) {
    result = result.filter(error => error.status === statusFilter.value)
  }
  
  if (dateRange.value) {
    const [start, end] = dateRange.value
    result = result.filter(error => {
      const errorTime = new Date(error.timestamp).getTime()
      return errorTime >= new Date(start).getTime() && errorTime <= new Date(end).getTime()
    })
  }
  
  return result.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())
})

// 方法
const loadErrors = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟错误数据
    errors.value = [
      {
        id: 'ERR-001',
        taskId: props.taskId || 'task-001',
        title: '样本质量不足',
        message: '字符"中"的样本图片质量过低，无法进行有效的特征提取',
        severity: 'critical',
        category: 'sample_quality',
        status: 'unresolved',
        timestamp: '2024-01-20 14:30:15',
        characterId: '中',
        retryCount: 3,
        impact: '阻塞字符生成',
        context: {
          imageQuality: 0.3,
          minRequiredQuality: 0.7,
          sampleCount: 5,
          validSamples: 1
        },
        suggestions: [
          '重新上传高质量的样本图片',
          '增加样本数量到至少10个',
          '检查图片分辨率是否符合要求'
        ],
        relatedErrors: ['ERR-002', 'ERR-003']
      },
      {
        id: 'ERR-002',
        taskId: props.taskId || 'task-001',
        title: '模型训练失败',
        message: '神经网络模型在训练过程中出现梯度爆炸，导致训练失败',
        severity: 'error',
        category: 'model_training',
        status: 'investigating',
        timestamp: '2024-01-20 14:25:30',
        retryCount: 1,
        impact: '影响整体生成进度',
        context: {
          epoch: 45,
          loss: 'NaN',
          learningRate: 0.001,
          batchSize: 32
        },
        stackTrace: `Traceback (most recent call last):
  File "model_trainer.py", line 123, in train_model
    loss = criterion(outputs, targets)
  File "torch/nn/modules/loss.py", line 1174, in forward
    return F.cross_entropy(input, target, weight=self.weight,
RuntimeError: CUDA out of memory`,
        suggestions: [
          '降低学习率到0.0001',
          '减少批次大小到16',
          '增加GPU内存或使用CPU训练'
        ],
        systemInfo: {
          gpuMemory: '8GB',
          usedMemory: '7.8GB',
          cudaVersion: '11.8',
          pytorchVersion: '2.0.1'
        }
      },
      {
        id: 'ERR-003',
        taskId: props.taskId || 'task-001',
        title: '字符生成超时',
        message: '字符"书"的生成过程超过了预设的超时时间（300秒）',
        severity: 'warning',
        category: 'character_generation',
        status: 'resolved',
        timestamp: '2024-01-20 14:20:45',
        characterId: '书',
        retryCount: 2,
        context: {
          timeout: 300,
          actualTime: 450,
          complexity: 'high',
          strokeCount: 10
        },
        suggestions: [
          '增加超时时间到600秒',
          '优化生成算法',
          '分解复杂字符为多个步骤'
        ],
        resolution: {
          timestamp: '2024-01-20 14:35:00',
          description: '增加了超时时间并优化了生成算法',
          actions: [
            '将超时时间调整为600秒',
            '启用了增量生成模式',
            '添加了进度检查点'
          ]
        }
      }
    ]
    
    // 计算统计数据
    errorStats.value = {
      totalErrors: errors.value.length,
      criticalErrors: errors.value.filter(e => e.severity === 'critical').length,
      resolvedErrors: errors.value.filter(e => e.status === 'resolved').length,
      errorRate: errors.value.length > 0 ? (errors.value.filter(e => e.severity === 'critical' || e.severity === 'error').length / errors.value.length) * 100 : 0
    }
  } catch (error) {
    ElMessage.error('加载错误日志失败')
  } finally {
    loading.value = false
  }
}

const refreshErrors = () => {
  loadErrors()
}

const handleFilter = () => {
  // 筛选逻辑已在计算属性中处理
}

const resetFilters = () => {
  severityFilter.value = ''
  categoryFilter.value = ''
  statusFilter.value = ''
  dateRange.value = null
}

const exportErrors = () => {
  ElMessage.info('导出错误日志功能开发中...')
}

const viewErrorDetail = (error: ErrorLog) => {
  currentError.value = error
  detailDialogVisible.value = true
  activeTab.value = 'basic'
  
  // 重置解决方案表单
  resolutionForm.description = ''
  resolutionForm.actions = ''
  
  // 初始化频率图表
  setTimeout(() => {
    initFrequencyChart()
  }, 100)
}

const toggleStack = (errorId: string) => {
  const index = expandedStacks.value.indexOf(errorId)
  if (index > -1) {
    expandedStacks.value.splice(index, 1)
  } else {
    expandedStacks.value.push(errorId)
  }
}

const handleErrorAction = async (command: string, error: ErrorLog) => {
  switch (command) {
    case 'retry':
      await retryError(error)
      break
    case 'resolve':
      await markAsResolved(error)
      break
    case 'ignore':
      await ignoreError(error)
      break
    case 'report':
      reportError(error)
      break
  }
}

const retryError = async (error: ErrorLog) => {
  try {
    await ElMessageBox.confirm('确定要重试这个错误吗？', '确认重试')
    
    // 模拟重试操作
    error.retryCount += 1
    ElMessage.success('重试操作已提交')
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('重试操作失败')
    }
  }
}

const markAsResolved = async (error: ErrorLog) => {
  try {
    await ElMessageBox.confirm('确定要标记这个错误为已解决吗？', '确认解决')
    
    error.status = 'resolved'
    error.resolution = {
      timestamp: new Date().toISOString(),
      description: '手动标记为已解决'
    }
    
    ElMessage.success('错误已标记为已解决')
    updateErrorStats()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('标记解决失败')
    }
  }
}

const ignoreError = async (error?: ErrorLog) => {
  const targetError = error || currentError.value
  if (!targetError) return
  
  try {
    await ElMessageBox.confirm('确定要忽略这个错误吗？', '确认忽略')
    
    targetError.status = 'ignored'
    ElMessage.success('错误已忽略')
    updateErrorStats()
    
    if (detailDialogVisible.value) {
      detailDialogVisible.value = false
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('忽略错误失败')
    }
  }
}

const resolveError = async () => {
  if (!currentError.value) return
  
  if (!resolutionForm.description.trim()) {
    ElMessage.warning('请填写解决描述')
    return
  }
  
  try {
    currentError.value.status = 'resolved'
    currentError.value.resolution = {
      timestamp: new Date().toISOString(),
      description: resolutionForm.description,
      actions: resolutionForm.actions ? resolutionForm.actions.split('\n').filter(a => a.trim()) : undefined
    }
    
    ElMessage.success('错误已标记为已解决')
    updateErrorStats()
    detailDialogVisible.value = false
  } catch (error) {
    ElMessage.error('解决错误失败')
  }
}

const reportError = (error: ErrorLog) => {
  ElMessage.info('错误报告功能开发中...')
}

const jumpToError = (errorId: string) => {
  const error = errors.value.find(e => e.id === errorId)
  if (error) {
    viewErrorDetail(error)
  } else {
    ElMessage.warning('未找到相关错误')
  }
}

const canRetry = (error: ErrorLog) => {
  return error.status !== 'resolved' && error.retryCount < 5
}

const updateErrorStats = () => {
  errorStats.value = {
    totalErrors: errors.value.length,
    criticalErrors: errors.value.filter(e => e.severity === 'critical').length,
    resolvedErrors: errors.value.filter(e => e.status === 'resolved').length,
    errorRate: errors.value.length > 0 ? (errors.value.filter(e => e.severity === 'critical' || e.severity === 'error').length / errors.value.length) * 100 : 0
  }
}

// 工具方法
const getSeverityTagType = (severity: string) => {
  const typeMap = {
    critical: 'danger',
    error: 'danger',
    warning: 'warning',
    info: 'primary'
  }
  return typeMap[severity as keyof typeof typeMap] || 'primary'
}

const getSeverityText = (severity: string) => {
  const textMap = {
    critical: '严重',
    error: '错误',
    warning: '警告',
    info: '信息'
  }
  return textMap[severity as keyof typeof textMap] || severity
}

const getCategoryTagType = (category: string) => {
  const typeMap = {
    sample_quality: 'warning',
    model_training: 'danger',
    character_generation: 'primary',
    file_processing: 'info',
    system_error: 'danger'
  }
  return typeMap[category as keyof typeof typeMap] || 'info'
}

const getCategoryText = (category: string) => {
  const textMap = {
    sample_quality: '样本质量',
    model_training: '模型训练',
    character_generation: '字符生成',
    file_processing: '文件处理',
    system_error: '系统错误'
  }
  return textMap[category as keyof typeof textMap] || category
}

const getStatusTagType = (status: string) => {
  const typeMap = {
    unresolved: 'danger',
    investigating: 'warning',
    resolved: 'success',
    ignored: 'info'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

const getStatusText = (status: string) => {
  const textMap = {
    unresolved: '未处理',
    investigating: '处理中',
    resolved: '已解决',
    ignored: '已忽略'
  }
  return textMap[status as keyof typeof textMap] || status
}

const formatDateTime = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

const formatContextKey = (key: string) => {
  const keyMap: Record<string, string> = {
    imageQuality: '图片质量',
    minRequiredQuality: '最低质量要求',
    sampleCount: '样本数量',
    validSamples: '有效样本',
    epoch: '训练轮次',
    loss: '损失值',
    learningRate: '学习率',
    batchSize: '批次大小',
    timeout: '超时时间',
    actualTime: '实际耗时',
    complexity: '复杂度',
    strokeCount: '笔画数',
    gpuMemory: 'GPU内存',
    usedMemory: '已用内存',
    cudaVersion: 'CUDA版本',
    pytorchVersion: 'PyTorch版本'
  }
  return keyMap[key] || key
}

const formatContextValue = (value: any) => {
  if (typeof value === 'number') {
    return value.toString()
  }
  return String(value)
}

const formatContextForTable = (context: Record<string, any>) => {
  return Object.entries(context).map(([key, value]) => ({
    key: formatContextKey(key),
    value: formatContextValue(value)
  }))
}

// 图表初始化
const initFrequencyChart = () => {
  if (!frequencyChartRef.value) return
  
  const chart = echarts.init(frequencyChartRef.value)
  
  // 模拟频率数据
  const hours = []
  const errorCounts = []
  const now = new Date()
  
  for (let i = 23; i >= 0; i--) {
    const time = new Date(now.getTime() - i * 60 * 60 * 1000)
    hours.push(time.getHours() + ':00')
    errorCounts.push(Math.floor(Math.random() * 10))
  }
  
  const option = {
    title: {
      text: '24小时错误频率',
      left: 'center',
      textStyle: {
        fontSize: 14
      }
    },
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}个错误'
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
      axisLabel: {
        formatter: '{value}个'
      }
    },
    series: [
      {
        name: '错误数量',
        type: 'bar',
        data: errorCounts,
        itemStyle: {
          color: '#F56C6C'
        }
      }
    ]
  }
  
  chart.setOption(option)
}

// 生命周期
onMounted(() => {
  loadErrors()
})
</script>

<style lang="scss" scoped>
.font-generation-error-log {
  .error-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    margin-bottom: 24px;

    .stat-card {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        color: white;

        &.total-errors .stat-icon {
          background: linear-gradient(135deg, #909399, #B1B3B8);
        }

        &.critical-errors .stat-icon {
          background: linear-gradient(135deg, #F56C6C, #F78989);
        }

        &.resolved-errors .stat-icon {
          background: linear-gradient(135deg, #67C23A, #85CE61);
        }

        &.error-rate .stat-icon {
          background: linear-gradient(135deg, #E6A23C, #EEBE77);
        }
      }

      .stat-content {
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #303133;
          line-height: 1.2;
        }

        .stat-label {
          font-size: 12px;
          color: #909399;
          margin-top: 4px;
        }
      }

      &.total-errors .stat-icon {
        background: linear-gradient(135deg, #909399, #B1B3B8);
      }

      &.critical-errors .stat-icon {
        background: linear-gradient(135deg, #F56C6C, #F78989);
      }

      &.resolved-errors .stat-icon {
        background: linear-gradient(135deg, #67C23A, #85CE61);
      }

      &.error-rate .stat-icon {
        background: linear-gradient(135deg, #E6A23C, #EEBE77);
      }
    }
  }

  .error-filters {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .filter-controls {
      display: flex;
      gap: 12px;
      align-items: center;
      flex-wrap: wrap;
    }

    .filter-actions {
      display: flex;
      gap: 8px;
    }
  }

  .error-list {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .error-item {
      padding: 20px;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      &.critical {
        border-left: 4px solid #F56C6C;
        background: #fef0f0;
      }

      &.error {
        border-left: 4px solid #F56C6C;
      }

      &.warning {
        border-left: 4px solid #E6A23C;
      }

      &.resolved {
        opacity: 0.7;
      }

      .error-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;

        .error-info {
          flex: 1;

          .error-title {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 8px;

            .severity-badge {
              width: 24px;
              height: 24px;
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 12px;
              color: white;

              &.critical {
                background: #F56C6C;
              }

              &.error {
                background: #F56C6C;
              }

              &.warning {
                background: #E6A23C;
              }

              &.info {
                background: #409EFF;
              }
            }

            h4 {
              margin: 0;
              color: #303133;
              font-size: 16px;
              font-weight: 600;
            }
          }

          .error-meta {
            display: flex;
            gap: 16px;
            font-size: 12px;
            color: #909399;
            flex-wrap: wrap;
          }
        }

        .error-actions {
          display: flex;
          gap: 8px;
          align-items: center;
        }
      }

      .error-content {
        .error-message {
          margin-bottom: 16px;

          p {
            margin: 0;
            color: #606266;
            line-height: 1.6;
          }
        }

        .error-context {
          margin-bottom: 16px;

          h5 {
            margin: 0 0 8px 0;
            color: #303133;
            font-size: 14px;
            font-weight: 600;
          }

          .context-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 8px;

            .context-item {
              display: flex;
              gap: 8px;
              font-size: 12px;

              label {
                color: #909399;
                min-width: 80px;
              }

              span {
                color: #606266;
              }
            }
          }
        }

        .error-stack {
          margin-bottom: 16px;

          .stack-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;

            h5 {
              margin: 0;
              color: #303133;
              font-size: 14px;
              font-weight: 600;
            }
          }

          .stack-content {
            background: #f8f9fa;
            border-radius: 4px;
            padding: 12px;
            max-height: 200px;
            overflow-y: auto;

            pre {
              margin: 0;
              font-size: 11px;
              color: #606266;
              white-space: pre-wrap;
            }
          }
        }

        .error-suggestions {
          margin-bottom: 16px;

          h5 {
            margin: 0 0 8px 0;
            color: #303133;
            font-size: 14px;
            font-weight: 600;
          }

          ul {
            margin: 0;
            padding-left: 20px;
            color: #606266;
            font-size: 13px;

            li {
              margin-bottom: 4px;
            }
          }
        }

        .related-errors {
          h5 {
            margin: 0 0 8px 0;
            color: #303133;
            font-size: 14px;
            font-weight: 600;
          }

          .related-list {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
          }
        }
      }

      .error-resolution {
        margin-top: 16px;
        padding: 12px;
        background: #f0f9ff;
        border-radius: 4px;
        border-left: 4px solid #67C23A;

        .resolution-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          h5 {
            margin: 0;
            color: #303133;
            font-size: 14px;
            font-weight: 600;
          }

          .resolution-time {
            font-size: 12px;
            color: #909399;
          }
        }

        .resolution-content {
          p {
            margin: 0 0 8px 0;
            color: #606266;
            font-size: 13px;
          }

          .resolution-actions {
            h6 {
              margin: 0 0 4px 0;
              color: #303133;
              font-size: 12px;
              font-weight: 600;
            }

            ul {
              margin: 0;
              padding-left: 16px;
              color: #606266;
              font-size: 12px;

              li {
                margin-bottom: 2px;
              }
            }
          }
        }
      }
    }
  }

  .error-detail-content {
    .error-message-section {
      margin-top: 20px;

      h4 {
        margin: 0 0 12px 0;
        color: #303133;
        font-size: 16px;
        font-weight: 600;
      }

      .message-content {
        padding: 12px;
        background: #f8f9fa;
        border-radius: 4px;
        color: #606266;
        line-height: 1.6;
      }
    }

    .technical-details {
      .context-section,
      .stack-section,
      .system-section {
        margin-bottom: 24px;

        h4 {
          margin: 0 0 12px 0;
          color: #303133;
          font-size: 16px;
          font-weight: 600;
        }
      }

      .stack-trace {
        background: #f8f9fa;
        border-radius: 4px;
        padding: 12px;
        max-height: 300px;
        overflow-y: auto;

        pre {
          margin: 0;
          font-size: 12px;
          color: #606266;
          white-space: pre-wrap;
        }
      }
    }

    .solution-content {
      .suggestions-section,
      .resolution-section,
      .manual-resolution {
        margin-bottom: 24px;

        h4 {
          margin: 0 0 12px 0;
          color: #303133;
          font-size: 16px;
          font-weight: 600;
        }
      }

      .suggestions-list {
        margin: 0;
        padding-left: 20px;
        color: #606266;

        li {
          margin-bottom: 8px;
        }
      }

      .resolution-info {
        padding: 12px;
        background: #f0f9ff;
        border-radius: 4px;

        p {
          margin: 0 0 8px 0;
          color: #606266;

          strong {
            color: #303133;
          }
        }

        ul {
          margin: 0;
          padding-left: 20px;
          color: #606266;

          li {
            margin-bottom: 4px;
          }
        }
      }
    }

    .related-content {
      .related-errors-section {
        margin-bottom: 24px;

        h4 {
          margin: 0 0 12px 0;
          color: #303133;
          font-size: 16px;
          font-weight: 600;
        }

        .related-errors-list {
          display: flex;
          gap: 12px;
          flex-wrap: wrap;

          .related-error-item {
            padding: 8px 12px;
            background: #f8f9fa;
            border-radius: 4px;
          }
        }
      }

      .error-frequency {
        h4 {
          margin: 0 0 12px 0;
          color: #303133;
          font-size: 16px;
          font-weight: 600;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .font-generation-error-log {
    .error-stats {
      grid-template-columns: repeat(2, 1fr);
    }

    .error-filters {
      flex-direction: column;
      gap: 12px;
      align-items: stretch;

      .filter-controls {
        justify-content: center;
      }

      .filter-actions {
        justify-content: center;
      }
    }

    .error-list {
      .error-item {
        .error-header {
          flex-direction: column;
          gap: 12px;
          align-items: stretch;
        }

        .error-content {
          .error-context {
            .context-grid {
              grid-template-columns: 1fr;
            }
          }
        }
      }
    }
  }
}
</style>