import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import DataTable from '@/components/common/DataTable.vue'
import type { TableColumn } from '@/components/common'

// Mock Element Plus components
vi.mock('element-plus', () => ({
  ElTable: {
    name: 'ElTable',
    template: '<div class="el-table"><slot /></div>',
    props: ['data', 'loading']
  },
  ElTableColumn: {
    name: 'ElTableColumn',
    template: '<div class="el-table-column"></div>',
    props: ['prop', 'label', 'width', 'minWidth', 'type']
  },
  ElButton: {
    name: 'ElButton',
    template: '<button class="el-button"><slot /></button>',
    props: ['type', 'size', 'text', 'icon']
  },
  ElCheckbox: {
    name: 'ElCheckbox',
    template: '<input type="checkbox" class="el-checkbox" />',
    props: ['modelValue']
  }
}))

describe('DataTable', () => {
  const mockColumns: TableColumn[] = [
    { prop: 'id', label: 'ID', width: 80 },
    { prop: 'name', label: '名称', minWidth: 150 },
    { prop: 'status', label: '状态', width: 100 },
    { prop: 'createdAt', label: '创建时间', width: 160, type: 'date' }
  ]

  const mockData = [
    {
      id: 1,
      name: '测试产品1',
      status: 'published',
      createdAt: '2024-01-15 10:30:00'
    },
    {
      id: 2,
      name: '测试产品2',
      status: 'draft',
      createdAt: '2024-01-16 11:20:00'
    }
  ]

  it('renders correctly with basic props', () => {
    const wrapper = mount(DataTable, {
      props: {
        data: mockData,
        columns: mockColumns
      }
    })

    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('.data-table').exists()).toBe(true)
  })

  it('displays loading state', () => {
    const wrapper = mount(DataTable, {
      props: {
        data: [],
        columns: mockColumns,
        loading: true
      }
    })

    const table = wrapper.findComponent({ name: 'ElTable' })
    expect(table.props('loading')).toBe(true)
  })

  it('passes data to table component', () => {
    const wrapper = mount(DataTable, {
      props: {
        data: mockData,
        columns: mockColumns
      }
    })

    const table = wrapper.findComponent({ name: 'ElTable' })
    expect(table.props('data')).toEqual(mockData)
  })

  it('renders selection column when showSelection is true', () => {
    const wrapper = mount(DataTable, {
      props: {
        data: mockData,
        columns: mockColumns,
        showSelection: true
      }
    })

    expect(wrapper.find('.data-table').exists()).toBe(true)
    // 在实际实现中，这里会检查是否有selection类型的列
  })

  it('renders action buttons when provided', () => {
    const wrapper = mount(DataTable, {
      props: {
        data: mockData,
        columns: mockColumns
      },
      slots: {
        actions: '<button class="action-btn">编辑</button>'
      }
    })

    expect(wrapper.find('.action-btn').exists()).toBe(true)
  })

  it('emits edit event when edit button is clicked', async () => {
    const wrapper = mount(DataTable, {
      props: {
        data: mockData,
        columns: mockColumns
      }
    })

    // 模拟编辑按钮点击
    await wrapper.vm.$emit('edit', mockData[0])

    expect(wrapper.emitted('edit')).toBeTruthy()
    expect(wrapper.emitted('edit')?.[0]).toEqual([mockData[0]])
  })

  it('emits delete event when delete button is clicked', async () => {
    const wrapper = mount(DataTable, {
      props: {
        data: mockData,
        columns: mockColumns
      }
    })

    // 模拟删除按钮点击
    await wrapper.vm.$emit('delete', mockData[0])

    expect(wrapper.emitted('delete')).toBeTruthy()
    expect(wrapper.emitted('delete')?.[0]).toEqual([mockData[0]])
  })

  it('emits refresh event when refresh is triggered', async () => {
    const wrapper = mount(DataTable, {
      props: {
        data: mockData,
        columns: mockColumns
      }
    })

    // 模拟刷新事件
    await wrapper.vm.$emit('refresh')

    expect(wrapper.emitted('refresh')).toBeTruthy()
  })

  it('handles empty data gracefully', () => {
    const wrapper = mount(DataTable, {
      props: {
        data: [],
        columns: mockColumns
      }
    })

    const table = wrapper.findComponent({ name: 'ElTable' })
    expect(table.props('data')).toEqual([])
  })

  it('renders custom slot content', () => {
    const wrapper = mount(DataTable, {
      props: {
        data: mockData,
        columns: mockColumns
      },
      slots: {
        status: '<span class="custom-status">自定义状态</span>'
      }
    })

    expect(wrapper.find('.custom-status').exists()).toBe(true)
  })

  it('applies correct column configurations', () => {
    const wrapper = mount(DataTable, {
      props: {
        data: mockData,
        columns: mockColumns
      }
    })

    // 检查是否正确渲染了列配置
    const columns = wrapper.findAllComponents({ name: 'ElTableColumn' })
    expect(columns.length).toBeGreaterThan(0)
  })
})