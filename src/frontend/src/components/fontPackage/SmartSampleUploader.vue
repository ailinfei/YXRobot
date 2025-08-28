<!--
  智能样本上传组件
  提供增强的拖拽上传体验、文件预览和编辑功能
-->
<template>
  <div class="smart-sample-uploader">
    <!-- 拖拽上传区域 -->
    <div 
      class="upload-zone"
      :class="{
        'drag-over': isDragOver,
        'has-files': uploadedFiles.length > 0,
        'uploading': isUploading
      }"
      @drop="handleDrop"
      @dragover="handleDragOver"
      @dragenter="handleDragEnter"
      @dragleave="handleDragLeave"
      @click="triggerFileSelect"
    >
      <input
        ref="fileInputRef"
        type="file"
        multiple
        accept="image/*"
        style="display: none"
        @change="handleFileSelect"
      />

      <div class="upload-content">
        <div v-if="!isUploading" class="upload-icon">
          <el-icon size="48" :class="{ 'bounce': isDragOver }">
            <UploadFilled />
          </el-icon>
        </div>
        
        <div v-else class="upload-loading">
          <el-icon size="48" class="rotating">
            <Loading />
          </el-icon>
        </div>

        <div class="upload-text">
          <h3 v-if="!isUploading">
            {{ isDragOver ? '释放文件开始上传' : '拖拽文件到此处或点击上传' }}
          </h3>
          <h3 v-else>正在上传文件...</h3>
          
          <p class="upload-hint">
            支持 JPG、PNG、GIF 格式，单个文件不超过 10MB
          </p>
          
          <div v-if="uploadedFiles.length > 0" class="file-count">
            已上传 {{ uploadedFiles.length }} 个文件
          </div>
        </div>

        <!-- 智能提示 -->
        <div v-if="smartTips.length > 0" class="smart-tips">
          <div class="tips-header">
            <el-icon><InfoFilled /></el-icon>
            智能提示
          </div>
          <div class="tips-content">
            <div 
              v-for="tip in smartTips.slice(0, 2)" 
              :key="tip.id"
              class="tip-item"
              :class="tip.type"
            >
              <el-icon>
                <Check v-if="tip.type === 'success'" />
                <Warning v-else-if="tip.type === 'warning'" />
                <InfoFilled v-else />
              </el-icon>
              <span>{{ tip.message }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 上传进度 -->
    <div v-if="uploadProgress.total > 0" class="upload-progress">
      <div class="progress-header">
        <span>上传进度: {{ uploadProgress.completed }}/{{ uploadProgress.total }}</span>
        <span>{{ Math.round((uploadProgress.completed / uploadProgress.total) * 100) }}%</span>
      </div>
      <el-progress 
        :percentage="Math.round((uploadProgress.completed / uploadProgress.total) * 100)"
        :status="uploadProgress.status"
        :stroke-width="6"
      />
    </div>

    <!-- 文件列表和预览 -->
    <div v-if="uploadedFiles.length > 0" class="file-list">
      <div class="list-header">
        <h4>已上传文件 ({{ uploadedFiles.length }})</h4>
        <div class="list-actions">
          <el-button size="small" @click="toggleViewMode">
            <el-icon><Grid v-if="viewMode === 'list'" /><List v-else /></el-icon>
            {{ viewMode === 'list' ? '网格视图' : '列表视图' }}
          </el-button>
          <el-button size="small" type="danger" @click="clearAllFiles">
            <el-icon><Delete /></el-icon>
            清空所有
          </el-button>
        </div>
      </div>

      <!-- 列表视图 -->
      <div v-if="viewMode === 'list'" class="file-list-view">
        <div 
          v-for="file in uploadedFiles" 
          :key="file.uid"
          class="file-item"
          :class="{ 'selected': selectedFiles.includes(file.uid) }"
        >
          <div class="file-checkbox">
            <el-checkbox 
              :model-value="selectedFiles.includes(file.uid)"
              @change="toggleFileSelection(file.uid)"
            />
          </div>
          
          <div class="file-preview" @click="previewFile(file)">
            <img :src="file.url" :alt="file.name" />
          </div>
          
          <div class="file-info">
            <div class="file-name" :title="file.name">{{ file.name }}</div>
            <div class="file-meta">
              <span class="file-size">{{ formatFileSize(file.size) }}</span>
              <span class="file-status" :class="file.status">{{ getStatusText(file.status) }}</span>
            </div>
            <div v-if="file.recognizedCharacters" class="recognized-chars">
              识别字符: 
              <el-tag 
                v-for="char in file.recognizedCharacters.slice(0, 3)" 
                :key="char"
                size="small"
                class="char-tag"
              >
                {{ char }}
              </el-tag>
              <span v-if="file.recognizedCharacters.length > 3">
                +{{ file.recognizedCharacters.length - 3 }}
              </span>
            </div>
          </div>
          
          <div class="file-actions">
            <el-button size="small" @click="editFile(file)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button size="small" @click="previewFile(file)">
              <el-icon><View /></el-icon>
            </el-button>
            <el-button size="small" type="danger" @click="removeFile(file)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 网格视图 -->
      <div v-else class="file-grid-view">
        <div 
          v-for="file in uploadedFiles" 
          :key="file.uid"
          class="file-card"
          :class="{ 'selected': selectedFiles.includes(file.uid) }"
        >
          <div class="card-header">
            <el-checkbox 
              :model-value="selectedFiles.includes(file.uid)"
              @change="toggleFileSelection(file.uid)"
            />
            <el-dropdown @command="handleFileAction" trigger="click">
              <el-button size="small" text>
                <el-icon><MoreFilled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="{ action: 'preview', file }">预览</el-dropdown-item>
                  <el-dropdown-item :command="{ action: 'edit', file }">编辑</el-dropdown-item>
                  <el-dropdown-item :command="{ action: 'download', file }">下载</el-dropdown-item>
                  <el-dropdown-item :command="{ action: 'remove', file }" divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          
          <div class="card-preview" @click="previewFile(file)">
            <img :src="file.url" :alt="file.name" />
            <div class="preview-overlay">
              <el-icon><View /></el-icon>
            </div>
          </div>
          
          <div class="card-info">
            <div class="file-name" :title="file.name">{{ file.name }}</div>
            <div class="file-size">{{ formatFileSize(file.size) }}</div>
            <div v-if="file.recognizedCharacters" class="recognized-chars">
              <el-tag 
                v-for="char in file.recognizedCharacters.slice(0, 2)" 
                :key="char"
                size="small"
                class="char-tag"
              >
                {{ char }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 批量操作 -->
      <div v-if="selectedFiles.length > 0" class="batch-actions">
        <div class="batch-info">
          已选择 {{ selectedFiles.length }} 个文件
        </div>
        <div class="batch-buttons">
          <el-button size="small" @click="selectAllFiles">全选</el-button>
          <el-button size="small" @click="clearSelection">取消选择</el-button>
          <el-button size="small" type="danger" @click="removeSelectedFiles">删除选中</el-button>
        </div>
      </div>
    </div>

    <!-- 文件预览对话框 -->
    <el-dialog
      v-model="previewDialog.visible"
      :title="previewDialog.file?.name"
      width="80%"
      center
    >
      <div v-if="previewDialog.file" class="file-preview-dialog">
        <div class="preview-image">
          <img :src="previewDialog.file.url" :alt="previewDialog.file.name" />
        </div>
        <div class="preview-info">
          <div class="info-section">
            <h4>文件信息</h4>
            <div class="info-item">
              <span class="label">文件名:</span>
              <span class="value">{{ previewDialog.file.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">文件大小:</span>
              <span class="value">{{ formatFileSize(previewDialog.file.size) }}</span>
            </div>
            <div class="info-item">
              <span class="label">文件类型:</span>
              <span class="value">{{ previewDialog.file.type }}</span>
            </div>
          </div>
          
          <div v-if="previewDialog.file.recognizedCharacters" class="info-section">
            <h4>识别结果</h4>
            <div class="recognized-chars-list">
              <el-tag 
                v-for="char in previewDialog.file.recognizedCharacters" 
                :key="char"
                class="char-tag"
              >
                {{ char }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 文件编辑对话框 -->
    <el-dialog
      v-model="editDialog.visible"
      title="编辑文件信息"
      width="60%"
      center
    >
      <div v-if="editDialog.file" class="file-edit-dialog">
        <el-form :model="editDialog.form" label-width="100px">
          <el-form-item label="文件名">
            <el-input v-model="editDialog.form.name" />
          </el-form-item>
          
          <el-form-item label="识别字符">
            <el-input
              v-model="editDialog.form.charactersInput"
              type="textarea"
              :rows="3"
              placeholder="请输入该文件包含的字符，用空格分隔"
            />
          </el-form-item>
          
          <el-form-item label="字符标签">
            <div class="character-tags">
              <el-tag
                v-for="char in editDialog.form.characters"
                :key="char"
                closable
                @close="removeCharacterFromEdit(char)"
                class="char-tag"
              >
                {{ char }}
              </el-tag>
            </div>
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveFileEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  UploadFilled, 
  Loading, 
  InfoFilled, 
  Check, 
  Warning, 
  Grid, 
  List, 
  Delete, 
  Edit, 
  View, 
  MoreFilled 
} from '@element-plus/icons-vue'

interface UploadedFile {
  uid: string
  name: string
  url: string
  raw: File
  size: number
  type: string
  status: 'ready' | 'uploading' | 'success' | 'error'
  recognizedCharacters?: string[]
}

interface SmartTip {
  id: string
  type: 'success' | 'warning' | 'info'
  message: string
}

interface Props {
  modelValue: UploadedFile[]
  maxFiles?: number
  maxSize?: number
  accept?: string[]
}

interface Emits {
  (e: 'update:modelValue', files: UploadedFile[]): void
  (e: 'file-added', file: UploadedFile): void
  (e: 'file-removed', file: UploadedFile): void
  (e: 'files-changed', files: UploadedFile[]): void
}

const props = withDefaults(defineProps<Props>(), {
  maxFiles: 50,
  maxSize: 10 * 1024 * 1024, // 10MB
  accept: () => ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']
})

const emit = defineEmits<Emits>()

// 响应式数据
const fileInputRef = ref<HTMLInputElement>()
const isDragOver = ref(false)
const isUploading = ref(false)
const viewMode = ref<'list' | 'grid'>('list')
const selectedFiles = ref<string[]>([])

const uploadProgress = ref({
  total: 0,
  completed: 0,
  status: 'success' as 'success' | 'exception' | 'warning'
})

const smartTips = ref<SmartTip[]>([])

const previewDialog = ref({
  visible: false,
  file: null as UploadedFile | null
})

const editDialog = ref({
  visible: false,
  file: null as UploadedFile | null,
  form: {
    name: '',
    charactersInput: '',
    characters: [] as string[]
  }
})

// 计算属性
const uploadedFiles = computed({
  get: () => props.modelValue,
  set: (files) => emit('update:modelValue', files)
})

// 方法
const triggerFileSelect = () => {
  fileInputRef.value?.click()
}

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = Array.from(target.files || [])
  processFiles(files)
  target.value = '' // 清空input以允许重复选择同一文件
}

const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = false
  
  const files = Array.from(event.dataTransfer?.files || [])
  processFiles(files)
}

const handleDragOver = (event: DragEvent) => {
  event.preventDefault()
}

const handleDragEnter = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = true
}

const handleDragLeave = (event: DragEvent) => {
  event.preventDefault()
  // 只有当离开整个拖拽区域时才设置为false
  if (!event.currentTarget?.contains(event.relatedTarget as Node)) {
    isDragOver.value = false
  }
}

const processFiles = async (files: File[]) => {
  if (files.length === 0) return

  // 验证文件
  const validFiles = files.filter(file => validateFile(file))
  
  if (validFiles.length === 0) {
    ElMessage.warning('没有有效的文件可以上传')
    return
  }

  // 检查文件数量限制
  if (uploadedFiles.value.length + validFiles.length > props.maxFiles) {
    ElMessage.warning(`最多只能上传 ${props.maxFiles} 个文件`)
    return
  }

  isUploading.value = true
  uploadProgress.value = {
    total: validFiles.length,
    completed: 0,
    status: 'success'
  }

  try {
    for (const file of validFiles) {
      await uploadFile(file)
      uploadProgress.value.completed++
    }
    
    generateSmartTips()
    ElMessage.success(`成功上传 ${validFiles.length} 个文件`)
  } catch (error) {
    console.error('文件上传失败:', error)
    uploadProgress.value.status = 'exception'
    ElMessage.error('部分文件上传失败')
  } finally {
    isUploading.value = false
    
    // 延迟清除进度
    setTimeout(() => {
      uploadProgress.value = { total: 0, completed: 0, status: 'success' }
    }, 2000)
  }
}

const validateFile = (file: File): boolean => {
  // 文件类型检查
  if (!props.accept.includes(file.type)) {
    ElMessage.error(`不支持的文件类型: ${file.name}`)
    return false
  }

  // 文件大小检查
  if (file.size > props.maxSize) {
    ElMessage.error(`文件过大: ${file.name} (${formatFileSize(file.size)})`)
    return false
  }

  // 重复文件检查
  const isDuplicate = uploadedFiles.value.some(existingFile => 
    existingFile.name === file.name && existingFile.size === file.size
  )
  
  if (isDuplicate) {
    ElMessage.warning(`文件已存在: ${file.name}`)
    return false
  }

  return true
}

const uploadFile = async (file: File): Promise<void> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    
    reader.onload = (e) => {
      const uploadedFile: UploadedFile = {
        uid: Date.now().toString() + Math.random().toString(36).substr(2),
        name: file.name,
        url: e.target?.result as string,
        raw: file,
        size: file.size,
        type: file.type,
        status: 'success',
        recognizedCharacters: extractCharactersFromFilename(file.name)
      }
      
      uploadedFiles.value.push(uploadedFile)
      emit('file-added', uploadedFile)
      emit('files-changed', uploadedFiles.value)
      resolve()
    }
    
    reader.onerror = () => {
      reject(new Error(`读取文件失败: ${file.name}`))
    }
    
    reader.readAsDataURL(file)
  })
}

const extractCharactersFromFilename = (filename: string): string[] => {
  const chineseRegex = /[\u4e00-\u9fa5]/g
  const matches = filename.match(chineseRegex)
  return matches ? [...new Set(matches)] : []
}

const removeFile = async (file: UploadedFile) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除文件 "${file.name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const index = uploadedFiles.value.findIndex(f => f.uid === file.uid)
    if (index > -1) {
      uploadedFiles.value.splice(index, 1)
      emit('file-removed', file)
      emit('files-changed', uploadedFiles.value)
      
      // 从选中列表中移除
      const selectedIndex = selectedFiles.value.indexOf(file.uid)
      if (selectedIndex > -1) {
        selectedFiles.value.splice(selectedIndex, 1)
      }
      
      generateSmartTips()
      ElMessage.success('文件已删除')
    }
  } catch {
    // 用户取消删除
  }
}

const clearAllFiles = async () => {
  if (uploadedFiles.value.length === 0) return

  try {
    await ElMessageBox.confirm(
      `确定要删除所有 ${uploadedFiles.value.length} 个文件吗？`,
      '确认清空',
      {
        confirmButtonText: '清空',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    uploadedFiles.value.splice(0)
    selectedFiles.value.splice(0)
    smartTips.value.splice(0)
    emit('files-changed', uploadedFiles.value)
    ElMessage.success('所有文件已清空')
  } catch {
    // 用户取消
  }
}

const toggleViewMode = () => {
  viewMode.value = viewMode.value === 'list' ? 'grid' : 'list'
}

const toggleFileSelection = (uid: string) => {
  const index = selectedFiles.value.indexOf(uid)
  if (index > -1) {
    selectedFiles.value.splice(index, 1)
  } else {
    selectedFiles.value.push(uid)
  }
}

const selectAllFiles = () => {
  selectedFiles.value = uploadedFiles.value.map(file => file.uid)
}

const clearSelection = () => {
  selectedFiles.value.splice(0)
}

const removeSelectedFiles = async () => {
  if (selectedFiles.value.length === 0) return

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedFiles.value.length} 个文件吗？`,
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const filesToRemove = uploadedFiles.value.filter(file => 
      selectedFiles.value.includes(file.uid)
    )
    
    filesToRemove.forEach(file => {
      const index = uploadedFiles.value.findIndex(f => f.uid === file.uid)
      if (index > -1) {
        uploadedFiles.value.splice(index, 1)
        emit('file-removed', file)
      }
    })
    
    selectedFiles.value.splice(0)
    emit('files-changed', uploadedFiles.value)
    generateSmartTips()
    ElMessage.success(`已删除 ${filesToRemove.length} 个文件`)
  } catch {
    // 用户取消
  }
}

const previewFile = (file: UploadedFile) => {
  previewDialog.value = {
    visible: true,
    file
  }
}

const editFile = (file: UploadedFile) => {
  editDialog.value = {
    visible: true,
    file,
    form: {
      name: file.name,
      charactersInput: file.recognizedCharacters?.join(' ') || '',
      characters: [...(file.recognizedCharacters || [])]
    }
  }
}

const removeCharacterFromEdit = (char: string) => {
  const index = editDialog.value.form.characters.indexOf(char)
  if (index > -1) {
    editDialog.value.form.characters.splice(index, 1)
  }
}

const saveFileEdit = () => {
  if (!editDialog.value.file) return

  const file = editDialog.value.file
  const form = editDialog.value.form
  
  // 更新文件信息
  file.name = form.name
  
  // 解析字符输入
  const inputChars = form.charactersInput
    .split(/[\s,，]+/)
    .filter(char => char.trim() && /[\u4e00-\u9fa5]/.test(char))
    .filter((char, index, arr) => arr.indexOf(char) === index)
  
  file.recognizedCharacters = [...new Set([...form.characters, ...inputChars])]
  
  editDialog.value.visible = false
  emit('files-changed', uploadedFiles.value)
  ElMessage.success('文件信息已更新')
}

const handleFileAction = ({ action, file }: { action: string, file: UploadedFile }) => {
  switch (action) {
    case 'preview':
      previewFile(file)
      break
    case 'edit':
      editFile(file)
      break
    case 'download':
      downloadFile(file)
      break
    case 'remove':
      removeFile(file)
      break
  }
}

const downloadFile = (file: UploadedFile) => {
  const link = document.createElement('a')
  link.href = file.url
  link.download = file.name
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const getStatusText = (status: string): string => {
  switch (status) {
    case 'ready': return '准备中'
    case 'uploading': return '上传中'
    case 'success': return '成功'
    case 'error': return '失败'
    default: return '未知'
  }
}

const generateSmartTips = () => {
  const tips: SmartTip[] = []
  
  if (uploadedFiles.value.length === 0) {
    tips.push({
      id: 'no-files',
      type: 'info',
      message: '还没有上传任何文件，请拖拽或点击上传'
    })
  } else {
    // 检查文件数量
    if (uploadedFiles.value.length < 10) {
      tips.push({
        id: 'few-files',
        type: 'warning',
        message: `当前只有 ${uploadedFiles.value.length} 个文件，建议上传更多样本以提高训练效果`
      })
    } else if (uploadedFiles.value.length >= 20) {
      tips.push({
        id: 'many-files',
        type: 'success',
        message: `已上传 ${uploadedFiles.value.length} 个文件，样本数量充足`
      })
    }
    
    // 检查字符识别
    const recognizedChars = new Set<string>()
    uploadedFiles.value.forEach(file => {
      file.recognizedCharacters?.forEach(char => recognizedChars.add(char))
    })
    
    if (recognizedChars.size > 0) {
      tips.push({
        id: 'recognized-chars',
        type: 'success',
        message: `已识别 ${recognizedChars.size} 个不同的字符`
      })
    }
    
    // 检查文件命名
    const unnamedFiles = uploadedFiles.value.filter(file => 
      !file.recognizedCharacters || file.recognizedCharacters.length === 0
    )
    
    if (unnamedFiles.length > 0) {
      tips.push({
        id: 'unnamed-files',
        type: 'warning',
        message: `有 ${unnamedFiles.length} 个文件无法从文件名识别字符，建议重命名或手动标注`
      })
    }
  }
  
  smartTips.value = tips
}

// 监听文件变化
watch(() => uploadedFiles.value.length, () => {
  generateSmartTips()
}, { immediate: true })

// 监听字符输入变化
watch(() => editDialog.value.form.charactersInput, (newValue) => {
  if (newValue) {
    const chars = newValue
      .split(/[\s,，]+/)
      .filter(char => char.trim() && /[\u4e00-\u9fa5]/.test(char))
      .filter((char, index, arr) => arr.indexOf(char) === index)
    
    editDialog.value.form.characters = chars
  }
})
</script>

<style lang="scss" scoped>
.smart-sample-uploader {
  .upload-zone {
    border: 2px dashed var(--border-color, #dcdfe6);
    border-radius: 12px;
    padding: 40px 20px;
    text-align: center;
    background: var(--bg-light, #fafbfc);
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;

    &:hover {
      border-color: var(--primary-color, #409eff);
      background: var(--primary-light-9, #ecf5ff);
    }

    &.drag-over {
      border-color: var(--primary-color, #409eff);
      background: var(--primary-light-9, #ecf5ff);
      transform: scale(1.02);
      box-shadow: 0 8px 25px rgba(64, 158, 255, 0.2);
    }

    &.has-files {
      border-style: solid;
      border-color: var(--success-color, #67c23a);
      background: var(--success-light-9, #f0f9ff);
    }

    &.uploading {
      border-color: var(--warning-color, #e6a23c);
      background: var(--warning-light-9, #fdf6ec);
    }

    .upload-content {
      .upload-icon {
        margin-bottom: 16px;
        color: var(--text-secondary, #606266);

        .bounce {
          animation: bounce 0.6s ease-in-out infinite alternate;
        }
      }

      .upload-loading {
        margin-bottom: 16px;
        color: var(--warning-color, #e6a23c);
      }

      .upload-text {
        h3 {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary, #303133);
          margin-bottom: 8px;
        }

        .upload-hint {
          font-size: 14px;
          color: var(--text-secondary, #606266);
          margin-bottom: 12px;
        }

        .file-count {
          font-size: 14px;
          color: var(--success-color, #67c23a);
          font-weight: 500;
        }
      }

      .smart-tips {
        margin-top: 20px;
        padding: 16px;
        background: white;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        text-align: left;

        .tips-header {
          display: flex;
          align-items: center;
          gap: 8px;
          font-weight: 600;
          color: var(--primary-color, #409eff);
          margin-bottom: 12px;
          font-size: 14px;
        }

        .tips-content {
          .tip-item {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 8px;
            font-size: 13px;
            padding: 6px 8px;
            border-radius: 4px;

            &.success {
              background: var(--success-light-9, #f0f9ff);
              color: var(--success-color, #67c23a);
            }

            &.warning {
              background: var(--warning-light-9, #fdf6ec);
              color: var(--warning-color, #e6a23c);
            }

            &.info {
              background: var(--info-light-9, #f4f4f5);
              color: var(--info-color, #909399);
            }
          }
        }
      }
    }
  }

  .upload-progress {
    margin-top: 16px;
    padding: 16px;
    background: white;
    border-radius: 8px;
    border: 1px solid var(--border-color, #e4e7ed);

    .progress-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      font-size: 14px;
      color: var(--text-secondary, #606266);
    }
  }

  .file-list {
    margin-top: 24px;

    .list-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid var(--border-color, #e4e7ed);

      h4 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary, #303133);
        margin: 0;
      }

      .list-actions {
        display: flex;
        gap: 8px;
      }
    }

    .file-list-view {
      .file-item {
        display: flex;
        align-items: center;
        padding: 12px;
        border: 1px solid var(--border-color, #e4e7ed);
        border-radius: 8px;
        margin-bottom: 8px;
        background: white;
        transition: all 0.2s ease;

        &:hover {
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        &.selected {
          border-color: var(--primary-color, #409eff);
          background: var(--primary-light-9, #ecf5ff);
        }

        .file-checkbox {
          margin-right: 12px;
        }

        .file-preview {
          width: 60px;
          height: 60px;
          border-radius: 6px;
          overflow: hidden;
          margin-right: 12px;
          cursor: pointer;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }

        .file-info {
          flex: 1;
          min-width: 0;

          .file-name {
            font-weight: 500;
            color: var(--text-primary, #303133);
            margin-bottom: 4px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .file-meta {
            display: flex;
            gap: 12px;
            font-size: 12px;
            color: var(--text-secondary, #606266);
            margin-bottom: 4px;

            .file-status {
              &.success { color: var(--success-color, #67c23a); }
              &.error { color: var(--danger-color, #f56c6c); }
              &.uploading { color: var(--warning-color, #e6a23c); }
            }
          }

          .recognized-chars {
            font-size: 12px;
            color: var(--text-secondary, #606266);

            .char-tag {
              margin: 0 2px;
              font-family: 'SimSun', serif;
            }
          }
        }

        .file-actions {
          display: flex;
          gap: 4px;
        }
      }
    }

    .file-grid-view {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 16px;

      .file-card {
        border: 1px solid var(--border-color, #e4e7ed);
        border-radius: 8px;
        overflow: hidden;
        background: white;
        transition: all 0.2s ease;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          transform: translateY(-2px);
        }

        &.selected {
          border-color: var(--primary-color, #409eff);
          box-shadow: 0 0 0 2px var(--primary-light-8, #b3d8ff);
        }

        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 12px;
          background: var(--bg-light, #fafbfc);
          border-bottom: 1px solid var(--border-color, #e4e7ed);
        }

        .card-preview {
          position: relative;
          height: 150px;
          cursor: pointer;
          overflow: hidden;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.2s ease;
          }

          .preview-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            opacity: 0;
            transition: opacity 0.2s ease;
          }

          &:hover {
            img {
              transform: scale(1.05);
            }

            .preview-overlay {
              opacity: 1;
            }
          }
        }

        .card-info {
          padding: 12px;

          .file-name {
            font-weight: 500;
            color: var(--text-primary, #303133);
            margin-bottom: 4px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            font-size: 14px;
          }

          .file-size {
            font-size: 12px;
            color: var(--text-secondary, #606266);
            margin-bottom: 8px;
          }

          .recognized-chars {
            .char-tag {
              margin: 0 2px 2px 0;
              font-family: 'SimSun', serif;
            }
          }
        }
      }
    }

    .batch-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 16px;
      padding: 12px 16px;
      background: var(--primary-light-9, #ecf5ff);
      border-radius: 8px;
      border: 1px solid var(--primary-light-7, #c6e2ff);

      .batch-info {
        font-size: 14px;
        color: var(--primary-color, #409eff);
        font-weight: 500;
      }

      .batch-buttons {
        display: flex;
        gap: 8px;
      }
    }
  }

  .file-preview-dialog {
    display: flex;
    gap: 24px;

    .preview-image {
      flex: 1;
      text-align: center;

      img {
        max-width: 100%;
        max-height: 400px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
    }

    .preview-info {
      width: 300px;

      .info-section {
        margin-bottom: 24px;

        h4 {
          font-size: 16px;
          font-weight: 600;
          color: var(--text-primary, #303133);
          margin-bottom: 12px;
          padding-bottom: 8px;
          border-bottom: 1px solid var(--border-color, #e4e7ed);
        }

        .info-item {
          display: flex;
          margin-bottom: 8px;

          .label {
            width: 80px;
            font-weight: 500;
            color: var(--text-secondary, #606266);
          }

          .value {
            flex: 1;
            color: var(--text-primary, #303133);
          }
        }

        .recognized-chars-list {
          .char-tag {
            margin: 0 4px 4px 0;
            font-family: 'SimSun', serif;
          }
        }
      }
    }
  }

  .file-edit-dialog {
    .character-tags {
      min-height: 32px;
      padding: 8px;
      border: 1px solid var(--border-color, #e4e7ed);
      border-radius: 4px;
      background: var(--bg-light, #fafbfc);

      .char-tag {
        margin: 0 4px 4px 0;
        font-family: 'SimSun', serif;
      }
    }
  }

  .rotating {
    animation: rotate 2s linear infinite;
  }

  @keyframes rotate {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }

  @keyframes bounce {
    from { transform: translateY(0px); }
    to { transform: translateY(-10px); }
  }
}
</style>