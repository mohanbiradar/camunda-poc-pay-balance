package com.demo.paymybalance.model;

public class FraudCheckResult {
    private String result;
    private String reason;
    private double riskScore;

    // Constructors
    public FraudCheckResult() {}

    public FraudCheckResult(String result, String reason, double riskScore) {
        this.result = result;
        this.reason = reason;
        this.riskScore = riskScore;
    }

    // Getters and Setters
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
}
