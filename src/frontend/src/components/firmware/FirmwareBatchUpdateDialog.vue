<!--
  固件批量更新对话框组件
  功能：批量选择设备、确认更新、进度监控、结果统计
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    title="批量固件更新"
    width="1000px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <div class="batch-update-dialog">
      <!-- 步骤指示器 -->
      <el-steps :active="currentStep" align-center class="update-steps">
        <el-step title="选择设备" icon="Select" />
        <el-step title="确认更新" icon="Warning" />
        <el-step title="执行更新" icon="Loading" />
        <el-step title="更新完成" icon="SuccessFilled" />
      </el-steps>

      <!-- 步骤1: 设备选择 -->
      <div v-if="currentStep === 0" class="step-content">
        <div class="device-selection">
          <!-- 选择工具栏 -->
          <div class="selection-toolbar">
            <div class="toolbar-left">
              <el-button @click="selectAll" :disabled="availableDevices.length === 0">
                <el-icon><Select /></el-icon>
                全选 ({{ availableDevices.length }})
              </el-button>
              <el-button @click="selectNone" :disabled="selectedDevices.length === 0">
                <el-icon><Close /></el-icon>
                清空选择
              </el-button>
              <el-button @click="selectByPriority('high')" :disabled="!hasHighPriorityDevices">
                <el-icon><Warning /></el-icon>
                选择高优先级
              </el-button>
            </div>
            <div class="toolbar-right">
              <span class="selection-count">已选择 {{ selectedDevices.length }} / {{ availableDevices.length }} 个设备</span>
            </div>
          </div>

          <!-- 筛选器 -->
          <div class="device-filters">
            <el-input
              v-model="deviceSearchKeyword"
              placeholder="搜索设备序列号或名称"
              :prefix-icon="Search"
              clearable
              @input="filterDevices"
              style="width: 300px;"
            />
            <el-select v-model="priorityFilter" placeholder="优先级" clearable @change="filterDevices">
              <el-option label="全部优先级" value="" />
              <el-option label="高优先级" value="high" />
              <el-option label="中优先级" value="medium" />
              <el-option label="低优先级" value="low" />
            </el-select>
            <el-select v-model="modelFilter" placeholder="设备型号" clearable @change="filterDevices">
              <el-option label="全部型号" value="" />
              <el-option label="教育版" value="YX-EDU-2024" />
              <el-option label="家庭版" value="YX-HOME-2024" />
              <el-option label="专业版" value="YX-PRO-2024" />
            </el-select>
          </div>

          <!-- 设备列表 -->
          <div class="device-list">
            <div
              v-for="device in filteredDevices"
              :key="device.deviceId"
              class="device-item"
              :class="{ 
                'selected': selectedDevices.includes(device.deviceId),
                'high-priority': device.priority === 'high',
                'medium-priority': device.priority === 'medium',
                'low-priority': device.priority === 'low'
              }"
              @click="toggleDeviceSelection(device.deviceId)"
            >
              <div class="device-checkbox">
                <el-checkbox
                  :model-value="selectedDevices.includes(device.deviceId)"
                  @change="toggleDeviceSelection(device.deviceId)"
                />
              </div>
              <div class="device-info">
                <div class="device-header">
                  <div class="device-name">{{ device.deviceName }}</div>
                  <div class="device-serial">{{ device.deviceSerialNumber }}</div>
                  <el-tag :type="getPriorityTagType(device.priority)" size="small">
                    {{ getPriorityText(device.priority) }}
                  </el-tag>
                </div>
                <div class="device-details">
                  <div class="detail-item">
                    <span class="detail-label">当前版本:</span>
                    <span class="detail-value">{{ device.currentVersion }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">目标版本:</span>
                    <span class="detail-value target-version">{{ device.targetVersion }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">设备型号:</span>
                    <span class="detail-value">{{ getModelName(device.deviceModel) }}</span>
                  </div>
                </div>
                <div class="device-status">
                  <div class="status-item">
                    <el-icon :class="device.isOnline ? 'status-online' : 'status-offline'">
                      <CircleCheckFilled v-if="device.isOnline" />
                      <CircleCloseFilled v-else />
                    </el-icon>
                    <span>{{ device.isOnline ? '在线' : '离线' }}</span>
                  </div>
                  <div class="status-item" v-if="device.signalStrength">
                    <el-icon><Link /></el-icon>
                    <span>信号 {{ device.signalStrength }}%</span>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="filteredDevices.length === 0" class="no-devices">
              <el-empty description="没有找到符合条件的设备" />
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="step-actions">
          <el-button @click="handleClose">取消</el-button>
          <el-button
            type="primary"
            @click="nextStep"
            :disabled="selectedDevices.length === 0"
          >
            下一步 ({{ selectedDevices.length }})
          </el-button>
        </div>
      </div>

      <!-- 步骤2: 确认更新 -->
      <div v-if="currentStep === 1" class="step-content">
        <div class="update-confirmation">
          <!-- 更新概览 -->
          <div class="confirmation-overview">
            <div class="overview-header">
              <h3>更新确认</h3>
              <p>即将为以下 {{ selectedDevices.length }} 个设备执行固件更新</p>
            </div>

            <div class="overview-stats">
              <div class="stat-card">
                <div class="stat-icon">
                  <el-icon><Monitor /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ selectedDevices.length }}</div>
                  <div class="stat-label">选中设备</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <el-icon><Warning /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ highPriorityCount }}</div>
                  <div class="stat-label">高优先级</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <el-icon><Timer /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ estimatedTotalTime }}</div>
                  <div class="stat-label">预计时间</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 风险提示 -->
          <div class="risk-warnings">
            <h4>重要提示</h4>
            <div class="warning-list">
              <el-alert
                title="更新过程中请勿断开设备连接"
                type="warning"
                show-icon
                :closable="false"
              />
              <el-alert
                title="确保设备电量充足（建议 > 50%）"
                type="warning"
                show-icon
                :closable="false"
              />
              <el-alert
                v-if="offlineDevicesCount > 0"
                :title="`${offlineDevicesCount} 个设备当前离线，可能无法更新`"
                type="error"
                show-icon
                :closable="false"
              />
              <el-alert
                v-if="lowSignalDevicesCount > 0"
                :title="`${lowSignalDevicesCount} 个设备信号较弱，更新可能中断`"
                type="warning"
                show-icon
                :closable="false"
              />
            </div>
          </div>

          <!-- 更新选项 -->
          <div class="update-options">
            <h4>更新选项</h4>
            <div class="options-form">
              <el-form :model="updateOptions" label-width="120px">
                <el-form-item label="更新模式">
                  <el-radio-group v-model="updateOptions.mode">
                    <el-radio label="parallel">并行更新（快速）</el-radio>
                    <el-radio label="sequential">顺序更新（稳定）</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="失败处理">
                  <el-radio-group v-model="updateOptions.failureHandling">
                    <el-radio label="continue">继续更新其他设备</el-radio>
                    <el-radio label="stop">停止所有更新</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="自动重试">
                  <el-switch v-model="updateOptions.autoRetry" />
                  <span class="option-description">更新失败时自动重试</span>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="step-actions">
          <el-button @click="prevStep">上一步</el-button>
          <el-button type="primary" @click="startBatchUpdate">
            <el-icon><Upload /></el-icon>
            开始更新
          </el-button>
        </div>
      </div>

      <!-- 步骤3: 执行更新 -->
      <div v-if="currentStep === 2" class="step-content">
        <div class="update-progress">
          <!-- 总体进度 -->
          <div class="overall-progress">
            <div class="progress-header">
              <h3>批量更新进行中...</h3>
              <p>正在更新 {{ selectedDevices.length }} 个设备，请勿关闭页面</p>
            </div>

            <div class="progress-stats">
              <div class="progress-item">
                <div class="progress-label">总体进度</div>
                <el-progress
                  :percentage="overallProgress"
                  :status="overallProgressStatus"
                  stroke-width="12"
                />
                <div class="progress-text">
                  {{ completedDevices }} / {{ selectedDevices.length }} 设备完成
                </div>
              </div>
            </div>

            <div class="status-summary">
              <div class="summary-item success">
                <el-icon><CircleCheckFilled /></el-icon>
                <span class="summary-label">成功</span>
                <span class="summary-value">{{ successCount }}</span>
              </div>
              <div class="summary-item processing">
                <el-icon><Loading /></el-icon>
                <span class="summary-label">进行中</span>
                <span class="summary-value">{{ processingCount }}</span>
              </div>
              <div class="summary-item failed">
                <el-icon><CircleCloseFilled /></el-icon>
                <span class="summary-label">失败</span>
                <span class="summary-value">{{ failedCount }}</span>
              </div>
              <div class="summary-item pending">
                <el-icon><Clock /></el-icon>
                <span class="summary-label">等待</span>
                <span class="summary-value">{{ pendingCount }}</span>
              </div>
            </div>
          </div>

          <!-- 设备更新详情 -->
          <div class="device-progress-list">
            <div class="progress-list-header">
              <h4>设备更新详情</h4>
              <div class="header-actions">
                <el-button size="small" @click="toggleShowAll">
                  {{ showAllDevices ? '只显示进行中' : '显示全部' }}
                </el-button>
              </div>
            </div>

            <div class="progress-list">
              <div
                v-for="device in displayedDeviceProgress"
                :key="device.deviceId"
                class="device-progress-item"
                :class="device.status"
              >
                <div class="device-progress-header">
                  <div class="device-info">
                    <span class="device-name">{{ device.deviceName }}</span>
                    <span class="device-serial">{{ device.deviceSerialNumber }}</span>
                  </div>
                  <div class="progress-status">
                    <el-icon class="status-icon">
                      <Loading v-if="device.status === 'updating'" />
                      <CircleCheckFilled v-else-if="device.status === 'completed'" />
                      <CircleCloseFilled v-else-if="device.status === 'failed'" />
                      <Clock v-else />
                    </el-icon>
                    <span class="status-text">{{ getDeviceStatusText(device.status) }}</span>
                  </div>
                </div>

                <div class="device-progress-content">
                  <div class="progress-bar">
                    <el-progress
                      :percentage="device.progress"
                      :status="getProgressStatus(device.status)"
                      :show-text="false"
                      stroke-width="6"
                    />
                    <span class="progress-percentage">{{ device.progress }}%</span>
                  </div>
                  <div class="progress-step">{{ device.currentStep }}</div>
                  <div v-if="device.errorMessage" class="error-message">
                    <el-icon><WarningFilled /></el-icon>
                    <span>{{ device.errorMessage }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="step-actions">
          <el-button @click="pauseUpdate" :disabled="!isUpdating">
            <el-icon><VideoPause /></el-icon>
            暂停更新
          </el-button>
          <el-button @click="cancelUpdate" :disabled="!isUpdating">
            <el-icon><Close /></el-icon>
            取消更新
          </el-button>
        </div>
      </div>

      <!-- 步骤4: 更新完成 -->
      <div v-if="currentStep === 3" class="step-content">
        <div class="update-results">
          <!-- 结果概览 -->
          <div class="results-overview">
            <div class="results-header">
              <div class="result-icon" :class="overallResultType">
                <el-icon>
                  <CircleCheckFilled v-if="overallResultType === 'success'" />
                  <WarningFilled v-else-if="overallResultType === 'warning'" />
                  <CircleCloseFilled v-else />
                </el-icon>
              </div>
              <h3>{{ overallResultTitle }}</h3>
              <p>{{ overallResultDescription }}</p>
            </div>

            <div class="results-stats">
              <div class="result-stat success">
                <div class="stat-value">{{ successCount }}</div>
                <div class="stat-label">更新成功</div>
              </div>
              <div class="result-stat failed">
                <div class="stat-value">{{ failedCount }}</div>
                <div class="stat-label">更新失败</div>
              </div>
              <div class="result-stat time">
                <div class="stat-value">{{ actualUpdateTime }}</div>
                <div class="stat-label">总用时</div>
              </div>
            </div>
          </div>

          <!-- 详细结果 -->
          <div class="detailed-results">
            <el-tabs v-model="resultTab" type="border-card">
              <el-tab-pane label="成功设备" name="success" v-if="successCount > 0">
                <div class="result-list">
                  <div
                    v-for="device in successDevices"
                    :key="device.deviceId"
                    class="result-item success"
                  >
                    <el-icon class="result-icon"><CircleCheckFilled /></el-icon>
                    <div class="result-info">
                      <div class="result-name">{{ device.deviceName }}</div>
                      <div class="result-details">
                        {{ device.deviceSerialNumber }} · 
                        {{ device.currentVersion }} → {{ device.targetVersion }} · 
                        用时 {{ device.duration }}
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>

              <el-tab-pane label="失败设备" name="failed" v-if="failedCount > 0">
                <div class="result-list">
                  <div
                    v-for="device in failedDevices"
                    :key="device.deviceId"
                    class="result-item failed"
                  >
                    <el-icon class="result-icon"><CircleCloseFilled /></el-icon>
                    <div class="result-info">
                      <div class="result-name">{{ device.deviceName }}</div>
                      <div class="result-details">
                        {{ device.deviceSerialNumber }} · {{ device.errorMessage }}
                      </div>
                      <div class="result-actions">
                        <el-button size="small" text type="primary" @click="retryDevice(device.deviceId)">
                          重试
                        </el-button>
                        <el-button size="small" text @click="viewDeviceLogs(device.deviceId)">
                          查看日志
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="step-actions">
          <el-button @click="handleClose">关闭</el-button>
          <el-button v-if="failedCount > 0" type="primary" @click="retryFailedDevices">
            <el-icon><Refresh /></el-icon>
            重试失败设备
          </el-button>
          <el-button type="success" @click="exportResults">
            <el-icon><Download /></el-icon>
            导出结果
          </el-button>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import {
  Select,
  Close,
  Warning,
  Search,
  Link,
  Monitor,
  Timer,
  Upload,
  CircleCheckFilled,
  Loading,
  CircleCloseFilled,
  Clock,
  WarningFilled,
  VideoPause,
  Refresh,
  Download
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// Props
interface Props {
  modelValue: boolean
  availableDevices?: BatchUpdateDevice[]
}

const props = withDefaults(defineProps<Props>(), {
  availableDevices: () => []
})

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'update-complete': [result: BatchUpdateResult]
}>()

// 批量更新设备接口
interface BatchUpdateDevice {
  deviceId: string
  deviceName: string
  deviceSerialNumber: string
  deviceModel: string
  currentVersion: string
  targetVersion: string
  priority: 'high' | 'medium' | 'low'
  isOnline: boolean
  signalStrength?: number
}

// 设备更新进度接口
interface DeviceUpdateProgress {
  deviceId: string
  deviceName: string
  deviceSerialNumber: string
  currentVersion: string
  targetVersion: string
  status: 'pending' | 'updating' | 'completed' | 'failed'
  progress: number
  currentStep: string
  errorMessage?: string
  startTime?: string
  endTime?: string
  duration?: string
}

// 批量更新结果接口
interface BatchUpdateResult {
  totalDevices: number
  successCount: number
  failedCount: number
  duration: string
  successDevices: DeviceUpdateProgress[]
  failedDevices: DeviceUpdateProgress[]
}

// 响应式数据
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const currentStep = ref(0)
const selectedDevices = ref<string[]>([])
const deviceSearchKeyword = ref('')
const priorityFilter = ref('')
const modelFilter = ref('')
const showAllDevices = ref(false)
const resultTab = ref('success')
const isUpdating = ref(false)

// 更新选项
const updateOptions = ref({
  mode: 'parallel',
  failureHandling: 'continue',
  autoRetry: true
})

// 设备更新进度
const deviceProgressMap = ref<Map<string, DeviceUpdateProgress>>(new Map())

// 计算属性
const filteredDevices = computed(() => {
  let devices = props.availableDevices

  if (deviceSearchKeyword.value) {
    const keyword = deviceSearchKeyword.value.toLowerCase()
    devices = devices.filter(device =>
      device.deviceName.toLowerCase().includes(keyword) ||
      device.deviceSerialNumber.toLowerCase().includes(keyword)
    )
  }

  if (priorityFilter.value) {
    devices = devices.filter(device => device.priority === priorityFilter.value)
  }

  if (modelFilter.value) {
    devices = devices.filter(device => device.deviceModel === modelFilter.value)
  }

  return devices
})

const hasHighPriorityDevices = computed(() => {
  return props.availableDevices.some(device => device.priority === 'high')
})

const highPriorityCount = computed(() => {
  return selectedDevices.value.filter(deviceId => {
    const device = props.availableDevices.find(d => d.deviceId === deviceId)
    return device?.priority === 'high'
  }).length
})

const estimatedTotalTime = computed(() => {
  const deviceCount = selectedDevices.value.length
  if (deviceCount === 0) return '0分钟'
  
  // 根据更新模式估算时间
  if (updateOptions.value.mode === 'parallel') {
    return `${Math.ceil(deviceCount / 5) * 6}-${Math.ceil(deviceCount / 5) * 10}分钟`
  } else {
    return `${deviceCount * 5}-${deviceCount * 8}分钟`
  }
})

const offlineDevicesCount = computed(() => {
  return selectedDevices.value.filter(deviceId => {
    const device = props.availableDevices.find(d => d.deviceId === deviceId)
    return !device?.isOnline
  }).length
})

const lowSignalDevicesCount = computed(() => {
  return selectedDevices.value.filter(deviceId => {
    const device = props.availableDevices.find(d => d.deviceId === deviceId)
    return device?.signalStrength && device.signalStrength < 50
  }).length
})

const overallProgress = computed(() => {
  if (selectedDevices.value.length === 0) return 0
  
  const totalProgress = Array.from(deviceProgressMap.value.values())
    .reduce((sum, device) => sum + device.progress, 0)
  
  return Math.round(totalProgress / selectedDevices.value.length)
})

const overallProgressStatus = computed(() => {
  if (failedCount.value > 0) return 'exception'
  if (completedDevices.value === selectedDevices.value.length) return 'success'
  return undefined
})

const completedDevices = computed(() => {
  return Array.from(deviceProgressMap.value.values())
    .filter(device => device.status === 'completed' || device.status === 'failed').length
})

const successCount = computed(() => {
  return Array.from(deviceProgressMap.value.values())
    .filter(device => device.status === 'completed').length
})

const failedCount = computed(() => {
  return Array.from(deviceProgressMap.value.values())
    .filter(device => device.status === 'failed').length
})

const processingCount = computed(() => {
  return Array.from(deviceProgressMap.value.values())
    .filter(device => device.status === 'updating').length
})

const pendingCount = computed(() => {
  return Array.from(deviceProgressMap.value.values())
    .filter(device => device.status === 'pending').length
})

const displayedDeviceProgress = computed(() => {
  const allProgress = Array.from(deviceProgressMap.value.values())
  
  if (showAllDevices.value) {
    return allProgress
  } else {
    return allProgress.filter(device => 
      device.status === 'updating' || device.status === 'failed'
    )
  }
})

const overallResultType = computed(() => {
  if (failedCount.value === 0) return 'success'
  if (successCount.value === 0) return 'error'
  return 'warning'
})

const overallResultTitle = computed(() => {
  if (failedCount.value === 0) return '批量更新完成'
  if (successCount.value === 0) return '批量更新失败'
  return '批量更新部分完成'
})

const overallResultDescription = computed(() => {
  if (failedCount.value === 0) {
    return `所有 ${successCount.value} 个设备已成功更新到最新版本`
  }
  if (successCount.value === 0) {
    return `所有 ${failedCount.value} 个设备更新失败，请检查设备状态后重试`
  }
  return `${successCount.value} 个设备更新成功，${failedCount.value} 个设备更新失败`
})

const actualUpdateTime = computed(() => {
  // 这里应该计算实际的更新时间
  return '15分钟'
})

const successDevices = computed(() => {
  return Array.from(deviceProgressMap.value.values())
    .filter(device => device.status === 'completed')
})

const failedDevices = computed(() => {
  return Array.from(deviceProgressMap.value.values())
    .filter(device => device.status === 'failed')
})

// 方法
const selectAll = () => {
  selectedDevices.value = filteredDevices.value.map(device => device.deviceId)
}

const selectNone = () => {
  selectedDevices.value = []
}

const selectByPriority = (priority: string) => {
  selectedDevices.value = props.availableDevices
    .filter(device => device.priority === priority)
    .map(device => device.deviceId)
}

const filterDevices = () => {
  // 筛选逻辑已在计算属性中实现
}

const toggleDeviceSelection = (deviceId: string) => {
  const index = selectedDevices.value.indexOf(deviceId)
  if (index > -1) {
    selectedDevices.value.splice(index, 1)
  } else {
    selectedDevices.value.push(deviceId)
  }
}

const nextStep = () => {
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const startBatchUpdate = async () => {
  currentStep.value = 2
  isUpdating.value = true

  // 初始化设备进度
  selectedDevices.value.forEach(deviceId => {
    const device = props.availableDevices.find(d => d.deviceId === deviceId)
    if (device) {
      deviceProgressMap.value.set(deviceId, {
        deviceId,
        deviceName: device.deviceName,
        deviceSerialNumber: device.deviceSerialNumber,
        currentVersion: device.currentVersion,
        targetVersion: device.targetVersion,
        status: 'pending',
        progress: 0,
        currentStep: '准备更新',
        startTime: new Date().toISOString()
      })
    }
  })

  // 模拟批量更新过程
  try {
    if (updateOptions.value.mode === 'parallel') {
      await simulateParallelUpdate()
    } else {
      await simulateSequentialUpdate()
    }
  } catch (error) {
    console.error('批量更新失败:', error)
  } finally {
    isUpdating.value = false
    currentStep.value = 3
  }
}

const simulateParallelUpdate = async () => {
  const updatePromises = selectedDevices.value.map(deviceId => 
    simulateDeviceUpdate(deviceId)
  )
  
  await Promise.allSettled(updatePromises)
}

const simulateSequentialUpdate = async () => {
  for (const deviceId of selectedDevices.value) {
    if (!isUpdating.value) break // 检查是否被取消
    await simulateDeviceUpdate(deviceId)
  }
}

const simulateDeviceUpdate = async (deviceId: string) => {
  const device = deviceProgressMap.value.get(deviceId)
  if (!device) return

  try {
    // 更新状态为正在更新
    device.status = 'updating'
    device.currentStep = '下载固件'
    deviceProgressMap.value.set(deviceId, { ...device })

    // 模拟下载进度
    for (let i = 0; i <= 50; i += 10) {
      if (!isUpdating.value) throw new Error('更新被取消')
      
      device.progress = i
      deviceProgressMap.value.set(deviceId, { ...device })
      await new Promise(resolve => setTimeout(resolve, 200))
    }

    // 模拟安装进度
    device.currentStep = '安装固件'
    deviceProgressMap.value.set(deviceId, { ...device })

    for (let i = 50; i <= 90; i += 10) {
      if (!isUpdating.value) throw new Error('更新被取消')
      
      device.progress = i
      deviceProgressMap.value.set(deviceId, { ...device })
      await new Promise(resolve => setTimeout(resolve, 300))
    }

    // 模拟重启
    device.currentStep = '重启设备'
    deviceProgressMap.value.set(deviceId, { ...device })
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 随机模拟成功或失败
    const shouldFail = Math.random() < 0.1 // 10% 失败率
    
    if (shouldFail) {
      throw new Error('设备更新失败：网络连接中断')
    }

    // 更新完成
    device.status = 'completed'
    device.progress = 100
    device.currentStep = '更新完成'
    device.endTime = new Date().toISOString()
    device.duration = '5分钟'
    deviceProgressMap.value.set(deviceId, { ...device })

  } catch (error: any) {
    device.status = 'failed'
    device.errorMessage = error.message
    device.endTime = new Date().toISOString()
    deviceProgressMap.value.set(deviceId, { ...device })
  }
}

const pauseUpdate = () => {
  isUpdating.value = false
  ElMessage.info('更新已暂停')
}

const cancelUpdate = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要取消批量更新吗？正在进行的更新将被中断。',
      '确认取消',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '继续更新',
        type: 'warning'
      }
    )
    
    isUpdating.value = false
    currentStep.value = 3
    ElMessage.info('批量更新已取消')
  } catch {
    // 用户取消
  }
}

const toggleShowAll = () => {
  showAllDevices.value = !showAllDevices.value
}

const retryDevice = (deviceId: string) => {
  ElMessage.info(`重试设备更新: ${deviceId}`)
}

const viewDeviceLogs = (deviceId: string) => {
  ElMessage.info(`查看设备日志: ${deviceId}`)
}

const retryFailedDevices = () => {
  ElMessage.info('重试失败设备功能开发中')
}

const exportResults = () => {
  ElMessage.success('更新结果导出功能开发中')
}

const handleClose = () => {
  if (isUpdating.value) {
    ElMessage.warning('更新进行中，无法关闭对话框')
    return
  }

  // 重置状态
  currentStep.value = 0
  selectedDevices.value = []
  deviceProgressMap.value.clear()
  
  emit('update:modelValue', false)
}

// 工具方法
const getPriorityTagType = (priority: string) => {
  const types: Record<string, any> = {
    high: 'danger',
    medium: 'warning',
    low: 'info'
  }
  return types[priority] || 'info'
}

const getPriorityText = (priority: string) => {
  const texts: Record<string, string> = {
    high: '高优先级',
    medium: '中优先级',
    low: '低优先级'
  }
  return texts[priority] || priority
}

const getModelName = (model: string) => {
  const names: Record<string, string> = {
    'YX-EDU-2024': '教育版',
    'YX-HOME-2024': '家庭版',
    'YX-PRO-2024': '专业版'
  }
  return names[model] || model
}

const getDeviceStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '等待中',
    updating: '更新中',
    completed: '已完成',
    failed: '失败'
  }
  return texts[status] || status
}

const getProgressStatus = (status: string) => {
  if (status === 'completed') return 'success'
  if (status === 'failed') return 'exception'
  return undefined
}

// 监听对话框关闭
watch(dialogVisible, (visible) => {
  if (!visible) {
    handleClose()
  }
})
</script>

<style lang="scss" scoped>
.batch-update-dialog {
  .update-steps {
    margin-bottom: 32px;
  }

  .step-content {
    min-height: 500px;
    display: flex;
    flex-direction: column;
  }

  .step-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: auto;
    padding-top: 24px;
    border-top: 1px solid #ebeef5;
  }

  // 步骤1: 设备选择样式
  .device-selection {
    flex: 1;
    display: flex;
    flex-direction: column;

    .selection-toolbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding: 12px 16px;
      background: #f8f9fa;
      border-radius: 6px;

      .toolbar-left {
        display: flex;
        gap: 8px;
      }

      .toolbar-right {
        .selection-count {
          font-size: 14px;
          color: #606266;
          font-weight: 500;
        }
      }
    }

    .device-filters {
      display: flex;
      gap: 12px;
      margin-bottom: 16px;
      flex-wrap: wrap;
    }

    .device-list {
      flex: 1;
      max-height: 400px;
      overflow-y: auto;
      border: 1px solid #e4e7ed;
      border-radius: 6px;

      .device-item {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        padding: 16px;
        border-bottom: 1px solid #f0f0f0;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          background: #f8f9fa;
        }

        &.selected {
          background: #f0f9ff;
          border-left: 4px solid #409eff;
        }

        &.high-priority {
          border-left: 4px solid #f56c6c;
        }

        &.medium-priority {
          border-left: 4px solid #e6a23c;
        }

        &.low-priority {
          border-left: 4px solid #909399;
        }

        .device-checkbox {
          margin-top: 4px;
        }

        .device-info {
          flex: 1;

          .device-header {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 8px;

            .device-name {
              font-size: 16px;
              font-weight: 500;
              color: #303133;
            }

            .device-serial {
              font-size: 12px;
              color: #909399;
              font-family: monospace;
            }
          }

          .device-details {
            display: flex;
            flex-wrap: wrap;
            gap: 16px;
            margin-bottom: 8px;

            .detail-item {
              display: flex;
              gap: 4px;
              font-size: 14px;

              .detail-label {
                color: #606266;
              }

              .detail-value {
                color: #303133;
                font-weight: 500;

                &.target-version {
                  color: #67c23a;
                }
              }
            }
          }

          .device-status {
            display: flex;
            gap: 16px;

            .status-item {
              display: flex;
              align-items: center;
              gap: 4px;
              font-size: 12px;
              color: #606266;

              .status-online {
                color: #67c23a;
              }

              .status-offline {
                color: #f56c6c;
              }
            }
          }
        }
      }

      .no-devices {
        padding: 40px;
        text-align: center;
      }
    }
  }

  // 步骤2: 确认更新样式
  .update-confirmation {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 24px;

    .confirmation-overview {
      .overview-header {
        text-align: center;
        margin-bottom: 20px;

        h3 {
          margin: 0 0 8px 0;
          font-size: 20px;
          color: #303133;
        }

        p {
          margin: 0;
          color: #606266;
        }
      }

      .overview-stats {
        display: flex;
        justify-content: center;
        gap: 24px;

        .stat-card {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 16px 20px;
          background: white;
          border: 1px solid #e4e7ed;
          border-radius: 8px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.06);

          .stat-icon {
            width: 40px;
            height: 40px;
            background: #f0f9ff;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #409eff;
            font-size: 18px;
          }

          .stat-info {
            .stat-value {
              font-size: 20px;
              font-weight: 700;
              color: #303133;
              margin-bottom: 2px;
            }

            .stat-label {
              font-size: 12px;
              color: #606266;
            }
          }
        }
      }
    }

    .risk-warnings {
      h4 {
        margin: 0 0 12px 0;
        font-size: 16px;
        color: #303133;
      }

      .warning-list {
        display: flex;
        flex-direction: column;
        gap: 8px;
      }
    }

    .update-options {
      h4 {
        margin: 0 0 12px 0;
        font-size: 16px;
        color: #303133;
      }

      .options-form {
        background: #f8f9fa;
        padding: 16px;
        border-radius: 6px;

        .option-description {
          margin-left: 8px;
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }

  // 步骤3: 执行更新样式
  .update-progress {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 24px;

    .overall-progress {
      .progress-header {
        text-align: center;
        margin-bottom: 20px;

        h3 {
          margin: 0 0 8px 0;
          font-size: 18px;
          color: #303133;
        }

        p {
          margin: 0;
          color: #606266;
        }
      }

      .progress-stats {
        margin-bottom: 20px;

        .progress-item {
          .progress-label {
            font-size: 14px;
            color: #606266;
            margin-bottom: 8px;
          }

          .progress-text {
            text-align: center;
            font-size: 14px;
            color: #606266;
            margin-top: 8px;
          }
        }
      }

      .status-summary {
        display: flex;
        justify-content: center;
        gap: 24px;

        .summary-item {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 14px;

          .summary-label {
            color: #606266;
          }

          .summary-value {
            font-weight: 600;
            color: #303133;
          }

          &.success .el-icon {
            color: #67c23a;
          }

          &.processing .el-icon {
            color: #409eff;
          }

          &.failed .el-icon {
            color: #f56c6c;
          }

          &.pending .el-icon {
            color: #909399;
          }
        }
      }
    }

    .device-progress-list {
      flex: 1;

      .progress-list-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        h4 {
          margin: 0;
          font-size: 16px;
          color: #303133;
        }
      }

      .progress-list {
        max-height: 300px;
        overflow-y: auto;
        border: 1px solid #e4e7ed;
        border-radius: 6px;

        .device-progress-item {
          padding: 12px 16px;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          &.completed {
            background: #f0f9ff;
          }

          &.failed {
            background: #fef0f0;
          }

          .device-progress-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;

            .device-info {
              display: flex;
              gap: 8px;
              align-items: center;

              .device-name {
                font-size: 14px;
                font-weight: 500;
                color: #303133;
              }

              .device-serial {
                font-size: 12px;
                color: #909399;
                font-family: monospace;
              }
            }

            .progress-status {
              display: flex;
              align-items: center;
              gap: 6px;
              font-size: 12px;

              .status-icon {
                &.updating {
                  color: #409eff;
                }

                &.completed {
                  color: #67c23a;
                }

                &.failed {
                  color: #f56c6c;
                }

                &.pending {
                  color: #909399;
                }
              }

              .status-text {
                color: #606266;
              }
            }
          }

          .device-progress-content {
            .progress-bar {
              display: flex;
              align-items: center;
              gap: 8px;
              margin-bottom: 4px;

              .el-progress {
                flex: 1;
              }

              .progress-percentage {
                font-size: 12px;
                color: #606266;
                min-width: 35px;
                text-align: right;
              }
            }

            .progress-step {
              font-size: 12px;
              color: #909399;
              margin-bottom: 4px;
            }

            .error-message {
              display: flex;
              align-items: center;
              gap: 4px;
              font-size: 12px;
              color: #f56c6c;

              .el-icon {
                flex-shrink: 0;
              }
            }
          }
        }
      }
    }
  }

  // 步骤4: 更新完成样式
  .update-results {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 24px;

    .results-overview {
      .results-header {
        text-align: center;
        margin-bottom: 20px;

        .result-icon {
          width: 64px;
          height: 64px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin: 0 auto 16px;
          font-size: 32px;

          &.success {
            background: #f0f9ff;
            color: #67c23a;
          }

          &.warning {
            background: #fef7e0;
            color: #e6a23c;
          }

          &.error {
            background: #fef0f0;
            color: #f56c6c;
          }
        }

        h3 {
          margin: 0 0 8px 0;
          font-size: 20px;
          color: #303133;
        }

        p {
          margin: 0;
          color: #606266;
        }
      }

      .results-stats {
        display: flex;
        justify-content: center;
        gap: 32px;

        .result-stat {
          text-align: center;

          .stat-value {
            font-size: 24px;
            font-weight: 700;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 12px;
            color: #606266;
          }

          &.success .stat-value {
            color: #67c23a;
          }

          &.failed .stat-value {
            color: #f56c6c;
          }

          &.time .stat-value {
            color: #409eff;
          }
        }
      }
    }

    .detailed-results {
      flex: 1;

      .result-list {
        max-height: 300px;
        overflow-y: auto;

        .result-item {
          display: flex;
          align-items: flex-start;
          gap: 12px;
          padding: 12px 16px;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .result-icon {
            margin-top: 2px;
            flex-shrink: 0;

            &.success {
              color: #67c23a;
            }

            &.failed {
              color: #f56c6c;
            }
          }

          .result-info {
            flex: 1;

            .result-name {
              font-size: 14px;
              font-weight: 500;
              color: #303133;
              margin-bottom: 4px;
            }

            .result-details {
              font-size: 12px;
              color: #606266;
              margin-bottom: 8px;
            }

            .result-actions {
              display: flex;
              gap: 8px;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .batch-update-dialog {
    .step-content {
      min-height: 400px;
    }

    .device-selection {
      .selection-toolbar {
        flex-direction: column;
        gap: 12px;
        align-items: stretch;

        .toolbar-left {
          justify-content: center;
        }

        .toolbar-right {
          text-align: center;
        }
      }

      .device-filters {
        justify-content: center;
      }
    }

    .update-confirmation {
      .overview-stats {
        flex-direction: column;
        align-items: center;
      }
    }

    .update-progress {
      .status-summary {
        flex-wrap: wrap;
        gap: 16px;
      }
    }

    .update-results {
      .results-stats {
        flex-direction: column;
        gap: 16px;
      }
    }

    .step-actions {
      flex-direction: column-reverse;

      .el-button {
        width: 100%;
      }
    }
  }
}
</style>