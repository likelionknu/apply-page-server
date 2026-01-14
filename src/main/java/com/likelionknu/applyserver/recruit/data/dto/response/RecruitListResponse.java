package com.likelionknu.applyserver.recruit.data.dto.response;

import com.likelionknu.applyserver.recruit.data.entity.Recruit;
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

    public static RecruitListResponse from(Recruit recruit) {
        return new RecruitListResponse(
                recruit.getId(),
                recruit.getTitle(),
                recruit.getStartAt(),
                recruit.getEndAt()
        );
    }
}