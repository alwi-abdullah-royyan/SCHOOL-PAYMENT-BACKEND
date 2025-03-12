package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
    Optional<PaymentType> findByPaymentTypeName(String paymentTypeName);
    @Query("SELECT p FROM PaymentType p WHERE LOWER(p.paymentTypeName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<PaymentType> searchByName(String query);
    @Modifying
    @Query("DELETE FROM PaymentType p WHERE p.paymentTypeId = :id")
    void hardDeleteById(@Param("id") Long id);

}
