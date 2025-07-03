package com.example.car_rental_project.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//租車地點
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id; // 地點ID (主鍵)

    @Column(name = "name", unique = true, nullable = false, length = 255)
    private String name; // 地點名稱

    @Column(name = "address", columnDefinition = "TEXT")
    private String address; // 詳細地址

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 記錄建立時間

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 記錄更新時間

    // Relationships
    @OneToMany(mappedBy = "defaultLocation")
    private List<Car> carsAtLocation; // 在此地點的車輛 (作為預設地點)

    @OneToMany(mappedBy = "pickupLocation")
    private List<Order> pickupsFromLocation; // 從此地點取車的訂單

    @OneToMany(mappedBy = "returnLocation")
    private List<Order> returnsToLocation; // 在此地點還車的訂單
}