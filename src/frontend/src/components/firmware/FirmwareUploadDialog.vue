<!--
  固件上传对话框组件
  功能：拖拽上传、点击上传、进度显示、文件验证、元数据编辑
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    title="上传固件"
    width="800px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <div class="firmware-upload-dialog">
      <!-- 步骤指示器 -->
      <el-steps :active="currentStep" align-center class="upload-steps">
        <el-step title="选择文件" icon="Upload" />
        <el-step title="文件验证" icon="CircleCheck" />
        <el-step title="编辑信息" icon="Edit" />
        <el-step title="上传完成" icon="SuccessFilled" />
      </el-steps>

      <!-- 步骤1: 文件选择 -->
      <div v-if="currentStep === 0" class="step-content">
        <div class="upload-area">
          <el-upload
            ref="uploadRef"
            class="firmware-uploader"
            drag
            :auto-upload="false"
            :show-file-list="false"
            :accept="acceptedFileTypes"
            :before-upload="handleBeforeUpload"
            :on-change="handleFileChange"
            :disabled="uploading"
          >
            <div class="upload-content">
              <el-icon class="upload-icon"><UploadFilled /></el-icon>
              <div class="upload-text">
                <p class="upload-title">拖拽固件文件到此处，或点击选择文件</p>
                <p class="upload-hint">支持 .bin, .hex, .fw 格式，文件大小不超过 {{ maxFileSizeMB }}MB</p>
              </div>
            </div>
          </el-upload>
          
          <!-- 已选择的文件信息 -->
          <div v-if="selectedFile" class="selected-file">
            <div class="file-info">
              <el-icon class="file-icon"><Document /></el-icon>
              <div class="file-details">
                <div class="file-name">{{ selectedFile.name }}</div>
                <div class="file-meta">
                  {{ formatFileSize(selectedFile.size) }} · {{ selectedFile.type || '未知类型' }}
                </div>
              </div>
              <el-button
                type="danger"
                size="small"
                text
                @click="removeFile"
                :disabled="uploading"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="step-actions">
          <el-button @click="handleClose" :disabled="uploading">取消</el-button>
          <el-button
            type="primary"
            @click="nextStep"
            :disabled="!selectedFile || uploading"
          >
            下一步
          </el-button>
        </div>
      </div>

      <!-- 步骤2: 文件验证 -->
      <div v-if="currentStep === 1" class="step-content">
        <div class="validation-content">
          <div class="validation-header">
            <h3>文件验证中...</h3>
            <p>正在检查文件完整性和格式</p>
          </div>

          <div class="validation-progress">
            <el-progress
              :percentage="validationProgress"
              :status="validationStatus"
              stroke-width="8"
            />
          </div>

          <div class="validation-results">
            <div
              v-for="check in validationChecks"
              :key="check.name"
              class="validation-item"
              :class="check.status"
            >
              <el-icon class="check-icon">
                <Loading v-if="check.status === 'checking'" />
                <CircleCheckFilled v-else-if="check.status === 'success'" />
                <CircleCloseFilled v-else-if="check.status === 'error'" />
                <Clock v-else />
              </el-icon>
              <span class="check-name">{{ check.name }}</span>
              <span class="check-message">{{ check.message }}</span>
            </div>
          </div>

          <!-- 验证错误信息 -->
          <el-alert
            v-if="validationError"
            :title="validationError"
            type="error"
            show-icon
            :closable="false"
            class="validation-error"
          />
        </div>

        <!-- 操作按钮 -->
        <div class="step-actions">
          <el-button @click="prevStep" :disabled="validating">上一步</el-button>
          <el-button
            type="primary"
            @click="nextStep"
            :disabled="validating || validationError"
            :loading="validating"
          >
            {{ validating ? '验证中...' : '下一步' }}
          </el-button>
        </div>
      </div>

      <!-- 步骤3: 编辑固件信息 -->
      <div v-if="currentStep === 2" class="step-content">
        <el-form
          ref="formRef"
          :model="firmwareInfo"
          :rules="formRules"
          label-width="120px"
          class="firmware-form"
        >
          <div class="form-section">
            <h4 class="section-title">基本信息</h4>
            
            <el-form-item label="版本号" prop="version" required>
              <el-input
                v-model="firmwareInfo.version"
                placeholder="例如: 2.1.0"
                :disabled="uploading"
              />
            </el-form-item>

            <el-form-item label="适用设备" prop="deviceModel" required>
              <el-select
                v-model="firmwareInfo.deviceModel"
                multiple
                placeholder="选择适用的设备型号"
                style="width: 100%"
                :disabled="uploading"
              >
                <el-option
                  v-for="model in deviceModels"
                  :key="model.value"
                  :label="model.label"
                  :value="model.value"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="版本描述" prop="description" required>
              <el-input
                v-model="firmwareInfo.description"
                type="textarea"
                :rows="3"
                placeholder="简要描述此版本的主要功能和改进"
                :disabled="uploading"
              />
            </el-form-item>
          </div>

          <div class="form-section">
            <h4 class="section-title">更新日志</h4>
            
            <el-form-item label="更新内容" prop="changelog">
              <div class="changelog-editor">
                <div
                  v-for="(item, index) in firmwareInfo.changelog"
                  :key="index"
                  class="changelog-item"
                >
                  <el-input
                    v-model="firmwareInfo.changelog[index]"
                    placeholder="输入更新内容"
                    :disabled="uploading"
                  />
                  <el-button
                    type="danger"
                    size="small"
                    text
                    @click="removeChangelogItem(index)"
                    :disabled="uploading"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
                <el-button
                  type="primary"
                  size="small"
                  text
                  @click="addChangelogItem"
                  :disabled="uploading"
                >
                  <el-icon><Plus /></el-icon>
                  添加更新内容
                </el-button>
              </div>
            </el-form-item>
          </div>

          <div class="form-section">
            <h4 class="section-title">兼容性信息</h4>
            
            <el-form-item label="兼容性说明" prop="compatibility">
              <div class="compatibility-editor">
                <div
                  v-for="(item, index) in firmwareInfo.compatibility"
                  :key="index"
                  class="compatibility-item"
                >
                  <el-input
                    v-model="firmwareInfo.compatibility[index]"
                    placeholder="例如: YX-EDU-2024 v1.0+"
                    :disabled="uploading"
                  />
                  <el-button
                    type="danger"
                    size="small"
                    text
                    @click="removeCompatibilityItem(index)"
                    :disabled="uploading"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
                <el-button
                  type="primary"
                  size="small"
                  text
                  @click="addCompatibilityItem"
                  :disabled="uploading"
                >
                  <el-icon><Plus /></el-icon>
                  添加兼容性说明
                </el-button>
              </div>
            </el-form-item>

            <el-form-item label="已知问题" prop="knownIssues">
              <div class="issues-editor">
                <div
                  v-for="(item, index) in firmwareInfo.knownIssues"
                  :key="index"
                  class="issue-item"
                >
                  <el-input
                    v-model="firmwareInfo.knownIssues[index]"
                    placeholder="描述已知问题（可选）"
                    :disabled="uploading"
                  />
                  <el-button
                    type="danger"
                    size="small"
                    text
                    @click="removeIssueItem(index)"
                    :disabled="uploading"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
                <el-button
                  type="primary"
                  size="small"
                  text
                  @click="addIssueItem"
                  :disabled="uploading"
                >
                  <el-icon><Plus /></el-icon>
                  添加已知问题
                </el-button>
              </div>
            </el-form-item>
          </div>
        </el-form>

        <!-- 操作按钮 -->
        <div class="step-actions">
          <el-button @click="prevStep" :disabled="uploading">上一步</el-button>
          <el-button
            type="primary"
            @click="handleUpload"
            :loading="uploading"
            :disabled="uploading"
          >
            {{ uploading ? '上传中...' : '开始上传' }}
          </el-button>
        </div>
      </div>

      <!-- 步骤4: 上传进度和完成 -->
      <div v-if="currentStep === 3" class="step-content">
        <div class="upload-progress-content">
          <div v-if="!uploadCompleted" class="uploading">
            <div class="upload-header">
              <h3>正在上传固件...</h3>
              <p>请勿关闭页面，上传过程可能需要几分钟</p>
            </div>

            <div class="progress-section">
              <el-progress
                :percentage="uploadProgress"
                :status="uploadStatus"
                stroke-width="12"
                class="main-progress"
              />
              <div class="progress-info">
                <span>{{ uploadProgress }}%</span>
                <span>{{ formatFileSize(uploadedBytes) }} / {{ formatFileSize(totalBytes) }}</span>
              </div>
            </div>

            <div class="upload-steps-detail">
              <div
                v-for="step in uploadSteps"
                :key="step.name"
                class="upload-step-item"
                :class="step.status"
              >
                <el-icon class="step-icon">
                  <Loading v-if="step.status === 'processing'" />
                  <CircleCheckFilled v-else-if="step.status === 'completed'" />
                  <CircleCloseFilled v-else-if="step.status === 'error'" />
                  <Clock v-else />
                </el-icon>
                <span class="step-name">{{ step.name }}</span>
                <span class="step-message">{{ step.message }}</span>
              </div>
            </div>
          </div>

          <div v-else class="upload-completed">
            <div class="success-icon">
              <el-icon><SuccessFilled /></el-icon>
            </div>
            <h3>固件上传成功！</h3>
            <p>固件版本 {{ firmwareInfo.version }} 已成功上传到系统</p>
            
            <div class="upload-result">
              <div class="result-item">
                <span class="label">文件名:</span>
                <span class="value">{{ selectedFile?.name }}</span>
              </div>
              <div class="result-item">
                <span class="label">文件大小:</span>
                <span class="value">{{ formatFileSize(selectedFile?.size || 0) }}</span>
              </div>
              <div class="result-item">
                <span class="label">版本号:</span>
                <span class="value">{{ firmwareInfo.version }}</span>
              </div>
              <div class="result-item">
                <span class="label">适用设备:</span>
                <span class="value">{{ firmwareInfo.deviceModel.join(', ') }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="step-actions">
          <el-button
            v-if="uploadCompleted"
            @click="handleClose"
          >
            关闭
          </el-button>
          <el-button
            v-if="uploadCompleted"
            type="primary"
            @click="uploadAnother"
          >
            继续上传
          </el-button>
          <el-button
            v-if="!uploadCompleted && !uploading"
            @click="prevStep"
          >
            返回编辑
          </el-button>
        </div>
      </div>
    </div>
  </el-dialog>
</template>
<script 
setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  UploadFilled,
  Document,
  Delete,
  Plus,
  Loading,
  CircleCheckFilled,
  CircleCloseFilled,
  Clock,
  SuccessFilled
} from '@element-plus/icons-vue'
import { formatFileSize } from '@/types/firmware'
import { mockFirmwareAPI } from '@/api/mock/firmware'
import type { FirmwareUploadData } from '@/types/firmware'

// Props
interface Props {
  modelValue: boolean
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'upload-success': [firmware: any]
}>()

// 响应式数据
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const uploadRef = ref()
const formRef = ref()
const currentStep = ref(0)
const selectedFile = ref<File | null>(null)
const uploading = ref(false)
const validating = ref(false)
const uploadCompleted = ref(false)

// 文件上传配置
const maxFileSizeMB = 100
const maxFileSize = maxFileSizeMB * 1024 * 1024
const acceptedFileTypes = '.bin,.hex,.fw'
const acceptedExtensions = ['bin', 'hex', 'fw']

// 设备型号选项
const deviceModels = [
  { label: 'YX-EDU-2024 (教育版)', value: 'YX-EDU-2024' },
  { label: 'YX-HOME-2024 (家用版)', value: 'YX-HOME-2024' },
  { label: 'YX-PRO-2024 (专业版)', value: 'YX-PRO-2024' },
  { label: 'YX-MINI-2024 (迷你版)', value: 'YX-MINI-2024' }
]

// 固件信息表单
const firmwareInfo = reactive<{
  version: string
  deviceModel: string[]
  description: string
  changelog: string[]
  compatibility: string[]
  knownIssues: string[]
}>({
  version: '',
  deviceModel: [],
  description: '',
  changelog: [''],
  compatibility: [''],
  knownIssues: []
})

// 表单验证规则
const formRules = {
  version: [
    { required: true, message: '请输入版本号', trigger: 'blur' },
    { pattern: /^\d+\.\d+\.\d+(-\w+)?$/, message: '版本号格式不正确，例如: 2.1.0 或 2.1.0-beta', trigger: 'blur' }
  ],
  deviceModel: [
    { required: true, message: '请选择适用设备', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入版本描述', trigger: 'blur' },
    { min: 10, message: '描述至少需要10个字符', trigger: 'blur' }
  ]
}

// 文件验证状态
const validationProgress = ref(0)
const validationStatus = ref<'success' | 'exception' | undefined>(undefined)
const validationError = ref('')

const validationChecks = ref([
  { name: '文件格式检查', status: 'pending', message: '等待检查' },
  { name: '文件大小检查', status: 'pending', message: '等待检查' },
  { name: '文件完整性校验', status: 'pending', message: '等待检查' },
  { name: '固件信息提取', status: 'pending', message: '等待检查' }
])

// 上传进度状态
const uploadProgress = ref(0)
const uploadStatus = ref<'success' | 'exception' | undefined>(undefined)
const uploadedBytes = ref(0)
const totalBytes = ref(0)

const uploadSteps = ref([
  { name: '准备上传', status: 'pending', message: '等待开始' },
  { name: '文件传输', status: 'pending', message: '等待开始' },
  { name: '服务器验证', status: 'pending', message: '等待开始' },
  { name: '数据库保存', status: 'pending', message: '等待开始' }
])

// 方法
const handleBeforeUpload = (file: File): boolean => {
  return validateFile(file)
}

const handleFileChange = (file: any) => {
  if (file.raw && validateFile(file.raw)) {
    selectedFile.value = file.raw
  }
}

const validateFile = (file: File): boolean => {
  // 检查文件扩展名
  const fileName = file.name.toLowerCase()
  const extension = fileName.split('.').pop()
  
  if (!extension || !acceptedExtensions.includes(extension)) {
    ElMessage.error(`不支持的文件格式，请选择 ${acceptedExtensions.join(', ')} 格式的文件`)
    return false
  }

  // 检查文件大小
  if (file.size > maxFileSize) {
    ElMessage.error(`文件大小不能超过 ${maxFileSizeMB}MB`)
    return false
  }

  // 检查文件是否为空
  if (file.size === 0) {
    ElMessage.error('文件不能为空')
    return false
  }

  return true
}

const removeFile = () => {
  selectedFile.value = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

const nextStep = async () => {
  if (currentStep.value === 0) {
    // 进入验证步骤
    currentStep.value = 1
    await performFileValidation()
  } else if (currentStep.value === 1) {
    // 进入信息编辑步骤
    if (!validationError.value) {
      currentStep.value = 2
      extractFirmwareInfo()
    }
  } else if (currentStep.value === 2) {
    // 验证表单并进入上传步骤
    if (formRef.value) {
      try {
        await formRef.value.validate()
        currentStep.value = 3
      } catch (error) {
        ElMessage.error('请完善固件信息')
      }
    }
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const performFileValidation = async () => {
  if (!selectedFile.value) return

  validating.value = true
  validationError.value = ''
  validationProgress.value = 0
  validationStatus.value = undefined

  // 重置验证检查状态
  validationChecks.value.forEach(check => {
    check.status = 'pending'
    check.message = '等待检查'
  })

  try {
    // 1. 文件格式检查
    validationChecks.value[0].status = 'checking'
    validationChecks.value[0].message = '检查中...'
    await delay(500)
    
    const fileName = selectedFile.value.name.toLowerCase()
    const extension = fileName.split('.').pop()
    
    if (!extension || !acceptedExtensions.includes(extension)) {
      validationChecks.value[0].status = 'error'
      validationChecks.value[0].message = '不支持的文件格式'
      throw new Error('不支持的文件格式')
    }
    
    validationChecks.value[0].status = 'success'
    validationChecks.value[0].message = '格式正确'
    validationProgress.value = 25

    // 2. 文件大小检查
    validationChecks.value[1].status = 'checking'
    validationChecks.value[1].message = '检查中...'
    await delay(300)
    
    if (selectedFile.value.size > maxFileSize) {
      validationChecks.value[1].status = 'error'
      validationChecks.value[1].message = `文件过大 (${formatFileSize(selectedFile.value.size)})`
      throw new Error('文件大小超出限制')
    }
    
    validationChecks.value[1].status = 'success'
    validationChecks.value[1].message = `大小正常 (${formatFileSize(selectedFile.value.size)})`
    validationProgress.value = 50

    // 3. 文件完整性校验
    validationChecks.value[2].status = 'checking'
    validationChecks.value[2].message = '计算校验和...'
    await delay(800)
    
    // 模拟计算文件校验和
    const checksum = await calculateFileChecksum(selectedFile.value)
    
    validationChecks.value[2].status = 'success'
    validationChecks.value[2].message = `校验和: ${checksum.substring(0, 8)}...`
    validationProgress.value = 75

    // 4. 固件信息提取
    validationChecks.value[3].status = 'checking'
    validationChecks.value[3].message = '提取固件信息...'
    await delay(600)
    
    // 模拟提取固件信息
    const firmwareMetadata = await extractFirmwareMetadata(selectedFile.value)
    
    validationChecks.value[3].status = 'success'
    validationChecks.value[3].message = `检测到版本信息: ${firmwareMetadata.version || '未知'}`
    validationProgress.value = 100
    validationStatus.value = 'success'

  } catch (error: any) {
    validationError.value = error.message || '文件验证失败'
    validationStatus.value = 'exception'
  } finally {
    validating.value = false
  }
}

const extractFirmwareInfo = () => {
  if (!selectedFile.value) return

  // 尝试从文件名提取版本信息
  const fileName = selectedFile.value.name
  const versionMatch = fileName.match(/v?(\d+\.\d+\.\d+(-\w+)?)/i)
  
  if (versionMatch) {
    firmwareInfo.version = versionMatch[1]
  }

  // 根据文件名推测设备型号
  const fileNameLower = fileName.toLowerCase()
  if (fileNameLower.includes('edu')) {
    firmwareInfo.deviceModel = ['YX-EDU-2024']
  } else if (fileNameLower.includes('home')) {
    firmwareInfo.deviceModel = ['YX-HOME-2024']
  } else if (fileNameLower.includes('pro')) {
    firmwareInfo.deviceModel = ['YX-PRO-2024']
  } else if (fileNameLower.includes('mini')) {
    firmwareInfo.deviceModel = ['YX-MINI-2024']
  }
}

const handleUpload = async () => {
  if (!selectedFile.value) return

  uploading.value = true
  uploadCompleted.value = false
  uploadProgress.value = 0
  uploadStatus.value = undefined
  uploadedBytes.value = 0
  totalBytes.value = selectedFile.value.size

  // 重置上传步骤状态
  uploadSteps.value.forEach(step => {
    step.status = 'pending'
    step.message = '等待开始'
  })

  try {
    // 1. 准备上传
    uploadSteps.value[0].status = 'processing'
    uploadSteps.value[0].message = '准备上传数据...'
    await delay(500)

    const uploadData: FirmwareUploadData = {
      file: selectedFile.value,
      version: firmwareInfo.version,
      deviceModel: firmwareInfo.deviceModel,
      description: firmwareInfo.description,
      changelog: firmwareInfo.changelog.filter(item => item.trim()),
      compatibility: firmwareInfo.compatibility.filter(item => item.trim()),
      knownIssues: firmwareInfo.knownIssues.filter(item => item.trim())
    }

    uploadSteps.value[0].status = 'completed'
    uploadSteps.value[0].message = '准备完成'

    // 2. 文件传输
    uploadSteps.value[1].status = 'processing'
    uploadSteps.value[1].message = '正在传输文件...'

    // 模拟文件上传进度
    for (let i = 0; i <= 100; i += 5) {
      uploadProgress.value = Math.floor(i * 0.7) // 文件传输占70%
      uploadedBytes.value = Math.floor((totalBytes.value * i) / 100)
      await delay(100)
    }

    uploadSteps.value[1].status = 'completed'
    uploadSteps.value[1].message = '文件传输完成'

    // 3. 服务器验证
    uploadSteps.value[2].status = 'processing'
    uploadSteps.value[2].message = '服务器验证中...'
    uploadProgress.value = 80
    await delay(1000)

    uploadSteps.value[2].status = 'completed'
    uploadSteps.value[2].message = '验证通过'

    // 4. 数据库保存
    uploadSteps.value[3].status = 'processing'
    uploadSteps.value[3].message = '保存到数据库...'
    uploadProgress.value = 90
    await delay(800)

    // 调用API上传
    const response = await mockFirmwareAPI.uploadFirmware(uploadData)

    uploadSteps.value[3].status = 'completed'
    uploadSteps.value[3].message = '保存完成'
    uploadProgress.value = 100
    uploadStatus.value = 'success'
    uploadCompleted.value = true

    ElMessage.success('固件上传成功！')
    emit('upload-success', response.data)

  } catch (error: any) {
    console.error('上传失败:', error)
    uploadStatus.value = 'exception'
    
    // 标记当前步骤为失败
    const currentStepIndex = uploadSteps.value.findIndex(step => step.status === 'processing')
    if (currentStepIndex >= 0) {
      uploadSteps.value[currentStepIndex].status = 'error'
      uploadSteps.value[currentStepIndex].message = error.message || '上传失败'
    }
    
    ElMessage.error(error.message || '固件上传失败')
  } finally {
    uploading.value = false
  }
}

const handleClose = async () => {
  if (uploading.value) {
    try {
      await ElMessageBox.confirm(
        '正在上传中，确定要关闭吗？关闭后上传将被中断。',
        '确认关闭',
        {
          confirmButtonText: '确定关闭',
          cancelButtonText: '继续上传',
          type: 'warning'
        }
      )
    } catch {
      return // 用户取消关闭
    }
  }

  resetDialog()
  emit('update:modelValue', false)
}

const uploadAnother = () => {
  resetDialog()
  currentStep.value = 0
}

const resetDialog = () => {
  currentStep.value = 0
  selectedFile.value = null
  uploading.value = false
  validating.value = false
  uploadCompleted.value = false
  validationError.value = ''
  validationProgress.value = 0
  validationStatus.value = undefined
  uploadProgress.value = 0
  uploadStatus.value = undefined
  uploadedBytes.value = 0
  totalBytes.value = 0

  // 重置表单
  Object.assign(firmwareInfo, {
    version: '',
    deviceModel: [],
    description: '',
    changelog: [''],
    compatibility: [''],
    knownIssues: []
  })

  // 重置验证检查
  validationChecks.value.forEach(check => {
    check.status = 'pending'
    check.message = '等待检查'
  })

  // 重置上传步骤
  uploadSteps.value.forEach(step => {
    step.status = 'pending'
    step.message = '等待开始'
  })

  // 清空文件选择
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 动态列表操作方法
const addChangelogItem = () => {
  firmwareInfo.changelog.push('')
}

const removeChangelogItem = (index: number) => {
  if (firmwareInfo.changelog.length > 1) {
    firmwareInfo.changelog.splice(index, 1)
  }
}

const addCompatibilityItem = () => {
  firmwareInfo.compatibility.push('')
}

const removeCompatibilityItem = (index: number) => {
  if (firmwareInfo.compatibility.length > 1) {
    firmwareInfo.compatibility.splice(index, 1)
  }
}

const addIssueItem = () => {
  firmwareInfo.knownIssues.push('')
}

const removeIssueItem = (index: number) => {
  firmwareInfo.knownIssues.splice(index, 1)
}

// 工具函数
const delay = (ms: number): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, ms))
}

const calculateFileChecksum = async (file: File): Promise<string> => {
  // 模拟计算文件校验和
  await delay(500)
  return `sha256:${Math.random().toString(36).substring(2, 15)}${Math.random().toString(36).substring(2, 15)}`
}

const extractFirmwareMetadata = async (file: File): Promise<{ version?: string }> => {
  // 模拟提取固件元数据
  await delay(300)
  
  // 尝试从文件名提取版本信息
  const fileName = file.name
  const versionMatch = fileName.match(/v?(\d+\.\d+\.\d+(-\w+)?)/i)
  
  return {
    version: versionMatch ? versionMatch[1] : undefined
  }
}

// 监听对话框关闭
watch(dialogVisible, (newValue) => {
  if (!newValue) {
    resetDialog()
  }
})
</script>

<style lang="scss" scoped>
.firmware-upload-dialog {
  .upload-steps {
    margin-bottom: 32px;
  }

  .step-content {
    min-height: 400px;
    display: flex;
    flex-direction: column;
  }

  .step-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: auto;
    padding-top: 24px;
    border-top: 1px solid #ebeef5;
  }

  // 步骤1: 文件选择样式
  .upload-area {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 20px;

    .firmware-uploader {
      :deep(.el-upload) {
        width: 100%;
      }

      :deep(.el-upload-dragger) {
        width: 100%;
        height: 200px;
        border: 2px dashed #d9d9d9;
        border-radius: 8px;
        background: #fafafa;
        transition: all 0.3s ease;

        &:hover {
          border-color: #409eff;
          background: #f0f9ff;
        }
      }

      .upload-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100%;
        gap: 16px;

        .upload-icon {
          font-size: 48px;
          color: #c0c4cc;
        }

        .upload-text {
          text-align: center;

          .upload-title {
            margin: 0 0 8px 0;
            font-size: 16px;
            color: #606266;
            font-weight: 500;
          }

          .upload-hint {
            margin: 0;
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }

    .selected-file {
      .file-info {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px;
        background: #f8f9fa;
        border: 1px solid #e9ecef;
        border-radius: 8px;

        .file-icon {
          font-size: 24px;
          color: #409eff;
        }

        .file-details {
          flex: 1;

          .file-name {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
            margin-bottom: 4px;
          }

          .file-meta {
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }
  }

  // 步骤2: 文件验证样式
  .validation-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 24px;

    .validation-header {
      text-align: center;

      h3 {
        margin: 0 0 8px 0;
        font-size: 18px;
        color: #303133;
      }

      p {
        margin: 0;
        font-size: 14px;
        color: #909399;
      }
    }

    .validation-progress {
      margin: 20px 0;
    }

    .validation-results {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 16px;

      .validation-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px;
        background: #f8f9fa;
        border-radius: 8px;
        border-left: 4px solid #e9ecef;

        &.checking {
          border-left-color: #409eff;
          background: #f0f9ff;
        }

        &.success {
          border-left-color: #67c23a;
          background: #f0f9ff;
        }

        &.error {
          border-left-color: #f56c6c;
          background: #fef0f0;
        }

        .check-icon {
          font-size: 18px;

          &.checking {
            color: #409eff;
          }

          &.success {
            color: #67c23a;
          }

          &.error {
            color: #f56c6c;
          }
        }

        .check-name {
          font-weight: 500;
          color: #303133;
          min-width: 120px;
        }

        .check-message {
          color: #606266;
          font-size: 14px;
        }
      }
    }

    .validation-error {
      margin-top: 16px;
    }
  }

  // 步骤3: 表单编辑样式
  .firmware-form {
    flex: 1;
    overflow-y: auto;
    max-height: 500px;

    .form-section {
      margin-bottom: 32px;

      .section-title {
        margin: 0 0 16px 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        padding-bottom: 8px;
        border-bottom: 1px solid #ebeef5;
      }
    }

    .changelog-editor,
    .compatibility-editor,
    .issues-editor {
      .changelog-item,
      .compatibility-item,
      .issue-item {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;

        .el-input {
          flex: 1;
        }
      }
    }
  }

  // 步骤4: 上传进度样式
  .upload-progress-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .uploading {
      text-align: center;

      .upload-header {
        margin-bottom: 32px;

        h3 {
          margin: 0 0 8px 0;
          font-size: 18px;
          color: #303133;
        }

        p {
          margin: 0;
          font-size: 14px;
          color: #909399;
        }
      }

      .progress-section {
        margin-bottom: 32px;

        .main-progress {
          margin-bottom: 16px;
        }

        .progress-info {
          display: flex;
          justify-content: space-between;
          font-size: 14px;
          color: #606266;
        }
      }

      .upload-steps-detail {
        display: flex;
        flex-direction: column;
        gap: 12px;
        text-align: left;

        .upload-step-item {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px 16px;
          background: #f8f9fa;
          border-radius: 6px;

          &.processing {
            background: #f0f9ff;
            border-left: 3px solid #409eff;
          }

          &.completed {
            background: #f0f9ff;
            border-left: 3px solid #67c23a;
          }

          &.error {
            background: #fef0f0;
            border-left: 3px solid #f56c6c;
          }

          .step-icon {
            font-size: 16px;

            &.processing {
              color: #409eff;
            }

            &.completed {
              color: #67c23a;
            }

            &.error {
              color: #f56c6c;
            }
          }

          .step-name {
            font-weight: 500;
            color: #303133;
            min-width: 100px;
          }

          .step-message {
            color: #606266;
            font-size: 14px;
          }
        }
      }
    }

    .upload-completed {
      text-align: center;

      .success-icon {
        margin-bottom: 24px;

        .el-icon {
          font-size: 64px;
          color: #67c23a;
        }
      }

      h3 {
        margin: 0 0 8px 0;
        font-size: 20px;
        color: #303133;
      }

      p {
        margin: 0 0 32px 0;
        font-size: 14px;
        color: #606266;
      }

      .upload-result {
        background: #f8f9fa;
        border-radius: 8px;
        padding: 20px;
        text-align: left;

        .result-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 0;
          border-bottom: 1px solid #ebeef5;

          &:last-child {
            border-bottom: none;
          }

          .label {
            font-weight: 500;
            color: #606266;
          }

          .value {
            color: #303133;
            font-weight: 500;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .firmware-upload-dialog {
    .step-content {
      min-height: 300px;
    }

    .upload-area {
      .firmware-uploader {
        :deep(.el-upload-dragger) {
          height: 150px;
        }

        .upload-content {
          .upload-icon {
            font-size: 36px;
          }

          .upload-text {
            .upload-title {
              font-size: 14px;
            }

            .upload-hint {
              font-size: 12px;
            }
          }
        }
      }
    }

    .firmware-form {
      max-height: 400px;
    }

    .step-actions {
      flex-direction: column-reverse;
      gap: 8px;

      .el-button {
        width: 100%;
      }
    }
  }
}
</style>