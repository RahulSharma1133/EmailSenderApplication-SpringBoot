package com.smtp.EmailSenderApplication02.helper;

import lombok.*;


@Builder
@Data
public class EmailRequest {
    private String to;
    private String subject;
    private String message;
}
