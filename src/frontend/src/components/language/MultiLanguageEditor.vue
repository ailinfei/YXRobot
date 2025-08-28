<template>
  <div class="multi-language-editor">
    <el-tabs v-model="activeLanguage" type="border-card" @tab-change="handleTabChange">
      <el-tab-pane
        v-for="language in languages"
        :key="language.code"
        :label="language.name"
        :name="language.code"
      >
        <div class="language-editor">
          <div class="editor-header">
            <div class="language-info">
              <span class="language-name">{{ language.name }}</span>
              <el-tag 
                :type="getTranslationStatus(language.code)" 
                size="small"
              >
                {{ getTranslationStatusText(language.code) }}
              </el-tag>
            </div>
            <div class="editor-actions">
              <el-button 
                size="small" 
                @click="clearTranslation(language.code)"
                :disabled="!modelValue[language.code]"
              >
                清空
              </el-button>
              <el-button 
                size="small" 
                type="primary" 
                @click="autoTranslate(language.code)"
                :loading="translating[language.code]"
              >
                自动翻译
              </el-button>
            </div>
          </div>
          
          <div class="editor-content">
            <el-input
              v-if="editorType === 'input'"
              v-model="modelValue[language.code]"
              :placeholder="`请输入${language.name}内容`"
              :maxlength="maxLength"
              show-word-limit
              @input="handleInput(language.code, $event)"
            />
            
            <el-input
              v-else-if="editorType === 'textarea'"
              v-model="modelValue[language.code]"
              type="textarea"
              :rows="rows"
              :placeholder="`请输入${language.name}内容`"
              :maxlength="maxLength"
              show-word-limit
              @input="handleInput(language.code, $event)"
            />
            
            <div v-else-if="editorType === 'rich'" class="rich-editor">
              <!-- 富文本编辑器占位 -->
              <el-input
                v-model="modelValue[language.code]"
                type="textarea"
                :rows="rows"
                :placeholder="`请输入${language.name}内容（富文本模式）`"
                @input="handleInput(language.code, $event)"
              />
            </div>
          </div>
          
          <div class="editor-footer">
            <div class="character-count">
              字符数: {{ getCharacterCount(language.code) }}
              <span v-if="maxLength"> / {{ maxLength }}</span>
            </div>
            <div class="last-updated" v-if="lastUpdated[language.code]">
              最后更新: {{ formatTime(lastUpdated[language.code]) }}
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 翻译进度 -->
    <div class="translation-progress" v-if="showProgress">
      <div class="progress-header">
        <span>翻译进度</span>
        <span class="progress-text">{{ completedCount }} / {{ languages.length }}</span>
      </div>
      <el-progress 
        :percentage="progressPercentage" 
        :color="progressColor"
        :show-text="false"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 接口定义
interface Language {
  code: string
  name: string
  flag?: string
}

interface Props {
  modelValue: Record<string, string>
  languages: Language[]
  editorType?: 'input' | 'textarea' | 'rich'
  rows?: number
  maxLength?: number
  showProgress?: boolean
  autoSave?: boolean
  autoSaveDelay?: number
}

interface Emits {
  (e: 'update:modelValue', value: Record<string, string>): void
  (e: 'change', language: string, value: string): void
  (e: 'translate', language: string): void
}

// Props 和 Emits
const props = withDefaults(defineProps<Props>(), {
  editorType: 'textarea',
  rows: 4,
  maxLength: 1000,
  showProgress: true,
  autoSave: false,
  autoSaveDelay: 2000
})

const emit = defineEmits<Emits>()

// 响应式数据
const activeLanguage = ref(props.languages[0]?.code || '')
const translating = reactive<Record<string, boolean>>({})
const lastUpdated = reactive<Record<string, Date>>({})

// 计算属性
const completedCount = computed(() => {
  return props.languages.filter(lang => 
    props.modelValue[lang.code] && props.modelValue[lang.code].trim().length > 0
  ).length
})

const progressPercentage = computed(() => {
  if (props.languages.length === 0) return 0
  return Math.round((completedCount.value / props.languages.length) * 100)
})

const progressColor = computed(() => {
  const percentage = progressPercentage.value
  if (percentage < 30) return '#f56c6c'
  if (percentage < 70) return '#e6a23c'
  return '#67c23a'
})

// 方法
const handleTabChange = (tabName: string) => {
  activeLanguage.value = tabName
}

const handleInput = (language: string, value: string) => {
  const newValue = { ...props.modelValue }
  newValue[language] = value
  lastUpdated[language] = new Date()
  
  emit('update:modelValue', newValue)
  emit('change', language, value)
}

const getTranslationStatus = (languageCode: string) => {
  const content = props.modelValue[languageCode]
  if (!content || content.trim().length === 0) return 'danger'
  if (content.trim().length < 10) return 'warning'
  return 'success'
}

const getTranslationStatusText = (languageCode: string) => {
  const content = props.modelValue[languageCode]
  if (!content || content.trim().length === 0) return '未翻译'
  if (content.trim().length < 10) return '不完整'
  return '已完成'
}

const getCharacterCount = (languageCode: string) => {
  return props.modelValue[languageCode]?.length || 0
}

const clearTranslation = (languageCode: string) => {
  const newValue = { ...props.modelValue }
  newValue[languageCode] = ''
  emit('update:modelValue', newValue)
  ElMessage.success(`已清空${props.languages.find(l => l.code === languageCode)?.name}翻译`)
}

const autoTranslate = async (languageCode: string) => {
  translating[languageCode] = true
  
  try {
    // 模拟自动翻译API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 这里应该调用实际的翻译API
    const mockTranslation = `自动翻译的${props.languages.find(l => l.code === languageCode)?.name}内容`
    
    const newValue = { ...props.modelValue }
    newValue[languageCode] = mockTranslation
    lastUpdated[languageCode] = new Date()
    
    emit('update:modelValue', newValue)
    emit('translate', languageCode)
    
    ElMessage.success(`${props.languages.find(l => l.code === languageCode)?.name}自动翻译完成`)
  } catch (error) {
    ElMessage.error('自动翻译失败')
  } finally {
    translating[languageCode] = false
  }
}

const formatTime = (date: Date) => {
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 监听器
watch(() => props.languages, (newLanguages) => {
  if (newLanguages.length > 0 && !activeLanguage.value) {
    activeLanguage.value = newLanguages[0].code
  }
}, { immediate: true })
</script>

<style lang="scss" scoped>
.multi-language-editor {
  .language-editor {
    .editor-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid #ebeef5;

      .language-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .language-name {
          font-weight: 600;
          color: #303133;
        }
      }

      .editor-actions {
        display: flex;
        gap: 8px;
      }
    }

    .editor-content {
      margin-bottom: 16px;

      .rich-editor {
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        min-height: 120px;
        padding: 12px;
      }
    }

    .editor-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 12px;
      color: #909399;

      .character-count {
        font-weight: 500;
      }

      .last-updated {
        font-style: italic;
      }
    }
  }

  .translation-progress {
    margin-top: 20px;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 8px;

    .progress-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      font-size: 14px;
      font-weight: 500;
      color: #606266;

      .progress-text {
        color: #409eff;
      }
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .multi-language-editor {
    .translation-progress {
      background: #2d2d2d;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .multi-language-editor {
    .language-editor {
      .editor-header {
        flex-direction: column;
        gap: 12px;
        align-items: stretch;

        .editor-actions {
          justify-content: center;
        }
      }

      .editor-footer {
        flex-direction: column;
        gap: 8px;
        align-items: flex-start;
      }
    }
  }
}
</style>