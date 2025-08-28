<template>
  <div class="file-upload-manager">
    <el-upload
      ref="uploadRef"
      :action="uploadUrl"
      :headers="uploadHeaders"
      :data="uploadData"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-remove="handleRemove"
      :file-list="fileList"
      :list-type="preview ? 'picture-card' : 'text'"
      :multiple="maxCount > 1"
      :limit="maxCount"
      :accept="accept"
      :disabled="disabled"
    >
      <template v-if="preview">
        <el-icon><Plus /></el-icon>
      </template>
      <template v-else>
        <el-button type="primary" :disabled="disabled">
          <el-icon><Upload /></el-icon>
          {{ uploadText }}
        </el-button>
      </template>
    </el-upload>
    
    <div v-if="tip" class="upload-tip">{{ tip }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'

interface FileUploadManagerProps {
  modelValue: string[]
  maxCount?: number
  accept?: string
  preview?: boolean
  uploadText?: string
  tip?: string
  disabled?: boolean
}

const props = withDefaults(defineProps<FileUploadManagerProps>(), {
  maxCount: 10,
  accept: '*',
  preview: false,
  uploadText: '上传文件',
  tip: '',
  disabled: false
})

const emit = defineEmits<{
  'update:modelValue': [value: string[]]
}>()

// 响应式数据
const uploadRef = ref()
const fileList = ref<any[]>([])

// 计算属性
const uploadUrl = computed(() => '/api/upload/temp')
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token') || ''}`
}))
const uploadData = computed(() => ({}))

// 方法
const beforeUpload = (file: File) => {
  // 可以在这里添加文件验证逻辑
  return true
}

const handleSuccess = (response: any, file: any) => {
  if (response.code === 200) {
    const newFiles = [...props.modelValue, response.data.url]
    emit('update:modelValue', newFiles)
    ElMessage.success('文件上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleError = (error: any) => {
  console.error('上传失败:', error)
  ElMessage.error('文件上传失败')
}

const handleRemove = (file: any) => {
  const url = file.response?.data?.url || file.url
  const newFiles = props.modelValue.filter(f => f !== url)
  emit('update:modelValue', newFiles)
}

// 监听modelValue变化，更新fileList
watch(() => props.modelValue, (newValue) => {
  if (Array.isArray(newValue)) {
    fileList.value = newValue.map((url, index) => ({
      uid: index,
      name: `文件${index + 1}`,
      status: 'success',
      url: url
    }))
  } else {
    fileList.value = []
  }
}, { immediate: true })
</script>

<style lang="scss" scoped>
.file-upload-manager {
  .upload-tip {
    margin-top: 8px;
    font-size: 12px;
    color: #909399;
    line-height: 1.4;
  }
}
</style>