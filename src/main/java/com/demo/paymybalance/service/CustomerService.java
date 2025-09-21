package com.demo.paymybalance.service;

import com.demo.paymybalance.model.Customer;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomerService {

    private final Map<String, Customer> customers = new HashMap<>();

    public CustomerService() {
        // Initialize with some test data
        customers.put("CUST001", new Customer("CUST001", "John Smith", "john.smith@company.com", "+1-555-0101", "ACTIVE", "Tech Corp Inc"));
        customers.put("CUST002", new Customer("CUST002", "Sarah Johnson", "sarah.j@enterprise.com", "+1-555-0102", "ACTIVE", "Enterprise Solutions LLC"));
        customers.put("CUST003", new Customer("CUST003", "Mike Brown", "mike.brown@startup.com", "+1-555-0103", "SUSPENDED", "StartupXYZ"));
    }

    public Optional<Customer> validateCustomer(String customerId) {
        Customer customer = customers.get(customerId);
        if (customer != null && "ACTIVE".equals(customer.getStatus())) {
            return Optional.of(customer);
        }
        return Optional.empty();
    }

    public boolean validateCardAccount(String customerId, String cardAccountId) {
        // Simple validation - in real world, this would check database relationships
        return customers.containsKey(customerId) && cardAccountId.startsWith("CC" + customerId.substring(4));
    }
}
