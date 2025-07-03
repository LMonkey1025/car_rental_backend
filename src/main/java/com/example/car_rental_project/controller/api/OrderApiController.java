package com.example.car_rental_project.controller.api;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.car_rental_project.model.dto.order.OrderSummaryDto;
import com.example.car_rental_project.model.dto.order.UserOrderCreateRequestDto;
import com.example.car_rental_project.model.dto.user.UserCert;
import com.example.car_rental_project.service.OrderService;

import jakarta.servlet.http.HttpSession;
import response.ApiResponse;

@RestController
@RequestMapping("/order/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OrderApiController {
    @Autowired
    private OrderService orderService;

    /**
     * 預約車輛API(OrderService)先將車輛待確認
     * 
     * @param orderCreateRequestDto 包含預約車輛的請求資料
     * @param session               HttpSession
     * 
     * @return ResponseEntity<ApiResponse<OrderSummaryDto>>
     * 
     *         使用POST方法接收預約車輛的請求，並返回預約結果。
     */
    @PostMapping("/order")
    public ResponseEntity<ApiResponse<OrderSummaryDto>> orderCar(
            @RequestBody UserOrderCreateRequestDto userOrderCreateRequestDto,
            HttpSession session) {
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        if (userCert == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "尚未登入"));
        }

        try {
            // 呼叫訂單服務來處理預約邏輯
            OrderSummaryDto orderSummary = orderService.createOrder(userOrderCreateRequestDto, userCert);
            return ResponseEntity.ok(ApiResponse.success("預約成功", orderSummary));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage() +
                            "訂單預約時發生內部錯誤" + userCert));
        }
    }

    /**
     * 確認訂單 使用POST方法接收訂單確認請求，並返回訂單確認結果。
     *
     * @param orderId 訂單的UUID
     * @param session HttpSession
     *
     * @return ResponseEntity<ApiResponse<String>>
     */
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<String>> confirmOrder(@RequestParam("orderId") UUID orderId,
            HttpSession session) {
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        if (userCert == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "尚未登入"));
        }

        try {
            Boolean isConfirmed = orderService.confirmOrder(orderId, userCert);
            if (isConfirmed) {
                return ResponseEntity.ok(ApiResponse.success("訂單確認成功", "訂單已成功確認"));
            } else {
                // 雖然 service 回傳 boolean，但如果確認失敗，service 應該會拋出例外
                // 這裡作為防禦性處理
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "訂單確認失敗"));
            }
        } catch (IllegalArgumentException e) {
            // 處理 service 拋出的業務邏輯錯誤，例如訂單不存在、狀態不正確、無權確認等
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // 使用 BAD_REQUEST 更符合業務邏輯錯誤
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            // 處理其他未知錯誤
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "訂單確認時發生內部錯誤: " + e.getMessage()));
        }
    }

    /**
     * 取得所有歷史訂單
     * 
     * 使用GET方法取得使用者的所有歷史訂單，並返回訂單列表。
     * 
     * @param session HttpSession
     * 
     * @return ResponseEntity<ApiResponse<List<OrderSummaryDto>>>
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderSummaryDto>>> getAllOrdersByUser(HttpSession session) {
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        if (userCert == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "尚未登入"));
        }
        try {
            List<OrderSummaryDto> orderSummaries = orderService.getAllOrdersByUser(userCert);
            if (orderSummaries.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "沒有歷史訂單"));
            }
            return ResponseEntity.ok(ApiResponse.success("取得歷史訂單成功", orderSummaries));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
        }
    }

    /**
     * 取得訂單詳情
     * 
     * 使用GET方法取得指定訂單的詳情，並返回訂單詳情。
     * 
     * @param orderId 訂單的UUID
     * @param session HttpSession
     * 
     * @return ResponseEntity<ApiResponse<OrderSummaryDto>>
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<OrderSummaryDto>> getOrderDetails(
            @PathVariable("orderId") UUID orderId,
            HttpSession session) {
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        if (userCert == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "尚未登入"));
        }

        try {
            OrderSummaryDto orderSummaryDto = orderService.getOrderDetails(orderId);
            if (orderSummaryDto == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "找不到該訂單"));
            }
            // 這裡可以加入權限檢查，確保用戶只能看到自己的訂單
            return ResponseEntity.ok(ApiResponse.success("成功取得訂單詳情", orderSummaryDto));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "取得訂單詳情時發生內部錯誤: " + e.getMessage()));
        }
    }
}
