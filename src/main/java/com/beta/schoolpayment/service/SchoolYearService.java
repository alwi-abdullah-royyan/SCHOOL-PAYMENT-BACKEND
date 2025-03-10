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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class SchoolYearService {
    @Autowired
    private SchoolYearRepository schoolYearRepository;

    public List<SchoolYearResponse> findAll() {
        try {
            return schoolYearRepository.findAll()
                    .stream()
                    .map(this::convertToResponse)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all classes", e);
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
            SchoolYear schoolYear = schoolYearRepository.findBySchoolYearId(id)
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

    private SchoolYearResponse convertToResponse(SchoolYear schoolYear) {
        SchoolYearResponse response = new SchoolYearResponse();
        response.setSchoolYearId(schoolYear.getSchoolYearId());
        response.setSchoolYear(schoolYear.getSchoolYear());
        response.setStartDate(schoolYear.getStartDate());
        response.setEndDate(schoolYear.getEndDate());
        response.setCreatedAt(schoolYear.getCreatedAt());
        response.setUpdatedAt(schoolYear.getUpdatedAt());
        return response;
    }
}
