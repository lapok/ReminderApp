package org.example.reminderapp.model;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name ="password", nullable = false)
    private String password;

    @Column(name = "telegram_chat_id")
    private String telegramChatId;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Reminder> reminders = new ArrayList<>();

}
