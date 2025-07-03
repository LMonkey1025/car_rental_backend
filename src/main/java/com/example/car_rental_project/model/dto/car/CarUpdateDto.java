package com.example.car_rental_project.model.dto.car;

import com.example.car_rental_project.model.entity.CarStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarUpdateDto {

    @NotNull(message = "車輛ID不能為空")
    private Long id; // 車輛ID (更新時必須提供)

    @Size(max = 100, message = "品牌長度不能超過100個字符")
    private String brand; // 品牌

    @Size(max = 100, message = "型號長度不能超過100個字符")
    private String model; // 型號

    @Size(max = 50, message = "車牌號碼長度不能超過50個字符")
    private String licensePlate; // 車牌號碼

    @DecimalMin(value = "0.0", inclusive = false, message = "日租金必須大於0")
    @Digits(integer = 8, fraction = 2, message = "日租金格式不正確")
    private BigDecimal dailyRate; // 日租金

    @Min(value = 1, message = "座位數必須至少為1")
    @Max(value = 50, message = "座位數不能超過50")
    private Integer seats; // 座位數

    private Integer defaultLocationId; // 預設據點ID

    private CarStatus status; // 車輛狀態
}