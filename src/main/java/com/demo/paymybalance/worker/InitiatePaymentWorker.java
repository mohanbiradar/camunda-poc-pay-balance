package com.demo.paymybalance.worker;

import com.demo.paymybalance.model.PaymentResponse;
import com.demo.paymybalance.service.PaymentGatewayService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class InitiatePaymentWorker {

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @JobWorker(type = "initiate-payment")
    public void initiatePayment(final JobClient client, final ActivatedJob job) {
        System.out.println("=== Initiating Payment ===");

        Map<String, Object> variables = job.getVariablesAsMap();
        String cardAccountId = (String) variables.get("cardAccountId");
        BigDecimal paymentAmount = new BigDecimal(variables.get("paymentAmount").toString());
        String paymentMethod = (String) variables.get("paymentMethod");

        System.out.println("Card Account ID: " + cardAccountId);
        System.out.println("Payment Amount: $" + paymentAmount);
        System.out.println("Payment Method: " + paymentMethod);

        Map<String, Object> outputVariables = new HashMap<>();

        try {
            PaymentResponse response = paymentGatewayService.initiatePayment(cardAccountId, paymentAmount, paymentMethod);

            outputVariables.put("transactionId", response.getTransactionId());
            outputVariables.put("paymentStatus", response.getStatus());
            outputVariables.put("paymentMessage", response.getMessage());

            System.out.println("✓ Payment initiated successfully");
            System.out.println("  Transaction ID: " + response.getTransactionId());
            System.out.println("  Status: " + response.getStatus());

            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();

        } catch (Exception e) {
            System.err.println("Error initiating payment: " + e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Payment initiation failed: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}

