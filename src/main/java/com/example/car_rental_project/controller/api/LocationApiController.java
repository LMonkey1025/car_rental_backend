package com.example.car_rental_project.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.car_rental_project.model.dto.location.LocationDto;
import com.example.car_rental_project.service.LocationService;

import response.ApiResponse;

@RestController
@RequestMapping("/location/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class LocationApiController {
    @Autowired
    private LocationService locationService;

    /**
     * 取得所有租車地點API
     * 
     * 使用GET方法取得所有租車地點，並返回租車地點列表。
     * 
     * @return ResponseEntity<ApiResponse<LocationDto[]>>
     */
    @GetMapping("/locations")
    public ResponseEntity<ApiResponse<LocationDto[]>> getAllLocations() {
        try {
            LocationDto[] locations = locationService.getAllLocations();
            return ResponseEntity.ok(ApiResponse.success("取得所有租車地點成功", locations));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
        }
    }

}
