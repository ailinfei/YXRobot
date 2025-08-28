package com.yxrobot.integration;

import com.yxrobot.service.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件上传功能集成测试
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@SpringBootTest
@ActiveProfiles("test")
class FileUploadIntegrationTest {

    @Autowired
    private FileUploadService fileUploadService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // 设置临时上传目录
        ReflectionTestUtils.setField(fileUploadService, "uploadPath", tempDir.toString());
        ReflectionTestUtils.setField(fileUploadService, "urlPrefix", "/api/v1/files");
    }

    @Test
    void testUploadProductCover_Success() throws IOException {
        // 准备测试文件
        MockMultipartFile imageFile = new MockMultipartFile(
            "cover",
            "test-cover.jpg",
            "image/jpeg",
            "test image content for cover".getBytes()
        );

        // 执行上传
        String fileUrl = fileUploadService.uploadProductCover(imageFile);

        // 验证结果
        assertNotNull(fileUrl);
        assertTrue(fileUrl.startsWith("/api/v1/files"));
        assertTrue(fileUrl.contains("products"));
        assertTrue(fileUrl.contains("covers"));
        assertTrue(fileUrl.endsWith(".jpg"));

        // 验证文件实际存在
        String relativePath = fileUrl.substring("/api/v1/files/".length());
        Path uploadedFile = tempDir.resolve(relativePath.replace("/", tempDir.getFileSystem().getSeparator()));
        assertTrue(Files.exists(uploadedFile));
        
        // 验证文件内容
        byte[] uploadedContent = Files.readAllBytes(uploadedFile);
        assertArrayEquals("test image content for cover".getBytes(), uploadedContent);
    }

    @Test
    void testUploadProductImage_Success() throws IOException {
        // 准备测试文件
        MockMultipartFile imageFile = new MockMultipartFile(
            "image",
            "product-image.png",
            "image/png",
            "test product image content".getBytes()
        );

        // 执行上传
        String fileUrl = fileUploadService.uploadProductImage(imageFile);

        // 验证结果
        assertNotNull(fileUrl);
        assertTrue(fileUrl.startsWith("/api/v1/files"));
        assertTrue(fileUrl.contains("products"));
        assertTrue(fileUrl.contains("media"));
        assertTrue(fileUrl.contains("image"));
        assertTrue(fileUrl.endsWith(".png"));

        // 验证文件实际存在
        String relativePath = fileUrl.substring("/api/v1/files/".length());
        Path uploadedFile = tempDir.resolve(relativePath.replace("/", tempDir.getFileSystem().getSeparator()));
        assertTrue(Files.exists(uploadedFile));
    }

    @Test
    void testUploadProductVideo_Success() throws IOException {
        // 准备测试文件
        MockMultipartFile videoFile = new MockMultipartFile(
            "video",
            "product-video.mp4",
            "video/mp4",
            "test product video content".getBytes()
        );

        // 执行上传
        String fileUrl = fileUploadService.uploadProductVideo(videoFile);

        // 验证结果
        assertNotNull(fileUrl);
        assertTrue(fileUrl.startsWith("/api/v1/files"));
        assertTrue(fileUrl.contains("products"));
        assertTrue(fileUrl.contains("media"));
        assertTrue(fileUrl.contains("video"));
        assertTrue(fileUrl.endsWith(".mp4"));

        // 验证文件实际存在
        String relativePath = fileUrl.substring("/api/v1/files/".length());
        Path uploadedFile = tempDir.resolve(relativePath.replace("/", tempDir.getFileSystem().getSeparator()));
        assertTrue(Files.exists(uploadedFile));
    }

    @Test
    void testDeleteFile_Success() throws IOException {
        // 先上传一个文件
        MockMultipartFile imageFile = new MockMultipartFile(
            "image",
            "delete-test.jpg",
            "image/jpeg",
            "test image for deletion".getBytes()
        );

        String fileUrl = fileUploadService.uploadProductImage(imageFile);
        
        // 验证文件存在
        String relativePath = fileUrl.substring("/api/v1/files/".length());
        Path uploadedFile = tempDir.resolve(relativePath.replace("/", tempDir.getFileSystem().getSeparator()));
        assertTrue(Files.exists(uploadedFile));

        // 执行删除
        boolean deleted = fileUploadService.deleteFile(fileUrl);

        // 验证删除结果
        assertTrue(deleted);
        assertFalse(Files.exists(uploadedFile));
    }

    @Test
    void testDeleteFile_NonExistentFile() {
        // 尝试删除不存在的文件
        String nonExistentUrl = "/api/v1/files/products/covers/2024/01/01/nonexistent.jpg";
        boolean deleted = fileUploadService.deleteFile(nonExistentUrl);

        // 验证结果
        assertFalse(deleted);
    }

    @Test
    void testUploadInvalidImageFile() {
        // 测试无效的图片文件类型
        MockMultipartFile invalidFile = new MockMultipartFile(
            "image",
            "test.txt",
            "text/plain",
            "this is not an image".getBytes()
        );

        // 验证抛出异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileUploadService.uploadProductImage(invalidFile)
        );

        assertTrue(exception.getMessage().contains("不支持的图片格式"));
    }

    @Test
    void testUploadOversizedImageFile() {
        // 创建超大文件（模拟超过10MB）
        byte[] oversizedContent = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile oversizedFile = new MockMultipartFile(
            "image",
            "oversized.jpg",
            "image/jpeg",
            oversizedContent
        );

        // 验证抛出异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileUploadService.uploadProductImage(oversizedFile)
        );

        assertTrue(exception.getMessage().contains("图片文件大小不能超过"));
    }

    @Test
    void testUploadInvalidVideoFile() {
        // 测试无效的视频文件类型
        MockMultipartFile invalidFile = new MockMultipartFile(
            "video",
            "test.txt",
            "text/plain",
            "this is not a video".getBytes()
        );

        // 验证抛出异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileUploadService.uploadProductVideo(invalidFile)
        );

        assertTrue(exception.getMessage().contains("不支持的视频格式"));
    }

    @Test
    void testUploadOversizedVideoFile() {
        // 创建超大视频文件（模拟超过100MB）
        // 为了测试性能，我们只创建一个较小的文件但设置错误的大小检查
        MockMultipartFile videoFile = new MockMultipartFile(
            "video",
            "large-video.mp4",
            "video/mp4",
            new byte[1024] // 实际很小，但我们会在服务中模拟大小检查
        ) {
            @Override
            public long getSize() {
                return 101L * 1024 * 1024; // 返回101MB
            }
        };

        // 验证抛出异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileUploadService.uploadProductVideo(videoFile)
        );

        assertTrue(exception.getMessage().contains("视频文件大小不能超过"));
    }

    @Test
    void testUploadEmptyFile() {
        // 测试空文件
        MockMultipartFile emptyFile = new MockMultipartFile(
            "image",
            "empty.jpg",
            "image/jpeg",
            new byte[0]
        );

        // 验证抛出异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileUploadService.uploadProductImage(emptyFile)
        );

        assertTrue(exception.getMessage().contains("图片文件不能为空"));
    }

    @Test
    void testUploadNullFile() {
        // 验证抛出异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileUploadService.uploadProductImage(null)
        );

        assertTrue(exception.getMessage().contains("图片文件不能为空"));
    }

    @Test
    void testFilePathGeneration() throws IOException {
        // 上传多个文件，验证路径生成的唯一性
        MockMultipartFile file1 = new MockMultipartFile(
            "image1",
            "test1.jpg",
            "image/jpeg",
            "test content 1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
            "image2",
            "test2.jpg",
            "image/jpeg",
            "test content 2".getBytes()
        );

        String url1 = fileUploadService.uploadProductImage(file1);
        String url2 = fileUploadService.uploadProductImage(file2);

        // 验证URL不同
        assertNotEquals(url1, url2);

        // 验证都包含正确的路径结构
        assertTrue(url1.contains("products/media/image"));
        assertTrue(url2.contains("products/media/image"));

        // 验证文件都存在
        String relativePath1 = url1.substring("/api/v1/files/".length());
        String relativePath2 = url2.substring("/api/v1/files/".length());
        
        Path file1Path = tempDir.resolve(relativePath1.replace("/", tempDir.getFileSystem().getSeparator()));
        Path file2Path = tempDir.resolve(relativePath2.replace("/", tempDir.getFileSystem().getSeparator()));
        
        assertTrue(Files.exists(file1Path));
        assertTrue(Files.exists(file2Path));
    }

    @Test
    void testDirectoryCreation() throws IOException {
        // 验证上传文件时会自动创建目录结构
        MockMultipartFile imageFile = new MockMultipartFile(
            "image",
            "directory-test.jpg",
            "image/jpeg",
            "test directory creation".getBytes()
        );

        String fileUrl = fileUploadService.uploadProductImage(imageFile);
        
        // 验证目录结构被创建
        String relativePath = fileUrl.substring("/api/v1/files/".length());
        Path uploadedFile = tempDir.resolve(relativePath.replace("/", tempDir.getFileSystem().getSeparator()));
        
        assertTrue(Files.exists(uploadedFile));
        assertTrue(Files.exists(uploadedFile.getParent()));
        
        // 验证目录结构包含预期的路径
        String pathString = uploadedFile.toString();
        assertTrue(pathString.contains("products"));
        assertTrue(pathString.contains("media"));
        assertTrue(pathString.contains("image"));
    }
}