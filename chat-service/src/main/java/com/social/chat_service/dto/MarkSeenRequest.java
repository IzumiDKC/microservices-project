package com.social.chat_service.dto;

import lombok.Data;

@Data
public class MarkSeenRequest {
    private String chatId;
    private String userId;
}