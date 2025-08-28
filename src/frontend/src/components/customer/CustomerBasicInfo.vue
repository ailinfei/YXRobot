<template>
  <div class="customer-basic-info">
    <div class="info-header">
      <h4>基本信息</h4>
      <el-button type="primary" size="small" @click="handleEdit">
        <el-icon><Edit /></el-icon>
        编辑信息
      </el-button>
    </div>

    <div class="info-content">
      <div class="info-grid">
        <div class="info-item">
          <label>客户姓名</label>
          <span>{{ customer?.name || '-' }}</span>
        </div>
        
        <div class="info-item">
          <label>客户等级</label>
          <el-tag :type="getLevelTagType(customer?.level)">
            {{ getLevelText(customer?.level) }}
          </el-tag>
        </div>
        
        <div class="info-item">
          <label>联系电话</label>
          <span>{{ customer?.phone || '-' }}</span>
        </div>
        
        <div class="info-item">
          <label>邮箱地址</label>
          <span>{{ customer?.email || '-' }}</span>
        </div>
        
        <div class="info-item">
          <label>公司名称</label>
          <span>{{ customer?.company || '-' }}</span>
        </div>
        
        <div class="info-item">
          <label>客户状态</label>
          <el-tag :type="getStatusTagType(customer?.status)">
            {{ getStatusText(customer?.status) }}
          </el-tag>
        </div>
        
        <div class="info-item">
          <label>注册时间</label>
          <span>{{ formatDate(customer?.registeredAt) }}</span>
        </div>
        
        <div class="info-item">
          <label>最后活跃</label>
          <span>{{ formatDate(customer?.lastActiveAt) }}</span>
        </div>
        
        <div class="info-item full-width">
          <label>联系地址</label>
          <span>{{ getFullAddress() }}</span>
        </div>
        
        <div class="info-item full-width" v-if="customer?.tags?.length">
          <label>客户标签</label>
          <div class="tags-container">
            <el-tag
              v-for="tag in customer.tags"
              :key="tag"
              size="small"
              type="info"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
        
        <div class="info-item full-width" v-if="customer?.notes">
          <label>备注信息</label>
          <div class="notes-content">{{ customer.notes }}</div>
        </div>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <CustomerFormDialog
      v-model="editDialogVisible"
      :customer="customer"
      @success="handleUpdateSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Edit } from '@element-plus/icons-vue'
import CustomerFormDialog from './CustomerFormDialog.vue'
import type { Customer } from '@/types/customer'

// Props
interface Props {
  customer: Customer | null
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  'update': []
}>()

// 响应式数据
const editDialogVisible = ref(false)

// 方法
const handleEdit = () => {
  editDialogVisible.value = true
}

const handleUpdateSuccess = () => {
  emit('update')
}

const getLevelTagType = (level?: string) => {
  const types: Record<string, any> = {
    regular: '',
    vip: 'warning',
    premium: 'success'
  }
  return types[level || ''] || ''
}

const getLevelText = (level?: string) => {
  const texts: Record<string, string> = {
    regular: '普通客户',
    vip: 'VIP客户',
    premium: '高级客户'
  }
  return texts[level || ''] || level || ''
}

const getStatusTagType = (status?: string) => {
  const types: Record<string, any> = {
    active: 'success',
    inactive: 'warning',
    suspended: 'danger'
  }
  return types[status || ''] || 'info'
}

const getStatusText = (status?: string) => {
  const texts: Record<string, string> = {
    active: '活跃',
    inactive: '非活跃',
    suspended: '已暂停'
  }
  return texts[status || ''] || status || ''
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const getFullAddress = () => {
  if (!props.customer?.address) return '-'
  
  const { province, city, detail } = props.customer.address
  const parts = [province, city, detail].filter(Boolean)
  return parts.length > 0 ? parts.join(' ') : '-'
}
</script>

<style lang="scss" scoped>
.customer-basic-info {
  .info-header {
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
  }
  
  .info-content {
    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;
      
      .info-item {
        display: flex;
        flex-direction: column;
        gap: 8px;
        
        &.full-width {
          grid-column: 1 / -1;
        }
        
        label {
          font-size: 14px;
          font-weight: 500;
          color: #606266;
        }
        
        span {
          font-size: 14px;
          color: #303133;
          word-break: break-all;
        }
        
        .tags-container {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          
          .tag-item {
            margin: 0;
          }
        }
        
        .notes-content {
          padding: 12px;
          background: #F5F7FA;
          border-radius: 4px;
          font-size: 14px;
          color: #303133;
          line-height: 1.5;
          white-space: pre-wrap;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .customer-basic-info {
    .info-content {
      .info-grid {
        grid-template-columns: 1fr;
        gap: 16px;
      }
    }
  }
}
</style>