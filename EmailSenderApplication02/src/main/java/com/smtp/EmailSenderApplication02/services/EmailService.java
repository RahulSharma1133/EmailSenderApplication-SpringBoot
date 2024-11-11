package com.smtp.EmailSenderApplication02.services;

import com.smtp.EmailSenderApplication02.helper.Message;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface EmailService {

    //    send email to single person
    void sendEmail(String to, String subject, String message);

    //    send email to multiple person
    void sendEmail(String[] to, String subject, String message);

    //    send email with HTML
    void sendEmailWithHTML(String to, String subject, String htmlContent);

    //    send email with File
    void sendEmailWithFile(String to, String subject, String message, File file);

    void  sendEmailWithFile(String to, String subject, String message, InputStream is);

//    Email Receiving
    List<Message> getInboxMessages();
}
