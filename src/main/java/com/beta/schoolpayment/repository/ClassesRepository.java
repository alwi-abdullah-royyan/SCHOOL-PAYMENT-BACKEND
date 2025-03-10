package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.Classes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassesRepository extends JpaRepository<Classes, Long> {
    List<Classes> findByClassesId(Long classesId);
    List<Classes> findByClassesNameContainingIgnoringCase(String classesName);
}
