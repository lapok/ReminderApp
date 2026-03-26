package org.example.reminderapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderListResponse {
    private List<ReminderDto> content;
    private int page;
    private int size;
    private Long totalElements;
    private int totalPages;
    private boolean last;

}
