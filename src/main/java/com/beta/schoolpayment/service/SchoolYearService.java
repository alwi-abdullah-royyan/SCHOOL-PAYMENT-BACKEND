package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.request.ClassesRequest;
import com.beta.schoolpayment.dto.request.SchoolYearRequest;
import com.beta.schoolpayment.dto.response.ClassesResponse;
import com.beta.schoolpayment.dto.response.SchoolYearResponse;
import com.beta.schoolpayment.model.Classes;
import com.beta.schoolpayment.model.SchoolYear;
import com.beta.schoolpayment.repository.SchoolYearRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class SchoolYearService {
    @Autowired
    private SchoolYearRepository schoolYearRepository;

    public Page<SchoolYearResponse> findAll(Pageable pageable) {
        try {
            return schoolYearRepository.findAll(pageable)
                    .map(this::convertToResponse);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all school years", e);
        }
    }

    @Transactional
    public SchoolYearResponse createSchoolYear(SchoolYearRequest request) {
        try {
            SchoolYear schoolYear = new SchoolYear();
            schoolYear.setSchoolYear(request.getSchoolYear());
            schoolYear.setStartDate(request.getStartDate());
            schoolYear.setEndDate(request.getEndDate());
            schoolYearRepository.save(schoolYear);
            return convertToResponse(schoolYear);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create classes", e);
        }
    }

    @Transactional
    public SchoolYearResponse updateSchoolYear(Long id, SchoolYearRequest request) {
        try {
            SchoolYear schoolYear = schoolYearRepository.findById(id)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("School year not found"));
            schoolYear.setSchoolYear(request.getSchoolYear());
            schoolYear.setStartDate(request.getStartDate());
            schoolYear.setEndDate(request.getEndDate());
            schoolYearRepository.save(schoolYear);
            return convertToResponse(schoolYear);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update classes", e);
        }
    }

    public void deleteSchoolYear(Long schoolYearId) {
        try {
            if (!schoolYearRepository.existsById(schoolYearId)) {
                throw new RuntimeException("School year not found");
            }
            schoolYearRepository.deleteById(schoolYearId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete classes", e);
        }
    }

    public List<SchoolYearResponse> searchSchoolYear(String schoolYear) {
        try {
            return schoolYearRepository.findBySchoolYearContainingIgnoringCase(schoolYear)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to search classes", e);
        }
    }

    @Transactional
    public void softDeleteSchoolYear(Long schoolYearId) {
        SchoolYear schoolYear = schoolYearRepository.findById(schoolYearId)
                .orElseThrow(() -> new RuntimeException("School year not found"));

        schoolYear.setDeletedAt(LocalDateTime.now());
        schoolYearRepository.save(schoolYear);
    }

    @Transactional
    public void restoreSchoolYear(Long id) {
        SchoolYear schoolYear = schoolYearRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("School year not found"));
        if (schoolYear.getDeletedAt() == null) {
            throw new RuntimeException("School year is not soft deleted");
        }
        schoolYear.setDeletedAt(null); // Mengembalikan data
        schoolYearRepository.save(schoolYear);
    }

    private SchoolYearResponse convertToResponse(SchoolYear schoolYear) {
        SchoolYearResponse response = new SchoolYearResponse();
        response.setSchoolYearId(schoolYear.getId());
        response.setSchoolYear(schoolYear.getSchoolYear());
        response.setStartDate(schoolYear.getStartDate());
        response.setEndDate(schoolYear.getEndDate());
        response.setCreatedAt(schoolYear.getCreatedAt());
        response.setUpdatedAt(schoolYear.getUpdatedAt());
        response.setDeletedAt(schoolYear.getDeletedAt());
        return response;
    }
}
