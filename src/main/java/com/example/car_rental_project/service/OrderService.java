package com.example.car_rental_project.service;

import java.util.List;
import java.util.UUID;

import com.example.car_rental_project.model.dto.order.*; // 保持通用導入
import com.example.car_rental_project.model.dto.user.UserCert;

public interface OrderService {
    OrderSummaryDto createOrder(UserOrderCreateRequestDto userOrderCreateRequestDto, UserCert userCert)
            throws Exception; // 創建訂單

    Boolean confirmOrder(UUID orderId, UserCert userCert); // 確認訂單

    OrderSummaryDto getOrderDetails(UUID orderId); // 獲取訂單詳情 (這個可以考慮是否也需要一個 AdminOrderDetailDto)

    List<OrderSummaryDto> getAllOrdersByUser(UserCert userCert); // 獲取用戶的所有訂單

    List<AdminOrderSummaryDto> getAllOrder(); // 獲取所有訂單 (更改回傳類型)

    Boolean adminPickupCar(UUID orderId);

    Boolean adminReturnCar(UUID orderId);
}
