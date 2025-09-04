package com.yxrobot.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * XML智能修复执行器
 */
public class XmlSmartFixRunner {
    
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String mapperPath = projectPath + File.separator + "src" + File.separator + 
                           "main" + File.separator + "resources" + File.separator + "mapper";
        
        System.out.println("开始智能修复XML文件中的替换字符...");
        System.out.println("项目路径: " + projectPath);
        System.out.println("映射文件路径: " + mapperPath);
        System.out.println("========================================");
        
        XmlSmartFixer fixer = new XmlSmartFixer();
        List<File> xmlFiles = new ArrayList<>();
        
        // 收集所有XML文件
        File mapperDir = new File(mapperPath);
        if (mapperDir.exists() && mapperDir.isDirectory()) {
            File[] files = mapperDir.listFiles((dir, name) -> name.endsWith(".xml"));
            if (files != null) {
                for (File file : files) {
                    xmlFiles.add(file);
                }
            }
        }
        
        System.out.println("找到 " + xmlFiles.size() + " 个XML文件");
        System.out.println("----------------------------------------");
        
        // 执行智能修复
        List<XmlSmartFixer.FixResult> results = fixer.smartFixMultiple(xmlFiles);
        
        // 统计结果
        int totalFiles = results.size();
        int filesWithReplacementChars = 0;
        int filesFixed = 0;
        int totalOriginalChars = 0;
        int totalFixedChars = 0;
        int totalRemainingChars = 0;
        
        System.out.println("========================================");
        System.out.println("智能修复结果统计:");
        
        for (XmlSmartFixer.FixResult result : results) {
            if (result.getOriginalReplacementCount() > 0) {
                filesWithReplacementChars++;
                totalOriginalChars += result.getOriginalReplacementCount();
                totalFixedChars += result.getFixedCount();
                totalRemainingChars += result.getFinalReplacementCount();
                
                if (result.getFixedCount() > 0) {
                    filesFixed++;
                }
                
                System.out.println(String.format("- %s: 原始%d个 -> 修复%d个 -> 剩余%d个", 
                    result.getFileName(), 
                    result.getOriginalReplacementCount(),
                    result.getFixedCount(),
                    result.getFinalReplacementCount()));
            }
        }
        
        System.out.println("========================================");
        System.out.println("总体统计:");
        System.out.println("总文件数: " + totalFiles);
        System.out.println("包含替换字符的文件: " + filesWithReplacementChars);
        System.out.println("成功修复的文件: " + filesFixed);
        System.out.println("原始替换字符总数: " + totalOriginalChars);
        System.out.println("成功修复字符数: " + totalFixedChars);
        System.out.println("剩余替换字符数: " + totalRemainingChars);
        
        double fixRate = totalOriginalChars > 0 ? (double) totalFixedChars / totalOriginalChars * 100 : 0;
        System.out.println("修复成功率: " + String.format("%.1f%%", fixRate));
        
        if (totalRemainingChars > 0) {
            System.out.println("========================================");
            System.out.println("注意事项:");
            System.out.println("- 仍有 " + totalRemainingChars + " 个替换字符需要手动修复");
            System.out.println("- 建议检查这些文件并根据业务逻辑手动修复");
            System.out.println("- 大部分替换字符应该是'射'字");
        } else {
            System.out.println("🎉 所有替换字符已成功修复！");
        }
        
        System.out.println("建议: 请运行编译测试验证修复结果");
    }
}