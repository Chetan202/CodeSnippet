package com.example.CodeHub.Configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class securityConfig {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String adminEmail;

    public securityConfig(AuthenticationSuccessHandler authenticationSuccessHandler,
                          AuthenticationFailureHandler authenticationFailureHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          @Value("${app.admin-email:chetanjha888@gmail.com}") String adminEmail) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.adminEmail = adminEmail;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(registry -> {
                    // Public access paths - only for registration, login, and verification
                    registry.requestMatchers("/register", "/login", "/verify", "/check-email", "/resend-verification", "/share/**", "/tags", "/users/**").permitAll();
                    registry.requestMatchers("/api/auth/login").permitAll();
                    registry.requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll();
                    
                    // Static resources should be accessible without login
                    registry.requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll();
                    
                    // API endpoints support JWT bearer authentication.
                    registry.requestMatchers("/api/search-suggestions").authenticated();
                    
                    // Restrict home page and snippets to authenticated users only
                    registry.requestMatchers("/home", "/snippets/**", "/api/search-snippets").authenticated();
                    
                    // Admin access is tied to the owner's authenticated email, not only a mutable role.
                    registry.requestMatchers("/admin/**").access(new WebExpressionAuthorizationManager(
                            "hasRole('ADMIN') and authentication.principal.email.equalsIgnoreCase('" + adminEmail + "')"));
                    
                    // All other requests require authentication
                    registry.anyRequest().authenticated();
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                            .loginPage("/login")
                            .loginProcessingUrl("/login")
                            .successHandler(authenticationSuccessHandler)
                            .failureHandler(authenticationFailureHandler)
                            .permitAll();
                })
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .deleteCookies("JSESSIONID")
                        .permitAll()) // Ensure logout is permitted
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
