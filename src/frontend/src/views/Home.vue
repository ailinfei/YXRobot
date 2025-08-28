<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card">
          <template #header>
            <div class="card-header">
              <span>欢迎使用 YXRobot 练字机器人管理系统</span>
            </div>
          </template>
          
          <div class="welcome-content">
            <el-row :gutter="40">
              <el-col :span="12">
                <div class="feature-section">
                  <h3>🤖 智能练字机器人</h3>
                  <p>专业的练字机器人管理平台，提供设备监控、产品管理、客户服务等全方位功能。</p>
                  
                  <div class="features">
                    <div class="feature-item">
                      <el-icon><Monitor /></el-icon>
                      <span>设备实时监控</span>
                    </div>
                    <div class="feature-item">
                      <el-icon><Box /></el-icon>
                      <span>产品管理</span>
                    </div>
                    <div class="feature-item">
                      <el-icon><User /></el-icon>
                      <span>客户服务</span>
                    </div>
                    <div class="feature-item">
                      <el-icon><DataAnalysis /></el-icon>
                      <span>数据分析</span>
                    </div>
                  </div>
                </div>
              </el-col>
              
              <el-col :span="12">
                <div class="stats-section">
                  <h3>📊 系统状态</h3>
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-statistic title="在线设备" :value="stats.onlineDevices" />
                    </el-col>
                    <el-col :span="12">
                      <el-statistic title="产品总数" :value="stats.totalProducts" />
                    </el-col>
                    <el-col :span="12">
                      <el-statistic title="活跃用户" :value="stats.activeUsers" />
                    </el-col>
                    <el-col :span="12">
                      <el-statistic title="今日订单" :value="stats.todayOrders" />
                    </el-col>
                  </el-row>
                </div>
              </el-col>
            </el-row>
            
            <div class="action-section">
              <el-button type="primary" size="large" @click="$router.push('/admin')">
                <el-icon><Setting /></el-icon>
                进入管理后台
              </el-button>
              <el-button type="success" size="large" @click="$router.push('/products')">
                <el-icon><Box /></el-icon>
                查看产品
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Monitor, Box, User, DataAnalysis, Setting } from '@element-plus/icons-vue'

const stats = ref({
  onlineDevices: 0,
  totalProducts: 0,
  activeUsers: 0,
  todayOrders: 0
})

onMounted(async () => {
  // 模拟加载统计数据
  setTimeout(() => {
    stats.value = {
      onlineDevices: 156,
      totalProducts: 8,
      activeUsers: 1234,
      todayOrders: 23
    }
  }, 1000)
})
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.welcome-content {
  padding: 20px 0;
}

.feature-section h3,
.stats-section h3 {
  margin-bottom: 20px;
  color: #303133;
}

.features {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-top: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 6px;
  color: #606266;
}

.stats-section .el-row {
  gap: 20px;
}

.action-section {
  margin-top: 40px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.action-section .el-button {
  margin: 0 10px;
}
</style>