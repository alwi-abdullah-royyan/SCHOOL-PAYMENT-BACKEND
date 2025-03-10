package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.request.PaymentRequest;
import com.beta.schoolpayment.dto.response.PaymentResponse;
import com.beta.schoolpayment.model.Payment;
import com.beta.schoolpayment.model.PaymentType;
import com.beta.schoolpayment.model.Student;
import com.beta.schoolpayment.model.User;
import com.beta.schoolpayment.repository.PaymentRepository;
import com.beta.schoolpayment.repository.PaymentTypeRepository;
import com.beta.schoolpayment.repository.StudentRepository;
import com.beta.schoolpayment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PaymentTypeRepository paymentTypeRepository;

    // ✅ Create Payment
    public PaymentResponse createPayment(PaymentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        PaymentType paymentType = paymentTypeRepository.findById(request.getPaymentTypeId())
                .orElseThrow(() -> new RuntimeException("Payment Type not found"));

        Payment payment = new Payment();
        payment.setPaymentName(request.getPaymentName());
        payment.setUser(user);
        payment.setStudent(student);
        payment.setPaymentType(paymentType);
        payment.setAmount(request.getAmount());
        payment.setPaymentStatus(request.getPaymentStatus());
        payment.setDescription(request.getDescription());

        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
    }

    // ✅ Get Payment by ID
    public PaymentResponse getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return convertToResponse(payment);
    }

    // ✅ Get All Payments
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Soft Delete Payment (Set deletedAt)
    public void deletePayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Soft delete by setting deletedAt instead of hard delete
        payment.setDeletedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    // ✅ Convert Payment Entity to DTO Response
    private PaymentResponse convertToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setPaymentName(payment.getPaymentName());
        response.setUserId(payment.getUser().getUserId());
        response.setStudentId(payment.getStudent().getId());
        response.setPaymentTypeId(Long.valueOf(payment.getPaymentType().getPaymentTypeId()));
        response.setAmount(payment.getAmount());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setDescription(payment.getDescription());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        response.setDeletedAt(payment.getDeletedAt());
        return response;
    }
}
