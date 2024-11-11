package com.smtp.EmailSenderApplication02.helper;

import lombok.*;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomResponse {
    private String message;
    private HttpStatus httpStatus;
    private boolean success = false;
}
