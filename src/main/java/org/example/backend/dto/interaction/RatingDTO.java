package org.example.backend.dto.interaction;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 评分DTO
 */
@Data
public class RatingDTO {
    
    @NotNull(message = "图书ID不能为空")
    private Long bookId;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分必须在1-5之间")
    @Max(value = 5, message = "评分必须在1-5之间")
    private Integer score;
}

