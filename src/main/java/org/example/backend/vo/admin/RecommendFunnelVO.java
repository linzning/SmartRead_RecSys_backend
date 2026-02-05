package org.example.backend.vo.admin;

import lombok.Data;

@Data
public class RecommendFunnelVO {
    private String recommendType;
    private Long exposureCount;
    private Long clickCount;
    private Long favoriteCount;
    private Long ratingCount;

    private Double ctr; // click/exposure
    private Double clickToFavorite; // favorite/click
    private Double favoriteToRating; // rating/favorite
}



