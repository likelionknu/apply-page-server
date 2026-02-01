package com.likelionknu.applyserver.application.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationInfoResponseDto {
    private String name;
    private String email;
    private String depart;

    @JsonProperty("student_id")
    private String studentId;

    private Integer grade;

    @JsonProperty("student_status")
    private String studentStatus;

    private String status;

    @JsonProperty("submitted_at")
    private LocalDateTime submittedAt;
    private String phone;
    private List<ApplicationAnswerResponseDto> answers;
}
