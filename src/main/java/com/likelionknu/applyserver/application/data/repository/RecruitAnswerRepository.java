package com.likelionknu.applyserver.application.data.repository;

import com.likelionknu.applyserver.application.data.entity.RecruitAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitAnswerRepository extends JpaRepository<RecruitAnswer, Long> {

    List<RecruitAnswer> findByApplicationId(Long applicationId);
}