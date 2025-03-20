package com.beta.schoolpayment.controller;

import com.beta.schoolpayment.dto.request.PaymentFilterCriteria;
import com.beta.schoolpayment.dto.request.PaymentRequest;
import com.beta.schoolpayment.dto.response.PaymentResponse;
import com.beta.schoolpayment.security.CustomUserDetails;
import com.beta.schoolpayment.service.PaymentExportService;
import com.beta.schoolpayment.service.PaymentReceiptService;
import com.beta.schoolpayment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentExportService paymentExportService;

    @Mock
    private PaymentReceiptService paymentReceiptService;

    @Mock
    private CustomUserDetails userDetails;

    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;
    private UUID paymentId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        userId = UUID.randomUUID();

        paymentRequest = new PaymentRequest();
        paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentId(paymentId);
        paymentResponse.setPaymentStatus("COMPLETED");

        when(userDetails.getUserId()).thenReturn(userId);
    }

    @Test
    void createPayment_Success() {
        when(paymentService.createPayment(any(PaymentRequest.class), any(CustomUserDetails.class)))
                .thenReturn(paymentResponse);

        ResponseEntity<?> response = paymentController.createPayment(paymentRequest, userDetails);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(paymentService).createPayment(any(PaymentRequest.class), any(CustomUserDetails.class));
    }

    @Test
    void getAllPayments_Success() {
        PaymentFilterCriteria criteria = new PaymentFilterCriteria();
        List<PaymentResponse> payments = List.of(paymentResponse);
        Page<PaymentResponse> page = new PageImpl<>(payments);

        when(paymentService.getAllPayments(any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        ResponseEntity<?> response = paymentController.getAllPayments(null, null, null, null, null, null, 0, 10, "createdAt", "desc");

        assertEquals(OK, response.getStatusCode());
        verify(paymentService).getAllPayments(any(), anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    void getPaymentById_Success() {
        when(paymentService.getPaymentById(paymentId)).thenReturn(paymentResponse);

        ResponseEntity<?> response = paymentController.getPaymentById(paymentId);

        assertEquals(OK, response.getStatusCode());
        verify(paymentService).getPaymentById(paymentId);
    }

    @Test
    void updatePaymentStatus_Success() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", "PAID");

        when(paymentService.updatePaymentStatus(paymentId, "PAID")).thenReturn(paymentResponse);

        ResponseEntity<?> response = paymentController.updatePaymentStatus(paymentId, requestBody);

        assertEquals(OK, response.getStatusCode());
        verify(paymentService).updatePaymentStatus(paymentId, "PAID");
    }

    @Test
    void deletePayment_Success() {
        doNothing().when(paymentService).deletePayment(paymentId);

        ResponseEntity<?> response = paymentController.deletePayment(paymentId);

        assertEquals(OK, response.getStatusCode());
        verify(paymentService).deletePayment(paymentId);
    }

    @Test
    void downloadPaymentReceipt_Success() throws Exception {
        when(paymentService.getPaymentById(paymentId)).thenReturn(paymentResponse);
        byte[] pdfBytes = {1, 2, 3, 4, 5};  // Simulasi file PDF
        when(paymentReceiptService.generatePaymentReceipt(any())).thenReturn(pdfBytes);

        ResponseEntity<?> response = paymentController.downloadPaymentReceipt(paymentId);

        assertEquals(OK, response.getStatusCode());
        assertArrayEquals(pdfBytes, (byte[]) response.getBody());
        verify(paymentReceiptService).generatePaymentReceipt(any());
    }
}
