package com.example.car_rental_project.model.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID; // 新增 import

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", unique = true, nullable = false)
    private UUID id; // 將 Long 改為 UUID

    @Column(name = "username", unique = true, nullable = false, length = 255)
    private String userName; // 用戶名/顯示名稱

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email; // 電子郵件 (也作為登入帳號)

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash; // 加密後的密碼 (已加鹽)

    @Column(name = "password_salt", nullable = false, length = 128)
    private String passwordSalt; // 用於密碼哈希的鹽值

    @Column(name = "phone_number", length = 50)
    private String phoneNumber; // 聯絡電話

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.CUSTOMER; // 角色

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 帳號建立時間

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 帳號更新時間

    @Column(name = "enabled", nullable = false)
    private boolean isEnabled = false; // 帳號是否啟用

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> bookings; // 該使用者的所有訂單

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Evaluate> evaluations; // 該使用者的所有評價
}