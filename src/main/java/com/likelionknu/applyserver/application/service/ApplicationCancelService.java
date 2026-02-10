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
                .findByUserIdAndRecruitId(userId, recruitId)
                .orElseThrow(() -> new IllegalStateException("지원서를 찾을 수 없습니다."));

        ApplicationStatus currentStatus = application.getStatus();

        if (currentStatus == ApplicationStatus.DRAFT ||
                currentStatus == ApplicationStatus.SUBMITTED) {
            throw new IllegalStateException("해당 단계에서는 지원서를 회수할 수 없습니다.");
        }

        if (currentStatus == ApplicationStatus.CANCELED) {
            throw new IllegalStateException("이미 회수된 지원서입니다.");
        }

        application.setBeforeCanceledStatus(currentStatus);
        application.setStatus(ApplicationStatus.CANCELED);
    }

    public void restore(Long userId, Long recruitId) {
        Application application = applicationRepository
                .findByUserIdAndRecruitIdAndStatus(
                        userId, recruitId, ApplicationStatus.CANCELED
                )
                .orElseThrow(() -> new IllegalStateException("복구 가능한 지원서가 없습니다."));

        ApplicationStatus beforeStatus = application.getBeforeCanceledStatus();

        if (beforeStatus == null) {
            throw new IllegalStateException("복구할 이전 상태 정보가 없습니다.");
        }

        application.setStatus(beforeStatus);

        application.setBeforeCanceledStatus(null);
    }
}