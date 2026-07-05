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

    List<Reminder> findByUserId(Long userId);
    List<Reminder> findByRemindBetweenAndNotifiedFalse(LocalDateTime start, LocalDateTime end);

    Page<Reminder> findByUserId(Long userId, Pageable pageable);

    Page<Reminder> findByUserIdAndRemindBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    List<Reminder> findByUserId(Long userId, Sort sort);
    List<Reminder> findByUserIdAndRemindBetween(Long userId, LocalDateTime start, LocalDateTime end);

}
