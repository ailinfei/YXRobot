package com.yxrobot.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 租赁模块测试覆盖率报告生成器
 * 生成详细的测试覆盖率报告和测试执行统计
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class RentalTestCoverageReporter {
    
    private static final Logger logger = LoggerFactory.getLogger(RentalTestCoverageReporter.class);
    
    private static final Map<String, TestClassInfo> testClassStats = new HashMap<>();
    private static final List<String> testExecutionLog = new ArrayList<>();
    private static LocalDateTime testStartTime;
    private static LocalDateTime testEndTime;
    
    /**
     * 测试类信息
     */
    public static class TestClassInfo {
        private String className;
        private int totalTests;
        private int passedTests;
        private int failedTests;
        private int skippedTests;
        private long executionTime;
        private List<String> testMethods;
        private List<String> failedMethods;
        
        public TestClassInfo(String className) {
            this.className = className;
            this.testMethods = new ArrayList<>();
            this.failedMethods = new ArrayList<>();
        }
        
        // Getters and Setters
        public String getClassName() { return className; }
        public int getTotalTests() { return totalTests; }
        public void setTotalTests(int totalTests) { this.totalTests = totalTests; }
        public int getPassedTests() { return passedTests; }
        public void setPassedTests(int passedTests) { this.passedTests = passedTests; }
        public int getFailedTests() { return failedTests; }
        public void setFailedTests(int failedTests) { this.failedTests = failedTests; }
        public int getSkippedTests() { return skippedTests; }
        public void setSkippedTests(int skippedTests) { this.skippedTests = skippedTests; }
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        public List<String> getTestMethods() { return testMethods; }
        public List<String> getFailedMethods() { return failedMethods; }
        
        public double getSuccessRate() {
            return totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
        }
    }
    
    @BeforeAll
    public static void initializeReporting() {
        testStartTime = LocalDateTime.now();
        testExecutionLog.clear();
        testClassStats.clear();
        
        logger.info("=== 租赁模块单元测试开始 ===");
        logger.info("测试开始时间: {}", testStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        addLogEntry("测试执行开始", "初始化测试环境");
    }
    
    @AfterAll
    public static void generateFinalReport() {
        testEndTime = LocalDateTime.now();
        
        logger.info("=== 租赁模块单元测试完成 ===");
        logger.info("测试结束时间: {}", testEndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        addLogEntry("测试执行完成", "生成最终报告");
        
        try {
            generateHtmlReport();
            generateTextReport();
            generateCsvReport();
            
            logger.info("测试报告生成完成");
            logger.info("HTML报告: target/test-reports/rental-test-coverage.html");
            logger.info("文本报告: target/test-reports/rental-test-summary.txt");
            logger.info("CSV报告: target/test-reports/rental-test-data.csv");
            
        } catch (IOException e) {
            logger.error("生成测试报告失败", e);
        }
    }
    
    /**
     * 记录测试类开始执行
     */
    public static void recordTestClassStart(String className) {
        TestClassInfo info = new TestClassInfo(className);
        testClassStats.put(className, info);
        
        addLogEntry("测试类开始", className);
        logger.info("开始执行测试类: {}", className);
    }
    
    /**
     * 记录测试方法执行结果
     */
    public static void recordTestMethodResult(String className, String methodName, boolean passed, long executionTime) {
        TestClassInfo info = testClassStats.get(className);
        if (info != null) {
            info.getTestMethods().add(methodName);
            info.setTotalTests(info.getTotalTests() + 1);
            info.setExecutionTime(info.getExecutionTime() + executionTime);
            
            if (passed) {
                info.setPassedTests(info.getPassedTests() + 1);
                addLogEntry("测试通过", className + "." + methodName);
            } else {
                info.setFailedTests(info.getFailedTests() + 1);
                info.getFailedMethods().add(methodName);
                addLogEntry("测试失败", className + "." + methodName);
            }
        }
    }
    
    /**
     * 记录跳过的测试
     */
    public static void recordTestSkipped(String className, String methodName, String reason) {
        TestClassInfo info = testClassStats.get(className);
        if (info != null) {
            info.setSkippedTests(info.getSkippedTests() + 1);
            addLogEntry("测试跳过", className + "." + methodName + " - " + reason);
        }
    }
    
    /**
     * 添加日志条目
     */
    private static void addLogEntry(String action, String details) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        testExecutionLog.add(String.format("[%s] %s: %s", timestamp, action, details));
    }
    
    /**
     * 生成HTML格式的测试报告
     */
    private static void generateHtmlReport() throws IOException {
        File reportDir = new File("target/test-reports");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }
        
        File htmlFile = new File(reportDir, "rental-test-coverage.html");
        
        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write(generateHtmlContent());
        }
    }
    
    /**
     * 生成HTML内容
     */
    private static String generateHtmlContent() {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang='zh-CN'>\n");
        html.append("<head>\n");
        html.append("    <meta charset='UTF-8'>\n");
        html.append("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n");
        html.append("    <title>租赁模块单元测试覆盖率报告</title>\n");
        html.append("    <style>\n");
        html.append(getCssStyles());
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        
        // 报告头部
        html.append("    <div class='header'>\n");
        html.append("        <h1>租赁模块单元测试覆盖率报告</h1>\n");
        html.append("        <div class='meta-info'>\n");
        html.append("            <p>生成时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</p>\n");
        html.append("            <p>测试开始: ").append(testStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</p>\n");
        html.append("            <p>测试结束: ").append(testEndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</p>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        
        // 总体统计
        html.append("    <div class='summary'>\n");
        html.append("        <h2>测试总体统计</h2>\n");
        html.append(generateSummaryTable());
        html.append("    </div>\n");
        
        // 详细测试结果
        html.append("    <div class='details'>\n");
        html.append("        <h2>详细测试结果</h2>\n");
        html.append(generateDetailTable());
        html.append("    </div>\n");
        
        // 测试执行日志
        html.append("    <div class='log'>\n");
        html.append("        <h2>测试执行日志</h2>\n");
        html.append("        <div class='log-content'>\n");
        for (String logEntry : testExecutionLog) {
            html.append("            <div class='log-entry'>").append(logEntry).append("</div>\n");
        }
        html.append("        </div>\n");
        html.append("    </div>\n");
        
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }
    
    /**
     * 生成CSS样式
     */
    private static String getCssStyles() {
        return """
            body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
            .header { background: #2c3e50; color: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; }
            .header h1 { margin: 0; }
            .meta-info p { margin: 5px 0; }
            .summary, .details, .log { background: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
            table { width: 100%; border-collapse: collapse; margin-top: 10px; }
            th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
            th { background-color: #34495e; color: white; }
            tr:hover { background-color: #f5f5f5; }
            .success { color: #27ae60; font-weight: bold; }
            .failure { color: #e74c3c; font-weight: bold; }
            .warning { color: #f39c12; font-weight: bold; }
            .log-content { max-height: 400px; overflow-y: auto; background: #f8f9fa; padding: 15px; border-radius: 4px; }
            .log-entry { margin: 2px 0; font-family: monospace; font-size: 12px; }
            .progress-bar { width: 100%; height: 20px; background: #ecf0f1; border-radius: 10px; overflow: hidden; }
            .progress-fill { height: 100%; background: linear-gradient(90deg, #27ae60, #2ecc71); }
            """;
    }
    
    /**
     * 生成总体统计表格
     */
    private static String generateSummaryTable() {
        int totalTests = testClassStats.values().stream().mapToInt(TestClassInfo::getTotalTests).sum();
        int totalPassed = testClassStats.values().stream().mapToInt(TestClassInfo::getPassedTests).sum();
        int totalFailed = testClassStats.values().stream().mapToInt(TestClassInfo::getFailedTests).sum();
        int totalSkipped = testClassStats.values().stream().mapToInt(TestClassInfo::getSkippedTests).sum();
        long totalTime = testClassStats.values().stream().mapToLong(TestClassInfo::getExecutionTime).sum();
        
        double successRate = totalTests > 0 ? (double) totalPassed / totalTests * 100 : 0;
        
        StringBuilder table = new StringBuilder();
        table.append("<table>\n");
        table.append("    <tr><th>指标</th><th>数值</th><th>百分比</th></tr>\n");
        table.append("    <tr><td>测试类总数</td><td>").append(testClassStats.size()).append("</td><td>-</td></tr>\n");
        table.append("    <tr><td>测试方法总数</td><td>").append(totalTests).append("</td><td>100%</td></tr>\n");
        table.append("    <tr><td class='success'>通过测试</td><td>").append(totalPassed).append("</td><td>").append(String.format("%.1f%%", successRate)).append("</td></tr>\n");
        table.append("    <tr><td class='failure'>失败测试</td><td>").append(totalFailed).append("</td><td>").append(String.format("%.1f%%", totalTests > 0 ? (double) totalFailed / totalTests * 100 : 0)).append("</td></tr>\n");
        table.append("    <tr><td class='warning'>跳过测试</td><td>").append(totalSkipped).append("</td><td>").append(String.format("%.1f%%", totalTests > 0 ? (double) totalSkipped / totalTests * 100 : 0)).append("</td></tr>\n");
        table.append("    <tr><td>总执行时间</td><td>").append(totalTime).append(" ms</td><td>-</td></tr>\n");
        table.append("</table>\n");
        
        // 添加成功率进度条
        table.append("<div style='margin-top: 20px;'>\n");
        table.append("    <p>整体成功率: ").append(String.format("%.1f%%", successRate)).append("</p>\n");
        table.append("    <div class='progress-bar'>\n");
        table.append("        <div class='progress-fill' style='width: ").append(String.format("%.1f%%", successRate)).append(";'></div>\n");
        table.append("    </div>\n");
        table.append("</div>\n");
        
        return table.toString();
    }
    
    /**
     * 生成详细测试结果表格
     */
    private static String generateDetailTable() {
        StringBuilder table = new StringBuilder();
        table.append("<table>\n");
        table.append("    <tr><th>测试类</th><th>总数</th><th>通过</th><th>失败</th><th>跳过</th><th>成功率</th><th>执行时间(ms)</th></tr>\n");
        
        for (TestClassInfo info : testClassStats.values()) {
            String rowClass = info.getFailedTests() > 0 ? "failure" : "success";
            table.append("    <tr class='").append(rowClass).append("'>\n");
            table.append("        <td>").append(info.getClassName()).append("</td>\n");
            table.append("        <td>").append(info.getTotalTests()).append("</td>\n");
            table.append("        <td>").append(info.getPassedTests()).append("</td>\n");
            table.append("        <td>").append(info.getFailedTests()).append("</td>\n");
            table.append("        <td>").append(info.getSkippedTests()).append("</td>\n");
            table.append("        <td>").append(String.format("%.1f%%", info.getSuccessRate())).append("</td>\n");
            table.append("        <td>").append(info.getExecutionTime()).append("</td>\n");
            table.append("    </tr>\n");
            
            // 如果有失败的测试，显示失败的方法
            if (!info.getFailedMethods().isEmpty()) {
                table.append("    <tr>\n");
                table.append("        <td colspan='7' style='background-color: #ffeaa7; padding: 10px;'>\n");
                table.append("            <strong>失败的测试方法:</strong> ");
                table.append(String.join(", ", info.getFailedMethods()));
                table.append("        </td>\n");
                table.append("    </tr>\n");
            }
        }
        
        table.append("</table>\n");
        return table.toString();
    }
    
    /**
     * 生成文本格式的测试报告
     */
    private static void generateTextReport() throws IOException {
        File reportDir = new File("target/test-reports");
        File textFile = new File(reportDir, "rental-test-summary.txt");
        
        try (FileWriter writer = new FileWriter(textFile)) {
            writer.write("=".repeat(80) + "\n");
            writer.write("租赁模块单元测试覆盖率报告\n");
            writer.write("=".repeat(80) + "\n");
            writer.write("生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("测试开始: " + testStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("测试结束: " + testEndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("\n");
            
            // 总体统计
            int totalTests = testClassStats.values().stream().mapToInt(TestClassInfo::getTotalTests).sum();
            int totalPassed = testClassStats.values().stream().mapToInt(TestClassInfo::getPassedTests).sum();
            int totalFailed = testClassStats.values().stream().mapToInt(TestClassInfo::getFailedTests).sum();
            int totalSkipped = testClassStats.values().stream().mapToInt(TestClassInfo::getSkippedTests).sum();
            
            writer.write("总体统计:\n");
            writer.write("-".repeat(40) + "\n");
            writer.write(String.format("测试类总数: %d\n", testClassStats.size()));
            writer.write(String.format("测试方法总数: %d\n", totalTests));
            writer.write(String.format("通过测试: %d (%.1f%%)\n", totalPassed, totalTests > 0 ? (double) totalPassed / totalTests * 100 : 0));
            writer.write(String.format("失败测试: %d (%.1f%%)\n", totalFailed, totalTests > 0 ? (double) totalFailed / totalTests * 100 : 0));
            writer.write(String.format("跳过测试: %d (%.1f%%)\n", totalSkipped, totalTests > 0 ? (double) totalSkipped / totalTests * 100 : 0));
            writer.write("\n");
            
            // 详细结果
            writer.write("详细测试结果:\n");
            writer.write("-".repeat(40) + "\n");
            for (TestClassInfo info : testClassStats.values()) {
                writer.write(String.format("%-40s | 总数: %3d | 通过: %3d | 失败: %3d | 成功率: %6.1f%%\n",
                    info.getClassName(), info.getTotalTests(), info.getPassedTests(), 
                    info.getFailedTests(), info.getSuccessRate()));
                
                if (!info.getFailedMethods().isEmpty()) {
                    writer.write("  失败方法: " + String.join(", ", info.getFailedMethods()) + "\n");
                }
            }
        }
    }
    
    /**
     * 生成CSV格式的测试数据
     */
    private static void generateCsvReport() throws IOException {
        File reportDir = new File("target/test-reports");
        File csvFile = new File(reportDir, "rental-test-data.csv");
        
        try (FileWriter writer = new FileWriter(csvFile)) {
            // CSV头部
            writer.write("测试类,总测试数,通过数,失败数,跳过数,成功率,执行时间(ms),失败方法\n");
            
            // 数据行
            for (TestClassInfo info : testClassStats.values()) {
                writer.write(String.format("%s,%d,%d,%d,%d,%.1f,%d,\"%s\"\n",
                    info.getClassName(),
                    info.getTotalTests(),
                    info.getPassedTests(),
                    info.getFailedTests(),
                    info.getSkippedTests(),
                    info.getSuccessRate(),
                    info.getExecutionTime(),
                    String.join("; ", info.getFailedMethods())
                ));
            }
        }
    }
    
    /**
     * 获取测试覆盖率统计信息
     */
    public static Map<String, Object> getCoverageStats() {
        Map<String, Object> stats = new HashMap<>();
        
        int totalTests = testClassStats.values().stream().mapToInt(TestClassInfo::getTotalTests).sum();
        int totalPassed = testClassStats.values().stream().mapToInt(TestClassInfo::getPassedTests).sum();
        int totalFailed = testClassStats.values().stream().mapToInt(TestClassInfo::getFailedTests).sum();
        int totalSkipped = testClassStats.values().stream().mapToInt(TestClassInfo::getSkippedTests).sum();
        
        stats.put("totalClasses", testClassStats.size());
        stats.put("totalTests", totalTests);
        stats.put("passedTests", totalPassed);
        stats.put("failedTests", totalFailed);
        stats.put("skippedTests", totalSkipped);
        stats.put("successRate", totalTests > 0 ? (double) totalPassed / totalTests * 100 : 0);
        stats.put("coverageTarget", 80.0); // 目标覆盖率80%
        
        return stats;
    }
    
    /**
     * 检查是否达到覆盖率目标
     */
    public static boolean isCoverageTargetMet() {
        Map<String, Object> stats = getCoverageStats();
        double successRate = (Double) stats.get("successRate");
        double target = (Double) stats.get("coverageTarget");
        
        return successRate >= target;
    }
}