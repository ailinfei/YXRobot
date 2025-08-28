<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    :width="width"
    :fullscreen="fullscreen"
    :top="top"
    :modal="modal"
    :modal-class="modalClass"
    :append-to-body="appendToBody"
    :lock-scroll="lockScroll"
    :custom-class="customClass"
    :open-delay="openDelay"
    :close-delay="closeDelay"
    :close-on-click-modal="closeOnClickModal"
    :close-on-press-escape="closeOnPressEscape"
    :show-close="showClose"
    :before-close="handleBeforeClose"
    :center="center"
    :align-center="alignCenter"
    :destroy-on-close="destroyOnClose"
    @open="handleOpen"
    @opened="handleOpened"
    @close="handleClose"
    @closed="handleClosed"
  >
    <!-- 自定义标题 -->
    <template #header v-if="$slots.header">
      <slot name="header" />
    </template>
    
    <!-- 对话框内容 -->
    <div class="dialog-content" :class="contentClass">
      <!-- 加载状态 -->
      <div v-if="loading" class="dialog-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>{{ loadingText }}</span>
      </div>
      
      <!-- 主要内容 -->
      <div v-else class="dialog-body">
        <slot />
      </div>
    </div>
    
    <!-- 自定义底部 -->
    <template #footer v-if="showFooter">
      <slot name="footer">
        <div class="dialog-footer">
          <el-button 
            v-if="showCancel"
            :size="buttonSize"
            @click="handleCancel"
            :disabled="loading || confirmLoading"
          >
            {{ cancelText }}
          </el-button>
          <el-button 
            v-if="showConfirm"
            type="primary"
            :size="buttonSize"
            :loading="confirmLoading"
            @click="handleConfirm"
            :disabled="loading"
          >
            {{ confirmText }}
          </el-button>
        </div>
      </slot>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Loading } from '@element-plus/icons-vue'

// 定义接口
interface DialogProps {
  modelValue: boolean
  title?: string
  width?: string | number
  fullscreen?: boolean
  top?: string
  modal?: boolean
  modalClass?: string
  appendToBody?: boolean
  lockScroll?: boolean
  customClass?: string
  contentClass?: string
  openDelay?: number
  closeDelay?: number
  closeOnClickModal?: boolean
  closeOnPressEscape?: boolean
  showClose?: boolean
  center?: boolean
  alignCenter?: boolean
  destroyOnClose?: boolean
  loading?: boolean
  loadingText?: string
  showFooter?: boolean
  showCancel?: boolean
  showConfirm?: boolean
  cancelText?: string
  confirmText?: string
  buttonSize?: 'large' | 'default' | 'small'
  confirmLoading?: boolean
  beforeClose?: (done: () => void) => void
}

// Props定义
const props = withDefaults(defineProps<DialogProps>(), {
  modelValue: false,
  title: '',
  width: '50%',
  fullscreen: false,
  top: '15vh',
  modal: true,
  modalClass: '',
  appendToBody: false,
  lockScroll: true,
  customClass: '',
  contentClass: '',
  openDelay: 0,
  closeDelay: 0,
  closeOnClickModal: true,
  closeOnPressEscape: true,
  showClose: true,
  center: false,
  alignCenter: false,
  destroyOnClose: false,
  loading: false,
  loadingText: '加载中...',
  showFooter: true,
  showCancel: true,
  showConfirm: true,
  cancelText: '取消',
  confirmText: '确定',
  buttonSize: 'default',
  confirmLoading: false
})

// Emits定义
const emit = defineEmits([
  'update:modelValue',
  'open',
  'opened',
  'close',
  'closed',
  'confirm',
  'cancel'
])

// 响应式数据
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 事件处理
const handleBeforeClose = (done: () => void) => {
  if (props.beforeClose) {
    props.beforeClose(done)
  } else {
    done()
  }
}

const handleOpen = () => {
  emit('open')
}

const handleOpened = () => {
  emit('opened')
}

const handleClose = () => {
  emit('close')
}

const handleClosed = () => {
  emit('closed')
}

const handleConfirm = () => {
  emit('confirm')
}

const handleCancel = () => {
  emit('cancel')
  dialogVisible.value = false
}

// 暴露方法给父组件
defineExpose({
  close: () => {
    dialogVisible.value = false
  },
  open: () => {
    dialogVisible.value = true
  }
})
</script>

<style lang="scss" scoped>
.dialog-content {
  min-height: 100px;
  
  &.no-padding {
    margin: -20px;
  }
  
  &.full-height {
    height: calc(100vh - 200px);
    display: flex;
    flex-direction: column;
  }
  
  .dialog-loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;
    color: var(--text-secondary);
    gap: 12px;
    
    .el-icon {
      font-size: 32px;
    }
    
    span {
      font-size: 14px;
    }
  }
  
  .dialog-body {
    flex: 1;
    overflow-y: auto;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  
  &.center {
    justify-content: center;
  }
  
  &.space-between {
    justify-content: space-between;
  }
}

// Element Plus 样式覆盖
:deep(.el-dialog) {
  border-radius: var(--radius-lg);
  overflow: hidden;
  
  .el-dialog__header {
    background: var(--bg-secondary);
    border-bottom: 1px solid var(--border-color);
    padding: 20px 24px 16px;
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
    }
    
    .el-dialog__headerbtn {
      top: 16px;
      right: 20px;
      
      .el-dialog__close {
        color: var(--text-secondary);
        font-size: 16px;
        
        &:hover {
          color: var(--text-primary);
        }
      }
    }
  }
  
  .el-dialog__body {
    padding: 24px;
    color: var(--text-primary);
    line-height: 1.6;
  }
  
  .el-dialog__footer {
    padding: 16px 24px 24px;
    border-top: 1px solid var(--border-color);
    background: var(--bg-secondary);
  }
}

// 全屏模式样式
:deep(.el-dialog.is-fullscreen) {
  .el-dialog__body {
    height: calc(100vh - 120px);
    overflow-y: auto;
  }
}

// 响应式设计
@media (max-width: 768px) {
  :deep(.el-dialog) {
    width: 95% !important;
    margin: 0 auto;
    
    .el-dialog__header {
      padding: 16px 20px 12px;
      
      .el-dialog__title {
        font-size: 16px;
      }
      
      .el-dialog__headerbtn {
        top: 12px;
        right: 16px;
      }
    }
    
    .el-dialog__body {
      padding: 20px;
    }
    
    .el-dialog__footer {
      padding: 12px 20px 20px;
      
      .dialog-footer {
        flex-direction: column-reverse;
        gap: 8px;
        
        .el-button {
          width: 100%;
        }
      }
    }
  }
}
</style>