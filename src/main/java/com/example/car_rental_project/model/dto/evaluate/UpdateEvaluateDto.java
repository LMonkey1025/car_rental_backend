package com.example.car_rental_project.model.dto.evaluate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEvaluateDto {

    @NotNull(message = "評分不能為空")
    @Min(value = 1, message = "評分最低為1")
    @Max(value = 5, message = "評分最高為5")
    private Integer score;

    @NotBlank(message = "評論內容不能為空")
    private String comment;
}
