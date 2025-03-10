package com.beta.schoolpayment.controller;


import com.beta.schoolpayment.dto.request.PaymentRequest;
import com.beta.schoolpayment.dto.response.ApiResponse;
import com.beta.schoolpayment.dto.response.PaymentResponse;
import com.beta.schoolpayment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

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

    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        try {
            List<PaymentResponse> payments = paymentService.getAllPayments();

            if (payments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message", "No payments found"));
            }
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Terjadi kesalahan pada server: " + e.getMessage()));
        }
    }


//    // 🔹 Endpoint untuk mendapatkan pembayaran berdasarkan ID
//    @GetMapping("/{paymentId}")
//    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID paymentId) {
//        PaymentResponse paymentResponse = paymentService.getPaymentById(paymentId);
//        return ResponseEntity.ok(paymentResponse);
//    }

//    // 🔹 Endpoint untuk menghapus pembayaran berdasarkan ID
//    @DeleteMapping("/{paymentId}")
//    public ResponseEntity<String> deletePayment(@PathVariable UUID paymentId) {
//        paymentService.deletePayment(paymentId);
//        return ResponseEntity.ok("Payment deleted successfully");
//    }

}

