SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                          `id` int NOT NULL AUTO_INCREMENT COMMENT 'id, primary key',
                          `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'username',
                          `password` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'password',
                          `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'nickname',
                          `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'avatar',
                          `tel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'phone',
                          `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'email',
                          `status` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'status',
                          `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create_time',
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'Bryan Ye', 'give me full mark', 'GPA 4.0 (in dream)', 'http://localhost:1000/file/8826e8c280cb3bec6a4fbeb61514ee74.png', '123456', '123456@northeastern.edu', 'Enable', '2024-12-07 17:29:35');
INSERT INTO `user` VALUES (2, 'Yuxin Li', 'give me 0 mark', 'Yuxin', 'http://localhost:1000/file/8826e8c280cb3bec6a4fbeb61514ee74.png', '123456', '123456@northeastern.edu', 'Enable', '2024-12-07 17:29:35');

SET FOREIGN_KEY_CHECKS = 1;
