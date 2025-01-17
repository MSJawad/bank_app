package com.example.bank_app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping("/api/test")
    public String getString() {
        return "Hello World";
    }
}
