package com.example.car_rental_project.service.impl;

import com.example.car_rental_project.exception.ModerationException;
import com.example.car_rental_project.service.ModerationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

// @Service
public class ModerationServiceImpl implements ModerationService {
    @Autowired
    private ChatClient chatClient;

    private static final String MODERATION_SYSTEM_PROMPT = "你是內容審查員。判斷以下文字是否包含髒話、攻擊或不當內容。" +
            "如果包含，回答'不當內容'。如果不包含，回答'內容合適'。" +
            "不要添加其他解釋。";

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
            SystemMessage systemMessage = new SystemMessage(MODERATION_SYSTEM_PROMPT); // 使用常量定義的系統提示
            UserMessage userMessage = new UserMessage(content); // 要審核的用戶消息
            Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

            if (this.chatClient == null) {
                throw new ModerationException("AI服務未正確配置 (ChatClient 為 null)。");
            }

            String response = this.chatClient.prompt(prompt).call().content();

            // 這裡.trim()是為了去除可能的空格或換行符號
            if (response == null || response.trim().isEmpty()) {
                throw new ModerationException("AI服務沒有回應");
            }

            if ("不當內容".equals(response.trim())) {
                throw new ModerationException("文字含有不當內容");
            } else if (!"內容合適".equals(response.trim())) {
                throw new ModerationException("AI返回其他的回應: " + response);
            }

        } catch (ModerationException me) {
            throw me;
        } catch (Exception e) {
            throw new ModerationException("因內部錯誤未能對內容進行審核: " + e.getMessage(), e);
        }
    }
}
