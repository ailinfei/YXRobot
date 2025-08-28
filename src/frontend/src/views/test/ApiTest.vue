<template>
  <div class="api-test-page">
    <h1>API测试页面</h1>
    
    <div class="test-section">
      <h2>产品API测试</h2>
      <el-button @click="testProductsApi" type="primary">测试产品API</el-button>
      
      <div v-if="loading" class="loading">
        <el-loading-directive />
        加载中...
      </div>
      
      <div v-if="apiResponse" class="api-response">
        <h3>API响应:</h3>
        <pre>{{ JSON.stringify(apiResponse, null, 2) }}</pre>
      </div>
      
      <div v-if="error" class="error">
        <h3>错误信息:</h3>
        <pre>{{ error }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { websiteApi } from '@/api/website'

const loading = ref(false)
const apiResponse = ref(null)
const error = ref(null)

const testProductsApi = async () => {
  try {
    loading.value = true
    error.value = null
    apiResponse.value = null
    
    console.log('开始测试产品API...')
    
    const response = await websiteApi.getProducts({
      page: 1,
      size: 20
    })
    
    console.log('API响应:', response)
    apiResponse.value = response
    
  } catch (err) {
    console.error('API测试失败:', err)
    error.value = err.message || '未知错误'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.api-test-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.test-section {
  margin: 20px 0;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.api-response, .error {
  margin-top: 20px;
  padding: 15px;
  border-radius: 4px;
}

.api-response {
  background: #f0f9ff;
  border: 1px solid #0ea5e9;
}

.error {
  background: #fef2f2;
  border: 1px solid #ef4444;
}

pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Courier New', monospace;
  font-size: 12px;
}

.loading {
  margin: 20px 0;
  text-align: center;
}
</style>