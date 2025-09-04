<template>
  <div class="order-api-test">
    <h2>订单API集成测试</h2>
    
    <div class="test-section">
      <h3>1. 获取订单列表</h3>
      <el-button @click="testGetOrders" :loading="loading.getOrders">
        测试获取订单列表
      </el-button>
      <div v-if="testResults.getOrders" class="test-result">
        <pre>{{ JSON.stringify(testResults.getOrders, null, 2) }}</pre>
      </div>
    </div>

    <div class="test-section">
      <h3>2. 获取订单统计</h3>
      <el-button @click="testGetStats" :loading="loading.getStats">
        测试获取订单统计
      </el-button>
      <div v-if="testResults.getStats" class="test-result">
        <pre>{{ JSON.stringify(testResults.getStats, null, 2) }}</pre>
      </div>
    </div>

    <div class="test-section">
      <h3>3. 创建订单</h3>
      <el-button @click="testCreateOrder" :loading="loading.createOrder">
        测试创建订单
      </el-button>
      <div v-if="testResults.createOrder" class="test-result">
        <pre>{{ JSON.stringify(testResults.createOrder, null, 2) }}</pre>
      </div>
    </div>

    <div class="test-section">
      <h3>4. 更新订单状态</h3>
      <el-input v-model="testOrderId" placeholder="输入订单ID" style="width: 200px; margin-right: 10px;" />
      <el-button @click="testUpdateStatus" :loading="loading.updateStatus">
        测试更新状态
      </el-button>
      <div v-if="testResults.updateStatus" class="test-result">
        <pre>{{ JSON.stringify(testResults.updateStatus, null, 2) }}</pre>
      </div>
    </div>

    <div class="test-section">
      <h3>测试结果汇总</h3>
      <div class="summary">
        <div class="summary-item" :class="{ success: testResults.getOrders, error: errors.getOrders }">
          获取订单列表: {{ testResults.getOrders ? '✅ 成功' : errors.getOrders ? '❌ 失败' : '⏳ 未测试' }}
        </div>
        <div class="summary-item" :class="{ success: testResults.getStats, error: errors.getStats }">
          获取订单统计: {{ testResults.getStats ? '✅ 成功' : errors.getStats ? '❌ 失败' : '⏳ 未测试' }}
        </div>
        <div class="summary-item" :class="{ success: testResults.createOrder, error: errors.createOrder }">
          创建订单: {{ testResults.createOrder ? '✅ 成功' : errors.createOrder ? '❌ 失败' : '⏳ 未测试' }}
        </div>
        <div class="summary-item" :class="{ success: testResults.updateStatus, error: errors.updateStatus }">
          更新状态: {{ testResults.updateStatus ? '✅ 成功' : errors.updateStatus ? '❌ 失败' : '⏳ 未测试' }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { orderApi } from '@/api/order'
import type { CreateOrderData } from '@/types/order'

// 响应式数据
const testOrderId = ref('')
const loading = reactive({
  getOrders: false,
  getStats: false,
  createOrder: false,
  updateStatus: false
})

const testResults = reactive({
  getOrders: null as any,
  getStats: null as any,
  createOrder: null as any,
  updateStatus: null as any
})

const errors = reactive({
  getOrders: null as any,
  getStats: null as any,
  createOrder: null as any,
  updateStatus: null as any
})

// 测试方法
const testGetOrders = async () => {
  loading.getOrders = true
  errors.getOrders = null
  try {
    const response = await orderApi.getOrders({
      page: 1,
      pageSize: 10
    })
    testResults.getOrders = response.data
    ElMessage.success('获取订单列表成功')
  } catch (error) {
    console.error('获取订单列表失败:', error)
    errors.getOrders = error
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.getOrders = false
  }
}

const testGetStats = async () => {
  loading.getStats = true
  errors.getStats = null
  try {
    const response = await orderApi.getOrderStats()
    testResults.getStats = response.data
    ElMessage.success('获取订单统计成功')
  } catch (error) {
    console.error('获取订单统计失败:', error)
    errors.getStats = error
    ElMessage.error('获取订单统计失败')
  } finally {
    loading.getStats = false
  }
}

const testCreateOrder = async () => {
  loading.createOrder = true
  errors.createOrder = null
  try {
    const orderData: CreateOrderData = {
      orderNumber: `TEST${Date.now()}`,
      type: 'sales',
      status: 'pending',
      customerName: '测试客户',
      customerPhone: '13800138000',
      customerEmail: 'test@example.com',
      deliveryAddress: '测试地址',
      items: [
        {
          productId: 'test-product-1',
          productName: '测试产品',
          quantity: 1,
          unitPrice: 100,
          totalPrice: 100
        }
      ],
      subtotal: 100,
      shippingFee: 10,
      discount: 0,
      totalAmount: 110,
      currency: 'CNY',
      paymentStatus: 'pending',
      expectedDeliveryDate: new Date().toISOString().split('T')[0],
      salesPerson: '测试销售',
      notes: '这是一个测试订单',
      createdAt: new Date().toISOString()
    }
    
    const response = await orderApi.createOrder(orderData)
    testResults.createOrder = response.data
    testOrderId.value = response.data.id
    ElMessage.success('创建订单成功')
  } catch (error) {
    console.error('创建订单失败:', error)
    errors.createOrder = error
    ElMessage.error('创建订单失败')
  } finally {
    loading.createOrder = false
  }
}

const testUpdateStatus = async () => {
  if (!testOrderId.value) {
    ElMessage.warning('请先创建订单或输入订单ID')
    return
  }
  
  loading.updateStatus = true
  errors.updateStatus = null
  try {
    const response = await orderApi.updateOrderStatus(testOrderId.value, 'confirmed')
    testResults.updateStatus = response.data
    ElMessage.success('更新订单状态成功')
  } catch (error) {
    console.error('更新订单状态失败:', error)
    errors.updateStatus = error
    ElMessage.error('更新订单状态失败')
  } finally {
    loading.updateStatus = false
  }
}
</script>

<style lang="scss" scoped>
.order-api-test {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  h2 {
    color: #303133;
    margin-bottom: 30px;
  }

  .test-section {
    margin-bottom: 30px;
    padding: 20px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    background: #fff;

    h3 {
      color: #606266;
      margin-bottom: 15px;
    }

    .test-result {
      margin-top: 15px;
      padding: 15px;
      background: #f5f7fa;
      border-radius: 4px;
      border: 1px solid #e4e7ed;

      pre {
        margin: 0;
        font-size: 12px;
        color: #303133;
        white-space: pre-wrap;
        word-break: break-all;
      }
    }
  }

  .summary {
    .summary-item {
      padding: 8px 12px;
      margin-bottom: 8px;
      border-radius: 4px;
      background: #f5f7fa;

      &.success {
        background: #f0f9ff;
        color: #067f23;
        border: 1px solid #b3d8ff;
      }

      &.error {
        background: #fef0f0;
        color: #f56c6c;
        border: 1px solid #fbc4c4;
      }
    }
  }
}
</style>