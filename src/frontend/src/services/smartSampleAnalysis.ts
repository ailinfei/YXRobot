/**
 * 智能样本分析服务
 * 提供字符识别、重复检测和基础质量检查功能
 */

export interface UploadedFile {
  uid: string
  name: string
  url: string
  raw: File
  size: number
  type: string
  status: 'ready' | 'uploading' | 'success' | 'error'
}

export interface CharacterRecognitionResult {
  character: string
  confidence: number
  boundingBox?: {
    x: number
    y: number
    width: number
    height: number
  }
}

export interface DuplicateInfo {
  file: string
  original: string
  similarity: number
  type: 'exact' | 'similar' | 'character'
}

export interface QualityIssue {
  type: 'format' | 'size' | 'clarity' | 'character' | 'background'
  message: string
  severity: 'low' | 'medium' | 'high'
  affectedFiles: string[]
  suggestion?: string
}

export interface BasicQualityResult {
  validFiles: number
  invalidFiles: number
  averageQuality: number
  issues: QualityIssue[]
}

export interface SampleAnalysisResult {
  totalFiles: number
  recognizedCharacters: number
  duplicates: DuplicateInfo[]
  basicQualityCheck: BasicQualityResult
  analysisTimestamp: string
  characterMap: Record<string, {
    files: string[]
    quality: number
    recommendations: string[]
  }>
}

export class SmartSampleAnalysisService {
  private canvas: HTMLCanvasElement
  private ctx: CanvasRenderingContext2D

  constructor() {
    this.canvas = document.createElement('canvas')
    this.ctx = this.canvas.getContext('2d')!
  }

  /**
   * 执行完整的智能分析
   */
  async analyzeFiles(files: UploadedFile[], targetCharacters: string[]): Promise<SampleAnalysisResult> {
    console.log('开始智能分析', files.length, '个文件')
    
    const startTime = Date.now()
    
    // 并行执行各种分析
    const [characterRecognition, duplicates, qualityCheck] = await Promise.all([
      this.performCharacterRecognition(files),
      this.detectDuplicates(files),
      this.performQualityCheck(files)
    ])

    // 构建字符映射
    const characterMap = this.buildCharacterMap(files, characterRecognition, targetCharacters)

    const result: SampleAnalysisResult = {
      totalFiles: files.length,
      recognizedCharacters: Object.keys(characterMap).length,
      duplicates,
      basicQualityCheck: qualityCheck,
      analysisTimestamp: new Date().toISOString(),
      characterMap
    }

    console.log('智能分析完成，耗时:', Date.now() - startTime, 'ms')
    return result
  }

  /**
   * 字符识别功能
   */
  private async performCharacterRecognition(files: UploadedFile[]): Promise<Record<string, CharacterRecognitionResult[]>> {
    const results: Record<string, CharacterRecognitionResult[]> = {}

    for (const file of files) {
      try {
        const recognitionResult = await this.recognizeCharacterFromFile(file)
        results[file.name] = recognitionResult
      } catch (error) {
        console.warn(`字符识别失败: ${file.name}`, error)
        results[file.name] = []
      }
    }

    return results
  }

  /**
   * 从文件名和图像内容识别字符
   */
  private async recognizeCharacterFromFile(file: UploadedFile): Promise<CharacterRecognitionResult[]> {
    const results: CharacterRecognitionResult[] = []

    // 1. 从文件名识别字符
    const filenameChars = this.extractChineseFromFilename(file.name)
    filenameChars.forEach(char => {
      results.push({
        character: char,
        confidence: 0.9, // 文件名识别置信度较高
      })
    })

    // 2. 从图像内容识别（简化版OCR）
    try {
      const imageChars = await this.performSimpleOCR(file)
      imageChars.forEach(result => {
        // 避免重复添加已从文件名识别的字符
        if (!results.some(r => r.character === result.character)) {
          results.push(result)
        }
      })
    } catch (error) {
      console.warn('图像OCR识别失败:', error)
    }

    return results
  }

  /**
   * 从文件名提取中文字符
   */
  private extractChineseFromFilename(filename: string): string[] {
    const chineseRegex = /[\u4e00-\u9fa5]/g
    const matches = filename.match(chineseRegex)
    return matches ? [...new Set(matches)] : []
  }

  /**
   * 简化版OCR识别（基于图像特征）
   */
  private async performSimpleOCR(file: UploadedFile): Promise<CharacterRecognitionResult[]> {
    return new Promise((resolve) => {
      const img = new Image()
      img.onload = () => {
        try {
          // 设置画布尺寸
          this.canvas.width = img.width
          this.canvas.height = img.height
          
          // 绘制图像
          this.ctx.drawImage(img, 0, 0)
          
          // 获取图像数据
          const imageData = this.ctx.getImageData(0, 0, img.width, img.height)
          
          // 简化的字符识别逻辑
          const recognizedChars = this.analyzeImageFeatures(imageData)
          
          resolve(recognizedChars)
        } catch (error) {
          console.warn('OCR处理失败:', error)
          resolve([])
        }
      }
      
      img.onerror = () => {
        resolve([])
      }
      
      img.src = file.url
    })
  }

  /**
   * 分析图像特征进行字符识别
   */
  private analyzeImageFeatures(imageData: ImageData): CharacterRecognitionResult[] {
    // 这里实现简化的图像特征分析
    // 实际项目中可以集成更专业的OCR库
    
    const { width, height, data } = imageData
    const results: CharacterRecognitionResult[] = []
    
    // 计算图像的基本特征
    let totalPixels = 0
    let darkPixels = 0
    
    for (let i = 0; i < data.length; i += 4) {
      const r = data[i]
      const g = data[i + 1]
      const b = data[i + 2]
      const brightness = (r + g + b) / 3
      
      totalPixels++
      if (brightness < 128) {
        darkPixels++
      }
    }
    
    const darkRatio = darkPixels / totalPixels
    
    // 基于图像特征推测可能的字符
    // 这是一个非常简化的示例，实际应用中需要更复杂的算法
    if (darkRatio > 0.3 && darkRatio < 0.7) {
      // 假设这是一个有效的字符图像
      results.push({
        character: '未', // 占位符，实际需要更复杂的识别算法
        confidence: Math.min(0.8, darkRatio * 1.2),
        boundingBox: {
          x: Math.floor(width * 0.1),
          y: Math.floor(height * 0.1),
          width: Math.floor(width * 0.8),
          height: Math.floor(height * 0.8)
        }
      })
    }
    
    return results
  }

  /**
   * 重复文件检测
   */
  private async detectDuplicates(files: UploadedFile[]): Promise<DuplicateInfo[]> {
    const duplicates: DuplicateInfo[] = []
    const fileHashes = new Map<string, string>()
    
    // 计算文件哈希值
    for (const file of files) {
      try {
        const hash = await this.calculateFileHash(file)
        
        // 检查是否存在相同哈希值的文件
        for (const [existingHash, existingFile] of fileHashes.entries()) {
          const similarity = this.calculateHashSimilarity(hash, existingHash)
          
          if (similarity > 0.95) {
            duplicates.push({
              file: file.name,
              original: existingFile,
              similarity,
              type: similarity === 1 ? 'exact' : 'similar'
            })
          }
        }
        
        fileHashes.set(hash, file.name)
      } catch (error) {
        console.warn(`计算文件哈希失败: ${file.name}`, error)
      }
    }

    // 基于文件名的字符重复检测
    const characterDuplicates = this.detectCharacterDuplicates(files)
    duplicates.push(...characterDuplicates)

    return duplicates
  }

  /**
   * 计算文件哈希值（简化版）
   */
  private async calculateFileHash(file: UploadedFile): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      
      reader.onload = () => {
        const arrayBuffer = reader.result as ArrayBuffer
        const uint8Array = new Uint8Array(arrayBuffer)
        
        // 简化的哈希计算（实际项目中应使用更好的哈希算法）
        let hash = 0
        for (let i = 0; i < Math.min(uint8Array.length, 1024); i++) {
          hash = ((hash << 5) - hash + uint8Array[i]) & 0xffffffff
        }
        
        resolve(hash.toString(16))
      }
      
      reader.onerror = reject
      reader.readAsArrayBuffer(file.raw)
    })
  }

  /**
   * 计算哈希相似度
   */
  private calculateHashSimilarity(hash1: string, hash2: string): number {
    if (hash1 === hash2) return 1
    
    // 简化的相似度计算
    let matches = 0
    const minLength = Math.min(hash1.length, hash2.length)
    
    for (let i = 0; i < minLength; i++) {
      if (hash1[i] === hash2[i]) {
        matches++
      }
    }
    
    return matches / Math.max(hash1.length, hash2.length)
  }

  /**
   * 基于字符的重复检测
   */
  private detectCharacterDuplicates(files: UploadedFile[]): DuplicateInfo[] {
    const duplicates: DuplicateInfo[] = []
    const characterFiles = new Map<string, string>()
    
    files.forEach(file => {
      const chars = this.extractChineseFromFilename(file.name)
      
      chars.forEach(char => {
        if (characterFiles.has(char)) {
          duplicates.push({
            file: file.name,
            original: characterFiles.get(char)!,
            similarity: 1,
            type: 'character'
          })
        } else {
          characterFiles.set(char, file.name)
        }
      })
    })
    
    return duplicates
  }

  /**
   * 基础质量检查
   */
  private async performQualityCheck(files: UploadedFile[]): Promise<BasicQualityResult> {
    const issues: QualityIssue[] = []
    let validFiles = 0
    let totalQuality = 0
    
    for (const file of files) {
      try {
        const quality = await this.checkFileQuality(file)
        
        if (quality.isValid) {
          validFiles++
          totalQuality += quality.score
        }
        
        issues.push(...quality.issues)
      } catch (error) {
        console.warn(`质量检查失败: ${file.name}`, error)
        issues.push({
          type: 'format',
          message: `文件 ${file.name} 无法读取`,
          severity: 'high',
          affectedFiles: [file.name],
          suggestion: '请检查文件格式是否正确'
        })
      }
    }
    
    return {
      validFiles,
      invalidFiles: files.length - validFiles,
      averageQuality: validFiles > 0 ? Math.round(totalQuality / validFiles) : 0,
      issues: this.consolidateIssues(issues)
    }
  }

  /**
   * 检查单个文件质量
   */
  private async checkFileQuality(file: UploadedFile): Promise<{
    isValid: boolean
    score: number
    issues: QualityIssue[]
  }> {
    const issues: QualityIssue[] = []
    let score = 100
    
    // 1. 文件大小检查
    if (file.size > 10 * 1024 * 1024) { // 10MB
      issues.push({
        type: 'size',
        message: '文件过大',
        severity: 'medium',
        affectedFiles: [file.name],
        suggestion: '建议压缩图片或降低分辨率'
      })
      score -= 10
    }
    
    if (file.size < 1024) { // 1KB
      issues.push({
        type: 'size',
        message: '文件过小',
        severity: 'medium',
        affectedFiles: [file.name],
        suggestion: '文件可能损坏或分辨率过低'
      })
      score -= 15
    }
    
    // 2. 格式检查
    const validFormats = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']
    if (!validFormats.includes(file.type)) {
      issues.push({
        type: 'format',
        message: '不支持的文件格式',
        severity: 'high',
        affectedFiles: [file.name],
        suggestion: '请使用 JPG、PNG 或 GIF 格式'
      })
      score -= 30
    }
    
    // 3. 图像质量检查
    try {
      const imageQuality = await this.analyzeImageQuality(file)
      score = Math.min(score, score * imageQuality.qualityFactor)
      issues.push(...imageQuality.issues)
    } catch (error) {
      console.warn('图像质量分析失败:', error)
    }
    
    return {
      isValid: score >= 50,
      score: Math.max(0, Math.round(score)),
      issues
    }
  }

  /**
   * 分析图像质量
   */
  private async analyzeImageQuality(file: UploadedFile): Promise<{
    qualityFactor: number
    issues: QualityIssue[]
  }> {
    return new Promise((resolve) => {
      const img = new Image()
      
      img.onload = () => {
        const issues: QualityIssue[] = []
        let qualityFactor = 1.0
        
        // 分辨率检查
        if (img.width < 100 || img.height < 100) {
          issues.push({
            type: 'size',
            message: '图像分辨率过低',
            severity: 'medium',
            affectedFiles: [file.name],
            suggestion: '建议使用至少 100x100 像素的图像'
          })
          qualityFactor *= 0.8
        }
        
        if (img.width > 2000 || img.height > 2000) {
          issues.push({
            type: 'size',
            message: '图像分辨率过高',
            severity: 'low',
            affectedFiles: [file.name],
            suggestion: '可以适当降低分辨率以提高处理速度'
          })
        }
        
        // 宽高比检查
        const aspectRatio = img.width / img.height
        if (aspectRatio > 3 || aspectRatio < 0.33) {
          issues.push({
            type: 'size',
            message: '图像宽高比异常',
            severity: 'medium',
            affectedFiles: [file.name],
            suggestion: '建议使用接近正方形的图像'
          })
          qualityFactor *= 0.9
        }
        
        // 使用画布分析图像内容
        this.canvas.width = img.width
        this.canvas.height = img.height
        this.ctx.drawImage(img, 0, 0)
        
        const imageData = this.ctx.getImageData(0, 0, img.width, img.height)
        const contentAnalysis = this.analyzeImageContent(imageData)
        
        issues.push(...contentAnalysis.issues.map(issue => ({
          ...issue,
          affectedFiles: [file.name]
        })))
        
        qualityFactor *= contentAnalysis.qualityFactor
        
        resolve({ qualityFactor, issues })
      }
      
      img.onerror = () => {
        resolve({
          qualityFactor: 0.5,
          issues: [{
            type: 'format',
            message: '图像无法加载',
            severity: 'high',
            affectedFiles: [file.name],
            suggestion: '请检查图像文件是否损坏'
          }]
        })
      }
      
      img.src = file.url
    })
  }

  /**
   * 分析图像内容质量
   */
  private analyzeImageContent(imageData: ImageData): {
    qualityFactor: number
    issues: Omit<QualityIssue, 'affectedFiles'>[]
  } {
    const { width, height, data } = imageData
    const issues: Omit<QualityIssue, 'affectedFiles'>[] = []
    let qualityFactor = 1.0
    
    // 计算图像统计信息
    let totalBrightness = 0
    let minBrightness = 255
    let maxBrightness = 0
    let edgePixels = 0
    
    for (let i = 0; i < data.length; i += 4) {
      const r = data[i]
      const g = data[i + 1]
      const b = data[i + 2]
      const brightness = (r + g + b) / 3
      
      totalBrightness += brightness
      minBrightness = Math.min(minBrightness, brightness)
      maxBrightness = Math.max(maxBrightness, brightness)
      
      // 简单的边缘检测
      const x = (i / 4) % width
      const y = Math.floor((i / 4) / width)
      
      if (x > 0 && y > 0 && x < width - 1 && y < height - 1) {
        const neighbors = [
          data[i - 4], data[i + 4], // 左右
          data[i - width * 4], data[i + width * 4] // 上下
        ]
        
        const avgNeighbor = neighbors.reduce((sum, val) => sum + val, 0) / neighbors.length
        if (Math.abs(brightness - avgNeighbor) > 50) {
          edgePixels++
        }
      }
    }
    
    const avgBrightness = totalBrightness / (data.length / 4)
    const contrast = maxBrightness - minBrightness
    const edgeRatio = edgePixels / (width * height)
    
    // 亮度检查
    if (avgBrightness < 50) {
      issues.push({
        type: 'clarity',
        message: '图像过暗',
        severity: 'medium',
        suggestion: '建议提高图像亮度'
      })
      qualityFactor *= 0.8
    } else if (avgBrightness > 200) {
      issues.push({
        type: 'clarity',
        message: '图像过亮',
        severity: 'medium',
        suggestion: '建议降低图像亮度'
      })
      qualityFactor *= 0.8
    }
    
    // 对比度检查
    if (contrast < 50) {
      issues.push({
        type: 'clarity',
        message: '图像对比度不足',
        severity: 'medium',
        suggestion: '建议增强图像对比度'
      })
      qualityFactor *= 0.7
    }
    
    // 清晰度检查（基于边缘密度）
    if (edgeRatio < 0.01) {
      issues.push({
        type: 'clarity',
        message: '图像可能模糊',
        severity: 'medium',
        suggestion: '建议使用更清晰的图像'
      })
      qualityFactor *= 0.8
    }
    
    return { qualityFactor, issues }
  }

  /**
   * 合并相似的问题
   */
  private consolidateIssues(issues: QualityIssue[]): QualityIssue[] {
    const consolidated = new Map<string, QualityIssue>()
    
    issues.forEach(issue => {
      const key = `${issue.type}-${issue.message}`
      
      if (consolidated.has(key)) {
        const existing = consolidated.get(key)!
        existing.affectedFiles.push(...issue.affectedFiles)
      } else {
        consolidated.set(key, { ...issue })
      }
    })
    
    return Array.from(consolidated.values())
  }

  /**
   * 构建字符映射
   */
  private buildCharacterMap(
    files: UploadedFile[],
    recognitionResults: Record<string, CharacterRecognitionResult[]>,
    targetCharacters: string[]
  ): Record<string, { files: string[]; quality: number; recommendations: string[] }> {
    const characterMap: Record<string, { files: string[]; quality: number; recommendations: string[] }> = {}
    
    // 初始化目标字符
    targetCharacters.forEach(char => {
      characterMap[char] = {
        files: [],
        quality: 0,
        recommendations: []
      }
    })
    
    // 填充识别结果
    Object.entries(recognitionResults).forEach(([filename, results]) => {
      results.forEach(result => {
        if (!characterMap[result.character]) {
          characterMap[result.character] = {
            files: [],
            quality: 0,
            recommendations: []
          }
        }
        
        characterMap[result.character].files.push(filename)
        characterMap[result.character].quality = Math.max(
          characterMap[result.character].quality,
          result.confidence * 100
        )
      })
    })
    
    // 生成建议
    Object.entries(characterMap).forEach(([char, info]) => {
      if (info.files.length === 0) {
        info.recommendations.push('缺少该字符的样本文件')
      } else if (info.files.length === 1) {
        info.recommendations.push('建议添加更多样本以提高训练效果')
      } else if (info.files.length > 10) {
        info.recommendations.push('样本数量充足，可以开始训练')
      }
      
      if (info.quality < 50) {
        info.recommendations.push('样本质量较低，建议更换高质量图片')
      } else if (info.quality > 80) {
        info.recommendations.push('样本质量良好')
      }
    })
    
    return characterMap
  }
}

// 导出单例实例
export const smartSampleAnalysisService = new SmartSampleAnalysisService()