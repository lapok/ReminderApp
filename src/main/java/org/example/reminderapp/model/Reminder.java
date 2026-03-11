package org.example.reminderapp.model;


import jakarta.persistence.*;

import java.time.DateTimeException;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")

public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 4096)
    private String description;

    @Column(nullable = false)
    private LocalDateTime remind;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Reminder(){};

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
