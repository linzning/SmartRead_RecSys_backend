package org.example.backend.dto.admin;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 图书DTO（管理员）
 */
@Data
public class BookDTO {
    
    private Long id;
    
    @NotBlank(message = "ISBN不能为空")
    private String isbn;
    
    @NotBlank(message = "书名不能为空")
    private String title;
    
    @NotBlank(message = "作者不能为空")
    private String author;
    
    private String publisher;
    private String publishDate;
    private String coverUrl;
    private String summary;
    private Integer status;
    private List<String> topics; // 主题列表
}

