package com.likelionknu.applyserver.recruit.service;

import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.auth.data.entity.Profile;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.auth.data.enums.StudentStatus;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;
import com.likelionknu.applyserver.common.security.SecurityUtil;
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitAvailabilityResponse;
import com.likelionknu.applyserver.recruit.data.dto.response.RecruitListResponse;
import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final RecruitRepository recruitRepository;
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

        // 1. 로그인 사용자 조회
        String email = SecurityUtil.getUsername();
        User user = userRepository.findByEmail(email);

        // 2. 모집공고 조회
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND) {});

        LocalDateTime now = LocalDateTime.now();

        // 3. 모집 기간 체크
        boolean isOpen =
                !now.isBefore(recruit.getStartAt()) && !now.isAfter(recruit.getEndAt());

        // 4. 임시 저장(DRAFT) 지원서 존재 여부
        boolean existDraft =
                applicationRepository.existsByUserIdAndRecruitIdAndStatus(
                        user.getId(),
                        recruitId,
                        ApplicationStatus.DRAFT
                );

        // 5. 이미 제출된 지원서 존재 여부 (DRAFT 제외)
        boolean hasSubmitted =
                applicationRepository.existsByUserIdAndRecruitIdAndStatusNot(
                        user.getId(),
                        recruitId,
                        ApplicationStatus.DRAFT
                );

        // 6. 프로필 완성 여부
        Profile profile = user.getProfile();
        boolean profileCompleted =
                profile != null &&
                        profile.getStudentId() != null &&
                        profile.getDepart() != null &&
                        profile.getPhone() != null &&
                        profile.getGrade() != null &&
                        profile.getStatus() == StudentStatus.ATTENDING;

        // 7. 최종 지원 가능 여부 판단
        boolean availableApply =
                isOpen &&
                        !hasSubmitted &&
                        profileCompleted;

        return new RecruitAvailabilityResponse(availableApply, existDraft);
    }
}