//package com.social.post_service.entity;
//
//import jakarta.persistence.*;
//import org.hibernate.annotations.CreationTimestamp;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "post_reports")
//public class PostReport {
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private Long postId;       // Bài viết bị báo cáo
//    private Long reporterId;   // Người báo cáo
//    private String reason;     // Lý do
//
//    private String status;     // PENDING (Chờ), APPROVED (Đã xử lý - Xóa bài), REJECTED (Bỏ qua)
//
//    @CreationTimestamp
//    private LocalDateTime createdAt;
//
//    public PostReport() {}
//
//    public PostReport(Long postId, Long reporterId, String reason) {
//        this.postId = postId;
//        this.reporterId = reporterId;
//        this.reason = reason;
//        this.status = "PENDING"; // Mặc định
//    }
//
//    public Long getId() {
//        return id;
//    }
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Long getPostId() {
//        return postId;
//    }
//    public void setPostId(Long postId) {
//        this.postId = postId;
//    }
//
//    public Long getReporterId() {
//        return reporterId;
//    }
//    public void setReporterId(Long reporterId) {
//        this.reporterId = reporterId;
//    }
//
//    public String getReason() {
//        return reason;
//    }
//    public void setReason(String reason) {
//        this.reason = reason;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//}