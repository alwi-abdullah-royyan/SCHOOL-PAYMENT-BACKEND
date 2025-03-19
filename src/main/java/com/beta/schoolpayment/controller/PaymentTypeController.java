package com.beta.schoolpayment.controller;

import com.beta.schoolpayment.dto.request.PaymentTypeRequest;
import com.beta.schoolpayment.dto.response.PaymentTypeResponse;
import com.beta.schoolpayment.service.PaymentTypeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<?> createPaymentType(@Valid @RequestBody PaymentTypeRequest request) {
        try {
            PaymentTypeResponse response = paymentTypeService.createPaymentType(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating payment type: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPaymentTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // Validasi input pagination
            if (page < 0 || size <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Page must be >= 0 and size must be > 0");
            }

            Page<PaymentTypeResponse> response = paymentTypeService.getAllPaymentTypes(page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    // ✅ Search Payment Type by Query
    @GetMapping("/search")
    public ResponseEntity<?> searchPaymentType(@RequestParam String query) {
        try {
            List<PaymentTypeResponse> results = paymentTypeService.searchPaymentType(query);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error searching payment types: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePaymentType(@PathVariable Long id, @Valid @RequestBody PaymentTypeRequest request) {
        try {
            Optional<PaymentTypeResponse> updatedPaymentType = paymentTypeService.updatePaymentType(id, request);

            if (updatedPaymentType.isPresent()) {
                return ResponseEntity.ok(updatedPaymentType.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Payment Type not found", "id", String.valueOf(id)));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating payment type", "message", e.getMessage()));
        }
    }

    // ✅ Delete Payment Type (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaymentType(@PathVariable Long id) {
        try {
            boolean isDeleted = paymentTypeService.deletePaymentType(id);
            return isDeleted ? ResponseEntity.ok("Payment Type deleted successfully")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment Type not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting payment type: " + e.getMessage());
        }
    }

    // ✅ Hard Delete Payment Type
    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<?> hardDeletePaymentType(@PathVariable Long id) {
        try {
            boolean deleted = paymentTypeService.hardDeletePaymentType(id);
            return deleted ? ResponseEntity.ok("Payment Type successfully deleted permanently.")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment Type not found or already deleted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error permanently deleting payment type: " + e.getMessage());
        }
    }
}
