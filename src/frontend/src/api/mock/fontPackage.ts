// 字体包管理 Mock API

import type {
  FontPackage,
  CreateFontPackageData,
  WizardData,
  TrainingProgress,
  QualityReport
} from '@/types/fontPackage'

// 模拟数据
const mockFontPackages: FontPackage[] = [
  {
    id: 1,
    name: '经典楷书字体包',
    description: '传统楷书风格，适合初学者练习',
    version: 'v1.0.0',
    fontType: 'kaishu',
    difficulty: 2,
    tags: ['经典', '教学', '楷书'],
    status: 'completed',
    createdAt: '2024-01-15T10:00:00Z',
    updatedAt: '2024-01-20T15:30:00Z',
    createdBy: 'admin',
    targetCharacters: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十']
  },
  {
    id: 2,
    name: '现代行书字体包',
    description: '现代行书风格，流畅美观',
    version: 'v1.2.0',
    fontType: 'xingshu',
    difficulty: 4,
    tags: ['现代', '美观', '行书'],
    status: 'training',
    createdAt: '2024-02-01T09:00:00Z',
    updatedAt: '2024-02-05T12:00:00Z',
    createdBy: 'admin',
    targetCharacters: ['春', '夏', '秋', '冬', '东', '南', '西', '北']
  },
  {
    id: 3,
    name: '古典隶书字体包',
    description: '汉代隶书风格，庄重典雅',
    version: 'v1.0.0',
    fontType: 'lishu',
    difficulty: 3,
    tags: ['古典', '隶书', '庄重'],
    status: 'draft',
    createdAt: '2024-02-10T14:00:00Z',
    updatedAt: '2024-02-10T14:00:00Z',
    createdBy: 'user1',
    targetCharacters: ['天', '地', '人', '和', '山', '水', '日', '月']
  },
  {
    id: 4,
    name: '创意草书字体包',
    description: '现代创意草书，艺术感强',
    version: 'v2.0.0',
    fontType: 'caoshu',
    difficulty: 5,
    tags: ['创意', '草书', '艺术'],
    status: 'failed',
    createdAt: '2024-01-25T11:00:00Z',
    updatedAt: '2024-01-30T16:00:00Z',
    createdBy: 'user2',
    targetCharacters: ['风', '花', '雪', '月', '诗', '酒', '茶', '琴']
  },
  {
    id: 5,
    name: '标准篆书字体包',
    description: '小篆风格，适合印章制作',
    version: 'v1.1.0',
    fontType: 'zhuanshu',
    difficulty: 4,
    tags: ['篆书', '印章', '标准'],
    status: 'completed',
    createdAt: '2024-01-20T08:00:00Z',
    updatedAt: '2024-01-28T10:00:00Z',
    createdBy: 'admin',
    targetCharacters: ['福', '寿', '康', '宁', '吉', '祥', '如', '意']
  }
]

// 模拟延迟
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

export const mockFontPackageAPI = {
  // 获取字体包列表
  async getFontPackages(params?: {
    page?: number
    pageSize?: number
    status?: string
    fontType?: string
    keyword?: string
    difficulty?: string
    creator?: string
    qualityRange?: number[]
    sampleRange?: number[]
    dateRange?: [string, string] | null
    sortBy?: string
    sortOrder?: string
  }): Promise<{
    data: {
      list: FontPackage[]
      total: number
      page: number
      pageSize: number
    }
  }> {
    await delay(500)

    let filteredPackages = [...mockFontPackages]

    // 关键词搜索
    if (params?.keyword) {
      const keyword = params.keyword.toLowerCase()
      filteredPackages = filteredPackages.filter(pkg =>
        pkg.name.toLowerCase().includes(keyword) ||
        pkg.description.toLowerCase().includes(keyword) ||
        pkg.tags.some(tag => tag.toLowerCase().includes(keyword))
      )
    }

    // 状态过滤
    if (params?.status) {
      filteredPackages = filteredPackages.filter(pkg => pkg.status === params.status)
    }

    // 字体类型过滤
    if (params?.fontType) {
      filteredPackages = filteredPackages.filter(pkg => pkg.fontType === params.fontType)
    }

    // 难度过滤
    if (params?.difficulty) {
      filteredPackages = filteredPackages.filter(pkg => pkg.difficulty.toString() === params.difficulty)
    }

    // 创建者过滤
    if (params?.creator) {
      filteredPackages = filteredPackages.filter(pkg => pkg.createdBy === params.creator)
    }

    // 排序
    if (params?.sortBy) {
      filteredPackages.sort((a, b) => {
        const aValue = a[params.sortBy as keyof FontPackage]
        const bValue = b[params.sortBy as keyof FontPackage]

        // 处理 undefined 值
        if (aValue === undefined && bValue === undefined) return 0
        if (aValue === undefined) return 1
        if (bValue === undefined) return -1

        if (params.sortOrder === 'desc') {
          return aValue > bValue ? -1 : 1
        } else {
          return aValue > bValue ? 1 : -1
        }
      })
    }

    const page = params?.page || 1
    const pageSize = params?.pageSize || 10
    const start = (page - 1) * pageSize
    const end = start + pageSize

    return {
      data: {
        list: filteredPackages.slice(start, end),
        total: filteredPackages.length,
        page,
        pageSize
      }
    }
  },

  // 获取字体包详情
  async getFontPackageById(id: number): Promise<FontPackage> {
    await delay(300)

    const fontPackage = mockFontPackages.find(pkg => pkg.id === id)
    if (!fontPackage) {
      throw new Error('字体包不存在')
    }

    return fontPackage
  },

  // 创建字体包（传统方式）
  async createFontPackage(data: CreateFontPackageData): Promise<FontPackage> {
    await delay(1000)

    const newPackage: FontPackage = {
      id: Date.now(),
      ...data,
      status: 'draft',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      createdBy: 'current-user'
    }

    mockFontPackages.push(newPackage)
    return newPackage
  },

  // 通过向导创建字体包
  async createFontPackageFromWizard(wizardData: WizardData): Promise<FontPackage> {
    await delay(1500)

    const newPackage: FontPackage = {
      id: Date.now(),
      name: wizardData.basicInfo.name,
      description: wizardData.basicInfo.description,
      version: wizardData.basicInfo.version,
      fontType: wizardData.basicInfo.fontType,
      difficulty: wizardData.basicInfo.difficulty,
      tags: wizardData.basicInfo.tags,
      targetCharacters: wizardData.sampleUpload.targetCharacters,
      status: 'training',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      createdBy: 'current-user',
      trainingProgress: {
        percentage: 0,
        currentPhase: {
          name: '初始化',
          description: '准备训练环境',
          status: 'running',
          progress: 0
        },
        estimatedTimeRemaining: 3600,
        charactersCompleted: 0,
        charactersTotal: wizardData.sampleUpload.targetCharacters.length,
        currentCharacter: wizardData.sampleUpload.targetCharacters[0] || '',
        startTime: new Date().toISOString(),
        logs: [
          {
            timestamp: new Date().toISOString(),
            level: 'info',
            message: '开始训练字体包',
            details: { packageName: wizardData.basicInfo.name }
          }
        ]
      }
    }

    mockFontPackages.push(newPackage)
    return newPackage
  },

  // 保存向导草稿
  async saveWizardDraft(wizardData: WizardData): Promise<{ success: boolean }> {
    await delay(500)

    // 这里可以保存到服务器或本地存储
    console.log('保存向导草稿:', wizardData)

    return { success: true }
  },

  // 获取训练进度
  async getTrainingProgress(packageId: number): Promise<TrainingProgress> {
    await delay(200)

    const fontPackage = mockFontPackages.find(pkg => pkg.id === packageId)
    if (!fontPackage || !fontPackage.trainingProgress) {
      throw new Error('训练进度不存在')
    }

    // 模拟进度更新
    const progress = fontPackage.trainingProgress
    if (progress.percentage < 100) {
      progress.percentage = Math.min(progress.percentage + Math.random() * 5, 100)
      progress.charactersCompleted = Math.floor(
        (progress.percentage / 100) * progress.charactersTotal
      )
      progress.estimatedTimeRemaining = Math.max(
        progress.estimatedTimeRemaining - 30,
        0
      )

      // 更新当前字符
      if (progress.charactersCompleted < fontPackage.targetCharacters.length) {
        progress.currentCharacter = fontPackage.targetCharacters[progress.charactersCompleted]
      }

      // 更新阶段
      if (progress.percentage < 20) {
        progress.currentPhase = {
          name: '数据预处理',
          description: '处理样本数据',
          status: 'running',
          progress: progress.percentage * 5
        }
      } else if (progress.percentage < 80) {
        progress.currentPhase = {
          name: '模型训练',
          description: '训练AI模型',
          status: 'running',
          progress: (progress.percentage - 20) * 1.67
        }
      } else {
        progress.currentPhase = {
          name: '质量评估',
          description: '评估生成质量',
          status: 'running',
          progress: (progress.percentage - 80) * 5
        }
      }

      // 添加日志
      if (Math.random() < 0.3) {
        progress.logs.push({
          timestamp: new Date().toISOString(),
          level: 'info',
          message: `正在训练字符: ${progress.currentCharacter}`,
          details: {
            progress: progress.percentage,
            phase: progress.currentPhase.name
          }
        })
      }
    }

    return progress
  },

  // 获取质量报告
  async getQualityReport(packageId: number): Promise<QualityReport> {
    await delay(800)

    const fontPackage = mockFontPackages.find(pkg => pkg.id === packageId)
    if (!fontPackage) {
      throw new Error('字体包不存在')
    }

    // 模拟质量报告
    const report: QualityReport = {
      overallScore: Math.floor(Math.random() * 20) + 75,
      characterScores: fontPackage.targetCharacters.map(char => ({
        character: char,
        score: Math.floor(Math.random() * 30) + 70,
        issues: Math.random() > 0.7 ? ['清晰度需要改进'] : [],
        sampleUrl: `/api/samples/${packageId}/${char}.png`
      })),
      metrics: {
        clarity: Math.floor(Math.random() * 20) + 75,
        consistency: Math.floor(Math.random() * 20) + 80,
        authenticity: Math.floor(Math.random() * 20) + 70,
        completeness: Math.floor(Math.random() * 15) + 85
      },
      recommendations: [
        '建议增加更多样本以提高一致性',
        '部分字符的笔画可以更加清晰',
        '整体质量良好，可以考虑发布'
      ],
      generatedAt: new Date().toISOString()
    }

    return report
  },

  // 更新字体包状态
  async updateFontPackageStatus(
    packageId: number,
    status: FontPackage['status']
  ): Promise<FontPackage> {
    await delay(500)

    const fontPackage = mockFontPackages.find(pkg => pkg.id === packageId)
    if (!fontPackage) {
      throw new Error('字体包不存在')
    }

    fontPackage.status = status
    fontPackage.updatedAt = new Date().toISOString()

    return fontPackage
  },

  // 删除字体包
  async deleteFontPackage(packageId: number): Promise<{ success: boolean }> {
    await delay(500)

    const index = mockFontPackages.findIndex(pkg => pkg.id === packageId)
    if (index === -1) {
      throw new Error('字体包不存在')
    }

    mockFontPackages.splice(index, 1)
    return { success: true }
  },

  // 上传样本文件
  async uploadSampleFile(file: File, packageId?: number): Promise<{
    url: string
    filename: string
    size: number
  }> {
    await delay(1000 + Math.random() * 2000) // 模拟上传时间

    // 模拟上传结果
    return {
      url: `/api/uploads/samples/${Date.now()}_${file.name}`,
      filename: file.name,
      size: file.size
    }
  },

  // 分析样本文件
  async analyzeSampleFiles(files: string[]): Promise<{
    recognizedCharacters: number
    duplicates: Array<{ file: string; original: string }>
    qualityScore: number
    qualityIssues: Array<{
      type: string
      message: string
      severity: string
      affectedFiles: string[]
    }>
  }> {
    await delay(2000)

    // 模拟分析结果
    return {
      recognizedCharacters: Math.floor(files.length * 0.8),
      duplicates: files.slice(0, Math.floor(files.length * 0.1)).map((file, index) => ({
        file,
        original: files[index + 1] || files[0]
      })),
      qualityScore: Math.floor(Math.random() * 20) + 75,
      qualityIssues: [
        {
          type: 'clarity',
          message: '部分图片清晰度不足',
          severity: 'medium',
          affectedFiles: files.slice(0, 2)
        },
        {
          type: 'size',
          message: '图片尺寸不统一',
          severity: 'low',
          affectedFiles: files.slice(2, 4)
        }
      ].slice(0, Math.floor(Math.random() * 3))
    }
  },

  // 获取字体包统计数据
  async getFontPackageStats(): Promise<{
    data: {
      totalPackages: number
      publishedPackages: number
      generatingPackages: number
      failedPackages: number
      totalSamples: number
      totalCharacters: number
      averageAccuracy: number
      averageGenerationTime: number
      statusDistribution: {
        draft: number
        training: number
        completed: number
        published: number
        failed: number
      }
      typeDistribution: {
        kaishu: number
        xingshu: number
        lishu: number
        zhuanshu: number
        caoshu: number
        other: number
      }
      recentActivity: {
        newPackagesThisWeek: number
        completedThisWeek: number
        publishedThisWeek: number
        totalDownloadsThisWeek: number
      }
    }
  }> {
    await delay(300)

    const totalPackages = mockFontPackages.length
    const completedPackages = mockFontPackages.filter(pkg => pkg.status === 'completed').length
    const trainingPackages = mockFontPackages.filter(pkg => pkg.status === 'training').length
    const failedPackages = mockFontPackages.filter(pkg => pkg.status === 'failed').length
    const draftPackages = mockFontPackages.filter(pkg => pkg.status === 'draft').length

    const stats = {
      totalPackages,
      publishedPackages: completedPackages,
      generatingPackages: trainingPackages,
      failedPackages,
      totalSamples: totalPackages * 150, // 模拟样本数量
      totalCharacters: mockFontPackages.reduce((sum, pkg) => sum + (pkg.targetCharacters?.length || 0), 0),
      averageAccuracy: 87.5,
      averageGenerationTime: 45,
      statusDistribution: {
        draft: draftPackages,
        training: trainingPackages,
        completed: completedPackages,
        published: completedPackages,
        failed: failedPackages
      },
      typeDistribution: {
        kaishu: mockFontPackages.filter(pkg => pkg.fontType === 'kaishu').length,
        xingshu: mockFontPackages.filter(pkg => pkg.fontType === 'xingshu').length,
        lishu: mockFontPackages.filter(pkg => pkg.fontType === 'lishu').length,
        zhuanshu: mockFontPackages.filter(pkg => pkg.fontType === 'zhuanshu').length,
        caoshu: mockFontPackages.filter(pkg => pkg.fontType === 'caoshu').length,
        other: mockFontPackages.filter(pkg => !['kaishu', 'xingshu', 'lishu', 'zhuanshu', 'caoshu'].includes(pkg.fontType)).length
      },
      recentActivity: {
        newPackagesThisWeek: Math.floor(totalPackages * 0.1),
        completedThisWeek: Math.floor(completedPackages * 0.2),
        publishedThisWeek: Math.floor(completedPackages * 0.15),
        totalDownloadsThisWeek: Math.floor(Math.random() * 1000) + 500
      }
    }

    return { data: stats }
  },

  // 开始生成字体包
  async startGeneration(packageId: number): Promise<{
    data: {
      task: {
        id: string
        estimatedTime: number
        totalCharacters: number
      }
    }
  }> {
    await delay(1000)

    const fontPackage = mockFontPackages.find(pkg => pkg.id === packageId)
    if (!fontPackage) {
      throw new Error('字体包不存在')
    }

    // 更新状态为训练中
    fontPackage.status = 'training'
    fontPackage.updatedAt = new Date().toISOString()

    return {
      data: {
        task: {
          id: `task_${Date.now()}`,
          estimatedTime: Math.ceil((fontPackage.targetCharacters?.length || 10) * 2),
          totalCharacters: fontPackage.targetCharacters?.length || 0
        }
      }
    }
  },

  // 发布字体包
  async publishFontPackage(packageId: number, publishOptions?: any): Promise<{
    data: {
      success: boolean
      publishUrl: string
    }
  }> {
    await delay(800)

    const fontPackage = mockFontPackages.find(pkg => pkg.id === packageId)
    if (!fontPackage) {
      throw new Error('字体包不存在')
    }

    if (fontPackage.status !== 'completed') {
      throw new Error('只有已完成的字体包才能发布')
    }

    // 更新状态为已发布
    fontPackage.status = 'published'
    fontPackage.updatedAt = new Date().toISOString()

    return {
      data: {
        success: true,
        publishUrl: `/fonts/${fontPackage.id}/download`
      }
    }
  },

  // 获取生成进度
  async getGenerationProgress(packageId: number): Promise<{
    data: {
      progress: {
        percentage: number
        currentPhase: string
        estimatedTimeRemaining: number
        status: string
      }
    }
  }> {
    await delay(300)

    const fontPackage = mockFontPackages.find(pkg => pkg.id === packageId)
    if (!fontPackage) {
      throw new Error('字体包不存在')
    }

    // 模拟进度更新
    const currentProgress = Math.min((fontPackage as any).progress || 0 + Math.random() * 5, 100)

    return {
      data: {
        progress: {
          percentage: currentProgress,
          currentPhase: currentProgress < 50 ? '数据预处理' : '模型训练',
          estimatedTimeRemaining: Math.max(60 - Math.floor(currentProgress * 0.6), 0),
          status: currentProgress >= 100 ? 'completed' : 'training'
        }
      }
    }
  }
}