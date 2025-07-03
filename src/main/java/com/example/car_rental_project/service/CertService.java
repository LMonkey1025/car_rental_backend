package com.example.car_rental_project.service;

import com.example.car_rental_project.exception.user.CertException;
import com.example.car_rental_project.model.dto.user.UserCert;

public interface CertService {
    // 取得使用者憑證
    UserCert getCert(String email, String password) throws CertException;
}
