<template>
  <div class="course-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>课程管理</h2>
        <p class="header-subtitle">汉字课程管理 · 练字机器人管理系统</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAddCourse">
          添加课程
        </el-button>
      </div>
    </div>



    <!-- 课程列表 -->
    <DataTable
      :data="courses"
      :columns="courseColumns"
      :loading="loading"
      :pagination="pagination"
      @page-change="handlePageChange"
      @size-change="handleSizeChange"
      @refresh="loadCourses"
    >
      <!-- 难度等级 -->
      <template #difficulty="{ row }">
        <el-tag 
          :type="getDifficultyTagType(row.difficulty)"
          size="small"
        >
          {{ getDifficultyText(row.difficulty) }}
        </el-tag>
      </template>

      <!-- 课程状态 -->
      <template #status="{ row }">
        <StatusTag :status="row.status" />
      </template>

      <!-- 操作列 -->
      <template #actions="{ row }">
        <el-button type="primary" size="small" text @click="handleViewCourse(row)">
          查看
        </el-button>
        <el-button type="primary" size="small" text @click="handleEditCourse(row)">
          编辑
        </el-button>
        <el-button type="danger" size="small" text @click="handleDeleteCourse(row.id)">
          删除
        </el-button>
      </template>
    </DataTable>

    <!-- 课程详情对话框 -->
    <CommonDialog
      v-model="detailDialogVisible"
      title="课程详情"
      width="1200px"
      @close="handleDetailDialogClose"
    >
      <div v-if="selectedCourse" class="course-detail">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h3>基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <label>课程名称：</label>
              <span>{{ selectedCourse.name }}</span>
            </div>
            <div class="info-item">
              <label>课程描述：</label>
              <span>{{ selectedCourse.description }}</span>
            </div>
            <div class="info-item">
              <label>难度等级：</label>
              <el-tag :type="getDifficultyTagType(selectedCourse.difficulty)" size="small">
                {{ getDifficultyText(selectedCourse.difficulty) }}
              </el-tag>
            </div>
            <div class="info-item">
              <label>课程分类：</label>
              <span>{{ selectedCourse.category }}</span>
            </div>
            <div class="info-item">
              <label>课程时长：</label>
              <span>{{ selectedCourse.duration }} 分钟</span>
            </div>
            <div class="info-item">
              <label>汉字数量：</label>
              <span>{{ selectedCourse.characterCount }} 个</span>
            </div>
            <div class="info-item">
              <label>课程状态：</label>
              <StatusTag :status="selectedCourse.status" />
            </div>
            <div class="info-item">
              <label>创建时间：</label>
              <span>{{ selectedCourse.createdAt }}</span>
            </div>
          </div>
        </div>

        <!-- 课程汉字管理 -->
        <div class="detail-section">
          <h3>课程汉字管理</h3>
          <CharacterManagement
            :course-id="selectedCourse.id"
            @update="handleCharactersUpdate"
          />
        </div>
      </div>
    </CommonDialog>

    <!-- 课程编辑对话框 -->
    <CommonDialog
      v-model="editDialogVisible"
      :title="editingCourse.id ? '编辑课程' : '添加课程'"
      width="600px"
      @close="handleEditDialogClose"
    >
      <el-form
        ref="courseFormRef"
        :model="editingCourse"
        :rules="courseRules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="editingCourse.name" placeholder="请输入课程名称" />
        </el-form-item>
        
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="editingCourse.description"
            type="textarea"
            :rows="3"
            placeholder="请输入课程描述"
          />
        </el-form-item>
        
        <el-form-item label="难度等级" prop="difficulty">
          <el-select v-model="editingCourse.difficulty" placeholder="请选择难度等级">
            <el-option label="初级" value="beginner" />
            <el-option label="中级" value="intermediate" />
            <el-option label="高级" value="advanced" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="课程分类" prop="category">
          <el-input v-model="editingCourse.category" placeholder="请输入课程分类" />
        </el-form-item>
        
        <el-form-item label="课程时长" prop="duration">
          <el-input-number
            v-model="editingCourse.duration"
            :min="1"
            :max="1000"
            placeholder="请输入课程时长（分钟）"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="课程状态" prop="status">
          <el-select v-model="editingCourse.status" placeholder="请选择课程状态">
            <el-option label="活跃" value="active" />
            <el-option label="非活跃" value="inactive" />
            <el-option label="草稿" value="draft" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="课程标签" prop="tags">
          <el-input v-model="tagsInput" placeholder="请输入标签，用逗号分隔" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleEditDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSaveCourse" :loading="saveLoading">
            保存
          </el-button>
        </div>
      </template>
    </CommonDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import DataTable from '@/components/common/DataTable.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import CommonDialog from '@/components/common/CommonDialog.vue'
import CharacterManagement from '@/components/course/CharacterManagement.vue'
import { mockCourseAPI } from '@/api/mock'
import type { Course, Character } from '@/api/mock'

// 响应式数据
const loading = ref(false)
const saveLoading = ref(false)

// 课程列表数据
const courses = ref<Course[]>([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})



// 表格列配置
const courseColumns = [
  { prop: 'id', label: 'ID', width: 80 },
  { prop: 'name', label: '课程名称', width: 200 },
  { prop: 'category', label: '分类', width: 120 },
  { prop: 'difficulty', label: '难度', width: 100, slot: 'difficulty' },
  { prop: 'duration', label: '时长(分钟)', width: 100 },
  { prop: 'characterCount', label: '汉字数量', width: 100 },
  { prop: 'status', label: '状态', width: 100, slot: 'status' },
  { prop: 'createdBy', label: '创建者', width: 120 },
  { prop: 'createdAt', label: '创建时间', width: 120 },
  { prop: 'actions', label: '操作', width: 180, slot: 'actions', fixed: 'right' }
]

// 对话框状态
const detailDialogVisible = ref(false)
const editDialogVisible = ref(false)

// 选中的课程
const selectedCourse = ref<Course | null>(null)
const courseCharacters = ref<Character[]>([])

// 编辑课程数据
const editingCourse = reactive<Partial<Course>>({
  name: '',
  description: '',
  difficulty: 'beginner',
  category: '',
  duration: 60,
  status: 'active',
  tags: []
})

// 标签输入
const tagsInput = ref('')

// 表单验证规则
const courseRules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入课程描述', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请输入课程分类', trigger: 'blur' }
  ],
  duration: [
    { required: true, message: '请输入课程时长', trigger: 'blur' }
  ]
}

// 表单引用
const courseFormRef = ref()

// 方法
const loadCourses = async () => {
  loading.value = true
  try {
    const response = await mockCourseAPI.getCourses({
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    
    courses.value = response.data.list
    pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('加载课程列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadCourses()
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.page = 1
  loadCourses()
}

const handleViewCourse = async (course: Course) => {
  selectedCourse.value = course
  detailDialogVisible.value = true
  
  // 加载课程汉字
  try {
    const charactersResponse = await mockCourseAPI.getCourseCharacters(course.id)
    courseCharacters.value = charactersResponse.data
  } catch (error) {
    ElMessage.error('加载课程汉字失败')
  }
}

const handleAddCourse = () => {
  Object.assign(editingCourse, {
    id: undefined,
    name: '',
    description: '',
    difficulty: 'beginner',
    category: '',
    duration: 60,
    status: 'active',
    tags: []
  })
  tagsInput.value = ''
  editDialogVisible.value = true
}

const handleEditCourse = (course: Course) => {
  Object.assign(editingCourse, { ...course })
  tagsInput.value = course.tags.join(', ')
  editDialogVisible.value = true
}

const handleSaveCourse = async () => {
  if (!courseFormRef.value) return
  
  try {
    await courseFormRef.value.validate()
    
    // 处理标签
    editingCourse.tags = tagsInput.value
      .split(',')
      .map(tag => tag.trim())
      .filter(tag => tag.length > 0)
    
    saveLoading.value = true
    
    if (editingCourse.id) {
      await mockCourseAPI.updateCourse(editingCourse.id, editingCourse)
      ElMessage.success('课程更新成功')
    } else {
      await mockCourseAPI.createCourse(editingCourse)
      ElMessage.success('课程创建成功')
    }
    
    editDialogVisible.value = false
    loadCourses()
  } catch (error) {
    ElMessage.error('保存课程失败')
  } finally {
    saveLoading.value = false
  }
}

const handleDeleteCourse = async (courseId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个课程吗？', '确认删除', {
      type: 'warning'
    })
    
    await mockCourseAPI.deleteCourse(courseId)
    ElMessage.success('课程删除成功')
    loadCourses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除课程失败')
    }
  }
}

const handleCharactersUpdate = () => {
  // 汉字更新后的回调处理
  ElMessage.success('课程汉字已更新')
}

const handleDetailDialogClose = () => {
  detailDialogVisible.value = false
  selectedCourse.value = null
  courseCharacters.value = []
}

const handleEditDialogClose = () => {
  editDialogVisible.value = false
  courseFormRef.value?.resetFields()
  tagsInput.value = ''
}

// 工具方法
const getDifficultyTagType = (difficulty: string) => {
  const typeMap: Record<string, string> = {
    beginner: 'success',
    intermediate: 'warning',
    advanced: 'danger'
  }
  return typeMap[difficulty] || ''
}

const getDifficultyText = (difficulty: string) => {
  const textMap: Record<string, string> = {
    beginner: '初级',
    intermediate: '中级',
    advanced: '高级'
  }
  return textMap[difficulty] || difficulty
}

// 生命周期
onMounted(() => {
  loadCourses()
})
</script>

<style scoped lang="scss">
.course-management {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .header-left {
      h2 {
        margin: 0 0 8px 0;
        color: #303133;
        font-size: 24px;
        font-weight: 600;
      }

      .header-subtitle {
        margin: 0;
        color: #909399;
        font-size: 14px;
      }
    }

    .header-right {
      display: flex;
      gap: 12px;
    }
  }

  .course-detail {
    .detail-section {
      margin-bottom: 32px;
      
      h3 {
        margin: 0 0 16px 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        border-bottom: 1px solid #EBEEF5;
        padding-bottom: 8px;
      }

      .info-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;

        .info-item {
          display: flex;
          align-items: center;

          label {
            min-width: 100px;
            color: #606266;
            font-weight: 500;
          }

          span {
            color: #303133;
          }
        }
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .course-management {
    padding: 16px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;

      .header-right {
        width: 100%;
        justify-content: flex-end;
      }
    }

    .course-detail {
      .detail-section {
        .info-grid {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}
</style>