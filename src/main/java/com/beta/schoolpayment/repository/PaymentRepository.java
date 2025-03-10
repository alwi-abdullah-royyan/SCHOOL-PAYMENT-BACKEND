package com.beta.schoolpayment.repository;


import com.beta.schoolpayment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;



@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByUser_UserId(UUID userId);
    List<Payment> findByStudent_Id(Long Id);
    List<Payment> findByUser_Nis(Long nis);
}
