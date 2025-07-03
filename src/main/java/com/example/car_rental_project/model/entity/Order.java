package com.example.car_rental_project.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID id; // 訂單ID (主鍵)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 預訂使用者 (可為null代表訪客訂單)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car; // 預訂車輛

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_location_id", nullable = false)
    private Location pickupLocation; // 取車地點

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_location_id", nullable = false)
    private Location returnLocation; // 還車地點

    @Column(name = "pickup_datetime", nullable = false)
    private LocalDateTime pickupDateTime; // 取車日期與時間

    @Column(name = "return_datetime", nullable = false)
    private LocalDateTime returnDateTime; // 還車日期與時間

    @Column(name = "customer_name", nullable = false, length = 255)
    private String customerName; // 承租人姓名 (訂單建立時的使用者名稱)

    @Column(name = "customer_phone", nullable = false, length = 50)
    private String customerPhone; // 承租人電話 (訂單建立時的電話)

    @Column(name = "customer_email", nullable = false, length = 255)
    private String customerEmail; // 承租人Email (訂單建立時的email)

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount; // 訂單總金額

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.PENDING_CONFIRMATION; // 訂單狀態

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 訂單建立時間

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 訂單更新時間
}