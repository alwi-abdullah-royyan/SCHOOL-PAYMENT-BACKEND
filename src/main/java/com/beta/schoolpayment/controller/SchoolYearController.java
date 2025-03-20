package com.beta.schoolpayment.controller;


import com.beta.schoolpayment.dto.request.SchoolYearRequest;
import com.beta.schoolpayment.dto.response.ApiResponse;
import com.beta.schoolpayment.dto.response.ClassesResponse;
import com.beta.schoolpayment.dto.response.SchoolYearResponse;
import com.beta.schoolpayment.service.SchoolYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/school-years")
public class SchoolYearController {
    @Autowired
    private SchoolYearService schoolYearService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllClasses(Pageable pageable) {
        try {
            Page<SchoolYearResponse> response = schoolYearService.findAll(pageable);
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSchoolYear(@RequestBody SchoolYearRequest schoolYearRequest) {
        try {
            SchoolYearResponse response = schoolYearService.createSchoolYear(schoolYearRequest);
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSchoolYear(@PathVariable Long id, @RequestBody SchoolYearRequest schoolYearRequest) {
        try {
            SchoolYearResponse response = schoolYearService.updateSchoolYear(id, schoolYearRequest);
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSchoolYear(@PathVariable Long id) {
        try {
            schoolYearService.deleteSchoolYear(id);
            return ResponseEntity.ok(new ApiResponse<>(200, "School year deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SchoolYearResponse>>> searchSchoolYear(@RequestParam String schoolYear) {
        List<SchoolYearResponse> response = schoolYearService.searchSchoolYear(schoolYear);
        return ResponseEntity.ok(new ApiResponse<>(200, response));
    }

    @DeleteMapping("/{id}/soft")
    public ResponseEntity<String> softDeleteSchoolYear(@PathVariable Long id) {
        try {
            schoolYearService.softDeleteSchoolYear(id);
            return ResponseEntity.ok("School year successfully soft deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<String> restoreSchoolYear(@PathVariable Long id) {
        try {
            schoolYearService.restoreSchoolYear(id);
            return ResponseEntity.ok("School year successfully restored");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
}
