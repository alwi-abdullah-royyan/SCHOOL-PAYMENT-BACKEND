package com.beta.schoolpayment.service;

import com.beta.schoolpayment.dto.response.PaymentResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PaymentExportService {

    public byte[] exportPaymentsToExcel(List<PaymentResponse> payments) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Payments");

            // 🔹 Buat header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Payment ID", "Payment Name", "User ID", "Student ID","Student Name", "Amount", "Status", "Created At"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(getHeaderStyle(workbook));
            }

            // 🔹 Isi data
            int rowNum = 1;
            for (PaymentResponse payment : payments) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(payment.getPaymentId() != null ? payment.getPaymentId().toString() : "N/A");
                row.createCell(1).setCellValue(payment.getPaymentName() != null ? payment.getPaymentName() : "N/A");
                row.createCell(2).setCellValue(payment.getUserId() != null ? payment.getUserId().toString() : "N/A");
                row.createCell(3).setCellValue(payment.getStudentName() != null ? payment.getStudentName() : "N/A");
                row.createCell(4).setCellValue(payment.getStudentId() != null ? payment.getStudentId().toString() : "N/A");
                row.createCell(5).setCellValue(payment.getAmount() != null ? payment.getAmount().doubleValue() : 0.0);
                row.createCell(6).setCellValue(payment.getPaymentStatus() != null ? payment.getPaymentStatus() : "N/A");
                row.createCell(7).setCellValue(payment.getCreatedAt() != null ? payment.getCreatedAt().toString() : "N/A");
            }


            // 🔹 Auto-size kolom
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 🔹 Simpan workbook ke dalam ByteArrayOutputStream
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        }
    }

    private CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }
}
