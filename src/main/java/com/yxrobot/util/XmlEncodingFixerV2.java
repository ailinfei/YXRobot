package com.yxrobot.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * XML编码修复工具 V2
 * 改进版本，正确处理编码检测和转换
 */
public class XmlEncodingFixerV2 {
    
    /**
     * 从备份恢复并正确修复编码
     * @param originalFile 原始文件
     * @param backupFile 备份文件
     * @return 修复结果
     */
    public FixResult restoreAndFix(File originalFile, File backupFile) {
        FixResult result = new FixResult();
        result.setFileName(originalFile.getName());
        result.setFilePath(originalFile.getAbsolutePath());
        
        try {
            // 1. 从备份文件读取原始内容（使用UTF-8）
            String content = Files.readString(Paths.get(backupFile.getAbsolutePath()), StandardCharsets.UTF_8);
            
            // 2. 检查是否包含替换字符
            boolean hasReplacementChars = content.contains("�");
            result.setHasReplacementChars(hasReplacementChars);
            
            // 3. 确保XML声明正确
            String fixedContent = ensureCorrectXmlDeclaration(content);
            
            // 4. 写回原文件（使用UTF-8编码，无BOM）
            Files.writeString(Paths.get(originalFile.getAbsolutePath()), fixedContent, StandardCharsets.UTF_8);
            
            result.setSuccess(true);
            result.setNewEncoding("UTF-8");
            result.setMessage("从备份恢复并修复成功");
            
            System.out.println("✓ 恢复修复: " + originalFile.getName() + 
                (hasReplacementChars ? " (包含替换字符)" : " (正常)"));
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("恢复修复失败: " + e.getMessage());
            System.err.println("✗ 恢复修复失败: " + originalFile.getName() + " - " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 批量恢复和修复文件
     * @param fileMapping 原文件到备份文件的映射
     * @return 修复结果列表
     */
    public List<FixResult> restoreAndFixMultiple(List<FileMapping> fileMapping) {
        List<FixResult> results = new ArrayList<>();
        
        System.out.println("开始从备份恢复并修复XML文件...");
        System.out.println("需要处理的文件数量: " + fileMapping.size());
        System.out.println("----------------------------------------");
        
        for (FileMapping mapping : fileMapping) {
            FixResult result = restoreAndFix(mapping.getOriginalFile(), mapping.getBackupFile());
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * 确保XML声明正确
     * @param content 原始内容
     * @return 修复后的内容
     */
    private String ensureCorrectXmlDeclaration(String content) {
        // 如果已经有正确的UTF-8声明，直接返回
        if (content.trim().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
            return content;
        }
        
        // 查找现有的XML声明并替换
        if (content.trim().startsWith("<?xml")) {
            int endIndex = content.indexOf("?>");
            if (endIndex != -1) {
                String afterDeclaration = content.substring(endIndex + 2);
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + afterDeclaration;
            }
        }
        
        // 如果没有XML声明，添加一个
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + content;
    }
    
    /**
     * 文件映射类
     */
    public static class FileMapping {
        private File originalFile;
        private File backupFile;
        
        public FileMapping(File originalFile, File backupFile) {
            this.originalFile = originalFile;
            this.backupFile = backupFile;
        }
        
        public File getOriginalFile() { return originalFile; }
        public File getBackupFile() { return backupFile; }
    }
    
    /**
     * 修复结果类
     */
    public static class FixResult {
        private String fileName;
        private String filePath;
        private String newEncoding;
        private boolean success;
        private String message;
        private boolean hasReplacementChars;
        
        // Getters and Setters
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public String getNewEncoding() { return newEncoding; }
        public void setNewEncoding(String newEncoding) { this.newEncoding = newEncoding; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public boolean isHasReplacementChars() { return hasReplacementChars; }
        public void setHasReplacementChars(boolean hasReplacementChars) { this.hasReplacementChars = hasReplacementChars; }
        
        @Override
        public String toString() {
            return String.format("FixResult{文件='%s', 编码='%s', 成功=%s, 替换字符=%s, 消息='%s'}", 
                    fileName, newEncoding, success, hasReplacementChars, message);
        }
    }
}