package com.example.demo.service;

import com.example.demo.dto.AiConsultResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 非遗 AI 咨询服务：接入 DeepSeek 接口，实现“提问-回答”基础功能
 * 用户可快速获取丽水非遗相关基础信息
 */
@Service
public class DeepSeekService {

    @Value("${deepseek.api-key:}")
    private String apiKey;

    @Value("${deepseek.api-url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${deepseek.model:deepseek-chat}")
    private String model;

    private static final String SYSTEM_PROMPT = "你是丽水市非物质文化遗产平台的智能咨询助手。请根据用户的问题，简要、准确地回答与丽水非遗相关的内容，包括但不限于：非遗项目介绍、传承人、传统技艺、民俗活动、保护与传承等。回答请简洁友好，便于普通用户理解。若问题与丽水非遗无关，可礼貌说明并引导用户提问非遗相关话题。";

    /**
     * 发送用户问题到 DeepSeek，返回回答文本
     * 若未配置 API Key 或调用失败，返回友好提示
     */
    public AiConsultResponse chat(String userQuestion) {
        if (apiKey == null || apiKey.isEmpty()) {
            return new AiConsultResponse("暂未配置 AI 服务，请联系管理员在后台配置 DeepSeek API Key 后使用。");
        }
        try {
            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user", "content", userQuestion)
                    ),
                    "max_tokens", 1024,
                    "temperature", 0.7
            );
            JsonNode root = WebClient.create()
                    .post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block(Duration.ofSeconds(50));
            if (root != null && root.has("choices") && root.get("choices").isArray() && root.get("choices").size() > 0) {
                JsonNode choice = root.get("choices").get(0);
                if (choice.has("message") && choice.get("message").has("content")) {
                    String answer = choice.get("message").get("content").asText();
                    return new AiConsultResponse(answer);
                }
            }
            return new AiConsultResponse("AI 返回格式异常，请稍后再试。");
        } catch (WebClientResponseException e) {
            String hint = "";
            if (e.getStatusCode() != null) {
                if (e.getStatusCode().value() == 401) hint = "API Key 无效或已过期，请检查配置。";
                else if (e.getStatusCode().value() == 429) hint = "请求过于频繁，请稍后再试。";
                else hint = "请检查 API Key 或网络。";
            }
            return new AiConsultResponse("AI 服务调用失败：" + e.getStatusCode() + "，" + hint);
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "请稍后再试。";
            if (msg.contains("Timeout") || msg.contains("timeout")) msg = "请求超时，请稍后再试。";
            return new AiConsultResponse("AI 服务暂时不可用：" + msg);
        }
    }
}
