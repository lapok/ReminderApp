package org.example.reminderapp.dto;


import java.time.LocalDateTime;

public class UpdateReminderRequest {
    private String title;
    private String description;
    private LocalDateTime remind;

    public UpdateReminderRequest(){};

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getRemind() {
        return remind;
    }

    public void setRemind(LocalDateTime remind) {
        this.remind = remind;
    }
}
