package com.likelionknu.applyserver.application.data.repository;

import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByUserIdAndRecruitIdAndStatus(Long userId, Long recruitId, ApplicationStatus status);

    boolean existsByUserIdAndRecruitIdAndStatus(Long userId, Long recruitId, ApplicationStatus status);

    boolean existsByUserIdAndRecruitIdAndStatusNot(Long userId, Long recruitId, ApplicationStatus status);

    Optional<Application> findByUserIdAndRecruitId(Long userId, Long recruitId);

    Optional<Application> findByIdAndUserId(Long id, Long userId);

    List<Application> findAllByRecruit(Recruit recruit);

    @Query("""
        select a from Application a
        join fetch a.recruit r
        where a.user.id = :userId
        order by a.submittedAt desc
    """)
    List<Application> findAllWithRecruitByUserId(@Param("userId") Long userId);
}