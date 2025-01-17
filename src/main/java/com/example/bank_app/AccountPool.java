package com.example.bank_app;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountPool extends JpaRepository<Account, Integer> {

}