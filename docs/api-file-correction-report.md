# API 文件修正报告

## 问题描述

在之前的开发过程中，`device.ts` API 文件被错误地修改为适配 ManagedDevice 模块的需求。这是不正确的，因为：

1. `device.ts` 应该是给其他模块使用的通用设备 API
2. ManagedDevice 模块应该使用专门的 `managedDevice.ts` API 文件

## 修正内容

### 1. 恢复 device.ts 为通用设备 API

**文件路径**: `src/frontend/src/api/device.ts`

**修正内容**:
- 移除了所有 ManagedDevice 特定的功能
- 保留了通用的设备基础功能
- 添加了 `BasicDevice` 接口，用于其他模块的基础设备信息需求
- 提供了以下通用 API 方法：
  - `getBasicDevices()` - 获取基础设备列表
  - `getBasicDeviceInfo()` - 获取设备基础信息
  - `getBasicDeviceBySerialNumber()` - 根据序列号获取设备
  - `checkDeviceStatus()` - 检查设备状态

### 2. 保持 managedDevice.ts 专用于 ManagedDevice 模块

**文件路径**: `src/frontend/src/api/managedDevice.ts`

**内容**:
- 保持完整的 ManagedDevice 模块 API 功能
- 与后端 ManagedDeviceController 完全对应
- 包含所有设备管理、操作、日志、维护等功能

### 3. 更新设备组件的 API 引用

**修正的组件**:
- `DeviceFormDialog.vue`
- `DeviceDetailDialog.vue` 
- `DeviceLogsDialog.vue`

**修正内容**:
- 将 `import { deviceAPI } from '@/api/device'` 改为 `import { managedDeviceAPI } from '@/api/managedDevice'`
- 将所有 `deviceAPI.xxx()` 调用改为 `managedDeviceAPI.xxx()`

### 4. 修正导入语句

**问题**: 使用了错误的导入语法 `import { request } from '@/utils/request'`
**修正**: 改为 `import request from '@/utils/request'`

**影响的文件**:
- `device.ts`
- `managedDevice.ts`

## 文件职责划分

### device.ts (通用设备 API)
- **用途**: 供其他模块使用的基础设备功能
- **功能**: 设备选择器、基础信息查询、状态检查等
- **API 路径**: `/api/devices/basic/*`
- **使用场景**: 
  - 订单模块需要选择设备
  - 客户模块需要查看关联设备
  - 其他模块需要设备基础信息

### managedDevice.ts (ManagedDevice 模块专用)
- **用途**: ManagedDevice 模块的完整设备管理功能
- **功能**: 设备 CRUD、操作控制、日志管理、维护记录等
- **API 路径**: `/api/admin/devices/*`
- **使用场景**: 
  - 设备管理页面
  - 设备详情和操作
  - 设备日志查看
  - 维护记录管理

## 验证建议

1. **检查其他模块**: 确认是否有其他模块需要使用通用设备 API
2. **测试设备组件**: 验证 ManagedDevice 模块的设备组件功能正常
3. **API 路径确认**: 确保后端提供了对应的 API 端点
4. **类型定义**: 检查 `BasicDevice` 接口是否满足其他模块需求

## 后续建议

1. **文档更新**: 更新 API 使用文档，明确两个文件的使用场景
2. **代码规范**: 建立清晰的模块间 API 使用规范
3. **测试覆盖**: 为通用设备 API 添加单元测试
4. **监控使用**: 监控其他模块对通用设备 API 的使用情况

## 总结

通过这次修正，我们：
- 恢复了 `device.ts` 的通用性，使其能够服务于其他模块
- 保持了 `managedDevice.ts` 的专用性，专门服务于 ManagedDevice 模块
- 修正了组件中的 API 引用，确保功能正常
- 建立了清晰的文件职责划分

这样的架构更加合理，符合模块化开发的最佳实践。