package com.example.car_rental_project.controller.api.root;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.car_rental_project.model.dto.order.AdminOrderSummaryDto;
import com.example.car_rental_project.service.OrderService;

import response.ApiResponse;

@RestController
@RequestMapping("root/order/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class RootApiOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AdminOrderSummaryDto>>> getAllOrder() {
        List<AdminOrderSummaryDto> orderSummaryDtos = orderService.getAllOrder();

        if (orderSummaryDtos == null || orderSummaryDtos.isEmpty()) {
            return ResponseEntity.ok(
                    ApiResponse.success("沒有訂單", null)); // 或者回傳一個空的 List

        }

        return ResponseEntity.ok(
                ApiResponse.success("獲取所有訂單", orderSummaryDtos));
    }

    @PutMapping("/{orderId}/pickup")
    public ResponseEntity<ApiResponse<String>> pickupCar(@PathVariable("orderId") UUID orderId) {
        try {
            Boolean isPickedUp = orderService.adminPickupCar(orderId);
            if (isPickedUp) {
                return ResponseEntity.ok(ApiResponse.success("取車成功", "車輛已成功取車"));
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "取車失敗"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "取車時發生內部錯誤: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/return")
    public ResponseEntity<ApiResponse<String>> returnCar(@PathVariable("orderId") UUID orderId) {
        try {
            Boolean isReturned = orderService.adminReturnCar(orderId);
            if (isReturned) {
                return ResponseEntity.ok(ApiResponse.success("還車成功", "車輛已成功歸還"));
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "還車失敗"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "還車時發生內部錯誤: " + e.getMessage()));
        }
    }
}
