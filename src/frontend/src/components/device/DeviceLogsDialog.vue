<!--
  设备日志对话框组件
  功能：显示设备运行日志
-->
<template>
  <el-dialog
    v-model="visible"
    :title="`设备日志 - ${device?.serialNumber || ''}`"
    width="1000px"
    :before-close="handleClose"
  >
    <!-- 日志筛选 -->
    <div class="log-filters">
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
      
      <el-button @click="refreshLogs" :loading="loading">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <!-- 日志列表 -->
    <div class="log-content">
      <div v-loading="loading" class="log-list">
        <div 
          v-for="log in logList" 
          :key="log.id"
          class="log-item"
          :class="getLevelClass(log.level)"
        >
          <div class="log-header">
            <div class="log-meta">
              <el-tag :type="getLevelTagType(log.level)" size="small">
                {{ getLevelText(log.level) }}
              </el-tag>
              <el-tag :type="getCategoryTagType(log.category)" size="small">
                {{ getCategoryText(log.category) }}
              </el-tag>
              <span class="log-time">{{ formatDateTime(log.timestamp) }}</span>
            </div>
            <el-button 
              v-if="log.details"
              text 
              size="small" 
              @click="toggleDetails(log.id)"
            >
              {{ expandedLogs.has(log.id) ? '收起' : '详情' }}
            </el-button>
          </div>
          
          <div class="log-message">
            {{ log.message }}
          </div>
          
          <div 
            v-if="log.details && expandedLogs.has(log.id)" 
            class="log-details"
          >
            <pre>{{ formatLogDetails(log.details) }}</pre>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-if="!loading && logList.length === 0" class="empty-state">
          <el-empty description="暂无日志数据" />
        </div>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="exportLogs">
          <el-icon><Download /></el-icon>
          导出日志
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { Refresh, Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { ManagedDevice, ManagedDeviceLog, LogLevel, LogCategory } from '@/types/managedDevice'
import { managedDeviceAPI } from '@/api/managedDevice'

interface Props {
  modelValue: boolean
  device: ManagedDevice | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const loading = ref(false)
const logList = ref<ManagedDeviceLog[]>([])
const expandedLogs = ref(new Set<string>())
const currentPage = ref(1)
const pageSize = ref(50)
const total = ref(0)

// 筛选条件
const levelFilter = ref('')
const categoryFilter = ref('')
const dateRange = ref<[string, string] | null>(null)

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 监听对话框显示
watch(visible, (show) => {
  if (show && props.device) {
    resetFilters()
    loadLogs()
  }
})

// 重置筛选条件
const resetFilters = () => {
  levelFilter.value = ''
  categoryFilter.value = ''
  dateRange.value = null
  currentPage.value = 1
  expandedLogs.value.clear()
}

// 加载日志数据
const loadLogs = async () => {
  if (!props.device) return
  
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    
    if (levelFilter.value) {
      params.level = levelFilter.value
    }
    
    if (categoryFilter.value) {
      params.category = categoryFilter.value
    }
    
    if (dateRange.value) {
      params.dateRange = dateRange.value
    }
    
    const response = await managedDeviceAPI.getDeviceLogs(props.device.id, params)
    logList.value = response.list || []
    total.value = response.total || 0
  } catch (error: any) {
    console.error('加载日志失败:', error)
    ElMessage.error(error?.message || '加载日志失败')
    // 设置默认值
    logList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 筛选处理
const handleFilter = () => {
  currentPage.value = 1
  loadLogs()
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadLogs()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadLogs()
}

// 刷新日志
const refreshLogs = () => {
  loadLogs()
}

// 切换详情显示
const toggleDetails = (logId: string) => {
  if (expandedLogs.value.has(logId)) {
    expandedLogs.value.delete(logId)
  } else {
    expandedLogs.value.add(logId)
  }
}

// 导出日志
const exportLogs = () => {
  ElMessage.info('日志导出功能开发中')
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
}

// 工具方法
const getLevelClass = (level: string) => {
  return `log-level-${level}`
}

const getLevelTagType = (level: string) => {
  const types: Record<string, any> = {
    info: 'primary',
    warning: 'warning',
    error: 'danger',
    debug: 'info'
  }
  return types[level] || 'info'
}

const getLevelText = (level: string) => {
  const texts: Record<string, string> = {
    info: '信息',
    warning: '警告',
    error: '错误',
    debug: '调试'
  }
  return texts[level] || level
}

const getCategoryTagType = (category: string) => {
  const types: Record<string, any> = {
    system: 'success',
    user: 'primary',
    network: 'warning',
    hardware: 'danger',
    software: 'info'
  }
  return types[category] || 'info'
}

const getCategoryText = (category: string) => {
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

const formatLogDetails = (details: Record<string, any>) => {
  return JSON.stringify(details, null, 2)
}
</script>

<style lang="scss" scoped>
.log-filters {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
  
  .el-select {
    width: 120px;
  }
  
  .el-date-editor {
    width: 300px;
  }
}

.log-content {
  .log-list {
    max-height: 500px;
    overflow-y: auto;
    border: 1px solid #f0f0f0;
    border-radius: 6px;
    
    .log-item {
      padding: 12px 16px;
      border-bottom: 1px solid #f0f0f0;
      transition: background-color 0.2s;
      
      &:last-child {
        border-bottom: none;
      }
      
      &:hover {
        background-color: #fafafa;
      }
      
      &.log-level-error {
        border-left: 4px solid #f56c6c;
      }
      
      &.log-level-warning {
        border-left: 4px solid #e6a23c;
      }
      
      &.log-level-info {
        border-left: 4px solid #409eff;
      }
      
      &.log-level-debug {
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
          
          .log-time {
            font-size: 12px;
            color: #8c8c8c;
            margin-left: 8px;
          }
        }
      }
      
      .log-message {
        color: #262626;
        font-size: 14px;
        line-height: 1.5;
        margin-bottom: 8px;
      }
      
      .log-details {
        background: #f8f9fa;
        border-radius: 4px;
        padding: 12px;
        margin-top: 8px;
        
        pre {
          margin: 0;
          font-size: 12px;
          color: #666;
          white-space: pre-wrap;
          word-break: break-all;
        }
      }
    }
    
    .empty-state {
      padding: 40px;
      text-align: center;
    }
  }
  
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>