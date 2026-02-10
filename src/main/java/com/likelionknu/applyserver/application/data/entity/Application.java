package com.likelionknu.applyserver.application.data.entity;

import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "application")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id", nullable = false)
    private Recruit recruit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 사용자 삭제 시 함께 삭제되도록 하기 위한 연관관계
    @OneToMany(mappedBy = "application",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RecruitAnswer> answers = new ArrayList<>();

    @Column(name = "note", length = 100)
    private String note;

    public void updateNote(String memo) {
        this.note = memo;
    }

    @Enumerated(EnumType.STRING)
    private ApplicationEvaluation evaluation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "before_canceled_status")
    private ApplicationStatus beforeCanceledStatus;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

}