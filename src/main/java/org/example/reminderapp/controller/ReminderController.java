package org.example.reminderapp.controller;

import lombok.RequiredArgsConstructor;
import org.example.reminderapp.model.CustomUserDetails;
import org.example.reminderapp.dto.ReminderDto;
import org.example.reminderapp.dto.ReminderListResponse;
import org.example.reminderapp.service.ReminderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reminder")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping("/create")
    public ResponseEntity<ReminderDto> createReminder(
            @RequestBody ReminderDto dto,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ){
//        вызывать сервис, вернуть ответ с кодом 201
        ReminderDto created = reminderService.createReminder(dto, currentUser.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderDto> getReminderById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ){
//        вызвать сервис, вернуть ответ с кодом 200
        return ResponseEntity.ok(reminderService.getReminderById(id, currentUser.getId()));
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
            @RequestBody ReminderDto dto,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {

//        вызвать сервис, вернуть обновленное напоминание
        ReminderDto updated = reminderService.updateReminder(id, dto, currentUser.getId());
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

        ReminderListResponse response = reminderService.getRemindersList(
                currentUser.getId(), page, size, sortBy, sortDirection, filterDate
        );

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
