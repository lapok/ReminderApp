package org.example.reminderapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.reminderapp.model.Reminder;
import org.example.reminderapp.model.User;
import org.example.reminderapp.repository.ReminderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ReminderRepository reminderRepository;
    private final EmailService emailService;
    private final TelegramService telegramService;

    @Transactional
    public void checkAndSendNotifications(){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(5);
        LocalDateTime windowEnd = now.plusSeconds(5);

        log.info("Поиск напоминаний в интервале: {} - {}", windowStart, windowEnd);

        List<Reminder> remindersToSend = reminderRepository
                .findByRemindBetweenAndNotifiedFalse(windowStart, windowEnd);

        if (remindersToSend.isEmpty()){
            log.debug("Напоминаний к отправке не найдено");
            return;
        }

        log.info("Найдено напоминаний к отправке: {}", remindersToSend.size());

        for (Reminder reminder: remindersToSend){
            try {
                sendNotifications(reminder);

                reminder.setNotified(true);
                reminderRepository.save(reminder);

                log.info("Напоминание '{}' (ID: {}) успешно отработано", reminder.getTitle(), reminder.getId());
            } catch (Exception e) {
                log.error("Ошибка при обработке напоминания ID {}: {}", reminder.getId(), e.getMessage());
            }
        }
    }

    private void sendNotifications(Reminder reminder){
        User user = reminder.getUser();
        String message = String.format(" %s\n%s",
                reminder.getTitle(),
                reminder.getDescription());

        // Отправка email
        try{
        emailService.sendReminderEmail(
                user.getEmail(),
                reminder.getTitle(),
                reminder.getDescription()
        );
        log.debug("Email отправлен на {}", user.getEmail());
        } catch (Exception e){
            log.warn("Не удалось отправить Email пользователю {}: {}", user.getEmail(), e.getMessage());
        }

        // Telegram
        if (user.getTelegramChatId() != null && !user.getTelegramChatId().isEmpty()){
            try {
                telegramService.sendTelegramMessage(user.getTelegramChatId(), message);
                log.debug("Telegram сообщение отправлено в чат {}", user.getTelegramChatId());
            } catch (Exception e) {
                log.warn("Не удалось отправить сообщение в чат {}: {}", user.getTelegramChatId(), e.getMessage());
            }
        } else {
            log.debug("Telegram сообщение пропщуено, не указан chatId у пользователя");
        }
    }
}
