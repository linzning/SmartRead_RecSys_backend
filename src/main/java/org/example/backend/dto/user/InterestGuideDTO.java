package org.example.backend.dto.user;

import lombok.Data;

import java.util.List;

/**
 * 兴趣引导DTO
 */
@Data
public class InterestGuideDTO {
    
    /**
     * 选择的兴趣主题
     */
    private List<String> selectedTopics;
    
    /**
     * 选择的作者
     */
    private List<String> selectedAuthors;
}

