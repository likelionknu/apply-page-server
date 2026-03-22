package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.dto.response.ApplicationDetailResponse;
import com.likelionknu.applyserver.application.data.dto.response.ApplicationSummaryResponse;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer;
import com.likelionknu.applyserver.application.data.exception.ApplicationNotFoundException;
import com.likelionknu.applyserver.application.data.exception.InvalidApplicationAccessException;
import com.likelionknu.applyserver.application.data.exception.UserNotFoundException;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationQueryService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final RecruitAnswerRepository recruitAnswerRepository;

    @Transactional(readOnly = true)
    public List<ApplicationSummaryResponse> getMyApplications(String email) {
        User user = userRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new UserNotFoundException());

        List<Application> applications = applicationRepository.findAllWithRecruitByUserId(user.getId());

        return applications.stream()
                .map(a -> new ApplicationSummaryResponse(
                        a.getId(),
                        a.getRecruit().getTitle(),
                        a.getStatus().name(),
                        a.getRecruit().getStartAt(),
                        a.getRecruit().getEndAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ApplicationDetailResponse getApplicationDetail(String email, Long applicationId) {
        User user = userRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new UserNotFoundException());

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException());

        if (!application.getUser().getId().equals(user.getId())) {
            throw new InvalidApplicationAccessException();
        }

        List<RecruitAnswer> answers =
                recruitAnswerRepository.findByApplicationId(applicationId);

        return new ApplicationDetailResponse(
                application.getId(),
                application.getRecruit().getId(),
                application.getRecruit().getTitle(),
                application.getStatus().name(),
                application.getRecruit().getStartAt(),
                application.getRecruit().getEndAt(),
                application.getSubmittedAt(),
                answers.stream()
                        .map(a -> new ApplicationDetailResponse.ApplicationAnswerResponse(
                                a.getContent().getId(),
                                a.getContent().getQuestion(),
                                a.getAnswer()
                        ))
                        .toList()
        );
    }

}