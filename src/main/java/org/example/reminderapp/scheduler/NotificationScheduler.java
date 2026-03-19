package org.example.reminderapp.scheduler;

import org.example.reminderapp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class NotificationScheduler {

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 * * * * *")
    public void checkAndSendNotifications() {
        System.out.println("Scheduler: проверка напоминаний в " + java.time.LocalDateTime.now());
        notificationService.checkAndSendNotifications();
    }
}
