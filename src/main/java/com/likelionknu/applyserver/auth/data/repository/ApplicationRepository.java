package com.likelionknu.applyserver.auth.data.repository;

import com.likelionknu.applyserver.auth.data.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
}