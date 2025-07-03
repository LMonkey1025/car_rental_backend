package com.example.car_rental_project.controller.api.root;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.car_rental_project.model.dto.car.CarAddDto;
import com.example.car_rental_project.model.dto.car.CarDto;
import com.example.car_rental_project.model.dto.car.CarUpdateDto;
import com.example.car_rental_project.service.CarService;

import response.ApiResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/root/car/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class RootApiCarController {
    @Autowired
    private CarService carService;

    /**
     * 新增車輛API
     * 
     * @param carAddDto 車輛新增資料傳輸物件
     * @param imageFile 圖片檔案
     * @return ResponseEntity<ApiResponse<carAddDto>> 新增成功的車輛資料傳輸物件
     */
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<CarDto>> addCar(@RequestPart("carAddDto") CarAddDto carAddDto,
            @RequestPart(name = "imageFile", required = false) MultipartFile imageFile) {
        try {
            // 呼叫服務層新增車輛
            carService.addCar(carAddDto, imageFile);
            CarDto newCarDto = carService.getCarByLicensePlate(carAddDto.getLicensePlate());
            // 回傳成功的API回應
            return ResponseEntity
                    .ok(ApiResponse.success("新增車輛成功", newCarDto));
        } catch (Exception e) {
            // 回傳錯誤的API回應
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "新增車輛失敗: " + e.getMessage()));
        }
    }

    /**
     * 更新車輛API
     * 
     * @param carUpdateDto 車輛更新資料傳輸物件
     * @param imageFile    圖片檔案
     * @return ResponseEntity<ApiResponse<CarDto>> 更新成功的車輛資料傳輸物件
     */
    @PatchMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<CarDto>> updateCar(@RequestPart("carUpdateDto") CarUpdateDto carUpdateDto,
            @RequestPart(name = "imageFile", required = false) MultipartFile imageFile) {
        try {
            // 呼叫服務層更新車輛
            carService.updateCar(carUpdateDto, imageFile);
            // 根據車牌號碼查詢更新後的車輛
            CarDto updatedCarDto = carService.getCarByLicensePlate(carUpdateDto.getLicensePlate());
            return ResponseEntity.ok(ApiResponse.success("更新車輛成功", updatedCarDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "更新車輛失敗: " + e.getMessage()));
        }
    }

    /**
     * 刪除車輛API
     * 
     * @param id 車輛ID
     * @return ResponseEntity<ApiResponse<String>> 刪除成功的訊息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCar(@PathVariable Long id) {
        try {
            // 呼叫服務層刪除車輛
            carService.deleteCar(id);
            return ResponseEntity.ok(ApiResponse.success("刪除車輛成功", "車輛已被刪除"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "刪除車輛失敗: " + e.getMessage()));
        }
    }

    /**
     * 查詢所有車輛API
     * 
     * @return ResponseEntity<ApiResponse<List<CarDto>>> 所有車輛的資料傳輸物件列表
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CarDto>>> getAllCars() {
        try {
            // 呼叫服務層查詢所有車輛
            List<CarDto> carDtos = carService.getAllCars();
            if (carDtos.isEmpty()) {
                // 如果沒有車輛，回傳空列表
                return ResponseEntity.ok(ApiResponse.success("目前沒有車輛", carDtos));
            }
            return ResponseEntity.ok(ApiResponse.success("查詢所有車輛成功", carDtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "查詢所有車輛失敗: " + e.getMessage()));
        }
    }

}
