package io.vv.receipt_generator.dto;

public class ReceiptResponse {
    private Long id;
    private String receiptNo;
    private String message;
    private boolean emailSent;
    private String pdfPath;

    public ReceiptResponse() {}

    public ReceiptResponse(Long id, String receiptNo, String message, boolean emailSent, String pdfPath) {
        this.id = id;
        this.receiptNo = receiptNo;
        this.message = message;
        this.emailSent = emailSent;
        this.pdfPath = pdfPath;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }
}