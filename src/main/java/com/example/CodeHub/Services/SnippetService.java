package com.example.CodeHub.Services;

import com.example.CodeHub.Dto.SnippetDto;
import com.example.CodeHub.Entity.Bookmark;
import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.SnippetVersion;
import com.example.CodeHub.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SnippetService {
    Snippet save(SnippetDto snippetDto, User user);
    List<Snippet> findAllSnippets();
    long countSnippets();
    long countPublicSnippets();
    long countPrivateSnippets();
    List<Snippet> findSnippetsByUser(User user);
    Snippet findById(Long id);
    Snippet findPublicByShareToken(String shareToken);
    Snippet update(Long id, SnippetDto snippetDto);
    List<SnippetVersion> findVersions(Snippet snippet);
    Snippet revertToVersion(Long snippetId, Long versionId);
    void delete(Long id);
    void toggleStar(Long id, boolean starred);
    boolean toggleBookmark(Long snippetId, User user);
    boolean isBookmarked(Snippet snippet, User user);
    List<Bookmark> findBookmarks(User user);
    Page<Snippet> findPublicSnippetsByUser(User user, Pageable pageable);
    
    // Pagination methods
    Page<Snippet> findAllSnippetsPaginated(Pageable pageable);
    Page<Snippet> findSnippetsByUserPaginated(User user, Pageable pageable);
    
    // Search methods
    Page<Snippet> searchSnippets(String searchTerm, Pageable pageable);
    Page<Snippet> searchUserSnippets(User user, String searchTerm, Pageable pageable);
}
