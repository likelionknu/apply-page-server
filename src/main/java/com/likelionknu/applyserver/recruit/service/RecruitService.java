package com.likelionknu.applyserver.recruit.service;

import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository;
import com.likelionknu.applyserver.auth.data.entity.Profile;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;
import com.likelionknu.applyserver.common.security.SecurityUtil;
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitAvailabilityResponse;
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitListResponse;
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitQuestionResponse;
import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent;
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository;
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final RecruitRepository recruitRepository;
    private final RecruitContentRepository recruitContentRepository;
    private final RecruitAnswerRepository recruitAnswerRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    @Transactional(readOnly = true)
    public List<RecruitListResponse> getRecruits() {
        return recruitRepository.findAll()
                .stream()
                .map(RecruitListResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecruitAvailabilityResponse checkAvailability(Long recruitId) {

        String email = SecurityUtil.getUsername();
        User user = userRepository.findByEmail(email);

        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND) {});

        LocalDateTime now = LocalDateTime.now();

        boolean isOpen = !now.isBefore(recruit.getStartAt()) && !now.isAfter(recruit.getEndAt());

        // 임시 저장(DRAFT) 지원서 존재 여부
        boolean existDraft =
                applicationRepository.existsByUserIdAndRecruitIdAndStatus(
                        user.getId(),
                        recruitId,
                        ApplicationStatus.DRAFT
                );

        // 이미 제출된 지원서 존재 여부 (DRAFT 제외)
        boolean hasSubmitted =
                applicationRepository.existsByUserIdAndRecruitIdAndStatusNot(
                        user.getId(),
                        recruitId,
                        ApplicationStatus.DRAFT
                );

        // 프로필 완성 여부 확인
        Profile profile = user.getProfile();
        boolean profileCompleted =
                profile != null &&
                        profile.getStudentId() != null &&
                        profile.getDepart() != null &&
                        profile.getPhone() != null &&
                        profile.getGrade() != null &&
                        profile.getStatus() != null;

        boolean availableApply = isOpen && !hasSubmitted && profileCompleted;

        return new RecruitAvailabilityResponse(availableApply, existDraft);
    }

    @Transactional(readOnly = true)
    public List<RecruitQuestionResponse> getRecruitQuestions(Long recruitId) {

        List<RecruitContent> contents = recruitContentRepository.findByRecruitIdOrderByPriorityAsc(recruitId);

        String email = SecurityUtil.getUsername();
        User user = userRepository.findByEmail(email);

        // 해당 공고에 대한 지원서 조회 (없을 수도 있음)
        Optional<Application> applicationOpt = applicationRepository.findByUserIdAndRecruitId(user.getId(), recruitId);

        // 답변 구성
        Map<Long, String> answerMap = new HashMap<>();

        if (applicationOpt.isPresent()) {
            List<RecruitAnswer> answers = recruitAnswerRepository.findByApplicationId(applicationOpt.get().getId());

            for (RecruitAnswer answer : answers) {
                answerMap.put(
                        answer.getContent().getId(),
                        answer.getAnswer()
                );
            }
        }

        // 질문
        List<RecruitQuestionResponse> responses = new ArrayList<>();

        for (RecruitContent content : contents) {
            responses.add(
                    new RecruitQuestionResponse(
                            content.getId(),
                            content.getQuestion(),
                            answerMap.get(content.getId())
                    )
            );
        }
        return responses;
    }
}