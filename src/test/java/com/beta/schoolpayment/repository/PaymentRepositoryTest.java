package com.beta.schoolpayment.repository;

import com.beta.schoolpayment.model.*;
import jakarta.persistence.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PaymentTypeRepository paymentTypeRepository;

    private User user;
    private Student student;
    private PaymentType paymentType;
    private Payment payment;

    @BeforeEach
    void setUp() {
        // Setup User
        user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("password123");
        userRepository.saveAndFlush(user);

        // Setup Student
        student = new Student();
        student.setName("John Doe");
        studentRepository.saveAndFlush(student);

        // Setup Payment Type
        paymentType = new PaymentType();
        paymentType.setPaymentTypeName("SPP");
        paymentTypeRepository.saveAndFlush(paymentType);

        // Setup Payment
        payment = new Payment();
        payment.setPaymentId(UUID.randomUUID());
        payment.setPaymentName("SPP Bulan Maret");
        payment.setUser(user);
        payment.setStudent(student);
        payment.setPaymentType(paymentType);
        payment.setAmount(BigDecimal.valueOf(500000));
        payment.setPaymentStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.saveAndFlush(payment);
    }


    @Test
    void testFindByUser_UserId() {
        List<Payment> payments = paymentRepository.findByUser_UserId(user.getUserId());

        assertThat(payments).isNotEmpty();
        assertThat(payments.get(0).getUser().getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    void testFindByStudent_Id() {
        List<Payment> payments = paymentRepository.findByStudent_Id(student.getId());

        assertThat(payments).isNotEmpty();
        assertThat(payments.get(0).getStudent().getId()).isEqualTo(student.getId());
    }

    @Test
    void testFindByUser_UserIdAndDeletedAtIsNull() {
        List<Payment> payments = paymentRepository.findByUser_UserIdAndDeletedAtIsNull(user.getUserId());

        assertThat(payments).isNotEmpty();
        assertThat(payments.get(0).getDeletedAt()).isNull();
    }

    @Test
    void testFindByPaymentStatus() {
        Pageable pageable = PageRequest.of(0, 10); // Menggunakan PageRequest untuk pagination
        Page<Payment> paymentsPage = paymentRepository.findByPaymentStatus("PENDING", pageable);

        assertThat(paymentsPage).isNotEmpty();
        assertThat(paymentsPage.getContent().get(0).getPaymentStatus()).isEqualTo("PENDING");
    }
}
