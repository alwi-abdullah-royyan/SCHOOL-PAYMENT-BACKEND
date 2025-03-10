package com.beta.schoolpayment.controller;

import com.beta.schoolpayment.dto.request.ClassesRequest;
import com.beta.schoolpayment.dto.response.ApiResponse;
import com.beta.schoolpayment.dto.response.ClassesResponse;
import com.beta.schoolpayment.service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassesController {
    @Autowired
    private ClassesService classesService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllClasses(){
        try {
            List<ClassesResponse> response = classesService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createClasses(@RequestBody ClassesRequest classesRequest){
        try {
            ClassesResponse response = classesService.createClasses(classesRequest);
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateClasses(@PathVariable Long id, @RequestBody ClassesRequest classesRequest){
        try {
            ClassesResponse response = classesService.updateClasses(id, classesRequest);
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClasses(@PathVariable Long id){
        try {
            classesService.deleteClasses(id);
            return ResponseEntity.ok(new ApiResponse<>(200, "Classes deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ClassesResponse>>> searchClasses(@RequestParam String classesName){
        List<ClassesResponse> response = classesService.searchClasses(classesName);
        return ResponseEntity.ok(new ApiResponse<>(200, response));
    }
}
