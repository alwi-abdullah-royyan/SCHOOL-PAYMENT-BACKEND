package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.request.ClassesRequest;
import com.beta.schoolpayment.dto.response.ClassesResponse;
import com.beta.schoolpayment.model.Classes;
import com.beta.schoolpayment.model.SchoolYear;
import com.beta.schoolpayment.repository.ClassesRepository;
import com.beta.schoolpayment.repository.SchoolYearRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class ClassesService {
    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private SchoolYearRepository schoolYearRepository;

    public Page<ClassesResponse> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Classes> classes = classesRepository.findAll(pageable);
            return classes.map(this::convertToResponse);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all classes", e);
        }
    }
    public ClassesResponse getClassesById(Long classesId) {
        try {
            Classes classes = classesRepository.findByClassesId(classesId)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Classes not found"));
            return convertToResponse(classes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get classes by id", e);
        }
    }
    @Transactional
    public ClassesResponse createClasses(ClassesRequest request) {
        try {
            Classes classes = new Classes();
            classes.setClassesName(request.getClassesName());
            SchoolYear schoolYear = schoolYearRepository.findById(request.getSchoolYearId())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("School year not found"));
            classes.setSchoolYear(schoolYear);
            classesRepository.save(classes);
            return convertToResponse(classes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create classes", e);
        }
    }

    @Transactional
    public ClassesResponse updateClasses(Long id, ClassesRequest classesRequest) {
        try {
            Classes classes = classesRepository.findByClassesId(id)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Classes not found"));
            classes.setClassesName(classesRequest.getClassesName());
            SchoolYear schoolYear = schoolYearRepository.findById(classesRequest.getSchoolYearId())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("School year not found"));
            classes.setSchoolYear(schoolYear);
            classesRepository.save(classes);
            return convertToResponse(classes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update classes", e);
        }
    }

    public void deleteClasses(Long classesId) {
        try {
            if (!classesRepository.existsById(classesId)) {
                throw new RuntimeException("Classes not found");
            }
            classesRepository.deleteById(classesId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete classes", e);
        }
    }

    public Page<ClassesResponse> searchClasses(String classesName, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Classes> classes = classesRepository.findByClassesNameContainingIgnoringCase(classesName, pageable);
            return classes.map(this::convertToResponse);
        } catch (Exception e) {
            throw new RuntimeException("Failed to search classes", e);
        }
    }

    private ClassesResponse convertToResponse(Classes classes) {
        ClassesResponse response = new ClassesResponse();
        response.setClassesId(classes.getClassesId());
        response.setClassesName(classes.getClassesName());
        response.setSchoolYearId(classes.getSchoolYear().getId());
        response.setCreatedAt(classes.getCreatedAt());
        response.setUpdatedAt(classes.getUpdatedAt());
        return response;
    }
}
