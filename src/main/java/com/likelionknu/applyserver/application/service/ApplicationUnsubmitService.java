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
public class ApplicationUnsubmitService {

    private final ApplicationRepository applicationRepository;

    public void unsubmit(Long userId, Long recruitId) {
        Application application = applicationRepository
                .findByUserIdAndRecruitIdAndStatus(
                        userId, recruitId, ApplicationStatus.SUBMITTED
                )
                .orElseThrow(() ->
                        new IllegalStateException("제출 취소 가능한 지원서가 없습니다.")
                );

        application.setStatus(ApplicationStatus.DRAFT);
    }
}