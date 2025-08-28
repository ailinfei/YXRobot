package com.yxrobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务类
 * 负责处理文件上传、存储和管理
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@Service
public class FileUploadService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);
    
    /**
     * 文件存储根路径
     */
    @Value("${file.upload.path:/uploads}")
    private String uploadPath;
    
    /**
     * 文件访问URL前缀
     */
    @Value("${file.access.url.prefix:/api/v1/files}")
    private String urlPrefix;
    
    /**
     * 允许的图片文件类型
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    /**
     * 允许的视频文件类型
     */
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
        "video/mp4", "video/avi", "video/mov", "video/wmv", "video/flv"
    );
    
    /**
     * 图片文件最大大小（10MB）
     */
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;
    
    /**
     * 视频文件最大大小（100MB）
     */
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024;
    
    /**
     * 上传产品封面图片
     * 
     * @param file 图片文件
     * @return 文件访问URL
     * @throws IOException 文件操作异常
     */
    public String uploadProductCover(MultipartFile file) throws IOException {
        logger.info("开始上传产品封面图片，文件名: {}, 大小: {} bytes", 
                   file.getOriginalFilename(), file.getSize());
        
        // 验证文件
        validateImageFile(file);
        
        // 生成文件存储路径
        String relativePath = generateProductCoverPath(file);
        String fullPath = uploadPath + File.separator + relativePath;
        
        // 确保目录存在
        createDirectoryIfNotExists(fullPath);
        
        // 保存文件
        Path targetPath = Paths.get(fullPath);
        Files.copy(file.getInputStream(), targetPath);
        
        // 生成访问URL
        String accessUrl = urlPrefix + "/" + relativePath.replace(File.separator, "/");
        
        logger.info("成功上传产品封面图片，存储路径: {}, 访问URL: {}", fullPath, accessUrl);
        return accessUrl;
    }
    
    /**
     * 上传产品图片
     * 
     * @param file 图片文件
     * @return 文件访问URL
     * @throws IOException 文件操作异常
     */
    public String uploadProductImage(MultipartFile file) throws IOException {
        return uploadProductMedia(file, "image");
    }
    
    /**
     * 上传产品视频
     * 
     * @param file 视频文件
     * @return 文件访问URL
     * @throws IOException 文件操作异常
     */
    public String uploadProductVideo(MultipartFile file) throws IOException {
        return uploadProductMedia(file, "video");
    }
    
    /**
     * 上传产品媒体文件（图片或视频）
     * 
     * @param file 媒体文件
     * @param mediaType 媒体类型：image 或 video
     * @return 文件访问URL
     * @throws IOException 文件操作异常
     */
    public String uploadProductMedia(MultipartFile file, String mediaType) throws IOException {
        logger.info("开始上传产品媒体文件，类型: {}, 文件名: {}, 大小: {} bytes", 
                   mediaType, file.getOriginalFilename(), file.getSize());
        
        // 根据媒体类型验证文件
        if ("image".equals(mediaType)) {
            validateImageFile(file);
        } else if ("video".equals(mediaType)) {
            validateVideoFile(file);
        } else {
            throw new IllegalArgumentException("不支持的媒体类型: " + mediaType);
        }
        
        // 生成文件存储路径
        String relativePath = generateProductMediaPath(file, mediaType);
        String fullPath = uploadPath + File.separator + relativePath;
        
        // 确保目录存在
        createDirectoryIfNotExists(fullPath);
        
        // 保存文件
        Path targetPath = Paths.get(fullPath);
        Files.copy(file.getInputStream(), targetPath);
        
        // 生成访问URL
        String accessUrl = urlPrefix + "/" + relativePath.replace(File.separator, "/");
        
        logger.info("成功上传产品媒体文件，存储路径: {}, 访问URL: {}", fullPath, accessUrl);
        return accessUrl;
    }
    
    /**
     * 删除文件
     * 
     * @param fileUrl 文件访问URL
     * @return 删除是否成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return false;
        }
        
        try {
            // 从URL提取相对路径
            String relativePath = extractRelativePathFromUrl(fileUrl);
            if (relativePath == null) {
                logger.warn("无法从URL提取文件路径: {}", fileUrl);
                return false;
            }
            
            // 构建完整文件路径
            String fullPath = uploadPath + File.separator + relativePath.replace("/", File.separator);
            Path filePath = Paths.get(fullPath);
            
            // 删除文件
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                logger.info("成功删除文件: {}", fullPath);
            } else {
                logger.warn("文件不存在或删除失败: {}", fullPath);
            }
            
            return deleted;
            
        } catch (Exception e) {
            logger.error("删除文件失败: {}", fileUrl, e);
            return false;
        }
    }
    
    /**
     * 验证图片文件
     * 
     * @param file 图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("图片文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException(
                String.format("图片文件大小不能超过 %d MB", MAX_IMAGE_SIZE / 1024 / 1024));
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                "不支持的图片格式，支持的格式: " + String.join(", ", ALLOWED_IMAGE_TYPES));
        }
        
        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !hasValidImageExtension(originalFilename)) {
            throw new IllegalArgumentException("图片文件扩展名不正确");
        }
    }
    
    /**
     * 验证视频文件
     * 
     * @param file 视频文件
     */
    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("视频文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > MAX_VIDEO_SIZE) {
            throw new IllegalArgumentException(
                String.format("视频文件大小不能超过 %d MB", MAX_VIDEO_SIZE / 1024 / 1024));
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                "不支持的视频格式，支持的格式: " + String.join(", ", ALLOWED_VIDEO_TYPES));
        }
        
        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !hasValidVideoExtension(originalFilename)) {
            throw new IllegalArgumentException("视频文件扩展名不正确");
        }
    }
    
    /**
     * 生成产品封面图片存储路径
     * 
     * @param file 图片文件
     * @return 相对存储路径
     */
    private String generateProductCoverPath(MultipartFile file) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = getFileExtension(file.getOriginalFilename());
        
        return "products" + File.separator + "covers" + File.separator + 
               dateStr + File.separator + uuid + "." + extension;
    }
    
    /**
     * 生成产品媒体文件存储路径
     * 
     * @param file 媒体文件
     * @param mediaType 媒体类型
     * @return 相对存储路径
     */
    private String generateProductMediaPath(MultipartFile file, String mediaType) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = getFileExtension(file.getOriginalFilename());
        
        return "products" + File.separator + "media" + File.separator + mediaType + 
               File.separator + dateStr + File.separator + uuid + "." + extension;
    }
    
    /**
     * 创建目录（如果不存在）
     * 
     * @param filePath 文件完整路径
     */
    private void createDirectoryIfNotExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Path parentDir = path.getParent();
        
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
            logger.info("创建目录: {}", parentDir);
        }
    }
    
    /**
     * 获取文件扩展名
     * 
     * @param filename 文件名
     * @return 扩展名（不包含点）
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        
        return "";
    }
    
    /**
     * 检查是否为有效的图片扩展名
     * 
     * @param filename 文件名
     * @return true-有效，false-无效
     */
    private boolean hasValidImageExtension(String filename) {
        String extension = getFileExtension(filename);
        return Arrays.asList("jpg", "jpeg", "png", "gif", "webp").contains(extension);
    }
    
    /**
     * 检查是否为有效的视频扩展名
     * 
     * @param filename 文件名
     * @return true-有效，false-无效
     */
    private boolean hasValidVideoExtension(String filename) {
        String extension = getFileExtension(filename);
        return Arrays.asList("mp4", "avi", "mov", "wmv", "flv").contains(extension);
    }
    
    /**
     * 从URL提取相对路径
     * 
     * @param fileUrl 文件访问URL
     * @return 相对路径
     */
    private String extractRelativePathFromUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.contains(urlPrefix)) {
            return null;
        }
        
        int prefixIndex = fileUrl.indexOf(urlPrefix);
        if (prefixIndex >= 0) {
            return fileUrl.substring(prefixIndex + urlPrefix.length() + 1);
        }
        
        return null;
    }
}