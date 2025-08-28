package com.yxrobot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件访问控制器
 * 提供上传文件的HTTP访问服务
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    
    /**
     * 文件存储根路径
     */
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;
    
    /**
     * 获取文件
     * 
     * @param filePath 文件相对路径
     * @return 文件资源
     */
    @GetMapping("/**")
    public ResponseEntity<Resource> getFile(HttpServletRequest request) {
        try {
            // 获取请求路径中的文件路径
            String requestPath = request.getRequestURI();
            String filePath = requestPath.substring("/api/v1/files/".length());
            
            logger.info("请求文件: {}", filePath);
            
            // 构建完整文件路径
            String fullPath = uploadPath + File.separator + filePath.replace("/", File.separator);
            Path path = Paths.get(fullPath);
            
            // 检查文件是否存在
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                logger.warn("文件不存在: {}", fullPath);
                return ResponseEntity.notFound().build();
            }
            
            // 安全检查：确保文件在上传目录内
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path requestedFile = path.toAbsolutePath().normalize();
            
            if (!requestedFile.startsWith(uploadDir)) {
                logger.warn("非法文件访问尝试: {}", fullPath);
                return ResponseEntity.badRequest().build();
            }
            
            // 创建文件资源
            Resource resource = new FileSystemResource(path);
            
            // 确定文件的MIME类型
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            logger.info("成功访问文件: {}, 类型: {}", fullPath, contentType);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName().toString() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            logger.error("文件访问失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}