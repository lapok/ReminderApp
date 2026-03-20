package org.example.reminderapp.repository;

import org.example.reminderapp.model.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

//  Находит все напоминалки конкретного пользователя
    List<Reminder> findByUserId(Long userId);
//    Находит напоминалки в диапазоне: прошлая минута - текущая минута
    List<Reminder> findByRemindBetweenAndNotifiedFalse(LocalDateTime start, LocalDateTime end);

//    Список с пагинацией
    Page<Reminder> findByUserId(Long userId, Pageable pageable);

// Список с поиском по времени с пагинацией
    Page<Reminder> findByUserIdAndRemindBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

//  Список для sort
    List<Reminder> findByUserId(Long userId, Sort sort);

//  Список для filter
    List<Reminder> findByUserIdAndRemindBetween(Long userId, LocalDateTime start, LocalDateTime end);

}
