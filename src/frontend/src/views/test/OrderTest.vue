<template>
  <div class="order-test-page">
    <div class="page-header">
      <h2>销售订单创建功能测试</h2>
      <p>测试销售订单创建、编辑、查看等功能</p>
    </div>

    <div class="test-actions">
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>
        创建销售订单
      </el-button>
      <el-button @click="loadTestOrders">
        <el-icon><Refresh /></el-icon>
        加载测试订单
      </el-button>
    </div>

    <!-- 订单列表 -->
    <div class="orders-list" v-if="orders.length > 0">
      <h3>测试订单列表</h3>
      <el-table :data="orders" border>
        <el-table-column prop="orderNumber" label="订单号" width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'sales' ? 'success' : 'primary'">
              {{ row.type === 'sales' ? '销售' : '租赁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="客户" width="120" />
        <el-table-column prop="customerPhone" label="电话" width="130" />
        <el-table-column label="商品" width="200">
          <template #default="{ row }">
            <div v-for="item in row.items.slice(0, 2)" :key="item.id">
              {{ item.productName }} x{{ item.quantity }}
            </div>
            <div v-if="row.items.length > 2">
              +{{ row.items.length - 2 }}个商品
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="viewOrder(row)">查看</el-button>
            <el-button size="small" type="primary" @click="editOrder(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteOrder(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 功能测试结果 -->
    <div class="test-results" v-if="testResults.length > 0">
      <h3>测试结果</h3>
      <el-timeline>
        <el-timeline-item
          v-for="result in testResults"
          :key="result.id"
          :timestamp="result.timestamp"
          :type="result.success ? 'success' : 'danger'"
        >
          <div class="test-result">
            <div class="result-title">{{ result.action }}</div>
            <div class="result-message">{{ result.message }}</div>
            <div class="result-data" v-if="result.data">
              <pre>{{ JSON.stringify(result.data, null, 2) }}</pre>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>

    <!-- 订单创建/编辑对话框 -->
    <OrderFormDialog
      v-model="createDialogVisible"
      :order="editingOrder"
      @success="handleOrderSuccess"
    />

    <!-- 订单详情对话框 -->
    <OrderDetailDialog
      v-model="detailDialogVisible"
      :order="selectedOrder"
      @edit="handleEditFromDetail"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { mockOrderAPI } from '@/api/mock/order'
import type { Order } from '@/types/order'
import { ORDER_STATUS_TEXT } from '@/types/order'
import OrderFormDialog from '@/components/order/OrderFormDialog.vue'
import OrderDetailDialog from '@/components/order/OrderDetailDialog.vue'

// 响应式数据
const orders = ref<Order[]>([])
const createDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const editingOrder = ref<Order | null>(null)
const selectedOrder = ref<Order | null>(null)

interface TestResult {
  id: string
  action: string
  message: string
  success: boolean
  timestamp: string
  data?: any
}

const testResults = ref<TestResult[]>([])

// 方法
const addTestResult = (action: string, message: string, success: boolean, data?: any) => {
  testResults.value.unshift({
    id: Date.now().toString(),
    action,
    message,
    success,
    timestamp: new Date().toLocaleString(),
    data
  })
}

const showCreateDialog = () => {
  editingOrder.value = null
  createDialogVisible.value = true
  addTestResult('打开创建对话框', '销售订单创建对话框已打开', true)
}

const loadTestOrders = async () => {
  try {
    const response = await mockOrderAPI.getOrders({ pageSize: 10 })
    orders.value = response.data.list
    addTestResult('加载订单列表', `成功加载 ${response.data.list.length} 个订单`, true, {
      total: response.data.total,
      stats: response.data.stats
    })
    ElMessage.success('订单列表加载成功')
  } catch (error) {
    addTestResult('加载订单列表', '加载失败: ' + error, false)
    ElMessage.error('加载订单列表失败')
  }
}

const viewOrder = (order: Order) => {
  selectedOrder.value = order
  detailDialogVisible.value = true
  addTestResult('查看订单详情', `查看订单: ${order.orderNumber}`, true, order)
}

const editOrder = (order: Order) => {
  editingOrder.value = order
  createDialogVisible.value = true
  addTestResult('编辑订单', `编辑订单: ${order.orderNumber}`, true)
}

const deleteOrder = async (order: Order) => {
  try {
    await mockOrderAPI.deleteOrder(order.id)
    await loadTestOrders()
    addTestResult('删除订单', `订单 ${order.orderNumber} 删除成功`, true)
    ElMessage.success('订单删除成功')
  } catch (error) {
    addTestResult('删除订单', '删除失败: ' + error, false)
    ElMessage.error('删除订单失败')
  }
}

const handleOrderSuccess = () => {
  loadTestOrders()
  const action = editingOrder.value ? '更新订单' : '创建订单'
  addTestResult(action, `${action}成功`, true)
}

const handleEditFromDetail = (order: Order) => {
  detailDialogVisible.value = false
  editOrder(order)
}

const getStatusType = (status: string) => {
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

const getStatusText = (status: string) => {
  return ORDER_STATUS_TEXT[status as keyof typeof ORDER_STATUS_TEXT] || status
}

// 自动测试功能
const runAutoTests = async () => {
  addTestResult('开始自动测试', '销售订单功能自动测试开始', true)
  
  // 测试1: 加载订单列表
  await loadTestOrders()
  
  // 测试2: 创建测试订单
  try {
    const testOrderData = {
      type: 'sales' as const,
      customerName: '测试客户',
      customerPhone: '13800138000',
      customerEmail: 'test@example.com',
      deliveryAddress: '北京市朝阳区测试地址123号',
      items: [
        {
          productId: 'prod_001',
          productName: '练字机器人 - 基础版',
          quantity: 1,
          unitPrice: 2999,
          totalPrice: 2999,
          notes: '测试商品'
        }
      ],
      shippingFee: 50,
      discount: 0,
      totalAmount: 3049,
      expectedDeliveryDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      salesPerson: '测试销售员',
      notes: '这是一个自动测试创建的订单'
    }
    
    const createResponse = await mockOrderAPI.createOrder(testOrderData)
    addTestResult('创建测试订单', '测试订单创建成功', true, createResponse.data)
    
    // 重新加载订单列表
    await loadTestOrders()
    
  } catch (error) {
    addTestResult('创建测试订单', '创建失败: ' + error, false)
  }
  
  addTestResult('自动测试完成', '销售订单功能自动测试完成', true)
}

onMounted(() => {
  // 页面加载时运行自动测试
  setTimeout(() => {
    runAutoTests()
  }, 1000)
})
</script>

<style lang="scss" scoped>
.order-test-page {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;

  .page-header {
    margin-bottom: 24px;
    
    h2 {
      margin: 0 0 8px 0;
      color: #262626;
      font-size: 24px;
      font-weight: 600;
    }
    
    p {
      margin: 0;
      color: #8c8c8c;
      font-size: 14px;
    }
  }

  .test-actions {
    margin-bottom: 24px;
    display: flex;
    gap: 12px;
  }

  .orders-list {
    background: white;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    h3 {
      margin: 0 0 16px 0;
      color: #262626;
      font-size: 18px;
      font-weight: 600;
    }
  }

  .test-results {
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    h3 {
      margin: 0 0 16px 0;
      color: #262626;
      font-size: 18px;
      font-weight: 600;
    }

    .test-result {
      .result-title {
        font-weight: 600;
        color: #262626;
        margin-bottom: 4px;
      }

      .result-message {
        color: #606266;
        margin-bottom: 8px;
      }

      .result-data {
        background: #f8f9fa;
        border-radius: 4px;
        padding: 12px;
        border: 1px solid #e9ecef;

        pre {
          margin: 0;
          font-size: 12px;
          color: #495057;
          white-space: pre-wrap;
          word-break: break-all;
        }
      }
    }
  }
}
</style>