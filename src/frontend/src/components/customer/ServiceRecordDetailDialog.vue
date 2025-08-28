<template>
  <el-dialog
    v-model="visible"
    title="服务记录详情"
    width="800px"
    :before-close="handleClose"
    destroy-on-close
  >
    <div v-if="record" class="service-record-detail">
      <!-- 基本信息 -->
      <div class="detail-section">
        <div class="section-header">
          <h4>基本信息</h4>
          <div class="record-status">
            <el-tag :type="getStatusTagType(record.status)" size="large">
              {{ getStatusText(record.status) }}
            </el-tag>
            <el-tag :type="getRecordTagType(record.type)" size="large">
              {{ getRecordTypeText(record.type) }}
            </el-tag>
          </div>
        </div>
        
        <div class="info-grid">
          <div class="info-item">
            <label>服务主题</label>
            <span class="service-subject">{{ record.subject }}</span>
          </div>
          <div class="info-item">
            <label>服务类型</label>
            <span>{{ getRecordTypeText(record.type) }}</span>
          </div>
          <div class="info-item">
            <label>服务人员</label>
            <span>{{ record.serviceStaff }}</span>
          </div>
          <div class="info-item">
            <label>服务费用</label>
            <span class="cost-amount">{{ record.cost ? `¥${record.cost.toLocaleString()}` : '免费' }}</span>
          </div>
          <div class="info-item">
            <label>创建时间</label>
            <span>{{ formatDate(record.createdAt) }}</span>
          </div>
          <div class="info-item">
            <label>更新时间</label>
            <span>{{ formatDate(record.updatedAt) }}</span>
          </div>
        </div>
      </div>

      <!-- 服务描述 -->
      <div class="detail-section">
        <h4>服务描述</h4>
        <div class="service-description">
          {{ record.description }}
        </div>
      </div>

      <!-- 相关设备 -->
      <div class="detail-section" v-if="record.deviceId">
        <h4>相关设备</h4>
        <div class="device-info">
          <div class="device-card">
            <div class="device-icon">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="device-details">
              <div class="device-id">设备ID: {{ record.deviceId }}</div>
              <div class="device-status">
                <el-tag type="success" size="small">正常运行</el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 服务进度 -->
      <div class="detail-section">
        <h4>服务进度</h4>
        <div class="service-progress">
          <el-steps :active="getProgressStep(record.status)" finish-status="success">
            <el-step title="服务创建" description="记录服务需求" />
            <el-step title="处理中" description="正在处理服务" />
            <el-step title="服务完成" description="服务已完成" />
          </el-steps>
        </div>
      </div>

      <!-- 附件列表 -->
      <div class="detail-section" v-if="record.attachments?.length">
        <h4>相关附件</h4>
        <div class="attachments-list">
          <div
            v-for="attachment in record.attachments"
            :key="attachment.id"
            class="attachment-item"
            @click="viewAttachment(attachment)"
          >
            <div class="attachment-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="attachment-info">
              <div class="attachment-name">{{ attachment.name }}</div>
              <div class="attachment-size">{{ formatFileSize(attachment.size) }}</div>
            </div>
            <div class="attachment-actions">
              <el-button text type="primary" size="small" @click.stop="downloadAttachment(attachment)">
                <el-icon><Download /></el-icon>
                下载
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 服务时间线 -->
      <div class="detail-section">
        <h4>服务时间线</h4>
        <div class="service-timeline">
          <el-timeline>
            <el-timeline-item
              v-for="event in serviceEvents"
              :key="event.id"
              :timestamp="formatDate(event.timestamp)"
              :type="getTimelineType(event.type)"
            >
              <div class="timeline-content">
                <div class="event-title">{{ event.title }}</div>
                <div class="event-description" v-if="event.description">{{ event.description }}</div>
                <div class="event-operator" v-if="event.operator">操作人: {{ event.operator }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <!-- 客户反馈 -->
      <div class="detail-section" v-if="customerFeedback">
        <h4>客户反馈</h4>
        <div class="customer-feedback">
          <div class="feedback-rating">
            <span class="rating-label">满意度评分:</span>
            <el-rate
              v-model="customerFeedback.rating"
              disabled
              show-score
              text-color="#ff9900"
            />
          </div>
          <div class="feedback-comment">
            <span class="comment-label">客户评价:</span>
            <div class="comment-text">{{ customerFeedback.comment }}</div>
          </div>
          <div class="feedback-time">
            反馈时间: {{ formatDate(customerFeedback.createdAt) }}
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit" v-if="record?.status !== 'completed'">
          编辑记录
        </el-button>
        <el-button type="success" @click="handleComplete" v-if="record?.status === 'in_progress'">
          标记完成
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Monitor,
  Document,
  Download
} from '@element-plus/icons-vue'
import type { CustomerServiceRecord, ServiceAttachment } from '@/types/customer'

// Props
interface Props {
  modelValue: boolean
  record?: CustomerServiceRecord | null
}

const props = withDefaults(defineProps<Props>(), {
  record: null
})

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'edit': [record: CustomerServiceRecord]
  'complete': [record: CustomerServiceRecord]
}>()

// 响应式数据
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 模拟服务事件数据
const serviceEvents = ref([
  {
    id: '1',
    type: 'created',
    title: '服务记录创建',
    description: '客户提交服务请求',
    operator: '系统',
    timestamp: props.record?.createdAt || new Date().toISOString()
  },
  {
    id: '2',
    type: 'assigned',
    title: '分配服务人员',
    description: '服务已分配给相关技术人员',
    operator: '客服主管',
    timestamp: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString()
  },
  {
    id: '3',
    type: 'processing',
    title: '开始处理',
    description: '技术人员开始处理服务请求',
    operator: props.record?.serviceStaff || '技术人员',
    timestamp: new Date(Date.now() - 1000 * 60 * 60).toISOString()
  }
])

// 模拟客户反馈数据
const customerFeedback = ref({
  rating: 5,
  comment: '服务很及时，技术人员很专业，问题得到了很好的解决。',
  createdAt: new Date().toISOString()
})

// 方法
const handleClose = () => {
  visible.value = false
}

const handleEdit = () => {
  if (props.record) {
    emit('edit', props.record)
    handleClose()
  }
}

const handleComplete = () => {
  if (props.record) {
    emit('complete', props.record)
    handleClose()
  }
}

const viewAttachment = (attachment: ServiceAttachment) => {
  window.open(attachment.url, '_blank')
}

const downloadAttachment = (attachment: ServiceAttachment) => {
  // 创建下载链接
  const link = document.createElement('a')
  link.href = attachment.url
  link.download = attachment.name
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  ElMessage.success('文件下载已开始')
}

// 工具方法
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

const getProgressStep = (status: string) => {
  const steps: Record<string, number> = {
    in_progress: 1,
    completed: 2,
    cancelled: 0
  }
  return steps[status] || 0
}

const getTimelineType = (type: string) => {
  const types: Record<string, any> = {
    created: 'primary',
    assigned: 'info',
    processing: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return types[type] || 'primary'
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
</script>

<style lang="scss" scoped>
.service-record-detail {
  .detail-section {
    margin-bottom: 32px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .section-header {
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
      
      .record-status {
        display: flex;
        gap: 8px;
      }
    }
    
    h4 {
      margin: 0 0 20px 0;
      color: #303133;
      font-size: 16px;
      font-weight: 600;
      border-bottom: 1px solid #EBEEF5;
      padding-bottom: 8px;
    }
    
    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;
      
      .info-item {
        display: flex;
        flex-direction: column;
        gap: 8px;
        
        label {
          font-size: 14px;
          font-weight: 500;
          color: #606266;
        }
        
        span {
          font-size: 14px;
          color: #303133;
          
          &.service-subject {
            font-weight: 600;
            color: #409EFF;
          }
          
          &.cost-amount {
            font-weight: 600;
            color: #E6A23C;
          }
        }
      }
    }
    
    .service-description {
      padding: 16px;
      background: #F5F7FA;
      border-radius: 6px;
      font-size: 14px;
      color: #303133;
      line-height: 1.6;
      white-space: pre-wrap;
    }
    
    .device-info {
      .device-card {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 16px;
        background: #F8F9FA;
        border-radius: 8px;
        border: 1px solid #EBEEF5;
        
        .device-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
          background: linear-gradient(135deg, #409EFF, #66B1FF);
          color: white;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;
        }
        
        .device-details {
          flex: 1;
          
          .device-id {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
            margin-bottom: 4px;
          }
        }
      }
    }
    
    .service-progress {
      padding: 20px;
      background: #F8F9FA;
      border-radius: 8px;
    }
    
    .attachments-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
      
      .attachment-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px;
        background: #F8F9FA;
        border-radius: 6px;
        border: 1px solid #EBEEF5;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
          background: #E6F7FF;
          border-color: #409EFF;
        }
        
        .attachment-icon {
          width: 32px;
          height: 32px;
          border-radius: 4px;
          background: #409EFF;
          color: white;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 16px;
        }
        
        .attachment-info {
          flex: 1;
          
          .attachment-name {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
            margin-bottom: 2px;
          }
          
          .attachment-size {
            font-size: 12px;
            color: #909399;
          }
        }
        
        .attachment-actions {
          opacity: 0;
          transition: opacity 0.3s ease;
        }
        
        &:hover .attachment-actions {
          opacity: 1;
        }
      }
    }
    
    .service-timeline {
      .timeline-content {
        .event-title {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 4px;
        }
        
        .event-description {
          font-size: 13px;
          color: #606266;
          margin-bottom: 4px;
        }
        
        .event-operator {
          font-size: 12px;
          color: #909399;
        }
      }
    }
    
    .customer-feedback {
      padding: 20px;
      background: #F8F9FA;
      border-radius: 8px;
      border-left: 4px solid #67C23A;
      
      .feedback-rating {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 16px;
        
        .rating-label {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
        }
      }
      
      .feedback-comment {
        margin-bottom: 12px;
        
        .comment-label {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
          display: block;
          margin-bottom: 8px;
        }
        
        .comment-text {
          font-size: 14px;
          color: #606266;
          line-height: 1.6;
          padding: 12px;
          background: white;
          border-radius: 4px;
        }
      }
      
      .feedback-time {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .service-record-detail {
    .detail-section {
      .info-grid {
        grid-template-columns: 1fr;
      }
      
      .section-header {
        flex-direction: column;
        gap: 12px;
        align-items: flex-start;
      }
    }
  }
}
</style>