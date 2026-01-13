package com.likelionknu.applyserver.auth.data.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleProfile {
    private String email;
    private String name;
    private String profileUrl;
}
