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

    public void updateEvaluation(ApplicationEvaluation evaluation) {
        this.evaluation = evaluation;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "before_canceled_status")
    private ApplicationStatus beforeCanceledStatus;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    public void changeStatus(ApplicationStatus newStatus) {
        if (newStatus == null) return;

        if (newStatus == ApplicationStatus.CANCELED && this.status != ApplicationStatus.CANCELED) {
            this.beforeCanceledStatus = this.status;
            this.status = ApplicationStatus.CANCELED;
            resetEvaluation();
            return;
        }

        if (this.status == ApplicationStatus.CANCELED && newStatus != ApplicationStatus.CANCELED) {
            this.status = newStatus;
            this.beforeCanceledStatus = null;
            resetEvaluation();
            return;
        }

        if (this.status != newStatus) {
            this.status = newStatus;
            resetEvaluation();
        }
    }

    public void restoreFromCanceled() {
        if (this.status != ApplicationStatus.CANCELED) {
            throw new IllegalStateException("CANCELED 상태가 아닙니다.");
        }

        if (this.beforeCanceledStatus == null) {
            throw new IllegalStateException("복구할 이전 상태 정보가 없습니다.");
        }

        this.status = this.beforeCanceledStatus;
        this.beforeCanceledStatus = null;
        resetEvaluation();
    }

    private void resetEvaluation() {
        this.evaluation = null;
    }
}