// 图片URL配置文件
// 使用可靠的网络图片服务替代本地图片路径

export const imageUrls = {
  // 默认图片 - 使用真实的随机图片
  'default-activity': 'https://picsum.photos/400/300?random=activity',
  'default-avatar': 'https://ui-avatars.com/api/?name=User&background=e0e0e0&color=999999&size=100',
  
  // 产品图片 - 使用真实的随机图片
  'robot-main': 'https://picsum.photos/600/400?random=robot1',
  'robot-detail-1': 'https://picsum.photos/500/350?random=robot2',
  'robot-detail-2': 'https://picsum.photos/500/350?random=robot3',
  
  // 公益活动图片 - 使用真实的随机图片
  'charity-teaching': 'https://picsum.photos/400/300?random=teaching1',
  'charity-activity': 'https://picsum.photos/400/300?random=activity1',
  'charity-equipment': 'https://picsum.photos/400/300?random=equipment1',
  'charity-people': 'https://picsum.photos/400/300?random=people1',
  
  // 访问活动图片 - 使用真实的随机图片
  'visit-1': 'https://picsum.photos/400/300?random=visit1',
  'visit-2': 'https://picsum.photos/400/300?random=visit2',
  
  // 错误和加载图片 - 使用占位符服务
  'image-error': 'https://via.placeholder.com/200x150/ffebee/f44336?text=Image+Error',
  'image-loading': 'https://via.placeholder.com/200x150/e3f2fd/2196f3?text=Loading...'
}

// 国家标志图片 - 使用免费的flagcdn服务
export const flagUrls = {
  'cn': 'https://flagcdn.com/w80/cn.png',
  'us': 'https://flagcdn.com/w80/us.png', 
  'jp': 'https://flagcdn.com/w80/jp.png',
  'kr': 'https://flagcdn.com/w80/kr.png',
  'es': 'https://flagcdn.com/w80/es.png'
}

// 获取图片URL的工具函数
export function getImageUrl(key: string): string {
  return imageUrls[key as keyof typeof imageUrls] || imageUrls['image-error']
}

// 获取国家标志URL的工具函数
export function getFlagUrl(countryCode: string): string {
  const code = countryCode.toLowerCase()
  return flagUrls[code as keyof typeof flagUrls] || `https://flagcdn.com/w80/${code}.png`
}

// 产品图片映射（用于替换mockData中的路径）
export const productImageMapping = {
  '/images/products/robot-main.jpg': imageUrls['robot-main'],
  '/images/products/robot-detail-1.jpg': imageUrls['robot-detail-1'],
  '/images/products/robot-detail-2.jpg': imageUrls['robot-detail-2'],
  '/images/charity/teaching-01.jpg': imageUrls['charity-teaching'],
  '/images/charity/activity-01.jpg': imageUrls['charity-activity'],
  '/images/charity/equipment-01.jpg': imageUrls['charity-equipment'],
  '/images/charity/people-01.jpg': imageUrls['charity-people'],
  '/images/visit/visit_1_1.jpg': imageUrls['visit-1'],
  '/images/visit/visit_1_2.jpg': imageUrls['visit-2'],
  '/images/avatars/default-avatar.svg': imageUrls['default-avatar']
}