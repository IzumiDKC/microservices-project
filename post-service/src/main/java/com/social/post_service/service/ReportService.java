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
        // Lấy toàn bộ bài viết
        List<Post> posts = postRepo.findAll();
        // Load file template .jrxml
        File file = ResourceUtils.getFile("classpath:reports/post-report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        // Fill data vào báo cáo
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(posts);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Admin System");
        // Fill Report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        // Xuất dạng byte array (PDF)
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}