//package com.demo.paymybalance.service;
//
//import com.demo.paymybalance.model.FraudCheckResult;
//import org.springframework.stereotype.Service;
//import java.math.BigDecimal;
//import java.util.Random;
//
//@Service
//public class FraudService {
//
//    private final Random random = new Random();
//
//    public FraudCheckResult performFraudCheck(String customerId, BigDecimal paymentAmount, String paymentMethod) {
//        // Simulate fraud check logic
//        double riskScore = calculateRiskScore(customerId, paymentAmount, paymentMethod);
//
//        String result;
//        String reason;
//
//        if (riskScore < 0.3) {
//            result = "APPROVED";
//            reason = "Low risk transaction";
//        } else if (riskScore < 0.7) {
//            result = "APPROVED";
//            reason = "Medium risk but approved";
//        } else {
//            result = "REJECTED";
//            reason = "High risk transaction detected";
//        }
//
//        return new FraudCheckResult(result, reason, riskScore);
//    }
//
//    private double calculateRiskScore(String customerId, BigDecimal paymentAmount, String paymentMethod) {
//        double baseRisk = 0.1;
//
//        // Higher amounts increase risk slightly
//        if (paymentAmount.compareTo(new BigDecimal("5000")) > 0) {
//            baseRisk += 0.2;
//        } else if (paymentAmount.compareTo(new BigDecimal("1000")) > 0) {
//            baseRisk += 0.1;
//        }
//
//        // Different payment methods have different risk levels
//        switch (paymentMethod.toUpperCase()) {
//            case "WIRE":
//                baseRisk += 0.05;
//                break;
//            case "ACH":
//                baseRisk += 0.02;
//                break;
//            case "BANK_TRANSFER":
//                baseRisk += 0.03;
//                break;
//        }
//
//        // Add some randomness to simulate real-world variability
//        baseRisk += (random.nextDouble() * 0.3);
//
//        return Math.min(baseRisk, 1.0);
//    }
//}
//
// src/main/java/com/demo/paymybalance/service/EnhancedFraudService.java
package com.demo.paymybalance.service;

import com.demo.paymybalance.model.FraudCheckResult;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Random;

@Service
public class FraudService {

    private final Random random = new Random();

    // Configurable thresholds for demo purposes
    private double highRiskThreshold = 0.7;  // Above this = REJECTED
    private double mediumRiskThreshold = 0.4; // Between medium and high = risky but may approve

    public FraudCheckResult performFraudCheck(String customerId, BigDecimal paymentAmount, String paymentMethod) {
        double riskScore = calculateRiskScore(customerId, paymentAmount, paymentMethod);

        String result;
        String reason;

        // Enhanced decision logic for better demo scenarios
        if (riskScore >= highRiskThreshold) {
            result = "REJECTED";
            reason = "High risk score: " + String.format("%.2f", riskScore) + " - Transaction declined";
        } else if (riskScore >= mediumRiskThreshold) {
            // Medium risk - sometimes approve, sometimes reject for demo variety
            boolean shouldApprove = random.nextBoolean(); // 50/50 chance
            if (shouldApprove) {
                result = "APPROVED";
                reason = "Medium risk but approved after additional checks";
            } else {
                result = "REJECTED";
                reason = "Medium risk score exceeded tolerance: " + String.format("%.2f", riskScore);
            }
        } else {
            result = "APPROVED";
            reason = "Low risk transaction";
        }

        return new FraudCheckResult(result, reason, riskScore);
    }

    private double calculateRiskScore(String customerId, BigDecimal paymentAmount, String paymentMethod) {
        double baseRisk = 0.1;

        // Customer-specific risk factors
        if ("CUST003".equals(customerId)) {
            baseRisk += 0.3; // Suspended customer has higher base risk
        }

        // Amount-based risk (more aggressive thresholds for demo)
        if (paymentAmount.compareTo(new BigDecimal("10000")) > 0) {
            baseRisk += 0.5; // Very high amounts
        } else if (paymentAmount.compareTo(new BigDecimal("5000")) > 0) {
            baseRisk += 0.3; // High amounts
        } else if (paymentAmount.compareTo(new BigDecimal("2000")) > 0) {
            baseRisk += 0.2; // Medium amounts
        } else if (paymentAmount.compareTo(new BigDecimal("1000")) > 0) {
            baseRisk += 0.1; // Low-medium amounts
        }

        // Payment method risk
        switch (paymentMethod.toUpperCase()) {
            case "WIRE":
                baseRisk += 0.15; // Wire transfers are riskier
                break;
            case "BANK_TRANSFER":
                baseRisk += 0.1;
                break;
            case "ACH":
                baseRisk += 0.05; // ACH is lowest risk
                break;
        }

        // Add some randomness for demo variety
        baseRisk += (random.nextDouble() * 0.3);

        return Math.min(baseRisk, 1.0);
    }

    // Utility methods for demo control
    public void setHighRiskThreshold(double threshold) {
        this.highRiskThreshold = threshold;
    }

    public void setMediumRiskThreshold(double threshold) {
        this.mediumRiskThreshold = threshold;
    }

    public double getHighRiskThreshold() {
        return highRiskThreshold;
    }

    public double getMediumRiskThreshold() {
        return mediumRiskThreshold;
    }
}