package com.beta.schoolpayment.controller;

import com.beta.schoolpayment.dto.request.PaymentFilterCriteria;
import com.beta.schoolpayment.dto.request.PaymentRequest;
import com.beta.schoolpayment.dto.response.PaymentResponse;
import com.beta.schoolpayment.service.PaymentExportService;
import com.beta.schoolpayment.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentExportService paymentExportService;

    // 🔹 Endpoint untuk membuat pembayaran baru
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody @Valid PaymentRequest paymentRequest) {
        try {
            PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);
            return ResponseEntity.ok(paymentResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal Server Error"));
        }
    }

    // 🔹 Endpoint untuk mendapatkan semua pembayaran
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getPayments(
            @RequestParam(required = false) String paymentName,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate schoolYearStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate schoolYearEndDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        try {
            // 🔹 Pastikan sortDirection hanya "asc" atau "desc"
            sortDirection = sortDirection.equalsIgnoreCase("asc") ? "asc" : "desc";

            PaymentFilterCriteria criteria = new PaymentFilterCriteria();
            criteria.setPaymentName(paymentName);
            criteria.setStudentName(studentName);
            criteria.setUserName(userName);
            criteria.setSchoolYearStartDate(schoolYearStartDate);
            criteria.setSchoolYearEndDate(schoolYearEndDate);
            criteria.setPaymentStatus(paymentStatus);

            Page<PaymentResponse> response = paymentService.getAllPayments(criteria, page, size, sortBy, sortDirection);

            if (response.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message", "No payments found"));
            }

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan pada server: " + e.getMessage()));
        }
    }

    // 🔹 Endpoint untuk mendapatkan pembayaran berdasarkan ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable UUID id) {
        try {
            PaymentResponse paymentResponse = paymentService.getPaymentById(id);
            return ResponseEntity.ok(paymentResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Payment not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan pada server: " + e.getMessage()));
        }
    }

    // 🔹 Endpoint untuk update status pembayaran
    @PutMapping("/status/{id}")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable UUID id, @RequestBody Map<String, String> requestBody) {
        try {
            // ✅ Validasi status tidak boleh null atau kosong
            if (!requestBody.containsKey("status") || requestBody.get("status").trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status tidak boleh kosong"));
            }

            String status = requestBody.get("status");

            // Panggil service untuk update status pembayaran
            PaymentResponse updatedPayment = paymentService.updatePaymentStatus(id, status);

            return ResponseEntity.ok(updatedPayment);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Payment not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan pada server: " + e.getMessage()));
        }
    }

    // 🔹 Endpoint untuk export payments ke Excel
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportPaymentsToExcel() {
        try {
            List<PaymentResponse> payments = paymentService.getAllPayments();
            byte[] excelData = paymentExportService.exportPaymentsToExcel(payments);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payments.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
