package org.example.backend.modules.admin.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminStatsMapper {

    @Select(
            "SELECT DATE(u.create_time) AS d, COUNT(*) AS c\n" +
            "FROM users u\n" +
            "WHERE u.create_time >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY)\n" +
            "GROUP BY DATE(u.create_time)\n" +
            "ORDER BY d ASC"
    )
    List<Map<String, Object>> userGrowth(@Param("days") int days);

    @Select(
            "SELECT b.id AS bookId, b.title AS title, b.author AS author, b.cover_url AS coverUrl, COUNT(*) AS c\n" +
            "FROM recommend_click rc\n" +
            "JOIN books b ON b.id = rc.book_id\n" +
            "WHERE rc.click_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY)\n" +
            "GROUP BY b.id, b.title, b.author, b.cover_url\n" +
            "ORDER BY c DESC\n" +
            "LIMIT #{limit}"
    )
    List<Map<String, Object>> bookRankByRecommendClick(@Param("days") int days, @Param("limit") int limit);

    @Select(
            "SELECT b.id AS bookId, b.title AS title, b.author AS author, b.cover_url AS coverUrl, COUNT(*) AS c\n" +
            "FROM favorites f\n" +
            "JOIN books b ON b.id = f.book_id\n" +
            "WHERE f.create_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY)\n" +
            "GROUP BY b.id, b.title, b.author, b.cover_url\n" +
            "ORDER BY c DESC\n" +
            "LIMIT #{limit}"
    )
    List<Map<String, Object>> bookRankByFavorite(@Param("days") int days, @Param("limit") int limit);

    @Select(
            "SELECT\n" +
            "  (SELECT COUNT(*) FROM recommend_exposure re WHERE re.recommend_type = #{type} AND re.exposure_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY)) AS exposureCount,\n" +
            "  (SELECT COUNT(*) FROM recommend_click rc WHERE rc.recommend_type = #{type} AND rc.click_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY)) AS clickCount,\n" +
            "  (SELECT COUNT(*) FROM favorites f\n" +
            "     JOIN recommend_click rc2 ON rc2.user_id = f.user_id AND rc2.book_id = f.book_id\n" +
            "     WHERE rc2.recommend_type = #{type}\n" +
            "       AND rc2.click_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY)\n" +
            "       AND f.create_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY)\n" +
            "  ) AS favoriteCount,\n" +
            "  (SELECT COUNT(*) FROM ratings r\n" +
            "     JOIN recommend_click rc3 ON rc3.user_id = r.user_id AND rc3.book_id = r.book_id\n" +
            "     WHERE rc3.recommend_type = #{type}\n" +
            "       AND rc3.click_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY)\n" +
            "       AND r.create_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY)\n" +
            "  ) AS ratingCount"
    )
    Map<String, Object> recommendFunnel(@Param("type") String recommendType, @Param("days") int days);
}


