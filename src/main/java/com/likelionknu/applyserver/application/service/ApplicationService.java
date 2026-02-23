package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest;
import com.likelionknu.applyserver.application.data.dto.response.ApplicationAnswerResponseDto;
import com.likelionknu.applyserver.application.data.dto.response.ApplicationInfoResponseDto;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationAnswerService applicationAnswerService;

    private final UserRepository userRepository;
    private final RecruitRepository recruitRepository;

    @Transactional
    public Long saveDraft(Long userId, Long recruitId, List<ApplicationDraftSaveRequest> requests) {

        Application application = applicationRepository
                .findByUserIdAndRecruitId(userId, recruitId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. userId=" + userId));

                    Recruit recruit = recruitRepository.findById(recruitId)
                            .orElseThrow(() -> new IllegalArgumentException("모집 공고를 찾을 수 없습니다. recruitId=" + recruitId));

                    Application newApp = new Application();
                    newApp.setUser(user);
                    newApp.setRecruit(recruit);
                    newApp.changeStatus(ApplicationStatus.DRAFT);
                    newApp.setSubmittedAt(LocalDateTime.now());
                    return applicationRepository.save(newApp);
                });

        log.info("[saveDraft] 사용자 지원서 임시 저장: {} {}", application.getRecruit().getTitle(), application.getUser().getEmail());

        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("임시 저장은 DRAFT 상태에서만 가능합니다.");
        }

        applicationAnswerService.replaceAnswers(application, requests);
        application.setSubmittedAt(LocalDateTime.now());

        return application.getId();
    }

    @Transactional
    public ApplicationInfoResponseDto getApplicationInfo(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));

        log.info("[getApplicationInfo] 사용자 지원서 조회: {} {}", application.getRecruit().getTitle(), application.getUser().getEmail());

        List<ApplicationAnswerResponseDto> applicationAnswerResponseDtoList = new ArrayList<>();
        List<RecruitAnswer> recruitAnswerList = applicationAnswerService.getAnswers(application);

        for (RecruitAnswer recruitAnswer : recruitAnswerList) {
            applicationAnswerResponseDtoList.add(
                    ApplicationAnswerResponseDto.builder()
                            .question(recruitAnswer.getContent().getQuestion())
                            .answer(recruitAnswer.getAnswer())
                            .build()
            );
        }

        return ApplicationInfoResponseDto.builder()
                .name(application.getUser().getName())
                .email(application.getUser().getEmail())
                .depart(application.getUser().getProfile().getDepart())
                .studentId(application.getUser().getProfile().getStudentId())
                .grade(application.getUser().getProfile().getGrade())
                .studentStatus(application.getUser().getProfile().getStatus().getDisplayName())
                .status(application.getStatus().toString())
                .submittedAt(application.getSubmittedAt())
                .phone(application.getUser().getProfile().getPhone())
                .answers(applicationAnswerResponseDtoList)
                .build();
    }
}