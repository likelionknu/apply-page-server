package com.likelionknu.applyserver.admin.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserDetailResponse {

    private String name;
    private String email;
    private String phone;

    @JsonProperty("student_id")
    private String studentId;

    private String depart;
    private Integer grade;
    private String status;
    private String role;
}