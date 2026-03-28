package com.likelionknu.applyserver.recruit.service

import com.likelionknu.applyserver.auth.data.entity.User
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import com.likelionknu.applyserver.common.security.SecurityUtil
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitAvailabilityResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitDetailResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitListResponse
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitQuestionResponse
import com.likelionknu.applyserver.recruit.data.entity.Recruit
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class RecruitService(
    private val recruitRepository: RecruitRepository,
    private val recruitContentRepository: RecruitContentRepository,
    private val recruitAnswerRepository: RecruitAnswerRepository,
    private val userRepository: UserRepository,
    private val applicationRepository: ApplicationRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getRecruits(): List<RecruitListResponse> {
        return recruitRepository.findAll()
            .map(RecruitListResponse::from)
    }

    fun checkAvailability(recruitId: Long): RecruitAvailabilityResponse {
        val email = SecurityUtil.getUsername()
        val user: User = userRepository.findByEmail(email)
            ?: throw object : GlobalException(ErrorCode.NOT_FOUND) {}

        val recruit: Recruit = recruitRepository.findById(recruitId)
            .orElseThrow { object : GlobalException(ErrorCode.NOT_FOUND) {} }

        log.info("[checkAvailability] 모집 공고 지원 가능 여부 조회: {} {}", recruit.title, user.email)

        val now = LocalDateTime.now()
        val isOpen = !now.isBefore(recruit.startAt) && !now.isAfter(recruit.endAt)

        val existDraft = applicationRepository.existsByUserIdAndRecruitIdAndStatus(
            user.id,
            recruitId,
            ApplicationStatus.DRAFT
        )

        val hasSubmitted = applicationRepository.existsByUserIdAndRecruitIdAndStatusNot(
            user.id,
            recruitId,
            ApplicationStatus.DRAFT
        )

        val profile = user.profile
        val profileCompleted =
            profile != null &&
                    profile.studentId != null &&
                    profile.depart != null &&
                    profile.phone != null &&
                    profile.grade != null &&
                    profile.status != null

        val availableApply = isOpen && !hasSubmitted && profileCompleted

        return RecruitAvailabilityResponse(
            availableApply = availableApply,
            existDraft = existDraft
        )
    }

    fun getRecruitQuestions(recruitId: Long): RecruitDetailResponse {
        val recruit: Recruit = recruitRepository.findById(recruitId)
            .orElseThrow { object : GlobalException(ErrorCode.NOT_FOUND) {} }

        val now = LocalDateTime.now()
        val isOpen = !now.isBefore(recruit.startAt) && !now.isAfter(recruit.endAt)

        if (!isOpen) {
            throw object : GlobalException(ErrorCode.FORBIDDEN) {}
        }

        val contents: List<RecruitContent> =
            recruitContentRepository.findByRecruitIdOrderByPriorityAsc(recruitId)

        val email = SecurityUtil.getUsername()
        val user: User = userRepository.findByEmail(email)
            ?: throw object : GlobalException(ErrorCode.NOT_FOUND) {}

        log.info("[getRecruitQuestions] 공고 질문 조회: 공고 ID: {} 요청: {}", recruitId, user.email)

        val application: Application? =
            applicationRepository.findByUserIdAndRecruitId(user.id, recruitId)

        val answerMap = mutableMapOf<Long, String>()

        if (application != null) {
            val applicationId = application.id
            val answers: List<RecruitAnswer> = recruitAnswerRepository.findByApplicationId(applicationId)

            for (answer in answers) {
                answerMap[answer.content.id!!] = answer.answer
            }
        }

        val questionList = contents.map { content ->
            RecruitQuestionResponse(
                id = content.id!!,
                question = content.question,
                savedAnswer = answerMap[content.id]
            )
        }

        return RecruitDetailResponse(
            title = recruit.title,
            startAt = recruit.startAt.toString(),
            endAt = recruit.endAt.toString(),
            questions = questionList
        )
    }
}