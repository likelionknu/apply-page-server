package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer;
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository;
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent;
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationAnswerService {

    private final RecruitAnswerRepository recruitAnswerRepository;
    private final RecruitContentRepository recruitContentRepository;

    @Transactional
    public void replaceAnswers(Application application, List<ApplicationDraftSaveRequest> requests) {
        recruitAnswerRepository.deleteByApplication_Id(application.getId());

        if (requests == null) return;

        for (ApplicationDraftSaveRequest req : requests) {
            if (req == null || req.getQuestionId() == null) continue;

            String answer = req.getAnswer();
            if (answer == null || answer.trim().isEmpty()) continue;

            RecruitContent content = recruitContentRepository.findById(req.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다. questionId=" + req.getQuestionId()));

            RecruitAnswer recruitAnswer = RecruitAnswer.builder()
                    .application(application)
                    .content(content)
                    .answer(answer)
                    .build();

            recruitAnswerRepository.save(recruitAnswer);
        }
    }
}