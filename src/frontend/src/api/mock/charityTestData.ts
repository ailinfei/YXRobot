/**
 * 公益慈善管理测试数据
 * 提供丰富的测试数据，包括图表数据、统计数值和详细文字内容
 */

import { CharityMockService } from './charity'
import type { CharityStats, CharityProject, CharityInstitution, CharityActivity } from './charity'

// 增强的公益统计数据生成器
export class EnhancedCharityMockService extends CharityMockService {
  
  // 生成增强的公益统计数据
  static generateEnhancedCharityStats(): CharityStats {
    return {
      totalProjects: 156,
      activeProjects: 42,
      completedProjects: 89,
      totalBeneficiaries: 28650,
      totalRaised: 18500000,
      totalDonated: 15200000,
      totalVolunteers: 285,
      activeVolunteers: 168,
      totalInstitutions: 342,
      cooperatingInstitutions: 198,
      totalActivities: 456,
      thisMonthActivities: 28
    }
  }

  // 生成详细的公益项目数据
  static generateDetailedCharityProjects(): CharityProject[] {
    const detailedProjects = [
      {
        id: 1,
        name: '山区儿童汉字启蒙计划',
        description: '针对偏远山区儿童开展的汉字启蒙教育项目，通过YX机器人提供个性化汉字学习服务，帮助山区儿童掌握基础汉字知识，提升文化素养。项目覆盖贵州、云南、四川等地的30所山区小学，预计受益学生超过2000人。',
        type: 'education' as const,
        status: 'active' as const,
        startDate: '2024-03-15',
        endDate: '2025-03-14',
        targetAmount: 500000,
        raisedAmount: 385000,
        beneficiaries: 2150,
        location: '贵州省黔东南州',
        organizer: 'YX机器人公益基金会',
        contactPerson: '张爱心',
        contactPhone: '13800138001',
        createdAt: '2024-02-20',
        updatedAt: '2024-12-15',
        images: ['https://picsum.photos/400/300?random=mountain1', 'https://picsum.photos/400/300?random=mountain2'],
        tags: ['教育扶贫', '汉字学习', '山区儿童', '智能教学']
      },
      {
        id: 2,
        name: '留守儿童书法教育项目',
        description: '为农村留守儿童提供专业的书法教育服务，通过YX机器人的智能书法指导功能，让留守儿童在缺乏家长陪伴的情况下也能接受优质的书法教育，培养文化自信和艺术修养。',
        type: 'education' as const,
        status: 'active' as const,
        startDate: '2024-01-10',
        endDate: '2024-12-31',
        targetAmount: 300000,
        raisedAmount: 280000,
        beneficiaries: 1580,
        location: '河南省信阳市',
        organizer: '教育发展基金会',
        contactPerson: '李志愿',
        contactPhone: '13800138002',
        createdAt: '2023-12-05',
        updatedAt: '2024-12-10',
        images: ['https://picsum.photos/400/300?random=calligraphy1', 'https://picsum.photos/400/300?random=calligraphy2'],
        tags: ['留守儿童', '书法教育', '文化传承', '艺术启蒙']
      },
      {
        id: 3,
        name: '特殊教育汉字学习支持',
        description: '专门为特殊教育学校的学生设计的汉字学习支持项目，根据不同类型特殊儿童的学习特点，定制化开发适合的汉字学习课程和教学方法，让每个孩子都能享受到平等的教育机会。',
        type: 'education' as const,
        status: 'completed' as const,
        startDate: '2023-09-01',
        endDate: '2024-08-31',
        targetAmount: 200000,
        raisedAmount: 220000,
        beneficiaries: 680,
        location: '北京市朝阳区',
        organizer: '儿童关爱协会',
        contactPerson: '王公益',
        contactPhone: '13800138003',
        createdAt: '2023-07-15',
        updatedAt: '2024-09-01',
        images: ['https://picsum.photos/400/300?random=special1', 'https://picsum.photos/400/300?random=special2'],
        tags: ['特殊教育', '汉字学习', '关爱儿童', '平等教育']
      },
      {
        id: 4,
        name: '乡村小学数字化教学援助',
        description: '为偏远乡村小学提供数字化教学设备和技术支持，包括YX机器人、多媒体教学设备等，帮助乡村学校提升教学质量，缩小城乡教育差距，让乡村孩子也能享受到现代化的教育资源。',
        type: 'donation' as const,
        status: 'active' as const,
        startDate: '2024-05-01',
        endDate: '2025-04-30',
        targetAmount: 800000,
        raisedAmount: 620000,
        beneficiaries: 3200,
        location: '甘肃省定西市',
        organizer: '科技助学联盟',
        contactPerson: '陈助学',
        contactPhone: '13800138004',
        createdAt: '2024-03-20',
        updatedAt: '2024-12-12',
        images: ['https://picsum.photos/400/300?random=rural1', 'https://picsum.photos/400/300?random=rural2'],
        tags: ['乡村教育', '数字化教学', '设备捐赠', '教育公平']
      },
      {
        id: 5,
        name: '贫困地区教育设备捐赠',
        description: '向全国贫困地区的学校捐赠YX机器人等教育设备，改善当地的教学条件。项目已覆盖西藏、新疆、青海等地的偏远学校，累计捐赠设备超过500台，直接受益学生超过万人。',
        type: 'donation' as const,
        status: 'active' as const,
        startDate: '2023-10-01',
        endDate: '2025-09-30',
        targetAmount: 1200000,
        raisedAmount: 950000,
        beneficiaries: 12500,
        location: '西藏自治区拉萨市',
        organizer: 'YX机器人公益基金会',
        contactPerson: '刘奉献',
        contactPhone: '13800138005',
        createdAt: '2023-08-15',
        updatedAt: '2024-12-08',
        images: ['https://picsum.photos/400/300?random=equipment1', 'https://picsum.photos/400/300?random=equipment2'],
        tags: ['设备捐赠', '贫困地区', '教育援助', '公益慈善']
      }
    ]

    // 生成更多项目数据
    const additionalProjects = super.generateCharityProjects(45)
    return [...detailedProjects, ...additionalProjects]
  }

  // 生成详细的合作机构数据
  static generateDetailedCharityInstitutions(): CharityInstitution[] {
    const detailedInstitutions = [
      {
        id: 1,
        name: '希望小学',
        type: 'school' as const,
        location: '贵州省黔东南州',
        address: '贵州省黔东南州凯里市希望路123号',
        contactPerson: '张校长',
        contactPhone: '13900139001',
        email: 'hope.school@example.com',
        studentCount: 450,
        cooperationDate: '2023-03-15',
        status: 'active' as const,
        deviceCount: 8,
        lastVisitDate: '2024-11-20',
        notes: '学校积极配合公益项目，学生学习热情高涨，汉字书写水平显著提升。',
        createdAt: '2023-02-10'
      },
      {
        id: 2,
        name: '阳光福利院',
        type: 'orphanage' as const,
        location: '河南省信阳市',
        address: '河南省信阳市浉河区阳光大道456号',
        contactPerson: '李院长',
        contactPhone: '13900139002',
        email: 'sunshine.welfare@example.com',
        studentCount: 120,
        cooperationDate: '2023-06-01',
        status: 'active' as const,
        deviceCount: 3,
        lastVisitDate: '2024-12-05',
        notes: '福利院儿童对汉字学习表现出浓厚兴趣，机器人教学效果良好。',
        createdAt: '2023-05-15'
      },
      {
        id: 3,
        name: '社区文化中心',
        type: 'community' as const,
        location: '北京市朝阳区',
        address: '北京市朝阳区建国路789号',
        contactPerson: '王主任',
        contactPhone: '13900139003',
        email: 'community.center@example.com',
        studentCount: 280,
        cooperationDate: '2024-01-10',
        status: 'active' as const,
        deviceCount: 5,
        lastVisitDate: '2024-12-01',
        notes: '社区居民参与度高，老年人和儿童都积极参与汉字学习活动。',
        createdAt: '2023-12-20'
      },
      {
        id: 4,
        name: '特殊教育学校',
        type: 'school' as const,
        location: '上海市浦东新区',
        address: '上海市浦东新区张江路321号',
        contactPerson: '赵老师',
        contactPhone: '13900139004',
        email: 'special.education@example.com',
        studentCount: 85,
        cooperationDate: '2023-09-01',
        status: 'active' as const,
        deviceCount: 4,
        lastVisitDate: '2024-11-15',
        notes: '针对特殊儿童的个性化教学方案效果显著，家长反馈良好。',
        createdAt: '2023-08-10'
      },
      {
        id: 5,
        name: '乡村图书馆',
        type: 'library' as const,
        location: '甘肃省定西市',
        address: '甘肃省定西市安定区文化街654号',
        contactPerson: '孙馆长',
        contactPhone: '13900139005',
        email: 'rural.library@example.com',
        studentCount: 200,
        cooperationDate: '2024-05-01',
        status: 'active' as const,
        deviceCount: 2,
        lastVisitDate: '2024-10-30',
        notes: '图书馆成为当地文化教育中心，汉字学习活动深受欢迎。',
        createdAt: '2024-04-15'
      }
    ]

    // 生成更多机构数据
    const additionalInstitutions = super.generateCharityInstitutions(75)
    return [...detailedInstitutions, ...additionalInstitutions]
  }

  // 生成详细的公益活动数据
  static generateDetailedCharityActivities(): CharityActivity[] {
    const detailedActivities = [
      {
        id: 1,
        projectId: 1,
        title: '山区儿童汉字启蒙设备捐赠仪式',
        description: '为贵州省黔东南州的5所山区小学捐赠YX机器人设备，现场举行捐赠仪式，并为当地教师提供设备使用培训。活动得到了当地政府和教育部门的大力支持，媒体广泛报道。',
        type: 'donation' as const,
        date: '2024-03-20',
        location: '贵州省黔东南州凯里市',
        participants: 150,
        organizer: 'YX机器人公益基金会',
        status: 'completed' as const,
        budget: 25000,
        actualCost: 23500,
        photos: ['https://picsum.photos/400/300?random=ceremony1', 'https://picsum.photos/400/300?random=ceremony2'],
        feedback: '活动圆满成功，当地师生反响热烈，媒体报道积极正面，为后续项目推广奠定了良好基础。',
        createdAt: '2024-03-15'
      },
      {
        id: 2,
        projectId: 2,
        title: '留守儿童书法教学培训活动',
        description: '邀请专业书法老师为河南省信阳市的留守儿童进行书法教学培训，结合YX机器人的智能书法指导功能，让孩子们在游戏中学习书法，培养对传统文化的兴趣。',
        type: 'training' as const,
        date: '2024-04-15',
        location: '河南省信阳市浉河区',
        participants: 80,
        organizer: '教育发展基金会',
        status: 'completed' as const,
        budget: 15000,
        actualCost: 14200,
        photos: ['https://picsum.photos/400/300?random=training1', 'https://picsum.photos/400/300?random=training2'],
        feedback: '孩子们学习积极性很高，书法水平有明显提升，家长和老师都很满意培训效果。',
        createdAt: '2024-04-10'
      },
      {
        id: 3,
        projectId: 3,
        title: '特殊教育学校爱心探访活动',
        description: '组织志愿者团队前往北京市朝阳区特殊教育学校进行爱心探访，为特殊儿童带去关爱和温暖，同时了解他们在汉字学习方面的需求和困难，为后续项目改进提供参考。',
        type: 'visit' as const,
        date: '2024-05-20',
        location: '北京市朝阳区',
        participants: 35,
        organizer: '儿童关爱协会',
        status: 'completed' as const,
        budget: 8000,
        actualCost: 7500,
        photos: ['https://picsum.photos/400/300?random=visit1', 'https://picsum.photos/400/300?random=visit2'],
        feedback: '志愿者们深受感动，特殊儿童们的坚强和乐观给大家留下深刻印象，活动意义深远。',
        createdAt: '2024-05-15'
      },
      {
        id: 4,
        projectId: 4,
        title: '乡村教师数字化教学技能培训',
        description: '为甘肃省定西市的乡村教师提供数字化教学技能培训，重点培训YX机器人的使用方法和教学应用，帮助教师掌握现代化教学工具，提升教学质量。',
        type: 'training' as const,
        date: '2024-06-10',
        location: '甘肃省定西市安定区',
        participants: 60,
        organizer: '科技助学联盟',
        status: 'completed' as const,
        budget: 20000,
        actualCost: 19800,
        photos: ['https://picsum.photos/400/300?random=teachertraining1', 'https://picsum.photos/400/300?random=teachertraining2'],
        feedback: '教师们学习热情很高，培训效果显著，大家都表示要将所学知识应用到实际教学中。',
        createdAt: '2024-06-05'
      },
      {
        id: 5,
        projectId: 5,
        title: '西藏地区教育设备捐赠活动',
        description: '向西藏自治区拉萨市的10所学校捐赠YX机器人等教育设备，克服高原环境的挑战，确保设备正常运行。活动得到了当地教育部门和学校的热烈欢迎。',
        type: 'donation' as const,
        date: '2024-07-25',
        location: '西藏自治区拉萨市',
        participants: 200,
        organizer: 'YX机器人公益基金会',
        status: 'completed' as const,
        budget: 50000,
        actualCost: 48500,
        photos: ['/images/activities/tibet_donation_1.jpg', '/images/activities/tibet_donation_2.jpg'],
        feedback: '在高原环境下成功完成设备安装和调试，当地师生对新设备充满期待和感激。',
        createdAt: '2024-07-20'
      }
    ]

    // 生成更多活动数据
    const additionalActivities = super.generateCharityActivities(145)
    return [...detailedActivities, ...additionalActivities]
  }

  // 生成图表数据
  static generateCharityChartData() {
    return {
      // 项目状态分布数据
      projectStatusData: [
        { name: '进行中', value: 42, color: '#409EFF' },
        { name: '已完成', value: 89, color: '#67C23A' },
        { name: '规划中', value: 25, color: '#E6A23C' }
      ],

      // 资金筹集趋势数据（12个月）
      fundingTrendData: {
        months: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
        raisedData: [850000, 920000, 1150000, 1380000, 1620000, 1450000, 1680000, 1820000, 1950000, 2100000, 1980000, 2250000],
        donatedData: [720000, 780000, 980000, 1150000, 1350000, 1200000, 1420000, 1550000, 1680000, 1800000, 1720000, 1950000]
      },

      // 地区分布数据
      regionDistributionData: [
        { name: '华北', value: 28, color: '#409EFF' },
        { name: '华东', value: 35, color: '#67C23A' },
        { name: '华南', value: 22, color: '#E6A23C' },
        { name: '华中', value: 18, color: '#F56C6C' },
        { name: '西南', value: 25, color: '#909399' },
        { name: '西北', value: 15, color: '#C0C4CC' },
        { name: '东北', value: 13, color: '#79BBFF' }
      ],

      // 志愿者活动统计数据（6个月）
      volunteerActivityData: {
        months: ['7月', '8月', '9月', '10月', '11月', '12月'],
        volunteerData: [85, 92, 78, 105, 118, 125],
        activityData: [12, 15, 11, 18, 22, 25]
      },

      // 受益人群分布
      beneficiaryDistribution: [
        { category: '学龄儿童', count: 18500, percentage: 64.6 },
        { category: '留守儿童', count: 5200, percentage: 18.1 },
        { category: '特殊儿童', count: 2800, percentage: 9.8 },
        { category: '成年学习者', count: 2150, percentage: 7.5 }
      ],

      // 机构类型分布
      institutionTypeDistribution: [
        { type: '小学', count: 145, percentage: 42.4 },
        { type: '社区中心', count: 89, percentage: 26.0 },
        { type: '福利院', count: 52, percentage: 15.2 },
        { type: '图书馆', count: 35, percentage: 10.2 },
        { type: '医院', count: 21, percentage: 6.1 }
      ]
    }
  }

  // 生成成功案例数据
  static generateSuccessStories() {
    return [
      {
        id: 1,
        title: '山区小学汉字识字率提升80%',
        location: '贵州省黔东南州',
        description: '通过YX机器人的智能教学，当地希望小学的学生汉字识字率从原来的45%提升到了81%，学习兴趣和积极性显著提高。',
        impact: '受益学生450人，识字率提升80%',
        testimonial: '"孩子们现在每天都期待着和机器人老师一起学习汉字，学习效果比以前好太多了！" - 张校长',
        image: '/images/success/mountain_school.jpg',
        date: '2024-11-15'
      },
      {
        id: 2,
        title: '留守儿童书法大赛获奖',
        location: '河南省信阳市',
        description: '参与项目的留守儿童在全市书法大赛中获得多个奖项，展现了显著的学习成果，增强了文化自信。',
        impact: '15名学生获奖，其中一等奖3名',
        testimonial: '"没想到我们的孩子也能写出这么漂亮的字，真的太感谢这个项目了！" - 学生家长',
        image: '/images/success/calligraphy_award.jpg',
        date: '2024-10-20'
      },
      {
        id: 3,
        title: '特殊教育突破性进展',
        location: '北京市朝阳区',
        description: '特殊教育学校的自闭症儿童通过个性化的汉字学习方案，在语言表达和社交能力方面取得突破性进展。',
        impact: '85%的学生在语言能力测评中有显著提升',
        testimonial: '"我的孩子现在能主动和我们交流了，这是我们从未想过的奇迹！" - 学生家长',
        image: '/images/success/special_education.jpg',
        date: '2024-09-30'
      }
    ]
  }
}

// 导出增强的公益测试数据API
export const enhancedCharityTestAPI = {
  // 获取增强的统计数据
  async getEnhancedCharityStats() {
    await new Promise(resolve => setTimeout(resolve, 300))
    const baseStats = EnhancedCharityMockService.generateEnhancedCharityStats()
    
    // 添加趋势数据和月度对比
    const enhancedStats = {
      ...baseStats,
      trends: {
        beneficiariesTrend: [22000, 23500, 25200, 26800, 27900, 28650],
        institutionsTrend: [280, 295, 315, 325, 335, 342],
        projectsTrend: [120, 135, 142, 148, 152, 156],
        fundingTrend: [12500000, 14200000, 15800000, 16900000, 17800000, 18500000]
      },
      monthlyComparison: {
        beneficiariesChange: 12.5,
        institutionsChange: 8.3,
        projectsChange: 15.2,
        fundingChange: 6.8
      },
      lastUpdated: new Date().toISOString()
    }
    
    return {
      code: 200,
      message: 'success',
      data: enhancedStats,
      timestamp: Date.now()
    }
  },

  // 获取实时统计数据（用于动画效果）
  async getRealTimeCharityStats() {
    await new Promise(resolve => setTimeout(resolve, 100))
    const baseStats = EnhancedCharityMockService.generateEnhancedCharityStats()
    
    // 模拟实时数据变化
    const realTimeStats = {
      ...baseStats,
      totalBeneficiaries: baseStats.totalBeneficiaries + Math.floor(Math.random() * 10),
      cooperatingInstitutions: baseStats.cooperatingInstitutions + Math.floor(Math.random() * 3),
      totalProjects: baseStats.totalProjects + Math.floor(Math.random() * 2),
      totalRaised: baseStats.totalRaised + Math.floor(Math.random() * 50000),
      lastUpdated: new Date().toISOString()
    }
    
    return {
      code: 200,
      message: 'success',
      data: realTimeStats,
      timestamp: Date.now()
    }
  },

  // 获取详细项目数据
  async getDetailedCharityProjects(params: any = {}) {
    await new Promise(resolve => setTimeout(resolve, 400))
    const data = EnhancedCharityMockService.generateDetailedCharityProjects()
    
    const page = params.page || 1
    const pageSize = params.pageSize || 10
    const total = data.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = data.slice(start, end)
    
    return {
      code: 200,
      message: 'success',
      data: {
        list,
        total,
        page,
        pageSize,
        totalPages: Math.ceil(total / pageSize)
      },
      timestamp: Date.now()
    }
  },

  // 获取详细机构数据
  async getDetailedCharityInstitutions(params: any = {}) {
    await new Promise(resolve => setTimeout(resolve, 400))
    const data = EnhancedCharityMockService.generateDetailedCharityInstitutions()
    
    const page = params.page || 1
    const pageSize = params.pageSize || 10
    const total = data.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = data.slice(start, end)
    
    return {
      code: 200,
      message: 'success',
      data: {
        list,
        total,
        page,
        pageSize,
        totalPages: Math.ceil(total / pageSize)
      },
      timestamp: Date.now()
    }
  },

  // 获取详细活动数据
  async getDetailedCharityActivities(params: any = {}) {
    await new Promise(resolve => setTimeout(resolve, 400))
    const data = EnhancedCharityMockService.generateDetailedCharityActivities()
    
    const page = params.page || 1
    const pageSize = params.pageSize || 10
    const total = data.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = data.slice(start, end)
    
    return {
      code: 200,
      message: 'success',
      data: {
        list,
        total,
        page,
        pageSize,
        totalPages: Math.ceil(total / pageSize)
      },
      timestamp: Date.now()
    }
  },

  // 获取图表数据
  async getCharityChartData() {
    await new Promise(resolve => setTimeout(resolve, 200))
    return {
      code: 200,
      message: 'success',
      data: EnhancedCharityMockService.generateCharityChartData(),
      timestamp: Date.now()
    }
  },

  // 获取成功案例
  async getSuccessStories() {
    await new Promise(resolve => setTimeout(resolve, 300))
    return {
      code: 200,
      message: 'success',
      data: EnhancedCharityMockService.generateSuccessStories(),
      timestamp: Date.now()
    }
  },

  // 创建合作机构
  async createCharityInstitution(institutionData: any) {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟创建机构
    const newInstitution = {
      id: Date.now(), // 使用时间戳作为ID
      ...institutionData,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    
    return {
      code: 200,
      message: '机构创建成功',
      data: newInstitution,
      timestamp: Date.now()
    }
  },

  // 更新合作机构
  async updateCharityInstitution(id: number, institutionData: any) {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟更新机构
    const updatedInstitution = {
      id,
      ...institutionData,
      updatedAt: new Date().toISOString()
    }
    
    return {
      code: 200,
      message: '机构更新成功',
      data: updatedInstitution,
      timestamp: Date.now()
    }
  },

  // 删除合作机构
  async deleteCharityInstitution(id: number) {
    await new Promise(resolve => setTimeout(resolve, 300))
    
    return {
      code: 200,
      message: '机构删除成功',
      data: { id },
      timestamp: Date.now()
    }
  },

  // 获取单个机构详情
  async getCharityInstitutionById(id: number) {
    await new Promise(resolve => setTimeout(resolve, 300))
    const institutions = EnhancedCharityMockService.generateDetailedCharityInstitutions()
    const institution = institutions.find(inst => inst.id === id)
    
    if (!institution) {
      return {
        code: 404,
        message: '机构不存在',
        data: null,
        timestamp: Date.now()
      }
    }
    
    return {
      code: 200,
      message: 'success',
      data: institution,
      timestamp: Date.now()
    }
  },

  // 更新公益统计数据
  async updateCharityStats(statsData: any) {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟数据验证
    const validationErrors = []
    
    if (statsData.cooperatingInstitutions > statsData.totalInstitutions) {
      validationErrors.push('活跃合作机构数不能超过合作机构总数')
    }
    
    if (statsData.activeProjects + statsData.completedProjects > statsData.totalProjects) {
      validationErrors.push('进行中项目数和已完成项目数之和不能超过项目总数')
    }
    
    if (statsData.totalDonated > statsData.totalRaised) {
      validationErrors.push('累计捐赠金额不能超过累计筹集金额')
    }
    
    if (statsData.thisMonthActivities > statsData.totalActivities) {
      validationErrors.push('本月活动数不能超过活动总数')
    }
    
    if (validationErrors.length > 0) {
      return {
        code: 400,
        message: validationErrors[0],
        data: null,
        timestamp: Date.now()
      }
    }
    
    // 模拟成功更新
    const updatedStats = {
      ...statsData,
      lastUpdated: new Date().toISOString(),
      updatedBy: '管理员'
    }
    
    // 记录更新日志
    const updateLog = {
      id: Date.now(),
      timestamp: new Date().toISOString(),
      updatedBy: '管理员',
      reason: statsData.updateReason,
      changes: Object.keys(statsData).reduce((acc, key) => {
        if (key !== 'updateReason') {
          acc[key] = {
            from: EnhancedCharityMockService.generateEnhancedCharityStats()[key as keyof typeof statsData],
            to: statsData[key]
          }
        }
        return acc
      }, {} as any)
    }
    
    console.log('公益数据更新成功:', updateLog)
    
    return {
      code: 200,
      message: '公益数据更新成功',
      data: {
        updatedStats,
        updateLog
      },
      timestamp: Date.now()
    }
  },

  // 获取数据更新历史记录
  async getCharityStatsUpdateHistory(params: any = {}) {
    await new Promise(resolve => setTimeout(resolve, 300))
    
    // 模拟历史记录数据
    const historyData = [
      {
        id: 1,
        timestamp: '2024-12-15T10:30:00.000Z',
        updatedBy: '管理员',
        reason: '月度数据统计更新',
        changes: {
          totalBeneficiaries: { from: 28500, to: 28650 },
          totalActivities: { from: 450, to: 456 }
        }
      },
      {
        id: 2,
        timestamp: '2024-12-10T14:20:00.000Z',
        updatedBy: '管理员',
        reason: '新增合作机构数据同步',
        changes: {
          totalInstitutions: { from: 340, to: 342 },
          cooperatingInstitutions: { from: 196, to: 198 }
        }
      },
      {
        id: 3,
        timestamp: '2024-12-05T09:15:00.000Z',
        updatedBy: '管理员',
        reason: '项目完成状态更新',
        changes: {
          activeProjects: { from: 45, to: 42 },
          completedProjects: { from: 86, to: 89 }
        }
      }
    ]
    
    const page = params.page || 1
    const pageSize = params.pageSize || 10
    const total = historyData.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const list = historyData.slice(start, end)
    
    return {
      code: 200,
      message: 'success',
      data: {
        list,
        total,
        page,
        pageSize,
        totalPages: Math.ceil(total / pageSize)
      },
      timestamp: Date.now()
    }
  }
}