<template>
  <div class="map-example">
    <el-card class="example-card">

      <!-- 控制面板 -->
      <div class="control-panel">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-select v-model="selectedDataType" @change="handleDataTypeChange">
              <el-option label="销售数据" value="sales" />
              <el-option label="客户分布" value="customers" />
              <el-option label="设备分布" value="devices" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-select v-model="selectedColorScheme" @change="handleColorSchemeChange">
              <el-option label="热力色彩" value="heat" />
              <el-option label="冷色调" value="cool" />
              <el-option label="彩虹色" value="rainbow" />
              <el-option label="商务色彩" value="business" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-input-number
              v-model="mapHeight"
              :min="300"
              :max="800"
              :step="50"
              @change="handleHeightChange"
            />
            <span style="margin-left: 8px;">高度(px)</span>
          </el-col>
          <el-col :span="6">
            <el-switch
              v-model="showControls"
              active-text="显示控件"
              inactive-text="隐藏控件"
            />
          </el-col>
        </el-row>
      </div>

      <!-- 地图组件 -->
      <MapVisualization
        :data="currentMapData"
        :color-scheme="currentColorScheme"
        :height="`${mapHeight}px`"
        :loading="loading"
        :show-controls="showControls"
        :title="currentTitle"
        @map-click="handleMapClick"
        @map-type-change="handleMapTypeChange"
        @visual-type-change="handleVisualTypeChange"
      />

      <!-- 数据详情 -->
      <div class="data-details" v-if="selectedRegion">
        <el-divider>选中区域详情</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="区域名称">
            {{ selectedRegion.name }}
          </el-descriptions-item>
          <el-descriptions-item label="数据值">
            {{ selectedRegion.value }}
          </el-descriptions-item>
          <el-descriptions-item label="坐标" v-if="selectedRegion.coordinates">
            {{ selectedRegion.coordinates.join(', ') }}
          </el-descriptions-item>
          <el-descriptions-item label="数据类型">
            {{ getDataTypeLabel(selectedDataType) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import MapVisualization from './MapVisualization.vue'
import { formatMapData, generateColorScheme, type MapDataPoint } from '@/utils/mapUtils'

// 响应式数据
const loading = ref(false)
const selectedDataType = ref('sales')
const selectedColorScheme = ref('heat')
const mapHeight = ref(500)
const showControls = ref(true)
const selectedRegion = ref<any>(null)

// 模拟数据
const mockData = {
  sales: [
    { name: '中国', value: 2580000, region: 'Asia' },
    { name: '美国', value: 1200000, region: 'North America' },
    { name: '日本', value: 890000, region: 'Asia' },
    { name: '韩国', value: 650000, region: 'Asia' },
    { name: '德国', value: 580000, region: 'Europe' },
    { name: '英国', value: 420000, region: 'Europe' },
    { name: '法国', value: 380000, region: 'Europe' },
    { name: '加拿大', value: 320000, region: 'North America' },
    { name: '澳大利亚', value: 280000, region: 'Oceania' },
    { name: '巴西', value: 250000, region: 'South America' }
  ],
  customers: [
    { name: '中国', value: 15600, region: 'Asia' },
    { name: '美国', value: 8900, region: 'North America' },
    { name: '日本', value: 6700, region: 'Asia' },
    { name: '韩国', value: 4500, region: 'Asia' },
    { name: '德国', value: 3800, region: 'Europe' },
    { name: '英国', value: 2900, region: 'Europe' },
    { name: '法国', value: 2600, region: 'Europe' },
    { name: '加拿大', value: 2200, region: 'North America' },
    { name: '澳大利亚', value: 1800, region: 'Oceania' },
    { name: '巴西', value: 1500, region: 'South America' }
  ],
  devices: [
    { name: '中国', value: 45600, region: 'Asia' },
    { name: '美国', value: 23400, region: 'North America' },
    { name: '日本', value: 18900, region: 'Asia' },
    { name: '韩国', value: 12300, region: 'Asia' },
    { name: '德国', value: 9800, region: 'Europe' },
    { name: '英国', value: 7600, region: 'Europe' },
    { name: '法国', value: 6900, region: 'Europe' },
    { name: '加拿大', value: 5400, region: 'North America' },
    { name: '澳大利亚', value: 4200, region: 'Oceania' },
    { name: '巴西', value: 3600, region: 'South America' }
  ]
}

// 计算属性
const currentMapData = computed(() => {
  const rawData = mockData[selectedDataType.value as keyof typeof mockData]
  return formatMapData(rawData, { addCoordinates: true })
})

const currentColorScheme = computed(() => {
  return generateColorScheme(selectedColorScheme.value as any, 11)
})

const currentTitle = computed(() => {
  const titles = {
    sales: '全球销售分布',
    customers: '全球客户分布', 
    devices: '全球设备分布'
  }
  return titles[selectedDataType.value as keyof typeof titles]
})

// 方法
const handleDataTypeChange = () => {
  selectedRegion.value = null
  refreshData()
}

const handleColorSchemeChange = () => {
  // 颜色方案变化时的处理
}

const handleHeightChange = () => {
  // 高度变化时的处理
}

const handleMapClick = (data: any) => {
  selectedRegion.value = data
  console.log('地图点击:', data)
}

const handleMapTypeChange = (mapType: string) => {
  console.log('地图类型变化:', mapType)
}

const handleVisualTypeChange = (visualType: string) => {
  console.log('可视化类型变化:', visualType)
}

const refreshData = async () => {
  loading.value = true
  
  // 模拟API请求延迟
  await new Promise(resolve => setTimeout(resolve, 1000))
  
  // 为数据添加一些随机变化
  const dataKey = selectedDataType.value as keyof typeof mockData
  mockData[dataKey] = mockData[dataKey].map(item => ({
    ...item,
    value: Math.round(item.value * (0.8 + Math.random() * 0.4))
  }))
  
  loading.value = false
}

const getDataTypeLabel = (type: string) => {
  const labels = {
    sales: '销售额',
    customers: '客户数量',
    devices: '设备数量'
  }
  return labels[type as keyof typeof labels] || type
}

// 组件挂载
onMounted(() => {
  refreshData()
})
</script>

<style lang="scss" scoped>
.map-example {
  padding: 20px;

  .example-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .control-panel {
    margin-bottom: 20px;
    padding: 16px;
    background: var(--bg-secondary);
    border-radius: var(--radius-md);
  }

  .data-details {
    margin-top: 20px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .map-example {
    padding: 10px;
    
    .control-panel {
      .el-row {
        .el-col {
          margin-bottom: 10px;
        }
      }
    }
  }
}
</style>