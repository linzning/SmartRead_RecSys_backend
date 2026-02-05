package org.example.backend.modules.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.common.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/admin/upload")
@Tag(name = "文件上传", description = "文件上传接口")
public class FileUploadController {
    
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;
    
    @Value("${file.upload.url-prefix:/uploads}")
    private String urlPrefix;
    
    /**
     * 上传图书封面
     */
    @PostMapping("/cover")
    @Operation(summary = "上传图书封面")
    public ApiResponse<String> uploadCover(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error("文件不能为空");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ApiResponse.error("只能上传图片文件");
        }
        
        // 检查文件大小（限制5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return ApiResponse.error("文件大小不能超过5MB");
        }
        
        try {
            // 创建上传目录（按日期分类）
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fullUploadPath = uploadPath + "/covers/" + dateDir;
            File uploadDir = new File(fullUploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = Paths.get(fullUploadPath, filename);
            Files.write(filePath, file.getBytes());
            
            // 返回访问URL
            String url = urlPrefix + "/covers/" + dateDir + "/" + filename;
            return ApiResponse.success("上传成功", url);
            
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传用户头像
     */
    @PostMapping("/avatar")
    @Operation(summary = "上传用户头像")
    public ApiResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error("文件不能为空");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ApiResponse.error("只能上传图片文件");
        }
        
        // 检查文件大小（限制2MB，头像比封面小）
        if (file.getSize() > 2 * 1024 * 1024) {
            return ApiResponse.error("文件大小不能超过2MB");
        }
        
        try {
            // 创建上传目录（按日期分类）
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fullUploadPath = uploadPath + "/avatars/" + dateDir;
            File uploadDir = new File(fullUploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = Paths.get(fullUploadPath, filename);
            Files.write(filePath, file.getBytes());
            
            // 返回访问URL
            String url = urlPrefix + "/avatars/" + dateDir + "/" + filename;
            return ApiResponse.success("上传成功", url);
            
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("文件上传失败：" + e.getMessage());
        }
    }
}


