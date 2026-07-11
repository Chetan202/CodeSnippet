package com.example.CodeHub.Services;

import com.example.CodeHub.Dto.SnippetDto;
import com.example.CodeHub.Entity.Bookmark;
import com.example.CodeHub.Entity.Comment;
import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.SnippetVersion;
import com.example.CodeHub.Entity.Tag;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.BookmarkRepository;
import com.example.CodeHub.Repository.CommentRepository;
import com.example.CodeHub.Repository.SnippetRepository;
import com.example.CodeHub.Repository.SnippetVersionRepository;
import com.example.CodeHub.Repository.TagRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SnippetServiceImpl implements SnippetService {

    private final SnippetRepository snippetRepository;
    private final SnippetVersionRepository snippetVersionRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final TagRepository tagRepository;

    public SnippetServiceImpl(SnippetRepository snippetRepository,
                              SnippetVersionRepository snippetVersionRepository,
                              CommentRepository commentRepository,
                              BookmarkRepository bookmarkRepository,
                              TagRepository tagRepository) {
        this.snippetRepository = snippetRepository;
        this.snippetVersionRepository = snippetVersionRepository;
        this.commentRepository = commentRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public Snippet save(SnippetDto snippetDto, User user) {
        Snippet snippet = new Snippet(
                snippetDto.getTitle(),
                snippetDto.getLanguage(),
                snippetDto.getCode(),
                user
        );
        applyEditableFields(snippet, snippetDto);
        return snippetRepository.save(snippet);
    }

    @Override
    public List<Snippet> findAllSnippets() {
        return snippetRepository.findAllByDeletedFalseOrderByStarredDescCreatedAtDesc();
    }

    @Override
    public long countSnippets() {
        return snippetRepository.countByDeletedFalse();
    }

    @Override
    public long countPublicSnippets() {
        return snippetRepository.countByPublicSnippetTrueAndDeletedFalse();
    }

    @Override
    public long countPrivateSnippets() {
        return snippetRepository.countByPublicSnippetFalseAndDeletedFalse();
    }

    @Override
    public List<Snippet> findSnippetsByUser(User user) {
        return snippetRepository.findByUserAndDeletedFalseOrderByStarredDescCreatedAtDesc(user);
    }

    @Override
    public Snippet findById(Long id) {
        return snippetRepository.findById(id)
                .filter(snippet -> !snippet.isDeleted())
                .orElse(null);
    }

    @Override
    public Snippet findPublicByShareToken(String shareToken) {
        return snippetRepository.findByShareTokenAndPublicSnippetTrueAndDeletedFalse(shareToken);
    }

    @Override
    public Snippet update(Long id, SnippetDto snippetDto) {
        Snippet existingSnippet = findById(id);
        if (existingSnippet == null) {
            return null;
        }

        snippetVersionRepository.save(new SnippetVersion(existingSnippet));
        applyEditableFields(existingSnippet, snippetDto);
        
        return snippetRepository.save(existingSnippet);
    }

    @Override
    public List<SnippetVersion> findVersions(Snippet snippet) {
        return snippetVersionRepository.findBySnippetOrderByCreatedAtDesc(snippet);
    }

    @Override
    public Snippet revertToVersion(Long snippetId, Long versionId) {
        Snippet snippet = findById(snippetId);
        if (snippet == null) {
            return null;
        }
        SnippetVersion version = snippetVersionRepository.findById(versionId).orElse(null);
        if (version == null || !version.getSnippet().getId().equals(snippet.getId())) {
            return null;
        }
        snippetVersionRepository.save(new SnippetVersion(snippet));
        snippet.setTitle(version.getTitle());
        snippet.setLanguage(version.getLanguage());
        snippet.setTags(version.getTags());
        snippet.setCollectionName(version.getCollectionName());
        snippet.setCode(version.getCode());
        return snippetRepository.save(snippet);
    }

    @Override
    public void delete(Long id) {
        Snippet snippet = findById(id);
        if (snippet != null) {
            snippet.setDeleted(true);
            snippet.setDeletedAt(LocalDateTime.now());
            snippetRepository.save(snippet);
        }
    }

    @Override
    public void toggleStar(Long id, boolean starred) {
        Snippet snippet = findById(id);
        if (snippet != null) {
            snippet.setStarred(starred);
            snippetRepository.save(snippet);
        }
    }

    @Override
    public Comment addComment(Long snippetId, User user, String content) {
        Snippet snippet = findById(snippetId);
        if (snippet == null || content == null || content.trim().isEmpty()) {
            return null;
        }
        return commentRepository.save(new Comment(snippet, user, content.trim()));
    }

    @Override
    public List<Comment> findComments(Snippet snippet) {
        return commentRepository.findBySnippetOrderByCreatedAtDesc(snippet);
    }

    @Override
    public boolean toggleBookmark(Long snippetId, User user) {
        Snippet snippet = findById(snippetId);
        if (snippet == null || user == null) {
            return false;
        }
        Bookmark existing = bookmarkRepository.findByUserAndSnippet(user, snippet);
        if (existing != null) {
            bookmarkRepository.delete(existing);
            return false;
        }
        bookmarkRepository.save(new Bookmark(user, snippet));
        return true;
    }

    @Override
    public boolean isBookmarked(Snippet snippet, User user) {
        return snippet != null && user != null && bookmarkRepository.existsByUserAndSnippet(user, snippet);
    }

    @Override
    public List<Bookmark> findBookmarks(User user) {
        return bookmarkRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public List<Tag> findAllTags() {
        return tagRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Page<Snippet> findPublicSnippetsByUser(User user, Pageable pageable) {
        return snippetRepository.findByUserAndPublicSnippetTrueAndDeletedFalseOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    public Page<Snippet> findAllSnippetsPaginated(Pageable pageable) {
        return snippetRepository.findAllByDeletedFalseOrderByStarredDescCreatedAtDesc(pageable);
    }
    
    @Override
    public Page<Snippet> findSnippetsByUserPaginated(User user, Pageable pageable) {
        return snippetRepository.findByUserAndDeletedFalseOrderByStarredDescCreatedAtDesc(user, pageable);
    }
    
    @Override
    public Page<Snippet> searchSnippets(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAllSnippetsPaginated(pageable);
        }
        return snippetRepository.searchSnippets(searchTerm.trim(), pageable);
    }
    
    @Override
    public Page<Snippet> searchUserSnippets(User user, String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findSnippetsByUserPaginated(user, pageable);
        }
        return snippetRepository.searchUserSnippets(user, searchTerm.trim(), pageable);
    }

    private void applyEditableFields(Snippet snippet, SnippetDto snippetDto) {
        snippet.setTitle(snippetDto.getTitle());
        snippet.setLanguage(snippetDto.getLanguage());
        snippet.setCode(snippetDto.getCode());
        snippet.setTags(snippetDto.getTags());
        snippet.setCollectionName(snippetDto.getCollectionName());
        snippet.setPublicSnippet(snippetDto.isPublicSnippet());
        snippet.setTagEntities(resolveTags(snippetDto.getTags()));
    }

    private Set<Tag> resolveTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .map(this::findOrCreateTag)
                .collect(Collectors.toSet());
    }

    private Tag findOrCreateTag(String name) {
        Tag existing = tagRepository.findByNameIgnoreCase(name);
        if (existing != null) {
            return existing;
        }
        return tagRepository.save(new Tag(name));
    }
}
