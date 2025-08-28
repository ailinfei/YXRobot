<template>
  <div class="rental-analytics">
    <!-- 页面标题和操作 -->
    <div class="page-header">
      <div class="header-left">
        <h2>租赁数据分析</h2>
        <p class="header-subtitle">租赁业务数据分析 · 练字机器人管理系统</p>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-cards">
      <div class="metric-card revenue-card">
        <div class="card-content">
          <div class="card-title">租赁总收入</div>
          <div class="card-value">
            <span class="currency">¥</span>
            <CountUp :value="rentalStats?.totalRentalRevenue || 0" :decimals="2" class="value-number" />
          </div>
          <div class="card-change positive">
            <el-icon class="change-icon"><ArrowUp /></el-icon>
            <span class="change-text">+{{ rentalStats?.revenueGrowthRate || 0 }}%</span>
          </div>
        </div>
      </div>
      
      <div class="metric-card devices-card">
        <div class="card-content">
          <div class="card-title">租赁设备数</div>
          <div class="card-value">
            <CountUp :value="rentalStats?.totalRentalDevices || 0" :decimals="0" class="value-number" />
          </div>
          <div class="card-change positive">
            <el-icon class="change-icon"><ArrowUp /></el-icon>
            <span class="change-text">+{{ rentalStats?.deviceGrowthRate || 0 }}%</span>
          </div>
        </div>
      </div>
      
      <div class="metric-card utilization-card">
        <div class="card-content">
          <div class="card-title">设备利用率</div>
          <div class="card-value">
            <CountUp :value="rentalStats?.deviceUtilizationRate || 0" :decimals="1" class="value-number" />
            <span class="unit">%</span>
          </div>
          <div class="card-change positive">
            <el-icon class="change-icon"><ArrowUp /></el-icon>
            <span class="change-text">+2.3%</span>
          </div>
        </div>
      </div>
      
      <div class="metric-card period-card">
        <div class="card-content">
          <div class="card-title">平均租期</div>
          <div class="card-value">
            <CountUp :value="rentalStats?.averageRentalPeriod || 0" :decimals="0" class="value-number" />
            <span class="unit">天</span>
          </div>
          <div class="card-change positive">
            <el-icon class="change-icon"><ArrowUp /></el-icon>
            <span class="change-text">+1.8天</span>
          </div>
        </div>
      </div>
    </div> 

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧内容区域 -->
      <div class="left-content">
        <!-- 图表区域 -->
        <div class="charts-section">
      <!-- 第一行图表 -->
      <div class="charts-row">
        <!-- 租赁趋势分析 -->
        <div class="chart-card rental-trend-card">
          <div class="chart-header">
            <div class="header-left">
              <h3 class="chart-title">租赁趋势分析</h3>
              <p class="chart-subtitle">租赁收入和订单数量趋势</p>
            </div>
            <div class="header-right">
              <el-select v-model="trendPeriod" @change="loadTrendData" size="small">
                <el-option label="最近7天" value="daily" />
                <el-option label="最近4周" value="weekly" />
                <el-option label="最近12月" value="monthly" />
                <el-option label="最近4季度" value="quarterly" />
              </el-select>
            </div>
          </div>
          <div class="chart-content">
            <div ref="rentalTrendChart" class="chart-container" style="height: 300px;"></div>
          </div>
        </div>

        <!-- 设备利用率排行 -->
        <div class="chart-card utilization-ranking-card">
          <div class="chart-header">
            <div class="header-left">
              <h3 class="chart-title">设备利用率排行</h3>
              <p class="chart-subtitle">各设备利用率对比</p>
            </div>
          
          </div>
          <div class="chart-content">
            <div ref="utilizationRankingChart" class="chart-container" style="height: 300px;"></div>
          </div>
        </div>
      </div>

      <!-- 第二行图表 -->
      <div class="charts-row">
        <!-- 地区分布 -->
        <div class="chart-card region-distribution-card">
          <div class="chart-header">
            <div class="header-left">
              <h3 class="chart-title">地区分布</h3>
              <p class="chart-subtitle">各地区租赁业务分布</p>
            </div>
          </div>
          <div class="chart-content">
            <div ref="regionDistributionChart" class="chart-container" style="height: 300px;"></div>
          </div>
        </div>

        <!-- 设备型号分析 -->
        <div class="chart-card device-model-card">
          <div class="chart-header">
            <div class="header-left">
              <h3 class="chart-title">设备型号分析</h3>
              <p class="chart-subtitle">各型号设备租赁表现</p>
            </div>
          </div>
          <div class="chart-content">
            <div ref="deviceModelChart" class="chart-container" style="height: 300px;"></div>
          </div>
        </div>
      </div>
    </div>
    <!-- 设备利用率详情表格 -->
    <div class="device-utilization-section">
      <div class="section-header">
        <h3>设备利用率详情</h3>
        <p>各设备的详细利用率数据</p>
      </div>
      
      <div class="table-controls">
        <div class="controls-left">
          <el-select v-model="deviceModelFilter" placeholder="设备型号" clearable @change="loadDeviceData">
            <el-option label="全部型号" value="" />
            <el-option label="YX-Robot-Pro" value="YX-Robot-Pro" />
            <el-option label="YX-Robot-Standard" value="YX-Robot-Standard" />
            <el-option label="YX-Robot-Lite" value="YX-Robot-Lite" />
            <el-option label="YX-Robot-Mini" value="YX-Robot-Mini" />
          </el-select>
          <el-select v-model="statusFilter" placeholder="设备状态" clearable @change="loadDeviceData">
            <el-option label="全部状态" value="" />
            <el-option label="运行中" value="active" />
            <el-option label="空闲" value="idle" />
            <el-option label="维护中" value="maintenance" />
          </el-select>
        </div>
        <div class="controls-right">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索设备ID"
            :prefix-icon="Search"
            clearable
            @input="handleSearch"
            style="width: 200px;"
          />
        </div>
      </div>

      <el-table :data="paginatedDeviceData" v-loading="tableLoading" stripe height="400">
        <el-table-column prop="deviceId" label="设备ID" width="120" />
        <el-table-column prop="deviceModel" label="设备型号" width="150">
          <template #default="{ row }">
            <el-tag size="small" :type="getModelTagType(row.deviceModel)">
              {{ row.deviceModel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="utilizationRate" label="利用率" width="140">
          <template #default="{ row }">
            <div class="utilization-cell">
              <span class="rate-text">{{ row.utilizationRate }}%</span>
              <el-progress 
                :percentage="row.utilizationRate" 
                :stroke-width="6"
                :show-text="false"
                :color="getUtilizationColor(row.utilizationRate)"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalRentalDays" label="租赁天数" width="100" align="center" />
        <el-table-column prop="totalAvailableDays" label="可用天数" width="100" align="center" />
        <el-table-column prop="currentStatus" label="当前状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.currentStatus)" size="small">
              {{ getStatusText(row.currentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastRentalDate" label="最后租赁日期" width="120" align="center">
          <template #default="{ row }">
            {{ row.lastRentalDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="viewDeviceDetail(row)">
              查看详情
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
          :total="filteredDeviceData.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      </div>
    </div>

      <!-- 右侧信息面板 -->
      <div class="right-sidebar">
        <!-- 今日概览 -->
        <div class="sidebar-card">
          <div class="card-header">
            <h4>今日概览</h4>
          </div>
          <div class="card-content">
            <div class="overview-item">
              <span class="label">今日收入</span>
              <span class="value">¥{{ todayStats.revenue.toLocaleString() }}</span>
            </div>
            <div class="overview-item">
              <span class="label">新增订单</span>
              <span class="value">{{ todayStats.orders }}个</span>
            </div>
            <div class="overview-item">
              <span class="label">活跃设备</span>
              <span class="value">{{ todayStats.activeDevices }}台</span>
            </div>
            <div class="overview-item">
              <span class="label">平均利用率</span>
              <span class="value">{{ todayStats.avgUtilization }}%</span>
            </div>
          </div>
        </div>

        <!-- 设备状态分布 -->
        <div class="sidebar-card">
          <div class="card-header">
            <h4>设备状态</h4>
          </div>
          <div class="card-content">
            <div class="status-item">
              <el-tag type="success" size="small">运行中</el-tag>
              <span class="count">{{ deviceStatusStats.active }}台</span>
            </div>
            <div class="status-item">
              <el-tag type="warning" size="small">空闲</el-tag>
              <span class="count">{{ deviceStatusStats.idle }}台</span>
            </div>
            <div class="status-item">
              <el-tag type="info" size="small">维护中</el-tag>
              <span class="count">{{ deviceStatusStats.maintenance }}台</span>
            </div>
          </div>
        </div>

        <!-- TOP设备排行 -->
        <div class="sidebar-card">
          <div class="card-header">
            <h4>利用率TOP5</h4>
          </div>
          <div class="card-content">
            <div v-for="(device, index) in topDevices" :key="device.deviceId" class="ranking-item">
              <div class="rank">{{ index + 1 }}</div>
              <div class="device-info">
                <div class="device-id">{{ device.deviceId }}</div>
                <div class="device-model">{{ device.deviceModel }}</div>
              </div>
              <div class="utilization">{{ device.utilizationRate }}%</div>
            </div>
          </div>
        </div>

   
      </div>
    </div>

    <!-- 设备详情弹窗 -->
    <DeviceDetailDialog 
      v-model="showDeviceDetail" 
      :device-data="selectedDevice" 
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import {
  MoreFilled,
  ArrowUp,
  Search
} from '@element-plus/icons-vue'
import { CountUp } from '@/components/common'
import DeviceDetailDialog from '@/components/rental/DeviceDetailDialog.vue'
import * as echarts from 'echarts'
import { mockRentalAPI } from '@/api/mock/rental'
import type { 
  RentalStats, 
  RentalTrendData, 
  DeviceUtilizationData,
  RentalRevenueAnalysis 
} from '@/api/rental'

// 响应式数据
const dateRange = ref<[string, string]>(['2024-01-01', '2024-01-31'])
const refreshLoading = ref(false)
const tableLoading = ref(false)
const trendPeriod = ref('monthly')
const deviceModelFilter = ref('')
const statusFilter = ref('')
const searchKeyword = ref('')

// 分页相关
const currentPage = ref(1)
const pageSize = ref(20)

// 设备详情弹窗相关
const showDeviceDetail = ref(false)
const selectedDevice = ref<DeviceUtilizationData | null>(null)

// 右侧面板数据
const todayStats = ref({
  revenue: 45680,
  orders: 23,
  activeDevices: 198,
  avgUtilization: 78.5
})

const deviceStatusStats = computed(() => {
  const stats = { active: 0, idle: 0, maintenance: 0 }
  deviceUtilizationData.value.forEach(device => {
    stats[device.currentStatus as keyof typeof stats]++
  })
  return stats
})

const topDevices = computed(() => {
  return deviceUtilizationData.value
    .slice()
    .sort((a, b) => b.utilizationRate - a.utilizationRate)
    .slice(0, 5)
})

// 筛选后的设备数据
const filteredDeviceData = computed(() => {
  let data = deviceUtilizationData.value
  
  // 按设备型号筛选
  if (deviceModelFilter.value) {
    data = data.filter(item => item.deviceModel === deviceModelFilter.value)
  }
  
  // 按状态筛选
  if (statusFilter.value) {
    data = data.filter(item => item.currentStatus === statusFilter.value)
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    data = data.filter(item => 
      item.deviceId.toLowerCase().includes(keyword) ||
      item.deviceModel.toLowerCase().includes(keyword)
    )
  }
  
  return data
})

// 分页后的设备数据
const paginatedDeviceData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredDeviceData.value.slice(start, end)
})

// 图表实例
const rentalTrendChart = ref<HTMLElement>()
const utilizationRankingChart = ref<HTMLElement>()
const regionDistributionChart = ref<HTMLElement>()
const deviceModelChart = ref<HTMLElement>()

let rentalTrendChartInstance: echarts.ECharts | null = null
let utilizationRankingChartInstance: echarts.ECharts | null = null
let regionDistributionChartInstance: echarts.ECharts | null = null
let deviceModelChartInstance: echarts.ECharts | null = null

// 数据
const rentalStats = ref<RentalStats | null>(null)
const trendData = ref<RentalTrendData[]>([])
const deviceUtilizationData = ref<DeviceUtilizationData[]>([])
const revenueAnalysis = ref<RentalRevenueAnalysis | null>(null)

// 处理日期范围变化
const handleDateRangeChange = (dates: [string, string]) => {
  console.log('日期范围变化:', dates)
  loadAllData()
}

// 导出报表
const exportReport = () => {
  console.log('导出租赁报表')
}

// 刷新数据
const refreshData = async () => {
  refreshLoading.value = true
  try {
    await loadAllData()
  } finally {
    setTimeout(() => {
      refreshLoading.value = false
    }, 1000)
  }
}

// 搜索处理（防抖）
let searchTimer: NodeJS.Timeout | null = null
const handleSearch = () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  searchTimer = setTimeout(() => {
    loadDeviceData()
  }, 300)
}

// 加载趋势数据
const loadTrendData = async () => {
  try {
    const response = await mockRentalAPI.getRentalTrendData({ period: trendPeriod.value })
    trendData.value = response.data
    updateRentalTrendChart()
  } catch (error) {
    console.error('加载趋势数据失败:', error)
  }
}

// 加载设备数据
const loadDeviceData = async () => {
  // 数据已经在loadAllData中加载，这里只需要重置分页
  currentPage.value = 1
}

// 加载所有数据
const loadAllData = async () => {
  console.log('🔄 开始加载租赁分析数据...')
  
  try {
    // 先设置默认数据，确保页面有内容显示
    setDefaultData()
    
    // 然后尝试加载真实数据
    const [statsRes, trendRes, deviceRes, analysisRes] = await Promise.all([
      mockRentalAPI.getRentalStats(),
      mockRentalAPI.getRentalTrendData({ period: trendPeriod.value }),
      mockRentalAPI.getDeviceUtilizationData({}),
      mockRentalAPI.getRentalRevenueAnalysis({})
    ])
    
    // 如果API调用成功，使用真实数据
    if (statsRes && statsRes.data) {
      rentalStats.value = statsRes.data
      console.log('📊 租赁统计数据加载成功:', rentalStats.value)
    }
    
    if (trendRes && trendRes.data) {
      trendData.value = trendRes.data
      console.log('📈 趋势数据加载成功，数据点数量:', trendData.value.length)
    }
    
    if (deviceRes && deviceRes.data) {
      deviceUtilizationData.value = deviceRes.data
      console.log('🔧 设备数据加载成功，设备数量:', deviceUtilizationData.value.length)
    }
    
    if (analysisRes && analysisRes.data) {
      revenueAnalysis.value = analysisRes.data
      console.log('💰 收入分析数据加载成功:', revenueAnalysis.value)
    }
    
  } catch (error) {
    console.error('❌ 加载数据失败，使用默认数据:', error)
    // 确保有默认数据
    if (!rentalStats.value) {
      setDefaultData()
    }
  }
  
  // 确保数据加载完成后再更新图表
  await nextTick()
  try {
    updateAllCharts()
    console.log('✅ 租赁分析数据和图表更新完成')
  } catch (chartError) {
    console.error('❌ 图表更新失败:', chartError)
  }
}

// 设置默认数据
const setDefaultData = () => {
  console.log('📊 设置默认租赁数据...')
  
  rentalStats.value = {
    totalRentalRevenue: 1285420,
    totalRentalDevices: 285,
    activeRentalDevices: 198,
    deviceUtilizationRate: 78.5,
    averageRentalPeriod: 32,
    totalRentalOrders: 1456,
    revenueGrowthRate: 18.6,
    deviceGrowthRate: 12.3
  }
  
  // 生成最近30天的趋势数据
  const dates = []
  const revenues = []
  const orders = []
  const today = new Date()
  
  for (let i = 29; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    dates.push(date.toISOString().slice(0, 10))
    
    // 生成有趋势的数据
    const baseRevenue = 35000 + Math.sin(i * 0.1) * 8000 + Math.random() * 5000
    const baseOrders = 45 + Math.sin(i * 0.15) * 15 + Math.random() * 10
    
    revenues.push(Math.floor(baseRevenue))
    orders.push(Math.floor(baseOrders))
  }
  
  trendData.value = dates.map((date, index) => ({
    date,
    revenue: revenues[index],
    orderCount: orders[index],
    deviceCount: Math.floor(Math.random() * 50) + 150,
    utilizationRate: Math.floor(Math.random() * 30) + 60
  }))
  
  // 生成设备利用率数据
  const deviceModels = ['YX-Robot-Pro', 'YX-Robot-Standard', 'YX-Robot-Lite', 'YX-Robot-Mini']
  const statuses = ['active', 'idle', 'maintenance']
  const regions = ['华东', '华北', '华南', '华中', '西南', '西北', '东北', '华西']
  const maintenanceStatuses = ['normal', 'warning', 'urgent']
  const devices = []
  
  for (let i = 1; i <= 50; i++) {
    const totalAvailableDays = 365
    const totalRentalDays = Math.floor(Math.random() * 250) + 50
    const utilizationRate = Math.floor((totalRentalDays / totalAvailableDays) * 100)
    const deviceModel = deviceModels[Math.floor(Math.random() * deviceModels.length)]
    
    // 根据设备型号设置不同的性能基准
    let basePerformance = 85
    let baseSignal = 78
    switch (deviceModel) {
      case 'YX-Robot-Pro':
        basePerformance = 90
        baseSignal = 85
        break
      case 'YX-Robot-Standard':
        basePerformance = 85
        baseSignal = 80
        break
      case 'YX-Robot-Lite':
        basePerformance = 80
        baseSignal = 75
        break
      case 'YX-Robot-Mini':
        basePerformance = 75
        baseSignal = 70
        break
    }
    
    devices.push({
      deviceId: `YX-${String(i).padStart(4, '0')}`,
      deviceModel,
      utilizationRate,
      totalRentalDays,
      totalAvailableDays,
      currentStatus: statuses[Math.floor(Math.random() * statuses.length)],
      lastRentalDate: Math.random() > 0.2 ? 
        new Date(Date.now() - Math.floor(Math.random() * 30) * 24 * 60 * 60 * 1000).toISOString().slice(0, 10) : 
        undefined,
      // 新增字段
      region: regions[Math.floor(Math.random() * regions.length)],
      performanceScore: Math.floor(basePerformance + (Math.random() - 0.5) * 20),
      signalStrength: Math.floor(baseSignal + (Math.random() - 0.5) * 30),
      maintenanceStatus: maintenanceStatuses[Math.floor(Math.random() * maintenanceStatuses.length)]
    })
  }
  
  deviceUtilizationData.value = devices.sort((a, b) => b.utilizationRate - a.utilizationRate)
  
  // 生成收入分析数据
  revenueAnalysis.value = {
    period: '2024年度',
    totalRevenue: 2450000,
    orderCount: 2800,
    averageOrderValue: 875,
    topPerformingDevices: [
      {
        deviceModel: 'YX-Robot-Pro',
        revenue: 857500,
        orderCount: 980,
        utilizationRate: 85.2
      },
      {
        deviceModel: 'YX-Robot-Standard',
        revenue: 686000,
        orderCount: 784,
        utilizationRate: 78.6
      },
      {
        deviceModel: 'YX-Robot-Lite',
        revenue: 539000,
        orderCount: 616,
        utilizationRate: 72.1
      },
      {
        deviceModel: 'YX-Robot-Mini',
        revenue: 367500,
        orderCount: 420,
        utilizationRate: 65.8
      }
    ],
    regionDistribution: [
      { region: '华东', revenue: 539000, orderCount: 616, deviceCount: 85 },
      { region: '华北', revenue: 441000, orderCount: 504, deviceCount: 72 },
      { region: '华南', revenue: 392000, orderCount: 448, deviceCount: 68 },
      { region: '华中', revenue: 294000, orderCount: 336, deviceCount: 52 },
      { region: '西南', revenue: 245000, orderCount: 280, deviceCount: 45 },
      { region: '西北', revenue: 196000, orderCount: 224, deviceCount: 38 },
      { region: '东北', revenue: 220500, orderCount: 252, deviceCount: 42 },
      { region: '华西', revenue: 122500, orderCount: 140, deviceCount: 28 }
    ]
  }
  
  console.log('✅ 默认租赁数据设置完成')
}

// 获取利用率颜色
const getUtilizationColor = (rate: number) => {
  if (rate >= 80) return '#67c23a'
  if (rate >= 60) return '#e6a23c'
  if (rate >= 40) return '#f56c6c'
  return '#909399'
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    active: 'success',
    idle: 'warning',
    maintenance: 'info'
  }
  return types[status] || 'info'
}

// 获取状态文字
const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    active: '运行中',
    idle: '空闲',
    maintenance: '维护中'
  }
  return texts[status] || status
}

// 获取设备型号标签类型
const getModelTagType = (model: string) => {
  const types: Record<string, any> = {
    'YX-Robot-Pro': 'danger',
    'YX-Robot-Standard': 'primary',
    'YX-Robot-Lite': 'success',
    'YX-Robot-Mini': 'warning'
  }
  return types[model] || 'info'
}

// 分页处理
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize
  currentPage.value = 1
}

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage
}

// 查看设备详情
const viewDeviceDetail = (device: DeviceUtilizationData) => {
  console.log('查看设备详情:', device)
  selectedDevice.value = device
  showDeviceDetail.value = true
}

// 查看所有设备
const viewAllDevices = () => {
  console.log('查看所有设备')
  // 这里可以跳转到设备管理页面或展开设备列表
}

// 初始化租赁趋势图表
const initRentalTrendChart = () => {
  if (!rentalTrendChart.value) return
  
  rentalTrendChartInstance = echarts.init(rentalTrendChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      },
      formatter: function(params: any) {
        let result = `${params[0].name}<br/>`
        params.forEach((param: any) => {
          if (param.seriesName === '租赁收入') {
            result += `${param.seriesName}: ¥${param.value.toLocaleString()}<br/>`
          } else if (param.seriesName === '订单数量') {
            result += `${param.seriesName}: ${param.value}个<br/>`
          } else {
            result += `${param.seriesName}: ${param.value}%<br/>`
          }
        })
        return result
      }
    },
    legend: {
      data: ['租赁收入', '订单数量', '设备利用率'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: [],
      axisLabel: {
        rotate: 45,
        fontSize: 11
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '收入(元)',
        position: 'left',
        axisLabel: {
          formatter: function(value: number) {
            return value >= 10000 ? (value / 10000).toFixed(1) + 'w' : value.toString()
          }
        }
      },
      {
        type: 'value',
        name: '订单数/利用率',
        position: 'right',
        max: 100
      }
    ],
    series: [
      {
        name: '租赁收入',
        type: 'line',
        yAxisIndex: 0,
        data: [],
        smooth: true,
        itemStyle: { color: '#5470c6' },
        lineStyle: { width: 3 }
      },
      {
        name: '订单数量',
        type: 'bar',
        yAxisIndex: 1,
        data: [],
        itemStyle: { color: '#91cc75', opacity: 0.8 }
      },
      {
        name: '设备利用率',
        type: 'line',
        yAxisIndex: 1,
        data: [],
        smooth: true,
        itemStyle: { color: '#fac858' },
        lineStyle: { width: 2, type: 'dashed' }
      }
    ]
  }
  
  rentalTrendChartInstance.setOption(option)
}

// 更新租赁趋势图表
const updateRentalTrendChart = () => {
  if (!rentalTrendChartInstance || !trendData.value.length) return
  
  const dates = trendData.value.map(item => {
    const date = new Date(item.date)
    return `${date.getMonth() + 1}/${date.getDate()}`
  })
  const revenues = trendData.value.map(item => item.revenue)
  const orders = trendData.value.map(item => item.orderCount)
  const utilization = trendData.value.map(item => item.utilizationRate)
  
  rentalTrendChartInstance.setOption({
    xAxis: { data: dates },
    series: [
      { data: revenues },
      { data: orders },
      { data: utilization }
    ]
  })
}

// 初始化设备利用率排行图表
const initUtilizationRankingChart = () => {
  if (!utilizationRankingChart.value) return
  
  utilizationRankingChartInstance = echarts.init(utilizationRankingChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params: any) {
        const data = params[0]
        return `${data.name}<br/>利用率: ${data.value}%`
      }
    },
    grid: {
      left: '25%',
      right: '8%',
      bottom: '3%',
      top: '3%',
      containLabel: false
    },
    xAxis: {
      type: 'value',
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    yAxis: {
      type: 'category',
      data: [],
      axisLabel: {
        fontSize: 11,
        width: 120,
        overflow: 'truncate'
      }
    },
    series: [
      {
        name: '利用率',
        type: 'bar',
        data: [],
        barWidth: '60%',
        itemStyle: {
          borderRadius: [0, 4, 4, 0]
        },
        label: {
          show: true,
          position: 'right',
          formatter: '{c}%',
          fontSize: 11
        }
      }
    ]
  }
  
  utilizationRankingChartInstance.setOption(option)
}

// 初始化地区分布图表
const initRegionDistributionChart = () => {
  if (!regionDistributionChart.value) return
  
  regionDistributionChartInstance = echarts.init(regionDistributionChart.value)
  
  const option = {
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
        name: '地区分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        data: [],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          show: true,
          formatter: '{b}: {c}'
        }
      }
    ],
    color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272']
  }
  
  regionDistributionChartInstance.setOption(option)
}

// 初始化设备型号分析图表
const initDeviceModelChart = () => {
  if (!deviceModelChart.value) return
  
  deviceModelChartInstance = echarts.init(deviceModelChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['租赁数量', '收入贡献']
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
    yAxis: [
      {
        type: 'value',
        name: '数量',
        position: 'left'
      },
      {
        type: 'value',
        name: '收入(万元)',
        position: 'right'
      }
    ],
    series: [
      {
        name: '租赁数量',
        type: 'bar',
        yAxisIndex: 0,
        data: [],
        itemStyle: { color: '#5470c6' }
      },
      {
        name: '收入贡献',
        type: 'line',
        yAxisIndex: 1,
        data: [],
        smooth: true,
        itemStyle: { color: '#91cc75' }
      }
    ]
  }
  
  deviceModelChartInstance.setOption(option)
}

// 更新设备利用率排行图表
const updateUtilizationRankingChart = () => {
  if (!utilizationRankingChartInstance || !deviceUtilizationData.value.length) return
  
  // 取前12个设备
  const topDevices = deviceUtilizationData.value
    .sort((a, b) => b.utilizationRate - a.utilizationRate)
    .slice(0, 12)
  
  const deviceNames = topDevices.map(item => `${item.deviceId} (${item.deviceModel})`)
  const utilizationRates = topDevices.map(item => item.utilizationRate)
  
  utilizationRankingChartInstance.setOption({
    yAxis: { 
      data: deviceNames,
      axisLabel: {
        fontSize: 11,
        width: 120,
        overflow: 'truncate'
      }
    },
    series: [{
      data: utilizationRates,
      itemStyle: {
        color: function(params: any) {
          const rate = params.value
          if (rate >= 85) return '#52c41a'
          if (rate >= 70) return '#1890ff'
          if (rate >= 50) return '#faad14'
          return '#f5222d'
        }
      }
    }]
  })
}

// 更新地区分布图表
const updateRegionDistributionChart = () => {
  if (!regionDistributionChartInstance || !revenueAnalysis.value) return
  
  // 使用真实的地区分布数据
  const regionData = revenueAnalysis.value.regionDistribution.map(item => ({
    name: item.region,
    value: item.deviceCount,
    revenue: item.revenue,
    orderCount: item.orderCount
  }))
  
  regionDistributionChartInstance.setOption({
    tooltip: {
      trigger: 'item',
      formatter: function(params: any) {
        const data = params.data
        return `${params.name}<br/>
                设备数量: ${data.value}台<br/>
                租赁收入: ¥${(data.revenue / 10000).toFixed(1)}万<br/>
                订单数量: ${data.orderCount}个<br/>
                占比: ${params.percent}%`
      }
    },
    series: [{ 
      data: regionData,
      label: {
        show: true,
        formatter: '{b}: {c}台'
      }
    }]
  })
}

// 更新设备型号分析图表
const updateDeviceModelChart = () => {
  if (!deviceModelChartInstance || !revenueAnalysis.value) return
  
  // 使用真实的设备型号数据
  const modelData = revenueAnalysis.value.topPerformingDevices
  const models = modelData.map(item => item.deviceModel)
  const orderCounts = modelData.map(item => item.orderCount)
  const revenues = modelData.map(item => Math.round(item.revenue / 10000)) // 转换为万元
  
  deviceModelChartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      },
      formatter: function(params: any) {
        let result = `${params[0].name}<br/>`
        params.forEach((param: any) => {
          if (param.seriesName === '订单数量') {
            result += `${param.seriesName}: ${param.value}个<br/>`
          } else {
            result += `${param.seriesName}: ${param.value}万元<br/>`
          }
        })
        return result
      }
    },
    xAxis: { 
      data: models,
      axisLabel: {
        rotate: 15,
        fontSize: 11
      }
    },
    series: [
      { 
        name: '订单数量',
        data: orderCounts,
        itemStyle: { color: '#5470c6' }
      },
      { 
        name: '收入贡献',
        data: revenues,
        itemStyle: { color: '#91cc75' }
      }
    ]
  })
}

// 初始化所有图表
const initAllCharts = async () => {
  await nextTick()
  
  try {
    console.log('📊 开始初始化图表...')
    
    if (rentalTrendChart.value) {
      initRentalTrendChart()
      console.log('✅ 租赁趋势图表初始化完成')
    } else {
      console.warn('⚠️ 租赁趋势图表DOM元素未找到')
    }
    
    if (utilizationRankingChart.value) {
      initUtilizationRankingChart()
      console.log('✅ 设备利用率排行图表初始化完成')
    } else {
      console.warn('⚠️ 设备利用率排行图表DOM元素未找到')
    }
    
    if (regionDistributionChart.value) {
      initRegionDistributionChart()
      console.log('✅ 地区分布图表初始化完成')
    } else {
      console.warn('⚠️ 地区分布图表DOM元素未找到')
    }
    
    if (deviceModelChart.value) {
      initDeviceModelChart()
      console.log('✅ 设备型号分析图表初始化完成')
    } else {
      console.warn('⚠️ 设备型号分析图表DOM元素未找到')
    }
    
    console.log('📊 所有图表初始化完成')
  } catch (error) {
    console.error('❌ 图表初始化过程中出错:', error)
  }
}

// 更新所有图表
const updateAllCharts = () => {
  try {
    console.log('📊 开始更新图表数据...')
    
    updateRentalTrendChart()
    console.log('✅ 租赁趋势图表数据更新完成')
    
    updateUtilizationRankingChart()
    console.log('✅ 设备利用率排行图表数据更新完成')
    
    updateRegionDistributionChart()
    console.log('✅ 地区分布图表数据更新完成')
    
    updateDeviceModelChart()
    console.log('✅ 设备型号分析图表数据更新完成')
    
    console.log('📊 所有图表数据更新完成')
  } catch (error) {
    console.error('❌ 图表数据更新过程中出错:', error)
  }
}

// 调整图表大小
const resizeCharts = () => {
  rentalTrendChartInstance?.resize()
  utilizationRankingChartInstance?.resize()
  regionDistributionChartInstance?.resize()
  deviceModelChartInstance?.resize()
}

onMounted(async () => {
  console.log('🚀 租赁分析页面开始初始化...')
  
  // 等待DOM渲染完成
  await nextTick()
  
  // 初始化图表
  try {
    initAllCharts()
    console.log('📊 图表初始化完成')
  } catch (error) {
    console.error('❌ 图表初始化失败:', error)
  }
  
  // 加载数据
  await loadAllData()
  
  // 监听窗口大小变化
  window.addEventListener('resize', resizeCharts)
  
  console.log('✅ 租赁分析页面初始化完成')
})

onUnmounted(() => {
  rentalTrendChartInstance?.dispose()
  utilizationRankingChartInstance?.dispose()
  regionDistributionChartInstance?.dispose()
  deviceModelChartInstance?.dispose()
  
  window.removeEventListener('resize', resizeCharts)
})
</script>

<style lang="scss" scoped>
.rental-analytics {
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
  
  .metrics-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;

    .metric-card {
      background: #ffffff;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      border: 1px solid #f0f0f0;
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 4px;
      }

      &.revenue-card::before {
        background: #10b981;
      }

      &.devices-card::before {
        background: #6366f1;
      }

      &.utilization-card::before {
        background: #f59e0b;
      }

      &.period-card::before {
        background: #ef4444;
      }

      .card-content {
        padding: 20px;
      }

      .card-title {
        font-size: 14px;
        font-weight: 400;
        color: #8c8c8c;
        margin-bottom: 12px;
      }

      .card-value {
        display: flex;
        align-items: baseline;
        margin-bottom: 8px;

        .currency {
          font-size: 20px;
          font-weight: 600;
          color: #262626;
          margin-right: 2px;
        }

        .value-number {
          font-size: 28px;
          font-weight: 600;
          color: #262626;
          line-height: 1;
        }

        .unit {
          font-size: 16px;
          font-weight: 500;
          color: #8c8c8c;
          margin-left: 4px;
        }
      }

      .card-change {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;

        &.positive {
          color: #52c41a;
        }

        &.negative {
          color: #ff4d4f;
        }

        .change-icon {
          font-size: 12px;
        }
      }
    }
  }

  .charts-section {
    margin-bottom: 24px;

    .charts-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      margin-bottom: 20px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .chart-card {
      background: #ffffff;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      border: 1px solid #f0f0f0;

      .chart-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 20px 0;

        .header-left {
          .chart-title {
            margin: 0 0 4px 0;
            color: #262626;
            font-size: 16px;
            font-weight: 600;
          }

          .chart-subtitle {
            margin: 0;
            color: #8c8c8c;
            font-size: 12px;
          }
        }
      }

      .chart-content {
        padding: 20px;

        .chart-container {
          width: 100%;
        }
      }
    }
  }

  .rental-orders-overview {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    padding: 24px;
    margin-bottom: 24px;

    .overview-stats {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 24px;
      margin-top: 16px;

      .stat-item {
        text-align: center;
        padding: 16px;
        background: #fafafa;
        border-radius: 6px;

        .stat-label {
          font-size: 14px;
          color: #8c8c8c;
          margin-bottom: 8px;
        }

        .stat-value {
          font-size: 20px;
          font-weight: 600;
          color: #262626;
        }
      }
    }
  }

  .device-utilization-section {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    padding: 24px;

    .section-header {
      margin-bottom: 20px;

      h3 {
        margin: 0 0 4px 0;
        color: #262626;
        font-size: 18px;
        font-weight: 600;
      }

      p {
        margin: 0;
        color: #8c8c8c;
        font-size: 14px;
      }
    }

    .table-controls {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .controls-left {
        display: flex;
        gap: 12px;
      }
    }

    .utilization-cell {
      .rate-text {
        display: block;
        margin-bottom: 4px;
        font-weight: 500;
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 20px;
      padding: 16px 0;
    }
  }
  }

  // 主要内容区域布局
  .main-content {
    display: flex;
    gap: 20px;
    margin-top: 20px;

    .left-content {
      flex: 1;
      min-width: 0; // 防止flex子项溢出
    }

    .right-sidebar {
      width: 300px;
      flex-shrink: 0;
    }
  }

  // 右侧面板样式
  .right-sidebar {
    .sidebar-card {
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      margin-bottom: 16px;
      overflow: hidden;

      .card-header {
        padding: 16px 20px 12px;
        border-bottom: 1px solid #f0f0f0;
        background: #fafafa;

        h4 {
          margin: 0;
          font-size: 14px;
          font-weight: 600;
          color: #303133;
        }
      }

      .card-content {
        padding: 16px 20px;
      }
    }

    // 今日概览样式
    .overview-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        font-size: 13px;
        color: #606266;
      }

      .value {
        font-size: 14px;
        font-weight: 600;
        color: #303133;
      }
    }

    // 设备状态样式
    .status-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;

      &:last-child {
        margin-bottom: 0;
      }

      .count {
        font-size: 14px;
        font-weight: 600;
        color: #303133;
      }
    }

    // 排行榜样式
    .ranking-item {
      display: flex;
      align-items: center;
      padding: 8px 0;
      border-bottom: 1px solid #f5f5f5;

      &:last-child {
        border-bottom: none;
      }

      .rank {
        width: 24px;
        height: 24px;
        border-radius: 50%;
        background: #409eff;
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        font-weight: 600;
        margin-right: 12px;
        flex-shrink: 0;

        &:nth-child(1) {
          background: #f56c6c;
        }
      }

      .device-info {
        flex: 1;
        min-width: 0;

        .device-id {
          font-size: 13px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 2px;
        }

        .device-model {
          font-size: 11px;
          color: #909399;
        }
      }

      .utilization {
        font-size: 14px;
        font-weight: 600;
        color: #67c23a;
        flex-shrink: 0;
      }
    }
  }

  // 响应式布局
  @media (max-width: 1400px) {
    .main-content {
      .right-sidebar {
        width: 280px;
      }
    }
  }

  @media (max-width: 1200px) {
    .main-content {
      flex-direction: column;

      .right-sidebar {
        width: 100%;
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 16px;

        .sidebar-card {
          margin-bottom: 0;
        }
      }
    }
  }
</style>