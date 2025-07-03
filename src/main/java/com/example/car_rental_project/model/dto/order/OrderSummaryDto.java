package com.example.car_rental_project.model.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.car_rental_project.model.entity.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

//這個Dto用於返回訂單摘要信息，用在訂單列表或概覽頁面使用
@Data
@AllArgsConstructor
public class OrderSummaryDto {
    private UUID id;
    private String carBrand;
    private String carModel;
    private String licensePlate; // 新增車牌號碼欄位
    private LocalDateTime pickupDateTime;
    private LocalDateTime returnDateTime;
    private String pickupLocationName;
    private String returnLocationName;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private LocalDateTime createdAt;
}