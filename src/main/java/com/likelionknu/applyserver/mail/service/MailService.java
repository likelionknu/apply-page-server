package com.likelionknu.applyserver.mail.service;

import com.likelionknu.applyserver.mail.data.dto.MailRequestDto;
import com.likelionknu.applyserver.mail.data.entity.MailContent;
import com.likelionknu.applyserver.mail.data.entity.MailHistory;
import com.likelionknu.applyserver.mail.data.repository.MailHistoryRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final MailHistoryRepository mailHistoryRepository;

    @Async
    public void sendMail(MailRequestDto mailRequestDto) {
        try {
            String subject = "[멋쟁이사자처럼 강남대학교] " + mailRequestDto.getTitle();
            String body = setContext(mailRequestDto.getTemplate(), mailRequestDto.getDataList());

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(mailRequestDto.getEmail());
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);

            mailSender.send(mimeMessage);

            mailHistoryRepository.save(
                    MailHistory.builder()
                            .subject(subject)
                            .body(body)
                            .user(mailRequestDto.getUser())
                            .recipient(mailRequestDto.getEmail())
                            .sentAt(LocalDateTime.now())
                            .build()
            );

            log.info("[sendMail] 메일 전송 성공: {}, {}", mailRequestDto.getEmail(), subject);
        } catch (MessagingException e) {
            log.error("[sendMail] 메일 전송 실패: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String setContext(String template, List<MailContent> dataList) {
        Context context = new Context();

        for(MailContent mailContent : dataList) {
            context.setVariable(mailContent.getKey(), mailContent.getValue());
        }

        return templateEngine.process(template, context);
    }
}
