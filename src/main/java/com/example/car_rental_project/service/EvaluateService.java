package com.example.car_rental_project.service;

import com.example.car_rental_project.exception.ModerationException; // 引入 ModerationException
import com.example.car_rental_project.model.dto.evaluate.CreateEvaluateDto;
import com.example.car_rental_project.model.dto.evaluate.EvaluateDto;

import java.util.List;
import java.util.UUID;

public interface EvaluateService {

    /**
     * 新增評論
     * 
     * @param createEvaluateDto 評論創建DTO (不強制要求包含userId，將由currentUserId提供)
     * @param currentUserId     當前登入用戶的ID
     * @return 創建的評論DTO
     * @throws ModerationException 如果內容審查失敗
     */
    EvaluateDto createEvaluation(CreateEvaluateDto createEvaluateDto, UUID currentUserId) throws ModerationException; // 新增
                                                                                                                      // currentUserId
                                                                                                                      // 參數

    /**
     * 根據車輛ID獲取評論列表
     * 
     * @param carId 車輛ID
     * @return 評論DTO列表
     */
    List<EvaluateDto> getEvaluationsByCarId(Long carId);

    /**
     * 根據車輛牌照獲取評論列表
     * 
     * @param licensePlate 車輛牌照
     * @return 評論DTO列表
     */
    List<EvaluateDto> getEvaluationsByLicensePlate(String licensePlate); // 新增方法

    /**
     * 根據用戶ID獲取評論列表
     * 
     * @param userId 用戶ID (UUID)
     * @return 評論DTO列表
     */
    List<EvaluateDto> getEvaluationsByUserId(UUID userId);

    /**
     * 根據評價ID獲取特定評價
     *
     * @param evaluateId 評價ID
     *
     * @return 評價資料傳輸對象
     */
    EvaluateDto getEvaluationById(Long evaluateId); // 新增 getEvaluationById 方法定義

    /**
     * 更新評論
     *
     * @param evaluationId      要更新的評論ID
     * @param updateEvaluateDto 評論更新DTO
     * @param currentUserId     當前登入用戶的ID
     * @return 更新後的評論DTO
     * @throws ModerationException 如果內容審查失敗
     */
    EvaluateDto updateEvaluation(Long evaluationId, CreateEvaluateDto updateEvaluateDto, UUID currentUserId)
            throws ModerationException;

    /**
     * 刪除評論
     * 
     * @param evaluationId  評論ID
     * @param currentUserId 當前操作用戶的ID (用於權限檢查)
     */
    void deleteEvaluation(Long evaluationId, UUID currentUserId);
}
