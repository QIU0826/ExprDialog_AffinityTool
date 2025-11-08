package com.exprdialog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class DeepSeekService {
    
    @Value("${deepseek.api-key}")
    private String apiKey;
    
    @Value("${deepseek.api-url}")
    private String baseUrl;
    
    @Value("${deepseek.model}")
    private String model;
    
    @Value("${deepseek.temperature}")
    private double temperature;
    
    @Value("${deepseek.max-tokens}")
    private int maxTokens;
    
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private boolean isInitialized = false;
    
    public DeepSeekService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    @PostConstruct
    public void init() {
        // 检查API密钥是否有效
        if (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your_api_key")) {
            isInitialized = true;
            System.out.println("DeepSeek服务初始化成功");
        } else {
            System.out.println("警告：DeepSeek API密钥未配置或使用默认值，API调用功能将不可用");
        }
    }
    
    /**
     * 生成对话回复建议
     * @param message 用户输入的消息
     * @param targetInfo 攻略对象信息
     * @param emotion 表情识别结果
     * @param conversationHistory 对话历史
     * @return 回复建议列表
     */
    public List<String> generateReplySuggestions(String message, 
                                               Map<String, Object> targetInfo, 
                                               String emotion, 
                                               List<Map<String, Object>> conversationHistory) {
        // 如果DeepSeek服务未初始化，直接返回默认建议
        if (!isInitialized) {
            System.out.println("DeepSeek服务未初始化，使用默认回复建议");
            return getDefaultSuggestions(message, emotion);
        }
        
        try {
            // 构建提示词
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加系统提示
            String systemPrompt = buildSystemPrompt(targetInfo, emotion);
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);
            
            // 添加对话历史
            addConversationHistory(messages, conversationHistory);
            
            // 添加用户最新消息
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.add(userMessage);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("temperature", temperature);
            requestBody.put("max_tokens", maxTokens);
            
            // 构建请求
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            Request request = new Request.Builder()
                    .url(baseUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .build();
            
            // 调用API
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    // 解析响应
                    @SuppressWarnings("unchecked")
                    Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> choice = choices.get(0);
                        Map<String, String> messageObj = (Map<String, String>) choice.get("message");
                        String content = messageObj.get("content");
                        
                        // 解析回复建议
                        return parseReplySuggestions(content);
                    }
                }
            }
            
            // 如果API调用失败，返回默认建议
            return getDefaultSuggestions(message, emotion);
            
        } catch (Exception e) {
            System.err.println("DeepSeek API调用失败: " + e.getMessage());
            // 如果API调用失败，返回默认建议
            return getDefaultSuggestions(message, emotion);
        }
    }
    
    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt(Map<String, Object> targetInfo, String emotion) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个恋爱对话助手，帮助用户与心仪对象进行更好的交流。");
        prompt.append("请根据以下信息，生成3条简短、自然、符合情境的回复建议。");
        prompt.append("回复建议需要考虑对方的性格特点和当前情绪状态。");
        prompt.append("\n\n当前情境信息：\n");
        
        if (targetInfo != null) {
            prompt.append("攻略对象信息：\n");
            prompt.append("- 昵称：").append(targetInfo.getOrDefault("nickname", "对方")).append("\n");
            prompt.append("- 性别：").append(targetInfo.getOrDefault("gender", "")).append("\n");
            prompt.append("- 性格：").append(targetInfo.getOrDefault("personality", "")).append("\n");
            
            @SuppressWarnings("unchecked")
            List<String> hobbies = (List<String>) targetInfo.getOrDefault("hobbies", new ArrayList<>());
            if (!hobbies.isEmpty()) {
                prompt.append("- 爱好：").append(String.join("、", hobbies)).append("\n");
            }
        }
        
        if (emotion != null && !emotion.isEmpty()) {
            prompt.append("对方当前情绪：").append(emotion).append("\n");
            
            // 根据情绪添加建议
            switch (emotion) {
                case "开心":
                    prompt.append("建议：对方心情很好，可以多聊一些积极的话题。\n");
                    break;
                case "皱眉":
                    prompt.append("建议：对方可能有些不开心，注意语气委婉，询问原因。\n");
                    break;
                case "无视":
                    prompt.append("建议：对方可能心不在焉，尝试找对方感兴趣的话题。\n");
                    break;
            }
        }
        
        prompt.append("\n请返回3条回复建议，每条建议单独一行。");
        return prompt.toString();
    }
    
    /**
     * 添加对话历史到消息列表
     */
    private void addConversationHistory(List<Map<String, String>> messages, List<Map<String, Object>> conversationHistory) {
        if (conversationHistory != null && !conversationHistory.isEmpty()) {
            // 只取最近的5条消息
            int startIndex = Math.max(0, conversationHistory.size() - 5);
            
            for (int i = startIndex; i < conversationHistory.size(); i++) {
                Map<String, Object> message = conversationHistory.get(i);
                boolean isUser = (Boolean) message.getOrDefault("isUser", false);
                String content = (String) message.getOrDefault("content", "");
                
                if (!content.isEmpty()) {
                    String role = isUser ? "user" : "assistant";
                    Map<String, String> historyMessage = new HashMap<>();
                    historyMessage.put("role", role);
                    historyMessage.put("content", content);
                    messages.add(historyMessage);
                }
            }
        }
    }
    
    /**
     * 解析API返回的回复建议
     */
    private List<String> parseReplySuggestions(String response) {
        List<String> suggestions = new ArrayList<>();
        String[] lines = response.split("\\n");
        
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && line.length() > 2) {
                // 移除序号等前缀
                if (line.matches("^\\d+[.、]\\s+.+")) {
                    line = line.replaceFirst("^\\d+[.、]\\s+", "");
                }
                suggestions.add(line);
            }
        }
        
        // 确保至少有一个建议
        if (suggestions.isEmpty()) {
            suggestions.add("这个话题很有意思，能详细说说吗？");
        }
        
        return suggestions;
    }
    
    /**
     * 获取默认回复建议（当API调用失败时）
     */
    private List<String> getDefaultSuggestions(String message, String emotion) {
        List<String> suggestions = new ArrayList<>();
        
        suggestions.add("我也觉得是这样，你说得很有道理！");
        
        if (emotion != null) {
            switch (emotion) {
                case "开心":
                    suggestions.add("看你这么开心，有什么好事吗？");
                    break;
                case "皱眉":
                    suggestions.add("你看起来有点不开心，需要聊聊吗？");
                    break;
                default:
                    suggestions.add("你平时有什么爱好？");
            }
        } else {
            suggestions.add("你平时有什么爱好？");
        }
        
        suggestions.add("下次有机会一起出来玩吧？");
        return suggestions;
    }
}