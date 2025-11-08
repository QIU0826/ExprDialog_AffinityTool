package com.exprdialog.controller;

import com.exprdialog.model.AffinityScore;
import com.exprdialog.repository.AffinityScoreRepository;
import com.exprdialog.service.AffinityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/affinity")
public class AffinityController {
    
    @Autowired
    private AffinityService affinityService;
    
    @Autowired
    private AffinityScoreRepository affinityScoreRepository;
    
    /**
     * 获取指定目标的当前好感度
     */
    @GetMapping("/{targetId}")
    public ResponseEntity<Map<String, Object>> getAffinityScore(@PathVariable Long targetId) {
        try {
            int currentScore = affinityService.getCurrentAffinityScore(targetId);
            Map<String, Object> response = new HashMap<>();
            response.put("score", currentScore);
            response.put("targetId", targetId);
            
            // 添加好感度等级说明
            String level = affinityService.determineAffinityLevel(currentScore);
            response.put("level", level);
            
            // 检查是否需要发送好感度预警
            try {
                if (affinityService.shouldSendAffinityWarning(targetId)) {
                    response.put("warning", true);
                }
            } catch (Exception e) {
                // 如果方法不存在或出错，忽略预警检查
                System.out.println("检查好感度预警时出错: " + e.getMessage());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取好感度失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 更新好感度
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateAffinityScore(@RequestBody Map<String, Object> request) {
        try {
            // 解析请求参数
            Long targetId = null;
            String targetIdStr = String.valueOf(request.get("targetId"));
            try {
                targetId = Long.parseLong(targetIdStr);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "无效的目标ID格式"));
            }
            
            String message = request.get("message") != null ? String.valueOf(request.get("message")) : "";
            String emotion = request.get("emotion") != null ? String.valueOf(request.get("emotion")) : "neutral";
            
            // 安全地获取回复速度参数
            int replySpeed = 5; // 默认回复速度为5秒
            Object replySpeedObj = request.get("replySpeed");
            if (replySpeedObj != null) {
                try {
                    if (replySpeedObj instanceof Number) {
                        replySpeed = ((Number) replySpeedObj).intValue();
                    } else {
                        replySpeed = Integer.parseInt(String.valueOf(replySpeedObj));
                    }
                } catch (NumberFormatException e) {
                    // 使用默认值5秒
                    System.out.println("无效的回复速度参数，使用默认值5秒");
                }
            }
            
            // 验证必要参数
            if (targetId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "缺少目标ID"));
            }
            
            // 更新好感度
            int newScore = affinityService.updateAffinityScore(targetId, message, emotion, replySpeed);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("targetId", targetId);
            response.put("newScore", newScore);
            response.put("level", affinityService.determineAffinityLevel(newScore));
            
            // 获取好感度变化信息
            int scoreChange = affinityService.getLastScoreChange(targetId);
            response.put("scoreChange", scoreChange);
            
            // 添加好感度提升建议（如果好感度较低）
            if (newScore < 60) {
                String suggestion = affinityService.getAffinityImprovementSuggestion(emotion);
                response.put("improvementSuggestion", suggestion);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "更新好感度失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 获取好感度历史记录
     */
    @GetMapping("/history/{targetId}")
    public ResponseEntity<?> getAffinityHistory(@PathVariable Long targetId) {
        try {
            List<AffinityScore> history = affinityScoreRepository.findByTargetIdOrderByTimestampDesc(targetId);
            return ResponseEntity.ok(history);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取好感度历史失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 重置好感度
     */
    @PostMapping("/reset/{targetId}")
    public ResponseEntity<Map<String, Object>> resetAffinityScore(@PathVariable Long targetId) {
        try {
            // 重置Redis中的缓存
            affinityService.resetAffinityScore(targetId);
            
            // 记录重置事件到数据库
            AffinityScore resetRecord = new AffinityScore();
            resetRecord.setTargetId(targetId);
            resetRecord.setScore(50); // 重置为默认值50
            resetRecord.setEmotion("重置");
            resetRecord.setEvent("好感度重置");
            affinityScoreRepository.save(resetRecord);
            
            Map<String, Object> response = new HashMap<>();
            response.put("targetId", targetId);
            response.put("score", 50);
            response.put("message", "好感度已重置为初始值50");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "重置好感度失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 获取好感度预警设置
     */
    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getAffinitySettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("warningThreshold", 30);
        settings.put("criticalThreshold", 20);
        settings.put("ignoreWarningCount", 3);
        settings.put("maxScore", 100);
        settings.put("minScore", 0);
        
        return ResponseEntity.ok(settings);
    }
    
    /**
     * 获取好感度提升策略
     */
    @GetMapping("/strategies")
    public ResponseEntity<?> getAffinityStrategies() {
        try {
            Map<String, List<String>> strategies = affinityService.getAffinityStrategies();
            return ResponseEntity.ok(strategies);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取好感度提升策略失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}