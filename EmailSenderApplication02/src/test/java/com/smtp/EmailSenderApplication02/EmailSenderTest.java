package com.smtp.EmailSenderApplication02;

import com.smtp.EmailSenderApplication02.helper.Message;
import com.smtp.EmailSenderApplication02.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
public class EmailSenderTest {

    @Autowired
    private EmailService emailService;


    @Test
    void emailSendTest() {
        System.out.println("Sending mail..");
        emailService.sendEmail("srahulb6@gmail.com", "Hello Rahul", "hello good morning buddy...");
    }



    @Test
    void sendEmailWithHTML() {
        String html = "" + "<h1 style='color:red; border:1px solid black;'>HEllo World</h1>";
        emailService.sendEmailWithHTML("srahulb6@gmail.com", "This mail From Dot squares", html);
    }



    @Test
    void sendEmailWithFile() {
        emailService.sendEmailWithFile("srahulb6@gmail.com", "This mail From Dotsquares", "Hello Bro", new File("D:\\Rahul\\EmailSenderApplication02\\src\\main\\resources\\static\\abc.txt"));
    }



    @Test
    void sendEmailWithFileWithStream() {
        File file = new File("D:\\Rahul\\EmailSenderApplication02\\src\\main\\resources\\static\\abc.txt");
        try {
            InputStream is = new FileInputStream(file);
            emailService.sendEmailWithFile("srahulb6@gmail.com", "This mail From Dotsquares", "Hello Bro", is);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


//    receiving mail test

    @Test
    void getInbox(){
   List<Message> inboxMessage =  emailService.getInboxMessages();
   inboxMessage.forEach(item->{
       System.out.println(item.getSubjects());
       System.out.println(item.getContent());
       System.out.println(item.getFiles());
       System.out.println("---------------------------");
   });
    }
}
