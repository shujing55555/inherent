package com.example.demo.entity.enums;

/**
 * 用户角色枚举
 * 用于区分普通用户与管理员，实现权限隔离
 */
public enum UserRole {
    /** 普通用户：仅可访问前台展示与互动 */
    USER,
    /** 管理员：可访问后台全部管理功能 */
    ADMIN
}
