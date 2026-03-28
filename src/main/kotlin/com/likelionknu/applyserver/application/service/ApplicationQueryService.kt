package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.dto.response.ApplicationDetailResponse
import com.likelionknu.applyserver.application.data.dto.response.ApplicationSummaryResponse
import com.likelionknu.applyserver.application.data.exception.ApplicationNotFoundException
import com.likelionknu.applyserver.application.data.exception.InvalidApplicationAccessException
import com.likelionknu.applyserver.application.data.exception.UserNotFoundException
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository
import com.likelionknu.applyserver.auth.data.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ApplicationQueryService(
    private val applicationRepository: ApplicationRepository,
    private val userRepository: UserRepository,
    private val recruitAnswerRepository: RecruitAnswerRepository
) {

    fun getMyApplications(email: String): List<ApplicationSummaryResponse> {
        val user = userRepository.findOptionalByEmail(email)
            .orElseThrow { UserNotFoundException() }

        val applications = applicationRepository.findAllWithRecruitByUserId(user.id)

        return applications.map { application ->
            ApplicationSummaryResponse(
                applicationId = application.id ?: throw ApplicationNotFoundException(),
                recruitTitle = application.recruit.title,
                status = application.status.name,
                startAt = application.recruit.startAt,
                endAt = application.recruit.endAt
            )
        }
    }

    fun getApplicationDetail(email: String, applicationId: Long): ApplicationDetailResponse {
        val user = userRepository.findOptionalByEmail(email)
            .orElseThrow { UserNotFoundException() }

        val application = applicationRepository.findById(applicationId)
            .orElseThrow { ApplicationNotFoundException() }

        if (application.user.id != user.id) {
            throw InvalidApplicationAccessException()
        }

        val answers = recruitAnswerRepository.findByApplicationId(applicationId)

        return ApplicationDetailResponse(
            applicationId = application.id ?: throw ApplicationNotFoundException(),
            recruitId = application.recruit.id ?: throw ApplicationNotFoundException(),
            recruitTitle = application.recruit.title,
            status = application.status.name,
            startAt = application.recruit.startAt,
            endAt = application.recruit.endAt,
            submittedAt = application.submittedAt,
            answers = answers.map { answer ->
                ApplicationDetailResponse.ApplicationAnswerResponse(
                    questionId = answer.content.id ?: throw ApplicationNotFoundException(),
                    question = answer.content.question,
                    answer = answer.answer
                )
            }
        )
    }
}