package com.example.car_rental_project.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.car_rental_project.mapper.UserMapper;
import com.example.car_rental_project.model.dto.user.UserCert;
import com.example.car_rental_project.model.dto.user.UserDto;
import com.example.car_rental_project.model.dto.user.UserRegisterDto;
import com.example.car_rental_project.model.dto.user.UserUpdateAdminDto;
import com.example.car_rental_project.model.dto.user.UserUpdateProfileDto;
import com.example.car_rental_project.model.entity.Order;
import com.example.car_rental_project.model.entity.Role;
import com.example.car_rental_project.model.entity.User;
import com.example.car_rental_project.repository.OrderRepository;
import com.example.car_rental_project.repository.UserRepository;
import com.example.car_rental_project.service.UserService;
import com.example.car_rental_project.util.Hash;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserMapper userMapper;

    /**
     * 註冊用戶
     * 
     * @param userRegisterDto 用戶註冊資訊
     */
    @Override
    @Transactional
    public void registerUser(UserRegisterDto userRegisterDto) {
        // 檢查用戶是否已存在
        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            throw new IllegalArgumentException("信箱已使用，請使用其他電子郵件地址。");
        }

        String passwordSalt = Hash.getSalt(); // 生成密碼鹽
        String passwordHash = Hash.getHash(userRegisterDto.getPassword(), passwordSalt);
        LocalDateTime createdAt = LocalDateTime.now(); // 獲取當前時間
        User user = new User(
                null,
                userRegisterDto.getUserName(),
                userRegisterDto.getEmail(),
                passwordHash,
                passwordSalt,
                userRegisterDto.getPhoneNumber(),
                Role.CUSTOMER, // 預設為 CUSTOMER
                createdAt,
                null, // updatedAt 初始為 null
                false, // enabled 初始為 false，等待郵件驗證
                null, // bookings 初始為 null
                null); // evaluations 初始為 null
        userRepository.save(user); // 儲存用戶到資料庫
    }

    /**
     * 啟動用戶帳號
     * 
     * @param activationCode 啟動碼(也就是UUID)
     */
    @Override
    @Transactional
    public void activateUser(String activationCode) {
        try {
            // 將activationCode轉換為UUID
            UUID userId = UUID.fromString(activationCode);

            // 查找用戶
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("找不到對應的用戶"));

            // 啟用用戶
            user.setEnabled(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

        } catch (IllegalArgumentException e) {
            // 重新拋出 IllegalArgumentException
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("無效的啟動碼: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserInfo(UserCert userCert) {
        // 根據UserCert中的用戶ID獲取用戶信息
        User user = userRepository.findById(userCert.getId())
                .orElseThrow(() -> new IllegalArgumentException("用戶不存在"));
        return userMapper.toDto(user); // 使用 UserMapper 轉換
    }

    @Override
    @Transactional
    public UserDto updateUserProfile(UserCert userCert, UserUpdateProfileDto userUpdateProfileDto) {
        User user = userRepository.findById(userCert.getId())
                .orElseThrow(() -> new IllegalArgumentException("用戶不存在"));

        // 檢查電子郵件是否已存在 (如果它已更改)
        if (!user.getEmail().equals(userUpdateProfileDto.getEmail())
                && userRepository.existsByEmail(userUpdateProfileDto.getEmail())) {
            throw new IllegalArgumentException("電子郵件已被使用");
        }

        user.setUserName(userUpdateProfileDto.getUserName());
        user.setPhoneNumber(userUpdateProfileDto.getPhoneNumber());
        user.setEmail(userUpdateProfileDto.getEmail());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    // --- 後台用戶管理功能實作 ---

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByIdForAdmin(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用戶 ID 不存在: " + userId));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUserByAdmin(UUID userId, UserUpdateAdminDto userUpdateAdminDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用戶 ID 不存在: " + userId));

        if (userUpdateAdminDto.getUserName() != null && !userUpdateAdminDto.getUserName().isEmpty()) {
            user.setUserName(userUpdateAdminDto.getUserName());
        }
        if (userUpdateAdminDto.getPhoneNumber() != null && !userUpdateAdminDto.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(userUpdateAdminDto.getPhoneNumber());
        }
        if (userUpdateAdminDto.getRole() != null) {
            user.setRole(userUpdateAdminDto.getRole());
        }
        if (userUpdateAdminDto.getEnabled() != null) {
            user.setEnabled(userUpdateAdminDto.getEnabled());
        }
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUserByAdmin(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用戶 ID 不存在: " + userId));

        // 檢查用戶是否有相關訂單
        List<Order> orders = orderRepository.findByUser(user);
        if (orders != null && !orders.isEmpty()) {
            throw new IllegalStateException("無法刪除該用戶，因為該用戶尚有關聯的訂單。請先處理訂單或考慮禁用該用戶。");
        }

        userRepository.deleteById(userId);
    }
}
