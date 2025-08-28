import { config } from '@vue/test-utils'
import { vi } from 'vitest'

// Mock Element Plus
config.global.stubs = {
  'el-table': true,
  'el-table-column': true,
  'el-button': true,
  'el-input': true,
  'el-select': true,
  'el-option': true,
  'el-form': true,
  'el-form-item': true,
  'el-dialog': true,
  'el-message': true,
  'el-message-box': true,
  'el-upload': true,
  'el-image': true,
  'el-tabs': true,
  'el-tab-pane': true
}

// Mock global properties
config.global.mocks = {
  $t: (key: string) => key,
  $router: {
    push: vi.fn(),
    replace: vi.fn(),
    go: vi.fn(),
    back: vi.fn()
  },
  $route: {
    path: '/',
    params: {},
    query: {},
    meta: {}
  }
}

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})

// Mock ResizeObserver
global.ResizeObserver = vi.fn().mockImplementation(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn(),
}))

// Mock IntersectionObserver
global.IntersectionObserver = vi.fn().mockImplementation(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn(),
}))