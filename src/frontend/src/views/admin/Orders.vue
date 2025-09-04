<template>
  <div class="orders-page">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-left">
        <h2>订单管理</h2>
        <p class="header-subtitle">销售和租赁订单管理 · 练字机器人管理系统</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showCreateDialog">
          <el-icon><Plus /></el-icon>
          新建订单
        </el-button>
      </div>
    </div>

    <!-- 订单统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><ShoppingCart /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ orderStats.total }}</div>
          <div class="stat-label">订单总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon revenue">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ orderStats.totalRevenue.toLocaleString() }}</div>
          <div class="stat-label">总收入</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon processing">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ orderStats.processing }}</div>
          <div class="stat-label">处理中</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon completed">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ orderStats.completed }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
    </div>
    <!-- 搜索和筛选 -->
    <div class="search-section">
      <el-form :inline="true" class="search-form">
        <el-form-item label="搜索">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索订单号、客户名称"
            clearable
            @clear="loadOrderData"
            @keyup.enter="loadOrderData"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="订单类型">
          <el-select v-model="typeFilter" placeholder="全部类型" clearable @change="loadOrderData" style="width: 120px">
            <el-option label="销售订单" value="sales" />
            <el-option label="租赁订单" value="rental" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="statusFilter" placeholder="全部状态" clearable @change="loadOrderData" style="width: 120px">
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="loadOrderData"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadOrderData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 订单列表表格 -->
    <div class="table-section">
      <el-table 
        :data="orderList" 
        v-loading="tableLoading" 
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNumber" label="订单号" width="150" />
        <el-table-column prop="type" label="订单类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'sales' ? 'success' : 'primary'" size="small">
              {{ row.type === 'sales' ? '销售' : '租赁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="客户信息" width="180">
          <template #default="{ row }">
            <div class="customer-info">
              <div class="customer-name">{{ row.customerName }}</div>
              <div class="customer-contact">{{ row.customerPhone }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="订单商品" width="200">
          <template #default="{ row }">
            <div class="order-items">
              <div v-for="item in row.items.slice(0, 2)" :key="item.id" class="item-info">
                <span class="item-name">{{ item.productName }}</span>
                <span class="item-quantity">x{{ item.quantity }}</span>
              </div>
              <div v-if="row.items.length > 2" class="more-items">
                +{{ row.items.length - 2 }}个商品
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            <div class="amount-display">
              <span class="amount">¥{{ row.totalAmount.toLocaleString() }}</span>
              <span class="currency">{{ row.currency }}</span>
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
        <el-table-column prop="paymentStatus" label="支付状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getPaymentStatusTagType(row.paymentStatus)" size="small">
              {{ getPaymentStatusText(row.paymentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="租赁信息" width="150" v-if="hasRentalOrders">
          <template #default="{ row }">
            <div v-if="row.type === 'rental'" class="rental-info">
              <div class="rental-period">{{ row.rentalDays }}天</div>
              <div class="rental-dates">
                {{ formatDate(row.rentalStartDate) }} 至<br>
                {{ formatDate(row.rentalEndDate) }}
              </div>
            </div>
            <span v-else class="not-applicable">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="viewOrderDetail(row)">
              查看详情
            </el-button>
            <el-button text type="primary" size="small" @click="editOrder(row)">
              编辑
            </el-button>
            <el-dropdown @command="(command) => handleOrderAction(command, row)" trigger="click">
              <el-button text type="primary" size="small">
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="confirm" v-if="row.status === 'pending'">
                    确认订单
                  </el-dropdown-item>
                  <el-dropdown-item command="ship" v-if="row.status === 'confirmed'">
                    发货
                  </el-dropdown-item>
                  <el-dropdown-item command="complete" v-if="row.status === 'delivered'">
                    完成订单
                  </el-dropdown-item>
                  <el-dropdown-item command="cancel" v-if="['pending', 'confirmed'].includes(row.status)">
                    取消订单
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    删除订单
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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

    <!-- 订单创建/编辑对话框 -->
    <OrderFormDialog
      v-model="createDialogVisible"
      :order="editingOrder"
      @success="handleOrderCreateSuccess"
    />

    <!-- 订单详情对话框 -->
    <OrderDetailDialog
      v-model="detailDialogVisible"
      :order="selectedOrder"
      @edit="handleEditFromDetail"
      @status-change="handleStatusChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  Plus,
  Refresh,
  ShoppingCart,
  Money,
  Clock,
  CircleCheck
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order'
import type { Order, OrderStats } from '@/types/order'
import { ORDER_STATUS_TEXT, ORDER_TYPE_TEXT, PAYMENT_STATUS_TEXT } from '@/types/order'
import OrderFormDialog from '@/components/order/OrderFormDialog.vue'
import OrderDetailDialog from '@/components/order/OrderDetailDialog.vue'

// 响应式数据
const refreshLoading = ref(false)
const tableLoading = ref(false)
const exportLoading = ref(false)

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 订单数据
const orderList = ref<Order[]>([])
const selectedOrders = ref<Order[]>([])
const selectedOrder = ref<Order | null>(null)
const orderStats = ref<OrderStats>({
  total: 0,
  pending: 0,
  processing: 0,
  completed: 0,
  cancelled: 0,
  totalRevenue: 0,
  averageOrderValue: 0,
  salesOrders: 0,
  rentalOrders: 0
})

// 搜索和筛选
const searchKeyword = ref('')
const typeFilter = ref('')
const statusFilter = ref('')
const dateRange = ref<[string, string] | null>(null)

// 弹窗状态
const detailDialogVisible = ref(false)
const createDialogVisible = ref(false)
const editingOrder = ref<Order | null>(null)

// 计算属性
const hasRentalOrders = computed(() => {
  return orderList.value.some(order => order.type === 'rental')
})

// 刷新数据
const refreshData = async () => {
  refreshLoading.value = true
  try {
    await loadOrderData()
    ElMessage.success('数据刷新成功')
  } finally {
    setTimeout(() => {
      refreshLoading.value = false
    }, 1000)
  }
}

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = ''
  typeFilter.value = ''
  statusFilter.value = ''
  dateRange.value = null
  currentPage.value = 1
  loadOrderData()
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadOrderData()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadOrderData()
}

// 选择处理
const handleSelectionChange = (selection: Order[]) => {
  selectedOrders.value = selection
}

// 加载订单数据
const loadOrderData = async () => {
  tableLoading.value = true
  try {
    const response = await orderApi.getOrders({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      type: typeFilter.value as any,
      status: statusFilter.value as any,
      dateRange: dateRange.value || undefined
    })
    
    orderList.value = response.data.list || []
    total.value = response.data.total || 0
    orderStats.value = response.data.stats || {
      total: 0,
      pending: 0,
      processing: 0,
      completed: 0,
      cancelled: 0,
      totalRevenue: 0,
      averageOrderValue: 0,
      salesOrders: 0,
      rentalOrders: 0
    }
  } catch (error) {
    console.error('加载订单数据失败:', error)
    ElMessage.error('加载订单数据失败')
  } finally {
    tableLoading.value = false
  }
}

// 查看订单详情
const viewOrderDetail = (order: Order) => {
  selectedOrder.value = order
  detailDialogVisible.value = true
}

// 编辑订单
const editOrder = (order: Order) => {
  editingOrder.value = order
  createDialogVisible.value = true
}

// 从详情页编辑
const handleEditFromDetail = (order: Order) => {
  detailDialogVisible.value = false
  editOrder(order)
}

// 显示创建弹窗
const showCreateDialog = () => {
  editingOrder.value = null
  createDialogVisible.value = true
}

// 订单创建/更新成功处理
const handleOrderCreateSuccess = () => {
  loadOrderData()
  editingOrder.value = null
}

// 订单操作处理
const handleOrderAction = async (command: string, order: Order) => {
  try {
    switch (command) {
      case 'confirm':
        await orderApi.updateOrderStatus(order.id, 'confirmed')
        ElMessage.success('订单已确认')
        break
      case 'ship':
        await orderApi.updateOrderStatus(order.id, 'shipped')
        ElMessage.success('订单已发货')
        break
      case 'complete':
        await orderApi.updateOrderStatus(order.id, 'completed')
        ElMessage.success('订单已完成')
        break
      case 'cancel':
        await ElMessageBox.confirm('确定要取消这个订单吗？', '确认取消', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await orderApi.updateOrderStatus(order.id, 'cancelled')
        ElMessage.success('订单已取消')
        break
      case 'delete':
        await ElMessageBox.confirm('确定要删除这个订单吗？此操作不可恢复。', '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await orderApi.deleteOrder(order.id)
        ElMessage.success('订单已删除')
        break
    }
    loadOrderData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('订单操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 批量操作处理
const handleBatchAction = async (command: string) => {
  const orderIds = selectedOrders.value.map(order => order.id)
  
  try {
    switch (command) {
      case 'confirm':
        await orderApi.batchUpdateOrderStatus(orderIds, 'confirmed')
        ElMessage.success(`已确认 ${orderIds.length} 个订单`)
        break
      case 'cancel':
        await ElMessageBox.confirm(`确定要取消选中的 ${orderIds.length} 个订单吗？`, '确认取消', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await orderApi.batchUpdateOrderStatus(orderIds, 'cancelled')
        ElMessage.success(`已取消 ${orderIds.length} 个订单`)
        break
      case 'delete':
        await ElMessageBox.confirm(`确定要删除选中的 ${orderIds.length} 个订单吗？此操作不可恢复。`, '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await orderApi.batchDeleteOrders(orderIds)
        ElMessage.success(`已删除 ${orderIds.length} 个订单`)
        break
    }
    loadOrderData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 状态变更处理
const handleStatusChange = () => {
  loadOrderData()
}

// 导出订单
const exportOrders = async () => {
  exportLoading.value = true
  try {
    await orderApi.exportOrders({
      keyword: searchKeyword.value,
      type: typeFilter.value as any,
      status: statusFilter.value as any,
      dateRange: dateRange.value || undefined
    })
    ElMessage.success('订单导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exportLoading.value = false
  }
}

// 工具方法
const getOrderStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    pending: 'warning',
    confirmed: 'primary',
    processing: 'primary',
    shipped: 'info',
    delivered: 'success',
    completed: 'success',
    cancelled: 'danger'
  }
  return types[status] || 'info'
}

const getOrderStatusText = (status: string) => {
  return ORDER_STATUS_TEXT[status as keyof typeof ORDER_STATUS_TEXT] || status
}

const getPaymentStatusTagType = (status?: string) => {
  const types: Record<string, any> = {
    pending: 'warning',
    paid: 'success',
    failed: 'danger',
    refunded: 'info'
  }
  return types[status || ''] || 'info'
}

const getPaymentStatusText = (status?: string) => {
  return PAYMENT_STATUS_TEXT[status as keyof typeof PAYMENT_STATUS_TEXT] || status || '未知'
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  loadOrderData()
})
</script>

<style lang="scss" scoped>
.orders-page {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
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
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 24px;

    .stat-card {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;

        &.total {
          background: linear-gradient(135deg, #409EFF, #66B1FF);
          color: white;
        }

        &.revenue {
          background: linear-gradient(135deg, #67C23A, #85CE61);
          color: white;
        }

        &.processing {
          background: linear-gradient(135deg, #E6A23C, #ELBF73);
          color: white;
        }

        &.completed {
          background: linear-gradient(135deg, #F56C6C, #F78989);
          color: white;
        }
      }

      .stat-content {
        flex: 1;

        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #303133;
          line-height: 1.2;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #606266;
        }
      }
    }
  }

  .action-buttons {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
  }

  .search-section {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    padding: 20px;
    margin-bottom: 20px;

    .search-form {
      .el-form-item {
        margin-bottom: 0;
        margin-right: 16px;
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
    display: flex;
    justify-content: space-between;
    align-items: center;

    .filters-left {
      display: flex;
      gap: 12px;
      align-items: center;
      flex-wrap: wrap;
    }

    .filters-right {
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
      .customer-name {
        font-weight: 500;
        color: #262626;
        margin-bottom: 4px;
      }

      .customer-contact {
        font-size: 12px;
        color: #8c8c8c;
      }
    }

    .order-items {
      .item-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 4px;

        .item-name {
          font-size: 13px;
          color: #262626;
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .item-quantity {
          font-size: 12px;
          color: #8c8c8c;
          margin-left: 8px;
        }
      }

      .more-items {
        font-size: 12px;
        color: #409EFF;
        cursor: pointer;
      }
    }

    .amount-display {
      .amount {
        font-weight: 500;
        color: #262626;
        display: block;
        margin-bottom: 2px;
      }

      .currency {
        font-size: 12px;
        color: #8c8c8c;
      }
    }

    .rental-info {
      font-size: 12px;

      .rental-period {
        font-weight: 500;
        color: #262626;
        margin-bottom: 4px;
      }

      .rental-dates {
        color: #8c8c8c;
        line-height: 1.4;
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
}

@media (max-width: 1200px) {
  .orders-page {
    .stats-cards {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}

@media (max-width: 768px) {
  .orders-page {
    padding: 16px;

    .page-header {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;

      .header-right {
        justify-content: center;
      }
    }

    .stats-cards {
      grid-template-columns: 1fr;
      gap: 16px;
    }

    .search-filters {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;

      .filters-left {
        justify-content: center;
      }

      .filters-right {
        justify-content: center;
      }
    }
  }
}
</style>