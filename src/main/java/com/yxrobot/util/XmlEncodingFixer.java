package com.yxrobot.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XML编码修复工具
 * 修复XML文件的编码问题，确保使用正确的UTF-8编码
 */
public class XmlEncodingFixer {
    
    private static final Pattern XML_DECLARATION_PATTERN = 
        Pattern.compile("(<\\?xml[^>]*encoding\\s*=\\s*[\"'])([^\"']+)([\"'][^>]*\\?>)");
    
    private static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    
    /**
     * 修复单个XML文件的编码
     * @param xmlFile 需要修复的XML文件
     * @return 修复结果
     */
    public FixResult fixFileEncoding(File xmlFile) {
        FixResult result = new FixResult();
        result.setFileName(xmlFile.getName());
        result.setFilePath(xmlFile.getAbsolutePath());
        
        try {
            // 1. 检测当前编码
            String detectedEncoding = detectFileEncoding(xmlFile);
            result.setOriginalEncoding(detectedEncoding);
            
            // 2. 读取文件内容
            String content = readFileWithEncoding(xmlFile, detectedEncoding);
            
            // 3. 修复XML声明中的编码
            String fixedContent = fixXmlDeclaration(content);
            
            // 4. 移除BOM标记（如果存在）
            fixedContent = removeBOM(fixedContent);
            
            // 5. 写入修复后的内容（使用UTF-8编码）
            writeFileAsUTF8(xmlFile, fixedContent);
            
            result.setSuccess(true);
            result.setNewEncoding("UTF-8");
            result.setMessage("编码修复成功");
            
            System.out.println("✓ 修复完成: " + xmlFile.getName() + " (" + detectedEncoding + " -> UTF-8)");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("修复失败: " + e.getMessage());
            System.err.println("✗ 修复失败: " + xmlFile.getName() + " - " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 批量修复多个XML文件
     * @param xmlFiles 需要修复的文件列表
     * @return 修复结果列表
     */
    public List<FixResult> fixMultipleFiles(List<File> xmlFiles) {
        List<FixResult> results = new ArrayList<>();
        
        System.out.println("开始批量修复XML文件编码...");
        System.out.println("需要修复的文件数量: " + xmlFiles.size());
        System.out.println("----------------------------------------");
        
        for (File xmlFile : xmlFiles) {
            FixResult result = fixFileEncoding(xmlFile);
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * 检测文件编码
     * @param file 文件
     * @return 检测到的编码名称
     */
    private String detectFileEncoding(File file) throws IOException {
        // 读取文件前几个字节检测BOM
        byte[] buffer = new byte[4];
        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead = fis.read(buffer);
            
            // 检测UTF-8 BOM
            if (bytesRead >= 3 && 
                buffer[0] == (byte) 0xEF && 
                buffer[1] == (byte) 0xBB && 
                buffer[2] == (byte) 0xBF) {
                return "UTF-8";
            }
        }
        
        // 尝试不同编码读取文件开头
        String[] encodings = {"UTF-8", "ISO-8859-1", "GBK", "GB2312"};
        
        for (String encoding : encodings) {
            try {
                String content = readFileWithEncoding(file, encoding);
                if (content != null && !content.contains("�")) {
                    return encoding;
                }
            } catch (Exception e) {
                // 继续尝试下一个编码
            }
        }
        
        // 默认返回UTF-8
        return "UTF-8";
    }
    
    /**
     * 使用指定编码读取文件
     * @param file 文件
     * @param encoding 编码
     * @return 文件内容
     */
    private String readFileWithEncoding(File file, String encoding) throws IOException {
        Charset charset;
        try {
            charset = Charset.forName(encoding);
        } catch (Exception e) {
            charset = StandardCharsets.UTF_8;
        }
        
        Path path = Paths.get(file.getAbsolutePath());
        byte[] bytes = Files.readAllBytes(path);
        
        // 如果是UTF-8且有BOM，跳过BOM
        if ("UTF-8".equals(encoding) && bytes.length >= 3 &&
            bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
            return new String(bytes, 3, bytes.length - 3, charset);
        }
        
        return new String(bytes, charset);
    }
    
    /**
     * 修复XML声明中的编码属性
     * @param content 原始内容
     * @return 修复后的内容
     */
    private String fixXmlDeclaration(String content) {
        Matcher matcher = XML_DECLARATION_PATTERN.matcher(content);
        
        if (matcher.find()) {
            // 替换编码为UTF-8
            String replacement = matcher.group(1) + "UTF-8" + matcher.group(3);
            return matcher.replaceFirst(replacement);
        } else {
            // 如果没有XML声明，添加一个
            if (!content.trim().startsWith("<?xml")) {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + content;
            }
        }
        
        return content;
    }
    
    /**
     * 移除BOM标记
     * @param content 内容
     * @return 移除BOM后的内容
     */
    private String removeBOM(String content) {
        if (content.length() > 0 && content.charAt(0) == '\uFEFF') {
            return content.substring(1);
        }
        return content;
    }
    
    /**
     * 将内容写入文件（使用UTF-8编码）
     * @param file 目标文件
     * @param content 内容
     */
    private void writeFileAsUTF8(File file, String content) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }
    
    /**
     * 修复结果类
     */
    public static class FixResult {
        private String fileName;
        private String filePath;
        private String originalEncoding;
        private String newEncoding;
        private boolean success;
        private String message;
        
        // Getters and Setters
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public String getOriginalEncoding() { return originalEncoding; }
        public void setOriginalEncoding(String originalEncoding) { this.originalEncoding = originalEncoding; }
        
        public String getNewEncoding() { return newEncoding; }
        public void setNewEncoding(String newEncoding) { this.newEncoding = newEncoding; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        @Override
        public String toString() {
            return String.format("FixResult{文件='%s', 原编码='%s', 新编码='%s', 成功=%s, 消息='%s'}", 
                    fileName, originalEncoding, newEncoding, success, message);
        }
    }
}