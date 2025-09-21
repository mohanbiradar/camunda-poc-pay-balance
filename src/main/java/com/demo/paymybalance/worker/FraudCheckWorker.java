//package com.demo.paymybalance.worker;
//
//import com.demo.paymybalance.model.FraudCheckResult;
//import com.demo.paymybalance.service.FraudService;
//import io.camunda.zeebe.client.api.response.ActivatedJob;
//import io.camunda.zeebe.client.api.worker.JobClient;
//import io.camunda.zeebe.spring.client.annotation.JobWorker;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class FraudCheckWorker {
//
//    @Autowired
//    private FraudService fraudService;
//
//    @JobWorker(type = "fraud-check")
//    public void performFraudCheck(final JobClient client, final ActivatedJob job) {
//        System.out.println("=== Performing Fraud Check ===");
//
//        Map<String, Object> variables = job.getVariablesAsMap();
//        String customerId = (String) variables.get("customerId");
//        BigDecimal paymentAmount = new BigDecimal(variables.get("paymentAmount").toString());
//        String paymentMethod = (String) variables.get("paymentMethod");
//
//        System.out.println("Customer ID: " + customerId);
//        System.out.println("Payment Amount: $" + paymentAmount);
//        System.out.println("Payment Method: " + paymentMethod);
//
//        Map<String, Object> outputVariables = new HashMap<>();
//
//        try {
//            FraudCheckResult result = fraudService.performFraudCheck(customerId, paymentAmount, paymentMethod);
//
//            outputVariables.put("fraudCheckResult", result.getResult());
//            outputVariables.put("fraudReason", result.getReason());
//            outputVariables.put("riskScore", result.getRiskScore());
//
//            System.out.println("✓ Fraud check result: " + result.getResult());
//            System.out.println("  Risk Score: " + String.format("%.2f", result.getRiskScore()));
//            System.out.println("  Reason: " + result.getReason());
//
//            client.newCompleteCommand(job.getKey())
//                    .variables(outputVariables)
//                    .send()
//                    .join();
//
//        } catch (Exception e) {
//            System.err.println("Error in fraud check: " + e.getMessage());
//            client.newFailCommand(job.getKey())
//                    .retries(job.getRetries() - 1)
//                    .errorMessage("Fraud check failed: " + e.getMessage())
//                    .send()
//                    .join();
//        }
//    }
//}
// src/main/java/com/demo/paymybalance/worker/UpdatedFraudCheckWorker.java
package com.demo.paymybalance.worker;

import com.demo.paymybalance.model.FraudCheckResult;
import com.demo.paymybalance.service.FraudService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class FraudCheckWorker {

    @Autowired
    private FraudService fraudService;

    @JobWorker(type = "fraud-check")
    public void performFraudCheck(final JobClient client, final ActivatedJob job) {
        System.out.println("=== Performing Enhanced Fraud Check ===");

        Map<String, Object> variables = job.getVariablesAsMap();
        String customerId = (String) variables.get("customerId");
        BigDecimal paymentAmount = new BigDecimal(variables.get("paymentAmount").toString());
        String paymentMethod = (String) variables.get("paymentMethod");

        System.out.println("Customer ID: " + customerId);
        System.out.println("Payment Amount: $" + paymentAmount);
        System.out.println("Payment Method: " + paymentMethod);
        System.out.println("High Risk Threshold: " + fraudService.getHighRiskThreshold());
        System.out.println("Medium Risk Threshold: " + fraudService.getMediumRiskThreshold());

        Map<String, Object> outputVariables = new HashMap<>();

        try {
            FraudCheckResult result = fraudService.performFraudCheck(customerId, paymentAmount, paymentMethod);

            outputVariables.put("fraudCheckResult", result.getResult());
            outputVariables.put("fraudReason", result.getReason());
            outputVariables.put("riskScore", result.getRiskScore());

            // Add rejection reason for the rejection notice task
            if ("REJECTED".equals(result.getResult())) {
                outputVariables.put("rejectionReason", result.getReason());
            }

            System.out.println("==========================================");
            if ("REJECTED".equals(result.getResult())) {
                System.out.println("🚫 FRAUD CHECK RESULT: " + result.getResult());
                System.out.println("🔴 Risk Score: " + String.format("%.2f", result.getRiskScore()));
                System.out.println("❌ Reason: " + result.getReason());
                System.out.println("➡️ Process will now go to REJECTION path");
            } else {
                System.out.println("✅ FRAUD CHECK RESULT: " + result.getResult());
                System.out.println("🟢 Risk Score: " + String.format("%.2f", result.getRiskScore()));
                System.out.println("✓ Reason: " + result.getReason());
                System.out.println("➡️ Process will continue to PAYMENT path");
            }
            System.out.println("==========================================");

            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();

        } catch (Exception e) {
            System.err.println("Error in fraud check: " + e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Fraud check failed: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
