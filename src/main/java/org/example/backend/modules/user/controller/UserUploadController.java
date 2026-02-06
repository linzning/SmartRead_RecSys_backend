package org.example.backend.modules.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backend.common.ApiResponse;
import org.example.backend.common.util.FileStorageFactory;
import org.example.backend.common.util.FileStorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户文件上传控制器
 */
@RestController
@RequestMapping("/api/user/upload")
@Tag(name = "用户文件上传", description = "用户文件上传接口")
public class UserUploadController {

    private final FileStorageFactory storageFactory;

    public UserUploadController(FileStorageFactory storageFactory) {
        this.storageFactory = storageFactory;
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

        // 检查文件大小（限制2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            return ApiResponse.error("文件大小不能超过2MB");
        }

        try {
            // 使用工厂获取存储服务并上传
            FileStorageService storageService = storageFactory.getStorageService();
            String url = storageService.uploadFile(file, "avatar");
            return ApiResponse.success("上传成功", url);

        } catch (Exception e) {
            return ApiResponse.error("文件上传失败：" + e.getMessage());
        }
    }
}
