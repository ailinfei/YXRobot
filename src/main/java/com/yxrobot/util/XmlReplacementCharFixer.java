package com.yxrobot.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XML替换字符修复工具
 * 专门处理XML文件中的替换字符（�）问题
 */
public class XmlReplacementCharFixer {
    
    // 常见的替换字符映射
    private static final Map<String, String> REPLACEMENT_MAP = new HashMap<>();
    
    static {
        // 常见的中文词汇映射
        REPLACEMENT_MAP.put("�?", "射");
        REPLACEMENT_MAP.put("映�?", "映射");
        REPLACEMENT_MAP.put("结果映�?", "结果映射");
        REPLACEMENT_MAP.put("销售记录结果映�?", "销售记录结果映射");
        REPLACEMENT_MAP.put("销售统计结果映�?", "销售统计结果映射");
        REPLACEMENT_MAP.put("客户实体结果映�?", "客户实体结果映射");
        REPLACEMENT_MAP.put("慈善活动结果映�?", "慈善活动结果映射");
        REPLACEMENT_MAP.put("慈善机构结果映�?", "慈善机构结果映射");
        REPLACEMENT_MAP.put("慈善项目结果映�?", "慈善项目结果映射");
        REPLACEMENT_MAP.put("新闻结果映�?", "新闻结果映射");
        REPLACEMENT_MAP.put("产品结果映�?", "产品结果映射");
        REPLACEMENT_MAP.put("租赁结果映�?", "租赁结果映射");
        
        // 其他常见替换
        REPLACEMENT_MAP.put("查询�?", "查询射");
        REPLACEMENT_MAP.put("插入�?", "插入射");
        REPLACEMENT_MAP.put("更新�?", "更新射");
        REPLACEMENT_MAP.put("删除�?", "删除射");
        
        // 更精确的映射
        REPLACEMENT_MAP.put("查询", "查询");
        REPLACEMENT_MAP.put("插入", "插入");
        REPLACEMENT_MAP.put("更新", "更新");
        REPLACEMENT_MAP.put("删除", "删除");
    }
    
    /**
     * 修复单个XML文件中的替换字符
     * @param xmlFile XML文件
     * @return 修复结果
     */
    public FixResult fixReplacementChars(File xmlFile) {
        FixResult result = new FixResult();
        result.setFileName(xmlFile.getName());
        result.setFilePath(xmlFile.getAbsolutePath());
        
        try {
            // 读取文件内容
            String content = Files.readString(Paths.get(xmlFile.getAbsolutePath()), StandardCharsets.UTF_8);
            String originalContent = content;
            
            // 统计替换字符数量
            int originalReplacementCount = countReplacementChars(content);
            result.setOriginalReplacementCount(originalReplacementCount);
            
            if (originalReplacementCount == 0) {
                result.setSuccess(true);
                result.setMessage("文件无需修复");
                return result;
            }
            
            // 应用替换映射
            String fixedContent = applyReplacements(content);
            
            // 统计修复后的替换字符数量
            int finalReplacementCount = countReplacementChars(fixedContent);
            result.setFinalReplacementCount(finalReplacementCount);
            result.setFixedCount(originalReplacementCount - finalReplacementCount);
            
            // 如果有改进，写入文件
            if (finalReplacementCount < originalReplacementCount) {
                Files.writeString(Paths.get(xmlFile.getAbsolutePath()), fixedContent, StandardCharsets.UTF_8);
                result.setSuccess(true);
                result.setMessage("部分替换字符已修复");
                System.out.println("✓ 修复: " + xmlFile.getName() + 
                    " (修复了 " + result.getFixedCount() + " 个替换字符)");
            } else {
                result.setSuccess(true);
                result.setMessage("无法自动修复替换字符");
                System.out.println("⚠ " + xmlFile.getName() + " 包含 " + originalReplacementCount + " 个替换字符，需要手动修复");
            }
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("修复失败: " + e.getMessage());
            System.err.println("✗ 修复失败: " + xmlFile.getName() + " - " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 批量修复多个文件
     * @param xmlFiles 文件列表
     * @return 修复结果列表
     */
    public List<FixResult> fixMultipleFiles(List<File> xmlFiles) {
        List<FixResult> results = new ArrayList<>();
        
        System.out.println("开始修复XML文件中的替换字符...");
        System.out.println("需要处理的文件数量: " + xmlFiles.size());
        System.out.println("----------------------------------------");
        
        for (File xmlFile : xmlFiles) {
            FixResult result = fixReplacementChars(xmlFile);
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * 统计替换字符数量
     * @param content 文件内容
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
     * 应用替换映射
     * @param content 原始内容
     * @return 修复后的内容
     */
    private String applyReplacements(String content) {
        String result = content;
        
        for (Map.Entry<String, String> entry : REPLACEMENT_MAP.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        
        return result;
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
            return String.format("FixResult{文件='%s', 成功=%s, 原始替换字符=%d, 最终替换字符=%d, 修复数量=%d, 消息='%s'}", 
                    fileName, success, originalReplacementCount, finalReplacementCount, fixedCount, message);
        }
    }
}