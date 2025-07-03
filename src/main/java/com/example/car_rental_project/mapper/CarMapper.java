package com.example.car_rental_project.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.car_rental_project.model.dto.car.CarDto;
import com.example.car_rental_project.model.entity.Car;

@Component
public class CarMapper {
    @Autowired
    private ModelMapper modelMapper;

    // 將 Car 物件轉換成 CarDto
    public CarDto toDto(Car car) {
        if (car == null) {
            return null;
        }

        // 完全手動映射，避免 ModelMapper 的自動映射問題
        CarDto carDto = new CarDto();

        carDto.setId(car.getId());
        carDto.setBrand(car.getBrand());
        carDto.setModel(car.getModel());
        carDto.setLicensePlate(car.getLicensePlate());
        carDto.setDailyRate(car.getDailyRate());
        carDto.setSeats(car.getSeats());
        carDto.setImageUrl(car.getImageUrl());
        carDto.setStatus(car.getStatus());
        carDto.setCreatedAt(car.getCreatedAt());
        carDto.setUpdatedAt(car.getUpdatedAt());

        // 手動設置 defaultLocationName
        if (car.getDefaultLocation() != null) {
            carDto.setDefaultLocationName(car.getDefaultLocation().getName());
        }

        return carDto;
    }
}
