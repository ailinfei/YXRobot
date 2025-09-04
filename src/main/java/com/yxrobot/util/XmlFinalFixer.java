package com.yxrobot.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * XML最终修复工具
 * 简单直接地修复所有替换字符
 */
public class XmlFinalFixer {
    
    /**
     * 修复单个XML文件中的所有替换字符
     * @param xmlFile XML文件
     * @return 修复结果
     */
    public FixResult fixFile(File xmlFile) {
        FixResult result = new FixResult();
        result.setFileName(xmlFile.getName());
        result.setFilePath(xmlFile.getAbsolutePath());
        
        try {
            // 读取文件内容
            byte[] bytes = Files.readAllBytes(Paths.get(xmlFile.getAbsolutePath()));
            String content = new String(bytes, StandardCharsets.UTF_8);
            
            // 统计原始替换字符数量
            int originalCount = countReplacementChars(content);
            result.setOriginalReplacementCount(originalCount);
            
            if (originalCount == 0) {
                result.setSuccess(true);
                result.setMessage("文件无需修复");
                return result;
            }
            
            // 简单粗暴地将所有替换字符替换为"射"
            String fixedContent = content.replace('\uFFFD', '射');
            
            // 统计修复后的替换字符数量
            int finalCount = countReplacementChars(fixedContent);
            result.setFinalReplacementCount(finalCount);
            result.setFixedCount(originalCount - finalCount);
            
            // 写入修复后的内容
            Files.writeString(Paths.get(xmlFile.getAbsolutePath()), fixedContent, StandardCharsets.UTF_8);
            
            result.setSuccess(true);
            result.setMessage("成功修复 " + result.getFixedCount() + " 个替换字符");
            
            System.out.println("✓ 修复: " + xmlFile.getName() + 
                " (修复了 " + result.getFixedCount() + " 个替换字符)");
            
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
        
        System.out.println("开始最终修复XML文件中的替换字符...");
        System.out.println("修复策略: 将所有替换字符(�)替换为'射'");
        System.out.println("需要处理的文件数量: " + xmlFiles.size());
        System.out.println("========================================");
        
        for (File xmlFile : xmlFiles) {
            FixResult result = fixFile(xmlFile);
            results.add(result);
        }
        
        return results;
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
            return String.format("FixResult{文件='%s', 成功=%s, 原始=%d, 修复=%d, 消息='%s'}", 
                    fileName, success, originalReplacementCount, fixedCount, message);
        }
    }
}