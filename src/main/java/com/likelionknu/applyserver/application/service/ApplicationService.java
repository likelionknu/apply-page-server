package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.dto.request.ApplicationDraftSaveRequest;
import com.likelionknu.applyserver.application.data.dto.response.ApplicationAnswerResponseDto;
import com.likelionknu.applyserver.application.data.dto.response.ApplicationInfoResponseDto;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional
    public ApplicationInfoResponseDto getApplicationInfo(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));

        List<ApplicationAnswerResponseDto> applicationAnswerResponseDtoList = new ArrayList<>();
        List<RecruitAnswer> recruitAnswerList = applicationAnswerService.getAnswers(application);

        for(RecruitAnswer recruitAnswer : recruitAnswerList) {
            applicationAnswerResponseDtoList.add(
                    ApplicationAnswerResponseDto.builder()
                            .question(recruitAnswer.getContent().getQuestion())
                            .answer(recruitAnswer.getAnswer())
                            .build()
            );
        }

        return ApplicationInfoResponseDto.builder()
                .name(application.getUser().getName())
                .email(application.getUser().getEmail())
                .depart(application.getUser().getProfile().getDepart())
                .studentId(application.getUser().getProfile().getStudentId())
                .grade(application.getUser().getProfile().getGrade())
                .studentStatus(application.getUser().getProfile().getStatus().getDisplayName())
                .status(application.getStatus().toString())
                .submittedAt(application.getSubmittedAt())
                .phone(application.getUser().getProfile().getPhone())
                .answers(applicationAnswerResponseDtoList)
                .build();
    }
}