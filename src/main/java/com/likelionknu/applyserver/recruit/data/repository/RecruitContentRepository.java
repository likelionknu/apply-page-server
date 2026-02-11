package com.likelionknu.applyserver.recruit.data.repository;

import com.likelionknu.applyserver.recruit.data.entity.RecruitContent;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface RecruitContentRepository extends JpaRepository<RecruitContent, Long> {

    List<RecruitContent> findByRecruitIdOrderByPriorityAsc(Long recruitId);
    List<RecruitContent> findAllByRecruit_IdAndRequiredTrue(Long recruitId);

    @Modifying
    @Query("delete from RecruitContent rc where rc.recruit.id = :recruitId")
    void deleteAllByRecruitId(@Param("recruitId") Long recruitId);
}