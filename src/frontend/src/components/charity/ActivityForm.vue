<template>
  <div class="activity-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      label-position="left"
    >
      <el-row :gutter="24">
        <el-col :span="16">
          <el-form-item label="活动标题" prop="title">
            <el-input
              v-model="formData.title"
              placeholder="请输入活动标题"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="8">
          <el-form-item label="活动类型" prop="type">
            <el-select
              v-model="formData.type"
              placeholder="请选择活动类型"
              style="width: 100%"
            >
              <el-option label="实地探访" value="visit">
                <div class="option-item">
                  <el-icon><View /></el-icon>
                  <span>实地探访</span>
                </div>
              </el-option>
              <el-option label="培训活动" value="training">
                <div class="option-item">
                  <el-icon><Reading /></el-icon>
                  <span>培训活动</span>
                </div>
              </el-option>
              <el-option label="捐赠活动" value="donation">
                <div class="option-item">
                  <el-icon><Present /></el-icon>
                  <span>捐赠活动</span>
                </div>
              </el-option>
              <el-option label="特殊活动" value="event">
                <div class="option-item">
                  <el-icon><Star /></el-icon>
                  <span>特殊活动</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="活动描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="4"
          placeholder="请详细描述活动的目的、内容和预期效果"
          maxlength="1000"
          show-word-limit
        />
      </el-form-item>

      <el-row :gutter="24">
        <el-col :span="8">
          <el-form-item label="活动日期" prop="date">
            <el-date-picker
              v-model="formData.date"
              type="date"
              placeholder="请选择活动日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="8">
          <el-form-item label="开始时间" prop="startTime">
            <el-time-picker
              v-model="formData.startTime"
              placeholder="请选择开始时间"
              format="HH:mm"
              value-format="HH:mm:ss"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="8">
          <el-form-item label="结束时间" prop="endTime">
            <el-time-picker
              v-model="formData.endTime"
              placeholder="请选择结束时间"
              format="HH:mm"
              value-format="HH:mm:ss"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="24">
        <el-col :span="24">
          <el-form-item label="活动地点" prop="location">
            <el-input
              v-model="formData.location"
              placeholder="请输入活动地点"
              maxlength="100"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="主办方" prop="organizer">
            <el-select
              v-model="formData.organizer"
              filterable
              allow-create
              placeholder="请选择或输入主办方"
              style="width: 100%"
            >
              <el-option
                v-for="organizer in organizerOptions"
                :key="organizer"
                :label="organizer"
                :value="organizer"
              />
            </el-select>
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="活动状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio value="planned">已计划</el-radio>
              <el-radio value="ongoing">进行中</el-radio>
              <el-radio value="completed">已完成</el-radio>
              <el-radio value="cancelled">已取消</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="活动负责人" prop="manager">
            <el-input
              v-model="formData.manager"
              placeholder="请输入活动负责人姓名"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="负责人联系方式" prop="managerContact">
            <el-input
              v-model="formData.managerContact"
              placeholder="请输入负责人手机号或电话"
              maxlength="20"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="24">
        <el-col :span="8">
          <el-form-item label="参与人数" prop="participants">
            <el-input-number
              v-model="formData.participants"
              :min="1"
              :max="10000"
              placeholder="预计参与人数"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="8">
          <el-form-item label="预算金额" prop="budget">
            <el-input-number
              v-model="formData.budget"
              :min="0"
              :max="1000000"
              :precision="2"
              placeholder="活动预算（元）"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="8">
          <el-form-item label="实际花费" prop="actualCost">
            <el-input-number
              v-model="formData.actualCost"
              :min="0"
              :max="1000000"
              :precision="2"
              placeholder="实际花费（元）"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="关联项目" prop="projectId">
        <el-select
          v-model="formData.projectId"
          placeholder="请选择关联的公益项目（可选）"
          style="width: 300px"
          clearable
        >
          <el-option
            v-for="project in projectOptions"
            :key="project.id"
            :label="project.name"
            :value="project.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="活动照片" prop="photos">
        <FileUploadManager
          v-model="formData.photos"
          :max-count="20"
          accept="image/*"
          :preview="true"
          upload-text="上传活动照片"
          tip="支持jpg、png格式，单张图片不超过5MB，最多上传20张"
        />
      </el-form-item>

      <el-form-item label="反馈意见" prop="feedback">
        <el-input
          v-model="formData.feedback"
          type="textarea"
          :rows="3"
          placeholder="请输入活动反馈和总结（活动完成后填写）"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <div class="form-actions">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        {{ mode === 'add' ? '添加' : '保存' }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  View,
  Reading,
  Present,
  Star
} from '@element-plus/icons-vue'
import FileUploadManager from '@/components/upload/FileUploadManager.vue'
import type { CharityActivity } from '@/api/mock/charity'

interface ActivityFormProps {
  activity?: CharityActivity | null
  mode: 'add' | 'edit'
}

const props = withDefaults(defineProps<ActivityFormProps>(), {
  activity: null,
  mode: 'add'
})

const emit = defineEmits<{
  submit: [data: any]
  cancel: []
}>()

// 响应式数据
const formRef = ref()
const submitting = ref(false)

const formData = reactive({
  title: '',
  type: '',
  description: '',
  date: '',
  startTime: '',
  endTime: '',
  location: '',
  organizer: '',
  status: 'planned',
  participants: 50,
  budget: 10000,
  actualCost: 0,
  projectId: null as number | null,
  photos: [] as string[],
  feedback: '',
  manager: '',
  managerContact: ''
})

// 选项数据
const organizerOptions = [
  'YX机器人公益基金会',
  '教育发展基金会',
  '儿童关爱协会',
  '文化传承基金',
  '科技助学联盟',
  '爱心企业联合会',
  '志愿者协会',
  '慈善总会'
]

const projectOptions = ref([
  { id: 1, name: '山区儿童汉字启蒙计划' },
  { id: 2, name: '留守儿童书法教育项目' },
  { id: 3, name: '特殊教育汉字学习支持' },
  { id: 4, name: '乡村小学数字化教学援助' },
  { id: 5, name: '贫困地区教育设备捐赠' }
])

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度在5-100个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择活动类型', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入活动描述', trigger: 'blur' },
    { min: 20, max: 1000, message: '描述长度在20-1000个字符', trigger: 'blur' }
  ],
  date: [
    { required: true, message: '请选择活动日期', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入活动地点', trigger: 'blur' },
    { min: 2, max: 100, message: '地点长度在2-100个字符', trigger: 'blur' }
  ],
  organizer: [
    { required: true, message: '请选择或输入主办方', trigger: 'blur' }
  ],
  participants: [
    { required: true, message: '请输入参与人数', trigger: 'blur' },
    { type: 'number', min: 1, max: 10000, message: '参与人数应在1-10000人之间', trigger: 'blur' }
  ],
  budget: [
    { required: true, message: '请输入预算金额', trigger: 'blur' },
    { type: 'number', min: 0, max: 1000000, message: '预算金额应在0-100万元之间', trigger: 'blur' }
  ],
  actualCost: [
    { type: 'number', min: 0, max: 1000000, message: '实际花费应在0-100万元之间', trigger: 'blur' }
  ],
  manager: [
    { required: true, message: '请输入活动负责人', trigger: 'blur' },
    { min: 2, max: 50, message: '负责人姓名长度在2-50个字符', trigger: 'blur' }
  ],
  managerContact: [
    { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入正确的手机号或电话号码', trigger: 'blur' }
  ]
}

// 方法
const initFormData = () => {
  if (props.activity && props.mode === 'edit') {
    // 处理时间字段的转换
    let startTimeStr = ''
    let endTimeStr = ''
    
    if (props.activity.startTime) {
      // 从 "2024-12-18 09:00:00" 格式中提取时间部分
      const startTimeParts = props.activity.startTime.split(' ')
      if (startTimeParts.length > 1) {
        startTimeStr = startTimeParts[1] // "09:00:00"
      }
    }
    
    if (props.activity.endTime) {
      // 从 "2024-12-18 17:00:00" 格式中提取时间部分
      const endTimeParts = props.activity.endTime.split(' ')
      if (endTimeParts.length > 1) {
        endTimeStr = endTimeParts[1] // "17:00:00"
      }
    }
    
    Object.assign(formData, {
      title: props.activity.title,
      type: props.activity.type,
      description: props.activity.description,
      date: props.activity.date,
      startTime: startTimeStr,
      endTime: endTimeStr,
      location: props.activity.location,
      organizer: props.activity.organizer,
      status: props.activity.status,
      participants: props.activity.participants,
      budget: props.activity.budget,
      actualCost: props.activity.actualCost,
      projectId: props.activity.projectId || null,
      photos: props.activity.photos || [],
      feedback: props.activity.feedback || '',
      manager: props.activity.manager || '',
      managerContact: props.activity.managerContact || ''
    })
  } else {
    // 重置表单数据
    Object.assign(formData, {
      title: '',
      type: '',
      description: '',
      date: '',
      startTime: '',
      endTime: '',
      location: '',
      organizer: '',
      status: 'planned',
      participants: 50,
      budget: 10000,
      actualCost: 0,
      projectId: null,
      photos: [],
      feedback: '',
      manager: '',
      managerContact: ''
    })
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    submitting.value = true
    
    // 组装时间数据
    let startTime = ''
    let endTime = ''
    
    if (formData.date && formData.startTime) {
      startTime = `${formData.date} ${formData.startTime}`
    }
    
    if (formData.date && formData.endTime) {
      endTime = `${formData.date} ${formData.endTime}`
    }
    
    // 准备提交数据
    const submitData = {
      ...formData,
      startTime,
      endTime,
      id: props.activity?.id || undefined
    }
    
    emit('submit', submitData)
  } catch (error) {
    ElMessage.error('请检查表单填写是否正确')
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  emit('cancel')
}

// 监听props变化
watch(() => props.activity, () => {
  initFormData()
}, { immediate: true })

// 生命周期
onMounted(() => {
  initFormData()
})

// 暴露方法给父组件
defineExpose({
  resetForm: () => {
    formRef.value?.resetFields()
    initFormData()
  }
})
</script>

<style lang="scss" scoped>
.activity-form {
  .option-item {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #f0f0f0;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .activity-form {
    .el-col {
      margin-bottom: 16px;
    }
    
    .form-actions {
      flex-direction: column;
      
      .el-button {
        width: 100%;
      }
    }
  }
}
</style>