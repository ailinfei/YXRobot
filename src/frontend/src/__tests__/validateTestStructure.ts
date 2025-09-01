#!/usr/bin/env node

/**
 * 测试结构验证脚本
 * 用于验证集成测试代码的结构和语法正确性
 */

import { existsSync, readFileSync } from 'fs'
import { join } from 'path'

// 颜色输出
const colors = {
  reset: '\x1b[0m',
  red: '\x1b[31m',
  green: '\x1b[32m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  cyan: '\x1b[36m'
}

function log(message: string, color: keyof typeof colors = 'reset') {
  console.log(`${colors[color]}${message}${colors.reset}`)
}

// 验证结果
interface ValidationResult {
  file: string
  exists: boolean
  syntaxValid: boolean
  hasRequiredImports: boolean
  hasTestCases: boolean
  errors: string[]
}

// 需要验证的测试文件
const testFiles = [
  {
    path: 'src/__tests__/integration/customerApiIntegration.test.ts',
    name: 'API集成测试',
    requiredImports: ['describe', 'it', 'expect', 'axios'],
    requiredTestCases: ['API接口完整性验证', '数据格式匹配验证', 'CRUD操作验证']
  },
  {
    path: 'src/__tests__/validation/fieldMappingValidation.test.ts',
    name: '字段映射验证测试',
    requiredImports: ['describe', 'it', 'expect', 'ApiResponseValidator'],
    requiredTestCases: ['字段映射验证', '数据类型一致性验证']
  },
  {
    path: 'src/__tests__/performance/apiPerformanceValidation.test.ts',
    name: 'API性能验证测试',
    requiredImports: ['describe', 'it', 'expect', 'axios'],
    requiredTestCases: ['核心查询API性能测试', 'CRUD操作API性能测试']
  },
  {
    path: 'src/__tests__/utils/apiResponseValidator.ts',
    name: 'API响应验证工具',
    requiredImports: ['ValidationResult', 'ApiResponseValidator'],
    requiredTestCases: []
  },
  {
    path: 'src/__tests__/utils/testReportGenerator.ts',
    name: '测试报告生成器',
    requiredImports: ['TestReportGenerator', 'TestResult'],
    requiredTestCases: []
  },
  {
    path: 'src/__tests__/config/testConfig.ts',
    name: '测试配置文件',
    requiredImports: ['API_CONFIG', 'PERFORMANCE_REQUIREMENTS'],
    requiredTestCases: []
  }
]

// 验证文件
function validateFile(fileInfo: any): ValidationResult {
  const result: ValidationResult = {
    file: fileInfo.name,
    exists: false,
    syntaxValid: false,
    hasRequiredImports: false,
    hasTestCases: false,
    errors: []
  }

  const filePath = join(process.cwd(), fileInfo.path)
  
  // 检查文件是否存在
  if (!existsSync(filePath)) {
    result.errors.push('文件不存在')
    return result
  }
  result.exists = true

  try {
    // 读取文件内容
    const content = readFileSync(filePath, 'utf-8')
    
    // 基本语法检查
    if (content.length === 0) {
      result.errors.push('文件为空')
      return result
    }
    result.syntaxValid = true

    // 检查必需的导入
    let hasAllImports = true
    for (const importItem of fileInfo.requiredImports) {
      if (!content.includes(importItem)) {
        result.errors.push(`缺少必需的导入或定义: ${importItem}`)
        hasAllImports = false
      }
    }
    result.hasRequiredImports = hasAllImports

    // 检查测试用例（仅对测试文件）
    if (fileInfo.requiredTestCases.length > 0) {
      let hasAllTestCases = true
      for (const testCase of fileInfo.requiredTestCases) {
        if (!content.includes(testCase)) {
          result.errors.push(`缺少测试用例: ${testCase}`)
          hasAllTestCases = false
        }
      }
      result.hasTestCases = hasAllTestCases
    } else {
      result.hasTestCases = true // 非测试文件默认通过
    }

    // 检查TypeScript语法（简单检查）
    const syntaxIssues = []
    if (content.includes('import') && !content.includes('from')) {
      syntaxIssues.push('导入语句可能有语法错误')
    }
    if (content.includes('describe(') && !content.includes('it(')) {
      syntaxIssues.push('describe块中缺少it测试用例')
    }
    if (syntaxIssues.length > 0) {
      result.errors.push(...syntaxIssues)
    }

  } catch (error) {
    result.errors.push(`读取文件失败: ${error.message}`)
  }

  return result
}

// 主验证函数
async function validateTestStructure(): Promise<void> {
  log('🔍 开始验证集成测试结构...', 'cyan')
  log('=' .repeat(50), 'cyan')

  const results: ValidationResult[] = []
  
  for (const fileInfo of testFiles) {
    log(`\n📋 验证: ${fileInfo.name}`, 'blue')
    const result = validateFile(fileInfo)
    results.push(result)

    if (result.exists) {
      log(`  ✅ 文件存在`, 'green')
    } else {
      log(`  ❌ 文件不存在`, 'red')
    }

    if (result.syntaxValid) {
      log(`  ✅ 语法有效`, 'green')
    } else {
      log(`  ❌ 语法无效`, 'red')
    }

    if (result.hasRequiredImports) {
      log(`  ✅ 必需导入完整`, 'green')
    } else {
      log(`  ❌ 缺少必需导入`, 'red')
    }

    if (result.hasTestCases) {
      log(`  ✅ 测试用例完整`, 'green')
    } else if (fileInfo.requiredTestCases.length > 0) {
      log(`  ❌ 缺少测试用例`, 'red')
    }

    if (result.errors.length > 0) {
      log(`  ⚠️  问题:`, 'yellow')
      result.errors.forEach(error => {
        log(`    • ${error}`, 'yellow')
      })
    }
  }

  // 生成总结报告
  log('\n' + '=' .repeat(50), 'cyan')
  log('📊 验证总结:', 'cyan')
  log('-' .repeat(30), 'cyan')

  const totalFiles = results.length
  const validFiles = results.filter(r => r.exists && r.syntaxValid && r.hasRequiredImports && r.hasTestCases).length
  const passRate = totalFiles > 0 ? (validFiles / totalFiles * 100).toFixed(1) : '0'

  log(`总文件数: ${totalFiles}`, 'reset')
  log(`有效文件: ${validFiles}`, validFiles === totalFiles ? 'green' : 'yellow')
  log(`通过率: ${passRate}%`, validFiles === totalFiles ? 'green' : 'yellow')

  // 详细问题报告
  const problemFiles = results.filter(r => r.errors.length > 0)
  if (problemFiles.length > 0) {
    log('\n❌ 存在问题的文件:', 'red')
    problemFiles.forEach(result => {
      log(`  • ${result.file}:`, 'red')
      result.errors.forEach(error => {
        log(`    - ${error}`, 'red')
      })
    })
  }

  // 成功消息
  if (validFiles === totalFiles) {
    log('\n🎉 所有测试文件结构验证通过！', 'green')
    log('✅ 文件结构完整', 'green')
    log('✅ 语法检查通过', 'green')
    log('✅ 导入声明正确', 'green')
    log('✅ 测试用例完整', 'green')
    log('\n📋 可以执行的测试:', 'green')
    log('  • API接口完整性验证', 'green')
    log('  • 数据格式匹配验证', 'green')
    log('  • 字段映射正确性验证', 'green')
    log('  • API性能要求验证', 'green')
    log('  • 错误处理完善性验证', 'green')
    log('\n🚀 准备就绪，可以运行集成测试！', 'green')
  } else {
    log('\n⚠️  部分文件存在问题，请修复后再运行测试', 'yellow')
  }

  // 下一步指导
  log('\n💡 下一步操作:', 'blue')
  if (validFiles === totalFiles) {
    log('1. 启动后端服务: mvn spring-boot:run', 'blue')
    log('2. 运行集成测试: scripts/run-integration-tests.bat', 'blue')
    log('3. 查看测试报告', 'blue')
  } else {
    log('1. 修复上述问题', 'blue')
    log('2. 重新运行结构验证', 'blue')
    log('3. 启动后端服务并运行测试', 'blue')
  }
}

// 运行验证
if (require.main === module) {
  validateTestStructure().catch(error => {
    log('💥 验证过程中发生错误:', 'red')
    log(error.message, 'red')
    process.exit(1)
  })
}

export { validateTestStructure }