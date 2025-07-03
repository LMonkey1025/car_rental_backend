package com.example.car_rental_project.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.car_rental_project.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // 使用信箱確認帳號是否已經存在
    boolean existsByEmail(String email);

    // 變更帳號啟動
    @Modifying
    @Query("UPDATE User u SET u.isEnabled = true WHERE u.id = :userId")
    void updateUserActiveStatus(@Param("userId") UUID userId);

    // 根據 User 實體中的 email 屬性查詢
    User findByEmail(String email); // 將 findByUserEmail 修改為 findByEmail
}
