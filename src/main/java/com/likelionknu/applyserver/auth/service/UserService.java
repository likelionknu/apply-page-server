package com.likelionknu.applyserver.auth.service;

import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository;
import com.likelionknu.applyserver.auth.data.dto.request.ModifyProfileRequestDto;
import com.likelionknu.applyserver.auth.data.dto.response.ProfileResponseDto;
import com.likelionknu.applyserver.auth.data.entity.Profile;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import com.likelionknu.applyserver.auth.exception.UserNotFoundException;
import com.likelionknu.applyserver.mail.data.entity.MailHistory;
import com.likelionknu.applyserver.mail.data.repository.MailHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final MailHistoryRepository mailHistoryRepository;
    private final ApplicationRepository applicationRepository;
    private final RecruitAnswerRepository recruitAnswerRepository;

    @Transactional
    public ProfileResponseDto modifyUsersProfile(String email, ModifyProfileRequestDto modifyProfileRequestDto) {
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new UserNotFoundException();
        }

        Profile profile = user.getProfile();

        if(modifyProfileRequestDto.getName() != null) {
            user.setName(modifyProfileRequestDto.getName());
        }

        if(modifyProfileRequestDto.getDepart() != null) {
            profile.setDepart(modifyProfileRequestDto.getDepart());
        }

        if(modifyProfileRequestDto.getStudentId() != null) {
            profile.setStudentId(modifyProfileRequestDto.getStudentId());
        }

        if(modifyProfileRequestDto.getGrade() != null) {
            profile.setGrade(modifyProfileRequestDto.getGrade());
        }

        if(modifyProfileRequestDto.getPhone() != null) {
            profile.setPhone(modifyProfileRequestDto.getPhone());
        }

        if(modifyProfileRequestDto.getStatus() != null) {
            profile.setStatus(modifyProfileRequestDto.getStatus());
        }

        log.info("[modifyUsersProfile] 사용자 상세 정보 수정: {}", email);

        return ProfileResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .profileUrl(user.getProfileUrl())
                .depart(user.getProfile().getDepart())
                .studentId(user.getProfile().getStudentId())
                .grade(user.getProfile().getGrade())
                .phone(user.getProfile().getPhone())
                .status(user.getProfile().getStatus() == null ? null : user.getProfile().getStatus().getDisplayName())
                .build();
    }

    public ProfileResponseDto getUsersProfile(String email) {
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new UserNotFoundException();
        }

        log.info("[getUsersProfile] 사용자 상세 정보 조회: {}", email);

        return ProfileResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .profileUrl(user.getProfileUrl())
                .depart(user.getProfile().getDepart())
                .studentId(user.getProfile().getStudentId())
                .grade(user.getProfile().getGrade())
                .phone(user.getProfile().getPhone())
                .status(user.getProfile().getStatus() == null ? null : user.getProfile().getStatus().getDisplayName())
                .build();
    }

    public void deleteUsersProfile(String email) {
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new UserNotFoundException();
        }

        List<MailHistory> mailHistoryList = mailHistoryRepository.findAllByUser(user);

        for(MailHistory mailHistory : mailHistoryList) {
            mailHistory.setUser(null);
            mailHistoryRepository.save(mailHistory);
        }

        List<Application> applicationList = applicationRepository.findAllByUser(user);

        for(Application application : applicationList) {
            List<RecruitAnswer> recruitAnswerList = recruitAnswerRepository.findAllByApplication(application);
            recruitAnswerRepository.deleteAll(recruitAnswerList);
        }

        log.info("[deleteUsersProfile] 사용자 회원 탈퇴: {}", email);

        applicationRepository.deleteAll(applicationList);

        userRepository.delete(user);
    }
}
