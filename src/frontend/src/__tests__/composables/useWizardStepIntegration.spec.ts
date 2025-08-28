/**
 * useWizardStepIntegration 组合式函数测试
 */

import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useWizardStepIntegration } from '@/composables/useWizardStepIntegration'

// Mock Element Plus
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn()
  }
}))

describe('useWizardStepIntegration', () => {
  let integration: ReturnType<typeof useWizardStepIntegration>

  beforeEach(() => {
    vi.clearAllMocks()
    integration = useWizardStepIntegration()
  })

  it('应该初始化空的状态', () => {
    expect(integration.currentStepData.value).toEqual({})
    expect(integration.stepValidations.value).toEqual({})
    expect(integration.stepTransitions.value).toEqual([])
    expect(integration.dataFlowLog.value).toEqual([])
  })

  it('应该更新步骤数据', () => {
    const stepData = {
      name: 'Test Font',
      description: 'Test Description'
    }

    integration.updateStepData(1, stepData)

    expect(integration.currentStepData.value[1]).toEqual(stepData)
    expect(integration.dataFlowLog.value).toHaveLength(1)
    expect(integration.dataFlowLog.value[0].action).toBe('update')
    expect(integration.dataFlowLog.value[0].step).toBe(1)
  })

  it('应该更新步骤验证状态', () => {
    const validation = {
      isValid: true,
      errors: [],
      warnings: ['Test warning']
    }

    integration.updateStepValidation(1, validation)

    expect(integration.stepValidations.value[1]).toEqual(validation)
    expect(integration.dataFlowLog.value).toHaveLength(1)
    expect(integration.dataFlowLog.value[0].action).toBe('validate')
  })

  it('应该计算步骤完成率', () => {
    // 添加一些验证状态
    integration.updateStepValidation(1, { isValid: true, errors: [], warnings: [] })
    integration.updateStepValidation(2, { isValid: false, errors: ['Error'], warnings: [] })
    integration.updateStepValidation(3, { isValid: true, errors: [], warnings: [] })

    expect(integration.stepCompletionRate.value).toBe(67) // 2/3 * 100 = 67%
  })

  it('应该检测验证错误', () => {
    integration.updateStepValidation(1, { isValid: true, errors: [], warnings: [] })
    expect(integration.hasValidationErrors.value).toBe(false)

    integration.updateStepValidation(2, { isValid: false, errors: ['Error'], warnings: [] })
    expect(integration.hasValidationErrors.value).toBe(true)
  })

  it('应该检测验证警告', () => {
    integration.updateStepValidation(1, { isValid: true, errors: [], warnings: [] })
    expect(integration.hasValidationWarnings.value).toBe(false)

    integration.updateStepValidation(2, { isValid: true, errors: [], warnings: ['Warning'] })
    expect(integration.hasValidationWarnings.value).toBe(true)
  })

  it('应该准备步骤转换', () => {
    // 设置有效的源步骤
    integration.updateStepValidation(1, { isValid: true, errors: [], warnings: [] })
    integration.updateStepData(1, { fontType: 'kaishu', difficulty: 3 })

    const canTransition = integration.prepareStepTransition(1, 2)

    expect(canTransition).toBe(true)
    expect(integration.stepTransitions.value).toHaveLength(1)
    expect(integration.stepTransitions.value[0].from).toBe(1)
    expect(integration.stepTransitions.value[0].to).toBe(2)
  })

  it('应该阻止无效步骤的转换', () => {
    // 设置无效的源步骤
    integration.updateStepValidation(1, { isValid: false, errors: ['Error'], warnings: [] })

    const canTransition = integration.prepareStepTransition(1, 2)

    expect(canTransition).toBe(false)
    expect(integration.stepTransitions.value).toHaveLength(0)
  })

  it('应该生成建议字符', () => {
    // 这是一个私有方法，我们通过步骤转换来测试
    integration.updateStepValidation(1, { isValid: true, errors: [], warnings: [] })
    integration.updateStepData(1, { fontType: 'kaishu' })

    integration.prepareStepTransition(1, 2)

    const step2Data = integration.currentStepData.value[2]
    expect(step2Data.suggestedCharacters).toBeDefined()
    expect(Array.isArray(step2Data.suggestedCharacters)).toBe(true)
    expect(step2Data.suggestedCharacters.length).toBeGreaterThan(0)
  })

  it('应该计算训练复杂度', () => {
    integration.updateStepValidation(2, { isValid: true, errors: [], warnings: [] })
    integration.updateStepData(2, {
      targetCharacters: Array.from({ length: 150 }, (_, i) => `字${i}`),
      uploadedFiles: Array.from({ length: 250 }, (_, i) => ({ name: `file${i}.jpg` })),
      analysisResult: { qualityScore: 50 }
    })

    integration.prepareStepTransition(2, 3)

    const step3Data = integration.currentStepData.value[3]
    expect(step3Data.trainingComplexity).toBe('high')
  })

  it('应该生成推荐设置', () => {
    integration.updateStepValidation(2, { isValid: true, errors: [], warnings: [] })
    integration.updateStepData(2, {
      targetCharacters: Array.from({ length: 30 }, (_, i) => `字${i}`),
      uploadedFiles: Array.from({ length: 50 }, (_, i) => ({ name: `file${i}.jpg` })),
      analysisResult: { qualityScore: 85 }
    })

    integration.prepareStepTransition(2, 3)

    const step3Data = integration.currentStepData.value[3]
    expect(step3Data.recommendedSettings).toBeDefined()
    expect(step3Data.recommendedSettings.epochs).toBeDefined()
    expect(step3Data.recommendedSettings.batchSize).toBeDefined()
    expect(step3Data.recommendedSettings.learningRate).toBeDefined()
  })

  it('应该计算估算时间', () => {
    integration.updateStepValidation(3, { isValid: true, errors: [], warnings: [] })
    integration.updateStepData(3, {
      trainingSettings: { epochs: 100 },
      characterCount: 25
    })

    integration.prepareStepTransition(3, 4)

    const step4Data = integration.currentStepData.value[4]
    expect(step4Data.estimatedTime).toBeDefined()
    expect(typeof step4Data.estimatedTime).toBe('number')
    expect(step4Data.estimatedTime).toBeGreaterThan(0)
  })

  it('应该获取步骤摘要', () => {
    integration.updateStepData(1, { name: 'Test Font' })
    integration.updateStepValidation(1, { isValid: true, errors: [], warnings: [] })

    const summary = integration.getStepSummary(1)

    expect(summary.step).toBe(1)
    expect(summary.data).toEqual({ name: 'Test Font' })
    expect(summary.validation.isValid).toBe(true)
    expect(summary.isComplete).toBe(true)
    expect(summary.lastUpdated).toBeDefined()
  })

  it('应该获取所有步骤摘要', () => {
    integration.updateStepValidation(1, { isValid: true, errors: [], warnings: [] })
    integration.updateStepValidation(2, { isValid: false, errors: ['Error'], warnings: [] })

    const summaries = integration.getAllStepsSummary()

    expect(summaries).toHaveLength(2)
    expect(summaries[0].step).toBe(1)
    expect(summaries[1].step).toBe(2)
  })

  it('应该导出向导数据', () => {
    integration.updateStepData(1, { name: 'Test' })
    integration.updateStepValidation(1, { isValid: true, errors: [], warnings: [] })

    const exportedData = integration.exportWizardData()

    expect(exportedData.stepData).toBeDefined()
    expect(exportedData.validations).toBeDefined()
    expect(exportedData.transitions).toBeDefined()
    expect(exportedData.dataFlowLog).toBeDefined()
    expect(exportedData.summary).toBeDefined()
    expect(exportedData.summary.exportTime).toBeDefined()
  })

  it('应该导入向导数据', () => {
    const importData = {
      stepData: { 1: { name: 'Imported Font' } },
      validations: { 1: { isValid: true, errors: [], warnings: [] } },
      transitions: [],
      dataFlowLog: []
    }

    const success = integration.importWizardData(importData)

    expect(success).toBe(true)
    expect(integration.currentStepData.value[1]).toEqual({ name: 'Imported Font' })
    expect(integration.stepValidations.value[1].isValid).toBe(true)
  })

  it('应该重置向导数据', () => {
    // 先添加一些数据
    integration.updateStepData(1, { name: 'Test' })
    integration.updateStepValidation(1, { isValid: true, errors: [], warnings: [] })

    // 重置
    integration.resetWizardData()

    expect(integration.currentStepData.value).toEqual({})
    expect(integration.stepValidations.value).toEqual({})
    expect(integration.stepTransitions.value).toEqual([])
    expect(integration.dataFlowLog.value).toEqual([])
  })
})