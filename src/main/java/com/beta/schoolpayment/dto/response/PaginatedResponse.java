package com.beta.schoolpayment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
    public int status;
    public List<T> data;
    public int totalPages;
    public int currentPage;
    public int size;
    public long totalElements;
    public PaginatedResponse(int status, Page<T> page) {
        this.status = status;
        this.data = page.getContent();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
    }
}