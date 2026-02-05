package org.example.backend.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 兴趣偏好DTO
 */
@Data
public class PreferenceDTO {
    
    /**
     * 偏好类型（topic, author等）
     */
    @NotBlank(message = "偏好类型不能为空")
    private String preferenceType;
    
    /**
     * 偏好值列表
     */
    @NotNull(message = "偏好值不能为空")
    private List<String> preferenceValues;
}

