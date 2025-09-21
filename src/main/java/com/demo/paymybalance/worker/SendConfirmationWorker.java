package com.demo.paymybalance.worker;

import com.demo.paymybalance.service.NotificationService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class SendConfirmationWorker {

    @Autowired
    private NotificationService notificationService;

    @JobWorker(type = "send-confirmation")
    public void sendConfirmation(final JobClient client, final ActivatedJob job) {
        System.out.println("=== Sending Confirmation ===");

        Map<String, Object> variables = job.getVariablesAsMap();
        String customerId = (String) variables.get("customerId");
        BigDecimal paymentAmount = new BigDecimal(variables.get("paymentAmount").toString());
        String transactionId = (String) variables.get("transactionId");

        System.out.println("Customer ID: " + customerId);
        System.out.println("Payment Amount: $" + paymentAmount);
        System.out.println("Transaction ID: " + transactionId);

        Map<String, Object> outputVariables = new HashMap<>();

        try {
            boolean sent = notificationService.sendPaymentConfirmation(customerId, paymentAmount, transactionId);

            outputVariables.put("confirmationSent", sent);

            if (sent) {
                System.out.println("✓ Confirmation sent successfully");
            } else {
                System.out.println("✗ Failed to send confirmation");
            }

            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();

        } catch (Exception e) {
            System.err.println("Error sending confirmation: " + e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Confirmation sending failed: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
