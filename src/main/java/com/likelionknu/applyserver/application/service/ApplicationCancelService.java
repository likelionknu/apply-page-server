package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplicationCancelService {

    private final ApplicationRepository applicationRepository;

    public void cancel(Long userId, Long recruitId) {
        Application application = applicationRepository
                .findByUserIdAndRecruitId(userId, recruitId)
                .orElseThrow(() -> new IllegalStateException("회수할 지원서가 없습니다."));

        if (application.getStatus() == ApplicationStatus.CANCELED) {
            return;
        }

        application.changeStatus(ApplicationStatus.CANCELED);

        log.info("[cancel] 지원서 회수 요청: {} {}", application.getRecruit().getTitle(), application.getUser().getEmail());
    }

    public void restore(Long userId, Long recruitId) {
        Application application = applicationRepository
                .findByUserIdAndRecruitIdAndStatus(userId, recruitId, ApplicationStatus.CANCELED)
                .orElseThrow(() -> new IllegalStateException("복구 가능한 지원서가 없습니다."));

        application.restoreFromCanceled();
    }
}