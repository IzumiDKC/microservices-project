package com.social.post_service.delegate;

import com.social.post_service.entity.PostReport;
import com.social.post_service.repository.PostReportRepository;
import com.social.post_service.repository.PostRepository;
import jakarta.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("deletePostDelegate") // Tên này phải trùng khớp với Delegate Expression trong bản vẽ
public class DeletePostDelegate implements JavaDelegate {

    private final PostRepository postRepo;
    private final PostReportRepository reportRepo;

    public DeletePostDelegate(PostRepository postRepo, PostReportRepository reportRepo) {
        this.postRepo = postRepo;
        this.reportRepo = reportRepo;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Lấy dữ liệu từ quy trình
        Long postId = (Long) execution.getVariable("postId");
        Long reportId = (Long) execution.getVariable("reportId");

        System.out.println("--- CAMUNDA SERVICE TASK: Đang xóa bài viết ID " + postId + " ---");

        if (postRepo.existsById(postId)) {
            postRepo.deleteById(postId);
            System.out.println("Đã xóa bài viết thành công!");
        }

        // Cập nhật trạng thái báo cáo thành APPROVED (Đã xử lý)
        PostReport report = reportRepo.findById(reportId).orElse(null);
        if (report != null) {
            report.setStatus("APPROVED");
            reportRepo.save(report);
        }
    }
}