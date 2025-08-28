import { ElMessage, ElNotification, ElMessageBox } from 'element-plus'

export interface ErrorContext {
  component?: string
  action?: string
  userId?: string
  timestamp?: string
  userAgent?: string
  url?: string
}

export interface ErrorDetails {
  code?: string
  message: string
  stack?: string
  context?: ErrorContext
  severity: 'low' | 'medium' | 'high' | 'critical'
  recoverable: boolean
  suggestions?: string[]
}

export class ErrorHandlingService {
  private errorLog: ErrorDetails[] = []
  private maxLogSize = 100

  /**
   * 处理错误并显示用户友好的消息
   */
  handleError(error: Error | string, context?: ErrorContext): void {
    const errorDetails = this.parseError(error, context)
    this.logError(errorDetails)
    this.displayError(errorDetails)
    
    // 自动恢复尝试
    if (errorDetails.recoverable) {
      this.attemptRecovery(errorDetails)
    }
  }

  /**
   * 解析错误信息
   */
  private parseError(error: Error | string, context?: ErrorContext): ErrorDetails {
    let message: string
    let stack: string | undefined
    let code: string | undefined

    if (typeof error === 'string') {
      message = error
    } else {
      message = error.message
      stack = error.stack
      code = (error as any).code
    }

    // 根据错误类型确定严重程度
    const severity = this.determineSeverity(message, code)
    const recoverable = this.isRecoverable(message, code)
    const suggestions = this.generateSuggestions(message, code)

    return {
      code,
      message: this.translateErrorMessage(message),
      stack,
      context: {
        ...context,
        timestamp: new Date().toISOString(),
        userAgent: navigator.userAgent,
        url: window.location.href
      },
      severity,
      recoverable,
      suggestions
    }
  }

  /**
   * 翻译错误消息为用户友好的中文
   */
  private translateErrorMessage(message: string): string {
    const translations: Record<string, string> = {
      'Network Error': '网络连接失败，请检查网络设置',
      'Timeout': '操作超时，请稍后重试',
      'Unauthorized': '登录已过期，请重新登录',
      'Forbidden': '权限不足，无法执行此操作',
      'Not Found': '请求的资源不存在',
      'Internal Server Error': '服务器内部错误，请稍后重试',
      'Bad Request': '请求参数错误，请检查输入信息',
      'File too large': '文件过大，请选择较小的文件',
      'Invalid file type': '文件类型不支持，请选择正确的文件格式',
      'Upload failed': '文件上传失败，请重试',
      'Connection refused': '无法连接到服务器，请检查网络',
      'Parse error': '数据解析失败，请刷新页面重试'
    }

    // 尝试匹配已知错误
    for (const [key, value] of Object.entries(translations)) {
      if (message.toLowerCase().includes(key.toLowerCase())) {
        return value
      }
    }

    // 如果没有匹配的翻译，返回原始消息
    return message || '发生未知错误'
  }

  /**
   * 确定错误严重程度
   */
  private determineSeverity(message: string, code?: string): ErrorDetails['severity'] {
    const criticalPatterns = ['crash', 'fatal', 'critical', 'system']
    const highPatterns = ['error', 'failed', 'exception', '500', '503']
    const mediumPatterns = ['warning', 'timeout', '404', '403', '401']

    const lowerMessage = message.toLowerCase()

    if (criticalPatterns.some(pattern => lowerMessage.includes(pattern))) {
      return 'critical'
    }
    if (highPatterns.some(pattern => lowerMessage.includes(pattern))) {
      return 'high'
    }
    if (mediumPatterns.some(pattern => lowerMessage.includes(pattern))) {
      return 'medium'
    }

    return 'low'
  }

  /**
   * 判断错误是否可恢复
   */
  private isRecoverable(message: string, code?: string): boolean {
    const nonRecoverablePatterns = ['fatal', 'critical', 'system', 'crash']
    const recoverablePatterns = ['network', 'timeout', 'connection', 'upload']

    const lowerMessage = message.toLowerCase()

    if (nonRecoverablePatterns.some(pattern => lowerMessage.includes(pattern))) {
      return false
    }
    if (recoverablePatterns.some(pattern => lowerMessage.includes(pattern))) {
      return true
    }

    // 根据HTTP状态码判断
    if (code) {
      const statusCode = parseInt(code)
      if (statusCode >= 500) return true // 服务器错误通常可重试
      if (statusCode === 401 || statusCode === 403) return true // 认证错误可重新登录
      if (statusCode === 404) return false // 资源不存在通常不可恢复
    }

    return true // 默认认为可恢复
  }

  /**
   * 生成解决建议
   */
  private generateSuggestions(message: string, code?: string): string[] {
    const suggestions: string[] = []
    const lowerMessage = message.toLowerCase()

    if (lowerMessage.includes('network') || lowerMessage.includes('connection')) {
      suggestions.push('检查网络连接是否正常')
      suggestions.push('尝试刷新页面')
      suggestions.push('稍后重试')
    }

    if (lowerMessage.includes('timeout')) {
      suggestions.push('网络较慢，请稍后重试')
      suggestions.push('检查网络连接')
    }

    if (lowerMessage.includes('unauthorized') || code === '401') {
      suggestions.push('请重新登录')
      suggestions.push('检查登录状态')
    }

    if (lowerMessage.includes('forbidden') || code === '403') {
      suggestions.push('联系管理员获取权限')
      suggestions.push('检查账户权限')
    }

    if (lowerMessage.includes('file') || lowerMessage.includes('upload')) {
      suggestions.push('检查文件格式是否正确')
      suggestions.push('确认文件大小不超过限制')
      suggestions.push('重新选择文件上传')
    }

    if (lowerMessage.includes('server') || code?.startsWith('5')) {
      suggestions.push('服务器暂时不可用，请稍后重试')
      suggestions.push('联系技术支持')
    }

    if (suggestions.length === 0) {
      suggestions.push('刷新页面重试')
      suggestions.push('联系技术支持')
    }

    return suggestions
  }

  /**
   * 记录错误
   */
  private logError(errorDetails: ErrorDetails): void {
    this.errorLog.unshift(errorDetails)
    
    // 限制日志大小
    if (this.errorLog.length > this.maxLogSize) {
      this.errorLog = this.errorLog.slice(0, this.maxLogSize)
    }

    // 发送到服务器（可选）
    this.sendErrorToServer(errorDetails)
  }

  /**
   * 显示错误信息
   */
  private displayError(errorDetails: ErrorDetails): void {
    const { message, severity, suggestions } = errorDetails

    switch (severity) {
      case 'critical':
        ElMessageBox.alert(
          `${message}\n\n建议操作：\n${suggestions?.join('\n') || '联系技术支持'}`,
          '严重错误',
          {
            type: 'error',
            showClose: false
          }
        )
        break

      case 'high':
        ElNotification({
          title: '操作失败',
          message: message,
          type: 'error',
          duration: 8000,
          showClose: true,
          onClick: () => {
            if (suggestions && suggestions.length > 0) {
              ElMessage.info(`建议：${suggestions[0]}`)
            }
          }
        })
        break

      case 'medium':
        ElMessage({
          message: message,
          type: 'warning',
          duration: 5000,
          showClose: true
        })
        break

      case 'low':
        ElMessage({
          message: message,
          type: 'info',
          duration: 3000
        })
        break
    }
  }

  /**
   * 尝试自动恢复
   */
  private async attemptRecovery(errorDetails: ErrorDetails): Promise<void> {
    const { message, context } = errorDetails

    // 网络错误恢复
    if (message.includes('网络') || message.includes('连接')) {
      setTimeout(() => {
        ElMessage.info('正在尝试重新连接...')
        // 这里可以添加重连逻辑
      }, 2000)
    }

    // 认证错误恢复
    if (message.includes('登录') || message.includes('权限')) {
      const shouldRelogin = await ElMessageBox.confirm(
        '登录状态已过期，是否重新登录？',
        '登录过期',
        {
          confirmButtonText: '重新登录',
          cancelButtonText: '稍后处理',
          type: 'warning'
        }
      ).catch(() => false)

      if (shouldRelogin) {
        // 跳转到登录页面
        window.location.href = '/login'
      }
    }
  }

  /**
   * 发送错误到服务器
   */
  private async sendErrorToServer(errorDetails: ErrorDetails): Promise<void> {
    try {
      // 只发送严重错误到服务器
      if (errorDetails.severity === 'high' || errorDetails.severity === 'critical') {
        // 这里可以调用API发送错误日志
        console.log('发送错误日志到服务器:', errorDetails)
      }
    } catch (error) {
      // 发送失败时不显示错误，避免无限循环
      console.warn('发送错误日志失败:', error)
    }
  }

  /**
   * 获取错误日志
   */
  getErrorLog(): ErrorDetails[] {
    return [...this.errorLog]
  }

  /**
   * 清除错误日志
   */
  clearErrorLog(): void {
    this.errorLog = []
  }

  /**
   * 获取错误统计
   */
  getErrorStats(): Record<string, number> {
    const stats: Record<string, number> = {
      total: this.errorLog.length,
      critical: 0,
      high: 0,
      medium: 0,
      low: 0
    }

    this.errorLog.forEach(error => {
      stats[error.severity]++
    })

    return stats
  }
}

// 创建全局实例
export const errorHandler = new ErrorHandlingService()

// 全局错误处理器
window.addEventListener('error', (event) => {
  errorHandler.handleError(event.error || event.message, {
    component: 'global',
    action: 'runtime_error'
  })
})

window.addEventListener('unhandledrejection', (event) => {
  errorHandler.handleError(event.reason, {
    component: 'global',
    action: 'unhandled_promise_rejection'
  })
})