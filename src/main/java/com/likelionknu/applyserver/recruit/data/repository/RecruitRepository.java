package com.likelionknu.applyserver.recruit.data.repository;

import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {
}