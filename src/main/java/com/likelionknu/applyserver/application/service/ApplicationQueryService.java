package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.dto.response.ApplicationSummaryResponse;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.exception.UserNotFoundException;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
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
}