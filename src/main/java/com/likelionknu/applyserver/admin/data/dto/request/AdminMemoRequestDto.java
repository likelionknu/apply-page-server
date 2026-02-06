package com.likelionknu.applyserver.admin.data.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AdminMemoRequestDto {
    @Size(max = 100)
    private String memo;
}
