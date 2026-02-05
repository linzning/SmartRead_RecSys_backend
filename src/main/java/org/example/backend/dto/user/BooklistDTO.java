package org.example.backend.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 书单DTO
 */
@Data
public class BooklistDTO {
    
    /**
     * 书单名称
     */
    @NotBlank(message = "书单名称不能为空")
    @Size(max = 100, message = "书单名称长度不能超过100")
    private String name;
    
    /**
     * 书单描述
     */
    @Size(max = 500, message = "书单描述长度不能超过500")
    private String description;
    
    /**
     * 是否公开：0-私有，1-公开
     */
    private Integer isPublic;
}

