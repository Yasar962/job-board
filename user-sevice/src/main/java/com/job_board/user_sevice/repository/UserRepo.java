package com.job_board.user_sevice.repository;

import com.job_board.user_sevice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
