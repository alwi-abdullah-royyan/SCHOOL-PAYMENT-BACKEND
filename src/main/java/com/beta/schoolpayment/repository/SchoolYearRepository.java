package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.SchoolYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SchoolYearRepository extends JpaRepository<SchoolYear, Long> {
    List<SchoolYear> findBySchoolYearContainingIgnoringCase(String schoolYear);
    Optional<SchoolYear> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
}
