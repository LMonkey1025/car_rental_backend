package com.example.car_rental_project.model.entity;

import java.math.BigDecimal;
import java.rmi.registry.LocateRegistry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long id; // 車輛ID (主鍵)

    @Column(name = "brand", nullable = false, length = 100)
    private String brand; // 品牌

    @Column(name = "model", nullable = false, length = 100)
    private String model; // 型號

    @Column(name = "license_plate", unique = true, nullable = false, length = 50)
    private String licensePlate; // 車牌號碼

    @Column(name = "daily_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRate; // 日租金

    @Column(name = "seats")
    private Integer seats; // 座位數

    @Column(name = "image_url", length = 255)
    private String imageUrl; // 車輛圖片連結

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_location_id")
    private Location defaultLocation; // 車輛預設/目前所在據點

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CarStatus status = CarStatus.AVAILABLE; // 車輛自身狀態 預設為 AVAILABLE可租用

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 記錄建立時間

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 記錄更新時間

    // Relationships
    @OneToMany(mappedBy = "car")
    private List<Order> bookings; // 該車輛的所有訂單

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Evaluate> evaluations; // 該車輛的所有評價

}
