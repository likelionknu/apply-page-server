package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.dto.request.FinalSubmitRequestDto;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer;
import com.likelionknu.applyserver.application.data.exception.*;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent;
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationFinalSubmitService {
    private final ApplicationRepository applicationRepository;
    private final RecruitAnswerRepository recruitAnswerRepository;
    private final RecruitContentRepository recruitContentRepository;
    private final UserRepository userRepository;

    public void finalSubmit(String email, FinalSubmitRequestDto request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new EmptyAnswerException();
        }

        User user = userRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new UserNotFoundException());

        // TODO: 상세 프로필 작성 여부 검증

        if (applicationRepository.findFirstByUserAndStatus(user, ApplicationStatus.SUBMITTED).isPresent()) {
            throw new ApplicationAlreadySubmittedException();
        }

        Application application = applicationRepository
                .findFirstByUserAndStatus(user, ApplicationStatus.DRAFT)
                .orElseThrow(() -> new ApplicationDraftNotFoundException());

        Long recruitId = application.getRecruit().getId();

        List<RecruitContent> requiredContents = recruitContentRepository
                .findAllByRecruit_IdAndRequiredTrue(recruitId);

        Set<Long> submittedQuestionIds = new HashSet<>();
        for (FinalSubmitRequestDto.Item item : request.items()) {
            submittedQuestionIds.add(item.questionId());
        }

        for (RecruitContent required : requiredContents) {
            if (!submittedQuestionIds.contains(required.getId())) {
                throw new EmptyAnswerException();
            }
        }

        recruitAnswerRepository.deleteByApplication_Id(application.getId());

        List<RecruitAnswer> answers = new ArrayList<>();

        for (FinalSubmitRequestDto.Item item : request.items()) {
            RecruitContent content = recruitContentRepository.findById(item.questionId())
                    .orElseThrow(() -> new RecruitContentNotFoundException());

            if (!application.getRecruit().getId().equals(content.getRecruit().getId())) {
                throw new InvalidApplicationQuestionException();
            }

            answers.add(RecruitAnswer.builder()
                    .application(application)
                    .content(content)
                    .answer(item.answer())
                    .build());
        }

        recruitAnswerRepository.saveAll(answers);
        application.setStatus(ApplicationStatus.SUBMITTED);
    }
}
