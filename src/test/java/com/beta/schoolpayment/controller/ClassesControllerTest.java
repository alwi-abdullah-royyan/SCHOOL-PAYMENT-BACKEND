package com.beta.schoolpayment.controller;

import com.beta.schoolpayment.dto.request.ClassesRequest;
import com.beta.schoolpayment.dto.response.ApiResponse;
import com.beta.schoolpayment.dto.response.ClassesResponse;
import com.beta.schoolpayment.service.ClassesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ClassesControllerTest {

    @Mock
    private ClassesService classesService;

    @InjectMocks
    private ClassesController classesController;

    private ClassesRequest classesRequest;
    private ClassesResponse classesResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        classesResponse = new ClassesResponse();
        classesResponse.setClassesId(1L);
        classesResponse.setClassesName("Mathematics");

        classesRequest = new ClassesRequest();
        classesRequest.setClassesName("Mathematics");
        classesRequest.setSchoolYearId(1L);
    }

//    @Test
//    void testGetAllClasses_Success() {
//        when(classesService.findAll()).thenReturn(List.of(classesResponse));
//
//        ResponseEntity<?> response = classesController.getAllClasses();
//
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(((ApiResponse<?>) response.getBody()).getData()).isEqualTo(List.of(classesResponse));
//        verify(classesService, times(1)).findAll();
//    }

    @Test
    void testCreateClasses_Success() {
        when(classesService.createClasses(classesRequest)).thenReturn(classesResponse);

        ResponseEntity<?> response = classesController.createClasses(classesRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(((ApiResponse<?>) response.getBody()).getData()).isEqualTo(classesResponse);
        verify(classesService, times(1)).createClasses(classesRequest);
    }

    @Test
    void testUpdateClasses_Success() {
        Long id = 1L;
        when(classesService.updateClasses(id, classesRequest)).thenReturn(classesResponse);

        ResponseEntity<?> response = classesController.updateClasses(id, classesRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(((ApiResponse<?>) response.getBody()).getData()).isEqualTo(classesResponse);
        verify(classesService, times(1)).updateClasses(id, classesRequest);
    }

    @Test
    void testDeleteClasses_Success() {
        Long id = 1L;
        doNothing().when(classesService).deleteClasses(id);

        ResponseEntity<?> response = classesController.deleteClasses(id);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(((ApiResponse<?>) response.getBody()).getData()).isEqualTo("Classes deleted successfully");
        verify(classesService, times(1)).deleteClasses(id);
    }

//    @Test
//    void testSearchClasses_Success() {
//        String searchKeyword = "Math";
//        when(classesService.searchClasses(searchKeyword)).thenReturn(List.of(classesResponse));
//
//        ResponseEntity<ApiResponse<List<ClassesResponse>>> response = classesController.searchClasses(searchKeyword);
//
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody().getData()).isEqualTo(List.of(classesResponse));
//        verify(classesService, times(1)).searchClasses(searchKeyword);
//    }
}
