package com.likelionknu.applyserver.auth.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyProfileRequestDto {
    private String name;
    private String depart;

    @JsonProperty("student_id")
    private String studentId;

    private Integer grade;
    private String phone;
}
