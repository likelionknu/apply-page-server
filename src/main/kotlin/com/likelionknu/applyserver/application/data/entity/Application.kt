package com.likelionknu.applyserver.application.data.entity

import com.likelionknu.applyserver.auth.data.entity.User
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.recruit.data.entity.Recruit
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "application")
class Application (
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id", nullable = false)
    val recruit: Recruit,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ApplicationStatus,

    @Column(name = "submitted_at", nullable = false)
    var submittedAt: LocalDateTime,

    @OneToMany(mappedBy = "application", cascade = [CascadeType.ALL], orphanRemoval = true)
    var answers: MutableList<RecruitAnswer> = mutableListOf(),

    @Column(name = "note", length = 100)
    var note: String? = null,

    @Enumerated(EnumType.STRING)
    var evaluation: ApplicationEvaluation? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "before_canceled_status")
    var beforeCanceledStatus: ApplicationStatus? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
) {
    fun updateNote(memo: String) {
        note = memo
    }

    fun updateEvaluation(evaluation: ApplicationEvaluation) {
        this.evaluation = evaluation
    }

    fun changeStatus(newStatus: ApplicationStatus?) {
        newStatus ?: return

        when {
            newStatus == ApplicationStatus.CANCELED && status != ApplicationStatus.CANCELED -> {
                beforeCanceledStatus = status
                status = ApplicationStatus.CANCELED
                resetEvaluation()
            }
            status == ApplicationStatus.CANCELED && newStatus != ApplicationStatus.CANCELED -> {
                status = newStatus
                beforeCanceledStatus = null
                resetEvaluation()
            }
            status != newStatus -> {
                status = newStatus
                resetEvaluation()
            }
        }
    }

    fun restoreFromCanceled() {
        check(status == ApplicationStatus.CANCELED) {
            "CANCELED 상태가 아닙니다."
        }

        checkNotNull(beforeCanceledStatus) {
            "복구할 이전 상태 정보가 없습니다."
        }

        status = beforeCanceledStatus!!
        beforeCanceledStatus = null
        resetEvaluation()
    }

    private fun resetEvaluation() {
        evaluation = null
    }
}
