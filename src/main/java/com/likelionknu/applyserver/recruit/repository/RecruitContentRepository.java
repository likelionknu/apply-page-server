package com.likelionknu.applyserver.recruit.repository;

import com.likelionknu.applyserver.recruit.entity.RecruitContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitContentRepository extends JpaRepository<RecruitContent, Long> {

    List<RecruitContent> findByRecruitIdOrderByPriorityAsc(Long recruitId);
}