/**
 * 官网Mock数据
 * 提供官网展示所需的模拟数据，用于前端开发和测试
 */

export interface WebsiteProduct {
  id: number
  name: string
  model: string
  description: string
  shortDescription: string
  price: number
  originalPrice?: number
  images: string[]
  videos: string[]
  features: string[]
  specifications: { [key: string]: string }
  isRecommended: boolean
  isNew: boolean
  rating: number
  reviewCount: number
  salesCount: number
  category: string
  tags: string[]
  createdAt: string
}

export interface WebsiteTestimonial {
  id: number
  customerName: string
  customerAvatar: string
  customerTitle: string
  content: string
  rating: number
  productModel: string
  location: string
  date: string
  images?: string[]
  isVerified: boolean
}

export interface CharityLocation {
  id: number
  name: string
  type: 'school' | 'community' | 'welfare' | 'library'
  address: string
  city: string
  province: string
  country: string
  latitude: number
  longitude: number
  studentCount: number
  deviceCount: number
  establishedDate: string
  contactPerson: string
  description: string
  images: string[]
  achievements: string[]
}

export interface StudentWork {
  id: number
  studentName: string
  age: number
  school: string
  workTitle: string
  workDescription: string
  imageUrl: string
  category: 'calligraphy' | 'character_practice' | 'creative_writing'
  difficulty: 'beginner' | 'intermediate' | 'advanced'
  createdAt: string
  likes: number
  isHighlighted: boolean
}

export interface VolunteerActivity {
  id: number
  title: string
  description: string
  location: string
  date: string
  volunteerCount: number
  beneficiaryCount: number
  images: string[]
  organizer: string
  type: 'teaching' | 'training' | 'donation' | 'maintenance'
  status: 'completed' | 'ongoing' | 'planned'
  feedback: string
}

export interface WebsitePlatformLink {
  id: number
  name: string
  type: 'purchase' | 'rental'
  platform: string
  url: string
  region: string
  language: string
  currency: string
  isRecommended: boolean
  priority: number
  logo: string
  description: string
}

export interface WebsiteStats {
  totalSales: number
  totalDonations: number
  beneficiaryCount: number
  schoolCount: number
  volunteerCount: number
  countriesServed: number
  coursesCompleted: number
  satisfactionRate: number
}

export interface MultiLanguageContent {
  key: string
  zh: string
  en: string
  ja: string
  ko: string
  es: string
}

// 生成随机数据的工具函数
const randomBetween = (min: number, max: number): number => {
  return Math.floor(Math.random() * (max - min + 1)) + min
}

const randomFloat = (min: number, max: number, decimals: number = 2): number => {
  return parseFloat((Math.random() * (max - min) + min).toFixed(decimals))
}

const generateRandomDate = (daysAgo: number): string => {
  const date = new Date()
  date.setDate(date.getDate() - randomBetween(0, daysAgo))
  return date.toISOString()
}

// Mock数据生成器
export class WebsiteMockService {
  // 产品数据
  private static productData = [
    {
      name: 'YX机器人专业版',
      model: 'YX-Robot-Pro-2024',
      shortDescription: '专业级汉字学习机器人，适合教育机构和专业用户',
      category: '专业系列',
      basePrice: 12999,
      features: [
        'AI智能识别技术', '15英寸高清触屏', '专业级书写引导',
        '多语言支持', '云端同步学习', '专业教学模式',
        '高精度压感笔', '长续航设计', '防摔保护'
      ]
    },
    {
      name: 'YX机器人标准版',
      model: 'YX-Robot-Standard-2024',
      shortDescription: '标准版汉字学习机器人，适合家庭和个人用户',
      category: '标准系列',
      basePrice: 8999,
      features: [
        'AI智能识别', '12英寸触屏', '标准书写引导',
        '多语言支持', '家庭学习模式', '护眼设计',
        '标准压感笔', '便携设计', '安全保护'
      ]
    },
    {
      name: 'YX机器人轻量版',
      model: 'YX-Robot-Lite-2024',
      shortDescription: '轻量版汉字学习机器人，经济实惠的入门选择',
      category: '轻量系列',
      basePrice: 5999,
      features: [
        '基础AI识别', '10英寸触屏', '基础书写引导',
        '中英文支持', '简易学习模式', '轻便设计',
        '基础触控笔', '经济实用', '简单操作'
      ]
    },
    {
      name: 'YX机器人教育版',
      model: 'YX-Robot-Education-2024',
      shortDescription: '专为教育机构设计的汉字学习机器人',
      category: '教育系列',
      basePrice: 9999,
      features: [
        '教育专用AI', '13英寸触屏', '课堂教学模式',
        '多学生管理', '教师监控面板', '课程定制',
        '教学专用笔', '批量管理', '数据统计'
      ]
    },
    {
      name: 'YX机器人家用版',
      model: 'YX-Robot-Home-2024',
      shortDescription: '专为家庭设计的汉字学习机器人',
      category: '家用系列',
      basePrice: 6999,
      features: [
        '家庭AI助手', '11英寸触屏', '家庭学习模式',
        '亲子互动', '家长监控', '趣味学习',
        '儿童专用笔', '安全设计', '简单易用'
      ]
    }
  ]

  // 客户评价数据
  private static testimonialData = [
    {
      customerName: '张女士',
      customerTitle: '小学教师',
      content: '这台机器人真的很棒！我的学生们都很喜欢，汉字识别准确率很高，孩子们的书写进步很明显。',
      rating: 5,
      location: '北京市',
      productModel: 'YX-Robot-Pro-2024'
    },
    {
      customerName: '李先生',
      customerTitle: '家长',
      content: '买给孩子学习汉字用的，效果超出预期。机器人很有耐心，孩子现在每天都主动练字。',
      rating: 5,
      location: '上海市',
      productModel: 'YX-Robot-Standard-2024'
    },
    {
      customerName: 'Sarah Johnson',
      customerTitle: 'Chinese Teacher',
      content: 'Amazing product! My students love learning Chinese characters with this robot. Very intuitive and effective.',
      rating: 5,
      location: 'New York, USA',
      productModel: 'YX-Robot-Pro-2024'
    }
  ]

  // 生成产品数据
  static generateWebsiteProducts(): WebsiteProduct[] {
    return this.productData.map((product, index) => {
      const hasDiscount = Math.random() > 0.7
      const price = product.basePrice + randomBetween(-500, 1000)
      const originalPrice = hasDiscount ? price + randomBetween(500, 2000) : undefined
      
      return {
        id: index + 1,
        name: product.name,
        model: product.model,
        description: `${product.shortDescription}。采用先进的AI技术，为用户提供个性化的汉字学习体验。产品具有${product.features.slice(0, 3).join('、')}等特色功能，适合各年龄段用户使用。`,
        shortDescription: product.shortDescription,
        price,
        originalPrice,
        images: [
          `https://picsum.photos/600/400?random=${product.model.toLowerCase()}1`,
          `https://picsum.photos/600/400?random=${product.model.toLowerCase()}2`,
          `https://picsum.photos/600/400?random=${product.model.toLowerCase()}3`,
          `https://picsum.photos/600/400?random=${product.model.toLowerCase()}4`
        ],
        videos: [
          `/videos/products/${product.model.toLowerCase()}_demo.mp4`,
          `/videos/products/${product.model.toLowerCase()}_tutorial.mp4`
        ],
        features: product.features,
        specifications: {
          '屏幕尺寸': product.features.find(f => f.includes('英寸'))?.match(/\d+/)?.[0] + '英寸' || '12英寸',
          '识别技术': 'AI智能识别',
          '支持语言': '中文、英文、日文、韩文',
          '电池续航': randomBetween(8, 15) + '小时',
          '重量': randomFloat(1.2, 2.8, 1) + 'kg',
          '保修期': '2年'
        },
        isRecommended: index < 2,
        isNew: index === 0,
        rating: randomFloat(4.2, 5.0, 1),
        reviewCount: randomBetween(50, 500),
        salesCount: randomBetween(100, 2000),
        category: product.category,
        tags: ['AI智能', '汉字学习', '教育科技', '触屏交互'],
        createdAt: generateRandomDate(365)
      }
    })
  }

  // 生成客户评价数据
  static generateWebsiteTestimonials(count: number = 20): WebsiteTestimonial[] {
    const testimonials: WebsiteTestimonial[] = []
    const customerTitles = ['家长', '教师', '学生', '教育工作者', '培训师', '校长']
    const locations = ['北京市', '上海市', '广州市', '深圳市', '杭州市', '成都市', '西安市', '南京市']
    const internationalLocations = ['New York, USA', 'Tokyo, Japan', 'Seoul, Korea', 'London, UK', 'Paris, France']
    
    const reviewContents = [
      '这台机器人真的很棒！孩子们都很喜欢，汉字识别准确率很高，书写进步很明显。',
      '买给孩子学习汉字用的，效果超出预期。机器人很有耐心，孩子现在每天都主动练字。',
      '作为老师，我觉得这个产品对教学帮助很大，学生的学习兴趣明显提高了。',
      '产品质量很好，功能齐全，客服服务也很到位，值得推荐。',
      '孩子用了一个月，汉字书写有了明显进步，而且对学习更有兴趣了。',
      '机器人的语音交互很自然，能够及时纠正错误，是很好的学习伙伴。'
    ]
    
    const internationalReviews = [
      'Amazing product! My students love learning Chinese characters with this robot.',
      'Very intuitive and effective for Chinese learning. Highly recommended!',
      'Great tool for teaching Chinese calligraphy. The AI recognition is impressive.',
      'My daughter enjoys using this robot every day. Her Chinese writing improved a lot.',
      'Excellent educational technology. Perfect for language learning centers.'
    ]

    // 生成中文评价
    for (let i = 1; i <= Math.floor(count * 0.7); i++) {
      testimonials.push({
        id: i,
        customerName: `${['张', '李', '王', '刘', '陈', '杨', '赵', '黄', '周', '吴'][randomBetween(0, 9)]}${['先生', '女士'][randomBetween(0, 1)]}`,
        customerAvatar: `https://ui-avatars.com/api/?name=User${i}&background=random&size=100`,
        customerTitle: customerTitles[randomBetween(0, customerTitles.length - 1)],
        content: reviewContents[randomBetween(0, reviewContents.length - 1)],
        rating: randomBetween(4, 5),
        productModel: this.productData[randomBetween(0, this.productData.length - 1)].model,
        location: locations[randomBetween(0, locations.length - 1)],
        date: generateRandomDate(180),
        images: Math.random() > 0.7 ? [`https://picsum.photos/400/300?random=testimonial${i}`] : undefined,
        isVerified: Math.random() > 0.2
      })
    }

    // 生成国际评价
    const internationalNames = ['John Smith', 'Sarah Johnson', 'Mike Brown', 'Emily Davis', 'David Wilson', 'Lisa Anderson']
    for (let i = Math.floor(count * 0.7) + 1; i <= count; i++) {
      testimonials.push({
        id: i,
        customerName: internationalNames[randomBetween(0, internationalNames.length - 1)],
        customerAvatar: `https://ui-avatars.com/api/?name=User${i}&background=random&size=100`,
        customerTitle: ['Parent', 'Teacher', 'Student', 'Educator'][randomBetween(0, 3)],
        content: internationalReviews[randomBetween(0, internationalReviews.length - 1)],
        rating: randomBetween(4, 5),
        productModel: this.productData[randomBetween(0, this.productData.length - 1)].model,
        location: internationalLocations[randomBetween(0, internationalLocations.length - 1)],
        date: generateRandomDate(180),
        isVerified: Math.random() > 0.3
      })
    }

    return testimonials.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
  }

  // 生成公益教学点数据
  static generateCharityLocations(count: number = 50): CharityLocation[] {
    const locations: CharityLocation[] = []
    const schoolNames = ['希望小学', '阳光小学', '育才小学', '明德小学', '启智小学', '实验小学']
    const communityNames = ['阳光社区', '和谐社区', '幸福社区', '温馨社区', '友爱社区']
    const welfareNames = ['爱心福利院', '阳光福利院', '温馨之家', '关爱中心', '希望之家']
    const libraryNames = ['市图书馆', '区图书馆', '社区图书馆', '儿童图书馆']
    
    const cities = [
      { name: '北京', province: '北京市', lat: 39.9042, lng: 116.4074 },
      { name: '上海', province: '上海市', lat: 31.2304, lng: 121.4737 },
      { name: '广州', province: '广东省', lat: 23.1291, lng: 113.2644 },
      { name: '深圳', province: '广东省', lat: 22.5431, lng: 114.0579 },
      { name: '杭州', province: '浙江省', lat: 30.2741, lng: 120.1551 },
      { name: '成都', province: '四川省', lat: 30.5728, lng: 104.0668 },
      { name: '西安', province: '陕西省', lat: 34.3416, lng: 108.9398 },
      { name: '南京', province: '江苏省', lat: 32.0603, lng: 118.7969 }
    ]

    const types: ('school' | 'community' | 'welfare' | 'library')[] = ['school', 'community', 'welfare', 'library']
    
    for (let i = 1; i <= count; i++) {
      const type = types[randomBetween(0, types.length - 1)]
      const city = cities[randomBetween(0, cities.length - 1)]
      
      let name = ''
      switch (type) {
        case 'school':
          name = schoolNames[randomBetween(0, schoolNames.length - 1)]
          break
        case 'community':
          name = communityNames[randomBetween(0, communityNames.length - 1)]
          break
        case 'welfare':
          name = welfareNames[randomBetween(0, welfareNames.length - 1)]
          break
        case 'library':
          name = libraryNames[randomBetween(0, libraryNames.length - 1)]
          break
      }

      locations.push({
        id: i,
        name,
        type,
        address: `${city.name}市${['朝阳区', '海淀区', '西城区', '东城区', '丰台区'][randomBetween(0, 4)]}第${randomBetween(1, 999)}号`,
        city: city.name,
        province: city.province,
        country: '中国',
        latitude: city.lat + randomFloat(-0.1, 0.1, 4),
        longitude: city.lng + randomFloat(-0.1, 0.1, 4),
        studentCount: randomBetween(50, 500),
        deviceCount: randomBetween(1, 10),
        establishedDate: generateRandomDate(1000),
        contactPerson: `${['张', '李', '王', '刘', '陈'][randomBetween(0, 4)]}老师`,
        description: `这是一个位于${city.name}的${name}，致力于为当地儿童提供优质的汉字学习环境。`,
        images: [
          `https://picsum.photos/400/300?random=location${i}1`,
          `https://picsum.photos/400/300?random=location${i}2`,
          `https://picsum.photos/400/300?random=location${i}3`
        ],
        achievements: [
          '成功培训学生超过100名',
          '汉字书写水平显著提升',
          '获得当地教育部门认可',
          '家长满意度达到95%以上'
        ]
      })
    }

    return locations
  }  
// 生成学员作品数据
  static generateStudentWorks(count: number = 100): StudentWork[] {
    const works: StudentWork[] = []
    const studentNames = ['小明', '小红', '小华', '小丽', '小强', '小美', '小刚', '小芳']
    const schools = ['希望小学', '阳光小学', '育才小学', '明德小学', '启智小学']
    const workTitles = [
      '我的第一幅书法作品', '汉字之美', '春天的诗', '我爱中国',
      '家乡美景', '感恩老师', '快乐学习', '梦想起航',
      '友谊之花', '勤奋学习', '美好明天', '传统文化'
    ]
    
    const categories: ('calligraphy' | 'character_practice' | 'creative_writing')[] = 
      ['calligraphy', 'character_practice', 'creative_writing']
    const difficulties: ('beginner' | 'intermediate' | 'advanced')[] = 
      ['beginner', 'intermediate', 'advanced']

    for (let i = 1; i <= count; i++) {
      const category = categories[randomBetween(0, categories.length - 1)]
      const difficulty = difficulties[randomBetween(0, difficulties.length - 1)]
      
      works.push({
        id: i,
        studentName: studentNames[randomBetween(0, studentNames.length - 1)],
        age: randomBetween(6, 12),
        school: schools[randomBetween(0, schools.length - 1)],
        workTitle: workTitles[randomBetween(0, workTitles.length - 1)],
        workDescription: `这是一幅${category === 'calligraphy' ? '书法' : category === 'character_practice' ? '汉字练习' : '创意写作'}作品，展现了学生的学习成果和创意表达。`,
        imageUrl: `https://picsum.photos/400/300?random=work${i}`,
        category,
        difficulty,
        createdAt: generateRandomDate(365),
        likes: randomBetween(10, 200),
        isHighlighted: Math.random() > 0.8
      })
    }

    return works.sort((a, b) => b.likes - a.likes)
  }

  // 生成志愿者活动数据
  static generateVolunteerActivities(count: number = 30): VolunteerActivity[] {
    const activities: VolunteerActivity[] = []
    const activityTitles = [
      '汉字学习设备捐赠活动', '书法教学志愿服务', '爱心支教活动',
      '教育资源分享会', '儿童节庆祝活动', '教师培训志愿服务',
      '学习成果展示会', '家长座谈会', '文化交流活动',
      '设备维护志愿服务', '心理健康讲座', '营养健康指导'
    ]
    
    const locations = ['北京市朝阳区', '上海市浦东新区', '广州市天河区', '深圳市南山区', '杭州市西湖区']
    const organizers = ['YX机器人公益基金会', '爱心志愿者协会', '教育发展基金会', '青年志愿者联盟']
    const types: ('teaching' | 'training' | 'donation' | 'maintenance')[] = 
      ['teaching', 'training', 'donation', 'maintenance']
    const statuses: ('completed' | 'ongoing' | 'planned')[] = ['completed', 'ongoing', 'planned']

    for (let i = 1; i <= count; i++) {
      const type = types[randomBetween(0, types.length - 1)]
      const status = statuses[randomBetween(0, statuses.length - 1)]
      
      activities.push({
        id: i,
        title: activityTitles[randomBetween(0, activityTitles.length - 1)],
        description: `这是一次意义深远的志愿者活动，旨在为当地儿童提供更好的汉字学习环境和资源。`,
        location: locations[randomBetween(0, locations.length - 1)],
        date: generateRandomDate(180),
        volunteerCount: randomBetween(5, 50),
        beneficiaryCount: randomBetween(20, 200),
        images: [
          `https://picsum.photos/400/300?random=volunteer${i}1`,
          `https://picsum.photos/400/300?random=volunteer${i}2`,
          `https://picsum.photos/400/300?random=volunteer${i}3`
        ],
        organizer: organizers[randomBetween(0, organizers.length - 1)],
        type,
        status,
        feedback: status === 'completed' ? '活动取得圆满成功，受到当地师生和家长的一致好评。' : ''
      })
    }

    return activities.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
  }

  // 生成官网平台链接数据
  static generateWebsitePlatformLinks(): WebsitePlatformLink[] {
    const purchaseLinks = [
      { name: '淘宝官方旗舰店', platform: 'taobao', region: '中国大陆', language: 'zh-CN', currency: 'CNY', url: 'https://yxrobot.taobao.com' },
      { name: '天猫官方旗舰店', platform: 'tmall', region: '中国大陆', language: 'zh-CN', currency: 'CNY', url: 'https://yxrobot.tmall.com' },
      { name: '京东官方旗舰店', platform: 'jd', region: '中国大陆', language: 'zh-CN', currency: 'CNY', url: 'https://yxrobot.jd.com' },
      { name: 'Amazon US Store', platform: 'amazon', region: '美国', language: 'en-US', currency: 'USD', url: 'https://amazon.com/yxrobot' },
      { name: 'Amazon Japan Store', platform: 'amazon', region: '日本', language: 'ja-JP', currency: 'JPY', url: 'https://amazon.co.jp/yxrobot' },
      { name: 'Amazon Germany Store', platform: 'amazon', region: '德国', language: 'de-DE', currency: 'EUR', url: 'https://amazon.de/yxrobot' }
    ]

    const rentalLinks = [
      { name: '租赁宝', platform: 'zulinbao', region: '中国大陆', language: 'zh-CN', currency: 'CNY', url: 'https://zulinbao.com/yxrobot' },
      { name: '设备租', platform: 'shebeizu', region: '中国大陆', language: 'zh-CN', currency: 'CNY', url: 'https://shebeizu.com/yxrobot' },
      { name: 'TechRental US', platform: 'techrental', region: '美国', language: 'en-US', currency: 'USD', url: 'https://techrental.com/yxrobot' },
      { name: 'EduRent Japan', platform: 'edurent', region: '日本', language: 'ja-JP', currency: 'JPY', url: 'https://edurent.jp/yxrobot' }
    ]

    const links: WebsitePlatformLink[] = []
    let id = 1

    // 购买链接
    purchaseLinks.forEach(link => {
      links.push({
        id: id++,
        name: link.name,
        type: 'purchase',
        platform: link.platform,
        url: link.url,
        region: link.region,
        language: link.language,
        currency: link.currency,
        isRecommended: ['taobao', 'amazon'].includes(link.platform),
        priority: ['taobao', 'tmall', 'amazon'].includes(link.platform) ? 1 : 2,
        logo: `https://ui-avatars.com/api/?name=${link.platform}&background=random&size=80`,
        description: `在${link.name}购买YX机器人产品，享受官方保障和优质服务。`
      })
    })

    // 租赁链接
    rentalLinks.forEach(link => {
      links.push({
        id: id++,
        name: link.name,
        type: 'rental',
        platform: link.platform,
        url: link.url,
        region: link.region,
        language: link.language,
        currency: link.currency,
        isRecommended: link.platform === 'zulinbao',
        priority: link.platform === 'zulinbao' ? 1 : 2,
        logo: `https://ui-avatars.com/api/?name=${link.platform}&background=random&size=80`,
        description: `通过${link.name}租赁YX机器人产品，灵活便捷，按需使用。`
      })
    })

    return links.sort((a, b) => a.priority - b.priority)
  } 
 // 生成官网统计数据
  static generateWebsiteStats(): WebsiteStats {
    return {
      totalSales: randomBetween(50000, 100000),
      totalDonations: randomBetween(10000, 30000),
      beneficiaryCount: randomBetween(80000, 150000),
      schoolCount: randomBetween(2000, 5000),
      volunteerCount: randomBetween(5000, 10000),
      countriesServed: randomBetween(15, 25),
      coursesCompleted: randomBetween(500000, 1000000),
      satisfactionRate: randomFloat(95, 99, 1)
    }
  }

  // 生成多语言内容数据
  static generateMultiLanguageContent(): MultiLanguageContent[] {
    return [
      {
        key: 'site_title',
        zh: 'YX机器人 - 智能汉字学习助手',
        en: 'YX Robot - Intelligent Chinese Character Learning Assistant',
        ja: 'YXロボット - インテリジェント漢字学習アシスタント',
        ko: 'YX로봇 - 지능형 한자 학습 도우미',
        es: 'YX Robot - Asistente Inteligente de Aprendizaje de Caracteres Chinos'
      },
      {
        key: 'hero_title',
        zh: '智能汉字学习的未来',
        en: 'The Future of Intelligent Chinese Character Learning',
        ja: 'インテリジェント漢字学習の未来',
        ko: '지능형 한자 학습의 미래',
        es: 'El Futuro del Aprendizaje Inteligente de Caracteres Chinos'
      },
      {
        key: 'hero_subtitle',
        zh: '通过AI技术和机器人交互，让每个人都能轻松掌握汉字书写',
        en: 'Through AI technology and robot interaction, everyone can easily master Chinese character writing',
        ja: 'AI技術とロボットの相互作用により、誰でも簡単に漢字の書き方をマスターできます',
        ko: 'AI 기술과 로봇 상호작용을 통해 누구나 쉽게 한자 쓰기를 마스터할 수 있습니다',
        es: 'A través de la tecnología AI y la interacción con robots, todos pueden dominar fácilmente la escritura de caracteres chinos'
      },
      {
        key: 'feature_ai_title',
        zh: 'AI智能识别',
        en: 'AI Intelligent Recognition',
        ja: 'AI知能認識',
        ko: 'AI 지능 인식',
        es: 'Reconocimiento Inteligente AI'
      },
      {
        key: 'feature_ai_desc',
        zh: '先进的AI算法，精准识别汉字笔画和结构',
        en: 'Advanced AI algorithms for precise recognition of Chinese character strokes and structure',
        ja: '先進的なAIアルゴリズムにより、漢字の筆画と構造を正確に認識',
        ko: '고급 AI 알고리즘으로 한자 획과 구조를 정확하게 인식',
        es: 'Algoritmos AI avanzados para el reconocimiento preciso de trazos y estructura de caracteres chinos'
      },
      {
        key: 'feature_personalized_title',
        zh: '个性化学习',
        en: 'Personalized Learning',
        ja: 'パーソナライズド学習',
        ko: '개인화 학습',
        es: 'Aprendizaje Personalizado'
      },
      {
        key: 'feature_personalized_desc',
        zh: '根据学习者水平，提供定制化的学习方案',
        en: 'Customized learning solutions based on learner levels',
        ja: '学習者のレベルに応じてカスタマイズされた学習ソリューション',
        ko: '학습자 수준에 따른 맞춤형 학습 솔루션',
        es: 'Soluciones de aprendizaje personalizadas basadas en los niveles del estudiante'
      },
      {
        key: 'charity_title',
        zh: '公益理念',
        en: 'Charity Mission',
        ja: '公益理念',
        ko: '공익 이념',
        es: 'Misión Benéfica'
      },
      {
        key: 'charity_subtitle',
        zh: '每售出一台，捐赠一节课',
        en: 'For every unit sold, we donate one lesson',
        ja: '1台売れるごとに1レッスンを寄付',
        ko: '한 대 판매할 때마다 한 수업을 기부',
        es: 'Por cada unidad vendida, donamos una lección'
      },
      {
        key: 'contact_title',
        zh: '联系我们',
        en: 'Contact Us',
        ja: 'お問い合わせ',
        ko: '문의하기',
        es: 'Contáctanos'
      },
      {
        key: 'buy_now',
        zh: '立即购买',
        en: 'Buy Now',
        ja: '今すぐ購入',
        ko: '지금 구매',
        es: 'Comprar Ahora'
      },
      {
        key: 'rent_now',
        zh: '立即租赁',
        en: 'Rent Now',
        ja: '今すぐレンタル',
        ko: '지금 대여',
        es: 'Alquilar Ahora'
      }
    ]
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

// 官网Mock API函数
export const mockWebsiteAPI = {
  // 获取产品列表
  async getProducts() {
    await mockDelay()
    const products = WebsiteMockService.generateWebsiteProducts()
    return createMockResponse(products)
  },

  // 获取产品详情
  async getProductDetail(productId: number) {
    await mockDelay()
    const products = WebsiteMockService.generateWebsiteProducts()
    const product = products.find(p => p.id === productId)
    
    if (!product) {
      return createMockResponse(null, 404, '产品不存在')
    }
    
    return createMockResponse(product)
  },

  // 获取推荐产品
  async getRecommendedProducts() {
    await mockDelay()
    const products = WebsiteMockService.generateWebsiteProducts()
    const recommended = products.filter(p => p.isRecommended)
    return createMockResponse(recommended)
  },

  // 获取客户评价
  async getTestimonials(params: any) {
    await mockDelay()
    let data = WebsiteMockService.generateWebsiteTestimonials(30)
    
    // 应用筛选
    if (params?.productModel) {
      data = data.filter(item => item.productModel === params.productModel)
    }
    
    if (params?.rating) {
      data = data.filter(item => item.rating >= params.rating)
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

  // 获取公益教学点
  async getCharityLocations(params: any) {
    await mockDelay()
    let data = WebsiteMockService.generateCharityLocations(60)
    
    // 应用筛选
    if (params?.type) {
      data = data.filter(item => item.type === params.type)
    }
    
    if (params?.city) {
      data = data.filter(item => item.city.includes(params.city))
    }
    
    if (params?.province) {
      data = data.filter(item => item.province.includes(params.province))
    }
    
    return createMockResponse(data)
  },

  // 获取学员作品
  async getStudentWorks(params: any) {
    await mockDelay()
    let data = WebsiteMockService.generateStudentWorks(120)
    
    // 应用筛选
    if (params?.category) {
      data = data.filter(item => item.category === params.category)
    }
    
    if (params?.difficulty) {
      data = data.filter(item => item.difficulty === params.difficulty)
    }
    
    if (params?.highlighted) {
      data = data.filter(item => item.isHighlighted)
    }
    
    // 分页
    const page = params?.page || 1
    const pageSize = params?.pageSize || 12
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

  // 获取志愿者活动
  async getVolunteerActivities(params: any) {
    await mockDelay()
    let data = WebsiteMockService.generateVolunteerActivities(40)
    
    // 应用筛选
    if (params?.type) {
      data = data.filter(item => item.type === params.type)
    }
    
    if (params?.status) {
      data = data.filter(item => item.status === params.status)
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

  // 获取平台链接
  async getPlatformLinks(params: any) {
    await mockDelay()
    let data = WebsiteMockService.generateWebsitePlatformLinks()
    
    // 应用筛选
    if (params?.type) {
      data = data.filter(item => item.type === params.type)
    }
    
    if (params?.region) {
      data = data.filter(item => item.region === params.region)
    }
    
    if (params?.language) {
      data = data.filter(item => item.language === params.language)
    }
    
    return createMockResponse(data)
  },

  // 获取官网统计数据
  async getWebsiteStats() {
    await mockDelay()
    const stats = WebsiteMockService.generateWebsiteStats()
    return createMockResponse(stats)
  },

  // 获取多语言内容
  async getMultiLanguageContent(language: string = 'zh') {
    await mockDelay()
    const content = WebsiteMockService.generateMultiLanguageContent()
    
    // 根据语言返回对应内容
    const languageContent: { [key: string]: string } = {}
    content.forEach(item => {
      languageContent[item.key] = item[language as keyof MultiLanguageContent] || item.zh
    })
    
    return createMockResponse(languageContent)
  },

  // 智能平台推荐
  async getRecommendedPlatforms(params: any) {
    await mockDelay()
    const { userLocation, userLanguage, type } = params
    let data = WebsiteMockService.generateWebsitePlatformLinks()
    
    // 根据用户位置和语言推荐平台
    if (type) {
      data = data.filter(item => item.type === type)
    }
    
    // 优先推荐用户所在地区的平台
    const localPlatforms = data.filter(item => 
      item.region.includes(userLocation) || item.language === userLanguage
    )
    
    const otherPlatforms = data.filter(item => 
      !item.region.includes(userLocation) && item.language !== userLanguage
    )
    
    const recommended = [...localPlatforms, ...otherPlatforms].slice(0, 6)
    
    return createMockResponse(recommended)
  }
}