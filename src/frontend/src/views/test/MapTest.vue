<template>
  <div class="map-test-page">
    <div class="page-header">
      <h1>地图可视化组件测试</h1>
      <p>测试MapVisualization组件的各种功能和配置</p>
    </div>

    <!-- 基础地图测试 -->
    <el-card class="test-section">
      <template #header>
        <h3>基础热力图测试</h3>
      </template>
      <MapVisualization
        :data="salesData"
        map-type="world"
        visual-type="heatmap"
        title="全球销售热力图"
        height="400px"
        @map-click="handleMapClick"
      />
    </el-card>

    <!-- 散点图测试 -->
    <el-card class="test-section">
      <template #header>
        <h3>散点图测试</h3>
      </template>
      <MapVisualization
        :data="customerData"
        map-type="world"
        visual-type="scatter"
        title="全球客户分布散点图"
        height="400px"
        :show-controls="false"
        @map-click="handleMapClick"
      />
    </el-card>

    <!-- 气泡图测试 -->
    <el-card class="test-section">
      <template #header>
        <h3>气泡图测试</h3>
      </template>
      <MapVisualization
        :data="deviceData"
        map-type="world"
        visual-type="bubble"
        title="全球设备分布气泡图"
        height="400px"
        :color-scheme="customColorScheme"
        @map-click="handleMapClick"
      />
    </el-card>

    <!-- 交互式示例 -->
    <el-card class="test-section">
      <template #header>
        <h3>完整功能示例</h3>
      </template>
      <MapVisualizationExample />
    </el-card>

    <!-- 点击信息显示 -->
    <el-card class="test-section" v-if="clickedData">
      <template #header>
        <h3>点击信息</h3>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="区域名称">
          {{ clickedData.name }}
        </el-descriptions-item>
        <el-descriptions-item label="数据值">
          {{ clickedData.value }}
        </el-descriptions-item>
        <el-descriptions-item label="坐标" v-if="clickedData.coordinates">
          {{ clickedData.coordinates.join(', ') }}
        </el-descriptions-item>
        <el-descriptions-item label="点击时间">
          {{ clickedData.timestamp }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import MapVisualization from '@/components/maps/MapVisualization.vue'
import MapVisualizationExample from '@/components/maps/MapVisualizationExample.vue'
import { formatMapData, generateColorScheme } from '@/utils/mapUtils'

// 测试数据
const rawSalesData = [
  { name: '中国', value: 2580000 },
  { name: '美国', value: 1200000 },
  { name: '日本', value: 890000 },
  { name: '韩国', value: 650000 },
  { name: '德国', value: 580000 },
  { name: '英国', value: 420000 },
  { name: '法国', value: 380000 },
  { name: '加拿大', value: 320000 },
  { name: '澳大利亚', value: 280000 },
  { name: '巴西', value: 250000 },
  { name: '印度', value: 220000 },
  { name: '俄罗斯', value: 180000 }
]

const rawCustomerData = [
  { name: '中国', value: 15600 },
  { name: '美国', value: 8900 },
  { name: '日本', value: 6700 },
  { name: '韩国', value: 4500 },
  { name: '德国', value: 3800 },
  { name: '英国', value: 2900 },
  { name: '法国', value: 2600 },
  { name: '加拿大', value: 2200 },
  { name: '澳大利亚', value: 1800 },
  { name: '巴西', value: 1500 }
]

const rawDeviceData = [
  { name: '中国', value: 45600 },
  { name: '美国', value: 23400 },
  { name: '日本', value: 18900 },
  { name: '韩国', value: 12300 },
  { name: '德国', value: 9800 },
  { name: '英国', value: 7600 },
  { name: '法国', value: 6900 },
  { name: '加拿大', value: 5400 },
  { name: '澳大利亚', value: 4200 },
  { name: '巴西', value: 3600 }
]

// 格式化数据
const salesData = formatMapData(rawSalesData, { addCoordinates: true })
const customerData = formatMapData(rawCustomerData, { addCoordinates: true })
const deviceData = formatMapData(rawDeviceData, { addCoordinates: true })

// 自定义颜色方案
const customColorScheme = generateColorScheme('business', 11)

// 点击数据
const clickedData = ref<any>(null)

// 处理地图点击
const handleMapClick = (data: any) => {
  clickedData.value = {
    ...data,
    timestamp: new Date().toLocaleString()
  }
  console.log('地图点击事件:', data)
}
</script>

<style lang="scss" scoped>
.map-test-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  .page-header {
    text-align: center;
    margin-bottom: 30px;

    h1 {
      color: var(--text-primary);
      margin-bottom: 10px;
    }

    p {
      color: var(--text-secondary);
      font-size: 16px;
    }
  }

  .test-section {
    margin-bottom: 30px;

    :deep(.el-card__header) {
      background: var(--bg-secondary);
      
      h3 {
        margin: 0;
        color: var(--text-primary);
        font-size: 18px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .map-test-page {
    padding: 10px;
    
    .page-header {
      h1 {
        font-size: 24px;
      }
      
      p {
        font-size: 14px;
      }
    }
  }
}
</style>