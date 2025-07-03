package com.example.car_rental_project.mapper;

import com.example.car_rental_project.model.dto.evaluate.EvaluateDto;
import com.example.car_rental_project.model.entity.Evaluate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EvaluateMapper {

    private final ModelMapper modelMapper;

    public EvaluateMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public EvaluateDto toDto(Evaluate evaluate) {
        if (evaluate == null) {
            return null;
        }
        EvaluateDto dto = modelMapper.map(evaluate, EvaluateDto.class);
        if (evaluate.getUser() != null) {
            dto.setUserId(evaluate.getUser().getId());
            dto.setUserName(evaluate.getUser().getUserName());
        }
        if (evaluate.getCar() != null) {
            dto.setCarId(evaluate.getCar().getId());
            dto.setCarBrand(evaluate.getCar().getBrand());
            dto.setCarModel(evaluate.getCar().getModel());
            dto.setLicensePlate(evaluate.getCar().getLicensePlate()); // 新增: 設定車牌
        }
        return dto;
    }

}
