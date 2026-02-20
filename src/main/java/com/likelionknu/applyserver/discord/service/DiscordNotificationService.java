package com.likelionknu.applyserver.discord.service;

import com.likelionknu.applyserver.discord.data.dto.DiscordMessageDto;
import com.likelionknu.applyserver.discord.data.enums.ChannelDivider;
import com.likelionknu.applyserver.discord.data.exception.DiscordNotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscordNotificationService {
    @Value("${discord.webhook.user}")
    private String userWebhookUrl;

    @Value("${discord.webhook.application}")
    private String applicationWebhookUrl;

    private final DiscordFeignClient discordFeignClient;

    private void sendDiscordNotification(ChannelDivider channelDivider, String content) {
        DiscordMessageDto discordMessageDto = new DiscordMessageDto(content);
        String webhookUrl = switch (channelDivider) {
            case USER -> userWebhookUrl;
            case APPLICATION -> applicationWebhookUrl;
        };

        try {
            log.info("[sendDiscordNotification] Discord 채널에 알림이 발송 됨");
            discordFeignClient.sendNotification(URI.create(webhookUrl), discordMessageDto);
        } catch (Exception e) {
            throw new DiscordNotificationException();
        }
    }

    @Async
    public void sendNewUserNotification(String name, String email, String profileUrl) {
        log.info("[sendNewUserNotification] Discord 채널에 신규 가입자 알림 발송 시도");

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분 ss초"));

        StringBuilder sb = new StringBuilder();
        sb.append("## 새로운 사용자가 등록 됨\n");
        sb.append("> 신규 사용자가 서비스에 등록되었습니다.\n\n");
        sb.append("```yml\n");
        sb.append(String.format("이름: %s\n", name));
        sb.append(String.format("이메일 주소: %s\n", email));
        sb.append(String.format("가입일: %s\n", now));
        sb.append("```\n");
        sb.append(String.format("[.](%s)", profileUrl));

        sendDiscordNotification(ChannelDivider.USER, sb.toString());
    }

    @Async
    public void sendUserSubmittedApplication(String name, String email, String recruitTitle) {
        log.info("[sendUserSubmittedApplication] Discord 채널에 지원서 최종 제출 알림 발송 시도");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분 ss초"));

        StringBuilder sb = new StringBuilder();
        sb.append("## 지원서 최종제출 됨\n");
        sb.append("> 사용자가 지원서를 최종 제출했습니다.\n\n");
        sb.append("```yml\n");
        sb.append(String.format("이름: %s\n", name));
        sb.append(String.format("이메일 주소: %s\n", email));
        sb.append(String.format("공고 명: %s\n", recruitTitle));
        sb.append(String.format("제출일: %s\n", now));
        sb.append("```\n");

        sendDiscordNotification(ChannelDivider.APPLICATION, sb.toString());
    }
}
