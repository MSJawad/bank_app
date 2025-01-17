package com.example.bank_app.controllers.response;

import com.example.bank_app.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TransferResponse {
    private Account transferFromAccount;
    private Account transferToAccount;
}
