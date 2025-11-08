package com.exprdialog.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class BaiDuAIService {
    
    @Value("${baidu.ai.api-key}")
    private String apiKey;
    
    @Value("${baidu.ai.secret-key}")
    private String secretKey;
    
    @Value("${baidu.ai.emotion-detect-url}")
    private String faceDetectUrl;
    
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    
    public BaiDuAIService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 获取百度AI的访问令牌
     */
    private String getAccessToken() throws IOException {
        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" 
                + apiKey + "&client_secret=" + secretKey;
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonNode root = objectMapper.readTree(response.body().string());
                return root.get("access_token").asText();
            }
            throw new IOException("获取百度AI访问令牌失败");
        }
    }
    
    /**
     * 调用百度AI表情识别API
     * @param base64Image Base64编码的图片数据
     * @return 表情识别结果
     */
    public Map<String, Object> detectEmotion(String base64Image) throws IOException {
        String accessToken = getAccessToken();
        String url = faceDetectUrl + "?access_token=" + accessToken;
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("image", base64Image);
        requestBody.put("image_type", "BASE64");
        requestBody.put("face_field", "emotion");
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), jsonBody))
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonNode root = objectMapper.readTree(response.body().string());
                
                // 检查API调用是否成功
                if (root.get("error_code").asInt() == 0) {
                    JsonNode result = root.get("result");
                    if (result != null && result.has("face_list") && !result.get("face_list").isEmpty()) {
                        JsonNode face = result.get("face_list").get(0);
                        if (face.has("emotion")) {
                            JsonNode emotion = face.get("emotion");
                            
                            // 获取最高置信度的表情
                            String maxEmotion = "neutral";
                            double maxConfidence = 0;
                            
                            for (JsonNode emotionNode : emotion) {
                                String emotionName = emotionNode.get("type").asText();
                                double confidence = emotionNode.get("probability").asDouble();
                                
                                if (confidence > maxConfidence) {
                                    maxConfidence = confidence;
                                    maxEmotion = emotionName;
                                }
                            }
                            
                            Map<String, Object> resultMap = new HashMap<>();
                            resultMap.put("emotion", convertEmotionName(maxEmotion));
                            resultMap.put("confidence", maxConfidence);
                            return resultMap;
                        }
                    }
                }
                throw new IOException("表情识别失败: " + root.get("error_msg").asText());
            }
            throw new IOException("表情识别请求失败");
        }
    }
    
    /**
     * 将百度AI返回的表情名称转换为我们系统使用的表情名称
     */
    private String convertEmotionName(String baiduEmotion) {
        switch (baiduEmotion) {
            case "happy":
                return "开心";
            case "sad":
                return "悲伤";
            case "angry":
                return "愤怒";
            case "surprise":
                return "惊讶";
            case "fear":
                return "恐惧";
            case "disgust":
                return "厌恶";
            case "contempt":
                return "蔑视";
            case "neutral":
                return "平静";
            default:
                return "其他";
        }
    }
}