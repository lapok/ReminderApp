package org.example.reminderapp.controller;

import org.example.reminderapp.config.CustomUserDetails;
import org.example.reminderapp.dto.*;
import org.example.reminderapp.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reminder")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @PostMapping("/create")
    public ResponseEntity<ReminderDto> createReminder(
            @RequestBody CreateReminderRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ){
//        вызывать сервис, вернуть ответ с кодом 201
        ReminderDto created = reminderService.createReminder(request, currentUser.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderDto> getReminderById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ){
//        вызвать сервис, вернуть ответ с кодом 200
        ReminderDto currentReminder = reminderService.getReminderById(id, currentUser.getId());
        return new ResponseEntity<>(currentReminder, HttpStatus.OK);
    }

//    Заккоментил этот метод, он нам не нужен я думаю
//    @GetMapping
//    public ResponseEntity<List<ReminderDto>> getReminderByUser(@RequestParam Long userId){
////        вызвать сервис, вернуть список
//        List<ReminderDto> reminders = reminderService.getRemindersByUserId(userId);
//        return new ResponseEntity<>(reminders, HttpStatus.OK);
//    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderDto> updateReminder(
            @PathVariable Long id,
            @RequestBody UpdateReminderRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {

//        вызвать сервис, вернуть обновленное напоминание
        ReminderDto updated = reminderService.updateReminder(id, request, currentUser.getId());
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReminder(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ){
        reminderService.deleteReminder(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping({"/list"})
    public ResponseEntity<ReminderListResponse> getRemindersList(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "remind") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String filterDate
    ){
        Long userId = currentUser.getId();
        Page<ReminderDto> dtoPage = reminderService.getRemindersList(userId, page, size, sortBy, sortDirection, filterDate);

        ReminderListResponse response = new ReminderListResponse();
        response.setContent(dtoPage.getContent());
        response.setPage(dtoPage.getNumber());
        response.setSize(dtoPage.getSize());
        response.setTotalElements(dtoPage.getTotalElements());
        response.setTotalPages(dtoPage.getTotalPages());
        response.setLast(dtoPage.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sort")
    public ResponseEntity<List<ReminderDto>> sortReminders(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam String by
    ){
        List<ReminderDto> sortedReminders = reminderService.sortReminders(
                currentUser.getId(), by
        );
        return ResponseEntity.ok(sortedReminders);
    }

    @GetMapping("/filtr")
    public ResponseEntity<List<ReminderDto>> filterReminders(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String time
            ){
        List<ReminderDto> filtered = reminderService.filterReminders(
                currentUser.getId(), date, time
        );

        return ResponseEntity.ok(filtered);
    }

}
