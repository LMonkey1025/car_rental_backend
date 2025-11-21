package com.example.car_rental_project.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.car_rental_project.exception.user.CertException;
import com.example.car_rental_project.model.dto.user.UserCert;
import com.example.car_rental_project.model.dto.user.UserDto;
import com.example.car_rental_project.model.dto.user.UserRegisterDto;
import com.example.car_rental_project.model.dto.user.UserUpdateProfileDto;
import com.example.car_rental_project.service.CertService;
import com.example.car_rental_project.service.RecaptchaService;
import com.example.car_rental_project.service.UserService;
import com.example.car_rental_project.service.impl.EmailServiceImpl;
import com.example.car_rental_project.exception.user.AccountNotActivatedException;
import com.example.car_rental_project.model.entity.User;
import com.example.car_rental_project.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import response.ApiResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger; // 引入 Logger
import org.slf4j.LoggerFactory; // 引入 LoggerFactory
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/user/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", methods = { RequestMethod.GET,
        RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS })
public class UserApiController {
    private static final Logger logger = LoggerFactory.getLogger(UserApiController.class); // 添加 Logger

    @Autowired
    private UserService userService;
    @Autowired
    private CertService certService;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private RecaptchaService recaptchaService;
    @Autowired
    private UserRepository userRepository;

    /**
     * 註冊帳號API
     * 
     * 使用者名稱、電子郵件、密碼和電話號碼，並返回註冊結果。
     * 
     * @return ResponseEntity<ApiResponse<String>>
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<String>> registerUser(
            @ModelAttribute("userName") String userName,
            @ModelAttribute("email") String email,
            @ModelAttribute("password") String password,
            @ModelAttribute("phoneNumber") String phoneNumber) {

        UserRegisterDto userRegisterDto = new UserRegisterDto(userName, email, password, phoneNumber);

        try {
            userService.registerUser(userRegisterDto);
            // 註冊成功後，會發送啟用帳號的郵件
            emailService.sendActivationEmail(email);
            return ResponseEntity.ok(ApiResponse.success("註冊成功，請到信箱啟用帳號", "註冊成功，請到信箱啟用帳號並重新登入"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "註冊失敗：" + e.getMessage()));
        }
    }

    /**
     * 登入帳號API
     * 
     * 使用POST方法接收帳號登入資料（username和password），並返回登入結果。
     * 
     * @param username 使用者名稱
     * @param password 使用者密碼
     * 
     * @return ResponseEntity<ApiResponse<UserCert>>
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserCert>> Login(@RequestParam String email,
            @RequestParam String password,
            @RequestParam String recaptchaToken,
            HttpSession session) {
        try {
            System.out.println("收到登入請求: " + email);

            // 提前檢查用戶狀態，避免不必要的 reCAPTCHA 驗證
            User user = userRepository.findByEmail(email);
            if (user != null && !user.isEnabled()) {
                throw new AccountNotActivatedException("帳號尚未啟用");
            }

            // 驗證 reCAPTCHA
            if (recaptchaToken == null || recaptchaToken.trim().isEmpty()) {
                System.out.println("reCAPTCHA token 為空");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "請完成人機驗證"));
            }

            boolean isValidRecaptcha = recaptchaService.verifyRecaptcha(recaptchaToken);
            if (!isValidRecaptcha) {
                System.out.println("reCAPTCHA 驗證失敗");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "人機驗證失敗，請重試"));
            }

            System.out.println("reCAPTCHA 驗證成功，繼續處理登入");

            // 呼叫你現有的登入邏輯
            UserCert userCert = certService.getCert(email, password);

            // 將使用者憑證存入 session
            session.setAttribute("userCert", userCert);

            return ResponseEntity.ok(ApiResponse.success("登入成功", userCert));

        } catch (AccountNotActivatedException e) {
            // 帳號未啟用，重新發送啟用信
            try {
                emailService.sendActivationEmail(email);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "帳號尚未啟用，已重新發送啟用信至您的信箱，請前往啟用後再登入。"));
            } catch (Exception mailException) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(500, "帳號尚未啟用，且啟用信發送失敗，請聯繫管理員。"));
            }
        } catch (Exception e) {
            System.err.println("登入失敗: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "登入失敗: " + e.getMessage()));
        }
    }

    /**
     * 登出API
     * 
     * 使用GET方法登出使用者，清除session並返回登出結果。
     * 
     * @param session HttpSession
     * 
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        if (session.getAttribute("userCert") == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "登出失敗: 尚未登入 "));
        }
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.success("登出成功", null));
    }

    /**
     * 取得使用者資訊API
     * 
     * 使用GET方法取得使用者資訊，並返回使用者資訊。
     * 
     * @param session HttpSession
     * 
     * @return ResponseEntity<ApiResponse<UserDto>>
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<UserDto>> getUserInfo(HttpSession session) {
        try {
            UserCert userCert = (UserCert) session.getAttribute("userCert");
            if (userCert == null) {
                throw new CertException("用戶未登入");
            }
            UserDto userDto = userService.getUserInfo(userCert);
            return ResponseEntity.ok(ApiResponse.success("成功獲取使用者資訊", userDto));
        } catch (CertException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            logger.error("獲取使用者資訊時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "獲取使用者資訊時發生未知錯誤"));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> updateUserProfile(
            HttpSession session,
            @RequestBody UserUpdateProfileDto userUpdateProfileDto) {
        try {
            UserCert userCert = (UserCert) session.getAttribute("userCert");
            if (userCert == null) {
                throw new CertException("用戶未登入");
            }

            UserDto updatedUser = userService.updateUserProfile(userCert, userUpdateProfileDto);
            return ResponseEntity.ok(ApiResponse.success("個人資訊更新成功", updatedUser));
        } catch (CertException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            logger.error("更新個人資訊時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "更新個人資訊時發生未知錯誤"));
        }
    }

    /**
     * 處理帳號啟用請求
     * 
     * @param uuid 啟用碼
     * @return JSON 回應
     */
    @GetMapping("/activate/{uuid}")
    public ResponseEntity<ApiResponse<String>> activateAccount(@PathVariable String uuid) {
        try {
            // 調用您現有的 activateUser 方法
            userService.activateUser(uuid);

            return ResponseEntity.ok(ApiResponse.success("帳號啟用成功！將跳轉到登入頁面。", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "啟用失敗：" + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "啟用過程中發生錯誤，請稍後再試。"));
        }
    }

}