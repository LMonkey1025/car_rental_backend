package com.example.car_rental_project.model.dto.user;

import lombok.Data;

@Data
public class UserUpdateProfileDto {
    private String userName;
    private String phoneNumber;
    private String email;
}
