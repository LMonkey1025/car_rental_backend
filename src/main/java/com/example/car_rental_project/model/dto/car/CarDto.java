package com.example.car_rental_project.model.dto.car;

import com.example.car_rental_project.model.entity.CarStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 查詢用DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {
    private Long id; // 車輛ID
    private String brand; // 品牌
    private String model; // 型號
    private String licensePlate; // 車牌號碼
    private BigDecimal dailyRate; // 日租金
    private Integer seats; // 座位數
    private String imageUrl; // 車輛圖片連結
    private String defaultLocationName; // 預設據點名稱（用於顯示）
    private CarStatus status; // 車輛狀態
    private LocalDateTime createdAt; // 建立時間
    private LocalDateTime updatedAt; // 更新時間
}