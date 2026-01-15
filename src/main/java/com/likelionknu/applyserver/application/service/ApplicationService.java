package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.dto.request.FinalSubmitRequestDto;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent;
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final RecruitAnswerRepository recruitAnswerRepository;
    private final RecruitContentRepository recruitContentRepository;

    public void finalSubmit(FinalSubmitRequestDto request) {
        // TODO: (1) security 연동 후 user 조회
        // TODO: (2) 상세 프로필 작성 여부 검증

        Application application = applicationRepository.findFirstByStatus(ApplicationStatus.DRAFT)
                .orElseThrow(() -> new IllegalStateException("임시 저장된 지원서가 없습니다."));

        recruitAnswerRepository.deleteAllByApplication_Id(application.getId());

        for (FinalSubmitRequestDto.Item item : request.items()) {
            RecruitContent content = recruitContentRepository.findById(item.questionId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문항입니다."));

            RecruitAnswer answer = RecruitAnswer.builder()
                    .application(application)
                    .content(content)
                    .answer(item.answer())
                    .build();

            recruitAnswerRepository.save(answer);
        }

        application.setStatus(ApplicationStatus.SUBMITTED);
    }


}
