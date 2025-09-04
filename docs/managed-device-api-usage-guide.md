# 设备管理模块 API 使用指南

## 📋 概述

设备管理模块现在有专门的API调用服务 `managedDevice.ts`，提供完整的错误处理、类型安全和功能支持。

## 🔧 API文件说明

### 文件结构
```
src/frontend/src/api/
├── device.ts          # 原有文件，供其他模块使用（保持不变）
└── managedDevice.ts   # 设备管理专用API（新增，功能完整）
```

### 使用建议
- **设备管理模块**: 使用 `managedDevice.ts`
- **其他模块**: 继续使用 `device.ts`

## 🚀 快速开始

### 1. 导入API服务

```typescript
// 推荐方式：使用专门的managedDevice API
import { managedDeviceAPI } from '@/api/managedDevice'

// 或者使用默认导入
import managedDeviceAPI from '@/api/managedDevice'

// 兼容性导入（别名方式）
import { deviceAPI } from '@/api/managedDevice'
```

### 2. 基础使用示例

```typescript
import { managedDeviceAPI } from '@/api/managedDevice'
import { Device, DeviceQueryParams } from '@/types/device'

// 获取设备列表
const getDeviceList = async () => {
  try {
    const result = await managedDeviceAPI.getDevices({
      page: 1,
      pageSize: 20,
      keyword: '搜索关键词',
      status: 'online'
    })
    
    console.log('设备列表:', result.list)
    console.log('统计数据:', result.stats)
  } catch (error) {
    console.error('获取失败:', error)
  }
}

// 获取设备详情
const getDeviceDetail = async (deviceId: string) => {
  try {
    const device = await managedDeviceAPI.getDeviceById(deviceId)
    console.log('设备详情:', device)
  } catch (error) {
    console.error('获取设备详情失败:', error)
  }
}
```

## 📚 完整功能列表

### 基础CRUD操作
```typescript
// 获取设备列表
await managedDeviceAPI.getDevices(params)

// 获取设备详情
await managedDeviceAPI.getDeviceById(id)

// 根据序列号获取设备
await managedDeviceAPI.getDeviceBySerialNumber(serialNumber)

// 创建设备
await managedDeviceAPI.createDevice(deviceData)

// 更新设备
await managedDeviceAPI.updateDevice(id, deviceData)

// 删除设备
await managedDeviceAPI.deleteDevice(id)
```

### 设备操作
```typescript
// 更新设备状态
await managedDeviceAPI.updateDeviceStatus(id, 'maintenance')

// 重启设备
await managedDeviceAPI.rebootDevice(id)

// 激活设备
await managedDeviceAPI.activateDevice(id)

// 推送固件
await managedDeviceAPI.pushFirmware(id, '1.2.3')
```

### 批量操作
```typescript
// 批量推送固件
await managedDeviceAPI.batchPushFirmware(deviceIds, version)

// 批量重启设备
await managedDeviceAPI.batchRebootDevices(deviceIds)

// 批量删除设备
await managedDeviceAPI.batchDeleteDevices(deviceIds)
```

### 数据查询
```typescript
// 获取设备统计
await managedDeviceAPI.getDeviceStats()

// 获取设备日志
await managedDeviceAPI.getDeviceLogs(id, params)

// 获取客户选项
await managedDeviceAPI.getCustomerOptions()

// 导出设备数据
await managedDeviceAPI.exportDevices(params)
```

### 维护记录
```typescript
// 添加维护记录
await managedDeviceAPI.addMaintenanceRecord(deviceId, maintenanceData)

// 获取维护记录
await managedDeviceAPI.getMaintenanceRecords(deviceId, params)

// 更新维护记录
await managedDeviceAPI.updateMaintenanceRecord(deviceId, recordId, data)
```

## 🎯 在Vue组件中使用

### 设备列表组件示例
```typescript
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { managedDeviceAPI } from '@/api/managedDevice'
import { Device, DeviceStats, DeviceQueryParams } from '@/types/device'
import { ElMessage } from 'element-plus'

// 响应式数据
const devices = ref<Device[]>([])
const deviceStats = ref<DeviceStats>()
const loading = ref(false)
const searchParams = ref<DeviceQueryParams>({
  page: 1,
  pageSize: 20,
  keyword: '',
  status: '',
  model: ''
})

// 获取设备列表
const fetchDevices = async () => {
  loading.value = true
  try {
    const result = await managedDeviceAPI.getDevices(searchParams.value)
    devices.value = result.list
    deviceStats.value = result.stats
  } catch (error) {
    ElMessage.error('获取设备列表失败')
  } finally {
    loading.value = false
  }
}

// 设备操作
const handleDeviceOperation = async (deviceId: string, operation: string) => {
  try {
    switch (operation) {
      case 'reboot':
        await managedDeviceAPI.rebootDevice(deviceId)
        ElMessage.success('设备重启指令已发送')
        break
      case 'activate':
        await managedDeviceAPI.activateDevice(deviceId)
        ElMessage.success('设备激活成功')
        break
      case 'maintenance':
        await managedDeviceAPI.updateDeviceStatus(deviceId, 'maintenance')
        ElMessage.success('设备已进入维护模式')
        break
    }
    
    // 刷新列表
    await fetchDevices()
  } catch (error) {
    ElMessage.error(`设备${operation}操作失败`)
  }
}

// 批量操作
const handleBatchOperation = async (deviceIds: string[], operation: string) => {
  try {
    let result
    
    switch (operation) {
      case 'reboot':
        result = await managedDeviceAPI.batchRebootDevices(deviceIds)
        break
      case 'firmware':
        result = await managedDeviceAPI.batchPushFirmware(deviceIds)
        break
      case 'delete':
        result = await managedDeviceAPI.batchDeleteDevices(deviceIds)
        break
    }
    
    // 处理结果
    if (result.failureCount > 0) {
      ElMessage.warning(`操作完成：成功${result.successCount}个，失败${result.failureCount}个`)
    } else {
      ElMessage.success(`批量操作成功：${result.successCount}个设备`)
    }
    
    await fetchDevices()
  } catch (error) {
    ElMessage.error(`批量${operation}操作失败`)
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchDevices()
})
</script>
```

## ⚠️ 错误处理

### 自动错误处理
`managedDeviceAPI` 已经内置了完整的错误处理机制：

- 自动记录错误日志到控制台
- 统一的错误信息格式
- 网络异常自动处理
- 类型安全的异常抛出

### 组件层错误处理
```typescript
try {
  const result = await managedDeviceAPI.someOperation()
  // 处理成功结果
} catch (error) {
  // API层已经记录了详细错误，这里只需要用户提示
  ElMessage.error('操作失败，请稍后重试')
}
```

## 🔄 迁移指南

### 从原有device.ts迁移

如果你的设备管理组件目前使用的是 `device.ts`，可以按以下步骤迁移：

1. **更新导入语句**
```typescript
// 原来
import { deviceAPI } from '@/api/device'

// 改为
import { managedDeviceAPI } from '@/api/managedDevice'
// 或者使用别名保持兼容
import { deviceAPI } from '@/api/managedDevice'
```

2. **更新API调用**
```typescript
// 如果使用了别名，代码无需修改
await deviceAPI.getDevices(params)

// 如果使用新名称，更新调用
await managedDeviceAPI.getDevices(params)
```

3. **享受新功能**
- 更好的错误处理
- 更完整的类型安全
- 更多的API方法
- 更详细的错误日志

## 📞 技术支持

如果在使用过程中遇到问题：

1. 检查控制台错误日志（API层会自动记录）
2. 确认TypeScript类型定义是否正确
3. 验证API参数格式是否符合要求
4. 查看网络请求是否正常发送

---

**最后更新**: 2025年9月3日  
**版本**: 1.0.0