package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.request.PaymentTypeRequest;
import com.beta.schoolpayment.dto.response.PaymentTypeResponse;
import com.beta.schoolpayment.model.PaymentType;
import com.beta.schoolpayment.repository.PaymentTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentTypeService {
    private final PaymentTypeRepository paymentTypeRepository;

    public PaymentTypeService(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    // ✅ Create Payment Type
    public PaymentTypeResponse createPaymentType(PaymentTypeRequest request) {
        PaymentType paymentType = new PaymentType();
        paymentType.setPaymentTypeName(request.getPaymentTypeName());

        PaymentType savedPaymentType = paymentTypeRepository.save(paymentType);
        return convertToResponse(savedPaymentType);
    }

    // ✅ Get All Payment Types
    public List<PaymentTypeResponse> getAllPaymentTypes() {
        return paymentTypeRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Search Payment Type by Query
    public List<PaymentTypeResponse> searchPaymentType(String query) {
        return paymentTypeRepository.searchByName(query)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Update Payment Type
    public Optional<PaymentTypeResponse> updatePaymentType(Long id, PaymentTypeRequest request) {
        return paymentTypeRepository.findById(id).map(paymentType -> {
            paymentType.setPaymentTypeName(request.getPaymentTypeName());
            return convertToResponse(paymentTypeRepository.save(paymentType));
        });
    }

    // ✅ Delete Payment Type (Soft Delete)
    public boolean deletePaymentType(Long id) {
        return paymentTypeRepository.findById(id).map(paymentType -> {
            paymentTypeRepository.delete(paymentType);
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean hardDeletePaymentType(Long id) {
        if (paymentTypeRepository.existsById(id)) {
            paymentTypeRepository.hardDeleteById(id);
            return true;
        }
        return false;
    }



    // ✅ Convert Entity to Response DTO
    private PaymentTypeResponse convertToResponse(PaymentType paymentType) {
        PaymentTypeResponse response = new PaymentTypeResponse();
        response.setPaymentTypeId(paymentType.getPaymentTypeId());
        response.setPaymentTypeName(paymentType.getPaymentTypeName());
        response.setCreatedAt(paymentType.getCreatedAt());
        response.setUpdatedAt(paymentType.getUpdatedAt());
        response.setDeletedAt(paymentType.getDeletedAt());
        return response;
    }
}
