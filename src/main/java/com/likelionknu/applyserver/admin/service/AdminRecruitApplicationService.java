package com.likelionknu.applyserver.admin.service;

import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitApplicationResponse;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRecruitApplicationService {

    private final ApplicationRepository applicationRepository;

    public List<AdminRecruitApplicationResponse> getApplicationsByRecruit(Long recruitId) {
        List<Application> applications = applicationRepository.findAllByRecruitIdWithUserProfile(recruitId);

        return applications.stream()
                .map(a -> new AdminRecruitApplicationResponse(
                        a.getId(),
                        a.getUser().getName(),
                        a.getNote(),
                        a.getEvaluation() == null ? null : a.getEvaluation().name(),
                        a.getStatus() == null ? null : a.getStatus().name(),
                        a.getUser().getProfile() == null ? null : a.getUser().getProfile().getDepart(),
                        a.getSubmittedAt()
                ))
                .toList();
    }
}