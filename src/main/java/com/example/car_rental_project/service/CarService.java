package com.example.car_rental_project.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.car_rental_project.exception.user.CarException;
import com.example.car_rental_project.model.dto.car.CarAddDto;
import com.example.car_rental_project.model.dto.car.CarDto;
import com.example.car_rental_project.model.dto.car.CarUpdateDto;
import com.example.car_rental_project.model.dto.order.OrderSearchCriteriaDto;

public interface CarService {
    // 新增車輛
    void addCar(CarAddDto carAddDto, MultipartFile imageFile) throws CarException;

    // 更新車輛
    void updateCar(CarUpdateDto carUpdateDto, MultipartFile imageFile) throws CarException;

    // 刪除車輛
    void deleteCar(Long id) throws CarException;

    // 查詢所有車輛
    List<CarDto> getAllCars();

    // 根據ID查詢車輛
    CarDto getCarById(Long id) throws CarException;

    // 根據車牌號碼查詢車輛
    CarDto getCarByLicensePlate(String licensePlate) throws CarException;

    // 查詢可用車輛(僅檢查車輛狀態，不考慮時間衝突)
    List<CarDto> getAvailableCars() throws CarException;

    // 根據租車地點查詢可用車輛(僅檢查車輛狀態和地點)
    List<CarDto> getAvailableCarsByLocation(Long locationId) throws CarException;

    /**
     * 根據搜尋條件查詢可用車輛(包含時間衝突檢查)
     * 
     * @param orderCreateRequestDto 搜尋條件（取車地點、還車地點、取車時間、還車時間）
     * @return 可用車輛列表
     */
    List<CarDto> searchAvailableCars(OrderSearchCriteriaDto orderSearchCriteriaDto) throws CarException;
}
