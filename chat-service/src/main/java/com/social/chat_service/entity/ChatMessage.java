package com.social.chat_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatId;
    private String senderId;
    private String recipientId;
    private String content;
    private Date timestamp;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "message_seen_by", joinColumns = @JoinColumn(name = "message_id"))
    @Column(name = "user_id")
    @Builder.Default
    private Set<String> seenBy = new HashSet<>();
}