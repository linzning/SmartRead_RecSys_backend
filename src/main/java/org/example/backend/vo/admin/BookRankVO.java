package org.example.backend.vo.admin;

import lombok.Data;

@Data
public class BookRankVO {
    private Long bookId;
    private String title;
    private String author;
    private String coverUrl;
    private Long count;
}



