package com.demo.paymybalance.worker;

import com.demo.paymybalance.service.AccountService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class UpdateBalanceWorker {

    @Autowired
    private AccountService accountService;

    @JobWorker(type = "update-balance")
    public void updateBalance(final JobClient client, final ActivatedJob job) {
        System.out.println("=== Updating Account Balance ===");

        Map<String, Object> variables = job.getVariablesAsMap();
        String cardAccountId = (String) variables.get("cardAccountId");
        BigDecimal paymentAmount = new BigDecimal(variables.get("paymentAmount").toString());
        String transactionId = (String) variables.get("transactionId");

        System.out.println("Card Account ID: " + cardAccountId);
        System.out.println("Payment Amount: $" + paymentAmount);
        System.out.println("Transaction ID: " + transactionId);

        Map<String, Object> outputVariables = new HashMap<>();

        try {
            boolean updated = accountService.updateBalance(cardAccountId, paymentAmount);

            outputVariables.put("balanceUpdated", updated);
            outputVariables.put("NewAmount", accountService.getBalance(cardAccountId));

            if (updated) {
                System.out.println("✓ Account balance updated successfully");
            } else {
                System.out.println("✗ Failed to update account balance");
            }

            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();

        } catch (Exception e) {
            System.err.println("Error updating balance: " + e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Balance update failed: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}