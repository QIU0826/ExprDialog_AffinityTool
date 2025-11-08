package com.exprdialog.service;

import com.exprdialog.model.AffinityScore;
import com.exprdialog.repository.AffinityScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AffinityService {
    
    @Autowired
    private AffinityScoreRepository affinityScoreRepository;
    
    // 使用本地内存缓存替代Redis
    private final ConcurrentHashMap<Long, Integer> localCache = new ConcurrentHashMap<>();
    
    private static final int DEFAULT_AFFINITY_SCORE = 50;
    
    /**
     * 获取指定目标的当前好感度
     */
    @Cacheable(value = "affinity", key = "#targetId")
    public int getCurrentAffinityScore(Long targetId) {
        // 先尝试从本地缓存获取
        Integer cachedScore = localCache.get(targetId);
        
        if (cachedScore != null) {
            return cachedScore;
        }
        
        // 从数据库获取最新的好感度
        AffinityScore latestScore = affinityScoreRepository.findTopByTargetIdOrderByTimestampDesc(targetId);
        int score = latestScore != null ? latestScore.getScore() : DEFAULT_AFFINITY_SCORE;
        
        // 缓存到本地内存
        localCache.put(targetId, score);
        
        return score;
    }
    
    /**
     * 更新好感度
     * @param targetId 目标ID
     * @param emotion 当前表情
     * @param messageContent 消息内容
     * @param replySpeed 回复速度（毫秒）
     * @return 更新后的好感度
     */
    public int updateAffinityScore(Long targetId, String emotion, String messageContent, long replySpeed) {
        int currentScore = getCurrentAffinityScore(targetId);
        int scoreChange = calculateScoreChange(emotion, messageContent, replySpeed);
        
        // 计算新的好感度，确保在0-100范围内
        int newScore = Math.max(0, Math.min(100, currentScore + scoreChange));
        
        // 保存到数据库
        AffinityScore scoreRecord = new AffinityScore();
        scoreRecord.setTargetId(targetId);
        scoreRecord.setScore(newScore);
        scoreRecord.setEmotion(emotion);
        scoreRecord.setEvent(buildEventDescription(emotion, messageContent, replySpeed, scoreChange));
        affinityScoreRepository.save(scoreRecord);
        
        // 更新缓存
        cacheAffinityScore(targetId, newScore);
        
        return newScore;
    }
    
    /**
     * 计算好感度变化值
     */
    private int calculateScoreChange(String emotion, String messageContent, long replySpeed) {
        int scoreChange = 0;
        
        // 根据表情计算变化
        if (emotion != null) {
            switch (emotion) {
                case "开心":
                    scoreChange += 2;
                    break;
                case "皱眉":
                    scoreChange -= 3;
                    break;
                case "无视":
                    scoreChange -= 1;
                    break;
                // 其他表情微调
                default:
                    scoreChange += 0;
            }
        }
        
        // 根据消息内容计算变化
        if (messageContent != null) {
            messageContent = messageContent.toLowerCase();
            
            // 负面关键词
            if (messageContent.contains("哦") && !messageContent.contains("哦哦") || 
                messageContent.contains("嗯") && messageContent.length() <= 5) {
                scoreChange -= 2;
            }
            
            // 正面关键词
            if (messageContent.contains("哈哈") || messageContent.contains("开心") || 
                messageContent.contains("喜欢") || messageContent.contains("好啊")) {
                scoreChange += 1;
            }
        }
        
        // 根据回复速度计算变化
        // 秒回（小于3秒）加分
        if (replySpeed < 3000) {
            scoreChange += 1;
        }
        // 长时间不回复（大于5分钟）减分
        else if (replySpeed > 300000) {
            scoreChange -= 1;
        }
        
        return scoreChange;
    }
    
    /**
     * 构建事件描述
     */
    private String buildEventDescription(String emotion, String messageContent, long replySpeed, int scoreChange) {
        StringBuilder description = new StringBuilder();
        
        if (emotion != null) {
            description.append("表情：").append(emotion).append("; ");
        }
        
        if (messageContent != null) {
            // 截取消息内容前20个字符
            String shortContent = messageContent.length() > 20 ? 
                messageContent.substring(0, 20) + "..." : messageContent;
            description.append("消息：").append(shortContent).append("; ");
        }
        
        // 回复速度描述
        String speedDesc;
        if (replySpeed < 3000) {
            speedDesc = "秒回";
        } else if (replySpeed < 30000) {
            speedDesc = "快速回复";
        } else if (replySpeed < 300000) {
            speedDesc = "一般回复";
        } else {
            speedDesc = "长时间回复";
        }
        description.append("回复速度：").append(speedDesc).append("; ");
        
        description.append("分数变化：").append(scoreChange > 0 ? "+" : "").append(scoreChange);
        
        return description.toString();
    }
    
    /**
     * 缓存好感度到本地内存
     */
    @CachePut(value = "affinity", key = "#targetId")
    private int cacheAffinityScore(Long targetId, int score) {
        // 缓存到本地内存
        localCache.put(targetId, score);
        return score;
    }
    
    /**
     * 检查是否需要发送好感度预警
     */
    public boolean shouldSendAffinityWarning(Long targetId) {
        int score = getCurrentAffinityScore(targetId);
        return score < 30; // 好感度低于30分触发预警
    }
    
    /**
     * 根据好感度分数确定等级
     */
    public String determineAffinityLevel(int score) {
        if (score >= 80) {
            return "很高";
        } else if (score >= 60) {
            return "良好";
        } else if (score >= 40) {
            return "一般";
        } else if (score >= 20) {
            return "较低";
        } else {
            return "很低";
        }
    }
    
    /**
     * 获取好感度恢复建议
     */
    public String getAffinityRecoverySuggestion(int currentScore, String personality) {
        if (currentScore >= 60) {
            return "好感度良好，继续保持当前的交流方式！";
        }
        
        StringBuilder suggestion = new StringBuilder();
        suggestion.append("建议调整交流策略：\n");
        
        if (personality != null) {
            switch (personality) {
                case "内向":
                    suggestion.append("- 尝试更多倾听，给对方更多表达的空间\n");
                    suggestion.append("- 避免过于直接的问题，用更含蓄的方式交流\n");
                    break;
                case "外向":
                    suggestion.append("- 可以更积极地回应，参与对方感兴趣的话题\n");
                    suggestion.append("- 适当分享自己的生活和经历\n");
                    break;
                case "直爽":
                    suggestion.append("- 直接表达想法，但注意语气和措辞\n");
                    suggestion.append("- 避免过多委婉和暗示\n");
                    break;
                case "敏感":
                    suggestion.append("- 注意用词谨慎，避免可能引起误解的表达\n");
                    suggestion.append("- 多给予肯定和鼓励\n");
                    break;
                default:
                    // 默认建议
                    suggestion.append("- 尝试聊一些对方感兴趣的话题\n");
                    suggestion.append("- 注意观察对方的反应，及时调整交流方式\n");
            }
        }
        
        // 通用建议
        suggestion.append("- 保持适当的回复频率，不要过于频繁或冷淡\n");
        suggestion.append("- 避免争论和负面话题\n");
        suggestion.append("- 多使用表情和语气词，让对话更生动\n");
        
        return suggestion.toString();
    }
    
    /**
     * 获取上次好感度变化值
     */
    public int getLastScoreChange(Long targetId) {
        // 简单实现，返回随机值表示变化
        return (int)(Math.random() * 5 - 2); // 返回-2到2之间的随机值
    }
    
    /**
     * 获取好感度提升建议
     */
    public String getAffinityImprovementSuggestion(String emotion) {
        if ("开心".equals(emotion)) {
            return "对方心情好，可以分享一些有趣的事情，进一步加深好感。";
        } else if ("皱眉".equals(emotion)) {
            return "对方似乎有些不满，建议改变话题或更温和地交流。";
        } else {
            return "多使用表情，注意回复速度，避免使用简短冷淡的回复。";
        }
    }
    
    /**
     * 重置好感度
     */
    @CacheEvict(value = "affinity", key = "#targetId")
    public void resetAffinityScore(Long targetId) {
        // 清除本地缓存
        localCache.remove(targetId);
    }
    
    /**
     * 获取好感度提升策略
     */
    public Map<String, List<String>> getAffinityStrategies() {
        Map<String, List<String>> strategies = new HashMap<>();
        
        // 初始化各种策略
        strategies.put("积极交流", Arrays.asList(
            "定期保持联系", 
            "及时回复消息", 
            "分享生活趣事"
        ));
        
        strategies.put("情绪管理", Arrays.asList(
            "注意避免负面情绪", 
            "适当表达积极情绪", 
            "尊重对方的感受"
        ));
        
        strategies.put("关系深化", Arrays.asList(
            "记住重要日子", 
            "给予真诚赞美", 
            "提供实际帮助"
        ));
        
        return strategies;
    }
}