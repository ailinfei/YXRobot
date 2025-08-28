<template>
  <div class="upload-example">
    <el-card class="example-card">
      <template #header>
        <div class="card-header">
          <span>文件上传管理组件示例</span>
          <div class="header-actions">
            <el-button @click="uploadAll">全部上传</el-button>
            <el-button @click="retryFailed">重试失败</el-button>
            <el-button @click="clearAll">清空全部</el-button>
          </div>
        </div>
      </template>

      <!-- 配置面板 -->
      <div class="config-panel">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-select v-model="uploadConfig.accept" placeholder="文件类型">
              <el-option label="所有文件" value="*/*" />
              <el-option label="图片文件" value="image/*" />
              <el-option label="文档文件" value=".pdf,.doc,.docx,.txt" />
              <el-option label="视频文件" value="video/*" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-input-number
              v-model="uploadConfig.maxSize"
              :min="1"
              :max="100"
              :step="1"
            />
            <span style="margin-left: 8px;">最大大小(MB)</span>
          </el-col>
          <el-col :span="6">
            <el-input-number
              v-model="uploadConfig.maxCount"
              :min="1"
              :max="20"
              :step="1"
            />
            <span style="margin-left: 8px;">最大数量</span>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="uploadConfig.multiple">允许多选</el-checkbox>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top: 10px;">
          <el-col :span="6">
            <el-checkbox v-model="uploadConfig.showPreview">显示预览</el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="uploadConfig.showCrop">显示裁剪</el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="uploadConfig.autoUpload">自动上传</el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="uploadConfig.disabled">禁用上传</el-checkbox>
          </el-col>
        </el-row>
      </div>

      <!-- 文件上传组件 -->
      <FileUploadManager
        ref="uploadManagerRef"
        :accept="uploadConfig.accept"
        :multiple="uploadConfig.multiple"
        :max-size="uploadConfig.maxSize * 1024 * 1024"
        :max-count="uploadConfig.maxCount"
        :disabled="uploadConfig.disabled"
        :show-preview="uploadConfig.showPreview"
        :show-crop="uploadConfig.showCrop"
        :auto-upload="uploadConfig.autoUpload"
        :upload-url="mockUploadUrl"
        @file-added="handleFileAdded"
        @file-removed="handleFileRemoved"
        @upload-progress="handleUploadProgress"
        @upload-success="handleUploadSuccess"
        @upload-error="handleUploadError"
        @preview="handlePreview"
        @crop="handleCrop"
      />

      <!-- 统计信息 -->
      <div class="upload-stats" v-if="stats.total > 0">
        <el-divider>上传统计</el-divider>
        <el-row :gutter="16">
          <el-col :span="6">
            <el-statistic title="总文件数" :value="stats.total" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="上传成功" :value="stats.success" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="上传失败" :value="stats.error" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="总大小" :value="formatFileSize(stats.totalSize)" />
          </el-col>
        </el-row>
      </div>

      <!-- 操作日志 -->
      <div class="action-log" v-if="actionLogs.length > 0">
        <el-divider>操作日志</el-divider>
        <div class="log-container">
          <div
            v-for="(log, index) in actionLogs.slice(-10)"
            :key="index"
            class="log-item"
          >
            <span class="log-time">{{ log.time }}</span>
            <span class="log-action" :class="log.type">{{ log.action }}</span>
            <span class="log-detail">{{ log.detail }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewDialog.visible"
      title="文件预览"
      width="60%"
      center
    >
      <div class="preview-content" v-if="previewDialog.file">
        <img
          v-if="isImageFile(previewDialog.file.type)"
          :src="previewDialog.file.preview"
          :alt="previewDialog.file.name"
          style="max-width: 100%; max-height: 500px;"
        />
        <div v-else class="file-info">
          <el-icon size="48"><Document /></el-icon>
          <h3>{{ previewDialog.file.name }}</h3>
          <p>文件大小: {{ formatFileSize(previewDialog.file.size) }}</p>
          <p>文件类型: {{ previewDialog.file.type }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import FileUploadManager from './FileUploadManager.vue'

// 响应式数据
const uploadManagerRef = ref()
const actionLogs = ref<any[]>([])
const previewDialog = reactive({
  visible: false,
  file: null as any
})

// 上传配置
const uploadConfig = reactive({
  accept: 'image/*',
  multiple: true,
  maxSize: 5, // MB
  maxCount: 10,
  disabled: false,
  showPreview: true,
  showCrop: false,
  autoUpload: false
})

// 模拟上传URL
const mockUploadUrl = '/api/mock/upload'

// 统计数据
const stats = reactive({
  total: 0,
  success: 0,
  error: 0,
  totalSize: 0
})

// 计算属性
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const isImageFile = (type: string): boolean => {
  return type.startsWith('image/')
}

// 事件处理
const handleFileAdded = (file: any) => {
  stats.total++
  stats.totalSize += file.size
  addActionLog('文件添加', `${file.name} (${formatFileSize(file.size)})`, 'info')
}

const handleFileRemoved = (file: any) => {
  stats.total--
  stats.totalSize -= file.size
  if (file.status === 'success') stats.success--
  if (file.status === 'error') stats.error--
  addActionLog('文件移除', `${file.name}`, 'warning')
}

const handleUploadProgress = (file: any, progress: number) => {
  if (progress === 100) {
    addActionLog('上传完成', `${file.name} - ${progress}%`, 'success')
  }
}

const handleUploadSuccess = (file: any, response: any) => {
  stats.success++
  addActionLog('上传成功', `${file.name}`, 'success')
  ElMessage.success(`${file.name} 上传成功`)
}

const handleUploadError = (file: any, error: any) => {
  stats.error++
  addActionLog('上传失败', `${file.name}: ${error}`, 'error')
  ElMessage.error(`${file.name} 上传失败`)
}

const handlePreview = (file: any) => {
  previewDialog.file = file
  previewDialog.visible = true
  addActionLog('预览文件', file.name, 'info')
}

const handleCrop = (file: any) => {
  addActionLog('裁剪文件', file.name, 'info')
  ElMessage.info(`裁剪功能待实现: ${file.name}`)
}

// 操作方法
const uploadAll = () => {
  uploadManagerRef.value?.uploadAll()
  addActionLog('批量上传', '开始上传所有文件', 'info')
}

const retryFailed = () => {
  uploadManagerRef.value?.retryFailed()
  addActionLog('重试上传', '重试失败的文件', 'warning')
}

const clearAll = () => {
  uploadManagerRef.value?.clearAllFiles()
  // 重置统计
  stats.total = 0
  stats.success = 0
  stats.error = 0
  stats.totalSize = 0
  addActionLog('清空文件', '清空所有文件', 'warning')
}

const addActionLog = (action: string, detail: string, type: string = 'info') => {
  actionLogs.value.push({
    time: new Date().toLocaleTimeString(),
    action,
    detail,
    type
  })
}

// 模拟上传服务器响应
if (typeof window !== 'undefined') {
  // 拦截上传请求，模拟服务器响应
  const originalXHR = window.XMLHttpRequest
  window.XMLHttpRequest = function() {
    const xhr = new originalXHR()
    const originalOpen = xhr.open
    
    xhr.open = function(method: string, url: string, ...args: any[]) {
      if (url === mockUploadUrl) {
        // 模拟上传延迟和响应
        setTimeout(() => {
          if (Math.random() > 0.1) { // 90% 成功率
            Object.defineProperty(xhr, 'status', { value: 200 })
            Object.defineProperty(xhr, 'responseText', { 
              value: JSON.stringify({ 
                url: `https://example.com/uploads/${Date.now()}.jpg`,
                message: '上传成功'
              })
            })
            xhr.dispatchEvent(new Event('load'))
          } else {
            Object.defineProperty(xhr, 'status', { value: 500 })
            xhr.dispatchEvent(new Event('error'))
          }
        }, 1000 + Math.random() * 2000) // 1-3秒随机延迟
      } else {
        originalOpen.apply(xhr, [method, url, ...args])
      }
    }
    
    return xhr
  }
}
</script>

<style lang="scss" scoped>
.upload-example {
  padding: 20px;

  .example-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        gap: 8px;
      }
    }
  }

  .config-panel {
    margin-bottom: 20px;
    padding: 16px;
    background: var(--bg-secondary);
    border-radius: var(--radius-md);
  }

  .upload-stats {
    margin-top: 20px;
  }

  .action-log {
    margin-top: 20px;

    .log-container {
      max-height: 300px;
      overflow-y: auto;
      background: var(--bg-tertiary);
      border-radius: var(--radius-md);
      padding: 12px;

      .log-item {
        display: flex;
        gap: 12px;
        padding: 4px 0;
        font-size: 12px;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        .log-time {
          color: var(--text-light);
          min-width: 80px;
        }

        .log-action {
          font-weight: 500;
          min-width: 80px;

          &.info { color: var(--primary-color); }
          &.success { color: #67c23a; }
          &.warning { color: #e6a23c; }
          &.error { color: #f56c6c; }
        }

        .log-detail {
          color: var(--text-secondary);
          flex: 1;
        }
      }
    }
  }

  .preview-content {
    text-align: center;

    .file-info {
      padding: 20px;

      h3 {
        margin: 16px 0 8px 0;
        color: var(--text-primary);
      }

      p {
        margin: 4px 0;
        color: var(--text-secondary);
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .upload-example {
    padding: 10px;
    
    .example-card {
      .card-header {
        flex-direction: column;
        gap: 12px;
        align-items: stretch;

        .header-actions {
          justify-content: center;
        }
      }
    }

    .config-panel {
      .el-row {
        .el-col {
          margin-bottom: 10px;
        }
      }
    }
  }
}
</style>