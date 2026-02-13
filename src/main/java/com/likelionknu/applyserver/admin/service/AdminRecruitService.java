package com.likelionknu.applyserver.admin.service;

import com.likelionknu.applyserver.admin.data.dto.request.AdminRecruitUpdateRequest;
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitDetailResponse;
import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitSummaryResponse;
import com.likelionknu.applyserver.admin.data.exception.InvalidRecruitUpdateRequestException;
import com.likelionknu.applyserver.application.data.exception.RecruitNotFoundException;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent;
import com.likelionknu.applyserver.recruit.data.exception.RecruitHasApplicationException;
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository;
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminRecruitService {

    private final RecruitRepository recruitRepository;
    private final RecruitContentRepository recruitContentRepository;
    private final ApplicationRepository applicationRepository;

    public AdminRecruitDetailResponse getRecruitDetail(Long recruitId) {
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(RecruitNotFoundException::new);

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

    public List<AdminRecruitSummaryResponse> getRecruitSummaries() {
        return applicationRepository.findRecruitSummary();
    }

    @Transactional
    public void updateRecruit(Long recruitId, AdminRecruitUpdateRequest request) {
        validateUpdateRequest(request);

        if (applicationRepository.existsByRecruitIdAndStatusNot(recruitId, ApplicationStatus.CANCELED)) {
            throw new RecruitHasApplicationException();
        }

        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(RecruitNotFoundException::new);

        recruit.setTitle(request.title());
        recruit.setStartAt(request.startAt());
        recruit.setEndAt(request.endAt());

        recruitContentRepository.deleteAllByRecruitId(recruitId);

        List<RecruitContent> newContents = request.questions().stream()
                .map(q -> RecruitContent.builder()
                        .recruit(recruit)
                        .question(q.question())
                        .priority(q.priority())
                        .required(true)
                        .build()
                )
                .toList();

        recruitContentRepository.saveAll(newContents);
    }

    @Transactional
    public void deleteRecruit(Long recruitId) {
        if (applicationRepository.existsByRecruitId(recruitId)) {
            throw new RecruitHasApplicationException();
        }

        if (!recruitRepository.existsById(recruitId)) {
            throw new RecruitNotFoundException();
        }

        recruitContentRepository.deleteAllByRecruitId(recruitId);
        recruitRepository.deleteById(recruitId);
    }

    private void validateUpdateRequest(AdminRecruitUpdateRequest request) {
        if (request == null) {
            throw new InvalidRecruitUpdateRequestException("요청 값이 비어있습니다.");
        }
        if (request.title() == null || request.title().isBlank()) {
            throw new InvalidRecruitUpdateRequestException("title은 필수입니다.");
        }
        if (request.startAt() == null || request.endAt() == null) {
            throw new InvalidRecruitUpdateRequestException("start_at, end_at은 필수입니다.");
        }
        if (request.startAt().isAfter(request.endAt())) {
            throw new InvalidRecruitUpdateRequestException("start_at은 end_at보다 이후일 수 없습니다.");
        }
        if (request.questions() == null || request.questions().isEmpty()) {
            throw new InvalidRecruitUpdateRequestException("questions는 최소 1개 이상이어야 합니다.");
        }

        Set<Integer> priorities = new HashSet<>();
        for (AdminRecruitUpdateRequest.Item item : request.questions()) {
            if (item == null) {
                throw new InvalidRecruitUpdateRequestException("questions 항목이 비어있습니다.");
            }
            if (item.question() == null || item.question().isBlank()) {
                throw new InvalidRecruitUpdateRequestException("question은 필수입니다.");
            }
            if (item.priority() == null) {
                throw new InvalidRecruitUpdateRequestException("priority는 필수입니다.");
            }
            if (!priorities.add(item.priority())) {
                throw new InvalidRecruitUpdateRequestException("priority가 중복되었습니다: " + item.priority());
            }
        }
    }
}