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

 Date: 18/05/2021 10:06:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for node_event_config
-- ----------------------------
DROP TABLE IF EXISTS `node_event_config`;
CREATE TABLE `node_event_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `flowId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作流名称',
  `nodeId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作节点名称',
  `label` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '控件名称',
  `text` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '条件控件名称',
  `shape` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '条件控件',
  `className` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标背景颜色',
  `iconType` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `color` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '颜色',
  `top` int(11) DEFAULT NULL COMMENT '高度',
  `leftPoint` int(11) DEFAULT NULL COMMENT '左距离',
  `endpoints` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '连线点',
  `vueClass` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点类型',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of node_event_config
-- ----------------------------
BEGIN;
INSERT INTO `node_event_config` VALUES (36, 'butterfly', 'begin_946', '开始', NULL, NULL, 'icon-background-color', 'icon-bofang', '#c6e5ff', 343, 395, '[{\"id\":\"right\",\"orientation\":[1,0],\"pos\":[0,0.5]}]', 'Node', NULL);
INSERT INTO `node_event_config` VALUES (37, 'butterfly', 'bean_25', '绑定对象执行节点', NULL, NULL, 'icon-background-color', 'icon-shujuji', '#c6e5ff', 258, 437, '[{\"id\":\"left\",\"orientation\":[-1,0],\"pos\":[0,0.5]},{\"id\":\"right\",\"orientation\":[1,0],\"pos\":[0,0.5]}]', 'Node', NULL);
INSERT INTO `node_event_config` VALUES (38, 'butterfly', 'native_984', '本地方法执行节点', NULL, NULL, 'icon-background-color', 'icon-guize-kai', '#c6e5ff', 362, 627, '[{\"id\":\"left\",\"orientation\":[-1,0],\"pos\":[0,0.5]},{\"id\":\"right\",\"orientation\":[1,0],\"pos\":[0,0.5]}]', 'Node', NULL);
INSERT INTO `node_event_config` VALUES (39, 'butterfly', 'rpc_225', '远程调用执行节点', NULL, NULL, 'icon-background-color', 'icon-guanlian', '#c6e5ff', 252, 830, '[{\"id\":\"left\",\"orientation\":[-1,0],\"pos\":[0,0.5]},{\"id\":\"right\",\"orientation\":[1,0],\"pos\":[0,0.5]}]', 'Node', NULL);
INSERT INTO `node_event_config` VALUES (40, 'butterfly', 'end_30', '结束', NULL, NULL, 'icon-background-color', 'icon-tingzhi', '#c6e5ff', 360, 1017, '[{\"id\":\"left\",\"orientation\":[-1,0],\"pos\":[0,0.5]}]', 'Node', NULL);
INSERT INTO `node_event_config` VALUES (84, 'flow', 'begin_434', '开始', NULL, NULL, 'icon-background-color', 'icon-bofang', '#c6e5ff', 274, 364, '[{\"id\":\"right\",\"orientation\":[1,0],\"pos\":[0,0.5]}]', 'Node', NULL);
INSERT INTO `node_event_config` VALUES (85, 'flow', 'bean_132', '绑定对象执行节点', NULL, NULL, 'icon-background-color', 'icon-shujuji', '#c6e5ff', 324, 454, '[{\"id\":\"left\",\"orientation\":[-1,0],\"pos\":[0,0.5]},{\"id\":\"right\",\"orientation\":[1,0],\"pos\":[0,0.5]}]', 'Node', NULL);
INSERT INTO `node_event_config` VALUES (86, 'flow', 'native_944', '本地方法执行节点', NULL, NULL, 'icon-background-color', 'icon-guize-kai', '#c6e5ff', 102, 743, '[{\"id\":\"left\",\"orientation\":[-1,0],\"pos\":[0,0.5]},{\"id\":\"right\",\"orientation\":[1,0],\"pos\":[0,0.5]}]', 'Node', NULL);
INSERT INTO `node_event_config` VALUES (87, 'flow', 'end_527', '结束', NULL, NULL, 'icon-background-color', 'icon-tingzhi', '#c6e5ff', 239, 1010, '[{\"id\":\"left\",\"orientation\":[-1,0],\"pos\":[0,0.5]}]', 'Node', NULL);
INSERT INTO `node_event_config` VALUES (88, 'flow', 'condition_101', NULL, '条件节点', 'diamond', 'icon-background-color', 'icon-rds', '#c6e5ff', 230, 632, '[{\"id\":\"top\",\"orientation\":[0,-1],\"pos\":[0.5,0]},{\"id\":\"bottom\",\"orientation\":[0,1],\"pos\":[0.5,0]},{\"id\":\"left\",\"orientation\":[-1,0],\"pos\":[0,0.5]},{\"id\":\"right\",\"orientation\":[1,0],\"pos\":[0,0.5]}]', 'BaseNode', NULL);
INSERT INTO `node_event_config` VALUES (89, 'flow', 'method_819', '本地方法执行节点', NULL, NULL, 'icon-background-color', 'icon-guize-kai', '#c6e5ff', 393, 745, '[{\"id\":\"left\",\"orientation\":[-1,0],\"pos\":[0,0.5]},{\"id\":\"right\",\"orientation\":[1,0],\"pos\":[0,0.5]}]', 'Node', NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
