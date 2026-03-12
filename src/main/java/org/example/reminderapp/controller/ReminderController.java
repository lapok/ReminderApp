package org.example.reminderapp.controller;

import org.example.reminderapp.dto.CreateReminderRequest;
import org.example.reminderapp.dto.ReminderDto;
import org.example.reminderapp.dto.UpdateReminderRequest;
import org.example.reminderapp.dto.UserDto;
import org.example.reminderapp.model.User;
import org.example.reminderapp.service.ReminderService;
import org.example.reminderapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reminder")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @PostMapping("/create")
    public ResponseEntity<ReminderDto> createReminder(@RequestBody CreateReminderRequest request){
//        вызывать сервис, вернуть ответ с кодом 201
        ReminderDto created = reminderService.createReminder(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderDto> getReminderById(@PathVariable Long id){
//        вызвать сервис, вернуть ответ с кодом 200
        ReminderDto currentReminder = reminderService.getReminderById(id);
        return new ResponseEntity<>(currentReminder, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ReminderDto>> getReminderByUser(@RequestParam Long userId){
//        вызвать сервис, вернуть список
        List<ReminderDto> reminders = reminderService.getRemindersByUserId(userId);
        return new ResponseEntity<>(reminders, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderDto> updateReminder(
            @PathVariable Long id,
            @RequestBody UpdateReminderRequest request) {
//        вызвать сервис, вернуть обновленное напоминание
        ReminderDto updated = reminderService.updateReminder(id, request);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id){
        reminderService.deleteReminder(id);

        return ResponseEntity.noContent().build();
    }

}
