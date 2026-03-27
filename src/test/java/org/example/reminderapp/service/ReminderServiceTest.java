package org.example.reminderapp.service;


import org.example.reminderapp.dto.ReminderDto;
import org.example.reminderapp.mapper.ReminderMapper;
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

    @Mock
    private ReminderMapper reminderMapper;

    @InjectMocks
    private ReminderService reminderService;

    private User testUser;
    private Reminder testReminder;
    private ReminderDto reminderDto;

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

        reminderDto = ReminderDto.builder()
                .title("Новое напоминание")
                .description("Описание")
                .remind(LocalDateTime.now().plusHours(1))
                .build();
    }

    @Test
    void createReminder_ShouldReturnReminderDto_WhenUserExists() {
//        given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reminderMapper.toEntity(any(ReminderDto.class))).thenReturn(testReminder);
        when(reminderRepository.save(any(Reminder.class))).thenReturn(testReminder);
        when(reminderMapper.toDto(any(Reminder.class))).thenReturn(reminderDto);

//        when
        ReminderDto result = reminderService.createReminder(reminderDto, 1L);

//        then
        assertNotNull(result);
        assertEquals("Новое напоминание", result.getTitle());
        verify(userRepository).findById(1L);
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void createReminder_ShouldThrowException_WhenUserNotFound() {
//        given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

//        when & then
        assertThrows(RuntimeException.class, () -> {
            reminderService.createReminder(reminderDto, 99L);
        });
    }

    @Test
    void getReminderById_ShouldReturnReminderDto_WhenUserIsOwner() {
//        given
        when(reminderRepository.findById(1L)).thenReturn(Optional.of(testReminder));
        when(reminderMapper.toDto(any(Reminder.class))).thenReturn(reminderDto);

//        when
        ReminderDto result = reminderService.getReminderById(1L, 1L);

//        then
        assertNotNull(result);
        assertEquals("Новое напоминание", result.getTitle());
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
