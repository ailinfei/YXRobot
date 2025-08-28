<template>
  <div class="language-configuration">
    <!-- 语言列表 -->
    <div class="languages-section">
      <div class="section-header">
        <h3>支持的语言</h3>
        <el-button type="primary" size="small" @click="handleAddLanguage">
          <el-icon><Plus /></el-icon>
          添加语言
        </el-button>
      </div>
      
      <div class="languages-list">
        <div
          v-for="language in languages"
          :key="language.code"
          class="language-item"
          :class="{ disabled: !language.enabled }"
        >
          <div class="language-info">
            <div class="language-flag">
              <span class="flag-icon">{{ getFlagEmoji(language.code) }}</span>
            </div>
            <div class="language-details">
              <div class="language-name">{{ language.name }}</div>
              <div class="language-native">{{ language.nativeName }}</div>
              <div class="language-code">{{ language.code }}</div>
            </div>
          </div>
          
          <div class="language-status">
            <el-switch
              v-model="language.enabled"
              @change="handleLanguageToggle(language)"
            />
          </div>
          
          <div class="language-actions">
            <el-button size="small" text @click="handleEditLanguage(language)">
              编辑
            </el-button>
            <el-button size="small" text type="danger" @click="handleDeleteLanguage(language.code)">
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 语言数据库配置 -->
    <div class="database-section">
      <div class="section-header">
        <h3>数据库配置</h3>
        <el-button size="small" @click="handleSyncDatabase">
          <el-icon><Refresh /></el-icon>
          同步数据库
        </el-button>
      </div>
      
      <div class="database-config">
        <el-form label-width="150px">
          <el-form-item label="默认语言">
            <el-select v-model="databaseConfig.defaultLanguage">
              <el-option
                v-for="lang in enabledLanguages"
                :key="lang.code"
                :label="lang.name"
                :value="lang.code"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="回退语言">
            <el-select v-model="databaseConfig.fallbackLanguage">
              <el-option
                v-for="lang in enabledLanguages"
                :key="lang.code"
                :label="lang.name"
                :value="lang.code"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="自动检测语言">
            <el-switch v-model="databaseConfig.autoDetect" />
          </el-form-item>
          
          <el-form-item label="缓存翻译">
            <el-switch v-model="databaseConfig.cacheTranslations" />
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 翻译服务配置 -->
    <div class="translation-services-section">
      <div class="section-header">
        <h3>翻译服务配置</h3>
      </div>
      
      <div class="services-config">
        <el-tabs v-model="activeServiceTab">
          <el-tab-pane
            v-for="service in translationServices"
            :key="service.id"
            :label="service.name"
            :name="service.id"
          >
            <div class="service-config">
              <el-form :model="service.config" label-width="120px">
                <el-form-item label="启用服务">
                  <el-switch v-model="service.enabled" />
                </el-form-item>
                
                <el-form-item label="API密钥" v-if="service.enabled">
                  <el-input
                    v-model="service.config.apiKey"
                    type="password"
                    placeholder="请输入API密钥"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item label="API地址" v-if="service.enabled && service.config.apiUrl">
                  <el-input
                    v-model="service.config.apiUrl"
                    placeholder="API服务地址"
                  />
                </el-form-item>
                
                <el-form-item label="请求频率限制" v-if="service.enabled">
                  <el-input-number
                    v-model="service.config.rateLimit"
                    :min="1"
                    :max="1000"
                    placeholder="每分钟请求次数"
                  />
                </el-form-item>
                
                <el-form-item v-if="service.enabled">
                  <el-button @click="handleTestService(service)" :loading="testingService === service.id">
                    测试连接
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 保存按钮 -->
    <div class="save-section">
      <el-button type="primary" @click="handleSaveConfig" :loading="saveLoading">
        保存配置
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'

// 接口定义
interface Language {
  code: string
  name: string
  nativeName: string
  direction: 'ltr' | 'rtl'
  enabled: boolean
  description?: string
}

interface DatabaseConfig {
  defaultLanguage: string
  fallbackLanguage: string
  autoDetect: boolean
  cacheTranslations: boolean
}

interface TranslationService {
  id: string
  name: string
  enabled: boolean
  config: {
    apiKey: string
    apiUrl?: string
    rateLimit: number
  }
}

// Props
const emit = defineEmits<{
  update: []
}>()

// 响应式数据
const saveLoading = ref(false)
const testingService = ref('')
const activeServiceTab = ref('google')

// 语言配置
const languages = ref<Language[]>([
  {
    code: 'zh',
    name: '中文',
    nativeName: '中文',
    direction: 'ltr',
    enabled: true,
    description: '简体中文'
  },
  {
    code: 'en',
    name: 'English',
    nativeName: 'English',
    direction: 'ltr',
    enabled: true,
    description: '英语'
  },
  {
    code: 'ja',
    name: '日本語',
    nativeName: '日本語',
    direction: 'ltr',
    enabled: true,
    description: '日语'
  },
  {
    code: 'ko',
    name: '한국어',
    nativeName: '한국어',
    direction: 'ltr',
    enabled: true,
    description: '韩语'
  },
  {
    code: 'es',
    name: 'Español',
    nativeName: 'Español',
    direction: 'ltr',
    enabled: false,
    description: '西班牙语'
  },
  {
    code: 'fr',
    name: 'Français',
    nativeName: 'Français',
    direction: 'ltr',
    enabled: false,
    description: '法语'
  }
])

// 数据库配置
const databaseConfig = reactive<DatabaseConfig>({
  defaultLanguage: 'zh',
  fallbackLanguage: 'en',
  autoDetect: true,
  cacheTranslations: true
})

// 翻译服务配置
const translationServices = ref<TranslationService[]>([
  {
    id: 'google',
    name: 'Google 翻译',
    enabled: true,
    config: {
      apiKey: '',
      rateLimit: 100
    }
  },
  {
    id: 'baidu',
    name: '百度翻译',
    enabled: false,
    config: {
      apiKey: '',
      apiUrl: 'https://fanyi-api.baidu.com/api/trans/vip/translate',
      rateLimit: 50
    }
  },
  {
    id: 'tencent',
    name: '腾讯翻译',
    enabled: false,
    config: {
      apiKey: '',
      rateLimit: 50
    }
  },
  {
    id: 'deepl',
    name: 'DeepL',
    enabled: false,
    config: {
      apiKey: '',
      apiUrl: 'https://api-free.deepl.com/v2/translate',
      rateLimit: 30
    }
  }
])

// 计算属性
const enabledLanguages = computed(() => {
  return languages.value.filter(lang => lang.enabled)
})

// 方法
const handleAddLanguage = () => {
  ElMessage.info('添加语言功能开发中...')
}

const handleEditLanguage = (language: Language) => {
  ElMessage.info(`编辑语言: ${language.name}`)
}

const handleDeleteLanguage = async (languageCode: string) => {
  try {
    await ElMessageBox.confirm('确定要删除这个语言吗？', '确认删除', {
      type: 'warning'
    })
    
    const index = languages.value.findIndex(lang => lang.code === languageCode)
    if (index > -1) {
      languages.value.splice(index, 1)
      ElMessage.success('语言删除成功')
      emit('update')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除语言失败')
    }
  }
}

const handleLanguageToggle = (language: Language) => {
  ElMessage.success(`${language.name} ${language.enabled ? '已启用' : '已禁用'}`)
  emit('update')
}

const handleSyncDatabase = async () => {
  try {
    await ElMessageBox.confirm('确定要同步数据库结构吗？这将更新语言表结构。', '确认同步')
    
    // 模拟同步操作
    ElMessage.success('数据库同步成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('数据库同步失败')
    }
  }
}

const handleTestService = async (service: TranslationService) => {
  if (!service.config.apiKey) {
    ElMessage.warning('请先配置API密钥')
    return
  }
  
  testingService.value = service.id
  try {
    // 模拟测试API连接
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success(`${service.name} 连接测试成功`)
  } catch (error) {
    ElMessage.error(`${service.name} 连接测试失败`)
  } finally {
    testingService.value = ''
  }
}

const handleSaveConfig = async () => {
  saveLoading.value = true
  try {
    // 模拟保存配置
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('配置保存成功')
    emit('update')
  } catch (error) {
    ElMessage.error('保存配置失败')
  } finally {
    saveLoading.value = false
  }
}

// 工具方法
const getFlagEmoji = (languageCode: string) => {
  const flagMap: Record<string, string> = {
    zh: '🇨🇳',
    en: '🇺🇸',
    ja: '🇯🇵',
    ko: '🇰🇷',
    es: '🇪🇸',
    fr: '🇫🇷',
    de: '🇩🇪',
    ru: '🇷🇺'
  }
  return flagMap[languageCode] || '🌐'
}
</script>

<style lang="scss" scoped>
.language-configuration {
  display: flex;
  flex-direction: column;
  gap: 24px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h3 {
      margin: 0;
      color: #303133;
      font-size: 16px;
      font-weight: 600;
    }
  }

  .languages-section {
    .languages-list {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .language-item {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 16px;
        background: #f8f9fa;
        border-radius: 8px;
        border: 1px solid #e4e7ed;
        transition: all 0.3s ease;

        &:hover {
          background: #f0f0f0;
        }

        &.disabled {
          opacity: 0.6;
        }

        .language-info {
          display: flex;
          align-items: center;
          gap: 12px;
          flex: 1;

          .language-flag {
            .flag-icon {
              font-size: 24px;
            }
          }

          .language-details {
            .language-name {
              font-size: 16px;
              font-weight: 600;
              color: #303133;
              margin-bottom: 2px;
            }

            .language-native {
              font-size: 14px;
              color: #606266;
              margin-bottom: 2px;
            }

            .language-code {
              font-size: 12px;
              color: #909399;
              font-family: monospace;
              background: #e4e7ed;
              padding: 2px 6px;
              border-radius: 3px;
            }
          }
        }

        .language-status {
          margin-right: 16px;
        }

        .language-actions {
          display: flex;
          gap: 8px;
        }
      }
    }
  }

  .database-section,
  .translation-services-section {
    padding: 20px;
    background: white;
    border-radius: 8px;
    border: 1px solid #e4e7ed;

    .database-config,
    .services-config {
      margin-top: 16px;
    }

    .service-config {
      padding: 16px 0;
    }
  }

  .save-section {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
    padding: 16px 0;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .language-configuration {
    .languages-section {
      .languages-list {
        .language-item {
          flex-direction: column;
          align-items: stretch;
          gap: 12px;

          .language-info {
            justify-content: flex-start;
          }

          .language-actions {
            justify-content: center;
          }
        }
      }
    }

    .save-section {
      flex-direction: column;

      .el-button {
        width: 100%;
      }
    }
  }
}
</style>