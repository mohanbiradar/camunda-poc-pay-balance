package com.demo.paymybalance.worker;

import com.demo.paymybalance.model.AccountBalance;
import com.demo.paymybalance.model.Customer;
import com.demo.paymybalance.model.FraudCheckResult;
import com.demo.paymybalance.model.PaymentResponse;
import com.demo.paymybalance.service.*;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ValidateCustomerWorker {

    @Autowired
    private CustomerService customerService;

    @JobWorker(type = "validate-customer")
    public void validateCustomer(final JobClient client, final ActivatedJob job) {
        System.out.println("=== Validating Customer ===");

        Map<String, Object> variables = job.getVariablesAsMap();
        String customerId = (String) variables.get("customerId");
        String cardAccountId = (String) variables.get("cardAccountId");

        System.out.println("Customer ID: " + customerId);
        System.out.println("Card Account ID: " + cardAccountId);

        Map<String, Object> outputVariables = new HashMap<>();

        try {
            Optional<Customer> customer = customerService.validateCustomer(customerId);
            boolean accountValid = customerService.validateCardAccount(customerId, cardAccountId);

            if (customer.isPresent() && accountValid) {
                outputVariables.put("customerValidated", true);
                outputVariables.put("customerName", customer.get().getName());
                outputVariables.put("customerEmail", customer.get().getEmail());
                outputVariables.put("companyName", customer.get().getCompanyName());
                System.out.println("✓ Customer validation successful");
            } else {
                outputVariables.put("customerValidated", false);
                outputVariables.put("validationError", "Invalid customer or account");
                System.out.println("✗ Customer validation failed");
            }

            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();

        } catch (Exception e) {
            System.err.println("Error in customer validation: " + e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Customer validation failed: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}

