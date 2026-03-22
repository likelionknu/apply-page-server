package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

        LocalDateTime now = LocalDateTime.now();
        boolean isOpen = !now.isBefore(application.getRecruit().getStartAt())
                && !now.isAfter(application.getRecruit().getEndAt());

        if (!isOpen) {
            throw new GlobalException(ErrorCode.FORBIDDEN) {};
        }

        application.restoreFromCanceled();
    }
}