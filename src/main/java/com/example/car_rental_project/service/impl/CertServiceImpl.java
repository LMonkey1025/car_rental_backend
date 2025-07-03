package com.example.car_rental_project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.car_rental_project.exception.user.CertException;
import com.example.car_rental_project.exception.user.PasswordInvalidException;
import com.example.car_rental_project.exception.user.UserNotFoundException;
import com.example.car_rental_project.exception.user.AccountNotActivatedException;
import com.example.car_rental_project.model.dto.user.UserCert;
import com.example.car_rental_project.model.entity.User;
import com.example.car_rental_project.repository.UserRepository;
import com.example.car_rental_project.service.CertService;
import com.example.car_rental_project.util.Hash;

@Service
public class CertServiceImpl implements CertService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserCert getCert(String email, String password) throws CertException {
        // 1. 是否有此人 (用 email 查詢)
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("查無此人");
        }
        // 2. 檢查帳號是否啟用
        if (!user.isEnabled()) {
            throw new AccountNotActivatedException("帳號尚未啟用");
        }
        // 3. 密碼 hash 比對
        String passwordHash = Hash.getHash(password, user.getPasswordSalt());
        if (!passwordHash.equals(user.getPasswordHash())) {
            throw new PasswordInvalidException("密碼錯誤");
        }
        // 4. 簽發憑證
        UserCert userCert = new UserCert(user.getId(), user.getUserName(), user.getRole());
        return userCert;
    }

}
