package com.beta.schoolpayment.controller;

import com.beta.schoolpayment.dto.request.PaymentTypeRequest;
import com.beta.schoolpayment.dto.response.PaymentTypeResponse;
import com.beta.schoolpayment.service.PaymentTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment-types")
public class PaymentTypeController {
    private final PaymentTypeService paymentTypeService;

    public PaymentTypeController(PaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    // ✅ Create Payment Type
    @PostMapping
    public ResponseEntity<PaymentTypeResponse> createPaymentType(@Valid @RequestBody PaymentTypeRequest request) {
        return ResponseEntity.ok(paymentTypeService.createPaymentType(request));
    }

    // ✅ Get All Payment Types
    @GetMapping
    public ResponseEntity<List<PaymentTypeResponse>> getAllPaymentTypes() {
        return ResponseEntity.ok(paymentTypeService.getAllPaymentTypes());
    }

    // ✅ Search Payment Type by Query
    @GetMapping("/search")
    public ResponseEntity<List<PaymentTypeResponse>> searchPaymentType(@RequestParam String query) {
        return ResponseEntity.ok(paymentTypeService.searchPaymentType(query));
    }

    // ✅ Update Payment Type
    @PutMapping("/{id}")
    public ResponseEntity<Optional<PaymentTypeResponse>> updatePaymentType(
            @PathVariable Long id,
            @Valid @RequestBody PaymentTypeRequest request) {
        return ResponseEntity.ok(paymentTypeService.updatePaymentType(id, request));
    }

    // ✅ Delete Payment Type (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePaymentType(@PathVariable Long id) {
        boolean isDeleted = paymentTypeService.deletePaymentType(id);
        return isDeleted ? ResponseEntity.ok("Payment Type deleted successfully")
                : ResponseEntity.badRequest().body("Payment Type not found");
    }

    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<String> hardDeletePaymentType(@PathVariable Long id) {
        boolean deleted = paymentTypeService.hardDeletePaymentType(id);
        if (deleted) {
            return ResponseEntity.ok("Payment Type successfully deleted permanently.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment Type not found or already deleted.");
        }
    }

}
