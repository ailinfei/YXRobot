<template>
  <div class="customer-order-history">
    <div class="order-header">
      <h4>订单历史</h4>
      <div class="header-actions">
        <el-select v-model="orderTypeFilter" placeholder="订单类型" size="small" @change="handleFilter">
          <el-option label="全部订单" value="" />
          <el-option label="销售订单" value="sales" />
          <el-option label="租赁订单" value="rental" />
        </el-select>
        <el-select v-model="statusFilter" placeholder="订单状态" size="small" @change="handleFilter">
          <el-option label="全部状态" value="" />
          <el-option label="待处理" value="pending" />
          <el-option label="进行中" value="processing" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
        <el-button size="small" @click="refreshOrders">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 订单统计 -->
    <div class="order-stats">
      <div class="stat-item">
        <div class="stat-value">{{ orderStats.total }}</div>
        <div class="stat-label">订单总数</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">¥{{ orderStats.totalAmount.toLocaleString() }}</div>
        <div class="stat-label">订单总额</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ orderStats.completed }}</div>
        <div class="stat-label">已完成</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ orderStats.processing }}</div>
        <div class="stat-label">进行中</div>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="order-table">
      <el-table :data="filteredOrders" v-loading="loading" stripe>
        <el-table-column prop="orderNumber" label="订单号" width="150" />
        <el-table-column prop="type" label="订单类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'sales' ? 'success' : 'primary'" size="small">
              {{ row.type === 'sales' ? '销售' : '租赁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="产品信息" width="200">
          <template #default="{ row }">
            <div class="product-info">
              <div class="product-name">{{ row.productName }}</div>
              <div class="product-details">
                <span class="quantity">数量: {{ row.quantity }}</span>
                <span class="model">型号: {{ row.productModel }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="订单金额" width="120">
          <template #default="{ row }">
            <div class="amount-display">
              <span class="amount">¥{{ row.amount.toLocaleString() }}</span>
              <span class="unit-price" v-if="row.quantity > 1">
                (¥{{ (row.amount / row.quantity).toFixed(2) }}/台)
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusTagType(row.status)" size="small">
              {{ getOrderStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="租赁信息" width="180" v-if="hasRentalOrders">
          <template #default="{ row }">
            <div v-if="row.type === 'rental'" class="rental-info">
              <div class="rental-period">
                <span class="label">租期:</span>
                <span class="value">{{ row.rentalDays }}天</span>
              </div>
              <div class="rental-dates">
                <div class="date-item">
                  <span class="label">开始:</span>
                  <span class="value">{{ formatDate(row.rentalStartDate) }}</span>
                </div>
                <div class="date-item">
                  <span class="label">结束:</span>
                  <span class="value">{{ formatDate(row.rentalEndDate) }}</span>
                </div>
              </div>
            </div>
            <span v-else class="not-applicable">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="viewOrderDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="orderList.length > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="orderList.length"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="orderDetailDialogVisible"
      title="订单详情"
      width="700px"
    >
      <div v-if="selectedOrder" class="order-detail">
        <div class="detail-section">
          <h5>基本信息</h5>
          <div class="detail-grid">
            <div class="detail-item">
              <label>订单号:</label>
              <span>{{ selectedOrder.orderNumber }}</span>
            </div>
            <div class="detail-item">
              <label>订单类型:</label>
              <el-tag :type="selectedOrder.type === 'sales' ? 'success' : 'primary'" size="small">
                {{ selectedOrder.type === 'sales' ? '销售订单' : '租赁订单' }}
              </el-tag>
            </div>
            <div class="detail-item">
              <label>订单状态:</label>
              <el-tag :type="getOrderStatusTagType(selectedOrder.status)" size="small">
                {{ getOrderStatusText(selectedOrder.status) }}
              </el-tag>
            </div>
            <div class="detail-item">
              <label>下单时间:</label>
              <span>{{ formatDate(selectedOrder.createdAt) }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h5>产品信息</h5>
          <div class="detail-grid">
            <div class="detail-item">
              <label>产品名称:</label>
              <span>{{ selectedOrder.productName }}</span>
            </div>
            <div class="detail-item">
              <label>产品型号:</label>
              <span>{{ selectedOrder.productModel }}</span>
            </div>
            <div class="detail-item">
              <label>购买数量:</label>
              <span>{{ selectedOrder.quantity }}台</span>
            </div>
            <div class="detail-item">
              <label>单价:</label>
              <span>¥{{ (selectedOrder.amount / selectedOrder.quantity).toFixed(2) }}</span>
            </div>
            <div class="detail-item">
              <label>订单总额:</label>
              <span class="amount-highlight">¥{{ selectedOrder.amount.toLocaleString() }}</span>
            </div>
          </div>
        </div>

        <div v-if="selectedOrder.type === 'rental'" class="detail-section">
          <h5>租赁信息</h5>
          <div class="detail-grid">
            <div class="detail-item">
              <label>租赁天数:</label>
              <span>{{ selectedOrder.rentalDays }}天</span>
            </div>
            <div class="detail-item">
              <label>日租金:</label>
              <span>¥{{ (selectedOrder.amount / selectedOrder.rentalDays).toFixed(2) }}/天</span>
            </div>
            <div class="detail-item">
              <label>开始日期:</label>
              <span>{{ formatDate(selectedOrder.rentalStartDate) }}</span>
            </div>
            <div class="detail-item">
              <label>结束日期:</label>
              <span>{{ formatDate(selectedOrder.rentalEndDate) }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section" v-if="selectedOrder.notes">
          <h5>备注信息</h5>
          <div class="notes-content">{{ selectedOrder.notes }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { customerRelationApi } from '@/api/customer'
import type { CustomerOrder } from '@/types/customer'

// Props
interface Props {
  customerId: string
  orders: CustomerOrder[]
}

const props = defineProps<Props>()

// 响应式数据
const loading = ref(false)
const orderList = ref<CustomerOrder[]>([])
const orderTypeFilter = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const orderDetailDialogVisible = ref(false)
const selectedOrder = ref<CustomerOrder | null>(null)

// 计算属性
const filteredOrders = computed(() => {
  let filtered = orderList.value

  if (orderTypeFilter.value) {
    filtered = filtered.filter(order => order.type === orderTypeFilter.value)
  }

  if (statusFilter.value) {
    filtered = filtered.filter(order => order.status === statusFilter.value)
  }

  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.slice(start, end)
})

const orderStats = computed(() => {
  const stats = {
    total: orderList.value.length,
    totalAmount: 0,
    completed: 0,
    processing: 0
  }

  orderList.value.forEach(order => {
    stats.totalAmount += order.amount
    if (order.status === 'completed') stats.completed++
    if (order.status === 'processing') stats.processing++
  })

  return stats
})

const hasRentalOrders = computed(() => {
  return orderList.value.some(order => order.type === 'rental')
})

// 方法
const loadOrders = async () => {
  loading.value = true
  try {
    const response = await customerRelationApi.getCustomerOrders(props.customerId)
    orderList.value = response.data.list || response.data
  } catch (error) {
    console.error('加载订单历史失败:', error)
    ElMessage.error('加载订单历史失败')
  } finally {
    loading.value = false
  }
}

const refreshOrders = () => {
  loadOrders()
}

const handleFilter = () => {
  currentPage.value = 1
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
}

const viewOrderDetail = (order: CustomerOrder) => {
  selectedOrder.value = order
  orderDetailDialogVisible.value = true
}

// 工具方法
const getOrderStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    pending: 'warning',
    processing: 'primary',
    completed: 'success',
    cancelled: 'danger'
  }
  return types[status] || 'info'
}

const getOrderStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待处理',
    processing: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return texts[status] || status
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  orderList.value = props.orders
})
</script>

<style lang="scss" scoped>
.customer-order-history {
  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h4 {
      margin: 0;
      color: #303133;
      font-size: 16px;
      font-weight: 600;
    }
    
    .header-actions {
      display: flex;
      gap: 8px;
      align-items: center;
    }
  }
  
  .order-stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 20px;
    
    .stat-item {
      text-align: center;
      padding: 16px;
      background: #F5F7FA;
      border-radius: 6px;
      
      .stat-value {
        font-size: 20px;
        font-weight: 700;
        color: #303133;
        margin-bottom: 4px;
      }
      
      .stat-label {
        font-size: 12px;
        color: #606266;
      }
    }
  }
  
  .order-table {
    .product-info {
      .product-name {
        font-weight: 500;
        color: #303133;
        margin-bottom: 4px;
      }
      
      .product-details {
        display: flex;
        gap: 12px;
        font-size: 12px;
        color: #909399;
      }
    }
    
    .amount-display {
      .amount {
        font-weight: 500;
        color: #303133;
        display: block;
        margin-bottom: 2px;
      }
      
      .unit-price {
        font-size: 12px;
        color: #909399;
      }
    }
    
    .rental-info {
      font-size: 12px;
      
      .rental-period {
        margin-bottom: 4px;
        
        .label {
          color: #909399;
          margin-right: 4px;
        }
        
        .value {
          color: #303133;
          font-weight: 500;
        }
      }
      
      .rental-dates {
        .date-item {
          display: flex;
          justify-content: space-between;
          margin-bottom: 2px;
          
          .label {
            color: #909399;
          }
          
          .value {
            color: #303133;
          }
        }
      }
    }
    
    .not-applicable {
      color: #C0C4CC;
      font-style: italic;
    }
    
    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }
  
  .order-detail {
    .detail-section {
      margin-bottom: 24px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      h5 {
        margin: 0 0 12px 0;
        color: #303133;
        font-size: 14px;
        font-weight: 600;
        border-bottom: 1px solid #EBEEF5;
        padding-bottom: 6px;
      }
      
      .detail-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 12px;
        
        .detail-item {
          display: flex;
          align-items: center;
          
          label {
            font-size: 13px;
            color: #606266;
            min-width: 80px;
            margin-right: 8px;
          }
          
          span {
            font-size: 13px;
            color: #303133;
            
            &.amount-highlight {
              font-weight: 600;
              color: #E6A23C;
            }
          }
        }
      }
      
      .notes-content {
        padding: 12px;
        background: #F5F7FA;
        border-radius: 4px;
        font-size: 13px;
        color: #303133;
        line-height: 1.5;
      }
    }
  }
}

@media (max-width: 768px) {
  .customer-order-history {
    .order-stats {
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
    }
    
    .order-header {
      flex-direction: column;
      gap: 12px;
      align-items: stretch;
      
      .header-actions {
        justify-content: center;
        flex-wrap: wrap;
      }
    }
    
    .order-detail {
      .detail-section {
        .detail-grid {
          grid-template-columns: 1fr;
          gap: 8px;
        }
      }
    }
  }
}
</style>