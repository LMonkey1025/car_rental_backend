package com.example.car_rental_project.model.dto.user;

import com.example.car_rental_project.model.entity.Role;
import lombok.Data;

@Data
public class UserUpdateAdminDto {
    private String userName;
    private String phoneNumber;
    private Role role;
    private Boolean enabled;
}