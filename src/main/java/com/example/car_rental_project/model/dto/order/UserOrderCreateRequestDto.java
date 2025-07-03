package com.example.car_rental_project.model.dto.order;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderCreateRequestDto {
    @NotNull(message = "車輛ID不能為空")
    private Long carId; // 車輛ID

    @NotNull(message = "取車地點ID不能為空")
    private Long pickupLocationId; // 取車地點ID

    @NotNull(message = "還車地點ID不能為空")
    private Long returnLocationId; // 還車地點ID

    @NotNull(message = "取車時間不能為空")
    private LocalDateTime pickupDateTime; // 取車時間

    @NotNull(message = "還車時間不能為空")
    private LocalDateTime returnDateTime; // 還車時間

}
