package com.likelionknu.applyserver.recruit.repository;

import com.likelionknu.applyserver.recruit.entity.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
}