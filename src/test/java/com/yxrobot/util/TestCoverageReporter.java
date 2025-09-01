package com.yxrobot.util;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试覆盖率报告生成器
 * 用于生成详细的测试执行报告和覆盖率统计
 */
public class TestCoverageReporter implements TestWatcher {
    
    private static final Logger logger = LoggerFactory.getLogger(TestCoverageReporter.class);
    
    private static final Map<String, TestResult> testResults = new HashMap<>();
    private static final List<String> failedTests = new ArrayList<>();
    private static final List<String> passedTests = new ArrayList<>();
    private static final List<String> skippedTests = new ArrayList<>();
    
    private static class TestResult {
        String testClass;
        String testMethod;
        String status;
        String errorMessage;
        long executionTime;
        LocalDateTime timestamp;
        
        TestResult(String testClass, String testMethod, String status) {
            this.testClass = testClass;
            this.testMethod = testMethod;
            this.status = status;
            this.timestamp = LocalDateTime.now();
        }
    }
    
    @Override
    public void testSuccessful(ExtensionContext context) {
        String testKey = getTestKey(context);
        TestResult result = new TestResult(
            context.getTestClass().map(Class::getSimpleName).orElse("Unknown"),
            context.getTestMethod().map(m -> m.getName()).orElse("Unknown"),
            "PASSED"
        );
        
        testResults.put(testKey, result);
        passedTests.add(testKey);
        
        logger.debug("Test passed: {}", testKey);
    }
    
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String testKey = getTestKey(context);
        TestResult result = new TestResult(
            context.getTestClass().map(Class::getSimpleName).orElse("Unknown"),
            context.getTestMethod().map(m -> m.getName()).orElse("Unknown"),
            "FAILED"
        );
        result.errorMessage = cause.getMessage();
        
        testResults.put(testKey, result);
        failedTests.add(testKey);
        
        logger.error("Test failed: {} - {}", testKey, cause.getMessage());
    }
    
    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        String testKey = getTestKey(context);
        TestResult result = new TestResult(
            context.getTestClass().map(Class::getSimpleName).orElse("Unknown"),
            context.getTestMethod().map(m -> m.getName()).orElse("Unknown"),
            "ABORTED"
        );
        result.errorMessage = cause != null ? cause.getMessage() : "Test aborted";
        
        testResults.put(testKey, result);
        skippedTests.add(testKey);
        
        logger.warn("Test aborted: {} - {}", testKey, result.errorMessage);
    }
    
    @Override
    public void testDisabled(ExtensionContext context, java.util.Optional<String> reason) {
        String testKey = getTestKey(context);
        TestResult result = new TestResult(
            context.getTestClass().map(Class::getSimpleName).orElse("Unknown"),
            context.getTestMethod().map(m -> m.getName()).orElse("Unknown"),
            "DISABLED"
        );
        result.errorMessage = reason.orElse("Test disabled");
        
        testResults.put(testKey, result);
        skippedTests.add(testKey);
        
        logger.info("Test disabled: {} - {}", testKey, result.errorMessage);
    }
    
    private String getTestKey(ExtensionContext context) {
        String className = context.getTestClass().map(Class::getSimpleName).orElse("Unknown");
        String methodName = context.getTestMethod().map(m -> m.getName()).orElse("Unknown");
        return className + "." + methodName;
    }
    
    /**
     * 生成测试覆盖率报告
     */
    public static void generateCoverageReport() {
        try {
            String reportPath = "target/test-reports/customer-module-test-report.md";
            generateMarkdownReport(reportPath);
            
            String csvPath = "target/test-reports/customer-module-test-results.csv";
            generateCsvReport(csvPath);
            
            logger.info("Test coverage report generated: {}", reportPath);
            logger.info("Test results CSV generated: {}", csvPath);
            
        } catch (IOException e) {
            logger.error("Failed to generate test coverage report", e);
        }
    }
    
    private static void generateMarkdownReport(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("# 客户管理模块测试覆盖率报告\n\n");
            writer.write("生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
            
            // 测试统计概览
            writer.write("## 测试统计概览\n\n");
            int totalTests = testResults.size();
            int passedCount = passedTests.size();
            int failedCount = failedTests.size();
            int skippedCount = skippedTests.size();
            double passRate = totalTests > 0 ? (double) passedCount / totalTests * 100 : 0;
            
            writer.write("| 指标 | 数量 | 百分比 |\n");
            writer.write("|------|------|--------|\n");
            writer.write(String.format("| 总测试数 | %d | 100%% |\n", totalTests));
            writer.write(String.format("| 通过测试 | %d | %.2f%% |\n", passedCount, passRate));
            writer.write(String.format("| 失败测试 | %d | %.2f%% |\n", failedCount, totalTests > 0 ? (double) failedCount / totalTests * 100 : 0));
            writer.write(String.format("| 跳过测试 | %d | %.2f%% |\n", skippedCount, totalTests > 0 ? (double) skippedCount / totalTests * 100 : 0));
            writer.write("\n");
            
            // 测试覆盖率评估
            writer.write("## 测试覆盖率评估\n\n");
            if (passRate >= 80) {
                writer.write("✅ **测试覆盖率优秀** - 通过率达到 " + String.format("%.2f%%", passRate) + "\n\n");
            } else if (passRate >= 60) {
                writer.write("⚠️ **测试覆盖率良好** - 通过率为 " + String.format("%.2f%%", passRate) + "，建议提升至80%以上\n\n");
            } else {
                writer.write("❌ **测试覆盖率不足** - 通过率仅为 " + String.format("%.2f%%", passRate) + "，需要改进\n\n");
            }
            
            // 按测试类分组统计
            writer.write("## 按测试类分组统计\n\n");
            Map<String, TestClassStats> classStats = calculateClassStats();
            
            writer.write("| 测试类 | 总数 | 通过 | 失败 | 跳过 | 通过率 |\n");
            writer.write("|--------|------|------|------|------|--------|\n");
            
            for (Map.Entry<String, TestClassStats> entry : classStats.entrySet()) {
                TestClassStats stats = entry.getValue();
                double classPassRate = stats.total > 0 ? (double) stats.passed / stats.total * 100 : 0;
                writer.write(String.format("| %s | %d | %d | %d | %d | %.2f%% |\n",
                    entry.getKey(), stats.total, stats.passed, stats.failed, stats.skipped, classPassRate));
            }
            writer.write("\n");
            
            // 失败测试详情
            if (!failedTests.isEmpty()) {
                writer.write("## 失败测试详情\n\n");
                for (String testKey : failedTests) {
                    TestResult result = testResults.get(testKey);
                    writer.write(String.format("### ❌ %s\n", testKey));
                    writer.write(String.format("- **错误信息**: %s\n", result.errorMessage != null ? result.errorMessage : "无详细信息"));
                    writer.write(String.format("- **执行时间**: %s\n", result.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
                    writer.write("\n");
                }
            }
            
            // 测试建议
            writer.write("## 测试改进建议\n\n");
            if (failedCount > 0) {
                writer.write("1. **修复失败测试**: 优先修复 " + failedCount + " 个失败的测试用例\n");
            }
            if (passRate < 80) {
                writer.write("2. **提升覆盖率**: 当前通过率为 " + String.format("%.2f%%", passRate) + "，建议增加测试用例提升至80%以上\n");
            }
            if (skippedCount > 0) {
                writer.write("3. **启用跳过测试**: 检查并启用 " + skippedCount + " 个被跳过的测试用例\n");
            }
            writer.write("4. **性能测试**: 添加性能测试用例验证系统在高负载下的表现\n");
            writer.write("5. **集成测试**: 增加端到端集成测试确保各组件协同工作\n");
            writer.write("\n");
            
            // 测试质量指标
            writer.write("## 测试质量指标\n\n");
            writer.write("| 质量指标 | 当前值 | 目标值 | 状态 |\n");
            writer.write("|----------|--------|--------|------|\n");
            writer.write(String.format("| 测试通过率 | %.2f%% | ≥80%% | %s |\n", 
                passRate, passRate >= 80 ? "✅ 达标" : "❌ 未达标"));
            writer.write(String.format("| 测试用例数 | %d | ≥50 | %s |\n", 
                totalTests, totalTests >= 50 ? "✅ 达标" : "❌ 未达标"));
            writer.write(String.format("| 失败测试数 | %d | =0 | %s |\n", 
                failedCount, failedCount == 0 ? "✅ 达标" : "❌ 未达标"));
        }
    }
    
    private static void generateCsvReport(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // CSV头部
            writer.write("测试类,测试方法,状态,错误信息,执行时间\n");
            
            // 测试结果数据
            for (Map.Entry<String, TestResult> entry : testResults.entrySet()) {
                TestResult result = entry.getValue();
                writer.write(String.format("%s,%s,%s,%s,%s\n",
                    result.testClass,
                    result.testMethod,
                    result.status,
                    result.errorMessage != null ? result.errorMessage.replace(",", ";") : "",
                    result.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                ));
            }
        }
    }
    
    private static Map<String, TestClassStats> calculateClassStats() {
        Map<String, TestClassStats> classStats = new HashMap<>();
        
        for (TestResult result : testResults.values()) {
            TestClassStats stats = classStats.computeIfAbsent(result.testClass, k -> new TestClassStats());
            stats.total++;
            
            switch (result.status) {
                case "PASSED":
                    stats.passed++;
                    break;
                case "FAILED":
                    stats.failed++;
                    break;
                default:
                    stats.skipped++;
                    break;
            }
        }
        
        return classStats;
    }
    
    private static class TestClassStats {
        int total = 0;
        int passed = 0;
        int failed = 0;
        int skipped = 0;
    }
    
    /**
     * 获取测试统计信息
     */
    public static Map<String, Object> getTestStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTests", testResults.size());
        stats.put("passedTests", passedTests.size());
        stats.put("failedTests", failedTests.size());
        stats.put("skippedTests", skippedTests.size());
        stats.put("passRate", testResults.size() > 0 ? (double) passedTests.size() / testResults.size() * 100 : 0);
        return stats;
    }
    
    /**
     * 清理测试结果
     */
    public static void clearResults() {
        testResults.clear();
        passedTests.clear();
        failedTests.clear();
        skippedTests.clear();
    }
}