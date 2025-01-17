package com.example.bank_app.store;

import com.example.bank_app.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountPool extends JpaRepository<Account, Integer> {

}