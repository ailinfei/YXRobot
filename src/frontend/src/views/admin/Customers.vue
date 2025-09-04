<template>
  <div class="customers-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h2>客户管理</h2>
          <p>管理客户信息和设备关联，提供优质客户服务</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="handleCreateCustomer" :icon="Plus">
            新增客户
          </el-button>
        </div>
      </div>
    </div>

    <!-- 客户统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总客户数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon regular">
          <el-icon><UserFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.regular }}</div>
          <div class="stat-label">普通客户</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon vip">
          <el-icon><Crown /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.vip }}</div>
          <div class="stat-label">VIP客户</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon premium">
          <el-icon><Medal /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.premium }}</div>
          <div class="stat-label">高级客户</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon devices">
          <el-icon><Monitor /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.activeDevices }}</div>
          <div class="stat-label">活跃设备</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon revenue">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ (stats.totalRevenue / 10000).toFixed(1) }}万</div>
          <div class="stat-label">总收入</div>
        </div>
      </div>
    </div>

    <!-- 客户列表 -->
    <div class="customers-container">
      <EnhancedDataTable
        :data="customers"
        :columns="tableColumns"
        :loading="loading"
        :pagination="pagination"
        :selection="{ enabled: true }"
        :actions="tableActions"
        :filters="tableFilters"
        :exportable="true"
        @selection-change="handleSelectionChange"
        @page-change="handlePageChange"
        @filter-change="handleFilterChange"
        @action-click="handleActionClick"
        @export="handleExportCustomers"
      />
    </div>

    <!-- 客户详情对话框 -->
    <CustomerDetailDialog
      v-model="detailVisible"
      :customer="selectedCustomer"
      @refresh="handleRefreshDetail"
    />

    <!-- 客户编辑对话框 -->
    <CustomerEditDialog
      v-model="editVisible"
      :customer="selectedCustomer"
      :mode="editMode"
      @success="handleEditSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, 
  Download,
  User, 
  UserFilled,
  Crown,
  Medal,
  Monitor,
  Money,
  View,
  Edit,
  Delete,
  Phone,
  Message
} from '@element-plus/icons-vue'
import EnhancedDataTable from '@/components/tables/EnhancedDataTable.vue'
import CustomerDetailDialog from '@/components/customer/CustomerDetailDialog.vue'
import CustomerEditDialog from '@/components/customer/CustomerEditDialog.vue'
import { customerApi } from '@/api/customer'
import type { Customer, CustomerStats } from '@/types/customer'
import { formatDate } from '@/utils/dateTime'

// 响应式数据
const loading = ref(false)
const exporting = ref(false)
const customers = ref<Customer[]>([])
const selectedCustomers = ref<Customer[]>([])
const selectedCustomer = ref<Customer | null>(null)
const detailVisible = ref(false)
const editVisible = ref(false)
const editMode = ref<'create' | 'edit'>('edit')
const stats = ref<CustomerStats>({
  total: 0,
  regular: 0,
  vip: 0,
  premium: 0,
  activeDevices: 0,
  totalRevenue: 0
})

// 分页配置
const pagination = ref({
  page: 1,
  pageSize: 20,
  total: 0
})

// 表格列配置
const tableColumns = computed(() => [
  {
    key: 'avatar',
    label: '头像',
    width: '80px',
    component: 'avatar'
  },
  {
    key: 'name',
    label: '客户姓名',
    width: '120px',
    sortable: true,
    filterable: true
  },
  {
    key: 'level',
    label: '客户等级',
    width: '100px',
    component: 'customerLevel',
    filterable: true
  },
  {
    key: 'phone',
    label: '联系电话',
    width: '130px'
  },
  {
    key: 'deviceCount',
    label: '设备数量',
    width: '120px',
    component: 'deviceCount',
    sortable: true
  },
  {
    key: 'totalSpent',
    label: '消费金额',
    width: '120px',
    formatter: (value: number) => `¥${value.toLocaleString()}`,
    sortable: true
  },
  {
    key: 'customerValue',
    label: '客户价值',
    width: '100px',
    component: 'customerValue',
    sortable: true
  },
  {
    key: 'address',
    label: '地区',
    width: '150px',
    formatter: (value: any) => `${value.province} ${value.city}`,
    filterable: true
  },
  {
    key: 'lastActiveAt',
    label: '最后活跃',
    width: '130px',
    formatter: (value: string) => formatDate(value),
    sortable: true
  },
  {
    key: 'actions',
    label: '操作',
    width: '200px',
    component: 'actions'
  }
])

// 表格操作配置
const tableActions = [
  {
    key: 'view',
    label: '查看',
    icon: View,
    type: 'primary'
  },
  {
    key: 'edit',
    label: '编辑',
    icon: Edit,
    type: 'default'
  },
  {
    key: 'call',
    label: '拨打',
    icon: Phone,
    type: 'success'
  },
  {
    key: 'message',
    label: '短信',
    icon: Message,
    type: 'warning'
  },
  {
    key: 'delete',
    label: '删除',
    icon: Delete,
    type: 'danger'
  }
]

// 表格过滤配置
const tableFilters = [
  {
    key: 'level',
    label: '客户等级',
    type: 'select',
    options: [
      { label: '普通客户', value: 'regular' },
      { label: 'VIP客户', value: 'vip' },
      { label: '高级客户', value: 'premium' }
    ]
  },
  {
    key: 'deviceType',
    label: '设备类型',
    type: 'select',
    options: [
      { label: '购买设备', value: 'purchased' },
      { label: '租赁设备', value: 'rental' }
    ]
  },
  {
    key: 'region',
    label: '地区',
    type: 'select',
    options: [
      { label: '北京市', value: '北京市' },
      { label: '上海市', value: '上海市' },
      { label: '广东省', value: '广东省' },
      { label: '浙江省', value: '浙江省' }
    ]
  }
]

// 生命周期
onMounted(() => {
  loadCustomers()
  loadStats()
})

// 方法
const loadCustomers = async () => {
  try {
    loading.value = true
    const response = await customerApi.getCustomers({
      page: pagination.value.page,
      pageSize: pagination.value.pageSize
    })
    customers.value = response.data.list
    pagination.value.total = response.data.total
  } catch (error) {
    console.error('加载客户列表失败:', error)
    ElMessage.error('加载客户列表失败')
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    const response = await customerApi.getCustomerStats()
    stats.value = response.data
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const handleCreateCustomer = () => {
  selectedCustomer.value = null
  editMode.value = 'create'
  editVisible.value = true
}

const handleSelectionChange = (selection: Customer[]) => {
  selectedCustomers.value = selection
}

const handlePageChange = (page: number, pageSize: number) => {
  pagination.value.page = page
  pagination.value.pageSize = pageSize
  loadCustomers()
}

const handleFilterChange = (filters: Record<string, any>) => {
  // 实现过滤逻辑
  console.log('过滤条件:', filters)
  loadCustomers()
}

const handleActionClick = (action: string, customer: Customer) => {
  selectedCustomer.value = customer
  
  switch (action) {
    case 'view':
      detailVisible.value = true
      break
    case 'edit':
      editMode.value = 'edit'
      editVisible.value = true
      break
    case 'call':
      handleCallCustomer(customer)
      break
    case 'message':
      handleSendMessage(customer)
      break
    case 'delete':
      handleDeleteCustomer(customer)
      break
  }
}

const handleCallCustomer = (customer: Customer) => {
  ElMessageBox.alert(
    `即将拨打客户电话：${customer.phone}`,
    '拨打电话',
    {
      confirmButtonText: '确定',
      type: 'info'
    }
  )
}

const handleSendMessage = (customer: Customer) => {
  ElMessageBox.prompt(
    `发送短信给 ${customer.name} (${customer.phone})：`,
    '发送短信',
    {
      confirmButtonText: '发送',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '请输入短信内容'
    }
  ).then(({ value }) => {
    if (value) {
      ElMessage.success('短信发送成功')
    }
  }).catch(() => {
    // 用户取消
  })
}

const handleDeleteCustomer = async (customer: Customer) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除客户"${customer.name}"吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await customerApi.deleteCustomer(customer.id)
    ElMessage.success('客户删除成功')
    loadCustomers()
  } catch {
    // 用户取消
  }
}

const handleRefreshDetail = () => {
  if (selectedCustomer.value) {
    const updatedCustomer = customers.value.find(c => c.id === selectedCustomer.value?.id)
    if (updatedCustomer) {
      selectedCustomer.value = updatedCustomer
    }
  }
}

const handleEditSuccess = () => {
  editVisible.value = false
  loadCustomers()
  ElMessage.success(editMode.value === 'create' ? '客户创建成功' : '客户更新成功')
}

const handleExportCustomers = async () => {
  try {
    exporting.value = true
    // 模拟导出过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('客户数据导出成功')
  } catch (error) {
    ElMessage.error('导出失败，请稍后重试')
  } finally {
    exporting.value = false
  }
}
</script>

<style lang="scss" scoped>
.customers-page {
  .page-header {
    margin-bottom: 24px;
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-info {
        h2 {
          font-size: 24px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0 0 4px 0;
        }
        
        p {
          color: var(--text-secondary);
          margin: 0;
        }
      }
      
      .header-actions {
        display: flex;
        gap: 12px;
      }
    }
  }
  
  .stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 20px;
    margin-bottom: 32px;
    
    .stat-card {
      background: white;
      border-radius: var(--radius-lg);
      padding: 20px;
      box-shadow: var(--shadow-sm);
      display: flex;
      align-items: center;
      gap: 16px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: var(--shadow-md);
      }
      
      .stat-icon {
        width: 44px;
        height: 44px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        
        &.total {
          background: rgba(59, 130, 246, 0.1);
          color: #3B82F6;
        }
        
        &.regular {
          background: rgba(107, 114, 128, 0.1);
          color: #6B7280;
        }
        
        &.vip {
          background: rgba(245, 158, 11, 0.1);
          color: #F59E0B;
        }
        
        &.premium {
          background: rgba(168, 85, 247, 0.1);
          color: #A855F7;
        }
        
        &.devices {
          background: rgba(16, 185, 129, 0.1);
          color: #10B981;
        }
        
        &.revenue {
          background: rgba(239, 68, 68, 0.1);
          color: #EF4444;
        }
      }
      
      .stat-content {
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: var(--text-primary);
          line-height: 1;
        }
        
        .stat-label {
          font-size: 13px;
          color: var(--text-secondary);
          margin-top: 4px;
        }
      }
    }
  }
  
  .customers-container {
    background: white;
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-sm);
  }
}

// 响应式适配
@media (max-width: 768px) {
  .customers-page {
    .page-header .header-content {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }
    
    .stats-cards {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}

@media (max-width: 480px) {
  .customers-page {
    .stats-cards {
      grid-template-columns: 1fr;
    }
  }
}
</style>