package com.chung.oauth.demo.repository;

import com.chung.oauth.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndProvider(String email, String provider);
}
