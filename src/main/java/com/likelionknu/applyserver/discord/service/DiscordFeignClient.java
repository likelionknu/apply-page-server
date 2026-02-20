package com.likelionknu.applyserver.discord.service;

import com.likelionknu.applyserver.discord.data.dto.DiscordMessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

@FeignClient(name = "discordFeignClient", url = "https://discord.com/api/webhooks")
public interface DiscordFeignClient {
    @PostMapping
    void sendNotification(URI uri, @RequestBody DiscordMessageDto discordMessageDto);
}
