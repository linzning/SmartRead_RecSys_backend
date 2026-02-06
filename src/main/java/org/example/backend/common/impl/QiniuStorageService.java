package org.example.backend.common.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.common.util.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 七牛云文件存储服务实现
 */
@Slf4j
@Service("qiniuStorageService")
public class QiniuStorageService implements FileStorageService {

    private final UploadManager uploadManager;
    private final Auth auth;

    @Value("${qiniu.bucket}")
    private String bucket;

    @Value("${qiniu.domain}")
    private String domain;

    public QiniuStorageService(UploadManager uploadManager, Auth auth) {
        this.uploadManager = uploadManager;
        this.auth = auth;
    }

    @Override
    public String uploadFile(MultipartFile file, String fileType) {
        try {
            // 生成唯一文件名：按日期分类/UUID.扩展名
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = fileType + "s/" + dateDir + "/" + UUID.randomUUID() + extension;

            // 上传到七牛云
            String upToken = auth.uploadToken(bucket);
            Response response = uploadManager.put(file.getInputStream(), filename, upToken, null, null);

            if (!response.isOK()) {
                throw new RuntimeException("七牛云上传失败：" + response.toString());
            }

            // 解析上传结果
            DefaultPutRet putRet = response.jsonToObject(DefaultPutRet.class);
            log.info("文件上传成功，key: {}", putRet.key);

            // 返回完整的访问URL
            return domain + "/" + filename;

        } catch (Exception e) {
            log.error("七牛云上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }
}
