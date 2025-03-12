package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.response.PaymentResponse;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PaymentReceiptService {

    public byte[] generatePaymentReceipt(PaymentResponse payment) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("TANDA TERIMA PEMBAYARAN"));
            document.add(new Paragraph("ID Pembayaran: " + payment.getPaymentId()));
            document.add(new Paragraph("Nama Pembayaran: " + payment.getPaymentName()));
            document.add(new Paragraph("Nama Siswa: " + payment.getStudentId()));
            document.add(new Paragraph("Jumlah: Rp " + payment.getAmount()));
            document.add(new Paragraph("Status: " + payment.getPaymentStatus()));
            document.add(new Paragraph("Tanggal Pembayaran: " + payment.getUpdatedAt()));

            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat PDF tanda terima pembayaran", e);
        }
    }
}
