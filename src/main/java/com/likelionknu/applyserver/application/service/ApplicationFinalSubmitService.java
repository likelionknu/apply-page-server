package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.dto.request.FinalSubmitRequestDto;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer;
import com.likelionknu.applyserver.application.data.exception.*;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository;
import com.likelionknu.applyserver.auth.data.entity.Profile;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent;
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository;
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final RecruitRepository recruitRepository;

    public void finalSubmit(String email, FinalSubmitRequestDto request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new EmptyAnswerException();
        }

        User user = userRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new UserNotFoundException());

        // 상세 프로필 검증
        Profile profile = user.getProfile();
        boolean profileCompleted =
                profile != null &&
                        profile.getStudentId() != null &&
                        profile.getDepart() != null &&
                        profile.getPhone() != null &&
                        profile.getGrade() != null &&
                        profile.getStatus() != null;

        if (!profileCompleted) {
            throw new ProfileIncompleteException();
        }

        Long recruitId = request.recruitId();

        if (applicationRepository.existsByUserIdAndRecruitIdAndStatus(
                user.getId(), recruitId, ApplicationStatus.SUBMITTED
        )) {
            throw new ApplicationAlreadySubmittedException();
        }

        Application application = applicationRepository
                .findFirstByUserAndStatus(user, ApplicationStatus.DRAFT)
                .orElseGet(() -> {
                    Recruit recruit = recruitRepository.findById(recruitId)
                            .orElseThrow(RecruitNotFoundException::new);

                    return applicationRepository.save(
                            Application.builder()
                                    .recruit(recruit)
                                    .user(user)
                                    .note("")
                                    .evaluation(ApplicationEvaluation.HOLD)
                                    .status(ApplicationStatus.DRAFT)
                                    .submittedAt(LocalDateTime.now())
                                    .build()
                    );
                });

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

            if (!recruitId.equals(content.getRecruit().getId())) {
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
