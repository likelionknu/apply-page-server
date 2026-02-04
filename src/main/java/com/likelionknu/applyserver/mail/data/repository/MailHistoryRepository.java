package com.likelionknu.applyserver.mail.data.repository;

import com.likelionknu.applyserver.mail.data.entity.MailHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailHistoryRepository extends JpaRepository<MailHistory, Long> {
}
