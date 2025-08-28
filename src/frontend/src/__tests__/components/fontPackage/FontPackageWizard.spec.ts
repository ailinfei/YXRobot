import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { ElButton, ElDialog, ElMessage } from 'element-plus'
import FontPackageWizard from '@/components/fontPackage/FontPackageWizard.vue'

// Mock Element Plus components
vi.mock('element-plus', () => ({
  ElButton: { name: 'ElButton', template: '<button><slot /></button>' },
  ElDialog: { name: 'ElDialog', template: '<div><slot /></div>' },
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn()
  },
  ElMessageBox: {
    confirm: vi.fn(() => Promise.resolve())
  }
}))

// Mock step components
vi.mock('@/components/fontPackage/wizard/WizardStepBasicInfo.vue', () => ({
  default: { name: 'WizardStepBasicInfo', template: '<div>Basic Info Step</div>' }
}))

vi.mock('@/components/fontPackage/wizard/WizardStepSampleUpload.vue', () => ({
  default: { name: 'WizardStepSampleUpload', template: '<div>Sample Upload Step</div>' }
}))

vi.mock('@/components/fontPackage/wizard/WizardStepConfiguration.vue', () => ({
  default: { name: 'WizardStepConfiguration', template: '<div>Configuration Step</div>' }
}))

vi.mock('@/components/fontPackage/wizard/WizardStepReview.vue', () => ({
  default: { name: 'WizardStepReview', template: '<div>Review Step</div>' }
}))

describe('FontPackageWizard', () => {
  let wrapper: any

  beforeEach(() => {
    // Mock localStorage
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: vi.fn(),
        setItem: vi.fn(),
        removeItem: vi.fn()
      }
    })

    wrapper = mount(FontPackageWizard, {
      props: {
        modelValue: true
      },
      global: {
        components: {
          ElButton,
          ElDialog
        }
      }
    })
  })

  it('renders correctly', () => {
    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('.font-package-wizard').exists()).toBe(true)
  })

  it('displays correct step information', () => {
    expect(wrapper.find('.wizard-progress').exists()).toBe(true)
    expect(wrapper.find('.wizard-steps').exists()).toBe(true)
    expect(wrapper.find('.wizard-content').exists()).toBe(true)
    expect(wrapper.find('.wizard-footer').exists()).toBe(true)
  })

  it('shows step 1 as active initially', () => {
    const steps = wrapper.findAll('.step-item')
    expect(steps[0].classes()).toContain('active')
    expect(steps[1].classes()).not.toContain('active')
  })

  it('displays correct progress percentage', () => {
    const progressText = wrapper.find('.progress-text')
    expect(progressText.text()).toContain('步骤 1 / 4')
  })

  it('has correct initial wizard data structure', () => {
    const component = wrapper.vm
    expect(component.wizardData).toHaveProperty('basicInfo')
    expect(component.wizardData).toHaveProperty('sampleUpload')
    expect(component.wizardData).toHaveProperty('configuration')
    expect(component.wizardData).toHaveProperty('review')
  })

  it('initializes with default values', () => {
    const component = wrapper.vm
    expect(component.wizardData.basicInfo.fontType).toBe('kaishu')
    expect(component.wizardData.basicInfo.difficulty).toBe(3)
    expect(component.wizardData.basicInfo.version).toBe('v1.0.0')
  })

  it('emits complete event when wizard is completed', async () => {
    const component = wrapper.vm
    
    // Mock all steps as valid
    component.stepValidations = {
      1: { isValid: true, errors: [], warnings: [] },
      2: { isValid: true, errors: [], warnings: [] },
      3: { isValid: true, errors: [], warnings: [] },
      4: { isValid: true, errors: [], warnings: [] }
    }
    
    // Set review confirmed
    component.wizardData.review.confirmed = true
    
    await component.handleComplete()
    
    expect(wrapper.emitted('complete')).toBeTruthy()
  })

  it('saves draft to localStorage', () => {
    const component = wrapper.vm
    component.saveToLocalStorage()
    
    expect(window.localStorage.setItem).toHaveBeenCalledWith(
      'font-package-wizard-draft',
      expect.any(String)
    )
  })

  it('validates steps correctly', () => {
    const component = wrapper.vm
    
    // Test step validation
    const validation = {
      isValid: true,
      errors: [],
      warnings: ['Test warning']
    }
    
    component.handleStepValidation(validation)
    
    expect(component.stepValidations[1]).toEqual(validation)
  })
})