package com.example.car_rental_project.model.dto.order;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 車輛搜尋條件，用於首頁搜尋表單
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchCriteriaDto {
    private Long pickupLocationId; // 取車地點ID
    private Long returnLocationId; // 還車地點ID
    private LocalDateTime pickupDateTime; // 取車時間
    private LocalDateTime returnDateTime; // 還車時間
}