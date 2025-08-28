/**
 * Mock数据工具
 * 提供测试数据和占位图片
 */

// 生成占位图片URL
export const generatePlaceholderImage = (width: number, height: number, text?: string): string => {
  const baseUrl = 'https://via.placeholder.com'
  const textParam = text ? `?text=${encodeURIComponent(text)}` : ''
  return `${baseUrl}/${width}x${height}/6366f1/ffffff${textParam}`
}

// 生成随机头像
export const generateAvatar = (name: string): string => {
  const colors = ['6366f1', '8b5cf6', 'ec4899', 'ef4444', 'f59e0b', '10b981', '06b6d4', '3b82f6']
  const color = colors[Math.abs(name.split('').reduce((a, b) => a + b.charCodeAt(0), 0)) % colors.length]
  const initial = name.charAt(0).toUpperCase()
  return `https://via.placeholder.com/60x60/${color}/ffffff?text=${initial}`
}

// 产品图片数据
export const productImages = {
  home: 'https://picsum.photos/600/400?random=robot1',
  education: 'https://picsum.photos/600/400?random=robot1',
  professional: 'https://picsum.photos/500/350?random=robot2',
  charity: 'https://picsum.photos/400/300?random=teaching1',
  hero: 'https://picsum.photos/600/400?random=robot1',
  technology: 'https://picsum.photos/500/350?random=robot3'
}

// 产品详细图片集合
export const productDetailImages = {
  // 主要产品图片
  main: [
    'https://picsum.photos/600/400?random=robot1', // 产品正面图
    'https://picsum.photos/500/350?random=robot2', // 产品侧面图
    'https://picsum.photos/500/350?random=robot3', // 产品背面图
    'https://picsum.photos/600/400?random=robot4'  // 产品俯视图
  ],

  // 细节特写图片
  details: [
    'https://picsum.photos/500/350?random=detail1', // 电子墨水屏细节
    'https://picsum.photos/500/350?random=detail2', // 机械臂细节
    'https://picsum.photos/600/400?random=detail3', // 控制面板细节
    'https://picsum.photos/500/350?random=detail4', // 笔架细节
    'https://picsum.photos/500/350?random=detail5', // 底座细节
    'https://picsum.photos/600/400?random=detail6'  // 接口细节
  ],

  // 使用场景图片
  usage: [
    'https://picsum.photos/400/300?random=usage1', // 小学生使用场景
    'https://picsum.photos/400/300?random=usage2', // 中学生使用场景
    'https://picsum.photos/400/300?random=usage3', // 老师指导场景
    'https://picsum.photos/400/300?random=usage4', // 课堂教学场景
    'https://picsum.photos/400/300?random=usage5', // 家庭使用场景
    'https://picsum.photos/400/300?random=usage6'  // 培训机构场景
  ],

  // 用户口碑图片
  testimonial: [
    'https://picsum.photos/400/300?random=testimonial1', // 学生作品展示1
    'https://picsum.photos/400/300?random=testimonial2', // 学生作品展示2
    'https://picsum.photos/400/300?random=testimonial3', // 学生作品展示3
    'https://picsum.photos/400/300?random=testimonial4', // 学生作品展示4
    'https://picsum.photos/400/300?random=testimonial5', // 获奖证书
    'https://picsum.photos/400/300?random=testimonial6'  // 教学成果
  ]
}

// 用户头像数据
export const userAvatars = {
  teacher: 'https://ui-avatars.com/api/?name=Teacher&background=4caf50&color=ffffff&size=100',
  enthusiast: 'https://ui-avatars.com/api/?name=Fan&background=2196f3&color=ffffff&size=100',
  elderly: 'https://ui-avatars.com/api/?name=Elder&background=ff9800&color=ffffff&size=100',
  student: 'https://ui-avatars.com/api/?name=Student&background=e91e63&color=ffffff&size=100',
  parent: 'https://ui-avatars.com/api/?name=Parent&background=9c27b0&color=ffffff&size=100'
}

// 公益教学点图片
export const charityImages = [
  'https://picsum.photos/400/300?random=charity1', // 小学教学点
  'https://picsum.photos/400/300?random=charity2', // 社区中心
  'https://picsum.photos/400/300?random=charity3', // 福利院
  'https://picsum.photos/400/300?random=charity4', // 老年大学
  'https://picsum.photos/400/300?random=charity5'  // 党群服务中心
]

// 学员作品图片
export const studentWorks = [
  'https://picsum.photos/400/300?random=work1', // 楷书作品
  'https://picsum.photos/400/300?random=work2', // 行书作品
  'https://picsum.photos/400/300?random=work3', // 隶书作品
  'https://picsum.photos/400/300?random=work4', // 篆书作品
  'https://picsum.photos/400/300?random=work5'  // 草书作品
]

// 志愿者活动图片
export const volunteerActivities = [
  'https://picsum.photos/400/300?random=volunteer1', // 志愿者教学
  'https://picsum.photos/400/300?random=volunteer2', // 书法比赛
  'https://picsum.photos/400/300?random=volunteer3', // 作品展示
  'https://picsum.photos/400/300?random=volunteer4', // 颁奖典礼
  'https://picsum.photos/400/300?random=volunteer5', // 培训活动
  'https://picsum.photos/400/300?random=volunteer6'  // 社区服务
]

// 志愿者活动详细数据
export const volunteerActivityDetails = [
  {
    id: 1,
    title: '志愿者教学活动',
    description: '资深书法志愿者深入偏远地区学校，为孩子们提供专业的书法指导，传承中华传统文化',
    date: '2024-03-15',
    location: '北京市第一小学',
    duration: '3小时',
    participants: 25,
    volunteers: 8,
    type: 'teaching',
    status: 'completed',
    imageUrl: volunteerActivities[0],
    highlights: ['一对一指导', '基础笔画教学', '作品点评'],
    impact: '帮助25名学生掌握基本笔画，提升书写兴趣',
    feedback: '孩子们学习热情很高，志愿者老师非常耐心专业',
    organizer: '北京书法志愿者协会',
    nextActivity: '2024-04-15'
  },
  {
    id: 2,
    title: '书法比赛颁奖典礼',
    description: '为优秀学员颁发奖状和奖品，鼓励他们继续学习书法，弘扬传统文化精神',
    date: '2024-02-28',
    location: '上海浦东社区中心',
    duration: '2小时',
    participants: 50,
    volunteers: 12,
    type: 'ceremony',
    status: 'completed',
    imageUrl: volunteerActivities[1],
    highlights: ['颁奖典礼', '作品展示', '经验分享'],
    impact: '表彰了30名优秀学员，激发更多人学习书法',
    feedback: '典礼隆重温馨，学员们备受鼓舞',
    organizer: '上海市书法教育基金会',
    awards: ['一等奖5名', '二等奖10名', '三等奖15名']
  },
  {
    id: 3,
    title: '学员作品展示会',
    description: '展示学员们的优秀书法作品，让更多人了解公益书法教育的成果和意义',
    date: '2024-01-20',
    location: '广州市文化中心',
    duration: '4小时',
    participants: 80,
    volunteers: 15,
    type: 'exhibition',
    status: 'completed',
    imageUrl: volunteerActivities[2],
    highlights: ['作品展览', '现场演示', '互动体验'],
    impact: '展出作品120幅，吸引观众500余人次',
    feedback: '作品质量很高，展示了公益教育的丰硕成果',
    organizer: '广州市青少年发展基金会',
    exhibitionWorks: 120
  },
  {
    id: 4,
    title: '公益项目庆典',
    description: '庆祝公益书法项目取得的丰硕成果，感谢所有志愿者和支持者的无私奉献',
    date: '2023-12-10',
    location: '深圳市福利院',
    duration: '3小时',
    participants: 100,
    volunteers: 20,
    type: 'celebration',
    status: 'completed',
    imageUrl: volunteerActivities[3],
    highlights: ['成果汇报', '志愿者表彰', '文艺演出'],
    impact: '总结一年成果，表彰优秀志愿者50名',
    feedback: '活动温馨感人，体现了公益事业的温暖力量',
    organizer: '深圳市慈善会',
    achievements: ['服务学员1000+', '培训志愿者200+', '开展活动50+']
  },
  {
    id: 5,
    title: '志愿者培训活动',
    description: '为新加入的志愿者提供专业培训，提升教学技能和服务水平',
    date: '2024-03-01',
    location: '成都市武侯区小学',
    duration: '6小时',
    participants: 30,
    volunteers: 5,
    type: 'training',
    status: 'completed',
    imageUrl: volunteerActivities[4],
    highlights: ['教学技巧培训', '心理辅导知识', '实践演练'],
    impact: '培训新志愿者30名，提升服务质量',
    feedback: '培训内容实用，志愿者收获很大',
    organizer: '成都市教育志愿者联盟',
    trainingModules: ['书法基础', '教学方法', '沟通技巧', '安全知识']
  },
  {
    id: 6,
    title: '社区书法服务',
    description: '走进社区为居民提供书法指导服务，让更多人感受传统文化魅力',
    date: '2024-02-15',
    location: '西安市雁塔区社区',
    duration: '4小时',
    participants: 60,
    volunteers: 10,
    type: 'community',
    status: 'completed',
    imageUrl: volunteerActivities[5],
    highlights: ['社区服务', '老年书法班', '亲子活动'],
    impact: '服务社区居民60人，开展亲子活动10场',
    feedback: '社区居民反响热烈，希望经常举办此类活动',
    organizer: '西安市社区服务中心',
    serviceTypes: ['老年书法指导', '儿童启蒙教学', '亲子互动课程']
  }
]

// Mock API响应数据
export const mockApiData = {
  // 数据看板统计
  dashboardStats: {
    totalRevenue: 1250000,
    totalOrders: 3420,
    newProducts: 5,
    newCustomers: 128,
    revenueGrowth: 12.5,
    ordersGrowth: 8.3,
    customersGrowth: 15.2
  },

  // 公益项目统计
  charityStats: {
    totalDonatedCourses: 52000,
    totalBeneficiaries: 180000,
    totalInstitutions: 1200,
    totalRobotsSold: 52000,
    donationRatio: 1.0,
    schoolsCount: 680,
    communityCentersCount: 220,
    careCentersCount: 150,
    welfareHomesCount: 80,
    elderlyUniversitiesCount: 70
  },

  // 产品列表 - 目前只有教育版练字机器人
  products: [
    {
      id: 1,
      name: '教育版练字机器人',
      model: 'YX-Edu-2024',
      description: '专为学校和教育机构设计的智能练字设备。配备先进的AI识别技术和教师管理系统，支持多人同时使用，可批量管理学生进度，是现代化书法教学的得力助手。',
      price: 8999,
      originalPrice: 9999,
      coverImage: productImages.education,
      status: 'published',
      features: ['AI智能识别', '电子墨水屏护眼', '手把手教学', '课程定制', '数据分析', '教师端管理', '批量评估', '进度跟踪'],
      badge: '新品上市',
      specifications: {
        dimensions: '45×35×20cm',
        weight: '5.2kg',
        display: '电子墨水屏护眼显示',
        connectivity: 'WiFi + 蓝牙连接',
        teaching: '智能机械臂手把手教学',
        recognition: 'AI智能笔画识别',
        courses: '内置5000+汉字课程库',
        management: '教师端管理系统'
      },
      highlights: [
        '教育机构专享优惠价',
        '3年超长质保服务',
        '免费上门安装调试',
        '专业培训支持',
        '7×24小时技术支持',
        '30天无理由退换',
        '全国联保服务'
      ],
      detailedFeatures: [
        {
          title: 'AI智能识别系统',
          description: '采用先进的计算机视觉技术，实时识别学生书写笔画，提供精准的姿势和笔法指导'
        },
        {
          title: '电子墨水屏护眼技术',
          description: '采用电子墨水屏技术，无蓝光伤害，长时间使用不伤眼，为学生视力健康保驾护航'
        },
        {
          title: '模仿老师手把手教学',
          description: '智能机械臂模拟真实老师的手部动作，手把手指导学生正确的握笔姿势和书写笔法'
        },
        {
          title: '个性化课程定制',
          description: '根据不同年级和学习水平，智能推荐适合的练字课程，支持自定义教学内容'
        },
        {
          title: '数据分析报告',
          description: '详细的学习数据分析，生成个人和班级学习报告，帮助教师优化教学方案'
        }
      ]
    }
  ],

  // 用户评价
  testimonials: [
    {
      id: 1,
      name: '张女士',
      title: '小学教师',
      avatar: userAvatars.teacher,
      content: '这台练字机器人真的很棒！我的学生们都很喜欢，字写得越来越好了。AI识别很准确，能及时纠正错误的笔画。',
      rating: 5,
      verified: true
    },
    {
      id: 2,
      name: '李先生',
      title: '书法爱好者',
      avatar: userAvatars.enthusiast,
      content: '作为一个书法爱好者，我对这个产品的专业性很满意。字体库很丰富，指导也很专业，确实帮助我提高了不少。',
      rating: 5,
      verified: true
    },
    {
      id: 3,
      name: '王奶奶',
      title: '退休教师',
      avatar: userAvatars.elderly,
      content: '老了还能学习新技术，这个机器人操作很简单，孙子教了我几次就会用了。现在每天练字成了我的乐趣。',
      rating: 5,
      verified: false
    }
  ],

  // 公益教学点
  charityLocations: [
    {
      id: 1,
      name: '北京市第一小学',
      locationType: 'school',
      address: '北京市朝阳区建国路88号',
      contactPerson: '李老师',
      contactPhone: '010-12345678',
      beneficiaryCount: 320,
      coursesCount: 48,
      imageUrls: [charityImages[0]],
      latitude: 39.9042,
      longitude: 116.4074,
      isActive: true,
      establishedDate: '2023-03-15',
      description: '北京市重点小学，书法教育传统深厚，学生基础扎实',
      achievements: ['市级书法教育示范校', '连续3年书法比赛团体第一名'],
      teacherCount: 8,
      classCount: 12,
      weeklyHours: 6,
      specialPrograms: ['书法兴趣班', '传统文化课程', '家长书法讲座']
    },
    {
      id: 2,
      name: '上海浦东社区中心',
      locationType: 'community_center',
      address: '上海市浦东新区世纪大道1000号',
      contactPerson: '王主任',
      contactPhone: '021-87654321',
      beneficiaryCount: 150,
      coursesCount: 24,
      imageUrls: [charityImages[1]],
      latitude: 31.2304,
      longitude: 121.4737,
      isActive: true,
      establishedDate: '2023-05-20',
      description: '服务社区居民的综合性文化中心，书法课程深受欢迎',
      achievements: ['区级文明社区', '优秀文化活动组织奖'],
      teacherCount: 4,
      classCount: 6,
      weeklyHours: 4,
      specialPrograms: ['老年书法班', '亲子书法课', '书法文化沙龙']
    },
    {
      id: 3,
      name: '广州市天河区中学',
      locationType: 'school',
      address: '广州市天河区珠江新城核心区',
      contactPerson: '陈校长',
      contactPhone: '020-88888888',
      beneficiaryCount: 280,
      coursesCount: 36,
      imageUrls: [charityImages[2]],
      latitude: 23.1291,
      longitude: 113.2644,
      isActive: true,
      establishedDate: '2023-04-10',
      description: '现代化中学，注重传统文化教育与现代科技结合',
      achievements: ['省级书法教育特色学校', '全国青少年书法大赛优秀组织奖'],
      teacherCount: 6,
      classCount: 10,
      weeklyHours: 4,
      specialPrograms: ['书法社团', '文化节书法展', '名家讲座']
    },
    {
      id: 4,
      name: '深圳市福田区实验小学',
      locationType: 'school',
      address: '深圳市福田区中心区福华路',
      contactPerson: '刘主任',
      contactPhone: '0755-12345678',
      beneficiaryCount: 200,
      coursesCount: 30,
      imageUrls: [charityImages[3]],
      latitude: 22.5431,
      longitude: 114.0579,
      isActive: true,
      establishedDate: '2023-06-01',
      description: '实验性小学，积极探索书法教育新模式',
      achievements: ['市级教育创新奖', '书法教学成果展示校'],
      teacherCount: 5,
      classCount: 8,
      weeklyHours: 5,
      specialPrograms: ['数字书法课堂', '书法艺术节', '家校共育书法班']
    },
    {
      id: 5,
      name: '成都市武侯区小学',
      locationType: 'school',
      address: '成都市武侯区人民南路四段',
      contactPerson: '张老师',
      contactPhone: '028-87654321',
      beneficiaryCount: 180,
      coursesCount: 28,
      imageUrls: [charityImages[4]],
      latitude: 30.6598,
      longitude: 104.0633,
      isActive: true,
      establishedDate: '2023-07-15',
      description: '历史悠久的小学，书法教育底蕴深厚',
      achievements: ['省级传统文化教育示范校', '书法教育百年传承奖'],
      teacherCount: 7,
      classCount: 9,
      weeklyHours: 6,
      specialPrograms: ['传统文化节', '书法名师工作室', '学生书法作品展']
    },
    {
      id: 6,
      name: '西安市雁塔区中学',
      locationType: 'school',
      address: '西安市雁塔区长安南路',
      contactPerson: '赵校长',
      contactPhone: '029-88888888',
      beneficiaryCount: 250,
      coursesCount: 32,
      imageUrls: [charityImages[0]],
      latitude: 34.2317,
      longitude: 108.9398,
      isActive: true,
      establishedDate: '2023-08-20',
      description: '古城西安的重点中学，传承千年书法文化',
      achievements: ['国家级书法教育示范校', '丝绸之路书法文化交流奖'],
      teacherCount: 9,
      classCount: 12,
      weeklyHours: 5,
      specialPrograms: ['古典书法研习', '碑帖临摹课', '书法文化研学']
    },
    {
      id: 7,
      name: '杭州市西湖区老年大学',
      locationType: 'elderly_university',
      address: '杭州市西湖区文三路',
      contactPerson: '吴教授',
      contactPhone: '0571-87654321',
      beneficiaryCount: 120,
      coursesCount: 20,
      imageUrls: [charityImages[1]],
      latitude: 30.2741,
      longitude: 120.1551,
      isActive: true,
      establishedDate: '2023-09-10',
      description: '专为老年人设立的书法学习基地，传承文化薪火',
      achievements: ['全国老年教育先进单位', '银龄书法教育典型'],
      teacherCount: 3,
      classCount: 5,
      weeklyHours: 3,
      specialPrograms: ['银龄书法班', '健康养生书法', '祖孙同堂书法课']
    },
    {
      id: 8,
      name: '武汉市江汉区福利院',
      locationType: 'care_center',
      address: '武汉市江汉区解放大道',
      contactPerson: '孙院长',
      contactPhone: '027-88888888',
      beneficiaryCount: 80,
      coursesCount: 16,
      imageUrls: [charityImages[2]],
      latitude: 30.5928,
      longitude: 114.3055,
      isActive: true,
      establishedDate: '2023-10-01',
      description: '关爱特殊群体，用书法温暖心灵，传递社会关怀',
      achievements: ['省级文明福利院', '特殊群体文化教育先进单位'],
      teacherCount: 2,
      classCount: 4,
      weeklyHours: 2,
      specialPrograms: ['康复书法疗法', '心理健康书法课', '节日主题书法活动']
    }
  ],

  // 学员作品
  studentWorks: [
    {
      id: 1,
      charityLocationId: 1,
      studentName: '李小明',
      studentAge: 8,
      school: '北京市第一小学',
      grade: '二年级',
      workTitle: '春晓',
      workDescription: '唐诗《春晓》楷书练习作品，笔画工整，结构匀称，体现了扎实的基本功',
      workImageUrl: studentWorks[0],
      workType: 'calligraphy',
      style: '楷书',
      isFeatured: true,
      awards: ['校级书法比赛一等奖', '区级优秀作品展入选'],
      practiceTime: '3个月',
      teacherComment: '进步显著，基础扎实，笔法日趋成熟',
      improvementScore: 95,
      displayOrder: 1
    },
    {
      id: 2,
      charityLocationId: 2,
      studentName: '王小红',
      studentAge: 10,
      school: '上海浦东新区实验小学',
      grade: '四年级',
      workTitle: '静夜思',
      workDescription: '唐诗《静夜思》行书练习作品，笔法流畅，意境深远，展现了良好的书法天赋',
      workImageUrl: studentWorks[1],
      workType: 'calligraphy',
      style: '行书',
      isFeatured: true,
      awards: ['区级书法展览入选作品'],
      practiceTime: '6个月',
      teacherComment: '天赋很好，用笔有力，行书已初具神韵',
      improvementScore: 92,
      displayOrder: 2
    },
    {
      id: 3,
      charityLocationId: 3,
      studentName: '张小华',
      studentAge: 12,
      school: '广州市天河区中学',
      grade: '初一',
      workTitle: '登鹳雀楼',
      workDescription: '王之涣《登鹳雀楼》楷书作品，气势恢宏，笔力雄健，展现了深厚的文化底蕴',
      workImageUrl: studentWorks[2],
      workType: 'calligraphy',
      style: '楷书',
      isFeatured: true,
      awards: ['市级青少年书法大赛二等奖', '省级书法比赛优秀奖'],
      practiceTime: '1年',
      teacherComment: '功底深厚，有书法家潜质，建议继续深造',
      improvementScore: 98,
      displayOrder: 3
    },
    {
      id: 4,
      charityLocationId: 4,
      studentName: '刘小强',
      studentAge: 9,
      school: '深圳市福田区实验小学',
      grade: '三年级',
      workTitle: '咏鹅',
      workDescription: '骆宾王《咏鹅》楷书作品，童趣盎然，笔画清秀，体现了儿童书法的纯真美',
      workImageUrl: studentWorks[3],
      workType: 'calligraphy',
      style: '楷书',
      isFeatured: false,
      awards: ['班级书法小能手', '年级书法比赛三等奖'],
      practiceTime: '4个月',
      teacherComment: '进步很快，字迹越来越工整，继续努力',
      improvementScore: 88,
      displayOrder: 4
    },
    {
      id: 5,
      charityLocationId: 5,
      studentName: '陈小美',
      studentAge: 11,
      school: '成都市武侯区小学',
      grade: '五年级',
      workTitle: '望庐山瀑布',
      workDescription: '李白《望庐山瀑布》行书作品，笔势如瀑布飞流，动感十足，意境优美',
      workImageUrl: studentWorks[4],
      workType: 'calligraphy',
      style: '行书',
      isFeatured: true,
      awards: ['省级书法比赛优秀奖', '全国青少年书法大赛入围奖'],
      practiceTime: '8个月',
      teacherComment: '悟性很高，已初具行书神韵，前途无量',
      improvementScore: 94,
      displayOrder: 5
    },
    {
      id: 6,
      charityLocationId: 6,
      studentName: '赵小刚',
      studentAge: 13,
      school: '西安市雁塔区中学',
      grade: '初二',
      workTitle: '将进酒',
      workDescription: '李白《将进酒》草书作品，豪放不羁，笔走龙蛇，展现了草书的狂放之美',
      workImageUrl: studentWorks[5],
      workType: 'calligraphy',
      style: '草书',
      isFeatured: true,
      awards: ['全国青少年书法大赛入围奖', '市级书法名家点评优秀作品'],
      practiceTime: '2年',
      teacherComment: '极具天赋，草书已有大家风范，建议专业发展',
      improvementScore: 96,
      displayOrder: 6
    }
  ],

  // 志愿者活动详细数据
  volunteerActivitiesData: volunteerActivityDetails,

  // 新闻数据
  news: [
    {
      id: 1,
      title: '艺学练字机器人荣获"2024年度教育科技创新奖"',
      excerpt: '在刚刚结束的全国教育科技创新大会上，我们的AI练字机器人凭借其卓越的技术创新和教育应用效果，荣获"2024年度教育科技创新奖"。这一荣誉不仅是对我们技术实力的认可，更是对我们致力于传承中华书法文化使命的肯定。',
      content: `
        <p>在刚刚结束的全国教育科技创新大会上，我们的AI练字机器人凭借其卓越的技术创新和教育应用效果，荣获"2024年度教育科技创新奖"。这一荣誉不仅是对我们技术实力的认可，更是对我们致力于传承中华书法文化使命的肯定。</p>
        
        <h3>技术创新引领行业发展</h3>
        <p>我们的AI练字机器人采用了最先进的计算机视觉技术和深度学习算法，能够实时识别用户的书写动作，提供精准的笔画指导和姿势纠正。经过不断的技术迭代和优化，目前系统的识别准确率已达到99.5%，响应时间缩短至0.1秒以内。</p>
        
        <h3>教育应用成果显著</h3>
        <p>自产品推出以来，我们已与全国500多所学校建立合作关系，为超过10万名学生提供了智能化的书法学习服务。通过AI技术的辅助，学生的书写水平平均提升了40%，学习兴趣和积极性也得到了显著提高。</p>
        
        <h3>公益理念传承文化</h3>
        <p>我们始终坚持"每售出一台机器人，捐赠一节书法课"的公益理念，已为全国1200多个教学点提供了免费的书法课程，让更多偏远地区的孩子也能感受到书法艺术的魅力。</p>
        
        <h3>未来发展展望</h3>
        <p>获得这一殊荣后，我们将继续加大技术研发投入，不断完善产品功能，为用户提供更加优质的学习体验。同时，我们也将扩大公益项目的覆盖范围，让AI技术更好地服务于书法教育事业的发展。</p>
      `,
      category: '公司新闻',
      author: '新闻编辑部',
      status: 'published',
      image: 'https://picsum.photos/600/400?random=news1',
      tags: ['教育科技', '创新奖', '人工智能', '书法教育'],
      date: '2024-12-15T10:00:00Z',
      views: 1520,
      comments: 45,
      likes: 128
    },
    {
      id: 2,
      title: '新版AI算法上线，识别准确率提升至99.5%',
      excerpt: '经过半年的技术攻关，我们的新一代AI识别算法正式上线，笔画识别准确率从99.2%提升至99.5%，响应速度提升30%，为用户带来更加精准和流畅的练字体验。',
      content: `
        <p>经过半年的技术攻关，我们的新一代AI识别算法正式上线，笔画识别准确率从99.2%提升至99.5%，响应速度提升30%，为用户带来更加精准和流畅的练字体验。</p>
        
        <h3>算法优化亮点</h3>
        <p>新版算法在以下几个方面实现了重大突破：</p>
        <ul>
          <li>笔画识别准确率提升至99.5%</li>
          <li>响应时间缩短30%，达到0.1秒以内</li>
          <li>支持更多字体风格的识别</li>
          <li>优化了复杂笔画的处理逻辑</li>
        </ul>
        
        <h3>技术创新点</h3>
        <p>本次算法升级采用了最新的深度学习技术，通过大量的书法样本训练，显著提升了系统的智能化水平。同时，我们还优化了算法的运行效率，在保证准确性的前提下，大幅提升了处理速度。</p>
        
        <h3>用户体验提升</h3>
        <p>新算法的上线将为用户带来更加流畅和精准的练字体验，特别是在处理复杂笔画和连笔字时，系统的表现更加出色。</p>
      `,
      category: '技术更新',
      author: '技术团队',
      status: 'published',
      image: 'https://picsum.photos/500/350?random=news2',
      tags: ['技术创新', '人工智能', '算法优化', '用户体验'],
      date: '2024-12-10T14:30:00Z',
      views: 890,
      comments: 23,
      likes: 67
    },
    {
      id: 3,
      title: '与北京师范大学达成战略合作协议',
      excerpt: '双方将在书法教育研究、AI技术应用、师资培训等方面开展深度合作，共同推动书法教育的数字化转型和创新发展。',
      content: `
        <p>近日，我公司与北京师范大学正式签署战略合作协议，双方将在书法教育研究、AI技术应用、师资培训等方面开展深度合作，共同推动书法教育的数字化转型和创新发展。</p>
        
        <h3>合作内容</h3>
        <p>本次合作将围绕以下几个方面展开：</p>
        <ul>
          <li>书法教育理论研究与实践</li>
          <li>AI技术在教育领域的应用研究</li>
          <li>师资培训和人才培养</li>
          <li>教学资源共建共享</li>
          <li>学术交流与成果转化</li>
        </ul>
        
        <h3>合作意义</h3>
        <p>北京师范大学作为国内顶尖的师范院校，在教育理论研究和师资培养方面具有深厚的底蕴。此次合作将有助于我们更好地理解教育规律，提升产品的教育价值。</p>
        
        <h3>未来展望</h3>
        <p>通过与北京师范大学的深度合作，我们期望能够在书法教育领域取得更多突破性成果，为推动传统文化教育的现代化发展贡献力量。</p>
      `,
      category: '合作动态',
      author: '商务合作部',
      status: 'published',
      image: 'https://picsum.photos/400/300?random=news3',
      tags: ['合作伙伴', '教育机构', '战略合作', '师资培训'],
      date: '2024-12-05T09:15:00Z',
      views: 756,
      comments: 18,
      likes: 89
    },
    {
      id: 4,
      title: '全国中小学书法教育论坛成功举办',
      excerpt: '来自全国各地的300多位书法教育专家和一线教师参加了本次论坛，共同探讨AI技术在书法教育中的应用前景和发展方向。',
      content: `
        <p>12月1日，由我公司协办的"全国中小学书法教育论坛"在北京成功举办。来自全国各地的300多位书法教育专家和一线教师参加了本次论坛，共同探讨AI技术在书法教育中的应用前景和发展方向。</p>
        
        <h3>论坛亮点</h3>
        <p>本次论坛设置了多个专题讨论环节：</p>
        <ul>
          <li>传统书法教育与现代技术的融合</li>
          <li>AI辅助书法教学的实践案例分享</li>
          <li>数字化书法教育资源建设</li>
          <li>书法教育评价体系的创新</li>
        </ul>
        
        <h3>专家观点</h3>
        <p>与会专家一致认为，AI技术为书法教育带来了新的机遇，能够有效解决传统书法教学中的一些难点问题，如个性化指导、标准化评价等。</p>
        
        <h3>成果展示</h3>
        <p>论坛期间，我们展示了最新的AI练字机器人产品，获得了与会专家的高度评价和广泛关注。</p>
      `,
      category: '行业资讯',
      author: '行业观察',
      status: 'published',
      image: 'https://picsum.photos/400/300?random=news4',
      tags: ['行业论坛', '书法教育', '专家观点', '技术应用'],
      date: '2024-11-28T16:00:00Z',
      views: 1234,
      comments: 56,
      likes: 145
    },
    {
      id: 5,
      title: '公益项目"书法进乡村"惠及千名学生',
      excerpt: '我们的公益项目已为偏远地区的1000多名学生提供了免费的书法学习机会，通过AI练字机器人让更多孩子感受到书法艺术的魅力。',
      content: `
        <p>截至目前，我们的公益项目"书法进乡村"已为偏远地区的1000多名学生提供了免费的书法学习机会，通过AI练字机器人让更多孩子感受到书法艺术的魅力。</p>
        
        <h3>项目成果</h3>
        <p>自项目启动以来，我们取得了以下成果：</p>
        <ul>
          <li>覆盖全国15个省份的偏远地区</li>
          <li>建立了50个公益教学点</li>
          <li>培训了100多名志愿者教师</li>
          <li>惠及1000多名农村学生</li>
        </ul>
        
        <h3>感人故事</h3>
        <p>在贵州山区的一所小学，8岁的小明通过AI练字机器人的指导，书写水平有了显著提升。他说："机器人老师很有耐心，我现在越来越喜欢写字了。"</p>
        
        <h3>未来计划</h3>
        <p>我们计划在2025年将公益项目扩展到更多地区，目标是惠及5000名学生，让更多孩子享受到优质的书法教育资源。</p>
      `,
      category: '公益活动',
      author: '公益项目组',
      status: 'published',
      image: 'https://picsum.photos/400/300?random=news5',
      tags: ['公益项目', '乡村教育', '书法普及', '社会责任'],
      date: '2024-11-20T11:30:00Z',
      views: 2100,
      comments: 78,
      likes: 234
    },
    {
      id: 6,
      title: '练字机器人2.0版本正式发布',
      excerpt: '全新的2.0版本在硬件性能、软件功能、用户体验等方面都有显著提升，新增了多种字体风格和个性化学习模式。',
      content: `
        <p>经过一年的精心研发，练字机器人2.0版本正式发布。全新的2.0版本在硬件性能、软件功能、用户体验等方面都有显著提升，为用户带来更加智能化的书法学习体验。</p>
        
        <h3>主要升级</h3>
        <p>2.0版本的主要升级包括：</p>
        <ul>
          <li>硬件性能提升50%，运行更加流畅</li>
          <li>新增楷书、行书、隶书等多种字体风格</li>
          <li>个性化学习模式，根据用户水平调整难度</li>
          <li>全新的UI界面设计，操作更加简便</li>
          <li>增强的语音交互功能</li>
        </ul>
        
        <h3>技术亮点</h3>
        <p>2.0版本采用了最新的AI芯片，处理能力大幅提升。同时，我们还优化了算法架构，使得系统响应更加迅速，识别更加准确。</p>
        
        <h3>市场反响</h3>
        <p>产品发布后，获得了市场的热烈反响。预售期间，订单量超过了我们的预期，充分说明了用户对新产品的认可。</p>
      `,
      category: '公司新闻',
      author: '产品团队',
      status: 'published',
      image: 'https://picsum.photos/500/350?random=news6',
      tags: ['产品发布', '版本更新', '技术升级', '用户体验'],
      date: '2024-11-15T13:45:00Z',
      views: 1876,
      comments: 92,
      likes: 187
    },
    {
      id: 7,
      title: 'AI书法教学系统获得国家专利认证',
      excerpt: '我们自主研发的AI书法教学系统正式获得国家知识产权局颁发的发明专利证书，标志着我们在技术创新方面的重要突破。',
      content: `
        <p>近日，我们自主研发的AI书法教学系统正式获得国家知识产权局颁发的发明专利证书，专利号为ZL202410123456.7。这标志着我们在技术创新方面取得了重要突破，也为公司的知识产权保护奠定了坚实基础。</p>
        
        <h3>专利技术</h3>
        <p>本项专利涵盖了以下核心技术：</p>
        <ul>
          <li>基于深度学习的笔画识别算法</li>
          <li>实时书写姿势检测与纠正技术</li>
          <li>个性化学习路径推荐系统</li>
          <li>多维度书法评价体系</li>
        </ul>
        
        <h3>技术优势</h3>
        <p>相比传统的书法教学方法，我们的AI系统具有以下优势：识别准确率高、响应速度快、个性化程度强、评价标准客观。</p>
        
        <h3>市场价值</h3>
        <p>专利的获得不仅保护了我们的技术成果，也为公司在书法教育市场的竞争中提供了有力支撑。</p>
      `,
      category: '技术更新',
      author: '法务部',
      status: 'published',
      image: 'https://picsum.photos/400/300?random=news7',
      tags: ['专利认证', '技术创新', '知识产权', '核心技术'],
      date: '2024-11-08T10:20:00Z',
      views: 1456,
      comments: 34,
      likes: 98
    },
    {
      id: 8,
      title: '国际书法文化交流大会圆满落幕',
      excerpt: '来自20多个国家和地区的书法爱好者齐聚一堂，共同体验AI练字机器人带来的创新书法学习方式，促进了国际文化交流。',
      content: `
        <p>10月30日，为期三天的"国际书法文化交流大会"在上海圆满落幕。来自20多个国家和地区的书法爱好者齐聚一堂，共同体验AI练字机器人带来的创新书法学习方式，促进了国际文化交流。</p>
        
        <h3>大会盛况</h3>
        <p>本次大会吸引了来自美国、日本、韩国、新加坡等20多个国家和地区的300多名参会者，包括书法家、教育专家、文化学者等。</p>
        
        <h3>技术展示</h3>
        <p>我们在大会上展示了最新的AI练字机器人技术，特别是多语言支持功能，获得了国际友人的高度赞赏。</p>
        
        <h3>文化交流</h3>
        <p>通过技术展示和文化交流，我们不仅推广了中华书法文化，也学习了其他国家的文字艺术，实现了真正的文化互鉴。</p>
        
        <h3>合作机遇</h3>
        <p>大会期间，我们与多个国际机构达成了初步合作意向，为产品的国际化发展奠定了基础。</p>
      `,
      category: '行业资讯',
      author: '国际事务部',
      status: 'published',
      image: 'https://picsum.photos/400/300?random=news8',
      tags: ['国际交流', '文化传播', '技术展示', '合作机遇'],
      date: '2024-10-30T15:00:00Z',
      views: 987,
      comments: 41,
      likes: 156
    }
  ],

  // 平台链接数据
  platformLinks: {
    purchase: [
      {
        id: 1,
        platformName: 'Amazon',
        platformUrl: 'https://amazon.com/yx-robot-calligraphy',
        region: 'US',
        language: 'en',
        currency: 'USD',
        price: 1299,
        isActive: true,
        priority: 1,
        description: 'Available on Amazon US with fast shipping'
      },
      {
        id: 2,
        platformName: '淘宝',
        platformUrl: 'https://taobao.com/yx-robot-calligraphy',
        region: 'CN',
        language: 'zh',
        currency: 'CNY',
        price: 8999,
        isActive: true,
        priority: 1,
        description: '淘宝官方旗舰店，正品保证'
      },
      {
        id: 3,
        platformName: '京东',
        platformUrl: 'https://jd.com/yx-robot-calligraphy',
        region: 'CN',
        language: 'zh',
        currency: 'CNY',
        price: 8999,
        isActive: true,
        priority: 2,
        description: '京东自营，次日达服务'
      },
      {
        id: 4,
        platformName: 'Amazon Japan',
        platformUrl: 'https://amazon.co.jp/yx-robot-calligraphy',
        region: 'JP',
        language: 'ja',
        currency: 'JPY',
        price: 189000,
        isActive: true,
        priority: 1,
        description: 'Amazon Japan での販売'
      },
      {
        id: 5,
        platformName: 'Coupang',
        platformUrl: 'https://coupang.com/yx-robot-calligraphy',
        region: 'KR',
        language: 'ko',
        currency: 'KRW',
        price: 1650000,
        isActive: true,
        priority: 1,
        description: '쿠팡에서 판매 중'
      },
      {
        id: 6,
        platformName: 'MercadoLibre',
        platformUrl: 'https://mercadolibre.com/yx-robot-calligraphy',
        region: 'ES',
        language: 'es',
        currency: 'EUR',
        price: 1199,
        isActive: true,
        priority: 1,
        description: 'Disponible en MercadoLibre España'
      }
    ],
    rental: [
      {
        id: 1,
        platformName: 'YX租赁平台',
        platformUrl: 'https://rental.yxrobot.com',
        region: 'CN',
        language: 'zh',
        currency: 'CNY',
        dailyRate: 50,
        monthlyRate: 1200,
        isActive: true,
        priority: 1,
        description: '官方租赁平台，灵活租期选择'
      },
      {
        id: 2,
        platformName: 'EduRent',
        platformUrl: 'https://edurent.com/yx-robot',
        region: 'US',
        language: 'en',
        currency: 'USD',
        dailyRate: 8,
        monthlyRate: 199,
        isActive: true,
        priority: 1,
        description: 'Educational equipment rental platform'
      },
      {
        id: 3,
        platformName: '教育设备租赁',
        platformUrl: 'https://edu-rental.cn/yx-robot',
        region: 'CN',
        language: 'zh',
        currency: 'CNY',
        dailyRate: 45,
        monthlyRate: 1100,
        isActive: true,
        priority: 2,
        description: '专业教育设备租赁服务'
      },
      {
        id: 4,
        platformName: 'Japan Edu Rental',
        platformUrl: 'https://edu-rental.jp/yx-robot',
        region: 'JP',
        language: 'ja',
        currency: 'JPY',
        dailyRate: 1200,
        monthlyRate: 29800,
        isActive: true,
        priority: 1,
        description: '日本教育機器レンタル'
      },
      {
        id: 5,
        platformName: 'Korea Edu Rent',
        platformUrl: 'https://edu-rent.kr/yx-robot',
        region: 'KR',
        language: 'ko',
        currency: 'KRW',
        dailyRate: 65000,
        monthlyRate: 1580000,
        isActive: true,
        priority: 1,
        description: '한국 교육장비 렌탈'
      }
    ]
  }
}

// 模拟API延迟
export const mockApiDelay = (ms: number = 500): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, ms))
}