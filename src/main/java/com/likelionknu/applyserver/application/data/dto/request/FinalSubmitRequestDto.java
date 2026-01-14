package com.likelionknu.applyserver.application.data.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FinalSubmitRequestDto(
        @NotEmpty @Valid
        List<Item> items
) {
    public record Item(
            @NotNull Long questionId,
            @NotBlank String answer
    ) {}
}