package com.social.user_service.service;

import com.social.user_service.entity.AppUser;
import com.social.user_service.repository.AppUserRepository;
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
public class UserReportService {

    private final AppUserRepository userRepo;

    public UserReportService(AppUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public byte[] exportReport() throws FileNotFoundException, JRException {
        // Lấy list user
        List<AppUser> users = userRepo.findAll();

        // Load template
        File file = ResourceUtils.getFile("classpath:reports/user-report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        // Fill data
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Admin");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}