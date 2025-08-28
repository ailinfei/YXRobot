<template>
  <div class="file-upload">
    <!-- 上传区域 -->
    <el-upload
      ref="uploadRef"
      :class="uploadClass"
      :action="action"
      :headers="headers"
      :data="data"
      :name="name"
      :multiple="multiple"
      :drag="drag"
      :accept="accept"
      :file-list="fileList"
      :auto-upload="autoUpload"
      :disabled="disabled"
      :limit="limit"
      :show-file-list="showFileList"
      :list-type="listType"
      :before-upload="handleBeforeUpload"
      :on-progress="handleProgress"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-remove="handleRemove"
      :on-exceed="handleExceed"
      :on-preview="handlePreview"
    >
      <!-- 上传触发区域 -->
      <template v-if="listType === 'picture-card'">
        <div class="upload-card-trigger">
          <el-icon><Plus /></el-icon>
          <div class="upload-text">{{ uploadText }}</div>
        </div>
      </template>
      
      <template v-else-if="drag">
        <div class="upload-drag-area">
          <el-icon class="upload-icon"><UploadFilled /></el-icon>
          <div class="upload-text">{{ uploadText }}</div>
          <div class="upload-hint">{{ uploadHint }}</div>
        </div>
      </template>
      
      <template v-else>
        <el-button :type="buttonType" :icon="Upload" :loading="uploading">
          {{ uploadText }}
        </el-button>
      </template>
      
      <!-- 上传提示 -->
      <template #tip v-if="showTip">
        <div class="upload-tip">
          <div class="tip-content">
            <span v-if="accept">支持格式: {{ formatAccept(accept) }}</span>
            <span v-if="maxSize">单个文件不超过 {{ formatSize(maxSize) }}</span>
            <span v-if="limit">最多上传 {{ limit }} 个文件</span>
          </div>
        </div>
      </template>
    </el-upload>

    <!-- 自定义文件列表 -->
    <div v-if="showCustomList && fileList.length > 0" class="custom-file-list">
      <div class="file-list-header">
        <span>已上传文件 ({{ fileList.length }})</span>
        <el-button 
          v-if="multiple" 
          type="danger" 
          size="small" 
          text
          @click="handleClearAll"
        >
          清空全部
        </el-button>
      </div>
      
      <div class="file-list-content">
        <div 
          v-for="(file, index) in fileList" 
          :key="file.uid || index"
          class="file-item"
          :class="{ 'is-uploading': file.status === 'uploading' }"
        >
          <!-- 文件图标 -->
          <div class="file-icon">
            <el-icon v-if="isImage(file)"><Picture /></el-icon>
            <el-icon v-else-if="isVideo(file)"><VideoPlay /></el-icon>
            <el-icon v-else-if="isDocument(file)"><Document /></el-icon>
            <el-icon v-else><Files /></el-icon>
          </div>
          
          <!-- 文件信息 -->
          <div class="file-info">
            <div class="file-name" :title="file.name">{{ file.name }}</div>
            <div class="file-meta">
              <span class="file-size">{{ formatFileSize(file.size) }}</span>
              <span class="file-status" :class="file.status">
                {{ getStatusText(file.status) }}
              </span>
            </div>
            
            <!-- 上传进度 -->
            <el-progress 
              v-if="file.status === 'uploading'" 
              :percentage="file.percentage || 0"
              :stroke-width="2"
              :show-text="false"
            />
          </div>
          
          <!-- 文件操作 -->
          <div class="file-actions">
            <el-button 
              v-if="file.status === 'success'"
              type="primary" 
              size="small" 
              text
              @click="handlePreview(file)"
            >
              预览
            </el-button>
            <el-button 
              v-if="file.url"
              type="primary" 
              size="small" 
              text
              @click="handleDownload(file)"
            >
              下载
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              text
              @click="handleRemove(file)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="图片预览"
      width="60%"
      :before-close="handleClosePreview"
    >
      <div class="preview-container">
        <el-image
          :src="previewUrl"
          fit="contain"
          class="preview-image"
          :preview-src-list="[previewUrl]"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile, UploadFiles, UploadProps } from 'element-plus'
import {
  Plus,
  Upload,
  UploadFilled,
  Picture,
  VideoPlay,
  Document,
  Files
} from '@element-plus/icons-vue'

// 定义接口
interface FileUploadProps {
  modelValue?: UploadFile[]
  action?: string
  headers?: Record<string, any>
  data?: Record<string, any>
  name?: string
  multiple?: boolean
  drag?: boolean
  accept?: string
  limit?: number
  maxSize?: number // MB
  autoUpload?: boolean
  disabled?: boolean
  showFileList?: boolean
  showCustomList?: boolean
  showTip?: boolean
  listType?: 'text' | 'picture' | 'picture-card'
  buttonType?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  uploadText?: string
  uploadHint?: string
}

// Props定义
const props = withDefaults(defineProps<FileUploadProps>(), {
  modelValue: () => [],
  action: '/api/v1/upload/temp', // 默认上传地址
  headers: () => ({}),
  data: () => ({}),
  name: 'file',
  multiple: false,
  drag: false,
  accept: '',
  limit: 0,
  maxSize: 10,
  autoUpload: false, // 改为手动上传，由父组件控制
  disabled: false,
  showFileList: true,
  showCustomList: false,
  showTip: true,
  listType: 'text',
  buttonType: 'primary',
  uploadText: '选择文件',
  uploadHint: '将文件拖到此处，或点击上传'
})

// Emits定义
const emit = defineEmits([
  'update:modelValue',
  'success',
  'error',
  'progress',
  'remove',
  'exceed',
  'preview'
])

// 响应式数据
const uploadRef = ref()
const uploading = ref(false)
const fileList = ref<UploadFile[]>([...props.modelValue])
const previewVisible = ref(false)
const previewUrl = ref('')

// 计算属性
const uploadClass = computed(() => {
  return {
    'upload-disabled': props.disabled,
    'upload-drag': props.drag,
    'upload-picture-card': props.listType === 'picture-card'
  }
})

// 文件类型判断
const isImage = (file: UploadFile) => {
  const imageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
  return imageTypes.includes(file.raw?.type || '') || /\.(jpg|jpeg|png|gif|webp)$/i.test(file.name || '')
}

const isVideo = (file: UploadFile) => {
  const videoTypes = ['video/mp4', 'video/avi', 'video/mov', 'video/wmv']
  return videoTypes.includes(file.raw?.type || '') || /\.(mp4|avi|mov|wmv)$/i.test(file.name || '')
}

const isDocument = (file: UploadFile) => {
  const docTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']
  return docTypes.includes(file.raw?.type || '') || /\.(pdf|doc|docx)$/i.test(file.name || '')
}

// 格式化函数
const formatAccept = (accept: string) => {
  return accept.split(',').map(type => type.trim()).join(', ')
}

const formatSize = (size: number) => {
  return `${size}MB`
}

const formatFileSize = (size?: number) => {
  if (!size) return '0B'
  const units = ['B', 'KB', 'MB', 'GB']
  let index = 0
  let fileSize = size
  
  while (fileSize >= 1024 && index < units.length - 1) {
    fileSize /= 1024
    index++
  }
  
  return `${fileSize.toFixed(1)}${units[index]}`
}

const getStatusText = (status?: string) => {
  const statusMap: Record<string, string> = {
    ready: '准备上传',
    uploading: '上传中',
    success: '上传成功',
    fail: '上传失败'
  }
  return statusMap[status || ''] || '未知状态'
}

// 事件处理
const handleBeforeUpload = (rawFile: File) => {
  // 文件大小检查
  if (props.maxSize && rawFile.size / 1024 / 1024 > props.maxSize) {
    ElMessage.error(`文件大小不能超过 ${props.maxSize}MB`)
    return false
  }
  
  // 文件类型检查
  if (props.accept) {
    const acceptTypes = props.accept.split(',').map(type => type.trim())
    const isValidType = acceptTypes.some(type => {
      if (type.startsWith('.')) {
        return rawFile.name.toLowerCase().endsWith(type.toLowerCase())
      } else {
        return rawFile.type.includes(type.replace('*', ''))
      }
    })
    
    if (!isValidType) {
      ElMessage.error(`文件格式不支持，请选择 ${formatAccept(props.accept)} 格式的文件`)
      return false
    }
  }
  
  uploading.value = true
  return true
}

const handleProgress = (event: any, file: UploadFile) => {
  emit('progress', event, file)
}

const handleSuccess = (response: any, file: UploadFile, files: UploadFiles) => {
  uploading.value = false
  fileList.value = [...files]
  emit('update:modelValue', fileList.value)
  emit('success', response, file, files)
  ElMessage.success('文件上传成功')
}

const handleError = (error: Error, file: UploadFile, files: UploadFiles) => {
  uploading.value = false
  emit('error', error, file, files)
  ElMessage.error('文件上传失败')
}

const handleRemove = async (file: UploadFile) => {
  try {
    await ElMessageBox.confirm('确定要删除这个文件吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const index = fileList.value.findIndex(item => item.uid === file.uid)
    if (index > -1) {
      fileList.value.splice(index, 1)
      emit('update:modelValue', fileList.value)
      emit('remove', file)
    }
  } catch {
    // 用户取消删除
  }
}

const handleExceed = (files: File[], fileList: UploadFiles) => {
  ElMessage.warning(`最多只能上传 ${props.limit} 个文件`)
  emit('exceed', files, fileList)
}

const handlePreview = (file: UploadFile) => {
  if (isImage(file) && file.url) {
    previewUrl.value = file.url
    previewVisible.value = true
  } else if (file.url) {
    window.open(file.url, '_blank')
  }
  emit('preview', file)
}

const handleDownload = (file: UploadFile) => {
  if (file.url) {
    const link = document.createElement('a')
    link.href = file.url
    link.download = file.name || 'download'
    link.click()
  }
}

const handleClearAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有文件吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    fileList.value = []
    emit('update:modelValue', fileList.value)
    uploadRef.value?.clearFiles()
  } catch {
    // 用户取消清空
  }
}

const handleClosePreview = () => {
  previewVisible.value = false
  previewUrl.value = ''
}

// 监听外部文件列表变化
watch(() => props.modelValue, (newVal) => {
  fileList.value = [...newVal]
}, { deep: true })

// 暴露方法给父组件
defineExpose({
  clearFiles: () => uploadRef.value?.clearFiles(),
  abort: () => uploadRef.value?.abort(),
  submit: () => uploadRef.value?.submit()
})
</script>

<style lang="scss" scoped>
.file-upload {
  .upload-card-trigger {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    color: var(--text-secondary);
    
    .el-icon {
      font-size: 28px;
      margin-bottom: 8px;
    }
    
    .upload-text {
      font-size: 14px;
    }
  }
  
  .upload-drag-area {
    text-align: center;
    padding: 40px 20px;
    
    .upload-icon {
      font-size: 48px;
      color: var(--text-light);
      margin-bottom: 16px;
    }
    
    .upload-text {
      font-size: 16px;
      color: var(--text-primary);
      margin-bottom: 8px;
    }
    
    .upload-hint {
      font-size: 14px;
      color: var(--text-secondary);
    }
  }
  
  .upload-tip {
    margin-top: 8px;
    
    .tip-content {
      font-size: 12px;
      color: var(--text-light);
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      
      span {
        &:not(:last-child)::after {
          content: '|';
          margin-left: 8px;
          color: var(--border-color);
        }
      }
    }
  }
}

.custom-file-list {
  margin-top: 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  overflow: hidden;
  
  .file-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background: var(--bg-secondary);
    border-bottom: 1px solid var(--border-color);
    font-size: 14px;
    font-weight: 500;
    color: var(--text-primary);
  }
  
  .file-list-content {
    max-height: 300px;
    overflow-y: auto;
  }
  
  .file-item {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    border-bottom: 1px solid var(--border-color);
    transition: background-color 0.2s ease;
    
    &:last-child {
      border-bottom: none;
    }
    
    &:hover {
      background: var(--bg-secondary);
    }
    
    &.is-uploading {
      background: rgba(255, 90, 95, 0.05);
    }
    
    .file-icon {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--bg-tertiary);
      border-radius: var(--radius-sm);
      margin-right: 12px;
      
      .el-icon {
        font-size: 16px;
        color: var(--text-secondary);
      }
    }
    
    .file-info {
      flex: 1;
      min-width: 0;
      
      .file-name {
        font-size: 14px;
        color: var(--text-primary);
        margin-bottom: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .file-meta {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 12px;
        
        .file-size {
          color: var(--text-secondary);
        }
        
        .file-status {
          &.success {
            color: var(--el-color-success);
          }
          
          &.uploading {
            color: var(--el-color-primary);
          }
          
          &.fail {
            color: var(--el-color-danger);
          }
        }
      }
      
      :deep(.el-progress) {
        margin-top: 4px;
        
        .el-progress-bar__outer {
          height: 4px;
        }
      }
    }
    
    .file-actions {
      display: flex;
      gap: 8px;
      margin-left: 12px;
    }
  }
}

.preview-container {
  text-align: center;
  
  .preview-image {
    max-width: 100%;
    max-height: 60vh;
  }
}

// Element Plus 样式覆盖
:deep(.el-upload) {
  &.upload-disabled {
    cursor: not-allowed;
    
    .el-upload-dragger {
      background-color: var(--bg-tertiary);
      border-color: var(--border-color);
      cursor: not-allowed;
    }
  }
  
  &.upload-picture-card {
    .el-upload-dragger {
      width: 148px;
      height: 148px;
      border-radius: var(--radius-md);
    }
  }
}

:deep(.el-upload-dragger) {
  border: 2px dashed var(--border-color);
  border-radius: var(--radius-lg);
  background: var(--bg-secondary);
  transition: all 0.3s ease;
  
  &:hover {
    border-color: var(--primary-color);
    background: rgba(255, 90, 95, 0.05);
  }
  
  &.is-dragover {
    border-color: var(--primary-color);
    background: rgba(255, 90, 95, 0.1);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .custom-file-list {
    .file-item {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
      
      .file-info {
        width: 100%;
      }
      
      .file-actions {
        margin-left: 0;
        width: 100%;
        justify-content: flex-end;
      }
    }
  }
}
</style>