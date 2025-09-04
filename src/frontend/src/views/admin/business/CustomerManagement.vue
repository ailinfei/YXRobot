<template>
  <div class="customer-management">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-left">
        <h2>客户管理</h2>
        <p class="header-subtitle">客户信息管理 · 练字机器人管理系统</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showCreateDialog">
          <el-icon><Plus /></el-icon>
          新增客户
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filters">
      <div class="filters-left">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索客户姓名、电话、邮箱"
          :prefix-icon="Search"
          clearable
          @input="handleSearch"
          style="width: 300px;"
        />
        <el-select v-model="statusFilter" placeholder="客户状态" clearable @change="handleFilter">
          <el-option label="全部状态" value="" />
          <el-option label="活跃" value="active" />
          <el-option label="非活跃" value="inactive" />
          <el-option label="已暂停" value="suspended" />
        </el-select>
        <el-select v-model="levelFilter" placeholder="客户等级" clearable @change="handleFilter">
          <el-option label="全部等级" value="" />
          <el-option label="普通客户" value="regular" />
          <el-option label="VIP客户" value="vip" />
          <el-option label="高级客户" value="premium" />
        </el-select>
        <el-select v-model="deviceTypeFilter" placeholder="设备类型" clearable @change="handleFilter">
          <el-option label="全部类型" value="" />
          <el-option label="仅购买" value="purchased" />
          <el-option label="仅租赁" value="rental" />
          <el-option label="混合" value="mixed" />
        </el-select>
        <el-select v-model="regionFilter" placeholder="地区筛选" clearable @change="handleFilter">
          <el-option label="全部地区" value="" />
          <el-option label="北京市" value="北京市" />
          <el-option label="上海市" value="上海市" />
          <el-option label="广东省" value="广东省" />
          <el-option label="浙江省" value="浙江省" />
          <el-option label="江苏省" value="江苏省" />
        </el-select>
      </div>
    </div>

    <!-- 客户列表表格 -->
    <div class="table-section">
      <el-table 
        :data="customerList" 
        v-loading="tableLoading" 
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="客户信息" width="180">
          <template #default="{ row }">
            <div class="customer-info">
              <div class="customer-avatar">
                <el-avatar 
                  :src="row.avatar" 
                  :size="32"
                  :icon="UserFilled"
                />
              </div>
              <div class="customer-details">
                <div class="customer-name">
                  <span>{{ row.name }}</span>
                  <el-tag 
                    :type="getLevelTagType(row.level)"
                    size="small"
                    class="level-tag"
                  >
                    {{ getLevelText(row.level) }}
                  </el-tag>
                </div>
                <div class="customer-tags">
                  <el-tag 
                    v-for="tag in row.tags?.slice(0, 2)" 
                    :key="tag" 
                    size="small" 
                    type="info"
                    class="customer-tag"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip />
        <el-table-column prop="company" label="公司" width="150" show-overflow-tooltip />
        <el-table-column prop="address" label="地址" width="200" show-overflow-tooltip />
        <el-table-column label="设备统计" width="120">
          <template #default="{ row }">
            <div class="device-stats">
              <div class="device-count">
                <el-icon><Monitor /></el-icon>
                <span>{{ row.deviceCount?.total || 0 }}</span>
              </div>
              <div class="device-breakdown">
                <el-tag v-if="row.deviceCount?.purchased > 0" type="success" size="small">
                  购买{{ row.deviceCount.purchased }}
                </el-tag>
                <el-tag v-if="row.deviceCount?.rental > 0" type="warning" size="small">
                  租赁{{ row.deviceCount.rental }}
                </el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalSpent" label="消费金额" width="100">
          <template #default="{ row }">
            <div class="amount-display">
              <span class="amount">¥{{ row.totalSpent.toLocaleString() }}</span>
              <div class="value-indicator">
                <el-rate 
                  v-model="row.customerValue" 
                  :max="10" 
                  size="small" 
                  disabled 
                  show-score
                  text-color="#ff9900"
                />
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registrationDate" label="注册日期" width="110" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="viewCustomerDetail(row)">
              查看详情
            </el-button>
            <el-button text type="primary" size="small" @click="editCustomer(row)">
              编辑
            </el-button>
            <el-button text type="danger" size="small" @click="deleteCustomer(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 客户创建/编辑对话框 -->
    <CustomerFormDialog
      v-model="createDialogVisible"
      :customer="editingCustomer"
      @success="handleCustomerCreateSuccess"
    />

    <!-- 客户详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="客户详情"
      width="800px"
      :before-close="handleDetailClose"
    >
      <div v-if="selectedCustomer" class="customer-detail">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="info-grid">
            <div class="info-item">
              <label>客户姓名：</label>
              <span>{{ selectedCustomer.name }}</span>
            </div>
            <div class="info-item">
              <label>联系电话：</label>
              <span>{{ selectedCustomer.phone }}</span>
            </div>
            <div class="info-item">
              <label>邮箱地址：</label>
              <span>{{ selectedCustomer.email }}</span>
            </div>
            <div class="info-item">
              <label>公司名称：</label>
              <span>{{ selectedCustomer.company }}</span>
            </div>
            <div class="info-item">
              <label>客户等级：</label>
              <el-tag :type="getLevelTagType(selectedCustomer.customerLevel)">
                {{ getLevelText(selectedCustomer.customerLevel) }}
              </el-tag>
            </div>
            <div class="info-item">
              <label>客户状态：</label>
              <el-tag :type="getStatusTagType(selectedCustomer.status)">
                {{ getStatusText(selectedCustomer.status) }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 统计信息 -->
        <div class="detail-section">
          <h4>统计信息</h4>
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-value">{{ selectedCustomer.totalOrders }}</div>
              <div class="stat-label">总订单数</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">¥{{ selectedCustomer.totalSpent.toLocaleString() }}</div>
              <div class="stat-label">总消费金额</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ customerDevices.length }}</div>
              <div class="stat-label">关联设备数</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ selectedCustomer.registrationDate }}</div>
              <div class="stat-label">注册日期</div>
            </div>
          </div>
        </div>

        <!-- 设备列表 -->
        <div class="detail-section">
          <h4>关联设备</h4>
          <el-table :data="customerDevices" size="small">
            <el-table-column prop="deviceId" label="设备ID" width="120" />
            <el-table-column prop="deviceModel" label="设备型号" width="150" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getDeviceStatusTagType(row.status)" size="small">
                  {{ getDeviceStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="rentalStartDate" label="开始日期" width="110" />
            <el-table-column prop="rentalEndDate" label="结束日期" width="110" />
            <el-table-column prop="dailyRate" label="日租金" width="80">
              <template #default="{ row }">
                ¥{{ row.dailyRate }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  Plus,
  Search,
  Monitor,
  UserFilled
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { customerApi, customerRelationApi } from '@/api/customer'
import type { Customer, CustomerDevice } from '@/types/customer'
import CustomerFormDialog from '@/components/customer/CustomerFormDialog.vue'

// 响应式数据
const tableLoading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const levelFilter = ref('')
const deviceTypeFilter = ref('')
const regionFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 客户数据
const customerList = ref<Customer[]>([])
const selectedCustomers = ref<Customer[]>([])
const selectedCustomer = ref<Customer | null>(null)
const customerDevices = ref<CustomerDevice[]>([])

// 弹窗状态
const detailDialogVisible = ref(false)
const createDialogVisible = ref(false)
const editingCustomer = ref<Customer | null>(null)

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadCustomerData()
}

// 筛选处理
const handleFilter = () => {
  currentPage.value = 1
  loadCustomerData()
}



// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadCustomerData()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadCustomerData()
}

// 选择处理
const handleSelectionChange = (selection: Customer[]) => {
  selectedCustomers.value = selection
}

// 加载客户数据
const loadCustomerData = async () => {
  tableLoading.value = true
  try {
    const response = await customerApi.getCustomers({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      level: levelFilter.value,
      deviceType: deviceTypeFilter.value,
      region: regionFilter.value
    })
    
    customerList.value = response.data.list
    total.value = response.data.total
  } catch (error) {
    console.error('加载客户数据失败:', error)
    ElMessage.error('加载客户数据失败')
  } finally {
    tableLoading.value = false
  }
}

// 查看客户详情
const viewCustomerDetail = async (customer: Customer) => {
  selectedCustomer.value = customer
  
  try {
    const response = await customerRelationApi.getCustomerDevices(customer.id)
    customerDevices.value = response.data.list
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载客户设备失败:', error)
    ElMessage.error('加载客户设备失败')
  }
}

// 编辑客户
const editCustomer = (customer: Customer) => {
  editingCustomer.value = customer
  createDialogVisible.value = true
}

// 删除客户
const deleteCustomer = async (customer: Customer) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除客户 "${customer.name}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await customerApi.deleteCustomer(customer.id)
    ElMessage.success('删除成功')
    loadCustomerData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除客户失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 显示创建弹窗
const showCreateDialog = () => {
  editingCustomer.value = null
  createDialogVisible.value = true
}

// 客户创建/更新成功处理
const handleCustomerCreateSuccess = () => {
  loadCustomerData()
  editingCustomer.value = null
}



// 关闭详情弹窗
const handleDetailClose = () => {
  detailDialogVisible.value = false
  selectedCustomer.value = null
  customerDevices.value = []
}

// 获取等级标签类型
const getLevelTagType = (level: string) => {
  const types: Record<string, any> = {
    regular: '',
    vip: 'warning',
    premium: 'success'
  }
  return types[level] || ''
}

// 获取等级文字
const getLevelText = (level: string) => {
  const texts: Record<string, string> = {
    regular: '普通',
    vip: 'VIP',
    premium: '高级'
  }
  return texts[level] || level
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    active: 'success',
    inactive: 'warning',
    suspended: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文字
const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    active: '活跃',
    inactive: '非活跃',
    suspended: '已暂停'
  }
  return texts[status] || status
}

// 获取设备状态标签类型
const getDeviceStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    active: 'success',
    expired: 'warning',
    returned: 'info'
  }
  return types[status] || 'info'
}

// 获取设备状态文字
const getDeviceStatusText = (status: string) => {
  const texts: Record<string, string> = {
    active: '使用中',
    expired: '已过期',
    returned: '已归还'
  }
  return texts[status] || status
}

onMounted(() => {
  loadCustomerData()
})
</script>

<style lang="scss" scoped>
.customer-management {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;
  width: 100%;
  box-sizing: border-box;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    width: 100%;
    
    .header-left {
      flex: 1;
      min-width: 0;
      margin-right: 20px;
      
      h2 {
        margin: 0 0 4px 0;
        color: #262626;
        font-size: 24px;
        font-weight: 600;
      }
      
      .header-subtitle {
        margin: 0;
        color: #8c8c8c;
        font-size: 14px;
        font-weight: 400;
      }
    }
    
    .header-right {
      flex-shrink: 0;
      
      .el-button {
        white-space: nowrap;
      }
    }
  }

  .search-filters {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    padding: 20px;
    margin-bottom: 20px;

    .filters-left {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }

  .table-section {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    padding: 20px;

    .customer-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .customer-avatar {
        flex-shrink: 0;
      }

      .customer-details {
        flex: 1;
        min-width: 0;

        .customer-name {
          display: flex;
          align-items: center;
          gap: 6px;
          margin-bottom: 4px;

          span {
            font-weight: 500;
            color: #262626;
          }

          .level-tag {
            font-size: 10px;
            height: 16px;
            line-height: 14px;
            padding: 0 4px;
          }
        }

        .customer-tags {
          display: flex;
          gap: 4px;
          flex-wrap: wrap;

          .customer-tag {
            font-size: 10px;
            height: 16px;
            line-height: 14px;
            padding: 0 4px;
          }
        }
      }
    }

    .device-stats {
      .device-count {
        display: flex;
        align-items: center;
        gap: 4px;
        margin-bottom: 4px;
        font-weight: 500;
        color: #262626;

        .el-icon {
          color: #1890ff;
        }
      }

      .device-breakdown {
        display: flex;
        gap: 4px;
        flex-wrap: wrap;

        .el-tag {
          font-size: 10px;
          height: 16px;
          line-height: 14px;
          padding: 0 4px;
        }
      }
    }

    .amount-display {
      .amount {
        display: block;
        font-weight: 500;
        color: #262626;
        margin-bottom: 4px;
      }

      .value-indicator {
        .el-rate {
          height: 16px;
          
          :deep(.el-rate__item) {
            margin-right: 2px;
          }
          
          :deep(.el-rate__icon) {
            font-size: 12px;
          }
        }
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }

  .customer-detail {
    .detail-section {
      margin-bottom: 24px;

      &:last-child {
        margin-bottom: 0;
      }

      h4 {
        margin: 0 0 16px 0;
        color: #262626;
        font-size: 16px;
        font-weight: 600;
        border-bottom: 1px solid #f0f0f0;
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
            font-weight: 500;
            color: #8c8c8c;
            min-width: 80px;
            margin-right: 8px;
          }

          span {
            color: #262626;
          }
        }
      }

      .stats-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 16px;

        .stat-card {
          text-align: center;
          padding: 16px;
          background: #f8f9fa;
          border-radius: 6px;

          .stat-value {
            font-size: 20px;
            font-weight: 600;
            color: #262626;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 12px;
            color: #8c8c8c;
          }
        }
      }
    }
  }
}
</style>