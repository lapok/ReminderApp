package org.example.reminderapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendReminderEmail(String to, String title, String description){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Напоминание: " + title);
        message.setText(description);
        message.setFrom("noreplyReminderApp@demomailtrap.co");

        mailSender.send(message);
        System.out.println("Email отправлен на " + to);
    }
}
