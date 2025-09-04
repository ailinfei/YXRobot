package com.yxrobot.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * XML智能修复工具
 * 基于上下文智能修复替换字符
 */
public class XmlSmartFixer {
    
    // 智能替换映射 - 基于上下文模式
    private static final Map<String, String> CONTEXT_REPLACEMENTS = new HashMap<>();
    
    static {
        // 结果映射相关
        CONTEXT_REPLACEMENTS.put("结果映�?", "结果映射");
        CONTEXT_REPLACEMENTS.put("映�?", "映射");
        CONTEXT_REPLACEMENTS.put("�?", "射");
        
        // 查询相关
        CONTEXT_REPLACEMENTS.put("查询�?", "查询");
        CONTEXT_REPLACEMENTS.put("查询所有�?", "查询所有");
        CONTEXT_REPLACEMENTS.put("根据ID查询�?", "根据ID查询");
        CONTEXT_REPLACEMENTS.put("分页查询�?", "分页查询");
        CONTEXT_REPLACEMENTS.put("条件查询�?", "条件查询");
        
        // 插入相关
        CONTEXT_REPLACEMENTS.put("插入�?", "插入");
        CONTEXT_REPLACEMENTS.put("批量插入�?", "批量插入");
        CONTEXT_REPLACEMENTS.put("新增�?", "新增");
        
        // 更新相关
        CONTEXT_REPLACEMENTS.put("更新�?", "更新");
        CONTEXT_REPLACEMENTS.put("批量更新�?", "批量更新");
        CONTEXT_REPLACEMENTS.put("修改�?", "修改");
        
        // 删除相关
        CONTEXT_REPLACEMENTS.put("删除�?", "删除");
        CONTEXT_REPLACEMENTS.put("批量删除�?", "批量删除");
        CONTEXT_REPLACEMENTS.put("逻辑删除�?", "逻辑删除");
        
        // 统计相关
        CONTEXT_REPLACEMENTS.put("统计�?", "统计");
        CONTEXT_REPLACEMENTS.put("计数�?", "计数");
        CONTEXT_REPLACEMENTS.put("求和�?", "求和");
        
        // 实体相关
        CONTEXT_REPLACEMENTS.put("客户实体�?", "客户实体");
        CONTEXT_REPLACEMENTS.put("销售记录实体�?", "销售记录实体");
        CONTEXT_REPLACEMENTS.put("产品实体�?", "产品实体");
        CONTEXT_REPLACEMENTS.put("订单实体�?", "订单实体");
        
        // 业务相关
        CONTEXT_REPLACEMENTS.put("销售记录�?", "销售记录");
        CONTEXT_REPLACEMENTS.put("销售统计�?", "销售统计");
        CONTEXT_REPLACEMENTS.put("客户信息�?", "客户信息");
        CONTEXT_REPLACEMENTS.put("产品信息�?", "产品信息");
        CONTEXT_REPLACEMENTS.put("订单信息�?", "订单信息");
        
        // 慈善相关
        CONTEXT_REPLACEMENTS.put("慈善活动�?", "慈善活动");
        CONTEXT_REPLACEMENTS.put("慈善机构�?", "慈善机构");
        CONTEXT_REPLACEMENTS.put("慈善项目�?", "慈善项目");
        CONTEXT_REPLACEMENTS.put("慈善统计�?", "慈善统计");
        
        // 新闻相关
        CONTEXT_REPLACEMENTS.put("新闻信息�?", "新闻信息");
        CONTEXT_REPLACEMENTS.put("新闻分类�?", "新闻分类");
        CONTEXT_REPLACEMENTS.put("新闻标签�?", "新闻标签");
        CONTEXT_REPLACEMENTS.put("新闻状态�?", "新闻状态");
        
        // 租赁相关
        CONTEXT_REPLACEMENTS.put("租赁记录�?", "租赁记录");
        CONTEXT_REPLACEMENTS.put("租赁客户�?", "租赁客户");
        CONTEXT_REPLACEMENTS.put("租赁设备�?", "租赁设备");
        
        // 链接相关
        CONTEXT_REPLACEMENTS.put("链接信息�?", "链接信息");
        CONTEXT_REPLACEMENTS.put("平台链接�?", "平台链接");
        CONTEXT_REPLACEMENTS.put("链接点击�?", "链接点击");
        CONTEXT_REPLACEMENTS.put("链接验证�?", "链接验证");
        
        // 地区相关
        CONTEXT_REPLACEMENTS.put("地区配置�?", "地区配置");
        CONTEXT_REPLACEMENTS.put("区域信息�?", "区域信息");
    }
    
    /**
     * 智能修复单个XML文件
     * @param xmlFile XML文件
     * @return 修复结果
     */
    public FixResult smartFix(File xmlFile) {
        FixResult result = new FixResult();
        result.setFileName(xmlFile.getName());
        result.setFilePath(xmlFile.getAbsolutePath());
        
        try {
            // 读取文件内容
            String content = Files.readString(Paths.get(xmlFile.getAbsolutePath()), StandardCharsets.UTF_8);
            String originalContent = content;
            
            // 统计原始替换字符数量
            int originalCount = countReplacementChars(content);
            result.setOriginalReplacementCount(originalCount);
            
            if (originalCount == 0) {
                result.setSuccess(true);
                result.setMessage("文件无需修复");
                return result;
            }
            
            // 应用智能替换
            String fixedContent = applySmartReplacements(content);
            
            // 统计修复后的替换字符数量
            int finalCount = countReplacementChars(fixedContent);
            result.setFinalReplacementCount(finalCount);
            result.setFixedCount(originalCount - finalCount);
            
            // 写入修复后的内容
            Files.writeString(Paths.get(xmlFile.getAbsolutePath()), fixedContent, StandardCharsets.UTF_8);
            
            result.setSuccess(true);
            if (result.getFixedCount() > 0) {
                result.setMessage("成功修复 " + result.getFixedCount() + " 个替换字符");
                System.out.println("✓ 智能修复: " + xmlFile.getName() + 
                    " (修复了 " + result.getFixedCount() + " 个替换字符)");
            } else {
                result.setMessage("无法自动修复替换字符");
                System.out.println("⚠ " + xmlFile.getName() + " 包含 " + originalCount + " 个无法自动修复的替换字符");
            }
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("修复失败: " + e.getMessage());
            System.err.println("✗ 修复失败: " + xmlFile.getName() + " - " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 批量智能修复
     * @param xmlFiles 文件列表
     * @return 修复结果列表
     */
    public List<FixResult> smartFixMultiple(List<File> xmlFiles) {
        List<FixResult> results = new ArrayList<>();
        
        System.out.println("开始智能修复XML文件中的替换字符...");
        System.out.println("需要处理的文件数量: " + xmlFiles.size());
        System.out.println("========================================");
        
        for (File xmlFile : xmlFiles) {
            FixResult result = smartFix(xmlFile);
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * 应用智能替换
     * @param content 原始内容
     * @return 修复后的内容
     */
    private String applySmartReplacements(String content) {
        String result = content;
        
        // 按照长度从长到短排序，避免短模式覆盖长模式
        List<Map.Entry<String, String>> sortedEntries = new ArrayList<>(CONTEXT_REPLACEMENTS.entrySet());
        sortedEntries.sort((a, b) -> b.getKey().length() - a.getKey().length());
        
        for (Map.Entry<String, String> entry : sortedEntries) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        
        // 处理剩余的单独替换字符
        result = handleRemainingReplacements(result);
        
        return result;
    }
    
    /**
     * 处理剩余的替换字符
     * @param content 内容
     * @return 处理后的内容
     */
    private String handleRemainingReplacements(String content) {
        String result = content;
        
        // 在注释中的替换字符，根据上下文推断
        if (result.contains("<!-- ") && result.contains("�")) {
            // 处理注释中的替换字符
            result = result.replaceAll("<!-- ([^�]*?)�([^>]*?) -->", "<!-- $1射$2 -->");
        }
        
        // 在SQL语句中的替换字符
        if (result.contains("SELECT") || result.contains("INSERT") || result.contains("UPDATE") || result.contains("DELETE")) {
            // 在SQL上下文中，�通常是"射"
            result = result.replaceAll("([A-Za-z_]+)�", "$1射");
        }
        
        return result;
    }
    
    /**
     * 统计替换字符数量
     * @param content 内容
     * @return 替换字符数量
     */
    private int countReplacementChars(String content) {
        int count = 0;
        for (char c : content.toCharArray()) {
            if (c == '\uFFFD') { // Unicode替换字符
                count++;
            }
        }
        return count;
    }
    
    /**
     * 修复结果类
     */
    public static class FixResult {
        private String fileName;
        private String filePath;
        private boolean success;
        private String message;
        private int originalReplacementCount;
        private int finalReplacementCount;
        private int fixedCount;
        
        // Getters and Setters
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public int getOriginalReplacementCount() { return originalReplacementCount; }
        public void setOriginalReplacementCount(int originalReplacementCount) { 
            this.originalReplacementCount = originalReplacementCount; 
        }
        
        public int getFinalReplacementCount() { return finalReplacementCount; }
        public void setFinalReplacementCount(int finalReplacementCount) { 
            this.finalReplacementCount = finalReplacementCount; 
        }
        
        public int getFixedCount() { return fixedCount; }
        public void setFixedCount(int fixedCount) { this.fixedCount = fixedCount; }
        
        @Override
        public String toString() {
            return String.format("FixResult{文件='%s', 成功=%s, 原始=%d, 最终=%d, 修复=%d, 消息='%s'}", 
                    fileName, success, originalReplacementCount, finalReplacementCount, fixedCount, message);
        }
    }
}