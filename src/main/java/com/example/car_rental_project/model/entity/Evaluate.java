package com.example.car_rental_project.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "evaluations")
public class Evaluate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 評價ID (主鍵)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 評論者 (用戶)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car; // 被評價的車輛

    @Column(nullable = false)
    private Integer score; // 例如 1 到 5

    @Lob
    @Column(columnDefinition = "TEXT")
    private String comment; // 評價內容

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 評價建立時間

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 評價更新時間

}
