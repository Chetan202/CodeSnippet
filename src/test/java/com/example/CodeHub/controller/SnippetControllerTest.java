package com.example.CodeHub.controller;

import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.SnippetRepository;
import com.example.CodeHub.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SnippetControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SnippetRepository snippetRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Snippet testSnippet;

    @BeforeEach
    void setUp() {
        testUser = new User("snippetowner", passwordEncoder.encode("pass"), "owner@example.com");
        testUser.setRole("USER");
        userRepository.save(testUser);

        testSnippet = new Snippet("Hello World", "java", "System.out.println(\"hi\");", testUser);
        snippetRepository.save(testSnippet);
    }

    @Test
    void unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "snippetowner")
    void home_returns200_whenAuthenticated() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    @WithMockUser(username = "snippetowner")
    void createSnippetForm_returns200() throws Exception {
        mockMvc.perform(get("/snippets/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-snippet"));
    }

    @Test
    @WithMockUser(username = "snippetowner")
    void createSnippet_redirectsToHome_onSuccess() throws Exception {
        mockMvc.perform(post("/snippets/create")
                        .with(csrf())
                        .param("title", "My Snippet")
                        .param("language", "python")
                        .param("code", "print('hello')")
                        .param("publicSnippet", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home?snippetCreated=true"));
    }

    @Test
    @WithMockUser(username = "snippetowner")
    void viewSnippet_returns200_forOwner() throws Exception {
        mockMvc.perform(get("/snippets/" + testSnippet.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("view-snippet"));
    }

    @Test
    @WithMockUser(username = "otheruser")
    void viewSnippet_redirectsUnauthorized_forNonOwner() throws Exception {
        User other = new User("otheruser", passwordEncoder.encode("pass"), "other@example.com");
        other.setRole("USER");
        userRepository.save(other);

        mockMvc.perform(get("/snippets/" + testSnippet.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home?error=unauthorized"));
    }

    @Test
    @WithMockUser(username = "snippetowner")
    void deleteSnippet_softDeletesAndRedirects() throws Exception {
        mockMvc.perform(post("/snippets/" + testSnippet.getId() + "/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        Snippet deleted = snippetRepository.findById(testSnippet.getId()).orElseThrow();
        assertThat(deleted.isDeleted()).isTrue();
    }

    @Test
    @WithMockUser(username = "snippetowner")
    void updateSnippet_updatesFieldsAndRedirects() throws Exception {
        mockMvc.perform(post("/snippets/" + testSnippet.getId() + "/update")
                        .with(csrf())
                        .param("title", "Updated Title")
                        .param("language", "kotlin")
                        .param("code", "fun main() {}")
                        .param("publicSnippet", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        Snippet updated = snippetRepository.findById(testSnippet.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getLanguage()).isEqualTo("kotlin");
        assertThat(updated.isPublicSnippet()).isTrue();
    }

    @Test
    @WithMockUser(username = "snippetowner")
    void shareLink_isPublicAndReturnsView() throws Exception {
        testSnippet.setPublicSnippet(true);
        snippetRepository.save(testSnippet);

        mockMvc.perform(get("/share/" + testSnippet.getShareToken()))
                .andExpect(status().isOk())
                .andExpect(view().name("public-snippet"));
    }

    @Test
    void shareLink_redirects_whenSnippetIsPrivate() throws Exception {
        mockMvc.perform(get("/share/" + testSnippet.getShareToken()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "snippetowner")
    void searchSuggestions_returnsJson() throws Exception {
        mockMvc.perform(get("/api/search-suggestions").param("term", "Hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }
}
