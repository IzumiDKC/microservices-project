# Microservices Project

Dự án backend mạng xã hội xây dựng theo kiến trúc Microservices, bao gồm các dịch vụ quản lý User, Post, Notification, được điều phối bởi API Gateway và sử dụng Service Discovery.

## 📋 Tổng quan hệ thống

Hệ thống bao gồm 4 dịch vụ chính hoạt động độc lập và liên kết với nhau:

| Dịch vụ (Service) | Cổng (Port) | Chức năng |
| :--- | :--- | :--- |
| **Discovery Server** | `8761` | (Eureka Server) Quản lý và định tuyến dịch vụ (Service Discovery). |
| **API Gateway** | `8081` | Cổng vào duy nhất, định tuyến request và cân bằng tải. |
| **User Service** | `8082` | Quản lý thông tin người dùng (Đăng ký, Đăng nhập). |
| **Post Service** | `8083` | Quản lý bài đăng và nội dung. |
| **Notification Service** | `8083` | Quản lý thông báo realtime. |


---

## 🛠 Công nghệ sử dụng

* **Core:** Java 21, Spring Boot
* **Architecture:** Microservices
* **Service Discovery:** Spring Cloud Netflix Eureka
* **Gateway:** Spring Cloud Gateway
* **Build Tool:** Maven
* **Database:** PostgreSQL
* **Containerization:** Docker & Docker Compose

---

## ⚙️ Hướng dẫn cài đặt & Chạy ứng dụng

Bạn có thể chọn 1 trong 2 cách để chạy dự án: **Chạy thủ công (Manual)** hoặc **Chạy bằng Docker**.

### Cách 1: Chạy thủ công (Dùng cho phát triển/Dev)

Yêu cầu máy đã cài Java 21 và Maven. Mở 4 terminal khác nhau và chạy lần lượt theo thứ tự sau (để tránh lỗi kết nối):

**1. Discovery Server (BẮT BUỘC CHẠY ĐẦU TIÊN)**
```bash
cd discovery-server
./mvnw spring-boot:run
# Server started at http://localhost:8761
```

**2. User Service**
```
cd user-service
./mvnw spring-boot:run
# Server started at http://localhost:8082
```

**3. Post Service**
```
cd post-service
./mvnw spring-boot:run
# Server started at http://localhost:8083
```
**4. API Gateway**
```
cd api-gateway
./mvnw spring-boot:run
# Server started at http://localhost:8081
```

### Cách 2: Chạy bằng Docker (Khuyên dùng)

Yêu cầu máy đã cài Docker và Docker Compose.

Tại thư mục gốc của dự án, chạy lệnh:
```
docker-compose up -d --build
```
Hệ thống sẽ tự động build và khởi chạy tất cả các container.

---

## Đóng góp (Contributing)

* Fork repository này.
* Tạo branch mới (git checkout -b feature/ten-tinh-nang).
* Commit thay đổi (git commit -m 'Thêm tính năng X').
* Push lên branch (git push origin feature/ten-tinh-nang).
* Tạo Pull Request.
