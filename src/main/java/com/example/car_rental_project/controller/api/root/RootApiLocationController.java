package com.example.car_rental_project.controller.api.root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.car_rental_project.model.dto.location.LocationDto;
import com.example.car_rental_project.service.LocationService;

import jakarta.servlet.http.HttpSession;
import response.ApiResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/root/location/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class RootApiLocationController {

    @Autowired
    private LocationService locationService;

    /**
     * 新增租車地點API
     *
     * @param locationName 地點名稱
     * @param address      地址
     */
    @PostMapping()
    public ResponseEntity<ApiResponse<LocationDto>> addLocation(@RequestParam String locationName,
            @RequestParam String address) {
        try {
            LocationDto locationAddDto = new LocationDto(null, locationName, address);

            locationService.addLocation(locationAddDto);
            return ResponseEntity.ok(ApiResponse.success("新增車輛成功", locationAddDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "新增車輛失敗: " + e.getMessage()));
        }
    }

    /**
     * 移除租車地點API
     * 
     * @param locationId 地點ID
     * @param session    HttpSession
     * 
     */
    @DeleteMapping("/{locationId}")
    public ResponseEntity<ApiResponse<String>> deleteMethodName(@PathVariable Long locationId, HttpSession session) {
        try {
            boolean isRemoved = locationService.removeLocation(locationId);
            if (isRemoved) {
                return ResponseEntity.ok(ApiResponse.success("移除地點成功", "地點已被移除"));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "移除地點失敗: 地點不存在或已被移除"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "移除地點失敗: " + e.getMessage()));
        }
    }

    /**
     * 更新租車地點API
     * 
     */
    @PatchMapping()
    public ResponseEntity<ApiResponse<LocationDto>> updateLocation(@RequestBody LocationDto locationDto) {
        try {
            boolean isUpdated = locationService.updatableLocation(locationDto);
            LocationDto newlocationDto = new LocationDto(null, locationDto.getName(), locationDto.getAddress());

            if (isUpdated) {
                return ResponseEntity.ok(ApiResponse.success("更新地點成功", newlocationDto));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "更新地點失敗: 地點不存在或已被更新"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "更新地點失敗: " + e.getMessage()));
        }
    }

    /**
     * 查詢所有租車地點API
     * 
     * @param session HttpSession
     * 
     * @return ResponseEntity<ApiResponse<List<LocationDto>>>
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<LocationDto[]>> getAllLocations(HttpSession session) {
        try {
            LocationDto[] locations = locationService.getAllLocations();
            return ResponseEntity.ok(ApiResponse.success("查詢所有地點成功", locations));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "查詢所有地點失敗: " + e.getMessage()));
        }
    }

}
