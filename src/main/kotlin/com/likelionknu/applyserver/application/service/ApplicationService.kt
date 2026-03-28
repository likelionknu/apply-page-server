package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest
import com.likelionknu.applyserver.application.data.dto.response.ApplicationAnswerResponseDto
import com.likelionknu.applyserver.application.data.dto.response.ApplicationInfoResponseDto
import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.exception.ApplicationNotFoundException
import com.likelionknu.applyserver.application.data.exception.ApplicationStateException
import com.likelionknu.applyserver.application.data.exception.ProfileIncompleteException
import com.likelionknu.applyserver.application.data.exception.UserNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException
import com.likelionknu.applyserver.discord.service.DiscordNotificationService
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val applicationAnswerService: ApplicationAnswerService,
    private val discordNotificationService: DiscordNotificationService,
    private val userRepository: UserRepository,
    private val recruitRepository: RecruitRepository
) {

    @Transactional
    fun saveDraft(
        userId: Long,
        recruitId: Long,
        requests: List<ApplicationDraftSaveRequest>
    ): Long {
        val recruit = recruitRepository.findById(recruitId)
            .orElseThrow { ApplicationNotFoundException() }

        val now = LocalDateTime.now()
        val isOpen = !now.isBefore(recruit.startAt) && !now.isAfter(recruit.endAt)

        if (!isOpen) {
            throw GlobalException(ErrorCode.FORBIDDEN)
        }

        val application = applicationRepository.findByUserIdAndRecruitId(userId, recruitId)
            ?: applicationRepository.save(
                Application(
                    user = userRepository.findById(userId)
                        .orElseThrow { UserNotFoundException() },
                    recruit = recruit,
                    status = ApplicationStatus.DRAFT,
                    submittedAt = now
                )
            )

        if (application.status != ApplicationStatus.DRAFT) {
            throw ApplicationStateException()
        }

        applicationAnswerService.replaceAnswers(application, requests)
        application.submittedAt = LocalDateTime.now()

        discordNotificationService.sendUserDraftApplication(
            application.user.name,
            application.user.email,
            application.recruit.title
        )

        return application.id ?: throw ApplicationNotFoundException()
    }

    @Transactional(readOnly = true)
    fun getApplicationInfo(applicationId: Long): ApplicationInfoResponseDto {
        val application = applicationRepository.findById(applicationId)
            .orElseThrow { ApplicationNotFoundException() }

        val answers = applicationAnswerService.getAnswers(application).map { recruitAnswer ->
            ApplicationAnswerResponseDto(
                question = recruitAnswer.content.question,
                answer = recruitAnswer.answer
            )
        }

        val profile = application.user.profile
            ?: throw ProfileIncompleteException()

        return ApplicationInfoResponseDto(
            name = application.user.name,
            email = application.user.email,
            depart = profile.depart,
            studentId = profile.studentId,
            grade = profile.grade,
            studentStatus = profile.status?.name ?: throw ProfileIncompleteException(),
            status = application.status.toString(),
            submittedAt = application.submittedAt,
            phone = profile.phone,
            answers = answers
        )
    }
}