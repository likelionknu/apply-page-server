package com.likelionknu.applyserver.application.service;

import com.likelionknu.applyserver.application.data.dto.request.FinalSubmitRequestDto;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.entity.RecruitAnswer;
import com.likelionknu.applyserver.application.data.exception.*;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import com.likelionknu.applyserver.application.data.repository.RecruitAnswerRepository;
import com.likelionknu.applyserver.auth.data.entity.Profile;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.enums.ApplicationStatus;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import com.likelionknu.applyserver.discord.service.DiscordNotificationService;
import com.likelionknu.applyserver.mail.data.dto.MailRequestDto;
import com.likelionknu.applyserver.mail.data.entity.MailContent;
import com.likelionknu.applyserver.mail.service.MailService;
import com.likelionknu.applyserver.recruit.data.entity.Recruit;
import com.likelionknu.applyserver.recruit.data.entity.RecruitContent;
import com.likelionknu.applyserver.recruit.data.repository.RecruitContentRepository;
import com.likelionknu.applyserver.recruit.data.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationFinalSubmitService {

    private final ApplicationRepository applicationRepository;
    private final RecruitAnswerRepository recruitAnswerRepository;
    private final RecruitContentRepository recruitContentRepository;
    private final UserRepository userRepository;
    private final RecruitRepository recruitRepository;
    private final MailService mailService;
    private final DiscordNotificationService discordNotificationService;

    public void finalSubmit(String email, FinalSubmitRequestDto request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new EmptyAnswerException();
        }

        User user = userRepository.findOptionalByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        Profile profile = user.getProfile();
        boolean profileCompleted =
                profile != null &&
                        profile.getStudentId() != null &&
                        profile.getDepart() != null &&
                        profile.getPhone() != null &&
                        profile.getGrade() != null &&
                        profile.getStatus() != null;

        if (!profileCompleted) {
            throw new ProfileIncompleteException();
        }

        Long recruitId = request.recruitId();

        Optional<Application> existingOpt = applicationRepository.findByUserIdAndRecruitId(user.getId(), recruitId);
        if (existingOpt.isPresent()) {
            Application existing = existingOpt.get();
            if (existing.getStatus() != ApplicationStatus.DRAFT) {
                throw new IllegalStateException("이미 제출되었거나 처리 중인 지원서는 최종 제출을 다시 할 수 없습니다. 회수 취소(restore)만 가능합니다.");
            }
        }

        Application application = existingOpt.orElseGet(() -> {
            Recruit recruit = recruitRepository.findById(recruitId)
                    .orElseThrow(RecruitNotFoundException::new);

            return applicationRepository.save(
                    Application.builder()
                            .recruit(recruit)
                            .user(user)
                            .note(null)
                            .evaluation(null)
                            .status(ApplicationStatus.DRAFT)
                            .submittedAt(LocalDateTime.now())
                            .build()
            );
        });

        List<RecruitContent> requiredContents =
                recruitContentRepository.findAllByRecruit_IdAndRequiredTrue(recruitId);

        Set<Long> submittedQuestionIds = new HashSet<>();
        for (FinalSubmitRequestDto.Item item : request.items()) {
            submittedQuestionIds.add(item.questionId());
        }

        for (RecruitContent required : requiredContents) {
            if (!submittedQuestionIds.contains(required.getId())) {
                throw new EmptyAnswerException();
            }
        }

        recruitAnswerRepository.deleteByApplication_Id(application.getId());

        List<RecruitAnswer> answers = new ArrayList<>();
        for (FinalSubmitRequestDto.Item item : request.items()) {
            RecruitContent content = recruitContentRepository.findById(item.questionId())
                    .orElseThrow(RecruitContentNotFoundException::new);

            if (!recruitId.equals(content.getRecruit().getId())) {
                throw new InvalidApplicationQuestionException();
            }

            answers.add(RecruitAnswer.builder()
                    .application(application)
                    .content(content)
                    .answer(item.answer())
                    .build());
        }

        recruitAnswerRepository.saveAll(answers);
        application.setSubmittedAt(LocalDateTime.now());
        application.changeStatus(ApplicationStatus.SUBMITTED);

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
                        .user(null)
                        .email(application.getUser().getEmail())
                        .title("지원이 완료되었습니다!")
                        .template("apply-success")
                        .dataList(mailContentList)
                        .build()
        );

        discordNotificationService.sendUserSubmittedApplication(
                application.getUser().getName(),
                application.getUser().getEmail(),
                application.getRecruit().getTitle()
        );
    }
}