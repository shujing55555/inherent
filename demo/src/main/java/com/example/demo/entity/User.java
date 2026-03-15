package com.example.demo.entity;

import com.example.demo.entity.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 用户实体
 * 存储普通用户注册信息：账号、密码、昵称、头像、注册时间等
 * 管理员也使用本表，通过 role 字段区分
 */
@Entity
@Table(name = "sys_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录账号，唯一：字母开头，仅字母和数字，4-10 位 */
    @NotBlank
    @Size(min = 4, max = 10, message = "账号长度需为4-10位")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9]*$", message = "账号只能包含字母和数字，且以字母开头")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /** 加密后的密码（原始密码 3-12 位） */
    @NotBlank
    @Column(nullable = false, length = 200)
    private String password;

    /** 昵称，用于前台展示 */
    @Size(max = 50)
    @Column(length = 50)
    private String nickname;

    /** 头像 URL，可为空 */
    @Column(length = 500)
    private String avatar;

    /** 邮箱，登录联系用，唯一 */
    @Email
    @Size(max = 100)
    @Column(length = 100, unique = true)
    private String email;

    /** 手机号，11 位数字，唯一 */
    @Pattern(regexp = "^\\d{11}$", message = "手机号必须为11位数字")
    @Column(length = 20, unique = true)
    private String phone;

    /** 角色：USER 普通用户 / ADMIN 管理员 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    /** 注册时间，系统自动记录 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }

    // ---------- Getter / Setter ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
