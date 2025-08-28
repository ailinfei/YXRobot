<template>
  <el-dialog
    v-model="visible"
    :title="`任务详情 - ${task?.name || ''}`"
    width="900px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div v-if="task" class="task-detail-dialog">
      <!-- 任务基本信息 -->
      <div class="task-info-section">
        <h3>基本信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务名称">{{ task.name }}</el-descriptions-item>
          <el-descriptions-item label="目标版本">{{ task.targetVersion }}</el-descriptions-item>
          <el-descriptions-item label="任务状态">
            <el-tag :type="getTaskStatusTagType(task.status)" size="large">
              {{ getTaskStatusText(task.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="设备数量">{{ task.deviceIds.length }}台</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(task.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="创建者">{{ task.createdBy }}</el-descriptions-item>
          <el-descriptions-item v-if="task.startedAt" label="开始时间">
            {{ formatDateTime(task.startedAt) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="task.completedAt" label="完成时间">
            {{ formatDateTime(task.completedAt) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="task.scheduledTime" label="计划时间">
            {{ formatDateTime(task.scheduledTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="执行时长">{{ getTaskDuration(task) }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 任务进度统计 -->
      <div class="task-progress-section">
        <h3>进度统计</h3>
        <div class="progress-stats">
          <div class="stat-card">
            <div class="stat-number">{{ task.progress.total }}</div>
            <div class="stat-label">总设备数</div>
          </div>
          <div class="stat-card success">
            <div class="stat-number">{{ task.progress.completed }}</div>
            <div class="stat-label">成功</div>
          </div>
          <div class="stat-card processing">
            <div class="stat-number">{{ task.progress.inProgress }}</div>
            <div class="stat-label">进行中</div>
          </div>
          <div class="stat-card failed">
            <div class="stat-number">{{ task.progress.failed }}</div>
            <div class="stat-label">失败</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ getTaskSuccessRate(task) }}%</div>
            <div class="stat-label">成功率</div>
          </div>
        </div>

        <!-- 整体进度条 -->
        <div class="overall-progress">
          <div class="progress-label">整体进度</div>
          <el-progress
            :percentage="getTaskProgressPercentage(task)"
            :color="getProgressColor(task.status)"
            :stroke-width="12"
          />
        </div>
      </div>

      <!-- 设备详情列表 -->
      <div class="device-details-section">
        <div class="section-header">
          <h3>设备更新详情</h3>
          <div class="header-actions">
            <el-input
              v-model="deviceSearchKeyword"
              placeholder="搜索设备"
              :prefix-icon="Search"
              clearable
              style="width: 200px;"
            />
            <el-select v-model="deviceStatusFilter" placeholder="设备状态" clearable style="width: 150px;">
              <el-option label="全部状态" value="" />
              <el-option label="等待中" value="pending" />
              <el-option label="下载中" value="downloading" />
              <el-option label="安装中" value="installing" />
              <el-option label="重启中" value="rebooting" />
              <el-option label="完成" value="completed" />
              <el-option label="失败" value="failed" />
            </el-select>
          </div>
        </div>

        <el-table :data="filteredDevices" style="width: 100%" max-height="400">
          <el-table-column prop="deviceSerialNumber" label="设备序列号" width="150" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getDeviceStatusTagType(row.status)" size="small">
                {{ getDeviceStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="进度" width="150">
            <template #default="{ row }">
              <el-progress
                :percentage="row.progress"
                :color="getDeviceProgressColor(row.status)"
                :stroke-width="6"
              />
            </template>
          </el-table-column>
          <el-table-column prop="currentStep" label="当前步骤" />
          <el-table-column label="开始时间" width="150">
            <template #default="{ row }">
              {{ row.startTime ? formatDateTime(row.startTime) : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="结束时间" width="150">
            <template #default="{ row }">
              {{ row.endTime ? formatDateTime(row.endTime) : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="重试次数" width="100">
            <template #default="{ row }">
              {{ row.retryCount || 0 }}
            </template>
          </el-table-column>
          <el-table-column label="错误信息">
            <template #default="{ row }">
              <span v-if="row.errorMessage" class="error-message">{{ row.errorMessage }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button
                v-if="row.status === 'failed'"
                text
                type="primary"
                size="small"
                @click="retryDevice(row)"
              >
                重试
              </el-button>
              <el-button text type="info" size="small" @click="viewDeviceLogs(row)">
                日志
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 任务操作历史 -->
      <div v-if="taskHistory.length > 0" class="task-history-section">
        <h3>操作历史</h3>
        <el-timeline>
          <el-timeline-item
            v-for="(history, index) in taskHistory"
            :key="index"
            :timestamp="formatDateTime(history.timestamp)"
            :type="getHistoryType(history.action)"
          >
            <div class="history-content">
              <div class="history-action">{{ history.action }}</div>
              <div v-if="history.description" class="history-description">{{ history.description }}</div>
              <div class="history-operator">操作者: {{ history.operator }}</div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button v-if="task?.status === 'running'" type="warning" @click="pauseTask">
          暂停任务
        </el-button>
        <el-button v-if="task?.status === 'pending'" type="success" @click="startTask">
          开始任务
        </el-button>
        <el-button v-if="['pending', 'running'].includes(task?.status || '')" type="danger" @click="cancelTask">
          取消任务
        </el-button>
        <el-button v-if="task?.status === 'failed'" type="primary" @click="retryTask">
          重试失败设备
        </el-button>
        <el-button type="primary" @click="exportTaskReport">
          导出报告
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import type {
  FirmwareUpdateTask,
  DeviceUpdateProgress,
  UpdateTaskStatus,
  UpdateProgressStatus
} from '@/types/firmware'
import {
  UPDATE_TASK_STATUS_TEXT,
  UPDATE_PROGRESS_STATUS_TEXT,
  FirmwareUpdateTaskManager
} from '@/types/firmware'
import { mockFirmwareAPI } from '@/api/mock/firmware'

// Props
interface Props {
  modelValue: boolean
  task: FirmwareUpdateTask | null
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'refresh': []
}>()

// 响应式数据
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const deviceSearchKeyword = ref('')
const deviceStatusFilter = ref<UpdateProgressStatus | ''>('')

// 模拟任务历史数据
const taskHistory = ref([
  {
    timestamp: '2024-01-16T14:00:00Z',
    action: '任务创建',
    description: '创建固件更新任务',
    operator: 'admin'
  },
  {
    timestamp: '2024-01-16T14:05:00Z',
    action: '任务开始',
    description: '开始执行固件更新',
    operator: 'admin'
  }
])

// 计算属性
const filteredDevices = computed(() => {
  if (!props.task?.devices) return []

  let filtered = props.task.devices

  // 关键词搜索
  if (deviceSearchKeyword.value) {
    const keyword = deviceSearchKeyword.value.toLowerCase()
    filtered = filtered.filter(device =>
      device.deviceSerialNumber.toLowerCase().includes(keyword)
    )
  }

  // 状态过滤
  if (deviceStatusFilter.value) {
    filtered = filtered.filter(device => device.status === deviceStatusFilter.value)
  }

  return filtered
})

// 方法
const handleClose = () => {
  visible.value = false
  deviceSearchKeyword.value = ''
  deviceStatusFilter.value = ''
}

const getTaskStatusTagType = (status: UpdateTaskStatus) => {
  const typeMap = {
    pending: 'info',
    running: 'primary',
    completed: 'success',
    failed: 'danger',
    cancelled: 'warning'
  }
  return typeMap[status] || 'info'
}

const getTaskStatusText = (status: UpdateTaskStatus) => {
  return UPDATE_TASK_STATUS_TEXT[status] || status
}

const getDeviceStatusTagType = (status: UpdateProgressStatus) => {
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

const getTaskProgressPercentage = (task: FirmwareUpdateTask) => {
  return FirmwareUpdateTaskManager.getTaskCompletionPercentage(task)
}

const getTaskSuccessRate = (task: FirmwareUpdateTask) => {
  return FirmwareUpdateTaskManager.getTaskSuccessRate(task)
}

const getProgressColor = (status: UpdateTaskStatus) => {
  const colorMap = {
    pending: '#909399',
    running: '#409eff',
    completed: '#67c23a',
    failed: '#f56c6c',
    cancelled: '#e6a23c'
  }
  return colorMap[status] || '#909399'
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

const getHistoryType = (action: string) => {
  const typeMap: Record<string, 'primary' | 'success' | 'warning' | 'danger' | 'info'> = {
    '任务创建': 'primary',
    '任务开始': 'success',
    '任务暂停': 'warning',
    '任务取消': 'danger',
    '任务完成': 'success',
    '任务失败': 'danger'
  }
  return typeMap[action] || 'info'
}

const formatDateTime = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const getTaskDuration = (task: FirmwareUpdateTask) => {
  if (!task.startedAt) return '未开始'
  
  const startTime = new Date(task.startedAt).getTime()
  const endTime = task.completedAt ? new Date(task.completedAt).getTime() : Date.now()
  const duration = Math.floor((endTime - startTime) / 1000) // 秒
  
  if (duration < 60) return `${duration}秒`
  if (duration < 3600) return `${Math.floor(duration / 60)}分${duration % 60}秒`
  
  const hours = Math.floor(duration / 3600)
  const minutes = Math.floor((duration % 3600) / 60)
  const seconds = duration % 60
  
  return `${hours}小时${minutes}分${seconds}秒`
}

const startTask = async () => {
  if (!props.task) return

  try {
    await ElMessageBox.confirm('确定要开始执行此任务吗？', '确认开始', {
      confirmButtonText: '开始',
      cancelButtonText: '取消',
      type: 'info'
    })

    ElMessage.success('任务已开始执行')
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('开始任务失败')
    }
  }
}

const pauseTask = async () => {
  if (!props.task) return

  try {
    await ElMessageBox.confirm('确定要暂停此任务吗？', '确认暂停', {
      confirmButtonText: '暂停',
      cancelButtonText: '取消',
      type: 'warning'
    })

    ElMessage.success('任务已暂停')
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('暂停任务失败')
    }
  }
}

const cancelTask = async () => {
  if (!props.task) return

  try {
    await ElMessageBox.confirm('确定要取消此任务吗？取消后无法恢复。', '确认取消', {
      confirmButtonText: '取消任务',
      cancelButtonText: '保留任务',
      type: 'danger'
    })

    const response = await mockFirmwareAPI.cancelUpdateTask(props.task.id)
    if (response.data.success) {
      ElMessage.success('任务已取消')
      emit('refresh')
      handleClose()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消任务失败')
    }
  }
}

const retryTask = async () => {
  if (!props.task) return

  try {
    await ElMessageBox.confirm('确定要重试失败的设备吗？', '确认重试', {
      confirmButtonText: '重试',
      cancelButtonText: '取消',
      type: 'info'
    })

    ElMessage.success('失败设备已重新开始更新')
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重试任务失败')
    }
  }
}

const retryDevice = async (device: DeviceUpdateProgress) => {
  if (!props.task) return

  try {
    await ElMessageBox.confirm(`确定要重试设备 ${device.deviceSerialNumber} 吗？`, '确认重试设备', {
      confirmButtonText: '重试',
      cancelButtonText: '取消',
      type: 'info'
    })

    const response = await mockFirmwareAPI.retryDeviceUpdate(props.task.id, device.deviceId)
    if (response.data.success) {
      ElMessage.success('设备已重新开始更新')
      emit('refresh')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重试设备失败')
    }
  }
}

const viewDeviceLogs = (device: DeviceUpdateProgress) => {
  ElMessage.info(`查看设备 ${device.deviceSerialNumber} 的更新日志`)
  // 这里可以打开设备日志对话框
}

const exportTaskReport = () => {
  if (!props.task) return

  ElMessage.info('正在生成任务报告...')
  // 这里可以实现导出功能
  setTimeout(() => {
    ElMessage.success('任务报告已生成并下载')
  }, 2000)
}

// 监听任务变化，更新历史记录
watch(() => props.task, (newTask) => {
  if (newTask) {
    // 这里可以根据任务状态更新历史记录
    // 实际项目中应该从API获取历史记录
  }
})
</script>

<style lang="scss" scoped>
.task-detail-dialog {
  .task-info-section,
  .task-progress-section,
  .device-details-section,
  .task-history-section {
    margin-bottom: 32px;

    h3 {
      margin: 0 0 16px 0;
      color: #303133;
      font-size: 18px;
      font-weight: 600;
      border-bottom: 2px solid #409eff;
      padding-bottom: 8px;
    }
  }

  .task-progress-section {
    .progress-stats {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
      gap: 16px;
      margin-bottom: 24px;

      .stat-card {
        padding: 16px;
        background: #f8f9fa;
        border-radius: 8px;
        text-align: center;
        border: 2px solid transparent;

        &.success {
          border-color: #67c23a;
          background: #f0f9ff;
        }

        &.processing {
          border-color: #409eff;
          background: #ecf5ff;
        }

        &.failed {
          border-color: #f56c6c;
          background: #fef0f0;
        }

        .stat-number {
          font-size: 24px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #606266;
        }
      }
    }

    .overall-progress {
      .progress-label {
        margin-bottom: 8px;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }
  }

  .device-details-section {
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h3 {
        margin: 0;
        border: none;
        padding: 0;
      }

      .header-actions {
        display: flex;
        gap: 12px;
      }
    }

    .error-message {
      color: #f56c6c;
      font-size: 12px;
    }
  }

  .task-history-section {
    .history-content {
      .history-action {
        font-weight: 600;
        color: #303133;
        margin-bottom: 4px;
      }

      .history-description {
        color: #606266;
        font-size: 14px;
        margin-bottom: 4px;
      }

      .history-operator {
        color: #909399;
        font-size: 12px;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

// 响应式设计
@media (max-width: 768px) {
  .task-detail-dialog {
    .task-progress-section .progress-stats {
      grid-template-columns: repeat(2, 1fr);
    }

    .device-details-section .section-header {
      flex-direction: column;
      gap: 12px;

      .header-actions {
        width: 100%;
        
        .el-input,
        .el-select {
          width: 100% !important;
        }
      }
    }
  }
}
</style>