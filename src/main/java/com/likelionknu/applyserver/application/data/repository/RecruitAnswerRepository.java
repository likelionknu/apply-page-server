package com.likelionknu.applyserver.application.data.repository;

import com.likelionknu.applyserver.application.data.entity.RecruitAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitAnswerRepository extends JpaRepository<RecruitAnswer, Long> {
    void deleteAllByApplication_Id(Long applicationId);
}