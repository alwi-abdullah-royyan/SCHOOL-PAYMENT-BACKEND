package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.request.PaymentFilterCriteria;
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
import com.beta.schoolpayment.specification.PaymentSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
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
    public PaymentResponse createPayment(PaymentRequest request, UserDetails userDetails) {
        // Ambil user berdasarkan email dari token JWT
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Cari student berdasarkan studentId dari request
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student tidak ditemukan"));

        // Cari jenis pembayaran
        PaymentType paymentType = paymentTypeRepository.findById(request.getPaymentTypeId())
                .orElseThrow(() -> new RuntimeException("Jenis pembayaran tidak ditemukan"));
        // ✅ List pembayaran yang diperbolehkan
        List<String> allowedPaymentTypes = Arrays.asList("SPP", "UTS", "UAS", "Ekstrakurikuler", "Lainnya");

        // Buat objek Payment
        Payment payment = new Payment();
        payment.setPaymentName(request.getPaymentName());
        payment.setUser(user);
        payment.setStudent(student);
        payment.setPaymentType(paymentType);
        payment.setAmount(request.getAmount());
        payment.setPaymentStatus(request.getPaymentStatus());
        payment.setDescription(request.getDescription());

        // Simpan ke database
        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
    }

    // ✅ Get Payment by ID
    public PaymentResponse getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment dengan ID " + paymentId + " tidak ditemukan"));
        return convertToResponse(payment);
    }

    // ✅ Get User Payments (Hanya untuk pengguna yang login & tanpa soft deleted)
    public List<PaymentResponse> getUserPayments(UUID userId) {
        List<Payment> payments = paymentRepository.findByUser_UserIdAndDeletedAtIsNull(userId);

        return payments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Get All Payments (Dengan Pagination, Sorting, dan Filtering)
    public Page<PaymentResponse> getAllPayments(
            PaymentFilterCriteria criteria, int page, int size, String sortBy, String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Payment> spec = new PaymentSpecification(criteria);
        Page<Payment> payments = paymentRepository.findAll(spec, pageable);

        return payments.map(this::convertToResponse);
    }

    // ✅ Get All Active Payments (Tanpa soft delete)
    public List<PaymentResponse> getAllActivePayments() {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getDeletedAt() == null) // Hanya yang belum dihapus
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Soft Delete Payment (Set deletedAt)
    public void deletePayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment tidak ditemukan"));

        payment.setDeletedAt(LocalDateTime.now()); // Soft delete
        paymentRepository.save(payment);
    }

    // ✅ Update Payment Status
    public PaymentResponse updatePaymentStatus(UUID id, String status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment tidak ditemukan dengan ID: " + id));

        // Validasi status pembayaran yang diperbolehkan
        List<String> validStatuses = Arrays.asList("PENDING","FAILED", "REFUNDED", "COMPLETED");
        if (!validStatuses.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Status pembayaran tidak valid. Pilihan: ,FAILED, PENDING,REFUNDED, CANCELED, COMPLETED");
        }

        payment.setPaymentStatus(status.toUpperCase());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        return convertToResponse(updatedPayment);
    }

    // ✅ Convert Payment Entity to DTO Response
    private PaymentResponse convertToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setPaymentName(payment.getPaymentName());
        response.setUserId(payment.getUser() != null ? payment.getUser().getUserId() : null); // Handle null user
        response.setStudentId(payment.getStudent().getId());
        response.setStudentName(payment.getStudent().getName());
        response.setPaymentTypeId(payment.getPaymentType() != null ? Long.valueOf(payment.getPaymentType().getPaymentTypeId()) : null);
        response.setPaymentTypeName(payment.getPaymentType() != null ? payment.getPaymentType().getPaymentTypeName() : null);
        response.setAmount(payment.getAmount());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setDescription(payment.getDescription());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        response.setDeletedAt(payment.getDeletedAt());

        return response;
    }

}
