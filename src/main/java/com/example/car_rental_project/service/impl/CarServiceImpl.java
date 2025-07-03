package com.example.car_rental_project.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.car_rental_project.exception.user.CarException;
import com.example.car_rental_project.mapper.CarMapper;
import com.example.car_rental_project.model.dto.car.CarAddDto;
import com.example.car_rental_project.model.dto.car.CarDto;
import com.example.car_rental_project.model.dto.car.CarUpdateDto;
import com.example.car_rental_project.model.dto.order.OrderSearchCriteriaDto;
import com.example.car_rental_project.model.entity.Car;
import com.example.car_rental_project.model.entity.CarStatus;
import com.example.car_rental_project.repository.CarRepository;
import com.example.car_rental_project.repository.LocationRepository;
import com.example.car_rental_project.repository.OrderRepository;
import com.example.car_rental_project.service.CarService;
import com.example.car_rental_project.service.FileService;
import java.io.IOException;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository; // 用來查詢資料庫的 CarRepository
    @Autowired
    private LocationRepository locationRepository; // 用來查詢資料庫的 LocateRegistry
    @Autowired
    private OrderRepository orderRepository; // 用來查詢訂單的 OrderRepository
    @Autowired
    private CarMapper carMapper;
    @Autowired
    private FileService fileService; // 用來處理檔案上傳的 FileService

    @Override
    public void addCar(CarAddDto carAddDto, MultipartFile imageFile) throws CarException {
        // 檢查車牌號碼是否已存在
        if (carRepository.existsByLicensePlate(carAddDto.getLicensePlate())) {
            throw new CarException("車牌號碼已存在，請使用其他車牌號碼。");
        }

        Car car = new Car();
        car.setBrand(carAddDto.getBrand());
        car.setModel(carAddDto.getModel());
        car.setLicensePlate(carAddDto.getLicensePlate());
        car.setDailyRate(carAddDto.getDailyRate());
        car.setSeats(carAddDto.getSeats());
        // car.setImageUrl(carAddDto.getImageUrl()); // Removed old imageUrl logic
        car.setDefaultLocation(locationRepository.findById(carAddDto.getDefaultLocationId()).orElse(null)); // 預設位置
        car.setStatus(CarStatus.AVAILABLE); // 新車輛預設狀態為可用
        car.setCreatedAt(LocalDateTime.now()); // 創建時間

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = fileService.uploadFile(imageFile, imageFile.getOriginalFilename());
                car.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new CarException("圖片上傳失敗：" + e.getMessage());
            }
        }

        // 儲存到資料庫
        carRepository.save(car);

    }

    @Override
    public void updateCar(CarUpdateDto carUpdateDto, MultipartFile imageFile) throws CarException {
        // 檢查車輛是否存在
        if (!carRepository.existsById(carUpdateDto.getId())) {
            throw new CarException("車輛不存在，無法更新。");
        }
        // 根據ID查詢車輛
        Car car = carRepository.findById(carUpdateDto.getId())
                .orElseThrow(() -> new CarException("車輛不存在。"));
        // 更新車輛資訊
        car.setBrand(carUpdateDto.getBrand());
        car.setModel(carUpdateDto.getModel());
        car.setLicensePlate(carUpdateDto.getLicensePlate());
        car.setDailyRate(carUpdateDto.getDailyRate());
        car.setSeats(carUpdateDto.getSeats());
        if (carUpdateDto.getDefaultLocationId() != null) {
            car.setDefaultLocation(
                    locationRepository.findById(carUpdateDto.getDefaultLocationId().longValue()).orElse(null)); // 更新預設位置
        } else {
            car.setDefaultLocation(null); // 若為 null 則設為 null
        }
        car.setStatus(carUpdateDto.getStatus()); // 更新狀態

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String newImageUrl = fileService.uploadFile(imageFile, imageFile.getOriginalFilename());
                car.setImageUrl(newImageUrl);
            } catch (IOException e) {
                throw new CarException("圖片上傳失敗：" + e.getMessage());
            }
        }
        // 儲存到資料庫
        carRepository.save(car);
    }

    @Override
    public void deleteCar(Long id) throws CarException {
        // 檢查車輛是否存在，車輛不存在則拋出例外
        carRepository.findById(id).orElseThrow(() -> new CarException("車輛不存在，無法刪除。"));
        // 刪除車輛
        carRepository.deleteById(id);
    }

    @Override
    public List<CarDto> getAllCars() {
        // 查詢所有車輛並同時載入 defaultLocation，避免 LazyInitializationException
        List<CarDto> carDtos = carRepository.findAllWithLocation().stream().map(carMapper::toDto).toList();
        return carDtos;
    }

    @Override
    public CarDto getCarById(Long id) throws CarException {
        // 檢查車輛是否存在
        if (!carRepository.existsById(id)) {
            throw new CarException("車輛不存在，無法查詢。");
        }

        // 根據ID查詢車輛並轉換成 DTO
        var car = carRepository.findById(id).orElseThrow(() -> new CarException("車輛不存在。"));
        return carMapper.toDto(car);
    }

    @Override
    public CarDto getCarByLicensePlate(String licensePlate) throws CarException {
        Car car = carRepository.findByLicensePlateWithLocation(licensePlate)
                .orElseThrow(() -> new CarException("找不到車牌號碼為 " + licensePlate + " 的車輛"));

        return carMapper.toDto(car);
    }

    @Override
    public List<CarDto> getAvailableCars() {
        // 直接從資料庫查詢可用車輛（狀態為 AVAILABLE）
        List<Car> availableCars = carRepository.findByStatus(CarStatus.AVAILABLE);

        // 轉換成 DTO 列表
        return availableCars.stream()
                .map(carMapper::toDto)
                .toList();
    }

    // 根據租車地點過濾查詢可用車輛
    @Override
    public List<CarDto> getAvailableCarsByLocation(Long locationId) {
        // 直接從資料庫查詢指定據點的可用車輛
        List<Car> availableCars = carRepository.findByStatusAndLocationId(CarStatus.AVAILABLE, locationId);

        // 轉換成 DTO 列表
        return availableCars.stream()
                .map(carMapper::toDto)
                .toList();
    }

    // 根據搜尋條件查詢可用車輛(包含時間衝突檢查)
    @Override
    @Transactional(readOnly = true) // Add this annotation
    public List<CarDto> searchAvailableCars(OrderSearchCriteriaDto searchCriteria) throws CarException {
        // 1. 檢查搜尋條件是否有效
        if (searchCriteria == null || searchCriteria.getPickupLocationId() == null) {
            throw new CarException("搜尋條件無效，請提供取車地點。");
        }

        // 2. 根據取車地點查詢可用車輛
        List<Car> availableCarsAtLocation = carRepository.findAvailableCarsByLocationId(
                searchCriteria.getPickupLocationId());

        // 3. 移除地點中車輛訂單有時間衝突的車輛
        List<Car> availableCars = availableCarsAtLocation.stream()
                .filter(car -> !hasTimeConflict(car.getId(), searchCriteria))
                .collect(Collectors.toList());

        // 4. 轉換成 DTO 列表並返回
        return availableCars.stream()
                .map(carMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 檢查車輛在指定時間段是否有衝突（透過查詢 Order 表）
     */
    private boolean hasTimeConflict(Long carId, OrderSearchCriteriaDto searchCriteria) {
        // 如果沒有提供時間條件，就不檢查時間衝突
        if (searchCriteria.getPickupDateTime() == null || searchCriteria.getReturnDateTime() == null) {
            return false;
        }

        // 從 OrderRepository 查詢是否有時間衝突的訂單
        long conflictCount = orderRepository.countConflictingOrders(
                carId,
                searchCriteria.getPickupDateTime(),
                searchCriteria.getReturnDateTime());

        return conflictCount > 0;
    }

}
