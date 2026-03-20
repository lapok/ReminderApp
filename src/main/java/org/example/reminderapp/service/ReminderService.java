package org.example.reminderapp.service;

import org.example.reminderapp.dto.CreateReminderRequest;
import org.example.reminderapp.dto.ReminderDto;
import org.example.reminderapp.dto.ReminderListResponse;
import org.example.reminderapp.dto.UpdateReminderRequest;
import org.example.reminderapp.model.Reminder;
import org.example.reminderapp.model.User;
import org.example.reminderapp.repository.ReminderRepository;
import org.example.reminderapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;
    @Autowired
    private UserRepository userRepository;

    public ReminderDto createReminder(CreateReminderRequest request, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Reminder reminder = new Reminder();

        reminder.setTitle(request.getTitle());
        reminder.setDescription(request.getDescription());
        reminder.setRemind(request.getRemind());
        reminder.setUser(user);

        Reminder savedReminder = reminderRepository.save(reminder);

        ReminderDto dto = new ReminderDto();

        dto.setId(savedReminder.getId());
        dto.setTitle(savedReminder.getTitle());
        dto.setDescription(savedReminder.getDescription());
        dto.setRemind(savedReminder.getRemind());
        dto.setUserId(savedReminder.getUser().getId());

        return dto;
    }

    public ReminderDto getReminderById(Long id, Long userId){
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Напоминание не найдено"));

        if (!reminder.getUser().getId().equals(userId)){
            throw new RuntimeException("У вас нет доступа к этому напоминанию");
        }

            ReminderDto dto = new ReminderDto();

            dto.setId(reminder.getId());
            dto.setTitle(reminder.getTitle());
            dto.setDescription(reminder.getDescription());
            dto.setRemind(reminder.getRemind());
            dto.setUserId(reminder.getUser().getId());

            return dto;
    }

    public ReminderDto updateReminder(Long id, UpdateReminderRequest request, Long userId){
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Напоминание с id " + id + " не найдено"));

        if (!reminder.getUser().getId().equals(userId)){
            throw new RuntimeException("У вас нет доступа к этому напоминанию");
        }


//          Будем проверять поля опционально (request == null) - запись не перезаписывается
            if (request.getTitle() != null){
            reminder.setTitle(request.getTitle());
            }

            if (request.getDescription() != null){
            reminder.setDescription(request.getDescription());
            }

            if (request.getRemind() != null){
            reminder.setRemind(request.getRemind());
            }

            Reminder updatedReminder = reminderRepository.save(reminder);

            ReminderDto dto = new ReminderDto();

            dto.setId(updatedReminder.getId());
            dto.setTitle(updatedReminder.getTitle());
            dto.setDescription(updatedReminder.getDescription());
            dto.setRemind(updatedReminder.getRemind());

            return dto;
    }

    public List<ReminderDto> getRemindersByUserId(Long userId){
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с id " + userId + " не найден"));

        List<Reminder> reminders = reminderRepository.findByUserId(userId);

        List<ReminderDto> remindersDtos = new ArrayList<>();
        for (Reminder reminder : reminders){
            ReminderDto dto = new ReminderDto();
            dto.setId(reminder.getId());
            dto.setTitle(reminder.getTitle());
            dto.setDescription(reminder.getDescription());
            dto.setRemind(reminder.getRemind());
            dto.setUserId(reminder.getUser().getId());
            remindersDtos.add(dto);
        }

        return remindersDtos;
    }

    public void deleteReminder(Long id, Long userId){
        Reminder reminder = reminderRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Напоминалка с таким id " + id + " не найдена или уже не существует."));

        if (!reminder.getUser().getId().equals(userId)){
            throw new RuntimeException("У вас нет доступа к этой напоминалке: " + id);
        }
        reminderRepository.delete(reminder);
    }

    public Page<ReminderDto> getRemindersList(Long userId, int page, int size, String sortBy, String sortDirection, String filterDate){
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

        Page<ReminderDto> dtoPage = reminderPage.map(reminder -> {
            ReminderDto dto = new ReminderDto();
            dto.setId(reminder.getId());
            dto.setTitle(reminder.getTitle());
            dto.setDescription(reminder.getDescription());
            dto.setRemind(reminder.getRemind());
            dto.setUserId(reminder.getUser().getId());
            return dto;
        });
        return dtoPage;
    }

    public List<ReminderDto> sortReminders(Long userId, String by){
//      Определяем поле для сортировки
        String sortField;
        switch(by){
            case "name":
                sortField = "title";
                break;
            case "date":
                sortField = "remind";
                break;
            case "time":
                sortField = "remind";
                break;
            default:
                throw new RuntimeException("Неверный параметр сортировки. Используйте: name, date, time");
        }

        Sort sort = Sort.by(sortField).ascending();

        List<Reminder> reminders = reminderRepository.findByUserId(userId, sort);


        List<ReminderDto> dtos = new ArrayList<>();
        for(Reminder reminder: reminders){
            ReminderDto dto = new ReminderDto();
            dto.setId(reminder.getId());
            dto.setTitle(reminder.getTitle());
            dto.setDescription(reminder.getDescription());
            dto.setRemind(reminder.getRemind());
            dto.setUserId(reminder.getUser().getId());
            dtos.add(dto);
        }

        return dtos;
    }

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

        List<ReminderDto> dtos = new ArrayList<>();
        for (Reminder reminder: reminders) {
            ReminderDto dto = new ReminderDto();
            dto.setId(reminder.getId());
            dto.setTitle(reminder.getTitle());
            dto.setDescription(reminder.getDescription());
            dto.setRemind(reminder.getRemind());
            dto.setUserId(reminder.getUser().getId());
            dtos.add(dto);
        }
        return dtos;
    }

}
