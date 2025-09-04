package com.yxrobot.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XML编码扫描器 - 扫描和检查XML映射文件的编码问题
 */
public class XmlEncodingScanner {
    
    private static final String MAPPER_DIR = "src/main/resources/mapper";
    private static final Pattern XML_ENCODING_PATTERN = 
        Pattern.compile("<?xml\\s+version\\s*=\\s*[\"'][^\"']*[\"']\\s+encoding\\s*=\\s*[\"']([^\"']+)[\"']");
    
    public static void main(String[] args) {
        XmlEncodingScanner scanner = new XmlEncodingScanner();
        scanner.scanAllXmlFiles();
    }
    
    /**
     * 扫描所有XML映射文件
     */
    public void scanAllXmlFiles() {
        System.out.println("=== XML编码扫描报告 ===");
        System.out.println("扫描目录: " + MAPPER_DIR);
        System.out.println();
        
        Path mapperPath = Paths.get(MAPPER_DIR);
        if (!Files.exists(mapperPath)) {
            System.err.println("错误: 映射文件目录不存在: " + mapperPath.toAbsolutePath());
            return;
        }
        
        List<EncodingIssue> issues = new ArrayList<>();
        int totalFiles = 0;
        int problemFiles = 0;
        
        try {
            Files.walk(mapperPath)
                .filter(path -> path.toString().endsWith(".xml"))
                .forEach(xmlFile -> {
                    EncodingIssue issue = analyzeXmlFile(xmlFile.toFile());
                    if (issue != null) {
                        issues.add(issue);
                    }
                });
            
            totalFiles = (int) Files.walk(mapperPath)
                .filter(path -> path.toString().endsWith(".xml"))
                .count();
                
        } catch (IOException e) {
            System.err.println("扫描文件时发生错误: " + e.getMessage());
            return;
        }
        
        // 统计问题文件数量
        problemFiles = (int) issues.stream().filter(EncodingIssue::hasProblems).count();
        
        // 输出扫描结果
        printScanResults(issues, totalFiles, problemFiles);
    } 
   
    /**
     * 分析单个XML文件的编码问题
     */
    private EncodingIssue analyzeXmlFile(File xmlFile) {
        EncodingIssue issue = new EncodingIssue(xmlFile);
        
        try {
            // 检测BOM标记
            issue.setBomDetected(detectBOM(xmlFile));
            
            // 检测声明的编码
            String declaredEncoding = detectDeclaredEncoding(xmlFile);
            issue.setDeclaredEncoding(declaredEncoding);
            
            // 检测实际编码
            String actualEncoding = detectActualEncoding(xmlFile);
            issue.setActualEncoding(actualEncoding);
            
            // 检查字符问题
            List<CharacterIssue> charIssues = detectCharacterIssues(xmlFile);
            issue.setCharacterIssues(charIssues);
            
            // 判断是否有问题
            boolean hasProblems = evaluateProblems(issue);
            issue.setHasProblems(hasProblems);
            
        } catch (IOException e) {
            issue.setError("无法读取文件: " + e.getMessage());
            issue.setHasProblems(true);
        }
        
        return issue;
    }
    
    /**
     * 检测BOM标记
     */
    private boolean detectBOM(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bom = new byte[3];
            int bytesRead = fis.read(bom);
            if (bytesRead >= 3) {
                return (bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF);
            }
        }
        return false;
    }
    
    /**
     * 检测XML声明中的编码
     */
    private String detectDeclaredEncoding(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String firstLine = reader.readLine();
            if (firstLine != null) {
                Matcher matcher = XML_ENCODING_PATTERN.matcher(firstLine);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (Exception e) {
            // 如果UTF-8读取失败，尝试其他编码
            return detectEncodingWithFallback(file);
        }
        return "未声明";
    }
    
    /**
     * 使用备用方法检测编码
     */
    private String detectEncodingWithFallback(File file) {
        String[] encodings = {"GBK", "GB2312", "ISO-8859-1", "UTF-16"};
        
        for (String encoding : encodings) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), encoding))) {
                String firstLine = reader.readLine();
                if (firstLine != null) {
                    Matcher matcher = XML_ENCODING_PATTERN.matcher(firstLine);
                    if (matcher.find()) {
                        return matcher.group(1) + " (通过" + encoding + "检测)";
                    }
                }
            } catch (Exception e) {
                // 继续尝试下一个编码
            }
        }
        return "检测失败";
    }   
 
    /**
     * 检测文件的实际编码
     */
    private String detectActualEncoding(File file) {
        // 尝试不同编码读取文件，看哪个不会产生异常
        String[] encodings = {"UTF-8", "GBK", "GB2312", "ISO-8859-1", "UTF-16"};
        
        for (String encoding : encodings) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), encoding))) {
                
                // 读取几行内容检查是否有乱码
                StringBuilder content = new StringBuilder();
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null && lineCount < 10) {
                    content.append(line).append("\n");
                    lineCount++;
                }
                
                // 检查是否包含明显的乱码字符
                String text = content.toString();
                if (!containsGarbledCharacters(text)) {
                    return encoding;
                }
                
            } catch (Exception e) {
                // 继续尝试下一个编码
            }
        }
        
        return "未知";
    }
    
    /**
     * 检查文本是否包含乱码字符
     */
    private boolean containsGarbledCharacters(String text) {
        // 检查常见的乱码模式
        return text.contains("�") || 
               text.matches(".*[\\u00C0-\\u00FF]{2,}.*") || // 连续的Latin-1扩展字符
               text.matches(".*[\uFFFD].*"); // 替换字符
    }
    
    /**
     * 检测字符问题
     */
    private List<CharacterIssue> detectCharacterIssues(File file) {
        List<CharacterIssue> issues = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line;
            int lineNumber = 1;
            
            while ((line = reader.readLine()) != null) {
                // 检查每行是否有问题字符
                for (int i = 0; i < line.length(); i++) {
                    char ch = line.charAt(i);
                    
                    if (ch == '\uFFFD') { // 替换字符
                        issues.add(new CharacterIssue(lineNumber, i + 1, ch, 
                            "替换字符(�)", getContext(line, i)));
                    } else if (ch > 127 && containsGarbledCharacters(String.valueOf(ch))) {
                        issues.add(new CharacterIssue(lineNumber, i + 1, ch, 
                            "可能的乱码字符", getContext(line, i)));
                    }
                }
                lineNumber++;
            }
            
        } catch (IOException e) {
            // 如果UTF-8读取失败，说明编码有问题
            issues.add(new CharacterIssue(0, 0, '\0', 
                "文件无法用UTF-8编码读取", "整个文件"));
        }
        
        return issues;
    }
    
    /**
     * 获取字符的上下文
     */
    private String getContext(String line, int position) {
        int start = Math.max(0, position - 10);
        int end = Math.min(line.length(), position + 10);
        return line.substring(start, end);
    }  
  
    /**
     * 评估是否存在编码问题
     */
    private boolean evaluateProblems(EncodingIssue issue) {
        // 检查各种问题条件
        boolean hasProblems = false;
        
        // 1. 声明编码与实际编码不匹配
        if (!"UTF-8".equalsIgnoreCase(issue.getDeclaredEncoding()) && 
            !"UTF-8".equalsIgnoreCase(issue.getActualEncoding())) {
            hasProblems = true;
        }
        
        // 2. 存在字符问题
        if (!issue.getCharacterIssues().isEmpty()) {
            hasProblems = true;
        }
        
        // 3. 无法检测编码
        if ("未知".equals(issue.getActualEncoding()) || 
            "检测失败".equals(issue.getDeclaredEncoding())) {
            hasProblems = true;
        }
        
        // 4. 存在错误
        if (issue.getError() != null) {
            hasProblems = true;
        }
        
        return hasProblems;
    }
    
    /**
     * 打印扫描结果
     */
    private void printScanResults(List<EncodingIssue> issues, int totalFiles, int problemFiles) {
        System.out.println("=== 扫描结果汇总 ===");
        System.out.println("总文件数: " + totalFiles);
        System.out.println("有问题的文件数: " + problemFiles);
        System.out.println("正常文件数: " + (totalFiles - problemFiles));
        System.out.println();
        
        if (problemFiles > 0) {
            System.out.println("=== 问题文件详情 ===");
            
            for (EncodingIssue issue : issues) {
                if (issue.hasProblems()) {
                    printIssueDetails(issue);
                    System.out.println();
                }
            }
            
            System.out.println("=== 修复建议 ===");
            System.out.println("1. 运行编码修复工具修复这些文件");
            System.out.println("2. 确保所有XML文件使用UTF-8编码");
            System.out.println("3. 验证修复后的文件能正常编译");
        } else {
            System.out.println("✓ 所有XML文件编码正常！");
        }
        
        System.out.println();
        System.out.println("=== 所有文件状态 ===");
        for (EncodingIssue issue : issues) {
            String status = issue.hasProblems() ? "❌ 有问题" : "✓ 正常";
            System.out.println(status + " - " + issue.getFile().getName());
        }
    }
    
    /**
     * 打印问题详情
     */
    private void printIssueDetails(EncodingIssue issue) {
        System.out.println("文件: " + issue.getFile().getName());
        System.out.println("路径: " + issue.getFile().getPath());
        
        if (issue.getError() != null) {
            System.out.println("错误: " + issue.getError());
            return;
        }
        
        System.out.println("声明编码: " + issue.getDeclaredEncoding());
        System.out.println("实际编码: " + issue.getActualEncoding());
        System.out.println("BOM标记: " + (issue.isBomDetected() ? "是" : "否"));
        
        if (!issue.getCharacterIssues().isEmpty()) {
            System.out.println("字符问题:");
            for (CharacterIssue charIssue : issue.getCharacterIssues()) {
                System.out.println("  行 " + charIssue.getLineNumber() + 
                    ", 列 " + charIssue.getColumnNumber() + 
                    ": " + charIssue.getDescription() + 
                    " (上下文: " + charIssue.getContext() + ")");
            }
        }
    }
}