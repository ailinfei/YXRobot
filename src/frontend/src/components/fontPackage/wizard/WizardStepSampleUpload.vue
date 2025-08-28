<!--
  向导步骤2: 样本上传
-->
<template>
  <div class="wizard-step-sample-upload">
    <div class="step-header">
      <h2 class="step-title">上传字符样本</h2>
      <p class="step-description">
        上传字符样本文件，系统将自动识别字符并进行基础质量检查。
      </p>
    </div>

    <!-- 目标字符设置 -->
    <div class="form-section">
      <h3 class="section-title">目标字符设置</h3>
      <el-form-item label="字符输入方式">
        <el-radio-group v-model="characterInputMode" @change="handleInputModeChange">
          <el-radio value="manual">手动输入</el-radio>
          <el-radio value="preset">预设字符集</el-radio>
          <el-radio value="file">文件导入</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 手动输入 -->
      <div v-if="characterInputMode === 'manual'" class="character-input-section">
        <el-form-item label="目标字符">
          <el-input
            v-model="characterInput"
            type="textarea"
            :rows="6"
            placeholder="请输入要生成的汉字，每个字符之间用空格或换行分隔..."
            @input="handleCharacterInput"
          />
        </el-form-item>
        <div class="character-preview">
          <div class="preview-header">
            <span>字符预览 ({{ formData.targetCharacters.length }}个)</span>
            <el-button size="small" @click="clearCharacters">清空</el-button>
          </div>
          <div class="character-list">
            <el-tag
              v-for="char in formData.targetCharacters"
              :key="char"
              closable
              @close="removeCharacter(char)"
              class="character-tag"
            >
              {{ char }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 预设字符集 -->
      <div v-else-if="characterInputMode === 'preset'" class="preset-section">
        <el-form-item label="选择字符集">
          <el-checkbox-group v-model="selectedPresets" @change="handlePresetChange">
            <el-checkbox value="common100">常用汉字100字</el-checkbox>
            <el-checkbox value="common500">常用汉字500字</el-checkbox>
            <el-checkbox value="primary">小学必学汉字</el-checkbox>
            <el-checkbox value="numbers">数字汉字</el-checkbox>
            <el-checkbox value="colors">颜色汉字</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </div>

      <!-- 文件导入 -->
      <div v-else-if="characterInputMode === 'file'" class="file-import-section">
        <el-form-item label="导入文件">
          <el-upload
            :before-upload="beforeCharacterFileUpload"
            :on-success="handleCharacterFileSuccess"
            :on-error="handleCharacterFileError"
            accept=".txt,.csv"
            :limit="1"
            :auto-upload="true"
            drag
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 .txt 和 .csv 格式，每行一个汉字
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </div>
    </div>

    <!-- 样本文件上传 -->
    <div class="form-section">
      <h3 class="section-title">样本文件上传</h3>
      
      <!-- 使用新的智能上传组件 -->
      <SmartSampleUploader
        v-model="formData.uploadedFiles"
        :max-files="100"
        :max-size="10 * 1024 * 1024"
        @files-changed="handleFilesChanged"
        @file-added="handleFileAdded"
        @file-removed="handleFileRemoved"
      />
    </div>

    <!-- 智能分析进度 -->
    <div v-if="analysisState.isAnalyzing" class="form-section">
      <h3 class="section-title">
        <el-icon class="rotating"><Loading /></el-icon>
        智能分析中
      </h3>
      
      <div class="analysis-progress">
        <div class="progress-info">
          <span class="progress-step">{{ analysisState.currentStep }}</span>
          <span class="progress-percentage">{{ analysisState.progress }}%</span>
        </div>
        <el-progress 
          :percentage="analysisState.progress" 
          :status="analysisState.error ? 'exception' : 'success'"
          :stroke-width="8"
        />
        <div v-if="analysisState.error" class="error-message">
          <el-icon><Warning /></el-icon>
          {{ analysisState.error }}
        </div>
      </div>
    </div>

    <!-- 智能分析结果 -->
    <div v-if="analysisResult && !analysisState.isAnalyzing" class="form-section">
      <h3 class="section-title">
        <el-icon><DataLine /></el-icon>
        智能分析结果
        <el-button 
          v-if="smartAnalysisResult" 
          size="small" 
          type="primary" 
          text 
          @click="performIntelligentAnalysis"
        >
          重新分析
        </el-button>
      </h3>
      
      <!-- 分析统计卡片 -->
      <div class="analysis-summary">
        <div class="summary-cards">
          <div class="summary-card">
            <div class="card-icon success">
              <el-icon><Check /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-number">{{ analysisResult.recognizedCharacters }}</div>
              <div class="card-label">识别字符</div>
            </div>
          </div>
          
          <div class="summary-card">
            <div class="card-icon warning">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-number">{{ analysisResult.duplicates.length }}</div>
              <div class="card-label">重复文件</div>
            </div>
          </div>
          
          <div class="summary-card">
            <div class="card-icon info">
              <el-icon><InfoFilled /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-number">{{ analysisResult.qualityScore }}%</div>
              <div class="card-label">平均质量</div>
            </div>
          </div>

          <!-- 字符覆盖率卡片 -->
          <div v-if="characterCoverage" class="summary-card">
            <div class="card-icon" :class="characterCoverage.percentage >= 80 ? 'success' : 'warning'">
              <el-icon><DataLine /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-number">{{ characterCoverage.percentage }}%</div>
              <div class="card-label">字符覆盖率</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 重复文件详细信息 -->
      <div v-if="analysisResult.duplicates.length > 0" class="duplicates-alert">
        <el-alert
          title="发现重复文件"
          type="warning"
          :closable="false"
          show-icon
        >
          <template #default>
            <p>检测到 {{ analysisResult.duplicates.length }} 个重复文件，建议删除以提高训练效率：</p>
            
            <!-- 按类型分组显示重复文件 -->
            <div v-if="duplicatesByType.exact.length > 0" class="duplicate-group">
              <h4>完全重复 ({{ duplicatesByType.exact.length }}个)</h4>
              <ul class="duplicate-list">
                <li v-for="dup in duplicatesByType.exact.slice(0, 3)" :key="dup.file">
                  <strong>{{ dup.file }}</strong> 与 <strong>{{ dup.original }}</strong> 完全相同
                </li>
                <li v-if="duplicatesByType.exact.length > 3">
                  还有 {{ duplicatesByType.exact.length - 3 }} 个完全重复的文件...
                </li>
              </ul>
            </div>

            <div v-if="duplicatesByType.similar.length > 0" class="duplicate-group">
              <h4>相似文件 ({{ duplicatesByType.similar.length }}个)</h4>
              <ul class="duplicate-list">
                <li v-for="dup in duplicatesByType.similar.slice(0, 3)" :key="dup.file">
                  <strong>{{ dup.file }}</strong> 与 <strong>{{ dup.original }}</strong> 相似度 {{ Math.round(dup.similarity * 100) }}%
                </li>
                <li v-if="duplicatesByType.similar.length > 3">
                  还有 {{ duplicatesByType.similar.length - 3 }} 个相似文件...
                </li>
              </ul>
            </div>

            <div v-if="duplicatesByType.character.length > 0" class="duplicate-group">
              <h4>字符重复 ({{ duplicatesByType.character.length }}个)</h4>
              <ul class="duplicate-list">
                <li v-for="dup in duplicatesByType.character.slice(0, 3)" :key="dup.file">
                  <strong>{{ dup.file }}</strong> 与 <strong>{{ dup.original }}</strong> 包含相同字符
                </li>
                <li v-if="duplicatesByType.character.length > 3">
                  还有 {{ duplicatesByType.character.length - 3 }} 个字符重复...
                </li>
              </ul>
            </div>
          </template>
        </el-alert>
      </div>

      <!-- 质量问题详细信息 -->
      <div v-if="analysisResult.qualityIssues.length > 0" class="quality-issues">
        <el-alert
          title="质量检查发现问题"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <!-- 按严重程度分组显示问题 -->
            <div v-if="issuesBySeverity.high.length > 0" class="issues-group">
              <h4 class="issues-title high">高优先级问题 ({{ issuesBySeverity.high.length }}个)</h4>
              <div class="issues-list">
                <div v-for="issue in issuesBySeverity.high.slice(0, 2)" :key="issue.type + issue.message" class="issue-item">
                  <el-tag type="danger" size="small">高</el-tag>
                  <span class="issue-message">{{ issue.message }}</span>
                  <div v-if="issue.suggestion" class="issue-suggestion">
                    建议: {{ issue.suggestion }}
                  </div>
                </div>
              </div>
            </div>

            <div v-if="issuesBySeverity.medium.length > 0" class="issues-group">
              <h4 class="issues-title medium">中等问题 ({{ issuesBySeverity.medium.length }}个)</h4>
              <div class="issues-list">
                <div v-for="issue in issuesBySeverity.medium.slice(0, 2)" :key="issue.type + issue.message" class="issue-item">
                  <el-tag type="warning" size="small">中</el-tag>
                  <span class="issue-message">{{ issue.message }}</span>
                  <div v-if="issue.suggestion" class="issue-suggestion">
                    建议: {{ issue.suggestion }}
                  </div>
                </div>
              </div>
            </div>

            <div v-if="issuesBySeverity.low.length > 0" class="issues-group">
              <h4 class="issues-title low">低优先级问题 ({{ issuesBySeverity.low.length }}个)</h4>
              <div class="issues-list">
                <div v-for="issue in issuesBySeverity.low.slice(0, 1)" :key="issue.type + issue.message" class="issue-item">
                  <el-tag type="info" size="small">低</el-tag>
                  <span class="issue-message">{{ issue.message }}</span>
                </div>
              </div>
            </div>

            <div v-if="analysisResult.qualityIssues.length > 5" class="more-issues">
              还有 {{ analysisResult.qualityIssues.length - 5 }} 个问题未显示...
            </div>
          </template>
        </el-alert>
      </div>

      <!-- 智能建议 -->
      <div v-if="smartAnalysisResult" class="smart-suggestions">
        <h4 class="suggestions-title">
          <el-icon><InfoFilled /></el-icon>
          智能建议
        </h4>
        
        <div class="suggestions-content">
          <!-- 缺失字符提醒 -->
          <div v-if="getMissingCharacters(formData.targetCharacters).length > 0" class="suggestion-item">
            <el-tag type="warning" size="small">缺失字符</el-tag>
            <span>还需要为以下字符添加样本: </span>
            <el-tag 
              v-for="char in getMissingCharacters(formData.targetCharacters).slice(0, 10)" 
              :key="char"
              size="small"
              class="character-tag"
            >
              {{ char }}
            </el-tag>
            <span v-if="getMissingCharacters(formData.targetCharacters).length > 10">
              等 {{ getMissingCharacters(formData.targetCharacters).length }} 个字符
            </span>
          </div>

          <!-- 低质量字符提醒 -->
          <div v-if="getLowQualityCharacters().length > 0" class="suggestion-item">
            <el-tag type="info" size="small">质量改进</el-tag>
            <span>以下字符的样本质量较低，建议更换: </span>
            <el-tag 
              v-for="item in getLowQualityCharacters().slice(0, 5)" 
              :key="item.character"
              size="small"
              class="character-tag"
            >
              {{ item.character }} ({{ item.quality }}%)
            </el-tag>
          </div>

          <!-- 重复文件清理建议 -->
          <div v-if="getDuplicateRemovalSuggestions().length > 0" class="suggestion-item">
            <el-tag type="warning" size="small">文件清理</el-tag>
            <span>建议删除 {{ getDuplicateRemovalSuggestions().length }} 个重复文件以优化训练效果</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, DataLine, Check, Warning, InfoFilled, Loading } from '@element-plus/icons-vue'
import { useSmartSampleAnalysis } from '@/composables/useSmartSampleAnalysis'
import { useWizardStepIntegration } from '@/composables/useWizardStepIntegration'
import SmartSampleUploader from '@/components/fontPackage/SmartSampleUploader.vue'

interface SampleUploadData {
  targetCharacters: string[]
  uploadedFiles: any[]
  analysisResult: any
}

interface StepValidation {
  isValid: boolean
  errors: string[]
  warnings: string[]
}

interface Props {
  modelValue: {
    sampleUpload: SampleUploadData
  }
  validation?: StepValidation
}

interface Emits {
  (e: 'update:modelValue', value: any): void
  (e: 'validate', validation: StepValidation): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 智能分析功能
const {
  analysisState,
  analysisResult: smartAnalysisResult,
  analysisStats,
  duplicatesByType,
  issuesBySeverity,
  characterCoverage,
  analyzeFiles,
  getMissingCharacters,
  getLowQualityCharacters,
  getDuplicateRemovalSuggestions,
  getQualityImprovementSuggestions
} = useSmartSampleAnalysis()

// 向导步骤集成
const {
  updateStepData,
  updateStepValidation,
  prepareStepTransition
} = useWizardStepIntegration()

// 响应式数据
const uploadRef = ref()
const characterInputMode = ref<'manual' | 'preset' | 'file'>('manual')
const characterInput = ref('')
const selectedPresets = ref<string[]>([])

// 移除旧的上传进度变量，现在由SmartSampleUploader组件内部管理

// 预设字符集
const presetCharacterSets: Record<string, string[]> = {
  common100: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '人', '大', '小', '上', '下', '左', '右', '中', '国', '家'],
  common500: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十'],
  primary: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十'],
  numbers: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '百', '千', '万'],
  colors: ['红', '黄', '蓝', '绿', '白', '黑', '灰', '紫', '粉', '橙']
}

// 表单数据
const formData = computed({
  get: () => props.modelValue.sampleUpload,
  set: (value) => {
    emit('update:modelValue', {
      ...props.modelValue,
      sampleUpload: value
    })
  }
})

// 分析结果 - 优先使用智能分析结果
const analysisResult = computed(() => {
  if (smartAnalysisResult.value) {
    return {
      recognizedCharacters: smartAnalysisResult.value.recognizedCharacters,
      duplicates: smartAnalysisResult.value.duplicates,
      qualityScore: smartAnalysisResult.value.basicQualityCheck.averageQuality,
      qualityIssues: smartAnalysisResult.value.basicQualityCheck.issues
    }
  }
  return formData.value.analysisResult
})

// 监听器
watch(formData, () => {
  validateStep()
}, { deep: true })

// 监听目标字符变化，自动重新分析
watch(() => formData.value.targetCharacters, (newChars, oldChars) => {
  // 只有在有上传文件且字符发生实际变化时才重新分析
  if (formData.value.uploadedFiles.length > 0 && 
      JSON.stringify(newChars) !== JSON.stringify(oldChars)) {
    console.log('目标字符已更新，重新分析样本...')
    performIntelligentAnalysis()
  }
}, { deep: true })

// 监听智能分析状态，提供用户反馈
watch(() => analysisState.isAnalyzing, (isAnalyzing) => {
  if (isAnalyzing) {
    console.log('开始智能分析...')
  } else {
    console.log('智能分析完成')
  }
})

// 增强的验证逻辑
const validateStep = () => {
  const errors: string[] = []
  const warnings: string[] = []
  
  // 检查目标字符
  if (formData.value.targetCharacters.length === 0) {
    errors.push('请至少设置一个目标字符')
  }
  
  // 检查上传文件
  if (formData.value.uploadedFiles.length === 0) {
    errors.push('请上传至少一个样本文件')
  }
  
  // 基于智能分析结果的验证
  if (smartAnalysisResult.value) {
    const analysisData = smartAnalysisResult.value
    
    // 检查字符覆盖率
    const coverage = characterCoverage.value
    if (coverage && coverage.percentage < 50) {
      warnings.push(`字符覆盖率较低 (${coverage.percentage}%)，建议上传更多样本`)
    }
    
    // 检查质量分数
    if (analysisData.basicQualityCheck.averageQuality < 60) {
      warnings.push(`样本平均质量较低 (${analysisData.basicQualityCheck.averageQuality}%)，建议提高图片质量`)
    }
    
    // 检查重复文件
    if (analysisData.duplicates.length > 0) {
      warnings.push(`发现 ${analysisData.duplicates.length} 个重复文件，建议清理以提高训练效率`)
    }
    
    // 检查高优先级质量问题
    const highPriorityIssues = analysisData.basicQualityCheck.issues.filter(
      issue => issue.severity === 'high'
    )
    if (highPriorityIssues.length > 0) {
      errors.push(`存在 ${highPriorityIssues.length} 个高优先级质量问题，请修复后继续`)
    }
    
    // 检查缺失字符
    const missingChars = getMissingCharacters(formData.value.targetCharacters)
    if (missingChars.length > 0) {
      if (missingChars.length > formData.value.targetCharacters.length * 0.5) {
        errors.push(`缺少超过50%字符的样本 (${missingChars.length}/${formData.value.targetCharacters.length})`)
      } else {
        warnings.push(`缺少 ${missingChars.length} 个字符的样本`)
      }
    }
  }
  
  // 添加其他警告
  if (formData.value.targetCharacters.length > 500) {
    warnings.push('目标字符较多，训练时间可能较长')
  }
  
  if (formData.value.uploadedFiles.length < 10) {
    warnings.push('样本文件较少，建议上传更多文件以提高训练效果')
  }
  
  const validation = {
    isValid: errors.length === 0,
    errors,
    warnings
  }
  
  // 更新向导步骤验证状态
  updateStepValidation(2, validation)
  
  emit('validate', validation)
}

// 字符输入处理
const handleInputModeChange = () => {
  formData.value.targetCharacters = []
  characterInput.value = ''
  selectedPresets.value = []
}

const handleCharacterInput = () => {
  const chars = characterInput.value
    .split(/[\s\n,，]+/)
    .filter(char => char.trim() && /[\u4e00-\u9fa5]/.test(char))
    .filter((char, index, arr) => arr.indexOf(char) === index)
  
  formData.value.targetCharacters = chars
}

const handlePresetChange = () => {
  const allChars = new Set<string>()
  
  selectedPresets.value.forEach(preset => {
    const chars = presetCharacterSets[preset] || []
    chars.forEach(char => allChars.add(char))
  })
  
  formData.value.targetCharacters = Array.from(allChars)
}

const removeCharacter = (char: string) => {
  const index = formData.value.targetCharacters.indexOf(char)
  if (index > -1) {
    formData.value.targetCharacters.splice(index, 1)
  }
}

const clearCharacters = () => {
  formData.value.targetCharacters = []
  characterInput.value = ''
}

// 字符文件上传
const beforeCharacterFileUpload = (file: File) => {
  const isValidType = file.type === 'text/plain' || file.name.endsWith('.csv')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isValidType) {
    ElMessage.error('只能上传 .txt 或 .csv 格式的文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
    return false
  }
  return true
}

const handleCharacterFileSuccess = (response: any, file: File) => {
  ElMessage.success('文件上传成功，正在解析字符...')
  
  const reader = new FileReader()
  reader.onload = (e) => {
    const content = e.target?.result as string
    const chars = content
      .split(/[\s\n,，]+/)
      .filter(char => char.trim() && /[\u4e00-\u9fa5]/.test(char))
      .filter((char, index, arr) => arr.indexOf(char) === index)
    
    formData.value.targetCharacters = chars
    ElMessage.success(`成功导入 ${chars.length} 个汉字`)
  }
  reader.readAsText(file.raw)
}

const handleCharacterFileError = () => {
  ElMessage.error('文件上传失败')
}

// 智能上传组件事件处理
const handleFilesChanged = (files: any[]) => {
  // 文件列表变化时触发智能分析
  if (files.length > 0) {
    performIntelligentAnalysis()
  } else {
    formData.value.analysisResult = null
  }
}

const handleFileAdded = (file: any) => {
  console.log('文件已添加:', file.name)
  // 可以在这里添加单个文件添加后的处理逻辑
}

const handleFileRemoved = (file: any) => {
  console.log('文件已删除:', file.name)
  // 重新分析剩余文件
  if (formData.value.uploadedFiles.length > 0) {
    performIntelligentAnalysis()
  }
}

// 智能分析
const performIntelligentAnalysis = async () => {
  if (formData.value.uploadedFiles.length === 0) {
    return
  }

  try {
    // 使用新的智能分析服务
    await analyzeFiles(formData.value.uploadedFiles, formData.value.targetCharacters)
    
    // 更新表单数据以保持兼容性
    if (smartAnalysisResult.value) {
      formData.value.analysisResult = {
        recognizedCharacters: smartAnalysisResult.value.recognizedCharacters,
        duplicates: smartAnalysisResult.value.duplicates,
        qualityScore: smartAnalysisResult.value.basicQualityCheck.averageQuality,
        qualityIssues: smartAnalysisResult.value.basicQualityCheck.issues
      }
      
      // 触发向导级别的数据同步
      syncDataWithWizard()
    }
  } catch (error) {
    console.error('智能分析失败:', error)
    ElMessage.error('智能分析失败，请重试')
  }
}

// 与向导主组件同步数据
const syncDataWithWizard = () => {
  // 确保分析结果同步到向导数据中
  if (smartAnalysisResult.value) {
    // 更新字符映射，帮助后续步骤使用
    const characterMap = smartAnalysisResult.value.characterMap
    const recognizedChars = Object.keys(characterMap).filter(char => 
      characterMap[char].files.length > 0
    )
    
    // 如果目标字符为空，自动填充识别到的字符
    if (formData.value.targetCharacters.length === 0 && recognizedChars.length > 0) {
      formData.value.targetCharacters = recognizedChars
      ElMessage.info(`自动识别到 ${recognizedChars.length} 个字符，已添加到目标字符列表`)
    }
    
    // 检查字符覆盖率并提供建议
    const missingChars = getMissingCharacters(formData.value.targetCharacters)
    if (missingChars.length > 0) {
      console.log(`缺少 ${missingChars.length} 个字符的样本:`, missingChars)
    }
    
    // 更新向导步骤数据
    updateStepData(2, {
      targetCharacters: formData.value.targetCharacters,
      uploadedFiles: formData.value.uploadedFiles,
      analysisResult: formData.value.analysisResult,
      smartAnalysisResult: smartAnalysisResult.value,
      characterCoverage: characterCoverage.value,
      qualityStats: analysisStats.value
    })
    
    // 触发验证更新
    validateStep()
  }
}

const getIssueTagType = (severity: string) => {
  switch (severity) {
    case 'high': return 'danger'
    case 'medium': return 'warning'
    case 'low': return 'info'
    default: return 'info'
  }
}

// 初始化验证
validateStep()
</script>

<style lang="scss" scoped>
.wizard-step-sample-upload {
  max-width: 800px;
  margin: 0 auto;

  .step-header {
    text-align: center;
    margin-bottom: 32px;

    .step-title {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary, #303133);
      margin-bottom: 8px;
    }

    .step-description {
      font-size: 14px;
      color: var(--text-secondary, #606266);
      line-height: 1.6;
    }
  }

  .form-section {
    margin-bottom: 32px;
    padding: 24px;
    background: var(--bg-secondary, #f8f9fa);
    border-radius: 8px;
    border: 1px solid var(--border-color, #e4e7ed);

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary, #303133);
      margin-bottom: 16px;
      padding-bottom: 8px;
      border-bottom: 2px solid var(--primary-color, #409eff);
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .character-input-section {
    .character-preview {
      margin-top: 16px;
      padding: 16px;
      background: white;
      border-radius: 8px;
      border: 1px solid var(--border-color, #e4e7ed);
      
      .preview-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        font-weight: 500;
      }
      
      .character-list {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        
        .character-tag {
          font-size: 16px;
          padding: 4px 8px;
        }
      }
    }
  }

  .preset-section {
    .el-checkbox-group {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
  }

  // 上传相关样式现在由SmartSampleUploader组件管理

  .analysis-summary {
    .summary-cards {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;
      margin-bottom: 24px;

      .summary-card {
        display: flex;
        align-items: center;
        padding: 16px;
        background: white;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

        .card-icon {
          width: 48px;
          height: 48px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;
          font-size: 20px;

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

        .card-content {
          .card-number {
            font-size: 24px;
            font-weight: 600;
            color: var(--text-primary, #303133);
            line-height: 1;
          }

          .card-label {
            font-size: 12px;
            color: var(--text-secondary, #606266);
            margin-top: 4px;
          }
        }
      }
    }
  }

  .analysis-progress {
    padding: 20px;
    background: white;
    border-radius: 8px;
    border: 1px solid var(--border-color, #e4e7ed);

    .progress-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      .progress-step {
        font-size: 14px;
        color: var(--text-primary, #303133);
        font-weight: 500;
      }

      .progress-percentage {
        font-size: 14px;
        color: var(--text-secondary, #606266);
      }
    }

    .error-message {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-top: 12px;
      padding: 8px 12px;
      background: var(--danger-light-9, #fef0f0);
      color: var(--danger-color, #f56c6c);
      border-radius: 4px;
      font-size: 13px;
    }
  }

  .duplicates-alert,
  .quality-issues {
    margin-top: 16px;

    .duplicate-group,
    .issues-group {
      margin-bottom: 16px;

      h4 {
        font-size: 14px;
        font-weight: 600;
        margin-bottom: 8px;
        color: var(--text-primary, #303133);

        &.high { color: var(--danger-color, #f56c6c); }
        &.medium { color: var(--warning-color, #e6a23c); }
        &.low { color: var(--info-color, #909399); }
      }
    }

    .duplicate-list {
      margin: 8px 0 0 16px;
      
      li {
        margin-bottom: 6px;
        font-size: 13px;
        line-height: 1.4;

        strong {
          color: var(--text-primary, #303133);
        }
      }
    }

    .issues-list {
      .issue-item {
        display: flex;
        align-items: flex-start;
        gap: 8px;
        margin-bottom: 12px;
        padding: 8px;
        background: var(--bg-light, #fafafa);
        border-radius: 4px;

        .issue-message {
          font-size: 13px;
          flex: 1;
        }

        .issue-suggestion {
          font-size: 12px;
          color: var(--text-secondary, #606266);
          margin-top: 4px;
          padding-left: 16px;
          border-left: 2px solid var(--border-color, #e4e7ed);
        }
      }

      .more-issues {
        font-size: 13px;
        color: var(--text-secondary, #606266);
        margin-top: 8px;
        text-align: center;
        font-style: italic;
      }
    }
  }

  .smart-suggestions {
    margin-top: 20px;
    padding: 16px;
    background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
    border-radius: 8px;
    border: 1px solid var(--primary-light-8, #b3d8ff);

    .suggestions-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--primary-color, #409eff);
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .suggestions-content {
      .suggestion-item {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 12px;
        padding: 8px 12px;
        background: white;
        border-radius: 6px;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        font-size: 13px;
        line-height: 1.4;

        .character-tag {
          margin: 0 2px;
          font-family: 'SimSun', serif;
        }
      }
    }
  }

  .rotating {
    animation: rotate 2s linear infinite;
  }

  @keyframes rotate {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
}
</style>