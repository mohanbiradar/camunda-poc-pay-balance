package com.demo.paymybalance.controller;

import com.demo.paymybalance.model.PaymentRequest;
import com.demo.paymybalance.service.FraudService;
import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
@CrossOrigin(origins = "*")
public class DemoScenariosController {

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private FraudService fraudService;

    @PostMapping("/scenario/low-risk")
    public ResponseEntity<?> testLowRiskScenario() {
        PaymentRequest request = new PaymentRequest("CUST001", "CC001", new BigDecimal("100.00"), "ACH", "2024-01-15T10:30:00");
        fraudService.setHighRiskThreshold(0.9); // Very high threshold - almost everything approved
        return initiatePaymentProcess(request, "Low Risk Scenario");
    }

    @PostMapping("/scenario/high-risk")
    public ResponseEntity<?> testHighRiskScenario() {
        PaymentRequest request = new PaymentRequest("CUST003", "CC003", new BigDecimal("15000.00"), "WIRE", "2024-01-15T10:30:00");
        fraudService.setHighRiskThreshold(0.3); // Low threshold - likely to reject
        return initiatePaymentProcess(request, "High Risk Scenario");
    }

    @PostMapping("/scenario/medium-risk")
    public ResponseEntity<?> testMediumRiskScenario() {
        PaymentRequest request = new PaymentRequest("CUST002", "CC002", new BigDecimal("3000.00"), "BANK_TRANSFER", "2024-01-15T10:30:00");
        fraudService.setMediumRiskThreshold(0.2); // Medium risk scenarios
        fraudService.setHighRiskThreshold(0.6);
        return initiatePaymentProcess(request, "Medium Risk Scenario (50/50 chance)");
    }

    @PostMapping("/scenario/suspended-customer")
    public ResponseEntity<?> testSuspendedCustomerScenario() {
        PaymentRequest request = new PaymentRequest("CUST003", "CC003", new BigDecimal("500.00"), "ACH", "2024-01-15T10:30:00");
        fraudService.setHighRiskThreshold(0.4); // Lower threshold for suspended customer
        return initiatePaymentProcess(request, "Suspended Customer Scenario");
    }

    @PostMapping("/scenario/large-amount")
    public ResponseEntity<?> testLargeAmountScenario() {
        PaymentRequest request = new PaymentRequest("CUST001", "CC001", new BigDecimal("25000.00"), "WIRE", "2024-01-15T10:30:00");
        fraudService.setHighRiskThreshold(0.5); // Medium threshold
        return initiatePaymentProcess(request, "Large Amount Scenario");
    }

    @PostMapping("/scenario/custom")
    public ResponseEntity<?> testCustomScenario(@RequestBody CustomScenarioRequest customRequest) {
        // Set custom thresholds
        fraudService.setHighRiskThreshold(customRequest.getHighRiskThreshold());
        fraudService.setMediumRiskThreshold(customRequest.getMediumRiskThreshold());

        PaymentRequest request = new PaymentRequest(
                customRequest.getCustomerId(),
                customRequest.getCardAccountId(),
                customRequest.getPaymentAmount(),
                customRequest.getPaymentMethod(),
                customRequest.getPaymentDate()
        );

        return initiatePaymentProcess(request, "Custom Scenario");
    }

    @GetMapping("/risk-thresholds")
    public ResponseEntity<?> getRiskThresholds() {
        Map<String, Object> thresholds = new HashMap<>();
        thresholds.put("highRiskThreshold", fraudService.getHighRiskThreshold());
        thresholds.put("mediumRiskThreshold", fraudService.getMediumRiskThreshold());
        return ResponseEntity.ok(thresholds);
    }

    @PostMapping("/risk-thresholds")
    public ResponseEntity<?> setRiskThresholds(@RequestBody Map<String, Double> thresholds) {
        if (thresholds.containsKey("highRiskThreshold")) {
            fraudService.setHighRiskThreshold(thresholds.get("highRiskThreshold"));
        }
        if (thresholds.containsKey("mediumRiskThreshold")) {
            fraudService.setMediumRiskThreshold(thresholds.get("mediumRiskThreshold"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Risk thresholds updated");
        response.put("highRiskThreshold", fraudService.getHighRiskThreshold());
        response.put("mediumRiskThreshold", fraudService.getMediumRiskThreshold());

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> initiatePaymentProcess(PaymentRequest paymentRequest, String scenarioName) {
        try {
            System.out.println("=== " + scenarioName + " ===");
            System.out.println("High Risk Threshold: " + fraudService.getHighRiskThreshold());
            System.out.println("Medium Risk Threshold: " + fraudService.getMediumRiskThreshold());

            Map<String, Object> processVariables = new HashMap<>();
            processVariables.put("customerId", paymentRequest.getCustomerId());
            processVariables.put("cardAccountId", paymentRequest.getCardAccountId());
            processVariables.put("paymentAmount", paymentRequest.getPaymentAmount());
            processVariables.put("paymentMethod", paymentRequest.getPaymentMethod());
            processVariables.put("paymentDate", paymentRequest.getPaymentDate());

            var processInstance = zeebeClient.newCreateInstanceCommand()
                    .bpmnProcessId("pay-my-balance-process")
                    .latestVersion()
                    .variables(processVariables)
                    .send()
                    .join();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("scenario", scenarioName);
            response.put("message", "Process initiated - watch console for fraud check result");
            response.put("processInstanceKey", processInstance.getProcessInstanceKey());
            response.put("expectedOutcome", predictOutcome(paymentRequest));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("scenario", scenarioName);
            errorResponse.put("message", "Failed: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    private String predictOutcome(PaymentRequest request) {
        // Rough prediction based on known risk factors
        double estimatedRisk = 0.1;

        if ("CUST003".equals(request.getCustomerId())) {
            estimatedRisk += 0.3;
        }

        if (request.getPaymentAmount().compareTo(new BigDecimal("10000")) > 0) {
            estimatedRisk += 0.5;
        } else if (request.getPaymentAmount().compareTo(new BigDecimal("5000")) > 0) {
            estimatedRisk += 0.3;
        }

        if ("WIRE".equals(request.getPaymentMethod())) {
            estimatedRisk += 0.15;
        }

        if (estimatedRisk >= fraudService.getHighRiskThreshold()) {
            return "Likely REJECTION due to high risk score";
        } else if (estimatedRisk >= fraudService.getMediumRiskThreshold()) {
            return "Medium risk - 50/50 chance of approval";
        } else {
            return "Likely APPROVAL due to low risk";
        }
    }

    // Inner class for custom scenario requests
    public static class CustomScenarioRequest extends PaymentRequest {
        private double highRiskThreshold = 0.7;
        private double mediumRiskThreshold = 0.4;

        public double getHighRiskThreshold() { return highRiskThreshold; }
        public void setHighRiskThreshold(double highRiskThreshold) { this.highRiskThreshold = highRiskThreshold; }
        public double getMediumRiskThreshold() { return mediumRiskThreshold; }
        public void setMediumRiskThreshold(double mediumRiskThreshold) { this.mediumRiskThreshold = mediumRiskThreshold; }
    }
}
