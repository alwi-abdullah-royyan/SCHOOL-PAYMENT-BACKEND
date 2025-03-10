package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByNis(Long nis);
    @Query("SELECT s FROM Student s WHERE (:startDate IS NULL OR s.createdAt >= :startDate) AND (:endDate IS NULL OR s.createdAt <= :endDate)")
    Page<Student> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
}
