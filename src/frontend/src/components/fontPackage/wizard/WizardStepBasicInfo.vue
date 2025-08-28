<!--
  向导步骤1: 基本信息设置
-->
<template>
  <div class="wizard-step-basic-info">
    <div class="step-header">
      <h2 class="step-title">设置字体包基本信息</h2>
      <p class="step-description">
        请填写字体包的基本信息，这些信息将帮助用户更好地了解和使用您的字体包。
      </p>
    </div>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      class="basic-info-form"
      @validate="handleFormValidate"
    >
      <div class="form-section">
        <h3 class="section-title">基本信息</h3>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="字体包名称" prop="name">
              <el-input
                v-model="formData.name"
                placeholder="请输入字体包名称"
                maxlength="50"
                show-word-limit
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="版本号" prop="version">
              <el-input
                v-model="formData.version"
                placeholder="如：v1.0.0"
                maxlength="20"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="字体类型" prop="fontType">
              <el-select 
                v-model="formData.fontType" 
                placeholder="选择字体类型" 
                style="width: 100%"
                clearable
              >
                <el-option label="楷书" value="kaishu" />
                <el-option label="行书" value="xingshu" />
                <el-option label="隶书" value="lishu" />
                <el-option label="篆书" value="zhuanshu" />
                <el-option label="草书" value="caoshu" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度等级" prop="difficulty">
              <el-select 
                v-model="formData.difficulty" 
                placeholder="选择难度等级" 
                style="width: 100%"
              >
                <el-option label="简单 ★☆☆☆☆" :value="1" />
                <el-option label="较易 ★★☆☆☆" :value="2" />
                <el-option label="中等 ★★★☆☆" :value="3" />
                <el-option label="较难 ★★★★☆" :value="4" />
                <el-option label="困难 ★★★★★" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="字体包描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述字体包的特点、适用场景等..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </div>

      <div class="form-section">
        <h3 class="section-title">标签设置</h3>
        
        <el-form-item label="字体包标签">
          <div class="tags-editor">
            <div class="current-tags" v-if="formData.tags.length > 0">
              <el-tag
                v-for="tag in formData.tags"
                :key="tag"
                closable
                @close="removeTag(tag)"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </div>
            
            <div class="add-tag">
              <el-input
                v-model="newTag"
                placeholder="输入标签名称"
                size="small"
                style="width: 150px; margin-right: 8px;"
                @keyup.enter="addTag"
                maxlength="20"
              />
              <el-button 
                size="small" 
                @click="addTag" 
                :disabled="!newTag.trim()"
              >
                添加标签
              </el-button>
            </div>
            
            <div class="preset-tags">
              <span class="preset-label">推荐标签：</span>
              <el-tag
                v-for="preset in presetTags"
                :key="preset"
                size="small"
                type="info"
                @click="addPresetTag(preset)"
                class="preset-tag"
                :class="{ 'tag-added': formData.tags.includes(preset) }"
              >
                {{ preset }}
              </el-tag>
            </div>
          </div>
        </el-form-item>
      </div>
    </el-form>

    <!-- 智能推荐区域 -->
    <div class="smart-recommendations" v-if="recommendations.length > 0">
      <h3 class="section-title">
        <el-icon><Star /></el-icon>
        智能推荐
      </h3>
      <div class="recommendations-list">
        <div 
          v-for="rec in recommendations" 
          :key="rec.type"
          class="recommendation-item"
        >
          <div class="rec-icon">
            <el-icon><InfoFilled /></el-icon>
          </div>
          <div class="rec-content">
            <div class="rec-title">{{ rec.title }}</div>
            <div class="rec-description">{{ rec.description }}</div>
          </div>
          <div class="rec-action" v-if="rec.action">
            <el-button 
              size="small" 
              type="primary" 
              text
              @click="applyRecommendation(rec)"
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
import { ref, computed, watch, nextTick } from 'vue'
import { Star, InfoFilled } from '@element-plus/icons-vue'

interface BasicInfoData {
  name: string
  description: string
  fontType: string
  difficulty: number
  version: string
  tags: string[]
}

interface StepValidation {
  isValid: boolean
  errors: string[]
  warnings: string[]
}

interface Recommendation {
  type: string
  title: string
  description: string
  action?: () => void
}

interface Props {
  modelValue: {
    basicInfo: BasicInfoData
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
const newTag = ref('')

// 预设标签
const presetTags = [
  'AI生成', '高质量', '专业', '教学', '练习',
  '标准', '美观', '实用', '推荐', '热门'
]

// 表单数据
const formData = computed({
  get: () => props.modelValue.basicInfo,
  set: (value) => {
    emit('update:modelValue', {
      ...props.modelValue,
      basicInfo: value
    })
  }
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入字体包名称', trigger: 'blur' },
    { min: 2, max: 50, message: '字体包名称长度在2-50个字符', trigger: 'blur' }
  ],
  fontType: [
    { required: true, message: '请选择字体类型', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入字体包描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度在10-500个字符', trigger: 'blur' }
  ],
  version: [
    { required: true, message: '请输入版本号', trigger: 'blur' },
    { pattern: /^v?\d+\.\d+\.\d+$/, message: '版本号格式不正确，如：v1.0.0', trigger: 'blur' }
  ]
}

// 智能推荐
const recommendations = computed<Recommendation[]>(() => {
  const recs: Recommendation[] = []
  
  // 基于字体类型的推荐
  if (formData.value.fontType) {
    const typeRecommendations = {
      kaishu: {
        tags: ['标准', '教学', '练习'],
        description: '楷书适合初学者练习，建议添加"标准"、"教学"标签'
      },
      xingshu: {
        tags: ['美观', '实用'],
        description: '行书流畅美观，建议添加"美观"、"实用"标签'
      },
      lishu: {
        tags: ['专业', '古典'],
        description: '隶书具有古典美感，建议添加"专业"、"古典"标签'
      }
    }
    
    const typeRec = typeRecommendations[formData.value.fontType as keyof typeof typeRecommendations]
    if (typeRec) {
      recs.push({
        type: 'tags',
        title: '推荐标签',
        description: typeRec.description,
        action: () => {
          typeRec.tags.forEach(tag => {
            if (!formData.value.tags.includes(tag)) {
              formData.value.tags.push(tag)
            }
          })
        }
      })
    }
  }
  
  // 基于难度的推荐
  if (formData.value.difficulty <= 2) {
    recs.push({
      type: 'difficulty',
      title: '初学者友好',
      description: '您选择的难度适合初学者，建议在描述中强调易学特点'
    })
  }
  
  return recs
})

// 监听器
watch(formData, () => {
  validateForm()
}, { deep: true })

// 方法
const validateForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    emit('validate', {
      isValid: true,
      errors: [],
      warnings: []
    })
  } catch (error) {
    const errors: string[] = []
    const warnings: string[] = []
    
    // 收集验证错误
    if (error && typeof error === 'object') {
      Object.values(error as Record<string, any>).forEach((fieldErrors: any) => {
        if (Array.isArray(fieldErrors)) {
          fieldErrors.forEach(err => {
            if (err.message) {
              errors.push(err.message)
            }
          })
        }
      })
    }
    
    // 添加警告
    if (formData.value.tags.length === 0) {
      warnings.push('建议添加一些标签以便用户更好地找到您的字体包')
    }
    
    emit('validate', {
      isValid: false,
      errors,
      warnings
    })
  }
}

const handleFormValidate = () => {
  nextTick(() => {
    validateForm()
  })
}

// 标签管理
const addTag = () => {
  const tag = newTag.value.trim()
  if (tag && !formData.value.tags.includes(tag)) {
    formData.value.tags.push(tag)
    newTag.value = ''
  }
}

const addPresetTag = (tag: string) => {
  if (!formData.value.tags.includes(tag)) {
    formData.value.tags.push(tag)
  }
}

const removeTag = (tag: string) => {
  const index = formData.value.tags.indexOf(tag)
  if (index > -1) {
    formData.value.tags.splice(index, 1)
  }
}

const applyRecommendation = (rec: Recommendation) => {
  if (rec.action) {
    rec.action()
  }
}

// 初始化验证
nextTick(() => {
  validateForm()
})
</script>

<style lang="scss" scoped>
.wizard-step-basic-info {
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

  .basic-info-form {
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

    .tags-editor {
      .current-tags {
        margin-bottom: 16px;

        .tag-item {
          margin-right: 8px;
          margin-bottom: 8px;
        }
      }

      .add-tag {
        display: flex;
        align-items: center;
        margin-bottom: 16px;
      }

      .preset-tags {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        gap: 8px;

        .preset-label {
          font-size: 14px;
          color: var(--text-secondary, #606266);
          margin-right: 8px;
        }

        .preset-tag {
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            background: var(--primary-color, #409eff);
            color: white;
          }

          &.tag-added {
            background: var(--success-color, #67c23a);
            color: white;
            cursor: default;
          }
        }
      }
    }
  }

  .smart-recommendations {
    margin-top: 32px;
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

    .recommendations-list {
      .recommendation-item {
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

        .rec-icon {
          width: 24px;
          height: 24px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: var(--primary-color, #409eff);
          margin-right: 12px;
          margin-top: 2px;
        }

        .rec-content {
          flex: 1;

          .rec-title {
            font-size: 14px;
            font-weight: 600;
            color: var(--text-primary, #303133);
            margin-bottom: 4px;
          }

          .rec-description {
            font-size: 13px;
            color: var(--text-secondary, #606266);
            line-height: 1.5;
          }
        }

        .rec-action {
          margin-left: 12px;
        }
      }
    }
  }
}
</style>