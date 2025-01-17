package com.example.bank_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankingService {

    @Autowired
    private AccountPool accountPool;

    public Account createAccount(Account account) {
        return accountPool.save(account);
    }

    public Optional<Account> getAccount(Integer id) {
        return accountPool.findById(id);
    }

    public Account depositCash(Integer id, double amount) {
        Account account = getAccount(id).orElseThrow(
                        () -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance() + amount);
        return accountPool.save(account);
    }

    private Account withdrawCash(Integer id, double amount, boolean accountTransfer) {
        Account account = getAccount(id).orElseThrow(
                () -> new RuntimeException("Account not found"));
        String accountType = account.getAccountType();

        double accountBalance = account.getBalance();
        // extra balance deducted because withdrawing cash from savings account
        double totalDeductibles = (accountType.equalsIgnoreCase("savings")
                && !accountTransfer) ? amount + 5 : amount;

        if (accountBalance < totalDeductibles) {
            if (accountBalance > amount) {
                throw new RuntimeException("Account balance cannot cover savings account cash withdrawal fees");
            }
            throw new RuntimeException("Insufficient funds");
        }
        account.setBalance(account.getBalance() - amount);
        return accountPool.save(account);
    }

    public Account withdrawCash(Integer id, double amount) {
        return withdrawCash(id, amount, false);
    }
}
