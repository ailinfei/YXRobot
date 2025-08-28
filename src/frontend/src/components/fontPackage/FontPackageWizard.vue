<!--
  字体包创建智能向导组件
  提供步骤式引导界面，简化字体包创建流程
-->
<template>
  <div class="font-package-wizard">
    <!-- 步骤指示器 -->
    <div class="wizard-steps">
      <div 
        v-for="(step, index) in steps" 
        :key="step.key"
        class="step-indicator"
        :class="{
          'active': index + 1 === currentStep,
          'completed': index + 1 < currentStep,
          'upcoming': index + 1 > currentStep
        }"
      >
        <div class="step-circle">
          <el-icon v-if="index + 1 < currentStep" class="step-check">
            <Check />
          </el-icon>
          <span v-else class="step-number">{{ index + 1 }}</span>
        </div>
        <div class="step-label">{{ step.title }}</div>
        <div v-if="index < steps.length - 1" class="step-connector"></div>
      </div>
    </div>
    
    <!-- 向导头部 - 步骤导航和进度指示器 -->
    <div class="wizard-header">
      <div class="wizard-progress">
        <div class="progress-bar">
          <div 
            class="progress-fill" 
            :style="{ width: `${progressPercentage}%` }"
          ></div>
        </div>
        <div class="progress-text">
          步骤 {{ currentStep }} / {{ totalSteps }} - {{ currentStepTitle }}
        </div>
      </div>
    </div>

    <!-- 向导内容区域 -->
    <div class="wizard-content">
      <!-- 步骤1: 基本信息 -->
      <div v-if="currentStep === 1" class="step-content">
        <h3>{{ currentStepTitle }}</h3>
        <div class="form-container">
          <el-form :model="wizardData.basicInfo" label-width="120px" size="large">
            <el-form-item label="字体包名称" required>
              <el-input 
                v-model="wizardData.basicInfo.name" 
                placeholder="请输入字体包名称，如：优雅楷书"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item label="字体类型" required>
              <el-select v-model="wizardData.basicInfo.fontType" placeholder="选择字体类型">
                <el-option label="楷书" value="kaishu" />
                <el-option label="行书" value="xingshu" />
                <el-option label="隶书" value="lishu" />
                <el-option label="篆书" value="zhuanshu" />
                <el-option label="草书" value="caoshu" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="难度等级">
              <el-rate 
                v-model="wizardData.basicInfo.difficulty" 
                :max="5"
                show-text
                :texts="['入门', '简单', '中等', '困难', '专家']"
              />
            </el-form-item>
            
            <el-form-item label="版本号">
              <el-input 
                v-model="wizardData.basicInfo.version" 
                placeholder="如：v1.0.0"
                style="width: 200px;"
              />
            </el-form-item>
            
            <el-form-item label="描述信息">
              <el-input 
                v-model="wizardData.basicInfo.description" 
                type="textarea"
                :rows="4"
                placeholder="请描述这个字体包的特点和用途..."
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item label="标签">
              <el-tag
                v-for="tag in wizardData.basicInfo.tags"
                :key="tag"
                closable
                @close="removeTag(tag)"
                style="margin-right: 8px; margin-bottom: 8px;"
              >
                {{ tag }}
              </el-tag>
              <el-input
                v-if="tagInputVisible"
                ref="tagInputRef"
                v-model="tagInputValue"
                size="small"
                style="width: 120px;"
                @keyup.enter="handleTagInputConfirm"
                @blur="handleTagInputConfirm"
              />
              <el-button v-else size="small" @click="showTagInput">
                + 添加标签
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
      
      <!-- 步骤2: 样本上传 -->
      <div v-else-if="currentStep === 2" class="step-content">
        <h3>{{ currentStepTitle }}</h3>
        <div class="upload-container">
          <el-alert
            title="上传说明"
            type="info"
            :closable="false"
            show-icon
            style="margin-bottom: 20px;"
          >
            <template #default>
              <ul style="margin: 0; padding-left: 20px;">
                <li>支持PNG、JPG、JPEG格式的图片文件</li>
                <li>建议图片尺寸：256x256像素</li>
                <li>文件名应包含对应汉字，如：一.png、二.jpg</li>
                <li>建议上传50-200个常用汉字样本</li>
              </ul>
            </template>
          </el-alert>
          
          <el-upload
            class="upload-demo"
            drag
            :auto-upload="false"
            multiple
            :file-list="wizardData.sampleUpload.uploadedFiles"
            @change="handleFileChange"
            accept=".png,.jpg,.jpeg"
          >
            <el-icon class="el-icon--upload"><Upload /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持批量上传，建议每次上传50-200个字符样本
              </div>
            </template>
          </el-upload>
          
          <div v-if="wizardData.sampleUpload.uploadedFiles.length > 0" style="margin-top: 20px;">
            <h4>已上传文件 ({{ wizardData.sampleUpload.uploadedFiles.length }}个)</h4>
            <div class="file-preview">
              <div 
                v-for="(file, index) in wizardData.sampleUpload.uploadedFiles.slice(0, 10)" 
                :key="index"
                class="file-item"
              >
                <div class="file-name">{{ file.name }}</div>
                <div class="file-char">{{ extractCharFromFilename(file.name) }}</div>
              </div>
              <div v-if="wizardData.sampleUpload.uploadedFiles.length > 10" class="more-files">
                还有 {{ wizardData.sampleUpload.uploadedFiles.length - 10 }} 个文件...
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 步骤3: 参数配置 -->
      <div v-else-if="currentStep === 3" class="step-content">
        <h3>{{ currentStepTitle }}</h3>
        <div class="config-container">
          <el-form label-width="150px" size="large">
            <h4>训练参数</h4>
            <el-form-item label="训练轮数">
              <el-slider 
                v-model="trainingEpochs" 
                :min="10" 
                :max="100" 
                :step="10"
                show-stops
                show-input
                style="width: 300px;"
              />
              <div class="param-desc">建议：50-80轮，轮数越多质量越高但耗时更长</div>
            </el-form-item>
            
            <el-form-item label="学习率">
              <el-select v-model="learningRate" style="width: 200px;">
                <el-option label="0.001 (保守)" value="0.001" />
                <el-option label="0.01 (推荐)" value="0.01" />
                <el-option label="0.1 (激进)" value="0.1" />
              </el-select>
              <div class="param-desc">推荐使用0.01，适合大多数字体类型</div>
            </el-form-item>
            
            <h4 style="margin-top: 30px;">质量设置</h4>
            <el-form-item label="输出质量">
              <el-radio-group v-model="outputQuality">
                <el-radio label="standard">标准质量</el-radio>
                <el-radio label="high">高质量</el-radio>
                <el-radio label="ultra">超高质量</el-radio>
              </el-radio-group>
              <div class="param-desc">质量越高生成时间越长，文件体积越大</div>
            </el-form-item>
            
            <el-form-item label="生成字符数">
              <el-input-number 
                v-model="targetCharCount" 
                :min="100" 
                :max="10000" 
                :step="100"
                style="width: 200px;"
              />
              <div class="param-desc">根据样本数量自动推荐，可手动调整</div>
            </el-form-item>
          </el-form>
        </div>
      </div>
      
      <!-- 步骤4: 确认创建 -->
      <div v-else-if="currentStep === 4" class="step-content">
        <h3>{{ currentStepTitle }}</h3>
        <div class="review-container">
          <el-descriptions title="字体包信息确认" :column="2" border>
            <el-descriptions-item label="字体包名称">{{ wizardData.basicInfo.name || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="字体类型">{{ getFontTypeName(wizardData.basicInfo.fontType) }}</el-descriptions-item>
            <el-descriptions-item label="难度等级">{{ getDifficultyText(wizardData.basicInfo.difficulty) }}</el-descriptions-item>
            <el-descriptions-item label="版本号">{{ wizardData.basicInfo.version }}</el-descriptions-item>
            <el-descriptions-item label="样本数量">{{ wizardData.sampleUpload.uploadedFiles.length }}个</el-descriptions-item>
            <el-descriptions-item label="预计生成时间">{{ getEstimatedTime() }}</el-descriptions-item>
            <el-descriptions-item label="训练轮数">{{ trainingEpochs }}轮</el-descriptions-item>
            <el-descriptions-item label="输出质量">{{ getQualityText(outputQuality) }}</el-descriptions-item>
          </el-descriptions>
          
          <div style="margin-top: 20px;">
            <h4>描述信息</h4>
            <p style="background: #f8f9fa; padding: 12px; border-radius: 4px; margin: 0;">
              {{ wizardData.basicInfo.description || '无描述' }}
            </p>
          </div>
          
          <div style="margin-top: 20px;">
            <h4>标签</h4>
            <div>
              <el-tag 
                v-for="tag in wizardData.basicInfo.tags" 
                :key="tag" 
                style="margin-right: 8px;"
              >
                {{ tag }}
              </el-tag>
              <span v-if="wizardData.basicInfo.tags.length === 0" style="color: #999;">无标签</span>
            </div>
          </div>
          
          <el-alert
            title="确认信息"
            type="warning"
            :closable="false"
            show-icon
            style="margin-top: 20px;"
          >
            <template #default>
              <p>请仔细检查以上信息，确认无误后点击"完成创建"开始AI训练。</p>
              <p>训练过程将消耗大量计算资源，预计需要 {{ getEstimatedTime() }}，请耐心等待。</p>
            </template>
          </el-alert>
          
          <div style="margin-top: 20px;">
            <el-checkbox v-model="wizardData.review.confirmed">
              我已确认以上信息无误，同意开始AI字体生成训练
            </el-checkbox>
          </div>
        </div>
      </div>
      
      <!-- 步骤5: 训练监控 -->
      <div v-else-if="currentStep === 5" class="step-content">
        <h3>{{ currentStepTitle }}</h3>
        <div class="progress-container">
          <el-result
            icon="success"
            title="字体包创建成功！"
            sub-title="AI训练任务已启动，您可以在字体包管理页面查看训练进度"
          >
            <template #extra>
              <el-button type="primary" @click="handleComplete">返回管理页面</el-button>
            </template>
          </el-result>
        </div>
      </div>
    </div>

    <!-- 向导底部导航 -->
    <div class="wizard-footer">
      <div class="footer-left">
        <el-button 
          v-if="currentStep > 1"
          @click="handlePrevious"
        >
          <el-icon><ArrowLeft /></el-icon>
          上一步
        </el-button>
      </div>
      
      <div class="footer-right">
        <el-button 
          v-if="currentStep < totalSteps"
          type="primary"
          @click="handleNext"
        >
          下一步
          <el-icon><ArrowRight /></el-icon>
        </el-button>
        
        <el-button 
          v-else
          type="primary"
          @click="handleComplete"
        >
          完成创建
          <el-icon><Check /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, ArrowLeft, ArrowRight, Upload } from '@element-plus/icons-vue'

// 类型定义
interface WizardData {
  basicInfo: {
    name: string
    description: string
    fontType: string
    difficulty: number
    version: string
    tags: string[]
  }
  sampleUpload: {
    targetCharacters: string[]
    uploadedFiles: any[]
    analysisResult: any
  }
  configuration: {
    trainingSettings: any
    qualitySettings: any
  }
  review: {
    confirmed: boolean
  }
  progress: {
    packageId?: number
    trainingStatus: 'pending' | 'training' | 'paused' | 'completed' | 'failed'
    startedAt?: string
    completedAt?: string
  }
}

interface Props {
  modelValue?: boolean
  initialData?: Partial<WizardData>
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'complete', data: WizardData): void
  (e: 'cancel'): void
  (e: 'save-draft', data: WizardData): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  initialData: () => ({})
})

const emit = defineEmits<Emits>()

// 响应式数据
const currentStep = ref(1)

// 标签输入相关
const tagInputVisible = ref(false)
const tagInputValue = ref('')
const tagInputRef = ref()

// 配置参数
const trainingEpochs = ref(50)
const learningRate = ref('0.01')
const outputQuality = ref('high')
const targetCharCount = ref(3000)

// 向导步骤配置
const steps = [
  { key: 'basicInfo', title: '基本信息', description: '设置字体包基本信息' },
  { key: 'sampleUpload', title: '样本上传', description: '上传字符样本文件' },
  { key: 'configuration', title: '参数配置', description: '配置训练参数' },
  { key: 'review', title: '确认创建', description: '检查并确认信息' },
  { key: 'progress', title: '训练监控', description: '监控训练进度' }
]

// 向导数据
const wizardData = ref<WizardData>({
  basicInfo: {
    name: '',
    description: '',
    fontType: 'kaishu',
    difficulty: 3,
    version: 'v1.0.0',
    tags: []
  },
  sampleUpload: {
    targetCharacters: [],
    uploadedFiles: [],
    analysisResult: null
  },
  configuration: {
    trainingSettings: {},
    qualitySettings: {}
  },
  review: {
    confirmed: false
  },
  progress: {
    trainingStatus: 'pending'
  }
})

// 计算属性
const totalSteps = computed(() => steps.length)

const currentStepTitle = computed(() => {
  return steps[currentStep.value - 1]?.title || ''
})

const progressPercentage = computed(() => {
  return Math.round((currentStep.value / totalSteps.value) * 100)
})

// 导航处理
const handleNext = () => {
  if (currentStep.value < totalSteps.value) {
    currentStep.value++
  }
}

const handlePrevious = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

// 标签处理方法
const removeTag = (tag: string) => {
  const index = wizardData.value.basicInfo.tags.indexOf(tag)
  if (index > -1) {
    wizardData.value.basicInfo.tags.splice(index, 1)
  }
}

const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => {
    tagInputRef.value?.focus()
  })
}

const handleTagInputConfirm = () => {
  if (tagInputValue.value && !wizardData.value.basicInfo.tags.includes(tagInputValue.value)) {
    wizardData.value.basicInfo.tags.push(tagInputValue.value)
  }
  tagInputVisible.value = false
  tagInputValue.value = ''
}

// 文件上传处理
const handleFileChange = (file: any, fileList: any[]) => {
  wizardData.value.sampleUpload.uploadedFiles = fileList
  console.log('文件列表更新:', fileList.length, '个文件')
}

// 从文件名提取汉字
const extractCharFromFilename = (filename: string) => {
  const match = filename.match(/[\u4e00-\u9fa5]/g)
  return match ? match[0] : '?'
}

// 字体类型名称转换
const getFontTypeName = (type: string) => {
  const names: Record<string, string> = {
    kaishu: '楷书',
    xingshu: '行书',
    lishu: '隶书',
    zhuanshu: '篆书',
    caoshu: '草书'
  }
  return names[type] || type
}

// 难度等级文本
const getDifficultyText = (difficulty: number) => {
  const texts = ['', '入门', '简单', '中等', '困难', '专家']
  return texts[difficulty] || '未知'
}

// 质量设置文本
const getQualityText = (quality: string) => {
  const texts: Record<string, string> = {
    standard: '标准质量',
    high: '高质量',
    ultra: '超高质量'
  }
  return texts[quality] || quality
}

// 预计时间计算
const getEstimatedTime = () => {
  const fileCount = wizardData.value.sampleUpload.uploadedFiles.length
  const epochs = trainingEpochs.value
  const baseTime = Math.max(fileCount * 2, 30) // 每个文件至少2分钟，最少30分钟
  const totalMinutes = Math.round(baseTime * (epochs / 50)) // 基于50轮的基准时间
  
  if (totalMinutes < 60) {
    return `${totalMinutes}分钟`
  } else {
    const hours = Math.floor(totalMinutes / 60)
    const minutes = totalMinutes % 60
    return `${hours}小时${minutes > 0 ? minutes + '分钟' : ''}`
  }
}

// 完成处理
const handleComplete = () => {
  console.log('完成向导')
  
  // 保存配置参数到向导数据
  wizardData.value.configuration = {
    trainingSettings: {
      epochs: trainingEpochs.value,
      learningRate: learningRate.value,
      targetCharCount: targetCharCount.value
    },
    qualitySettings: {
      outputQuality: outputQuality.value
    }
  }
  
  ElMessage.success('字体包创建向导完成！')
  emit('complete', wizardData.value)
}

// 监听器
watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    console.log('向导打开')
    currentStep.value = 1
  }
})

// 生命周期
onMounted(() => {
  console.log('FontPackageWizard mounted')
})
</script>

<style scoped lang="scss">
.font-package-wizard {
  padding: 24px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 12px;
  
  // 步骤指示器样式
  .wizard-steps {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 32px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    backdrop-filter: blur(10px);
    
    .step-indicator {
      display: flex;
      flex-direction: column;
      align-items: center;
      position: relative;
      flex: 1;
      max-width: 120px;
      
      .step-circle {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
        font-size: 16px;
        margin-bottom: 8px;
        transition: all 0.3s ease;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        
        .step-number {
          color: #fff;
        }
        
        .step-check {
          color: #fff;
          font-size: 20px;
        }
      }
      
      .step-label {
        font-size: 13px;
        font-weight: 500;
        text-align: center;
        transition: all 0.3s ease;
        white-space: nowrap;
      }
      
      .step-connector {
        position: absolute;
        top: 24px;
        left: calc(50% + 24px);
        right: calc(-50% + 24px);
        height: 2px;
        background: #e4e7ed;
        transition: all 0.3s ease;
        z-index: 1;
      }
      
      // 已完成状态
      &.completed {
        .step-circle {
          background: linear-gradient(135deg, #67c23a, #85ce61);
          transform: scale(1.05);
          box-shadow: 0 4px 16px rgba(103, 194, 58, 0.3);
        }
        
        .step-label {
          color: #67c23a;
          font-weight: 600;
        }
        
        .step-connector {
          background: linear-gradient(90deg, #67c23a, #85ce61);
        }
      }
      
      // 当前激活状态
      &.active {
        .step-circle {
          background: linear-gradient(135deg, #409eff, #66b1ff);
          transform: scale(1.1);
          box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
          animation: pulse 2s infinite;
        }
        
        .step-label {
          color: #409eff;
          font-weight: 700;
          transform: scale(1.05);
        }
      }
      
      // 未来步骤状态
      &.upcoming {
        .step-circle {
          background: #f0f2f5;
          color: #c0c4cc;
          border: 2px solid #e4e7ed;
        }
        
        .step-label {
          color: #909399;
        }
      }
    }
  }
  
  @keyframes pulse {
    0% {
      box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
    }
    50% {
      box-shadow: 0 6px 25px rgba(64, 158, 255, 0.6);
    }
    100% {
      box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
    }
  }
  
  .wizard-header {
    margin-bottom: 24px;
    
    .wizard-progress {
      background: rgba(255, 255, 255, 0.9);
      padding: 16px 20px;
      border-radius: 12px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
      backdrop-filter: blur(10px);
      
      .progress-bar {
        width: 100%;
        height: 10px;
        background: rgba(240, 240, 240, 0.8);
        border-radius: 6px;
        overflow: hidden;
        margin-bottom: 12px;
        box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
        
        .progress-fill {
          height: 100%;
          background: linear-gradient(90deg, #409eff 0%, #67c23a 100%);
          transition: width 0.5s cubic-bezier(0.4, 0, 0.2, 1);
          box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
          position: relative;
          
          &::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
            animation: shimmer 2s infinite;
          }
        }
      }
      
      .progress-text {
        text-align: center;
        font-size: 15px;
        color: #303133;
        font-weight: 600;
        letter-spacing: 0.5px;
      }
    }
  }
  
  @keyframes shimmer {
    0% {
      transform: translateX(-100%);
    }
    100% {
      transform: translateX(100%);
    }
  }
  
  .wizard-content {
    flex: 1;
    overflow-y: auto;
    padding: 20px 0;
    
    .step-content {
      max-width: 800px;
      margin: 0 auto;
      padding: 20px;
      background: rgba(255, 255, 255, 0.95);
      border-radius: 16px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      
      h3 {
        margin: 0 0 32px 0;
        color: #303133;
        font-size: 28px;
        text-align: center;
        font-weight: 700;
        background: linear-gradient(135deg, #409eff, #67c23a);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          bottom: -8px;
          left: 50%;
          transform: translateX(-50%);
          width: 60px;
          height: 3px;
          background: linear-gradient(90deg, #409eff, #67c23a);
          border-radius: 2px;
        }
      }
      
      .form-container {
        text-align: left;
        
        .el-form {
          max-width: 600px;
          margin: 0 auto;
          
          .el-form-item {
            margin-bottom: 24px;
            
            :deep(.el-form-item__label) {
              font-weight: 600;
              color: #303133;
              font-size: 15px;
            }
            
            :deep(.el-input__wrapper) {
              border-radius: 8px;
              box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
              transition: all 0.3s ease;
              
              &:hover {
                box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
              }
              
              &.is-focus {
                box-shadow: 0 4px 16px rgba(64, 158, 255, 0.25);
              }
            }
            
            :deep(.el-select) {
              .el-input__wrapper {
                border-radius: 8px;
              }
            }
            
            :deep(.el-textarea__inner) {
              border-radius: 8px;
              box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
              transition: all 0.3s ease;
              
              &:hover {
                box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
              }
              
              &:focus {
                box-shadow: 0 4px 16px rgba(64, 158, 255, 0.25);
              }
            }
          }
        }
        
        .param-desc {
          font-size: 12px;
          color: #909399;
          margin-top: 6px;
          padding: 4px 8px;
          background: rgba(144, 147, 153, 0.1);
          border-radius: 4px;
          border-left: 3px solid #409eff;
        }
      }
      
      .upload-container {
        text-align: left;
        
        .upload-demo {
          margin-bottom: 24px;
          
          :deep(.el-upload-dragger) {
            border-radius: 12px;
            border: 2px dashed #d9d9d9;
            background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
            transition: all 0.3s ease;
            
            &:hover {
              border-color: #409eff;
              background: linear-gradient(135deg, #ecf5ff 0%, #ffffff 100%);
              transform: translateY(-2px);
              box-shadow: 0 8px 24px rgba(64, 158, 255, 0.15);
            }
          }
        }
        
        .file-preview {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
          gap: 16px;
          margin-top: 20px;
          
          .file-item {
            background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
            border: 1px solid #e9ecef;
            border-radius: 12px;
            padding: 12px;
            text-align: center;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
            
            &::before {
              content: '';
              position: absolute;
              top: 0;
              left: -100%;
              width: 100%;
              height: 100%;
              background: linear-gradient(90deg, transparent, rgba(64, 158, 255, 0.1), transparent);
              transition: left 0.5s ease;
            }
            
            &:hover {
              transform: translateY(-4px);
              box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
              border-color: #409eff;
              
              &::before {
                left: 100%;
              }
            }
            
            .file-name {
              font-size: 12px;
              color: #606266;
              margin-bottom: 8px;
              word-break: break-all;
              font-weight: 500;
            }
            
            .file-char {
              font-size: 24px;
              font-weight: bold;
              color: #409eff;
              text-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
            }
          }
          
          .more-files {
            grid-column: 1 / -1;
            text-align: center;
            color: #909399;
            font-style: italic;
            padding: 16px;
            background: rgba(144, 147, 153, 0.05);
            border-radius: 8px;
            border: 1px dashed #d9d9d9;
          }
        }
      }
      
      .config-container {
        text-align: left;
        
        .el-form {
          max-width: 600px;
          margin: 0 auto;
          
          .el-form-item {
            margin-bottom: 24px;
            
            :deep(.el-slider__runway) {
              border-radius: 6px;
              background: linear-gradient(90deg, #f0f2f5 0%, #e4e7ed 100%);
            }
            
            :deep(.el-slider__bar) {
              background: linear-gradient(90deg, #409eff 0%, #67c23a 100%);
              border-radius: 6px;
            }
            
            :deep(.el-slider__button) {
              border: 3px solid #409eff;
              box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
            }
          }
        }
        
        h4 {
          color: #303133;
          font-size: 18px;
          font-weight: 700;
          margin: 32px 0 20px 0;
          padding: 12px 16px;
          background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(103, 194, 58, 0.1) 100%);
          border-radius: 8px;
          border-left: 4px solid #409eff;
          position: relative;
          
          &::after {
            content: '';
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            width: 3px;
            background: linear-gradient(180deg, #409eff 0%, #67c23a 100%);
            border-radius: 0 8px 8px 0;
          }
        }
      }
      
      .review-container {
        text-align: left;
        
        h4 {
          color: #303133;
          font-size: 18px;
          font-weight: 600;
          margin: 24px 0 16px 0;
          padding-bottom: 8px;
          border-bottom: 2px solid transparent;
          background: linear-gradient(90deg, #409eff, #67c23a) bottom/100% 2px no-repeat;
        }
        
        .el-descriptions {
          margin-bottom: 24px;
          border-radius: 8px;
          overflow: hidden;
          box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
          
          :deep(.el-descriptions__header) {
            background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
            color: white;
            font-weight: 600;
          }
          
          :deep(.el-descriptions__body) {
            background: rgba(255, 255, 255, 0.8);
          }
        }
        
        .el-alert {
          border-radius: 12px;
          border: none;
          box-shadow: 0 4px 16px rgba(230, 162, 60, 0.15);
        }
        
        .el-checkbox {
          margin-top: 16px;
          padding: 12px;
          background: rgba(64, 158, 255, 0.05);
          border-radius: 8px;
          border: 1px solid rgba(64, 158, 255, 0.2);
          
          :deep(.el-checkbox__label) {
            font-weight: 500;
            color: #303133;
          }
        }
      }
      
      .progress-container {
        text-align: center;
        padding: 60px 20px;
        
        .el-result {
          :deep(.el-result__icon) {
            .el-result__success {
              color: #67c23a;
              font-size: 80px;
              animation: successPulse 2s ease-in-out infinite;
            }
          }
          
          :deep(.el-result__title) {
            font-size: 32px;
            font-weight: 700;
            background: linear-gradient(135deg, #67c23a, #85ce61);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
          }
          
          :deep(.el-result__subtitle) {
            font-size: 16px;
            color: #606266;
            margin-top: 16px;
          }
        }
      }
    }
  }
  
  @keyframes successPulse {
    0%, 100% {
      transform: scale(1);
    }
    50% {
      transform: scale(1.1);
    }
  }
  
  .wizard-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24px 20px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 12px;
    box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.08);
    backdrop-filter: blur(10px);
    margin-top: 24px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    .footer-left,
    .footer-right {
      display: flex;
      gap: 12px;
    }
    
    .el-button {
      padding: 12px 24px;
      border-radius: 8px;
      font-weight: 600;
      font-size: 14px;
      transition: all 0.3s ease;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
      }
      
      &.el-button--primary {
        background: linear-gradient(135deg, #409eff, #66b1ff);
        border: none;
        
        &:hover {
          background: linear-gradient(135deg, #66b1ff, #409eff);
          box-shadow: 0 4px 16px rgba(64, 158, 255, 0.3);
        }
      }
      
      &:not(.el-button--primary) {
        background: rgba(255, 255, 255, 0.8);
        border: 1px solid #e4e7ed;
        color: #606266;
        
        &:hover {
          background: rgba(255, 255, 255, 1);
          border-color: #409eff;
          color: #409eff;
        }
      }
      
      .el-icon {
        font-size: 16px;
      }
    }
  }
  
  // 响应式设计
  @media (max-width: 768px) {
    padding: 16px;
    
    .wizard-steps {
      padding: 16px;
      
      .step-indicator {
        max-width: 80px;
        
        .step-circle {
          width: 36px;
          height: 36px;
          font-size: 14px;
        }
        
        .step-label {
          font-size: 11px;
        }
      }
    }
    
    .wizard-content {
      .step-content {
        padding: 16px;
        
        h3 {
          font-size: 22px;
        }
        
        .form-container .el-form {
          :deep(.el-form-item__label) {
            font-size: 14px;
          }
        }
      }
    }
    
    .wizard-footer {
      padding: 16px;
      flex-direction: column;
      gap: 12px;
      
      .footer-left,
      .footer-right {
        width: 100%;
        justify-content: center;
      }
      
      .el-button {
        flex: 1;
        min-width: 120px;
      }
    }
  }
}
</style>