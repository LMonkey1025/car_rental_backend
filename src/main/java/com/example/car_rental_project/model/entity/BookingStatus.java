package com.example.car_rental_project.model.entity;

public enum BookingStatus {
    PENDING_CONFIRMATION, // 待確認
    CONFIRMED, // 已確認
    RENTED_OUT, // 已租出(使用者已取車)
    COMPLETED, // 已完成(使用者已還車)
    CANCELLED // 已取消
}
