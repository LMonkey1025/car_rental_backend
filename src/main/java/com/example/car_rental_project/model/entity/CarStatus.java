package com.example.car_rental_project.model.entity;

public enum CarStatus {
    AVAILABLE, // 可用 - 可以被預訂
    RESERVED, // 已預訂 - 有確認的訂單但尚未取車
    RENTED, // 租用中 - 已取車但尚未還車
    MAINTENANCE, // 維修中 - 無法使用
    OUT_OF_SERVICE // 停用 - 暫時下架或報廢
}
