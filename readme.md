# 說明

這是我上商研院 Java雲端應用開發工程師實戰養成班的第一個專題，也是我首次接觸web應用的後端

前端在這:https://github.com/LMonkey1025/car_rental_frontend

此專案是一個極簡易的租車系統

專案使用Maven建構Spring boot

# application.properties 文件內容

```text
spring.application.name=car_rental_project
server.port=8083

# Spring ai Ollama 配置
spring.ai.ollama.base-url=
spring.ai.ollama.chat.model=gemma3:4b

# mysql 配置
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA 配置
# 自動更新表結構，可根據需要設置為 create, update, validate, none
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false


# 啟用 hiddenmethod filter 用來攔截 PUT、DELETE 請求
spring.mvc.hiddenmethod.filter.enabled=

# Log 配置
# 根日誌層級為 INFO
logging.level.root=
# 設定日誌保存目錄和文件名稱(會在專案目錄下自動建立一個 log 資料夾與 app.log 檔案)
logging.file.name=

# Google reCAPTCHA 設定
recaptcha.secret-key=

# AWS S3 (Cloudflare R2) 配置
# 公鑰 (Access Key ID)
aws.s3.access-key-id=
# 私鑰 (Secret Access Key)
aws.s3.secret-access-key=
# 區域設定，Cloudflare R2 使用 auto
aws.s3.region=auto
# Cloudflare R2 的 端點 URL
aws.s3.endpoint-url=
# S3 Bucket 名稱
aws.s3.bucket-name=
# Cloudflare R2 對外公開的 URL 前綴
aws.s3.public-url-prefix=

GOOGLE_API_KEY=

# Google App 設定(用於EmailService)
google.app.password=
google.app.from=
```

# API說明(使用Copilot生成)

## 驗證與授權

- **一般使用者**: 大多數需要使用者身分的 API 端點會透過 `HttpSession` 來驗證使用者。客戶端（前端）在登入成功後需要儲存並在後續請求中攜帶 Session ID。
- **管理員**: 管理員專用的 API 位於 `/root` 路徑下，需要管理員權限才能存取。

## 通用回應格式

所有 API 回應都使用統一的 `ApiResponse` 格式：

```json
{
  "status": "success", // "success" 或 "error"
  "message": "操作成功的訊息",
  "data": { ... }, // 成功時的回應資料
  "errorCode": 400, // 錯誤碼，僅在 status 為 "error" 時出現
  "errorMessage": "錯誤的詳細訊息" // 錯誤訊息，僅在 status 為 "error" 時出現
}
```

---

## 公開 API (`/public/api`)

無需驗證即可存取。

### 搜尋可用車輛

- **Endpoint**: `POST /public/api/search`
- **說明**: 根據指定的租還車地點和時間，查詢可用的車輛列表。
- **Request Body**: `OrderSearchCriteriaDto`

  ```json
  {
    "pickupLocationId": 1,
    "returnLocationId": 1,
    "pickupDate": "2025-08-10T10:00:00",
    "returnDate": "2025-08-12T10:00:00"
  }
  ```

- **Success Response**: `200 OK` - `ApiResponse<List<CarDto>>`
- **Error Response**: `404 Not Found` - 如果沒有符合條件的車輛。

---

## 使用者 API (`/user/api`)

處理使用者註冊、登入、登出及個人資料管理。

### 使用者註冊

- **Endpoint**: `POST /user/api`
- **說明**: 註冊新使用者帳號，成功後會發送一封啟用郵件。
- **Content-Type**: `multipart/form-data`
- **Request Body (form-data)**:
  - `userName` (String)
  - `email` (String)
  - `password` (String)
  - `phoneNumber` (String)
- **Success Response**: `200 OK` - `ApiResponse<String>`
- **Error Response**: `400 Bad Request` - 註冊失敗（例如：信箱已存在）。

### 使用者登入

- **Endpoint**: `POST /user/api/login`
- **說明**: 使用者登入並進行 reCAPTCHA 驗證。成功後 Session 中會包含 `userCert`。
- **Request Body (x-www-form-urlencoded)**:
  - `email` (String)
  - `password` (String)
  - `recaptchaToken` (String)
- **Success Response**: `200 OK` - `ApiResponse<UserCert>`
- **Error Response**:
  - `400 Bad Request` - 登入失敗或 reCAPTCHA 驗證失敗。
  - `403 Forbidden` - 帳號尚未啟用，會重發啟用信。

### 使用者登出

- **Endpoint**: `GET /user/api/logout`
- **說明**: 清除伺服器上的使用者 Session。
- **Success Response**: `200 OK` - `ApiResponse<Void>`
- **Error Response**: `401 Unauthorized` - 使用者尚未登入。

### 取得目前使用者資訊

- **Endpoint**: `GET /user/api`
- **說明**: 取得當前登入使用者的詳細資訊。
- **權限**: 需要登入。
- **Success Response**: `200 OK` - `ApiResponse<UserDto>`
- **Error Response**: `401 Unauthorized` - 使用者尚未登入。

### 更新使用者個人資料

- **Endpoint**: `PUT /user/api/profile`
- **說明**: 更新當前登入使用者的個人資料。
- **權限**: 需要登入。
- **Request Body**: `UserUpdateProfileDto`

  ```json
  {
    "userName": "新的使用者名稱",
    "phoneNumber": "0987654321"
  }
  ```

- **Success Response**: `200 OK` - `ApiResponse<UserDto>`
- **Error Response**: `401 Unauthorized` - 使用者尚未登入。

### 啟用帳號

- **Endpoint**: `GET /user/api/activate/{uuid}`
- **說明**: 透過點擊郵件中的連結來啟用使用者帳號。
- **Path Variable**: `uuid` (String) - 啟用碼。
- **Success Response**: `200 OK` - `ApiResponse<String>`
- **Error Response**: `400 Bad Request` - 啟用碼無效或已過期。

---

## 訂單 API (`/order/api`)

處理使用者建立和檢視訂單。

### 建立訂單

- **Endpoint**: `POST /order/api/order`
- **說明**: 使用者建立一筆新的租車訂單。
- **權限**: 需要登入。
- **Request Body**: `UserOrderCreateRequestDto`

  ```json
  {
    "carId": 1,
    "pickupDate": "2025-08-10T10:00:00",
    "returnDate": "2025-08-12T10:00:00",
    "pickupLocationId": 1,
    "returnLocationId": 1
  }
  ```

- **Success Response**: `200 OK` - `ApiResponse<OrderSummaryDto>`
- **Error Response**: `401 Unauthorized` - 使用者尚未登入。

### 確認訂單

- **Endpoint**: `POST /order/api/confirm`
- **說明**: 使用者確認一筆待確認的訂單。
- **權限**: 需要登入。
- **Request Body (x-www-form-urlencoded)**:
  - `orderId` (UUID)
- **Success Response**: `200 OK` - `ApiResponse<String>`
- **Error Response**: `400 Bad Request` - 訂單確認失敗（例如訂單不存在或狀態不符）。

### 取得目前使用者的所有訂單

- **Endpoint**: `GET /order/api/orders`
- **說明**: 取得當前登入使用者的所有歷史訂單。
- **權限**: 需要登入。
- **Success Response**: `200 OK` - `ApiResponse<List<OrderSummaryDto>>`
- **Error Response**: `404 Not Found` - 如果使用者沒有任何訂單。

### 取得特定訂單詳情

- **Endpoint**: `GET /order/api/order/{orderId}`
- **說明**: 取得特定一筆訂單的詳細資訊。
- **權限**: 需要登入。
- **Path Variable**: `orderId` (UUID) - 訂單 ID。
- **Success Response**: `200 OK` - `ApiResponse<OrderSummaryDto>`
- **Error Response**: `404 Not Found` - 找不到該訂單。

---

## 評論 API (`/evaluations/api`)

處理使用者對車輛的評論。

### 新增評論

- **Endpoint**: `POST /evaluations/api`
- **說明**: 為一筆已完成的訂單新增評論。
- **權限**: 需要登入。
- **Request Body**: `CreateEvaluateDto`

  ```json
  {
    "orderId": "...", // UUID String
    "rating": 5,
    "comment": "車況很好！"
  }
  ```

- **Success Response**: `201 Created` - `ApiResponse<EvaluateDto>`
- **Error Response**:
  - `400 Bad Request` - 評論內容不當。
  - `404 Not Found` - 訂單不存在。

### 根據車輛 ID 獲取評論

- **Endpoint**: `GET /evaluations/api/car/{carId}`
- **說明**: 獲取特定車輛的所有評論。
- **Path Variable**: `carId` (Long) - 車輛 ID。
- **Success Response**: `200 OK` - `ApiResponse<List<EvaluateDto>>`

### 獲取目前使用者的評論

- **Endpoint**: `GET /evaluations/api/user/me`
- **說明**: 獲取當前登入使用者的所有評論。
- **權限**: 需要登入。
- **Success Response**: `200 OK` - `ApiResponse<List<EvaluateDto>>`

### 更新評論

- **Endpoint**: `PUT /evaluations/api/{evaluationId}`
- **說明**: 更新使用者自己的某條評論。
- **權限**: 需要登入，且只能修改自己的評論。
- **Path Variable**: `evaluationId` (Long) - 評論 ID。
- **Request Body**: `CreateEvaluateDto` (同新增評論)
- **Success Response**: `200 OK` - `ApiResponse<EvaluateDto>`
- **Error Response**: `403 Forbidden` - 無權限修改此評論。

### 刪除評論

- **Endpoint**: `DELETE /evaluations/api/{evaluationId}`
- **說明**: 刪除使用者自己的某條評論。
- **權限**: 需要登入，且只能刪除自己的評論。
- **Path Variable**: `evaluationId` (Long) - 評論 ID。
- **Success Response**: `200 OK` - `ApiResponse<String>`
- **Error Response**: `403 Forbidden` - 無權限刪除此評論。

---

## 地點 API (`/location/api`)

### 取得所有租車地點

- **Endpoint**: `GET /location/api/locations`
- **說明**: 取得所有可用的租車地點列表。
- **Success Response**: `200 OK` - `ApiResponse<LocationDto[]>`

---

## 管理員 API - 車輛管理 (`/root/car/api`)

### 新增車輛

- **Endpoint**: `POST /root/car/api`
- **權限**: 管理員。
- **Content-Type**: `multipart/form-data`
- **Request Parts**:
  - `carAddDto` (JSON `CarAddDto`)
  - `imageFile` (File, optional)
- **Success Response**: `200 OK` - `ApiResponse<CarDto>`

### 更新車輛

- **Endpoint**: `PATCH /root/car/api`
- **權限**: 管理員。
- **Content-Type**: `multipart/form-data`
- **Request Parts**:
  - `carUpdateDto` (JSON `CarUpdateDto`)
  - `imageFile` (File, optional)
- **Success Response**: `200 OK` - `ApiResponse<CarDto>`

### 刪除車輛

- **Endpoint**: `DELETE /root/car/api/{id}`
- **權限**: 管理員。
- **Path Variable**: `id` (Long) - 車輛 ID。
- **Success Response**: `200 OK` - `ApiResponse<String>`

### 查詢所有車輛

- **Endpoint**: `GET /root/car/api/all`
- **權限**: 管理員。
- **Success Response**: `200 OK` - `ApiResponse<List<CarDto>>`

---

## 管理員 API - 地點管理 (`/root/location/api`)

### 新增租車地點

- **Endpoint**: `POST /root/location/api`
- **權限**: 管理員。
- **Request Params**: `locationName` (String), `address` (String)
- **Success Response**: `200 OK` - `ApiResponse<LocationDto>`

### 移除租車地點

- **Endpoint**: `DELETE /root/location/api/{locationId}`
- **權限**: 管理員。
- **Path Variable**: `locationId` (Long) - 地點 ID。
- **Success Response**: `200 OK` - `ApiResponse<String>`

### 更新租車地點

- **Endpoint**: `PATCH /root/location/api`
- **權限**: 管理員。
- **Request Body**: `LocationDto`
- **Success Response**: `200 OK` - `ApiResponse<LocationDto>`

### 查詢所有租車地點

- **Endpoint**: `GET /root/location/api/all`
- **權限**: 管理員。
- **Success Response**: `200 OK` - `ApiResponse<LocationDto[]>`

---

## 管理員 API - 訂單管理 (`/root/order/api`)

### 獲取所有訂單

- **Endpoint**: `GET /root/order/api/all`
- **權限**: 管理員。
- **Success Response**: `200 OK` - `ApiResponse<List<AdminOrderSummaryDto>>`

### 確認取車

- **Endpoint**: `PUT /root/order/api/{orderId}/pickup`
- **權限**: 管理員。
- **Path Variable**: `orderId` (UUID) - 訂單 ID。
- **Success Response**: `200 OK` - `ApiResponse<String>`

### 確認還車

- **Endpoint**: `PUT /root/order/api/{orderId}/return`
- **權限**: 管理員。
- **Path Variable**: `orderId` (UUID) - 訂單 ID。
- **Success Response**: `200 OK` - `ApiResponse<String>`

---

## 管理員 API - 使用者管理 (`/root/user/api`)

### 獲取所有用戶列表

- **Endpoint**: `GET /root/user/api/all`
- **權限**: 管理員。
- **Success Response**: `200 OK` - `ApiResponse<List<UserDto>>`

### 根據 ID 獲取用戶資訊

- **Endpoint**: `GET /root/user/api/{userId}`
- **權限**: 管理員。
- **Path Variable**: `userId` (UUID) - 使用者 ID。
- **Success Response**: `200 OK` - `ApiResponse<UserDto>`

### 更新用戶資訊

- **Endpoint**: `PUT /root/user/api/{userId}`
- **權限**: 管理員。
- **Path Variable**: `userId` (UUID) - 使用者 ID。
- **Request Body**: `UserUpdateAdminDto`
- **Success Response**: `200 OK` - `ApiResponse<UserDto>`

### 刪除用戶

- **Endpoint**: `DELETE /root/user/api/{userId}`
- **權限**: 管理員。
- **Path Variable**: `userId` (UUID) - 使用者 ID。
- **Success Response**: `200 OK` - `ApiResponse<String>`
- **Error Response**: `409 Conflict` - 如果使用者有關聯訂單而無法刪除。
