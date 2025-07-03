package io.vv.receipt_generator.service;

import io.vv.receipt_generator.entity.Receipt;
import io.vv.receipt_generator.util.NumberToWordsConverter;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGenerationService {

    private final NumberToWordsConverter numberToWordsConverter;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public PdfGenerationService(NumberToWordsConverter numberToWordsConverter) {
        this.numberToWordsConverter = numberToWordsConverter;
    }

    public byte[] generateReceiptPdf(Receipt receipt) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Set fonts
        PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont regularFont = PdfFontFactory.createFont("Helvetica");

        // Add header with green background
        addHeader(document, boldFont);

        // Add receipt title
        addReceiptTitle(document, boldFont);

        // Add receipt details
        addReceiptDetails(document, receipt, boldFont, regularFont);

        // Add main content
        addMainContent(document, receipt, boldFont, regularFont);

        // Add footer
        addFooter(document, receipt, boldFont, regularFont);

        document.close();
        return baos.toByteArray();
    }

    private void addHeader(Document document, PdfFont boldFont) {
        // Green header background
        Color greenColor = new DeviceRgb(46, 204, 113);
        
        Paragraph header = new Paragraph()
                .setBackgroundColor(greenColor)
                .setFontColor(DeviceRgb.WHITE)
                .setFont(boldFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(10)
                .add("PAYMENT")
                .add(new Text("\nNavi Mumbai"))
                .add(new Text("\nPAYMENT.... NA"));
        
        document.add(header);
    }

    private void addReceiptTitle(Document document, PdfFont boldFont) {
        Paragraph title = new Paragraph("Receipt")
                .setFont(boldFont)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginTop(20)
                .setMarginBottom(20);
        
        document.add(title);
    }

    private void addReceiptDetails(Document document, Receipt receipt, PdfFont boldFont, PdfFont regularFont) {
        // Create a table for receipt details
        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{3, 5, 3, 5}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        // Add details
        addDetailRow(detailsTable, "Owner Name", receipt.getOwnerName(), 
                    "Receipt No", receipt.getReceiptNo(), regularFont);
        
        addDetailRow(detailsTable, "Tenant Name", 
                    StringUtils.hasText(receipt.getTenantName()) ? receipt.getTenantName() : "", 
                    "Receipt Date", receipt.getReceiptDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")), 
                    regularFont);
        
        addDetailRow(detailsTable, "Bill No", receipt.getBillNo(), "", "", regularFont);

        document.add(detailsTable);
    }

    private void addDetailRow(Table table, String label1, String value1, String label2, String value2, PdfFont font) {
        table.addCell(new Cell().add(new Paragraph(label1 + " :").setFont(font).setFontSize(10))
                .setBorder(null).setPadding(2));
        table.addCell(new Cell().add(new Paragraph(value1).setFont(font).setFontSize(10))
                .setBorder(null).setPadding(2));
        table.addCell(new Cell().add(new Paragraph(label2 + " :").setFont(font).setFontSize(10))
                .setBorder(null).setPadding(2));
        table.addCell(new Cell().add(new Paragraph(value2).setFont(font).setFontSize(10))
                .setBorder(null).setPadding(2));
    }

    private void addMainContent(Document document, Receipt receipt, PdfFont boldFont, PdfFont regularFont) {
        // Main content paragraph
        String mainContent = String.format(
                "Received with thanks from %s against Tower No. %s Flat No. %s the sum of\n\n" +
                "Rupees %s/- (%s) Vide %s\n\n" +
                "Transaction ID %s drawn on Payment Successful. towards\n\n" +
                "%s",
                receipt.getOwnerName(),
                StringUtils.hasText(receipt.getTowerNo()) ? receipt.getTowerNo() : "C-Wing",
                StringUtils.hasText(receipt.getFlatNo()) ? receipt.getFlatNo() : "504",
                decimalFormat.format(receipt.getAmount()),
                numberToWordsConverter.convertToWords(receipt.getAmount()),
                StringUtils.hasText(receipt.getPaymentMethod()) ? receipt.getPaymentMethod() : "CHEQUE",
                StringUtils.hasText(receipt.getTransactionId()) ? receipt.getTransactionId() : "8a96bc8397ca1e0...",
                StringUtils.hasText(receipt.getDescription()) ? receipt.getDescription() : 
                    String.format("Maintenance Charges Bill period of %s", 
                        StringUtils.hasText(receipt.getBillPeriod()) ? receipt.getBillPeriod() : "July 2025")
        );

        Paragraph content = new Paragraph(mainContent)
                .setFont(regularFont)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginTop(20)
                .setMarginBottom(30);

        document.add(content);
    }

    private void addFooter(Document document, Receipt receipt, PdfFont boldFont, PdfFont regularFont) {
        // Apartment name
        Paragraph apartmentName = new Paragraph(
                StringUtils.hasText(receipt.getApartmentName()) ? 
                    "FOR " + receipt.getApartmentName() : "FOR IRA GALAXY APARTMENT")
                .setFont(regularFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(40)
                .setMarginBottom(40);

        document.add(apartmentName);

        // Signature
        Paragraph signature = new Paragraph("Authorised signatory")
                .setFont(regularFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20);

        document.add(signature);
    }
}