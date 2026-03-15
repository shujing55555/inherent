package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 用户表数据访问层
 * 支持按用户名、角色查询，用于登录与后台用户管理
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** 按登录账号查询（登录时用） */
    Optional<User> findByUsername(String username);

    /** 按账号与角色查询（管理员登录时校验必须是 ADMIN） */
    Optional<User> findByUsernameAndRole(String username, UserRole role);

    /** 后台：按角色查用户列表 */
    List<User> findByRoleOrderByCreateTimeDesc(UserRole role);

    /** 检查账号是否已存在（注册时防重复） */
    boolean existsByUsername(String username);

    /** 检查邮箱是否已存在（注册时防重复） */
    boolean existsByEmail(String email);

    /** 检查手机号是否已存在（注册时防重复） */
    boolean existsByPhone(String phone);
}
