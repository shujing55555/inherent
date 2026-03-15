package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户注册请求 DTO
 */
public class RegisterRequest {

    @NotBlank(message = "账号不能为空")
    @Size(min = 4, max = 10, message = "账号长度需为4-10位")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9]*$", message = "账号只能包含字母和数字，且以字母开头")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 3, max = 12, message = "密码长度需为3-12位")
    private String password;

    /** 邮箱 */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 手机号，11 位数字 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "手机号必须为11位数字")
    private String phone;

    /** 验证码（具体校验逻辑根据实际实现） */
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;

    @Size(max = 50)
    private String nickname;

    private String avatar;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getVerifyCode() { return verifyCode; }
    public void setVerifyCode(String verifyCode) { this.verifyCode = verifyCode; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
