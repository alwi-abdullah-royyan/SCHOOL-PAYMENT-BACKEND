package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.Classes;
import com.beta.schoolpayment.model.SchoolYear;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassesRepository extends JpaRepository<Classes, Long> {
    List<Classes> findByClassesId(Long classesId);
    List<Classes> findBySchoolYear(SchoolYear schoolYear);
    Page<Classes> findByClassesNameContainingIgnoringCase(String classesName, Pageable pageable);
}
