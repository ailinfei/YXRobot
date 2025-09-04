package com.yxrobot.util;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * XML验证测试工具
 * 验证修复后的XML文件是否能被MyBatis正确解析
 */
public class XmlValidationTest {
    
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String mapperPath = projectPath + File.separator + "src" + File.separator + 
                           "main" + File.separator + "resources" + File.separator + "mapper";
        
        System.out.println("开始验证XML文件MyBatis解析...");
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
        
        Configuration configuration = new Configuration();
        List<String> successFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();
        
        for (File xmlFile : xmlFiles) {
            try {
                System.out.print("验证: " + xmlFile.getName() + " ... ");
                
                try (InputStream inputStream = new FileInputStream(xmlFile)) {
                    XMLMapperBuilder builder = new XMLMapperBuilder(
                        inputStream, 
                        configuration, 
                        xmlFile.getAbsolutePath(), 
                        configuration.getSqlFragments()
                    );
                    builder.parse();
                    
                    System.out.println("✓ 成功");
                    successFiles.add(xmlFile.getName());
                }
                
            } catch (Exception e) {
                System.out.println("✗ 失败: " + e.getMessage());
                failedFiles.add(xmlFile.getName() + " (" + e.getMessage() + ")");
            }
        }
        
        System.out.println("========================================");
        System.out.println("验证结果统计:");
        System.out.println("总文件数: " + xmlFiles.length);
        System.out.println("解析成功: " + successFiles.size());
        System.out.println("解析失败: " + failedFiles.size());
        
        if (!failedFiles.isEmpty()) {
            System.out.println("\n失败文件详情:");
            for (String failed : failedFiles) {
                System.out.println("- " + failed);
            }
        }
        
        if (failedFiles.isEmpty()) {
            System.out.println("\n🎉 所有XML文件都能被MyBatis正确解析！");
            System.out.println("XML编码修复验证成功！");
        } else {
            System.out.println("\n⚠️ 有 " + failedFiles.size() + " 个文件解析失败，需要进一步检查");
        }
    }
}