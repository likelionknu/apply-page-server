package com.likelionknu.applyserver.auth.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponseDto {
    private String email;
    private String name;

    @JsonProperty("profile_url")
    private String profileUrl;

    private String depart;

    @JsonProperty("student_id")
    private String studentId;

    private Integer grade;
    private String phone;
    private String status;
}
