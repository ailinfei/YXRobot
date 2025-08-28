<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    :label-width="labelWidth"
    :label-position="labelPosition"
    :inline="inline"
    :size="size"
    :disabled="disabled"
    :validate-on-rule-change="validateOnRuleChange"
    :hide-required-asterisk="hideRequiredAsterisk"
    :show-message="showMessage"
    :inline-message="inlineMessage"
    :status-icon="statusIcon"
    @validate="handleValidate"
    class="form-validator"
    :class="formClass"
  >
    <slot :form="formData" :rules="formRules" />
    
    <!-- 表单操作按钮 -->
    <div v-if="showActions" class="form-actions" :class="actionsClass">
      <slot name="actions" :form="formData" :validate="validateForm" :reset="resetForm">
        <el-button 
          v-if="showReset"
          :size="size"
          @click="resetForm"
          :disabled="disabled || loading"
        >
          {{ resetText }}
        </el-button>
        <el-button 
          v-if="showSubmit"
          type="primary"
          :size="size"
          :loading="loading"
          @click="handleSubmit"
          :disabled="disabled"
        >
          {{ submitText }}
        </el-button>
      </slot>
    </div>
  </el-form>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

// 定义接口
interface FormValidatorProps {
  modelValue: Record<string, any>
  rules?: FormRules
  labelWidth?: string | number
  labelPosition?: 'left' | 'right' | 'top'
  inline?: boolean
  size?: 'large' | 'default' | 'small'
  disabled?: boolean
  validateOnRuleChange?: boolean
  hideRequiredAsterisk?: boolean
  showMessage?: boolean
  inlineMessage?: boolean
  statusIcon?: boolean
  showActions?: boolean
  showSubmit?: boolean
  showReset?: boolean
  submitText?: string
  resetText?: string
  loading?: boolean
  formClass?: string
  actionsClass?: string
  autoValidate?: boolean
  validateOnMount?: boolean
}

// Props定义
const props = withDefaults(defineProps<FormValidatorProps>(), {
  modelValue: () => ({}),
  rules: () => ({}),
  labelWidth: '120px',
  labelPosition: 'right',
  inline: false,
  size: 'default',
  disabled: false,
  validateOnRuleChange: true,
  hideRequiredAsterisk: false,
  showMessage: true,
  inlineMessage: false,
  statusIcon: false,
  showActions: true,
  showSubmit: true,
  showReset: true,
  submitText: '提交',
  resetText: '重置',
  loading: false,
  formClass: '',
  actionsClass: '',
  autoValidate: false,
  validateOnMount: false
})

// Emits定义
const emit = defineEmits([
  'update:modelValue',
  'submit',
  'reset',
  'validate',
  'field-change'
])

// 响应式数据
const formRef = ref<FormInstance>()
const formData = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formRules = computed(() => props.rules)

// 验证表单
const validateForm = async (): Promise<boolean> => {
  if (!formRef.value) return false
  
  try {
    await formRef.value.validate()
    return true
  } catch (error) {
    console.warn('表单验证失败:', error)
    return false
  }
}

// 验证指定字段
const validateField = async (prop: string): Promise<boolean> => {
  if (!formRef.value) return false
  
  try {
    await formRef.value.validateField(prop)
    return true
  } catch (error) {
    console.warn(`字段 ${prop} 验证失败:`, error)
    return false
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  emit('reset')
}

// 清除验证
const clearValidate = (props?: string | string[]) => {
  if (formRef.value) {
    formRef.value.clearValidate(props)
  }
}

// 滚动到错误字段
const scrollToField = (prop: string) => {
  if (formRef.value) {
    formRef.value.scrollToField(prop)
  }
}

// 事件处理
const handleSubmit = async () => {
  const isValid = await validateForm()
  if (isValid) {
    emit('submit', formData.value)
  } else {
    ElMessage.warning('请检查表单填写是否正确')
  }
}

const handleValidate = (prop: string, isValid: boolean, message: string) => {
  emit('validate', { prop, isValid, message })
}

// 监听表单数据变化
watch(
  () => props.modelValue,
  (newVal, oldVal) => {
    // 检测字段变化
    if (oldVal) {
      Object.keys(newVal).forEach(key => {
        if (newVal[key] !== oldVal[key]) {
          emit('field-change', { field: key, value: newVal[key], oldValue: oldVal[key] })
          
          // 自动验证
          if (props.autoValidate) {
            validateField(key)
          }
        }
      })
    }
  },
  { deep: true }
)

// 生命周期
onMounted(() => {
  if (props.validateOnMount) {
    validateForm()
  }
})

// 暴露方法给父组件
defineExpose({
  validate: validateForm,
  validateField,
  resetFields: resetForm,
  clearValidate,
  scrollToField,
  getFormRef: () => formRef.value
})
</script>

<style lang="scss" scoped>
.form-validator {
  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 24px;
    padding-top: 16px;
    border-top: 1px solid var(--border-color);
    
    &.center {
      justify-content: center;
    }
    
    &.left {
      justify-content: flex-start;
    }
    
    &.space-between {
      justify-content: space-between;
    }
    
    &.no-border {
      border-top: none;
      padding-top: 0;
    }
  }
}

// Element Plus 样式覆盖
:deep(.el-form) {
  .el-form-item {
    margin-bottom: 20px;
    
    .el-form-item__label {
      color: var(--text-primary);
      font-weight: 500;
      
      &::before {
        color: var(--el-color-danger);
      }
    }
    
    .el-form-item__content {
      .el-form-item__error {
        color: var(--el-color-danger);
        font-size: 12px;
        line-height: 1.4;
      }
    }
  }
  
  // 内联表单样式
  &.el-form--inline {
    .el-form-item {
      margin-right: 16px;
      margin-bottom: 16px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .form-validator {
    :deep(.el-form) {
      .el-form-item {
        .el-form-item__label {
          text-align: left !important;
        }
      }
      
      &:not(.el-form--inline) {
        .el-form-item {
          display: block;
          
          .el-form-item__label {
            display: block;
            width: 100% !important;
            margin-bottom: 8px;
            line-height: 1.4;
          }
          
          .el-form-item__content {
            margin-left: 0 !important;
          }
        }
      }
    }
    
    .form-actions {
      flex-direction: column-reverse;
      
      .el-button {
        width: 100%;
      }
    }
  }
}
</style>