package io.vv.receipt_generator.rest;

import io.vv.receipt_generator.dto.ReceiptRequest;
import io.vv.receipt_generator.dto.ReceiptResponse;
import io.vv.receipt_generator.entity.Receipt;
import io.vv.receipt_generator.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/receipts")
@CrossOrigin(origins = "*")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @PostMapping
    public ResponseEntity<ReceiptResponse> createReceipt(@Valid @RequestBody ReceiptRequest request) {
        ReceiptResponse response = receiptService.createReceipt(request);
        
        if (response.getId() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Receipt> getReceiptById(@PathVariable Long id) {
        Optional<Receipt> receipt = receiptService.getReceiptById(id);
        return receipt.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/receipt-no/{receiptNo}")
    public ResponseEntity<Receipt> getReceiptByReceiptNo(@PathVariable String receiptNo) {
        Optional<Receipt> receipt = receiptService.getReceiptByReceiptNo(receiptNo);
        return receipt.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        List<Receipt> receipts = receiptService.getAllReceipts();
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/owner/{ownerName}")
    public ResponseEntity<List<Receipt>> getReceiptsByOwnerName(@PathVariable String ownerName) {
        List<Receipt> receipts = receiptService.getReceiptsByOwnerName(ownerName);
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<Receipt>> getReceiptsByEmail(@PathVariable String email) {
        List<Receipt> receipts = receiptService.getReceiptsByEmail(email);
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Receipt>> getReceiptsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Receipt> receipts = receiptService.getReceiptsByDateRange(startDate, endDate);
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdfById(@PathVariable Long id) {
        try {
            byte[] pdfData = receiptService.generatePdfById(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "receipt-" + id + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/receipt-no/{receiptNo}/pdf")
    public ResponseEntity<byte[]> generatePdfByReceiptNo(@PathVariable String receiptNo) {
        try {
            byte[] pdfData = receiptService.generatePdfByReceiptNo(receiptNo);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "receipt-" + receiptNo + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/resend-email")
    public ResponseEntity<String> resendEmail(@PathVariable Long id) {
        try {
            receiptService.resendEmail(id);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceiptResponse> updateReceipt(@PathVariable Long id, 
                                                        @Valid @RequestBody ReceiptRequest request) {
        ReceiptResponse response = receiptService.updateReceipt(id, request);
        
        if (response.getId() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReceipt(@PathVariable Long id) {
        boolean deleted = receiptService.deleteReceipt(id);
        
        if (deleted) {
            return ResponseEntity.ok("Receipt deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats/total-count")
    public ResponseEntity<Long> getTotalReceiptsCount() {
        long count = receiptService.getTotalReceiptsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/count-by-date")
    public ResponseEntity<Long> getTotalReceiptsCountByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        long count = receiptService.getTotalReceiptsCountByDate(date);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/total-amount")
    public ResponseEntity<Double> getTotalAmountByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Double total = receiptService.getTotalAmountByDateRange(startDate, endDate);
        return ResponseEntity.ok(total);
    }
}