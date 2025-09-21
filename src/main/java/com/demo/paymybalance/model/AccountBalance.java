package com.demo.paymybalance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountBalance {
    private String accountId;
    private BigDecimal currentBalance;
    private BigDecimal availableCredit;
    private BigDecimal creditLimit;
    private LocalDateTime lastPaymentDate;
    private BigDecimal minimumPayment;

    // Constructors
    public AccountBalance() {}

    public AccountBalance(String accountId, BigDecimal currentBalance, BigDecimal availableCredit,
                          BigDecimal creditLimit, LocalDateTime lastPaymentDate, BigDecimal minimumPayment) {
        this.accountId = accountId;
        this.currentBalance = currentBalance;
        this.availableCredit = availableCredit;
        this.creditLimit = creditLimit;
        this.lastPaymentDate = lastPaymentDate;
        this.minimumPayment = minimumPayment;
    }

    // Getters and Setters
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    public BigDecimal getAvailableCredit() { return availableCredit; }
    public void setAvailableCredit(BigDecimal availableCredit) { this.availableCredit = availableCredit; }
    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }
    public LocalDateTime getLastPaymentDate() { return lastPaymentDate; }
    public void setLastPaymentDate(LocalDateTime lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }
    public BigDecimal getMinimumPayment() { return minimumPayment; }
    public void setMinimumPayment(BigDecimal minimumPayment) { this.minimumPayment = minimumPayment; }
}
