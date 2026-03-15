package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户登录请求 DTO
 */
public class LoginRequest {

    @NotBlank(message = "账号不能为空")
    @Size(min = 4, max = 10, message = "账号长度需为4-10位")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9]*$", message = "账号只能包含字母和数字，且以字母开头")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 3, max = 12, message = "密码长度需为3-12位")
    private String password;

    /** 是否为管理员登录（走后台入口时传 true） */
    private boolean admin = false;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
}
