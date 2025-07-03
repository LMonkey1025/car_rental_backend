package com.example.car_rental_project.service;

import com.example.car_rental_project.model.dto.location.LocationDto;

public interface LocationService {
    void addLocation(LocationDto locationAddDto) throws IllegalArgumentException; // 新增租車地點API

    boolean removeLocation(Long locationId) throws IllegalArgumentException; // 刪除租車地點API

    boolean updatableLocation(LocationDto LocationDto) throws IllegalArgumentException; // 更新租車地點API

    LocationDto[] getAllLocations() throws IllegalArgumentException; // 取得所有租車地點API
}
