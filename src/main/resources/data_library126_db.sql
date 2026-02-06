/*
 Navicat Premium Data Transfer

 Source Server         : 3307
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : localhost:3307
 Source Schema         : library126_db

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 27/01/2026 23:28:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for book_cold_start
-- ----------------------------
DROP TABLE IF EXISTS `book_cold_start`;
CREATE TABLE `book_cold_start`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `initial_exposure` int(11) NULL DEFAULT 0 COMMENT '初始曝光量（运营目标）',
  `manual_weight` double NULL DEFAULT 0 COMMENT '手动权重扶持（0-1）',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
  `is_enabled` tinyint(4) NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_book_id`(`book_id`) USING BTREE,
  INDEX `idx_enabled`(`is_enabled`) USING BTREE,
  INDEX `idx_time`(`start_time`, `end_time`) USING BTREE,
  CONSTRAINT `book_cold_start_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书冷启动配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_cold_start
-- ----------------------------
INSERT INTO `book_cold_start` VALUES (1, 6, 20, 1, '2026-01-21 18:41:00', '2026-01-30 18:41:00', 1, '2026-01-27 18:41:29', '2026-01-27 18:42:21');

-- ----------------------------
-- Table structure for book_relations
-- ----------------------------
DROP TABLE IF EXISTS `book_relations`;
CREATE TABLE `book_relations`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `related_book_id` bigint(20) NOT NULL COMMENT '关联图书ID',
  `relation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联类型（similar, sequel, prequel, series等）',
  `weight` double NULL DEFAULT 1 COMMENT '关联权重（0-1）',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_book_relation`(`book_id`, `related_book_id`, `relation_type`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  INDEX `idx_related_book_id`(`related_book_id`) USING BTREE,
  INDEX `idx_relation_type`(`relation_type`) USING BTREE,
  CONSTRAINT `book_relations_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `book_relations_ibfk_2` FOREIGN KEY (`related_book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书关联关系表（知识图谱）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_relations
-- ----------------------------

-- ----------------------------
-- Table structure for book_topics
-- ----------------------------
DROP TABLE IF EXISTS `book_topics`;
CREATE TABLE `book_topics`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `topic_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主题名称',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  INDEX `idx_topic_name`(`topic_name`) USING BTREE,
  CONSTRAINT `book_topics_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书主题关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_topics
-- ----------------------------
INSERT INTO `book_topics` VALUES (2, 7, 'Python');
INSERT INTO `book_topics` VALUES (6, 3, '软件工程');
INSERT INTO `book_topics` VALUES (7, 6, '软件工程');
INSERT INTO `book_topics` VALUES (8, 4, '软件工程');
INSERT INTO `book_topics` VALUES (9, 8, '机器学习');
INSERT INTO `book_topics` VALUES (10, 9, '机器学习');
INSERT INTO `book_topics` VALUES (12, 10, '科幻');
INSERT INTO `book_topics` VALUES (16, 1, '人工智能');
INSERT INTO `book_topics` VALUES (17, 1, 'Python');
INSERT INTO `book_topics` VALUES (18, 2, '算法');
INSERT INTO `book_topics` VALUES (20, 11, 'test');

-- ----------------------------
-- Table structure for book_views
-- ----------------------------
DROP TABLE IF EXISTS `book_views`;
CREATE TABLE `book_views`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID（未登录用户为NULL）',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `view_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '浏览时间',
  `duration` int(11) NULL DEFAULT 0 COMMENT '浏览时长（秒）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  INDEX `idx_view_time`(`view_time`) USING BTREE,
  CONSTRAINT `book_views_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `book_views_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书浏览记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_views
-- ----------------------------
INSERT INTO `book_views` VALUES (1, 2, 10, '2026-01-26 22:21:20', 0);
INSERT INTO `book_views` VALUES (2, 2, 10, '2026-01-26 22:21:20', 0);
INSERT INTO `book_views` VALUES (3, 2, 10, '2026-01-26 22:31:54', 0);
INSERT INTO `book_views` VALUES (4, 2, 10, '2026-01-26 22:31:54', 0);
INSERT INTO `book_views` VALUES (5, 2, 2, '2026-01-26 22:33:11', 0);
INSERT INTO `book_views` VALUES (6, 2, 2, '2026-01-26 22:33:11', 0);
INSERT INTO `book_views` VALUES (7, 2, 1, '2026-01-26 22:33:45', 0);
INSERT INTO `book_views` VALUES (8, 2, 1, '2026-01-26 22:33:46', 0);
INSERT INTO `book_views` VALUES (9, 2, 1, '2026-01-26 22:33:48', 0);
INSERT INTO `book_views` VALUES (10, 2, 1, '2026-01-26 22:33:48', 0);
INSERT INTO `book_views` VALUES (11, 2, 3, '2026-01-26 22:33:50', 0);
INSERT INTO `book_views` VALUES (12, 2, 3, '2026-01-26 22:33:50', 0);
INSERT INTO `book_views` VALUES (13, 2, 8, '2026-01-26 22:33:52', 0);
INSERT INTO `book_views` VALUES (14, 2, 8, '2026-01-26 22:33:53', 0);
INSERT INTO `book_views` VALUES (15, 2, 11, '2026-01-26 22:33:56', 0);
INSERT INTO `book_views` VALUES (16, 2, 11, '2026-01-26 22:33:56', 0);
INSERT INTO `book_views` VALUES (17, 2, 1, '2026-01-26 22:34:01', 0);
INSERT INTO `book_views` VALUES (18, 2, 1, '2026-01-26 22:34:01', 0);
INSERT INTO `book_views` VALUES (19, 2, 1, '2026-01-26 22:34:03', 0);
INSERT INTO `book_views` VALUES (20, 2, 1, '2026-01-26 22:34:03', 0);
INSERT INTO `book_views` VALUES (21, 2, 6, '2026-01-26 22:58:43', 0);
INSERT INTO `book_views` VALUES (22, 2, 6, '2026-01-26 22:58:43', 0);
INSERT INTO `book_views` VALUES (23, 2, 8, '2026-01-26 23:00:21', 0);
INSERT INTO `book_views` VALUES (24, 2, 8, '2026-01-26 23:00:21', 0);
INSERT INTO `book_views` VALUES (25, 2, 8, '2026-01-26 23:00:26', 0);
INSERT INTO `book_views` VALUES (26, 2, 8, '2026-01-26 23:00:26', 0);
INSERT INTO `book_views` VALUES (27, 2, 2, '2026-01-26 23:01:12', 0);
INSERT INTO `book_views` VALUES (28, 2, 2, '2026-01-26 23:01:12', 0);
INSERT INTO `book_views` VALUES (29, 2, 10, '2026-01-26 23:01:15', 0);
INSERT INTO `book_views` VALUES (30, 2, 10, '2026-01-26 23:01:15', 0);
INSERT INTO `book_views` VALUES (31, 2, 6, '2026-01-26 23:04:58', 0);
INSERT INTO `book_views` VALUES (32, 2, 6, '2026-01-26 23:04:58', 0);
INSERT INTO `book_views` VALUES (33, 2, 11, '2026-01-27 13:54:28', 0);
INSERT INTO `book_views` VALUES (34, 2, 11, '2026-01-27 13:54:28', 0);
INSERT INTO `book_views` VALUES (35, 2, 1, '2026-01-27 13:54:34', 0);
INSERT INTO `book_views` VALUES (36, 2, 1, '2026-01-27 13:54:34', 0);
INSERT INTO `book_views` VALUES (37, 2, 1, '2026-01-27 13:54:37', 0);
INSERT INTO `book_views` VALUES (38, 2, 1, '2026-01-27 13:54:37', 0);
INSERT INTO `book_views` VALUES (39, 2, 11, '2026-01-27 13:54:38', 0);
INSERT INTO `book_views` VALUES (40, 2, 11, '2026-01-27 13:54:38', 0);
INSERT INTO `book_views` VALUES (41, 2, 11, '2026-01-27 13:54:41', 0);
INSERT INTO `book_views` VALUES (42, 2, 11, '2026-01-27 13:54:41', 0);
INSERT INTO `book_views` VALUES (43, 2, 2, '2026-01-27 13:54:43', 0);
INSERT INTO `book_views` VALUES (44, 2, 2, '2026-01-27 13:54:43', 0);
INSERT INTO `book_views` VALUES (45, 2, 1, '2026-01-27 14:25:19', 0);
INSERT INTO `book_views` VALUES (46, 2, 1, '2026-01-27 14:25:19', 0);
INSERT INTO `book_views` VALUES (47, 2, 10, '2026-01-27 14:31:31', 0);
INSERT INTO `book_views` VALUES (48, 2, 10, '2026-01-27 14:31:31', 0);
INSERT INTO `book_views` VALUES (49, 2, 4, '2026-01-27 14:31:35', 0);
INSERT INTO `book_views` VALUES (50, 2, 4, '2026-01-27 14:31:35', 0);
INSERT INTO `book_views` VALUES (51, 2, 4, '2026-01-27 14:31:49', 0);
INSERT INTO `book_views` VALUES (52, 2, 4, '2026-01-27 14:31:49', 0);
INSERT INTO `book_views` VALUES (53, 2, 4, '2026-01-27 14:32:05', 0);
INSERT INTO `book_views` VALUES (54, 2, 4, '2026-01-27 14:32:05', 0);
INSERT INTO `book_views` VALUES (55, 2, 10, '2026-01-27 14:32:15', 0);
INSERT INTO `book_views` VALUES (56, 2, 10, '2026-01-27 14:32:15', 0);
INSERT INTO `book_views` VALUES (57, 2, 3, '2026-01-27 14:47:13', 0);
INSERT INTO `book_views` VALUES (58, 2, 3, '2026-01-27 14:47:13', 0);
INSERT INTO `book_views` VALUES (59, 2, 3, '2026-01-27 14:53:32', 0);
INSERT INTO `book_views` VALUES (60, 2, 3, '2026-01-27 14:53:32', 0);
INSERT INTO `book_views` VALUES (61, 2, 11, '2026-01-27 14:55:05', 0);
INSERT INTO `book_views` VALUES (62, 2, 11, '2026-01-27 14:55:05', 0);
INSERT INTO `book_views` VALUES (63, 2, 3, '2026-01-27 18:38:40', 0);
INSERT INTO `book_views` VALUES (64, 2, 3, '2026-01-27 18:38:40', 0);
INSERT INTO `book_views` VALUES (65, 2, 1, '2026-01-27 18:39:06', 0);
INSERT INTO `book_views` VALUES (66, 2, 1, '2026-01-27 18:39:06', 0);
INSERT INTO `book_views` VALUES (67, 2, 6, '2026-01-27 18:40:57', 0);
INSERT INTO `book_views` VALUES (68, 2, 6, '2026-01-27 18:40:57', 0);
INSERT INTO `book_views` VALUES (69, 3, 1, '2026-01-27 18:48:02', 0);
INSERT INTO `book_views` VALUES (70, 3, 1, '2026-01-27 18:48:03', 0);
INSERT INTO `book_views` VALUES (71, 3, 6, '2026-01-27 18:48:13', 0);
INSERT INTO `book_views` VALUES (72, 3, 6, '2026-01-27 18:48:13', 0);
INSERT INTO `book_views` VALUES (73, NULL, 2, '2026-01-27 19:00:21', 0);
INSERT INTO `book_views` VALUES (74, 100, 1, '2026-01-27 19:19:41', 0);
INSERT INTO `book_views` VALUES (75, 100, 1, '2026-01-27 19:19:41', 0);
INSERT INTO `book_views` VALUES (76, 100, 2, '2026-01-27 19:19:52', 0);
INSERT INTO `book_views` VALUES (77, 100, 2, '2026-01-27 19:19:52', 0);
INSERT INTO `book_views` VALUES (78, 100, 6, '2026-01-27 19:19:58', 0);
INSERT INTO `book_views` VALUES (79, 100, 6, '2026-01-27 19:19:58', 0);
INSERT INTO `book_views` VALUES (80, 100, 6, '2026-01-27 19:20:03', 0);
INSERT INTO `book_views` VALUES (81, 100, 6, '2026-01-27 19:20:03', 0);
INSERT INTO `book_views` VALUES (82, 100, 6, '2026-01-27 19:24:46', 0);
INSERT INTO `book_views` VALUES (83, 100, 6, '2026-01-27 19:24:46', 0);
INSERT INTO `book_views` VALUES (84, 100, 10, '2026-01-27 19:24:48', 0);
INSERT INTO `book_views` VALUES (85, 100, 10, '2026-01-27 19:24:48', 0);
INSERT INTO `book_views` VALUES (86, 1, 2, '2026-01-27 19:27:08', 0);
INSERT INTO `book_views` VALUES (87, 1, 2, '2026-01-27 19:27:08', 0);
INSERT INTO `book_views` VALUES (88, 100, 11, '2026-01-27 19:31:42', 0);
INSERT INTO `book_views` VALUES (89, 100, 11, '2026-01-27 19:31:42', 0);
INSERT INTO `book_views` VALUES (90, 100, 11, '2026-01-27 22:48:32', 0);
INSERT INTO `book_views` VALUES (91, 100, 11, '2026-01-27 22:48:32', 0);
INSERT INTO `book_views` VALUES (92, 100, 11, '2026-01-27 22:48:53', 0);
INSERT INTO `book_views` VALUES (93, 100, 11, '2026-01-27 22:48:53', 0);
INSERT INTO `book_views` VALUES (94, 100, 11, '2026-01-27 22:48:53', 0);
INSERT INTO `book_views` VALUES (95, 100, 11, '2026-01-27 22:48:53', 0);
INSERT INTO `book_views` VALUES (96, 100, 10, '2026-01-27 22:48:57', 0);
INSERT INTO `book_views` VALUES (97, 100, 10, '2026-01-27 22:48:57', 0);
INSERT INTO `book_views` VALUES (98, 100, 10, '2026-01-27 22:49:36', 0);
INSERT INTO `book_views` VALUES (99, 100, 10, '2026-01-27 22:49:36', 0);

-- ----------------------------
-- Table structure for booklist_books
-- ----------------------------
DROP TABLE IF EXISTS `booklist_books`;
CREATE TABLE `booklist_books`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `booklist_id` bigint(20) NOT NULL COMMENT '书单ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `add_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '添加时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_booklist_book`(`booklist_id`, `book_id`) USING BTREE,
  INDEX `idx_booklist_id`(`booklist_id`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  CONSTRAINT `booklist_books_ibfk_1` FOREIGN KEY (`booklist_id`) REFERENCES `booklists` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `booklist_books_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '书单图书关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of booklist_books
-- ----------------------------
INSERT INTO `booklist_books` VALUES (2, 1, 1, '2026-01-27 14:55:11');

-- ----------------------------
-- Table structure for booklists
-- ----------------------------
DROP TABLE IF EXISTS `booklists`;
CREATE TABLE `booklists`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '书单ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书单名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书单描述',
  `is_public` tinyint(4) NULL DEFAULT 0 COMMENT '是否公开：0-私有，1-公开',
  `book_count` int(11) NULL DEFAULT 0 COMMENT '图书数量',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_is_public`(`is_public`) USING BTREE,
  CONSTRAINT `booklists_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '书单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of booklists
-- ----------------------------
INSERT INTO `booklists` VALUES (1, 2, 'test', '11', 0, 1, '2026-01-26 22:58:35', '2026-01-26 22:58:35');

-- ----------------------------
-- Table structure for books
-- ----------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '图书ID',
  `isbn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ISBN',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书名',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  `publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出版社',
  `publish_date` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出版日期',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面URL',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '摘要',
  `avg_rating` decimal(3, 1) NULL DEFAULT 0.0 COMMENT '平均评分',
  `rating_count` int(11) NULL DEFAULT 0 COMMENT '评分人数',
  `borrow_count` int(11) NULL DEFAULT 0 COMMENT '借阅次数',
  `favorite_count` int(11) NULL DEFAULT 0 COMMENT '收藏次数',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `isbn`(`isbn`) USING BTREE,
  INDEX `idx_title`(`title`) USING BTREE,
  INDEX `idx_author`(`author`) USING BTREE,
  INDEX `idx_isbn`(`isbn`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_borrow_count`(`borrow_count`) USING BTREE,
  FULLTEXT INDEX `ft_title_author`(`title`, `author`, `summary`)
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of books
-- ----------------------------
INSERT INTO `books` VALUES (1, '9787111544937', '深度学习入门：基于Python的理论与实现', '斋藤康毅', '人民邮电出版社', '2018-07', '/uploads/covers/2026/01/26/ac490e71-78a6-4e6d-96af-8bef91f0f407.png', '本书是深度学习真正意义上的入门书，深入浅出地剖析了深度学习的原理和相关技术。', 4.0, 1, 121, 85, 1, '2026-01-26 14:32:03', '2026-01-26 15:30:13');
INSERT INTO `books` VALUES (2, '9787111213826', '算法导论（原书第3版）', 'Thomas H. Cormen', '机械工业出版社', '2013-01', '/uploads/covers/2026/01/26/533aa6ba-914c-493a-be6c-fbbe3ec2726e.png', '算法导论（原书第3版）是一本关于计算机算法的经典教材。', 9.0, 203, 99, 73, 1, '2026-01-26 14:32:03', '2026-01-26 17:30:10');
INSERT INTO `books` VALUES (3, '9787111407010', '人月神话', 'Frederick P. Brooks Jr.', '机械工业出版社', '2012-11', 'https://img.shetu66.com/2023/04/04/1680590478322274.jpg', '软件工程领域的经典著作，探讨了软件项目管理中的各种问题。', 9.0, 134, 77, 58, 1, '2026-01-26 14:32:03', '2026-01-26 15:16:49');
INSERT INTO `books` VALUES (4, '9787111075776', '设计模式：可复用面向对象软件的基础', 'GoF', '机械工业出版社', '2000-09', 'https://img.shetu66.com/2023/04/04/1680590478322274.jpg', '设计模式经典著作，介绍了23种常用的设计模式。', 9.0, 187, 66, 49, 1, '2026-01-26 14:32:03', '2026-01-26 15:16:49');
INSERT INTO `books` VALUES (5, '9787111213827', '黑客与画家', 'Paul Graham', '人民邮电出版社', '2011-04', 'https://img.shetu66.com/2023/04/04/1680590478322274.jpg', '硅谷创业教父Paul Graham的文集，探讨了编程、创业、设计等话题。', 9.0, 98, 54, 41, 1, '2026-01-26 14:32:03', '2026-01-26 15:16:49');
INSERT INTO `books` VALUES (6, '9787111213828', '代码大全（第2版）', 'Steve McConnell', '电子工业出版社', '2006-03', 'https://img.shetu66.com/2023/04/04/1680590478322274.jpg', '软件构建的实践指南，涵盖了软件开发的各个方面。', 3.0, 1, 44, 33, 1, '2026-01-26 14:32:03', '2026-01-26 15:16:49');
INSERT INTO `books` VALUES (7, '9787111213829', 'Python编程：从入门到实践', 'Eric Matthes', '人民邮电出版社', '2016-07', 'https://img.shetu66.com/2023/04/04/1680590478322274.jpg', '一本针对所有层次Python读者而作的Python入门书。', 9.2, 234, 89, 67, 1, '2026-01-26 14:32:03', '2026-01-26 15:16:49');
INSERT INTO `books` VALUES (8, '9787111213830', '机器学习实战', 'Peter Harrington', '人民邮电出版社', '2013-06', 'https://img.shetu66.com/2023/04/04/1680590478322274.jpg', '通过实例讲解机器学习算法的实际应用。', 8.8, 178, 67, 51, 1, '2026-01-26 14:32:03', '2026-01-26 15:16:49');
INSERT INTO `books` VALUES (9, '9787111213831', '统计学习方法', '李航', '清华大学出版社', '2012-03', 'https://img.shetu66.com/2023/04/04/1680590478322274.jpg', '统计学习方法的经典教材，系统介绍了统计学习的基本理论和方法。', 9.1, 167, 56, 43, 1, '2026-01-26 14:32:03', '2026-01-26 15:16:49');
INSERT INTO `books` VALUES (10, '9787111213832', '三体', '刘慈欣', '重庆出版社', '2008-01', 'https://img.shetu66.com/2023/04/04/1680590478322274.jpg', '中国科幻文学的里程碑之作，雨果奖获奖作品。', 9.5, 456, 236, 189, 1, '2026-01-26 14:32:03', '2026-01-26 15:16:49');
INSERT INTO `books` VALUES (11, '97871112138322', 'test', 'test', 'test', 'test', '/uploads/covers/2026/01/26/ad683fd5-9012-4c25-a44c-0b84f5f6dfd9.jpg', 'test', 0.0, 0, 1, 1, 1, '2026-01-26 17:30:42', '2026-01-27 14:18:08');

-- ----------------------------
-- Table structure for borrow_records
-- ----------------------------
DROP TABLE IF EXISTS `borrow_records`;
CREATE TABLE `borrow_records`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `apply_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '申请时间',
  `borrow_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '借阅时间',
  `expected_return_time` datetime(0) NULL DEFAULT NULL COMMENT '预计归还时间',
  `return_apply_time` datetime(0) NULL DEFAULT NULL COMMENT '还书申请时间',
  `return_time` datetime(0) NULL DEFAULT NULL COMMENT '归还时间',
  `overdue_days` int(11) NULL DEFAULT 0 COMMENT '逾期天数',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-已归还，1-借阅中',
  `audit_status` tinyint(4) NULL DEFAULT 0 COMMENT '审核状态：0-待审核，1-已通过，2-已拒绝',
  `audit_time` datetime(0) NULL DEFAULT NULL COMMENT '审核时间',
  `audit_admin_id` bigint(20) NULL DEFAULT NULL COMMENT '审核管理员ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  INDEX `idx_borrow_time`(`borrow_time`) USING BTREE,
  INDEX `idx_audit_status`(`audit_status`) USING BTREE,
  INDEX `idx_expected_return_time`(`expected_return_time`) USING BTREE,
  CONSTRAINT `borrow_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `borrow_records_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '借阅记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of borrow_records
-- ----------------------------
INSERT INTO `borrow_records` VALUES (1, 2, 2, '2026-01-27 14:23:24', '2026-01-27 14:25:54', '2026-02-26 14:25:54', NULL, '2026-01-27 14:25:45', 0, 1, 1, '2026-01-27 14:25:54', 1);
INSERT INTO `borrow_records` VALUES (2, 2, 10, '2026-01-27 14:23:24', '2026-01-27 14:54:46', '2026-02-26 14:54:46', NULL, NULL, 0, 1, 1, '2026-01-27 14:54:46', 1);
INSERT INTO `borrow_records` VALUES (3, 2, 11, '2026-01-27 14:23:24', '2026-01-26 22:33:58', NULL, NULL, NULL, 0, 1, 0, NULL, NULL);
INSERT INTO `borrow_records` VALUES (4, 2, 6, '2026-01-27 14:23:24', '2026-01-26 22:58:44', NULL, NULL, '2026-01-26 23:04:56', 0, 0, 0, NULL, NULL);
INSERT INTO `borrow_records` VALUES (5, 2, 1, '2026-01-27 14:23:24', '2026-01-27 13:54:35', NULL, NULL, '2026-01-27 14:25:23', 0, 0, 0, NULL, NULL);
INSERT INTO `borrow_records` VALUES (6, 2, 4, '2026-01-27 14:31:36', '2026-01-27 14:31:48', '2026-02-26 14:31:48', '2026-01-27 14:31:57', '2026-01-27 14:32:02', 0, 0, 1, '2026-01-27 14:31:48', 1);
INSERT INTO `borrow_records` VALUES (7, 2, 3, '2026-01-27 14:47:17', '2026-01-27 14:53:26', '2026-01-29 23:59:59', '2026-01-27 14:53:36', NULL, 0, 1, 1, '2026-01-27 14:53:26', 1);
INSERT INTO `borrow_records` VALUES (8, 100, 1, '2026-01-27 19:19:45', '2026-01-27 19:19:44', '2026-01-27 23:59:59', NULL, NULL, 0, 0, 0, NULL, NULL);
INSERT INTO `borrow_records` VALUES (9, 100, 6, '2026-01-27 19:20:03', '2026-01-27 19:20:02', '2026-01-30 23:59:59', NULL, NULL, 0, 0, 0, NULL, NULL);

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `rating` tinyint(4) NULL DEFAULT NULL COMMENT '评分（1-5）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '审核状态：0-待审核，1-已通过，2-已拒绝',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES (1, 2, 2, 3, '1', 1, '2026-01-26 17:56:33', '2026-01-26 17:56:33');
INSERT INTO `comments` VALUES (2, 2, 10, 5, '12', 1, '2026-01-26 18:05:29', '2026-01-26 18:05:29');
INSERT INTO `comments` VALUES (3, 2, 10, 5, '22', 2, '2026-01-26 22:32:11', '2026-01-26 22:33:11');
INSERT INTO `comments` VALUES (4, 100, 2, 5, '222', 1, '2026-01-27 19:19:55', '2026-01-27 19:27:05');

-- ----------------------------
-- Table structure for favorites
-- ----------------------------
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_book`(`user_id`, `book_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  CONSTRAINT `favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `favorites_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorites
-- ----------------------------
INSERT INTO `favorites` VALUES (1, 2, 1, '2026-01-26 15:12:24');
INSERT INTO `favorites` VALUES (2, 2, 2, '2026-01-26 15:16:43');
INSERT INTO `favorites` VALUES (3, 2, 11, '2026-01-26 17:31:18');
INSERT INTO `favorites` VALUES (4, 2, 6, '2026-01-26 22:58:48');
INSERT INTO `favorites` VALUES (6, 6, 3, '2026-01-23 17:50:35');
INSERT INTO `favorites` VALUES (7, 8, 6, '2026-01-24 17:50:35');
INSERT INTO `favorites` VALUES (8, 11, 9, '2026-01-26 17:50:35');
INSERT INTO `favorites` VALUES (9, 14, 10, '2026-01-27 17:50:35');

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `feedback_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '反馈类型（not_interested, negative_feedback等）',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '反馈原因',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `feedback_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feedback
-- ----------------------------
INSERT INTO `feedback` VALUES (1, 2, 2, 'not_interested', '不感兴趣', '2026-01-26 20:11:35');

-- ----------------------------
-- Table structure for ratings
-- ----------------------------
DROP TABLE IF EXISTS `ratings`;
CREATE TABLE `ratings`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `score` tinyint(4) NOT NULL COMMENT '评分（1-5）',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_book`(`user_id`, `book_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  CONSTRAINT `ratings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `ratings_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评分表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ratings
-- ----------------------------
INSERT INTO `ratings` VALUES (1, 2, 1, 4, '2026-01-26 22:34:03', '2026-01-26 22:34:03');
INSERT INTO `ratings` VALUES (2, 3, 1, 5, '2026-01-22 17:50:35', '2026-01-22 17:50:35');
INSERT INTO `ratings` VALUES (3, 6, 3, 4, '2026-01-24 17:50:35', '2026-01-24 17:50:35');
INSERT INTO `ratings` VALUES (4, 11, 9, 5, '2026-01-26 17:50:35', '2026-01-26 17:50:35');
INSERT INTO `ratings` VALUES (5, 100, 6, 3, '2026-01-27 19:20:03', '2026-01-27 19:20:03');

-- ----------------------------
-- Table structure for recommend_analytics
-- ----------------------------
DROP TABLE IF EXISTS `recommend_analytics`;
CREATE TABLE `recommend_analytics`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `recommend_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '推荐类型（home, similar, user_also_read等）',
  `date` date NOT NULL COMMENT '统计日期',
  `exposure_count` bigint(20) NULL DEFAULT 0 COMMENT '曝光次数',
  `click_count` bigint(20) NULL DEFAULT 0 COMMENT '点击次数',
  `ctr` double NULL DEFAULT 0 COMMENT '点击率',
  `avg_rating` double NULL DEFAULT 0 COMMENT '平均评分',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type_date`(`recommend_type`, `date`) USING BTREE,
  INDEX `idx_recommend_type`(`recommend_type`) USING BTREE,
  INDEX `idx_date`(`date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '推荐效果分析表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recommend_analytics
-- ----------------------------

-- ----------------------------
-- Table structure for recommend_blacklist
-- ----------------------------
DROP TABLE IF EXISTS `recommend_blacklist`;
CREATE TABLE `recommend_blacklist`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `recommend_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推荐类型（为空表示全局）',
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推荐位（为空表示全位置）',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '屏蔽原因',
  `is_enabled` tinyint(4) NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_blacklist`(`book_id`, `recommend_type`, `position`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  INDEX `idx_enabled`(`is_enabled`) USING BTREE,
  INDEX `idx_type_pos`(`recommend_type`, `position`) USING BTREE,
  CONSTRAINT `recommend_blacklist_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '推荐黑名单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recommend_blacklist
-- ----------------------------
INSERT INTO `recommend_blacklist` VALUES (2, 3, NULL, NULL, '1', 1, '2026-01-27 18:38:52');
INSERT INTO `recommend_blacklist` VALUES (3, 1, NULL, NULL, '不行', 1, '2026-01-27 18:39:22');

-- ----------------------------
-- Table structure for recommend_click
-- ----------------------------
DROP TABLE IF EXISTS `recommend_click`;
CREATE TABLE `recommend_click`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `recommend_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推荐类型',
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推荐位置',
  `click_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '点击时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  INDEX `idx_click_time`(`click_time`) USING BTREE,
  CONSTRAINT `recommend_click_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `recommend_click_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '推荐点击记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recommend_click
-- ----------------------------
INSERT INTO `recommend_click` VALUES (1, 3, 1, 'home', 'home_0', '2026-01-20 17:50:35');
INSERT INTO `recommend_click` VALUES (2, 4, 1, 'home', 'home_0', '2026-01-21 17:50:35');
INSERT INTO `recommend_click` VALUES (3, 5, 1, 'home', 'home_0', '2026-01-22 17:50:35');
INSERT INTO `recommend_click` VALUES (4, 6, 3, 'home', 'home_2', '2026-01-22 17:50:35');
INSERT INTO `recommend_click` VALUES (5, 7, 3, 'home', 'home_2', '2026-01-23 17:50:35');
INSERT INTO `recommend_click` VALUES (6, 8, 6, 'home', 'home_5', '2026-01-23 17:50:35');
INSERT INTO `recommend_click` VALUES (7, 9, 6, 'home', 'home_5', '2026-01-24 17:50:35');
INSERT INTO `recommend_click` VALUES (8, 10, 6, 'home', 'home_5', '2026-01-25 17:50:35');
INSERT INTO `recommend_click` VALUES (9, 11, 9, 'home', 'home_8', '2026-01-25 17:50:35');
INSERT INTO `recommend_click` VALUES (10, 12, 9, 'home', 'home_8', '2026-01-26 17:50:35');
INSERT INTO `recommend_click` VALUES (11, 13, 10, 'home', 'home_9', '2026-01-26 17:50:35');
INSERT INTO `recommend_click` VALUES (12, 14, 10, 'home', 'home_9', '2026-01-27 17:50:35');
INSERT INTO `recommend_click` VALUES (13, 100, 6, 'home', 'home_guess_you_like', '2026-01-27 19:24:46');
INSERT INTO `recommend_click` VALUES (14, 100, 10, 'home', 'home_guess_you_like', '2026-01-27 19:24:48');
INSERT INTO `recommend_click` VALUES (15, 100, 10, 'home', 'home_guess_you_like', '2026-01-27 22:48:57');

-- ----------------------------
-- Table structure for recommend_exposure
-- ----------------------------
DROP TABLE IF EXISTS `recommend_exposure`;
CREATE TABLE `recommend_exposure`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `recommend_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推荐类型',
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推荐位置',
  `exposure_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '曝光时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  INDEX `idx_exposure_time`(`exposure_time`) USING BTREE,
  CONSTRAINT `recommend_exposure_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `recommend_exposure_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '推荐曝光记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recommend_exposure
-- ----------------------------
INSERT INTO `recommend_exposure` VALUES (1, 3, 1, 'home', 'home_0', '2026-01-20 17:50:35');
INSERT INTO `recommend_exposure` VALUES (2, 4, 1, 'home', 'home_0', '2026-01-21 17:50:35');
INSERT INTO `recommend_exposure` VALUES (3, 5, 2, 'home', 'home_1', '2026-01-21 17:50:35');
INSERT INTO `recommend_exposure` VALUES (4, 6, 3, 'home', 'home_2', '2026-01-22 17:50:35');
INSERT INTO `recommend_exposure` VALUES (5, 7, 4, 'home', 'home_3', '2026-01-22 17:50:35');
INSERT INTO `recommend_exposure` VALUES (6, 8, 5, 'home', 'home_4', '2026-01-23 17:50:35');
INSERT INTO `recommend_exposure` VALUES (7, 9, 6, 'home', 'home_5', '2026-01-23 17:50:35');
INSERT INTO `recommend_exposure` VALUES (8, 10, 7, 'home', 'home_6', '2026-01-24 17:50:35');
INSERT INTO `recommend_exposure` VALUES (9, 11, 8, 'home', 'home_7', '2026-01-24 17:50:35');
INSERT INTO `recommend_exposure` VALUES (10, 12, 9, 'home', 'home_8', '2026-01-25 17:50:35');
INSERT INTO `recommend_exposure` VALUES (11, 13, 10, 'home', 'home_9', '2026-01-25 17:50:35');
INSERT INTO `recommend_exposure` VALUES (12, 14, 1, 'home', 'home_0', '2026-01-26 17:50:35');
INSERT INTO `recommend_exposure` VALUES (13, 15, 3, 'home', 'home_2', '2026-01-26 17:50:35');
INSERT INTO `recommend_exposure` VALUES (14, 16, 6, 'home', 'home_5', '2026-01-27 17:50:35');
INSERT INTO `recommend_exposure` VALUES (15, 100, 11, 'home-new', 'home_new_books', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (16, 100, 2, 'home-new', 'home_new_books', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (17, 100, 4, 'home-new', 'home_new_books', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (18, 100, 6, 'home-new', 'home_new_books', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (19, 100, 5, 'home-new', 'home_new_books', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (20, 100, 7, 'home-new', 'home_new_books', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (21, 100, 4, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (22, 100, 8, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (23, 100, 7, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (24, 100, 2, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (25, 100, 9, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (26, 100, 10, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (27, 100, 6, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (28, 100, 11, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (29, 100, 5, 'long-tail', 'home_long_tail', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (30, 100, 8, 'long-tail', 'home_long_tail', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (31, 100, 5, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (32, 100, 11, 'long-tail', 'home_long_tail', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (33, 100, 6, 'home', 'home_guess_you_like', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (34, 100, 4, 'long-tail', 'home_long_tail', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (35, 100, 9, 'long-tail', 'home_long_tail', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (36, 100, 10, 'long-tail', 'home_long_tail', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (37, 100, 2, 'long-tail', 'home_long_tail', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (38, 100, 7, 'long-tail', 'home_long_tail', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (39, 100, 7, 'home', 'home_guess_you_like', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (40, 100, 1, 'home', 'home_guess_you_like', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (41, 100, 10, 'home', 'home_guess_you_like', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (42, 100, 2, 'home', 'home_guess_you_like', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (43, 100, 8, 'home', 'home_guess_you_like', '2026-01-27 19:24:45');
INSERT INTO `recommend_exposure` VALUES (44, 100, 11, 'home-new', 'home_new_books', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (45, 100, 2, 'home-new', 'home_new_books', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (46, 100, 6, 'home-new', 'home_new_books', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (47, 100, 5, 'home-new', 'home_new_books', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (48, 100, 4, 'home-new', 'home_new_books', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (49, 100, 7, 'home-new', 'home_new_books', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (50, 100, 7, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (51, 100, 8, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (52, 100, 4, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (53, 100, 2, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (54, 100, 9, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (55, 100, 10, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (56, 100, 5, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (57, 100, 10, 'long-tail', 'home_long_tail', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (58, 100, 11, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (59, 100, 7, 'long-tail', 'home_long_tail', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (60, 100, 6, 'home-hot', 'home_hot_rank', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (61, 100, 8, 'long-tail', 'home_long_tail', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (62, 100, 11, 'long-tail', 'home_long_tail', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (63, 100, 5, 'long-tail', 'home_long_tail', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (64, 100, 4, 'long-tail', 'home_long_tail', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (65, 100, 2, 'long-tail', 'home_long_tail', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (66, 100, 9, 'long-tail', 'home_long_tail', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (67, 100, 6, 'home', 'home_guess_you_like', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (68, 100, 1, 'home', 'home_guess_you_like', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (69, 100, 7, 'home', 'home_guess_you_like', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (70, 100, 2, 'home', 'home_guess_you_like', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (71, 100, 8, 'home', 'home_guess_you_like', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (72, 100, 10, 'home', 'home_guess_you_like', '2026-01-27 19:24:48');
INSERT INTO `recommend_exposure` VALUES (73, 100, 11, 'home-new', 'home_new_books', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (74, 100, 4, 'home-new', 'home_new_books', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (75, 100, 2, 'home-new', 'home_new_books', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (76, 100, 5, 'home-new', 'home_new_books', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (77, 100, 6, 'home-new', 'home_new_books', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (78, 100, 7, 'home-new', 'home_new_books', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (79, 100, 10, 'home-hot', 'home_hot_rank', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (80, 100, 7, 'home-hot', 'home_hot_rank', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (81, 100, 2, 'home-hot', 'home_hot_rank', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (82, 100, 9, 'home-hot', 'home_hot_rank', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (83, 100, 8, 'home-hot', 'home_hot_rank', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (84, 100, 4, 'home-hot', 'home_hot_rank', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (85, 100, 11, 'home-hot', 'home_hot_rank', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (86, 100, 8, 'long-tail', 'home_long_tail', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (87, 100, 7, 'long-tail', 'home_long_tail', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (88, 100, 5, 'home-hot', 'home_hot_rank', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (89, 100, 6, 'home-hot', 'home_hot_rank', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (90, 100, 10, 'long-tail', 'home_long_tail', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (91, 100, 6, 'home', 'home_guess_you_like', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (92, 100, 2, 'long-tail', 'home_long_tail', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (93, 100, 9, 'long-tail', 'home_long_tail', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (94, 100, 5, 'long-tail', 'home_long_tail', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (95, 100, 11, 'long-tail', 'home_long_tail', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (96, 100, 4, 'long-tail', 'home_long_tail', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (97, 100, 1, 'home', 'home_guess_you_like', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (98, 100, 8, 'home', 'home_guess_you_like', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (99, 100, 10, 'home', 'home_guess_you_like', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (100, 100, 7, 'home', 'home_guess_you_like', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (101, 100, 2, 'home', 'home_guess_you_like', '2026-01-27 19:27:42');
INSERT INTO `recommend_exposure` VALUES (102, 100, 11, 'home-new', 'home_new_books', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (103, 100, 2, 'home-new', 'home_new_books', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (104, 100, 4, 'home-new', 'home_new_books', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (105, 100, 5, 'home-new', 'home_new_books', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (106, 100, 6, 'home-new', 'home_new_books', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (107, 100, 7, 'home-new', 'home_new_books', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (108, 100, 4, 'long-tail', 'home_long_tail', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (109, 100, 10, 'long-tail', 'home_long_tail', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (110, 100, 5, 'long-tail', 'home_long_tail', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (111, 100, 8, 'long-tail', 'home_long_tail', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (112, 100, 9, 'long-tail', 'home_long_tail', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (113, 100, 11, 'long-tail', 'home_long_tail', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (114, 100, 8, 'home-hot', 'home_hot_rank', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (115, 100, 7, 'home-hot', 'home_hot_rank', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (116, 100, 10, 'home-hot', 'home_hot_rank', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (117, 100, 2, 'home-hot', 'home_hot_rank', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (118, 100, 7, 'long-tail', 'home_long_tail', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (119, 100, 2, 'long-tail', 'home_long_tail', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (120, 100, 4, 'home-hot', 'home_hot_rank', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (121, 100, 5, 'home-hot', 'home_hot_rank', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (122, 100, 6, 'home-hot', 'home_hot_rank', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (123, 100, 6, 'home', 'home_guess_you_like', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (124, 100, 9, 'home-hot', 'home_hot_rank', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (125, 100, 11, 'home-hot', 'home_hot_rank', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (126, 100, 2, 'home', 'home_guess_you_like', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (127, 100, 8, 'home', 'home_guess_you_like', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (128, 100, 10, 'home', 'home_guess_you_like', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (129, 100, 7, 'home', 'home_guess_you_like', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (130, 100, 1, 'home', 'home_guess_you_like', '2026-01-27 22:48:56');
INSERT INTO `recommend_exposure` VALUES (131, 100, 11, 'home-new', 'home_new_books', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (132, 100, 2, 'home-new', 'home_new_books', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (133, 100, 4, 'home-new', 'home_new_books', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (134, 100, 6, 'home-new', 'home_new_books', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (135, 100, 5, 'home-new', 'home_new_books', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (136, 100, 7, 'home-new', 'home_new_books', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (137, 100, 11, 'long-tail', 'home_long_tail', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (138, 100, 5, 'long-tail', 'home_long_tail', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (139, 100, 9, 'long-tail', 'home_long_tail', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (140, 100, 4, 'long-tail', 'home_long_tail', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (141, 100, 8, 'long-tail', 'home_long_tail', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (142, 100, 7, 'long-tail', 'home_long_tail', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (143, 100, 7, 'home-hot', 'home_hot_rank', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (144, 100, 2, 'home-hot', 'home_hot_rank', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (145, 100, 10, 'home-hot', 'home_hot_rank', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (146, 100, 10, 'long-tail', 'home_long_tail', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (147, 100, 2, 'long-tail', 'home_long_tail', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (148, 100, 8, 'home-hot', 'home_hot_rank', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (149, 100, 6, 'home-hot', 'home_hot_rank', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (150, 100, 9, 'home-hot', 'home_hot_rank', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (151, 100, 11, 'home-hot', 'home_hot_rank', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (152, 100, 4, 'home-hot', 'home_hot_rank', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (153, 100, 5, 'home-hot', 'home_hot_rank', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (154, 100, 10, 'home', 'home_guess_you_like', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (155, 100, 7, 'home', 'home_guess_you_like', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (156, 100, 6, 'home', 'home_guess_you_like', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (157, 100, 8, 'home', 'home_guess_you_like', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (158, 100, 2, 'home', 'home_guess_you_like', '2026-01-27 22:49:46');
INSERT INTO `recommend_exposure` VALUES (159, 100, 1, 'home', 'home_guess_you_like', '2026-01-27 22:49:46');

-- ----------------------------
-- Table structure for recommend_positions
-- ----------------------------
DROP TABLE IF EXISTS `recommend_positions`;
CREATE TABLE `recommend_positions`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `position_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '推荐位标识（home_recommend, new_book等）',
  `position_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '推荐位名称',
  `book_id` bigint(20) NULL DEFAULT NULL COMMENT '推荐图书ID（手动配置）',
  `priority` int(11) NULL DEFAULT 0 COMMENT '优先级（数字越大优先级越高）',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
  `is_enabled` tinyint(4) NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `position_key`(`position_key`) USING BTREE,
  INDEX `book_id`(`book_id`) USING BTREE,
  INDEX `idx_position_key`(`position_key`) USING BTREE,
  INDEX `idx_is_enabled`(`is_enabled`) USING BTREE,
  INDEX `idx_priority`(`priority`) USING BTREE,
  CONSTRAINT `recommend_positions_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '推荐位配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recommend_positions
-- ----------------------------

-- ----------------------------
-- Table structure for recommend_reasons
-- ----------------------------
DROP TABLE IF EXISTS `recommend_reasons`;
CREATE TABLE `recommend_reasons`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `recommend_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '推荐类型',
  `reason_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '理由类型（borrow_history, topic_match, rating_rank等）',
  `reason_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '理由内容（JSON格式）',
  `weight` double NULL DEFAULT 1 COMMENT '理由权重',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  INDEX `idx_recommend_type`(`recommend_type`) USING BTREE,
  CONSTRAINT `recommend_reasons_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `recommend_reasons_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '推荐解释详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recommend_reasons
-- ----------------------------

-- ----------------------------
-- Table structure for recommend_strategy
-- ----------------------------
DROP TABLE IF EXISTS `recommend_strategy`;
CREATE TABLE `recommend_strategy`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `strategy_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '策略键',
  `strategy_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '策略值（JSON格式）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '策略描述',
  `is_enabled` tinyint(4) NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `strategy_key`(`strategy_key`) USING BTREE,
  INDEX `idx_strategy_key`(`strategy_key`) USING BTREE,
  INDEX `idx_is_enabled`(`is_enabled`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '推荐策略配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recommend_strategy
-- ----------------------------
INSERT INTO `recommend_strategy` VALUES (1, 'hot_recommend_ratio', '0.8', '热门推荐比例', 1, '2026-01-26 22:21:06', '2026-01-27 14:09:25');
INSERT INTO `recommend_strategy` VALUES (2, 'global_diversity_weight', '0.3', '全局多样性权重', 1, '2026-01-26 22:21:06', '2026-01-27 14:09:25');
INSERT INTO `recommend_strategy` VALUES (3, 'cold_start_hot_ratio', '0.8', '冷启动热门推荐比例', 1, '2026-01-26 22:21:06', '2026-01-27 14:09:25');
INSERT INTO `recommend_strategy` VALUES (4, 'long_tail_ratio', '0.2', '长尾推荐比例', 1, '2026-01-26 22:21:06', '2026-01-27 14:09:25');
INSERT INTO `recommend_strategy` VALUES (5, 'long_tail_threshold', '10', '长尾图书阈值', 1, '2026-01-26 22:21:06', '2026-01-27 14:09:25');
INSERT INTO `recommend_strategy` VALUES (6, 'enable_long_tail', 'true', '是否启用长尾推荐', 1, '2026-01-26 22:21:06', '2026-01-27 14:09:25');

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称（ADMIN, USER）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (1, 'ADMIN', '管理员');
INSERT INTO `roles` VALUES (2, 'USER', '普通用户');

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置描述',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key`) USING BTREE,
  INDEX `idx_config_key`(`config_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, 'redis.enabled', 'true', 'Redis缓存开关', '2026-01-26 15:25:00', '2026-01-26 14:32:03');
INSERT INTO `system_config` VALUES (2, 'cache.ttl', '3600', '缓存TTL（秒）', '2026-01-26 15:25:00', '2026-01-26 14:32:03');
INSERT INTO `system_config` VALUES (3, 'model.version', 'v2.4.1', '推荐模型版本', '2026-01-26 15:25:00', '2026-01-26 14:32:03');

-- ----------------------------
-- Table structure for topic_relations
-- ----------------------------
DROP TABLE IF EXISTS `topic_relations`;
CREATE TABLE `topic_relations`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `topic_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主题名称',
  `related_topic_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联主题名称',
  `relation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联类型（related, parent, child等）',
  `weight` double NULL DEFAULT 1 COMMENT '关联权重（0-1）',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_topic_relation`(`topic_name`, `related_topic_name`, `relation_type`) USING BTREE,
  INDEX `idx_topic_name`(`topic_name`) USING BTREE,
  INDEX `idx_related_topic_name`(`related_topic_name`) USING BTREE,
  INDEX `idx_relation_type`(`relation_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '主题关联关系表（知识图谱）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of topic_relations
-- ----------------------------

-- ----------------------------
-- Table structure for topics
-- ----------------------------
DROP TABLE IF EXISTS `topics`;
CREATE TABLE `topics`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主题ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主题名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主题描述',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标类名（Font Awesome）',
  `gradient` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '渐变色类名（Tailwind CSS）',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '主题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of topics
-- ----------------------------
INSERT INTO `topics` VALUES (1, '人工智能', '探索机器学习、神经网络以及AI伦理的深度思考。', 'fas fa-robot', 'bg-gradient-to-br from-indigo-500 to-purple-600', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (2, '机器学习', '从算法原理到实际应用，掌握智能系统的核心。', 'fas fa-brain', 'bg-gradient-to-br from-blue-500 to-cyan-600', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (3, 'Python', '简洁优雅的编程语言，从入门到精通。', 'fab fa-python', 'bg-gradient-to-br from-yellow-400 to-orange-500', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (4, '算法', '数据结构与算法的经典教材，提升编程能力。', 'fas fa-calculator', 'bg-gradient-to-br from-teal-500 to-green-600', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (5, '软件工程', '软件开发的实践指南，从设计到实现。', 'fas fa-laptop-code', 'bg-gradient-to-br from-blue-400 to-cyan-500', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (6, '设计', '从包豪斯到极简主义，探寻视觉传达的底层逻辑。', 'fas fa-palette', 'bg-gradient-to-br from-pink-500 to-rose-600', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (7, '商业', '底层逻辑、创新管理与数字化转型的实战指南。', 'fas fa-chart-line', 'bg-gradient-to-br from-teal-500 to-green-600', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (8, '科幻', '刘慈欣、阿西莫夫构建的宏大未来世界。', 'fas fa-rocket', 'bg-gradient-to-br from-blue-500 to-cyan-600', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (9, '文学', '经典文学作品，感受文字的力量与美感。', 'fas fa-book-open', 'bg-gradient-to-br from-amber-500 to-yellow-600', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (10, '历史', '回顾过去，理解现在，展望未来。', 'fas fa-landmark', 'bg-gradient-to-br from-stone-500 to-gray-600', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (11, '哲学', '思考人生、世界和存在的根本问题。', 'fas fa-lightbulb', 'bg-gradient-to-br from-violet-500 to-purple-600', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (12, '心理学', '探索人类行为与思维的奥秘。', 'fas fa-user-friends', 'bg-gradient-to-br from-rose-500 to-pink-600', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (13, '技术', '前沿技术趋势，掌握未来发展方向。', 'fas fa-laptop-code', 'bg-gradient-to-br from-slate-500 to-gray-600', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');
INSERT INTO `topics` VALUES (14, '自我提升', '个人成长与能力提升的实用指南。', 'fas fa-user-graduate', 'bg-gradient-to-br from-emerald-500 to-teal-600', 1, '2026-01-26 15:21:59', '2026-01-26 15:21:59');

-- ----------------------------
-- Table structure for user_behaviors
-- ----------------------------
DROP TABLE IF EXISTS `user_behaviors`;
CREATE TABLE `user_behaviors`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `behavior_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '行为类型：VIEW-浏览, FAVORITE-收藏, RATING-评分, BORROW-借阅',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `behavior_data` json NULL COMMENT '行为数据（JSON格式，如评分分数、浏览时长等）',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '行为时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_book_id`(`book_id`) USING BTREE,
  INDEX `idx_behavior_type`(`behavior_type`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  CONSTRAINT `user_behaviors_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_behaviors_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户行为日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_behaviors
-- ----------------------------
INSERT INTO `user_behaviors` VALUES (1, 2, 'VIEW', 3, '{\"duration\": 0}', '2026-01-27 14:47:13');
INSERT INTO `user_behaviors` VALUES (2, 2, 'BORROW', 3, '{\"stage\": \"APPLY\", \"expectedReturnTime\": \"2026-01-29 23:59:59\"}', '2026-01-27 14:47:17');
INSERT INTO `user_behaviors` VALUES (3, 2, 'VIEW', 3, '{\"duration\": 0}', '2026-01-27 14:53:32');
INSERT INTO `user_behaviors` VALUES (4, 2, 'VIEW', 11, '{\"duration\": 0}', '2026-01-27 14:55:05');
INSERT INTO `user_behaviors` VALUES (5, 2, 'VIEW', 3, '{\"duration\": 0}', '2026-01-27 18:38:40');
INSERT INTO `user_behaviors` VALUES (6, 2, 'VIEW', 1, '{\"duration\": 0}', '2026-01-27 18:39:06');
INSERT INTO `user_behaviors` VALUES (7, 2, 'VIEW', 6, '{\"duration\": 0}', '2026-01-27 18:40:57');
INSERT INTO `user_behaviors` VALUES (8, 3, 'VIEW', 1, '{\"duration\": 0}', '2026-01-27 18:48:03');
INSERT INTO `user_behaviors` VALUES (9, 3, 'FAVORITE', 1, '{\"action\": \"REMOVE\"}', '2026-01-27 18:48:05');
INSERT INTO `user_behaviors` VALUES (10, 3, 'VIEW', 6, '{\"duration\": 0}', '2026-01-27 18:48:13');
INSERT INTO `user_behaviors` VALUES (11, 100, 'VIEW', 1, '{\"duration\": 0}', '2026-01-27 19:19:41');
INSERT INTO `user_behaviors` VALUES (12, 100, 'BORROW', 1, '{\"stage\": \"APPLY\", \"expectedReturnTime\": \"2026-01-27 23:59:59\"}', '2026-01-27 19:19:45');
INSERT INTO `user_behaviors` VALUES (13, 100, 'VIEW', 2, '{\"duration\": 0}', '2026-01-27 19:19:52');
INSERT INTO `user_behaviors` VALUES (14, 100, 'VIEW', 6, '{\"duration\": 0}', '2026-01-27 19:19:58');
INSERT INTO `user_behaviors` VALUES (15, 100, 'BORROW', 6, '{\"stage\": \"APPLY\", \"expectedReturnTime\": \"2026-01-30 23:59:59\"}', '2026-01-27 19:20:03');
INSERT INTO `user_behaviors` VALUES (16, 100, 'RATING', 6, '{\"score\": 3}', '2026-01-27 19:20:03');
INSERT INTO `user_behaviors` VALUES (17, 100, 'VIEW', 6, '{\"duration\": 0}', '2026-01-27 19:20:03');
INSERT INTO `user_behaviors` VALUES (18, 100, 'VIEW', 6, '{\"duration\": 0}', '2026-01-27 19:24:46');
INSERT INTO `user_behaviors` VALUES (19, 100, 'VIEW', 10, '{\"duration\": 0}', '2026-01-27 19:24:48');
INSERT INTO `user_behaviors` VALUES (20, 1, 'VIEW', 2, '{\"duration\": 0}', '2026-01-27 19:27:08');
INSERT INTO `user_behaviors` VALUES (21, 100, 'VIEW', 11, '{\"duration\": 0}', '2026-01-27 19:31:42');
INSERT INTO `user_behaviors` VALUES (22, 100, 'VIEW', 11, '{\"duration\": 0}', '2026-01-27 22:48:32');
INSERT INTO `user_behaviors` VALUES (23, 100, 'VIEW', 11, '{\"duration\": 0}', '2026-01-27 22:48:53');
INSERT INTO `user_behaviors` VALUES (24, 100, 'VIEW', 11, '{\"duration\": 0}', '2026-01-27 22:48:53');
INSERT INTO `user_behaviors` VALUES (25, 100, 'VIEW', 10, '{\"duration\": 0}', '2026-01-27 22:48:57');
INSERT INTO `user_behaviors` VALUES (26, 100, 'VIEW', 10, '{\"duration\": 0}', '2026-01-27 22:49:36');

-- ----------------------------
-- Table structure for user_interest_guide
-- ----------------------------
DROP TABLE IF EXISTS `user_interest_guide`;
CREATE TABLE `user_interest_guide`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `selected_topics` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '选择的兴趣主题（JSON数组）',
  `selected_authors` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '选择的作者（JSON数组）',
  `is_completed` tinyint(4) NULL DEFAULT 0 COMMENT '是否完成引导：0-未完成，1-已完成',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE,
  INDEX `idx_is_completed`(`is_completed`) USING BTREE,
  CONSTRAINT `user_interest_guide_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户兴趣引导记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_interest_guide
-- ----------------------------
INSERT INTO `user_interest_guide` VALUES (1, 2, '[\"软件工程\"]', '[]', 1, '2026-01-26 22:18:17', '2026-01-26 22:18:17');
INSERT INTO `user_interest_guide` VALUES (2, 3, '[\"软件工程\",\"机器学习\",\"Python\"]', '[]', 1, '2026-01-27 18:47:33', '2026-01-27 18:47:33');
INSERT INTO `user_interest_guide` VALUES (3, 100, '[\"软件工程\",\"机器学习\",\"Python\"]', '[]', 1, '2026-01-27 19:00:41', '2026-01-27 19:00:41');

-- ----------------------------
-- Table structure for user_preference
-- ----------------------------
DROP TABLE IF EXISTS `user_preference`;
CREATE TABLE `user_preference`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `preference_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '偏好类型（topic, author等）',
  `preference_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '偏好值',
  `weight` double NULL DEFAULT 0 COMMENT '权重（0-1）',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_preference_type`(`preference_type`) USING BTREE,
  CONSTRAINT `user_preference_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户偏好表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_preference
-- ----------------------------
INSERT INTO `user_preference` VALUES (1, 2, 'topic', '软件工程', 1, '2026-01-26 22:18:17');
INSERT INTO `user_preference` VALUES (2, 3, 'topic', '软件工程', 0.3333333333333333, '2026-01-27 18:47:33');
INSERT INTO `user_preference` VALUES (3, 3, 'topic', '机器学习', 0.3333333333333333, '2026-01-27 18:47:33');
INSERT INTO `user_preference` VALUES (4, 3, 'topic', 'Python', 0.3333333333333333, '2026-01-27 18:47:33');
INSERT INTO `user_preference` VALUES (8, 100, 'topic', '科幻', 1, '2026-01-27 23:10:39');

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `role_id`(`role_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_roles
-- ----------------------------
INSERT INTO `user_roles` VALUES (1, 1);
INSERT INTO `user_roles` VALUES (2, 2);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  UNIQUE INDEX `email`(`email`) USING BTREE,
  INDEX `idx_username`(`username`) USING BTREE,
  INDEX `idx_email`(`email`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', 'admin@example.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '管理员', NULL, 1, '2026-01-26 14:32:03', '2026-01-26 15:13:28');
INSERT INTO `users` VALUES (2, 'test123', 'test123@example.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '111', '/uploads/avatars/2026/01/27/2f878907-92a1-4403-92b6-ae4fed2f7c5d.jpg', 1, '2026-01-26 15:11:51', '2026-01-27 13:54:16');
INSERT INTO `users` VALUES (3, 'u03', 'u03@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户03', NULL, 1, '2025-12-28 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (4, 'u04', 'u04@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户04', NULL, 1, '2025-12-29 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (5, 'u05', 'u05@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户05', NULL, 1, '2025-12-30 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (6, 'u06', 'u06@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户06', NULL, 1, '2025-12-31 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (7, 'u07', 'u07@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户07', NULL, 1, '2026-01-01 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (8, 'u08', 'u08@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户08', NULL, 1, '2026-01-02 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (9, 'u09', 'u09@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户09', NULL, 1, '2026-01-03 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (10, 'u10', 'u10@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户10', NULL, 1, '2026-01-04 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (11, 'u11', 'u11@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户11', NULL, 1, '2026-01-05 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (12, 'u12', 'u12@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户12', NULL, 1, '2026-01-06 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (13, 'u13', 'u13@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户13', NULL, 1, '2026-01-07 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (14, 'u14', 'u14@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户14', NULL, 1, '2026-01-08 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (15, 'u15', 'u15@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户15', NULL, 1, '2026-01-09 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (16, 'u16', 'u16@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户16', NULL, 1, '2026-01-10 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (17, 'u17', 'u17@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户17', NULL, 1, '2026-01-11 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (18, 'u18', 'u18@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户18', NULL, 1, '2026-01-12 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (19, 'u19', 'u19@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户19', NULL, 1, '2026-01-13 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (20, 'u20', 'u20@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户20', NULL, 1, '2026-01-14 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (21, 'u21', 'u21@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户21', NULL, 1, '2026-01-15 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (22, 'u22', 'u22@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户22', NULL, 1, '2026-01-16 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (23, 'u23', 'u23@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户23', NULL, 1, '2026-01-17 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (24, 'u24', 'u24@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户24', NULL, 1, '2026-01-18 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (25, 'u25', 'u25@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户25', NULL, 1, '2026-01-19 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (26, 'u26', 'u26@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户26', NULL, 1, '2026-01-20 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (27, 'u27', 'u27@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户27', NULL, 1, '2026-01-21 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (28, 'u28', 'u28@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户28', NULL, 1, '2026-01-22 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (29, 'u29', 'u29@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户29', NULL, 1, '2026-01-23 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (30, 'u30', 'u30@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户30', NULL, 1, '2026-01-24 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (31, 'u31', 'u31@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户31', NULL, 1, '2026-01-25 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (32, 'u32', 'u32@test.com', '$2a$10$cB8W7231UE89AeIi/FxX2OBdgDYlXy2H1c.HVBYWhdtQ4YUz9ekfW', '用户32', NULL, 1, '2026-01-26 17:50:35', '2026-01-27 18:47:24');
INSERT INTO `users` VALUES (100, 'u33', 'u33@test.com', '$2a$10$3pEmCsXgYFznpfROgfYRvOh.iUzAG36qSEilRH6pB3HyhsiR097su', NULL, NULL, 1, '2026-01-27 19:00:04', '2026-01-27 19:00:04');

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 搜索历史表
-- ----------------------------
DROP TABLE IF EXISTS `search_history`;

CREATE TABLE `search_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NULL COMMENT '用户ID（NULL表示匿名搜索）',
  `keyword` varchar(200) NOT NULL COMMENT '搜索关键词',
  `search_mode` varchar(20) NOT NULL DEFAULT 'keyword' COMMENT '搜索模式',
  `result_count` int(11) NULL DEFAULT 0 COMMENT '搜索结果数量',
  `ip_address` varchar(50) NULL COMMENT 'IP地址（防刷）',
  `user_agent` varchar(500) NULL COMMENT '用户代理',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '搜索时间',
  PRIMARY KEY (`id`),
  INDEX `idx_keyword_time`(`keyword`, `create_time`) COMMENT '热搜统计核心索引',
  INDEX `idx_user_time`(`user_id`, `create_time`),
  INDEX `idx_ip_keyword_time`(`ip_address`, `keyword`, `create_time`) COMMENT '防刷索引'
) ENGINE = InnoDB COMMENT = '搜索历史表';
