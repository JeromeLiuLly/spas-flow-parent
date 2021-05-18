/*
 Navicat Premium Data Transfer

 Source Server         : saas+测试环境地址
 Source Server Type    : MySQL
 Source Server Version : 50641
 Source Host           : 122.112.159.200:3306
 Source Schema         : spas-demo

 Target Server Type    : MySQL
 Target Server Version : 50641
 File Encoding         : 65001

 Date: 18/05/2021 10:06:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for flow_event_config
-- ----------------------------
DROP TABLE IF EXISTS `flow_event_config`;
CREATE TABLE `flow_event_config` (
  `id` int(11) NOT NULL COMMENT '自增id',
  `flowId` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作流id',
  `flowName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作流名称',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `status` int(1) DEFAULT NULL COMMENT '工作流状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of flow_event_config
-- ----------------------------
BEGIN;
INSERT INTO `flow_event_config` VALUES (1, 'chain', '链路工作流示例', '链路工作流示例', 1);
INSERT INTO `flow_event_config` VALUES (2, 'butterfly', '小蝴蝶工作流示例', '小蝴蝶工作流示例', 1);
INSERT INTO `flow_event_config` VALUES (3, 'flow', '工作流本地DEMO示例', '工作流本地DEMO示例', 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
