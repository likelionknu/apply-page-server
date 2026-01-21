package com.likelionknu.applyserver.recruit.data.repository;

import com.likelionknu.applyserver.recruit.data.entity.RecruitContent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruitContentRepository extends JpaRepository<RecruitContent, Long> {

    List<RecruitContent> findByRecruitIdOrderByPriorityAsc(Long recruitId);
}