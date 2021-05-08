/*
 Navicat Premium Data Transfer

 Source Server         : saas+测试环境地址
 Source Server Type    : MySQL
 Source Server Version : 50641
 Source Host           : 
 Source Schema         : spas-demo

 Target Server Type    : MySQL
 Target Server Version : 50641
 File Encoding         : 65001

 Date: 08/05/2021 11:15:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for transfer_event_config
-- ----------------------------
DROP TABLE IF EXISTS `transfer_event_config`;
CREATE TABLE `transfer_event_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `flowId` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作流名称',
  `nodeId` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作节点名称',
  `eventName` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件名称',
  `desc` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件描述',
  `input` text COLLATE utf8mb4_unicode_ci COMMENT '输入参数',
  `output` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '输出参数结构',
  `url` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求地址',
  `fullLink` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '完整请求链接',
  `requertType` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求协议',
  `serverPort` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求端口',
  `methodName` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法',
  `inputParamTypesValues` varchar(10000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法的入参类型',
  `convertRule` text COLLATE utf8mb4_unicode_ci COMMENT 'json转换规则',
  `timeout` int(5) DEFAULT '30' COMMENT '请求超时时间(秒)',
  `eventType` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件类型',
  `version` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件模型版本',
  `status` int(1) DEFAULT NULL COMMENT '事件模型状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of transfer_event_config
-- ----------------------------
BEGIN;
INSERT INTO `transfer_event_config` VALUES (1, 'chain', 'chain02', 'beanInvoke', 'beanInvoke', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '{\"sn\":\"projectName\",\"teacher\":{\"sn\":\"teacher.number\",\"name\":\"teacher.userName\",\"projectSN\":\"\"},\"name\":\"\",\"teachersSN\":{\"*\":\"teachersSN.*\"},\"studentCount\":\"studentCount\",\"classesSN\":{\"*\":\"\"}}', 30, NULL, '0.0.1', 1);
INSERT INTO `transfer_event_config` VALUES (2, 'chain', 'chain03', 'methodInvoke', 'methodInvoke', NULL, NULL, 'com.candao.spas.flow.sample.flow.service.FlowNativeService', NULL, NULL, NULL, 'commonMethod3', '[java.lang.String:projectName,java.lang.Integer:studentCount,com.candao.spas.flow.sample.flow.bean.TempProject]', NULL, 30, NULL, NULL, NULL);
INSERT INTO `transfer_event_config` VALUES (3, 'chain', 'chain04', 'serviceInvoke', 'serviceInvoke', NULL, '{\"code\":\"status\",\"value\":1,\"data\":\"data\",\"msg\":\"message\"}', 'http://127.0.0.1', NULL, NULL, '8888', '/flow/http/demo', NULL, NULL, 30, 'http', NULL, NULL);
INSERT INTO `transfer_event_config` VALUES (4, 'chain', 'chain05', 'serviceInvoke', 'serviceInvoke', NULL, NULL, 'com.candao.auth.dubbo.api.AccountProvider', NULL, NULL, NULL, 'getAccountByToken', '[java.lang.String:token]', NULL, 30, 'dubboSOA', NULL, NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
