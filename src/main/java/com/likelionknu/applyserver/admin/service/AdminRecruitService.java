package com.likelionknu.applyserver.admin.service;

import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitDetailResponse;
import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent;
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository;
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRecruitService {

    private final RecruitRepository recruitRepository;
    private final RecruitContentRepository recruitContentRepository;

    public AdminRecruitDetailResponse getRecruitDetail(Long recruitId) {
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new IllegalStateException("모집 공고를 찾을 수 없습니다."));

        List<RecruitContent> contents =
                recruitContentRepository.findByRecruitIdOrderByPriorityAsc(recruitId);

        List<String> questions = contents.stream()
                .map(RecruitContent::getQuestion)
                .toList();

        return AdminRecruitDetailResponse.builder()
                .title(recruit.getTitle())
                .startAt(recruit.getStartAt())
                .endAt(recruit.getEndAt())
                .questions(questions)
                .build();
    }
}