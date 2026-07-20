package com.example.CodeHub.service;

import com.example.CodeHub.Dto.SnippetDto;
import com.example.CodeHub.Entity.Bookmark;
import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.SnippetVersion;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.BookmarkRepository;
import com.example.CodeHub.Repository.SnippetRepository;
import com.example.CodeHub.Repository.SnippetVersionRepository;
import com.example.CodeHub.Services.SnippetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SnippetServiceTest {

    @Mock
    private SnippetRepository snippetRepository;
    @Mock
    private SnippetVersionRepository snippetVersionRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;

    @InjectMocks
    private SnippetServiceImpl snippetService;

    private User user;
    private Snippet snippet;
    private SnippetDto snippetDto;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "encodedpass", "test@example.com");
        user.setRole("USER");

        snippet = new Snippet("Test Title", "java", "System.out.println(\"hello\");", user);
        snippet.setPublicSnippet(false);

        snippetDto = new SnippetDto();
        snippetDto.setTitle("Test Title");
        snippetDto.setLanguage("java");
        snippetDto.setCode("System.out.println(\"hello\");");
        snippetDto.setPublicSnippet(false);
    }

    @Test
    void save_persistsSnippetWithCorrectFields() {
        when(snippetRepository.save(any(Snippet.class))).thenAnswer(inv -> inv.getArgument(0));

        Snippet saved = snippetService.save(snippetDto, user);

        assertThat(saved.getTitle()).isEqualTo("Test Title");
        assertThat(saved.getLanguage()).isEqualTo("java");
        assertThat(saved.getCode()).isEqualTo("System.out.println(\"hello\");");
        assertThat(saved.getUser()).isEqualTo(user);
        verify(snippetRepository).save(any(Snippet.class));
    }

    @Test
    void findById_returnsSnippet_whenNotDeleted() {
        when(snippetRepository.findById(1L)).thenReturn(Optional.of(snippet));

        Snippet result = snippetService.findById(1L);

        assertThat(result).isEqualTo(snippet);
    }

    @Test
    void findById_returnsNull_whenDeleted() {
        snippet.setDeleted(true);
        when(snippetRepository.findById(1L)).thenReturn(Optional.of(snippet));

        Snippet result = snippetService.findById(1L);

        assertThat(result).isNull();
    }

    @Test
    void findById_returnsNull_whenNotFound() {
        when(snippetRepository.findById(99L)).thenReturn(Optional.empty());

        Snippet result = snippetService.findById(99L);

        assertThat(result).isNull();
    }

    @Test
    void update_savesVersionBeforeUpdating() {
        when(snippetRepository.findById(1L)).thenReturn(Optional.of(snippet));
        when(snippetRepository.save(any(Snippet.class))).thenAnswer(inv -> inv.getArgument(0));

        SnippetDto update = new SnippetDto();
        update.setTitle("Updated Title");
        update.setLanguage("python");
        update.setCode("print('hello')");
        update.setPublicSnippet(true);

        Snippet result = snippetService.update(1L, update);

        verify(snippetVersionRepository).save(any(SnippetVersion.class));
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getLanguage()).isEqualTo("python");
        assertThat(result.isPublicSnippet()).isTrue();
    }

    @Test
    void update_returnsNull_whenSnippetNotFound() {
        when(snippetRepository.findById(99L)).thenReturn(Optional.empty());

        Snippet result = snippetService.update(99L, snippetDto);

        assertThat(result).isNull();
        verifyNoInteractions(snippetVersionRepository);
    }

    @Test
    void delete_softDeletesSnippet() {
        when(snippetRepository.findById(1L)).thenReturn(Optional.of(snippet));
        when(snippetRepository.save(any(Snippet.class))).thenAnswer(inv -> inv.getArgument(0));

        snippetService.delete(1L);

        assertThat(snippet.isDeleted()).isTrue();
        assertThat(snippet.getDeletedAt()).isNotNull();
        verify(snippetRepository).save(snippet);
    }

    @Test
    void toggleStar_updatesStarredStatus() {
        when(snippetRepository.findById(1L)).thenReturn(Optional.of(snippet));
        when(snippetRepository.save(any(Snippet.class))).thenAnswer(inv -> inv.getArgument(0));

        snippetService.toggleStar(1L, true);

        assertThat(snippet.isStarred()).isTrue();
        verify(snippetRepository).save(snippet);
    }

    @Test
    void toggleBookmark_createsBookmark_whenNotExists() {
        when(snippetRepository.findById(1L)).thenReturn(Optional.of(snippet));
        when(bookmarkRepository.findByUserAndSnippet(user, snippet)).thenReturn(null);

        boolean result = snippetService.toggleBookmark(1L, user);

        assertThat(result).isTrue();
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    void toggleBookmark_removesBookmark_whenExists() {
        Bookmark existing = new Bookmark(user, snippet);
        when(snippetRepository.findById(1L)).thenReturn(Optional.of(snippet));
        when(bookmarkRepository.findByUserAndSnippet(user, snippet)).thenReturn(existing);

        boolean result = snippetService.toggleBookmark(1L, user);

        assertThat(result).isFalse();
        verify(bookmarkRepository).delete(existing);
    }

    @Test
    void toggleBookmark_returnsFalse_whenSnippetNotFound() {
        when(snippetRepository.findById(99L)).thenReturn(Optional.empty());

        boolean result = snippetService.toggleBookmark(99L, user);

        assertThat(result).isFalse();
        verifyNoInteractions(bookmarkRepository);
    }

    @Test
    void isBookmarked_returnsTrue_whenBookmarkExists() {
        when(bookmarkRepository.existsByUserAndSnippet(user, snippet)).thenReturn(true);

        boolean result = snippetService.isBookmarked(snippet, user);

        assertThat(result).isTrue();
    }

    @Test
    void isBookmarked_returnsFalse_forNullInputs() {
        assertThat(snippetService.isBookmarked(null, user)).isFalse();
        assertThat(snippetService.isBookmarked(snippet, null)).isFalse();
    }

    @Test
    void revertToVersion_appliesVersionFields() {
        snippet.setId(1L);

        Snippet oldState = new Snippet("Old Title", "kotlin", "fun main() {}", user);
        oldState.setId(1L);
        SnippetVersion version = new SnippetVersion(oldState);

        when(snippetRepository.findById(1L)).thenReturn(Optional.of(snippet));
        when(snippetVersionRepository.findById(10L)).thenReturn(Optional.of(version));
        when(snippetRepository.save(any(Snippet.class))).thenAnswer(inv -> inv.getArgument(0));

        Snippet result = snippetService.revertToVersion(1L, 10L);

        verify(snippetVersionRepository).save(any(SnippetVersion.class));
        assertThat(result.getTitle()).isEqualTo("Old Title");
        assertThat(result.getLanguage()).isEqualTo("kotlin");
        assertThat(result.getCode()).isEqualTo("fun main() {}");
    }

    @Test
    void countSnippets_delegatesToRepository() {
        when(snippetRepository.countByDeletedFalse()).thenReturn(42L);

        assertThat(snippetService.countSnippets()).isEqualTo(42L);
    }

    @Test
    void findVersions_returnsOrderedList() {
        List<SnippetVersion> versions = List.of(new SnippetVersion(snippet));
        when(snippetVersionRepository.findBySnippetOrderByCreatedAtDesc(snippet)).thenReturn(versions);

        List<SnippetVersion> result = snippetService.findVersions(snippet);

        assertThat(result).hasSize(1);
    }
}
