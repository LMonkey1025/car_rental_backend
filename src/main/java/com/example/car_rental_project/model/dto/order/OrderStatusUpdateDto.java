package com.example.car_rental_project.model.dto.order;

import java.util.UUID;

import com.example.car_rental_project.model.entity.BookingStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 更新訂單狀態，用於後台管理
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDto {
    @NotNull(message = "訂單ID不能為空")
    private UUID orderId;

    @NotNull(message = "訂單狀態不能為空")
    private BookingStatus status;
}