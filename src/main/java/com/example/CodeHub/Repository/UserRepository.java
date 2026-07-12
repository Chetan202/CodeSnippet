package com.example.CodeHub.Repository;

import com.example.CodeHub.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email); // NEW
    List<User> findAllByOrderByCreatedAtDesc();

}
