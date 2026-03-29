package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.dto.request.FinalSubmitRequestDto
import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer
import com.likelionknu.applyserver.application.data.exception.EmptyAnswerException
import com.likelionknu.applyserver.application.data.exception.InvalidApplicationQuestionException
import com.likelionknu.applyserver.application.data.exception.ProfileIncompleteException
import com.likelionknu.applyserver.application.data.exception.RecruitContentNotFoundException
import com.likelionknu.applyserver.application.data.exception.RecruitIsNotOpenedException
import com.likelionknu.applyserver.application.data.exception.RecruitNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import com.likelionknu.applyserver.discord.data.service.DiscordNotificationService
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository
import java.time.LocalDateTime

class ApplicationFinalSubmitService(
    private val applicationRepository: ApplicationRepository,
    private val recruitAnswerRepository: RecruitAnswerRepository,
    private val recruitContentRepository: RecruitContentRepository,
    private val userRepository: UserRepository,
    private val recruitRepository: RecruitRepository,
//    private val mailService: MailService,
    private val discordNotificationService: DiscordNotificationService
) {
    fun finalSubmit(
        email: String, request: FinalSubmitRequestDto
    ) {
        if (request.items.isEmpty()) throw EmptyAnswerException()

        val user = userRepository.findByEmail(email)
        val profile = user.profile

        val profileCompleted = profile != null &&
                profile.studentId != null &&
                profile.depart != null &&
                profile.phone != null &&
                profile.grade != null &&
                profile.status != null

        if (!profileCompleted) throw ProfileIncompleteException()

        val recruit = recruitRepository.findById(request.recruitId)
            .orElseThrow { RecruitNotFoundException() }

        val now = LocalDateTime.now()
        val isOpen = !now.isBefore(recruit.startAt) && !now.isAfter(recruit.endAt)
        if (!isOpen) throw RecruitIsNotOpenedException()

        val existingApplication = applicationRepository.findByUserIdAndRecruitId(user.id, request.recruitId)

        if (existingApplication != null && existingApplication.status != ApplicationStatus.DRAFT) {
            throw IllegalStateException("이미 제출되었거나 처리 중인 지원서는 최종 제출을 다시 할 수 없습니다. 회수 취소(restore)만 가능합니다.")
        }

        val application = existingApplication ?: applicationRepository.save(
            Application(
                recruit = recruit,
                user = user,
                status = ApplicationStatus.DRAFT,
                submittedAt = now,
            )
        )

        val requiredContents = recruitContentRepository.findAllByRecruit_IdAndRequiredTrue(recruit.id)
        val submittedQuestionIds = request.items.map { it.questionId }.toSet()

        requiredContents.forEach { required ->
            if (required.id !in submittedQuestionIds) throw EmptyAnswerException()
        }

        recruitAnswerRepository.deleteByApplication_Id(application.id)

        val answers = request.items.map { item ->
            val content = recruitContentRepository.findById(item.questionId)
                .orElseThrow { RecruitContentNotFoundException() }

            if (request.recruitId != content.recruit?.id) throw InvalidApplicationQuestionException()

            RecruitAnswer(
                application = application,
                content = content,
                answer = item.answer
            )
        }

        recruitAnswerRepository.saveAll(answers)
        application.submittedAt = LocalDateTime.now()
        application.changeStatus(ApplicationStatus.SUBMITTED)

        // TODO: MailService 리펙토링 완료 시 추가
//        val mailContentList = listOf(
//            MailContent(key = "userName", value = application.user.name),
//            MailContent(key = "part", value = application.recruit.title)
//        )
//
//        mailService.sendMail(
//            MailRequestDto(
//                user = null,
//                email = application.user.email,
//                title = "지원이 완료되었습니다!",
//                template = "apply-success",
//                dataList = mailContentList
//            )
//        )

        discordNotificationService.sendUserSubmittedApplication(
            application.user.name,
            application.user.email,
            application.recruit.title
        )
    }
}