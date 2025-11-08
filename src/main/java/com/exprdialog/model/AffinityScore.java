package com.exprdialog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "affinity_score")

public class AffinityScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "target_id")
    private Long targetId;
    
    private Integer score;
    private String emotion;
    private String event;
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
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public String getEmotion() {
        return emotion;
    }
    
    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }
    
    public String getEvent() {
        return event;
    }
    
    public void setEvent(String event) {
        this.event = event;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}