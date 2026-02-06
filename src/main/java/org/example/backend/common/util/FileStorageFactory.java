package org.example.backend.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 文件存储工厂类
 */
@Component
public class FileStorageFactory {

    @Value("${file.upload.storage-type:qiniu}")
    private String storageType;

    private final FileStorageService localStorageService;
    private final FileStorageService qiniuStorageService;

    public FileStorageFactory(
            @org.springframework.beans.factory.annotation.Qualifier("localStorageService")
            FileStorageService localStorageService,
            @org.springframework.beans.factory.annotation.Qualifier("qiniuStorageService")
            FileStorageService qiniuStorageService) {
        this.localStorageService = localStorageService;
        this.qiniuStorageService = qiniuStorageService;
    }

    /**
     * 获取当前配置的文件存储服务
     */
    public FileStorageService getStorageService() {
        if ("qiniu".equalsIgnoreCase(storageType)) {
            return qiniuStorageService;
        } else {
            return localStorageService;
        }
    }
}
