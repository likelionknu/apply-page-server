package com.likelionknu.applyserver.mail.data.dto;

import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.mail.data.entity.MailContent;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MailRequestDto {
    private User user;
    private String email;
    private String title;
    private String template;
    private List<MailContent> dataList;
}
