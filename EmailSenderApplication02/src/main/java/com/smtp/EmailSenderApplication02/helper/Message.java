package com.smtp.EmailSenderApplication02.helper;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String from;
    private String subjects;
    private String content;
    private List<String> files;
}
