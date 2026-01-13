package com.likelionknu.applyserver.auth.data.repository;

import com.likelionknu.applyserver.auth.data.entity.RecruitAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitAnswerRepository extends JpaRepository<RecruitAnswer, Long> {
}