<template>
  <el-dialog
    v-model="visible"
    title="固件更新"
    width="800px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <div class="firmware-update-dialog">
      <!-- 步骤指示器 -->
      <el-steps :active="currentStep" align-center class="update-steps">
        <el-step title="选择设备" icon="Monitor" />
        <el-step title="选择版本" icon="Download" />
        <el-step title="确认更新" icon="Check" />
      </el-steps>

      <!-- 步骤1: 设备选择 -->
      <div v-if="currentStep === 0" class="step-content">
        <div class="step-header">
          <h3>选择要更新的设备</h3>
          <p class="step-description">选择需要进行固件更新的设备，支持单个或批量选择</p>
        </div>

        <!-- 设备筛选 -->
        <div class="device-filters">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-input
                v-model="deviceFilters.keyword"
                placeholder="搜索设备名称或序列号"
                prefix-icon="Search"
                clearable
                @input="handleDeviceFilter"
              />
            </el-col>
            <el-col :span="6">
              <el-select
                v-model="deviceFilters.deviceModel"
                placeholder="设备型号"
                clearable
                @change="handleDeviceFilter"
              >
                <el-option label="全部型号" value="" />
                <el-option label="YX-EDU-2024" value="YX-EDU-2024" />
                <el-option label="YX-HOME-2024" value="YX-HOME-2024" />
                <el-option label="YX-PRO-2024" value="YX-PRO-2024" />
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-select
                v-model="deviceFilters.updateStatus"
                placeholder="更新状态"
                clearable
                @change="handleDeviceFilter"
              >
                <el-option label="全部状态" value="" />
                <el-option label="有可用更新" value="update-available" />
                <el-option label="最新版本" value="up-to-date" />
                <el-option label="更新失败" value="failed" />
              </el-select>
            </el-col>
            <el-col :span="4">
              <el-checkbox
                v-model="deviceFilters.onlineOnly"
                @change="handleDeviceFilter"
              >
                仅在线设备
              </el-checkbox>
            </el-col>
          </el-row>
        </div>

        <!-- 设备列表 -->
        <div class="device-list">
          <div class="list-header">
            <el-checkbox
              v-model="selectAll"
              :indeterminate="isIndeterminate"
              @change="handleSelectAll"
            >
              全选 ({{ selectedDevices.length }}/{{ filteredDevices.length }})
            </el-checkbox>
            <span class="device-count">
              共 {{ filteredDevices.length }} 台设备，{{ availableUpdateDevices.length }} 台可更新
            </span>
          </div>

          <div class="device-items">
            <div
              v-for="device in filteredDevices"
              :key="device.deviceId"
              class="device-item"
              :class="{
                'selected': selectedDevices.includes(device.deviceId),
                'disabled': !device.canUpdate
              }"
              @click="handleDeviceSelect(device)"
            >
              <el-checkbox
                :model-value="selectedDevices.includes(device.deviceId)"
                :disabled="!device.canUpdate"
                @change="(checked) => handleDeviceCheck(device.deviceId, checked)"
              />
              
              <div class="device-info">
                <div class="device-main">
                  <span class="device-name">{{ device.deviceName }}</span>
                  <span class="device-serial">{{ device.deviceSerialNumber }}</span>
                  <el-tag :type="device.isOnline ? 'success' : 'danger'" size="small">
                    {{ device.isOnline ? '在线' : '离线' }}
                  </el-tag>
                </div>
                <div class="device-details">
                  <span class="device-model">{{ device.deviceModel }}</span>
                  <span class="device-version">当前版本: {{ device.currentVersion }}</span>
                  <span v-if="device.updateAvailable" class="latest-version">
                    最新版本: {{ device.latestVersion }}
                  </span>
                </div>
                <div v-if="device.updateRestrictions?.length" class="device-restrictions">
                  <el-icon class="warning-icon"><Warning /></el-icon>
                  <span>{{ device.updateRestrictions.join(', ') }}</span>
                </div>
              </div>

              <div class="device-status">
                <el-tag
                  :type="getUpdateStatusType(device.updateStatus)"
                  size="small"
                >
                  {{ getUpdateStatusText(device.updateStatus) }}
                </el-tag>
                <div v-if="device.signalStrength !== undefined" class="signal-strength">
                  <el-icon><Connection /></el-icon>
                  <span>{{ device.signalStrength }}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 步骤2: 版本选择 -->
      <div v-if="currentStep === 1" class="step-content">
        <div class="step-header">
          <h3>选择目标固件版本</h3>
          <p class="step-description">为选中的设备选择要更新的固件版本</p>
        </div>

        <!-- 选中设备摘要 -->
        <div class="selected-devices-summary">
          <h4>已选择设备 ({{ selectedDevices.length }}台)</h4>
          <div class="device-tags">
            <el-tag
              v-for="deviceId in selectedDevices"
              :key="deviceId"
              closable
              @close="handleRemoveDevice(deviceId)"
            >
              {{ getDeviceName(deviceId) }}
            </el-tag>
          </div>
        </div>

        <!-- 版本列表 -->
        <div class="version-list">
          <div
            v-for="version in compatibleVersions"
            :key="version.id"
            class="version-item"
            :class="{ 'selected': selectedVersion?.id === version.id }"
            @click="handleVersionSelect(version)"
          >
            <el-radio
              :model-value="selectedVersion?.id === version.id"
              @change="handleVersionSelect(version)"
            >
              <div class="version-info">
                <div class="version-header">
                  <span class="version-number">{{ version.version }}</span>
                  <el-tag
                    :type="getVersionStatusType(version.status)"
                    size="small"
                  >
                    {{ getVersionStatusText(version.status) }}
                  </el-tag>
                  <span class="version-date">{{ formatDate(version.releaseDate) }}</span>
                </div>
                <div class="version-description">{{ version.description }}</div>
                <div class="version-details">
                  <span class="file-size">大小: {{ formatFileSize(version.fileSize) }}</span>
                  <span class="device-models">
                    兼容: {{ version.deviceModel.join(', ') }}
                  </span>
                </div>
              </div>
            </el-radio>
          </div>
        </div>

        <!-- 版本详情 -->
        <div v-if="selectedVersion" class="version-details-panel">
          <el-collapse v-model="activeCollapse">
            <el-collapse-item title="更新日志" name="changelog">
              <ul class="changelog-list">
                <li v-for="change in selectedVersion.changelog" :key="change">
                  {{ change }}
                </li>
              </ul>
            </el-collapse-item>
            <el-collapse-item title="兼容性说明" name="compatibility">
              <ul class="compatibility-list">
                <li v-for="compat in selectedVersion.compatibility" :key="compat">
                  {{ compat }}
                </li>
              </ul>
            </el-collapse-item>
            <el-collapse-item v-if="selectedVersion.knownIssues.length" title="已知问题" name="issues">
              <ul class="issues-list">
                <li v-for="issue in selectedVersion.knownIssues" :key="issue">
                  <el-icon class="warning-icon"><Warning /></el-icon>
                  {{ issue }}
                </li>
              </ul>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>

      <!-- 步骤3: 确认更新 -->
      <div v-if="currentStep === 2" class="step-content">
        <div class="step-header">
          <h3>确认更新信息</h3>
          <p class="step-description">请确认更新信息，点击开始更新后将立即执行</p>
        </div>

        <!-- 更新摘要 -->
        <div class="update-summary">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="任务名称">
              <el-input
                v-model="updateTaskName"
                placeholder="请输入任务名称"
                style="width: 300px"
              />
            </el-descriptions-item>
            <el-descriptions-item label="目标版本">
              {{ selectedVersion?.version }}
            </el-descriptions-item>
            <el-descriptions-item label="更新设备数量">
              {{ selectedDevices.length }} 台
            </el-descriptions-item>
            <el-descriptions-item label="预计时间">
              {{ estimatedTime }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 设备更新列表 -->
        <div class="update-device-list">
          <h4>设备更新列表</h4>
          <el-table :data="selectedDeviceDetails" style="width: 100%">
            <el-table-column prop="deviceName" label="设备名称" width="150" />
            <el-table-column prop="deviceSerialNumber" label="序列号" width="120" />
            <el-table-column prop="deviceModel" label="型号" width="120" />
            <el-table-column prop="currentVersion" label="当前版本" width="100" />
            <el-table-column label="目标版本" width="100">
              <template #default>
                {{ selectedVersion?.version }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.isOnline ? 'success' : 'warning'" size="small">
                  {{ row.isOnline ? '就绪' : '离线' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="location" label="位置" />
          </el-table>
        </div>

        <!-- 更新选项 -->
        <div class="update-options">
          <h4>更新选项</h4>
          <el-checkbox v-model="updateOptions.forceUpdate">
            强制更新（忽略版本兼容性警告）
          </el-checkbox>
          <el-checkbox v-model="updateOptions.autoRetry">
            失败时自动重试（最多3次）
          </el-checkbox>
        </div>

        <!-- 风险提示 -->
        <el-alert
          title="更新风险提示"
          type="warning"
          :closable="false"
          show-icon
        >
          <ul>
            <li>固件更新过程中请勿断开设备电源或网络连接</li>
            <li>更新过程可能需要几分钟时间，期间设备将无法正常使用</li>
            <li>如果更新失败，设备可能需要手动恢复</li>
            <li>建议在设备使用较少的时间段进行更新</li>
          </ul>
        </el-alert>
      </div>
    </div>

    <!-- 对话框底部按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button v-if="currentStep > 0" @click="handlePrevStep">上一步</el-button>
        <el-button
          v-if="currentStep < 2"
          type="primary"
          :disabled="!canNextStep"
          @click="handleNextStep"
        >
          下一步
        </el-button>
        <el-button
          v-if="currentStep === 2"
          type="primary"
          :loading="creating"
          :disabled="!canStartUpdate"
          @click="handleStartUpdate"
        >
          开始更新
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Monitor, Download, Check, Warning, Connection } from '@element-plus/icons-vue'
import type {
  DeviceFirmwareStatus,
  FirmwareVersion,
  DeviceUpdateStatus,
  FirmwareStatus
} from '@/types/firmware'
import {
  DEVICE_UPDATE_STATUS_TEXT,
  FIRMWARE_STATUS_TEXT,
  formatFileSize
} from '@/types/firmware'
import { mockFirmwareAPI } from '@/api/mock/firmware'

// Props
interface Props {
  modelValue: boolean
  preSelectedDevices?: string[]
}

const props = withDefaults(defineProps<Props>(), {
  preSelectedDevices: () => []
})

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'update-started': [taskId: string]
}>()

// 响应式数据
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const currentStep = ref(0)
const creating = ref(false)

// 设备相关数据
const devices = ref<DeviceFirmwareStatus[]>([])
const selectedDevices = ref<string[]>([])
const deviceFilters = reactive({
  keyword: '',
  deviceModel: '',
  updateStatus: '',
  onlineOnly: false
})

// 版本相关数据
const firmwareVersions = ref<FirmwareVersion[]>([])
const selectedVersion = ref<FirmwareVersion | null>(null)
const activeCollapse = ref<string[]>([])

// 更新任务数据
const updateTaskName = ref('')
const updateOptions = reactive({
  forceUpdate: false,
  autoRetry: true
})

// 计算属性
const filteredDevices = computed(() => {
  let filtered = devices.value

  // 关键词搜索
  if (deviceFilters.keyword) {
    const keyword = deviceFilters.keyword.toLowerCase()
    filtered = filtered.filter(device =>
      device.deviceName.toLowerCase().includes(keyword) ||
      device.deviceSerialNumber.toLowerCase().includes(keyword)
    )
  }

  // 设备型号过滤
  if (deviceFilters.deviceModel) {
    filtered = filtered.filter(device => device.deviceModel === deviceFilters.deviceModel)
  }

  // 更新状态过滤
  if (deviceFilters.updateStatus) {
    filtered = filtered.filter(device => device.updateStatus === deviceFilters.updateStatus)
  }

  // 仅在线设备
  if (deviceFilters.onlineOnly) {
    filtered = filtered.filter(device => device.isOnline)
  }

  return filtered
})

const availableUpdateDevices = computed(() => {
  return filteredDevices.value.filter(device => device.canUpdate)
})

const selectAll = computed({
  get: () => {
    const availableDeviceIds = availableUpdateDevices.value.map(d => d.deviceId)
    return availableDeviceIds.length > 0 && 
           availableDeviceIds.every(id => selectedDevices.value.includes(id))
  },
  set: (value) => {
    if (value) {
      selectedDevices.value = [...new Set([
        ...selectedDevices.value,
        ...availableUpdateDevices.value.map(d => d.deviceId)
      ])]
    } else {
      const availableDeviceIds = availableUpdateDevices.value.map(d => d.deviceId)
      selectedDevices.value = selectedDevices.value.filter(id => !availableDeviceIds.includes(id))
    }
  }
})

const isIndeterminate = computed(() => {
  const availableDeviceIds = availableUpdateDevices.value.map(d => d.deviceId)
  const selectedAvailableDevices = selectedDevices.value.filter(id => availableDeviceIds.includes(id))
  return selectedAvailableDevices.length > 0 && selectedAvailableDevices.length < availableDeviceIds.length
})

const compatibleVersions = computed(() => {
  if (selectedDevices.value.length === 0) return []

  const selectedDeviceModels = selectedDevices.value
    .map(deviceId => devices.value.find(d => d.deviceId === deviceId)?.deviceModel)
    .filter(Boolean) as string[]

  return firmwareVersions.value.filter(version => {
    // 检查版本是否与所有选中设备兼容
    return selectedDeviceModels.every(model => version.deviceModel.includes(model))
  }).sort((a, b) => {
    // 按版本号降序排列，稳定版本优先
    if (a.status === 'stable' && b.status !== 'stable') return -1
    if (b.status === 'stable' && a.status !== 'stable') return 1
    return b.version.localeCompare(a.version)
  })
})

const selectedDeviceDetails = computed(() => {
  return selectedDevices.value
    .map(deviceId => devices.value.find(d => d.deviceId === deviceId))
    .filter(Boolean) as DeviceFirmwareStatus[]
})

const estimatedTime = computed(() => {
  const deviceCount = selectedDevices.value.length
  const avgTimePerDevice = 3 // 平均每台设备3分钟
  const totalMinutes = deviceCount * avgTimePerDevice
  
  if (totalMinutes < 60) {
    return `约 ${totalMinutes} 分钟`
  } else {
    const hours = Math.floor(totalMinutes / 60)
    const minutes = totalMinutes % 60
    return `约 ${hours} 小时 ${minutes} 分钟`
  }
})

const canNextStep = computed(() => {
  switch (currentStep.value) {
    case 0:
      return selectedDevices.value.length > 0
    case 1:
      return selectedVersion.value !== null
    default:
      return false
  }
})

const canStartUpdate = computed(() => {
  return updateTaskName.value.trim() !== '' && 
         selectedVersion.value !== null && 
         selectedDevices.value.length > 0
})

// 方法
const loadDevices = async () => {
  try {
    const response = await mockFirmwareAPI.getDeviceFirmwareStatus({
      pageSize: 1000 // 加载所有设备
    })
    devices.value = response.data.list
  } catch (error) {
    ElMessage.error('加载设备列表失败')
  }
}

const loadFirmwareVersions = async () => {
  try {
    const response = await mockFirmwareAPI.getFirmwareVersions({
      pageSize: 1000 // 加载所有版本
    })
    firmwareVersions.value = response.data.list
  } catch (error) {
    ElMessage.error('加载固件版本失败')
  }
}

const handleDeviceFilter = () => {
  // 过滤逻辑在计算属性中处理
}

const handleSelectAll = (checked: boolean) => {
  selectAll.value = checked
}

const handleDeviceSelect = (device: DeviceFirmwareStatus) => {
  if (!device.canUpdate) return
  
  const index = selectedDevices.value.indexOf(device.deviceId)
  if (index > -1) {
    selectedDevices.value.splice(index, 1)
  } else {
    selectedDevices.value.push(device.deviceId)
  }
}

const handleDeviceCheck = (deviceId: string, checked: boolean) => {
  if (checked) {
    if (!selectedDevices.value.includes(deviceId)) {
      selectedDevices.value.push(deviceId)
    }
  } else {
    const index = selectedDevices.value.indexOf(deviceId)
    if (index > -1) {
      selectedDevices.value.splice(index, 1)
    }
  }
}

const handleRemoveDevice = (deviceId: string) => {
  const index = selectedDevices.value.indexOf(deviceId)
  if (index > -1) {
    selectedDevices.value.splice(index, 1)
  }
}

const handleVersionSelect = (version: FirmwareVersion) => {
  selectedVersion.value = version
}

const handleNextStep = () => {
  if (canNextStep.value) {
    currentStep.value++
    
    // 进入版本选择步骤时，生成默认任务名称
    if (currentStep.value === 2 && !updateTaskName.value) {
      const timestamp = new Date().toLocaleString('zh-CN')
      updateTaskName.value = `批量更新到${selectedVersion.value?.version} - ${timestamp}`
    }
  }
}

const handlePrevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const handleStartUpdate = async () => {
  if (!canStartUpdate.value) return

  try {
    await ElMessageBox.confirm(
      `确定要开始更新 ${selectedDevices.value.length} 台设备到版本 ${selectedVersion.value?.version} 吗？`,
      '确认更新',
      {
        confirmButtonText: '开始更新',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    creating.value = true

    const response = await mockFirmwareAPI.createUpdateTask({
      name: updateTaskName.value,
      targetVersion: selectedVersion.value!.version,
      deviceIds: selectedDevices.value
    })

    ElMessage.success('更新任务创建成功')
    emit('update-started', response.data.id)
    handleClose()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('创建更新任务失败')
    }
  } finally {
    creating.value = false
  }
}

const handleClose = () => {
  visible.value = false
  // 重置状态
  currentStep.value = 0
  selectedDevices.value = []
  selectedVersion.value = null
  updateTaskName.value = ''
  Object.assign(deviceFilters, {
    keyword: '',
    deviceModel: '',
    updateStatus: '',
    onlineOnly: false
  })
  Object.assign(updateOptions, {
    forceUpdate: false,
    autoRetry: true
  })
}

// 工具方法
const getDeviceName = (deviceId: string) => {
  const device = devices.value.find(d => d.deviceId === deviceId)
  return device ? device.deviceName : deviceId
}

const getUpdateStatusType = (status: DeviceUpdateStatus) => {
  const typeMap = {
    'up-to-date': 'success',
    'update-available': 'warning',
    'updating': 'primary',
    'failed': 'danger'
  }
  return typeMap[status] || 'info'
}

const getUpdateStatusText = (status: DeviceUpdateStatus) => {
  return DEVICE_UPDATE_STATUS_TEXT[status] || status
}

const getVersionStatusType = (status: FirmwareStatus) => {
  const typeMap = {
    'stable': 'success',
    'testing': 'warning',
    'draft': 'info',
    'deprecated': 'danger'
  }
  return typeMap[status] || 'info'
}

const getVersionStatusText = (status: FirmwareStatus) => {
  return FIRMWARE_STATUS_TEXT[status] || status
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 监听器
watch(visible, (newVisible) => {
  if (newVisible) {
    loadDevices()
    loadFirmwareVersions()
    
    // 如果有预选设备，设置选中状态
    if (props.preSelectedDevices.length > 0) {
      selectedDevices.value = [...props.preSelectedDevices]
    }
  }
})

// 生命周期
onMounted(() => {
  if (visible.value) {
    loadDevices()
    loadFirmwareVersions()
  }
})
</script>

<style lang="scss" scoped>
.firmware-update-dialog {
  .update-steps {
    margin-bottom: 32px;
  }

  .step-content {
    min-height: 400px;

    .step-header {
      margin-bottom: 24px;
      text-align: center;

      h3 {
        margin: 0 0 8px 0;
        color: #303133;
        font-size: 18px;
        font-weight: 600;
      }

      .step-description {
        margin: 0;
        color: #909399;
        font-size: 14px;
      }
    }

    // 设备选择步骤样式
    .device-filters {
      margin-bottom: 20px;
      padding: 16px;
      background: #f8f9fa;
      border-radius: 6px;
    }

    .device-list {
      .list-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        padding: 12px 16px;
        background: #f0f2f5;
        border-radius: 6px;

        .device-count {
          color: #606266;
          font-size: 14px;
        }
      }

      .device-items {
        max-height: 300px;
        overflow-y: auto;
        border: 1px solid #e4e7ed;
        border-radius: 6px;

        .device-item {
          display: flex;
          align-items: center;
          padding: 16px;
          border-bottom: 1px solid #f0f0f0;
          cursor: pointer;
          transition: background-color 0.2s;

          &:hover:not(.disabled) {
            background-color: #f8f9fa;
          }

          &.selected {
            background-color: #e6f7ff;
            border-color: #409eff;
          }

          &.disabled {
            opacity: 0.6;
            cursor: not-allowed;
          }

          &:last-child {
            border-bottom: none;
          }

          .device-info {
            flex: 1;
            margin-left: 12px;

            .device-main {
              display: flex;
              align-items: center;
              gap: 12px;
              margin-bottom: 8px;

              .device-name {
                font-weight: 600;
                color: #303133;
              }

              .device-serial {
                color: #909399;
                font-size: 14px;
              }
            }

            .device-details {
              display: flex;
              align-items: center;
              gap: 16px;
              margin-bottom: 4px;
              font-size: 14px;
              color: #606266;

              .latest-version {
                color: #409eff;
              }
            }

            .device-restrictions {
              display: flex;
              align-items: center;
              gap: 4px;
              font-size: 12px;
              color: #e6a23c;

              .warning-icon {
                font-size: 14px;
              }
            }
          }

          .device-status {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
            gap: 8px;

            .signal-strength {
              display: flex;
              align-items: center;
              gap: 4px;
              font-size: 12px;
              color: #909399;
            }
          }
        }
      }
    }

    // 版本选择步骤样式
    .selected-devices-summary {
      margin-bottom: 24px;
      padding: 16px;
      background: #f8f9fa;
      border-radius: 6px;

      h4 {
        margin: 0 0 12px 0;
        color: #303133;
        font-size: 16px;
      }

      .device-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
      }
    }

    .version-list {
      max-height: 300px;
      overflow-y: auto;
      border: 1px solid #e4e7ed;
      border-radius: 6px;
      margin-bottom: 20px;

      .version-item {
        padding: 16px;
        border-bottom: 1px solid #f0f0f0;
        cursor: pointer;
        transition: background-color 0.2s;

        &:hover {
          background-color: #f8f9fa;
        }

        &.selected {
          background-color: #e6f7ff;
          border-color: #409eff;
        }

        &:last-child {
          border-bottom: none;
        }

        .version-info {
          margin-left: 24px;

          .version-header {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 8px;

            .version-number {
              font-weight: 600;
              font-size: 16px;
              color: #303133;
            }

            .version-date {
              color: #909399;
              font-size: 14px;
            }
          }

          .version-description {
            margin-bottom: 8px;
            color: #606266;
            font-size: 14px;
          }

          .version-details {
            display: flex;
            gap: 16px;
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }

    .version-details-panel {
      .changelog-list,
      .compatibility-list,
      .issues-list {
        margin: 0;
        padding-left: 20px;

        li {
          margin-bottom: 8px;
          line-height: 1.5;

          &:last-child {
            margin-bottom: 0;
          }
        }
      }

      .issues-list li {
        display: flex;
        align-items: flex-start;
        gap: 6px;
        color: #e6a23c;

        .warning-icon {
          margin-top: 2px;
          font-size: 14px;
        }
      }
    }

    // 确认更新步骤样式
    .update-summary {
      margin-bottom: 24px;
    }

    .update-device-list {
      margin-bottom: 24px;

      h4 {
        margin: 0 0 16px 0;
        color: #303133;
        font-size: 16px;
      }
    }

    .update-options {
      margin-bottom: 24px;

      h4 {
        margin: 0 0 16px 0;
        color: #303133;
        font-size: 16px;
      }

      .el-checkbox {
        display: block;
        margin-bottom: 12px;

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .firmware-update-dialog {
    .device-filters {
      .el-row {
        flex-direction: column;
        gap: 12px;
      }
    }

    .device-item {
      flex-direction: column;
      align-items: flex-start !important;
      gap: 12px;

      .device-info {
        margin-left: 0 !important;
        width: 100%;
      }

      .device-status {
        align-items: flex-start !important;
        width: 100%;
      }
    }

    .version-item .version-info .version-header {
      flex-direction: column;
      align-items: flex-start !important;
      gap: 8px;
    }
  }
}
</style>