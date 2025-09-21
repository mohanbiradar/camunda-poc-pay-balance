package com.demo.paymybalance.worker;

import com.demo.paymybalance.model.PaymentResponse;
import com.demo.paymybalance.service.PaymentGatewayService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConfirmPaymentWorker {

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @JobWorker(type = "confirm-payment")
    public void confirmPayment(final JobClient client, final ActivatedJob job) {
        System.out.println("=== Confirming Payment ===");

        Map<String, Object> variables = job.getVariablesAsMap();
        String transactionId = (String) variables.get("transactionId");

        System.out.println("Transaction ID: " + transactionId);

        Map<String, Object> outputVariables = new HashMap<>();

        try {
            // Simulate some processing time
            Thread.sleep(1000);

            PaymentResponse response = paymentGatewayService.confirmPayment(transactionId);

            outputVariables.put("paymentConfirmed", "COMPLETED".equals(response.getStatus()));
            outputVariables.put("finalPaymentStatus", response.getStatus());
            outputVariables.put("paymentMessage", response.getMessage());

            System.out.println("✓ Payment confirmation: " + response.getStatus());
            System.out.println("  Message: " + response.getMessage());

            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();

        } catch (Exception e) {
            System.err.println("Error confirming payment: " + e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Payment confirmation failed: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
