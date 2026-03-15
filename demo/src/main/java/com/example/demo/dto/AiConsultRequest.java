package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * AI 咨询请求 DTO
 */
public class AiConsultRequest {

    @NotBlank(message = "提问内容不能为空")
    private String question;

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}
