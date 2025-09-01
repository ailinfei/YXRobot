#!/usr/bin/env node

/**
 * 客户管理模块API集成测试运行脚本
 * 用于验证前后端对接的完整性
 */

import { execSync } from 'child_process'
import { existsSync } from 'fs'
import { resolve } from 'path'

const testCategories = {
  api: {
    name: 'API接口测试',
    pattern: 'src/__tests__/api/**/*.test.ts',
    description: '测试API接口的功能性和数据格式'
  },
  integration: {
    name: '集成测试',
    pattern: 'src/__tests__/integration/**/*.test.ts',
    description: '测试前端页面与API的集成'
  },
  validation: {
    name: '数据验证测试',
    pattern: 'src/__tests__/validation/**/*.test.ts',
    description: '验证API响应格式与前端接口的匹配性'
  },
  performance: {
    name: '性能测试',
    pattern: 'src/__tests__/performance/**/*.test.ts',
    description: '测试API响应时间和大数据量处理能力'
  }
}

interface TestResult {
  category: string
  passed: boolean
  duration: number
  details: string
}

class TestRunner {
  private results: TestResult[] = []

  async runAllTests(): Promise<void> {
    console.log('🚀 开始执行客户管理模块API集成测试...\n')

    for (const [key, config] of Object.entries(testCategories)) {
      await this.runTestCategory(key, config)
    }

    this.printSummary()
  }

  private async runTestCategory(
    category: string, 
    config: { name: string; pattern: string; description: string }
  ): Promise<void> {
    console.log(`📋 ${config.name}`)
    console.log(`   ${config.description}`)
    console.log(`   模式: ${config.pattern}`)

    const startTime = Date.now()
    let passed = false
    let details = ''

    try {
      // 检查测试文件是否存在
      const testFiles = this.findTestFiles(config.pattern)
      if (testFiles.length === 0) {
        details = '未找到测试文件'
        console.log(`   ⚠️  ${details}\n`)
      } else {
        // 运行测试
        const command = `npx vitest run ${config.pattern} --reporter=verbose`
        const output = execSync(command, { 
          encoding: 'utf8',
          cwd: resolve(__dirname, '../../../')
        })
        
        passed = !output.includes('FAILED') && !output.includes('failed')
        details = this.extractTestSummary(output)
        
        console.log(`   ✅ ${details}`)
        console.log(`   输出: ${output.split('\n').slice(-3, -1).join(' ')}\n`)
      }
    } catch (error: any) {
      details = `执行失败: ${error.message}`
      console.log(`   ❌ ${details}\n`)
    }

    const duration = Date.now() - startTime
    this.results.push({ category, passed, duration, details })
  }

  private findTestFiles(pattern: string): string[] {
    try {
      const glob = require('glob')
      return glob.sync(pattern, { cwd: resolve(__dirname, '../../../') })
    } catch (error) {
      return []
    }
  }

  private extractTestSummary(output: string): string {
    const lines = output.split('\n')
    const summaryLine = lines.find(line => 
      line.includes('Test Files') || 
      line.includes('Tests') || 
      line.includes('passed')
    )
    return summaryLine || '测试完成'
  }

  private printSummary(): void {
    console.log('📊 测试结果汇总')
    console.log('=' .repeat(60))

    const totalTests = this.results.length
    const passedTests = this.results.filter(r => r.passed).length
    const totalDuration = this.results.reduce((sum, r) => sum + r.duration, 0)

    console.log(`总测试类别: ${totalTests}`)
    console.log(`通过测试: ${passedTests}`)
    console.log(`失败测试: ${totalTests - passedTests}`)
    console.log(`总耗时: ${totalDuration}ms`)
    console.log('')

    // 详细结果
    this.results.forEach(result => {
      const status = result.passed ? '✅' : '❌'
      const category = testCategories[result.category as keyof typeof testCategories]?.name || result.category
      console.log(`${status} ${category}: ${result.details} (${result.duration}ms)`)
    })

    console.log('')

    // 验收检查点
    this.printAcceptanceCriteria()

    // 总结
    if (passedTests === totalTests) {
      console.log('🎉 所有测试通过！前后端API对接验证成功。')
    } else {
      console.log('⚠️  部分测试失败，请检查API接口实现。')
      process.exit(1)
    }
  }

  private printAcceptanceCriteria(): void {
    console.log('🎯 验收检查点')
    console.log('-'.repeat(40))

    const criteria = [
      {
        name: 'API接口完整性',
        check: () => this.results.find(r => r.category === 'api')?.passed || false,
        description: '所有前端页面需要的API接口都已实现'
      },
      {
        name: '数据格式匹配',
        check: () => this.results.find(r => r.category === 'validation')?.passed || false,
        description: 'API响应格式与前端TypeScript接口完全匹配'
      },
      {
        name: '功能完整支持',
        check: () => this.results.find(r => r.category === 'integration')?.passed || false,
        description: '前端页面的所有功能都能正常工作'
      },
      {
        name: '性能要求满足',
        check: () => this.results.find(r => r.category === 'performance')?.passed || false,
        description: 'API响应时间满足前端页面的性能要求'
      }
    ]

    criteria.forEach(criterion => {
      const status = criterion.check() ? '✅' : '❌'
      console.log(`${status} ${criterion.name}: ${criterion.description}`)
    })

    console.log('')
  }
}

// 运行测试
if (require.main === module) {
  const runner = new TestRunner()
  runner.runAllTests().catch(error => {
    console.error('测试运行失败:', error)
    process.exit(1)
  })
}

export { TestRunner }