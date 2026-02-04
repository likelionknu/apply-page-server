package com.likelionknu.applyserver.mail.data.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailContent {
    private String key;
    private String value;
}
