package org.example.reminderapp.service;

import lombok.RequiredArgsConstructor;
import org.example.reminderapp.dto.ReminderDto;
import org.example.reminderapp.dto.ReminderListResponse;
import org.example.reminderapp.mapper.ReminderMapper;
import org.example.reminderapp.model.Reminder;
import org.example.reminderapp.model.User;
import org.example.reminderapp.repository.ReminderRepository;
import org.example.reminderapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private static final String SORT_BY_NAME = "name";
    private static final String SORT_BY_DATE = "date";
    private static final String SORT_BY_TIME = "time";

    private static final String ENTITY_FIELD_TITLE = "title";
    private static final String ENTITY_FIELD_REMIND = "remind";


    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final ReminderMapper reminderMapper;

    @Transactional
    public ReminderDto createReminder(ReminderDto dto, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Reminder reminder = reminderMapper.toEntity(dto);

        reminder.setUser(user);
        // Scheduler проверяет напоминания раз в минуту (cron с фиксированной секундой),
        // поэтому нормализуем время до границы минуты, чтобы не промахиваться из-за seconds/nanos.
        if (reminder.getRemind() != null) {
            reminder.setRemind(reminder.getRemind().withSecond(0).withNano(0));
        }

        return reminderMapper.toDto(reminderRepository.save(reminder));
    }

    @Transactional(readOnly = true)
    public ReminderDto getReminderById(Long id, Long userId){
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Напоминание не найдено"));

        if (!reminder.getUser().getId().equals(userId)){
            throw new RuntimeException("У вас нет доступа к этому напоминанию");
        }

        return reminderMapper.toDto(reminder);
    }

    @Transactional
    public ReminderDto updateReminder(Long id, ReminderDto dto, Long userId){
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Напоминание с id " + id + " не найдено"));

        if (!reminder.getUser().getId().equals(userId)){
            throw new RuntimeException("У вас нет доступа к этому напоминанию");
        }


//          Будем проверять поля опционально (request == null) - запись не перезаписывается
            if (dto.getTitle() != null){
            reminder.setTitle(dto.getTitle());
            }

            if (dto.getDescription() != null){
            reminder.setDescription(dto.getDescription());
            }

            if (dto.getRemind() != null){
            // Аналогично createReminder: приводим к границе минуты.
            reminder.setRemind(dto.getRemind().withSecond(0).withNano(0));
            }

            return reminderMapper.toDto(reminderRepository.save(reminder));
    }

    @Transactional(readOnly = true)
    public List<ReminderDto> getRemindersByUserId(Long userId){
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с id " + userId + " не найден"));

        return reminderRepository.findByUserId(userId).stream()
                .map(reminderMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteReminder(Long id, Long userId){
        Reminder reminder = reminderRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Напоминалка с таким id " + id + " не найдена или уже не существует."));

        if (!reminder.getUser().getId().equals(userId)){
            throw new RuntimeException("У вас нет доступа к этой напоминалке: " + id);
        }
        reminderRepository.delete(reminder);
    }

    @Transactional(readOnly = true)
    public ReminderListResponse getRemindersList(Long userId, int page, int size, String sortBy, String sortDirection, String filterDate){
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Reminder> reminderPage;

        if (filterDate != null && !filterDate.isEmpty()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(filterDate, formatter);

            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            reminderPage = reminderRepository.findByUserIdAndRemindBetween(userId, start, end, pageable);
        } else {
            reminderPage = reminderRepository.findByUserId(userId, pageable);
        }

        return ReminderListResponse.builder()
                .content(reminderPage.getContent().stream()
                        .map(reminderMapper::toDto)
                        .toList())
                .page(reminderPage.getNumber())
                .size(reminderPage.getSize())
                .totalElements(reminderPage.getTotalElements())
                .totalPages(reminderPage.getTotalPages())
                .last(reminderPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReminderDto> sortReminders(Long userId, String by){
//      Определяем поле для сортировки
        String sortField = switch (by) {
            case SORT_BY_NAME -> ENTITY_FIELD_TITLE;
            case SORT_BY_DATE, SORT_BY_TIME -> ENTITY_FIELD_REMIND;
            default -> throw new RuntimeException("Неверный параметр сортировки");
        };

        return reminderRepository.findByUserId(userId, Sort.by(sortField).ascending()).stream()
                .map(reminderMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReminderDto> filterReminders(Long userId, String date, String time){
        List<Reminder> reminders;

        if (date != null && !date.isEmpty() && time != null && !time.isEmpty()){
//            Фильтр по дате и времени (точный момент)
            LocalDateTime exactTime = LocalDateTime.parse(date + "T" + time);
            LocalDateTime start = exactTime;
            LocalDateTime end = exactTime.plusSeconds(1);
            reminders = reminderRepository.findByUserIdAndRemindBetween(userId, start, end);
        } else if (date != null && !date.isEmpty()){
//            Фильтр по дате (весь день)
            LocalDate filterDate = LocalDate.parse(date);
            LocalDateTime start = filterDate.atStartOfDay();
            LocalDateTime end = filterDate.plusDays(1).atStartOfDay();
            reminders = reminderRepository.findByUserIdAndRemindBetween(userId, start, end);
        } else if (time != null && !time.isEmpty()){
//            Фильтр по времени дня (любой день)
//            Разбираем время: "15:30" -> часы и минуты
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            reminders = reminderRepository.findByUserId(userId);

            reminders = reminders.stream()
                    .filter(r -> r.getRemind().getHour() == hour && r.getRemind().getMinute() == minute)
                    .collect(Collectors.toList());
        } else {
//            Без фильтра, значит все напоминалки
            reminders = reminderRepository.findByUserId(userId);
        }

        return reminders.stream()
                .map(reminderMapper::toDto)
                .toList();
    }

}
