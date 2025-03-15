package com.beta.schoolpayment.controller;

import com.beta.schoolpayment.dto.request.SchoolYearRequest;
import com.beta.schoolpayment.dto.response.ApiResponse;
import com.beta.schoolpayment.dto.response.SchoolYearResponse;
import com.beta.schoolpayment.service.SchoolYearService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class SchoolYearControllerTest {
    @InjectMocks
    private SchoolYearController schoolYearController;

    @Mock
    private SchoolYearService schoolYearService;

    private SchoolYearResponse schoolYearResponse;
    private SchoolYearRequest schoolYearRequest;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        schoolYearResponse = new SchoolYearResponse();
        schoolYearResponse.setSchoolYearId(1L);
        schoolYearResponse.setSchoolYear("2023-2024");

        schoolYearRequest = new SchoolYearRequest();
        schoolYearRequest.setSchoolYear("2023-2024");
        schoolYearRequest.setStartDate(LocalDate.of(2023, 9, 1));
        schoolYearRequest.setEndDate(LocalDate.of(2024, 6, 30));
    }

    @Test
    void testGetAllClasses_Success() {
        when(schoolYearService.findAll()).thenReturn(List.of(schoolYearResponse));

        ResponseEntity<?> response = schoolYearController.getAllClasses();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((ApiResponse<?>) response.getBody()).getStatus()).isEqualTo(200);
        assertThat(((ApiResponse<?>) response.getBody()).getData()).isNotNull();
    }

    @Test
    void testGetAllClasses_InternalServerError() {
        when(schoolYearService.findAll()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = schoolYearController.getAllClasses();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(((ApiResponse<?>) response.getBody()).getStatus()).isEqualTo(500);
        assertThat(((ApiResponse<?>) response.getBody()).getData()).isEqualTo("Database error");
    }

    @Test
    void testCreateSchoolYear_Success() {
        when(schoolYearService.createSchoolYear(schoolYearRequest)).thenReturn(schoolYearResponse);

        ResponseEntity<?> response = schoolYearController.createSchoolYear(schoolYearRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(200);
        assertThat(apiResponse.getData()).isEqualTo(schoolYearResponse);

        verify(schoolYearService, times(1)).createSchoolYear(schoolYearRequest);
    }

    @Test
    void testCreateSchoolYear_InternalServerError() {
        when(schoolYearService.createSchoolYear(schoolYearRequest)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = schoolYearController.createSchoolYear(schoolYearRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(500);
        assertThat(apiResponse.getData()).isEqualTo("Database error");

        verify(schoolYearService, times(1)).createSchoolYear(schoolYearRequest);
    }

    @Test
    void testUpdateSchoolYear_Success() {
        Long schoolYearId = 1L;

        when(schoolYearService.updateSchoolYear(schoolYearId, schoolYearRequest)).thenReturn(schoolYearResponse);

        ResponseEntity<?> response = schoolYearController.updateSchoolYear(schoolYearId, schoolYearRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(200);
        assertThat(apiResponse.getData()).isEqualTo(schoolYearResponse);

        verify(schoolYearService, times(1)).updateSchoolYear(schoolYearId, schoolYearRequest);
    }

    @Test
    void testUpdateSchoolYear_InternalServerError() {
        Long schoolYearId = 1L;

        when(schoolYearService.updateSchoolYear(schoolYearId, schoolYearRequest)).thenThrow(new RuntimeException("Update failed"));

        ResponseEntity<?> response = schoolYearController.updateSchoolYear(schoolYearId, schoolYearRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(500);
        assertThat(apiResponse.getData()).isEqualTo("Update failed");

        verify(schoolYearService, times(1)).updateSchoolYear(schoolYearId, schoolYearRequest);
    }

    @Test
    void testDeleteSchoolYear_Success() {
        Long schoolYearId = 1L;

        doNothing().when(schoolYearService).deleteSchoolYear(schoolYearId);

        ResponseEntity<?> response = schoolYearController.deleteSchoolYear(schoolYearId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(200);
        assertThat(apiResponse.getData()).isEqualTo("School year deleted successfully");

        verify(schoolYearService, times(1)).deleteSchoolYear(schoolYearId);
    }

    @Test
    void testDeleteSchoolYear_InternalServerError() {
        Long schoolYearId = 1L;

        doThrow(new RuntimeException("Delete failed")).when(schoolYearService).deleteSchoolYear(schoolYearId);

        ResponseEntity<?> response = schoolYearController.deleteSchoolYear(schoolYearId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(500);
        assertThat(apiResponse.getData()).isEqualTo("Delete failed");

        verify(schoolYearService, times(1)).deleteSchoolYear(schoolYearId);
    }

    @Test
    void testSearchSchoolYear_Success() {
        String searchQuery = "2023";
        List<SchoolYearResponse> mockResponseList = List.of(schoolYearResponse);

        when(schoolYearService.searchSchoolYear(searchQuery)).thenReturn(mockResponseList);

        ResponseEntity<ApiResponse<List<SchoolYearResponse>>> response = schoolYearController.searchSchoolYear(searchQuery);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<List<SchoolYearResponse>> apiResponse = response.getBody();
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getStatus()).isEqualTo(200);
        assertThat(apiResponse.getData()).isEqualTo(mockResponseList);

        verify(schoolYearService, times(1)).searchSchoolYear(searchQuery);
    }

    @Test
    void testSearchSchoolYear_EmptyResult() {
        String searchQuery = "2025";
        List<SchoolYearResponse> emptyResponseList = List.of();

        when(schoolYearService.searchSchoolYear(searchQuery)).thenReturn(emptyResponseList);

        ResponseEntity<ApiResponse<List<SchoolYearResponse>>> response = schoolYearController.searchSchoolYear(searchQuery);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<List<SchoolYearResponse>> apiResponse = response.getBody();
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getStatus()).isEqualTo(200);
        assertThat(apiResponse.getData()).isEqualTo(List.of());

        verify(schoolYearService, times(1)).searchSchoolYear(searchQuery);
    }
}
