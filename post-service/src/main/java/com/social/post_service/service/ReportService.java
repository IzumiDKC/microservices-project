package com.social.post_service.service;

import com.social.post_service.entity.Post;
import com.social.post_service.repository.PostRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final PostRepository postRepo;

    public ReportService(PostRepository postRepo) {
        this.postRepo = postRepo;
    }

    public byte[] exportReport() throws FileNotFoundException, JRException {
        // 1. Lấy toàn bộ bài viết từ DB
        List<Post> posts = postRepo.findAll();

        // 2. Load file template .jrxml
        File file = ResourceUtils.getFile("classpath:reports/post-report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        // 3. Đổ dữ liệu vào báo cáo
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(posts);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Admin System");

        // 4. Fill Report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // 5. Xuất ra dạng byte array (PDF)
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}