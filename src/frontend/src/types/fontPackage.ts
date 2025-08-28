// 字体包相关类型定义

export interface FontPackage {
  id: number
  name: string
  description: string
  version: string
  fontType: string
  difficulty: number
  tags: string[]
  status: 'draft' | 'training' | 'completed' | 'failed' | 'published'
  createdAt: string
  updatedAt: string
  createdBy: string
  targetCharacters: string[]
  trainingProgress?: TrainingProgress
  qualityReport?: QualityReport
}

export interface CreateFontPackageData {
  name: string
  description: string
  version: string
  fontType: string
  difficulty: number
  targetCharacters: string[]
  tags: string[]
}

// 向导相关类型
export interface WizardData {
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
    uploadedFiles: UploadedFile[]
    analysisResult: SampleAnalysisResult | null
  }
  configuration: {
    trainingSettings: TrainingSettings
    qualitySettings: QualitySettings
    advancedSettings: AdvancedSettings
  }
  review: {
    confirmed: boolean
  }
}

export interface TrainingSettings {
  mode: 'fast' | 'standard' | 'high_quality' | 'custom'
  epochs: number
  learningRate: string
  batchSize: number
  augmentation: string[]
}

export interface QualitySettings {
  resolution: string
  threshold: number
  styleConsistency: number
  creativity: number
  postProcessing: string[]
}

export interface AdvancedSettings {
  useGPU: boolean
  parallelTraining: boolean
  customParams: string
}

export interface UploadedFile {
  uid: string
  name: string
  url: string
  size: number
  type: string
  status: 'ready' | 'uploading' | 'success' | 'error'
}

export interface SampleAnalysisResult {
  recognizedCharacters: number
  duplicates: DuplicateInfo[]
  qualityScore: number
  qualityIssues: QualityIssue[]
  analysisTimestamp: string
}

export interface DuplicateInfo {
  file: string
  original: string
  similarity: number
}

export interface QualityIssue {
  type: 'format' | 'size' | 'clarity' | 'character'
  message: string
  severity: 'low' | 'medium' | 'high'
  affectedFiles: string[]
}

export interface TrainingProgress {
  percentage: number
  currentPhase: TrainingPhase
  estimatedTimeRemaining: number
  charactersCompleted: number
  charactersTotal: number
  currentCharacter: string
  startTime: string
  logs: TrainingLog[]
}

export interface TrainingPhase {
  name: string
  description: string
  status: 'pending' | 'running' | 'completed' | 'failed'
  progress: number
}

export interface TrainingLog {
  timestamp: string
  level: 'info' | 'warning' | 'error'
  message: string
  details?: any
}

export interface QualityReport {
  overallScore: number
  characterScores: CharacterScore[]
  metrics: QualityMetrics
  recommendations: string[]
  generatedAt: string
}

export interface CharacterScore {
  character: string
  score: number
  issues: string[]
  sampleUrl: string
}

export interface QualityMetrics {
  clarity: number
  consistency: number
  authenticity: number
  completeness: number
}

// 向导步骤验证
export interface StepValidation {
  isValid: boolean
  errors: string[]
  warnings: string[]
}

// 向导步骤配置
export interface WizardStep {
  key: string
  title: string
  description: string
  component: any
  validation?: () => boolean
}

// 智能推荐
export interface Recommendation {
  type: string
  title: string
  description: string
  action?: () => void
}

// 字符映射
export interface CharacterMap {
  [character: string]: {
    files: UploadedFile[]
    quality: QualityScore
    recommendations: string[]
  }
}

export interface QualityScore {
  overall: number
  clarity: number
  consistency: number
  completeness: number
}

// 进度监控
export interface ProgressData {
  percentage: number
  currentPhase: TrainingPhase
  estimatedTimeRemaining: number
  charactersCompleted: number
  charactersTotal: number
  currentCharacter: string
}

export interface CharacterProgressMap {
  [character: string]: {
    status: 'pending' | 'training' | 'completed' | 'failed'
    progress: number
    quality: number
    issues: string[]
  }
}

export interface PerformanceMetrics {
  trainingSpeed: number
  memoryUsage: number
  gpuUtilization: number
  estimatedCompletion: string
}

export interface AnomalyAlert {
  type: 'performance' | 'quality' | 'error'
  severity: 'low' | 'medium' | 'high'
  message: string
  timestamp: string
  resolved: boolean
}

export interface SystemHealth {
  status: 'healthy' | 'warning' | 'error'
  issues: string[]
  lastCheck: string
}

export interface ProgressSnapshot {
  timestamp: string
  progress: ProgressData
  metrics: PerformanceMetrics
  health: SystemHealth
}