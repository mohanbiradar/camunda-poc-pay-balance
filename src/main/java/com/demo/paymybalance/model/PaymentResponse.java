package com.demo.paymybalance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {
    private String transactionId;
    private String status;
    private BigDecimal amount;
    private LocalDateTime processedAt;
    private String message;

    // Constructors
    public PaymentResponse() {}

    public PaymentResponse(String transactionId, String status, BigDecimal amount,
                           LocalDateTime processedAt, String message) {
        this.transactionId = transactionId;
        this.status = status;
        this.amount = amount;
        this.processedAt = processedAt;
        this.message = message;
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
