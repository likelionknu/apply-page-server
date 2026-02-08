package com.likelionknu.applyserver.admin.service;

import com.likelionknu.applyserver.admin.data.dto.response.AdminUserResponseDto;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.enums.Role;
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

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND) {});
        userRepository.delete(user);
    }

    @Transactional
    public void updateUserRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND) {});

        Role newRole = Role.valueOf(role);
        user.setRole(newRole);
    }
}