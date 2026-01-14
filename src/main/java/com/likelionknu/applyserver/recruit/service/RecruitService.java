package com.likelionknu.applyserver.recruit.service;

import com.likelionknu.applyserver.recruit.data.dto.response.RecruitListResponse;
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final RecruitRepository recruitRepository;

    @Transactional(readOnly = true)
    public List<RecruitListResponse> getRecruits() {
        return recruitRepository.findAll()
                .stream()
                .map(RecruitListResponse::from)
                .toList();
    }
}