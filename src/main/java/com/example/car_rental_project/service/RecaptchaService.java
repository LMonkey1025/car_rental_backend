package com.example.car_rental_project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate(); // 用於發送 HTTP 請求
    private final ObjectMapper objectMapper = new ObjectMapper(); // 用於處理 JSON 數據

    public boolean verifyRecaptcha(String token) {
        try {
            String url = "https://www.google.com/recaptcha/api/siteverify";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); // 使用 MultiValueMap 來構建 POST 請求的參數
            params.add("secret", secretKey); // reCAPTCHA 密鑰，這是從配置文件中獲取的
            params.add("response", token); // reCAPTCHA token，這是從前端獲取的用戶驗證令牌

            String response = restTemplate.postForObject(url, params, String.class); // 發送 POST 請求到 reCAPTCHA 驗證 API
            JsonNode jsonNode = objectMapper.readTree(response); // 將響應字符串轉換為 JsonNode 以便於處理

            boolean success = jsonNode.get("success").asBoolean(); // 檢查 "success" 欄位以確定驗證是否成功

            if (success) {
                System.out.println("reCAPTCHA 驗證成功");
            } else {
                System.out.println("reCAPTCHA 驗證失敗: " + response);
            }

            return success;

        } catch (Exception e) {
            System.err.println("reCAPTCHA 驗證過程發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}