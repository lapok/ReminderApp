package org.example.reminderapp.repository;

import org.example.reminderapp.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

//  Находит все напоминалки конкретного пользователя
    List<Reminder> findByUserId(Long userId);
}
