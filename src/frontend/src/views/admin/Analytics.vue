<template>
  <div class="analytics-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>销售数据分析</h2>
        <p>全球销售数据可视化分析 · 练字机器人管理系统</p>
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
          style="margin-right: 12px;"
        />
      </div>
    </div>

    <!-- 销售统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon revenue">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ salesStats.totalRevenue.toLocaleString() }}</div>
          <div class="stat-label">总销售额</div>
          <div class="stat-trend" :class="salesStats.revenueTrend > 0 ? 'positive' : 'negative'">
            <el-icon><DataLine /></el-icon>
            {{ salesStats.revenueTrend > 0 ? '+' : '' }}{{ salesStats.revenueTrend }}%
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon orders">
          <el-icon><ShoppingCart /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ salesStats.totalOrders.toLocaleString() }}</div>
          <div class="stat-label">订单总数</div>
          <div class="stat-trend" :class="salesStats.ordersTrend > 0 ? 'positive' : 'negative'">
            <el-icon><DataLine /></el-icon>
            {{ salesStats.ordersTrend > 0 ? '+' : '' }}{{ salesStats.ordersTrend }}%
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon customers">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ salesStats.totalCustomers.toLocaleString() }}</div>
          <div class="stat-label">客户总数</div>
          <div class="stat-trend" :class="salesStats.customersTrend > 0 ? 'positive' : 'negative'">
            <el-icon><DataLine /></el-icon>
            {{ salesStats.customersTrend > 0 ? '+' : '' }}{{ salesStats.customersTrend }}%
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon regions">
          <el-icon><Location /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ salesStats.activeRegions }}</div>
          <div class="stat-label">活跃地区</div>
          <div class="stat-trend positive">
            <el-icon><DataLine /></el-icon>
            覆盖全球
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <el-row :gutter="24">
        <!-- 地图可视化 -->
        <el-col :span="16">
          <el-card class="map-card">
            <template #header>
              <div class="card-header">
                <span>全球销售分布</span>
                <div class="map-controls">
                  <el-radio-group v-model="mapDataType" @change="handleMapDataChange" size="small">
                    <el-radio-button value="revenue">销售额</el-radio-button>
                    <el-radio-button value="orders">订单数</el-radio-button>
                    <el-radio-button value="customers">客户数</el-radio-button>
                  </el-radio-group>
                </div>
              </div>
            </template>
            
            <div class="map-container" v-loading="mapLoading">
              <MapVisualization
                ref="mapRef"
                :data="mapData"
                :config="mapConfig"
                @region-click="handleRegionClick"
              />
            </div>
          </el-card>
        </el-col>

        <!-- 地区排行榜 -->
        <el-col :span="8">
          <el-card class="ranking-card">
            <template #header>
              <div class="card-header">
                <span>地区排行榜</span>
                <el-tag type="info" size="small">{{ mapDataType === 'revenue' ? '按销售额' : mapDataType === 'orders' ? '按订单数' : '按客户数' }}</el-tag>
              </div>
            </template>
            
            <div class="ranking-list">
              <div
                v-for="(region, index) in topRegions"
                :key="region.name"
                class="ranking-item"
                :class="{ active: selectedRegion === region.name }"
                @click="handleRegionSelect(region.name)"
              >
                <div class="rank-number" :class="`rank-${index + 1}`">{{ index + 1 }}</div>
                <div class="region-info">
                  <div class="region-name">{{ region.name }}</div>
                  <div class="region-value">
                    {{ mapDataType === 'revenue' ? `¥${region.value.toLocaleString()}` : `${region.value.toLocaleString()}` }}
                  </div>
                </div>
                <div class="region-percentage">
                  {{ ((region.value / totalValue) * 100).toFixed(1) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 销售趋势图表 -->
      <el-row :gutter="24" style="margin-top: 24px;">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>销售趋势</span>
            </template>
            <div class="chart-container" v-loading="chartLoading">
              <SalesTrendChart :data="trendData" />
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>产品销售分布</span>
            </template>
            <div class="chart-container" v-loading="chartLoading">
              <ProductSalesChart :data="productData" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 地区详情弹窗 -->
    <el-dialog
      v-model="regionDetailVisible"
      :title="`${selectedRegion} - 销售详情`"
      width="80%"
      class="region-detail-dialog"
    >
      <div v-if="regionDetail" class="region-detail-content">
        <el-row :gutter="24">
          <el-col :span="8">
            <div class="detail-stat">
              <div class="stat-value">¥{{ regionDetail.revenue.toLocaleString() }}</div>
              <div class="stat-label">销售额</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="detail-stat">
              <div class="stat-value">{{ regionDetail.orders.toLocaleString() }}</div>
              <div class="stat-label">订单数</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="detail-stat">
              <div class="stat-value">{{ regionDetail.customers.toLocaleString() }}</div>
              <div class="stat-label">客户数</div>
            </div>
          </el-col>
        </el-row>
        
        <div class="detail-table" style="margin-top: 24px;">
          <el-table :data="regionDetail.orders_list" stripe>
            <el-table-column prop="orderNumber" label="订单号" width="150" />
            <el-table-column prop="customerName" label="客户名称" width="120" />
            <el-table-column prop="productName" label="产品" width="150" />
            <el-table-column prop="amount" label="金额" width="100">
              <template #default="{ row }">
                ¥{{ row.amount.toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column prop="orderDate" label="订单日期" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'completed' ? 'success' : 'warning'" size="small">
                  {{ row.status === 'completed' ? '已完成' : '处理中' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Money, ShoppingCart, User, Location, DataLine 
} from '@element-plus/icons-vue'
import MapVisualization from '@/components/charts/MapVisualization.vue'
import SalesTrendChart from '@/components/charts/SalesTrendChart.vue'
import ProductSalesChart from '@/components/charts/ProductSalesChart.vue'

// 响应式数据
const loading = ref(false)
const exporting = ref(false)
const mapLoading = ref(false)
const chartLoading = ref(false)
const dateRange = ref<[string, string]>(['2024-01-01', '2024-12-31'])
const mapDataType = ref<'revenue' | 'orders' | 'customers'>('revenue')
const selectedRegion = ref('')
const regionDetailVisible = ref(false)

// 销售统计数据
const salesStats = ref({
  totalRevenue: 12580000,
  totalOrders: 3420,
  totalCustomers: 2180,
  activeRegions: 28,
  revenueTrend: 15.8,
  ordersTrend: 12.3,
  customersTrend: 8.7
})

// 地图数据
const mapData = ref([
  { name: '中国', value: 4580000, orders: 1250, customers: 820 },
  { name: '美国', value: 3200000, orders: 890, customers: 650 },
  { name: '日本', value: 2100000, orders: 580, customers: 420 },
  { name: '韩国', value: 1800000, orders: 520, customers: 380 },
  { name: '德国', value: 900000, orders: 280, customers: 210 },
  { name: '英国', value: 650000, orders: 180, customers: 140 },
  { name: '法国', value: 580000, orders: 160, customers: 120 },
  { name: '加拿大', value: 420000, orders: 120, customers: 95 },
  { name: '澳大利亚', value: 380000, orders: 110, customers: 85 },
  { name: '新加坡', value: 320000, orders: 95, customers: 70 }
])

// 地图配置
const mapConfig = ref({
  type: 'heatmap',
  colorRange: ['#e6f3ff', '#0066cc'],
  showTooltip: true,
  responsive: true
})

// 趋势数据
const trendData = ref([
  { month: '2024-01', revenue: 850000, orders: 280, customers: 180 },
  { month: '2024-02', revenue: 920000, orders: 310, customers: 195 },
  { month: '2024-03', revenue: 1100000, orders: 350, customers: 220 },
  { month: '2024-04', revenue: 980000, orders: 320, customers: 205 },
  { month: '2024-05', revenue: 1250000, orders: 380, customers: 240 },
  { month: '2024-06', revenue: 1180000, orders: 360, customers: 230 },
  { month: '2024-07', revenue: 1350000, orders: 420, customers: 270 },
  { month: '2024-08', revenue: 1280000, orders: 390, customers: 250 },
  { month: '2024-09', revenue: 1420000, orders: 450, customers: 290 },
  { month: '2024-10', revenue: 1380000, orders: 430, customers: 280 },
  { month: '2024-11', revenue: 1520000, orders: 480, customers: 310 },
  { month: '2024-12', revenue: 1680000, orders: 520, customers: 340 }
])

// 产品销售数据
const productData = ref([
  { name: '教育版练字机器人', value: 5200000, percentage: 41.3 },
  { name: '家庭版练字机器人', value: 4100000, percentage: 32.6 },
  { name: '专业版练字机器人', value: 2800000, percentage: 22.3 },
  { name: '配件及耗材', value: 480000, percentage: 3.8 }
])

// 地区详情数据
const regionDetail = ref<any>(null)

// 计算属性
const topRegions = computed(() => {
  const key = mapDataType.value === 'revenue' ? 'value' : mapDataType.value
  return mapData.value
    .map(item => ({
      name: item.name,
      value: item[key as keyof typeof item] as number
    }))
    .sort((a, b) => b.value - a.value)
    .slice(0, 10)
})

const totalValue = computed(() => {
  return topRegions.value.reduce((sum, region) => sum + region.value, 0)
})

// 方法
const refreshData = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('数据刷新成功')
  } catch (error) {
    ElMessage.error('数据刷新失败')
  } finally {
    loading.value = false
  }
}

const exportData = async () => {
  exporting.value = true
  try {
    // 模拟导出功能
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('报告导出成功')
  } catch (error) {
    ElMessage.error('报告导出失败')
  } finally {
    exporting.value = false
  }
}

const handleDateRangeChange = (dates: [string, string] | null) => {
  if (dates) {
    dateRange.value = dates
    refreshData()
  }
}

const handleMapDataChange = () => {
  mapLoading.value = true
  // 模拟数据切换
  setTimeout(() => {
    mapLoading.value = false
  }, 500)
}

const handleRegionClick = (regionName: string) => {
  handleRegionSelect(regionName)
}

const handleRegionSelect = (regionName: string) => {
  selectedRegion.value = regionName
  
  // 模拟获取地区详情数据
  const region = mapData.value.find(item => item.name === regionName)
  if (region) {
    regionDetail.value = {
      revenue: region.value,
      orders: region.orders,
      customers: region.customers,
      orders_list: [
        {
          orderNumber: 'ORD-2024-001',
          customerName: '张三',
          productName: '教育版练字机器人',
          amount: 12800,
          orderDate: '2024-02-15',
          status: 'completed'
        },
        {
          orderNumber: 'ORD-2024-002',
          customerName: '李四',
          productName: '家庭版练字机器人',
          amount: 8900,
          orderDate: '2024-02-16',
          status: 'completed'
        },
        {
          orderNumber: 'ORD-2024-003',
          customerName: '王五',
          productName: '专业版练字机器人',
          amount: 15600,
          orderDate: '2024-02-17',
          status: 'processing'
        }
      ]
    }
    regionDetailVisible.value = true
  }
}

// 生命周期
onMounted(() => {
  refreshData()
})
</script>

<style lang="scss" scoped>
.analytics-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    
    .header-left {
      h2 {
        font-size: 24px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0 0 4px 0;
      }
      
      p {
        color: var(--text-secondary);
        margin: 0;
        font-size: 14px;
      }
    }
    
    .header-right {
      display: flex;
      align-items: center;
      gap: 12px;
    }
  }

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 20px;
    margin-bottom: 24px;
    
    .stat-card {
      background: white;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      display: flex;
      align-items: center;
      gap: 16px;
      transition: transform 0.2s ease, box-shadow 0.2s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
      }
      
      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 24px;
        
        &.revenue {
          background: linear-gradient(135deg, #67C23A, #85CE61);
        }
        
        &.orders {
          background: linear-gradient(135deg, #409EFF, #66B1FF);
        }
        
        &.customers {
          background: linear-gradient(135deg, #E6A23C, #EEBE77);
        }
        
        &.regions {
          background: linear-gradient(135deg, #F56C6C, #F78989);
        }
      }
      
      .stat-content {
        flex: 1;
        
        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: var(--text-primary);
          margin-bottom: 4px;
        }
        
        .stat-label {
          font-size: 14px;
          color: var(--text-secondary);
          margin-bottom: 8px;
        }
        
        .stat-trend {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 12px;
          font-weight: 500;
          
          &.positive {
            color: #67C23A;
          }
          
          &.negative {
            color: #F56C6C;
          }
        }
      }
    }
  }

  .main-content {
    .map-card, .ranking-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .map-controls {
          .el-radio-group {
            --el-radio-button-checked-bg-color: var(--primary-color);
            --el-radio-button-checked-text-color: white;
          }
        }
      }
      
      .map-container {
        height: 400px;
        width: 100%;
      }
    }
    
    .ranking-card {
      .ranking-list {
        max-height: 400px;
        overflow-y: auto;
        
        .ranking-item {
          display: flex;
          align-items: center;
          padding: 12px;
          border-radius: 8px;
          margin-bottom: 8px;
          cursor: pointer;
          transition: all 0.2s ease;
          
          &:hover {
            background: var(--bg-secondary);
          }
          
          &.active {
            background: var(--primary-color-light);
            border: 1px solid var(--primary-color);
          }
          
          .rank-number {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            color: white;
            margin-right: 12px;
            
            &.rank-1 {
              background: linear-gradient(135deg, #FFD700, #FFA500);
            }
            
            &.rank-2 {
              background: linear-gradient(135deg, #C0C0C0, #A9A9A9);
            }
            
            &.rank-3 {
              background: linear-gradient(135deg, #CD7F32, #B8860B);
            }
            
            &:not(.rank-1):not(.rank-2):not(.rank-3) {
              background: linear-gradient(135deg, #909399, #B3B6BC);
            }
          }
          
          .region-info {
            flex: 1;
            
            .region-name {
              font-weight: 500;
              color: var(--text-primary);
              margin-bottom: 2px;
            }
            
            .region-value {
              font-size: 14px;
              color: var(--text-secondary);
            }
          }
          
          .region-percentage {
            font-size: 12px;
            color: var(--text-secondary);
            font-weight: 500;
          }
        }
      }
    }
    
    .chart-container {
      height: 300px;
      width: 100%;
    }
  }

  .region-detail-dialog {
    .region-detail-content {
      .detail-stat {
        text-align: center;
        padding: 20px;
        background: var(--bg-secondary);
        border-radius: 8px;
        
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: var(--primary-color);
          margin-bottom: 8px;
        }
        
        .stat-label {
          font-size: 14px;
          color: var(--text-secondary);
        }
      }
      
      .detail-table {
        .el-table {
          border-radius: 8px;
          overflow: hidden;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .analytics-page {
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
      
      .header-right {
        width: 100%;
        justify-content: flex-start;
        flex-wrap: wrap;
      }
    }
    
    .stats-cards {
      grid-template-columns: 1fr;
    }
    
    .main-content {
      .el-row {
        .el-col {
          margin-bottom: 24px;
        }
      }
    }
  }
}
</style>