package com.social.chat_service.controller;

import com.social.chat_service.dto.MarkSeenRequest;
import com.social.chat_service.entity.ChatMessage;
import com.social.chat_service.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatMessageRepository repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // API: Mark as Seen
    // FE gọi khi: Mở khung chat HOẶC Có tin nhắn mới khi đang mở khung chat
    @PostMapping("/mark-seen")
    @Transactional
    public ResponseEntity<?> markAsSeen(@RequestBody MarkSeenRequest request) {
        // 1. Tìm các tin nhắn chưa đọc trong hội thoại này của user này
        List<ChatMessage> unseenMessages = repository.findUnseenMessages(request.getChatId(), request.getUserId());

        if (!unseenMessages.isEmpty()) {
            // 2. Cập nhật DB: Thêm user vào list seenBy
            for (ChatMessage msg : unseenMessages) {
                msg.getSeenBy().add(request.getUserId());
            }
            repository.saveAll(unseenMessages);

            // 3. Phát Socket Event báo cho người bên kia biết
            // Gửi vào topic chung của hội thoại hoặc gửi riêng cho sender của các tin nhắn đó
            // Ở đây ta gửi event broadcast vào chatId để ai trong phòng cũng biết

            Map<String, Object> seenEvent = Map.of(
                    "type", "READ_RECEIPT",
                    "chatId", request.getChatId(),
                    "readerId", request.getUserId(),
                    "lastSeenMessageId", unseenMessages.get(unseenMessages.size() - 1).getId() // ID tin mới nhất vừa đọc
            );

            // Gửi tới topic: /topic/chat/{chatId} (Cần config thêm ở WebSocketConfig nếu muốn dùng topic động)
            // Hoặc gửi đơn giản qua user recipient (giả sử chat 1-1)
            // Cách đơn giản nhất cho chat 1-1: Gửi cho người kia
            String senderOfMessages = unseenMessages.get(0).getSenderId(); // Người gửi tin nhắn (người cần được báo)
            messagingTemplate.convertAndSendToUser(
                    senderOfMessages,
                    "/queue/messages", // Tái sử dụng queue hoặc tạo queue mới /queue/read-status
                    seenEvent
            );
        }

        return ResponseEntity.ok().build();
    }
}