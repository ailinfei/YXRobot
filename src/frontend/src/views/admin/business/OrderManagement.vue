<template>
  <div class="order-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>订单管理</h2>
        <p class="header-subtitle">销售订单和租赁订单统一管理</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleCreateOrder">
          <el-icon><Plus /></el-icon>
          创建订单
        </el-button>
      </div>
    </div>

    <!-- 订单统计 -->
    <div class="order-stats">
      <div class="stat-card">
        <div class="stat-icon sales">
          <el-icon><ShoppingBag /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ orderStats.salesOrders }}</div>
          <div class="stat-label">销售订单</div>
          <div class="stat-amount">¥{{ (orderStats.totalRevenue * 0.7).toLocaleString() }}</div>
        </div>
      </div>
      
      <div class="stat-card rental">
        <div class="stat-icon">
          <el-icon><Timer /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ orderStats.rentalOrders }}</div>
          <div class="stat-label">租赁订单</div>
          <div class="stat-amount">¥{{ (orderStats.totalRevenue * 0.3).toLocaleString() }}</div>
        </div>
      </div>
      
      <div class="stat-card pending">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ orderStats.pending }}</div>
          <div class="stat-label">待处理</div>
        </div>
      </div>
      
      <div class="stat-card completed">
        <div class="stat-icon">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ orderStats.completed }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-controls">
        <el-select v-model="typeFilter" placeholder="订单类型" @change="handleFilter">
          <el-option label="全部类型" value="" />
          <el-option label="销售订单" value="sales" />
          <el-option label="租赁订单" value="rental" />
        </el-select>
        
        <el-select v-model="statusFilter" placeholder="订单状态" @change="handleFilter">
          <el-option label="全部状态" value="" />
          <el-option label="待付款" value="pending" />
          <el-option label="已付款" value="paid" />
          <el-option label="已发货" value="shipped" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
        
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleFilter"
        />
        
        <el-input
          v-model="searchKeyword"
          placeholder="搜索订单号或客户"
          :prefix-icon="Search"
          clearable
          @input="handleSearch"
        />
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders-table">
      <el-table
        :data="filteredOrders"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        row-key="id"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNumber" label="订单号" width="150" sortable />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="客户" width="120" />
        <el-table-column prop="items" label="产品" min-width="200">
          <template #default="{ row }">
            <div class="products-cell">
              <div v-for="item in row.items.slice(0, 2)" :key="item.id" class="product-item">
                {{ item.productName }} ×{{ item.quantity }}
              </div>
              <div v-if="row.items.length > 2" class="more-products">
                +{{ row.items.length - 2 }}个商品
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="120" sortable>
          <template #default="{ row }">
            ¥{{ row.totalAmount.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="150" sortable />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text @click="handleViewOrder(row)">
              查看
            </el-button>
            <el-button size="small" text @click="handleEditOrder(row)">
              编辑
            </el-button>
            <el-dropdown @command="(cmd) => handleOrderAction(cmd, row)">
              <el-button size="small" text>
                更多<el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="ship" :disabled="row.status !== 'paid'">
                    发货
                  </el-dropdown-item>
                  <el-dropdown-item command="complete" :disabled="row.status !== 'shipped'">
                    完成
                  </el-dropdown-item>
                  <el-dropdown-item command="cancel" :disabled="!canCancel(row)">
                    取消
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 批量操作 -->
    <div v-if="selectedOrders.length > 0" class="batch-actions">
      <div class="selected-info">
        已选择 {{ selectedOrders.length }} 个订单
      </div>
      <div class="batch-buttons">
        <el-button size="small" @click="handleBatchShip">批量发货</el-button>
        <el-button size="small" @click="handleBatchCancel" type="danger">批量取消</el-button>
        <el-button size="small" @click="clearSelection">取消选择</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  ShoppingBag,
  Timer,
  Clock,
  CircleCheck,
  ArrowDown
} from '@element-plus/icons-vue'
import { orderApi } from '@/api/order'
import type { Order, OrderStats } from '@/types/order'

// 接口定义 - 使用统一的类型定义

// 响应式数据
const loading = ref(false)
const typeFilter = ref('')
const statusFilter = ref('')
const dateRange = ref<[string, string] | null>(null)
const searchKeyword = ref('')
const selectedOrders = ref<string[]>([])

// 订单数据
const orders = ref<Order[]>([])
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

// 计算属性
const filteredOrders = computed(() => {
  let result = [...orders.value]
  
  if (typeFilter.value) {
    result = result.filter(order => order.type === typeFilter.value)
  }
  
  if (statusFilter.value) {
    result = result.filter(order => order.status === statusFilter.value)
  }
  
  if (dateRange.value) {
    const [start, end] = dateRange.value
    result = result.filter(order => {
      const orderDate = new Date(order.createdAt).getTime()
      return orderDate >= new Date(start).getTime() && orderDate <= new Date(end).getTime()
    })
  }
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(order =>
      order.orderNumber.toLowerCase().includes(keyword) ||
      order.customerName.toLowerCase().includes(keyword) ||
      order.customerPhone.includes(keyword)
    )
  }
  
  return result
})

// 方法
const loadOrders = async () => {
  loading.value = true
  try {
    // 调用真实API获取订单数据
    const response = await orderApi.getOrders({
      keyword: searchKeyword.value,
      type: typeFilter.value as any,
      status: statusFilter.value as any,
      dateRange: dateRange.value || undefined
    })
    
    orders.value = response.data.list || []
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
    console.error('加载订单列表失败:', error)
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

// 统计数据现在从API直接获取，不需要本地计算

const handleFilter = () => {
  // 重新加载数据
  loadOrders()
}

const handleSearch = () => {
  // 重新加载数据
  loadOrders()
}

const handleSelectionChange = (selection: Order[]) => {
  selectedOrders.value = selection.map(order => order.id)
}

const clearSelection = () => {
  selectedOrders.value = []
}

const handleCreateOrder = () => {
  ElMessage.info('创建订单功能开发中...')
}

const handleViewOrder = (order: Order) => {
  ElMessage.info(`查看订单: ${order.orderNumber}`)
}

const handleEditOrder = (order: Order) => {
  ElMessage.info(`编辑订单: ${order.orderNumber}`)
}

const handleOrderAction = async (command: string, order: Order) => {
  switch (command) {
    case 'ship':
      await handleShipOrder(order)
      break
    case 'complete':
      await handleCompleteOrder(order)
      break
    case 'cancel':
      await handleCancelOrder(order)
      break
  }
}

const handleShipOrder = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要发货这个订单吗？', '确认发货')
    await orderApi.updateOrderStatus(order.id, 'shipped')
    ElMessage.success('订单已发货')
    loadOrders() // 重新加载数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发货失败:', error)
      ElMessage.error('发货失败')
    }
  }
}

const handleCompleteOrder = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要完成这个订单吗？', '确认完成')
    await orderApi.updateOrderStatus(order.id, 'completed')
    ElMessage.success('订单已完成')
    loadOrders() // 重新加载数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成订单失败:', error)
      ElMessage.error('完成订单失败')
    }
  }
}

const handleCancelOrder = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '确认取消', {
      type: 'warning'
    })
    await orderApi.updateOrderStatus(order.id, 'cancelled')
    ElMessage.success('订单已取消')
    loadOrders() // 重新加载数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

const handleBatchShip = async () => {
  if (selectedOrders.value.length === 0) return
  
  try {
    await ElMessageBox.confirm(`确定要批量发货 ${selectedOrders.value.length} 个订单吗？`, '确认批量发货')
    
    await orderApi.batchUpdateOrderStatus(selectedOrders.value, 'shipped')
    
    ElMessage.success('批量发货成功')
    selectedOrders.value = []
    loadOrders() // 重新加载数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量发货失败:', error)
      ElMessage.error('批量发货失败')
    }
  }
}

const handleBatchCancel = async () => {
  if (selectedOrders.value.length === 0) return
  
  try {
    await ElMessageBox.confirm(`确定要批量取消 ${selectedOrders.value.length} 个订单吗？`, '确认批量取消', {
      type: 'warning'
    })
    
    await orderApi.batchUpdateOrderStatus(selectedOrders.value, 'cancelled')
    
    ElMessage.success('批量取消成功')
    selectedOrders.value = []
    loadOrders() // 重新加载数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量取消失败:', error)
      ElMessage.error('批量取消失败')
    }
  }
}

// 工具方法
const getTypeTagType = (type: string) => {
  return type === 'sales' ? 'success' : 'warning'
}

const getTypeText = (type: string) => {
  return type === 'sales' ? '销售' : '租赁'
}

const getStatusTagType = (status: string) => {
  const typeMap = {
    pending: 'info',
    paid: 'warning',
    shipped: 'primary',
    completed: 'success',
    cancelled: 'danger'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

const getStatusText = (status: string) => {
  const textMap = {
    pending: '待付款',
    paid: '已付款',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return textMap[status as keyof typeof textMap] || status
}

const canCancel = (order: Order) => {
  return ['pending', 'paid'].includes(order.status)
}

// 生命周期
onMounted(() => {
  loadOrders()
})
</script>

<style lang="scss" scoped>
.order-management {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
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

  .order-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    margin-bottom: 24px;

    .stat-card {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        color: white;

        &.sales {
          background: linear-gradient(135deg, #67C23A, #85CE61);
        }

        &.rental {
          background: linear-gradient(135deg, #E6A23C, #EEBE77);
        }

        &.pending {
          background: linear-gradient(135deg, #409EFF, #66B1FF);
        }

        &.completed {
          background: linear-gradient(135deg, #F56C6C, #F78989);
        }
      }

      .stat-content {
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #303133;
          line-height: 1.2;
        }

        .stat-label {
          font-size: 12px;
          color: #909399;
          margin: 4px 0;
        }

        .stat-amount {
          font-size: 14px;
          font-weight: 600;
          color: #409EFF;
        }
      }
    }
  }

  .filter-section {
    margin-bottom: 20px;
    padding: 16px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .filter-controls {
      display: flex;
      gap: 12px;
      align-items: center;
      flex-wrap: wrap;
    }
  }

  .orders-table {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .products-cell {
      .product-item {
        font-size: 12px;
        color: #606266;
        margin-bottom: 2px;
      }

      .more-products {
        font-size: 11px;
        color: #909399;
        font-style: italic;
      }
    }
  }

  .batch-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 16px;
    padding: 12px 16px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .selected-info {
      color: #606266;
      font-size: 14px;
    }

    .batch-buttons {
      display: flex;
      gap: 8px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .order-management {
    padding: 16px;

    .page-header {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;

      .header-right {
        justify-content: center;
        flex-wrap: wrap;
      }
    }

    .order-stats {
      grid-template-columns: repeat(2, 1fr);
    }

    .filter-section {
      .filter-controls {
        flex-direction: column;
        align-items: stretch;

        > * {
          width: 100%;
        }
      }
    }

    .batch-actions {
      flex-direction: column;
      gap: 8px;
      align-items: stretch;

      .batch-buttons {
        justify-content: center;
      }
    }
  }
}
</style>