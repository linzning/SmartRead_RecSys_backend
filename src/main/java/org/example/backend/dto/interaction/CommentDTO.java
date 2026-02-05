package org.example.backend.dto.interaction;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 评论DTO
 */
@Data
public class CommentDTO {
    
    @NotNull(message = "图书ID不能为空")
    private Long bookId;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分必须在1-5之间")
    @Max(value = 5, message = "评分必须在1-5之间")
    private Integer rating;
    
    @NotBlank(message = "评论内容不能为空")
    private String content;
}

