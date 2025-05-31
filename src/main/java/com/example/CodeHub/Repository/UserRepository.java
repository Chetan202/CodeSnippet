package com.example.CodeHub.Repository;


import com.example.CodeHub.Dto.UserDto;
import com.example.CodeHub.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email); // NEW
    User findByVerificationToken(String verificationToken); // NEW
    User save(UserDto userDto);

}