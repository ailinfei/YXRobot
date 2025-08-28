/**
 * 向导步骤集成组合式函数
 * 管理步骤间的数据流转和状态同步
 */

import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'

export interface WizardStepData {
  basicInfo: {
    name: string
    description: string
    fontType: string
    difficulty: number
    version: string
    tags: string[]
  }
  sampleUpload: {
    targetCharacters: string[]
    uploadedFiles: any[]
    analysisResult: any
  }
  configuration: {
    trainingSettings: any
    qualitySettings: any
  }
  review: {
    confirmed: boolean
  }
}

export interface StepValidation {
  isValid: boolean
  errors: string[]
  warnings: string[]
}

export interface StepTransition {
  from: number
  to: number
  data: any
  timestamp: number
}

export function useWizardStepIntegration() {
  // 状态管理
  const currentStepData = ref<any>({})
  const stepValidations = ref<Record<number, StepValidation>>({})
  const stepTransitions = ref<StepTransition[]>([])
  const dataFlowLog = ref<Array<{ step: number, action: string, data: any, timestamp: number }>>([])

  // 计算属性
  const isCurrentStepValid = computed(() => {
    const currentStep = getCurrentStep()
    return stepValidations.value[currentStep]?.isValid || false
  })

  const hasValidationErrors = computed(() => {
    return Object.values(stepValidations.value).some(validation => 
      validation.errors.length > 0
    )
  })

  const hasValidationWarnings = computed(() => {
    return Object.values(stepValidations.value).some(validation => 
      validation.warnings.length > 0
    )
  })

  const stepCompletionRate = computed(() => {
    const totalSteps = Object.keys(stepValidations.value).length
    const validSteps = Object.values(stepValidations.value).filter(v => v.isValid).length
    return totalSteps > 0 ? Math.round((validSteps / totalSteps) * 100) : 0
  })

  // 方法
  const getCurrentStep = (): number => {
    // 这里应该从父组件或路由获取当前步骤
    // 暂时返回默认值
    return 2 // 假设当前在样本上传步骤
  }

  const updateStepData = (step: number, data: any) => {
    currentStepData.value[step] = { ...currentStepData.value[step], ...data }
    
    // 记录数据流转日志
    dataFlowLog.value.push({
      step,
      action: 'update',
      data: JSON.parse(JSON.stringify(data)),
      timestamp: Date.now()
    })

    console.log(`步骤 ${step} 数据已更新:`, data)
  }

  const updateStepValidation = (step: number, validation: StepValidation) => {
    stepValidations.value[step] = validation
    
    // 记录验证状态变化
    dataFlowLog.value.push({
      step,
      action: 'validate',
      data: validation,
      timestamp: Date.now()
    })

    console.log(`步骤 ${step} 验证状态:`, validation)
  }

  const prepareStepTransition = (fromStep: number, toStep: number): boolean => {
    const fromValidation = stepValidations.value[fromStep]
    
    if (!fromValidation?.isValid) {
      ElMessage.warning(`步骤 ${fromStep} 验证未通过，无法继续`)
      return false
    }

    // 执行步骤间数据传递
    const transitionData = performDataTransition(fromStep, toStep)
    
    // 记录步骤转换
    stepTransitions.value.push({
      from: fromStep,
      to: toStep,
      data: transitionData,
      timestamp: Date.now()
    })

    return true
  }

  const performDataTransition = (fromStep: number, toStep: number): any => {
    const fromData = currentStepData.value[fromStep]
    let transitionData = {}

    // 根据步骤执行特定的数据传递逻辑
    switch (`${fromStep}-${toStep}`) {
      case '1-2': // 基本信息 -> 样本上传
        transitionData = {
          fontType: fromData?.fontType,
          suggestedCharacters: generateSuggestedCharacters(fromData?.fontType),
          qualityRequirements: getQualityRequirements(fromData?.difficulty)
        }
        break
        
      case '2-3': // 样本上传 -> 参数配置
        transitionData = {
          characterCount: fromData?.targetCharacters?.length || 0,
          sampleQuality: fromData?.analysisResult?.qualityScore || 0,
          trainingComplexity: calculateTrainingComplexity(fromData),
          recommendedSettings: generateRecommendedSettings(fromData)
        }
        break
        
      case '3-4': // 参数配置 -> 确认创建
        transitionData = {
          estimatedTime: calculateEstimatedTime(fromData),
          resourceRequirements: calculateResourceRequirements(fromData),
          qualityPrediction: predictQuality(fromData)
        }
        break
    }

    // 更新目标步骤的数据
    updateStepData(toStep, transitionData)
    
    return transitionData
  }

  const generateSuggestedCharacters = (fontType: string): string[] => {
    // 根据字体类型生成建议字符
    const suggestions: Record<string, string[]> = {
      kaishu: ['一', '二', '三', '四', '五', '人', '大', '小', '中', '国'],
      xingshu: ['书', '法', '艺', '术', '美', '学', '文', '字', '体', '风'],
      caoshu: ['草', '书', '飞', '舞', '龙', '凤', '云', '水', '山', '川'],
      lishu: ['隶', '书', '古', '典', '雅', '致', '端', '庄', '正', '统']
    }
    
    return suggestions[fontType] || suggestions.kaishu
  }

  const getQualityRequirements = (difficulty: number): any => {
    // 根据难度等级返回质量要求
    const requirements = {
      1: { minQuality: 60, minSamples: 5 },
      2: { minQuality: 70, minSamples: 8 },
      3: { minQuality: 80, minSamples: 10 },
      4: { minQuality: 85, minSamples: 15 },
      5: { minQuality: 90, minSamples: 20 }
    }
    
    return requirements[difficulty as keyof typeof requirements] || requirements[3]
  }

  const calculateTrainingComplexity = (sampleData: any): 'low' | 'medium' | 'high' => {
    const characterCount = sampleData?.targetCharacters?.length || 0
    const sampleCount = sampleData?.uploadedFiles?.length || 0
    const qualityScore = sampleData?.analysisResult?.qualityScore || 0
    
    if (characterCount > 100 || sampleCount > 200 || qualityScore < 60) {
      return 'high'
    } else if (characterCount > 50 || sampleCount > 100 || qualityScore < 80) {
      return 'medium'
    } else {
      return 'low'
    }
  }

  const generateRecommendedSettings = (sampleData: any): any => {
    const complexity = calculateTrainingComplexity(sampleData)
    const qualityScore = sampleData?.analysisResult?.qualityScore || 0
    
    const settings = {
      low: {
        epochs: 50,
        batchSize: 32,
        learningRate: 0.001,
        augmentation: false
      },
      medium: {
        epochs: 100,
        batchSize: 16,
        learningRate: 0.0005,
        augmentation: true
      },
      high: {
        epochs: 200,
        batchSize: 8,
        learningRate: 0.0001,
        augmentation: true
      }
    }
    
    const baseSetting = settings[complexity]
    
    // 根据质量分数调整设置
    if (qualityScore < 70) {
      baseSetting.epochs = Math.round(baseSetting.epochs * 1.5)
      baseSetting.augmentation = true
    }
    
    return baseSetting
  }

  const calculateEstimatedTime = (configData: any): number => {
    // 根据配置估算训练时间（分钟）
    const baseTime = 30 // 基础时间30分钟
    const epochs = configData?.trainingSettings?.epochs || 100
    const characterCount = configData?.characterCount || 10
    
    return Math.round(baseTime * (epochs / 100) * Math.sqrt(characterCount / 10))
  }

  const calculateResourceRequirements = (configData: any): any => {
    const complexity = configData?.trainingComplexity || 'medium'
    
    const requirements = {
      low: { gpu: '2GB', memory: '4GB', storage: '1GB' },
      medium: { gpu: '4GB', memory: '8GB', storage: '2GB' },
      high: { gpu: '8GB', memory: '16GB', storage: '4GB' }
    }
    
    return requirements[complexity as keyof typeof requirements]
  }

  const predictQuality = (configData: any): number => {
    // 基于配置预测最终质量分数
    const sampleQuality = configData?.sampleQuality || 70
    const epochs = configData?.trainingSettings?.epochs || 100
    const augmentation = configData?.trainingSettings?.augmentation || false
    
    let predictedQuality = sampleQuality
    
    // 训练轮数影响
    if (epochs > 150) {
      predictedQuality += 5
    } else if (epochs < 50) {
      predictedQuality -= 5
    }
    
    // 数据增强影响
    if (augmentation) {
      predictedQuality += 3
    }
    
    return Math.min(95, Math.max(60, Math.round(predictedQuality)))
  }

  const getStepSummary = (step: number): any => {
    const data = currentStepData.value[step]
    const validation = stepValidations.value[step]
    
    return {
      step,
      data,
      validation,
      isComplete: validation?.isValid || false,
      lastUpdated: dataFlowLog.value
        .filter(log => log.step === step)
        .sort((a, b) => b.timestamp - a.timestamp)[0]?.timestamp
    }
  }

  const getAllStepsSummary = (): any[] => {
    const steps = Object.keys(stepValidations.value).map(Number)
    return steps.map(step => getStepSummary(step))
  }

  const exportWizardData = (): any => {
    return {
      stepData: currentStepData.value,
      validations: stepValidations.value,
      transitions: stepTransitions.value,
      dataFlowLog: dataFlowLog.value,
      summary: {
        completionRate: stepCompletionRate.value,
        hasErrors: hasValidationErrors.value,
        hasWarnings: hasValidationWarnings.value,
        exportTime: Date.now()
      }
    }
  }

  const importWizardData = (data: any): boolean => {
    try {
      if (data.stepData) {
        currentStepData.value = data.stepData
      }
      if (data.validations) {
        stepValidations.value = data.validations
      }
      if (data.transitions) {
        stepTransitions.value = data.transitions
      }
      if (data.dataFlowLog) {
        dataFlowLog.value = data.dataFlowLog
      }
      
      ElMessage.success('向导数据导入成功')
      return true
    } catch (error) {
      console.error('导入向导数据失败:', error)
      ElMessage.error('导入向导数据失败')
      return false
    }
  }

  const resetWizardData = () => {
    currentStepData.value = {}
    stepValidations.value = {}
    stepTransitions.value = []
    dataFlowLog.value = []
    
    ElMessage.info('向导数据已重置')
  }

  // 监听器
  watch(stepValidations, (newValidations) => {
    console.log('步骤验证状态更新:', newValidations)
  }, { deep: true })

  watch(currentStepData, (newData) => {
    console.log('步骤数据更新:', newData)
  }, { deep: true })

  return {
    // 状态
    currentStepData,
    stepValidations,
    stepTransitions,
    dataFlowLog,
    
    // 计算属性
    isCurrentStepValid,
    hasValidationErrors,
    hasValidationWarnings,
    stepCompletionRate,
    
    // 方法
    updateStepData,
    updateStepValidation,
    prepareStepTransition,
    getStepSummary,
    getAllStepsSummary,
    exportWizardData,
    importWizardData,
    resetWizardData
  }
}