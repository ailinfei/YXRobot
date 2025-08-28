<!--
  向导步骤3: 参数配置
-->
<template>
  <div class="wizard-step-configuration">
    <div class="step-header">
      <h2 class="step-title">配置训练参数</h2>
      <p class="step-description">
        根据您的需求配置AI训练参数，系统已为您预设了推荐配置。
      </p>
    </div>

    <el-form
      ref="formRef"
      :model="formData"
      label-width="140px"
      class="configuration-form"
    >
      <!-- 训练设置 -->
      <div class="form-section">
        <h3 class="section-title">
          <el-icon><Setting /></el-icon>
          训练设置
        </h3>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="训练模式">
              <el-select v-model="formData.trainingSettings.mode" style="width: 100%">
                <el-option label="快速模式 (推荐)" value="fast" />
                <el-option label="标准模式" value="standard" />
                <el-option label="高质量模式" value="high_quality" />
                <el-option label="自定义模式" value="custom" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="训练轮数">
              <el-input-number
                v-model="formData.trainingSettings.epochs"
                :min="10"
                :max="1000"
                :step="10"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="学习率">
              <el-select v-model="formData.trainingSettings.learningRate" style="width: 100%">
                <el-option label="自动 (推荐)" value="auto" />
                <el-option label="0.001" value="0.001" />
                <el-option label="0.01" value="0.01" />
                <el-option label="0.1" value="0.1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批次大小">
              <el-select v-model="formData.trainingSettings.batchSize" style="width: 100%">
                <el-option label="16 (快速)" :value="16" />
                <el-option label="32 (推荐)" :value="32" />
                <el-option label="64 (高质量)" :value="64" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="数据增强">
          <el-checkbox-group v-model="formData.trainingSettings.augmentation">
            <el-checkbox value="rotation">旋转</el-checkbox>
            <el-checkbox value="scaling">缩放</el-checkbox>
            <el-checkbox value="noise">噪声</el-checkbox>
            <el-checkbox value="blur">模糊</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </div>

      <!-- 质量设置 -->
      <div class="form-section">
        <h3 class="section-title">
          <el-icon><Medal /></el-icon>
          质量设置
        </h3>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="输出分辨率">
              <el-select v-model="formData.qualitySettings.resolution" style="width: 100%">
                <el-option label="512x512 (标准)" value="512" />
                <el-option label="1024x1024 (高清)" value="1024" />
                <el-option label="2048x2048 (超高清)" value="2048" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="质量阈值">
              <el-slider
                v-model="formData.qualitySettings.threshold"
                :min="60"
                :max="95"
                :step="5"
                show-stops
                show-input
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="风格保持度">
              <el-slider
                v-model="formData.qualitySettings.styleConsistency"
                :min="0.1"
                :max="1.0"
                :step="0.1"
                show-input
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创新程度">
              <el-slider
                v-model="formData.qualitySettings.creativity"
                :min="0.1"
                :max="1.0"
                :step="0.1"
                show-input
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="后处理选项">
          <el-checkbox-group v-model="formData.qualitySettings.postProcessing">
            <el-checkbox value="denoise">降噪处理</el-checkbox>
            <el-checkbox value="sharpen">锐化处理</el-checkbox>
            <el-checkbox value="normalize">标准化</el-checkbox>
            <el-checkbox value="enhance">质量增强</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </div>

      <!-- 高级设置 -->
      <div class="form-section" v-if="showAdvanced">
        <h3 class="section-title">
          <el-icon><Tools /></el-icon>
          高级设置
        </h3>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="GPU加速">
              <el-switch
                v-model="formData.advancedSettings.useGPU"
                active-text="启用"
                inactive-text="禁用"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="并行训练">
              <el-switch
                v-model="formData.advancedSettings.parallelTraining"
                active-text="启用"
                inactive-text="禁用"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="自定义参数">
          <el-input
            v-model="formData.advancedSettings.customParams"
            type="textarea"
            :rows="3"
            placeholder="JSON格式的自定义参数..."
          />
        </el-form-item>
      </div>
    </el-form>

    <!-- 配置预览 -->
    <div class="configuration-preview">
      <div class="preview-header">
        <h3 class="section-title">
          <el-icon><View /></el-icon>
          配置预览
        </h3>
        <el-button 
          type="primary" 
          text 
          @click="showAdvanced = !showAdvanced"
        >
          {{ showAdvanced ? '隐藏' : '显示' }}高级设置
        </el-button>
      </div>
      
      <div class="preview-content">
        <div class="preview-cards">
          <div class="preview-card">
            <div class="card-header">
              <el-icon><Timer /></el-icon>
              <span>预计训练时间</span>
            </div>
            <div class="card-value">{{ estimatedTime }}</div>
          </div>
          
          <div class="preview-card">
            <div class="card-header">
              <el-icon><Coin /></el-icon>
              <span>预计消耗积分</span>
            </div>
            <div class="card-value">{{ estimatedCost }}</div>
          </div>
          
          <div class="preview-card">
            <div class="card-header">
              <el-icon><DataLine /></el-icon>
              <span>预期质量评分</span>
            </div>
            <div class="card-value">{{ expectedQuality }}%</div>
          </div>
        </div>

        <div class="configuration-summary">
          <h4>配置摘要</h4>
          <ul class="summary-list">
            <li>训练模式: {{ getModeLabel(formData.trainingSettings.mode) }}</li>
            <li>训练轮数: {{ formData.trainingSettings.epochs }} 轮</li>
            <li>输出分辨率: {{ formData.qualitySettings.resolution }}x{{ formData.qualitySettings.resolution }}</li>
            <li>质量阈值: {{ formData.qualitySettings.threshold }}%</li>
            <li>数据增强: {{ formData.trainingSettings.augmentation.length }} 项</li>
            <li>后处理: {{ formData.qualitySettings.postProcessing.length }} 项</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 智能建议 -->
    <div class="smart-suggestions" v-if="suggestions.length > 0">
      <h3 class="section-title">
        <el-icon><Star /></el-icon>
        智能建议
      </h3>
      <div class="suggestions-list">
        <div 
          v-for="suggestion in suggestions" 
          :key="suggestion.type"
          class="suggestion-item"
        >
          <div class="suggestion-icon">
            <el-icon><InfoFilled /></el-icon>
          </div>
          <div class="suggestion-content">
            <div class="suggestion-title">{{ suggestion.title }}</div>
            <div class="suggestion-description">{{ suggestion.description }}</div>
          </div>
          <div class="suggestion-action" v-if="suggestion.action">
            <el-button 
              size="small" 
              type="primary" 
              text
              @click="applySuggestion(suggestion)"
            >
              应用
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Setting, Medal, Tools, View, Timer, Coin, DataLine, Star, InfoFilled } from '@element-plus/icons-vue'

interface ConfigurationData {
  trainingSettings: {
    mode: string
    epochs: number
    learningRate: string
    batchSize: number
    augmentation: string[]
  }
  qualitySettings: {
    resolution: string
    threshold: number
    styleConsistency: number
    creativity: number
    postProcessing: string[]
  }
  advancedSettings: {
    useGPU: boolean
    parallelTraining: boolean
    customParams: string
  }
}

interface StepValidation {
  isValid: boolean
  errors: string[]
  warnings: string[]
}

interface Suggestion {
  type: string
  title: string
  description: string
  action?: () => void
}

interface Props {
  modelValue: {
    configuration: ConfigurationData
  }
  validation?: StepValidation
}

interface Emits {
  (e: 'update:modelValue', value: any): void
  (e: 'validate', validation: StepValidation): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式数据
const formRef = ref()
const showAdvanced = ref(false)

// 表单数据
const formData = computed({
  get: () => props.modelValue.configuration,
  set: (value) => {
    emit('update:modelValue', {
      ...props.modelValue,
      configuration: value
    })
  }
})

// 计算属性
const estimatedTime = computed(() => {
  const baseTime = 30 // 基础时间（分钟）
  const epochMultiplier = formData.value.trainingSettings.epochs / 100
  const resolutionMultiplier = parseInt(formData.value.qualitySettings.resolution) / 512
  const modeMultiplier = getModeMultiplier(formData.value.trainingSettings.mode)
  
  const totalMinutes = Math.round(baseTime * epochMultiplier * resolutionMultiplier * modeMultiplier)
  
  if (totalMinutes < 60) {
    return `${totalMinutes} 分钟`
  } else {
    const hours = Math.floor(totalMinutes / 60)
    const minutes = totalMinutes % 60
    return `${hours} 小时 ${minutes} 分钟`
  }
})

const estimatedCost = computed(() => {
  const baseCost = 100
  const epochMultiplier = formData.value.trainingSettings.epochs / 100
  const resolutionMultiplier = parseInt(formData.value.qualitySettings.resolution) / 512
  const modeMultiplier = getModeMultiplier(formData.value.trainingSettings.mode)
  
  return Math.round(baseCost * epochMultiplier * resolutionMultiplier * modeMultiplier)
})

const expectedQuality = computed(() => {
  let baseQuality = 75
  
  // 训练模式影响
  const modeBonus = {
    fast: 0,
    standard: 5,
    high_quality: 15,
    custom: 10
  }[formData.value.trainingSettings.mode] || 0
  
  // 训练轮数影响
  const epochBonus = Math.min(formData.value.trainingSettings.epochs / 100 * 10, 15)
  
  // 分辨率影响
  const resolutionBonus = parseInt(formData.value.qualitySettings.resolution) / 512 * 5
  
  // 后处理影响
  const postProcessingBonus = formData.value.qualitySettings.postProcessing.length * 2
  
  return Math.min(Math.round(baseQuality + modeBonus + epochBonus + resolutionBonus + postProcessingBonus), 95)
})

const suggestions = computed<Suggestion[]>(() => {
  const sug: Suggestion[] = []
  
  // 基于训练模式的建议
  if (formData.value.trainingSettings.mode === 'fast') {
    sug.push({
      type: 'mode',
      title: '建议使用标准模式',
      description: '快速模式虽然速度快，但质量可能不如标准模式，建议使用标准模式获得更好的效果',
      action: () => {
        formData.value.trainingSettings.mode = 'standard'
      }
    })
  }
  
  // 基于训练轮数的建议
  if (formData.value.trainingSettings.epochs < 50) {
    sug.push({
      type: 'epochs',
      title: '建议增加训练轮数',
      description: '当前训练轮数较少，可能影响字体质量，建议至少设置100轮',
      action: () => {
        formData.value.trainingSettings.epochs = 100
      }
    })
  }
  
  // 基于数据增强的建议
  if (formData.value.trainingSettings.augmentation.length === 0) {
    sug.push({
      type: 'augmentation',
      title: '建议启用数据增强',
      description: '数据增强可以提高模型的泛化能力，建议启用旋转和缩放',
      action: () => {
        formData.value.trainingSettings.augmentation = ['rotation', 'scaling']
      }
    })
  }
  
  return sug
})

// 监听器
watch(formData, () => {
  validateStep()
}, { deep: true })

// 方法
const validateStep = () => {
  const errors: string[] = []
  const warnings: string[] = []
  
  // 基本验证
  if (formData.value.trainingSettings.epochs < 10) {
    errors.push('训练轮数不能少于10轮')
  }
  
  if (formData.value.qualitySettings.threshold < 60) {
    warnings.push('质量阈值较低，可能影响输出质量')
  }
  
  // 高级设置验证
  if (formData.value.advancedSettings.customParams) {
    try {
      JSON.parse(formData.value.advancedSettings.customParams)
    } catch {
      errors.push('自定义参数格式不正确，请使用有效的JSON格式')
    }
  }
  
  emit('validate', {
    isValid: errors.length === 0,
    errors,
    warnings
  })
}

const getModeLabel = (mode: string) => {
  const labels = {
    fast: '快速模式',
    standard: '标准模式',
    high_quality: '高质量模式',
    custom: '自定义模式'
  }
  return labels[mode as keyof typeof labels] || mode
}

const getModeMultiplier = (mode: string) => {
  const multipliers = {
    fast: 0.5,
    standard: 1.0,
    high_quality: 2.0,
    custom: 1.5
  }
  return multipliers[mode as keyof typeof multipliers] || 1.0
}

const applySuggestion = (suggestion: Suggestion) => {
  if (suggestion.action) {
    suggestion.action()
  }
}

// 初始化验证
validateStep()
</script>

<style lang="scss" scoped>
.wizard-step-configuration {
  max-width: 900px;
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

  .configuration-form {
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
  }

  .configuration-preview {
    margin-bottom: 32px;
    padding: 24px;
    background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
    border-radius: 8px;
    border: 1px solid var(--primary-light-7, #a0cfff);

    .preview-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .section-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--primary-color, #409eff);
        margin: 0;
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }

    .preview-content {
      .preview-cards {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 16px;
        margin-bottom: 24px;

        .preview-card {
          padding: 16px;
          background: white;
          border-radius: 8px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

          .card-header {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            color: var(--text-secondary, #606266);
            margin-bottom: 8px;
          }

          .card-value {
            font-size: 20px;
            font-weight: 600;
            color: var(--text-primary, #303133);
          }
        }
      }

      .configuration-summary {
        padding: 16px;
        background: white;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

        h4 {
          font-size: 14px;
          font-weight: 600;
          color: var(--text-primary, #303133);
          margin-bottom: 12px;
        }

        .summary-list {
          list-style: none;
          padding: 0;
          margin: 0;

          li {
            padding: 4px 0;
            font-size: 13px;
            color: var(--text-secondary, #606266);
            border-bottom: 1px solid var(--border-light, #f0f0f0);

            &:last-child {
              border-bottom: none;
            }
          }
        }
      }
    }
  }

  .smart-suggestions {
    padding: 24px;
    background: linear-gradient(135deg, #fff7e6 0%, #fef3e2 100%);
    border-radius: 8px;
    border: 1px solid var(--warning-light-7, #f0c78a);

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--warning-color, #e6a23c);
      margin-bottom: 16px;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .suggestions-list {
      .suggestion-item {
        display: flex;
        align-items: flex-start;
        padding: 16px;
        background: white;
        border-radius: 6px;
        margin-bottom: 12px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

        &:last-child {
          margin-bottom: 0;
        }

        .suggestion-icon {
          width: 24px;
          height: 24px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: var(--warning-color, #e6a23c);
          margin-right: 12px;
          margin-top: 2px;
        }

        .suggestion-content {
          flex: 1;

          .suggestion-title {
            font-size: 14px;
            font-weight: 600;
            color: var(--text-primary, #303133);
            margin-bottom: 4px;
          }

          .suggestion-description {
            font-size: 13px;
            color: var(--text-secondary, #606266);
            line-height: 1.5;
          }
        }

        .suggestion-action {
          margin-left: 12px;
        }
      }
    }
  }
}
</style>