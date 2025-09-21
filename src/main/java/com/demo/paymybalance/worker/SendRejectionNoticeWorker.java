package com.demo.paymybalance.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendRejectionNoticeWorker {

    @JobWorker(type = "send-rejection-notice")
    public void sendRejectionNotice(final JobClient client, final ActivatedJob job) {
        System.out.println("=== Sending Rejection Notice ===");

        Map<String, Object> variables = job.getVariablesAsMap();
        String customerId = (String) variables.get("customerId");
        String rejectionReason = (String) variables.get("fraudReason"); // Using fraudReason as rejectionReason

        System.out.println("Customer ID: " + customerId);
        System.out.println("Rejection Reason: " + rejectionReason);

        try {
            // Simulate sending rejection notification
            System.out.println("\n=== PAYMENT REJECTION NOTICE ===");
            System.out.println("Dear Customer " + customerId + ",");
            System.out.println("Your payment request has been declined.");
            System.out.println("Reason: " + (rejectionReason != null ? rejectionReason : "Risk assessment failed"));
            System.out.println("Please contact customer service for assistance.");
            System.out.println("================================\n");

            // In real implementation, this would send email/SMS

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

            System.out.println("✓ Rejection notice sent successfully");

        } catch (Exception e) {
            System.err.println("Error sending rejection notice: " + e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Rejection notice failed: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
