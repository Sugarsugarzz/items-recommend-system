/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : localhost:3306
 Source Schema         : zbzs1

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 22/04/2021 14:25:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for recommendations
-- ----------------------------
DROP TABLE IF EXISTS `recommendations`;
CREATE TABLE `recommendations` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(20) NOT NULL COMMENT '用户ID',
  `item_id` int(20) NOT NULL COMMENT '推荐项ID',
  `info_type` int(20) NOT NULL COMMENT '信息类型 1头条 2百科 3期刊 4报告',
  `derive_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '生成时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_key` (`user_id`,`item_id`,`info_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='推荐结果表';

-- ----------------------------
-- Table structure for recommendations_default
-- ----------------------------
DROP TABLE IF EXISTS `recommendations_default`;
CREATE TABLE `recommendations_default` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `item_id` int(20) NOT NULL COMMENT '推荐项ID',
  `info_type` int(20) NOT NULL COMMENT '信息类型 1头条 2百科 3期刊 4报告',
  `derive_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '生成时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_key` (`item_id`,`info_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='默认推荐结果表';

SET FOREIGN_KEY_CHECKS = 1;
