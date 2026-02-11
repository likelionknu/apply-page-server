package com.likelionknu.applyserver.application.data.repository;

import com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitSummaryResponse;
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

    @Query("""
        select (count(a) > 0)
        from Application a
        where a.recruit.id = :recruitId
    """)
    boolean existsByRecruitId(@Param("recruitId") Long recruitId);

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

    @Query("""
        select new com.likelionknu.applyserver.admin.data.dto.response.AdminRecruitSummaryResponse(
            r.id,
            r.title,
            r.startAt,
            r.endAt,
            coalesce(sum(case
                when a.status = com.likelionknu.applyserver.auth.data.enums.ApplicationStatus.SUBMITTED then 1 else 0
            end), 0),
            coalesce(sum(case
                when a.status = com.likelionknu.applyserver.auth.data.enums.ApplicationStatus.DRAFT then 1 else 0
            end), 0)
        )
        from Recruit r
        left join Application a on a.recruit = r
        group by r.id, r.title, r.startAt, r.endAt
        order by r.startAt desc
    """)
    List<AdminRecruitSummaryResponse> findRecruitSummary();

    @Query("""
        select a from Application a
        join fetch a.user u
        left join fetch u.profile p
        join fetch a.recruit r
        where r.id = :recruitId
        order by
            case when a.submittedAt is null then 1 else 0 end,
            a.submittedAt desc,
            a.id desc
    """)
    List<Application> findAllByRecruitIdWithUserProfile(@Param("recruitId") Long recruitId);
}