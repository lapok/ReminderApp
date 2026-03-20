package org.example.reminderapp.dto;

import java.util.List;

public class ReminderListResponse {
    private List<ReminderDto> content;
    private int page;
    private int size;
    private Long totalElements;
    private int totalPages;
    private boolean last;

    public ReminderListResponse(){}

    public List<ReminderDto> getContent() {
        return content;
    }

    public void setContent(List<ReminderDto> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
