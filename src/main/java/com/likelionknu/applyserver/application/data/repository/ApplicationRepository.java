package com.likelionknu.applyserver.application.data.repository;

import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findFirstByUserAndStatus(User user, ApplicationStatus status);


    boolean existsByUserIdAndRecruitIdAndStatus(
            Long userId,
            Long recruitId,
            ApplicationStatus status
    );

    boolean existsByUserIdAndRecruitIdAndStatusNot(
            Long userId,
            Long recruitId,
            ApplicationStatus status
    );
}