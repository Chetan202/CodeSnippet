package com.example.CodeHub.Controller;

import com.example.CodeHub.Dto.SnippetDto;
import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import com.example.CodeHub.Services.SnippetService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Controller
public class SnippetController {

    private final SnippetService snippetService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public SnippetController(SnippetService snippetService, UserRepository userRepository, ObjectMapper objectMapper) {
        this.snippetService = snippetService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/snippets/create")
    public String showCreateForm(Model model) {
        model.addAttribute("snippet", new SnippetDto());
        return "create-snippet";
    }

    @PostMapping("/snippets/create")
    public String createSnippet(@ModelAttribute("snippet") SnippetDto snippetDto,
                                Model model, RedirectAttributes redirectAttributes) {
        User user = currentUser();
        if (user == null) {
            return "redirect:/login";
        }
        try {
            snippetService.save(snippetDto, user);
            return "redirect:/home?snippetCreated=true";
        } catch (Exception e) {
            model.addAttribute("snippet", snippetDto);
            model.addAttribute("error", "Failed to save snippet. Please try again.");
            return "create-snippet";
        }
    }
    
    @GetMapping("/snippets/{id}")
    public String viewSnippet(@PathVariable Long id, Model model) {
        // Check if user is authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        // Get the current user
        User currentUser = userRepository.findByEmail(auth.getName());
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Get the snippet
        Snippet snippet = snippetService.findById(id);
        if (snippet == null) {
            return "redirect:/home";
        }
        
        // Check if the current user is the owner of the snippet
        if (!snippet.getUser().getId().equals(currentUser.getId())) {
            // User is not the owner, redirect to home
            return "redirect:/home?error=unauthorized";
        }
        
        model.addAttribute("snippet", snippet);
        model.addAttribute("bookmarked", snippetService.isBookmarked(snippet, currentUser));
        return "view-snippet";
    }

    @GetMapping("/snippets/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        Snippet snippet = snippetService.findById(id);
        if (snippet == null) {
            return "redirect:/home";
        }
        
        // Check if user is the owner of the snippet
        User currentUser = userRepository.findByEmail(auth.getName());
        if (!snippet.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/home";  // User is not the owner
        }
        
        SnippetDto snippetDto = new SnippetDto();
        snippetDto.setTitle(snippet.getTitle());
        snippetDto.setLanguage(snippet.getLanguage());
        snippetDto.setCode(snippet.getCode());
        snippetDto.setPublicSnippet(snippet.isPublicSnippet());
        
        model.addAttribute("snippet", snippetDto);
        model.addAttribute("snippetId", id);
        return "edit-snippet";
    }
    
    @PostMapping("/snippets/{id}/update")
    public String updateSnippet(@PathVariable Long id, @ModelAttribute("snippet") SnippetDto snippetDto,
                               RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        User currentUser = userRepository.findByEmail(auth.getName());
        Snippet snippet = snippetService.findById(id);
        
        if (snippet == null || !snippet.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/home";  // Snippet not found or user not owner
        }
        
        snippetService.update(id, snippetDto);
        redirectAttributes.addFlashAttribute("snippetUpdated", true);
        return "redirect:/home";
    }
    
    @PostMapping("/snippets/{id}/delete")
    public String deleteSnippet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        // Get the current user
        User currentUser = userRepository.findByEmail(auth.getName());
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Get the snippet
        Snippet snippet = snippetService.findById(id);
        if (snippet == null) {
            return "redirect:/home?error=notfound";  // Snippet not found
        }
        
        // Check if the current user is the owner of the snippet
        if (!snippet.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/home?error=unauthorized";  // User not owner
        }
        
        snippetService.delete(id);
        redirectAttributes.addFlashAttribute("snippetDeleted", true);
        return "redirect:/home";
    }
    
    @PostMapping("/snippets/{id}/star")
    public String toggleStarSnippet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        // Get the current user
        User currentUser = userRepository.findByEmail(auth.getName());
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Get the snippet
        Snippet snippet = snippetService.findById(id);
        if (snippet == null) {
            return "redirect:/home";  // Snippet not found
        }
        
        // Check if the current user is the owner of the snippet
        if (!snippet.getUser().getId().equals(currentUser.getId())) {
            // User is not the owner, redirect to home
            return "redirect:/home?error=unauthorized";
        }
        
        // Toggle the starred status
        boolean newStarredStatus = !snippet.isStarred();
        snippetService.toggleStar(id, newStarredStatus);
        
        redirectAttributes.addFlashAttribute("snippetStarred", newStarredStatus);
        return "redirect:/home";
    }

    @PostMapping("/snippets/{id}/bookmark")
    public String toggleBookmark(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = currentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        boolean bookmarked = snippetService.toggleBookmark(id, currentUser);
        redirectAttributes.addFlashAttribute("bookmarkUpdated", bookmarked);
        return "redirect:/snippets/" + id;
    }

    @GetMapping("/bookmarks")
    public String bookmarks(Model model) {
        User currentUser = currentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("bookmarks", snippetService.findBookmarks(currentUser));
        model.addAttribute("user", currentUser);
        return "bookmarks";
    }

    @GetMapping("/users/{username}")
    public String profile(@PathVariable String username,
                          @RequestParam(name = "page", defaultValue = "0") int page,
                          Model model) {
        User profileUser = userRepository.findByUsername(username);
        if (profileUser == null) {
            return "redirect:/home?error=notfound";
        }
        Page<Snippet> snippets = snippetService.findPublicSnippetsByUser(profileUser, PageRequest.of(page, 10));
        model.addAttribute("profileUser", profileUser);
        model.addAttribute("snippets", snippets.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", snippets.getTotalPages());
        return "profile";
    }

    @GetMapping("/snippets/{id}/versions")
    public String showVersions(@PathVariable Long id, Model model) {
        User currentUser = currentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        Snippet snippet = snippetService.findById(id);
        if (snippet == null || !snippet.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/home?error=unauthorized";
        }
        model.addAttribute("snippet", snippet);
        model.addAttribute("versions", snippetService.findVersions(snippet));
        return "snippet-versions";
    }

    @PostMapping("/snippets/{snippetId}/versions/{versionId}/revert")
    public String revertVersion(@PathVariable Long snippetId, @PathVariable Long versionId,
                                RedirectAttributes redirectAttributes) {
        User currentUser = currentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        Snippet snippet = snippetService.findById(snippetId);
        if (snippet == null || !snippet.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/home?error=unauthorized";
        }
        snippetService.revertToVersion(snippetId, versionId);
        redirectAttributes.addFlashAttribute("snippetUpdated", true);
        return "redirect:/snippets/" + snippetId;
    }

    @GetMapping("/share/{token}")
    public String viewSharedSnippet(@PathVariable String token, Model model) {
        Snippet snippet = snippetService.findPublicByShareToken(token);
        if (snippet == null) {
            return "redirect:/login?notFound";
        }
        model.addAttribute("snippet", snippet);
        return "public-snippet";
    }
    
    /**
     * REST endpoint for search suggestions
     * Returns snippet previews that match the search term
     * Only returns the user's own snippets if authenticated
     */
    @GetMapping("/api/search-suggestions")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getSearchSuggestions(@RequestParam("term") String searchTerm) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Snippet> suggestions;
        
        // Limit to 5 results for performance and better UX
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 5);
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            // User is authenticated, only show their snippets
            User currentUser = userRepository.findByEmail(auth.getName());
            if (currentUser != null) {
                // Get suggestions for the current user only
                suggestions = snippetService.searchUserSnippets(currentUser, searchTerm, pageable).getContent();
            } else {
                // User not found in database, return empty list
                return ResponseEntity.ok(List.of());
            }
        } else {
            // Not authenticated, return empty list
            return ResponseEntity.ok(List.of());
        }
        
        // Create preview data for each snippet
        List<Map<String, String>> previewData = suggestions.stream()
                .map(snippet -> {
                    Map<String, String> preview = new HashMap<>();
                    preview.put("id", snippet.getId().toString());
                    preview.put("title", snippet.getTitle());
                    preview.put("language", snippet.getLanguage());
                    
                    // Get a short preview of the code (first 100 chars)
                    String codePreview = snippet.getCode();
                    if (codePreview.length() > 100) {
                        codePreview = codePreview.substring(0, 100) + "...";
                    }
                    preview.put("codePreview", codePreview);
                    
                    return preview;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(previewData);
    }

    @GetMapping("/snippets/import-export")
    public String importExportPage(Model model) {
        User user = currentUser();
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "import-export";
    }

    @GetMapping("/snippets/export")
    public ResponseEntity<byte[]> exportSnippets() {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        try {
            List<Map<String, Object>> data = snippetService.findSnippetsByUser(user).stream().map(s -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("title", s.getTitle());
                m.put("language", s.getLanguage());
                m.put("code", s.getCode());
                m.put("publicSnippet", s.isPublicSnippet());
                return m;
            }).collect(Collectors.toList());
            byte[] bytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(data);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"codehub-snippets.json\"")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/snippets/import/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        String template = "[\n" +
                "  {\n" +
                "    \"title\": \"My First Snippet\",\n" +
                "    \"language\": \"JavaScript\",\n" +
                "    \"code\": \"console.log('Hello, World!');\",\n" +
                "    \"publicSnippet\": false\n" +
                "  },\n" +
                "  {\n" +
                "    \"title\": \"Python Hello World\",\n" +
                "    \"language\": \"Python\",\n" +
                "    \"code\": \"print('Hello, World!')\",\n" +
                "    \"publicSnippet\": false\n" +
                "  }\n" +
                "]\n";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"snippets-template.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(template.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/snippets/import")
    public String importSnippets(@RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {
        User user = currentUser();
        if (user == null) return "redirect:/login";
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("importError", "Please select a file to upload.");
            return "redirect:/snippets/import-export";
        }
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            List<SnippetDto> dtos = objectMapper.readValue(content,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, SnippetDto.class));
            int count = 0;
            for (SnippetDto dto : dtos) {
                if (dto.getTitle() != null && !dto.getTitle().isBlank()
                        && dto.getCode() != null && !dto.getCode().isBlank()) {
                    if (dto.getLanguage() == null || dto.getLanguage().isBlank()) {
                        dto.setLanguage("Plaintext");
                    }
                    snippetService.save(dto, user);
                    count++;
                }
            }
            redirectAttributes.addFlashAttribute("importSuccess", count + " snippet(s) imported successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("importError",
                    "Invalid file format. Please use the JSON template provided.");
        }
        return "redirect:/snippets/import-export";
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        User user = userRepository.findByEmail(auth.getName());
        if (user == null) {
            user = userRepository.findByUsername(auth.getName());
        }
        return user;
    }
}
