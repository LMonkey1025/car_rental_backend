package com.example.car_rental_project.model.dto.order;

import com.example.car_rental_project.model.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderSummaryDto {
    private UUID id; // 訂單ID
    private String carBrand; // 車輛品牌
    private String carModel; // 車輛型號
    private String licensePlate; // 車牌號碼
    private LocalDateTime pickupDateTime; // 取車時間
    private LocalDateTime returnDateTime; // 還車時間
    private String pickupLocationName; // 取車地點名稱
    private String returnLocationName; // 還車地點名稱
    private BigDecimal totalAmount; // 總金額
    private BookingStatus status; // 訂單狀態
    private LocalDateTime createdAt; // 訂單建立時間

    // 訂購人資訊
    private UUID userId; // 訂購人ID
    private String userName; // 訂購人名稱
    private String userEmail; // 訂購人電子郵件
}