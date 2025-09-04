<template>
  <div class="customer-service-records">
    <div class="service-header">
      <h4>服务记录</h4>
      <div class="header-actions">
        <el-button type="primary" size="small" @click="handleAddRecord">
          <el-icon><Plus /></el-icon>
          添加记录
        </el-button>
        <el-select v-model="typeFilter" placeholder="记录类型" size="small" @change="handleFilter">
          <el-option label="全部类型" value="" />
          <el-option label="维修记录" value="maintenance" />
          <el-option label="升级记录" value="upgrade" />
          <el-option label="咨询记录" value="consultation" />
          <el-option label="投诉处理" value="complaint" />
        </el-select>
        <el-button size="small" @click="refreshRecords">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 服务统计 -->
    <div class="service-stats">
      <div class="stat-item">
        <div class="stat-value">{{ serviceStats.total }}</div>
        <div class="stat-label">服务总数</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ serviceStats.maintenance }}</div>
        <div class="stat-label">维修记录</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ serviceStats.upgrade }}</div>
        <div class="stat-label">升级记录</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ serviceStats.consultation }}</div>
        <div class="stat-label">咨询记录</div>
      </div>
    </div>

    <!-- 服务记录时间线 -->
    <div class="service-timeline">
      <el-timeline>
        <el-timeline-item
          v-for="record in filteredRecords"
          :key="record.id"
          :timestamp="formatDate(record.createdAt)"
          placement="top"
          :type="getTimelineType(record.type)"
          :icon="getTimelineIcon(record.type)"
        >
          <div class="timeline-content">
            <div class="record-header">
              <div class="record-title">
                <el-tag :type="getRecordTagType(record.type)" size="small">
                  {{ getRecordTypeText(record.type) }}
                </el-tag>
                <span class="record-subject">{{ record.subject }}</span>
              </div>
              <div class="record-actions">
                <el-button text type="primary" size="small" @click="viewRecordDetail(record)">
                  查看详情
                </el-button>
                <el-button text type="primary" size="small" @click="editRecord(record)">
                  编辑
                </el-button>
              </div>
            </div>
            
            <div class="record-content">
              <p class="record-description">{{ record.description }}</p>
              
              <div class="record-meta">
                <div class="meta-item" v-if="record.deviceId">
                  <el-icon><Monitor /></el-icon>
                  <span>设备: {{ record.deviceId }}</span>
                </div>
                <div class="meta-item" v-if="record.serviceStaff">
                  <el-icon><User /></el-icon>
                  <span>服务人员: {{ record.serviceStaff }}</span>
                </div>
                <div class="meta-item" v-if="record.cost">
                  <el-icon><Money /></el-icon>
                  <span>费用: ¥{{ record.cost.toLocaleString() }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Clock /></el-icon>
                  <span>状态: </span>
                  <el-tag :type="getStatusTagType(record.status)" size="small">
                    {{ getStatusText(record.status) }}
                  </el-tag>
                </div>
              </div>
              
              <div class="record-attachments" v-if="record.attachments?.length">
                <div class="attachments-label">附件:</div>
                <div class="attachments-list">
                  <el-tag
                    v-for="attachment in record.attachments"
                    :key="attachment.id"
                    size="small"
                    type="info"
                    class="attachment-tag"
                    @click="viewAttachment(attachment)"
                  >
                    <el-icon><Document /></el-icon>
                    {{ attachment.name }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
      
      <div v-if="filteredRecords.length === 0" class="empty-records">
        <el-empty description="暂无服务记录" />
      </div>
    </div>

    <!-- 添加服务记录对话框 -->
    <el-dialog
      v-model="addRecordDialogVisible"
      title="添加服务记录"
      width="700px"
    >
      <el-form
        ref="recordFormRef"
        :model="recordForm"
        :rules="recordFormRules"
        label-width="100px"
      >
        <el-form-item label="记录类型" prop="type">
          <el-select v-model="recordForm.type" placeholder="请选择记录类型">
            <el-option label="维修记录" value="maintenance" />
            <el-option label="升级记录" value="upgrade" />
            <el-option label="咨询记录" value="consultation" />
            <el-option label="投诉处理" value="complaint" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="服务主题" prop="subject">
          <el-input
            v-model="recordForm.subject"
            placeholder="请输入服务主题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="相关设备" prop="deviceId">
          <el-select v-model="recordForm.deviceId" placeholder="请选择相关设备" clearable>
            <el-option
              v-for="device in customerDevices"
              :key="device.id"
              :label="`${device.serialNumber} (${device.model})`"
              :value="device.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="服务人员" prop="serviceStaff">
          <el-input
            v-model="recordForm.serviceStaff"
            placeholder="请输入服务人员姓名"
            maxlength="50"
          />
        </el-form-item>
        
        <el-form-item label="服务费用" prop="cost">
          <el-input-number
            v-model="recordForm.cost"
            :min="0"
            :precision="2"
            placeholder="请输入服务费用"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="服务状态" prop="status">
          <el-select v-model="recordForm.status" placeholder="请选择服务状态">
            <el-option label="进行中" value="in_progress" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="服务描述" prop="description">
          <el-input
            v-model="recordForm.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述服务内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="附件上传" prop="attachments">
          <el-upload
            v-model:file-list="recordForm.attachments"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :before-upload="beforeUpload"
            multiple
            :limit="5"
          >
            <el-button size="small">
              <el-icon><Upload /></el-icon>
              上传附件
            </el-button>
            <template #tip>
              <div class="upload-tip">支持图片、文档格式，单个文件不超过10MB，最多5个文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addRecordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddRecordSubmit" :loading="addRecordLoading">
            添加
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 服务记录详情对话框 -->
    <ServiceRecordDetailDialog
      v-model="recordDetailDialogVisible"
      :record="selectedRecord"
      @edit="handleEditRecord"
      @complete="handleCompleteRecord"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import {
  Plus,
  Refresh,
  Monitor,
  User,
  Money,
  Clock,
  Document,
  Upload,
  Tools,
  ChatDotRound,
  Warning
} from '@element-plus/icons-vue'
import { customerRelationApi } from '@/api/customer'
import type { CustomerServiceRecord, CustomerDevice } from '@/types/customer'
import ServiceRecordDetailDialog from './ServiceRecordDetailDialog.vue'

// Props
interface Props {
  customerId: string
  records: CustomerServiceRecord[]
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  'refresh': []
}>()

// 响应式数据
const loading = ref(false)
const recordList = ref<CustomerServiceRecord[]>([])
const customerDevices = ref<CustomerDevice[]>([])
const typeFilter = ref('')
const addRecordDialogVisible = ref(false)
const addRecordLoading = ref(false)
const recordFormRef = ref<FormInstance>()

// 服务记录表单
const recordForm = reactive({
  type: '',
  subject: '',
  deviceId: '',
  serviceStaff: '',
  cost: 0,
  status: 'in_progress',
  description: '',
  attachments: [] as any[]
})

// 表单验证规则
const recordFormRules: FormRules = {
  type: [
    { required: true, message: '请选择记录类型', trigger: 'change' }
  ],
  subject: [
    { required: true, message: '请输入服务主题', trigger: 'blur' },
    { min: 2, max: 100, message: '服务主题长度在2-100个字符', trigger: 'blur' }
  ],
  serviceStaff: [
    { required: true, message: '请输入服务人员姓名', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择服务状态', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入服务描述', trigger: 'blur' },
    { min: 10, max: 500, message: '服务描述长度在10-500个字符', trigger: 'blur' }
  ]
}

// 上传配置
const uploadUrl = computed(() => '/api/upload/service-attachments')
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
}))

// 计算属性
const filteredRecords = computed(() => {
  if (!typeFilter.value) return recordList.value
  return recordList.value.filter(record => record.type === typeFilter.value)
})

const serviceStats = computed(() => {
  const stats = {
    total: recordList.value.length,
    maintenance: 0,
    upgrade: 0,
    consultation: 0,
    complaint: 0
  }
  
  recordList.value.forEach(record => {
    if (record.type === 'maintenance') stats.maintenance++
    if (record.type === 'upgrade') stats.upgrade++
    if (record.type === 'consultation') stats.consultation++
    if (record.type === 'complaint') stats.complaint++
  })
  
  return stats
})

// 方法
const loadRecords = async () => {
  loading.value = true
  try {
    const response = await customerRelationApi.getCustomerServiceRecords(props.customerId)
    recordList.value = response.data.list || response.data
  } catch (error) {
    console.error('加载服务记录失败:', error)
    ElMessage.error('加载服务记录失败')
  } finally {
    loading.value = false
  }
}

const loadCustomerDevices = async () => {
  try {
    const response = await customerRelationApi.getCustomerDevices(props.customerId)
    customerDevices.value = response.data.list || response.data
  } catch (error) {
    console.error('加载客户设备失败:', error)
  }
}

const refreshRecords = () => {
  loadRecords()
  emit('refresh')
}

const handleFilter = () => {
  // 筛选处理
}

const handleAddRecord = () => {
  // 重置表单
  Object.assign(recordForm, {
    type: '',
    subject: '',
    deviceId: '',
    serviceStaff: '',
    cost: 0,
    status: 'in_progress',
    description: '',
    attachments: []
  })
  addRecordDialogVisible.value = true
}

const handleAddRecordSubmit = async () => {
  if (!recordFormRef.value) return
  
  try {
    await recordFormRef.value.validate()
    
    addRecordLoading.value = true
    
    const recordData = {
      customerId: props.customerId,
      type: recordForm.type,
      subject: recordForm.subject,
      deviceId: recordForm.deviceId || undefined,
      serviceStaff: recordForm.serviceStaff,
      cost: recordForm.cost,
      status: recordForm.status,
      description: recordForm.description,
      attachments: recordForm.attachments.map(file => ({
        name: file.name,
        url: file.response?.url || file.url,
        size: file.size
      }))
    }
    
    await customerRelationApi.createServiceRecord(props.customerId, recordData)
    
    ElMessage.success('服务记录添加成功')
    addRecordDialogVisible.value = false
    refreshRecords()
    
  } catch (error) {
    console.error('添加服务记录失败:', error)
    ElMessage.error('添加服务记录失败')
  } finally {
    addRecordLoading.value = false
  }
}

const recordDetailDialogVisible = ref(false)
const selectedRecord = ref<CustomerServiceRecord | null>(null)

const viewRecordDetail = (record: CustomerServiceRecord) => {
  selectedRecord.value = record
  recordDetailDialogVisible.value = true
}

const editRecord = (record: CustomerServiceRecord) => {
  ElMessage.info('编辑记录功能开发中...')
}

const handleEditRecord = (record: CustomerServiceRecord) => {
  // 从服务记录详情对话框触发的编辑操作
  editRecord(record)
}

const handleCompleteRecord = async (record: CustomerServiceRecord) => {
  try {
    // 更新服务记录状态为已完成
    await customerRelationApi.createServiceRecord(props.customerId, {
      type: record.type,
      subject: record.subject,
      description: record.description,
      deviceId: record.deviceId,
      serviceStaff: record.serviceStaff,
      cost: record.cost,
      status: 'completed',
      attachments: record.attachments
    });
    
    ElMessage.success('服务记录已标记为完成')
    refreshRecords()
  } catch (error) {
    console.error('更新服务记录失败:', error)
    ElMessage.error('更新服务记录失败')
  }
}

const viewAttachment = (attachment: any) => {
  window.open(attachment.url, '_blank')
}

// 文件上传处理
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isValidType = [
    'image/jpeg',
    'image/png',
    'image/gif',
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
  ].includes(file.type)
  
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isValidType) {
    ElMessage.error('只支持图片、PDF、Word格式文件')
    return false
  }
  
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
  
  return true
}

// 工具方法
const getRecordTagType = (type: string) => {
  const types: Record<string, any> = {
    maintenance: 'warning',
    upgrade: 'success',
    consultation: 'primary',
    complaint: 'danger'
  }
  return types[type] || 'info'
}

const getRecordTypeText = (type: string) => {
  const texts: Record<string, string> = {
    maintenance: '维修记录',
    upgrade: '升级记录',
    consultation: '咨询记录',
    complaint: '投诉处理'
  }
  return texts[type] || type
}

const getStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    in_progress: 'primary',
    completed: 'success',
    cancelled: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    in_progress: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return texts[status] || status
}

const getTimelineType = (type: string) => {
  const types: Record<string, any> = {
    maintenance: 'warning',
    upgrade: 'success',
    consultation: 'primary',
    complaint: 'danger'
  }
  return types[type] || 'info'
}

const getTimelineIcon = (type: string) => {
  const icons: Record<string, any> = {
    maintenance: Tools,
    upgrade: Upgrade,
    consultation: ChatDotRound,
    complaint: Warning
  }
  return icons[type] || Document
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  recordList.value = props.records
  loadCustomerDevices()
})
</script>

<style lang="scss" scoped>
.customer-service-records {
  .service-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h4 {
      margin: 0;
      color: #303133;
      font-size: 16px;
      font-weight: 600;
    }
    
    .header-actions {
      display: flex;
      gap: 8px;
      align-items: center;
    }
  }
  
  .service-stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 20px;
    
    .stat-item {
      text-align: center;
      padding: 16px;
      background: #F5F7FA;
      border-radius: 6px;
      
      .stat-value {
        font-size: 20px;
        font-weight: 700;
        color: #303133;
        margin-bottom: 4px;
      }
      
      .stat-label {
        font-size: 12px;
        color: #606266;
      }
    }
  }
  
  .service-timeline {
    .timeline-content {
      .record-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        
        .record-title {
          display: flex;
          align-items: center;
          gap: 8px;
          
          .record-subject {
            font-weight: 500;
            color: #303133;
          }
        }
        
        .record-actions {
          display: flex;
          gap: 4px;
        }
      }
      
      .record-content {
        .record-description {
          margin: 0 0 12px 0;
          color: #606266;
          line-height: 1.5;
        }
        
        .record-meta {
          display: flex;
          flex-wrap: wrap;
          gap: 16px;
          margin-bottom: 12px;
          
          .meta-item {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 13px;
            color: #909399;
            
            .el-icon {
              font-size: 14px;
            }
          }
        }
        
        .record-attachments {
          .attachments-label {
            font-size: 13px;
            color: #606266;
            margin-bottom: 8px;
          }
          
          .attachments-list {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            
            .attachment-tag {
              cursor: pointer;
              transition: all 0.3s ease;
              
              &:hover {
                background-color: #409EFF;
                color: white;
              }
              
              .el-icon {
                margin-right: 4px;
              }
            }
          }
        }
      }
    }
    
    .empty-records {
      text-align: center;
      padding: 40px 0;
    }
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
  
  .upload-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }
}

@media (max-width: 768px) {
  .customer-service-records {
    .service-stats {
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
    }
    
    .service-header {
      flex-direction: column;
      gap: 12px;
      align-items: stretch;
      
      .header-actions {
        justify-content: center;
        flex-wrap: wrap;
      }
    }
    
    .service-timeline {
      .timeline-content {
        .record-header {
          flex-direction: column;
          gap: 8px;
          align-items: flex-start;
          
          .record-actions {
            align-self: flex-end;
          }
        }
        
        .record-content {
          .record-meta {
            flex-direction: column;
            gap: 8px;
          }
        }
      }
    }
  }
}
</style>