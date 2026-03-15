package com.example.demo.repository;

import com.example.demo.entity.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {

    List<UserBehavior> findTop100ByUserIdAndTypeOrderByCreateTimeDesc(Long userId, String type);

    List<UserBehavior> findByTypeAndCreateTimeAfterOrderByCreateTimeAsc(String type, LocalDateTime after);

    List<UserBehavior> findByType(String type);
}

