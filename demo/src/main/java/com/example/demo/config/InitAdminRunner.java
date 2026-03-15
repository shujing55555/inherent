package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动时执行：仅在不存在管理员账号时创建默认管理员；
 * 若已存在管理员账号，则不再修改其密码，避免覆盖手动设置的密码。
 * 首次创建时默认账号为 admin / admin123
 */
@Component
public class InitAdminRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InitAdminRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        userRepository.findByUsernameAndRole("admin", UserRole.ADMIN).ifPresentOrElse(
            admin -> {
                // 已存在管理员：不修改密码，防止覆盖手动设置的密码
                System.out.println("检测到管理员账号 admin 已存在，未修改其密码。");
            },
            () -> {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNickname("系统管理员");
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
                System.out.println("已创建默认管理员账号: admin / admin123");
            }
        );
    }
}
