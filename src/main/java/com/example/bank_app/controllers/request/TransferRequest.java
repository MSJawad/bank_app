package com.example.bank_app.controllers.request;

import lombok.Getter;


@Getter
public class TransferRequest {

    private int accountFromId;
    private int accountToId;
    private Double amount;
}