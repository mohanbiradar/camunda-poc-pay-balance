// src/main/java/com/demo/paymybalance/worker/DebugUserTaskWorker.java
package com.demo.paymybalance.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class DebugUserTaskWorker {

    // This worker will handle the user task if it's not being completed through Tasklist
//    @JobWorker(type = "io.camunda.zeebe:userTask", autoComplete = false)
//    public void handleUserTaskDebug(final JobClient client, final ActivatedJob job) {
//        System.out.println("\n🔍 =DEBUG= USER TASK DETECTED ===");
//        System.out.println("Job Key: " + job.getKey());
//        System.out.println("Element ID: " + job.getElementId());
//        System.out.println("Element Instance Key: " + job.getElementInstanceKey());
//        System.out.println("Process Instance Key: " + job.getProcessInstanceKey());
//        System.out.println("Job Type: " + job.getType());
//        System.out.println("Job Custom Headers: " + job.getCustomHeaders());
//
//        System.out.println("\n📋 CURRENT VARIABLES:");
//        Map<String, Object> variables = job.getVariablesAsMap();
//        variables.forEach((key, value) ->
//                System.out.println("  " + key + " = " + value)
//        );
//
//        System.out.println("\n⏰ JOB TIMING:");
//        System.out.println("Created: " + job.getDeadline());
//        System.out.println("Retries: " + job.getRetries());
//
//        // Check if this is the payment options form
//        if ("Activity_PresentPaymentOptions".equals(job.getElementId())) {
//            System.out.println("\n💳 PAYMENT OPTIONS FORM DETECTED");
//            System.out.println("This task should be completed via Tasklist at: http://localhost:8082");
//            System.out.println("OR we can auto-complete it here for demo purposes");
//
//            // Ask user what to do
//            System.out.println("\n🤖 AUTO-COMPLETING IN 5 SECONDS...");
//            System.out.println("   (Go to Tasklist to complete manually, or let this auto-complete)");
//
//            try {
//                Thread.sleep(5000); // Wait 5 seconds
//
//                // Auto-complete with form data
//                Map<String, Object> formData = new HashMap<>();
//
//                // Get current balance for smart defaults
//                Object currentBalance = variables.get("currentBalance");
//                Object minimumPayment = variables.get("minimumPayment");
//
//                // Set payment amount (use minimum payment * 2 or default)
//                double paymentAmount = 500.00;
//                if (minimumPayment != null) {
//                    try {
//                        double minPay = Double.parseDouble(minimumPayment.toString());
//                        paymentAmount = minPay * 2; // Pay double the minimum
//                    } catch (NumberFormatException e) {
//                        System.out.println("Could not parse minimum payment, using default");
//                    }
//                }
//
//                formData.put("paymentAmount", paymentAmount);
//                formData.put("paymentMethod", "ACH");
//                formData.put("paymentDate", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//                formData.put("confirmPayment", true);
//
//                System.out.println("\n✅ AUTO-COMPLETING USER TASK WITH:");
//                formData.forEach((key, value) ->
//                        System.out.println("  " + key + " = " + value)
//                );
//
//                client.newCompleteCommand(job.getKey())
//                        .variables(formData)
//                        .send()
//                        .join();
//
//                System.out.println("🚀 USER TASK COMPLETED - PROCESS SHOULD CONTINUE");
//                System.out.println("=====================================\n");
//
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                System.err.println("Auto-completion interrupted");
//            } catch (Exception e) {
//                System.err.println("❌ Error auto-completing user task: " + e.getMessage());
//                e.printStackTrace();
//
//                client.newFailCommand(job.getKey())
//                        .retries(job.getRetries() - 1)
//                        .errorMessage("Auto-completion failed: " + e.getMessage())
//                        .send()
//                        .join();
//            }
//        } else {
//            System.out.println("\n⚠️ UNKNOWN USER TASK: " + job.getElementId());
//            System.out.println("Not auto-completing this task");
//        }
//    }
//}

// Alternative: Simple Auto-Complete Worker (uncomment if you want immediate completion)
/*
@Component
public class SimpleUserTaskAutoComplete {

    @JobWorker(type = "io.camunda.zeebe:userTask")
    public void autoCompleteUserTask(final JobClient client, final ActivatedJob job) {
        if ("Activity_PresentPaymentOptions".equals(job.getElementId())) {
            System.out.println("\n🤖 AUTO-COMPLETING USER TASK IMMEDIATELY");
            
            Map<String, Object> variables = job.getVariablesAsMap();
            Map<String, Object> formData = new HashMap<>();
            
            // Smart defaults based on current data
            Object minPayment = variables.get("minimumPayment");
            double paymentAmount = 500.00;
            
            if (minPayment != null) {
                try {
                    paymentAmount = Double.parseDouble(minPayment.toString()) * 2;
                } catch (NumberFormatException ignored) {}
            }
            
            formData.put("paymentAmount", paymentAmount);
            formData.put("paymentMethod", "ACH");
            formData.put("paymentDate", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            formData.put("confirmPayment", true);
            
            client.newCompleteCommand(job.getKey())
                .variables(formData)
                .send()
                .join();
                
            System.out.println("✅ User task completed with amount: $" + paymentAmount);
        }
    }
}
*/
}