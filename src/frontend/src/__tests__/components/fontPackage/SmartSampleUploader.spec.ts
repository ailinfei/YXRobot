/**
 * SmartSampleUploader 组件测试
 */

import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { ElMessage } from 'element-plus'
import SmartSampleUploader from '@/components/fontPackage/SmartSampleUploader.vue'

// Mock Element Plus
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn()
  },
  ElMessageBox: {
    confirm: vi.fn()
  }
}))

// Mock file reader
global.FileReader = class {
  result: string | ArrayBuffer | null = null
  onload: ((event: ProgressEvent<FileReader>) => void) | null = null
  onerror: ((event: ProgressEvent<FileReader>) => void) | null = null
  
  readAsDataURL(file: File) {
    setTimeout(() => {
      this.result = `data:image/jpeg;base64,mock-base64-data-for-${file.name}`
      if (this.onload) {
        this.onload({ target: this } as ProgressEvent<FileReader>)
      }
    }, 10)
  }
  
  readAsArrayBuffer(file: File) {
    setTimeout(() => {
      this.result = new ArrayBuffer(8)
      if (this.onload) {
        this.onload({ target: this } as ProgressEvent<FileReader>)
      }
    }, 10)
  }
}

// Mock URL.createObjectURL
global.URL.createObjectURL = vi.fn(() => 'mock-url')
global.URL.revokeObjectURL = vi.fn()

describe('SmartSampleUploader', () => {
  let wrapper: any

  beforeEach(() => {
    vi.clearAllMocks()
    wrapper = mount(SmartSampleUploader, {
      props: {
        modelValue: [],
        maxFiles: 50,
        maxSize: 10 * 1024 * 1024
      }
    })
  })

  it('应该正确渲染上传组件', () => {
    expect(wrapper.find('.smart-sample-uploader').exists()).toBe(true)
    expect(wrapper.find('.upload-zone').exists()).toBe(true)
    expect(wrapper.text()).toContain('拖拽文件到此处或点击上传')
  })

  it('应该显示智能提示', async () => {
    await wrapper.vm.$nextTick()
    
    const tips = wrapper.find('.smart-tips')
    expect(tips.exists()).toBe(true)
    expect(wrapper.text()).toContain('还没有上传任何文件')
  })

  it('应该验证文件类型', async () => {
    const invalidFile = new File(['test'], 'test.txt', { type: 'text/plain' })
    
    const isValid = wrapper.vm.validateFile(invalidFile)
    expect(isValid).toBe(false)
    expect(ElMessage.error).toHaveBeenCalledWith('不支持的文件类型: test.txt')
  })

  it('应该验证文件大小', async () => {
    const largeFile = new File(['x'.repeat(11 * 1024 * 1024)], 'large.jpg', { 
      type: 'image/jpeg' 
    })
    
    const isValid = wrapper.vm.validateFile(largeFile)
    expect(isValid).toBe(false)
    expect(ElMessage.error).toHaveBeenCalledWith(
      expect.stringContaining('文件过大: large.jpg')
    )
  })

  it('应该检测重复文件', async () => {
    const file1 = new File(['test'], 'test.jpg', { type: 'image/jpeg' })
    const file2 = new File(['test'], 'test.jpg', { type: 'image/jpeg' })
    
    // 先添加第一个文件
    await wrapper.setProps({
      modelValue: [{
        uid: '1',
        name: 'test.jpg',
        size: file1.size,
        type: file1.type,
        url: 'mock-url',
        raw: file1,
        status: 'success'
      }]
    })
    
    const isValid = wrapper.vm.validateFile(file2)
    expect(isValid).toBe(false)
    expect(ElMessage.warning).toHaveBeenCalledWith('文件已存在: test.jpg')
  })

  it('应该从文件名提取中文字符', () => {
    const chars = wrapper.vm.extractCharactersFromFilename('测试文件_123_test.jpg')
    expect(chars).toEqual(['测', '试', '文', '件'])
  })

  it('应该正确格式化文件大小', () => {
    expect(wrapper.vm.formatFileSize(0)).toBe('0 B')
    expect(wrapper.vm.formatFileSize(1024)).toBe('1 KB')
    expect(wrapper.vm.formatFileSize(1024 * 1024)).toBe('1 MB')
    expect(wrapper.vm.formatFileSize(1024 * 1024 * 1024)).toBe('1 GB')
  })

  it('应该处理文件上传', async () => {
    const file = new File(['test'], '测试.jpg', { type: 'image/jpeg' })
    
    await wrapper.vm.processFiles([file])
    
    // 等待异步操作完成
    await new Promise(resolve => setTimeout(resolve, 50))
    
    expect(wrapper.emitted('update:modelValue')).toBeTruthy()
    expect(wrapper.emitted('file-added')).toBeTruthy()
    expect(wrapper.emitted('files-changed')).toBeTruthy()
  })

  it('应该切换视图模式', async () => {
    expect(wrapper.vm.viewMode).toBe('list')
    
    await wrapper.vm.toggleViewMode()
    expect(wrapper.vm.viewMode).toBe('grid')
    
    await wrapper.vm.toggleViewMode()
    expect(wrapper.vm.viewMode).toBe('list')
  })

  it('应该处理文件选择', async () => {
    const files = [
      {
        uid: '1',
        name: 'test1.jpg',
        size: 1024,
        type: 'image/jpeg',
        url: 'mock-url-1',
        raw: new File(['test1'], 'test1.jpg', { type: 'image/jpeg' }),
        status: 'success'
      },
      {
        uid: '2',
        name: 'test2.jpg',
        size: 2048,
        type: 'image/jpeg',
        url: 'mock-url-2',
        raw: new File(['test2'], 'test2.jpg', { type: 'image/jpeg' }),
        status: 'success'
      }
    ]
    
    await wrapper.setProps({ modelValue: files })
    
    // 选择文件
    wrapper.vm.toggleFileSelection('1')
    expect(wrapper.vm.selectedFiles).toContain('1')
    
    // 取消选择
    wrapper.vm.toggleFileSelection('1')
    expect(wrapper.vm.selectedFiles).not.toContain('1')
    
    // 全选
    wrapper.vm.selectAllFiles()
    expect(wrapper.vm.selectedFiles).toEqual(['1', '2'])
    
    // 清空选择
    wrapper.vm.clearSelection()
    expect(wrapper.vm.selectedFiles).toEqual([])
  })

  it('应该生成智能提示', async () => {
    // 测试无文件时的提示
    wrapper.vm.generateSmartTips()
    expect(wrapper.vm.smartTips).toHaveLength(1)
    expect(wrapper.vm.smartTips[0].message).toContain('还没有上传任何文件')
    
    // 测试有文件时的提示
    const files = Array.from({ length: 5 }, (_, i) => ({
      uid: `${i}`,
      name: `test${i}.jpg`,
      size: 1024,
      type: 'image/jpeg',
      url: `mock-url-${i}`,
      raw: new File([`test${i}`], `test${i}.jpg`, { type: 'image/jpeg' }),
      status: 'success',
      recognizedCharacters: [`字${i}`]
    }))
    
    await wrapper.setProps({ modelValue: files })
    wrapper.vm.generateSmartTips()
    
    expect(wrapper.vm.smartTips.some((tip: any) => 
      tip.message.includes('建议上传更多样本')
    )).toBe(true)
  })

  it('应该处理拖拽事件', async () => {
    const uploadZone = wrapper.find('.upload-zone')
    
    // 测试拖拽进入
    await uploadZone.trigger('dragenter')
    expect(wrapper.vm.isDragOver).toBe(true)
    
    // 测试拖拽离开
    await uploadZone.trigger('dragleave')
    // 注意：实际的拖拽离开逻辑更复杂，这里简化测试
    
    // 测试拖拽放置
    const mockFiles = [new File(['test'], 'test.jpg', { type: 'image/jpeg' })]
    const mockEvent = {
      preventDefault: vi.fn(),
      dataTransfer: { files: mockFiles }
    }
    
    await wrapper.vm.handleDrop(mockEvent)
    expect(mockEvent.preventDefault).toHaveBeenCalled()
    expect(wrapper.vm.isDragOver).toBe(false)
  })

  it('应该处理文件预览', async () => {
    const file = {
      uid: '1',
      name: 'test.jpg',
      url: 'mock-url',
      size: 1024,
      type: 'image/jpeg',
      raw: new File(['test'], 'test.jpg', { type: 'image/jpeg' }),
      status: 'success',
      recognizedCharacters: ['测', '试']
    }
    
    wrapper.vm.previewFile(file)
    
    expect(wrapper.vm.previewDialog.visible).toBe(true)
    expect(wrapper.vm.previewDialog.file).toBe(file)
  })

  it('应该处理文件编辑', async () => {
    const file = {
      uid: '1',
      name: 'test.jpg',
      url: 'mock-url',
      size: 1024,
      type: 'image/jpeg',
      raw: new File(['test'], 'test.jpg', { type: 'image/jpeg' }),
      status: 'success',
      recognizedCharacters: ['测', '试']
    }
    
    wrapper.vm.editFile(file)
    
    expect(wrapper.vm.editDialog.visible).toBe(true)
    expect(wrapper.vm.editDialog.file).toBe(file)
    expect(wrapper.vm.editDialog.form.name).toBe('test.jpg')
    expect(wrapper.vm.editDialog.form.charactersInput).toBe('测 试')
  })
})