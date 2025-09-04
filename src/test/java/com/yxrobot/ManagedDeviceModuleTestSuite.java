package com.yxrobot;

import com.yxrobot.controller.ManagedDeviceControllerTest;
import com.yxrobot.mapper.ManagedDeviceMapperTest;
import com.yxrobot.service.ManagedDeviceServiceTest;
import com.yxrobot.service.ManagedDeviceOperationServiceTest;
import com.yxrobot.service.ManagedDeviceLogServiceTest;
import com.yxrobot.validation.DeviceValidationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * 设备管理模块单元测试套件
 * 
 * 包含的测试类：
 * - ManagedDeviceServiceTest - 设备管理服务测试
 * - ManagedDeviceControllerTest - 设备管理控制器测试
 * - ManagedDeviceMapperTest - 设备管理数据访问层测试
 * - DeviceValidationTest - 设备数据验证测试
 * - ManagedDeviceOperationServiceTest - 设备操作服务测试
 * - ManagedDeviceLogServiceTest - 设备日志服务测试
 * 
 * 测试覆盖率目标：
 * - 行覆盖率 ≥ 80%
 * - 分支覆盖率 ≥ 75%
 * - 方法覆盖率 ≥ 85%
 * - 类覆盖率 ≥ 90%
 * 
 * 运行方式：
 * mvn test -Dtest=ManagedDeviceModuleTestSuite
 */
@Suite
@SelectClasses({
    ManagedDeviceServiceTest.class,
    ManagedDeviceControllerTest.class,
    ManagedDeviceMapperTest.class,
    DeviceValidationTest.class,
    ManagedDeviceOperationServiceTest.class,
    ManagedDeviceLogServiceTest.class
})
@DisplayName("设备管理模块单元测试套件")
public class ManagedDeviceModuleTestSuite {
    
    // 测试套件类，用于组织和运行所有设备管理模块的单元测试
    // 该类本身不包含测试方法，仅用于配置测试套件
    
    /**
     * 测试覆盖范围说明：
     * 
     * 1. 业务逻辑层测试 (ManagedDeviceServiceTest)
     *    - 设备CRUD操作
     *    - 数据转换和验证
     *    - 异常处理
     *    - 边界条件测试
     * 
     * 2. 控制器层测试 (ManagedDeviceControllerTest)
     *    - RESTful API接口
     *    - 请求参数验证
     *    - 响应格式验证
     *    - HTTP状态码验证
     * 
     * 3. 数据访问层测试 (ManagedDeviceMapperTest)
     *    - 数据库CRUD操作
     *    - 复杂查询和筛选
     *    - 分页查询
     *    - 字段映射验证
     * 
     * 4. 数据验证测试 (DeviceValidationTest)
     *    - 数据格式验证
     *    - 业务规则验证
     *    - 安全验证（XSS、SQL注入）
     *    - PII数据保护
     * 
     * 5. 设备操作服务测试 (ManagedDeviceOperationServiceTest)
     *    - 设备状态管理
     *    - 设备控制操作
     *    - 批量操作功能
     *    - 权限验证
     * 
     * 6. 设备日志服务测试 (ManagedDeviceLogServiceTest)
     *    - 日志查询和筛选
     *    - 日志创建和管理
     *    - 日志统计功能
     *    - 日志清理和导出
     */
}