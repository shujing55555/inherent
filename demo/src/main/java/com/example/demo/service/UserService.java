package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务：当前用户信息、后台用户管理（增删改）
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** 将实体转为 DTO（不暴露密码） */
    public static UserDTO toDTO(User u) {
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setNickname(u.getNickname());
        dto.setAvatar(u.getAvatar());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setRole(u.getRole());
        dto.setCreateTime(u.getCreateTime());
        return dto;
    }

    /** 根据 ID 查用户（不存在抛异常） */
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    /** 当前登录用户信息（供前台“个人中心”等使用） */
    public UserDTO getCurrentUser(Long userId) {
        User user = getById(userId);
        return toDTO(user);
    }

    /** 后台：用户列表（可选关键词：账号、昵称；可选角色筛选） */
    public List<UserDTO> listAll(String keyword, UserRole role) {
        return userRepository.findAll().stream()
                .filter(u -> {
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        String k = keyword.trim().toLowerCase();
                        if (!((u.getUsername() != null && u.getUsername().toLowerCase().contains(k))
                                || (u.getNickname() != null && u.getNickname().toLowerCase().contains(k))))
                            return false;
                    }
                    if (role != null && u.getRole() != role) return false;
                    return true;
                })
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .map(UserService::toDTO)
                .collect(Collectors.toList());
    }

    /** 后台：新增用户（可指定为普通用户或管理员） */
    @Transactional
    public UserDTO create(String username, String password, String nickname, String avatar,
                          String email, String phone, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("账号已存在");
        }
        if (email != null && !email.isBlank() && userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }
        if (phone != null && !phone.isBlank() && userRepository.existsByPhone(phone)) {
            throw new RuntimeException("手机号已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname != null && !nickname.isBlank() ? nickname : username);
        user.setAvatar(avatar);
        user.setEmail(email != null && !email.isBlank() ? email : null);
        user.setPhone(phone != null && !phone.isBlank() ? phone : null);
        user.setRole(role != null ? role : UserRole.USER);
        user = userRepository.save(user);
        return toDTO(user);
    }

    /** 后台：更新用户信息（不含密码则不改） */
    @Transactional
    public UserDTO update(Long id, String nickname, String avatar, String newPassword,
                          String email, String phone, UserRole role) {
        User user = getById(id);
        if (nickname != null) user.setNickname(nickname);
        if (avatar != null) user.setAvatar(avatar);
        if (email != null) {
            String e = email.trim();
            if (!e.isEmpty() && !e.equals(user.getEmail()) && userRepository.existsByEmail(e)) {
                throw new RuntimeException("邮箱已存在");
            }
            user.setEmail(e.isEmpty() ? null : e);
        }
        if (phone != null) {
            String p = phone.trim();
            if (!p.isEmpty() && !p.equals(user.getPhone()) && userRepository.existsByPhone(p)) {
                throw new RuntimeException("手机号已存在");
            }
            user.setPhone(p.isEmpty() ? null : p);
        }
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        if (role != null) user.setRole(role);
        user = userRepository.save(user);
        return toDTO(user);
    }

    /** 后台：删除用户 */
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id);
    }
}
