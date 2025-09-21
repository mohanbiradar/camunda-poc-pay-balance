package com.demo.paymybalance.model;

import java.math.BigDecimal;

public class PaymentRequest {
    private String customerId;
    private String cardAccountId;
    private BigDecimal paymentAmount;
    private String paymentMethod;
    private String paymentDate;

    // Constructors
    public PaymentRequest() {}

    public PaymentRequest(String customerId, String cardAccountId, BigDecimal paymentAmount,
                          String paymentMethod, String paymentDate) {
        this.customerId = customerId;
        this.cardAccountId = cardAccountId;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
    }

    // Getters and Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getCardAccountId() { return cardAccountId; }
    public void setCardAccountId(String cardAccountId) { this.cardAccountId = cardAccountId; }
    public BigDecimal getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
}
