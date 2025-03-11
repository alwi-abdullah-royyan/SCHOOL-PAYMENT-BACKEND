package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.request.StudentRequest;
import com.beta.schoolpayment.dto.response.StudentResponse;
import com.beta.schoolpayment.exception.DataNotFoundException;
import com.beta.schoolpayment.exception.ValidationException;
import com.beta.schoolpayment.model.Classes;
import com.beta.schoolpayment.model.Student;
import com.beta.schoolpayment.repository.ClassesRepository;
import com.beta.schoolpayment.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassesRepository classesRepository;

    public Page<StudentResponse> getAllStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> students = studentRepository.findAll(pageable);

        return students.map(StudentService::convertToResponse);
    }

    @Transactional
    public StudentResponse createStudent(StudentRequest studentRequest) {
        // Cek apakah NIS sudah ada di database
        if (studentRepository.existsByNis(studentRequest.getNis())) {
            throw new ValidationException("NIS " + studentRequest.getNis() + " is already registered.");
        }

        Student student = new Student();
        Classes classes = classesRepository.findById(studentRequest.getClassId())
                .orElseThrow(() -> new DataNotFoundException("Class not found"));

        student.setNis(studentRequest.getNis());
        student.setName(studentRequest.getName());
        student.setBirthdate(studentRequest.getBirthdate());
        student.setAddress(studentRequest.getAddress());
        student.setPhoneNumber(studentRequest.getPhoneNumber());
        student.setClasses(classes);

        student = studentRepository.save(student);
        return convertToResponse(student);
    }
    //soft delete
    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found"));
        student.setDeletedAt(LocalDateTime.now());
        studentRepository.save(student);
    }
    //hard delete
    public void hardDeleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found"));
        studentRepository.deleteById(studentId);
    }
    @Transactional
    public StudentResponse updateStudent(Long studentId, StudentRequest studentRequest) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("Student not found"));
        if (studentRequest.getName() != null && !studentRequest.getName().equals(student.getName())) {
            student.setName(studentRequest.getName());
        }
        if (studentRequest.getBirthdate() != null && !studentRequest.getBirthdate().equals(student.getBirthdate())) {
            student.setBirthdate(studentRequest.getBirthdate());
        }
        if (studentRequest.getAddress() != null && !studentRequest.getAddress().equals(student.getAddress())) {
            student.setAddress(studentRequest.getAddress());
        }
        if (studentRequest.getPhoneNumber() != null && !studentRequest.getPhoneNumber().equals(student.getPhoneNumber())) {
            student.setPhoneNumber(studentRequest.getPhoneNumber());
        }
        if (studentRequest.getClassId() != null && !studentRequest.getClassId().equals(student.getClasses().getClassesId())) {
            Classes classes = classesRepository.findById(studentRequest.getClassId())
                    .orElseThrow(() -> new DataNotFoundException("Class not found"));
            student.setClasses(classes);
        }

        student = studentRepository.save(student);
        return convertToResponse(student);
    }



    public Page<Student> getStudents(String search, LocalDate startDate, LocalDate endDate, String sort, int page, int size) {
        // Pastikan search tidak null (menghindari error LOWER(NULL))
        search = (search == null) ? "" : search;

        // Atur sorting berdasarkan nama
        Sort sortBy = Sort.by(sort.equalsIgnoreCase("desc") ? Sort.Order.desc("name") : Sort.Order.asc("name"));
        Pageable pageable = PageRequest.of(page, size, sortBy);

        return studentRepository.findStudents(search, startDate, endDate, pageable);
    }






    public static StudentResponse convertToResponse(Student student) {
        StudentResponse response = new StudentResponse();

        response.setId(student.getId());
        response.setNis(student.getNis());
        response.setName(student.getName());

        if (student.getClasses() != null) {
            response.setClassId(student.getClasses().getClassesId());
            response.setClassName(student.getClasses().getClassesName());
        }

        response.setBirthdate(student.getBirthdate());
        response.setAddress(student.getAddress());
        response.setPhoneNumber(student.getPhoneNumber());
        response.setCreatedAt(student.getCreatedAt());
        response.setUpdatedAt(student.getUpdatedAt());

        return response;
    }

}
