<!--
  向导步骤4: 确认创建
-->
<template>
  <div class="wizard-step-review">
    <div class="step-header">
      <h2 class="step-title">确认创建字体包</h2>
      <p class="step-description">
        请仔细检查以下信息，确认无误后即可开始创建字体包。
      </p>
    </div>

    <!-- 信息汇总 -->
    <div class="review-sections">
      <!-- 基本信息 -->
      <div class="review-section">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><InfoFilled /></el-icon>
            基本信息
          </h3>
          <el-button type="primary" text size="small" @click="editSection('basicInfo')">
            编辑
          </el-button>
        </div>
        <div class="section-content">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">字体包名称:</span>
              <span class="info-value">{{ wizardData.basicInfo.name }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">版本号:</span>
              <span class="info-value">{{ wizardData.basicInfo.version }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">字体类型:</span>
              <span class="info-value">{{ getFontTypeLabel(wizardData.basicInfo.fontType) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">难度等级:</span>
              <span class="info-value">{{ getDifficultyLabel(wizardData.basicInfo.difficulty) }}</span>
            </div>
            <div class="info-item full-width">
              <span class="info-label">描述:</span>
              <span class="info-value">{{ wizardData.basicInfo.description }}</span>
            </div>
            <div class="info-item full-width" v-if="wizardData.basicInfo.tags.length > 0">
              <span class="info-label">标签:</span>
              <div class="tags-display">
                <el-tag 
                  v-for="tag in wizardData.basicInfo.tags" 
                  :key="tag" 
                  size="small"
                  class="tag-item"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 样本信息 -->
      <div class="review-section">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><Picture /></el-icon>
            样本信息
          </h3>
          <el-button type="primary" text size="small" @click="editSection('sampleUpload')">
            编辑
          </el-button>
        </div>
        <div class="section-content">
          <div class="sample-summary">
            <div class="summary-cards">
              <div class="summary-card">
                <div class="card-icon">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="card-content">
                  <div class="card-number">{{ wizardData.sampleUpload.targetCharacters.length }}</div>
                  <div class="card-label">目标字符</div>
                </div>
              </div>
              <div class="summary-card">
                <div class="card-icon">
                  <el-icon><Upload /></el-icon>
                </div>
                <div class="card-content">
                  <div class="card-number">{{ wizardData.sampleUpload.uploadedFiles.length }}</div>
                  <div class="card-label">上传文件</div>
                </div>
              </div>
              <div class="summary-card" v-if="wizardData.sampleUpload.analysisResult">
                <div class="card-icon">
                  <el-icon><DataLine /></el-icon>
                </div>
                <div class="card-content">
                  <div class="card-number">{{ wizardData.sampleUpload.analysisResult.qualityScore }}%</div>
                  <div class="card-label">平均质量</div>
                </div>
              </div>
            </div>
            
            <!-- 字符预览 -->
            <div class="characters-preview">
              <div class="preview-header">
                <span>目标字符预览 (前20个)</span>
              </div>
              <div class="characters-list">
                <el-tag 
                  v-for="char in wizardData.sampleUpload.targetCharacters.slice(0, 20)" 
                  :key="char"
                  class="character-tag"
                >
                  {{ char }}
                </el-tag>
                <span v-if="wizardData.sampleUpload.targetCharacters.length > 20" class="more-indicator">
                  还有 {{ wizardData.sampleUpload.targetCharacters.length - 20 }} 个字符...
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 配置信息 -->
      <div class="review-section">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><Setting /></el-icon>
            配置信息
          </h3>
          <el-button type="primary" text size="small" @click="editSection('configuration')">
            编辑
          </el-button>
        </div>
        <div class="section-content">
          <div class="config-summary">
            <div class="config-group">
              <h4>训练设置</h4>
              <div class="config-items">
                <div class="config-item">
                  <span class="config-label">训练模式:</span>
                  <span class="config-value">{{ getModeLabel(wizardData.configuration.trainingSettings.mode) }}</span>
                </div>
                <div class="config-item">
                  <span class="config-label">训练轮数:</span>
                  <span class="config-value">{{ wizardData.configuration.trainingSettings.epochs }} 轮</span>
                </div>
                <div class="config-item">
                  <span class="config-label">学习率:</span>
                  <span class="config-value">{{ wizardData.configuration.trainingSettings.learningRate }}</span>
                </div>
                <div class="config-item">
                  <span class="config-label">批次大小:</span>
                  <span class="config-value">{{ wizardData.configuration.trainingSettings.batchSize }}</span>
                </div>
              </div>
            </div>
            
            <div class="config-group">
              <h4>质量设置</h4>
              <div class="config-items">
                <div class="config-item">
                  <span class="config-label">输出分辨率:</span>
                  <span class="config-value">{{ wizardData.configuration.qualitySettings.resolution }}x{{ wizardData.configuration.qualitySettings.resolution }}</span>
                </div>
                <div class="config-item">
                  <span class="config-label">质量阈值:</span>
                  <span class="config-value">{{ wizardData.configuration.qualitySettings.threshold }}%</span>
                </div>
                <div class="config-item">
                  <span class="config-label">风格保持度:</span>
                  <span class="config-value">{{ wizardData.configuration.qualitySettings.styleConsistency }}</span>
                </div>
                <div class="config-item">
                  <span class="config-label">创新程度:</span>
                  <span class="config-value">{{ wizardData.configuration.qualitySettings.creativity }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 预估信息 -->
    <div class="estimation-section">
      <h3 class="section-title">
        <el-icon><DataLine /></el-icon>
        预估信息
      </h3>
      <div class="estimation-cards">
        <div class="estimation-card">
          <div class="card-icon time">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-title">预计训练时间</div>
            <div class="card-value">{{ estimatedTime }}</div>
          </div>
        </div>
        
        <div class="estimation-card">
          <div class="card-icon cost">
            <el-icon><Coin /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-title">预计消耗积分</div>
            <div class="card-value">{{ estimatedCost }}</div>
          </div>
        </div>
        
        <div class="estimation-card">
          <div class="card-icon quality">
            <el-icon><Medal /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-title">预期质量评分</div>
            <div class="card-value">{{ expectedQuality }}%</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 确认选项 -->
    <div class="confirmation-section">
      <el-checkbox v-model="formData.confirmed" size="large">
        我已仔细检查以上信息，确认创建此字体包
      </el-checkbox>
      
      <div class="confirmation-notes">
        <el-alert
          title="创建提醒"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <ul class="notes-list">
              <li>字体包创建后将立即开始AI训练过程</li>
              <li>训练过程中可以随时查看进度，但无法修改配置</li>
              <li>训练完成后系统会自动进行质量评估</li>
              <li>如果质量不达标，系统会提供改进建议</li>
            </ul>
          </template>
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { 
  InfoFilled, Picture, Setting, Document, Upload, DataLine, 
  Timer, Coin, Medal 
} from '@element-plus/icons-vue'

interface ReviewData {
  confirmed: boolean
}

interface StepValidation {
  isValid: boolean
  errors: string[]
  warnings: string[]
}

interface Props {
  modelValue: {
    basicInfo: any
    sampleUpload: any
    configuration: any
    review: ReviewData
  }
  validation?: StepValidation
}

interface Emits {
  (e: 'update:modelValue', value: any): void
  (e: 'validate', validation: StepValidation): void
  (e: 'edit-section', section: string): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 表单数据
const formData = computed({
  get: () => props.modelValue.review,
  set: (value) => {
    emit('update:modelValue', {
      ...props.modelValue,
      review: value
    })
    validateStep()
  }
})

const wizardData = computed(() => props.modelValue)

// 计算属性
const estimatedTime = computed(() => {
  const baseTime = 30
  const epochs = wizardData.value.configuration.trainingSettings.epochs
  const resolution = parseInt(wizardData.value.configuration.qualitySettings.resolution)
  const mode = wizardData.value.configuration.trainingSettings.mode
  
  const epochMultiplier = epochs / 100
  const resolutionMultiplier = resolution / 512
  const modeMultiplier = getModeMultiplier(mode)
  
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
  const epochs = wizardData.value.configuration.trainingSettings.epochs
  const resolution = parseInt(wizardData.value.configuration.qualitySettings.resolution)
  const mode = wizardData.value.configuration.trainingSettings.mode
  
  const epochMultiplier = epochs / 100
  const resolutionMultiplier = resolution / 512
  const modeMultiplier = getModeMultiplier(mode)
  
  return Math.round(baseCost * epochMultiplier * resolutionMultiplier * modeMultiplier)
})

const expectedQuality = computed(() => {
  let baseQuality = 75
  
  const mode = wizardData.value.configuration.trainingSettings.mode
  const epochs = wizardData.value.configuration.trainingSettings.epochs
  const resolution = parseInt(wizardData.value.configuration.qualitySettings.resolution)
  const postProcessing = wizardData.value.configuration.qualitySettings.postProcessing
  
  const modeBonus = {
    fast: 0,
    standard: 5,
    high_quality: 15,
    custom: 10
  }[mode] || 0
  
  const epochBonus = Math.min(epochs / 100 * 10, 15)
  const resolutionBonus = resolution / 512 * 5
  const postProcessingBonus = postProcessing.length * 2
  
  return Math.min(Math.round(baseQuality + modeBonus + epochBonus + resolutionBonus + postProcessingBonus), 95)
})

// 方法
const validateStep = () => {
  const errors: string[] = []
  const warnings: string[] = []
  
  if (!formData.value.confirmed) {
    errors.push('请确认已检查所有信息')
  }
  
  emit('validate', {
    isValid: errors.length === 0,
    errors,
    warnings
  })
}

const editSection = (section: string) => {
  emit('edit-section', section)
}

const getFontTypeLabel = (type: string) => {
  const labels = {
    kaishu: '楷书',
    xingshu: '行书',
    lishu: '隶书',
    zhuanshu: '篆书',
    caoshu: '草书'
  }
  return labels[type as keyof typeof labels] || type
}

const getDifficultyLabel = (difficulty: number) => {
  const labels = {
    1: '简单 ★☆☆☆☆',
    2: '较易 ★★☆☆☆',
    3: '中等 ★★★☆☆',
    4: '较难 ★★★★☆',
    5: '困难 ★★★★★'
  }
  return labels[difficulty as keyof typeof labels] || `${difficulty}星`
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

// 初始化验证
validateStep()
</script>

<style lang="scss" scoped>
.wizard-step-review {
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

  .review-sections {
    .review-section {
      margin-bottom: 24px;
      background: var(--bg-secondary, #f8f9fa);
      border-radius: 8px;
      border: 1px solid var(--border-color, #e4e7ed);
      overflow: hidden;

      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 24px;
        background: white;
        border-bottom: 1px solid var(--border-color, #e4e7ed);

        .section-title {
          font-size: 16px;
          font-weight: 600;
          color: var(--text-primary, #303133);
          margin: 0;
          display: flex;
          align-items: center;
          gap: 8px;
        }
      }

      .section-content {
        padding: 24px;

        .info-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
          gap: 16px;

          .info-item {
            display: flex;
            flex-direction: column;
            gap: 4px;

            &.full-width {
              grid-column: 1 / -1;
            }

            .info-label {
              font-size: 12px;
              color: var(--text-secondary, #606266);
              font-weight: 500;
            }

            .info-value {
              font-size: 14px;
              color: var(--text-primary, #303133);
            }

            .tags-display {
              display: flex;
              flex-wrap: wrap;
              gap: 8px;

              .tag-item {
                margin: 0;
              }
            }
          }
        }

        .sample-summary {
          .summary-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
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
                width: 40px;
                height: 40px;
                border-radius: 50%;
                background: var(--primary-light-9, #ecf5ff);
                color: var(--primary-color, #409eff);
                display: flex;
                align-items: center;
                justify-content: center;
                margin-right: 12px;
                font-size: 18px;
              }

              .card-content {
                .card-number {
                  font-size: 20px;
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

          .characters-preview {
            padding: 16px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

            .preview-header {
              font-size: 14px;
              font-weight: 500;
              color: var(--text-primary, #303133);
              margin-bottom: 12px;
            }

            .characters-list {
              display: flex;
              flex-wrap: wrap;
              gap: 8px;
              align-items: center;

              .character-tag {
                font-size: 16px;
                padding: 4px 8px;
              }

              .more-indicator {
                font-size: 12px;
                color: var(--text-secondary, #606266);
                font-style: italic;
              }
            }
          }
        }

        .config-summary {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
          gap: 24px;

          .config-group {
            padding: 16px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

            h4 {
              font-size: 14px;
              font-weight: 600;
              color: var(--text-primary, #303133);
              margin-bottom: 12px;
              padding-bottom: 8px;
              border-bottom: 1px solid var(--border-light, #f0f0f0);
            }

            .config-items {
              .config-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 6px 0;
                border-bottom: 1px solid var(--border-light, #f0f0f0);

                &:last-child {
                  border-bottom: none;
                }

                .config-label {
                  font-size: 13px;
                  color: var(--text-secondary, #606266);
                }

                .config-value {
                  font-size: 13px;
                  color: var(--text-primary, #303133);
                  font-weight: 500;
                }
              }
            }
          }
        }
      }
    }
  }

  .estimation-section {
    margin-bottom: 32px;
    padding: 24px;
    background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
    border-radius: 8px;
    border: 1px solid var(--primary-light-7, #a0cfff);

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--primary-color, #409eff);
      margin-bottom: 16px;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .estimation-cards {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 16px;

      .estimation-card {
        display: flex;
        align-items: center;
        padding: 20px;
        background: white;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

        .card-icon {
          width: 48px;
          height: 48px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;
          font-size: 20px;

          &.time {
            background: var(--info-light-9, #f4f4f5);
            color: var(--info-color, #909399);
          }

          &.cost {
            background: var(--warning-light-9, #fdf6ec);
            color: var(--warning-color, #e6a23c);
          }

          &.quality {
            background: var(--success-light-9, #f0f9ff);
            color: var(--success-color, #67c23a);
          }
        }

        .card-content {
          .card-title {
            font-size: 14px;
            color: var(--text-secondary, #606266);
            margin-bottom: 4px;
          }

          .card-value {
            font-size: 18px;
            font-weight: 600;
            color: var(--text-primary, #303133);
          }
        }
      }
    }
  }

  .confirmation-section {
    padding: 24px;
    background: var(--bg-secondary, #f8f9fa);
    border-radius: 8px;
    border: 1px solid var(--border-color, #e4e7ed);

    .el-checkbox {
      margin-bottom: 16px;
      font-size: 16px;
      font-weight: 500;
    }

    .confirmation-notes {
      .notes-list {
        margin: 8px 0 0 16px;
        
        li {
          margin-bottom: 4px;
          font-size: 13px;
          line-height: 1.5;
        }
      }
    }
  }
}
</style>