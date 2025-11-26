# 🚀 Microservices Project

Dự án backend theo kiến trúc Microservices, bao gồm quản lý User và Post, được điều phối bởi API Gateway.

## 📋 Tổng quan hệ thống

Hệ thống bao gồm 3 dịch vụ chính hoạt động độc lập:

| Dịch vụ (Service) | Cổng (Port) | Chức năng |
| :--- | :--- | :--- |
| **API Gateway** | `8081` | Cổng vào duy nhất, định tuyến và cân bằng tải. |
| **User Service** | `8082` | Quản lý thông tin người dùng (Đăng ký, Đăng nhập). |
| **Post Service** | `8083` | Quản lý bài đăng và nội dung. |

---

## 🛠 Công nghệ sử dụng

* **Core:** Java 21, Spring Boot
* **Architecture:** Microservices
* **Microservices:** Spring Cloud Gateway
* **Build Tool:** Maven (hoặc Gradle)
* **Database:** (PostgreSQL)
* **Containerization:** Docker & Docker Compose

---

## ⚙️ Hướng dẫn cài đặt & Chạy ứng dụng

Bạn có thể chọn 1 trong 2 cách để chạy dự án: **Chạy thủ công (Manual)** hoặc **Chạy bằng Docker**.

### Cách 1: Chạy thủ công (Dùng cho phát triển/Dev)

Yêu cầu máy đã cài Java và Maven. Mở 3 terminal khác nhau và chạy lần lượt:

**1. User Service**
```bash
cd user-service
./mvnw spring-boot:run
# Server started at http://localhost:8082

**2. Post Service**
```bash
cd post-service
./mvnw spring-boot:run
# Server started at http://localhost:8083

**3. API Gateway**
```bash
cd api-gateway
./mvnw spring-boot:run
# Server started at http://localhost:8081
