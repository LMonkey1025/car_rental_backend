package com.example.car_rental_project.service.impl;

import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.car_rental_project.repository.UserRepository;

@Service
public class EmailServiceImpl {

    @Autowired
    private UserRepository userRepository;

    /**
     * 發送帳號啟動電子郵件
     * 
     * @param email 用戶的電子郵件地址
     */
    public void sendActivationEmail(String email) {
        // 檢查用戶是否存在
        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("用戶不存在，無法發送啟動郵件。");
        }
        // 使用email獲取UUID
        UUID uuid = userRepository.findByEmail(email).getId(); // 假設User實體有一個UUID作為ID

        // 生成確認連結
        String confirmUrl = "http://localhost:8083/user/api/activate/" + uuid; // 假設啟動連結為此格式
        // 發送電子郵件
        sendEmail(email, confirmUrl);
    }

    // Google應用程式密碼
    // 請參考此篇 https://www.yongxin-design.com/Article/10
    // 請自行產生Google應用程式密碼
    @Value("${google.app.password}")
    String googleAppPassword;

    // 寄件者的電子郵件地址
    @Value("${google.app.from}")
    String from;

    // to: // 收件者的電子郵件地址
    public void sendEmail(String to, String confirmUrl) {
        // 使用 Gmail SMTP 伺服器
        String host = "smtp.gmail.com";

        // 設定屬性
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, googleAppPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("會員註冊確認信");

            // 使用 HTML 格式內容，包含一個美觀的按鈕
            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                    "  <h2>歡迎加入！</h2>" +
                    "  <p>請點擊下方按鈕完成帳號啟用：</p>" +
                    "  <a href='" + confirmUrl
                    + "' style='display: inline-block; padding: 12px 24px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; font-size: 16px;'>啟用帳號</a>"
                    +
                    "  <p style='margin-top: 24px; color: #888;'>如果按鈕無法點擊，請複製以下連結到瀏覽器：<br>" + confirmUrl + "</p>" +
                    "</div>";
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("發送成功: " + to);
        } catch (MessagingException e) {
            System.out.println("發送失敗: " + e.getMessage());
        }
    }

}
