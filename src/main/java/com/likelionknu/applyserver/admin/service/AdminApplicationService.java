package com.likelionknu.applyserver.admin.service;

import com.likelionknu.applyserver.admin.data.dto.request.AdminMemoRequestDto;
import com.likelionknu.applyserver.admin.data.dto.request.ApplicationStatusUpdateRequestDto;
import com.likelionknu.applyserver.admin.data.exception.InvalidRequestException;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.exception.ApplicationNotFoundException;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.auth.data.enums.ApplicationEvaluation;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminApplicationService {

    private final ApplicationRepository applicationRepository;

    @Transactional
    public void saveAdminMemo(Long applicationId, AdminMemoRequestDto request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(ApplicationNotFoundException::new);

        application.updateNote(request.getMemo());
    }

    @Transactional
    public void updateStatus(Long applicationId, ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(ApplicationNotFoundException::new);

        application.changeStatus(status);
    }

    @Transactional
    public void updateEvaluation(Long applicationId, ApplicationEvaluation evaluation) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(ApplicationNotFoundException::new);

        application.updateEvaluation(evaluation);
    }

    @Transactional
    public void patch(Long applicationId, ApplicationStatusUpdateRequestDto request) {
        boolean hasStatus = request.status() != null;
        boolean hasEvaluation = request.evaluation() != null;

        if (!hasStatus && !hasEvaluation) throw new InvalidRequestException();
        if (hasStatus && hasEvaluation) throw new InvalidRequestException();

        if (hasStatus) updateStatus(applicationId, request.status());
        else updateEvaluation(applicationId, request.evaluation());
    }

    @Transactional
    public void deleteAdminApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(ApplicationNotFoundException::new);

        applicationRepository.delete(application);
    }
}