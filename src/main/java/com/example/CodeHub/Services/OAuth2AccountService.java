package com.example.CodeHub.Services;

import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class OAuth2AccountService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;

    public OAuth2AccountService(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                @Value("${app.admin-email:chetanjha888@gmail.com}") String adminEmail) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = resolveEmail(oauthUser, provider);
        String username = resolveUsername(oauthUser, provider, email);
        User user = findOrCreateUser(email, username);

        Map<String, Object> attributes = new HashMap<>(oauthUser.getAttributes());
        attributes.put("username", user.getUsername());
        attributes.put("email", user.getEmail());

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
                attributes,
                "username"
        );
    }

    private User findOrCreateUser(String email, String username) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = userRepository.findByUsername(username);
        }
        if (user == null) {
            user = new User(username, passwordEncoder.encode(UUID.randomUUID().toString()), email);
            user.setRole(adminEmail.equalsIgnoreCase(email) ? "ADMIN" : "USER");
        }
        return userRepository.save(user);
    }

    private String resolveEmail(OAuth2User oauthUser, String provider) {
        Object email = oauthUser.getAttribute("email");
        if (email != null && !email.toString().isBlank()) {
            return email.toString().trim().toLowerCase(Locale.ROOT);
        }
        Object login = oauthUser.getAttribute("login");
        Object id = oauthUser.getAttribute("id");
        String fallback = login != null ? login.toString() : String.valueOf(id);
        return fallback.toLowerCase(Locale.ROOT) + "@" + provider + ".oauth";
    }

    private String resolveUsername(OAuth2User oauthUser, String provider, String email) {
        Object login = oauthUser.getAttribute("login");
        Object name = oauthUser.getAttribute("name");
        String base = login != null ? login.toString() : name != null ? name.toString() : email.split("@")[0];
        return uniqueUsername(base.replaceAll("[^A-Za-z0-9_]", "").toLowerCase(Locale.ROOT), provider);
    }

    private String uniqueUsername(String base, String provider) {
        String cleanBase = base == null || base.isBlank() ? provider + "user" : base;
        String candidate = cleanBase;
        int suffix = 1;
        while (userRepository.findByUsername(candidate) != null) {
            candidate = cleanBase + suffix;
            suffix++;
        }
        return candidate;
    }
}
