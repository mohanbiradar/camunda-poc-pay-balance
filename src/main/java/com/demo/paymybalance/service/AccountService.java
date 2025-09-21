package com.demo.paymybalance.service;

import com.demo.paymybalance.model.AccountBalance;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AccountService {

    private final Map<String, AccountBalance> accounts = new HashMap<>();

    public AccountService() {
        // Initialize with test data
        accounts.put("CC001", new AccountBalance("CC001", new BigDecimal("2450.75"), new BigDecimal("7549.25"),
                new BigDecimal("10000.00"), LocalDateTime.now().minusDays(15), new BigDecimal("125.00")));
        accounts.put("CC002", new AccountBalance("CC002", new BigDecimal("5230.50"), new BigDecimal("14769.50"),
                new BigDecimal("20000.00"), LocalDateTime.now().minusDays(8), new BigDecimal("261.50")));
        accounts.put("CC003", new AccountBalance("CC003", new BigDecimal("890.25"), new BigDecimal("4109.75"),
                new BigDecimal("5000.00"), LocalDateTime.now().minusDays(22), new BigDecimal("45.00")));
    }

    public Optional<AccountBalance> getBalance(String cardAccountId) {
        return Optional.ofNullable(accounts.get(cardAccountId));
    }

    public boolean updateBalance(String cardAccountId, BigDecimal paymentAmount) {
        AccountBalance account = accounts.get(cardAccountId);
        if (account != null) {
            BigDecimal newBalance = account.getCurrentBalance().subtract(paymentAmount);
            BigDecimal newAvailableCredit = account.getAvailableCredit().add(paymentAmount);

            account.setCurrentBalance(newBalance);
            account.setAvailableCredit(newAvailableCredit);
            account.setLastPaymentDate(LocalDateTime.now());

            return true;
        }
        return false;
    }
}

