package com.beta.schoolpayment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setPaymentId(UUID.randomUUID());
        payment.setPaymentName("SPP Bulanan");
        payment.setAmount(new BigDecimal("500000"));
        payment.setPaymentStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testPaymentInitialization() {
        assertThat(payment).isNotNull();
        assertThat(payment.getPaymentName()).isEqualTo("SPP Bulanan");
        assertThat(payment.getAmount()).isEqualTo(new BigDecimal("500000"));
        assertThat(payment.getPaymentStatus()).isEqualTo("PENDING");
    }

    @Test
    void testPrePersistCallback() {
        Payment newPayment = new Payment();
        newPayment.onCreate();

        assertThat(newPayment.getCreatedAt()).isNotNull();
        assertThat(newPayment.getUpdatedAt()).isNotNull();
        assertThat(newPayment.getCreatedAt()).isEqualTo(newPayment.getUpdatedAt());
    }

    @Test
    void testPreUpdateCallback() throws InterruptedException {
        Payment newPayment = new Payment();
        newPayment.onCreate();

        LocalDateTime beforeUpdate = newPayment.getUpdatedAt();
        Thread.sleep(1000); // Simulasi jeda sebelum update

        newPayment.onUpdate();
        assertThat(newPayment.getUpdatedAt()).isAfter(beforeUpdate);
    }

    @Test
    void testSoftDelete() {
        payment.setDeletedAt(LocalDateTime.now());
        assertThat(payment.getDeletedAt()).isNotNull();
    }

    @Test
    void testPaymentStatusConstraint() {
        payment.setPaymentStatus("COMPLETED");
        assertThat(payment.getPaymentStatus()).isEqualTo("COMPLETED");

        payment.setPaymentStatus("FAILED");
        assertThat(payment.getPaymentStatus()).isEqualTo("FAILED");

        payment.setPaymentStatus("REFUNDED");
        assertThat(payment.getPaymentStatus()).isEqualTo("REFUNDED");
    }

    @Test
    void testInvalidPaymentStatus() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            payment.setPaymentStatus("INVALID_STATUS");
        });

        assertEquals("Invalid payment status: INVALID_STATUS", exception.getMessage());
    }

    @Test
    void testNullValuesShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            Payment invalidPayment = new Payment();
            invalidPayment.setPaymentName(null); // Seharusnya tidak boleh null
        });

        assertThrows(NullPointerException.class, () -> {
            Payment invalidPayment = new Payment();
            invalidPayment.setAmount(null); // Seharusnya tidak boleh null
        });
    }

    @Test
    void testEqualsAndHashCode() {
        Payment payment1 = new Payment();
        payment1.setPaymentId(UUID.randomUUID());
        payment1.setPaymentName("SPP Semester");

        Payment payment2 = new Payment();
        payment2.setPaymentId(payment1.getPaymentId());
        payment2.setPaymentName("SPP Semester");

        assertThat(payment1).isEqualTo(payment2);
        assertThat(payment1.hashCode()).isEqualTo(payment2.hashCode());
    }
}
