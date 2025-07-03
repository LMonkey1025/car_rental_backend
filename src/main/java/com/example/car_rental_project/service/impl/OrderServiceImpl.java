package com.example.car_rental_project.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.car_rental_project.model.dto.order.UserOrderCreateRequestDto; // 匯入新的 DTO
import com.example.car_rental_project.model.dto.order.AdminOrderSummaryDto;
import com.example.car_rental_project.model.dto.order.OrderSummaryDto;
import com.example.car_rental_project.model.dto.user.UserCert;
import com.example.car_rental_project.model.entity.BookingStatus;
import com.example.car_rental_project.model.entity.Car;
import com.example.car_rental_project.model.entity.CarStatus;
import com.example.car_rental_project.model.entity.Location;
import com.example.car_rental_project.model.entity.Order;
import com.example.car_rental_project.model.entity.User;
import com.example.car_rental_project.repository.CarRepository;
import com.example.car_rental_project.repository.LocationRepository;
import com.example.car_rental_project.repository.OrderRepository;
import com.example.car_rental_project.repository.UserRepository;
import com.example.car_rental_project.service.OrderService;

//這是一個有關於訂單服務的實現類別，負責處理訂單相關的業務邏輯 會需要做資料回滾
@Service
public class OrderServiceImpl implements OrderService {

        @Autowired
        private OrderRepository orderRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private CarRepository carRepository;
        @Autowired
        private LocationRepository locationRepository;

        // 建立訂單
        @Transactional
        public OrderSummaryDto createOrder(UserOrderCreateRequestDto userOrderCreateRequestDto, UserCert userCert) { // 修改方法簽名
                // 1. 驗證車輛是否存在且可用
                Car car = carRepository.findById(userOrderCreateRequestDto.getCarId()) // 使用新的 DTO
                                .orElseThrow(() -> new IllegalArgumentException("指定的車輛不存在或不可用"));

                // 2. 驗證取車和還車地點是否存在
                Location pickupLocation = locationRepository.findById(userOrderCreateRequestDto.getPickupLocationId()) // 使用新的
                                                                                                                       // DTO
                                .orElseThrow(() -> new IllegalArgumentException("取車地點不存在"));
                Location returnLocation = locationRepository.findById(userOrderCreateRequestDto.getReturnLocationId()) // 使用新的
                                                                                                                       // DTO
                                .orElseThrow(() -> new IllegalArgumentException("還車地點不存在"));

                // 3. 檢查時間段內車輛是否可用
                boolean isAvailable = checkCarAvailability(
                                userOrderCreateRequestDto.getCarId(), // 使用新的 DTO
                                userOrderCreateRequestDto.getPickupDateTime(), // 使用新的 DTO
                                userOrderCreateRequestDto.getReturnDateTime()); // 使用新的 DTO

                if (!isAvailable) {
                        throw new IllegalArgumentException("此車輛在您選擇的時間段內已被預訂");
                }

                // 4. 計算總金額
                BigDecimal totalAmount = car.getDailyRate()
                                .multiply(BigDecimal.valueOf(userOrderCreateRequestDto.getPickupDateTime().until( // 使用新的
                                                                                                                  // DTO
                                                userOrderCreateRequestDto.getReturnDateTime(), // 使用新的 DTO
                                                java.time.temporal.ChronoUnit.DAYS)));

                // 4使用UserCert來獲取用戶資訊
                User user = userRepository.findById(userCert.getId())
                                .orElseThrow(() -> new IllegalArgumentException("用戶不存在"));
                // 5. 創建訂單實體
                Order order = new Order();
                order.setUser(user);
                order.setCar(car); // 直接使用上面查詢到的 car 物件
                order.setPickupLocation(pickupLocation); // 直接使用上面查詢到的 pickupLocation 物件
                order.setReturnLocation(returnLocation); // 直接使用上面查詢到的 returnLocation 物件
                order.setPickupDateTime(userOrderCreateRequestDto.getPickupDateTime()); // 使用新的 DTO
                order.setReturnDateTime(userOrderCreateRequestDto.getReturnDateTime()); // 使用新的 DTO
                order.setCustomerName(user.getUserName());
                order.setCustomerPhone(user.getPhoneNumber());
                order.setCustomerEmail(user.getEmail());
                order.setTotalAmount(totalAmount);
                order.setStatus(BookingStatus.PENDING_CONFIRMATION);

                Order savedOrder = orderRepository.save(order);

                return new OrderSummaryDto(
                                savedOrder.getId(),
                                car.getBrand(),
                                car.getModel(),
                                car.getLicensePlate(), // 新增車牌號碼欄位
                                savedOrder.getPickupDateTime(),
                                savedOrder.getReturnDateTime(),
                                pickupLocation.getName(),
                                returnLocation.getName(),
                                savedOrder.getTotalAmount(),
                                savedOrder.getStatus(),
                                savedOrder.getCreatedAt());
        }

        // 新增一個方法來檢查車輛在指定時間段內是否可用
        private boolean checkCarAvailability(Long carId, java.time.LocalDateTime pickupDateTime,
                        java.time.LocalDateTime returnDateTime) {
                // 這裡需要實作查詢邏輯
                // 查詢 OrderRepository，尋找是否有針對 carId
                // 且狀態為 CONFIRMED 或 PENDING_CONFIRMATION
                // 且訂單的 pickupDateTime 或 returnDateTime 與傳入的時間段有重疊的訂單
                // 如果找到任何重疊的訂單，則表示車輛不可用，返回 false
                // 否則返回 true

                // 範例查詢邏輯 (您需要在 OrderRepository 中定義對應的方法)
                List<Order> conflictingOrders = orderRepository.findConflictingOrders(
                                carId,
                                pickupDateTime,
                                returnDateTime);

                return conflictingOrders.isEmpty(); // 如果沒有衝突訂單，則表示可用
        }

        // 確認訂單
        @Override
        @Transactional
        public Boolean confirmOrder(UUID orderId, UserCert userCert) { // 新增 UserCert 參數
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new IllegalArgumentException("訂單不存在"));

                // 驗證使用者是否為訂單擁有者
                if (!order.getUser().getId().equals(userCert.getId())) {
                        throw new IllegalArgumentException("您無權確認此訂單");
                }

                // 檢查訂單狀態是否為待確認
                if (order.getStatus() != BookingStatus.PENDING_CONFIRMATION) {
                        throw new IllegalArgumentException("訂單狀態不正確，無法確認");
                }

                // 更新訂單狀態為已確認
                order.setStatus(BookingStatus.CONFIRMED);
                Order updatedOrder = orderRepository.save(order);

                return updatedOrder.getStatus() == BookingStatus.CONFIRMED;
        }

        // 客戶取得所有歷史訂單
        @Override
        @Transactional(readOnly = true)
        public List<OrderSummaryDto> getAllOrdersByUser(UserCert userCert) {
                User user = userRepository.findById(userCert.getId())
                                .orElseThrow(() -> new IllegalArgumentException("用戶不存在：" + userCert.getId()));
                return orderRepository.findOrderHistoryByUser(user);
        }

        // 取得訂單詳細資訊
        @Override
        @Transactional(readOnly = true) // Add this annotation
        public OrderSummaryDto getOrderDetails(UUID orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new IllegalArgumentException("訂單不存在"));
                return new OrderSummaryDto(
                                order.getId(),
                                order.getCar().getBrand(),
                                order.getCar().getModel(),
                                order.getCar().getLicensePlate(),
                                order.getPickupDateTime(),
                                order.getReturnDateTime(),
                                order.getPickupLocation().getName(),
                                order.getReturnLocation().getName(),
                                order.getTotalAmount(),
                                order.getStatus(),
                                order.getCreatedAt());
        }

        // 還車
        @Override
        @Transactional
        public Boolean adminReturnCar(UUID orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new IllegalArgumentException("訂單不存在"));

                // 檢查訂單狀態是否為已確認或已租出
                if (order.getStatus() != BookingStatus.CONFIRMED && order.getStatus() != BookingStatus.RENTED_OUT) {
                        throw new IllegalArgumentException("訂單狀態不正確，無法還車");
                }

                // 更新訂單狀態為已完成
                order.setStatus(BookingStatus.COMPLETED);
                orderRepository.save(order);

                // 更新車輛狀態為可用，並更新車輛的目前位置為還車地點
                Car car = order.getCar();
                car.setStatus(CarStatus.AVAILABLE);
                car.setDefaultLocation(order.getReturnLocation()); // 更新車輛位置為還車地點
                carRepository.save(car);

                return true;
        }

        // 取車
        @Override
        @Transactional
        public Boolean adminPickupCar(UUID orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new IllegalArgumentException("訂單不存在"));

                // 檢查訂單狀態是否為已確認
                if (order.getStatus() != BookingStatus.CONFIRMED) {
                        throw new IllegalArgumentException("訂單狀態不正確，無法取車");
                }

                // 更新訂單狀態為已租出
                order.setStatus(BookingStatus.RENTED_OUT);
                orderRepository.save(order);

                // 更新車輛狀態為租用中
                Car car = order.getCar();
                car.setStatus(CarStatus.RENTED); // 將狀態改為 RENTED
                carRepository.save(car);

                return true;
        }

        @Override
        @Transactional(readOnly = true)
        public List<AdminOrderSummaryDto> getAllOrder() {
                List<Order> orders = orderRepository.findAll();
                return orders.stream()
                                .map(order -> new AdminOrderSummaryDto(
                                                order.getId(),
                                                order.getCar().getBrand(),
                                                order.getCar().getModel(),
                                                order.getCar().getLicensePlate(),
                                                order.getPickupDateTime(),
                                                order.getReturnDateTime(),
                                                order.getPickupLocation().getName(),
                                                order.getReturnLocation().getName(),
                                                order.getTotalAmount(),
                                                order.getStatus(),
                                                order.getCreatedAt(),
                                                order.getUser().getId(), // 訂購人ID
                                                order.getUser().getUserName(), // 訂購人名稱
                                                order.getUser().getEmail() // 訂購人電子郵件
                                ))
                                .collect(Collectors.toList());
        }

}
