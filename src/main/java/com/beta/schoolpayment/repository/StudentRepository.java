package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByNis(Long nis);
    // Filter by School Year, Search Name, and Soft Delete Check
    @Query("SELECT s FROM Student s " +
            "JOIN s.classes c " +
            "JOIN c.schoolYear sy " +
            "WHERE (:startDate IS NULL OR sy.startDate >= :startDate) " +
            "AND (:endDate IS NULL OR sy.endDate <= :endDate) " +
            "AND (:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND s.deletedAt IS NULL")
    Page<Student> findStudents(
            @Param("search") String search,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );



}
