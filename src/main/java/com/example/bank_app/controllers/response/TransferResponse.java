package com.example.bank_app.controllers.response;

import com.example.bank_app.entities.Account;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TransferResponse {
    private Account transferFromAccount;
    private Account transferToAccount;
}
