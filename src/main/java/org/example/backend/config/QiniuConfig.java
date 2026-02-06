package org.example.backend.config;

import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 七牛云配置类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "qiniu")
public class QiniuConfig {

    private String accessKey;
    private String secretKey;
    private String bucket;
    private String domain;

    /**
     * 创建认证对象
     */
    @Bean
    public Auth qiniuAuth() {
        return Auth.create(accessKey, secretKey);
    }

    /**
     * 创建上传管理器
     */
    @Bean
    public UploadManager uploadManager() {
        Configuration cfg = new Configuration();
        return new UploadManager(cfg);
    }
}
