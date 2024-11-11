package com.smtp.EmailSenderApplication02.services;

import com.smtp.EmailSenderApplication02.helper.Message;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender; // use for mail send

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    } // if we don't want to make constoter we use @Autowired annotation


    @Override
    public void sendEmail(String to, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom("rahulkumar.sharma@dotsquares.com");
        mailSender.send(simpleMailMessage); // send the email
        logger.info("Email has been sent..");

    }

    @Override
    public void sendEmail(String[] to, String subject, String message) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        mailSender.send(simpleMailMessage);
        simpleMailMessage.setFrom("rahulkumar.sharma@dotsquares.com");
    }

    @Override
    public void sendEmailWithHTML(String to, String subject, String htmlContent) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("rahulkumar.sharma@dotsquares.com");
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage); // send the email
            logger.info("Email has been sent..");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void sendEmailWithFile(String to, String subject, String message, File file) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message);
            helper.setFrom("rahulkumar.sharma@dotsquares.com");
            FileSystemResource fileSystemResource = new FileSystemResource(file);
            helper.addAttachment(Objects.requireNonNull(fileSystemResource.getFilename()), file);
            mailSender.send(mimeMessage); // send the email
            logger.info("Email has been sent..");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmailWithFile(String to, String subject, String message, InputStream is) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true);
            helper.setFrom("rahulkumar.sharma@dotsquares.com");
            File file = new File("D:\\Rahul\\EmailSenderApplication02\\src\\main\\resources\\static\\abc.txt");
            Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            FileSystemResource fileSystemResource = new FileSystemResource(file);
            helper.addAttachment(Objects.requireNonNull(fileSystemResource.getFilename()), file);

            mailSender.send(mimeMessage); // send the email
            logger.info("Email has been sent..");
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Value("${mail.store.protocol}")
    String protocol;

    @Value("${mail.imaps.host}")
    String host;

    @Value("${mail.imaps.port}  ")
    String port;

    @Value("${spring.mail.username}")
    String username;

    @Value("${spring.mail.password}")
    String password;

    @Override
    public List<Message> getInboxMessages() {
        Properties configurations = new Properties();
        configurations.setProperty("mail.store.protocol", protocol);
        configurations.setProperty("mail.imaps.host", host);
        configurations.setProperty("mail.imaps.port", port);

        Session session = Session.getDefaultInstance(configurations,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password); // Assuming username and password are class fields
                    }
                });
        try {
            Store store = session.getStore();
            store.connect();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            jakarta.mail.Message[] messages = inbox.getMessages();

            List<Message> list = new ArrayList<>();

            for (jakarta.mail.Message message : messages) {


                String content = getContentFromEmailMessage(message);
                List<String> files = getFilesFromEmailMessage(message);

                list.add(Message.builder().subjects(message.getSubject()).content(content).files(files).build());
            }


            return list;


        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getFilesFromEmailMessage(jakarta.mail.Message message) throws MessagingException, IOException {
        List<String> files = new ArrayList<>();
        if(message.isMimeType("multipart/*")){

            Multipart content = (Multipart)message.getContent();


            for (int i=0;i<content.getCount();i++){
             BodyPart bodyPart = content.getBodyPart(i);
             if(Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())){
              InputStream inputStream= bodyPart.getInputStream();
              File file = new File("src/main/resources/email"+bodyPart.getFileName());

              Files.copy(inputStream,file.toPath(),StandardCopyOption.REPLACE_EXISTING);
//              urls
              files.add(file.getAbsolutePath());


             }
            }
        }

return  files;
    }

    private String getContentFromEmailMessage(jakarta.mail.Message message) throws MessagingException, IOException {

        if(message.isMimeType("text/plain") || message.isMimeType("text/html")){
            String content = (String) message.getContent();
            return content;
        }else if(message.isMimeType("multipart/*"))
        {
           Multipart part = (Multipart) message.getContent();
           for(int i=0;i<part.getCount();i++){
              BodyPart  bodyPart = part.getBodyPart(i);

              if(bodyPart.isMimeType("text/plan")){
                  return (String) bodyPart.getContent();
              }
           }
        }
        return null;
    }
}
