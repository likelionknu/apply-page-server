package com.likelionknu.applyserver.application.service

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest
import com.likelionknu.applyserver.application.data.entity.Application
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicationAnswerService(
    private val recruitAnswerRepository: RecruitAnswerRepository,
    private val recruitContentRepository: RecruitContentRepository
) {

    @Transactional
    fun replaceAnswers(application: Application, requests: List<ApplicationDraftSaveRequest>?) {
        recruitAnswerRepository.deleteByApplication_Id(application.id)

        if (requests == null) return

        for (req in requests) {
            val questionId = req.questionId ?: continue
            val answer = req.answer?.trim()

            if (answer.isNullOrEmpty()) continue

            val content: RecruitContent = recruitContentRepository.findById(questionId)
                .orElseThrow {
                    IllegalArgumentException("질문을 찾을 수 없습니다. questionId=$questionId")
                }

            val recruitAnswer = RecruitAnswer(
                application = application,
                content = content,
                answer = answer
            )

            recruitAnswerRepository.save(recruitAnswer)
        }
    }

    @Transactional(readOnly = true)
    fun getAnswers(application: Application): List<RecruitAnswer> {
        return recruitAnswerRepository.findByApplication(application)
    }
}