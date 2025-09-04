<!--
  通用文件上传组件
  支持多种文件类型上传，包括图片、文档、固件等
-->
<template>
  <div class="file-upload">
    <el-upload
      ref="uploadRef"
      :action="uploadUrl"
      :headers="uploadHeaders"
      :data="uploadData"
      :multiple="multiple"
      :limit="limit"
      :accept="acceptTypes"
      :before-upload="handleBeforeUpload"
      :on-progress="handleProgress"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-exceed="handleExceed"
      :on-remove="handleRemove"
      :file-list="fileList"
      :list-type="listType"
      :drag="drag"
      :disabled="disabled"
      :auto-upload="autoUpload"
      class="upload-component"
    >
      <!-- 上传触发区域 -->
      <template #trigger v-if="!drag">
        <el-button type="primary" :loading="uploading" :disabled="disabled">
          <el-icon><Upload /></el-icon>
          {{ triggerText }}
        </el-button>
      </template>
      
      <!-- 拖拽上传区域 -->
      <template #default v-if="drag">
        <div class="drag-upload-area">
          <el-icon class="upload-icon"><UploadFilled /></el-icon>
          <div class="upload-text">
            <p>{{ dragText }}</p>
            <p class="upload-hint">{{ hintText }}</p>
          </div>
        </div>
      </template>
      
      <!-- 提示信息 -->
      <template #tip v-if="showTip">
        <div class="upload-tip">
          <p>{{ tipText }}</p>
          <p v-if="maxSize">文件大小不超过 {{ maxSize }}MB</p>
        </div>
      </template>
    </el-upload>
    
    <!-- 上传进度 -->
    <div v-if="uploading && showProgress" class="upload-progress">
      <el-progress 
        :percentage="uploadProgress" 
        :status="uploadStatus"
        :stroke-width="6"
      />
      <p class="progress-text">{{ progressText }}</p>
    </div>
    
    <!-- 已上传文件列表 -->
    <div v-if="showFileList && uploadedFiles.length > 0" class="uploaded-files">
      <h4>已上传文件</h4>
      <div class="file-list">
        <div 
          v-for="file in uploadedFiles" 
          :key="file.data.fileId"
          class="file-item"
        >
          <div class="file-info">
            <el-icon class="file-icon">
              <Document v-if="!isImageFile(file.data.fileName)" />
              <Picture v-else />
            </el-icon>
            <div class="file-details">
              <p class="file-name">{{ file.data.fileName }}</p>
              <p class="file-size">{{ formatFileSize(file.data.fileSize) }}</p>
            </div>
          </div>
          <div class="file-actions">
            <el-button 
              text 
              type="primary" 
              size="small"
              @click="previewFile(file)"
              v-if="canPreview(file)"
            >
              预览
            </el-button>
            <el-button 
              text 
              type="primary" 
              size="small"
              @click="downloadFile(file)"
            >
              下载
            </el-button>
            <el-button 
              text 
              type="danger" 
              size="small"
              @click="deleteFile(file)"
              :disabled="disabled"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile, UploadRawFile, UploadProgressEvent } from 'element-plus'
import { Upload, UploadFilled, Document, Picture } from '@element-plus/icons-vue'
import { FileUploadService, FileUploadType, type FileUploadResponse } from '@/services/fileUploadService'

// 组件属性
interface Props {
  // 上传类型
  uploadType: FileUploadType
  // 设备ID（可选）
  deviceId?: string
  // 是否支持多文件上传
  multiple?: boolean
  // 最大文件数量
  limit?: number
  // 是否拖拽上传
  drag?: boolean
  // 列表类型
  listType?: 'text' | 'picture' | 'picture-card'
  // 是否禁用
  disabled?: boolean
  // 是否自动上传
  autoUpload?: boolean
  // 是否显示进度条
  showProgress?: boolean
  // 是否显示文件列表
  showFileList?: boolean
  // 是否显示提示
  showTip?: boolean
  // 自定义触发文本
  triggerText?: string
  // 自定义拖拽文本
  dragText?: string
  // 自定义提示文本
  tipText?: string
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  limit: 1,
  drag: false,
  listType: 'text',
  disabled: false,
  autoUpload: true,
  showProgress: true,
  showFileList: true,
  showTip: true,
  triggerText: '选择文件',
  dragText: '将文件拖到此处，或点击上传',
  tipText: '请选择要上传的文件'
})

// 组件事件
const emit = defineEmits<{
  'upload-success': [file: FileUploadResponse]
  'upload-error': [error: Error]
  'file-remove': [fileId: string]
}>()

// 响应式数据
const uploadRef = ref()
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadedFiles = ref<FileUploadResponse[]>([])
const fileList = ref<UploadFile[]>([])

// 计算属性
const uploadUrl = computed(() => {
  const configs = {
    [FileUploadType.DEVICE_IMAGE]: '/api/admin/devices/upload/image',
    [FileUploadType.DEVICE_DOCUMENT]: '/api/admin/devices/upload/document',
    [FileUploadType.FIRMWARE_FILE]: '/api/admin/devices/upload/firmware',
    [FileUploadType.DEVICE_MANUAL]: '/api/admin/devices/upload/manual',
    [FileUploadType.DEVICE_CERTIFICATE]: '/api/admin/devices/upload/certificate'
  }
  return configs[props.uploadType]
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

const uploadData = computed(() => {
  return {
    type: props.uploadType,
    deviceId: props.deviceId
  }
})

const acceptTypes = computed(() => {
  const typeMap = {
    [FileUploadType.DEVICE_IMAGE]: 'image/*',
    [FileUploadType.DEVICE_DOCUMENT]: '.pdf,.doc,.docx',
    [FileUploadType.FIRMWARE_FILE]: '.zip,.tar,.bin',
    [FileUploadType.DEVICE_MANUAL]: '.pdf',
    [FileUploadType.DEVICE_CERTIFICATE]: '.pdf,image/*'
  }
  return typeMap[props.uploadType]
})

const maxSize = computed(() => {
  const sizeMap = {
    [FileUploadType.DEVICE_IMAGE]: 5,
    [FileUploadType.DEVICE_DOCUMENT]: 10,
    [FileUploadType.FIRMWARE_FILE]: 50,
    [FileUploadType.DEVICE_MANUAL]: 20,
    [FileUploadType.DEVICE_CERTIFICATE]: 5
  }
  return sizeMap[props.uploadType]
})

const hintText = computed(() => {
  return `支持 ${acceptTypes.value} 格式，大小不超过 ${maxSize.value}MB`
})

const uploadStatus = computed(() => {
  if (uploadProgress.value === 100) return 'success'
  if (uploading.value) return undefined
  return 'exception'
})

const progressText = computed(() => {
  if (uploadProgress.value === 100) return '上传完成'
  if (uploading.value) return `上传中... ${uploadProgress.value}%`
  return '准备上传'
})

// 方法
const handleBeforeUpload = (file: UploadRawFile) => {
  return FileUploadService.validateFile(file, props.uploadType)
}

const handleProgress = (event: UploadProgressEvent) => {
  uploadProgress.value = Math.round(event.percent || 0)
  uploading.value = true
}

const handleSuccess = (response: any, file: UploadFile) => {
  uploading.value = false
  uploadProgress.value = 100
  
  if (response.code === 200) {
    uploadedFiles.value.push(response)
    emit('upload-success', response)
    ElMessage.success('文件上传成功')
  } else {
    handleError(new Error(response.message || '上传失败'), file)
  }
}

const handleError = (error: Error, file: UploadFile) => {
  uploading.value = false
  uploadProgress.value = 0
  emit('upload-error', error)
  ElMessage.error(`文件上传失败: ${error.message}`)
}

const handleExceed = () => {
  ElMessage.warning(`最多只能上传 ${props.limit} 个文件`)
}

const handleRemove = (file: UploadFile) => {
  // 从文件列表中移除
  const index = fileList.value.findIndex(f => f.uid === file.uid)
  if (index > -1) {
    fileList.value.splice(index, 1)
  }
}

const deleteFile = async (file: FileUploadResponse) => {
  try {
    await ElMessageBox.confirm('确定要删除这个文件吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await FileUploadService.deleteFile(file.data.fileId)
    
    const index = uploadedFiles.value.findIndex(f => f.data.fileId === file.data.fileId)
    if (index > -1) {
      uploadedFiles.value.splice(index, 1)
    }
    
    emit('file-remove', file.data.fileId)
    ElMessage.success('文件删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('文件删除失败')
    }
  }
}

const previewFile = (file: FileUploadResponse) => {
  const previewUrl = FileUploadService.getPreviewUrl(file.data.fileId)
  window.open(previewUrl, '_blank')
}

const downloadFile = (file: FileUploadResponse) => {
  const downloadUrl = FileUploadService.getFileUrl(file.data.fileId)
  const link = document.createElement('a')
  link.href = downloadUrl
  link.download = file.data.fileName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

const canPreview = (file: FileUploadResponse) => {
  const previewableTypes = ['pdf', 'jpg', 'jpeg', 'png', 'gif', 'webp']
  const extension = FileUploadService.getFileExtension(file.data.fileName).toLowerCase()
  return previewableTypes.includes(extension)
}

const isImageFile = (fileName: string) => {
  const imageTypes = ['jpg', 'jpeg', 'png', 'gif', 'webp']
  const extension = FileUploadService.getFileExtension(fileName).toLowerCase()
  return imageTypes.includes(extension)
}

const formatFileSize = (bytes: number) => {
  return FileUploadService.formatFileSize(bytes)
}

// 清空文件列表
const clearFiles = () => {
  uploadRef.value?.clearFiles()
  uploadedFiles.value = []
  fileList.value = []
}

// 手动上传
const submit = () => {
  uploadRef.value?.submit()
}

// 暴露方法
defineExpose({
  clearFiles,
  submit
})
</script>

<style lang="scss" scoped>
.file-upload {
  .upload-component {
    width: 100%;
  }
  
  .drag-upload-area {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
    border: 2px dashed #d9d9d9;
    border-radius: 6px;
    background: #fafafa;
    transition: border-color 0.3s;
    
    &:hover {
      border-color: #409eff;
    }
    
    .upload-icon {
      font-size: 48px;
      color: #c0c4cc;
      margin-bottom: 16px;
    }
    
    .upload-text {
      text-align: center;
      
      p {
        margin: 0 0 8px 0;
        color: #606266;
        font-size: 14px;
      }
      
      .upload-hint {
        color: #909399;
        font-size: 12px;
      }
    }
  }
  
  .upload-tip {
    margin-top: 8px;
    
    p {
      margin: 0 0 4px 0;
      color: #909399;
      font-size: 12px;
      line-height: 1.4;
    }
  }
  
  .upload-progress {
    margin-top: 16px;
    
    .progress-text {
      margin: 8px 0 0 0;
      color: #606266;
      font-size: 12px;
      text-align: center;
    }
  }
  
  .uploaded-files {
    margin-top: 20px;
    
    h4 {
      margin: 0 0 12px 0;
      color: #303133;
      font-size: 14px;
      font-weight: 500;
    }
    
    .file-list {
      border: 1px solid #e4e7ed;
      border-radius: 6px;
      overflow: hidden;
      
      .file-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 12px 16px;
        border-bottom: 1px solid #f5f7fa;
        
        &:last-child {
          border-bottom: none;
        }
        
        .file-info {
          display: flex;
          align-items: center;
          flex: 1;
          
          .file-icon {
            font-size: 20px;
            color: #909399;
            margin-right: 12px;
          }
          
          .file-details {
            .file-name {
              margin: 0 0 4px 0;
              color: #303133;
              font-size: 14px;
              font-weight: 500;
            }
            
            .file-size {
              margin: 0;
              color: #909399;
              font-size: 12px;
            }
          }
        }
        
        .file-actions {
          display: flex;
          gap: 8px;
        }
      }
    }
  }
}
</style>