<template>
  <div class="visit-record-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      label-position="left"
    >
      <el-form-item label="探访标题" prop="title">
        <el-input
          v-model="formData.title"
          placeholder="请输入探访标题"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>

      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="探访类型" prop="type">
            <el-select
              v-model="formData.type"
              placeholder="请选择探访类型"
              style="width: 100%"
            >
              <el-option label="常规探访" value="routine" />
              <el-option label="特殊活动" value="special" />
              <el-option label="紧急探访" value="emergency" />
              <el-option label="评估探访" value="evaluation" />
            </el-select>
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="探访时间" prop="visitDate">
            <el-date-picker
              v-model="formData.visitDate"
              type="datetime"
              placeholder="请选择探访时间"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="探访时长" prop="duration">
            <el-input-number
              v-model="formData.duration"
              :min="0.5"
              :max="24"
              :step="0.5"
              placeholder="探访时长（小时）"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="参与人数" prop="participantCount">
            <el-input-number
              v-model="formData.participantCount"
              :min="1"
              :max="1000"
              placeholder="参与人数"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="探访人员" prop="visitors">
        <el-select
          v-model="formData.visitors"
          multiple
          filterable
          allow-create
          placeholder="请选择或输入探访人员"
          style="width: 100%"
        >
          <el-option
            v-for="visitor in visitorOptions"
            :key="visitor"
            :label="visitor"
            :value="visitor"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="探访描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="4"
          placeholder="请详细描述探访过程、目的和收获"
          maxlength="1000"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="反馈意见" prop="feedback">
        <el-input
          v-model="formData.feedback"
          type="textarea"
          :rows="3"
          placeholder="请输入探访反馈和建议"
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
import type { CharityInstitution } from '@/api/mock/charity'

interface VisitRecord {
  id?: number
  title: string
  type: string
  visitDate: string
  visitors: string[]
  duration: number
  participantCount: number
  description: string
  feedback: string
}

interface VisitRecordFormProps {
  record?: VisitRecord | null
  mode: 'add' | 'edit'
  institution?: CharityInstitution | null
}

const props = withDefaults(defineProps<VisitRecordFormProps>(), {
  record: null,
  mode: 'add',
  institution: null
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
  visitDate: '',
  visitors: [] as string[],
  duration: 2,
  participantCount: 20,
  description: '',
  feedback: ''
})

const visitorOptions = [
  '张志愿', '李爱心', '王公益', '陈助学', '刘奉献',
  '杨热心', '赵善良', '黄温暖', '周关爱', '吴慈善'
]

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入探访标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度在2-50个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择探访类型', trigger: 'change' }
  ],
  visitDate: [
    { required: true, message: '请选择探访时间', trigger: 'change' }
  ],
  visitors: [
    { required: true, message: '请选择探访人员', trigger: 'change' },
    { type: 'array', min: 1, message: '至少选择一名探访人员', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请输入探访时长', trigger: 'blur' },
    { type: 'number', min: 0.5, max: 24, message: '探访时长应在0.5-24小时之间', trigger: 'blur' }
  ],
  participantCount: [
    { required: true, message: '请输入参与人数', trigger: 'blur' },
    { type: 'number', min: 1, max: 1000, message: '参与人数应在1-1000人之间', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入探访描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '描述长度在10-1000个字符', trigger: 'blur' }
  ]
}

// 方法
const initFormData = () => {
  if (props.record && props.mode === 'edit') {
    Object.assign(formData, {
      title: props.record.title,
      type: props.record.type,
      visitDate: props.record.visitDate,
      visitors: [...props.record.visitors],
      duration: props.record.duration,
      participantCount: props.record.participantCount,
      description: props.record.description,
      feedback: props.record.feedback || ''
    })
  } else {
    // 重置表单数据
    Object.assign(formData, {
      title: '',
      type: '',
      visitDate: '',
      visitors: [],
      duration: 2,
      participantCount: 20,
      description: '',
      feedback: ''
    })
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    submitting.value = true
    
    // 准备提交数据
    const submitData = {
      ...formData,
      id: props.record?.id || undefined
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
watch(() => props.record, () => {
  initFormData()
}, { immediate: true })

// 生命周期
onMounted(() => {
  initFormData()
})
</script>

<style lang="scss" scoped>
.visit-record-form {
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
  .visit-record-form {
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