/*
 Navicat Premium Dump SQL

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 80028 (8.0.28)
 Source Host           : localhost:3306
 Source Schema         : parking_management

 Target Server Type    : MySQL
 Target Server Version : 80028 (8.0.28)
 File Encoding         : 65001

 Date: 03/03/2026 01:51:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for app_announcement
-- ----------------------------
DROP TABLE IF EXISTS `app_announcement`;
CREATE TABLE `app_announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `org_id` bigint NULL DEFAULT 0 COMMENT '所属机构(0为全局)',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `type` smallint NULL DEFAULT 0 COMMENT '0内部通知 1车主端公告',
  `status` tinyint NULL DEFAULT 1,
  `creator_id` bigint NULL DEFAULT NULL,
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公告' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_announcement
-- ----------------------------
INSERT INTO `app_announcement` VALUES (1, 0, '系统 V3.0 旗舰版升级通知', '系统将于今晚24点停机升级...', 0, 1, 1, '2026-02-19 02:36:16');
INSERT INTO `app_announcement` VALUES (2, 1, '周末洗车半价优惠活动', '尊敬的车主，本周末凭此公告享受精洗半价！', 1, 1, 1, '2026-02-19 02:36:16');
INSERT INTO `app_announcement` VALUES (3, 2, '万达广场周末停车费上调通知', '尊敬的车主，本周末起万达地下车库高峰期收费上调至首小时8元', 1, 1, NULL, '2026-02-22 02:18:52');
INSERT INTO `app_announcement` VALUES (4, 3, '凤城河老街梅花节免费停车活动', '为庆祝梅花节，本周末在老街消费满100元即可凭小票免停3小时！', 1, 1, NULL, '2026-02-22 02:18:52');
INSERT INTO `app_announcement` VALUES (5, 4, '医药城车牌识别系统升级维护', '园区各单位请注意，今晚22点至次日凌晨系统升级，请手动刷卡入内。', 0, 1, NULL, '2026-02-22 02:18:52');
INSERT INTO `app_announcement` VALUES (6, 5, '中医院严禁占用救护车通道', '近期发现多起私家车占用急救通道现象，现已接入交警违停抓拍系统！', 1, 1, NULL, '2026-02-22 02:18:52');
INSERT INTO `app_announcement` VALUES (7, 6, '坡子街商户包月活动开启', '即日起，坡子街沿街商户可凭营业执照办理200元/月的特惠包月套餐。', 0, 1, NULL, '2026-02-22 02:18:52');

-- ----------------------------
-- Table structure for app_car_reservation
-- ----------------------------
DROP TABLE IF EXISTS `app_car_reservation`;
CREATE TABLE `app_car_reservation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parking_lot_id` bigint NOT NULL,
  `parking_space_id` bigint NOT NULL,
  `plate_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `reservation_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL,
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0:预约中 1:已履约 2:已取消 3:已违约过期',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plate`(`plate_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '车位预约' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_car_reservation
-- ----------------------------
INSERT INTO `app_car_reservation` VALUES (1, 1, 1, '苏G6B608', '2026-02-19 02:36:16', '2026-02-19 03:06:16', 0, '2026-02-19 02:36:16');

-- ----------------------------
-- Table structure for app_car_tag
-- ----------------------------
DROP TABLE IF EXISTS `app_car_tag`;
CREATE TABLE `app_car_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parking_lot_id` bigint NOT NULL,
  `plate_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `tag_type` smallint NULL DEFAULT 0 COMMENT '0:普通 1:白名单(特种车/固定) 2:黑名单(逃避漏费)',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `expire_time` datetime NULL DEFAULT NULL COMMENT '到期时间',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tag_plate_park`(`plate_number` ASC, `parking_lot_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '黑白名单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_car_tag
-- ----------------------------
INSERT INTO `app_car_tag` VALUES (1, 1, '苏M12345', '校内教职工', 1, 'green', '教师免收费', NULL, '2026-03-19 02:36:16', '2026-02-19 02:36:16');
INSERT INTO `app_car_tag` VALUES (2, 1, '苏M99999', '恶意逃费车', 2, 'red', '多次跟车闯闸逃费', NULL, '2026-03-19 02:36:16', '2026-02-19 02:36:16');
INSERT INTO `app_car_tag` VALUES (3, 4, '苏MD1234', '医药城研发人员', 1, 'green', '园区内部员工免停', NULL, '2027-01-01 00:00:00', '2026-02-22 02:17:11');
INSERT INTO `app_car_tag` VALUES (4, 2, '苏M00000', '万达黑名单', 2, 'red', '多次恶意冲卡逃费', NULL, '2099-12-31 00:00:00', '2026-02-22 02:17:11');
INSERT INTO `app_car_tag` VALUES (5, 5, '苏MJ9999', '医院救护车', 1, 'green', '特种车辆无条件抬杆', NULL, '2099-12-31 00:00:00', '2026-02-22 02:17:11');
INSERT INTO `app_car_tag` VALUES (6, 3, '苏M11111', '老街商户', 1, 'blue', '老街餐饮店老板车', NULL, '2026-12-31 00:00:00', '2026-02-22 02:17:11');
INSERT INTO `app_car_tag` VALUES (7, 6, '苏M22222', '坡子街卸货车', 1, 'yellow', '每日早晨允许卸货', NULL, '2026-06-30 00:00:00', '2026-02-22 02:17:11');

-- ----------------------------
-- Table structure for app_carwash_order
-- ----------------------------
DROP TABLE IF EXISTS `app_carwash_order`;
CREATE TABLE `app_carwash_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联基础支付订单号',
  `plate_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `parking_lot_id` bigint NULL DEFAULT NULL COMMENT '停车场ID',
  `appointment_time` datetime NULL DEFAULT NULL COMMENT '预约时间',
  `wash_type` tinyint NULL DEFAULT 1 COMMENT '洗车类型',
  `staff_id` bigint NULL DEFAULT NULL COMMENT '指派洗车技师ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '流程状态: 0待清洗 1清洗中 2已完成质检',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '洗车工单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_carwash_order
-- ----------------------------
INSERT INTO `app_carwash_order` VALUES (2, 'VS26030215301234', '苏G6B608', 1, '2026-03-03 11:00:00', 2, NULL, 0, '2026-03-02 05:40:29', '2026-03-02 06:35:45');

-- ----------------------------
-- Table structure for app_equipment
-- ----------------------------
DROP TABLE IF EXISTS `app_equipment`;
CREATE TABLE `app_equipment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备名称',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备序列号/IP',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备类型(闸机/监控/充电桩/卫生间传感器)',
  `parking_lot_id` bigint NOT NULL,
  `status` tinyint NULL DEFAULT 1 COMMENT '0:故障/离线 1:在线正常',
  `last_maintain_time` datetime NULL DEFAULT NULL COMMENT '上次维保完成时间',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '0未删除 1已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_equipment
-- ----------------------------
INSERT INTO `app_equipment` VALUES (1, '南门入口抓拍机', 'CAM-S-IN-01', 'camera', 1, 1, NULL, '2026-02-19 02:36:16', '2026-02-19 02:36:16', 0);
INSERT INTO `app_equipment` VALUES (2, '南门出口道闸', 'GATE-S-OUT-01', 'barrier', 1, 0, NULL, '2026-02-19 02:36:16', '2026-02-19 02:36:16', 0);
INSERT INTO `app_equipment` VALUES (3, '万达南门入口海康抓拍机', 'HK-IN-WANDA-01', 'camera', 2, 1, NULL, '2026-02-22 02:18:30', '2026-02-22 02:18:30', 0);
INSERT INTO `app_equipment` VALUES (4, '万达北门出口大华道闸', 'DH-OUT-WANDA-01', 'barrier', 2, 1, NULL, '2026-02-22 02:18:30', '2026-02-22 02:18:30', 0);
INSERT INTO `app_equipment` VALUES (5, '老街东入口抓拍一体机', 'CAM-FCH-E01', 'camera', 3, 1, NULL, '2026-02-22 02:18:30', '2026-02-22 02:18:30', 0);

-- ----------------------------
-- Table structure for app_equipment_maintain
-- ----------------------------
DROP TABLE IF EXISTS `app_equipment_maintain`;
CREATE TABLE `app_equipment_maintain`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `equipment_id` bigint NOT NULL COMMENT '故障设备ID',
  `issue_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '故障详细描述',
  `reporter_id` bigint NULL DEFAULT NULL COMMENT '报修人(保安/巡检员)ID',
  `maintainer_id` bigint NULL DEFAULT NULL COMMENT '指派维修工程师ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '维保状态: 0待派单 1维修中 2已修复 3需返厂报废',
  `pay_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '本次维修产生的费用',
  `fix_time` datetime NULL DEFAULT NULL COMMENT '彻底修复时间',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报修时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备维保' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_equipment_maintain
-- ----------------------------
INSERT INTO `app_equipment_maintain` VALUES (4, 4, '万达北门出口大华道闸，雷达防砸不灵敏', NULL, NULL, 2, 150.00, NULL, '2026-02-22 02:18:42');
INSERT INTO `app_equipment_maintain` VALUES (5, 5, '老街东入口抓拍一体机夜间补光灯不亮', NULL, NULL, 1, 0.00, NULL, '2026-02-22 02:18:42');
INSERT INTO `app_equipment_maintain` VALUES (6, 3, '南门出口道闸主板损坏（历史遗留）', NULL, NULL, 3, 800.00, NULL, '2026-02-22 02:18:42');

-- ----------------------------
-- Table structure for app_expense
-- ----------------------------
DROP TABLE IF EXISTS `app_expense`;
CREATE TABLE `app_expense`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expense_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支出单号',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支出类别: 设备维护费/电费/人工成本/退款金额/其他支出',
  `amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '支出金额',
  `parking_lot_id` bigint NULL DEFAULT NULL COMMENT '关联停车场ID',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支出说明',
  `status` tinyint NULL DEFAULT NULL COMMENT '状态: 0待审核 1已通过 2已拒绝',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '审批人ID',
  `approve_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `gmt_create` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '支出' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_expense
-- ----------------------------
INSERT INTO `app_expense` VALUES (1, 'EX26022117080675', '设备维护费', 1500.00, 1, '道闸系统年度维护保养', 1, 1, 1, '2026-02-21 17:08:40', '2026-02-21 17:08:40');
INSERT INTO `app_expense` VALUES (2, 'EX26022117080618', '电费', 980.50, 1, '2026年2月电费缴纳', 1, 1, 1, '2026-02-21 17:08:40', '2026-02-21 17:08:40');
INSERT INTO `app_expense` VALUES (3, 'EX26022119121067', '设备维护费', 8500.00, 1, '停车场道闸系统采购', 2, NULL, NULL, '2026-02-21 19:12:29', '2026-02-21 19:12:14');
INSERT INTO `app_expense` VALUES (4, 'EX26022119123481', '水费', 1280.50, 1, '2026年2月停车场水费', 1, NULL, NULL, '2026-02-23 01:11:16', '2026-02-21 19:12:14');
INSERT INTO `app_expense` VALUES (5, 'EX26022119124203', '其他支出', 15000.00, 1, '2026年2月停车场房租', 1, NULL, NULL, '2026-02-23 01:11:28', '2026-02-21 19:12:14');
INSERT INTO `app_expense` VALUES (6, 'EX26022301050575', '电费', 15800.50, 2, '万达广场地库2026年1月照明电费', 1, NULL, NULL, NULL, NULL);
INSERT INTO `app_expense` VALUES (7, 'EX26022301050265', '人工成本', 35000.00, 3, '老街保安队2026年1月工资发放', 1, NULL, NULL, NULL, NULL);
INSERT INTO `app_expense` VALUES (8, 'EX26022301059601', '设备维护费', 4200.00, 4, '医药城道闸系统第一季度维保款', 0, NULL, NULL, NULL, NULL);
INSERT INTO `app_expense` VALUES (9, 'EX26022301057212', '其他支出', 800.00, 5, '中医院车场绿化带修剪费用', 2, NULL, NULL, NULL, NULL);
INSERT INTO `app_expense` VALUES (10, 'EX26022301057256', '退款金额', 15.00, 3, '退还吴先生多扣的停车费', 1, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for app_fee_standard
-- ----------------------------
DROP TABLE IF EXISTS `app_fee_standard`;
CREATE TABLE `app_fee_standard`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parking_lot_id` bigint NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收费标准名称',
  `vehicle_type` smallint NOT NULL DEFAULT 0 COMMENT '车辆类型: 0小型车 1中型车 2大型车',
  `free_minutes` int NOT NULL DEFAULT 30 COMMENT '免费时长(分钟)',
  `first_hour_fee` decimal(10, 2) NOT NULL DEFAULT 5.00 COMMENT '首小时收费(元)',
  `after_first_hour_fee` decimal(10, 2) NOT NULL DEFAULT 2.00 COMMENT '首小时后每小时收费(元)',
  `day_max_fee` decimal(10, 2) NOT NULL DEFAULT 50.00 COMMENT '单日最高收费(元)',
  `night_start_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '22:00' COMMENT '夜间开始时间',
  `night_end_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '06:00' COMMENT '夜间结束时间',
  `night_fee` decimal(10, 2) NULL DEFAULT 10.00 COMMENT '夜间收费(元)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_park_vehicle`(`parking_lot_id` ASC, `vehicle_type` ASC) USING BTREE,
  INDEX `idx_lot_vehicle`(`parking_lot_id` ASC, `vehicle_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收费标准' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_fee_standard
-- ----------------------------
INSERT INTO `app_fee_standard` VALUES (1, 1, '小型车收费标准', 0, 0, 5.00, 2.00, 50.00, '22:00', '06:00', 10.00, 1, '2026-02-19 02:36:16', '2026-02-19 02:36:16');
INSERT INTO `app_fee_standard` VALUES (4, 2, '小型车收费标准', 0, 0, 4.00, 1.50, 40.00, '22:00', '06:00', 8.00, 1, '2026-02-19 02:36:16', '2026-02-19 02:36:16');

-- ----------------------------
-- Table structure for app_feedback
-- ----------------------------
DROP TABLE IF EXISTS `app_feedback`;
CREATE TABLE `app_feedback`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '投诉/咨询内容',
  `reply_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客服回复内容',
  `reply_user_id` bigint NULL DEFAULT NULL COMMENT '处理客服ID',
  `reply_time` datetime NULL DEFAULT NULL,
  `type` smallint NULL DEFAULT 0 COMMENT '0意见反馈 1错扣费申诉 2设备报修',
  `status` tinyint NULL DEFAULT 0 COMMENT '0待处理 1已回复 2已完结',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客诉反馈' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_feedback
-- ----------------------------
INSERT INTO `app_feedback` VALUES (1, 1, '张先生', '13811112222', '我的月卡明明没过期，为什么今天入场提示我缴费？', NULL, NULL, NULL, 1, 1, '2026-02-19 02:36:16');
INSERT INTO `app_feedback` VALUES (2, 2, '李女士', '13933334444', '地下车库B区灯光太暗了，麻烦修理一下', NULL, NULL, NULL, 0, 1, '2026-02-19 02:36:16');
INSERT INTO `app_feedback` VALUES (4, 4, '吴先生', '13952662222', '老街停车场出来的时候扫码扣了两次费！要求退款！', NULL, NULL, NULL, 1, 1, '2026-02-22 02:19:32');
INSERT INTO `app_feedback` VALUES (5, 5, '陈工', '15852663333', '医药城充电桩有两台都是坏的，请尽快维修。', NULL, NULL, NULL, 2, 0, '2026-02-22 02:19:32');
INSERT INTO `app_feedback` VALUES (6, 6, '王阿姨', '18952664444', '中医院停车太难了，建议多开几个临时车位。', '收到，马上整改！', NULL, NULL, 0, 1, '2026-02-22 02:19:32');

-- ----------------------------
-- Table structure for app_invoice
-- ----------------------------
DROP TABLE IF EXISTS `app_invoice`;
CREATE TABLE `app_invoice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联支付订单号(支持多个订单合并开票，用逗号分隔)',
  `user_id` bigint NOT NULL COMMENT '申请人ID',
  `amount` decimal(18, 2) NOT NULL COMMENT '开票总金额',
  `invoice_type` tinyint NULL DEFAULT 1 COMMENT '发票类型: 1企业普票 2企业专票 3个人普票',
  `invoice_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发票抬头',
  `invoice_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '开票内容',
  `tax_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '企业税号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接收电子发票邮箱',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0待开具 1已开具 2开具失败/驳回',
  `invoice_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电子发票PDF下载链接',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '发票' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_invoice
-- ----------------------------
INSERT INTO `app_invoice` VALUES (1, 'VS202602190001', 2, 13.00, 3, '李四(个人)', NULL, NULL, 'lisi@example.com', 1, NULL, '2026-02-19 02:36:16');
INSERT INTO `app_invoice` VALUES (2, 'VS202602190002', 1, 88.00, 1, '泰州某某科技有限公司', NULL, '', 'zhangsan@example.com', 1, NULL, '2026-02-19 02:36:16');
INSERT INTO `app_invoice` VALUES (7, 'PK26030215305678', 5, 0.00, 1, '个人', '停车服务车辆停放费', NULL, 'user@example.com', 1, NULL, '2026-03-02 17:41:29');
INSERT INTO `app_invoice` VALUES (8, 'PK26030215305678', 5, 0.00, 1, '个人', '*停车服务*车辆停放费', NULL, 'user@example.com', 1, NULL, '2026-03-02 17:48:39');
INSERT INTO `app_invoice` VALUES (9, 'PK26030215305678', 5, 0.00, 1, '个人', '*停车服务*车辆停放费', NULL, 'user@example.com', 1, NULL, '2026-03-02 17:50:27');
INSERT INTO `app_invoice` VALUES (10, 'PK26030215305678', 5, 0.00, 1, '个人', '*停车服务*车辆停放费', NULL, 'user@example.com', 1, NULL, '2026-03-02 17:53:21');

-- ----------------------------
-- Table structure for app_operation_strategy
-- ----------------------------
DROP TABLE IF EXISTS `app_operation_strategy`;
CREATE TABLE `app_operation_strategy`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parking_lot_id` bigint NOT NULL DEFAULT 0 COMMENT '所属停车场ID(0为全局)',
  `strategy_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '策略名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '策略描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
  `rule_condition` json NULL COMMENT '触发规则详情(JSON格式，包含时段、车型等条件)',
  `is_stackable` tinyint NOT NULL DEFAULT 0 COMMENT '是否允许与其他优惠叠加: 0否 1是',
  `apply_vehicle_types` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0,1,2' COMMENT '适用车辆类型，逗号分隔',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `creator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人姓名(对应截图)',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '0未删除 1已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '运营策略' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_operation_strategy
-- ----------------------------
INSERT INTO `app_operation_strategy` VALUES (1, 0, '高峰期价格策略', '工作日 7:00-9:00, 17:00-19:00 提高停车费率', 1, NULL, 0, '0,1,2', NULL, 'admin', '2024-01-01 00:00:00', '2026-02-19 16:26:54', 0);
INSERT INTO `app_operation_strategy` VALUES (2, 0, '节假日优惠策略', '法定节假日停车费用8折', 1, NULL, 0, '0,1,2', NULL, 'admin', '2024-01-02 00:00:00', '2026-02-19 16:26:54', 0);
INSERT INTO `app_operation_strategy` VALUES (5, 0, '新能源车辆优惠', '新能源车辆停车费用8.5折', 1, NULL, 0, '0,1,2', NULL, 'admin', '2024-01-05 00:00:00', '2026-02-19 16:26:54', 0);
INSERT INTO `app_operation_strategy` VALUES (6, 2, '万达周末高峰溢价', '周六日 14:00-20:00 停车费上浮20%', 1, NULL, 0, '0,1,2', NULL, 'wanda_admin', '2026-02-22 02:19:12', '2026-02-23 02:05:31', 0);
INSERT INTO `app_operation_strategy` VALUES (7, 3, '老街夜游半价', '晚20:00以后入场，停车费打5折', 1, NULL, 0, '0,1,2', NULL, 'fch_admin', '2026-02-22 02:19:12', '2026-02-23 02:05:33', 0);
INSERT INTO `app_operation_strategy` VALUES (8, 4, '医药城绿牌车优惠', '响应泰州环保政策，新能源车打8折', 1, NULL, 0, '0,1,2', NULL, 'yyc_admin', '2026-02-22 02:19:12', '2026-02-22 02:19:12', 0);

-- ----------------------------
-- Table structure for app_order
-- ----------------------------
DROP TABLE IF EXISTS `app_order`;
CREATE TABLE `app_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统内部订单号',
  `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方支付流水号(微信/支付宝)',
  `order_type` tinyint NOT NULL DEFAULT 1 COMMENT '业务类型: 1代泊车 2洗车服务 3月卡服务',
  `body` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单描述',
  `pay_amount` decimal(12, 2) NULL DEFAULT 0.00,
  `original_amount` decimal(12, 2) NULL DEFAULT 0.00,
  `discount_amount` decimal(12, 2) NULL DEFAULT 0.00,
  `plate_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联车牌号',
  `parking_lot_id` bigint NOT NULL,
  `fee_standard_id` bigint NULL DEFAULT NULL COMMENT '收费标准ID',
  `vehicle_type` smallint NULL DEFAULT 0 COMMENT '车辆类型: 0小型车 1中型车 2大型车',
  `charge_minutes` int NULL DEFAULT 0 COMMENT '计费时长(分钟)',
  `gmt_into` datetime NULL DEFAULT NULL COMMENT '入场时间',
  `gmt_out` datetime NULL DEFAULT NULL COMMENT '出场时间',
  `status` smallint NULL DEFAULT 0 COMMENT '0:未支付 1:已支付 2:已退款 3:已取消',
  `pay_way` smallint NULL DEFAULT 0 COMMENT '支付方式: 0微信 1支付宝',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户(下单人)',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '实际支付成功时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_order_type`(`order_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '主订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_order
-- ----------------------------
INSERT INTO `app_order` VALUES (1, 'VS26030215303456', NULL, 1, '泊车服务', 20.00, 20.00, 0.00, '苏G6B608', 1, NULL, 0, 0, '2026-02-19 02:36:16', '2026-02-20 02:36:16', 1, 1, 2, NULL, NULL, '2026-02-19 02:36:16');
INSERT INTO `app_order` VALUES (13, 'VS26030123531921', NULL, 3, '月卡服务\r\n', 0.00, 0.00, 0.00, '苏G6B608', 0, NULL, 0, 0, NULL, NULL, 1, 0, NULL, NULL, NULL, '2026-03-01 23:53:24');

-- ----------------------------
-- Table structure for app_parking_lot
-- ----------------------------
DROP TABLE IF EXISTS `app_parking_lot`;
CREATE TABLE `app_parking_lot`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '停车场名称',
  `org_id` bigint NOT NULL COMMENT '所属机构',
  `parking_space_number` int NULL DEFAULT 0 COMMENT '总车位数',
  `available_space_number` int NULL DEFAULT 0 COMMENT '剩余可用车位(实时更新)',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1开放 0关闭',
  `creator_id` bigint NULL DEFAULT NULL,
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '0未删除 1已删除',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '停车场地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '停车场' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_parking_lot
-- ----------------------------
INSERT INTO `app_parking_lot` VALUES (1, '泰州学院南门地下车库', 1, 50, 50, 1, NULL, '2026-02-19 02:36:16', '2026-03-03 01:49:46', 0, '泰州市海陵区济川东路93号');
INSERT INTO `app_parking_lot` VALUES (2, '泰州学院行政楼地面车场', 1, 100, 100, 1, NULL, '2026-02-19 02:36:16', '2026-02-22 18:26:39', 0, '泰州市海陵区济川东路93号');
INSERT INTO `app_parking_lot` VALUES (3, '泰州万达广场地下停车场', 2, 100, 100, 1, NULL, '2026-02-22 02:15:24', '2026-02-22 18:26:46', 0, '泰州市海陵区济川东路222号');
INSERT INTO `app_parking_lot` VALUES (4, '凤城河老街游客车场', 3, 100, 100, 1, NULL, '2026-02-22 02:15:24', '2026-02-22 18:26:50', 0, '泰州市海陵区迎春东路老街南门');
INSERT INTO `app_parking_lot` VALUES (5, '医药城国际会议中心车场', 4, 100, 100, 1, NULL, '2026-02-22 02:15:24', '2026-02-22 18:26:52', 0, '泰州市高新区药城大道');

-- ----------------------------
-- Table structure for app_parking_record
-- ----------------------------
DROP TABLE IF EXISTS `app_parking_record`;
CREATE TABLE `app_parking_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plate_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '车牌号',
  `parking_lot_id` bigint NOT NULL,
  `gmt_into` datetime NULL DEFAULT NULL COMMENT '入场时间',
  `gmt_out` datetime NULL DEFAULT NULL COMMENT '出场时间',
  `stay_minutes` int NULL DEFAULT 0 COMMENT '已停留总时长(分钟) - 用于僵尸车分级预警大屏',
  `pay_amount` decimal(12, 2) NULL DEFAULT 0.00,
  `original_amount` decimal(12, 2) NULL DEFAULT 0.00,
  `discount_amount` decimal(12, 2) NULL DEFAULT 0.00,
  `fee_standard_id` bigint NULL DEFAULT NULL COMMENT '收费标准ID',
  `vehicle_type` smallint NULL DEFAULT 0 COMMENT '车辆类型: 0小型车 1中型车 2大型车',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联缴费订单号',
  `payment_status` tinyint NULL DEFAULT 0 COMMENT '支付状态: 0未支付 1已支付 2部分支付',
  `pay_way` smallint NULL DEFAULT 0 COMMENT '支付方式: 0微信 1支付宝',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户(下单人)',
  `type` smallint NULL DEFAULT NULL COMMENT '入场时车辆类型快照(临时/月卡)',
  `status` int NULL DEFAULT 0 COMMENT '0:停车中 1:已出场 2:异常放行',
  `space_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '车位号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plate_time`(`plate_number` ASC, `gmt_into` ASC) USING BTREE,
  INDEX `idx_space_no`(`space_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '进出场流水' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_parking_record
-- ----------------------------
INSERT INTO `app_parking_record` VALUES (16, '苏G6B608', 1, '2026-03-02 01:49:47', '2026-03-02 02:54:42', 64, 0.00, 0.00, 0.00, NULL, 0, 'PK26030215305678', 0, 0, 5, 1, 1, '043');
INSERT INTO `app_parking_record` VALUES (17, '苏G6B608', 1, '2026-03-02 02:54:45', '2026-03-03 01:49:47', 1375, 0.00, 0.00, 0.00, NULL, 0, 'PK26030202543096', 1, 0, 5, 1, 1, '030');

-- ----------------------------
-- Table structure for app_parking_space
-- ----------------------------
DROP TABLE IF EXISTS `app_parking_space`;
CREATE TABLE `app_parking_space`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parking_lot_id` bigint NOT NULL COMMENT '所属停车场ID',
  `space_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '车位编号(如 A-001)',
  `status` tinyint NULL DEFAULT 0 COMMENT '0:空闲 1:占用 2:预约 3:维护',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_lot_space`(`parking_lot_id` ASC, `space_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8745 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '车位池' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_parking_space
-- ----------------------------
INSERT INTO `app_parking_space` VALUES (8295, 1, '001', 0);
INSERT INTO `app_parking_space` VALUES (8296, 1, '002', 0);
INSERT INTO `app_parking_space` VALUES (8297, 1, '003', 0);
INSERT INTO `app_parking_space` VALUES (8298, 1, '004', 0);
INSERT INTO `app_parking_space` VALUES (8299, 1, '005', 0);
INSERT INTO `app_parking_space` VALUES (8300, 1, '006', 0);
INSERT INTO `app_parking_space` VALUES (8301, 1, '007', 0);
INSERT INTO `app_parking_space` VALUES (8302, 1, '008', 0);
INSERT INTO `app_parking_space` VALUES (8303, 1, '009', 0);
INSERT INTO `app_parking_space` VALUES (8304, 1, '010', 0);
INSERT INTO `app_parking_space` VALUES (8305, 1, '011', 0);
INSERT INTO `app_parking_space` VALUES (8306, 1, '012', 0);
INSERT INTO `app_parking_space` VALUES (8307, 1, '013', 0);
INSERT INTO `app_parking_space` VALUES (8308, 1, '014', 0);
INSERT INTO `app_parking_space` VALUES (8309, 1, '015', 0);
INSERT INTO `app_parking_space` VALUES (8310, 1, '016', 0);
INSERT INTO `app_parking_space` VALUES (8311, 1, '017', 0);
INSERT INTO `app_parking_space` VALUES (8312, 1, '018', 0);
INSERT INTO `app_parking_space` VALUES (8313, 1, '019', 0);
INSERT INTO `app_parking_space` VALUES (8314, 1, '020', 0);
INSERT INTO `app_parking_space` VALUES (8315, 1, '021', 0);
INSERT INTO `app_parking_space` VALUES (8316, 1, '022', 0);
INSERT INTO `app_parking_space` VALUES (8317, 1, '023', 0);
INSERT INTO `app_parking_space` VALUES (8318, 1, '024', 0);
INSERT INTO `app_parking_space` VALUES (8319, 1, '025', 0);
INSERT INTO `app_parking_space` VALUES (8320, 1, '026', 0);
INSERT INTO `app_parking_space` VALUES (8321, 1, '027', 0);
INSERT INTO `app_parking_space` VALUES (8322, 1, '028', 0);
INSERT INTO `app_parking_space` VALUES (8323, 1, '029', 0);
INSERT INTO `app_parking_space` VALUES (8324, 1, '030', 0);
INSERT INTO `app_parking_space` VALUES (8325, 1, '031', 0);
INSERT INTO `app_parking_space` VALUES (8326, 1, '032', 0);
INSERT INTO `app_parking_space` VALUES (8327, 1, '033', 0);
INSERT INTO `app_parking_space` VALUES (8328, 1, '034', 0);
INSERT INTO `app_parking_space` VALUES (8329, 1, '035', 0);
INSERT INTO `app_parking_space` VALUES (8330, 1, '036', 0);
INSERT INTO `app_parking_space` VALUES (8331, 1, '037', 0);
INSERT INTO `app_parking_space` VALUES (8332, 1, '038', 0);
INSERT INTO `app_parking_space` VALUES (8333, 1, '039', 0);
INSERT INTO `app_parking_space` VALUES (8334, 1, '040', 0);
INSERT INTO `app_parking_space` VALUES (8335, 1, '041', 0);
INSERT INTO `app_parking_space` VALUES (8336, 1, '042', 0);
INSERT INTO `app_parking_space` VALUES (8337, 1, '043', 0);
INSERT INTO `app_parking_space` VALUES (8338, 1, '044', 0);
INSERT INTO `app_parking_space` VALUES (8339, 1, '045', 0);
INSERT INTO `app_parking_space` VALUES (8340, 1, '046', 0);
INSERT INTO `app_parking_space` VALUES (8341, 1, '047', 0);
INSERT INTO `app_parking_space` VALUES (8342, 1, '048', 0);
INSERT INTO `app_parking_space` VALUES (8343, 1, '049', 0);
INSERT INTO `app_parking_space` VALUES (8344, 1, '050', 0);
INSERT INTO `app_parking_space` VALUES (8345, 2, '001', 0);
INSERT INTO `app_parking_space` VALUES (8346, 2, '002', 0);
INSERT INTO `app_parking_space` VALUES (8347, 2, '003', 0);
INSERT INTO `app_parking_space` VALUES (8348, 2, '004', 0);
INSERT INTO `app_parking_space` VALUES (8349, 2, '005', 0);
INSERT INTO `app_parking_space` VALUES (8350, 2, '006', 0);
INSERT INTO `app_parking_space` VALUES (8351, 2, '007', 0);
INSERT INTO `app_parking_space` VALUES (8352, 2, '008', 0);
INSERT INTO `app_parking_space` VALUES (8353, 2, '009', 0);
INSERT INTO `app_parking_space` VALUES (8354, 2, '010', 0);
INSERT INTO `app_parking_space` VALUES (8355, 2, '011', 0);
INSERT INTO `app_parking_space` VALUES (8356, 2, '012', 0);
INSERT INTO `app_parking_space` VALUES (8357, 2, '013', 0);
INSERT INTO `app_parking_space` VALUES (8358, 2, '014', 0);
INSERT INTO `app_parking_space` VALUES (8359, 2, '015', 0);
INSERT INTO `app_parking_space` VALUES (8360, 2, '016', 0);
INSERT INTO `app_parking_space` VALUES (8361, 2, '017', 0);
INSERT INTO `app_parking_space` VALUES (8362, 2, '018', 0);
INSERT INTO `app_parking_space` VALUES (8363, 2, '019', 0);
INSERT INTO `app_parking_space` VALUES (8364, 2, '020', 0);
INSERT INTO `app_parking_space` VALUES (8365, 2, '021', 0);
INSERT INTO `app_parking_space` VALUES (8366, 2, '022', 0);
INSERT INTO `app_parking_space` VALUES (8367, 2, '023', 0);
INSERT INTO `app_parking_space` VALUES (8368, 2, '024', 0);
INSERT INTO `app_parking_space` VALUES (8369, 2, '025', 0);
INSERT INTO `app_parking_space` VALUES (8370, 2, '026', 0);
INSERT INTO `app_parking_space` VALUES (8371, 2, '027', 0);
INSERT INTO `app_parking_space` VALUES (8372, 2, '028', 0);
INSERT INTO `app_parking_space` VALUES (8373, 2, '029', 0);
INSERT INTO `app_parking_space` VALUES (8374, 2, '030', 0);
INSERT INTO `app_parking_space` VALUES (8375, 2, '031', 0);
INSERT INTO `app_parking_space` VALUES (8376, 2, '032', 0);
INSERT INTO `app_parking_space` VALUES (8377, 2, '033', 0);
INSERT INTO `app_parking_space` VALUES (8378, 2, '034', 0);
INSERT INTO `app_parking_space` VALUES (8379, 2, '035', 0);
INSERT INTO `app_parking_space` VALUES (8380, 2, '036', 0);
INSERT INTO `app_parking_space` VALUES (8381, 2, '037', 0);
INSERT INTO `app_parking_space` VALUES (8382, 2, '038', 0);
INSERT INTO `app_parking_space` VALUES (8383, 2, '039', 0);
INSERT INTO `app_parking_space` VALUES (8384, 2, '040', 0);
INSERT INTO `app_parking_space` VALUES (8385, 2, '041', 0);
INSERT INTO `app_parking_space` VALUES (8386, 2, '042', 0);
INSERT INTO `app_parking_space` VALUES (8387, 2, '043', 0);
INSERT INTO `app_parking_space` VALUES (8388, 2, '044', 0);
INSERT INTO `app_parking_space` VALUES (8389, 2, '045', 0);
INSERT INTO `app_parking_space` VALUES (8390, 2, '046', 0);
INSERT INTO `app_parking_space` VALUES (8391, 2, '047', 0);
INSERT INTO `app_parking_space` VALUES (8392, 2, '048', 0);
INSERT INTO `app_parking_space` VALUES (8393, 2, '049', 0);
INSERT INTO `app_parking_space` VALUES (8394, 2, '050', 0);
INSERT INTO `app_parking_space` VALUES (8395, 2, '051', 0);
INSERT INTO `app_parking_space` VALUES (8396, 2, '052', 0);
INSERT INTO `app_parking_space` VALUES (8397, 2, '053', 0);
INSERT INTO `app_parking_space` VALUES (8398, 2, '054', 0);
INSERT INTO `app_parking_space` VALUES (8399, 2, '055', 0);
INSERT INTO `app_parking_space` VALUES (8400, 2, '056', 0);
INSERT INTO `app_parking_space` VALUES (8401, 2, '057', 0);
INSERT INTO `app_parking_space` VALUES (8402, 2, '058', 0);
INSERT INTO `app_parking_space` VALUES (8403, 2, '059', 0);
INSERT INTO `app_parking_space` VALUES (8404, 2, '060', 0);
INSERT INTO `app_parking_space` VALUES (8405, 2, '061', 0);
INSERT INTO `app_parking_space` VALUES (8406, 2, '062', 0);
INSERT INTO `app_parking_space` VALUES (8407, 2, '063', 0);
INSERT INTO `app_parking_space` VALUES (8408, 2, '064', 0);
INSERT INTO `app_parking_space` VALUES (8409, 2, '065', 0);
INSERT INTO `app_parking_space` VALUES (8410, 2, '066', 0);
INSERT INTO `app_parking_space` VALUES (8411, 2, '067', 0);
INSERT INTO `app_parking_space` VALUES (8412, 2, '068', 0);
INSERT INTO `app_parking_space` VALUES (8413, 2, '069', 0);
INSERT INTO `app_parking_space` VALUES (8414, 2, '070', 0);
INSERT INTO `app_parking_space` VALUES (8415, 2, '071', 0);
INSERT INTO `app_parking_space` VALUES (8416, 2, '072', 0);
INSERT INTO `app_parking_space` VALUES (8417, 2, '073', 0);
INSERT INTO `app_parking_space` VALUES (8418, 2, '074', 0);
INSERT INTO `app_parking_space` VALUES (8419, 2, '075', 0);
INSERT INTO `app_parking_space` VALUES (8420, 2, '076', 0);
INSERT INTO `app_parking_space` VALUES (8421, 2, '077', 0);
INSERT INTO `app_parking_space` VALUES (8422, 2, '078', 0);
INSERT INTO `app_parking_space` VALUES (8423, 2, '079', 0);
INSERT INTO `app_parking_space` VALUES (8424, 2, '080', 0);
INSERT INTO `app_parking_space` VALUES (8425, 2, '081', 0);
INSERT INTO `app_parking_space` VALUES (8426, 2, '082', 0);
INSERT INTO `app_parking_space` VALUES (8427, 2, '083', 0);
INSERT INTO `app_parking_space` VALUES (8428, 2, '084', 0);
INSERT INTO `app_parking_space` VALUES (8429, 2, '085', 0);
INSERT INTO `app_parking_space` VALUES (8430, 2, '086', 0);
INSERT INTO `app_parking_space` VALUES (8431, 2, '087', 0);
INSERT INTO `app_parking_space` VALUES (8432, 2, '088', 0);
INSERT INTO `app_parking_space` VALUES (8433, 2, '089', 0);
INSERT INTO `app_parking_space` VALUES (8434, 2, '090', 0);
INSERT INTO `app_parking_space` VALUES (8435, 2, '091', 0);
INSERT INTO `app_parking_space` VALUES (8436, 2, '092', 0);
INSERT INTO `app_parking_space` VALUES (8437, 2, '093', 0);
INSERT INTO `app_parking_space` VALUES (8438, 2, '094', 0);
INSERT INTO `app_parking_space` VALUES (8439, 2, '095', 0);
INSERT INTO `app_parking_space` VALUES (8440, 2, '096', 0);
INSERT INTO `app_parking_space` VALUES (8441, 2, '097', 0);
INSERT INTO `app_parking_space` VALUES (8442, 2, '098', 0);
INSERT INTO `app_parking_space` VALUES (8443, 2, '099', 0);
INSERT INTO `app_parking_space` VALUES (8444, 2, '100', 0);
INSERT INTO `app_parking_space` VALUES (8445, 3, '001', 0);
INSERT INTO `app_parking_space` VALUES (8446, 3, '002', 0);
INSERT INTO `app_parking_space` VALUES (8447, 3, '003', 0);
INSERT INTO `app_parking_space` VALUES (8448, 3, '004', 0);
INSERT INTO `app_parking_space` VALUES (8449, 3, '005', 0);
INSERT INTO `app_parking_space` VALUES (8450, 3, '006', 0);
INSERT INTO `app_parking_space` VALUES (8451, 3, '007', 0);
INSERT INTO `app_parking_space` VALUES (8452, 3, '008', 0);
INSERT INTO `app_parking_space` VALUES (8453, 3, '009', 0);
INSERT INTO `app_parking_space` VALUES (8454, 3, '010', 0);
INSERT INTO `app_parking_space` VALUES (8455, 3, '011', 0);
INSERT INTO `app_parking_space` VALUES (8456, 3, '012', 0);
INSERT INTO `app_parking_space` VALUES (8457, 3, '013', 0);
INSERT INTO `app_parking_space` VALUES (8458, 3, '014', 0);
INSERT INTO `app_parking_space` VALUES (8459, 3, '015', 0);
INSERT INTO `app_parking_space` VALUES (8460, 3, '016', 0);
INSERT INTO `app_parking_space` VALUES (8461, 3, '017', 0);
INSERT INTO `app_parking_space` VALUES (8462, 3, '018', 0);
INSERT INTO `app_parking_space` VALUES (8463, 3, '019', 0);
INSERT INTO `app_parking_space` VALUES (8464, 3, '020', 0);
INSERT INTO `app_parking_space` VALUES (8465, 3, '021', 0);
INSERT INTO `app_parking_space` VALUES (8466, 3, '022', 0);
INSERT INTO `app_parking_space` VALUES (8467, 3, '023', 0);
INSERT INTO `app_parking_space` VALUES (8468, 3, '024', 0);
INSERT INTO `app_parking_space` VALUES (8469, 3, '025', 0);
INSERT INTO `app_parking_space` VALUES (8470, 3, '026', 0);
INSERT INTO `app_parking_space` VALUES (8471, 3, '027', 0);
INSERT INTO `app_parking_space` VALUES (8472, 3, '028', 0);
INSERT INTO `app_parking_space` VALUES (8473, 3, '029', 0);
INSERT INTO `app_parking_space` VALUES (8474, 3, '030', 0);
INSERT INTO `app_parking_space` VALUES (8475, 3, '031', 0);
INSERT INTO `app_parking_space` VALUES (8476, 3, '032', 0);
INSERT INTO `app_parking_space` VALUES (8477, 3, '033', 0);
INSERT INTO `app_parking_space` VALUES (8478, 3, '034', 0);
INSERT INTO `app_parking_space` VALUES (8479, 3, '035', 0);
INSERT INTO `app_parking_space` VALUES (8480, 3, '036', 0);
INSERT INTO `app_parking_space` VALUES (8481, 3, '037', 0);
INSERT INTO `app_parking_space` VALUES (8482, 3, '038', 0);
INSERT INTO `app_parking_space` VALUES (8483, 3, '039', 0);
INSERT INTO `app_parking_space` VALUES (8484, 3, '040', 0);
INSERT INTO `app_parking_space` VALUES (8485, 3, '041', 0);
INSERT INTO `app_parking_space` VALUES (8486, 3, '042', 0);
INSERT INTO `app_parking_space` VALUES (8487, 3, '043', 0);
INSERT INTO `app_parking_space` VALUES (8488, 3, '044', 0);
INSERT INTO `app_parking_space` VALUES (8489, 3, '045', 0);
INSERT INTO `app_parking_space` VALUES (8490, 3, '046', 0);
INSERT INTO `app_parking_space` VALUES (8491, 3, '047', 0);
INSERT INTO `app_parking_space` VALUES (8492, 3, '048', 0);
INSERT INTO `app_parking_space` VALUES (8493, 3, '049', 0);
INSERT INTO `app_parking_space` VALUES (8494, 3, '050', 0);
INSERT INTO `app_parking_space` VALUES (8495, 3, '051', 0);
INSERT INTO `app_parking_space` VALUES (8496, 3, '052', 0);
INSERT INTO `app_parking_space` VALUES (8497, 3, '053', 0);
INSERT INTO `app_parking_space` VALUES (8498, 3, '054', 0);
INSERT INTO `app_parking_space` VALUES (8499, 3, '055', 0);
INSERT INTO `app_parking_space` VALUES (8500, 3, '056', 0);
INSERT INTO `app_parking_space` VALUES (8501, 3, '057', 0);
INSERT INTO `app_parking_space` VALUES (8502, 3, '058', 0);
INSERT INTO `app_parking_space` VALUES (8503, 3, '059', 0);
INSERT INTO `app_parking_space` VALUES (8504, 3, '060', 0);
INSERT INTO `app_parking_space` VALUES (8505, 3, '061', 0);
INSERT INTO `app_parking_space` VALUES (8506, 3, '062', 0);
INSERT INTO `app_parking_space` VALUES (8507, 3, '063', 0);
INSERT INTO `app_parking_space` VALUES (8508, 3, '064', 0);
INSERT INTO `app_parking_space` VALUES (8509, 3, '065', 0);
INSERT INTO `app_parking_space` VALUES (8510, 3, '066', 0);
INSERT INTO `app_parking_space` VALUES (8511, 3, '067', 0);
INSERT INTO `app_parking_space` VALUES (8512, 3, '068', 0);
INSERT INTO `app_parking_space` VALUES (8513, 3, '069', 0);
INSERT INTO `app_parking_space` VALUES (8514, 3, '070', 0);
INSERT INTO `app_parking_space` VALUES (8515, 3, '071', 0);
INSERT INTO `app_parking_space` VALUES (8516, 3, '072', 0);
INSERT INTO `app_parking_space` VALUES (8517, 3, '073', 0);
INSERT INTO `app_parking_space` VALUES (8518, 3, '074', 0);
INSERT INTO `app_parking_space` VALUES (8519, 3, '075', 0);
INSERT INTO `app_parking_space` VALUES (8520, 3, '076', 0);
INSERT INTO `app_parking_space` VALUES (8521, 3, '077', 0);
INSERT INTO `app_parking_space` VALUES (8522, 3, '078', 0);
INSERT INTO `app_parking_space` VALUES (8523, 3, '079', 0);
INSERT INTO `app_parking_space` VALUES (8524, 3, '080', 0);
INSERT INTO `app_parking_space` VALUES (8525, 3, '081', 0);
INSERT INTO `app_parking_space` VALUES (8526, 3, '082', 0);
INSERT INTO `app_parking_space` VALUES (8527, 3, '083', 0);
INSERT INTO `app_parking_space` VALUES (8528, 3, '084', 0);
INSERT INTO `app_parking_space` VALUES (8529, 3, '085', 0);
INSERT INTO `app_parking_space` VALUES (8530, 3, '086', 0);
INSERT INTO `app_parking_space` VALUES (8531, 3, '087', 0);
INSERT INTO `app_parking_space` VALUES (8532, 3, '088', 0);
INSERT INTO `app_parking_space` VALUES (8533, 3, '089', 0);
INSERT INTO `app_parking_space` VALUES (8534, 3, '090', 0);
INSERT INTO `app_parking_space` VALUES (8535, 3, '091', 0);
INSERT INTO `app_parking_space` VALUES (8536, 3, '092', 0);
INSERT INTO `app_parking_space` VALUES (8537, 3, '093', 0);
INSERT INTO `app_parking_space` VALUES (8538, 3, '094', 0);
INSERT INTO `app_parking_space` VALUES (8539, 3, '095', 0);
INSERT INTO `app_parking_space` VALUES (8540, 3, '096', 0);
INSERT INTO `app_parking_space` VALUES (8541, 3, '097', 0);
INSERT INTO `app_parking_space` VALUES (8542, 3, '098', 0);
INSERT INTO `app_parking_space` VALUES (8543, 3, '099', 0);
INSERT INTO `app_parking_space` VALUES (8544, 3, '100', 0);
INSERT INTO `app_parking_space` VALUES (8545, 4, '001', 0);
INSERT INTO `app_parking_space` VALUES (8546, 4, '002', 0);
INSERT INTO `app_parking_space` VALUES (8547, 4, '003', 0);
INSERT INTO `app_parking_space` VALUES (8548, 4, '004', 0);
INSERT INTO `app_parking_space` VALUES (8549, 4, '005', 0);
INSERT INTO `app_parking_space` VALUES (8550, 4, '006', 0);
INSERT INTO `app_parking_space` VALUES (8551, 4, '007', 0);
INSERT INTO `app_parking_space` VALUES (8552, 4, '008', 0);
INSERT INTO `app_parking_space` VALUES (8553, 4, '009', 0);
INSERT INTO `app_parking_space` VALUES (8554, 4, '010', 0);
INSERT INTO `app_parking_space` VALUES (8555, 4, '011', 0);
INSERT INTO `app_parking_space` VALUES (8556, 4, '012', 0);
INSERT INTO `app_parking_space` VALUES (8557, 4, '013', 0);
INSERT INTO `app_parking_space` VALUES (8558, 4, '014', 0);
INSERT INTO `app_parking_space` VALUES (8559, 4, '015', 0);
INSERT INTO `app_parking_space` VALUES (8560, 4, '016', 0);
INSERT INTO `app_parking_space` VALUES (8561, 4, '017', 0);
INSERT INTO `app_parking_space` VALUES (8562, 4, '018', 0);
INSERT INTO `app_parking_space` VALUES (8563, 4, '019', 0);
INSERT INTO `app_parking_space` VALUES (8564, 4, '020', 0);
INSERT INTO `app_parking_space` VALUES (8565, 4, '021', 0);
INSERT INTO `app_parking_space` VALUES (8566, 4, '022', 0);
INSERT INTO `app_parking_space` VALUES (8567, 4, '023', 0);
INSERT INTO `app_parking_space` VALUES (8568, 4, '024', 0);
INSERT INTO `app_parking_space` VALUES (8569, 4, '025', 0);
INSERT INTO `app_parking_space` VALUES (8570, 4, '026', 0);
INSERT INTO `app_parking_space` VALUES (8571, 4, '027', 0);
INSERT INTO `app_parking_space` VALUES (8572, 4, '028', 0);
INSERT INTO `app_parking_space` VALUES (8573, 4, '029', 0);
INSERT INTO `app_parking_space` VALUES (8574, 4, '030', 0);
INSERT INTO `app_parking_space` VALUES (8575, 4, '031', 0);
INSERT INTO `app_parking_space` VALUES (8576, 4, '032', 0);
INSERT INTO `app_parking_space` VALUES (8577, 4, '033', 0);
INSERT INTO `app_parking_space` VALUES (8578, 4, '034', 0);
INSERT INTO `app_parking_space` VALUES (8579, 4, '035', 0);
INSERT INTO `app_parking_space` VALUES (8580, 4, '036', 0);
INSERT INTO `app_parking_space` VALUES (8581, 4, '037', 0);
INSERT INTO `app_parking_space` VALUES (8582, 4, '038', 0);
INSERT INTO `app_parking_space` VALUES (8583, 4, '039', 0);
INSERT INTO `app_parking_space` VALUES (8584, 4, '040', 0);
INSERT INTO `app_parking_space` VALUES (8585, 4, '041', 0);
INSERT INTO `app_parking_space` VALUES (8586, 4, '042', 0);
INSERT INTO `app_parking_space` VALUES (8587, 4, '043', 0);
INSERT INTO `app_parking_space` VALUES (8588, 4, '044', 0);
INSERT INTO `app_parking_space` VALUES (8589, 4, '045', 0);
INSERT INTO `app_parking_space` VALUES (8590, 4, '046', 0);
INSERT INTO `app_parking_space` VALUES (8591, 4, '047', 0);
INSERT INTO `app_parking_space` VALUES (8592, 4, '048', 0);
INSERT INTO `app_parking_space` VALUES (8593, 4, '049', 0);
INSERT INTO `app_parking_space` VALUES (8594, 4, '050', 0);
INSERT INTO `app_parking_space` VALUES (8595, 4, '051', 0);
INSERT INTO `app_parking_space` VALUES (8596, 4, '052', 0);
INSERT INTO `app_parking_space` VALUES (8597, 4, '053', 0);
INSERT INTO `app_parking_space` VALUES (8598, 4, '054', 0);
INSERT INTO `app_parking_space` VALUES (8599, 4, '055', 0);
INSERT INTO `app_parking_space` VALUES (8600, 4, '056', 0);
INSERT INTO `app_parking_space` VALUES (8601, 4, '057', 0);
INSERT INTO `app_parking_space` VALUES (8602, 4, '058', 0);
INSERT INTO `app_parking_space` VALUES (8603, 4, '059', 0);
INSERT INTO `app_parking_space` VALUES (8604, 4, '060', 0);
INSERT INTO `app_parking_space` VALUES (8605, 4, '061', 0);
INSERT INTO `app_parking_space` VALUES (8606, 4, '062', 0);
INSERT INTO `app_parking_space` VALUES (8607, 4, '063', 0);
INSERT INTO `app_parking_space` VALUES (8608, 4, '064', 0);
INSERT INTO `app_parking_space` VALUES (8609, 4, '065', 0);
INSERT INTO `app_parking_space` VALUES (8610, 4, '066', 0);
INSERT INTO `app_parking_space` VALUES (8611, 4, '067', 0);
INSERT INTO `app_parking_space` VALUES (8612, 4, '068', 0);
INSERT INTO `app_parking_space` VALUES (8613, 4, '069', 0);
INSERT INTO `app_parking_space` VALUES (8614, 4, '070', 0);
INSERT INTO `app_parking_space` VALUES (8615, 4, '071', 0);
INSERT INTO `app_parking_space` VALUES (8616, 4, '072', 0);
INSERT INTO `app_parking_space` VALUES (8617, 4, '073', 0);
INSERT INTO `app_parking_space` VALUES (8618, 4, '074', 0);
INSERT INTO `app_parking_space` VALUES (8619, 4, '075', 0);
INSERT INTO `app_parking_space` VALUES (8620, 4, '076', 0);
INSERT INTO `app_parking_space` VALUES (8621, 4, '077', 0);
INSERT INTO `app_parking_space` VALUES (8622, 4, '078', 0);
INSERT INTO `app_parking_space` VALUES (8623, 4, '079', 0);
INSERT INTO `app_parking_space` VALUES (8624, 4, '080', 0);
INSERT INTO `app_parking_space` VALUES (8625, 4, '081', 0);
INSERT INTO `app_parking_space` VALUES (8626, 4, '082', 0);
INSERT INTO `app_parking_space` VALUES (8627, 4, '083', 0);
INSERT INTO `app_parking_space` VALUES (8628, 4, '084', 0);
INSERT INTO `app_parking_space` VALUES (8629, 4, '085', 0);
INSERT INTO `app_parking_space` VALUES (8630, 4, '086', 0);
INSERT INTO `app_parking_space` VALUES (8631, 4, '087', 0);
INSERT INTO `app_parking_space` VALUES (8632, 4, '088', 0);
INSERT INTO `app_parking_space` VALUES (8633, 4, '089', 0);
INSERT INTO `app_parking_space` VALUES (8634, 4, '090', 0);
INSERT INTO `app_parking_space` VALUES (8635, 4, '091', 0);
INSERT INTO `app_parking_space` VALUES (8636, 4, '092', 0);
INSERT INTO `app_parking_space` VALUES (8637, 4, '093', 0);
INSERT INTO `app_parking_space` VALUES (8638, 4, '094', 0);
INSERT INTO `app_parking_space` VALUES (8639, 4, '095', 0);
INSERT INTO `app_parking_space` VALUES (8640, 4, '096', 0);
INSERT INTO `app_parking_space` VALUES (8641, 4, '097', 0);
INSERT INTO `app_parking_space` VALUES (8642, 4, '098', 0);
INSERT INTO `app_parking_space` VALUES (8643, 4, '099', 0);
INSERT INTO `app_parking_space` VALUES (8644, 4, '100', 0);
INSERT INTO `app_parking_space` VALUES (8645, 5, '001', 0);
INSERT INTO `app_parking_space` VALUES (8646, 5, '002', 0);
INSERT INTO `app_parking_space` VALUES (8647, 5, '003', 0);
INSERT INTO `app_parking_space` VALUES (8648, 5, '004', 0);
INSERT INTO `app_parking_space` VALUES (8649, 5, '005', 0);
INSERT INTO `app_parking_space` VALUES (8650, 5, '006', 0);
INSERT INTO `app_parking_space` VALUES (8651, 5, '007', 0);
INSERT INTO `app_parking_space` VALUES (8652, 5, '008', 0);
INSERT INTO `app_parking_space` VALUES (8653, 5, '009', 0);
INSERT INTO `app_parking_space` VALUES (8654, 5, '010', 0);
INSERT INTO `app_parking_space` VALUES (8655, 5, '011', 0);
INSERT INTO `app_parking_space` VALUES (8656, 5, '012', 0);
INSERT INTO `app_parking_space` VALUES (8657, 5, '013', 0);
INSERT INTO `app_parking_space` VALUES (8658, 5, '014', 0);
INSERT INTO `app_parking_space` VALUES (8659, 5, '015', 0);
INSERT INTO `app_parking_space` VALUES (8660, 5, '016', 0);
INSERT INTO `app_parking_space` VALUES (8661, 5, '017', 0);
INSERT INTO `app_parking_space` VALUES (8662, 5, '018', 0);
INSERT INTO `app_parking_space` VALUES (8663, 5, '019', 0);
INSERT INTO `app_parking_space` VALUES (8664, 5, '020', 0);
INSERT INTO `app_parking_space` VALUES (8665, 5, '021', 0);
INSERT INTO `app_parking_space` VALUES (8666, 5, '022', 0);
INSERT INTO `app_parking_space` VALUES (8667, 5, '023', 0);
INSERT INTO `app_parking_space` VALUES (8668, 5, '024', 0);
INSERT INTO `app_parking_space` VALUES (8669, 5, '025', 0);
INSERT INTO `app_parking_space` VALUES (8670, 5, '026', 0);
INSERT INTO `app_parking_space` VALUES (8671, 5, '027', 0);
INSERT INTO `app_parking_space` VALUES (8672, 5, '028', 0);
INSERT INTO `app_parking_space` VALUES (8673, 5, '029', 0);
INSERT INTO `app_parking_space` VALUES (8674, 5, '030', 0);
INSERT INTO `app_parking_space` VALUES (8675, 5, '031', 0);
INSERT INTO `app_parking_space` VALUES (8676, 5, '032', 0);
INSERT INTO `app_parking_space` VALUES (8677, 5, '033', 0);
INSERT INTO `app_parking_space` VALUES (8678, 5, '034', 0);
INSERT INTO `app_parking_space` VALUES (8679, 5, '035', 0);
INSERT INTO `app_parking_space` VALUES (8680, 5, '036', 0);
INSERT INTO `app_parking_space` VALUES (8681, 5, '037', 0);
INSERT INTO `app_parking_space` VALUES (8682, 5, '038', 0);
INSERT INTO `app_parking_space` VALUES (8683, 5, '039', 0);
INSERT INTO `app_parking_space` VALUES (8684, 5, '040', 0);
INSERT INTO `app_parking_space` VALUES (8685, 5, '041', 0);
INSERT INTO `app_parking_space` VALUES (8686, 5, '042', 0);
INSERT INTO `app_parking_space` VALUES (8687, 5, '043', 0);
INSERT INTO `app_parking_space` VALUES (8688, 5, '044', 0);
INSERT INTO `app_parking_space` VALUES (8689, 5, '045', 0);
INSERT INTO `app_parking_space` VALUES (8690, 5, '046', 0);
INSERT INTO `app_parking_space` VALUES (8691, 5, '047', 0);
INSERT INTO `app_parking_space` VALUES (8692, 5, '048', 0);
INSERT INTO `app_parking_space` VALUES (8693, 5, '049', 0);
INSERT INTO `app_parking_space` VALUES (8694, 5, '050', 0);
INSERT INTO `app_parking_space` VALUES (8695, 5, '051', 0);
INSERT INTO `app_parking_space` VALUES (8696, 5, '052', 0);
INSERT INTO `app_parking_space` VALUES (8697, 5, '053', 0);
INSERT INTO `app_parking_space` VALUES (8698, 5, '054', 0);
INSERT INTO `app_parking_space` VALUES (8699, 5, '055', 0);
INSERT INTO `app_parking_space` VALUES (8700, 5, '056', 0);
INSERT INTO `app_parking_space` VALUES (8701, 5, '057', 0);
INSERT INTO `app_parking_space` VALUES (8702, 5, '058', 0);
INSERT INTO `app_parking_space` VALUES (8703, 5, '059', 0);
INSERT INTO `app_parking_space` VALUES (8704, 5, '060', 0);
INSERT INTO `app_parking_space` VALUES (8705, 5, '061', 0);
INSERT INTO `app_parking_space` VALUES (8706, 5, '062', 0);
INSERT INTO `app_parking_space` VALUES (8707, 5, '063', 0);
INSERT INTO `app_parking_space` VALUES (8708, 5, '064', 0);
INSERT INTO `app_parking_space` VALUES (8709, 5, '065', 0);
INSERT INTO `app_parking_space` VALUES (8710, 5, '066', 0);
INSERT INTO `app_parking_space` VALUES (8711, 5, '067', 0);
INSERT INTO `app_parking_space` VALUES (8712, 5, '068', 0);
INSERT INTO `app_parking_space` VALUES (8713, 5, '069', 0);
INSERT INTO `app_parking_space` VALUES (8714, 5, '070', 0);
INSERT INTO `app_parking_space` VALUES (8715, 5, '071', 0);
INSERT INTO `app_parking_space` VALUES (8716, 5, '072', 0);
INSERT INTO `app_parking_space` VALUES (8717, 5, '073', 0);
INSERT INTO `app_parking_space` VALUES (8718, 5, '074', 0);
INSERT INTO `app_parking_space` VALUES (8719, 5, '075', 0);
INSERT INTO `app_parking_space` VALUES (8720, 5, '076', 0);
INSERT INTO `app_parking_space` VALUES (8721, 5, '077', 0);
INSERT INTO `app_parking_space` VALUES (8722, 5, '078', 0);
INSERT INTO `app_parking_space` VALUES (8723, 5, '079', 0);
INSERT INTO `app_parking_space` VALUES (8724, 5, '080', 0);
INSERT INTO `app_parking_space` VALUES (8725, 5, '081', 0);
INSERT INTO `app_parking_space` VALUES (8726, 5, '082', 0);
INSERT INTO `app_parking_space` VALUES (8727, 5, '083', 0);
INSERT INTO `app_parking_space` VALUES (8728, 5, '084', 0);
INSERT INTO `app_parking_space` VALUES (8729, 5, '085', 0);
INSERT INTO `app_parking_space` VALUES (8730, 5, '086', 0);
INSERT INTO `app_parking_space` VALUES (8731, 5, '087', 0);
INSERT INTO `app_parking_space` VALUES (8732, 5, '088', 0);
INSERT INTO `app_parking_space` VALUES (8733, 5, '089', 0);
INSERT INTO `app_parking_space` VALUES (8734, 5, '090', 0);
INSERT INTO `app_parking_space` VALUES (8735, 5, '091', 0);
INSERT INTO `app_parking_space` VALUES (8736, 5, '092', 0);
INSERT INTO `app_parking_space` VALUES (8737, 5, '093', 0);
INSERT INTO `app_parking_space` VALUES (8738, 5, '094', 0);
INSERT INTO `app_parking_space` VALUES (8739, 5, '095', 0);
INSERT INTO `app_parking_space` VALUES (8740, 5, '096', 0);
INSERT INTO `app_parking_space` VALUES (8741, 5, '097', 0);
INSERT INTO `app_parking_space` VALUES (8742, 5, '098', 0);
INSERT INTO `app_parking_space` VALUES (8743, 5, '099', 0);
INSERT INTO `app_parking_space` VALUES (8744, 5, '100', 0);

-- ----------------------------
-- Table structure for app_pay_config
-- ----------------------------
DROP TABLE IF EXISTS `app_pay_config`;
CREATE TABLE `app_pay_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parking_lot_id` bigint NULL DEFAULT NULL,
  `mch_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信/支付宝商户号',
  `secret_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付API密钥',
  `creator_id` bigint NULL DEFAULT NULL,
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '支付配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_pay_config
-- ----------------------------
INSERT INTO `app_pay_config` VALUES (1, 1, '1600000001', 'ABCDEFG123456789', 1, '2026-02-19 02:36:16', '2026-02-19 02:36:16');

-- ----------------------------
-- Table structure for app_user_car_bind
-- ----------------------------
DROP TABLE IF EXISTS `app_user_car_bind`;
CREATE TABLE `app_user_car_bind`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '关联 sys_user 的 ID',
  `plate_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '绑定的车牌号',
  `bind_type` tinyint NULL DEFAULT 1 COMMENT '绑定关系: 1=车主(主账号), 2=亲属(副账号，可使用主账号权益/代付)',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '关联的主账号ID',
  `family_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '家庭组名称',
  `is_auto_pay` tinyint NULL DEFAULT 0 COMMENT '是否开通无感支付: 1是 0否',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '到期时间',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plate`(`plate_number` ASC) USING BTREE,
  INDEX `idx_parent`(`parent_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '车辆绑定' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_user_car_bind
-- ----------------------------
INSERT INTO `app_user_car_bind` VALUES (3, 5, '苏G6B608', 1, NULL, NULL, 0, 1, '2027-03-03 01:49:07', '2026-03-03 01:49:06');
INSERT INTO `app_user_car_bind` VALUES (4, 5, '苏M000000', 2, 3, NULL, 0, 1, '2027-03-03 01:49:33', '2026-03-03 01:49:32');

-- ----------------------------
-- Table structure for app_valet_order
-- ----------------------------
DROP TABLE IF EXISTS `app_valet_order`;
CREATE TABLE `app_valet_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联基础支付订单号',
  `plate_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '车牌号',
  `parking_lot_id` bigint NULL DEFAULT NULL COMMENT '停车场ID',
  `appointment_time` datetime NULL DEFAULT NULL COMMENT '预约时间',
  `staff_id` bigint NULL DEFAULT NULL COMMENT '接单代泊员(员工)ID',
  `check_images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '交接验车时拍摄的划痕照片(JSON URLs)',
  `status` tinyint NULL DEFAULT 0 COMMENT '流程状态: 0待接车 1泊车中 2已停妥 3车主呼叫取车 4已交还车主',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代泊工单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_valet_order
-- ----------------------------

-- ----------------------------
-- Table structure for app_vehicle
-- ----------------------------
DROP TABLE IF EXISTS `app_vehicle`;
CREATE TABLE `app_vehicle`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plate_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '车牌号',
  `owner_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '车主姓名',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户ID',
  `parking_lot_id` bigint NOT NULL,
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0:禁用(拒绝入场) 1:正常',
  `type` smallint NULL DEFAULT 0 COMMENT '车辆计费属性: 0临时车 1月租车',
  `validity_time` datetime NULL DEFAULT NULL COMMENT '月租卡到期时间',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '0未删除 1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_plate_park`(`plate_number` ASC, `parking_lot_id` ASC) USING BTREE COMMENT '同一车场车牌唯一',
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '车辆档案' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_vehicle
-- ----------------------------
INSERT INTO `app_vehicle` VALUES (8, '苏G6B608', '测试', '19515900000', 5, 0, 1, 1, '2026-06-02 00:08:34', '2026-03-01 22:43:34', '2026-03-02 00:31:58', 0);
INSERT INTO `app_vehicle` VALUES (9, '苏M000000', '测试', '19515900001', 5, 0, 1, 0, NULL, '2026-03-03 01:49:26', '2026-03-03 01:49:26', 0);

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `config_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `creator_id` bigint NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'system.logo', 'https://example.com/logo.png', '系统左上角Logo图', 1, 1, '2026-02-19 02:36:16', '2026-02-19 02:36:16');
INSERT INTO `sys_config` VALUES (2, 'parking.free_time', '30', '全局默认免费时长(分钟)', 1, 1, '2026-02-19 02:36:16', '2026-02-19 02:36:16');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `creator_id` bigint NULL DEFAULT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `operation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `time` int NULL DEFAULT NULL COMMENT '响应时间(ms)',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `device_type` tinyint NOT NULL DEFAULT 0,
  `log_type` tinyint NOT NULL DEFAULT 0,
  `exception_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES (1, 1, 'admin', '修改计费规则', 'FeeController.update', NULL, 120, '192.168.1.100', 0, 0, NULL, '2026-02-19 02:36:16');
INSERT INTO `sys_log` VALUES (2, 2, 'test', '手动开闸放行', 'DeviceController.openGate', NULL, 45, '192.168.1.101', 0, 0, NULL, '2026-02-19 02:36:16');

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `creator_id` bigint NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `login_way` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录方式',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录IP',
  `place` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录地点',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登录日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------
INSERT INTO `sys_login_log` VALUES (1, 1, 'admin', 'PC Web', '192.168.1.100', '江苏省泰州市', '2026-02-19 02:36:16');
INSERT INTO `sys_login_log` VALUES (2, 2, 'test', 'PC Web', '192.168.1.101', '江苏省泰州市', '2026-02-19 02:36:16');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NULL DEFAULT 0,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `perms` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `type` int NULL DEFAULT NULL COMMENT '0目录 1菜单 2按钮',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `order_num` int NULL DEFAULT 0,
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '后台菜单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (10, 0, '数据监控与运营', '/dashboard', '', 0, 'ri:dashboard-line', 1, '2026-02-18 23:20:14', '2026-02-18 23:20:14');
INSERT INTO `sys_menu` VALUES (11, 10, '可视化数据看板', 'board', '', 1, 'ri:bar-chart-2-line', 1, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (12, 10, '僵尸车分级预警', 'zombie', '', 1, 'ri:alert-line', 2, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (14, 10, '运营策略调整', 'strategy', '', 1, 'ri:settings-line', 4, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (15, 10, '车场实况监控', 'monitor', '', 1, 'ri:webcam-line', 5, '2026-02-19 05:31:51', '2026-02-19 05:50:20');
INSERT INTO `sys_menu` VALUES (20, 0, '基础业务管理', '/business', '', 0, 'ri:briefcase-4-line', 2, '2026-02-18 23:20:14', '2026-02-18 23:20:14');
INSERT INTO `sys_menu` VALUES (21, 20, '车辆综合查询', 'vehicle', '', 1, 'ri:car-line', 1, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (22, 20, '黑白名单管理', 'blacklist', '', 1, 'ri:list-check', 2, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (23, 20, '亲情组监管', 'family', '', 1, 'ri:team-line', 3, '2026-02-18 23:20:14', '2026-02-19 05:50:20');
INSERT INTO `sys_menu` VALUES (24, 20, '停车记录管理', 'record', '', 1, 'ri:history-line', 4, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (25, 20, '增值服务订单', 'vas-order', '', 1, 'ri:shopping-cart-line', 5, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (30, 0, '停车场配置', '/config', '', 0, 'ri:settings-3-line', 3, '2026-02-18 23:20:14', '2026-02-18 23:20:14');
INSERT INTO `sys_menu` VALUES (31, 30, '停车场管理', 'lot', '', 1, 'ri:building-2-line', 0, '2026-02-20 20:16:47', '2026-02-20 22:15:59');
INSERT INTO `sys_menu` VALUES (32, 30, '收费标准设置', 'fee-setup', '', 1, 'ri:money-cny-box-line', 2, '2026-02-20 23:25:00', '2026-02-22 15:55:45');
INSERT INTO `sys_menu` VALUES (33, 30, '综合设施管理', 'facility', '', 1, 'ri:building-line', 3, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (40, 0, '财务核算', '/finance', '', 0, 'ri:money-cny-circle-line', 4, '2026-02-18 23:20:14', '2026-02-18 23:20:14');
INSERT INTO `sys_menu` VALUES (41, 40, '订单明细查询', 'order', '', 1, 'ri:bill-line', 1, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (42, 40, '财务报表中心', 'report', '', 1, 'ri:file-chart-line', 2, '2026-02-18 23:20:14', '2026-02-19 05:50:20');
INSERT INTO `sys_menu` VALUES (43, 40, '发票综合管理', 'invoice', '', 1, 'ri:receipt-line', 3, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (44, 40, '支出管理', 'expense', '', 1, 'ri:wallet-3-line', 4, '2026-02-21 16:56:55', '2026-02-21 17:05:26');
INSERT INTO `sys_menu` VALUES (50, 0, '客户互动与服务', '/service', '', 0, 'ri:customer-service-2-line', 5, '2026-02-18 23:20:14', '2026-02-18 23:20:14');
INSERT INTO `sys_menu` VALUES (51, 50, '咨询与投诉处理', 'complaint', '', 1, 'ri:message-2-line', 1, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (52, 50, '维修工单跟进', 'maintain', '', 1, 'ri:tools-line', 2, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (53, 50, '通知与公告发布', 'notice', '', 1, 'ri:notification-line', 3, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (54, 50, '服务工单跟进', 'service-order', '', 1, 'ri:file-list-2-line', 4, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (60, 0, '权限与系统管理', '/system', '', 0, 'ri:shield-keyhole-line', 6, '2026-02-18 23:20:14', '2026-02-18 23:20:14');
INSERT INTO `sys_menu` VALUES (61, 60, '管理员账号管理', 'admin', '', 1, 'ri:user-3-line', 1, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (65, 60, '系统操作日志', 'log', '', 1, 'ri:file-text-line', 5, '2026-02-18 23:20:14', '2026-02-19 05:37:18');
INSERT INTO `sys_menu` VALUES (68, 60, '合作单位', 'partner', '', 1, 'ri:building-4-line', 6, '2026-02-22 00:17:47', '2026-02-22 00:17:47');

-- ----------------------------
-- Table structure for sys_menu_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_user`;
CREATE TABLE `sys_menu_user`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NULL DEFAULT 0,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `perms` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `type` int NULL DEFAULT NULL,
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `order_num` int NULL DEFAULT 0,
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '前端菜单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu_user
-- ----------------------------
INSERT INTO `sys_menu_user` VALUES (1, 0, '个人中心', '/user', '', 0, 'ri:user-line', 1, '2026-02-24 19:34:30', '2026-02-24 19:34:30');
INSERT INTO `sys_menu_user` VALUES (3, 1, '个人记录', 'record', '', 1, 'ri:history-line', 2, '2026-02-24 19:34:30', '2026-02-24 19:34:30');
INSERT INTO `sys_menu_user` VALUES (4, 1, '通知中心', 'notification', '', 1, 'ri:bell-line', 3, '2026-02-24 19:34:30', '2026-02-24 19:34:30');
INSERT INTO `sys_menu_user` VALUES (5, 0, '车辆中心', '/user/vehicle', '', 0, 'ri:car-line', 2, '2026-02-24 19:34:30', '2026-02-24 19:34:30');
INSERT INTO `sys_menu_user` VALUES (6, 5, '我的车辆', 'my-vehicle', '', 1, 'ri:car-fill', 1, '2026-02-24 19:34:30', '2026-02-24 19:34:30');
INSERT INTO `sys_menu_user` VALUES (7, 5, '停车记录', 'record', '', 1, 'ri:history-line', 2, '2026-02-24 19:34:30', '2026-02-24 19:34:30');
INSERT INTO `sys_menu_user` VALUES (9, 0, '智慧停车', '/user/parking', '', 0, 'ri:parking-line', 3, '2026-02-24 19:34:30', '2026-02-24 19:34:30');
INSERT INTO `sys_menu_user` VALUES (10, 9, '附近停车场', 'nearby', '', 1, 'ri:map-pin-line', 1, '2026-02-24 19:34:30', '2026-02-24 19:34:30');
INSERT INTO `sys_menu_user` VALUES (11, 9, '预约停车', 'reservation', '', 1, 'ri:calendar-event-line', 2, '2026-02-24 19:34:30', '2026-02-24 23:31:39');
INSERT INTO `sys_menu_user` VALUES (13, 0, '服务大厅', '/user/service', '', 0, 'ri:service-line', 4, '2026-02-24 19:34:30', '2026-02-24 19:34:30');
INSERT INTO `sys_menu_user` VALUES (15, 13, '服务申请', 'server', '', 1, 'ri:car-washing-line', 2, '2026-02-24 19:34:30', '2026-03-02 06:15:28');
INSERT INTO `sys_menu_user` VALUES (16, 13, '咨询与投诉', 'complain', '', 1, 'ri:feedback-line', 3, '2026-02-24 19:34:30', '2026-03-02 06:24:52');
INSERT INTO `sys_menu_user` VALUES (17, 13, '充电服务', 'charge', '', 1, 'ri:battery-charge-line', 4, '2026-02-24 19:34:30', '2026-03-02 06:12:56');
INSERT INTO `sys_menu_user` VALUES (27, 0, '我的首页', '/home', '', 0, 'ri:home-line', 0, '2026-02-25 19:52:48', '2026-02-25 19:52:48');
INSERT INTO `sys_menu_user` VALUES (28, 1, '我的信息', 'profile', '', 1, 'ri:user-settings-line', 1, '2026-02-26 22:23:55', '2026-02-26 22:23:55');
INSERT INTO `sys_menu_user` VALUES (29, 5, '家庭组管理', 'family', '', 1, 'ri:group-line', 3, '2026-03-02 07:20:26', '2026-03-02 07:20:26');

-- ----------------------------
-- Table structure for sys_org
-- ----------------------------
DROP TABLE IF EXISTS `sys_org`;
CREATE TABLE `sys_org`  (
  `org_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NULL DEFAULT 0,
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `director` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `order_num` int NULL DEFAULT 0,
  `status` tinyint NULL DEFAULT 1,
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '0未删除 1已删除',
  PRIMARY KEY (`org_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '机构' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_org
-- ----------------------------
INSERT INTO `sys_org` VALUES (1, 0, 'TZXY-001', '泰州学院', '泰州学院济川校区', '张主任', '13800001234', 'admin@tzu.edu.cn', '江苏省泰州市海陵区济川东路93号', 1, 1, '2026-02-18 23:28:45', '2026-02-19 02:16:07', 0);
INSERT INTO `sys_org` VALUES (42, 0, 'TZ-WANDA', '泰州万达', '泰州万达商业管理有限公司', '王健林', '0523-88888888', 'wanda@taizhou.com', '江苏省泰州市海陵区济川东路222号', 0, 1, '2026-02-22 02:14:53', '2026-02-22 02:14:53', 0);
INSERT INTO `sys_org` VALUES (43, 0, 'TZ-FCH', '凤城河管委会', '泰州凤城河风景区管理委员会', '李主任', '0523-86888888', 'fch@taizhou.com', '江苏省泰州市海陵区迎春东路', 0, 1, '2026-02-22 02:14:53', '2026-02-22 02:14:53', 0);
INSERT INTO `sys_org` VALUES (44, 0, 'TZ-YYC', '医药城物业', '中国医药城核心区物业管理处', '张经理', '0523-87888888', 'yyc@taizhou.com', '江苏省泰州市高新区药城大道1号', 0, 1, '2026-02-22 02:14:53', '2026-02-22 02:14:53', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `org_id` bigint NULL DEFAULT NULL,
  `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `role_sign` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `creator_id` bigint NULL DEFAULT NULL,
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '0未删除 1已删除',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 1, '管理员', 'admin', '拥有系统所有权限的最高管理员', 1, '2026-02-22 00:11:37', '2026-02-22 00:11:37', 0);
INSERT INTO `sys_role` VALUES (2, 1, '普通用户', 'user', '基层操作员或普通用户', 1, '2026-02-22 00:11:37', '2026-02-22 00:11:37', 0);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NULL DEFAULT NULL,
  `menu_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1, 10);
INSERT INTO `sys_role_menu` VALUES (2, 1, 11);
INSERT INTO `sys_role_menu` VALUES (3, 1, 12);
INSERT INTO `sys_role_menu` VALUES (4, 1, 13);
INSERT INTO `sys_role_menu` VALUES (5, 1, 14);
INSERT INTO `sys_role_menu` VALUES (6, 1, 20);
INSERT INTO `sys_role_menu` VALUES (7, 1, 21);
INSERT INTO `sys_role_menu` VALUES (8, 1, 22);
INSERT INTO `sys_role_menu` VALUES (9, 1, 23);
INSERT INTO `sys_role_menu` VALUES (10, 1, 24);
INSERT INTO `sys_role_menu` VALUES (11, 1, 25);
INSERT INTO `sys_role_menu` VALUES (12, 1, 30);
INSERT INTO `sys_role_menu` VALUES (13, 1, 31);
INSERT INTO `sys_role_menu` VALUES (14, 1, 32);
INSERT INTO `sys_role_menu` VALUES (15, 1, 33);
INSERT INTO `sys_role_menu` VALUES (16, 1, 40);
INSERT INTO `sys_role_menu` VALUES (17, 1, 41);
INSERT INTO `sys_role_menu` VALUES (18, 1, 42);
INSERT INTO `sys_role_menu` VALUES (19, 1, 43);
INSERT INTO `sys_role_menu` VALUES (20, 1, 50);
INSERT INTO `sys_role_menu` VALUES (21, 1, 51);
INSERT INTO `sys_role_menu` VALUES (22, 1, 52);
INSERT INTO `sys_role_menu` VALUES (23, 1, 53);
INSERT INTO `sys_role_menu` VALUES (24, 1, 60);
INSERT INTO `sys_role_menu` VALUES (25, 1, 61);
INSERT INTO `sys_role_menu` VALUES (26, 1, 62);
INSERT INTO `sys_role_menu` VALUES (27, 1, 63);
INSERT INTO `sys_role_menu` VALUES (28, 1, 64);
INSERT INTO `sys_role_menu` VALUES (29, 1, 65);
INSERT INTO `sys_role_menu` VALUES (32, 1, 15);
INSERT INTO `sys_role_menu` VALUES (33, 1, 66);
INSERT INTO `sys_role_menu` VALUES (34, 1, 44);
INSERT INTO `sys_role_menu` VALUES (35, 1, 54);

-- ----------------------------
-- Table structure for sys_role_menu_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu_user`;
CREATE TABLE `sys_role_menu_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NULL DEFAULT NULL,
  `menu_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_org
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_org`;
CREATE TABLE `sys_role_org`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NULL DEFAULT NULL,
  `org_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_org
-- ----------------------------
INSERT INTO `sys_role_org` VALUES (1, 1, 1);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `org_id` bigint NOT NULL COMMENT '所属机构ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码哈希',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '性别 1男 2女',
  `status` tinyint NULL DEFAULT 1 COMMENT '0禁用 1正常',
  `user_type` tinyint NULL DEFAULT 0 COMMENT '用户类型: 0管理员 1普通用户',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像路径',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '0未删除 1已删除',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', '13800000001', '1', 1, 0, NULL, '拥有系统所有权限的最高管理员', '2026-02-19 02:31:31', '2026-02-25 02:04:32', 0);
INSERT INTO `sys_user` VALUES (5, 1, 'user1', 'e10adc3949ba59abbe56e057f20f883e', '普通用户', '13900000001', '1', 1, 1, NULL, '普通用户', '2026-02-25 01:30:31', '2026-02-25 01:30:31', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL,
  `role_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2, 2);
INSERT INTO `sys_user_role` VALUES (3, 5, 2);

SET FOREIGN_KEY_CHECKS = 1;
