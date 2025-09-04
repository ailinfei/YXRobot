<!--
  客户创建/编辑对话框
  支持客户基本信息的创建和编辑
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑客户' : '创建客户'"
    width="70%"
    :before-close="handleClose"
    class="customer-form-dialog"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      class="customer-form"
    >
      <!-- 基本信息 -->
      <div class="form-section">
        <h3 class="section-title">基本信息</h3>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入客户姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户类型" prop="type">
              <el-select v-model="formData.type" placeholder="选择客户类型" style="width: 100%">
                <el-option label="个人客户" value="individual" />
                <el-option label="企业客户" value="enterprise" />
                <el-option label="教育机构" value="education" />
                <el-option label="政府机构" value="government" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入电子邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="客户等级" prop="level">
              <el-select v-model="formData.level" placeholder="选择客户等级" style="width: 100%">
                <el-option label="普通客户" value="regular" />
                <el-option label="VIP客户" value="vip" />
                <el-option label="高级客户" value="premium" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户状态" prop="status">
              <el-select v-model="formData.status" placeholder="选择客户状态" style="width: 100%">
                <el-option label="活跃" value="active" />
                <el-option label="非活跃" value="inactive" />
                <el-option label="暂停" value="suspended" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 联系信息 -->
      <div class="form-section">
        <h3 class="section-title">联系信息</h3>
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="省份" prop="address.province">
              <el-select
                v-model="formData.address.province"
                placeholder="选择省份"
                @change="handleProvinceChange"
                style="width: 100%"
              >
                <el-option
                  v-for="province in provinces"
                  :key="province"
                  :label="province"
                  :value="province"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="address.city">
              <el-select
                v-model="formData.address.city"
                placeholder="选择城市"
                style="width: 100%"
              >
                <el-option
                  v-for="city in cities"
                  :key="city"
                  :label="city"
                  :value="city"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区县" prop="address.district">
              <el-input v-model="formData.address.district" placeholder="区县" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="18">
            <el-form-item label="详细地址" prop="address.detail">
              <el-input v-model="formData.address.detail" placeholder="详细地址" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="邮政编码" prop="address.zipCode">
              <el-input v-model="formData.address.zipCode" placeholder="邮政编码" />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 企业信息 (仅企业客户显示) -->
      <div v-if="formData.type === 'enterprise'" class="form-section">
        <h3 class="section-title">企业信息</h3>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="企业名称" prop="companyName">
              <el-input v-model="formData.companyName" placeholder="请输入企业名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="统一社会信用代码" prop="creditCode">
              <el-input v-model="formData.creditCode" placeholder="请输入统一社会信用代码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="法定代表人" prop="legalRepresentative">
              <el-input v-model="formData.legalRepresentative" placeholder="请输入法定代表人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业规模" prop="companySize">
              <el-select v-model="formData.companySize" placeholder="选择企业规模" style="width: 100%">
                <el-option label="小型企业" value="small" />
                <el-option label="中型企业" value="medium" />
                <el-option label="大型企业" value="large" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 标签和备注 -->
      <div class="form-section">
        <h3 class="section-title">标签和备注</h3>
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="客户标签" prop="tags">
              <el-select
                v-model="formData.tags"
                multiple
                filterable
                allow-create
                placeholder="选择或创建标签"
                style="width: 100%"
              >
                <el-option
                  v-for="tag in availableTags"
                  :key="tag"
                  :label="tag"
                  :value="tag"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="备注信息" prop="notes">
              <el-input
                v-model="formData.notes"
                type="textarea"
                :rows="3"
                placeholder="客户备注信息"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '更新客户' : '创建客户' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import type { Customer } from '@/types/customer'
import { customerApi } from '@/api/customer'

// Props
interface Props {
  modelValue: boolean
  customer?: Customer | null
}

const props = withDefaults(defineProps<Props>(), {
  customer: null
})

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  success: []
}>()

// 响应式数据
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formRef = ref()
const submitLoading = ref(false)

// 表单数据
const defaultFormData = () => ({
  name: '',
  type: 'individual',
  phone: '',
  email: '',
  level: 'regular',
  status: 'active',
  address: {
    province: '',
    city: '',
    district: '',
    detail: '',
    zipCode: ''
  },
  companyName: '',
  creditCode: '',
  legalRepresentative: '',
  companySize: '',
  tags: [] as string[],
  notes: ''
})

const formData = ref(defaultFormData())

// 计算属性
const isEdit = computed(() => !!props.customer)

// 选项数据
const provinces = ref([
  '北京市', '上海市', '天津市', '重庆市',
  '河北省', '山西省', '辽宁省', '吉林省', '黑龙江省',
  '江苏省', '浙江省', '安徽省', '福建省', '江西省', '山东省',
  '河南省', '湖北省', '湖南省', '广东省', '海南省',
  '四川省', '贵州省', '云南省', '陕西省', '甘肃省', '青海省',
  '内蒙古自治区', '广西壮族自治区', '西藏自治区', '宁夏回族自治区', '新疆维吾尔自治区'
])

const cities = ref<string[]>([])

const availableTags = ref([
  '重要客户', '长期合作', '教育行业', '政府客户', '企业客户',
  '个人用户', '新客户', '老客户', '高价值', '潜在客户'
])

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  type: [{ required: true, message: '请选择客户类型', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入电子邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  level: [{ required: true, message: '请选择客户等级', trigger: 'change' }],
  status: [{ required: true, message: '请选择客户状态', trigger: 'change' }],
  'address.province': [{ required: true, message: '请选择省份', trigger: 'change' }],
  'address.city': [{ required: true, message: '请选择城市', trigger: 'change' }],
  'address.detail': [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  companyName: [
    { 
      required: true, 
      message: '请输入企业名称', 
      trigger: 'blur',
      validator: (rule: any, value: string, callback: Function) => {
        if (formData.value.type === 'enterprise' && !value) {
          callback(new Error('请输入企业名称'))
        } else {
          callback()
        }
      }
    }
  ]
}

// 监听器
watch(() => props.modelValue, (visible) => {
  if (visible) {
    initForm()
  }
})

watch(() => props.customer, (customer) => {
  if (customer && props.modelValue) {
    initForm()
  }
})

// 方法
const initForm = () => {
  if (props.customer) {
    // 编辑模式
    formData.value = {
      ...defaultFormData(),
      ...props.customer
    }
  } else {
    // 创建模式
    formData.value = defaultFormData()
  }
  
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const handleProvinceChange = (province: string) => {
  // 简化处理，实际应该根据省份获取城市列表
  const cityMap: Record<string, string[]> = {
    '北京市': ['北京市'],
    '上海市': ['上海市'],
    '广东省': ['广州市', '深圳市', '珠海市', '佛山市', '东莞市'],
    '浙江省': ['杭州市', '宁波市', '温州市', '嘉兴市', '湖州市'],
    '江苏省': ['南京市', '苏州市', '无锡市', '常州市', '南通市']
  }
  
  cities.value = cityMap[province] || [province]
  formData.value.address.city = ''
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    
    submitLoading.value = true
    
    if (isEdit.value) {
      // 更新客户
      await customerApi.updateCustomer(props.customer!.id, formData.value)
      ElMessage.success('客户更新成功')
    } else {
      // 创建客户
      await customerApi.createCustomer(formData.value)
      ElMessage.success('客户创建成功')
    }
    
    emit('success')
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    submitLoading.value = false
  }
}

const handleClose = () => {
  emit('update:modelValue', false)
}
</script>

<style lang="scss" scoped>
.customer-form-dialog {
  .customer-form {
    max-height: 70vh;
    overflow-y: auto;
    padding-right: 10px;
  }

  .form-section {
    margin-bottom: 32px;
    padding: 20px;
    background: #fafafa;
    border-radius: 8px;

    .section-title {
      margin: 0 0 20px 0;
      font-size: 16px;
      font-weight: 600;
      color: #262626;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .customer-form-dialog {
    .customer-form {
      .form-section {
        padding: 16px;
      }
    }
  }
}
</style>