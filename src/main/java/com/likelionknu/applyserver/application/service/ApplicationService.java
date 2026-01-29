package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationAnswerService applicationAnswerService;

    private final UserRepository userRepository;
    private final RecruitRepository recruitRepository;

    @Transactional
    public Long saveDraft(Long userId, Long recruitId, List<ApplicationDraftSaveRequest> requests) {

        Application application = applicationRepository
                .findByUserIdAndRecruitIdAndStatus(userId, recruitId, ApplicationStatus.DRAFT)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. userId=" + userId));

                    Recruit recruit = recruitRepository.findById(recruitId)
                            .orElseThrow(() -> new IllegalArgumentException("모집 공고를 찾을 수 없습니다. recruitId=" + recruitId));

                    Application newApp = new Application();
                    newApp.setUser(user);
                    newApp.setRecruit(recruit);
                    newApp.setStatus(ApplicationStatus.DRAFT);
                    newApp.setSubmittedAt(LocalDateTime.now());

                    return applicationRepository.save(newApp);
                });

        applicationAnswerService.replaceAnswers(application, requests);

        application.setStatus(ApplicationStatus.DRAFT);
        application.setSubmittedAt(LocalDateTime.now());

        return application.getId();
    }
}