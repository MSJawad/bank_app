package com.example.bank_app.models.response;

import com.example.bank_app.models.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TransferResponse {
    private Account transferFromAccount;
    private Account transferToAccount;
}
