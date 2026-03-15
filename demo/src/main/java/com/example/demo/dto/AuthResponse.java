package com.example.demo.dto;

import com.example.demo.entity.enums.UserRole;

/**
 * 登录/注册成功后的响应 DTO
 */
public class AuthResponse {

    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private UserRole role;

    public static AuthResponse of(String token, Long userId, String username, String nickname, String avatar, UserRole role) {
        AuthResponse r = new AuthResponse();
        r.setToken(token);
        r.setUserId(userId);
        r.setUsername(username);
        r.setNickname(nickname);
        r.setAvatar(avatar);
        r.setRole(role);
        return r;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
