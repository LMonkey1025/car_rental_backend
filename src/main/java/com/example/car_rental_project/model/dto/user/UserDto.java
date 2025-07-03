package com.example.car_rental_project.model.dto.user;

import java.util.UUID;

import com.example.car_rental_project.model.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//使用者資訊
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private UUID Id;
    private String userName; // 使用者名稱
    private String email; // 郵件
    private String phoneNumber; // 電話號碼
    private Role role; // 角色權限
    private String createdAt; // 帳號建立時間
    private String updatedAt; // 帳號更新時間
    private boolean isEnabled; // 是否啟用帳號

}
