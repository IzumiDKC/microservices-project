package com.social.chat_service.repository;

import com.social.chat_service.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Tìm tin nhắn trong hội thoại X, không phải do tôi gửi, và tôi chưa có trong list seenBy
    @Query("SELECT m FROM ChatMessage m WHERE m.chatId = :chatId AND m.senderId != :myId AND :myId NOT MEMBER OF m.seenBy")
    List<ChatMessage> findUnseenMessages(@Param("chatId") String chatId, @Param("myId") String myId);
}