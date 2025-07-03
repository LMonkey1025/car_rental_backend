package com.example.car_rental_project.controller.api.root;

import com.example.car_rental_project.model.dto.user.UserDto;
import com.example.car_rental_project.model.dto.user.UserUpdateAdminDto;
import com.example.car_rental_project.service.UserService;

import response.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/root/user/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // 根據您的前端調整
public class RootApiUserController {

    @Autowired
    private UserService userService;

    /**
     * 獲取所有用戶列表 (管理員)
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        try {
            List<UserDto> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success("成功獲取所有用戶", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "獲取用戶列表失敗: " + e.getMessage()));
        }
    }

    /**
     * 根據 ID 獲取用戶資訊 (管理員)
     * 
     * @param userId 用戶 ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByIdForAdmin(@PathVariable UUID userId) {
        try {
            UserDto userDto = userService.getUserByIdForAdmin(userId);
            return ResponseEntity.ok(ApiResponse.success("成功獲取用戶資訊", userDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "獲取用戶資訊失敗: " + e.getMessage()));
        }
    }

    /**
     * 更新用戶資訊 (管理員)
     * 
     * @param userId             用戶 ID
     * @param userUpdateAdminDto 更新資訊
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> updateUserByAdmin(@PathVariable UUID userId,
            @RequestBody UserUpdateAdminDto userUpdateAdminDto) {
        try {
            UserDto updatedUser = userService.updateUserByAdmin(userId, userUpdateAdminDto);
            return ResponseEntity.ok(ApiResponse.success("用戶資訊更新成功", updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "用戶資訊更新失敗: " + e.getMessage()));
        }
    }

    /**
     * 刪除用戶 (管理員)
     * 
     * @param userId 用戶 ID
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUserByAdmin(@PathVariable UUID userId) {
        try {
            userService.deleteUserByAdmin(userId);
            return ResponseEntity.ok(ApiResponse.success("用戶刪除成功", "用戶 ID: " + userId + " 已被刪除"));
        } catch (IllegalArgumentException e) { // 用戶不存在
            return ResponseEntity.status(404).body(ApiResponse.error(404, e.getMessage()));
        } catch (IllegalStateException e) { // 無法刪除 (例如，有關聯訂單)
            return ResponseEntity.status(409).body(ApiResponse.error(409, e.getMessage())); // 409 Conflict
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "用戶刪除失敗: " + e.getMessage()));
        }
    }
}
