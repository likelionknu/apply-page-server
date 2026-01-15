package com.likelionknu.applyserver.application.data.repository;

import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findFirstByStatus(ApplicationStatus status);
}