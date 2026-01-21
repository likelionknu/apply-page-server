package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationAnswerService applicationAnswerService;

    @Transactional
    public void saveDraft(Long userId, Long applicationId, List<ApplicationDraftSaveRequest> requests) {
        Application application = applicationRepository.findByIdAndUserId(applicationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));

        applicationAnswerService.replaceAnswers(application, requests);

        application.setStatus(ApplicationStatus.DRAFT);
        applicationRepository.save(application);
    }
}