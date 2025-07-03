package com.example.car_rental_project.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.car_rental_project.model.dto.order.OrderSummaryDto;
import com.example.car_rental_project.model.entity.Order;
import com.example.car_rental_project.model.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
        /**
         * 檢查車輛在指定時間段是否有衝突的預訂
         * 排除已取消或已完成的訂單
         */
        @Query("SELECT COUNT(o) FROM Order o WHERE o.car.id = :carId AND " +
                        "o.status NOT IN ('CANCELLED', 'COMPLETED') AND " +
                        "NOT (o.returnDateTime <= :pickupDateTime OR o.pickupDateTime >= :returnDateTime)")
        long countConflictingOrders(@Param("carId") Long carId,
                        @Param("pickupDateTime") LocalDateTime pickupDateTime,
                        @Param("returnDateTime") LocalDateTime returnDateTime);

        List<Order> findByUser(User user);

        @Query("SELECT new com.example.car_rental_project.model.dto.order.OrderSummaryDto("
                        + "o.id, c.brand, c.model, c.licensePlate, o.pickupDateTime, o.returnDateTime, pl.name, rl.name, o.totalAmount, o.status, o.createdAt) "
                        + "FROM Order o "
                        + "JOIN o.car c "
                        + "JOIN o.pickupLocation pl "
                        + "JOIN o.returnLocation rl "
                        + "WHERE o.user = :user "
                        + "ORDER BY o.createdAt DESC")
        List<OrderSummaryDto> findOrderHistoryByUser(@Param("user") User user);

        /**
         * 尋找與指定時間段衝突的訂單
         * 排除已取消或已完成的訂單
         */
        @Query("SELECT o FROM Order o WHERE o.car.id = :carId AND " +
                        "o.status NOT IN ('CANCELLED', 'COMPLETED') AND " +
                        "((o.pickupDateTime < :returnDateTime AND o.returnDateTime > :pickupDateTime))")
        List<Order> findConflictingOrders(
                        @Param("carId") Long carId,
                        @Param("pickupDateTime") LocalDateTime pickupDateTime,
                        @Param("returnDateTime") LocalDateTime returnDateTime);
}
