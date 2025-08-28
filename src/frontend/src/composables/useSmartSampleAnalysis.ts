/**
 * 智能样本分析组合式函数
 * 提供响应式的样本分析功能
 */

import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  smartSampleAnalysisService, 
  type UploadedFile, 
  type SampleAnalysisResult,
  type DuplicateInfo,
  type QualityIssue
} from '@/services/smartSampleAnalysis'

export interface AnalysisState {
  isAnalyzing: boolean
  progress: number
  currentStep: string
  error: string | null
}

export function useSmartSampleAnalysis() {
  // 响应式状态
  const analysisState = ref<AnalysisState>({
    isAnalyzing: false,
    progress: 0,
    currentStep: '',
    error: null
  })

  const analysisResult = ref<SampleAnalysisResult | null>(null)
  const lastAnalysisTime = ref<Date | null>(null)

  // 计算属性
  const hasAnalysisResult = computed(() => analysisResult.value !== null)
  
  const analysisStats = computed(() => {
    if (!analysisResult.value) return null
    
    return {
      totalFiles: analysisResult.value.totalFiles,
      recognizedCharacters: analysisResult.value.recognizedCharacters,
      duplicateCount: analysisResult.value.duplicates.length,
      qualityScore: analysisResult.value.basicQualityCheck.averageQuality,
      validFiles: analysisResult.value.basicQualityCheck.validFiles,
      invalidFiles: analysisResult.value.basicQualityCheck.invalidFiles,
      issueCount: analysisResult.value.basicQualityCheck.issues.length
    }
  })

  const duplicatesByType = computed(() => {
    if (!analysisResult.value) return { exact: [], similar: [], character: [] }
    
    return analysisResult.value.duplicates.reduce((acc, dup) => {
      acc[dup.type].push(dup)
      return acc
    }, { exact: [] as DuplicateInfo[], similar: [] as DuplicateInfo[], character: [] as DuplicateInfo[] })
  })

  const issuesBySeverity = computed(() => {
    if (!analysisResult.value) return { high: [], medium: [], low: [] }
    
    return analysisResult.value.basicQualityCheck.issues.reduce((acc, issue) => {
      acc[issue.severity].push(issue)
      return acc
    }, { high: [] as QualityIssue[], medium: [] as QualityIssue[], low: [] as QualityIssue[] })
  })

  const characterCoverage = computed(() => {
    if (!analysisResult.value) return { covered: 0, total: 0, percentage: 0 }
    
    const characterMap = analysisResult.value.characterMap
    const coveredChars = Object.values(characterMap).filter(info => info.files.length > 0).length
    const totalChars = Object.keys(characterMap).length
    
    return {
      covered: coveredChars,
      total: totalChars,
      percentage: totalChars > 0 ? Math.round((coveredChars / totalChars) * 100) : 0
    }
  })

  const qualityDistribution = computed(() => {
    if (!analysisResult.value) return { excellent: 0, good: 0, fair: 0, poor: 0 }
    
    const characterMap = analysisResult.value.characterMap
    const qualities = Object.values(characterMap).map(info => info.quality)
    
    return qualities.reduce((acc, quality) => {
      if (quality >= 90) acc.excellent++
      else if (quality >= 70) acc.good++
      else if (quality >= 50) acc.fair++
      else acc.poor++
      return acc
    }, { excellent: 0, good: 0, fair: 0, poor: 0 })
  })

  // 方法
  const analyzeFiles = async (files: UploadedFile[], targetCharacters: string[]) => {
    if (files.length === 0) {
      ElMessage.warning('没有文件需要分析')
      return
    }

    analysisState.value = {
      isAnalyzing: true,
      progress: 0,
      currentStep: '准备分析...',
      error: null
    }

    try {
      // 模拟分析进度
      const progressSteps = [
        { step: '字符识别中...', progress: 25 },
        { step: '重复检测中...', progress: 50 },
        { step: '质量检查中...', progress: 75 },
        { step: '生成报告中...', progress: 90 }
      ]

      // 逐步更新进度
      for (const { step, progress } of progressSteps) {
        analysisState.value.currentStep = step
        analysisState.value.progress = progress
        await new Promise(resolve => setTimeout(resolve, 300))
      }

      // 执行实际分析
      const result = await smartSampleAnalysisService.analyzeFiles(files, targetCharacters)
      
      analysisResult.value = result
      lastAnalysisTime.value = new Date()
      
      analysisState.value.progress = 100
      analysisState.value.currentStep = '分析完成'
      
      // 显示分析结果摘要
      showAnalysisSummary(result)
      
      // 延迟重置状态
      setTimeout(() => {
        analysisState.value.isAnalyzing = false
      }, 1000)

    } catch (error) {
      console.error('智能分析失败:', error)
      analysisState.value.error = error instanceof Error ? error.message : '分析失败'
      ElMessage.error('智能分析失败，请重试')
      
      setTimeout(() => {
        analysisState.value.isAnalyzing = false
      }, 1000)
    }
  }

  const showAnalysisSummary = (result: SampleAnalysisResult) => {
    const stats = {
      totalFiles: result.totalFiles,
      recognizedCharacters: result.recognizedCharacters,
      duplicates: result.duplicates.length,
      qualityScore: result.basicQualityCheck.averageQuality
    }

    let messageType: 'success' | 'warning' | 'info' = 'success'
    let message = `分析完成！识别 ${stats.recognizedCharacters} 个字符，平均质量 ${stats.qualityScore}%`

    if (stats.duplicates > 0) {
      message += `，发现 ${stats.duplicates} 个重复文件`
      messageType = 'warning'
    }

    if (stats.qualityScore < 60) {
      messageType = 'warning'
    }

    ElMessage({
      type: messageType,
      message,
      duration: 5000
    })
  }

  const clearAnalysisResult = () => {
    analysisResult.value = null
    lastAnalysisTime.value = null
    analysisState.value = {
      isAnalyzing: false,
      progress: 0,
      currentStep: '',
      error: null
    }
  }

  const retryAnalysis = async (files: UploadedFile[], targetCharacters: string[]) => {
    clearAnalysisResult()
    await analyzeFiles(files, targetCharacters)
  }

  // 获取字符建议
  const getCharacterRecommendations = (character: string) => {
    if (!analysisResult.value) return []
    
    const characterInfo = analysisResult.value.characterMap[character]
    return characterInfo?.recommendations || []
  }

  // 获取缺失字符
  const getMissingCharacters = (targetCharacters: string[]) => {
    if (!analysisResult.value) return targetCharacters
    
    return targetCharacters.filter(char => {
      const info = analysisResult.value!.characterMap[char]
      return !info || info.files.length === 0
    })
  }

  // 获取质量较低的字符
  const getLowQualityCharacters = (threshold = 50) => {
    if (!analysisResult.value) return []
    
    return Object.entries(analysisResult.value.characterMap)
      .filter(([_, info]) => info.quality < threshold && info.files.length > 0)
      .map(([char, info]) => ({ character: char, quality: info.quality, files: info.files }))
  }

  // 获取重复文件建议
  const getDuplicateRemovalSuggestions = () => {
    if (!analysisResult.value) return []
    
    return analysisResult.value.duplicates.map(dup => ({
      action: 'remove',
      file: dup.file,
      reason: `与 ${dup.original} 重复 (${dup.type}, 相似度: ${Math.round(dup.similarity * 100)}%)`,
      priority: dup.type === 'exact' ? 'high' : 'medium'
    }))
  }

  // 获取质量改进建议
  const getQualityImprovementSuggestions = () => {
    if (!analysisResult.value) return []
    
    const suggestions: Array<{
      type: string
      message: string
      files: string[]
      priority: 'high' | 'medium' | 'low'
      action?: string
    }> = []

    // 基于质量问题生成建议
    analysisResult.value.basicQualityCheck.issues.forEach(issue => {
      suggestions.push({
        type: issue.type,
        message: issue.message,
        files: issue.affectedFiles,
        priority: issue.severity,
        action: issue.suggestion
      })
    })

    // 基于字符覆盖率生成建议
    const missing = getMissingCharacters(Object.keys(analysisResult.value.characterMap))
    if (missing.length > 0) {
      suggestions.push({
        type: 'coverage',
        message: `缺少 ${missing.length} 个字符的样本`,
        files: [],
        priority: 'high',
        action: `请为以下字符添加样本: ${missing.slice(0, 5).join(', ')}${missing.length > 5 ? '...' : ''}`
      })
    }

    return suggestions.sort((a, b) => {
      const priorityOrder = { high: 3, medium: 2, low: 1 }
      return priorityOrder[b.priority] - priorityOrder[a.priority]
    })
  }

  // 导出分析报告
  const exportAnalysisReport = () => {
    if (!analysisResult.value) {
      ElMessage.warning('没有分析结果可导出')
      return
    }

    const report = {
      timestamp: analysisResult.value.analysisTimestamp,
      summary: analysisStats.value,
      characterMap: analysisResult.value.characterMap,
      duplicates: analysisResult.value.duplicates,
      qualityIssues: analysisResult.value.basicQualityCheck.issues,
      recommendations: {
        duplicateRemoval: getDuplicateRemovalSuggestions(),
        qualityImprovement: getQualityImprovementSuggestions(),
        missingCharacters: getMissingCharacters(Object.keys(analysisResult.value.characterMap))
      }
    }

    const blob = new Blob([JSON.stringify(report, null, 2)], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `sample-analysis-report-${Date.now()}.json`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)

    ElMessage.success('分析报告已导出')
  }

  // 自动分析监听器
  const enableAutoAnalysis = ref(false)
  
  watch([enableAutoAnalysis], ([enabled]) => {
    if (enabled) {
      console.log('自动分析已启用')
    }
  })

  return {
    // 状态
    analysisState,
    analysisResult,
    lastAnalysisTime,
    
    // 计算属性
    hasAnalysisResult,
    analysisStats,
    duplicatesByType,
    issuesBySeverity,
    characterCoverage,
    qualityDistribution,
    
    // 方法
    analyzeFiles,
    clearAnalysisResult,
    retryAnalysis,
    getCharacterRecommendations,
    getMissingCharacters,
    getLowQualityCharacters,
    getDuplicateRemovalSuggestions,
    getQualityImprovementSuggestions,
    exportAnalysisReport,
    
    // 配置
    enableAutoAnalysis
  }
}