package com.yxrobot.util;

/**
 * 字符问题实体类
 */
public class CharacterIssue {
    private int lineNumber;
    private int columnNumber;
    private char problematicCharacter;
    private String description;
    private String context;
    
    public CharacterIssue(int lineNumber, int columnNumber, char problematicCharacter, 
                         String description, String context) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.problematicCharacter = problematicCharacter;
        this.description = description;
        this.context = context;
    }
    
    // Getters and Setters
    public int getLineNumber() {
        return lineNumber;
    }
    
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    public int getColumnNumber() {
        return columnNumber;
    }
    
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }
    
    public char getProblematicCharacter() {
        return problematicCharacter;
    }
    
    public void setProblematicCharacter(char problematicCharacter) {
        this.problematicCharacter = problematicCharacter;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getContext() {
        return context;
    }
    
    public void setContext(String context) {
        this.context = context;
    }
}