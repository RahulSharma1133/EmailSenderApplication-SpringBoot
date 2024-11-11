package com.smtp.EmailSenderApplication02.controllers.api;

import com.smtp.EmailSenderApplication02.helper.CustomResponse;
import com.smtp.EmailSenderApplication02.helper.EmailRequest;
import com.smtp.EmailSenderApplication02.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/email")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    //    send email API
    @PostMapping("/send")
    public ResponseEntity<CustomResponse> sendEmail(@RequestBody EmailRequest request) {
        emailService.sendEmailWithHTML(request.getTo(), request.getSubject(), request.getMessage());
        return ResponseEntity.ok(
                CustomResponse.builder()
                        .message("Email sent Successfully")
                        .httpStatus(HttpStatus.OK)
                        .success(true)
                        .build()
        );
    }

    @PostMapping(value = "/send-with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomResponse> sendWithFile(
            @RequestPart EmailRequest request,
            @RequestPart MultipartFile file
    ) {

        try {
            // Debugging logs to check received data
            if (file == null || file.isEmpty()) {
                log.error("File is empty or not provided");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(CustomResponse.builder()
                                .message("File is empty or not provided")
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .success(false)
                                .build());
            }

            log.debug("Sending email to: {}", request.getTo());
            log.debug("Subject: {}", request.getSubject());
            log.debug("File name: {}", file.getOriginalFilename());

            // Sending the email with the provided file
            emailService.sendEmailWithFile(request.getTo(), request.getSubject(), request.getMessage(), file.getInputStream());

            return ResponseEntity.ok(
                    CustomResponse.builder()
                            .message("Email with file sent Successfully")
                            .httpStatus(HttpStatus.OK)
                            .success(true)
                            .build()
            );
        } catch (IOException e) {
            log.error("Failed to send email with file: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomResponse.builder()
                            .message("Failed to send email with file: " + e.getMessage())
                            .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                            .success(false).build());
        }
    }
}
