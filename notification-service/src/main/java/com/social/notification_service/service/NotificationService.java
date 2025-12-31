package com.social.notification_service.service;

import com.social.notification_service.entity.Notification;
import com.social.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket

    public void createAndSendNotification(Notification notification) {
        Notification saved = notificationRepo.save(notification);

        // Gửi riêng cho User có tên là recipientId
        //  client lắng nghe là: /user/queue/notifications
        messagingTemplate.convertAndSendToUser(
                String.valueOf(notification.getRecipientId()),
                "/queue/notifications",
                saved
        );

        System.out.println("Đã gửi PRIVATE cho User: " + notification.getRecipientId());
    }
}