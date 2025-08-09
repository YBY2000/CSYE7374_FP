/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : localhost:3306
 Source Schema         : canteen

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 27/06/2024 12:06:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Username',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Password',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Name',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Avatar URL',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Role',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Admin' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', 'admin', 'IwantFULLmark', 'http://localhost:9090/files/download/QQ截图20230330090659.png', 'ADMIN');
INSERT INTO `admin` VALUES (2, 'admin123', 'admin', '0markISok', 'http://localhost:9090/files/download/微信截图_20231114162404.png', 'ADMIN');
INSERT INTO `admin` VALUES (6, 'admin2', 'admin', 'IlovePETER', 'http://localhost:9090/files/download/微信截图_20231018172251.png', 'ADMIN');

-- ----------------------------
-- Table structure for foods
-- ----------------------------
DROP TABLE IF EXISTS `foods`;
CREATE TABLE `foods`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Food name',
  `descr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Food description',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT 'Price',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Food image',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of foods
-- ----------------------------
INSERT INTO `foods` VALUES (1, 'Tenders', 'Not Healthy', 29.00, 'http://localhost:9090/files/download/微信截图_20240227101316.png');
INSERT INTO `foods` VALUES (6, 'Signature Chicken', 'Also not healthy', 39.00, 'http://localhost:9090/files/download/微信截图_20240227101659.png');
INSERT INTO `foods` VALUES (7, 'Biscuits', 'Who will eat this?', 28.00, 'http://localhost:9090/files/download/微信截图_20240227101826.png');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Menu content',
  `total` decimal(10, 0) NULL DEFAULT NULL COMMENT 'Total price',
  `user_id` int(11) NULL DEFAULT NULL COMMENT 'Ordering user id',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Order time',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Order status: COMPLETED, PREPARING, PENDING',
  `order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Order No.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Order info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (4, 'Tenders * 1, Signature Chicken * 1', 67, 1, '2024-06-27 11:29:55', 'PREPARING', '56a456d0773c4019a9f3a1b19cb57d69');
INSERT INTO `orders` VALUES (5, 'Tenders * 3，Signature Chicken * 3', 201, 1, '2024-06-27 11:37:23', 'PREPARING', 'ee7c36e22b6345fb9799c593fcca3d35');
INSERT INTO `orders` VALUES (6, 'Tenders * 3，Signature Chicken * 3', 201, 3, '2024-06-27 11:47:57', 'COMPLETED', '684833a767d94c318e63c9308b5706b0');
INSERT INTO `orders` VALUES (7, 'Tenders * 1，Signature Chicken * 1', 67, 7, '2024-06-27 12:00:13', 'COMPLETED', '99b2d93ca12f428ea0e018f571dcbce7');

-- ----------------------------
-- Table structure for tables
-- ----------------------------
DROP TABLE IF EXISTS `tables`;
CREATE TABLE `tables`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Table No.',
  `unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Table unit',
  `free` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Is table free? Yes, No',
  `user_id` int(11) NULL DEFAULT NULL COMMENT 'Occupant(the user who is using) ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Table info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tables
-- ----------------------------
INSERT INTO `tables` VALUES (1, 'A101', '4', 'Yes', NULL);
INSERT INTO `tables` VALUES (2, 'A102', '4', 'Yes', NULL);
INSERT INTO `tables` VALUES (3, 'A103', '4', 'Yes', NULL);
INSERT INTO `tables` VALUES (4, 'A104', '4', 'Yes', NULL);
INSERT INTO `tables` VALUES (5, 'A105', '4', 'Yes', NULL);
INSERT INTO `tables` VALUES (6, 'A106', '4', 'Yes', NULL);
INSERT INTO `tables` VALUES (7, 'A107', '4', 'Yes', NULL);
INSERT INTO `tables` VALUES (8, 'A108', '4', 'Yes', NULL);
INSERT INTO `tables` VALUES (10, 'B101', '8', 'Yes', NULL);
INSERT INTO `tables` VALUES (11, 'B102', '8', 'Yes', NULL);
INSERT INTO `tables` VALUES (12, 'B103', '8', 'Yes', NULL);
INSERT INTO `tables` VALUES (13, 'B104', '8', 'Yes', NULL);
INSERT INTO `tables` VALUES (14, 'B105', '8', 'Yes', NULL);
INSERT INTO `tables` VALUES (15, 'B106', '8', 'No', 3);
INSERT INTO `tables` VALUES (16, 'B107', '8', 'Yes', NULL);
INSERT INTO `tables` VALUES (17, 'B108', '8', 'No', 6);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Username',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Password',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Name',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Avatar URL',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Role:USER ADMIN',
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Gender: Male Female',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Phone number',
  `account` decimal(10, 2) NULL DEFAULT 0.00 COMMENT 'Account balance',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'User info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'aaa', '123', '100_mark', 'http://localhost:9090/files/download/微信截图_20240409161232.png', 'USER', 'Male', '13899887799', 0.00);
INSERT INTO `user` VALUES (3, 'ccc', '123', 'GPA4.0', NULL, 'USER', NULL, NULL, 0.00);
INSERT INTO `user` VALUES (4, 'bbb', '123', 'WHATisGPA', 'http://localhost:9090/files/download/122121211_1708655721764.jpg', 'USER', 'Male', '13899887788', 0.00);
INSERT INTO `user` VALUES (5, 'sss', '123', 'HSR', 'http://localhost:9090/files/download/QQ截图20230330085759_1708566053188.png', 'USER', 'Male', '13899887788', 0.00);
INSERT INTO `user` VALUES (6, 'ddd', '123', 'GENSHIN', NULL, 'USER', NULL, NULL, 0.00);
INSERT INTO `user` VALUES (7, 'accc', '123', 'ZZZ', NULL, 'USER', NULL, NULL, 0.00);

SET FOREIGN_KEY_CHECKS = 1;
