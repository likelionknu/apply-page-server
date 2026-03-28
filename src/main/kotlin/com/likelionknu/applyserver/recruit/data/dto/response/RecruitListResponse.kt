package com.likelionknu.applyserver.recruit.data.dto.response

import com.likelionknu.applyserver.recruit.data.entity.Recruit
import com.likelionknu.applyserver.recruit.data.enums.RecruitStatus
import java.time.LocalDateTime

data class RecruitListResponse(
    val id: Long,
    val title: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val status: RecruitStatus
) {
    companion object {
        fun from(recruit: Recruit): RecruitListResponse {
            val now = LocalDateTime.now()

            val status = when {
                now.isBefore(recruit.startAt) -> RecruitStatus.UPCOMING
                now.isAfter(recruit.endAt) -> RecruitStatus.CLOSED
                else -> RecruitStatus.OPEN
            }

            return RecruitListResponse(
                id = recruit.id!!,
                title = recruit.title,
                startAt = recruit.startAt,
                endAt = recruit.endAt,
                status = status
            )
        }
    }
}