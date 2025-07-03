package com.example.car_rental_project.service.impl;

import com.example.car_rental_project.exception.ModerationException;
import com.example.car_rental_project.exception.ResourceNotFoundException;
import com.example.car_rental_project.mapper.EvaluateMapper;
import com.example.car_rental_project.model.dto.evaluate.CreateEvaluateDto;
import com.example.car_rental_project.model.dto.evaluate.EvaluateDto;
import com.example.car_rental_project.model.entity.Car;
import com.example.car_rental_project.model.entity.Evaluate;
import com.example.car_rental_project.model.entity.User;
import com.example.car_rental_project.model.entity.Role; // 假設 Role enum 存在於此包或可訪問
import com.example.car_rental_project.repository.CarRepository;
import com.example.car_rental_project.repository.EvaluateRepository;
import com.example.car_rental_project.repository.UserRepository;
import com.example.car_rental_project.service.EvaluateService;
import com.example.car_rental_project.service.ModerationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EvaluateServiceImpl implements EvaluateService {

    @Autowired
    private ModerationService moderationService;
    @Autowired
    private EvaluateRepository evaluateRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private EvaluateMapper evaluateMapper;

    /**
     * 新增評價
     * 
     * @param createEvaluateDto 評價創建資料傳輸對象
     * @param currentUserId     當前登入用戶的ID
     * @return 評價資料傳輸對象
     * @throws ModerationException 如果內容審查失敗
     */
    @Override
    @Transactional
    public EvaluateDto createEvaluation(CreateEvaluateDto createEvaluateDto, UUID currentUserId)
            throws ModerationException {
        // 內容審查
        moderationService.checkForInappropriateContent(createEvaluateDto.getComment());

        User user = userRepository.findById(currentUserId) // 使用 currentUserId 查找用戶
                .orElseThrow(() -> new ResourceNotFoundException("找不到用戶ID: " + currentUserId));

        // 改為使用 licensePlate 查找車輛
        Car car = carRepository.findByLicensePlate(createEvaluateDto.getLicensePlate())
                .orElseThrow(
                        () -> new ResourceNotFoundException("找不到車牌為 " + createEvaluateDto.getLicensePlate() + " 的車輛"));

        Evaluate evaluate = new Evaluate();
        evaluate.setUser(user);
        evaluate.setCar(car);
        evaluate.setScore(createEvaluateDto.getScore()); // DTO 使用 getScore()
        evaluate.setComment(createEvaluateDto.getComment());
        evaluate.setCreatedAt(LocalDateTime.now()); // Entity 使用 setCreatedAt()

        Evaluate savedEvaluate = evaluateRepository.save(evaluate);
        return evaluateMapper.toDto(savedEvaluate);
    }

    /**
     * 獲取特定汽車的所有評價
     * 
     * @param carId 汽車ID
     * 
     * @return 評價資料傳輸對象列表
     */
    @Override
    @Transactional(readOnly = true) // 查詢操作建議加上 readOnly = true
    public List<EvaluateDto> getEvaluationsByCarId(Long carId) {
        // 使用新的 JOIN FETCH 方法
        List<Evaluate> evaluations = evaluateRepository.findByCarIdFetchingAssociations(carId);
        if (evaluations == null || evaluations.isEmpty()) {
            return Collections.emptyList();
        }
        return evaluations.stream()
                .map(evaluateMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 根據車牌號碼獲取特定汽車的所有評價
     *
     * @param licensePlate 車牌號碼
     *
     * @return 評價資料傳輸對象列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<EvaluateDto> getEvaluationsByLicensePlate(String licensePlate) {
        // 直接使用新的 JOIN FETCH 方法
        List<Evaluate> evaluations = evaluateRepository.findByCarLicensePlateFetchingAssociations(licensePlate);
        if (evaluations == null || evaluations.isEmpty()) {
            return Collections.emptyList();
        }
        return evaluations.stream()
                .map(evaluateMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 獲取特定用戶的所有評價
     *
     * @param userId 用戶ID
     *
     * @return 評價資料傳輸對象列表
     */
    @Override
    @Transactional(readOnly = true) // 查詢操作建議加上 readOnly = true
    public List<EvaluateDto> getEvaluationsByUserId(UUID userId) {
        // 使用新的 JOIN FETCH 方法
        List<Evaluate> evaluations = evaluateRepository.findByUserIdFetchingAssociations(userId);
        if (evaluations == null || evaluations.isEmpty()) {
            return Collections.emptyList();
        }
        return evaluations.stream()
                .map(evaluateMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 根據評價ID獲取特定評價
     *
     * @param evaluateId 評價ID
     *
     * @return 評價資料傳輸對象
     */
    @Override
    @Transactional(readOnly = true)
    public EvaluateDto getEvaluationById(Long evaluateId) {
        Evaluate evaluate = evaluateRepository.findById(evaluateId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到具有該ID的評論: " + evaluateId));
        return evaluateMapper.toDto(evaluate);
    }

    /**
     * 刪除評價
     *
     * @param evaluateId 評價ID
     * @param userCert   用戶憑證
     */
    @Override
    @Transactional
    public void deleteEvaluation(Long evaluationId, UUID currentUserId) {
        Evaluate evaluate = evaluateRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到評論ID: " + evaluationId));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到操作用戶ID: " + currentUserId));

        boolean isOwner = evaluate.getUser().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getRole() == Role.ADMIN; // 假設 User 實體有 getRole() 和 Role.ADMIN

        if (!isOwner && !isAdmin) {
            // 應拋出更合適的權限不足例外，例如 AccessDeniedException
            throw new SecurityException("用戶無權限刪除此評論。");
        }

        evaluateRepository.delete(evaluate);
    }

    @Override
    @Transactional
    public EvaluateDto updateEvaluation(Long evaluationId, CreateEvaluateDto updateEvaluateDto, UUID currentUserId)
            throws ModerationException {
        Evaluate evaluate = evaluateRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到評論ID: " + evaluationId));

        userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到操作用戶ID: " + currentUserId));

        // 驗證是否為評論擁有者
        if (!evaluate.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("用戶無權限修改此評論。");
        }

        // 內容審查
        moderationService.checkForInappropriateContent(updateEvaluateDto.getComment());

        // 更新評論內容
        evaluate.setScore(updateEvaluateDto.getScore());
        evaluate.setComment(updateEvaluateDto.getComment());
        evaluate.setUpdatedAt(LocalDateTime.now()); // 假設 Evaluate 實體有 setUpdatedAt 方法

        Evaluate updatedEvaluate = evaluateRepository.save(evaluate);
        return evaluateMapper.toDto(updatedEvaluate);
    }
}
