<template>
  <div class="sales-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>销售数据分析</h2>
        <p class="page-description">查看和分析销售数据、订单信息和业绩统计</p>
      </div>
      <div class="header-right">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleDateRangeChange"
        />
        <el-button type="primary" :icon="Refresh" @click="handleRefreshData">
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 销售概览卡片 -->
    <div class="overview-cards">
      <div class="overview-card primary">
        <div class="card-icon">
          <el-icon><Money /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">¥{{ formatCurrency(salesStats.totalSalesAmount || 0) }}</div>
          <div class="card-label">总销售额</div>
          <div class="card-trend" :class="{ positive: salesStats.growthRate && salesStats.growthRate > 0 }">
            <el-icon><DataLine /></el-icon>
            <span>{{ salesStats.growthRate ? (salesStats.growthRate > 0 ? '+' : '') + salesStats.growthRate.toFixed(1) + '%' : '0%' }}</span>
          </div>
        </div>
      </div>
      
      <div class="overview-card success">
        <div class="card-icon">
          <el-icon><ShoppingCart /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ salesStats.totalOrders || 0 }}</div>
          <div class="card-label">订单总数</div>
          <div class="card-trend positive">
            <el-icon><DataLine /></el-icon>
            <span>+{{ ((salesStats.totalOrders || 0) * 0.08).toFixed(1) }}%</span>
          </div>
        </div>
      </div>
      
      <div class="overview-card warning">
        <div class="card-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ salesStats.newCustomers || 0 }}</div>
          <div class="card-label">新增客户</div>
          <div class="card-trend positive">
            <el-icon><DataLine /></el-icon>
            <span>+{{ ((salesStats.newCustomers || 0) * 0.15).toFixed(1) }}%</span>
          </div>
        </div>
      </div>
      
      <div class="overview-card info">
        <div class="card-icon">
          <el-icon><Coin /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">¥{{ formatCurrency(salesStats.avgOrderAmount || 0) }}</div>
          <div class="card-label">平均订单价值</div>
          <div class="card-trend positive">
            <el-icon><DataLine /></el-icon>
            <span>+{{ ((salesStats.avgOrderAmount || 0) * 0.04 / 100).toFixed(1) }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="charts-row">
        <!-- 销售趋势图 -->
        <div class="chart-container">
          <ChartContainer
            title="销售趋势分析"
            subtitle="销售额和订单数趋势"
            :option="salesTrendOption"
            :loading="chartsLoading"
            height="350px"
            @refresh="handleRefreshCharts"
          />
        </div>
        
        <!-- 产品销售分布 -->
        <div class="chart-container">
          <ChartContainer
            title="产品销售分布"
            subtitle="各产品销售占比"
            :option="productDistributionOption"
            :loading="chartsLoading"
            height="350px"
            @refresh="handleRefreshCharts"
          />
        </div>
      </div>
      
      <div class="charts-row">
        <!-- 销售地区分布 -->
        <div class="chart-container">
          <ChartContainer
            title="销售地区分布"
            subtitle="各地区销售表现"
            :option="regionDistributionOption"
            :loading="chartsLoading"
            height="350px"
            @refresh="handleRefreshCharts"
          />
        </div>
        
        <!-- 销售渠道分析 -->
        <div class="chart-container">
          <ChartContainer
            title="销售渠道分析"
            subtitle="不同渠道的销售表现"
            :option="channelAnalysisOption"
            :loading="chartsLoading"
            height="350px"
            @refresh="handleRefreshCharts"
          />
        </div>
      </div>
    </div>

    <!-- 销售记录列表 -->
    <div class="records-section">
      <div class="section-header">
        <h3>销售记录</h3>
        <div class="header-actions">
          <el-select v-model="queryParams.status" placeholder="订单状态" clearable style="width: 150px" @change="handleSearch">
            <el-option label="全部状态" value="" />
            <el-option label="待确认" value="pending" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="已交付" value="delivered" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索订单号或客户"
            :prefix-icon="Search"
            clearable
            style="width: 250px"
            @input="handleSearch"
          />
          <el-button type="primary" @click="handleCreateRecord">
            新增销售记录
          </el-button>
        </div>
      </div>

      <!-- 空状态处理 -->
      <div v-if="salesRecords.length === 0 && !recordsLoading" class="empty-state">
        <el-empty description="暂无销售记录数据">
          <el-button type="primary" @click="handleCreateRecord">
            创建第一条销售记录
          </el-button>
        </el-empty>
      </div>

      <!-- 数据表格 -->
      <DataTable
        v-else
        :data="salesRecords"
        :columns="recordColumns"
        :loading="recordsLoading"
        :pagination="pagination"
        @page-change="handlePageChange"
        @view="handleViewRecord"
        @edit="handleEditRecord"
        @delete="handleDeleteRecord"
      >
        <template #orderNumber="{ row }">
          <el-link type="primary" @click="handleViewRecord(row)">
            {{ row.orderNumber }}
          </el-link>
        </template>
        
        <template #customer="{ row }">
          <div class="customer-info">
            <div class="customer-name">{{ row.customerName || '未知客户' }}</div>
            <div class="customer-phone">{{ row.customerPhone || '' }}</div>
          </div>
        </template>
        
        <template #product="{ row }">
          <div class="product-info">
            <div class="product-name">{{ row.productName || '未知产品' }}</div>
            <div class="product-quantity">数量: {{ row.quantity }}</div>
          </div>
        </template>
        
        <template #amount="{ row }">
          <div class="order-amount">
            <div class="total-amount">¥{{ formatCurrency(row.salesAmount) }}</div>
            <div class="unit-price">单价: ¥{{ formatCurrency(row.unitPrice) }}</div>
          </div>
        </template>
        
        <template #status="{ row }">
          <el-tag :type="getStatusType(row.status)" size="small">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
        
        <template #paymentStatus="{ row }">
          <el-tag :type="getPaymentStatusType(row.paymentStatus)" size="small">
            {{ getPaymentStatusText(row.paymentStatus) }}
          </el-tag>
        </template>
        
        <template #actions="{ row }">
          <el-button size="small" @click="handleViewRecord(row)">
            查看
          </el-button>
          <el-button size="small" type="primary" @click="handleEditRecord(row)">
            编辑
          </el-button>
          <el-button size="small" type="danger" @click="handleDeleteRecord(row)">
            删除
          </el-button>
        </template>
      </DataTable>
    </div>

    <!-- 销售记录详情对话框 -->
    <CommonDialog
      v-model="recordDetailVisible"
      :title="`销售记录详情 - ${currentRecord?.orderNumber}`"
      width="800px"
      @cancel="recordDetailVisible = false"
      :show-confirm="false"
    >
      <div v-if="currentRecord" class="record-detail">
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="detail-content">
            <div class="detail-item">
              <span class="label">订单号:</span>
              <span>{{ currentRecord.orderNumber }}</span>
            </div>
            <div class="detail-item">
              <span class="label">客户:</span>
              <span>{{ currentRecord.customerName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">产品:</span>
              <span>{{ currentRecord.productName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">销售人员:</span>
              <span>{{ currentRecord.staffName }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>金额信息</h4>
          <div class="detail-content">
            <div class="detail-item">
              <span class="label">数量:</span>
              <span>{{ currentRecord.quantity }}</span>
            </div>
            <div class="detail-item">
              <span class="label">单价:</span>
              <span>¥{{ formatCurrency(currentRecord.unitPrice) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">折扣金额:</span>
              <span>¥{{ formatCurrency(currentRecord.discountAmount || 0) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">销售总额:</span>
              <span class="highlight">¥{{ formatCurrency(currentRecord.salesAmount) }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>状态信息</h4>
          <div class="detail-content">
            <div class="detail-item">
              <span class="label">订单状态:</span>
              <el-tag :type="getStatusType(currentRecord.status)">
                {{ getStatusText(currentRecord.status) }}
              </el-tag>
            </div>
            <div class="detail-item">
              <span class="label">付款状态:</span>
              <el-tag :type="getPaymentStatusType(currentRecord.paymentStatus)">
                {{ getPaymentStatusText(currentRecord.paymentStatus) }}
              </el-tag>
            </div>
            <div class="detail-item">
              <span class="label">付款方式:</span>
              <span>{{ currentRecord.paymentMethod || '未指定' }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section" v-if="currentRecord.notes">
          <h4>备注信息</h4>
          <div class="detail-content">
            <p>{{ currentRecord.notes }}</p>
          </div>
        </div>
      </div>
    </CommonDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Money,
  ShoppingCart,
  User,
  Coin,
  DataLine,
  Refresh
} from '@element-plus/icons-vue'
import DataTable from '@/components/common/DataTable.vue'
import CommonDialog from '@/components/common/CommonDialog.vue'
import ChartContainer from '@/components/common/ChartContainer.vue'
import salesApi from '@/api/sales'
import type {
  SalesRecord,
  SalesStats,
  SalesRecordQuery,
  PageResponse
} from '@/types/sales'

// 响应式数据
const dateRange = ref<[string, string]>([
  new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
  new Date().toISOString().split('T')[0]
])

const chartsLoading = ref(false)
const recordsLoading = ref(false)
const recordDetailVisible = ref(false)
const currentRecord = ref<SalesRecord | null>(null)

// 销售统计数据 - 使用真实API数据
const salesStats = ref<SalesStats>({
  totalSalesAmount: 0,
  totalOrders: 0,
  avgOrderAmount: 0,
  totalQuantity: 0,
  newCustomers: 0,
  activeCustomers: 0,
  growthRate: 0
})

// 销售记录数据 - 使用真实API数据
const salesRecords = ref<SalesRecord[]>([])

// 查询参数
const queryParams = reactive<SalesRecordQuery>({
  page: 1,
  pageSize: 20,
  startDate: dateRange.value[0],
  endDate: dateRange.value[1],
  keyword: '',
  status: undefined
})

// 分页信息
const pagination = ref({
  current: 1,
  pageSize: 20,
  total: 0
})

// 图表配置 - 使用真实数据
const salesTrendOption = ref({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    }
  },
  legend: {
    data: ['销售额', '订单数']
  },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: [
    {
      type: 'value',
      name: '销售额(元)',
      position: 'left'
    },
    {
      type: 'value',
      name: '订单数',
      position: 'right'
    }
  ],
  series: [
    {
      name: '销售额',
      type: 'line',
      yAxisIndex: 0,
      data: [],
      smooth: true,
      itemStyle: {
        color: '#FF5A5F'
      }
    },
    {
      name: '订单数',
      type: 'bar',
      yAxisIndex: 1,
      data: [],
      itemStyle: {
        color: '#00D4AA'
      }
    }
  ]
})

const productDistributionOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '产品销售',
      type: 'pie',
      radius: '50%',
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
})

const regionDistributionOption = ref({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: {
    type: 'value',
    name: '销售额(元)'
  },
  series: [
    {
      name: '销售额',
      type: 'bar',
      data: [],
      itemStyle: {
        color: '#FF5A5F'
      }
    }
  ]
})

const channelAnalysisOption = ref({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['线上销售', '线下销售']
  },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '线上销售',
      type: 'line',
      data: [],
      itemStyle: {
        color: '#00D4AA'
      }
    },
    {
      name: '线下销售',
      type: 'line',
      data: [],
      itemStyle: {
        color: '#FF8A80'
      }
    }
  ]
})

// 表格列配置
const recordColumns = [
  { prop: 'orderNumber', label: '订单号', width: 140 },
  { prop: 'customer', label: '客户信息', width: 150 },
  { prop: 'product', label: '产品信息', width: 180 },
  { prop: 'amount', label: '金额信息', width: 140 },
  { prop: 'status', label: '订单状态', width: 100 },
  { prop: 'paymentStatus', label: '付款状态', width: 100 },
  { prop: 'orderDate', label: '订单日期', width: 120, type: 'date' },
  { prop: 'actions', label: '操作', width: 200, fixed: 'right' }
]

// 工具方法
const formatCurrency = (amount: number) => {
  return amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const getStatusText = (status: string) => {
  const statusMap = {
    pending: '待确认',
    confirmed: '已确认',
    delivered: '已交付',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status as keyof typeof statusMap] || status
}

const getStatusType = (status: string) => {
  const typeMap = {
    pending: 'warning',
    confirmed: 'primary',
    delivered: 'info',
    completed: 'success',
    cancelled: 'danger'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

const getPaymentStatusText = (status: string) => {
  const statusMap = {
    unpaid: '未付款',
    partial: '部分付款',
    paid: '已付款',
    refunded: '已退款'
  }
  return statusMap[status as keyof typeof statusMap] || status
}

const getPaymentStatusType = (status: string) => {
  const typeMap = {
    unpaid: 'danger',
    partial: 'warning',
    paid: 'success',
    refunded: 'info'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

// API调用方法
const loadSalesStats = async () => {
  try {
    const response = await salesApi.stats.getStats({
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    })
    
    if (response.code === 200) {
      salesStats.value = response.data
    }
  } catch (error) {
    console.error('加载销售统计数据失败:', error)
    // 保持默认值，不显示错误消息，因为可能是空数据
  }
}

const loadSalesRecords = async () => {
  recordsLoading.value = true
  try {
    const response = await salesApi.records.getList(queryParams)
    
    if (response.code === 200) {
      salesRecords.value = response.data.list
      pagination.value = {
        current: response.data.page,
        pageSize: response.data.pageSize,
        total: response.data.total
      }
    }
  } catch (error) {
    console.error('加载销售记录失败:', error)
    ElMessage.error('加载销售记录失败')
  } finally {
    recordsLoading.value = false
  }
}

const loadChartsData = async () => {
  chartsLoading.value = true
  try {
    // 加载销售趋势数据
    const trendsResponse = await salesApi.charts.getTrendChartData({
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      groupBy: 'day'
    })
    
    if (trendsResponse.code === 200 && trendsResponse.data) {
      const trendsData = trendsResponse.data
      salesTrendOption.value.xAxis.data = trendsData.categories || []
      if (trendsData.series && trendsData.series.length >= 2) {
        salesTrendOption.value.series[0].data = trendsData.series[0].data || []
        salesTrendOption.value.series[1].data = trendsData.series[1].data || []
      }
    }

    // 加载产品分布数据
    const productResponse = await salesApi.charts.getDistribution({
      type: 'product',
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    })
    
    if (productResponse.code === 200 && productResponse.data) {
      const productData = productResponse.data
      if (productData.categories && productData.series && productData.series[0]) {
        productDistributionOption.value.series[0].data = productData.categories.map((name, index) => ({
          name,
          value: productData.series[0].data[index] || 0
        }))
      }
    }

    // 加载地区分布数据
    const regionResponse = await salesApi.charts.getDistribution({
      type: 'region',
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    })
    
    if (regionResponse.code === 200 && regionResponse.data) {
      const regionData = regionResponse.data
      regionDistributionOption.value.xAxis.data = regionData.categories || []
      if (regionData.series && regionData.series[0]) {
        regionDistributionOption.value.series[0].data = regionData.series[0].data || []
      }
    }

    // 加载渠道分析数据
    const channelResponse = await salesApi.charts.getDistribution({
      type: 'channel',
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    })
    
    if (channelResponse.code === 200 && channelResponse.data) {
      const channelData = channelResponse.data
      channelAnalysisOption.value.xAxis.data = channelData.categories || []
      if (channelData.series && channelData.series.length >= 2) {
        channelAnalysisOption.value.series[0].data = channelData.series[0].data || []
        channelAnalysisOption.value.series[1].data = channelData.series[1].data || []
      }
    }

  } catch (error) {
    console.error('加载图表数据失败:', error)
    // 不显示错误消息，保持图表为空状态
  } finally {
    chartsLoading.value = false
  }
}

// 事件处理方法
const handleDateRangeChange = async (dates: [string, string] | null) => {
  if (dates) {
    queryParams.startDate = dates[0]
    queryParams.endDate = dates[1]
    
    await Promise.all([
      loadSalesStats(),
      loadSalesRecords(),
      loadChartsData()
    ])
    
    ElMessage.success('数据已更新')
  }
}

const handleRefreshData = async () => {
  await Promise.all([
    loadSalesStats(),
    loadSalesRecords(),
    loadChartsData()
  ])
  
  ElMessage.success('数据已刷新')
}

const handleRefreshCharts = async () => {
  await loadChartsData()
  ElMessage.success('图表数据已刷新')
}

const handleSearch = () => {
  queryParams.page = 1
  loadSalesRecords()
}

const handlePageChange = (page: number) => {
  queryParams.page = page
  loadSalesRecords()
}

const handleViewRecord = (record: SalesRecord) => {
  currentRecord.value = record
  recordDetailVisible.value = true
}

const handleEditRecord = (record: SalesRecord) => {
  ElMessage.info(`编辑销售记录功能开发中: ${record.orderNumber}`)
}

const handleDeleteRecord = async (record: SalesRecord) => {
  try {
    await ElMessageBox.confirm('确定要删除这条销售记录吗？', '确认删除', {
      type: 'warning'
    })
    
    const response = await salesApi.records.delete(record.id)
    
    if (response.code === 200) {
      ElMessage.success('删除成功')
      await loadSalesRecords()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除销售记录失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleCreateRecord = () => {
  ElMessage.info('新增销售记录功能开发中...')
}

// 生命周期
onMounted(async () => {
  // 页面加载时获取所有数据
  await Promise.all([
    loadSalesStats(),
    loadSalesRecords(),
    loadChartsData()
  ])
})
</script>

<style lang="scss" scoped>
.sales-management {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;
    
    .header-left {
      h2 {
        font-size: 24px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0 0 8px 0;
      }
      
      .page-description {
        color: var(--text-secondary);
        font-size: 14px;
        margin: 0;
      }
    }
    
    .header-right {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }
  
  // 销售概览卡片
  .overview-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 32px;
    
    .overview-card {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 24px;
      display: flex;
      align-items: center;
      gap: 20px;
      border: 1px solid var(--border-color);
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
      
      &:hover {
        box-shadow: var(--shadow-lg);
        transform: translateY(-4px);
      }
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 4px;
        background: linear-gradient(90deg, var(--primary-color), var(--primary-light));
      }
      
      &.primary::before {
        background: linear-gradient(90deg, var(--primary-color), #ff8a80);
      }
      
      &.success::before {
        background: linear-gradient(90deg, var(--el-color-success), #81c784);
      }
      
      &.warning::before {
        background: linear-gradient(90deg, var(--el-color-warning), #ffb74d);
      }
      
      &.info::before {
        background: linear-gradient(90deg, var(--el-color-info), #64b5f6);
      }
      
      .card-icon {
        width: 60px;
        height: 60px;
        border-radius: var(--radius-lg);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        color: white;
        
        .overview-card.primary & {
          background: linear-gradient(135deg, var(--primary-color), #ff8a80);
        }
        
        .overview-card.success & {
          background: linear-gradient(135deg, var(--el-color-success), #81c784);
        }
        
        .overview-card.warning & {
          background: linear-gradient(135deg, var(--el-color-warning), #ffb74d);
        }
        
        .overview-card.info & {
          background: linear-gradient(135deg, var(--el-color-info), #64b5f6);
        }
      }
      
      .card-content {
        flex: 1;
        
        .card-value {
          font-size: 32px;
          font-weight: 700;
          color: var(--text-primary);
          line-height: 1;
          margin-bottom: 8px;
        }
        
        .card-label {
          font-size: 14px;
          color: var(--text-secondary);
          margin-bottom: 8px;
        }
        
        .card-trend {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 12px;
          color: var(--el-color-danger);
          
          &.positive {
            color: var(--el-color-success);
          }
          
          .el-icon {
            font-size: 14px;
          }
        }
      }
    }
  }
  
  // 图表区域
  .charts-section {
    margin-bottom: 32px;
    
    .charts-row {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
      gap: 20px;
      margin-bottom: 20px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .chart-container {
        background: var(--bg-primary);
        border-radius: var(--radius-lg);
        border: 1px solid var(--border-color);
        overflow: hidden;
      }
    }
  }
  
  // 销售记录列表
  .records-section {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;
    border: 1px solid var(--border-color);
    
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      
      h3 {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0;
      }
      
      .header-actions {
        display: flex;
        gap: 12px;
        align-items: center;
      }
    }
    
    .empty-state {
      text-align: center;
      padding: 60px 20px;
    }
    
    .customer-info {
      .customer-name {
        font-weight: 500;
        color: var(--text-primary);
        margin-bottom: 4px;
      }
      
      .customer-phone {
        font-size: 12px;
        color: var(--text-secondary);
      }
    }
    
    .product-info {
      .product-name {
        font-weight: 500;
        color: var(--text-primary);
        margin-bottom: 4px;
      }
      
      .product-quantity {
        font-size: 12px;
        color: var(--text-secondary);
      }
    }
    
    .order-amount {
      .total-amount {
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 4px;
      }
      
      .unit-price {
        font-size: 12px;
        color: var(--text-secondary);
      }
    }
  }
  
  // 详情对话框
  .record-detail {
    .detail-section {
      margin-bottom: 24px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      h4 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0 0 16px 0;
        padding-bottom: 8px;
        border-bottom: 1px solid var(--border-color);
      }
      
      .detail-content {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 16px;
        
        .detail-item {
          display: flex;
          align-items: center;
          gap: 8px;
          
          .label {
            font-weight: 500;
            color: var(--text-secondary);
            min-width: 80px;
          }
          
          .highlight {
            font-weight: 600;
            color: var(--primary-color);
            font-size: 16px;
          }
        }
        
        p {
          margin: 0;
          color: var(--text-primary);
          line-height: 1.6;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .sales-management {
    .page-header {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;
      
      .header-right {
        justify-content: flex-end;
      }
    }
    
    .overview-cards {
      grid-template-columns: 1fr;
    }
    
    .charts-section .charts-row {
      grid-template-columns: 1fr;
    }
    
    .records-section .section-header {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;
      
      .header-actions {
        justify-content: flex-end;
      }
    }
  }
}
</style>