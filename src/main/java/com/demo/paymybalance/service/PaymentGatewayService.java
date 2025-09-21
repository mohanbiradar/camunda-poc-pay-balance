package com.demo.paymybalance.service;

import com.demo.paymybalance.model.PaymentResponse;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentGatewayService {

    private final Map<String, PaymentResponse> transactions = new HashMap<>();

    public PaymentResponse initiatePayment(String cardAccountId, BigDecimal paymentAmount, String paymentMethod) {
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Simulate payment processing
        PaymentResponse response = new PaymentResponse(
                transactionId,
                "PROCESSING",
                paymentAmount,
                LocalDateTime.now(),
                "Payment initiated successfully"
        );

        transactions.put(transactionId, response);
        return response;
    }

    public PaymentResponse confirmPayment(String transactionId) {
        PaymentResponse transaction = transactions.get(transactionId);
        if (transaction != null) {
            // Simulate successful payment confirmation
            transaction.setStatus("COMPLETED");
            transaction.setMessage("Payment completed successfully");
            transaction.setProcessedAt(LocalDateTime.now());
            return transaction;
        }

        return new PaymentResponse(transactionId, "FAILED", null, LocalDateTime.now(), "Transaction not found");
    }
}
