package com.example.car_rental_project.controller.api;

import com.example.car_rental_project.exception.ModerationException;
import com.example.car_rental_project.exception.ResourceNotFoundException;
import com.example.car_rental_project.model.dto.evaluate.CreateEvaluateDto;
import com.example.car_rental_project.model.dto.evaluate.EvaluateDto;
import com.example.car_rental_project.model.dto.user.UserCert; // 引入 UserCert
import com.example.car_rental_project.service.EvaluateService;
import jakarta.servlet.http.HttpSession; // 引入 HttpSession
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.ApiResponse; // 引入 ApiResponse

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/evaluations/api") // 根據用戶的最新路徑
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class EvaluateController {

    private final EvaluateService evaluateService;

    public EvaluateController(EvaluateService evaluateService) {
        this.evaluateService = evaluateService;
    }

    /**
     * 新增評論
     * 
     * @param createEvaluateDto 評論創建DTO
     * @param session           HttpSession
     * @return 創建的評論DTO或錯誤訊息，使用ApiResponse包裝
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createEvaluation(@RequestBody CreateEvaluateDto createEvaluateDto,
            HttpSession session) {
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        if (userCert == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "用戶未登入"));
        }

        try {
            EvaluateDto createdEvaluate = evaluateService.createEvaluation(createEvaluateDto, userCert.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("評論新增成功", createdEvaluate));
        } catch (ModerationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    /**
     * 根據車輛ID獲取評論列表
     * 
     * @param carId 車輛ID
     * @return 評論DTO列表，使用ApiResponse包裝
     */
    @GetMapping("/car/{carId}")
    public ResponseEntity<ApiResponse<List<EvaluateDto>>> getEvaluationsByCarId(@PathVariable Long carId) {
        try {
            List<EvaluateDto> evaluations = evaluateService.getEvaluationsByCarId(carId);
            return ResponseEntity.ok(ApiResponse.success("查詢成功", evaluations));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    /**
     * 根據車輛車牌獲取評論列表
     * 
     * @param licensePlate 車輛車牌
     * @return 評論DTO列表，使用ApiResponse包裝
     */
    @GetMapping("/car/plate/{licensePlate}")
    public ResponseEntity<ApiResponse<List<EvaluateDto>>> getEvaluationsByLicensePlate(
            @PathVariable String licensePlate) {
        try {
            List<EvaluateDto> evaluations = evaluateService.getEvaluationsByLicensePlate(licensePlate);
            return ResponseEntity.ok(ApiResponse.success("查詢成功", evaluations));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    /**
     * (管理員用) 根據用戶ID獲取評論列表
     * TODO: 應加上管理員權限檢查
     * 
     * @param userId  用戶ID (UUID)
     * @param session HttpSession (用於未來可能的權限檢查)
     * @return 評論DTO列表，使用ApiResponse包裝
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<EvaluateDto>>> getEvaluationsByUserIdForAdmin(@PathVariable UUID userId,
            HttpSession session) {
        try {
            List<EvaluateDto> evaluations = evaluateService.getEvaluationsByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("查詢成功", evaluations));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    /**
     * 獲取當前登入用戶的評論列表
     * 
     * @param session HttpSession
     * @return 評論DTO列表，使用ApiResponse包裝
     */
    @GetMapping("/user/me")
    public ResponseEntity<ApiResponse<List<EvaluateDto>>> getCurrentUserEvaluations(HttpSession session) {
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        if (userCert == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "用戶未登入"));
        }

        try {
            List<EvaluateDto> evaluations = evaluateService.getEvaluationsByUserId(userCert.getId());
            return ResponseEntity.ok(ApiResponse.success("查詢成功", evaluations));
        } catch (ResourceNotFoundException e) { // 雖然不太可能發生，但保持健壯性
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "找不到用戶相關評價: " + e.getMessage()));
        }
    }

    /**
     * 根據評價ID獲取特定評價
     *
     * @param evaluateId 評價ID
     * @return 評價資料傳輸對象，使用ApiResponse包裝
     */
    @GetMapping("/{evaluateId}")
    public ResponseEntity<ApiResponse<EvaluateDto>> getEvaluationById(@PathVariable Long evaluateId) {
        try {
            EvaluateDto evaluateDto = evaluateService.getEvaluationById(evaluateId);
            return ResponseEntity.ok(ApiResponse.success("查詢成功", evaluateDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    /**
     * 更新評論
     *
     * @param evaluationId      要更新的評論ID
     * @param updateEvaluateDto 評論更新DTO
     * @param session           HttpSession
     * @return 更新後的評論DTO或錯誤訊息，使用ApiResponse包裝
     */
    @PutMapping("/{evaluationId}")
    public ResponseEntity<ApiResponse<?>> updateEvaluation(@PathVariable Long evaluationId,
            @RequestBody CreateEvaluateDto updateEvaluateDto, // 注意：這裡使用 CreateEvaluateDto 作為請求體
            HttpSession session) {
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        if (userCert == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "用戶未登入"));
        }

        try {
            EvaluateDto updatedEvaluate = evaluateService.updateEvaluation(evaluationId, updateEvaluateDto,
                    userCert.getId());
            return ResponseEntity.ok(ApiResponse.success("評論更新成功", updatedEvaluate));
        } catch (ModerationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
        }
    }

    /**
     * 刪除評論
     * 
     * @param evaluationId 評論ID
     * @param session      HttpSession
     * @return HTTP狀態碼，使用ApiResponse包裝
     */
    @DeleteMapping("/{evaluationId}")
    public ResponseEntity<ApiResponse<String>> deleteEvaluation(@PathVariable Long evaluationId, HttpSession session) {
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        if (userCert == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "用戶未登入"));
        }

        try {
            evaluateService.deleteEvaluation(evaluationId, userCert.getId());
            return ResponseEntity.ok(ApiResponse.success("評論刪除成功", null)); // 或者提供一個具體的成功訊息字串
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
        }
    }
}
