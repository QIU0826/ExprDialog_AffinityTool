package com.exprdialog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "conversation_history")

public class ConversationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "target_id")
    private Long targetId;
    
    private String content;
    private Boolean isUser;
    private String emotion;
    private Long timestamp;
    
    @PrePersist
    public void prePersist() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // 添加getter和setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTargetId() {
        return targetId;
    }
    
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Boolean getIsUser() {
        return isUser;
    }
    
    public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
    }
    
    public String getEmotion() {
        return emotion;
    }
    
    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}