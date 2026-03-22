package com.likelionknu.applyserver.application.data.entity

import com.likelionknu.applyserver.recruit.data.entity.RecruitContent
import jakarta.persistence.*

@Entity
@Table(name = "recruit_answer")
class RecruitAnswer(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    var application: Application,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    var content: RecruitContent,

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    var answer: String
) {
    fun updateAnswer(answer: String) {
        this.answer = answer
    }
}