/**
 * 课程管理Mock数据
 * 提供课程相关的模拟数据，用于前端开发和测试
 */

export interface Course {
  id: number
  name: string
  description: string
  difficulty: 'beginner' | 'intermediate' | 'advanced'
  category: string
  duration: number // 分钟
  characterCount: number
  status: 'active' | 'inactive' | 'draft'
  createdAt: string
  updatedAt: string
  createdBy: string
  tags: string[]
}

export interface Character {
  id: number
  courseId: number
  character: string
  pinyin: string
  strokeCount: number
  radical: string
  difficulty: number
  order: number
  createdAt: string
}

export interface CharacterTranslation {
  id: number
  characterId: number
  language: string
  meaning: string
  pronunciation: string
  example: string
  exampleTranslation: string
  createdAt: string
  updatedAt: string
}

// 生成随机数据的工具函数
const randomBetween = (min: number, max: number): number => {
  return Math.floor(Math.random() * (max - min + 1)) + min
}

const generateRandomDate = (daysAgo: number): string => {
  const date = new Date()
  date.setDate(date.getDate() - randomBetween(0, daysAgo))
  return date.toISOString()
}

// Mock数据生成器
export class CourseMockService {
  private static courseNames = [
    '基础汉字入门', '常用汉字练习', '汉字笔画学习', '部首认识课程',
    '小学一年级汉字', '小学二年级汉字', '小学三年级汉字', '小学四年级汉字',
    '日常生活汉字', '商务汉字课程', '旅游汉字指南', '餐饮汉字学习',
    '数字汉字练习', '颜色汉字认知', '动物汉字大全', '植物汉字学习',
    '家庭汉字课程', '学校汉字练习', '交通汉字指南', '天气汉字学习',
    '节日汉字文化', '传统汉字艺术', '现代汉字应用', '汉字书法入门',
    '汉字历史文化', '汉字构造原理', '汉字演变过程', '汉字美学欣赏'
  ]

  private static categories = [
    '基础入门', '小学课程', '生活应用', '商务汉字', 
    '文化传承', '专业进阶', '兴趣拓展', '考试辅导'
  ]

  private static tags = [
    '基础', '入门', '练习', '认知', '应用', '文化', 
    '传统', '现代', '实用', '趣味', '专业', '考试'
  ]

  private static creators = [
    '张老师', '李老师', '王老师', '陈老师', '刘老师',
    '杨老师', '赵老师', '黄老师', '周老师', '吴老师'
  ]

  // 常用汉字库
  private static commonCharacters = [
    '一', '二', '三', '四', '五', '六', '七', '八', '九', '十',
    '人', '大', '小', '上', '下', '左', '右', '中', '国', '家',
    '我', '你', '他', '她', '们', '的', '是', '在', '有', '不',
    '了', '和', '与', '或', '但', '而', '也', '都', '很', '最',
    '好', '坏', '新', '旧', '多', '少', '高', '低', '长', '短',
    '红', '黄', '蓝', '绿', '白', '黑', '灰', '紫', '粉', '橙',
    '水', '火', '土', '木', '金', '山', '川', '河', '海', '天',
    '日', '月', '星', '云', '雨', '雪', '风', '雷', '电', '光',
    '爸', '妈', '儿', '女', '子', '孙', '爷', '奶', '叔', '姨',
    '学', '校', '生', '师', '书', '本', '笔', '纸', '桌', '椅',
    '吃', '喝', '睡', '走', '跑', '跳', '看', '听', '说', '读',
    '写', '画', '唱', '跳', '玩', '笑', '哭', '想', '爱', '恨'
  ]

  private static radicals = [
    '人', '口', '手', '心', '水', '火', '土', '木', '金', '日',
    '月', '山', '川', '田', '目', '耳', '鼻', '足', '车', '门'
  ]

  private static languages = [
    { code: 'en', name: '英语' },
    { code: 'ja', name: '日语' },
    { code: 'ko', name: '韩语' },
    { code: 'fr', name: '法语' },
    { code: 'de', name: '德语' },
    { code: 'es', name: '西班牙语' },
    { code: 'ru', name: '俄语' },
    { code: 'ar', name: '阿拉伯语' }
  ]

  // 生成课程数据
  static generateCourses(count: number = 50): Course[] {
    const courses: Course[] = []
    const difficulties: ('beginner' | 'intermediate' | 'advanced')[] = ['beginner', 'intermediate', 'advanced']
    const statuses: ('active' | 'inactive' | 'draft')[] = ['active', 'inactive', 'draft']

    for (let i = 1; i <= count; i++) {
      const characterCount = randomBetween(10, 100)
      const duration = characterCount * randomBetween(2, 5) // 每个字2-5分钟
      const tagCount = randomBetween(2, 5)
      const selectedTags = []
      
      for (let j = 0; j < tagCount; j++) {
        const tag = this.tags[randomBetween(0, this.tags.length - 1)]
        if (!selectedTags.includes(tag)) {
          selectedTags.push(tag)
        }
      }

      courses.push({
        id: i,
        name: this.courseNames[randomBetween(0, this.courseNames.length - 1)],
        description: `这是一个专门设计的汉字学习课程，包含${characterCount}个精选汉字，适合不同水平的学习者。通过系统化的学习方法，帮助学习者掌握汉字的书写、读音和含义。`,
        difficulty: difficulties[randomBetween(0, difficulties.length - 1)],
        category: this.categories[randomBetween(0, this.categories.length - 1)],
        duration,
        characterCount,
        status: statuses[randomBetween(0, statuses.length - 1)],
        createdAt: generateRandomDate(365),
        updatedAt: generateRandomDate(30),
        createdBy: this.creators[randomBetween(0, this.creators.length - 1)],
        tags: selectedTags
      })
    }

    return courses.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }

  // 生成汉字数据
  static generateCharacters(courseId: number, count: number = 20): Character[] {
    const characters: Character[] = []
    const usedCharacters = new Set<string>()

    for (let i = 1; i <= count; i++) {
      let character: string
      do {
        character = this.commonCharacters[randomBetween(0, this.commonCharacters.length - 1)]
      } while (usedCharacters.has(character))
      
      usedCharacters.add(character)

      characters.push({
        id: i,
        courseId,
        character,
        pinyin: this.generatePinyin(character),
        strokeCount: randomBetween(1, 20),
        radical: this.radicals[randomBetween(0, this.radicals.length - 1)],
        difficulty: randomBetween(1, 5),
        order: i,
        createdAt: generateRandomDate(30)
      })
    }

    return characters.sort((a, b) => a.order - b.order)
  }

  // 生成汉字多语言释义
  static generateCharacterTranslations(characterId: number): CharacterTranslation[] {
    const translations: CharacterTranslation[] = []
    const languageCount = randomBetween(3, 6)
    const selectedLanguages = []

    for (let i = 0; i < languageCount; i++) {
      const lang = this.languages[randomBetween(0, this.languages.length - 1)]
      if (!selectedLanguages.find(l => l.code === lang.code)) {
        selectedLanguages.push(lang)
      }
    }

    selectedLanguages.forEach((lang, index) => {
      translations.push({
        id: index + 1,
        characterId,
        language: lang.code,
        meaning: this.generateMeaning(lang.code),
        pronunciation: this.generatePronunciation(lang.code),
        example: this.generateExample(lang.code),
        exampleTranslation: this.generateExampleTranslation(lang.code),
        createdAt: generateRandomDate(30),
        updatedAt: generateRandomDate(7)
      })
    })

    return translations
  }

  // 生成拼音
  private static generatePinyin(character: string): string {
    const pinyinMap: { [key: string]: string } = {
      '一': 'yī', '二': 'èr', '三': 'sān', '四': 'sì', '五': 'wǔ',
      '六': 'liù', '七': 'qī', '八': 'bā', '九': 'jiǔ', '十': 'shí',
      '人': 'rén', '大': 'dà', '小': 'xiǎo', '上': 'shàng', '下': 'xià',
      '我': 'wǒ', '你': 'nǐ', '他': 'tā', '好': 'hǎo', '水': 'shuǐ'
    }
    return pinyinMap[character] || 'unknown'
  }

  // 生成含义
  private static generateMeaning(language: string): string {
    const meanings: { [key: string]: string[] } = {
      'en': ['one', 'two', 'three', 'person', 'big', 'small', 'good', 'water', 'fire', 'mountain'],
      'ja': ['一つ', '二つ', '三つ', '人', '大きい', '小さい', '良い', '水', '火', '山'],
      'ko': ['하나', '둘', '셋', '사람', '큰', '작은', '좋은', '물', '불', '산'],
      'fr': ['un', 'deux', 'trois', 'personne', 'grand', 'petit', 'bon', 'eau', 'feu', 'montagne'],
      'de': ['eins', 'zwei', 'drei', 'Person', 'groß', 'klein', 'gut', 'Wasser', 'Feuer', 'Berg']
    }
    const langMeanings = meanings[language] || meanings['en']
    return langMeanings[randomBetween(0, langMeanings.length - 1)]
  }

  // 生成发音
  private static generatePronunciation(language: string): string {
    const pronunciations: { [key: string]: string[] } = {
      'en': ['/wʌn/', '/tuː/', '/θriː/', '/ˈpɜːrsən/', '/bɪɡ/', '/smɔːl/', '/ɡʊd/', '/ˈwɔːtər/'],
      'ja': ['ひとつ', 'ふたつ', 'みっつ', 'ひと', 'おおきい', 'ちいさい', 'いい', 'みず'],
      'ko': ['하나', '둘', '셋', '사람', '큰', '작은', '좋은', '물'],
      'fr': ['/œ̃/', '/dø/', '/tʁwa/', '/pɛʁsɔn/', '/ɡʁɑ̃/', '/pəti/', '/bɔ̃/', '/o/'],
      'de': ['/aɪns/', '/tsvaɪ/', '/draɪ/', '/pɛʁˈzoːn/', '/ɡroːs/', '/klaɪn/', '/ɡuːt/', '/ˈvasɐ/']
    }
    const langPronunciations = pronunciations[language] || pronunciations['en']
    return langPronunciations[randomBetween(0, langPronunciations.length - 1)]
  }

  // 生成例句
  private static generateExample(language: string): string {
    const examples: { [key: string]: string[] } = {
      'en': ['This is one book.', 'I am a person.', 'The mountain is big.', 'Water is good.'],
      'ja': ['これは一冊の本です。', '私は人です。', '山は大きいです。', '水は良いです。'],
      'ko': ['이것은 한 권의 책입니다.', '나는 사람입니다.', '산이 큽니다.', '물이 좋습니다.'],
      'fr': ['Ceci est un livre.', 'Je suis une personne.', 'La montagne est grande.', "L'eau est bonne."],
      'de': ['Das ist ein Buch.', 'Ich bin eine Person.', 'Der Berg ist groß.', 'Wasser ist gut.']
    }
    const langExamples = examples[language] || examples['en']
    return langExamples[randomBetween(0, langExamples.length - 1)]
  }

  // 生成例句翻译
  private static generateExampleTranslation(language: string): string {
    return '这是一个例句的翻译。'
  }
}

// 模拟API延迟
const mockDelay = (ms: number = 500): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// Mock API响应格式
const createMockResponse = <T>(data: T, code: number = 200, message: string = 'success') => {
  return {
    code,
    message,
    data,
    timestamp: Date.now()
  }
}

// Mock API函数
export const mockCourseAPI = {
  // 获取课程列表
  async getCourses(params: any) {
    await mockDelay()
    let data = CourseMockService.generateCourses(100)
    
    // 应用筛选
    if (params?.status) {
      data = data.filter(item => item.status === params.status)
    }
    
    if (params?.difficulty) {
      data = data.filter(item => item.difficulty === params.difficulty)
    }
    
    if (params?.category) {
      data = data.filter(item => item.category === params.category)
    }
    
    if (params?.keyword) {
      data = data.filter(item => 
        item.name.includes(params.keyword) ||
        item.description.includes(params.keyword) ||
        item.tags.some(tag => tag.includes(params.keyword))
      )
    }
    
    // 分页
    const page = params?.page || 1
    const pageSize = params?.pageSize || 10
    const total = data.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = data.slice(start, end)
    
    return createMockResponse({
      list,
      total,
      page,
      pageSize,
      totalPages: Math.ceil(total / pageSize)
    })
  },

  // 获取课程详情
  async getCourseDetail(courseId: number) {
    await mockDelay()
    const courses = CourseMockService.generateCourses(100)
    const course = courses.find(c => c.id === courseId)
    
    if (!course) {
      return createMockResponse(null, 404, '课程不存在')
    }
    
    return createMockResponse(course)
  },

  // 获取课程汉字列表
  async getCourseCharacters(courseId: number) {
    await mockDelay()
    const characters = CourseMockService.generateCharacters(courseId, randomBetween(15, 50))
    return createMockResponse(characters)
  },

  // 获取汉字多语言释义
  async getCharacterTranslations(characterId: number) {
    await mockDelay()
    const translations = CourseMockService.generateCharacterTranslations(characterId)
    return createMockResponse(translations)
  },

  // 创建课程
  async createCourse(courseData: any) {
    await mockDelay()
    const newCourse = {
      id: Date.now(),
      ...courseData,
      characterCount: 0,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    return createMockResponse(newCourse)
  },

  // 更新课程
  async updateCourse(courseId: number, courseData: any) {
    await mockDelay()
    const updatedCourse = {
      id: courseId,
      ...courseData,
      updatedAt: new Date().toISOString()
    }
    return createMockResponse(updatedCourse)
  },

  // 删除课程
  async deleteCourse(courseId: number) {
    await mockDelay()
    return createMockResponse(null)
  },

  // 添加汉字到课程
  async addCharacterToCourse(courseId: number, characterData: any) {
    await mockDelay()
    const newCharacter = {
      id: Date.now(),
      courseId,
      ...characterData,
      createdAt: new Date().toISOString()
    }
    return createMockResponse(newCharacter)
  },

  // 更新汉字
  async updateCharacter(characterId: number, characterData: any) {
    await mockDelay()
    const updatedCharacter = {
      id: characterId,
      ...characterData
    }
    return createMockResponse(updatedCharacter)
  },

  // 删除汉字
  async deleteCharacter(characterId: number) {
    await mockDelay()
    return createMockResponse(null)
  },

  // 添加汉字翻译
  async addCharacterTranslation(characterId: number, translationData: any) {
    await mockDelay()
    const newTranslation = {
      id: Date.now(),
      characterId,
      ...translationData,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    return createMockResponse(newTranslation)
  },

  // 更新汉字翻译
  async updateCharacterTranslation(translationId: number, translationData: any) {
    await mockDelay()
    const updatedTranslation = {
      id: translationId,
      ...translationData,
      updatedAt: new Date().toISOString()
    }
    return createMockResponse(updatedTranslation)
  },

  // 删除汉字翻译
  async deleteCharacterTranslation(translationId: number) {
    await mockDelay()
    return createMockResponse(null)
  }
}