package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import com.likelionknu.applyserver.auth.exception.UserNotFoundException;
import com.likelionknu.applyserver.mail.data.dto.MailRequestDto;
import com.likelionknu.applyserver.mail.data.entity.MailContent;
import com.likelionknu.applyserver.mail.service.MailService;
import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationMailService {
    private final MailService mailService;
    private final ApplicationRepository applicationRepository;
    private final RecruitRepository recruitRepository;
    private final UserRepository userRepository;

    public void sendDocumentResult(String email, Long recruitId) {
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new UserNotFoundException();
        }

        Recruit recruit = recruitRepository.findById(recruitId).orElse(null);
        List<Application> applicationList = applicationRepository.findAllByRecruit(recruit);

        for(Application application : applicationList) {
            if(application.getStatus().equals(ApplicationStatus.DOCUMENT_PASSED)) {
                List<MailContent> mailContentList = new ArrayList<>();
                mailContentList.add(
                        MailContent.builder()
                                .key("userName")
                                .value(application.getUser().getName())
                                .build()
                );
                mailContentList.add(
                        MailContent.builder()
                                .key("part")
                                .value(application.getRecruit().getTitle())
                                .build()
                );
                mailContentList.add(
                        MailContent.builder()
                                .key("interviewLocation")
                                .value("추후 공지 예정 (SMS 개별 안내)")
                                .build()
                );
                mailContentList.add(
                        MailContent.builder()
                                .key("interviewTime")
                                .value("15")
                                .build()
                );

                mailService.sendMail(
                        MailRequestDto.builder()
                                .user(user)
                                .email(application.getUser().getEmail())
                                .title("1차 서류 전형 결과 안내")
                                .template("document-passed")
                                .dataList(mailContentList)
                                .build()
                );
            } else if (application.getStatus().equals(ApplicationStatus.DOCUMENT_FAILED)) {
                List<MailContent> mailContentList = new ArrayList<>();
                mailContentList.add(
                        MailContent.builder()
                                .key("userName")
                                .value(application.getUser().getName())
                                .build()
                );
                mailContentList.add(
                        MailContent.builder()
                                .key("part")
                                .value(application.getRecruit().getTitle())
                                .build()
                );

                mailService.sendMail(
                        MailRequestDto.builder()
                                .user(user)
                                .email(application.getUser().getEmail())
                                .title("1차 서류 전형 결과 안내")
                                .template("document-failed")
                                .dataList(mailContentList)
                                .build()
                );
            }
        }
    }

    public void sendFinalResult(String email, Long recruitId) {
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new UserNotFoundException();
        }

        Recruit recruit = recruitRepository.findById(recruitId).orElse(null);
        List<Application> applicationList = applicationRepository.findAllByRecruit(recruit);

        for(Application application : applicationList) {
            if(application.getStatus().equals(ApplicationStatus.FINAL_PASSED)) {
                List<MailContent> mailContentList = new ArrayList<>();
                mailContentList.add(
                        MailContent.builder()
                                .key("userName")
                                .value(application.getUser().getName())
                                .build()
                );
                mailContentList.add(
                        MailContent.builder()
                                .key("part")
                                .value(application.getRecruit().getTitle())
                                .build()
                );

                mailService.sendMail(
                        MailRequestDto.builder()
                                .user(user)
                                .email(application.getUser().getEmail())
                                .title("최종 선발 전형 결과 안내")
                                .template("final-passed")
                                .dataList(mailContentList)
                                .build()
                );
            } else if (application.getStatus().equals(ApplicationStatus.FAIL_INTERVIEW)) {
                List<MailContent> mailContentList = new ArrayList<>();
                mailContentList.add(
                        MailContent.builder()
                                .key("userName")
                                .value(application.getUser().getName())
                                .build()
                );
                mailContentList.add(
                        MailContent.builder()
                                .key("part")
                                .value(application.getRecruit().getTitle())
                                .build()
                );

                mailService.sendMail(
                        MailRequestDto.builder()
                                .user(user)
                                .email(application.getUser().getEmail())
                                .title("최종 선발 전형 결과 안내")
                                .template("final-failed")
                                .dataList(mailContentList)
                                .build()
                );
            }
        }
    }
}
