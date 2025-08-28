<template>
  <div class="product-workflow">
    <div class="workflow-header">
      <h4>产品状态工作流</h4>
      <div class="current-status">
        当前状态：
        <el-tag :type="getStatusType(currentStatus)" size="large">
          {{ getStatusText(currentStatus) }}
        </el-tag>
      </div>
    </div>

    <div class="workflow-steps">
      <div 
        v-for="(step, index) in workflowSteps" 
        :key="step.status"
        class="workflow-step"
        :class="{
          active: step.status === currentStatus,
          completed: isStepCompleted(step.status),
          disabled: !canTransitionTo(step.status)
        }"
      >
        <!-- 步骤图标 -->
        <div class="step-icon">
          <el-icon v-if="isStepCompleted(step.status)">
            <CircleCheck />
          </el-icon>
          <el-icon v-else-if="step.status === currentStatus">
            <component :is="step.icon" />
          </el-icon>
          <el-icon v-else>
            <component :is="step.icon" />
          </el-icon>
        </div>

        <!-- 步骤内容 -->
        <div class="step-content">
          <div class="step-title">{{ step.title }}</div>
          <div class="step-description">{{ step.description }}</div>
          
          <!-- 操作按钮 -->
          <div class="step-actions" v-if="step.status !== currentStatus && canTransitionTo(step.status)">
            <el-button
              size="small"
              :type="getActionButtonType(step.status)"
              @click="handleStatusChange(step.status)"
              :loading="transitioning"
            >
              {{ getActionText(step.status) }}
            </el-button>
          </div>
        </div>

        <!-- 连接线 -->
        <div 
          v-if="index < workflowSteps.length - 1" 
          class="step-connector"
          :class="{ active: isStepCompleted(step.status) }"
        />
      </div>
    </div>

    <!-- 状态变更历史 -->
    <div class="status-history" v-if="statusHistory.length > 0">
      <h4>状态变更历史</h4>
      <div class="history-list">
        <div 
          v-for="history in statusHistory" 
          :key="history.id"
          class="history-item"
        >
          <div class="history-icon">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="history-content">
            <div class="history-action">
              从 <el-tag size="small">{{ getStatusText(history.fromStatus) }}</el-tag>
              变更为 <el-tag size="small">{{ getStatusText(history.toStatus) }}</el-tag>
            </div>
            <div class="history-meta">
              <span class="history-user">{{ history.operator }}</span>
              <span class="history-time">{{ formatDate(history.createdAt) }}</span>
            </div>
            <div v-if="history.comment" class="history-comment">
              备注：{{ history.comment }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  CircleCheck, 
  Clock, 
  Edit, 
  View, 
  Upload, 
  Box 
} from '@element-plus/icons-vue'
import { formatDate } from '@/utils/dateTime'

interface WorkflowStep {
  status: string
  title: string
  description: string
  icon: any
}

interface StatusHistory {
  id: string
  fromStatus: string
  toStatus: string
  operator: string
  comment?: string
  createdAt: string
}

interface Props {
  currentStatus: string
  productId?: string
  readonly?: boolean
}

interface Emits {
  (e: 'status-change', status: string, comment?: string): void
}

const props = withDefaults(defineProps<Props>(), {
  readonly: false
})

const emit = defineEmits<Emits>()

// 响应式数据
const transitioning = ref(false)

// 工作流步骤定义
const workflowSteps: WorkflowStep[] = [
  {
    status: 'draft',
    title: '草稿',
    description: '产品信息编辑中，尚未提交审核',
    icon: Edit
  },
  {
    status: 'review',
    title: '审核中',
    description: '产品信息已提交，等待审核',
    icon: View
  },
  {
    status: 'published',
    title: '已发布',
    description: '产品已发布到官网，用户可见',
    icon: Upload
  },
  {
    status: 'archived',
    title: '已归档',
    description: '产品已归档，不再显示',
    icon: Box
  }
]

// Mock状态变更历史
const statusHistory = ref<StatusHistory[]>([
  {
    id: '1',
    fromStatus: 'draft',
    toStatus: 'review',
    operator: '张三',
    comment: '产品信息已完善，提交审核',
    createdAt: '2024-02-15T10:30:00Z'
  },
  {
    id: '2',
    fromStatus: 'review',
    toStatus: 'published',
    operator: '李四',
    comment: '审核通过，发布到官网',
    createdAt: '2024-02-16T14:20:00Z'
  }
])

// 计算属性
const currentStepIndex = computed(() => {
  return workflowSteps.findIndex(step => step.status === props.currentStatus)
})

// 方法
const getStatusText = (status: string): string => {
  const statusMap = {
    draft: '草稿',
    review: '审核中',
    published: '已发布',
    archived: '已归档'
  }
  return statusMap[status as keyof typeof statusMap] || status
}

const getStatusType = (status: string): string => {
  const typeMap = {
    draft: 'info',
    review: 'warning',
    published: 'success',
    archived: 'info'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

const isStepCompleted = (status: string): boolean => {
  const stepIndex = workflowSteps.findIndex(step => step.status === status)
  return stepIndex < currentStepIndex.value
}

const canTransitionTo = (targetStatus: string): boolean => {
  if (props.readonly) return false
  
  const currentIndex = currentStepIndex.value
  const targetIndex = workflowSteps.findIndex(step => step.status === targetStatus)
  
  // 定义允许的状态转换规则
  const allowedTransitions: Record<string, string[]> = {
    draft: ['review', 'archived'],
    review: ['draft', 'published', 'archived'],
    published: ['archived'],
    archived: ['draft']
  }
  
  return allowedTransitions[props.currentStatus]?.includes(targetStatus) || false
}

const getActionButtonType = (status: string): string => {
  const typeMap = {
    review: 'warning',
    published: 'success',
    archived: 'info',
    draft: 'primary'
  }
  return typeMap[status as keyof typeof typeMap] || 'primary'
}

const getActionText = (status: string): string => {
  const actionMap = {
    draft: '退回草稿',
    review: '提交审核',
    published: '发布产品',
    archived: '归档产品'
  }
  return actionMap[status as keyof typeof actionMap] || '变更状态'
}

const handleStatusChange = async (targetStatus: string) => {
  try {
    let comment = ''
    
    // 对于某些状态变更，需要用户输入备注
    if (targetStatus === 'archived' || targetStatus === 'draft') {
      const { value } = await ElMessageBox.prompt(
        `请输入${getActionText(targetStatus)}的原因：`,
        '状态变更确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputType: 'textarea',
          inputPlaceholder: '请输入变更原因（可选）'
        }
      )
      comment = value || ''
    } else {
      await ElMessageBox.confirm(
        `确定要${getActionText(targetStatus)}吗？`,
        '状态变更确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
    }
    
    transitioning.value = true
    
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    emit('status-change', targetStatus, comment)
    
    // 添加到历史记录
    statusHistory.value.unshift({
      id: Date.now().toString(),
      fromStatus: props.currentStatus,
      toStatus: targetStatus,
      operator: '当前用户', // 实际应该从用户信息获取
      comment,
      createdAt: new Date().toISOString()
    })
    
    ElMessage.success(`产品状态已变更为${getStatusText(targetStatus)}`)
  } catch {
    // 用户取消操作
  } finally {
    transitioning.value = false
  }
}
</script>

<style lang="scss" scoped>
.product-workflow {
  .workflow-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid var(--border-color);
    
    h4 {
      margin: 0;
      color: var(--text-primary);
    }
    
    .current-status {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      color: var(--text-secondary);
    }
  }
  
  .workflow-steps {
    position: relative;
    
    .workflow-step {
      display: flex;
      align-items: flex-start;
      gap: 16px;
      padding: 20px 0;
      position: relative;
      
      &.active {
        .step-icon {
          background: var(--primary-color);
          color: white;
          box-shadow: 0 0 0 4px rgba(var(--primary-color-rgb), 0.2);
        }
        
        .step-title {
          color: var(--primary-color);
          font-weight: 600;
        }
      }
      
      &.completed {
        .step-icon {
          background: var(--success-color);
          color: white;
        }
        
        .step-title {
          color: var(--success-color);
        }
      }
      
      &.disabled {
        opacity: 0.5;
        
        .step-content {
          pointer-events: none;
        }
      }
      
      .step-icon {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: var(--bg-secondary);
        border: 2px solid var(--border-color);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        color: var(--text-light);
        transition: all 0.3s ease;
        flex-shrink: 0;
        z-index: 2;
        position: relative;
      }
      
      .step-content {
        flex: 1;
        
        .step-title {
          font-size: 16px;
          font-weight: 500;
          color: var(--text-primary);
          margin-bottom: 4px;
        }
        
        .step-description {
          font-size: 14px;
          color: var(--text-secondary);
          margin-bottom: 12px;
          line-height: 1.5;
        }
        
        .step-actions {
          margin-top: 8px;
        }
      }
      
      .step-connector {
        position: absolute;
        left: 19px;
        top: 60px;
        bottom: -20px;
        width: 2px;
        background: var(--border-color);
        z-index: 1;
        
        &.active {
          background: var(--success-color);
        }
      }
      
      &:last-child .step-connector {
        display: none;
      }
    }
  }
  
  .status-history {
    margin-top: 32px;
    padding-top: 24px;
    border-top: 1px solid var(--border-color);
    
    h4 {
      margin: 0 0 16px 0;
      color: var(--text-primary);
    }
    
    .history-list {
      .history-item {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        padding: 16px;
        background: var(--bg-secondary);
        border-radius: var(--radius-md);
        margin-bottom: 12px;
        
        .history-icon {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          background: var(--primary-light);
          color: var(--primary-color);
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 14px;
          flex-shrink: 0;
        }
        
        .history-content {
          flex: 1;
          
          .history-action {
            font-size: 14px;
            color: var(--text-primary);
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 8px;
          }
          
          .history-meta {
            display: flex;
            align-items: center;
            gap: 16px;
            font-size: 12px;
            color: var(--text-light);
            margin-bottom: 4px;
            
            .history-user {
              font-weight: 500;
            }
          }
          
          .history-comment {
            font-size: 12px;
            color: var(--text-secondary);
            font-style: italic;
          }
        }
      }
    }
  }
}
</style>