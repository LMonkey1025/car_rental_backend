package com.example.car_rental_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.car_rental_project.model.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    boolean existsByName(String name);

    boolean existsByAddress(String address);

    boolean existsById(Long id);

    // 使用ID移除地點
    void deleteById(Long id);

}