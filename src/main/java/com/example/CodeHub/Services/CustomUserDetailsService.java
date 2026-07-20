package com.example.CodeHub.Services;

import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Email or password not found");
        }
        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                authorities(user.getRole()),
                user.getEmail(),
                !user.isAccountLocked(),
                user.isVerified()
        );
    }

    public Collection<? extends GrantedAuthority> authorities(String role) {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
