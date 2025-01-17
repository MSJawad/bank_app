package com.example.bank_app.services;

import com.example.bank_app.store.AccountPool;
import com.example.bank_app.entities.Account;
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
        account.setBalance(account.getBalance() - totalDeductibles);
        return accountPool.save(account);
    }

    public Account withdrawCash(Integer id, double amount) {
        return withdrawCash(id, amount, false);
    }

    public List<Account> transferCash(Integer fromTransferId, Integer toTransferId, double amount) {
        Account fromAccount = getAccount(fromTransferId).orElseThrow(() ->
                new RuntimeException("Source account not found"));
        Account toAccount = getAccount(toTransferId).orElseThrow(() ->
                new RuntimeException("Destination account not found"));

        if (!fromAccount.getAccountHolderName().equalsIgnoreCase(toAccount.getAccountHolderName())) {
            throw new RuntimeException("Source account holder name does not match destination account holder name");
        }
        List<Account> accounts = new ArrayList<>();
        accounts.add(withdrawCash(fromTransferId, amount, true));
        accounts.add(depositCash(toTransferId, amount));
        return accounts;
    }
}
