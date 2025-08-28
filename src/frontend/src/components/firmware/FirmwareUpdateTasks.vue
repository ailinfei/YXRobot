<!--
  固件更新任务管理组件
  功能：显示更新任务列表，监控更新进度，管理任务生命周期
-->
<template>
  <div class="firmware-update-tasks">
    <!-- 任务统计 -->
    <div class="task-stats">
      <div class="stat-item">
        <div class="stat-label">进行中任务</div>
        <div class="stat-value running">{{ runningTasks }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">今日完成</div>
        <div class="stat-value completed">{{ completedToday }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">失败任务</div>
        <div class="stat-value failed">{{ failedTasks }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">总任务数</div>
        <div class="stat-value total">{{ total }}</div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filters">
      <div class="filters-left">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索任务名称或版本"
          :prefix-icon="Search"
          clearable
          @input="handleSearch"
          style="width: 300px;"
        />
        <el-select v-model="statusFilter" placeholder="任务状态" clearable @change="handleFilter">
          <el-option label="全部状态" value="" />
          <el-option label="等待中" value="pending" />
          <el-option label="执行中" value="running" />
          <el-option label="已完成" value="completed" />
          <el-option label="失败" value="failed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleFilter"
          style="width: 240px;"
        />
      </div>
      <div class="filters-right">
        <el-button type="primary" @click="createNewTask">
          <el-icon><Plus /></el-icon>
          创建任务
        </el-button>
        <el-button @click="refreshTasks" :loading="refreshLoading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 任务列表 -->
    <div v-loading="tableLoading" class="task-list">
      <div
        v-for="task in taskList"
        :key="task.id"
        class="task-card"
        :class="{ 
          'task-running': task.status === 'running',
          'task-completed': task.status === 'completed',
          'task-failed': task.status === 'failed'
        }"
      >
        <div class="task-header">
          <div class="task-info">
            <h4 class="task-name">{{ task.name }}</h4>
            <div class="task-meta">
              <span class="task-version">目标版本: {{ task.targetVersion }}</span>
              <span class="task-devices">设备数量: {{ task.deviceIds.length }}台</span>
              <span class="task-time">创建时间: {{ formatDateTime(task.createdAt) }}</span>
              <span v-if="task.startedAt" class="task-started">开始时间: {{ formatDateTime(task.startedAt) }}</span>
              <span v-if="task.completedAt" class="task-completed">完成时间: {{ formatDateTime(task.completedAt) }}</span>
            </div>
          </div>
          <div class="task-status">
            <el-tag :type="getTaskStatusTagType(task.status)" size="large">
              {{ getTaskStatusText(task.status) }}
            </el-tag>
            <div v-if="task.status === 'running'" class="task-duration">
              运行时长: {{ getTaskDuration(task) }}
            </div>
          </div>
        </div>

        <div class="task-progress">
          <div class="progress-info">
            <span>进度: {{ task.progress.completed + task.progress.failed }}/{{ task.progress.total }}</span>
            <span>成功: {{ task.progress.completed }}</span>
            <span>失败: {{ task.progress.failed }}</span>
            <span v-if="task.progress.inProgress > 0">进行中: {{ task.progress.inProgress }}</span>
            <span class="success-rate">成功率: {{ getTaskSuccessRate(task) }}%</span>
          </div>
          <el-progress
            :percentage="getTaskProgressPercentage(task)"
            :color="getProgressColor(task.status)"
            :stroke-width="8"
          />
        </div>

        <!-- 设备更新详情 -->
        <div v-if="task.status === 'running' && task.devices.length > 0" class="device-progress">
          <div class="device-header">
            <h5>设备更新进度</h5>
            <el-button text size="small" @click="toggleDeviceDetails(task.id)">
              {{ expandedTasks.includes(task.id) ? '收起' : '展开' }}
            </el-button>
          </div>
          <div v-show="expandedTasks.includes(task.id)" class="device-list">
            <div
              v-for="device in task.devices"
              :key="device.deviceId"
              class="device-item"
            >
              <div class="device-info">
                <span class="device-serial">{{ device.deviceSerialNumber }}</span>
                <span class="device-step">{{ device.currentStep }}</span>
                <span v-if="device.errorMessage" class="device-error">{{ device.errorMessage }}</span>
              </div>
              <div class="device-status">
                <div class="device-progress-bar">
                  <el-progress
                    :percentage="device.progress"
                    :color="getDeviceProgressColor(device.status)"
                    :stroke-width="4"
                    :show-text="false"
                  />
                  <span class="progress-text">{{ device.progress }}%</span>
                </div>
                <el-tag :type="getDeviceStatusTagType(device.status)" size="small">
                  {{ getDeviceStatusText(device.status) }}
                </el-tag>
                <div v-if="device.retryCount > 0" class="retry-count">
                  重试: {{ device.retryCount }}次
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 任务摘要（已完成或失败的任务） -->
        <div v-if="['completed', 'failed', 'cancelled'].includes(task.status)" class="task-summary">
          <div class="summary-item">
            <span class="summary-label">执行时长:</span>
            <span class="summary-value">{{ getTaskDuration(task) }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">成功设备:</span>
            <span class="summary-value success">{{ task.progress.completed }}台</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">失败设备:</span>
            <span class="summary-value failed">{{ task.progress.failed }}台</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">成功率:</span>
            <span class="summary-value">{{ getTaskSuccessRate(task) }}%</span>
          </div>
        </div>

        <div class="task-actions">
          <el-button
            v-if="task.status === 'pending'"
            text
            type="success"
            size="small"
            @click="startTask(task)"
            :loading="task.id === operatingTaskId"
          >
            开始任务
          </el-button>
          <el-button
            v-if="['pending', 'running'].includes(task.status)"
            text
            type="danger"
            size="small"
            @click="cancelTask(task)"
            :loading="task.id === operatingTaskId"
          >
            取消任务
          </el-button>
          <el-button text type="primary" size="small" @click="viewTaskDetail(task)">
            查看详情
          </el-button>
          <el-button
            v-if="task.status === 'failed'"
            text
            type="primary"
            size="small"
            @click="retryTask(task)"
            :loading="task.id === operatingTaskId"
          >
            重试失败设备
          </el-button>
          <el-button
            v-if="['completed', 'failed', 'cancelled'].includes(task.status)"
            text
            type="info"
            size="small"
            @click="deleteTask(task)"
            :loading="task.id === operatingTaskId"
          >
            删除任务
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="taskList.length === 0 && !tableLoading" class="empty-state">
        <el-empty description="暂无更新任务">
          <el-button type="primary" @click="createNewTask">创建第一个任务</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 任务详情对话框 -->
    <TaskDetailDialog
      v-model="taskDetailVisible"
      :task="selectedTask"
      @refresh="loadTasks"
    />

    <!-- 固件更新对话框 -->
    <FirmwareUpdateDialog
      v-model="updateDialogVisible"
      @update-started="handleUpdateStarted"
    />
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Refresh } from '@element-plus/icons-vue'
import type {
  FirmwareUpdateTask,
  UpdateTaskStatus,
  UpdateProgressStatus,
  UpdateTaskQueryParams
} from '@/types/firmware'
import {
  UPDATE_TASK_STATUS_TEXT,
  UPDATE_PROGRESS_STATUS_TEXT,
  FirmwareUpdateTaskManager
} from '@/types/firmware'
import { mockFirmwareAPI } from '@/api/mock/firmware'
import FirmwareUpdateDialog from './FirmwareUpdateDialog.vue'
import TaskDetailDialog from './TaskDetailDialog.vue'

// 响应式数据
const tableLoading = ref(false)
const refreshLoading = ref(false)
const operatingTaskId = ref<string>('')

// 搜索和筛选
const searchKeyword = ref('')
const statusFilter = ref<UpdateTaskStatus | ''>('')
const dateRange = ref<[string, string] | null>(null)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 任务数据
const taskList = ref<FirmwareUpdateTask[]>([])
const expandedTasks = ref<string[]>([])

// 对话框状态
const updateDialogVisible = ref(false)
const taskDetailVisible = ref(false)
const selectedTask = ref<FirmwareUpdateTask | null>(null)

// 定时器
let refreshTimer: NodeJS.Timeout | null = null

// 计算属性
const runningTasks = computed(() => {
  return taskList.value.filter(task => task.status === 'running').length
})

const completedToday = computed(() => {
  const today = new Date().toDateString()
  return taskList.value.filter(task => 
    task.status === 'completed' && 
    task.completedAt && 
    new Date(task.completedAt).toDateString() === today
  ).length
})

const failedTasks = computed(() => {
  return taskList.value.filter(task => task.status === 'failed').length
})

// 方法
const loadTasks = async () => {
  tableLoading.value = true
  try {
    const params: UpdateTaskQueryParams = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || undefined,
      status: statusFilter.value || undefined,
      dateRange: dateRange.value || undefined
    }

    const response = await mockFirmwareAPI.getUpdateTasks(params)
    taskList.value = response.data.list
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('加载任务列表失败')
  } finally {
    tableLoading.value = false
  }
}

const refreshTasks = async () => {
  refreshLoading.value = true
  try {
    await loadTasks()
    ElMessage.success('任务列表已刷新')
  } catch (error) {
    ElMessage.error('刷新任务列表失败')
  } finally {
    refreshLoading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadTasks()
}

const handleFilter = () => {
  currentPage.value = 1
  loadTasks()
}

const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize
  currentPage.value = 1
  loadTasks()
}

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage
  loadTasks()
}

const createNewTask = () => {
  updateDialogVisible.value = true
}

const handleUpdateStarted = (taskId: string) => {
  ElMessage.success('更新任务已创建')
  loadTasks()
}

const startTask = async (task: FirmwareUpdateTask) => {
  const canStart = FirmwareUpdateTaskManager.canStartTask(task)
  if (!canStart.canStart) {
    ElMessage.warning(canStart.reason)
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要开始执行任务 "${task.name}" 吗？`,
      '确认开始任务',
      {
        confirmButtonText: '开始',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    operatingTaskId.value = task.id
    // 这里应该调用实际的启动任务API
    // await mockFirmwareAPI.startUpdateTask(task.id)
    
    // 模拟启动任务
    const taskIndex = taskList.value.findIndex(t => t.id === task.id)
    if (taskIndex > -1) {
      taskList.value[taskIndex] = FirmwareUpdateTaskManager.updateTaskStatus(
        task, 
        'running',
        { startedAt: new Date().toISOString() }
      )
    }

    ElMessage.success('任务已开始执行')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('启动任务失败')
    }
  } finally {
    operatingTaskId.value = ''
  }
}

const cancelTask = async (task: FirmwareUpdateTask) => {
  const canCancel = FirmwareUpdateTaskManager.canCancelTask(task)
  if (!canCancel.canCancel) {
    ElMessage.warning(canCancel.reason)
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要取消任务 "${task.name}" 吗？取消后无法恢复。`,
      '确认取消任务',
      {
        confirmButtonText: '取消任务',
        cancelButtonText: '保留任务',
        type: 'warning'
      }
    )

    operatingTaskId.value = task.id
    const response = await mockFirmwareAPI.cancelUpdateTask(task.id)
    
    if (response.data.success) {
      const taskIndex = taskList.value.findIndex(t => t.id === task.id)
      if (taskIndex > -1) {
        taskList.value[taskIndex] = FirmwareUpdateTaskManager.cancelTask(task, 'admin')
      }
      ElMessage.success('任务已取消')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消任务失败')
    }
  } finally {
    operatingTaskId.value = ''
  }
}

const retryTask = async (task: FirmwareUpdateTask) => {
  try {
    await ElMessageBox.confirm(
      `确定要重试任务 "${task.name}" 中失败的设备吗？`,
      '确认重试任务',
      {
        confirmButtonText: '重试',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    operatingTaskId.value = task.id
    // 这里应该调用实际的重试任务API
    // await mockFirmwareAPI.retryUpdateTask(task.id)
    
    // 模拟重试任务
    const taskIndex = taskList.value.findIndex(t => t.id === task.id)
    if (taskIndex > -1) {
      taskList.value[taskIndex] = FirmwareUpdateTaskManager.retryFailedDevices(task)
    }

    ElMessage.success('失败设备已重新开始更新')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重试任务失败')
    }
  } finally {
    operatingTaskId.value = ''
  }
}

const deleteTask = async (task: FirmwareUpdateTask) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除任务 "${task.name}" 吗？删除后无法恢复。`,
      '确认删除任务',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'danger'
      }
    )

    operatingTaskId.value = task.id
    // 这里应该调用实际的删除任务API
    // await mockFirmwareAPI.deleteUpdateTask(task.id)
    
    // 模拟删除任务
    const taskIndex = taskList.value.findIndex(t => t.id === task.id)
    if (taskIndex > -1) {
      taskList.value.splice(taskIndex, 1)
      total.value--
    }

    ElMessage.success('任务已删除')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除任务失败')
    }
  } finally {
    operatingTaskId.value = ''
  }
}

const viewTaskDetail = (task: FirmwareUpdateTask) => {
  selectedTask.value = task
  taskDetailVisible.value = true
}

const toggleDeviceDetails = (taskId: string) => {
  const index = expandedTasks.value.indexOf(taskId)
  if (index > -1) {
    expandedTasks.value.splice(index, 1)
  } else {
    expandedTasks.value.push(taskId)
  }
}

// 工具方法
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

// 自动刷新运行中的任务
const startAutoRefresh = () => {
  refreshTimer = setInterval(() => {
    const hasRunningTasks = taskList.value.some(task => task.status === 'running')
    if (hasRunningTasks) {
      loadTasks()
    }
  }, 5000) // 每5秒刷新一次
}

const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

// 生命周期
onMounted(() => {
  loadTasks()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style lang="scss" scoped>
.firmware-update-tasks {
  padding: 24px;

  .task-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    margin-bottom: 24px;

    .stat-item {
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      text-align: center;

      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }

      .stat-value {
        font-size: 28px;
        font-weight: 600;
        
        &.running {
          color: #409eff;
        }
        
        &.completed {
          color: #67c23a;
        }
        
        &.failed {
          color: #f56c6c;
        }
        
        &.total {
          color: #303133;
        }
      }
    }
  }

  .search-filters {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .filters-left {
      display: flex;
      gap: 16px;
      align-items: center;
    }

    .filters-right {
      display: flex;
      gap: 12px;
    }
  }

  .task-list {
    min-height: 400px;

    .task-card {
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      margin-bottom: 16px;
      padding: 20px;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      &.task-running {
        border-left: 4px solid #409eff;
      }

      &.task-completed {
        border-left: 4px solid #67c23a;
      }

      &.task-failed {
        border-left: 4px solid #f56c6c;
      }

      .task-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;

        .task-info {
          flex: 1;

          .task-name {
            margin: 0 0 8px 0;
            color: #303133;
            font-size: 18px;
            font-weight: 600;
          }

          .task-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 16px;
            font-size: 14px;
            color: #606266;

            span {
              display: flex;
              align-items: center;
            }
          }
        }

        .task-status {
          display: flex;
          flex-direction: column;
          align-items: flex-end;
          gap: 8px;

          .task-duration {
            font-size: 12px;
            color: #909399;
          }
        }
      }

      .task-progress {
        margin-bottom: 16px;

        .progress-info {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
          font-size: 14px;
          color: #606266;

          .success-rate {
            font-weight: 600;
            color: #409eff;
          }
        }
      }

      .device-progress {
        margin-bottom: 16px;
        padding: 16px;
        background: #f8f9fa;
        border-radius: 6px;

        .device-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;

          h5 {
            margin: 0;
            color: #303133;
            font-size: 16px;
          }
        }

        .device-list {
          .device-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px;
            background: white;
            border-radius: 4px;
            margin-bottom: 8px;

            &:last-child {
              margin-bottom: 0;
            }

            .device-info {
              flex: 1;

              .device-serial {
                font-weight: 600;
                color: #303133;
                margin-right: 12px;
              }

              .device-step {
                color: #606266;
                font-size: 14px;
                margin-right: 12px;
              }

              .device-error {
                color: #f56c6c;
                font-size: 12px;
              }
            }

            .device-status {
              display: flex;
              align-items: center;
              gap: 12px;

              .device-progress-bar {
                display: flex;
                align-items: center;
                gap: 8px;
                width: 120px;

                .progress-text {
                  font-size: 12px;
                  color: #606266;
                  min-width: 35px;
                }
              }

              .retry-count {
                font-size: 12px;
                color: #e6a23c;
              }
            }
          }
        }
      }

      .task-summary {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
        gap: 16px;
        margin-bottom: 16px;
        padding: 16px;
        background: #f8f9fa;
        border-radius: 6px;

        .summary-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          text-align: center;

          .summary-label {
            font-size: 12px;
            color: #909399;
            margin-bottom: 4px;
          }

          .summary-value {
            font-size: 16px;
            font-weight: 600;
            color: #303133;

            &.success {
              color: #67c23a;
            }

            &.failed {
              color: #f56c6c;
            }
          }
        }
      }

      .task-actions {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
      }
    }

    .empty-state {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 300px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 24px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .firmware-update-tasks {
    padding: 16px;

    .task-stats {
      grid-template-columns: repeat(2, 1fr);
    }

    .search-filters {
      flex-direction: column;
      gap: 16px;

      .filters-left {
        flex-direction: column;
        width: 100%;
        
        .el-input,
        .el-select,
        .el-date-picker {
          width: 100% !important;
        }
      }

      .filters-right {
        width: 100%;
        justify-content: center;
      }
    }

    .task-card {
      .task-header {
        flex-direction: column;
        gap: 12px;

        .task-status {
          align-items: flex-start;
        }
      }

      .task-progress .progress-info {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
      }

      .device-progress .device-item {
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;

        .device-status {
          width: 100%;
          justify-content: space-between;
        }
      }

      .task-summary {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  }
}
</style>