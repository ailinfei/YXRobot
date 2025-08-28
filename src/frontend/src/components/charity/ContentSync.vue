<template>
  <div class="content-sync">
    <!-- 同步设置 -->
    <div class="sync-settings">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>同步设置</span>
            <el-button type="primary" @click="handleSaveSettings" :loading="saving">
              <el-icon><Setting /></el-icon>
              保存设置
            </el-button>
          </div>
        </template>

        <el-form :model="settings" label-width="120px">
          <el-form-item label="自动同步">
            <el-switch
              v-model="settings.autoSync"
              active-text="开启"
              inactive-text="关闭"
            />
            <div class="form-tip">开启后，发布的公益动态将自动同步到官网</div>
          </el-form-item>

          <el-form-item label="同步频率" v-if="settings.autoSync">
            <el-select v-model="settings.syncFrequency">
              <el-option label="实时同步" value="realtime" />
              <el-option label="每小时" value="hourly" />
              <el-option label="每天" value="daily" />
              <el-option label="手动同步" value="manual" />
            </el-select>
          </el-form-item>

          <el-form-item label="同步内容类型">
            <el-checkbox-group v-model="settings.syncTypes">
              <el-checkbox value="activity">活动报道</el-checkbox>
              <el-checkbox value="progress">项目进展</el-checkbox>
              <el-checkbox value="thanks">感谢信</el-checkbox>
              <el-checkbox value="achievement">成果展示</el-checkbox>
              <el-checkbox value="volunteer">志愿者故事</el-checkbox>
            </el-checkbox-group>
          </el-form-item>

          <el-form-item label="官网API地址">
            <el-input
              v-model="settings.websiteApiUrl"
              placeholder="请输入官网API地址"
            />
          </el-form-item>

          <el-form-item label="API密钥">
            <el-input
              v-model="settings.apiKey"
              type="password"
              placeholder="请输入API密钥"
              show-password
            />
          </el-form-item>

          <el-form-item label="同步状态通知">
            <el-checkbox v-model="settings.notifyOnSuccess">同步成功时通知</el-checkbox>
            <el-checkbox v-model="settings.notifyOnError">同步失败时通知</el-checkbox>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 同步状态 -->
    <div class="sync-status">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>同步状态</span>
            <div class="header-actions">
              <el-button @click="handleTestConnection" :loading="testing">
                <el-icon><Link /></el-icon>
                测试连接
              </el-button>
              <el-button type="primary" @click="handleManualSync" :loading="syncing">
                <el-icon><Refresh /></el-icon>
                手动同步
              </el-button>
            </div>
          </div>
        </template>

        <div class="status-overview">
          <div class="status-item">
            <div class="status-label">连接状态</div>
            <div class="status-value">
              <el-tag :type="connectionStatus === 'connected' ? 'success' : 'danger'">
                {{ connectionStatus === 'connected' ? '已连接' : '未连接' }}
              </el-tag>
            </div>
          </div>

          <div class="status-item">
            <div class="status-label">最后同步时间</div>
            <div class="status-value">{{ lastSyncTime || '从未同步' }}</div>
          </div>

          <div class="status-item">
            <div class="status-label">待同步内容</div>
            <div class="status-value">{{ pendingSyncCount }}条</div>
          </div>

          <div class="status-item">
            <div class="status-label">同步成功率</div>
            <div class="status-value">{{ syncSuccessRate }}%</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 同步记录 -->
    <div class="sync-records">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>同步记录</span>
            <div class="header-actions">
              <el-select
                v-model="recordFilter"
                placeholder="筛选状态"
                style="width: 120px;"
                @change="loadSyncRecords"
              >
                <el-option label="全部" value="" />
                <el-option label="成功" value="success" />
                <el-option label="失败" value="failed" />
                <el-option label="进行中" value="pending" />
              </el-select>
              <el-button @click="loadSyncRecords">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </div>
        </template>

        <el-table :data="syncRecords" :loading="recordsLoading" stripe>
          <el-table-column prop="id" label="记录ID" width="100" />
          
          <el-table-column prop="contentTitle" label="内容标题" min-width="200" />
          
          <el-table-column prop="contentType" label="内容类型" width="120">
            <template #default="{ row }">
              <el-tag size="small">{{ getContentTypeText(row.contentType) }}</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="同步状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="syncTime" label="同步时间" width="150">
            <template #default="{ row }">
              {{ formatDate(row.syncTime) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="duration" label="耗时" width="80">
            <template #default="{ row }">
              {{ row.duration }}ms
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button size="small" @click="handleViewRecord(row)">详情</el-button>
              <el-button
                v-if="row.status === 'failed'"
                size="small"
                type="primary"
                @click="handleRetrySync(row)"
              >
                重试
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="recordPagination.page"
            v-model:page-size="recordPagination.pageSize"
            :total="recordPagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleRecordSizeChange"
            @current-change="handleRecordCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 同步详情对话框 -->
    <el-dialog
      v-model="showRecordDetail"
      title="同步记录详情"
      width="60%"
    >
      <div v-if="selectedRecord" class="record-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="记录ID">{{ selectedRecord.id }}</el-descriptions-item>
          <el-descriptions-item label="内容标题">{{ selectedRecord.contentTitle }}</el-descriptions-item>
          <el-descriptions-item label="内容类型">{{ getContentTypeText(selectedRecord.contentType) }}</el-descriptions-item>
          <el-descriptions-item label="同步状态">
            <el-tag :type="getStatusTagType(selectedRecord.status)">
              {{ getStatusText(selectedRecord.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="同步时间">{{ formatDate(selectedRecord.syncTime) }}</el-descriptions-item>
          <el-descriptions-item label="耗时">{{ selectedRecord.duration }}ms</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section" v-if="selectedRecord.error">
          <h4>错误信息</h4>
          <el-alert :title="selectedRecord.error" type="error" show-icon />
        </div>

        <div class="detail-section" v-if="selectedRecord.response">
          <h4>响应信息</h4>
          <pre class="response-content">{{ JSON.stringify(selectedRecord.response, null, 2) }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Setting,
  Link,
  Refresh
} from '@element-plus/icons-vue'

// 同步记录接口
interface SyncRecord {
  id: string
  contentTitle: string
  contentType: string
  status: 'success' | 'failed' | 'pending'
  syncTime: string
  duration: number
  error?: string
  response?: any
}

// 响应式数据
const saving = ref(false)
const testing = ref(false)
const syncing = ref(false)
const recordsLoading = ref(false)
const showRecordDetail = ref(false)
const recordFilter = ref('')
const connectionStatus = ref<'connected' | 'disconnected'>('disconnected')
const lastSyncTime = ref('')
const pendingSyncCount = ref(0)
const syncSuccessRate = ref(0)

// 设置数据
const settings = reactive({
  autoSync: true,
  syncFrequency: 'realtime',
  syncTypes: ['activity', 'progress', 'achievement'],
  websiteApiUrl: 'https://api.example.com/charity',
  apiKey: '',
  notifyOnSuccess: true,
  notifyOnError: true
})

// 同步记录数据
const syncRecords = ref<SyncRecord[]>([])
const selectedRecord = ref<SyncRecord | null>(null)

// 分页数据
const recordPagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 方法
const getContentTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    activity: '活动报道',
    progress: '项目进展',
    thanks: '感谢信',
    achievement: '成果展示',
    volunteer: '志愿者故事'
  }
  return typeMap[type] || type
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    success: '成功',
    failed: '失败',
    pending: '进行中'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status: string) => {
  const typeMap: Record<string, string> = {
    success: 'success',
    failed: 'danger',
    pending: 'warning'
  }
  return typeMap[status] || ''
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const handleSaveSettings = async () => {
  saving.value = true
  try {
    // 模拟保存设置
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('设置保存成功')
  } catch (error) {
    ElMessage.error('保存设置失败')
  } finally {
    saving.value = false
  }
}

const handleTestConnection = async () => {
  testing.value = true
  try {
    // 模拟测试连接
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 随机成功或失败
    const success = Math.random() > 0.3
    if (success) {
      connectionStatus.value = 'connected'
      ElMessage.success('连接测试成功')
    } else {
      connectionStatus.value = 'disconnected'
      ElMessage.error('连接测试失败，请检查API地址和密钥')
    }
  } catch (error) {
    connectionStatus.value = 'disconnected'
    ElMessage.error('连接测试失败')
  } finally {
    testing.value = false
  }
}

const handleManualSync = async () => {
  if (connectionStatus.value !== 'connected') {
    ElMessage.warning('请先测试连接成功后再进行同步')
    return
  }

  syncing.value = true
  try {
    // 模拟手动同步
    await new Promise(resolve => setTimeout(resolve, 3000))
    
    ElMessage.success('手动同步完成')
    lastSyncTime.value = new Date().toLocaleString('zh-CN')
    pendingSyncCount.value = Math.max(0, pendingSyncCount.value - 2)
    
    // 刷新同步记录
    loadSyncRecords()
  } catch (error) {
    ElMessage.error('手动同步失败')
  } finally {
    syncing.value = false
  }
}

const loadSyncRecords = async () => {
  recordsLoading.value = true
  try {
    // 模拟加载同步记录
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const mockRecords: SyncRecord[] = [
      {
        id: 'sync-001',
        contentTitle: '山区小学汉字学习成果展示',
        contentType: 'achievement',
        status: 'success',
        syncTime: '2024-12-01 10:30:00',
        duration: 1250,
        response: { code: 200, message: 'success', id: 'web-001' }
      },
      {
        id: 'sync-002',
        contentTitle: '志愿者李老师的感人故事',
        contentType: 'volunteer',
        status: 'failed',
        syncTime: '2024-12-01 09:15:00',
        duration: 5000,
        error: 'API密钥验证失败'
      },
      {
        id: 'sync-003',
        contentTitle: '书法培训活动圆满结束',
        contentType: 'activity',
        status: 'success',
        syncTime: '2024-11-30 16:45:00',
        duration: 980,
        response: { code: 200, message: 'success', id: 'web-002' }
      }
    ]
    
    // 应用筛选
    let filteredRecords = mockRecords
    if (recordFilter.value) {
      filteredRecords = mockRecords.filter(record => record.status === recordFilter.value)
    }
    
    syncRecords.value = filteredRecords
    recordPagination.total = filteredRecords.length
    
    // 更新统计数据
    const successCount = mockRecords.filter(r => r.status === 'success').length
    syncSuccessRate.value = Math.round((successCount / mockRecords.length) * 100)
    
  } catch (error) {
    ElMessage.error('加载同步记录失败')
  } finally {
    recordsLoading.value = false
  }
}

const handleViewRecord = (record: SyncRecord) => {
  selectedRecord.value = record
  showRecordDetail.value = true
}

const handleRetrySync = async (record: SyncRecord) => {
  try {
    await ElMessageBox.confirm(
      `确定要重试同步"${record.contentTitle}"吗？`,
      '确认重试',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    ElMessage.success('重试同步已启动')
    loadSyncRecords()
  } catch (error) {
    // 用户取消操作
  }
}

const handleRecordSizeChange = (size: number) => {
  recordPagination.pageSize = size
  recordPagination.page = 1
  loadSyncRecords()
}

const handleRecordCurrentChange = (page: number) => {
  recordPagination.page = page
  loadSyncRecords()
}

const loadInitialData = async () => {
  // 模拟加载初始数据
  lastSyncTime.value = '2024-12-01 10:30:00'
  pendingSyncCount.value = 3
  syncSuccessRate.value = 85
  connectionStatus.value = 'connected'
  
  await loadSyncRecords()
}

// 生命周期
onMounted(() => {
  loadInitialData()
})

// 暴露方法给父组件
defineExpose({
  refresh: loadSyncRecords
})
</script>

<style lang="scss" scoped>
.content-sync {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      display: flex;
      gap: 8px;
      align-items: center;
    }
  }

  .form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }

  .status-overview {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;

    .status-item {
      padding: 16px;
      background: #f8f9fa;
      border-radius: 8px;
      text-align: center;

      .status-label {
        font-size: 14px;
        color: #606266;
        margin-bottom: 8px;
      }

      .status-value {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }

  .record-detail {
    .detail-section {
      margin-top: 20px;

      h4 {
        margin: 0 0 12px 0;
        color: #303133;
        font-size: 14px;
        font-weight: 600;
      }

      .response-content {
        background: #f8f9fa;
        padding: 12px;
        border-radius: 4px;
        font-size: 12px;
        line-height: 1.4;
        overflow-x: auto;
      }
    }
  }
}

@media (max-width: 768px) {
  .content-sync {
    .status-overview {
      grid-template-columns: 1fr;
      gap: 12px;
    }

    .card-header {
      flex-direction: column;
      gap: 12px;
      align-items: flex-start;

      .header-actions {
        width: 100%;
        justify-content: flex-end;
      }
    }
  }
}
</style>