package io.vv.receipt_generator.service;

import io.vv.receipt_generator.dto.ReceiptRequest;
import io.vv.receipt_generator.dto.ReceiptResponse;
import io.vv.receipt_generator.entity.Receipt;
import io.vv.receipt_generator.repository.ReceiptRepository;
import io.vv.receipt_generator.util.NumberToWordsConverter;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NumberToWordsConverter numberToWordsConverter;

    public ReceiptResponse createReceipt(ReceiptRequest request) {
        try {
            // Check if receipt number already exists
            if (receiptRepository.existsByReceiptNo(request.getReceiptNo())) {
                return new ReceiptResponse(null, request.getReceiptNo(), 
                    "Receipt number already exists", false, null);
            }

            // Check if bill number already exists
            if (receiptRepository.existsByBillNo(request.getBillNo())) {
                return new ReceiptResponse(null, request.getReceiptNo(), 
                    "Bill number already exists", false, null);
            }

            // Create receipt entity
            Receipt receipt = new Receipt();
            receipt.setOwnerName(request.getOwnerName());
            receipt.setTenantName(request.getTenantName());
            receipt.setBillNo(request.getBillNo());
            receipt.setReceiptNo(request.getReceiptNo());
            receipt.setReceiptDate(request.getReceiptDate());
            receipt.setAmount(request.getAmount());
            receipt.setAmountInWords(numberToWordsConverter.convertToWords(request.getAmount()));
            receipt.setPaymentMethod(request.getPaymentMethod());
            receipt.setTransactionId(request.getTransactionId());
            receipt.setTowerNo(request.getTowerNo());
            receipt.setFlatNo(request.getFlatNo());
            receipt.setBillPeriod(request.getBillPeriod());
            receipt.setDescription(request.getDescription());
            receipt.setApartmentName(request.getApartmentName());
            receipt.setEmail(request.getEmail());

            // Save receipt
            Receipt savedReceipt = receiptRepository.save(receipt);

            // Generate PDF
            byte[] pdfData = pdfGenerationService.generateReceiptPdf(savedReceipt);

            // Send email with PDF attachment
            boolean emailSent = false;
            String message = "Receipt created successfully";
            
            if (StringUtils.hasText(request.getEmail())) {
                try {
                    emailService.sendReceiptEmailWithPlainTemplate(savedReceipt, pdfData);
                    emailSent = true;
                    message += " and email sent successfully";
                } catch (Exception e) {
                    message += " but failed to send email: " + e.getMessage();
                }
            }

            return new ReceiptResponse(savedReceipt.getId(), savedReceipt.getReceiptNo(), 
                message, emailSent, "Receipt generated successfully");

        } catch (IOException e) {
            return new ReceiptResponse(null, request.getReceiptNo(), 
                "Failed to generate PDF: " + e.getMessage(), false, null);
        } catch (Exception e) {
            return new ReceiptResponse(null, request.getReceiptNo(), 
                "Failed to create receipt: " + e.getMessage(), false, null);
        }
    }

    public Optional<Receipt> getReceiptById(Long id) {
        return receiptRepository.findById(id);
    }

    public Optional<Receipt> getReceiptByReceiptNo(String receiptNo) {
        return receiptRepository.findByReceiptNo(receiptNo);
    }

    public List<Receipt> getAllReceipts() {
        return receiptRepository.findAll();
    }

    public List<Receipt> getReceiptsByOwnerName(String ownerName) {
        return receiptRepository.findByOwnerNameContainingIgnoreCase(ownerName);
    }

    public List<Receipt> getReceiptsByDateRange(LocalDate startDate, LocalDate endDate) {
        return receiptRepository.findByReceiptDateBetween(startDate, endDate);
    }

    public List<Receipt> getReceiptsByEmail(String email) {
        return receiptRepository.findByEmailOrderByReceiptDateDesc(email);
    }

    public byte[] generatePdfById(Long id) throws IOException {
        Optional<Receipt> receiptOpt = receiptRepository.findById(id);
        if (receiptOpt.isPresent()) {
            return pdfGenerationService.generateReceiptPdf(receiptOpt.get());
        }
        throw new RuntimeException("Receipt not found with id: " + id);
    }

    public byte[] generatePdfByReceiptNo(String receiptNo) throws IOException {
        Optional<Receipt> receiptOpt = receiptRepository.findByReceiptNo(receiptNo);
        if (receiptOpt.isPresent()) {
            return pdfGenerationService.generateReceiptPdf(receiptOpt.get());
        }
        throw new RuntimeException("Receipt not found with receipt number: " + receiptNo);
    }

    public void resendEmail(Long id) throws MessagingException, IOException {
        Optional<Receipt> receiptOpt = receiptRepository.findById(id);
        if (receiptOpt.isPresent()) {
            Receipt receipt = receiptOpt.get();
            if (StringUtils.hasText(receipt.getEmail())) {
                byte[] pdfData = pdfGenerationService.generateReceiptPdf(receipt);
                emailService.sendReceiptEmailWithPlainTemplate(receipt, pdfData);
            } else {
                throw new RuntimeException("No email address found for receipt");
            }
        } else {
            throw new RuntimeException("Receipt not found with id: " + id);
        }
    }

    public ReceiptResponse updateReceipt(Long id, ReceiptRequest request) {
        try {
            Optional<Receipt> existingReceiptOpt = receiptRepository.findById(id);
            if (existingReceiptOpt.isEmpty()) {
                return new ReceiptResponse(null, request.getReceiptNo(), 
                    "Receipt not found", false, null);
            }

            Receipt existingReceipt = existingReceiptOpt.get();

            // Check if receipt number is being changed and if it already exists
            if (!existingReceipt.getReceiptNo().equals(request.getReceiptNo()) && 
                receiptRepository.existsByReceiptNo(request.getReceiptNo())) {
                return new ReceiptResponse(null, request.getReceiptNo(), 
                    "Receipt number already exists", false, null);
            }

            // Update receipt
            existingReceipt.setOwnerName(request.getOwnerName());
            existingReceipt.setTenantName(request.getTenantName());
            existingReceipt.setBillNo(request.getBillNo());
            existingReceipt.setReceiptNo(request.getReceiptNo());
            existingReceipt.setReceiptDate(request.getReceiptDate());
            existingReceipt.setAmount(request.getAmount());
            existingReceipt.setAmountInWords(numberToWordsConverter.convertToWords(request.getAmount()));
            existingReceipt.setPaymentMethod(request.getPaymentMethod());
            existingReceipt.setTransactionId(request.getTransactionId());
            existingReceipt.setTowerNo(request.getTowerNo());
            existingReceipt.setFlatNo(request.getFlatNo());
            existingReceipt.setBillPeriod(request.getBillPeriod());
            existingReceipt.setDescription(request.getDescription());
            existingReceipt.setApartmentName(request.getApartmentName());
            existingReceipt.setEmail(request.getEmail());

            Receipt updatedReceipt = receiptRepository.save(existingReceipt);

            return new ReceiptResponse(updatedReceipt.getId(), updatedReceipt.getReceiptNo(), 
                "Receipt updated successfully", false, null);

        } catch (Exception e) {
            return new ReceiptResponse(null, request.getReceiptNo(), 
                "Failed to update receipt: " + e.getMessage(), false, null);
        }
    }

    public boolean deleteReceipt(Long id) {
        if (receiptRepository.existsById(id)) {
            receiptRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public long getTotalReceiptsCount() {
        return receiptRepository.count();
    }

    public long getTotalReceiptsCountByDate(LocalDate date) {
        return receiptRepository.countByReceiptDate(date);
    }

    public Double getTotalAmountByDateRange(LocalDate startDate, LocalDate endDate) {
        Double total = receiptRepository.getTotalAmountByDateRange(startDate, endDate);
        return total != null ? total : 0.0;
    }
}