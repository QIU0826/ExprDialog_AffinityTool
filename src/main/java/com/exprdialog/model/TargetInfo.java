package com.exprdialog.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "target_info")
public class TargetInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nickname;
    private String gender;
    private String personality;
    
    @ElementCollection
    private List<String> hobbies;
    
    private String notes;
    
    private Long createTime;
    private Long updateTime;
    
    @PrePersist
    public void prePersist() {
        long currentTime = System.currentTimeMillis();
        this.createTime = currentTime;
        this.updateTime = currentTime;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updateTime = System.currentTimeMillis();
    }
    
    // 添加getter和setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getPersonality() {
        return personality;
    }
    
    public void setPersonality(String personality) {
        this.personality = personality;
    }
    
    public List<String> getHobbies() {
        return hobbies;
    }
    
    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    
    public Long getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}