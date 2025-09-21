package com.demo.paymybalance.worker;

import com.demo.paymybalance.model.AccountBalance;
import com.demo.paymybalance.service.AccountService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class RetrieveBalanceWorker {

    @Autowired
    private AccountService accountService;

    @JobWorker(type = "retrieve-balance")
    public void retrieveBalance(final JobClient client, final ActivatedJob job) {
        System.out.println("=== Retrieving Balance ===");

        Map<String, Object> variables = job.getVariablesAsMap();
        String cardAccountId = (String) variables.get("cardAccountId");

        System.out.println("Card Account ID: " + cardAccountId);

        Map<String, Object> outputVariables = new HashMap<>();

        try {
            Optional<AccountBalance> balance = accountService.getBalance(cardAccountId);

            if (balance.isPresent()) {
                AccountBalance account = balance.get();
                outputVariables.put("currentBalance", account.getCurrentBalance());
                outputVariables.put("availableCredit", account.getAvailableCredit());
                outputVariables.put("creditLimit", account.getCreditLimit());
                outputVariables.put("minimumPayment", account.getMinimumPayment());

                System.out.println("✓ Balance retrieved: $" + account.getCurrentBalance());
                System.out.println("  Available Credit: $" + account.getAvailableCredit());
            } else {
                outputVariables.put("balanceRetrieved", false);
                outputVariables.put("balanceError", "Account not found");
                System.out.println("✗ Account not found");
            }

            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();

        } catch (Exception e) {
            System.err.println("Error retrieving balance: " + e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Balance retrieval failed: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
