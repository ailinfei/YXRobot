package com.yxrobot.integration;

import com.yxrobot.validator.OrderValidationIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * 订单管理系统集成测试套件
 * 包含所有系统集成测试和部署验证测试
 */
@Suite
@SuiteDisplayName("订单管理系统完整集成测试套件")
@SelectClasses({
    // 基础API集成测试
    OrderCrudApiIntegrationTest.class,
    OrderListApiIntegrationTest.class,
    OrderStatusApiIntegrationTest.class,
    OrderStatsApiIntegrationTest.class,
    
    // 系统功能集成测试
    OrderSystemIntegrationTest.class,
    OrderSystemComprehensiveIntegrationTest.class,
    
    // 部署验证测试
    OrderSystemDeploymentValidationTest.class,
    
    // 数据验证集成测试
    OrderValidationIntegrationTest.class
})
public class OrderSystemIntegrationTestSuite {
    
    /**
     * 测试套件说明：
     * 
     * 1. OrderCrudApiIntegrationTest - 订单CRUD操作API测试
     * 2. OrderListApiIntegrationTest - 订单列表查询API测试
     * 3. OrderStatusApiIntegrationTest - 订单状态管理API测试
     * 4. OrderStatsApiIntegrationTest - 订单统计API测试
     * 5. OrderSystemIntegrationTest - 系统基础集成测试
     * 6. OrderSystemComprehensiveIntegrationTest - 系统全面集成测试
     * 7. OrderSystemDeploymentValidationTest - 部署验证测试
     * 8. OrderValidationIntegrationTest - 数据验证集成测试
     * 
     * 运行方式：
     * - 在IDE中右键运行此测试套件
     * - 使用Maven命令：mvn test -Dtest=OrderSystemIntegrationTestSuite
     * - 使用Gradle命令：./gradlew test --tests OrderSystemIntegrationTestSuite
     */
}