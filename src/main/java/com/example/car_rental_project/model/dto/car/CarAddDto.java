package com.example.car_rental_project.model.dto.car;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarAddDto {

    @NotBlank(message = "品牌不能為空")
    @Size(max = 100, message = "品牌長度不能超過100個字符")
    private String brand; // 品牌

    @NotBlank(message = "型號不能為空")
    @Size(max = 100, message = "型號長度不能超過100個字符")
    private String model; // 型號

    @NotBlank(message = "車牌號碼不能為空")
    @Size(max = 50, message = "車牌號碼長度不能超過50個字符")
    private String licensePlate; // 車牌號碼

    @NotNull(message = "日租金不能為空")
    @DecimalMin(value = "0.0", inclusive = false, message = "日租金必須大於0")
    @Digits(integer = 8, fraction = 2, message = "日租金格式不正確")
    private BigDecimal dailyRate; // 日租金

    @NotNull(message = "座位數不能為空")
    @Min(value = 1, message = "座位數必須至少為1")
    @Max(value = 50, message = "座位數不能超過50")
    private Integer seats; // 座位數

    @NotNull(message = "預設據點ID不能為空")
    private Long defaultLocationId; // 預設據點ID (改為 Long 類型)
}