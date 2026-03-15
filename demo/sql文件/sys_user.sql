/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 90500 (9.5.0)
 Source Host           : localhost:3306
 Source Schema         : lishui_ich

 Target Server Type    : MySQL
 Target Server Version : 90500 (9.5.0)
 File Encoding         : 65001

 Date: 04/03/2026 22:38:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录账号，唯一',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密后的密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像 URL',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `role` enum('USER','ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime(6) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_sys_user_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `uk_sys_user_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 259 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'admin', '管理员', '/pictures/yonghu.png', '', NULL, 'ADMIN', '2025-11-05 00:00:00.000000');
INSERT INTO `sys_user` VALUES (13, 'zwq123', '123', '张伟强', '/pictures/yonghu.png', 'zwq12301@demo.com', '19900000001', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (14, 'lfn123', '123', '李芳娜', '/pictures/yonghu.png', 'lfn12302@demo.com', '19900000002', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (15, 'wjt123', '123', '王俊涛', '/pictures/yonghu.png', 'wjt12303@demo.com', '19900000003', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (16, 'zml123', '123', '赵敏丽', '/pictures/yonghu.png', 'zml12304@demo.com', '19900000004', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (17, 'lhr123', '123', '刘浩然', '/pictures/yonghu.png', 'lhr12305@demo.com', '19900000005', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (18, 'cjy123', '123', '陈静怡', '/pictures/yonghu.png', 'cjy12306@demo.com', '19900000006', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (19, 'ytt123', '123', '杨婷婷', '/pictures/yonghu.png', 'ytt12307@demo.com', '19900000007', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (20, 'hxq123', '123', '黄雪琴', '/pictures/yonghu.png', 'hxq12308@demo.com', '19900000008', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (21, 'wgq123', '123', '吴国强', '/pictures/yonghu.png', 'wgq12309@demo.com', '19900000009', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (22, 'zhl123', '123', '周海龙', '/pictures/yonghu.png', 'zhl12310@demo.com', '19900000010', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (23, 'zwn123', '123', '张文娜', '/pictures/yonghu.png', 'zwn12311@demo.com', '19900000011', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (24, 'lqx123', '123', '李强翔', '/pictures/yonghu.png', 'lqx12312@demo.com', '19900000012', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (25, 'wjl123', '123', '王洁丽', '/pictures/yonghu.png', 'wjl12313@demo.com', '19900000013', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (26, 'zxy123', '123', '赵欣怡', '/pictures/yonghu.png', 'zxy12314@demo.com', '19900000014', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (27, 'lmn123', '123', '刘美楠', '/pictures/yonghu.png', 'lmn12315@demo.com', '19900000015', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (28, 'cht123', '123', '陈浩天', '/pictures/yonghu.png', 'cht12316@demo.com', '19900000016', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (29, 'yhw123', '123', '杨浩文', '/pictures/yonghu.png', 'yhw12317@demo.com', '19900000017', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (30, 'hjj123', '123', '黄俊杰', '/pictures/yonghu.png', 'hjj12318@demo.com', '19900000018', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (31, 'wyt123', '123', '吴雨婷', '/pictures/yonghu.png', 'wyt12319@demo.com', '19900000019', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (32, 'zqy123', '123', '周晴怡', '/pictures/yonghu.png', 'zqy12320@demo.com', '19900000020', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (33, 'zjh123', '123', '张俊浩', '/pictures/yonghu.png', 'zjh12321@demo.com', '19900000021', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (34, 'lfq123', '123', '李芳倩', '/pictures/yonghu.png', 'lfq12322@demo.com', '19900000022', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (35, 'whl123', '123', '王浩磊', '/pictures/yonghu.png', 'whl12323@demo.com', '19900000023', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (36, 'zxt123', '123', '赵晓彤', '/pictures/yonghu.png', 'zxt12324@demo.com', '19900000024', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (37, 'ljm123', '123', '刘佳敏', '/pictures/yonghu.png', 'ljm12325@demo.com', '19900000025', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (38, 'cyj123', '123', '陈雅静', '/pictures/yonghu.png', 'cyj12326@demo.com', '19900000026', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (39, 'yxl123', '123', '杨雪丽', '/pictures/yonghu.png', 'yxl12327@demo.com', '19900000027', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (40, 'hhx123', '123', '黄浩轩', '/pictures/yonghu.png', 'hhx12328@demo.com', '19900000028', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (41, 'wqn123', '123', '吴倩楠', '/pictures/yonghu.png', 'wqn12329@demo.com', '19900000029', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (42, 'zzh123', '123', '周志豪', '/pictures/yonghu.png', 'zzh12330@demo.com', '19900000030', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (43, 'zwl123', '123', '张文丽', '/pictures/yonghu.png', 'zwl12331@demo.com', '19900000031', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (44, 'lnn123', '123', '李宁楠', '/pictures/yonghu.png', 'lnn12332@demo.com', '19900000032', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (45, 'wxy123', '123', '王欣妍', '/pictures/yonghu.png', 'wxy12333@demo.com', '19900000033', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (46, 'zht123', '123', '赵海涛', '/pictures/yonghu.png', 'zht12334@demo.com', '19900000034', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (47, 'lxq123', '123', '刘晓晴', '/pictures/yonghu.png', 'lxq12335@demo.com', '19900000035', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (48, 'cql123', '123', '陈秋琳', '/pictures/yonghu.png', 'cql12336@demo.com', '19900000036', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (49, 'yjl123', '123', '杨静琳', '/pictures/yonghu.png', 'yjl12337@demo.com', '19900000037', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (50, 'hyy123', '123', '黄云阳', '/pictures/yonghu.png', 'hyy12338@demo.com', '19900000038', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (51, 'wjj123', '123', '吴佳杰', '/pictures/yonghu.png', 'wjj12339@demo.com', '19900000039', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (52, 'zwnx123', '123', '周文娜', '/pictures/yonghu.png', 'zwnx12340@demo.com', '19900000040', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (53, 'zqh123', '123', '张清慧', '/pictures/yonghu.png', 'zqh12341@demo.com', '19900000041', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (54, 'lwy123', '123', '李文雅', '/pictures/yonghu.png', 'lwy12342@demo.com', '19900000042', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (55, 'wxn123', '123', '王晓楠', '/pictures/yonghu.png', 'wxn12343@demo.com', '19900000043', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (56, 'zjl123', '123', '赵佳丽', '/pictures/yonghu.png', 'zjl12344@demo.com', '19900000044', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (57, 'lhh123', '123', '刘浩辉', '/pictures/yonghu.png', 'lhh12345@demo.com', '19900000045', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (58, 'cyn123', '123', '陈雨楠', '/pictures/yonghu.png', 'cyn12346@demo.com', '19900000046', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (59, 'yht123', '123', '杨海涛', '/pictures/yonghu.png', 'yht12347@demo.com', '19900000047', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (60, 'hql123', '123', '黄秋玲', '/pictures/yonghu.png', 'hql12348@demo.com', '19900000048', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (61, 'wxl123', '123', '吴秀兰', '/pictures/yonghu.png', 'wxl12349@demo.com', '19900000049', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (62, 'zln123', '123', '周丽娜', '/pictures/yonghu.png', 'zln12350@demo.com', '19900000050', 'USER', '2026-03-03 00:00:00.000000');
INSERT INTO `sys_user` VALUES (63, 'zt123', '123', '周涛', '/pictures/yonghu.png', 'zt123451@qq.com', '19900001111', 'USER', '2026-03-01 00:00:00.000000');

SET FOREIGN_KEY_CHECKS = 1;
