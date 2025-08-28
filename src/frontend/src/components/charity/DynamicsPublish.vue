<template>
  <div class="dynamics-publish">
    <!-- 发布表单 -->
    <div class="publish-form">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>发布公益动态</span>
            <el-button type="primary" @click="handlePublish" :loading="publishing">
              <el-icon><Promotion /></el-icon>
              发布动态
            </el-button>
          </div>
        </template>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="动态标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="请输入动态标题"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="动态类型" prop="type">
            <el-select v-model="form.type" placeholder="请选择动态类型">
              <el-option label="活动报道" value="activity" />
              <el-option label="项目进展" value="progress" />
              <el-option label="感谢信" value="thanks" />
              <el-option label="成果展示" value="achievement" />
              <el-option label="志愿者故事" value="volunteer" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>

          <el-form-item label="关联项目" prop="projectId">
            <el-select
              v-model="form.projectId"
              placeholder="请选择关联项目（可选）"
              clearable
              filterable
            >
              <el-option
                v-for="project in projects"
                :key="project.id"
                :label="project.name"
                :value="project.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="动态内容" prop="content">
            <div class="editor-container">
              <div class="editor-toolbar">
                <el-button-group>
                  <el-button size="small" @click="insertText('**粗体**')">
                    <el-icon><Bold /></el-icon>
                  </el-button>
                  <el-button size="small" @click="insertText('*斜体*')">
                    <el-icon><Italic /></el-icon>
                  </el-button>
                  <el-button size="small" @click="insertText('~~删除线~~')">
                    <el-icon><Strikethrough /></el-icon>
                  </el-button>
                </el-button-group>
                
                <el-button-group>
                  <el-button size="small" @click="insertText('# 标题1')">H1</el-button>
                  <el-button size="small" @click="insertText('## 标题2')">H2</el-button>
                  <el-button size="small" @click="insertText('### 标题3')">H3</el-button>
                </el-button-group>
                
                <el-button-group>
                  <el-button size="small" @click="insertText('- 列表项')">
                    <el-icon><List /></el-icon>
                  </el-button>
                  <el-button size="small" @click="insertText('1. 有序列表')">
                    <el-icon><Sort /></el-icon>
                  </el-button>
                  <el-button size="small" @click="insertText('[链接文字](链接地址)')">
                    <el-icon><Link /></el-icon>
                  </el-button>
                </el-button-group>
                
                <el-button size="small" @click="showImageSelector = true">
                  <el-icon><Picture /></el-icon>
                  插入图片
                </el-button>
              </div>
              
              <el-input
                ref="editorRef"
                v-model="form.content"
                type="textarea"
                :rows="12"
                placeholder="请输入动态内容，支持Markdown格式"
                class="content-editor"
              />
            </div>
          </el-form-item>

          <el-form-item label="内容预览">
            <div class="content-preview" v-html="renderedContent"></div>
          </el-form-item>

          <el-form-item label="标签">
            <el-tag
              v-for="tag in form.tags"
              :key="tag"
              closable
              @close="removeTag(tag)"
              style="margin-right: 8px; margin-bottom: 8px;"
            >
              {{ tag }}
            </el-tag>
            
            <el-input
              v-if="inputVisible"
              ref="inputRef"
              v-model="inputValue"
              size="small"
              style="width: 100px;"
              @keyup.enter="handleInputConfirm"
              @blur="handleInputConfirm"
            />
            
            <el-button
              v-else
              size="small"
              @click="showInput"
            >
              + 添加标签
            </el-button>
          </el-form-item>

          <el-form-item label="发布设置">
            <el-checkbox v-model="form.syncToWebsite">同步到官网</el-checkbox>
            <el-checkbox v-model="form.allowComments">允许评论</el-checkbox>
            <el-checkbox v-model="form.featured">设为精选</el-checkbox>
          </el-form-item>

          <el-form-item label="发布时间">
            <el-radio-group v-model="form.publishType">
              <el-radio value="now">立即发布</el-radio>
              <el-radio value="scheduled">定时发布</el-radio>
            </el-radio-group>
            
            <el-date-picker
              v-if="form.publishType === 'scheduled'"
              v-model="form.scheduledTime"
              type="datetime"
              placeholder="选择发布时间"
              style="margin-left: 12px;"
            />
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 已发布动态列表 -->
    <div class="published-dynamics">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>已发布动态</span>
            <div class="header-actions">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索动态"
                clearable
                style="width: 200px;"
                @input="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>
          </div>
        </template>

        <div class="dynamics-list" v-loading="loading">
          <div
            v-for="dynamic in filteredDynamics"
            :key="dynamic.id"
            class="dynamic-item"
          >
            <div class="dynamic-header">
              <div class="dynamic-title">{{ dynamic.title }}</div>
              <div class="dynamic-meta">
                <el-tag :type="getTypeTagType(dynamic.type)" size="small">
                  {{ getTypeText(dynamic.type) }}
                </el-tag>
                <span class="publish-time">{{ formatDate(dynamic.publishTime) }}</span>
              </div>
            </div>
            
            <div class="dynamic-content">
              {{ dynamic.summary }}
            </div>
            
            <div class="dynamic-stats">
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span>{{ dynamic.views }}</span>
              </div>
              <div class="stat-item">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ dynamic.comments }}</span>
              </div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span>{{ dynamic.likes }}</span>
              </div>
            </div>
            
            <div class="dynamic-actions">
              <el-button size="small" @click="handleView(dynamic)">查看</el-button>
              <el-button size="small" @click="handleEdit(dynamic)">编辑</el-button>
              <el-button size="small" type="warning" @click="handleToggleStatus(dynamic)">
                {{ dynamic.status === 'published' ? '下线' : '上线' }}
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(dynamic)">删除</el-button>
            </div>
          </div>

          <el-empty v-if="!loading && filteredDynamics.length === 0" description="暂无动态" />
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper" v-if="filteredDynamics.length > 0">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 图片选择器对话框 -->
    <el-dialog
      v-model="showImageSelector"
      title="选择图片"
      width="80%"
      :before-close="handleImageSelectorClose"
    >
      <ImageSelector
        ref="imageSelectorRef"
        @select="handleImageSelect"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Promotion,
  Bold,
  Italic,
  Strikethrough,
  List,
  Sort,
  Link,
  Picture,
  Search,
  View,
  ChatDotRound,
  Star
} from '@element-plus/icons-vue'
import { marked } from 'marked'
import ImageSelector from './ImageSelector.vue'

// 动态接口
interface Dynamic {
  id: string
  title: string
  type: string
  content: string
  summary: string
  tags: string[]
  publishTime: string
  status: 'published' | 'draft' | 'offline'
  views: number
  comments: number
  likes: number
  syncToWebsite: boolean
  projectId?: string
}

// 项目接口
interface Project {
  id: string
  name: string
}

// 响应式数据
const formRef = ref()
const editorRef = ref()
const inputRef = ref()
const imageSelectorRef = ref()
const publishing = ref(false)
const loading = ref(false)
const showImageSelector = ref(false)
const inputVisible = ref(false)
const inputValue = ref('')
const searchKeyword = ref('')

// 表单数据
const form = reactive({
  title: '',
  type: '',
  projectId: '',
  content: '',
  tags: [] as string[],
  syncToWebsite: true,
  allowComments: true,
  featured: false,
  publishType: 'now',
  scheduledTime: null as Date | null
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入动态标题', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择动态类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入动态内容', trigger: 'blur' },
    { min: 10, message: '内容至少需要10个字符', trigger: 'blur' }
  ]
}

// 数据
const projects = ref<Project[]>([])
const dynamics = ref<Dynamic[]>([])

// 分页数据
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 计算属性
const renderedContent = computed(() => {
  if (!form.content) return ''
  try {
    return marked(form.content)
  } catch (error) {
    return form.content
  }
})

const filteredDynamics = computed(() => {
  if (!searchKeyword.value) return dynamics.value
  
  return dynamics.value.filter(dynamic =>
    dynamic.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
    dynamic.content.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

// 方法
const getTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    activity: '活动报道',
    progress: '项目进展',
    thanks: '感谢信',
    achievement: '成果展示',
    volunteer: '志愿者故事',
    other: '其他'
  }
  return typeMap[type] || type
}

const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    activity: 'primary',
    progress: 'success',
    thanks: 'warning',
    achievement: 'info',
    volunteer: 'danger',
    other: ''
  }
  return typeMap[type] || ''
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const insertText = (text: string) => {
  const textarea = editorRef.value?.textarea
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const value = form.content

  form.content = value.substring(0, start) + text + value.substring(end)
  
  nextTick(() => {
    textarea.focus()
    textarea.setSelectionRange(start + text.length, start + text.length)
  })
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    inputRef.value?.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value && !form.tags.includes(inputValue.value)) {
    form.tags.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}

const removeTag = (tag: string) => {
  const index = form.tags.indexOf(tag)
  if (index > -1) {
    form.tags.splice(index, 1)
  }
}

const handleImageSelect = (imageUrl: string) => {
  insertText(`![图片](${imageUrl})`)
  showImageSelector.value = false
}

const handleImageSelectorClose = () => {
  showImageSelector.value = false
}

const handlePublish = async () => {
  try {
    await formRef.value?.validate()
    
    publishing.value = true
    
    // 模拟发布
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('动态发布成功')
    
    // 重置表单
    formRef.value?.resetFields()
    form.tags = []
    
    // 刷新动态列表
    loadDynamics()
  } catch (error) {
    console.error('发布失败:', error)
  } finally {
    publishing.value = false
  }
}

const loadProjects = async () => {
  try {
    // 模拟加载项目数据
    projects.value = [
      { id: '1', name: '山区儿童汉字启蒙计划' },
      { id: '2', name: '留守儿童书法教育项目' },
      { id: '3', name: '特殊教育汉字学习支持' }
    ]
  } catch (error) {
    ElMessage.error('加载项目列表失败')
  }
}

const loadDynamics = async () => {
  loading.value = true
  try {
    // 模拟加载动态数据
    await new Promise(resolve => setTimeout(resolve, 500))
    
    dynamics.value = [
      {
        id: '1',
        title: '山区小学汉字学习成果展示',
        type: 'achievement',
        content: '经过三个月的汉字启蒙教育...',
        summary: '经过三个月的汉字启蒙教育，山区孩子们的汉字识字率显著提升...',
        tags: ['教育', '成果', '山区'],
        publishTime: '2024-12-01 10:30:00',
        status: 'published',
        views: 1250,
        comments: 23,
        likes: 89,
        syncToWebsite: true,
        projectId: '1'
      },
      {
        id: '2',
        title: '志愿者李老师的感人故事',
        type: 'volunteer',
        content: '李老师是我们项目的资深志愿者...',
        summary: '李老师是我们项目的资深志愿者，她用爱心和耐心帮助了无数孩子...',
        tags: ['志愿者', '感人', '故事'],
        publishTime: '2024-11-28 14:20:00',
        status: 'published',
        views: 890,
        comments: 15,
        likes: 67,
        syncToWebsite: true
      }
    ]
    
    pagination.total = dynamics.value.length
  } catch (error) {
    ElMessage.error('加载动态列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
}

const handleView = (dynamic: Dynamic) => {
  ElMessage.info(`查看动态：${dynamic.title}`)
}

const handleEdit = (dynamic: Dynamic) => {
  // 填充表单数据
  form.title = dynamic.title
  form.type = dynamic.type
  form.content = dynamic.content
  form.tags = [...dynamic.tags]
  form.syncToWebsite = dynamic.syncToWebsite
  form.projectId = dynamic.projectId || ''
  
  ElMessage.info('动态数据已加载到编辑器')
}

const handleToggleStatus = async (dynamic: Dynamic) => {
  const action = dynamic.status === 'published' ? '下线' : '上线'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}动态"${dynamic.title}"吗？`,
      `确认${action}`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success(`${action}成功`)
    loadDynamics()
  } catch (error) {
    // 用户取消操作
  }
}

const handleDelete = async (dynamic: Dynamic) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除动态"${dynamic.title}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('删除成功')
    loadDynamics()
  } catch (error) {
    // 用户取消删除
  }
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.page = 1
  loadDynamics()
}

const handleCurrentChange = (page: number) => {
  pagination.page = page
  loadDynamics()
}

// 生命周期
onMounted(() => {
  loadProjects()
  loadDynamics()
})

// 暴露方法给父组件
defineExpose({
  refresh: loadDynamics
})
</script>

<style lang="scss" scoped>
.dynamics-publish {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }

  .publish-form {
    .editor-container {
      .editor-toolbar {
        display: flex;
        gap: 8px;
        margin-bottom: 8px;
        padding: 8px;
        background: #f8f9fa;
        border-radius: 4px;
        flex-wrap: wrap;
      }

      .content-editor {
        :deep(.el-textarea__inner) {
          font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
          line-height: 1.6;
        }
      }
    }

    .content-preview {
      max-height: 300px;
      overflow-y: auto;
      padding: 12px;
      background: #f8f9fa;
      border-radius: 4px;
      border: 1px solid #dcdfe6;

      :deep(h1), :deep(h2), :deep(h3) {
        margin-top: 0;
        margin-bottom: 12px;
      }

      :deep(p) {
        margin-bottom: 8px;
        line-height: 1.6;
      }

      :deep(ul), :deep(ol) {
        margin-bottom: 8px;
        padding-left: 20px;
      }

      :deep(img) {
        max-width: 100%;
        height: auto;
        border-radius: 4px;
      }
    }
  }

  .published-dynamics {
    .dynamics-list {
      .dynamic-item {
        padding: 16px;
        border-bottom: 1px solid #f0f0f0;
        transition: background-color 0.3s ease;

        &:hover {
          background-color: #f8f9fa;
        }

        &:last-child {
          border-bottom: none;
        }

        .dynamic-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 8px;

          .dynamic-title {
            font-size: 16px;
            font-weight: 600;
            color: #303133;
            flex: 1;
          }

          .dynamic-meta {
            display: flex;
            align-items: center;
            gap: 8px;

            .publish-time {
              font-size: 12px;
              color: #909399;
            }
          }
        }

        .dynamic-content {
          color: #606266;
          line-height: 1.6;
          margin-bottom: 12px;
          overflow: hidden;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .dynamic-stats {
          display: flex;
          gap: 16px;
          margin-bottom: 12px;

          .stat-item {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 12px;
            color: #909399;

            .el-icon {
              font-size: 14px;
            }
          }
        }

        .dynamic-actions {
          display: flex;
          gap: 8px;
        }
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }
}

@media (max-width: 768px) {
  .dynamics-publish {
    .publish-form {
      .editor-container {
        .editor-toolbar {
          gap: 4px;

          .el-button-group {
            margin-bottom: 4px;
          }
        }
      }
    }

    .published-dynamics {
      .dynamics-list {
        .dynamic-item {
          .dynamic-header {
            flex-direction: column;
            gap: 8px;
          }

          .dynamic-actions {
            flex-wrap: wrap;
          }
        }
      }
    }
  }
}
</style>