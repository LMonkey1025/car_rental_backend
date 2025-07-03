package com.example.car_rental_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.car_rental_project.model.entity.Evaluate;
import com.example.car_rental_project.model.entity.User;
import com.example.car_rental_project.model.entity.Car;

import java.util.List;
import java.util.UUID;

public interface EvaluateRepository extends JpaRepository<Evaluate, Long> {

    List<Evaluate> findByCar(Car car);

    List<Evaluate> findByUser(User user);

    List<Evaluate> findByCarId(Long carId);

    List<Evaluate> findByUserId(UUID userId);

    /**
     * 根據用戶ID查詢評論，並同時載入關聯的 Car 和 User 實體。
     * - Car 實體中的 defaultLocation 也一併載入。
     * 
     * @param userId 用戶ID
     * @return 包含完整關聯數據的評論列表
     */
    @Query("SELECT e FROM Evaluate e " +
            "LEFT JOIN FETCH e.user u " +
            "LEFT JOIN FETCH e.car c " +
            "LEFT JOIN FETCH c.defaultLocation cl " +
            "WHERE e.user.id = :userId")
    List<Evaluate> findByUserIdFetchingAssociations(@Param("userId") UUID userId);

    /**
     * 根據車牌號碼查詢評論，並同時載入關聯的 Car 和 User 實體。
     * 
     * @param licensePlate 車牌號碼
     * @return 包含完整關聯數據的評論列表
     */
    @Query("SELECT e FROM Evaluate e " +
            "LEFT JOIN FETCH e.user u " +
            "LEFT JOIN FETCH e.car c " +
            "LEFT JOIN FETCH c.defaultLocation cl " +
            "WHERE e.car.licensePlate = :licensePlate")
    List<Evaluate> findByCarLicensePlateFetchingAssociations(@Param("licensePlate") String licensePlate);

    /**
     * 根據車輛ID查詢評論，並同時載入關聯的 Car 和 User 實體。
     *
     * @param carId 車輛ID
     * @return 包含完整關聯數據的評論列表
     */
    @Query("SELECT e FROM Evaluate e " +
            "LEFT JOIN FETCH e.user u " +
            "LEFT JOIN FETCH e.car c " +
            "LEFT JOIN FETCH c.defaultLocation cl " +
            "WHERE e.car.id = :carId")
    List<Evaluate> findByCarIdFetchingAssociations(@Param("carId") Long carId);
}
