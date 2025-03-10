package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
    Optional<PaymentType> findByPaymentTypeName(String paymentTypeName);
}
