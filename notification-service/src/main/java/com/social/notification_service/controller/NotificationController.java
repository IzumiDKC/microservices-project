package com.social.notification_service.controller;

import com.social.notification_service.entity.Notification;
import com.social.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public void sendNotification(@RequestBody Notification notification) {
        notificationService.createAndSendNotification(notification);
    }
}