package com.likelionknu.applyserver.auth.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likelionknu.applyserver.auth.data.enums.StudentStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyProfileRequestDto {
    @Size(min = 2, max = 4)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣]*$")
    private String name;

    @Size(min = 4, max = 20)
    private String depart;

    @Size(min = 8, max = 9)
    @Pattern(regexp = "^[0-9]*$")
    @JsonProperty("student_id")
    private String studentId;

    @Max(value = 4)
    @Positive
    private Integer grade;

    @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$")
    private String phone;

    private StudentStatus status;
}
