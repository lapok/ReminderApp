package org.example.reminderapp.mapper;


import org.example.reminderapp.dto.ReminderDto;
import org.example.reminderapp.model.Reminder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReminderMapper {

    @Mapping(source = "user.id", target = "userId")
    ReminderDto toDto(Reminder reminder);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "notified", ignore = true)
    Reminder toEntity(ReminderDto dto);
}