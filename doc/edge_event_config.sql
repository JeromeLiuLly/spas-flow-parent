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

 Date: 18/05/2021 10:05:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for edge_event_config
-- ----------------------------
DROP TABLE IF EXISTS `edge_event_config`;
CREATE TABLE `edge_event_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `flowId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作流名称',
  `nodeId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作节点名称',
  `source` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '源连线节点位置',
  `target` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目标连线节点位置',
  `sourceNode` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '源连线节点名称',
  `targetNode` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目标连线节点名称',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of edge_event_config
-- ----------------------------
BEGIN;
INSERT INTO `edge_event_config` VALUES (5, 'butterfly', NULL, 'right', 'left', 'begin_946', 'bean_25', NULL);
INSERT INTO `edge_event_config` VALUES (6, 'butterfly', NULL, 'right', 'left', 'bean_25', 'native_984', NULL);
INSERT INTO `edge_event_config` VALUES (7, 'butterfly', NULL, 'right', 'left', 'native_984', 'rpc_225', NULL);
INSERT INTO `edge_event_config` VALUES (8, 'butterfly', NULL, 'right', 'left', 'rpc_225', 'end_30', NULL);
INSERT INTO `edge_event_config` VALUES (48, 'flow', 'begin_434', 'right', 'left', 'begin_434', 'bean_132', NULL);
INSERT INTO `edge_event_config` VALUES (49, 'flow', 'bean_132', 'right', 'left', 'bean_132', 'condition_101', NULL);
INSERT INTO `edge_event_config` VALUES (50, 'flow', 'native_944', 'right', 'left', 'native_944', 'end_527', NULL);
INSERT INTO `edge_event_config` VALUES (51, 'flow', 'condition_101', 'right', 'left', 'condition_101', 'native_944', NULL);
INSERT INTO `edge_event_config` VALUES (52, 'flow', 'condition_101', 'right', 'left', 'condition_101', 'method_819', NULL);
INSERT INTO `edge_event_config` VALUES (53, 'flow', 'method_819', 'right', 'left', 'method_819', 'end_527', NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
