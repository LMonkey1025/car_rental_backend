package com.example.car_rental_project.service;

import com.example.car_rental_project.model.dto.user.UserCert;
import com.example.car_rental_project.model.dto.user.UserDto;
import com.example.car_rental_project.model.dto.user.UserRegisterDto;
import com.example.car_rental_project.model.dto.user.UserUpdateAdminDto;
import com.example.car_rental_project.model.dto.user.UserUpdateProfileDto;

import java.util.List; // 新增導入
import java.util.UUID; // 新增導入

public interface UserService {

    // 註冊方法
    void registerUser(UserRegisterDto userRegisterDto);

    // 帳號啟用
    void activateUser(String activationCode);

    // 持UserCert獲取UserDto
    UserDto getUserInfo(UserCert userCert);

    // 更新使用者個人資訊
    UserDto updateUserProfile(UserCert userCert, UserUpdateProfileDto userUpdateProfileDto);

    // --- 後台用戶管理功能 ---
    /**
     * 獲取所有用戶列表 (供管理員使用)
     * 
     * @return 用戶 DTO 列表
     */
    List<UserDto> getAllUsers();

    /**
     * 根據用戶 ID 獲取用戶資訊 (供管理員使用)
     * 
     * @param userId 用戶 ID
     * @return 用戶 DTO
     */
    UserDto getUserByIdForAdmin(UUID userId);

    /**
     * 更新用戶資訊 (供管理員使用)
     * 
     * @param userId             用戶 ID
     * @param userUpdateAdminDto 用戶更新資訊 DTO
     * @return 更新後的用戶 DTO
     */
    UserDto updateUserByAdmin(UUID userId, UserUpdateAdminDto userUpdateAdminDto);

    /**
     * 刪除用戶 (供管理員使用)
     * 
     * @param userId 用戶 ID
     */
    void deleteUserByAdmin(UUID userId);

}
