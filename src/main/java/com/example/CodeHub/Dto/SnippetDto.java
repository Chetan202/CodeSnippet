package com.example.CodeHub.Dto;

public class SnippetDto {
    private String title;
    private String language;
    private String code;
    
    public SnippetDto() {
    }
    
    public SnippetDto(String title, String language, String code) {
        this.title = title;
        this.language = language;
        this.code = code;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
}
