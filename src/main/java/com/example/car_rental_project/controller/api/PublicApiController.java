package com.example.car_rental_project.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.car_rental_project.exception.user.CarException;
import com.example.car_rental_project.model.dto.car.CarDto;
import com.example.car_rental_project.model.dto.order.OrderSearchCriteriaDto;
import com.example.car_rental_project.service.CarService;

import response.ApiResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/public/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PublicApiController {
    @Autowired
    private CarService carService;

    /**
     * 指定租車還車時間和地點查詢可用車輛
     * 
     * 使用POST方法接收租車還車及租借時間的查詢請求，並返回符合條件的車輛列表。
     * 
     * @param OrderSearchCriteriaDto 包含租車還車地點和時間的請求資料
     * 
     * @return ResponseEntity<ApiResponse<List<CarDto>>>
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<CarDto>>> searchCarLisr(
            @RequestBody OrderSearchCriteriaDto orderSearchCriteriaDto) {
        try {
            List<CarDto> carDtos = carService.searchAvailableCars(orderSearchCriteriaDto);
            if (carDtos.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "沒有符合條件的車輛"));
            }
            return ResponseEntity.ok(ApiResponse.success("查詢成功", carDtos));
        } catch (CarException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
        }
    }

}
