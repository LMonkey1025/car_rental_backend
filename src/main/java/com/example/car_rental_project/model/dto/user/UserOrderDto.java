package com.example.car_rental_project.model.dto.user;

import java.util.List;

import com.example.car_rental_project.model.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderDto {
    private List<Order> orders; // 使用者的訂單列表
}
