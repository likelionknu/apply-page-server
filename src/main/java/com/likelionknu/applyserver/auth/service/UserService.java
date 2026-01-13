package com.likelionknu.applyserver.auth.service;

import com.likelionknu.applyserver.auth.data.dto.request.ModifyProfileRequestDto;
import com.likelionknu.applyserver.auth.data.dto.response.ProfileResponseDto;
import com.likelionknu.applyserver.auth.data.entity.Profile;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public ProfileResponseDto modifyUsersProfile(String email, ModifyProfileRequestDto modifyProfileRequestDto) {
        User user = userRepository.findByEmail(email);
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

        return ProfileResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .profileUrl(user.getProfileUrl())
                .depart(user.getProfile().getDepart())
                .studentId(user.getProfile().getStudentId())
                .grade(user.getProfile().getGrade())
                .phone(user.getProfile().getPhone())
                .build();
    }

    public ProfileResponseDto getUsersProfile(String email) {
        User user = userRepository.findByEmail(email);

        return ProfileResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .profileUrl(user.getProfileUrl())
                .depart(user.getProfile().getDepart())
                .studentId(user.getProfile().getStudentId())
                .grade(user.getProfile().getGrade())
                .phone(user.getProfile().getPhone())
                .build();
    }

    public void deleteUsersProfile(String email) {
        // TODO: 다른 엔티티에 대한 사용자 데이터 삭제 로직 추가 필요
        User user = userRepository.findByEmail(email);
        userRepository.delete(user);
    }
}
