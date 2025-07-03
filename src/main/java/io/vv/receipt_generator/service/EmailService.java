package io.vv.receipt_generator.service;

import io.vv.receipt_generator.entity.Receipt;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.name}")
    private String fromName;

    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public void sendReceiptEmail(Receipt receipt, byte[] pdfData) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Set email details
        helper.setFrom(fromEmail, fromName);
        helper.setTo(receipt.getEmail());
        helper.setSubject("Receipt - " + receipt.getReceiptNo() + " for " + receipt.getOwnerName());

        // Create email content using Thymeleaf template
        Context context = new Context();
        context.setVariable("receipt", receipt);
        context.setVariable("formattedAmount", decimalFormat.format(receipt.getAmount()));
        context.setVariable("formattedDate", receipt.getReceiptDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));

        String htmlContent = templateEngine.process("email/receipt-email", context);
        helper.setText(htmlContent, true);

        // Attach PDF
        helper.addAttachment("Receipt-" + receipt.getReceiptNo() + ".pdf", 
                           new ByteArrayResource(pdfData), "application/pdf");

        mailSender.send(message);
    }

    public void sendReceiptEmailWithPlainTemplate(Receipt receipt, byte[] pdfData) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Set email details
        helper.setFrom(fromEmail, fromName);
        helper.setTo(receipt.getEmail());
        helper.setSubject("Receipt - " + receipt.getReceiptNo() + " for " + receipt.getOwnerName());

        // Create HTML email content
        String htmlContent = createEmailTemplate(receipt);
        helper.setText(htmlContent, true);

        // Attach PDF
        helper.addAttachment("Receipt-" + receipt.getReceiptNo() + ".pdf", 
                           new ByteArrayResource(pdfData), "application/pdf");

        mailSender.send(message);
    }

    private String createEmailTemplate(Receipt receipt) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Receipt Email</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .header {
                        background: linear-gradient(135deg, #2ecc71, #27ae60);
                        color: white;
                        padding: 30px;
                        text-align: center;
                        border-radius: 10px 10px 0 0;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 28px;
                        font-weight: bold;
                    }
                    .content {
                        background: #f8f9fa;
                        padding: 30px;
                        border-radius: 0 0 10px 10px;
                    }
                    .receipt-details {
                        background: white;
                        padding: 20px;
                        border-radius: 8px;
                        margin: 20px 0;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                    .detail-row {
                        display: flex;
                        justify-content: space-between;
                        margin: 10px 0;
                        padding: 8px 0;
                        border-bottom: 1px solid #eee;
                    }
                    .detail-label {
                        font-weight: bold;
                        color: #555;
                    }
                    .detail-value {
                        color: #333;
                    }
                    .amount-highlight {
                        background: #e8f5e8;
                        padding: 15px;
                        border-radius: 5px;
                        text-align: center;
                        margin: 20px 0;
                        border-left: 4px solid #2ecc71;
                    }
                    .amount-highlight .amount {
                        font-size: 24px;
                        font-weight: bold;
                        color: #2ecc71;
                    }
                    .footer {
                        margin-top: 30px;
                        text-align: center;
                        color: #666;
                        font-size: 12px;
                    }
                    .button {
                        display: inline-block;
                        padding: 12px 24px;
                        background: #2ecc71;
                        color: white;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 10px 0;
                    }
                    .apartment-name {
                        color: #2ecc71;
                        font-weight: bold;
                        font-size: 18px;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Payment Receipt</h1>
                    <p>Thank you for your payment</p>
                </div>
                
                <div class="content">
                    <p>Dear %s,</p>
                    
                    <p>We have received your payment successfully. Please find the receipt details below:</p>
                    
                    <div class="receipt-details">
                        <div class="detail-row">
                            <span class="detail-label">Receipt Number:</span>
                            <span class="detail-value">%s</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Receipt Date:</span>
                            <span class="detail-value">%s</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Bill Number:</span>
                            <span class="detail-value">%s</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Tower/Flat:</span>
                            <span class="detail-value">%s / %s</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Payment Method:</span>
                            <span class="detail-value">%s</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Description:</span>
                            <span class="detail-value">%s</span>
                        </div>
                    </div>
                    
                    <div class="amount-highlight">
                        <div class="amount">₹ %s</div>
                        <div>Amount Paid</div>
                    </div>
                    
                    <p>The detailed receipt is attached to this email as a PDF document.</p>
                    
                    <p>If you have any questions regarding this payment, please don't hesitate to contact us.</p>
                    
                    <p>Best regards,<br>
                    <span class="apartment-name">%s</span></p>
                </div>
                
                <div class="footer">
                    <p>This is an automated email. Please do not reply to this email.</p>
                    <p>For any queries, please contact the apartment management.</p>
                </div>
            </body>
            </html>
            """.formatted(
                receipt.getOwnerName(),
                receipt.getReceiptNo(),
                receipt.getReceiptDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")),
                receipt.getBillNo(),
                receipt.getTowerNo() != null ? receipt.getTowerNo() : "C-Wing",
                receipt.getFlatNo() != null ? receipt.getFlatNo() : "504",
                receipt.getPaymentMethod() != null ? receipt.getPaymentMethod() : "CHEQUE",
                receipt.getDescription() != null ? receipt.getDescription() : "Maintenance Charges",
                decimalFormat.format(receipt.getAmount()),
                receipt.getApartmentName() != null ? receipt.getApartmentName() : "IRA GALAXY APARTMENT"
            );
    }
}