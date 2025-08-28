<!--
  设备日志对话框
  显示设备的运行日志和系统信息
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    :title="`设备日志 - ${device?.serialNumber || ''}`"
    width="80%"
    :before-close="handleClose"
    class="device-logs-dialog"
  >
    <div v-if="device" class="logs-content">
      <!-- 日志筛选 -->
      <div class="logs-filters">
        <div class="filters-left">
          <el-select v-model="levelFilter" placeholder="日志级别" clearable @change="handleFilter">
            <el-option label="全部级别" value="" />
            <el-option label="信息" value="info" />
            <el-option label="警告" value="warning" />
            <el-option label="错误" value="error" />
            <el-option label="调试" value="debug" />
          </el-select>
          <el-select v-model="categoryFilter" placeholder="日志分类" clearable @change="handleFilter">
            <el-option label="全部分类" value="" />
            <el-option label="系统" value="system" />
            <el-option label="用户" value="user" />
            <el-option label="网络" value="network" />
            <el-option label="硬件" value="hardware" />
            <el-option label="软件" value="software" />
          </el-select>
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="handleFilter"
          />
        </div>
        <div class="filters-right">
          <el-button @click="refreshLogs" :loading="logsLoading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button @click="exportLogs">
            <el-icon><Download /></el-icon>
            导出日志
          </el-button>
        </div>
      </div>

      <!-- 日志列表 -->
      <div class="logs-list" v-loading="logsLoading">
        <div
          v-for="log in logsList"
          :key="log.id"
          :class="['log-item', `log-${log.level}`]"
        >
          <div class="log-header">
            <div class="log-meta">
              <el-tag :type="getLogLevelTagType(log.level)" size="small">
                {{ getLogLevelText(log.level) }}
              </el-tag>
              <el-tag type="info" size="small" style="margin-left: 8px;">
                {{ getLogCategoryText(log.category) }}
              </el-tag>
              <span class="log-timestamp">{{ formatDateTime(log.timestamp) }}</span>
            </div>
          </div>
          <div class="log-message">
            {{ log.message }}
          </div>
          <div v-if="log.details" class="log-details">
            <el-collapse>
              <el-collapse-item title="详细信息" name="details">
                <div class="details-content">
                  <pre>{{ JSON.stringify(log.details, null, 2) }}</pre>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="logsList.length === 0 && !logsLoading" class="empty-logs">
          <el-empty description="暂无日志数据" />
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="exportLogs">导出日志</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Download } from '@element-plus/icons-vue'
import type { Device, DeviceLog } from '@/types/device'
import { mockDeviceAPI } from '@/api/mock/device'

// Props
interface Props {
  modelValue: boolean
  device?: Device | null
}

const props = withDefaults(defineProps<Props>(), {
  device: null
})

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

// 响应式数据
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const logsLoading = ref(false)
const levelFilter = ref('')
const categoryFilter = ref('')
const dateRange = ref<[string, string] | null>(null)
const currentPage = ref(1)
const pageSize = ref(50)
const total = ref(0)

// 日志数据
const logsList = ref<DeviceLog[]>([])

// 监听器
watch(() => props.modelValue, (visible) => {
  if (visible && props.device) {
    loadDeviceLogs()
  }
})

// 方法
const loadDeviceLogs = async () => {
  if (!props.device) return

  logsLoading.value = true
  try {
    const response = await mockDeviceAPI.getDeviceLogs(props.device.id, {
      page: currentPage.value,
      pageSize: pageSize.value,
      level: levelFilter.value || undefined
    })
    
    logsList.value = response.data.list
    total.value = response.data.total
  } catch (error) {
    console.error('加载设备日志失败:', error)
    ElMessage.error('加载设备日志失败')
  } finally {
    logsLoading.value = false
  }
}

const handleFilter = () => {
  currentPage.value = 1
  loadDeviceLogs()
}

const refreshLogs = () => {
  loadDeviceLogs()
}

const exportLogs = () => {
  ElMessage.success('日志导出功能开发中')
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadDeviceLogs()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadDeviceLogs()
}

const handleClose = () => {
  emit('update:modelValue', false)
}

// 工具方法
const getLogLevelTagType = (level: string) => {
  const types: Record<string, any> = {
    info: 'primary',
    warning: 'warning',
    error: 'danger',
    debug: 'info'
  }
  return types[level] || 'info'
}

const getLogLevelText = (level: string) => {
  const texts: Record<string, string> = {
    info: '信息',
    warning: '警告',
    error: '错误',
    debug: '调试'
  }
  return texts[level] || level
}

const getLogCategoryText = (category: string) => {
  const texts: Record<string, string> = {
    system: '系统',
    user: '用户',
    network: '网络',
    hardware: '硬件',
    software: '软件'
  }
  return texts[category] || category
}

const formatDateTime = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<style lang="scss" scoped>
.device-logs-dialog {
  .logs-content {
    .logs-filters {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      padding: 16px;
      background: #f8f9fa;
      border-radius: 8px;

      .filters-left {
        display: flex;
        gap: 12px;
        align-items: center;
        flex-wrap: wrap;
      }

      .filters-right {
        display: flex;
        gap: 12px;
        align-items: center;
      }
    }

    .logs-list {
      max-height: 60vh;
      overflow-y: auto;
      border: 1px solid #e8e8e8;
      border-radius: 8px;

      .log-item {
        padding: 16px;
        border-bottom: 1px solid #f0f0f0;
        transition: background-color 0.2s;

        &:hover {
          background-color: #f8f9fa;
        }

        &:last-child {
          border-bottom: none;
        }

        &.log-error {
          border-left: 4px solid #f56c6c;
        }

        &.log-warning {
          border-left: 4px solid #e6a23c;
        }

        &.log-info {
          border-left: 4px solid #409eff;
        }

        &.log-debug {
          border-left: 4px solid #909399;
        }

        .log-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .log-meta {
            display: flex;
            align-items: center;
            gap: 8px;

            .log-timestamp {
              color: #666;
              font-size: 12px;
              margin-left: 8px;
            }
          }
        }

        .log-message {
          color: #262626;
          line-height: 1.6;
          margin-bottom: 8px;
          font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
        }

        .log-details {
          .details-content {
            background: #f8f9fa;
            padding: 12px;
            border-radius: 4px;
            font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
            font-size: 12px;
            color: #666;
            overflow-x: auto;

            pre {
              margin: 0;
              white-space: pre-wrap;
              word-break: break-all;
            }
          }
        }
      }

      .empty-logs {
        padding: 40px;
        text-align: center;
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .device-logs-dialog {
    .logs-content {
      .logs-filters {
        flex-direction: column;
        gap: 16px;
        align-items: stretch;

        .filters-left {
          justify-content: center;
        }

        .filters-right {
          justify-content: center;
        }
      }
    }
  }
}
</style>