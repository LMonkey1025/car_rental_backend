package com.example.car_rental_project.model.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationDto {
    private Long id; // 地點 ID
    private String name; // 地點名稱
    private String address; // 地址
}
