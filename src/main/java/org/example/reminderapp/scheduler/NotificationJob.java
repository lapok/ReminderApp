package org.example.reminderapp.scheduler;


import org.example.reminderapp.service.NotificationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationJob implements Job {

    @Autowired
    private NotificationService notificationService;

    public NotificationJob() {}

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Quartz: Проверка напоминаний в " + java.time.LocalDateTime.now());

        notificationService.checkAndSendNotifications();
    }
}
