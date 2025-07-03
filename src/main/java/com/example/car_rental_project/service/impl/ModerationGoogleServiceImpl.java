package com.example.car_rental_project.service.impl;

import com.example.car_rental_project.exception.ModerationException;
import com.example.car_rental_project.service.ModerationService;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ModerationGoogleServiceImpl implements ModerationService {
    private static final String MODERATION_SYSTEM_PROMPT = "你是內容審查員。判斷以下文字是否包含髒話、攻擊或不當內容。" +
            "如果包含，回答'不當內容'。如果不包含，回答'內容合適'。" +
            "不要添加其他解釋。";

    @Value("${GOOGLE_API_KEY}")
    private String googleApiKey; // 假設你有一個Google API Key需要

    /**
     * 檢查內容是否包含不當內容
     * 
     * @param content 要檢查的內容
     * @throws ModerationException 如果內容包含不當內容或審核過
     * @throws ModerationException 如果審核過程中發生錯誤
     * @throws ModerationException 如果AI服務未正確配置
     * @throws ModerationException 如果AI服務沒有回應
     * @throws ModerationException 如果AI返回其他的回應
     */
    @Override
    public void checkForInappropriateContent(String content) throws ModerationException {
        if (content == null || content.trim().isEmpty()) {
            return;
        }

        try {

            Client client = Client
                    .builder()
                    .apiKey(googleApiKey)
                    .build();

            Content text = Content.fromParts(Part.fromText(MODERATION_SYSTEM_PROMPT),
                    Part.fromText(content));

            GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", text, null);
            String responseMessage = response.text();

            if (responseMessage == null || responseMessage.trim().isEmpty()) {
                throw new ModerationException("AI服務沒有回應");
            }

            if ("不當內容".equals(responseMessage.trim())) {
                throw new ModerationException("文字含有不當內容");
            } else if (!"內容合適".equals(responseMessage.trim())) {
                throw new ModerationException("AI返回其他的回應: " + response);
            }

        } catch (ModerationException me) {
            throw me;
        } catch (Exception e) {
            throw new ModerationException("因內部錯誤未能對內容進行審核: " + e.getMessage(), e);
        }
    }
}
