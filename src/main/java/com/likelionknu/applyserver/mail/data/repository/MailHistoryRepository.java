package com.likelionknu.applyserver.mail.data.repository;

import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.mail.data.entity.MailHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailHistoryRepository extends JpaRepository<MailHistory, Long> {
    List<MailHistory> findAllByUser(User user);
}
