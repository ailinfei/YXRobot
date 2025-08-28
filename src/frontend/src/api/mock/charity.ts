/**
 * 公益慈善管理Mock数据
 * 提供公益慈善相关的模拟数据，用于前端开发和测试
 */

export interface CharityProject {
  id: number
  name: string
  description: string
  type: 'education' | 'donation' | 'volunteer' | 'training'
  status: 'planning' | 'active' | 'completed' | 'suspended'
  startDate: string
  endDate: string
  targetAmount: number
  raisedAmount: number
  beneficiaries: number
  location: string
  organizer: string
  contactPerson: string
  contactPhone: string
  createdAt: string
  updatedAt: string
  images: string[]
  tags: string[]
}

export interface CharityInstitution {
  id: number
  name: string
  type: 'school' | 'orphanage' | 'community' | 'hospital' | 'library'
  location: string
  address: string
  contactPerson: string
  contactPhone: string
  email: string
  studentCount: number
  cooperationDate: string
  status: 'active' | 'inactive' | 'pending'
  deviceCount: number
  lastVisitDate?: string
  notes: string
  createdAt: string
}

export interface CharityActivity {
  id: number
  projectId: number
  title: string
  description: string
  type: 'visit' | 'training' | 'donation' | 'event'
  date: string
  location: string
  participants: number
  organizer: string
  status: 'planned' | 'ongoing' | 'completed' | 'cancelled'
  budget: number
  actualCost: number
  photos: string[]
  feedback: string
  createdAt: string
}

export interface CharityDonation {
  id: number
  projectId: number
  donorName: string
  donorType: 'individual' | 'company' | 'organization'
  amount: number
  donationType: 'money' | 'device' | 'material' | 'service'
  donationDate: string
  status: 'pending' | 'confirmed' | 'delivered' | 'completed'
  receipt: string
  notes: string
  isAnonymous: boolean
  createdAt: string
}

export interface CharityVolunteer {
  id: number
  name: string
  phone: string
  email: string
  profession: string
  skills: string[]
  availableTime: string
  joinDate: string
  status: 'active' | 'inactive'
  participatedActivities: number
  totalHours: number
  rating: number
  notes: string
  createdAt: string
}

export interface CharityStats {
  totalProjects: number
  activeProjects: number
  completedProjects: number
  totalBeneficiaries: number
  totalRaised: number
  totalDonated: number
  totalVolunteers: number
  activeVolunteers: number
  totalInstitutions: number
  cooperatingInstitutions: number
  totalActivities: number
  thisMonthActivities: number
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
  return date.toISOString().slice(0, 10)
}

// Mock数据生成器
export class CharityMockService {
  private static projectNames = [
    '山区儿童汉字启蒙计划', '留守儿童书法教育项目', '特殊教育汉字学习支持',
    '乡村小学数字化教学援助', '贫困地区教育设备捐赠', '汉字文化传承公益行',
    '智能教育扶贫计划', '残障儿童学习辅助项目', '偏远地区教师培训计划',
    '城乡教育资源共享项目', '汉字学习机器人普及计划', '文化教育精准扶贫',
    '青少年书法艺术推广', '多元化教育支持项目', '科技助学公益计划',
    '希望小学建设项目', '爱心图书馆援建', '温暖冬衣捐赠行动',
    '营养午餐改善计划', '心理健康关爱项目', '科技创新教育支持',
    '传统文化进校园', '绿色环保教育推广', '安全教育普及计划',
    '艺术教育启蒙项目', '体育器材捐赠活动', '医疗健康检查服务',
    '职业技能培训计划', '创业扶持项目', '老年关爱服务'
  ]

  private static institutionNames = [
    '希望小学', '阳光小学', '育才小学', '明德小学', '启智小学',
    '实验小学', '红星小学', '新华小学', '光明小学', '向阳小学',
    '爱心孤儿院', '阳光福利院', '温馨之家', '关爱中心', '希望之家',
    '彩虹儿童村', '星星之家', '快乐成长园', '天使之翼', '梦想家园',
    '社区文化中心', '青少年活动中心', '老年活动中心', '文化站', '图书馆',
    '科技馆', '博物馆', '艺术中心', '体育中心', '教育培训中心',
    '康复医院', '儿童医院', '社区卫生院', '福利医院', '专科医院',
    '妇幼保健院', '中医院', '人民医院', '第一医院', '第二医院',
    '特殊教育学校', '职业技术学校', '师范学校', '艺术学校', '体育学校'
  ]

  private static locations = [
    '北京市', '上海市', '广州市', '深圳市', '杭州市', '成都市', '西安市',
    '重庆市', '天津市', '南京市', '武汉市', '沈阳市', '长沙市', '郑州市',
    '济南市', '哈尔滨市', '长春市', '石家庄市', '太原市', '呼和浩特市'
  ]

  private static organizers = [
    'YX机器人公益基金会', '教育发展基金会', '儿童关爱协会', '文化传承基金',
    '科技助学联盟', '爱心企业联合会', '志愿者协会', '慈善总会'
  ]

  private static volunteerNames = [
    '张志愿', '李爱心', '王公益', '陈助学', '刘奉献', '杨热心', '赵善良',
    '黄温暖', '周关爱', '吴慈善', '徐帮助', '孙友爱', '马仁慈', '朱善行'
  ]

  private static skills = [
    '汉字教学', '书法指导', '心理辅导', '计算机技术', '音乐教学',
    '美术指导', '体育运动', '医疗护理', '营养指导', '法律咨询',
    '财务管理', '项目管理', '摄影摄像', '文案写作', '翻译服务'
  ]

  private static tags = [
    '教育扶贫', '汉字学习', '书法教育', '智能教学', '设备捐赠',
    '师资培训', '文化传承', '科技助学', '关爱儿童', '乡村振兴'
  ]

  // 生成公益项目数据
  static generateCharityProjects(count: number = 30): CharityProject[] {
    const projects: CharityProject[] = []
    const types: ('education' | 'donation' | 'volunteer' | 'training')[] =
      ['education', 'donation', 'volunteer', 'training']
    const statuses: ('planning' | 'active' | 'completed' | 'suspended')[] =
      ['planning', 'active', 'completed', 'suspended']

    for (let i = 1; i <= count; i++) {
      const targetAmount = randomBetween(50000, 500000)
      const raisedAmount = randomBetween(0, targetAmount * 1.2)
      const startDate = generateRandomDate(365)
      const endDate = new Date(new Date(startDate).getTime() + randomBetween(30, 365) * 24 * 60 * 60 * 1000)
        .toISOString().slice(0, 10)

      const tagCount = randomBetween(2, 5)
      const selectedTags: string[] = []
      for (let j = 0; j < tagCount; j++) {
        const tag = this.tags[randomBetween(0, this.tags.length - 1)]
        if (!selectedTags.includes(tag)) {
          selectedTags.push(tag)
        }
      }

      projects.push({
        id: i,
        name: this.projectNames[randomBetween(0, this.projectNames.length - 1)],
        description: `这是一个专注于${selectedTags[0]}的公益项目，旨在通过创新的方式帮助更多需要帮助的人群，提供优质的教育资源和学习机会。`,
        type: types[randomBetween(0, types.length - 1)],
        status: statuses[randomBetween(0, statuses.length - 1)],
        startDate,
        endDate,
        targetAmount,
        raisedAmount,
        beneficiaries: randomBetween(50, 1000),
        location: this.locations[randomBetween(0, this.locations.length - 1)],
        organizer: this.organizers[randomBetween(0, this.organizers.length - 1)],
        contactPerson: `联系人${i}`,
        contactPhone: `1${randomBetween(3, 9)}${String(randomBetween(10000000, 99999999))}`,
        createdAt: generateRandomDate(400),
        updatedAt: generateRandomDate(30),
        images: [
          `https://picsum.photos/400/300?random=project${i}1`,
          `https://picsum.photos/400/300?random=project${i}2`,
          `https://picsum.photos/400/300?random=project${i}3`
        ],
        tags: selectedTags
      })
    }

    return projects.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }

  // 生成合作机构数据
  static generateCharityInstitutions(count: number = 50): CharityInstitution[] {
    const institutions: CharityInstitution[] = []
    const types: ('school' | 'orphanage' | 'community' | 'hospital' | 'library')[] =
      ['school', 'orphanage', 'community', 'hospital', 'library']
    const statuses: ('active' | 'inactive' | 'pending')[] = ['active', 'inactive', 'pending']

    for (let i = 1; i <= count; i++) {
      const location = this.locations[randomBetween(0, this.locations.length - 1)]
      const hasLastVisit = Math.random() > 0.3

      institutions.push({
        id: i,
        name: this.institutionNames[randomBetween(0, this.institutionNames.length - 1)],
        type: types[randomBetween(0, types.length - 1)],
        location,
        address: `${location}${['朝阳区', '海淀区', '西城区', '东城区', '丰台区'][randomBetween(0, 4)]}第${randomBetween(1, 999)}号`,
        contactPerson: `负责人${i}`,
        contactPhone: `1${randomBetween(3, 9)}${String(randomBetween(10000000, 99999999))}`,
        email: `contact${i}@institution.org`,
        studentCount: randomBetween(50, 800),
        cooperationDate: generateRandomDate(730),
        status: statuses[randomBetween(0, statuses.length - 1)],
        deviceCount: randomBetween(0, 20),
        lastVisitDate: hasLastVisit ? generateRandomDate(90) : undefined,
        notes: Math.random() > 0.7 ? `机构备注信息 ${i}` : '',
        createdAt: generateRandomDate(800)
      })
    }

    return institutions.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }

  // 生成公益活动数据
  static generateCharityActivities(count: number = 100): CharityActivity[] {
    const activities: CharityActivity[] = []
    const types: ('visit' | 'training' | 'donation' | 'event')[] =
      ['visit', 'training', 'donation', 'event']
    const statuses: ('planned' | 'ongoing' | 'completed' | 'cancelled')[] =
      ['planned', 'ongoing', 'completed', 'cancelled']

    const activityTitles = [
      '汉字学习设备捐赠仪式', '书法教学培训活动', '爱心志愿者探访',
      '教育资源分享会', '儿童节庆祝活动', '教师技能培训',
      '学习成果展示会', '家长座谈会', '文化交流活动',
      '设备使用培训', '心理健康讲座', '营养健康指导'
    ]

    for (let i = 1; i <= count; i++) {
      const budget = randomBetween(5000, 50000)
      const actualCost = randomBetween(budget * 0.8, budget * 1.1)

      activities.push({
        id: i,
        projectId: randomBetween(1, 30),
        title: activityTitles[randomBetween(0, activityTitles.length - 1)],
        description: `这是一次意义深远的公益活动，旨在为受益群体提供实际帮助和支持，促进教育资源的公平分配。`,
        type: types[randomBetween(0, types.length - 1)],
        date: generateRandomDate(180),
        location: this.locations[randomBetween(0, this.locations.length - 1)],
        participants: randomBetween(10, 100),
        organizer: this.organizers[randomBetween(0, this.organizers.length - 1)],
        status: statuses[randomBetween(0, statuses.length - 1)],
        budget,
        actualCost,
        photos: [
          `https://picsum.photos/400/300?random=activity${i}1`,
          `https://picsum.photos/400/300?random=activity${i}2`
        ],
        feedback: Math.random() > 0.5 ? `活动反馈：参与者反响热烈，达到了预期效果。` : '',
        createdAt: generateRandomDate(200)
      })
    }

    return activities.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
  }

  // 生成捐赠记录数据
  static generateCharityDonations(count: number = 80): CharityDonation[] {
    const donations: CharityDonation[] = []
    const donorTypes: ('individual' | 'company' | 'organization')[] =
      ['individual', 'company', 'organization']
    const donationTypes: ('money' | 'device' | 'material' | 'service')[] =
      ['money', 'device', 'material', 'service']
    const statuses: ('pending' | 'confirmed' | 'delivered' | 'completed')[] =
      ['pending', 'confirmed', 'delivered', 'completed']

    const companyNames = [
      '爱心科技有限公司', '慈善基金会', '教育发展集团', '公益联盟',
      '温暖企业', '关爱公司', '希望集团', '善行科技'
    ]

    for (let i = 1; i <= count; i++) {
      const donorType = donorTypes[randomBetween(0, donorTypes.length - 1)]
      const isAnonymous = Math.random() > 0.8

      let donorName = ''
      if (isAnonymous) {
        donorName = '匿名捐赠者'
      } else if (donorType === 'individual') {
        donorName = `爱心人士${i}`
      } else {
        donorName = companyNames[randomBetween(0, companyNames.length - 1)]
      }

      donations.push({
        id: i,
        projectId: randomBetween(1, 30),
        donorName,
        donorType,
        amount: randomBetween(1000, 100000),
        donationType: donationTypes[randomBetween(0, donationTypes.length - 1)],
        donationDate: generateRandomDate(365),
        status: statuses[randomBetween(0, statuses.length - 1)],
        receipt: `RECEIPT_${String(i).padStart(6, '0')}`,
        notes: Math.random() > 0.6 ? `捐赠备注信息 ${i}` : '',
        isAnonymous,
        createdAt: generateRandomDate(400)
      })
    }

    return donations.sort((a, b) => new Date(b.donationDate).getTime() - new Date(a.donationDate).getTime())
  }

  // 生成志愿者数据
  static generateCharityVolunteers(count: number = 60): CharityVolunteer[] {
    const volunteers: CharityVolunteer[] = []
    const statuses: ('active' | 'inactive')[] = ['active', 'inactive']
    const professions = [
      '教师', '医生', '工程师', '设计师', '律师', '会计师', '程序员',
      '护士', '心理咨询师', '社工', '记者', '翻译', '艺术家', '学生'
    ]

    for (let i = 1; i <= count; i++) {
      const skillCount = randomBetween(2, 5)
      const selectedSkills: string[] = []
      for (let j = 0; j < skillCount; j++) {
        const skill = this.skills[randomBetween(0, this.skills.length - 1)]
        if (!selectedSkills.includes(skill)) {
          selectedSkills.push(skill)
        }
      }

      volunteers.push({
        id: i,
        name: this.volunteerNames[randomBetween(0, this.volunteerNames.length - 1)],
        phone: `1${randomBetween(3, 9)}${String(randomBetween(10000000, 99999999))}`,
        email: `volunteer${i}@example.com`,
        profession: professions[randomBetween(0, professions.length - 1)],
        skills: selectedSkills,
        availableTime: ['周末', '工作日晚上', '节假日', '随时'][randomBetween(0, 3)],
        joinDate: generateRandomDate(730),
        status: statuses[randomBetween(0, statuses.length - 1)],
        participatedActivities: randomBetween(1, 20),
        totalHours: randomBetween(10, 500),
        rating: randomFloat(4.0, 5.0, 1),
        notes: Math.random() > 0.7 ? `志愿者备注信息 ${i}` : '',
        createdAt: generateRandomDate(800)
      })
    }

    return volunteers.sort((a, b) => new Date(b.joinDate).getTime() - new Date(a.joinDate).getTime())
  }

  // 生成公益统计数据
  static generateCharityStats(): CharityStats {
    const totalProjects = randomBetween(80, 120)
    const activeProjects = randomBetween(25, 35)
    const completedProjects = randomBetween(35, 50)

    return {
      totalProjects,
      activeProjects,
      completedProjects,
      totalBeneficiaries: randomBetween(15000, 35000),
      totalRaised: randomBetween(8000000, 25000000),
      totalDonated: randomBetween(6000000, 20000000),
      totalVolunteers: randomBetween(150, 300),
      activeVolunteers: randomBetween(80, 150),
      totalInstitutions: randomBetween(200, 400),
      cooperatingInstitutions: randomBetween(120, 250),
      totalActivities: randomBetween(300, 500),
      thisMonthActivities: randomBetween(15, 35)
    }
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
export const mockCharityAPI = {
  // 获取公益统计数据
  async getCharityStats() {
    await mockDelay()
    return createMockResponse(CharityMockService.generateCharityStats())
  },

  // 获取公益项目列表
  async getCharityProjects(params: any) {
    await mockDelay()
    let data = CharityMockService.generateCharityProjects(50)

    // 应用筛选
    if (params?.status) {
      data = data.filter(item => item.status === params.status)
    }

    if (params?.type) {
      data = data.filter(item => item.type === params.type)
    }

    if (params?.keyword) {
      data = data.filter(item =>
        item.name.includes(params.keyword) ||
        item.description.includes(params.keyword) ||
        item.organizer.includes(params.keyword)
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

  // 获取合作机构列表
  async getCharityInstitutions(params: any) {
    await mockDelay()
    let data = CharityMockService.generateCharityInstitutions(80)

    // 应用筛选
    if (params?.type) {
      data = data.filter(item => item.type === params.type)
    }

    if (params?.status) {
      data = data.filter(item => item.status === params.status)
    }

    if (params?.keyword) {
      data = data.filter(item =>
        item.name.includes(params.keyword) ||
        item.location.includes(params.keyword) ||
        item.contactPerson.includes(params.keyword)
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

  // 获取公益活动列表
  async getCharityActivities(params: any) {
    await mockDelay()
    let data = CharityMockService.generateCharityActivities(150)

    // 应用筛选
    if (params?.type) {
      data = data.filter(item => item.type === params.type)
    }

    if (params?.status) {
      data = data.filter(item => item.status === params.status)
    }

    if (params?.projectId) {
      data = data.filter(item => item.projectId === params.projectId)
    }

    if (params?.keyword) {
      data = data.filter(item =>
        item.title.includes(params.keyword) ||
        item.description.includes(params.keyword) ||
        item.location.includes(params.keyword)
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

  // 获取捐赠记录列表
  async getCharityDonations(params: any) {
    await mockDelay()
    let data = CharityMockService.generateCharityDonations(120)

    // 应用筛选
    if (params?.donorType) {
      data = data.filter(item => item.donorType === params.donorType)
    }

    if (params?.donationType) {
      data = data.filter(item => item.donationType === params.donationType)
    }

    if (params?.status) {
      data = data.filter(item => item.status === params.status)
    }

    if (params?.projectId) {
      data = data.filter(item => item.projectId === params.projectId)
    }

    if (params?.keyword) {
      data = data.filter(item =>
        item.donorName.includes(params.keyword) ||
        item.receipt.includes(params.keyword)
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

  // 获取志愿者列表
  async getCharityVolunteers(params: any) {
    await mockDelay()
    let data = CharityMockService.generateCharityVolunteers(100)

    // 应用筛选
    if (params?.status) {
      data = data.filter(item => item.status === params.status)
    }

    if (params?.profession) {
      data = data.filter(item => item.profession === params.profession)
    }

    if (params?.keyword) {
      data = data.filter(item =>
        item.name.includes(params.keyword) ||
        item.phone.includes(params.keyword) ||
        item.email.includes(params.keyword) ||
        item.profession.includes(params.keyword)
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

  // 创建公益项目
  async createCharityProject(projectData: any) {
    await mockDelay()
    const newProject = {
      id: Date.now(),
      ...projectData,
      raisedAmount: 0,
      createdAt: new Date().toISOString().slice(0, 10),
      updatedAt: new Date().toISOString().slice(0, 10)
    }
    return createMockResponse(newProject)
  },

  // 更新公益项目
  async updateCharityProject(projectId: number, projectData: any) {
    await mockDelay()
    const updatedProject = {
      id: projectId,
      ...projectData,
      updatedAt: new Date().toISOString().slice(0, 10)
    }
    return createMockResponse(updatedProject)
  },

  // 删除公益项目
  async deleteCharityProject(projectId: number) {
    await mockDelay()
    return createMockResponse(null)
  }
}