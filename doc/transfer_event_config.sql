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

 Date: 18/05/2021 10:07:18
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
  `nodeName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点名称',
  `nodeType` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点类型',
  `component` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '实现类',
  `front` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上个节点',
  `next` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '下个节点',
  `eventName` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件名称',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件描述',
  `input` varchar(0) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '输入参数',
  `output` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '输出参数结构',
  `url` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求地址',
  `fullLink` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '完整请求链接',
  `requertType` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求协议',
  `serverPort` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求端口',
  `methodName` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法',
  `inputParamTypesValues` varchar(10000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法的入参类型',
  `convertRule` mediumtext COLLATE utf8mb4_unicode_ci COMMENT 'json转换规则',
  `timeout` int(5) DEFAULT '30' COMMENT '请求超时时间(秒)',
  `eventType` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件类型',
  `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件模型版本',
  `status` int(1) DEFAULT NULL COMMENT '事件模型状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=126 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of transfer_event_config
-- ----------------------------
BEGIN;
INSERT INTO `transfer_event_config` VALUES (1, 'chain', 'chain02', NULL, NULL, NULL, NULL, NULL, 'beanInvoke', 'beanInvoke', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 30, NULL, '0.0.1', 0);
INSERT INTO `transfer_event_config` VALUES (2, 'chain', 'chain03', NULL, NULL, NULL, NULL, NULL, 'methodInvoke', 'methodInvoke', NULL, NULL, 'com.candao.spas.flow.sample.flow.service.FlowNativeService', NULL, NULL, NULL, 'commonMethod3', '[java.lang.String:projectName,java.lang.String:studentCount,com.candao.spas.flow.sample.flow.bean.TempProject]', NULL, 30, NULL, NULL, 0);
INSERT INTO `transfer_event_config` VALUES (3, 'chain', 'chain04', NULL, NULL, NULL, NULL, NULL, 'serviceInvoke', 'serviceInvoke', NULL, '{\"code\":\"status\",\"value\":1,\"data\":\"data\",\"msg\":\"message\"}', 'http://127.0.0.1', NULL, NULL, '8888', '/flow/http/demo', NULL, NULL, 30, 'http', NULL, 0);
INSERT INTO `transfer_event_config` VALUES (4, 'chain', 'chain05', NULL, NULL, NULL, NULL, NULL, 'serviceInvoke', 'serviceInvoke', NULL, NULL, 'com.candao.auth.dubbo.api.AccountProvider', NULL, NULL, NULL, 'getAccountByToken', '[java.lang.String:token]', NULL, 30, 'dubboSOA', NULL, 0);
INSERT INTO `transfer_event_config` VALUES (72, 'butterfly', 'begin_946', 'beginInvoke', 'begin', NULL, 'root', 'bean_25', 'beginInvoke', 'beginInvoke', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1);
INSERT INTO `transfer_event_config` VALUES (73, 'butterfly', 'bean_25', 'beanInvoke', 'bean', 'commonBeanFlowChainService', 'begin946', 'native_984', 'beanInvoke', 'beanInvoke', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '{\"sn\":\"projectName\",\"teacher\":{\"sn\":\"teacher.number\",\"name\":\"teacher.userName\",\"projectSN\":\"\"},\"name\":\"\",\"teachersSN\":{\"*\":\"teachersSN.*\"},\"studentCount\":\"studentCount\",\"classesSN\":{\"*\":\"\"}}', NULL, NULL, NULL, 1);
INSERT INTO `transfer_event_config` VALUES (74, 'butterfly', 'native_984', 'nativeInvoke', 'native', 'commonMethodFlowChainService', 'bean_25', 'rpc_225', 'nativeInvoke', 'nativeInvoke', NULL, NULL, 'com.candao.spas.flow.sample.flow.service.FlowNativeService', NULL, NULL, NULL, 'commonMethod3', 'java.lang.String:projectName,java.lang.String:studentCount,com.candao.spas.flow.sample.flow.bean.TempProject', NULL, NULL, NULL, NULL, 1);
INSERT INTO `transfer_event_config` VALUES (75, 'butterfly', 'rpc_225', 'serverInvoke', 'server', 'commonServiceFlowChainService', 'native_984', 'end_30', 'serverInvoke', 'serverInvoke', NULL, '{\"code\":\"status\",\"value\":1,\"data\":\"data\",\"msg\":\"message\"}', 'http://127.0.0.1', NULL, 'POST', '8888', '/flow/http/demo', NULL, NULL, 30, 'http', NULL, 1);
INSERT INTO `transfer_event_config` VALUES (76, 'butterfly', 'end_30', 'endInvoke', 'end', NULL, 'rpc_225', NULL, 'endInvoke', 'endInvoke', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1);
INSERT INTO `transfer_event_config` VALUES (81, 'flow', 'begin_434', 'beginInvoke', 'begin', NULL, 'root', 'bean_132', 'beginInvoke', 'beginInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 14:10:42', 0);
INSERT INTO `transfer_event_config` VALUES (82, 'flow', 'bean_132', 'beanInvoke', 'bean', 'commonBeanFlowChainService', 'begin_434', 'native_944', 'beanInvoke', 'beanInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, '{\"sn\":\"projectName\",\"teacher\":{\"sn\":\"teacher.number\",\"name\":\"teacher.userName\",\"projectSN\":\"\"},\"name\":\"\",\"teachersSN\":{\"*\":\"teachersSN.*\"},\"studentCount\":\"studentCount\",\"classesSN\":{\"*\":\"\"}}', 30, NULL, '2021-05-14 14:10:42', 0);
INSERT INTO `transfer_event_config` VALUES (83, 'flow', 'native_944', 'nativeInvoke', 'method', 'commonMethodFlowChainService', 'bean_132', 'end_527', 'nativeInvoke', 'nativeInvoke', NULL, NULL, 'com.candao.spas.flow.sample.flow.service.FlowNativeService', NULL, 'POST', NULL, 'commonMethod3', 'java.lang.String:projectName,java.lang.String:studentCount,com.candao.spas.flow.sample.flow.bean.TempProject', NULL, 30, NULL, '2021-05-14 14:10:42', 0);
INSERT INTO `transfer_event_config` VALUES (84, 'flow', 'end_527', 'endInvoke', 'end', NULL, 'native_944', NULL, 'endInvoke', 'endInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 14:10:42', 0);
INSERT INTO `transfer_event_config` VALUES (85, 'flow', 'begin_434', 'beginInvoke', 'begin', NULL, 'root', 'bean_132', 'beginInvoke', 'beginInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 14:16:58', 0);
INSERT INTO `transfer_event_config` VALUES (86, 'flow', 'bean_132', 'beanInvoke', 'bean', 'commonBeanFlowChainService', 'begin_434', 'native_944', 'beanInvoke', 'beanInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, '{\"sn\":\"projectName\",\"teacher\":{\"sn\":\"teacher.number\",\"name\":\"teacher.userName\",\"projectSN\":\"\"},\"name\":\"\",\"teachersSN\":{\"*\":\"teachersSN.*\"},\"studentCount\":\"studentCount\",\"classesSN\":{\"*\":\"\"}}', 30, NULL, '2021-05-14 14:16:58', 0);
INSERT INTO `transfer_event_config` VALUES (87, 'flow', 'native_944', 'methodInvoke', 'method', 'commonMethodFlowChainService', 'bean_132', 'end_527', 'methodInvoke', 'methodInvoke', NULL, NULL, 'com.candao.spas.flow.sample.flow.service.FlowNativeService', NULL, 'POST', NULL, 'commonMethod3', 'java.lang.String:projectName,java.lang.String:studentCount,com.candao.spas.flow.sample.flow.bean.TempProject', NULL, 30, NULL, '2021-05-14 14:16:58', 0);
INSERT INTO `transfer_event_config` VALUES (88, 'flow', 'end_527', 'endInvoke', 'end', NULL, 'native_944', NULL, 'endInvoke', 'endInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 14:16:58', 0);
INSERT INTO `transfer_event_config` VALUES (89, 'flow', 'begin_434', 'beginInvoke', 'begin', NULL, 'root', 'bean_132', 'beginInvoke', 'beginInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 14:18:20', 0);
INSERT INTO `transfer_event_config` VALUES (90, 'flow', 'bean_132', 'beanInvoke', 'bean', 'commonBeanFlowChainService', 'begin_434', 'native_944', 'beanInvoke', 'beanInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, '{\"sn\":\"projectName\",\"teacher\":{\"sn\":\"teacher.number\",\"name\":\"teacher.userName\",\"projectSN\":\"\"},\"name\":\"\",\"teachersSN\":{\"*\":\"teachersSN.*\"},\"studentCount\":\"studentCount\",\"classesSN\":{\"*\":\"\"}}', 30, NULL, '2021-05-14 14:18:20', 0);
INSERT INTO `transfer_event_config` VALUES (91, 'flow', 'native_944', 'methodInvoke', 'method', 'commonMethodFlowChainService', 'condition_101', 'end_527', 'methodInvoke', 'methodInvoke', NULL, NULL, 'com.candao.spas.flow.sample.flow.service.FlowNativeService', NULL, 'POST', NULL, 'commonMethod3', 'java.lang.String:projectName,java.lang.String:studentCount,com.candao.spas.flow.sample.flow.bean.TempProject', NULL, 30, NULL, '2021-05-14 14:18:20', 0);
INSERT INTO `transfer_event_config` VALUES (92, 'flow', 'end_527', 'endInvoke', 'end', NULL, 'native_944', NULL, 'endInvoke', 'endInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 14:18:20', 0);
INSERT INTO `transfer_event_config` VALUES (93, 'flow', 'condition_101', 'conditionInvoke', 'condition', '(sn != nil && sn == \'1140\'):native_944', NULL, 'native_944', 'conditionInvoke', 'conditionInvoke', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-05-14 14:18:20', 0);
INSERT INTO `transfer_event_config` VALUES (94, 'flow', 'begin_434', 'beginInvoke', 'begin', NULL, 'root', 'bean_132', 'beginInvoke', 'beginInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 14:28:38', 0);
INSERT INTO `transfer_event_config` VALUES (95, 'flow', 'bean_132', 'beanInvoke', 'bean', 'commonBeanFlowChainService', 'begin_434', 'condition_101', 'beanInvoke', 'beanInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, '{\"sn\":\"projectName\",\"teacher\":{\"sn\":\"teacher.number\",\"name\":\"teacher.userName\",\"projectSN\":\"\"},\"name\":\"\",\"teachersSN\":{\"*\":\"teachersSN.*\"},\"studentCount\":\"studentCount\",\"classesSN\":{\"*\":\"\"}}', 30, NULL, '2021-05-14 14:28:38', 0);
INSERT INTO `transfer_event_config` VALUES (96, 'flow', 'native_944', 'methodInvoke', 'method', 'commonMethodFlowChainService', 'condition_101', 'end_527', 'methodInvoke', 'methodInvoke', NULL, NULL, 'com.candao.spas.flow.sample.flow.service.FlowNativeService', NULL, 'POST', NULL, 'commonMethod3', 'java.lang.String:projectName,java.lang.String:studentCount,com.candao.spas.flow.sample.flow.bean.TempProject', NULL, 30, NULL, '2021-05-14 14:28:38', 0);
INSERT INTO `transfer_event_config` VALUES (97, 'flow', 'end_527', 'endInvoke', 'end', NULL, 'native_944', NULL, 'endInvoke', 'endInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 14:28:38', 0);
INSERT INTO `transfer_event_config` VALUES (98, 'flow', 'condition_101', 'conditionInvoke', 'condition', '(sn != nil && sn == \'1140\'):native_944', 'bean_132', 'native_944', 'conditionInvoke', 'conditionInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 14:28:38', 0);
INSERT INTO `transfer_event_config` VALUES (99, 'flow', 'begin_434', 'beginInvoke', 'begin', NULL, 'root', 'bean_132', 'beginInvoke', 'beginInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 16:47:40', 0);
INSERT INTO `transfer_event_config` VALUES (100, 'flow', 'bean_132', 'beanInvoke', 'bean', 'commonBeanFlowChainService', 'begin_434', 'condition_101', 'beanInvoke', 'beanInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, '{\"sn\":\"projectName\",\"teacher\":{\"sn\":\"teacher.number\",\"name\":\"teacher.userName\",\"projectSN\":\"\"},\"name\":\"\",\"teachersSN\":{\"*\":\"teachersSN.*\"},\"studentCount\":\"studentCount\",\"classesSN\":{\"*\":\"\"}}', 30, NULL, '2021-05-14 16:47:40', 0);
INSERT INTO `transfer_event_config` VALUES (101, 'flow', 'native_944', 'methodInvoke', 'method', 'commonMethodFlowChainService', 'condition_101', 'end_527', 'methodInvoke', 'methodInvoke', NULL, NULL, 'com.candao.spas.flow.sample.flow.service.FlowNativeService', NULL, 'POST', NULL, 'commonMethod3', 'java.lang.String:projectName,java.lang.String:studentCount,com.candao.spas.flow.sample.flow.bean.TempProject', NULL, 30, NULL, '2021-05-14 16:47:40', 0);
INSERT INTO `transfer_event_config` VALUES (102, 'flow', 'end_527', 'endInvoke', 'end', NULL, 'native_944', NULL, 'endInvoke', 'endInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 16:47:40', 0);
INSERT INTO `transfer_event_config` VALUES (103, 'flow', 'condition_101', 'conditionInvoke', 'condition', '(sn != nil && sn == \'1140\'):native_944', 'bean_132', 'native_944', 'conditionInvoke', 'conditionInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 16:47:40', 0);
INSERT INTO `transfer_event_config` VALUES (104, 'flow', 'begin_434', 'beginInvoke', 'begin', NULL, 'root', 'bean_132', 'beginInvoke', 'beginInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 16:48:08', 0);
INSERT INTO `transfer_event_config` VALUES (105, 'flow', 'bean_132', 'beanInvoke', 'bean', 'commonBeanFlowChainService', 'begin_434', 'condition_101', 'beanInvoke', 'beanInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, '{\"sn\":\"projectName\",\"teacher\":{\"sn\":\"teacher.number\",\"name\":\"teacher.userName\",\"projectSN\":\"\"},\"name\":\"\",\"teachersSN\":{\"*\":\"teachersSN.*\"},\"studentCount\":\"studentCount\",\"classesSN\":{\"*\":\"\"}}', 30, NULL, '2021-05-14 16:48:08', 0);
INSERT INTO `transfer_event_config` VALUES (106, 'flow', 'native_944', 'methodInvoke', 'method', 'commonMethodFlowChainService', 'condition_101', 'end_527', 'methodInvoke', 'methodInvoke', NULL, NULL, 'com.candao.spas.flow.sample.flow.service.FlowNativeService', NULL, 'POST', NULL, 'commonMethod3', 'java.lang.String:projectName,java.lang.String:studentCount,com.candao.spas.flow.sample.flow.bean.TempProject', NULL, 30, NULL, '2021-05-14 16:48:08', 0);
INSERT INTO `transfer_event_config` VALUES (107, 'flow', 'end_527', 'endInvoke', 'end', NULL, 'native_944', NULL, 'endInvoke', 'endInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 16:48:08', 0);
INSERT INTO `transfer_event_config` VALUES (108, 'flow', 'condition_101', 'conditionInvoke', 'condition', '(sn != nil && sn == \'1140\'):native_944', 'bean_132', 'native_944', 'conditionInvoke', 'conditionInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 16:48:08', 0);
INSERT INTO `transfer_event_config` VALUES (109, 'flow', 'begin_434', 'beginInvoke', 'begin', NULL, 'root', 'bean_132', 'beginInvoke', 'beginInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 16:55:02', 0);
INSERT INTO `transfer_event_config` VALUES (110, 'flow', 'bean_132', 'beanInvoke', 'bean', 'commonBeanFlowChainService', 'begin_434', 'condition_101', 'beanInvoke', 'beanInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, '{\"sn\":\"projectName\",\"teacher\":{\"sn\":\"teacher.number\",\"name\":\"teacher.userName\",\"projectSN\":\"\"},\"name\":\"\",\"teachersSN\":{\"*\":\"teachersSN.*\"},\"studentCount\":\"studentCount\",\"classesSN\":{\"*\":\"\"}}', 30, NULL, '2021-05-14 16:55:02', 0);
INSERT INTO `transfer_event_config` VALUES (111, 'flow', 'native_944', 'methodInvoke', 'method', 'commonMethodFlowChainService', 'condition_101', 'end_527', 'methodInvoke', 'methodInvoke', NULL, NULL, 'com.candao.spas.flow.sample.flow.service.FlowNativeService', NULL, 'POST', NULL, 'commonMethod3', 'java.lang.String:projectName,java.lang.String:studentCount,com.candao.spas.flow.sample.flow.bean.TempProject', NULL, 30, NULL, '2021-05-14 16:55:02', 0);
INSERT INTO `transfer_event_config` VALUES (112, 'flow', 'end_527', 'endInvoke', 'end', NULL, 'native_944', NULL, 'endInvoke', 'endInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 16:55:02', 0);
INSERT INTO `transfer_event_config` VALUES (113, 'flow', 'condition_101', 'conditionInvoke', 'condition', '(sn != nil && sn == \'1141\'):native_944', 'bean_132', 'native_944', 'conditionInvoke', 'conditionInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 16:55:02', 0);
INSERT INTO `transfer_event_config` VALUES (114, 'flow', 'begin_434', 'beginInvoke', 'begin', NULL, 'root', 'bean_132', 'beginInvoke', 'beginInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 16:56:18', 0);
INSERT INTO `transfer_event_config` VALUES (115, 'flow', 'bean_132', 'beanInvoke', 'bean', 'commonBeanFlowChainService', 'begin_434', 'condition_101', 'beanInvoke', 'beanInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, '{\"sn\":\"projectName\",\"teacher\":{\"sn\":\"teacher.number\",\"name\":\"teacher.userName\",\"projectSN\":\"\"},\"name\":\"\",\"teachersSN\":{\"*\":\"teachersSN.*\"},\"studentCount\":\"studentCount\",\"classesSN\":{\"*\":\"\"}}', 30, NULL, '2021-05-14 16:56:18', 0);
INSERT INTO `transfer_event_config` VALUES (116, 'flow', 'native_944', 'methodInvoke', 'method', 'commonMethodFlowChainService', 'condition_101', 'end_527', 'methodInvoke', 'methodInvoke', NULL, NULL, 'com.candao.spas.flow.sample.flow.service.FlowNativeService', NULL, 'POST', NULL, 'commonMethod3', 'java.lang.String:projectName,java.lang.String:studentCount,com.candao.spas.flow.sample.flow.bean.TempProject', NULL, 30, NULL, '2021-05-14 16:56:18', 0);
INSERT INTO `transfer_event_config` VALUES (117, 'flow', 'end_527', 'endInvoke', 'end', NULL, 'method_819', NULL, 'endInvoke', 'endInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 16:56:18', 0);
INSERT INTO `transfer_event_config` VALUES (118, 'flow', 'condition_101', 'conditionInvoke', 'condition', '(sn != nil && sn == \'1141\'):native_944', 'bean_132', 'method_819', 'conditionInvoke', 'conditionInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, '2021-05-14 16:56:18', 0);
INSERT INTO `transfer_event_config` VALUES (119, 'flow', 'method_819', 'methodInvoke', 'method', 'commonMethodFlowChainService', 'condition_101', 'end_527', 'methodInvoke', 'methodInvoke', NULL, NULL, 'flowService', NULL, NULL, NULL, 'commonMethod', 'com.candao.spas.flow.core.model.req.RequestFlowDataVo,com.candao.spas.flow.core.model.resp.ResponseFlowDataVo', NULL, NULL, NULL, '2021-05-14 16:56:18', 0);
INSERT INTO `transfer_event_config` VALUES (120, 'flow', 'begin_434', 'beginInvoke', 'begin', NULL, 'root', 'bean_132', 'beginInvoke', 'beginInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, NULL, 1);
INSERT INTO `transfer_event_config` VALUES (121, 'flow', 'bean_132', 'beanInvoke', 'bean', 'commonBeanFlowChainService', 'begin_434', 'condition_101', 'beanInvoke', 'beanInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, '{\"sn\":\"projectName\",\"teacher\":{\"sn\":\"teacher.number\",\"name\":\"teacher.userName\",\"projectSN\":\"\"},\"name\":\"\",\"teachersSN\":{\"*\":\"teachersSN.*\"},\"studentCount\":\"studentCount\",\"classesSN\":{\"*\":\"\"}}', 30, NULL, NULL, 1);
INSERT INTO `transfer_event_config` VALUES (122, 'flow', 'native_944', 'methodInvoke', 'method', 'commonMethodFlowChainService', 'condition_101', 'end_527', 'methodInvoke', 'methodInvoke', NULL, NULL, 'com.candao.spas.flow.sample.flow.service.FlowNativeService', NULL, 'POST', NULL, 'commonMethod3', 'java.lang.String:projectName,java.lang.String:studentCount,com.candao.spas.flow.sample.flow.bean.TempProject', NULL, 30, NULL, NULL, 1);
INSERT INTO `transfer_event_config` VALUES (123, 'flow', 'end_527', 'endInvoke', 'end', NULL, 'method_819', NULL, 'endInvoke', 'endInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, NULL, 1);
INSERT INTO `transfer_event_config` VALUES (124, 'flow', 'condition_101', 'conditionInvoke', 'condition', '(sn != nil && sn == \'1141\'):native_944,(sn != nil && sn == \'1140\'):method_819', 'bean_132', 'method_819', 'conditionInvoke', 'conditionInvoke', NULL, NULL, NULL, NULL, 'POST', NULL, NULL, NULL, NULL, 30, NULL, NULL, 1);
INSERT INTO `transfer_event_config` VALUES (125, 'flow', 'method_819', 'methodInvoke', 'method', 'commonMethodFlowChainService', 'condition_101', 'end_527', 'methodInvoke', 'methodInvoke', NULL, NULL, 'flowService', NULL, NULL, NULL, 'commonMethod', 'com.candao.spas.flow.core.model.req.RequestFlowDataVo,com.candao.spas.flow.core.model.resp.ResponseFlowDataVo', NULL, NULL, NULL, NULL, 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
