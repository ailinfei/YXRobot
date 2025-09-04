package com.yxrobot.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 编码问题实体类
 */
public class EncodingIssue {
    private File file;
    private String declaredEncoding;
    private String actualEncoding;
    private boolean bomDetected;
    private List<CharacterIssue> characterIssues;
    private boolean hasProblems;
    private String error;
    
    public EncodingIssue(File file) {
        this.file = file;
        this.characterIssues = new ArrayList<>();
        this.hasProblems = false;
    }
    
    // Getters and Setters
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public String getDeclaredEncoding() {
        return declaredEncoding;
    }
    
    public void setDeclaredEncoding(String declaredEncoding) {
        this.declaredEncoding = declaredEncoding;
    }
    
    public String getActualEncoding() {
        return actualEncoding;
    }
    
    public void setActualEncoding(String actualEncoding) {
        this.actualEncoding = actualEncoding;
    }
    
    public boolean isBomDetected() {
        return bomDetected;
    }
    
    public void setBomDetected(boolean bomDetected) {
        this.bomDetected = bomDetected;
    }
    
    public List<CharacterIssue> getCharacterIssues() {
        return characterIssues;
    }
    
    public void setCharacterIssues(List<CharacterIssue> characterIssues) {
        this.characterIssues = characterIssues;
    }
    
    public boolean hasProblems() {
        return hasProblems;
    }
    
    public void setHasProblems(boolean hasProblems) {
        this.hasProblems = hasProblems;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}