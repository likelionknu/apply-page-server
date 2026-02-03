package com.likelionknu.applyserver.mail.data.entity;

import com.likelionknu.applyserver.auth.data.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mail_history")
public class MailHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String recipient;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
}