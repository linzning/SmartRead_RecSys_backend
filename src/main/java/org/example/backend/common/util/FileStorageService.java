package org.example.backend.common.util;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {

    /**
     * 上传文件
     * @param file 文件
     * @param fileType 文件类型 (cover/avatar)
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String fileType);

}
