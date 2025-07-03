package com.example.car_rental_project.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.car_rental_project.model.dto.user.UserDto;
import com.example.car_rental_project.model.entity.User;
import com.example.car_rental_project.repository.UserRepository;

//用來將資料庫的資料轉換成DTO物件
@Component // 此物件由 Springboot 來管理
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper; // 自動注入 ModelMapper 物件ModelMapper 是一個用來轉換物件的工具類別
    @Autowired
    private UserRepository userRepository; // 用來查詢資料庫的 UserRepository

    public UserDto toDto(User user) {

        return modelMapper.map(user, UserDto.class); // 將 User
    }

    public User toEntity(UserDto userDto) {

        // 將 UserDto 轉換成 User
        if (userDto == null) {
            return null; // 如果 userDto 為 null，則返回 null
        }

        // TODO: This method is highly inefficient due to multiple DB calls. Consider
        // refactoring.
        User existingUser = userRepository.findById(userDto.getId()).orElse(null);

        return new User(
                userDto.getId(),
                userDto.getUserName(),
                userDto.getEmail(),
                existingUser != null ? existingUser.getPasswordHash() : null, // 從資料庫中查詢密碼
                existingUser != null ? existingUser.getPasswordSalt() : null, // 從資料庫中查詢密碼鹽
                userDto.getPhoneNumber(),
                existingUser != null ? existingUser.getRole() : null, // 從資料庫中查詢角色
                existingUser != null ? existingUser.getCreatedAt() : null, // 從資料庫中查詢創建時間
                existingUser != null ? existingUser.getUpdatedAt() : null, // 從資料庫中查詢更新時間
                existingUser != null ? existingUser.isEnabled() : false, // 從資料庫中查詢是否啟用
                existingUser != null ? existingUser.getBookings() : null, // 從資料庫中查詢訂單
                existingUser != null ? existingUser.getEvaluations() : null); // 從資料庫中查詢評價 (changed from getEvaluates)
    }
}
