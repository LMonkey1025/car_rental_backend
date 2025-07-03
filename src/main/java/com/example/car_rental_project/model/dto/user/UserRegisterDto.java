package com.example.car_rental_project.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

//提供給使用者註冊的資料傳輸物件
@Data
@AllArgsConstructor
public class UserRegisterDto {
    private String userName; // 使用者名稱
    private String email; // 郵件
    private String password; // 密碼
    private String phoneNumber; // 電話號碼
}
