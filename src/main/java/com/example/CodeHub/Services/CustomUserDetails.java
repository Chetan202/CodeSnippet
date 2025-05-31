package com.example.CodeHub.Services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private String email;
    private boolean verified;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
                             String email, boolean verified) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.email = email;
        this.verified = verified;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Only allow login if the account is verified
        return verified;
    }
}