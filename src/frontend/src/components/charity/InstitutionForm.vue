<template>
  <div class="institution-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      label-position="left"
    >
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="机构名称" prop="name">
            <el-input
              v-model="formData.name"
              placeholder="请输入机构名称"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="机构类型" prop="type">
            <el-select
              v-model="formData.type"
              placeholder="请选择机构类型"
              style="width: 100%"
            >
              <el-option label="学校" value="school">
                <div class="option-item">
                  <el-icon><School /></el-icon>
                  <span>学校</span>
                </div>
              </el-option>
              <el-option label="社区中心" value="community">
                <div class="option-item">
                  <el-icon><OfficeBuilding /></el-icon>
                  <span>社区中心</span>
                </div>
              </el-option>
              <el-option label="福利院" value="orphanage">
                <div class="option-item">
                  <el-icon><House /></el-icon>
                  <span>福利院</span>
                </div>
              </el-option>
              <el-option label="图书馆" value="library">
                <div class="option-item">
                  <el-icon><Reading /></el-icon>
                  <span>图书馆</span>
                </div>
              </el-option>
              <el-option label="医院" value="hospital">
                <div class="option-item">
                  <el-icon><FirstAidKit /></el-icon>
                  <span>医院</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="所在地区" prop="location">
            <el-cascader
              v-model="formData.locationArray"
              :options="locationOptions"
              placeholder="请选择所在地区"
              style="width: 100%"
              @change="handleLocationChange"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="详细地址" prop="address">
            <el-input
              v-model="formData.address"
              placeholder="请输入详细地址"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="联系人" prop="contactPerson">
            <el-input
              v-model="formData.contactPerson"
              placeholder="请输入联系人姓名"
              maxlength="20"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="联系电话" prop="contactPhone">
            <el-input
              v-model="formData.contactPhone"
              placeholder="请输入联系电话"
              maxlength="20"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="电子邮箱" prop="email">
            <el-input
              v-model="formData.email"
              placeholder="请输入电子邮箱"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="受益学生数" prop="studentCount">
            <el-input-number
              v-model="formData.studentCount"
              :min="0"
              :max="10000"
              placeholder="请输入受益学生数量"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="合作状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio value="active">合作中</el-radio>
              <el-radio value="pending">待审核</el-radio>
              <el-radio value="inactive">暂停合作</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="设备数量" prop="deviceCount">
            <el-input-number
              v-model="formData.deviceCount"
              :min="0"
              :max="100"
              placeholder="已配置设备数量"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="合作开始时间" prop="cooperationDate">
        <el-date-picker
          v-model="formData.cooperationDate"
          type="date"
          placeholder="请选择合作开始时间"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 300px"
        />
      </el-form-item>

      <el-form-item label="最近探访时间" prop="lastVisitDate">
        <el-date-picker
          v-model="formData.lastVisitDate"
          type="date"
          placeholder="请选择最近探访时间"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 300px"
        />
      </el-form-item>

      <el-form-item label="备注信息" prop="notes">
        <el-input
          v-model="formData.notes"
          type="textarea"
          :rows="4"
          placeholder="请输入备注信息"
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
  School,
  OfficeBuilding,
  House,
  Reading,
  FirstAidKit
} from '@element-plus/icons-vue'
import type { CharityInstitution } from '@/api/mock/charity'

interface InstitutionFormProps {
  institution?: CharityInstitution | null
  mode: 'add' | 'edit'
}

const props = withDefaults(defineProps<InstitutionFormProps>(), {
  institution: null,
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
  name: '',
  type: '',
  location: '',
  locationArray: [] as string[],
  address: '',
  contactPerson: '',
  contactPhone: '',
  email: '',
  studentCount: 0,
  status: 'active',
  deviceCount: 0,
  cooperationDate: '',
  lastVisitDate: '',
  notes: ''
})

// 地区选项数据
const locationOptions = [
  {
    value: '北京市',
    label: '北京市',
    children: [
      { value: '朝阳区', label: '朝阳区' },
      { value: '海淀区', label: '海淀区' },
      { value: '西城区', label: '西城区' },
      { value: '东城区', label: '东城区' },
      { value: '丰台区', label: '丰台区' }
    ]
  },
  {
    value: '上海市',
    label: '上海市',
    children: [
      { value: '浦东新区', label: '浦东新区' },
      { value: '黄浦区', label: '黄浦区' },
      { value: '静安区', label: '静安区' },
      { value: '徐汇区', label: '徐汇区' },
      { value: '长宁区', label: '长宁区' }
    ]
  },
  {
    value: '广东省',
    label: '广东省',
    children: [
      { value: '广州市', label: '广州市' },
      { value: '深圳市', label: '深圳市' },
      { value: '珠海市', label: '珠海市' },
      { value: '佛山市', label: '佛山市' },
      { value: '东莞市', label: '东莞市' }
    ]
  },
  {
    value: '浙江省',
    label: '浙江省',
    children: [
      { value: '杭州市', label: '杭州市' },
      { value: '宁波市', label: '宁波市' },
      { value: '温州市', label: '温州市' },
      { value: '嘉兴市', label: '嘉兴市' },
      { value: '湖州市', label: '湖州市' }
    ]
  },
  {
    value: '江苏省',
    label: '江苏省',
    children: [
      { value: '南京市', label: '南京市' },
      { value: '苏州市', label: '苏州市' },
      { value: '无锡市', label: '无锡市' },
      { value: '常州市', label: '常州市' },
      { value: '南通市', label: '南通市' }
    ]
  }
]

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入机构名称', trigger: 'blur' },
    { min: 2, max: 50, message: '机构名称长度在2-50个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择机构类型', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请选择所在地区', trigger: 'change' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 5, max: 100, message: '详细地址长度在5-100个字符', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '联系人姓名长度在2-20个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入正确的电话号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  studentCount: [
    { required: true, message: '请输入受益学生数量', trigger: 'blur' },
    { type: 'number', min: 0, max: 10000, message: '学生数量应在0-10000之间', trigger: 'blur' }
  ],
  cooperationDate: [
    { required: true, message: '请选择合作开始时间', trigger: 'change' }
  ]
}

// 方法
const handleLocationChange = (value: string[]) => {
  if (value && value.length > 0) {
    formData.location = value.join(' ')
  } else {
    formData.location = ''
  }
}

const initFormData = () => {
  if (props.institution && props.mode === 'edit') {
    Object.assign(formData, {
      name: props.institution.name,
      type: props.institution.type,
      location: props.institution.location,
      locationArray: props.institution.location.split(' '),
      address: props.institution.address,
      contactPerson: props.institution.contactPerson,
      contactPhone: props.institution.contactPhone,
      email: props.institution.email,
      studentCount: props.institution.studentCount,
      status: props.institution.status,
      deviceCount: props.institution.deviceCount,
      cooperationDate: props.institution.cooperationDate,
      lastVisitDate: props.institution.lastVisitDate || '',
      notes: props.institution.notes
    })
  } else {
    // 重置表单数据
    Object.assign(formData, {
      name: '',
      type: '',
      location: '',
      locationArray: [],
      address: '',
      contactPerson: '',
      contactPhone: '',
      email: '',
      studentCount: 0,
      status: 'active',
      deviceCount: 0,
      cooperationDate: '',
      lastVisitDate: '',
      notes: ''
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
      id: props.institution?.id || undefined
    }
    
    // 移除不需要的字段
    delete submitData.locationArray
    
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
watch(() => props.institution, () => {
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
.institution-form {
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
  .institution-form {
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