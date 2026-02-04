package com.likelionknu.applyserver.admin.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AdminUserResponseDto {

    @JsonProperty("user_id")
    private Long userId;

    private String name;
    private String email;
    private String role;
    private String depart;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("last_accessed_at")
    private LocalDateTime lastAccessedAt;
}