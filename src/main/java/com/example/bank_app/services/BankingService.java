package com.example.bank_app.services;

import com.example.bank_app.store.AccountPool;
import com.example.bank_app.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BankingService {

    @Autowired
    private AccountPool accountPool;

    public BankingService(AccountPool accountPool) {
        this.accountPool = accountPool;
    }

    public Account createAccount(Account account) {
        return accountPool.save(account);
    }

    public Optional<Account> getAccount(Integer id) {
        return accountPool.findById(id);
    }

    public Account depositCash(Integer id, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Invalid deposit amount");
        }
        Account account = getAccount(id).orElseThrow(
                        () -> new IllegalArgumentException("Account not found"));
        account.setBalance(account.getBalance() + amount);
        return accountPool.save(account);
    }

    private Account withdrawCash(Integer id, double amount, boolean accountTransfer) {
        if (amount < 0) {
            throw new IllegalArgumentException("Invalid withdrawal amount");
        }
        Account account = getAccount(id).orElseThrow(
                () -> new IllegalArgumentException("Account not found"));
        String accountType = account.getAccountType();

        double accountBalance = account.getBalance();
        // extra balance deducted because withdrawing cash from savings account
        double totalDeductibles = (accountType.equalsIgnoreCase("savings")
                && !accountTransfer) ? amount + 5 : amount;

        if (accountBalance < totalDeductibles) {
            if (accountBalance > amount) {
                throw new RuntimeException("Account balance cannot cover savings account " +
                        "cash withdrawal fees");
            }
            throw new RuntimeException("Insufficient funds");
        }
        account.setBalance(account.getBalance() - totalDeductibles);
        return accountPool.save(account);
    }

    public Account withdrawCash(Integer id, double amount) {
        return withdrawCash(id, amount, false);
    }

    public List<Account> transferCash(Integer fromTransferId, Integer toTransferId, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Invalid transfer amount");
        }
        Account fromAccount = getAccount(fromTransferId).orElseThrow(() ->
                new IllegalArgumentException("Source account not found"));
        Account toAccount = getAccount(toTransferId).orElseThrow(() ->
                new IllegalArgumentException("Destination account not found"));

        if (!fromAccount.getAccountHolderName().equalsIgnoreCase(toAccount.getAccountHolderName())) {
            throw new RuntimeException("Source account holder name does not match destination account holder name");
        }
        List<Account> accounts = new ArrayList<>();
        accounts.add(withdrawCash(fromTransferId, amount, true));
        accounts.add(depositCash(toTransferId, amount));
        return accounts;
    }
}
