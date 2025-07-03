package com.example.car_rental_project.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.car_rental_project.model.entity.Car;
import com.example.car_rental_project.model.entity.CarStatus;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

        // 檢查是否存在指定車牌號碼的車輛
        boolean existsByLicensePlate(String licensePlate);

        // 根據車牌號碼查詢車輛
        Optional<Car> findByLicensePlate(String licensePlate); // 改為返回 Optional<Car> 以便更好地處理找不到的情況

        // 根據狀態查詢車輛
        List<Car> findByStatus(CarStatus status);

        // 根據狀態和據點ID查詢車輛
        @Query("SELECT c FROM Car c WHERE c.status = :status AND c.defaultLocation.id = :locationId")
        List<Car> findByStatusAndLocationId(@Param("status") CarStatus status, @Param("locationId") Long locationId);

        // 查詢在指定時間段內可用的車輛（沒有時間衝突的訂單）
        @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE' AND NOT EXISTS " +
                        "(SELECT 1 FROM Order o WHERE o.car.id = c.id AND o.status IN ('CONFIRMED', 'RENTED') " +
                        "AND o.pickupDateTime < :endTime AND o.returnDateTime > :startTime)")
        List<Car> findAvailableCarsInTimeRange(@Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

        // 結合據點和時間範圍的查詢
        @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE' " +
                        "AND c.defaultLocation.id = :locationId " +
                        "AND NOT EXISTS " +
                        "(SELECT 1 FROM Order o WHERE o.car.id = c.id AND o.status IN ('CONFIRMED', 'RENTED') " +
                        "AND o.pickupDateTime < :endTime AND o.returnDateTime > :startTime)")
        List<Car> findAvailableCarsInTimeRangeAndLocation(@Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime,
                        @Param("locationId") Long locationId);

        // 查詢位於指定據點的所有可用車輛
        @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE' AND c.defaultLocation.id = :locationId")
        List<Car> findAvailableCarsByLocationId(@Param("locationId") Long locationId);

        // 根據車牌號碼查詢車輛及其據點信息
        @Query("SELECT c FROM Car c LEFT JOIN FETCH c.defaultLocation WHERE c.licensePlate = :licensePlate")
        Optional<Car> findByLicensePlateWithLocation(@Param("licensePlate") String licensePlate);

        // 查詢所有車輛並同時載入 defaultLocation 避免 LazyInitializationException
        @Query("SELECT c FROM Car c LEFT JOIN FETCH c.defaultLocation")
        List<Car> findAllWithLocation();
}