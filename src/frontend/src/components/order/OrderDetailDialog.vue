<template>
  <el-dialog
    v-model="dialogVisible"
    title="订单详情"
    width="1000px"
    :close-on-click-modal="false"
  >
    <div class="order-detail" v-if="order">
      <!-- 订单基本信息 -->
      <div class="detail-section">
        <div class="section-header">
          <h3>订单信息</h3>
          <div class="order-actions">
            <el-button type="primary" size="small" @click="handleEdit">
              <el-icon><Edit /></el-icon>
              编辑订单
            </el-button>
            <el-dropdown @command="handleStatusAction" trigger="click">
              <el-button size="small">
                状态操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="confirm" v-if="order.status === 'pending'">
                    确认订单
                  </el-dropdown-item>
                  <el-dropdown-item command="ship" v-if="order.status === 'confirmed'">
                    发货
                  </el-dropdown-item>
                  <el-dropdown-item command="deliver" v-if="order.status === 'shipped'">
                    确认送达
                  </el-dropdown-item>
                  <el-dropdown-item command="complete" v-if="order.status === 'delivered'">
                    完成订单
                  </el-dropdown-item>
                  <el-dropdown-item command="cancel" v-if="['pending', 'confirmed'].includes(order.status)">
                    取消订单
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        
        <el-row :gutter="24">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">订单号:</span>
              <span class="value">{{ order.orderNumber }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">订单类型:</span>
              <el-tag :type="order.type === 'sales' ? 'success' : 'primary'" size="small">
                {{ order.type === 'sales' ? '销售订单' : '租赁订单' }}
              </el-tag>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">订单状态:</span>
              <el-tag :type="getOrderStatusTagType(order.status)" size="small">
                {{ getOrderStatusText(order.status) }}
              </el-tag>
            </div>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">创建时间:</span>
              <span class="value">{{ formatDate(order.createdAt) }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">预计交付:</span>
              <span class="value">{{ formatDate(order.expectedDeliveryDate) || '-' }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">销售人员:</span>
              <span class="value">{{ order.salesPerson || '-' }}</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 客户信息 -->
      <div class="detail-section">
        <h3>客户信息</h3>
        <el-row :gutter="24">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">客户姓名:</span>
              <span class="value">{{ order.customerName }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">联系电话:</span>
              <span class="value">{{ order.customerPhone }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">客户邮箱:</span>
              <span class="value">{{ order.customerEmail || '-' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">收货地址:</span>
              <span class="value">{{ order.deliveryAddress }}</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 商品信息 -->
      <div class="detail-section">
        <h3>商品信息</h3>
        <el-table :data="order.items" border>
          <el-table-column prop="productName" label="产品名称" />
          <el-table-column prop="quantity" label="数量" width="100" align="center" />
          <el-table-column prop="unitPrice" label="单价" width="120" align="right">
            <template #default="{ row }">
              ¥{{ row.unitPrice.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="totalPrice" label="小计" width="120" align="right">
            <template #default="{ row }">
              ¥{{ row.totalPrice.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="notes" label="备注" />
        </el-table>
      </div>

      <!-- 租赁信息 (仅租赁订单显示) -->
      <div class="detail-section" v-if="order.type === 'rental'">
        <h3>租赁信息</h3>
        <el-row :gutter="24">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">租赁开始日期:</span>
              <span class="value">{{ formatDate(order.rentalStartDate) }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">租赁结束日期:</span>
              <span class="value">{{ formatDate(order.rentalEndDate) }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">租赁天数:</span>
              <span class="value">{{ order.rentalDays }} 天</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="24" v-if="order.rentalNotes">
          <el-col :span="24">
            <div class="info-item">
              <span class="label">租赁备注:</span>
              <span class="value">{{ order.rentalNotes }}</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 金额信息 -->
      <div class="detail-section">
        <h3>金额信息</h3>
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
                <span class="value">¥{{ (order.shippingFee || 0).toFixed(2) }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="amount-item">
                <span class="label">折扣:</span>
                <span class="value">-¥{{ (order.discount || 0).toFixed(2) }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="amount-item total">
                <span class="label">订单总额:</span>
                <span class="value">¥{{ order.totalAmount.toFixed(2) }}</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 支付信息 -->
      <div class="detail-section" v-if="order.paymentStatus">
        <h3>支付信息</h3>
        <el-row :gutter="24">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">支付状态:</span>
              <el-tag :type="getPaymentStatusTagType(order.paymentStatus)" size="small">
                {{ getPaymentStatusText(order.paymentStatus) }}
              </el-tag>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">支付方式:</span>
              <span class="value">{{ order.paymentMethod || '-' }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">支付时间:</span>
              <span class="value">{{ formatDate(order.paymentTime) || '-' }}</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 物流信息 -->
      <div class="detail-section" v-if="order.shippingInfo">
        <h3>物流信息</h3>
        <el-row :gutter="24">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">物流公司:</span>
              <span class="value">{{ order.shippingInfo.company || '-' }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">运单号:</span>
              <span class="value">{{ order.shippingInfo.trackingNumber || '-' }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">发货时间:</span>
              <span class="value">{{ formatDate(order.shippingInfo.shippedAt) || '-' }}</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 订单备注 -->
      <div class="detail-section" v-if="order.notes">
        <h3>订单备注</h3>
        <div class="notes-content">
          <p>{{ order.notes }}</p>
        </div>
      </div>

      <!-- 操作日志 -->
      <div class="detail-section" v-if="order.logs && order.logs.length > 0">
        <h3>操作日志</h3>
        <el-timeline>
          <el-timeline-item
            v-for="log in order.logs"
            :key="log.id"
            :timestamp="formatDate(log.createdAt)"
            :type="getLogType(log.action)"
          >
            <div class="log-content">
              <div class="log-action">{{ log.action }}</div>
              <div class="log-operator">操作人: {{ log.operator }}</div>
              <div class="log-notes" v-if="log.notes">{{ log.notes }}</div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handlePrint">
          <el-icon><Printer /></el-icon>
          打印订单
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, ArrowDown, Printer } from '@element-plus/icons-vue'
import { mockOrderAPI } from '@/api/mock/order'
import type { Order } from '@/types/order'
import { ORDER_STATUS_TEXT, PAYMENT_STATUS_TEXT } from '@/types/order'

interface Props {
  modelValue: boolean
  order?: Order | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'edit', order: Order): void
  (e: 'status-change'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 计算属性
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const subtotal = computed(() => {
  if (!props.order?.items) return 0
  return props.order.items.reduce((sum, item) => sum + (item.totalPrice || 0), 0)
})

// 方法
const handleEdit = () => {
  if (props.order) {
    emit('edit', props.order)
  }
}

const handleStatusAction = async (command: string) => {
  if (!props.order) return
  
  try {
    let message = ''
    switch (command) {
      case 'confirm':
        message = '确定要确认这个订单吗？'
        break
      case 'ship':
        message = '确定要标记为已发货吗？'
        break
      case 'deliver':
        message = '确定要标记为已送达吗？'
        break
      case 'complete':
        message = '确定要完成这个订单吗？'
        break
      case 'cancel':
        message = '确定要取消这个订单吗？'
        break
    }
    
    await ElMessageBox.confirm(message, '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await mockOrderAPI.updateOrderStatus(props.order.id, command as any)
    ElMessage.success('操作成功')
    emit('status-change')
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('状态更新失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const handlePrint = () => {
  ElMessage.info('打印功能开发中...')
}

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

const getLogType = (action: string) => {
  const types: Record<string, any> = {
    '创建订单': 'primary',
    '确认订单': 'success',
    '发货': 'info',
    '送达': 'success',
    '完成': 'success',
    '取消': 'danger'
  }
  return types[action] || 'primary'
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<style lang="scss" scoped>
.order-detail {
  .detail-section {
    margin-bottom: 32px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
      
      .order-actions {
        display: flex;
        gap: 8px;
      }
    }
    
    h3 {
      margin: 0 0 16px 0;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      border-bottom: 2px solid #f0f0f0;
      padding-bottom: 8px;
    }
    
    .info-item {
      display: flex;
      align-items: center;
      margin-bottom: 12px;
      
      .label {
        font-weight: 500;
        color: #606266;
        margin-right: 8px;
        min-width: 80px;
      }
      
      .value {
        color: #303133;
        flex: 1;
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
  
  .notes-content {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 16px;
    border: 1px solid #e9ecef;
    
    p {
      margin: 0;
      color: #606266;
      line-height: 1.6;
    }
  }
  
  .log-content {
    .log-action {
      font-weight: 500;
      color: #303133;
      margin-bottom: 4px;
    }
    
    .log-operator {
      font-size: 12px;
      color: #909399;
      margin-bottom: 4px;
    }
    
    .log-notes {
      font-size: 13px;
      color: #606266;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>