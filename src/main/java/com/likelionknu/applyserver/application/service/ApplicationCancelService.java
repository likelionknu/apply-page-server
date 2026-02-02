package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationCancelService {

    private final ApplicationRepository applicationRepository;

    public void cancel(Long userId, Long recruitId) {
        Application application = applicationRepository
                .findByUserIdAndRecruitIdAndStatus(
                        userId, recruitId, ApplicationStatus.UNDER_DOCUMENT_REVIEW
                )
                .orElseThrow(() -> new IllegalStateException("회수 가능한 지원서가 없습니다."));

        application.setStatus(ApplicationStatus.CANCELED);
    }

    public void restore(Long userId, Long recruitId) {
        Application application = applicationRepository
                .findByUserIdAndRecruitIdAndStatus(
                        userId, recruitId, ApplicationStatus.CANCELED
                )
                .orElseThrow(() -> new IllegalStateException("복구 가능한 지원서가 없습니다."));

        application.setStatus(ApplicationStatus.UNDER_DOCUMENT_REVIEW);
    }
}