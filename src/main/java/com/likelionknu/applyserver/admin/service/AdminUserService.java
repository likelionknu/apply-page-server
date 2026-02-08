package com.likelionknu.applyserver.admin.service;

import com.likelionknu.applyserver.admin.data.dto.response.AdminUserDetailResponse;
import com.likelionknu.applyserver.admin.data.dto.response.AdminUserResponseDto;
import com.likelionknu.applyserver.auth.data.entity.Profile;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;

    public List<AdminUserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<AdminUserResponseDto> responses = new ArrayList<>();

        for (User user : users) {
            AdminUserResponseDto dto = AdminUserResponseDto.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .depart(user.getProfile() != null ? user.getProfile().getDepart() : null)
                    .createdAt(user.getRegisteredAt())
                    .lastAccessedAt(user.getLastAccessAt())
                    .build();

            responses.add(dto);
        }

        return responses;
    }

    public AdminUserDetailResponse getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND) {});

        Profile profile = user.getProfile();

        return AdminUserDetailResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phone(profile != null ? profile.getPhone() : null)
                .studentId(profile != null ? profile.getStudentId() : null)
                .depart(profile != null ? profile.getDepart() : null)
                .grade(profile != null ? profile.getGrade() : null)
                .status(profile != null && profile.getStatus() != null ? profile.getStatus().name() : null)
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND) {});
        userRepository.delete(user);
    }
}