package com.yxrobot;

import com.yxrobot.controller.CustomerControllerTest;
import com.yxrobot.mapper.CustomerMapperTest;
import com.yxrobot.service.CustomerServiceTest;
import com.yxrobot.service.CustomerSearchOptimizationServiceTest;
import com.yxrobot.validation.CustomerValidationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * 客户管理模块测试套件
 * 包含所有客户相关的单元测试
 */
@Suite
@SuiteDisplayName("Customer Module Test Suite")
@SelectClasses({
    // 服务层测试
    CustomerServiceTest.class,
    CustomerSearchOptimizationServiceTest.class,
    
    // 控制器层测试
    CustomerControllerTest.class,
    
    // 数据访问层测试
    CustomerMapperTest.class,
    
    // 验证层测试
    CustomerValidationTest.class
})
public class CustomerModuleTestSuite {
    // 测试套件类，用于组织和运行所有客户模块相关的测试
}