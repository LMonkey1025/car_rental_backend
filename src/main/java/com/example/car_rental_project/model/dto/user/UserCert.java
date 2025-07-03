package com.example.car_rental_project.model.dto.user;

import java.util.UUID;

import com.example.car_rental_project.model.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

//要傳給瀏覽器的登入
// 使用者憑證
// 登入成功之後會得到的憑證資料(只有 Getter)
@AllArgsConstructor
@Getter
@ToString
public class UserCert {
    private UUID Id; // 使用者 Id
    private String userName; // 使用者名稱
    private Role role; // 角色權限
}
