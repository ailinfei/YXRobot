/**
 * 测试报告生成器
 * 用于生成详细的API集成测试报告
 */

import { writeFileSync } from 'fs'
import { join } from 'path'

// 测试结果接口
export interface TestResult {
  testName: string
  category: string
  status: 'passed' | 'failed' | 'skipped'
  duration: number
  error?: string
  details?: any
}

export interface PerformanceResult {
  apiName: string
  responseTime: number
  requirement: number
  passed: boolean
  requestSize?: number
  responseSize?: number
}

export interface ValidationResult {
  isValid: boolean
  errors: string[]
  warnings: string[]
  fieldMappings: Array<{
    field: string
    expected: string
    actual: string
    isMatch: boolean
  }>
}

export interface TestReport {
  summary: {
    totalTests: number
    passedTests: number
    failedTests: number
    skippedTests: number
    passRate: number
    totalDuration: number
    generatedAt: string
  }
  categories: {
    [category: string]: {
      tests: TestResult[]
      passRate: number
    }
  }
  performance: {
    results: PerformanceResult[]
    overallPassRate: number
    averageResponseTime: number
  }
  validation: {
    results: ValidationResult[]
    overallValid: boolean
    totalErrors: number
    totalWarnings: number
  }
  recommendations: string[]
}

export class TestReportGenerator {
  private testResults: TestResult[] = []
  private performanceResults: PerformanceResult[] = []
  private validationResults: ValidationResult[] = []
  private startTime: number = Date.now()

  /**
   * 添加测试结果
   */
  addTestResult(result: TestResult): void {
    this.testResults.push(result)
  }

  /**
   * 添加性能测试结果
   */
  addPerformanceResult(result: PerformanceResult): void {
    this.performanceResults.push(result)
  }

  /**
   * 添加验证结果
   */
  addValidationResult(result: ValidationResult): void {
    this.validationResults.push(result)
  }

  /**
   * 生成测试报告
   */
  generateReport(): TestReport {
    const totalDuration = Date.now() - this.startTime
    const totalTests = this.testResults.length
    const passedTests = this.testResults.filter(r => r.status === 'passed').length
    const failedTests = this.testResults.filter(r => r.status === 'failed').length
    const skippedTests = this.testResults.filter(r => r.status === 'skipped').length
    const passRate = totalTests > 0 ? (passedTests / totalTests) * 100 : 0

    // 按类别分组测试结果
    const categories: { [category: string]: { tests: TestResult[], passRate: number } } = {}
    
    this.testResults.forEach(result => {
      if (!categories[result.category]) {
        categories[result.category] = { tests: [], passRate: 0 }
      }
      categories[result.category].tests.push(result)
    })

    // 计算每个类别的通过率
    Object.keys(categories).forEach(category => {
      const categoryTests = categories[category].tests
      const categoryPassed = categoryTests.filter(r => r.status === 'passed').length
      categories[category].passRate = categoryTests.length > 0 ? (categoryPassed / categoryTests.length) * 100 : 0
    })

    // 性能测试统计
    const performancePassedCount = this.performanceResults.filter(r => r.passed).length
    const performancePassRate = this.performanceResults.length > 0 
      ? (performancePassedCount / this.performanceResults.length) * 100 
      : 0
    const averageResponseTime = this.performanceResults.length > 0
      ? this.performanceResults.reduce((sum, r) => sum + r.responseTime, 0) / this.performanceResults.length
      : 0

    // 验证结果统计
    const validResults = this.validationResults.filter(r => r.isValid).length
    const overallValid = this.validationResults.length > 0 && validResults === this.validationResults.length
    const totalErrors = this.validationResults.reduce((sum, r) => sum + r.errors.length, 0)
    const totalWarnings = this.validationResults.reduce((sum, r) => sum + r.warnings.length, 0)

    // 生成建议
    const recommendations = this.generateRecommendations(passRate, performancePassRate, totalErrors, totalWarnings)

    return {
      summary: {
        totalTests,
        passedTests,
        failedTests,
        skippedTests,
        passRate: Math.round(passRate * 100) / 100,
        totalDuration,
        generatedAt: new Date().toISOString()
      },
      categories,
      performance: {
        results: this.performanceResults,
        overallPassRate: Math.round(performancePassRate * 100) / 100,
        averageResponseTime: Math.round(averageResponseTime)
      },
      validation: {
        results: this.validationResults,
        overallValid,
        totalErrors,
        totalWarnings
      },
      recommendations
    }
  }

  /**
   * 生成建议
   */
  private generateRecommendations(passRate: number, performancePassRate: number, totalErrors: number, totalWarnings: number): string[] {
    const recommendations: string[] = []

    if (passRate < 100) {
      recommendations.push(`总体测试通过率为${passRate.toFixed(1)}%，建议检查失败的测试用例并修复相关问题`)
    }

    if (performancePassRate < 100) {
      recommendations.push(`性能测试通过率为${performancePassRate.toFixed(1)}%，建议优化响应时间较慢的API接口`)
    }

    if (totalErrors > 0) {
      recommendations.push(`发现${totalErrors}个数据验证错误，建议检查API响应格式和字段映射`)
    }

    if (totalWarnings > 5) {
      recommendations.push(`发现${totalWarnings}个警告，建议检查数据格式和业务逻辑的合理性`)
    }

    // 性能优化建议
    const slowApis = this.performanceResults.filter(r => !r.passed)
    if (slowApis.length > 0) {
      recommendations.push(`以下API响应时间超过要求，建议优化: ${slowApis.map(r => r.apiName).join(', ')}`)
    }

    // 数据库优化建议
    const dbRelatedSlowApis = slowApis.filter(r => 
      r.apiName.includes('列表') || r.apiName.includes('搜索') || r.apiName.includes('统计')
    )
    if (dbRelatedSlowApis.length > 0) {
      recommendations.push('建议检查数据库索引配置，优化查询性能')
    }

    // 缓存建议
    const statsApis = this.performanceResults.filter(r => r.apiName.includes('统计'))
    if (statsApis.some(r => r.responseTime > 500)) {
      recommendations.push('建议为统计类API添加缓存机制，提高响应速度')
    }

    if (recommendations.length === 0) {
      recommendations.push('所有测试都通过了！API接口运行良好，前后端对接成功')
    }

    return recommendations
  }

  /**
   * 生成控制台报告
   */
  generateConsoleReport(): string {
    const report = this.generateReport()
    let output = '\n'
    
    // 标题
    output += '🎯 API集成测试报告\n'
    output += '=' .repeat(60) + '\n'
    
    // 总结
    output += '\n📊 测试总结:\n'
    output += '-' .repeat(30) + '\n'
    output += `总测试数: ${report.summary.totalTests}\n`
    output += `通过测试: ${report.summary.passedTests} ✅\n`
    output += `失败测试: ${report.summary.failedTests} ❌\n`
    output += `跳过测试: ${report.summary.skippedTests} ⏭️\n`
    output += `通过率: ${report.summary.passRate}%\n`
    output += `总耗时: ${(report.summary.totalDuration / 1000).toFixed(2)}秒\n`
    output += `生成时间: ${new Date(report.summary.generatedAt).toLocaleString()}\n`

    // 分类测试结果
    output += '\n📋 分类测试结果:\n'
    output += '-' .repeat(30) + '\n'
    Object.entries(report.categories).forEach(([category, data]) => {
      const status = data.passRate === 100 ? '✅' : data.passRate > 0 ? '⚠️' : '❌'
      output += `${status} ${category}: ${data.tests.filter(t => t.status === 'passed').length}/${data.tests.length} (${data.passRate.toFixed(1)}%)\n`
      
      // 显示失败的测试
      const failedTests = data.tests.filter(t => t.status === 'failed')
      if (failedTests.length > 0) {
        failedTests.forEach(test => {
          output += `    ❌ ${test.testName}: ${test.error || '未知错误'}\n`
        })
      }
    })

    // 性能测试结果
    if (report.performance.results.length > 0) {
      output += '\n⚡ 性能测试结果:\n'
      output += '-' .repeat(30) + '\n'
      output += `总体通过率: ${report.performance.overallPassRate}%\n`
      output += `平均响应时间: ${report.performance.averageResponseTime}ms\n\n`
      
      report.performance.results.forEach(result => {
        const status = result.passed ? '✅' : '❌'
        const percentage = ((result.responseTime / result.requirement) * 100).toFixed(1)
        output += `${status} ${result.apiName}: ${result.responseTime}ms (${percentage}% of ${result.requirement}ms)\n`
      })
    }

    // 验证结果
    if (report.validation.results.length > 0) {
      output += '\n🔍 数据验证结果:\n'
      output += '-' .repeat(30) + '\n'
      output += `验证状态: ${report.validation.overallValid ? '✅ 通过' : '❌ 失败'}\n`
      output += `错误数量: ${report.validation.totalErrors}\n`
      output += `警告数量: ${report.validation.totalWarnings}\n`
      
      if (report.validation.totalErrors > 0) {
        output += '\n错误详情:\n'
        report.validation.results.forEach((result, index) => {
          if (result.errors.length > 0) {
            output += `  验证${index + 1}: ${result.errors.join(', ')}\n`
          }
        })
      }
    }

    // 建议
    if (report.recommendations.length > 0) {
      output += '\n💡 优化建议:\n'
      output += '-' .repeat(30) + '\n'
      report.recommendations.forEach((recommendation, index) => {
        output += `${index + 1}. ${recommendation}\n`
      })
    }

    output += '\n' + '=' .repeat(60) + '\n'
    
    return output
  }

  /**
   * 生成JSON报告
   */
  generateJsonReport(): string {
    const report = this.generateReport()
    return JSON.stringify(report, null, 2)
  }

  /**
   * 保存报告到文件
   */
  saveReport(format: 'console' | 'json' = 'console', filePath?: string): void {
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-')
    const defaultPath = join(process.cwd(), `api-integration-test-report-${timestamp}.${format === 'json' ? 'json' : 'txt'}`)
    const outputPath = filePath || defaultPath

    let content: string
    if (format === 'json') {
      content = this.generateJsonReport()
    } else {
      content = this.generateConsoleReport()
    }

    try {
      writeFileSync(outputPath, content, 'utf-8')
      console.log(`📄 测试报告已保存到: ${outputPath}`)
    } catch (error) {
      console.error('❌ 保存测试报告失败:', error)
    }
  }

  /**
   * 重置报告生成器
   */
  reset(): void {
    this.testResults = []
    this.performanceResults = []
    this.validationResults = []
    this.startTime = Date.now()
  }
}

export default TestReportGenerator