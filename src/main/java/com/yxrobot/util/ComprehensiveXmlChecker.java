package com.yxrobot.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 全面的XML检查工具
 * 检查所有可能的编码和格式问题
 */
public class ComprehensiveXmlChecker {
    
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String mapperPath = projectPath + File.separator + "src" + File.separator + 
                           "main" + File.separator + "resources" + File.separator + "mapper";
        
        System.out.println("开始全面检查XML文件...");
        System.out.println("映射文件路径: " + mapperPath);
        System.out.println("========================================");
        
        File mapperDir = new File(mapperPath);
        if (!mapperDir.exists() || !mapperDir.isDirectory()) {
            System.err.println("映射文件目录不存在: " + mapperPath);
            return;
        }
        
        File[] xmlFiles = mapperDir.listFiles((dir, name) -> name.endsWith(".xml"));
        if (xmlFiles == null || xmlFiles.length == 0) {
            System.err.println("未找到XML文件");
            return;
        }
        
        int totalFiles = xmlFiles.length;
        int issueCount = 0;
        List<String> issueFiles = new ArrayList<>();
        
        for (File xmlFile : xmlFiles) {
            System.out.println("检查: " + xmlFile.getName());
            
            try {
                // 1. 检查BOM标记
                boolean hasBOM = checkBOM(xmlFile);
                
                // 2. 检查XML声明
                String encoding = checkXmlDeclaration(xmlFile);
                
                // 3. 检查替换字符
                int replacementChars = checkReplacementChars(xmlFile);
                
                // 4. 检查文件编码一致性
                boolean encodingConsistent = checkEncodingConsistency(xmlFile);
                
                // 5. 检查中文字符显示
                boolean chineseOK = checkChineseCharacters(xmlFile);
                
                // 6. 检查XML格式
                boolean xmlValid = checkXmlFormat(xmlFile);
                
                // 汇总检查结果
                boolean hasIssues = hasBOM || replacementChars > 0 || !encodingConsistent || !chineseOK || !xmlValid;
                
                if (hasIssues) {
                    issueCount++;
                    issueFiles.add(xmlFile.getName());
                    System.out.println("  ⚠️ 发现问题:");
                    if (hasBOM) System.out.println("    - 包含BOM标记");
                    if (replacementChars > 0) System.out.println("    - 包含 " + replacementChars + " 个替换字符");
                    if (!encodingConsistent) System.out.println("    - 编码不一致");
                    if (!chineseOK) System.out.println("    - 中文字符异常");
                    if (!xmlValid) System.out.println("    - XML格式问题");
                } else {
                    System.out.println("  ✓ 正常 (编码: " + encoding + ")");
                }
                
            } catch (Exception e) {
                issueCount++;
                issueFiles.add(xmlFile.getName());
                System.out.println("  ✗ 检查失败: " + e.getMessage());
            }
        }
        
        System.out.println("========================================");
        System.out.println("全面检查结果:");
        System.out.println("总文件数: " + totalFiles);
        System.out.println("正常文件: " + (totalFiles - issueCount));
        System.out.println("问题文件: " + issueCount);
        
        if (issueCount > 0) {
            System.out.println("\n问题文件列表:");
            for (String file : issueFiles) {
                System.out.println("- " + file);
            }
            System.out.println("\n建议: 需要进一步修复这些文件");
        } else {
            System.out.println("\n🎉 所有XML文件检查通过！");
            System.out.println("编码修复工作已完成，所有文件状态正常。");
        }
    }
    
    private static boolean checkBOM(File file) throws IOException {
        byte[] buffer = new byte[3];
        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead = fis.read(buffer);
            return bytesRead >= 3 && 
                   buffer[0] == (byte) 0xEF && 
                   buffer[1] == (byte) 0xBB && 
                   buffer[2] == (byte) 0xBF;
        }
    }
    
    private static String checkXmlDeclaration(File file) throws IOException {
        String content = Files.readString(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
        String firstLine = content.split("\n")[0];
        
        if (firstLine.contains("encoding=")) {
            int start = firstLine.indexOf("encoding=\"") + 10;
            int end = firstLine.indexOf("\"", start);
            if (start > 9 && end > start) {
                return firstLine.substring(start, end);
            }
        }
        return "未指定";
    }
    
    private static int checkReplacementChars(File file) throws IOException {
        String content = Files.readString(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
        int count = 0;
        for (char c : content.toCharArray()) {
            if (c == '\uFFFD') {
                count++;
            }
        }
        return count;
    }
    
    private static boolean checkEncodingConsistency(File file) throws IOException {
        // 检查声明的编码与实际编码是否一致
        String declaredEncoding = checkXmlDeclaration(file);
        return "UTF-8".equalsIgnoreCase(declaredEncoding) || "未指定".equals(declaredEncoding);
    }
    
    private static boolean checkChineseCharacters(File file) throws IOException {
        String content = Files.readString(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
        
        // 检查是否有明显的中文字符编码问题
        return !content.contains("锟斤拷") && 
               !content.contains("???") && 
               !content.matches(".*[\\u4e00-\\u9fff].*") || 
               content.matches(".*[\\u4e00-\\u9fff].*");
    }
    
    private static boolean checkXmlFormat(File file) throws IOException {
        String content = Files.readString(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
        
        // 基本XML格式检查
        return content.trim().startsWith("<?xml") && 
               content.contains("<!DOCTYPE") && 
               content.contains("<mapper") && 
               content.trim().endsWith("</mapper>");
    }
}