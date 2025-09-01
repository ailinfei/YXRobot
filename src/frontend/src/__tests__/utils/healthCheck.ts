/**
 * API健康检查工具
 * 用于验证后端服务是否可用
 */

import axios from 'axios'

const API_BASE_URL = 'http://localhost:8081'

export interface HealthCheckResult {
  isHealthy: boolean
  message: string
  responseTime?: number
  error?: string
}

/**
 * 检查后端服务健康状态
 */
export async function checkBackendHealth(): Promise<HealthCheckResult> {
  const startTime = Date.now()
  
  try {
    const response = await axios.get(`${API_BASE_URL}/api/admin/customers/stats`, {
      timeout: 5000
    })
    
    const responseTime = Date.now() - startTime
    
    if (response.status === 200) {
      return {
        isHealthy: true,
        message: '后端服务正常运行',
        responseTime
      }
    } else {
      return {
        isHealthy: false,
        message: `后端服务响应异常: HTTP ${response.status}`,
        responseTime
      }
    }
  } catch (error: any) {
    const responseTime = Date.now() - startTime
    
    if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
      return {
        isHealthy: false,
        message: '无法连接到后端服务，请确保服务已启动',
        responseTime,
        error: '连接被拒绝'
      }
    } else if (error.code === 'ECONNRESET') {
      return {
        isHealthy: false,
        message: '连接被重置，后端服务可能正在重启',
        responseTime,
        error: '连接重置'
      }
    } else if (error.code === 'ETIMEDOUT') {
      return {
        isHealthy: false,
        message: '连接超时，后端服务响应缓慢',
        responseTime,
        error: '连接超时'
      }
    } else {
      return {
        isHealthy: false,
        message: `后端服务检查失败: ${error.message}`,
        responseTime,
        error: error.code || 'UNKNOWN_ERROR'
      }
    }
  }
}

/**
 * 等待后端服务启动
 */
export async function waitForBackendReady(
  maxWaitTime: number = 30000,
  checkInterval: number = 2000
): Promise<boolean> {
  const startTime = Date.now()
  
  console.log('🔍 等待后端服务启动...')
  
  while (Date.now() - startTime < maxWaitTime) {
    const healthResult = await checkBackendHealth()
    
    if (healthResult.isHealthy) {
      console.log(`✅ ${healthResult.message} (响应时间: ${healthResult.responseTime}ms)`)
      return true
    }
    
    console.log(`⏳ ${healthResult.message}`)
    await new Promise(resolve => setTimeout(resolve, checkInterval))
  }
  
  console.log('❌ 等待后端服务启动超时')
  return false
}

/**
 * 显示后端服务状态
 */
export async function showBackendStatus(): Promise<void> {
  console.log('🔍 检查后端服务状态...')
  console.log(`📍 API地址: ${API_BASE_URL}`)
  
  const healthResult = await checkBackendHealth()
  
  if (healthResult.isHealthy) {
    console.log(`✅ ${healthResult.message}`)
    console.log(`⚡ 响应时间: ${healthResult.responseTime}ms`)
  } else {
    console.log(`❌ ${healthResult.message}`)
    if (healthResult.error) {
      console.log(`🔧 错误类型: ${healthResult.error}`)
    }
    
    console.log('')
    console.log('🔧 解决方案:')
    console.log('1. 确保后端服务已启动: mvn spring-boot:run')
    console.log('2. 检查端口8081是否被占用')
    console.log('3. 验证数据库连接是否正常')
    console.log(`4. 手动访问: ${API_BASE_URL}/api/admin/customers/stats`)
  }
}