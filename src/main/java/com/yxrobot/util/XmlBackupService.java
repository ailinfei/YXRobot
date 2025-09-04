package com.yxrobot.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * XML文件备份服务
 * 为需要修复编码的XML文件创建安全备份
 */
public class XmlBackupService {
    
    private static final String BACKUP_DIR_NAME = "xml-backup";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    
    private final String projectPath;
    private final String backupBasePath;
    
    public XmlBackupService(String projectPath) {
        this.projectPath = projectPath;
        this.backupBasePath = projectPath + File.separator + BACKUP_DIR_NAME;
    }
    
    /**
     * 为单个XML文件创建备份
     * @param xmlFile 需要备份的XML文件
     * @return 备份文件的路径
     * @throws IOException 如果备份过程中出现IO错误
     */
    public String backupFile(File xmlFile) throws IOException {
        if (!xmlFile.exists()) {
            throw new IOException("源文件不存在: " + xmlFile.getAbsolutePath());
        }
        
        // 创建备份目录
        createBackupDirectory();
        
        // 生成备份文件名
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String originalName = xmlFile.getName();
        String backupFileName = originalName.replace(".xml", "_" + timestamp + ".xml.bak");
        
        // 构建备份文件路径
        Path backupPath = Paths.get(backupBasePath, backupFileName);
        
        // 执行文件复制
        Files.copy(xmlFile.toPath(), backupPath, StandardCopyOption.REPLACE_EXISTING);
        
        System.out.println("已备份文件: " + xmlFile.getName() + " -> " + backupPath.toString());
        
        return backupPath.toString();
    }
    
    /**
     * 批量备份多个XML文件
     * @param xmlFiles 需要备份的XML文件列表
     * @return 备份结果列表
     */
    public List<BackupResult> backupFiles(List<File> xmlFiles) {
        List<BackupResult> results = new ArrayList<>();
        
        for (File xmlFile : xmlFiles) {
            BackupResult result = new BackupResult();
            result.setOriginalFile(xmlFile.getAbsolutePath());
            
            try {
                String backupPath = backupFile(xmlFile);
                result.setBackupPath(backupPath);
                result.setSuccess(true);
                result.setMessage("备份成功");
            } catch (IOException e) {
                result.setSuccess(false);
                result.setMessage("备份失败: " + e.getMessage());
                System.err.println("备份文件失败: " + xmlFile.getName() + " - " + e.getMessage());
            }
            
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * 创建备份目录
     * @throws IOException 如果无法创建目录
     */
    private void createBackupDirectory() throws IOException {
        Path backupDir = Paths.get(backupBasePath);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
            System.out.println("创建备份目录: " + backupBasePath);
        }
    }
    
    /**
     * 验证备份文件是否存在且完整
     * @param originalFile 原始文件
     * @param backupPath 备份文件路径
     * @return 验证结果
     */
    public boolean validateBackup(File originalFile, String backupPath) {
        try {
            Path backup = Paths.get(backupPath);
            if (!Files.exists(backup)) {
                return false;
            }
            
            // 比较文件大小
            long originalSize = originalFile.length();
            long backupSize = Files.size(backup);
            
            return originalSize == backupSize;
        } catch (IOException e) {
            System.err.println("验证备份文件时出错: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取备份目录路径
     * @return 备份目录的完整路径
     */
    public String getBackupDirectory() {
        return backupBasePath;
    }
    
    /**
     * 备份结果类
     */
    public static class BackupResult {
        private String originalFile;
        private String backupPath;
        private boolean success;
        private String message;
        
        // Getters and Setters
        public String getOriginalFile() { return originalFile; }
        public void setOriginalFile(String originalFile) { this.originalFile = originalFile; }
        
        public String getBackupPath() { return backupPath; }
        public void setBackupPath(String backupPath) { this.backupPath = backupPath; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        @Override
        public String toString() {
            return String.format("BackupResult{原文件='%s', 备份路径='%s', 成功=%s, 消息='%s'}", 
                    originalFile, backupPath, success, message);
        }
    }
}