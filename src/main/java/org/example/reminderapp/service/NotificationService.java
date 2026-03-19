package org.example.reminderapp.service;

import jakarta.mail.SendFailedException;
import org.example.reminderapp.model.Reminder;
import org.example.reminderapp.model.User;
import org.example.reminderapp.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TelegramService telegramService;

    public void checkAndSendNotifications(){
        System.out.println("Поиск напоминаний для отправки...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);

        List<Reminder> remindersToSend = reminderRepository
                .findByRemindBetweenAndNotifiedFalse(oneMinuteAgo, now);

        System.out.println("Найдено напоминаний: " + remindersToSend.size());
        System.out.println("Поиск напоминаний между " + oneMinuteAgo + " и " + now);

        for (Reminder reminder: remindersToSend){
            System.out.println("Отправка: " + reminder.getTitle());

            reminder.setNotified(true);
            reminderRepository.save(reminder);
            sendNotifications(reminder);
        }
    }

    private void sendNotifications(Reminder reminder){
        User user = reminder.getUser();
        String message = String.format(" %s\n%s",
                reminder.getTitle(),
                reminder.getDescription());

        System.out.println("Пользователь: id=" + user.getId() +
                ", telegramChatId=" + user.getTelegramChatId());

        // Отправка email
        try{
        emailService.sendReminderEmail(
                user.getEmail(),
                reminder.getTitle(),
                reminder.getDescription()
        );
        } catch (Exception e){
            System.err.println("Ошибка отправки email: " + e.getMessage());
        }

        // Telegram
        if (user.getTelegramChatId() != null){
            System.out.println("Telegram chatId найден: " + user.getTelegramChatId());
            telegramService.sendTelegramMessage(
                    user.getTelegramChatId(),
                    message
            );
        } else {
            System.out.println("Telegram chatId = NULL");
        }
    }
}
