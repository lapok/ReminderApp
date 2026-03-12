package org.example.reminderapp.service;

import org.example.reminderapp.dto.CreateReminderRequest;
import org.example.reminderapp.dto.ReminderDto;
import org.example.reminderapp.dto.UpdateReminderRequest;
import org.example.reminderapp.model.Reminder;
import org.example.reminderapp.model.User;
import org.example.reminderapp.repository.ReminderRepository;
import org.example.reminderapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;
    @Autowired
    private UserRepository userRepository;

    public ReminderDto createReminder(CreateReminderRequest request){
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

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

    public ReminderDto getReminderById(Long id){
        Optional<Reminder> optionalReminder = reminderRepository.findById(id);

        if (optionalReminder.isPresent()){
            Reminder reminder = optionalReminder.get();

            ReminderDto dto = new ReminderDto();

            dto.setId(reminder.getId());
            dto.setTitle(reminder.getTitle());
            dto.setDescription(reminder.getDescription());
            dto.setRemind(reminder.getRemind());
            dto.setUserId(reminder.getUser().getId());

            return dto;
        } else throw new RuntimeException ("Напоминалка с таким id " + id + " не найдена");
    }

    public ReminderDto updateReminder(Long id, UpdateReminderRequest request){
        Optional<Reminder> optionalReminder = reminderRepository.findById(id);

        if (optionalReminder.isPresent()){
            Reminder reminder = optionalReminder.get();

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
        } else throw new RuntimeException("Напоминалка с id " + id + " не найдена");
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

    public void deleteReminder(Long id){
            Optional<Reminder> optionalReminder = reminderRepository.findById(id);

            if (optionalReminder.isPresent()){
                Reminder reminder = optionalReminder.get();
                reminderRepository.delete(reminder);
        } else throw new RuntimeException("Напоминалка с id " + id + " не найдена");
    }

}
