/**
 * 租赁数据分析导航测试
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

import { describe, it, expect } from 'vitest'
import { mainNavigationConfig, getBreadcrumb } from '@/config/navigation'

describe('租赁数据分析导航测试', () => {
  it('应该在业务管理菜单中包含租赁数据分析', () => {
    const businessMenu = mainNavigationConfig.find(item => item.path === '/admin/business')
    expect(businessMenu).toBeDefined()
    expect(businessMenu?.children).toBeDefined()
    
    const rentalAnalyticsMenu = businessMenu?.children?.find(
      child => child.path === '/admin/business/rental-analytics'
    )
    expect(rentalAnalyticsMenu).toBeDefined()
    expect(rentalAnalyticsMenu?.title).toBe('租赁数据分析')
    expect(rentalAnalyticsMenu?.permissions).toContain('rental:view')
  })

  it('应该正确配置面包屑导航', () => {
    const breadcrumb = getBreadcrumb('/admin/business/rental-analytics', 'admin')
    expect(breadcrumb).toHaveLength(3)
    expect(breadcrumb[0].title).toBe('控制台')
    expect(breadcrumb[0].path).toBe('/admin/dashboard')
    expect(breadcrumb[1].title).toBe('业务管理')
    expect(breadcrumb[1].path).toBe('/admin/business')
    expect(breadcrumb[2].title).toBe('租赁数据分析')
    expect(breadcrumb[2].path).toBe('/admin/business/rental-analytics')
  })

  it('应该具有正确的权限配置', () => {
    const businessMenu = mainNavigationConfig.find(item => item.path === '/admin/business')
    const rentalAnalyticsMenu = businessMenu?.children?.find(
      child => child.path === '/admin/business/rental-analytics'
    )
    
    expect(rentalAnalyticsMenu?.permissions).toEqual(['rental:view'])
  })

  it('应该使用正确的图标', () => {
    const businessMenu = mainNavigationConfig.find(item => item.path === '/admin/business')
    const rentalAnalyticsMenu = businessMenu?.children?.find(
      child => child.path === '/admin/business/rental-analytics'
    )
    
    expect(rentalAnalyticsMenu?.icon).toBeDefined()
  })
})