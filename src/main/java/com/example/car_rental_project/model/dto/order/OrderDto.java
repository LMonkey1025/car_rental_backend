package com.example.car_rental_project.model.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.car_rental_project.model.entity.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//完整的訂單資訊傳輸，包含關聯實體的詳細資訊
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID id;
    private Long userId;
    private String userName;
    private Long carId;
    private String carBrand;
    private String carModel;
    private String carPlateNumber;
    private String carImageUrl;
    private BigDecimal carDailyRate;
    private Long pickupLocationId;
    private String pickupLocationName;
    private Long returnLocationId;
    private String returnLocationName;
    private LocalDateTime pickupDateTime;
    private LocalDateTime returnDateTime;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
