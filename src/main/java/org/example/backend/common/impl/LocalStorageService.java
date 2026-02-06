package org.example.backend.common.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.backend.common.util.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地文件存储服务实现
 */
@Slf4j
@Service("localStorageService")
public class LocalStorageService implements FileStorageService {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:/uploads}")
    private String urlPrefix;

    @Override
    public String uploadFile(MultipartFile file, String fileType) {
        try {
            // 创建上传目录（按日期分类）
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fullUploadPath = uploadPath + "/" + fileType + "s/" + dateDir;
            Files.createDirectories(Paths.get(fullUploadPath));

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID() + extension;

            // 保存文件
            Path filePath = Paths.get(fullUploadPath, filename);
            Files.write(filePath, file.getBytes());

            // 返回相对路径URL
            return urlPrefix + "/" + fileType + "s/" + dateDir + "/" + filename;

        } catch (Exception e) {
            log.error("本地文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }
}
