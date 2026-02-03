package com.likelionknu.applyserver.admin.service;

import com.likelionknu.applyserver.admin.data.dto.response.AdminUserResponseDto;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
}