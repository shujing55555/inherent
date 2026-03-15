package com.example.demo.dto;

/**
 * AI 咨询响应 DTO
 */
public class AiConsultResponse {

    private String answer;

    public AiConsultResponse() {}
    public AiConsultResponse(String answer) {
        this.answer = answer;
    }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
