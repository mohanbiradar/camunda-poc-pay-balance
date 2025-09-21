package com.demo.paymybalance.controller;

import com.demo.paymybalance.model.PaymentRequest;
import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PayMyBalanceController {

    @Autowired
    private ZeebeClient zeebeClient;

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            System.out.println("=== Initiating Payment Process ===");
            System.out.println("Customer ID: " + paymentRequest.getCustomerId());
            System.out.println("Card Account ID: " + paymentRequest.getCardAccountId());
            System.out.println("Payment Amount: $" + paymentRequest.getPaymentAmount());
            System.out.println("Payment Method: " + paymentRequest.getPaymentMethod());

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
            response.put("message", "Payment process initiated successfully");
            response.put("processInstanceKey", processInstance.getProcessInstanceKey());

            System.out.println("✓ Process Instance Key: " + processInstance.getProcessInstanceKey());
            System.out.println("=====================================");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error initiating payment process: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to initiate payment process: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Pay My Balance Demo");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
