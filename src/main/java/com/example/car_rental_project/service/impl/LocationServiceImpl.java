package com.example.car_rental_project.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.car_rental_project.model.dto.location.LocationDto;
import com.example.car_rental_project.model.entity.Location;
import com.example.car_rental_project.repository.LocationRepository;
import com.example.car_rental_project.service.LocationService;

@Service
public class LocationServiceImpl implements LocationService {
    @Autowired
    private LocationRepository locationRepository;

    /**
     * 新增位置
     * 
     * @param locationName 位置名稱
     * @param address      地址
     * 
     */
    @Override
    public void addLocation(LocationDto locationAddDto) throws IllegalArgumentException {
        // 如果位置名稱或地址為空，則拋出異常
        if (locationAddDto.getName() == null || locationAddDto.getName().isEmpty() ||
                locationAddDto.getAddress() == null || locationAddDto.getAddress().isEmpty()) {
            throw new IllegalArgumentException("位置名稱和地址不能為空。");
        }
        // 如果位置已存在，則拋出異常
        if (locationRepository.existsByName(locationAddDto.getName()) ||
                locationRepository.existsByAddress(locationAddDto.getAddress())) {
            throw new IllegalArgumentException("地點已存在。");
        }

        try {
            LocalDateTime createdAt = LocalDateTime.now(); // 獲取當前時間
            LocalDateTime updatedAt = LocalDateTime.now(); // 獲取當前時間

            Location location = new Location(
                    null,
                    locationAddDto.getName(),
                    locationAddDto.getAddress(),
                    createdAt,
                    updatedAt,
                    null,
                    null,
                    null);

            locationRepository.save(location); // 儲存位置到資料庫
        } catch (Exception e) {
            throw new RuntimeException("新增位置失敗: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean removeLocation(Long locationId) throws IllegalArgumentException {

        // 確認地點是否存在
        if (!locationRepository.existsById(locationId)) {
            throw new IllegalArgumentException("地點不存在。");
        }

        try {
            locationRepository.deleteById(locationId); // 刪除地點
            return true; // 刪除成功
        } catch (Exception e) {
            throw new RuntimeException("刪除地點失敗: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updatableLocation(LocationDto locationDto)
            throws IllegalArgumentException {
        Location location = locationRepository.findById(locationDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("地點不存在。"));
        location.setName(locationDto.getName());
        location.setAddress(locationDto.getAddress());
        location.setUpdatedAt(LocalDateTime.now()); // 更新時間戳

        locationRepository.save(location); // 儲存更新後的地點
        return true; // 更新成功
    }

    @Override
    public LocationDto[] getAllLocations() throws IllegalArgumentException {
        try {
            // 取得所有位置並轉換為 DTO 陣列
            return locationRepository.findAll().stream()
                    .map(location -> new LocationDto(
                            location.getId(),
                            location.getName(),
                            location.getAddress()))
                    .toArray(LocationDto[]::new);
        } catch (Exception e) {
            throw new RuntimeException("取得所有位置失敗: " + e.getMessage(), e);
        }
    }

}
