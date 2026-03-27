package org.example.reminderapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.email.username:noreply@reminderapp.com}")

    public void sendReminderEmail(String to, String title, String description){
        log.info("Подготовка к отправке Email на адрес: {}", to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Напоминание: " + title);
        message.setText(description);
        message.setFrom("noreplyReminderApp@demomailtrap.co");

        try {
            mailSender.send(message);
            log.info("Email успешно отправлен на {}", to);
        } catch (Exception e) {
            log.error("Не удалось отправить email на {}: {}", to, e.getMessage());
        }
    }
}
