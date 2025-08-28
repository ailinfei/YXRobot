<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑订单' : '创建销售订单'"
    width="900px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      label-position="left"
    >
      <!-- 基本信息 -->
      <div class="form-section">
        <h4 class="section-title">基本信息</h4>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="订单类型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择订单类型" style="width: 100%">
                <el-option label="销售订单" value="sales" />
                <el-option label="租赁订单" value="rental" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单号" prop="orderNumber">
              <el-input
                v-model="formData.orderNumber"
                placeholder="系统自动生成"
                :disabled="true"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 客户信息 -->
      <div class="form-section">
        <h4 class="section-title">客户信息</h4>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="客户选择" prop="customerId">
              <el-select
                v-model="formData.customerId"
                placeholder="请选择客户"
                filterable
                remote
                :remote-method="searchCustomers"
                :loading="customerLoading"
                style="width: 100%"
                @change="handleCustomerChange"
              >
                <el-option
                  v-for="customer in customerOptions"
                  :key="customer.id"
                  :label="`${customer.name} (${customer.phone})`"
                  :value="customer.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="customerName">
              <el-input
                v-model="formData.customerName"
                placeholder="请输入客户姓名"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="customerPhone">
              <el-input
                v-model="formData.customerPhone"
                placeholder="请输入联系电话"
                maxlength="20"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户邮箱" prop="customerEmail">
              <el-input
                v-model="formData.customerEmail"
                placeholder="请输入客户邮箱"
                maxlength="100"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="收货地址" prop="deliveryAddress">
              <el-input
                v-model="formData.deliveryAddress"
                type="textarea"
                :rows="2"
                placeholder="请输入详细的收货地址"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 商品信息 -->
      <div class="form-section">
        <h4 class="section-title">
          商品信息
          <el-button type="primary" size="small" @click="addOrderItem">
            <el-icon><Plus /></el-icon>
            添加商品
          </el-button>
        </h4>
        
        <div class="order-items">
          <div
            v-for="(item, index) in formData.items"
            :key="index"
            class="order-item"
          >
            <el-row :gutter="16" align="middle">
              <el-col :span="6">
                <el-form-item
                  :prop="`items.${index}.productId`"
                  :rules="[{ required: true, message: '请选择产品', trigger: 'change' }]"
                  label="产品型号"
                >
                  <el-select
                    v-model="item.productId"
                    placeholder="请选择产品"
                    filterable
                    style="width: 100%"
                    @change="(value) => handleProductChange(value, index)"
                  >
                    <el-option
                      v-for="product in productOptions"
                      :key="product.id"
                      :label="product.name"
                      :value="product.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="3">
                <el-form-item
                  :prop="`items.${index}.quantity`"
                  :rules="[{ required: true, message: '请输入数量', trigger: 'blur' }]"
                  label="数量"
                >
                  <el-input-number
                    v-model="item.quantity"
                    :min="1"
                    :max="999"
                    style="width: 100%"
                    @change="() => calculateItemTotal(index)"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item
                  :prop="`items.${index}.unitPrice`"
                  :rules="[{ required: true, message: '请输入单价', trigger: 'blur' }]"
                  label="单价"
                >
                  <el-input-number
                    v-model="item.unitPrice"
                    :min="0"
                    :precision="2"
                    style="width: 100%"
                    @change="() => calculateItemTotal(index)"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item label="小计">
                  <el-input
                    :value="`¥${item.totalPrice?.toFixed(2) || '0.00'}`"
                    disabled
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="备注">
                  <el-input
                    v-model="item.notes"
                    placeholder="商品备注"
                    maxlength="100"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="2">
                <el-form-item label=" ">
                  <el-button
                    type="danger"
                    size="small"
                    text
                    @click="removeOrderItem(index)"
                    :disabled="formData.items.length <= 1"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>

      <!-- 租赁信息 (仅租赁订单显示) -->
      <div class="form-section" v-if="formData.type === 'rental'">
        <h4 class="section-title">租赁信息</h4>
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="租赁开始日期" prop="rentalStartDate">
              <el-date-picker
                v-model="formData.rentalStartDate"
                type="date"
                placeholder="选择开始日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                @change="calculateRentalDays"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="租赁结束日期" prop="rentalEndDate">
              <el-date-picker
                v-model="formData.rentalEndDate"
                type="date"
                placeholder="选择结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                @change="calculateRentalDays"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="租赁天数">
              <el-input
                :value="`${formData.rentalDays || 0} 天`"
                disabled
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="租赁备注">
              <el-input
                v-model="formData.rentalNotes"
                type="textarea"
                :rows="2"
                placeholder="租赁相关备注信息"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 订单金额 -->
      <div class="form-section">
        <h4 class="section-title">订单金额</h4>
        <div class="amount-summary">
          <el-row :gutter="24">
            <el-col :span="6">
              <div class="amount-item">
                <span class="label">商品小计:</span>
                <span class="value">¥{{ subtotal.toFixed(2) }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="amount-item">
                <span class="label">运费:</span>
                <el-input-number
                  v-model="formData.shippingFee"
                  :min="0"
                  :precision="2"
                  size="small"
                  @change="calculateTotal"
                />
              </div>
            </el-col>
            <el-col :span="6">
              <div class="amount-item">
                <span class="label">折扣:</span>
                <el-input-number
                  v-model="formData.discount"
                  :min="0"
                  :precision="2"
                  size="small"
                  @change="calculateTotal"
                />
              </div>
            </el-col>
            <el-col :span="6">
              <div class="amount-item total">
                <span class="label">订单总额:</span>
                <span class="value">¥{{ formData.totalAmount.toFixed(2) }}</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 其他信息 -->
      <div class="form-section">
        <h4 class="section-title">其他信息</h4>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="预计交付日期" prop="expectedDeliveryDate">
              <el-date-picker
                v-model="formData.expectedDeliveryDate"
                type="date"
                placeholder="选择预计交付日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售人员" prop="salesPerson">
              <el-input
                v-model="formData.salesPerson"
                placeholder="请输入销售人员姓名"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="订单备注">
              <el-input
                v-model="formData.notes"
                type="textarea"
                :rows="3"
                placeholder="订单相关备注信息"
                maxlength="500"
                show-word-limit
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
          {{ isEdit ? '更新订单' : '创建订单' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { mockOrderAPI } from '@/api/mock/order'
import { mockCustomerAPI } from '@/api/mock/customer'  
import { mockProductAPI } from '@/api/mock/product'
import type { Order, OrderItem } from '@/types/order'
import type { Customer } from '@/types/customer'
import type { Product } from '@/types/product'

interface Props {
  modelValue: boolean
  order?: Order | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式数据
const formRef = ref()
const submitLoading = ref(false)
const customerLoading = ref(false)
const customerOptions = ref<Customer[]>([])
const productOptions = ref<Product[]>([])

// 表单数据
const formData = ref<Partial<Order> & { items: OrderItem[] }>({
  type: 'sales',
  orderNumber: '',
  customerId: '',
  customerName: '',
  customerPhone: '',
  customerEmail: '',
  deliveryAddress: '',
  items: [
    {
      productId: '',
      productName: '',
      quantity: 1,
      unitPrice: 0,
      totalPrice: 0,
      notes: ''
    }
  ],
  shippingFee: 0,
  discount: 0,
  totalAmount: 0,
  expectedDeliveryDate: '',
  salesPerson: '',
  notes: '',
  rentalStartDate: '',
  rentalEndDate: '',
  rentalDays: 0,
  rentalNotes: ''
})

// 表单验证规则
const formRules = {
  type: [{ required: true, message: '请选择订单类型', trigger: 'change' }],
  customerName: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  customerPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  customerEmail: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  deliveryAddress: [{ required: true, message: '请输入收货地址', trigger: 'blur' }],
  rentalStartDate: [
    { required: true, message: '请选择租赁开始日期', trigger: 'change' }
  ],
  rentalEndDate: [
    { required: true, message: '请选择租赁结束日期', trigger: 'change' }
  ]
}

// 计算属性
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isEdit = computed(() => !!props.order)

const subtotal = computed(() => {
  return formData.value.items.reduce((sum, item) => sum + (item.totalPrice || 0), 0)
})

// 方法定义（需要在监听器之前定义）
const resetForm = () => {
  formData.value = {
    type: 'sales',
    orderNumber: '',
    customerId: '',
    customerName: '',
    customerPhone: '',
    customerEmail: '',
    deliveryAddress: '',
    items: [
      {
        productId: '',
        productName: '',
        quantity: 1,
        unitPrice: 0,
        totalPrice: 0,
        notes: ''
      }
    ],
    shippingFee: 0,
    discount: 0,
    totalAmount: 0,
    expectedDeliveryDate: '',
    salesPerson: '',
    notes: '',
    rentalStartDate: '',
    rentalEndDate: '',
    rentalDays: 0,
    rentalNotes: ''
  }
}

const generateOrderNumber = () => {
  const now = new Date()
  const timestamp = now.getTime().toString().slice(-8)
  const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0')
  formData.value.orderNumber = `ORD${timestamp}${random}`
}

// 监听器
watch(() => props.order, (newOrder) => {
  if (newOrder) {
    // 编辑模式，填充表单数据
    Object.assign(formData.value, {
      ...newOrder,
      items: newOrder.items || [
        {
          productId: '',
          productName: '',
          quantity: 1,
          unitPrice: 0,
          totalPrice: 0,
          notes: ''
        }
      ]
    })
  } else {
    // 新建模式，重置表单
    resetForm()
    generateOrderNumber()
  }
}, { immediate: true })

watch(() => formData.value.type, (newType) => {
  if (newType === 'rental') {
    // 租赁订单需要验证租赁日期
    formRules.rentalStartDate = [
      { required: true, message: '请选择租赁开始日期', trigger: 'change' }
    ]
    formRules.rentalEndDate = [
      { required: true, message: '请选择租赁结束日期', trigger: 'change' }
    ]
  }
})

// 其他方法

const loadProductOptions = async () => {
  try {
    const response = await mockProductAPI.getProducts({ pageSize: 100 })
    productOptions.value = response.data.list
  } catch (error) {
    console.error('加载产品列表失败:', error)
  }
}

const searchCustomers = async (query: string) => {
  if (!query) {
    customerOptions.value = []
    return
  }
  
  customerLoading.value = true
  try {
    const response = await mockCustomerAPI.getCustomers({
      keyword: query,
      pageSize: 20
    })
    customerOptions.value = response.data.list
  } catch (error) {
    console.error('搜索客户失败:', error)
  } finally {
    customerLoading.value = false
  }
}

const handleCustomerChange = (customerId: string) => {
  const customer = customerOptions.value.find(c => c.id === customerId)
  if (customer) {
    formData.value.customerName = customer.name
    formData.value.customerPhone = customer.phone
    formData.value.customerEmail = customer.email
    formData.value.deliveryAddress = customer.address
  }
}

const handleProductChange = (productId: string, index: number) => {
  const product = productOptions.value.find(p => p.id === productId)
  if (product) {
    formData.value.items[index].productName = product.name
    formData.value.items[index].unitPrice = product.price
    calculateItemTotal(index)
  }
}

const addOrderItem = () => {
  formData.value.items.push({
    productId: '',
    productName: '',
    quantity: 1,
    unitPrice: 0,
    totalPrice: 0,
    notes: ''
  })
}

const removeOrderItem = (index: number) => {
  if (formData.value.items.length > 1) {
    formData.value.items.splice(index, 1)
    calculateTotal()
  }
}

const calculateItemTotal = (index: number) => {
  const item = formData.value.items[index]
  item.totalPrice = (item.quantity || 0) * (item.unitPrice || 0)
  calculateTotal()
}

const calculateTotal = () => {
  const itemsTotal = subtotal.value
  const shipping = formData.value.shippingFee || 0
  const discount = formData.value.discount || 0
  formData.value.totalAmount = Math.max(0, itemsTotal + shipping - discount)
}

const calculateRentalDays = () => {
  if (formData.value.rentalStartDate && formData.value.rentalEndDate) {
    const startDate = new Date(formData.value.rentalStartDate)
    const endDate = new Date(formData.value.rentalEndDate)
    const diffTime = endDate.getTime() - startDate.getTime()
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    formData.value.rentalDays = Math.max(0, diffDays)
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    submitLoading.value = true
    
    if (isEdit.value) {
      await mockOrderAPI.updateOrder(props.order!.id, formData.value)
      ElMessage.success('订单更新成功')
    } else {
      await mockOrderAPI.createOrder(formData.value)
      ElMessage.success('订单创建成功')
    }
    
    emit('success')
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
    if (error !== false) { // 不是表单验证错误
      ElMessage.error('操作失败，请重试')
    }
  } finally {
    submitLoading.value = false
  }
}

const handleClose = () => {
  dialogVisible.value = false
  nextTick(() => {
    formRef.value?.resetFields()
  })
}

// 初始化
const init = async () => {
  await loadProductOptions()
  if (!isEdit.value) {
    generateOrderNumber()
  }
}

watch(dialogVisible, (visible) => {
  if (visible) {
    init()
  }
})
</script>

<style lang="scss" scoped>
.form-section {
  margin-bottom: 32px;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  .section-title {
    margin: 0 0 16px 0;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    border-bottom: 2px solid #f0f0f0;
    padding-bottom: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.order-items {
  .order-item {
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
    margin-bottom: 16px;
    border: 1px solid #e9ecef;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.amount-summary {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid #e9ecef;
  
  .amount-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .label {
      font-size: 14px;
      color: #606266;
      margin-right: 8px;
    }
    
    .value {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }
    
    &.total {
      padding-top: 12px;
      border-top: 1px solid #e9ecef;
      
      .label {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
      
      .value {
        font-size: 18px;
        font-weight: 700;
        color: #409EFF;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input-number) {
  width: 100%;
}
</style>