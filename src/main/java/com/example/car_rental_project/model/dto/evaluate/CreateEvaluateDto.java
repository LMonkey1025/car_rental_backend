package com.example.car_rental_project.model.dto.evaluate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEvaluateDto {

    @NotNull(message = "User ID cannot be null")
    private UUID userId; // 評價者的用戶ID

    @NotBlank(message = "License plate cannot be blank")
    private String licensePlate; // 被評價的車輛車牌

    @NotNull(message = "Score cannot be null")
    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 5, message = "Score must be at most 5")
    private Integer score; // 評分數值 1-5

    @NotBlank(message = "Comment cannot be blank")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment; // 評價內容，最大長度1000字元
}
