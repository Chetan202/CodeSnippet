package com.example.CodeHub.Configuration;


import com.example.CodeHub.Services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.GetMapping;


@Configuration
@EnableWebSecurity
public class securityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    // Public access paths - only for registration, login, and verification
                    registry.requestMatchers("/register", "/login", "/verify").permitAll();
                    
                    // Static resources should be accessible without login
                    registry.requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll();
                    
                    // API endpoints for search suggestions can be accessed by anyone
                    registry.requestMatchers("/api/search-suggestions").permitAll();
                    
                    // Restrict home page and snippets to authenticated users only
                    registry.requestMatchers("/home", "/snippets/**", "/api/search-snippets").authenticated();
                    
                    // Admin paths require ADMIN role
                    registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    
                    // All other requests require authentication
                    registry.anyRequest().authenticated();
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                            .loginPage("/login")
                            .loginProcessingUrl("/login")
                            .defaultSuccessUrl("/home", true)
                            .permitAll();
                })
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .deleteCookies("JSESSIONID")
                        .permitAll()); // Ensure logout is permitted
        return http.build();
    }

}