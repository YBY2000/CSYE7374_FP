-- ----------------------------
-- Table structure for discount_config
-- ----------------------------
DROP TABLE IF EXISTS `discount_config`;
CREATE TABLE `discount_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `discount_rate` decimal(3, 2) NOT NULL DEFAULT 1.00 COMMENT 'Discount rate, 1.00 means no discount, 0.85 means 15% off',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Whether discount is enabled, 0-disabled, 1-enabled',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Discount description',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Discount configuration table' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of discount_config - Initial data
-- ----------------------------
INSERT INTO `discount_config` VALUES (1, 1.00, 0, 'No discount', NOW(), NOW()); 