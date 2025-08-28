<template>
  <div class="data-card" :class="cardClass">
    <!-- 卡片头部 -->
    <div class="card-header" v-if="showHeader">
      <div class="header-left">
        <div class="card-icon" v-if="icon || $slots.icon" :class="iconClass">
          <slot name="icon">
            <el-icon v-if="icon">
              <component :is="icon" />
            </el-icon>
          </slot>
        </div>
        <div class="card-title-section">
          <h3 class="card-title">{{ title }}</h3>
          <p class="card-subtitle" v-if="subtitle">{{ subtitle }}</p>
        </div>
      </div>
      <div class="header-right">
        <slot name="header-actions">
          <el-dropdown v-if="showActions" @command="handleAction">
            <el-button text :icon="MoreFilled" />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="refresh">刷新</el-dropdown-item>
                <el-dropdown-item command="export">导出</el-dropdown-item>
                <el-dropdown-item command="settings">设置</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </slot>
      </div>
    </div>

    <!-- 卡片内容 -->
    <div class="card-content">
      <!-- 加载状态 -->
      <div v-if="loading" class="card-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>{{ loadingText }}</span>
      </div>
      
      <!-- 错误状态 -->
      <div v-else-if="error" class="card-error">
        <el-icon><Warning /></el-icon>
        <span>{{ error }}</span>
        <el-button type="primary" size="small" @click="handleRetry">重试</el-button>
      </div>
      
      <!-- 空数据状态 -->
      <div v-else-if="isEmpty" class="card-empty">
        <el-icon><DocumentRemove /></el-icon>
        <span>{{ emptyText }}</span>
      </div>
      
      <!-- 主要内容 -->
      <div v-else class="card-body">
        <!-- 数值显示模式 -->
        <div v-if="type === 'number'" class="number-display">
          <div class="card-title-inline" v-if="title">{{ title }}</div>
          <div class="main-value">
            <CountUp 
              :value="Number(value)" 
              :duration="1500"
              :decimals="0"
              :suffix="unit"
              class="value-number" 
              :style="{ color: valueColor }"
            />
          </div>
          <div class="value-change" v-if="change !== undefined">
            <el-icon :class="changeClass">
              <component :is="changeIcon" />
            </el-icon>
            <span :class="changeClass">{{ formatChange(change) }}</span>
          </div>
        </div>
        
        <!-- 进度显示模式 -->
        <div v-else-if="type === 'progress'" class="progress-display">
          <div class="progress-info">
            <span class="progress-label">{{ progressLabel }}</span>
            <span class="progress-value">{{ value }}{{ unit || '%' }}</span>
          </div>
          <el-progress 
            :percentage="Number(value)" 
            :color="progressColor"
            :stroke-width="progressStrokeWidth"
            :show-text="false"
          />
        </div>
        
        <!-- 列表显示模式 -->
        <div v-else-if="type === 'list'" class="list-display">
          <div 
            v-for="(item, index) in listData" 
            :key="index"
            class="list-item"
          >
            <div class="item-label">{{ item.label }}</div>
            <div class="item-value">{{ item.value }}</div>
          </div>
        </div>
        
        <!-- 自定义内容 -->
        <div v-else class="custom-content">
          <slot />
        </div>
      </div>
    </div>

    <!-- 卡片底部 -->
    <div class="card-footer" v-if="showFooter || $slots.footer">
      <slot name="footer">
        <div class="footer-info">
          <span class="update-time" v-if="updateTime">
            更新时间: {{ formatTime(updateTime) }}
          </span>
          <el-link 
            v-if="moreLink" 
            type="primary" 
            :href="moreLink"
            :underline="false"
          >
            查看更多
          </el-link>
        </div>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  MoreFilled,
  Loading,
  Warning,
  DocumentRemove,
  ArrowUp,
  ArrowDown,
  Minus
} from '@element-plus/icons-vue'
import CountUp from './CountUp.vue'

// 定义列表项接口
interface ListItem {
  label: string
  value: string | number
}

// 定义组件属性接口
interface DataCardProps {
  title?: string
  subtitle?: string
  type?: 'number' | 'progress' | 'list' | 'custom'
  value?: string | number
  unit?: string
  change?: number
  changeType?: 'percent' | 'number'
  icon?: any
  iconClass?: string
  valueColor?: string
  progressLabel?: string
  progressColor?: string | string[]
  progressStrokeWidth?: number
  listData?: ListItem[]
  loading?: boolean
  loadingText?: string
  error?: string
  isEmpty?: boolean
  emptyText?: string
  showHeader?: boolean
  showFooter?: boolean
  showActions?: boolean
  updateTime?: string | Date
  moreLink?: string
  theme?: 'default' | 'primary' | 'success' | 'warning' | 'danger' | 'info'
  size?: 'small' | 'default' | 'large'
  clickable?: boolean
  customClass?: string
}

// Props定义
const props = withDefaults(defineProps<DataCardProps>(), {
  title: '',
  subtitle: '',
  type: 'custom',
  value: 0,
  unit: '',
  change: undefined,
  changeType: 'percent',
  icon: undefined,
  iconClass: '',
  valueColor: '',
  progressLabel: '',
  progressColor: '#409eff',
  progressStrokeWidth: 6,
  listData: () => [],
  loading: false,
  loadingText: '加载中...',
  error: '',
  isEmpty: false,
  emptyText: '暂无数据',
  showHeader: true,
  showFooter: false,
  showActions: false,
  updateTime: '',
  moreLink: '',
  theme: 'default',
  size: 'default',
  clickable: false,
  customClass: ''
})

// Emits定义
const emit = defineEmits([
  'click',
  'action',
  'retry'
])

// 计算属性
const cardClass = computed(() => {
  return [
    `card-${props.theme}`,
    `card-${props.size}`,
    props.customClass,
    {
      'clickable': props.clickable,
      'has-error': props.error,
      'is-loading': props.loading,
      'is-empty': props.isEmpty
    }
  ]
})

const changeClass = computed(() => {
  if (props.change === undefined) return ''
  
  if (props.change > 0) {
    return 'change-positive'
  } else if (props.change < 0) {
    return 'change-negative'
  } else {
    return 'change-neutral'
  }
})

const changeIcon = computed(() => {
  if (props.change === undefined) return Minus
  
  if (props.change > 0) {
    return ArrowUp
  } else if (props.change < 0) {
    return ArrowDown
  } else {
    return Minus
  }
})

// 方法

const formatChange = (change: number) => {
  const absChange = Math.abs(change)
  const prefix = change > 0 ? '+' : change < 0 ? '-' : ''
  const suffix = props.changeType === 'percent' ? '%' : ''
  
  return `${prefix}${absChange}${suffix}`
}

const formatTime = (time: string | Date) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 事件处理
const handleAction = (command: string) => {
  emit('action', command)
}

const handleRetry = () => {
  emit('retry')
}

const handleClick = (event: Event) => {
  if (props.clickable) {
    emit('click', event)
  }
}
</script>

<style lang="scss" scoped>
.data-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  overflow: hidden;
  transition: all 0.3s ease;
  position: relative;
  
  &.clickable {
    cursor: pointer;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }
  
  // 主题样式 - 添加顶部彩色条纹
  &.card-primary {
    border-top: 4px solid #3b82f6;
    
    .card-icon {
      background: #3b82f6;
      color: white;
    }
  }
  
  &.card-success {
    border-top: 4px solid #10b981;
    
    .card-icon {
      background: #10b981;
      color: white;
    }
  }
  
  &.card-warning {
    border-top: 4px solid #f59e0b;
    
    .card-icon {
      background: #f59e0b;
      color: white;
    }
  }
  
  &.card-danger {
    border-top: 4px solid #ef4444;
    
    .card-icon {
      background: #ef4444;
      color: white;
    }
  }
  
  &.card-info {
    border-top: 4px solid #6b7280;
    
    .card-icon {
      background: #6b7280;
      color: white;
    }
  }
  
  // 尺寸样式
  &.card-small {
    .card-header {
      padding: 12px 16px;
    }
    
    .card-content {
      padding: 12px 16px;
    }
    
    .card-footer {
      padding: 8px 16px;
    }
  }
  
  &.card-large {
    .card-header {
      padding: 24px 32px;
    }
    
    .card-content {
      padding: 24px 32px;
    }
    
    .card-footer {
      padding: 16px 32px;
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-secondary);
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .card-icon {
      width: 40px;
      height: 40px;
      border-radius: var(--radius-md);
      background: var(--bg-tertiary);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      color: var(--text-secondary);
    }
    
    .card-title-section {
      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0 0 4px 0;
      }
      
      .card-subtitle {
        font-size: 12px;
        color: var(--text-secondary);
        margin: 0;
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.card-content {
  padding: 20px;
  
  .card-loading,
  .card-error,
  .card-empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 120px;
    color: var(--text-secondary);
    gap: 12px;
    
    .el-icon {
      font-size: 32px;
      color: var(--text-light);
    }
    
    span {
      font-size: 14px;
    }
  }
  
  .card-error {
    .el-icon {
      color: var(--el-color-warning);
    }
  }
  
  .card-body {
    .number-display {
      text-align: left;
      padding: 8px 0 0 0;
      
      .card-title-inline {
        font-size: 14px;
        color: #6b7280;
        font-weight: 500;
        margin-bottom: 8px;
        line-height: 1.4;
      }
      
      .main-value {
        display: flex;
        align-items: baseline;
        gap: 4px;
        margin-bottom: 8px;
        
        .value-number {
          font-size: 32px;
          font-weight: 700;
          color: #1f2937;
          line-height: 1;
          letter-spacing: -0.02em;
        }
        
        .value-unit {
          font-size: 16px;
          color: #6b7280;
          font-weight: 500;
        }
      }
      
      .value-change {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        font-weight: 500;
        
        &.change-positive {
          color: #10b981;
        }
        
        &.change-negative {
          color: #ef4444;
        }
        
        &.change-neutral {
          color: #6b7280;
        }
        
        .el-icon {
          font-size: 12px;
        }
      }
    }
    
    .progress-display {
      .progress-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;
        
        .progress-label {
          font-size: 14px;
          color: var(--text-secondary);
        }
        
        .progress-value {
          font-size: 14px;
          font-weight: 600;
          color: var(--text-primary);
        }
      }
    }
    
    .list-display {
      .list-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 0;
        border-bottom: 1px solid var(--border-color);
        
        &:last-child {
          border-bottom: none;
        }
        
        .item-label {
          font-size: 14px;
          color: var(--text-secondary);
        }
        
        .item-value {
          font-size: 14px;
          font-weight: 500;
          color: var(--text-primary);
        }
      }
    }
    
    .custom-content {
      min-height: 60px;
    }
  }
}

.card-footer {
  padding: 12px 20px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-tertiary);
  
  .footer-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    
    .update-time {
      color: var(--text-light);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .data-card {
    .card-header {
      padding: 12px 16px;
      
      .header-left {
        gap: 8px;
        
        .card-icon {
          width: 32px;
          height: 32px;
          font-size: 16px;
        }
        
        .card-title-section {
          .card-title {
            font-size: 14px;
          }
        }
      }
    }
    
    .card-content {
      padding: 16px;
      
      .card-body {
        .number-display {
          .main-value {
            .value-number {
              font-size: 24px;
            }
            
            .value-unit {
              font-size: 14px;
            }
          }
        }
      }
    }
    
    .card-footer {
      padding: 8px 16px;
      
      .footer-info {
        flex-direction: column;
        gap: 4px;
        align-items: flex-start;
      }
    }
  }
}
</style>