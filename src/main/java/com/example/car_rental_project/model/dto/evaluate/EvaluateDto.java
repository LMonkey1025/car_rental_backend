package com.example.car_rental_project.model.dto.evaluate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateDto {
    private Long id;
    private UUID userId;
    private String userName;
    private Long carId;
    private String carBrand;
    private String carModel;
    private String licensePlate; // 新增車牌欄位
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
}
