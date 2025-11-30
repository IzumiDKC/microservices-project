package com.social.post_service.controller;

import com.social.post_service.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/posts-pdf")
    public ResponseEntity<byte[]> generatePostReport() throws FileNotFoundException, JRException {
        byte[] data = reportService.exportReport();

        HttpHeaders headers = new HttpHeaders();
        // Tên file khi tải về
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=posts.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}