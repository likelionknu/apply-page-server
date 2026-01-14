package com.likelionknu.applyserver.recruit.data.dto.response;

import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import com.likelionknu.applyserver.recruit.data.enums.RecruitStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RecruitListResponse {

    private Long id;
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private RecruitStatus status;

    public static RecruitListResponse from(Recruit recruit) {
        LocalDateTime now = LocalDateTime.now();

        RecruitStatus status =
                now.isAfter(recruit.getEndAt())
                        ? RecruitStatus.CLOSED
                        : RecruitStatus.OPEN;

        return new RecruitListResponse(
                recruit.getId(),
                recruit.getTitle(),
                recruit.getStartAt(),
                recruit.getEndAt(),
                status
        );
    }
}