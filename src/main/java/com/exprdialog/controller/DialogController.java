package com.exprdialog.controller;

import com.exprdialog.model.ConversationHistory;
import com.exprdialog.repository.ConversationHistoryRepository;
import com.exprdialog.service.AffinityService;
import com.exprdialog.service.DeepSeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dialog")
public class DialogController {
    
    @Autowired
    private DeepSeekService deepSeekService;
    
    @Autowired
    private AffinityService affinityService;
    
    @Autowired
    private ConversationHistoryRepository conversationHistoryRepository;
    
    /**
     * 生成对话回复建议
     */
    @PostMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> generateRecommendations(@RequestBody Map<String, Object> request) {
        try {
            String message = (String) request.get("message");
            Map<String, Object> targetInfo = (Map<String, Object>) request.get("targetInfo");
            String emotion = (String) request.get("emotion");
            List<Map<String, Object>> conversationHistory = (List<Map<String, Object>>) request.getOrDefault("conversationHistory", List.of());
            
            // 生成回复建议
            List<String> suggestions = deepSeekService.generateReplySuggestions(
                    message, targetInfo, emotion, conversationHistory);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("suggestions", suggestions);
            
            // 如果有目标ID，检查好感度并添加建议
            if (targetInfo != null && targetInfo.containsKey("id")) {
                Long targetId = Long.valueOf(targetInfo.get("id").toString());
                int affinityScore = affinityService.getCurrentAffinityScore(targetId);
                response.put("affinityScore", affinityScore);
                
                // 发送好感度预警（如果需要）
                if (affinityService.shouldSendAffinityWarning(targetId)) {
                    String personality = (String) targetInfo.getOrDefault("personality", "");
                    String recoverySuggestion = affinityService.getAffinityRecoverySuggestion(affinityScore, personality);
                    response.put("affinityWarning", recoverySuggestion);
                }
            }
            
            // 保存对话历史（如果有目标ID）
            if (targetInfo != null && targetInfo.containsKey("id")) {
                saveConversationHistory(Long.valueOf(targetInfo.get("id").toString()), message, true, emotion);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "生成回复建议失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 获取指定目标的对话历史
     */
    @GetMapping("/history/{targetId}")
    public ResponseEntity<List<ConversationHistory>> getConversationHistory(@PathVariable Long targetId) {
        List<ConversationHistory> history = conversationHistoryRepository.findTop10ByTargetIdOrderByTimestampDesc(targetId);
        return ResponseEntity.ok(history);
    }
    
    /**
     * 保存对话历史
     */
    private void saveConversationHistory(Long targetId, String content, boolean isUser, String emotion) {
        ConversationHistory history = new ConversationHistory();
        history.setTargetId(targetId);
        history.setContent(content);
        history.setIsUser(isUser);
        history.setEmotion(emotion);
        conversationHistoryRepository.save(history);
    }
    
    /**
     * 模拟对方回复
     */
    @PostMapping("/simulate-reply")
    public ResponseEntity<Map<String, Object>> simulateReply(@RequestBody Map<String, Object> request) {
        try {
            String userMessage = (String) request.get("message");
            String targetPersonality = (String) request.get("personality");
            String emotion = (String) request.get("emotion");
            
            // 这里可以根据性格和情绪生成不同风格的模拟回复
            String simulatedReply = generateSimulatedReply(userMessage, targetPersonality, emotion);
            
            Map<String, Object> response = new HashMap<>();
            response.put("reply", simulatedReply);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "生成模拟回复失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 生成模拟回复
     */
    private String generateSimulatedReply(String userMessage, String personality, String emotion) {
        // 根据性格和情绪生成不同风格的回复
        if ("内向".equals(personality)) {
            return generateIntrovertedReply(userMessage, emotion);
        } else if ("外向".equals(personality)) {
            return generateExtrovertedReply(userMessage, emotion);
        } else if ("直爽".equals(personality)) {
            return generateDirectReply(userMessage, emotion);
        } else if ("敏感".equals(personality)) {
            return generateSensitiveReply(userMessage, emotion);
        }
        
        // 默认回复
        switch (emotion) {
            case "开心":
                return "是啊，感觉真不错！";
            case "皱眉":
                return "嗯...可能我理解错了什么？";
            case "无视":
                return "（短暂沉默）你刚才说什么？";
            default:
                return "嗯，我明白了。";
        }
    }
    
    private String generateIntrovertedReply(String userMessage, String emotion) {
        if (emotion != null) {
            switch (emotion) {
                case "开心":
                    return "嗯，我也觉得...";
                case "皱眉":
                    return "是不是我哪里说错了？";
                default:
                    return "我在听呢...";
            }
        }
        return "好的，我明白了...";
    }
    
    private String generateExtrovertedReply(String userMessage, String emotion) {
        if (emotion != null) {
            switch (emotion) {
                case "开心":
                    return "对啊对啊！我也是这么想的，太有趣了！";
                case "皱眉":
                    return "怎么了？有什么不开心的吗？说出来听听！";
                default:
                    return "这个话题太棒了！我们可以深入聊聊！";
            }
        }
        return "哇！这个我知道，跟我你说啊...";
    }
    
    private String generateDirectReply(String userMessage, String emotion) {
        if (emotion != null) {
            switch (emotion) {
                case "开心":
                    return "不错，继续保持！";
                case "皱眉":
                    return "有话直说，别拐弯抹角的。";
                default:
                    return "直接说重点吧。";
            }
        }
        return "明白了，我会考虑的。";
    }
    
    private String generateSensitiveReply(String userMessage, String emotion) {
        if (emotion != null) {
            switch (emotion) {
                case "开心":
                    return "真的吗？你真的这么觉得？";
                case "皱眉":
                    return "你是不是觉得我哪里不好？";
                default:
                    return "我是不是让你感到困扰了？";
            }
        }
        return "我...我只是觉得这样可能不太好...";
    }
}