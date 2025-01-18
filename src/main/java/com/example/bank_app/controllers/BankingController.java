package com.example.bank_app.controllers;

import com.example.bank_app.models.request.DepositRequest;
import com.example.bank_app.models.request.TransferRequest;
import com.example.bank_app.models.request.WithdrawRequest;
import com.example.bank_app.models.response.TransferResponse;
import com.example.bank_app.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.bank_app.services.BankingService;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/accounts")
public class BankingController {

    @Autowired
    private BankingService bankingService;

    @GetMapping("/test")
    public String getString() {
        return "Hello User";
    }

    private static boolean validAccount(Account account) {
        return account.getBalance() > 0 && !account.getAccountHolderName().isEmpty()
                && (Arrays.asList("chequing", "savings").contains(account.getAccountType().toLowerCase()));
    }

    @PostMapping("/createAccount")
    public Account createAccount(@RequestBody Account account) {
        if (!validAccount(account)) {
            throw new IllegalArgumentException("Invalid account parameters");
        }
        return bankingService.createAccount(account);
    }

    @GetMapping("/accounts/{id}")
    public Account getAccount(@PathVariable int id) {
        return bankingService.getAccount(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @PostMapping("/deposit")
    public Account deposit(@RequestBody DepositRequest depositRequest) {
        return bankingService.depositCash(depositRequest.getId(), depositRequest.getAmount());
    }

    @PostMapping("/withdraw")
    public Account withdraw(@RequestBody WithdrawRequest withdrawRequest) {
        return bankingService.withdrawCash(withdrawRequest.getId(), withdrawRequest.getAmount());
    }

    @PostMapping("/transfer")
    public TransferResponse transfer(@RequestBody TransferRequest transferRequest) {
        List<Account> accounts = bankingService.transferCash(transferRequest.getAccountFromId(),
                transferRequest.getAccountToId(), transferRequest.getAmount());
        if (accounts.size() != 2) {
            throw new RuntimeException("Undefined number of accounts found during transfer request completion");
        }
        return new TransferResponse(accounts.get(0), accounts.get(1));
    }
}
