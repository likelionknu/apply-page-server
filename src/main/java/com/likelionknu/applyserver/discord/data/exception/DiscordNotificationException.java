package com.likelionknu.applyserver.discord.data.exception;

import com.likelionknu.applyserver.common.response.ErrorCode;
import com.likelionknu.applyserver.common.response.GlobalException;

public class DiscordNotificationException extends GlobalException {
    public DiscordNotificationException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}