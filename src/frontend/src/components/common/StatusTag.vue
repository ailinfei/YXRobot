<template>
  <el-tag
    :type="tagType"
    :size="size"
    :effect="effect"
    :closable="closable"
    :disable-transitions="disableTransitions"
    :hit="hit"
    :color="customColor"
    :round="round"
    @close="handleClose"
    @click="handleClick"
    class="status-tag"
    :class="tagClass"
  >
    <!-- 状态图标 -->
    <el-icon v-if="showIcon && statusIcon" class="status-icon">
      <component :is="statusIcon" />
    </el-icon>
    
    <!-- 状态文本 -->
    <span class="status-text">{{ statusText }}</span>
    
    <!-- 额外内容 -->
    <slot />
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  CircleCheck,
  CircleClose,
  Warning,
  Clock,
  Loading,
  QuestionFilled,
  Minus
} from '@element-plus/icons-vue'

// 定义状态配置接口
interface StatusConfig {
  text: string
  type: 'success' | 'info' | 'warning' | 'danger' | ''
  icon?: any
  color?: string
}

// 定义组件属性接口
interface StatusTagProps {
  status: string | number
  statusMap?: Record<string | number, StatusConfig>
  size?: 'large' | 'default' | 'small'
  effect?: 'dark' | 'light' | 'plain'
  closable?: boolean
  disableTransitions?: boolean
  hit?: boolean
  round?: boolean
  showIcon?: boolean
  clickable?: boolean
  customClass?: string
}

// Props定义
const props = withDefaults(defineProps<StatusTagProps>(), {
  status: '',
  statusMap: () => ({}),
  size: 'default',
  effect: 'light',
  closable: false,
  disableTransitions: false,
  hit: false,
  round: false,
  showIcon: true,
  clickable: false,
  customClass: ''
})

// Emits定义
const emit = defineEmits(['close', 'click'])

// 默认状态映射
const defaultStatusMap: Record<string | number, StatusConfig> = {
  // 通用状态
  'active': { text: '启用', type: 'success', icon: CircleCheck },
  'inactive': { text: '禁用', type: 'info', icon: Minus },
  'pending': { text: '待处理', type: 'warning', icon: Clock },
  'processing': { text: '处理中', type: 'warning', icon: Loading },
  'success': { text: '成功', type: 'success', icon: CircleCheck },
  'failed': { text: '失败', type: 'danger', icon: CircleClose },
  'error': { text: '错误', type: 'danger', icon: CircleClose },
  'warning': { text: '警告', type: 'warning', icon: Warning },
  'unknown': { text: '未知', type: 'info', icon: QuestionFilled },
  
  // 数字状态
  1: { text: '启用', type: 'success', icon: CircleCheck },
  0: { text: '禁用', type: 'info', icon: Minus },
  
  // 订单状态
  'draft': { text: '草稿', type: 'info', icon: Minus },
  'published': { text: '已发布', type: 'success', icon: CircleCheck },
  'archived': { text: '已归档', type: 'warning', icon: Warning },
  
  // 设备状态
  'online': { text: '在线', type: 'success', icon: CircleCheck },
  'offline': { text: '离线', type: 'info', icon: Minus },
  'fault': { text: '故障', type: 'danger', icon: CircleClose },
  'maintenance': { text: '维护中', type: 'warning', icon: Warning },
  
  // 订单状态
  'paid': { text: '已付款', type: 'success', icon: CircleCheck },
  'shipped': { text: '已发货', type: 'warning', icon: Clock },
  'delivered': { text: '已送达', type: 'success', icon: CircleCheck },
  'cancelled': { text: '已取消', type: 'danger', icon: CircleClose },
  
  // 租赁状态
  'rental_active': { text: '租赁中', type: 'success', icon: CircleCheck },
  'rental_expired': { text: '已过期', type: 'warning', icon: Warning },
  'rental_returned': { text: '已归还', type: 'info', icon: Minus },
  
  // 下载状态
  'not_downloaded': { text: '未下载', type: 'info', icon: Minus },
  'downloading': { text: '下载中', type: 'warning', icon: Loading },
  'downloaded': { text: '已下载', type: 'success', icon: CircleCheck },
  'download_failed': { text: '下载失败', type: 'danger', icon: CircleClose },
  
  // AI生成状态
  'sample_uploading': { text: '样本上传中', type: 'warning', icon: Loading },
  'ai_generating': { text: 'AI生成中', type: 'warning', icon: Loading },
  'generated': { text: '已生成', type: 'success', icon: CircleCheck },
  'publishing': { text: '发布中', type: 'warning', icon: Loading },
  'generation_failed': { text: '生成失败', type: 'danger', icon: CircleClose }
}

// 计算属性
const statusConfig = computed(() => {
  const mergedMap = { ...defaultStatusMap, ...props.statusMap }
  return mergedMap[props.status] || {
    text: String(props.status),
    type: 'info' as const,
    icon: QuestionFilled
  }
})

const statusText = computed(() => statusConfig.value.text)
const tagType = computed(() => statusConfig.value.type)
const statusIcon = computed(() => statusConfig.value.icon)
const customColor = computed(() => statusConfig.value.color)

const tagClass = computed(() => {
  return [
    props.customClass,
    {
      'clickable': props.clickable,
      'with-icon': props.showIcon && statusIcon.value
    }
  ]
})

// 事件处理
const handleClose = (event: Event) => {
  emit('close', event)
}

const handleClick = (event: Event) => {
  if (props.clickable) {
    emit('click', event)
  }
}
</script>

<style lang="scss" scoped>
.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  
  &.clickable {
    cursor: pointer;
    transition: all 0.2s ease;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
  
  &.with-icon {
    .status-icon {
      font-size: 12px;
      
      &.is-loading {
        animation: rotating 2s linear infinite;
      }
    }
  }
  
  .status-text {
    font-weight: 500;
  }
}

// 动画
@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

// Element Plus 样式覆盖
:deep(.el-tag) {
  border-radius: var(--radius-sm);
  font-size: 12px;
  
  &.el-tag--success {
    background-color: rgba(16, 185, 129, 0.1);
    border-color: rgba(16, 185, 129, 0.3);
    color: #059669;
  }
  
  &.el-tag--warning {
    background-color: rgba(245, 158, 11, 0.1);
    border-color: rgba(245, 158, 11, 0.3);
    color: #d97706;
  }
  
  &.el-tag--danger {
    background-color: rgba(239, 68, 68, 0.1);
    border-color: rgba(239, 68, 68, 0.3);
    color: #dc2626;
  }
  
  &.el-tag--info {
    background-color: rgba(107, 114, 128, 0.1);
    border-color: rgba(107, 114, 128, 0.3);
    color: #6b7280;
  }
  
  // 深色效果
  &.el-tag--dark {
    &.el-tag--success {
      background-color: #059669;
      border-color: #059669;
      color: white;
    }
    
    &.el-tag--warning {
      background-color: #d97706;
      border-color: #d97706;
      color: white;
    }
    
    &.el-tag--danger {
      background-color: #dc2626;
      border-color: #dc2626;
      color: white;
    }
    
    &.el-tag--info {
      background-color: #6b7280;
      border-color: #6b7280;
      color: white;
    }
  }
  
  // 朴素效果
  &.el-tag--plain {
    background-color: transparent;
    
    &.el-tag--success {
      color: #059669;
      border-color: #059669;
    }
    
    &.el-tag--warning {
      color: #d97706;
      border-color: #d97706;
    }
    
    &.el-tag--danger {
      color: #dc2626;
      border-color: #dc2626;
    }
    
    &.el-tag--info {
      color: #6b7280;
      border-color: #6b7280;
    }
  }
}
</style>