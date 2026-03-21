package org.example.reminderapp.service;


import org.example.reminderapp.dto.CreateReminderRequest;
import org.example.reminderapp.dto.ReminderDto;
import org.example.reminderapp.model.Reminder;
import org.example.reminderapp.model.User;
import org.example.reminderapp.repository.ReminderRepository;
import org.example.reminderapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ReminderServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReminderService reminderService;

    private User testUser;
    private Reminder testReminder;
    private CreateReminderRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@mail.ru");

        testReminder = new Reminder();
        testReminder.setId(1L);
        testReminder.setTitle("Тест");
        testReminder.setDescription("Описание");
        testReminder.setRemind(LocalDateTime.now().plusHours(1));
        testReminder.setUser(testUser);

        createRequest = new CreateReminderRequest();
        createRequest.setTitle("Новое напоминание");
        createRequest.setDescription("Описание");
        createRequest.setRemind(LocalDateTime.now().plusHours(1));
    }

    @Test
    void createReminder_ShouldReturnReminderDto_WhenUserExists() {
//        given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(testReminder);

//        when
        ReminderDto result = reminderService.createReminder(createRequest, 1L);

//        then
        assertNotNull(result);
        assertEquals("Тест", result.getTitle());
        verify(userRepository).findById(1L);
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void createReminder_ShouldThrowException_WhenUserNotFound() {
//        given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

//        when & then
        assertThrows(RuntimeException.class, () -> {
            reminderService.createReminder(createRequest, 99L);
        });
    }

    @Test
    void getReminderById_ShouldReturnReminderDto_WhenUserIsOwner() {
//        given
        when(reminderRepository.findById(1L)).thenReturn(Optional.of(testReminder));

//        when
        ReminderDto result = reminderService.getReminderById(1L, 1L);

//        then
        assertNotNull(result);
        assertEquals("Тест", result.getTitle());
    }

    @Test
    void getReminderById_ShouldThrowException_WhenUserIsNotOwner() {
//        given
        when(reminderRepository.findById(1L)).thenReturn(Optional.of(testReminder));

//        when & then
        assertThrows(RuntimeException.class, () -> {
            reminderService.getReminderById(1L, 99L);
        });
    }

    @Test
    void deleteReminder_ShouldDelete_WhenUserIsOwner() {
//        given
        when(reminderRepository.findById(1L)).thenReturn(Optional.of(testReminder));

//        when
        reminderService.deleteReminder(1L, 1L);

//        then
        verify(reminderRepository).delete(testReminder);
    }

    @Test
    void deleteReminder_ShouldThrowException_WhenUserIsNotOwner() {
//        given
        when(reminderRepository.findById(1L)).thenReturn(Optional.of(testReminder));

//        when & then
        assertThrows(RuntimeException.class, () -> {
            reminderService.deleteReminder(1L, 99L);
        });
    }
}
