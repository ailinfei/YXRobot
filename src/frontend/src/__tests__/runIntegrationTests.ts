#!/usr/bin/env node

/**
 * 客户API集成测试运行脚本
 * 用于验证前后端API对接的完整性
 */

import { execSync } from 'child_process'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8081'
const HEALTH_CHECK_TIMEOUT = 30000
const HEALTH_CHECK_INTERVAL = 2000

// 颜色输出
const colors = {
  reset: '\x1b[0m',
  red: '\x1b[31m',
  green: '\x1b[32m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  magenta: '\x1b[35m',
  cyan: '\x1b[36m'
}

function log(message: string, color: keyof typeof colors = 'reset') {
  console.log(`${colors[color]}${message}${colors.reset}`)
}

// 检查后端服务是否可用
async function checkBackendHealth(): Promise<boolean> {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/admin/customers/stats`, {
      timeout: 5000
    })
    return response.status === 200
  } catch (error) {
    return false
  }
}

// 等待后端服务启动
async function waitForBackend(): Promise<void> {
  log('🔍 检查后端服务状态...', 'blue')
  
  const startTime = Date.now()
  
  while (Date.now() - startTime < HEALTH_CHECK_TIMEOUT) {
    if (await checkBackendHealth()) {
      log('✅ 后端服务已就绪', 'green')
      return
    }
    
    log('⏳ 等待后端服务启动...', 'yellow')
    await new Promise(resolve => setTimeout(resolve, HEALTH_CHECK_INTERVAL))
  }
  
  throw new Error('后端服务启动超时')
}

// 运行集成测试
async function runIntegrationTests(): Promise<void> {
  try {
    log('🚀 开始客户API集成测试', 'cyan')
    log('=' .repeat(50), 'cyan')
    
    // 检查后端服务
    await waitForBackend()
    
    // 运行测试
    log('🧪 执行集成测试...', 'blue')
    
    const testFiles = [
      'src/__tests__/integration/customerApiIntegration.test.ts',
      'src/__tests__/validation/fieldMappingValidation.test.ts',
      'src/__tests__/performance/apiPerformanceValidation.test.ts'
    ]
    
    let allTestsPassed = true
    
    for (const testFile of testFiles) {
      try {
        log(`\n📋 运行测试: ${testFile}`, 'cyan')
        const testCommand = `npx vitest run ${testFile} --reporter=verbose`
        
        execSync(testCommand, {
          stdio: 'inherit',
          cwd: process.cwd()
        })
        
        log(`✅ ${testFile} 测试通过`, 'green')
        
      } catch (testError) {
        log(`❌ ${testFile} 测试失败`, 'red')
        allTestsPassed = false
        // 继续运行其他测试，不立即抛出异常
      }
    }
    
    log('=' .repeat(50), allTestsPassed ? 'green' : 'red')
    
    if (allTestsPassed) {
      log('✅ 所有集成测试通过！', 'green')
      log('🎉 前后端API对接验证成功', 'green')
    } else {
      log('❌ 部分集成测试失败', 'red')
      log('请检查测试输出中的错误信息', 'yellow')
      throw new Error('集成测试失败')
    }
    
  } catch (error) {
    log('💥 集成测试执行失败:', 'red')
    log(error.message, 'red')
    
    if (error.message.includes('后端服务启动超时')) {
      log('', 'reset')
      log('🔧 解决方案:', 'yellow')
      log('1. 确保后端服务已启动: mvn spring-boot:run', 'yellow')
      log('2. 检查服务端口是否为8081', 'yellow')
      log('3. 验证数据库连接是否正常', 'yellow')
      log(`4. 手动访问: ${API_BASE_URL}/api/admin/customers/stats`, 'yellow')
    }
    
    process.exit(1)
  }
}

// 显示测试信息
function showTestInfo(): void {
  log('📋 客户API集成测试信息', 'magenta')
  log('-' .repeat(30), 'magenta')
  log(`🌐 API地址: ${API_BASE_URL}`, 'reset')
  log('🧪 测试范围:', 'reset')
  log('  • API接口完整性验证', 'reset')
  log('  • 数据格式匹配验证', 'reset')
  log('  • 字段映射正确性验证', 'reset')
  log('  • CRUD操作功能验证', 'reset')
  log('  • 搜索筛选功能验证', 'reset')
  log('  • 关联数据查询验证', 'reset')
  log('  • 错误处理完善性验证', 'reset')
  log('  • API性能要求验证', 'reset')
  log('')
}

// 主函数
async function main(): Promise<void> {
  showTestInfo()
  await runIntegrationTests()
}

// 处理未捕获的异常
process.on('unhandledRejection', (reason, promise) => {
  log('💥 未处理的Promise拒绝:', 'red')
  log(String(reason), 'red')
  process.exit(1)
})

process.on('uncaughtException', (error) => {
  log('💥 未捕获的异常:', 'red')
  log(error.message, 'red')
  process.exit(1)
})

// 运行主函数
if (require.main === module) {
  main()
}

export { runIntegrationTests, checkBackendHealth }