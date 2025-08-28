<template>
  <div class="dashboard" style="width: 100%; max-width: 100%; margin: 0; padding: 24px 8px;">
    <!-- 页面标题和操作 -->
    <div class="dashboard-header">
      <div class="header-left">
        <h2>销售租赁数据</h2>
        <p class="header-subtitle">销售数据分析 · 练字机器人管理系统</p>
      </div>
    </div>

    <!-- 核心业务指标卡片 -->
    <div class="metrics-cards">
      <div class="metric-card sales-card">
        <div class="card-content">
          <div class="card-title">销售总额</div>
          <div class="card-value">
            <span class="currency">¥</span>
            <CountUp :value="businessMetrics.totalSales" :decimals="2" class="value-number" />
          </div>
          <div class="card-change positive">
            <el-icon class="change-icon"><ArrowUp /></el-icon>
            <span class="change-text">+{{ businessMetrics.salesChange }}%</span>
          </div>
        </div>
      </div>
      
      <div class="metric-card rental-card">
        <div class="card-content">
          <div class="card-title">租赁收入</div>
          <div class="card-value">
            <span class="currency">¥</span>
            <CountUp :value="businessMetrics.totalRental" :decimals="2" class="value-number" />
          </div>
          <div class="card-change positive">
            <el-icon class="change-icon"><ArrowUp /></el-icon>
            <span class="change-text">+{{ businessMetrics.rentalChange }}%</span>
          </div>
        </div>
      </div>
      
      <div class="metric-card orders-card">
        <div class="card-content">
          <div class="card-title">订单数量</div>
          <div class="card-value">
            <CountUp :value="businessMetrics.totalOrders" :decimals="0" class="value-number" />
          </div>
          <div class="card-change positive">
            <el-icon class="change-icon"><ArrowUp /></el-icon>
            <span class="change-text">+{{ businessMetrics.ordersChange }}%</span>
          </div>
        </div>
      </div>
      
      <div class="metric-card customers-card">
        <div class="card-content">
          <div class="card-title">新客户</div>
          <div class="card-value">
            <CountUp :value="businessMetrics.newCustomers" :decimals="0" class="value-number" />
          </div>
          <div class="card-change positive">
            <el-icon class="change-icon"><ArrowUp /></el-icon>
            <span class="change-text">+{{ businessMetrics.customersChange }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 设备状态概览 -->
    <div class="device-status-section" v-if="dashboardStats">
      <div class="section-header">
        <h3>设备状态概览</h3>
        <p>实时设备运行状态监控</p>
      </div>
      <div class="device-status-cards">
        <div class="status-card online">
          <div class="status-icon">🟢</div>
          <div class="status-info">
            <div class="status-title">在线</div>
            <div class="status-count">{{ dashboardStats.devices.online }}</div>
          </div>
        </div>
        <div class="status-card offline">
          <div class="status-icon">🔴</div>
          <div class="status-info">
            <div class="status-title">离线</div>
            <div class="status-count">{{ dashboardStats.devices.offline }}</div>
          </div>
        </div>
        <div class="status-card maintenance">
          <div class="status-icon">🔧</div>
          <div class="status-info">
            <div class="status-title">维护中</div>
            <div class="status-count">{{ dashboardStats.devices.maintenance }}</div>
          </div>
        </div>
        <div class="status-card error">
          <div class="status-icon">⚠️</div>
          <div class="status-info">
            <div class="status-title">故障</div>
            <div class="status-count">{{ dashboardStats.devices.error }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section" style="width: 100%; max-width: 100%; margin: 0;">
      <!-- 第一行：销售趋势分析和销售渠道分析 -->
      <div class="charts-row two-column-layout">
        <!-- 销售租赁趋势分析卡片 -->
        <div class="chart-card sales-trend-card" style="width: 100%; min-width: 0;">
          <div class="chart-header">
            <div class="header-left">
              <h3 class="chart-title">销售租赁趋势分析</h3>
              <p class="chart-subtitle">各月销售租赁数据对比分析</p>
            </div>
            <div class="header-right">
              <el-button text :icon="MoreFilled" />
            </div>
          </div>
          <div class="chart-content">
            <div class="chart-legend">
              <div class="legend-item">
                <span class="legend-dot sales-dot"></span>
                <span class="legend-text">销售额</span>
              </div>
              <div class="legend-item">
                <span class="legend-dot orders-dot"></span>
                <span class="legend-text">订单数</span>
              </div>
            </div>
            <div ref="salesTrendChart" class="chart-container" style="height: 320px; width: 100%;"></div>
          </div>
        </div>

        <!-- 销售渠道分析卡片 -->
        <div class="chart-card channel-analysis-card" style="width: 100%; min-width: 0;">
          <div class="chart-header">
            <div class="header-left">
              <h3 class="chart-title">销售租赁渠道分析</h3>
              <p class="chart-subtitle">各渠道租赁销售数据对比</p>
            </div>
            <div class="header-right">
              <el-button text :icon="MoreFilled" />
            </div>
          </div>
          <div class="chart-content">
            <div class="channel-legend">
              <div class="legend-item">
                <span class="legend-dot online-dot"></span>
                <span class="legend-text">线上渠道</span>
              </div>
              <div class="legend-item">
                <span class="legend-dot offline-dot"></span>
                <span class="legend-text">线下渠道</span>
              </div>
              <div class="legend-item">
                <span class="legend-dot partner-dot"></span>
                <span class="legend-text">合作伙伴</span>
              </div>
            </div>
            <div ref="channelAnalysisChart" class="chart-container" style="height: 320px; width: 100%;"></div>
          </div>
        </div>
      </div>

      <!-- 第二行：业务分布地图可视化 -->
      <div class="charts-row single-column-layout">
        <div class="chart-card map-visualization-card" style="width: 100%; min-width: 0;">
          <DashboardMapVisualization ref="mapVisualizationRef" />
        </div>
      </div>
    </div>

    <!-- 最近活动 -->
    <div class="recent-activities-section" v-if="recentActivities.length > 0">
      <div class="section-header">
        <h3>最近活动</h3>
        <p>系统最新动态和操作记录</p>
      </div>
      <div class="activities-list">
        <div 
          v-for="activity in recentActivities.slice(0, 8)" 
          :key="activity.id"
          class="activity-item"
          :class="activity.status"
        >
          <div class="activity-icon">
            <span v-if="activity.type === 'order'">📋</span>
            <span v-else-if="activity.type === 'device'">🤖</span>
            <span v-else-if="activity.type === 'course'">📚</span>
            <span v-else-if="activity.type === 'fontPackage'">🎨</span>
            <span v-else-if="activity.type === 'translation'">🌐</span>
            <span v-else>ℹ️</span>
          </div>
          <div class="activity-content">
            <div class="activity-title">{{ activity.title }}</div>
            <div class="activity-description">{{ activity.description }}</div>
            <div class="activity-meta">
              <span class="activity-user" v-if="activity.user">{{ activity.user }}</span>
              <span class="activity-time">{{ formatTime(activity.timestamp) }}</span>
            </div>
          </div>
          <div class="activity-status" :class="activity.status">
            <span v-if="activity.status === 'success'">✅</span>
            <span v-else-if="activity.status === 'warning'">⚠️</span>
            <span v-else-if="activity.status === 'error'">❌</span>
            <span v-else>ℹ️</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import {
  MoreFilled,
  ArrowUp
} from '@element-plus/icons-vue'
import { CountUp } from '@/components/common'
import DashboardMapVisualization from '@/components/dashboard/DashboardMapVisualization.vue'
import * as echarts from 'echarts'
import { mockDashboardAPI } from '@/api/mock'
import type { DashboardStats, RecentActivity, TrendData, TopPerformers } from '@/api/mock/dashboard'

// 响应式数据
const dateRange = ref<[string, string]>(['2024-01-01', '2024-01-31'])
const refreshLoading = ref(false)

// 图表实例
const salesTrendChart = ref<HTMLElement>()
const regionDistributionChart = ref<HTMLElement>()
const channelAnalysisChart = ref<HTMLElement>()
const mapVisualizationRef = ref()

let salesTrendChartInstance: echarts.ECharts | null = null
let regionDistributionChartInstance: echarts.ECharts | null = null
let channelAnalysisChartInstance: echarts.ECharts | null = null

// 仪表板数据
const dashboardStats = ref<DashboardStats | null>(null)
const recentActivities = ref<RecentActivity[]>([])
const trendData = ref<TrendData[]>([])
const topPerformers = ref<TopPerformers | null>(null)

// 核心业务指标数据（从dashboardStats计算得出）
const businessMetrics = ref({
  totalSales: 1250000.00,
  salesChange: 12.5,
  totalRental: 385000.00,
  rentalChange: 8.7,
  totalOrders: 3680,
  ordersChange: 8.3,
  newCustomers: 456,
  customersChange: 15.2
})

// 处理日期范围变化
const handleDateRangeChange = (dates: [string, string]) => {
  console.log('日期范围变化:', dates)
  loadDashboardData()
}

// 导出报表
const exportReport = () => {
  console.log('导出报表')
}

// 刷新数据
const refreshData = async () => {
  refreshLoading.value = true
  try {
    await loadDashboardData()
    // 同时刷新地图数据
    if (mapVisualizationRef.value) {
      await mapVisualizationRef.value.refreshData()
    }
  } finally {
    setTimeout(() => {
      refreshLoading.value = false
    }, 1000)
  }
}

// 格式化时间
const formatTime = (timestamp: string) => {
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric'
  })
}

// 加载仪表板数据
const loadDashboardData = async () => {
  try {
    // 并行加载所有数据
    const [statsRes, activitiesRes, trendRes, performersRes] = await Promise.all([
      mockDashboardAPI.getDashboardStats(),
      mockDashboardAPI.getRecentActivities({ pageSize: 10 }),
      mockDashboardAPI.getTrendData({ days: 30 }),
      mockDashboardAPI.getTopPerformers()
    ])
    
    // 更新数据
    dashboardStats.value = statsRes.data
    recentActivities.value = activitiesRes.data.list
    trendData.value = trendRes.data
    topPerformers.value = performersRes.data
    
    // 更新业务指标显示
    if (dashboardStats.value) {
      const totalRevenue = dashboardStats.value.business.totalRevenue
      const rentalRevenue = Math.round(totalRevenue * 0.25) // 租赁收入约占总收入的25%
      const salesRevenue = totalRevenue - rentalRevenue
      
      businessMetrics.value = {
        totalSales: salesRevenue,
        salesChange: dashboardStats.value.business.revenueGrowth,
        totalRental: rentalRevenue,
        rentalChange: dashboardStats.value.rental?.rentalGrowth || Math.round(Math.random() * 15) + 5,
        totalOrders: dashboardStats.value.business.totalOrders,
        ordersChange: dashboardStats.value.business.orderGrowth,
        newCustomers: dashboardStats.value.business.totalCustomers - dashboardStats.value.business.activeCustomers,
        customersChange: Math.round(Math.random() * 20) + 5 // 模拟新客户增长率
      }
    }
    
    // 更新图表数据
    updateCharts()
    
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

// 初始化销售趋势图表
const initSalesTrendChart = () => {
  if (!salesTrendChart.value) return
  
  salesTrendChartInstance = echarts.init(salesTrendChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function(params: any) {
        let result = params[0].name + '<br/>'
        params.forEach((item: any) => {
          result += item.marker + item.seriesName + ': ¥' + item.value.toLocaleString() + '<br/>'
        })
        return result
      }
    },
    legend: {
      data: ['销售收入', '租赁收入'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月'],
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0'
        }
      },
      axisLabel: {
        formatter: function(value: number) {
          return '¥' + (value / 10000).toFixed(0) + 'w'
        }
      }
    },
    series: [
      {
        name: '销售收入',
        type: 'bar',
        data: [185000, 198000, 165000, 220000, 245000, 268000, 285000],
        itemStyle: {
          color: '#6366f1',
          borderRadius: [4, 4, 0, 0]
        },
        barWidth: '30%'
      },
      {
        name: '租赁收入',
        type: 'bar',
        data: [45000, 52000, 48000, 58000, 65000, 72000, 78000],
        itemStyle: {
          color: '#10b981',
          borderRadius: [4, 4, 0, 0]
        },
        barWidth: '30%'
      }
    ]
  }
  
  salesTrendChartInstance.setOption(option)
}



// 初始化地区分布图表
const initRegionDistributionChart = () => {
  if (!regionDistributionChart.value) return
  
  regionDistributionChartInstance = echarts.init(regionDistributionChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: function(params: any) {
        return `${params.seriesName}<br/>${params.name}: ${params.value}人 (${params.percent}%)`
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: ['中国大陆', '美国', '日本', '韩国', '新加坡', '其他地区']
    },
    series: [
      {
        name: '客户地区分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        data: [
          { value: 1248, name: '中国大陆' },
          { value: 435, name: '美国' },
          { value: 380, name: '日本' },
          { value: 284, name: '韩国' },
          { value: 156, name: '新加坡' },
          { value: 197, name: '其他地区' }
        ],
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{c}人 ({d}%)',
          fontSize: 12
        },
        labelLine: {
          show: true,
          length: 15,
          length2: 10
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ],
    color: ['#6366f1', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4']
  }
  
  regionDistributionChartInstance.setOption(option)
}

// 初始化渠道分析图表
const initChannelAnalysisChart = () => {
  if (!channelAnalysisChart.value) return
  
  channelAnalysisChartInstance = echarts.init(channelAnalysisChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月'],
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#666'
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0'
        }
      },
      axisLabel: {
        color: '#666'
      }
    },
    series: [
      {
        name: '线上渠道',
        type: 'line',
        data: [120, 132, 101, 134, 90, 230],
        smooth: true,
        itemStyle: {
          color: '#5470c6'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(84, 112, 198, 0.3)' },
              { offset: 1, color: 'rgba(84, 112, 198, 0.05)' }
            ]
          }
        }
      },
      {
        name: '线下渠道',
        type: 'line',
        data: [220, 182, 191, 234, 290, 330],
        smooth: true,
        itemStyle: {
          color: '#91cc75'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(145, 204, 117, 0.3)' },
              { offset: 1, color: 'rgba(145, 204, 117, 0.05)' }
            ]
          }
        }
      },
      {
        name: '合作伙伴',
        type: 'line',
        data: [150, 232, 201, 154, 190, 330],
        smooth: true,
        itemStyle: {
          color: '#fac858'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(250, 200, 88, 0.3)' },
              { offset: 1, color: 'rgba(250, 200, 88, 0.05)' }
            ]
          }
        }
      }
    ]
  }
  
  channelAnalysisChartInstance.setOption(option)
}

// 更新所有图表
const updateCharts = () => {
  if (trendData.value.length > 0) {
    updateSalesTrendChart()
  }
  if (topPerformers.value) {
    updateRegionDistributionChart()
  }
  updateChannelAnalysisChart()
}

// 更新销售趋势图表
const updateSalesTrendChart = () => {
  if (!salesTrendChartInstance || !trendData.value.length) return
  
  const dates = trendData.value.map(item => {
    const date = new Date(item.date)
    return `${date.getMonth() + 1}/${date.getDate()}`
  })
  const revenues = trendData.value.map(item => item.revenue)
  const orders = trendData.value.map(item => item.orders * 1000) // 转换为金额显示
  
  salesTrendChartInstance.setOption({
    xAxis: {
      data: dates.slice(-7) // 显示最近7天
    },
    series: [
      {
        name: '销售收入',
        data: revenues.slice(-7)
      },
      {
        name: '租赁收入',
        data: orders.slice(-7)
      }
    ]
  })
}



// 更新地区分布图表
const updateRegionDistributionChart = () => {
  if (!regionDistributionChartInstance || !dashboardStats.value) return
  
  // 使用模拟的地区分布数据
  const regionData = [
    { value: Math.round(dashboardStats.value.business.totalCustomers * 0.45), name: '中国大陆' },
    { value: Math.round(dashboardStats.value.business.totalCustomers * 0.20), name: '美国' },
    { value: Math.round(dashboardStats.value.business.totalCustomers * 0.15), name: '日本' },
    { value: Math.round(dashboardStats.value.business.totalCustomers * 0.10), name: '韩国' },
    { value: Math.round(dashboardStats.value.business.totalCustomers * 0.06), name: '新加坡' },
    { value: Math.round(dashboardStats.value.business.totalCustomers * 0.04), name: '其他地区' }
  ]
  
  regionDistributionChartInstance.setOption({
    series: [
      {
        data: regionData
      }
    ]
  })
}

// 更新渠道分析图表
const updateChannelAnalysisChart = () => {
  if (!channelAnalysisChartInstance || !trendData.value.length) return
  
  const dates = trendData.value.slice(-6).map(item => {
    const date = new Date(item.date)
    return `${date.getMonth() + 1}月`
  })
  
  // 基于趋势数据生成渠道数据
  const onlineData = trendData.value.slice(-6).map(item => Math.round(item.orders * 0.4))
  const offlineData = trendData.value.slice(-6).map(item => Math.round(item.orders * 0.45))
  const partnerData = trendData.value.slice(-6).map(item => Math.round(item.orders * 0.15))
  
  channelAnalysisChartInstance.setOption({
    xAxis: {
      data: dates
    },
    series: [
      {
        name: '线上渠道',
        data: onlineData
      },
      {
        name: '线下渠道',
        data: offlineData
      },
      {
        name: '合作伙伴',
        data: partnerData
      }
    ]
  })
}

// 初始化所有图表
const initAllCharts = () => {
  initSalesTrendChart()
  initRegionDistributionChart()
  initChannelAnalysisChart()
}

// 调整图表大小
const resizeCharts = () => {
  salesTrendChartInstance?.resize()
  regionDistributionChartInstance?.resize()
  channelAnalysisChartInstance?.resize()
}

onMounted(async () => {
  await nextTick()
  await loadDashboardData()
  
  // 确保数据加载完成后再初始化图表
  await nextTick()
  initAllCharts()
  
  // 监听窗口大小变化
  window.addEventListener('resize', resizeCharts)
})

onUnmounted(() => {
  // 销毁图表实例
  salesTrendChartInstance?.dispose()
  regionDistributionChartInstance?.dispose()
  channelAnalysisChartInstance?.dispose()
  
  // 移除事件监听
  window.removeEventListener('resize', resizeCharts)
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 24px 8px;
  background: #f5f5f5;
  min-height: 100vh;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  margin: 0;

  .dashboard-header {
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

      :deep(.el-date-editor) {
        background: #ffffff;
        border: 1px solid #d9d9d9;
        border-radius: 6px;
        font-size: 14px;

        &:hover {
          border-color: #40a9ff;
        }

        &:focus-within {
          border-color: #1890ff;
          box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
        }
      }

      :deep(.el-button) {
        border-radius: 6px;
        font-weight: 400;
        padding: 8px 16px;
        font-size: 14px;
        
        &.el-button--primary {
          background: #1890ff;
          border-color: #1890ff;

          &:hover {
            background: #40a9ff;
            border-color: #40a9ff;
          }
        }

        &:not(.el-button--primary) {
          background: #ffffff;
          border: 1px solid #d9d9d9;
          color: #262626;

          &:hover {
            border-color: #40a9ff;
            color: #1890ff;
          }
        }
      }
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

      &.sales-card::before {
        background: #ff4757;
      }

      &.rental-card::before {
        background: #10b981;
      }

      &.orders-card::before {
        background: #2ed573;
      }

      &.customers-card::before {
        background: #ffa502;
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

  .device-status-section {
    margin-bottom: 24px;

    .section-header {
      margin-bottom: 16px;

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

    .device-status-cards {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;

      .status-card {
        background: #ffffff;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        border: 1px solid #f0f0f0;
        padding: 20px;
        display: flex;
        align-items: center;
        gap: 12px;
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          transform: translateY(-2px);
        }

        .status-icon {
          font-size: 24px;
        }

        .status-info {
          .status-title {
            font-size: 14px;
            color: #8c8c8c;
            margin-bottom: 4px;
          }

          .status-count {
            font-size: 24px;
            font-weight: 600;
            color: #262626;
          }
        }

        &.active {
          border-left: 4px solid #52c41a;
        }

        &.idle {
          border-left: 4px solid #faad14;
        }

        &.maintenance {
          border-left: 4px solid #1890ff;
        }

        &.offline {
          border-left: 4px solid #ff4d4f;
        }
      }
    }
  }

  .recent-activities-section {
    margin-bottom: 24px;

    .section-header {
      margin-bottom: 16px;

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

    .activities-list {
      background: #ffffff;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      border: 1px solid #f0f0f0;
      overflow: hidden;

      .activity-item {
        display: flex;
        align-items: center;
        padding: 16px 20px;
        border-bottom: 1px solid #f0f0f0;
        transition: background-color 0.3s ease;

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          background-color: #fafafa;
        }

        .activity-icon {
          font-size: 20px;
          margin-right: 12px;
          flex-shrink: 0;
        }

        .activity-content {
          flex: 1;
          min-width: 0;

          .activity-title {
            font-size: 14px;
            font-weight: 500;
            color: #262626;
            margin-bottom: 4px;
          }

          .activity-description {
            font-size: 12px;
            color: #8c8c8c;
            margin-bottom: 6px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .activity-meta {
            display: flex;
            gap: 12px;
            font-size: 12px;
            color: #bfbfbf;

            .activity-user {
              font-weight: 500;
            }
          }
        }

        .activity-status {
          font-size: 16px;
          flex-shrink: 0;
          margin-left: 12px;
        }
      }
    }
  }
  
  .charts-section {
    width: 100%;
    max-width: 100%;
    margin: 0;
    
    .charts-row {
      display: grid;
      gap: 20px;
      margin-bottom: 24px;
      width: 100%;
      max-width: 100%;

      &.two-column-layout {
        grid-template-columns: repeat(2, 1fr);
      }

      &.single-column-layout {
        grid-template-columns: 1fr;
      }

      .chart-card {
        background: #ffffff;
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        border: 1px solid #f0f0f0;
        overflow: hidden;
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
          transform: translateY(-2px);
        }

        .chart-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 20px 24px 16px;
          border-bottom: 1px solid #f0f0f0;

          .header-left {
            .chart-title {
              margin: 0 0 4px 0;
              color: #262626;
              font-size: 18px;
              font-weight: 600;
            }

            .chart-subtitle {
              margin: 0;
              color: #8c8c8c;
              font-size: 14px;
              font-weight: 400;
            }
          }

          .header-right {
            :deep(.el-button) {
              color: #8c8c8c;
              
              &:hover {
                color: #1890ff;
              }
            }
          }
        }

        .chart-content {
          padding: 20px 24px 24px;

          .chart-legend, .channel-legend {
            display: flex;
            gap: 20px;
            margin-bottom: 16px;
            flex-wrap: wrap;

            .legend-item {
              display: flex;
              align-items: center;
              gap: 8px;
              font-size: 14px;

              .legend-dot {
                width: 12px;
                height: 12px;
                border-radius: 50%;
                flex-shrink: 0;

                &.sales-dot {
                  background: #6366f1;
                }

                &.orders-dot {
                  background: #10b981;
                }

                &.online-dot {
                  background: #5470c6;
                }

                &.offline-dot {
                  background: #91cc75;
                }

                &.partner-dot {
                  background: #fac858;
                }
              }

              .legend-text {
                color: #595959;
                font-weight: 500;
              }
            }
          }

          .chart-container {
            width: 100%;
            min-height: 300px;
          }
        }

        &.sales-trend-card {
          .chart-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-bottom: none;

            .chart-title, .chart-subtitle {
              color: white;
            }

            .header-right :deep(.el-button) {
              color: rgba(255, 255, 255, 0.8);
              
              &:hover {
                color: white;
              }
            }
          }
        }

        &.channel-analysis-card {
          .chart-header {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            border-bottom: none;

            .chart-title, .chart-subtitle {
              color: white;
            }

            .header-right :deep(.el-button) {
              color: rgba(255, 255, 255, 0.8);
              
              &:hover {
                color: white;
              }
            }
          }
        }

        &.map-visualization-card {
          .chart-header {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            border-bottom: none;

            .chart-title, .chart-subtitle {
              color: white;
            }
          }
        }
      }
      
      &:last-child {
        margin-bottom: 0;
      }

      &.horizontal-layout {
        grid-template-columns: repeat(3, 1fr);
        gap: 16px;
      }

      &.two-column-layout {
        grid-template-columns: repeat(2, 1fr);
      }

      &.single-column-layout {
        grid-template-columns: 1fr;
      }

      .chart-card {
        background: #ffffff;
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        border: 1px solid #f0f0f0;
        overflow: hidden;
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
          transform: translateY(-2px);
        }

        .chart-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 20px 24px 16px;
          border-bottom: 1px solid #f0f0f0;

          .header-left {
            .chart-title {
              margin: 0 0 4px 0;
              color: #262626;
              font-size: 18px;
              font-weight: 600;
            }

            .chart-subtitle {
              margin: 0;
              color: #8c8c8c;
              font-size: 14px;
              font-weight: 400;
            }
          }

          .header-right {
            :deep(.el-button) {
              color: #8c8c8c;
              
              &:hover {
                color: #1890ff;
              }
            }
          }
        }

        .chart-content {
          padding: 20px 24px 24px;

          .chart-legend, .channel-legend {
            display: flex;
            gap: 20px;
            margin-bottom: 16px;
            flex-wrap: wrap;

            .legend-item {
              display: flex;
              align-items: center;
              gap: 8px;
              font-size: 14px;

              .legend-dot {
                width: 12px;
                height: 12px;
                border-radius: 50%;
                flex-shrink: 0;

                &.sales-dot {
                  background: #6366f1;
                }

                &.orders-dot {
                  background: #10b981;
                }

                &.online-dot {
                  background: #5470c6;
                }

                &.offline-dot {
                  background: #91cc75;
                }

                &.partner-dot {
                  background: #fac858;
                }
              }

              .legend-text {
                color: #595959;
                font-weight: 500;
              }
            }
          }

          .chart-container {
            width: 100%;
            min-height: 300px;
          }
        }

        &.sales-trend-card {
          .chart-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-bottom: none;

            .chart-title, .chart-subtitle {
              color: white;
            }

            .header-right :deep(.el-button) {
              color: rgba(255, 255, 255, 0.8);
              
              &:hover {
                color: white;
              }
            }
          }
        }

        &.channel-analysis-card {
          .chart-header {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            border-bottom: none;

            .chart-title, .chart-subtitle {
              color: white;
            }

            .header-right :deep(.el-button) {
              color: rgba(255, 255, 255, 0.8);
              
              &:hover {
                color: white;
              }
            }
          }
        }

        &.map-visualization-card {
          .chart-header {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            border-bottom: none;

            .chart-title, .chart-subtitle {
              color: white;
            }
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .dashboard {
    .metrics-cards {
      grid-template-columns: repeat(2, 1fr);
    }

    .device-status-section {
      .device-status-cards {
        grid-template-columns: repeat(2, 1fr);
      }
    }
    
    .charts-section {
      .charts-row {
        grid-template-columns: 1fr;

        &.horizontal-layout {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}

@media (max-width: 1024px) {
  .dashboard {
    .charts-section {
      .charts-row {
        &.horizontal-layout {
          grid-template-columns: repeat(2, 1fr);
        }

        &.two-column-layout {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 16px 8px;

    .dashboard-header {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;
      
      .header-right {
        justify-content: flex-end;
      }
    }
    
    .metrics-cards {
      grid-template-columns: 1fr;
      gap: 16px;
    }

    .device-status-section {
      .device-status-cards {
        grid-template-columns: 1fr;
      }
    }

    .charts-section {
      .charts-row {
        grid-template-columns: 1fr;
        gap: 12px;

        &.horizontal-layout {
          grid-template-columns: 1fr;
        }

        &.two-column-layout {
          grid-template-columns: 1fr;
          gap: 8px;
        }
      }
    }
  }
}
</style>