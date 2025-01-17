package com.example.bank_app.services;

import com.example.bank_app.entities.Account;
import com.example.bank_app.store.AccountPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankingServiceTest {

    private BankingService bankingService;
    private AccountPool accountPool;

    /**
     * Sets up the test environment by mocking the AccountPool and injecting it into the BankingService.
     */
    @BeforeEach
    void setUp() {
        accountPool = Mockito.mock(AccountPool.class);
        bankingService = new BankingService(accountPool); // Inject mock dependency
    }

    /**
     * Tests the creation of an account to ensure the AccountPool's save method is called and the account is returned.
     */
    @Test
    void testCreateAccount() {
        Account account = new Account();
        account.setId(1);
        account.setAccountHolderName("John Doe");
        account.setAccountType("savings");
        account.setBalance(100.0);

        when(accountPool.save(account)).thenReturn(account);

        Account result = bankingService.createAccount(account);

        assertNotNull(result);
        assertEquals(account, result);
        verify(accountPool, times(1)).save(account);
    }

    /**
     * Tests the retrieval of an account by ID to ensure the correct account is returned.
     */
    @Test
    void testGetAccount() {
        Account account = new Account();
        account.setId(1);
        account.setAccountHolderName("John Doe");
        account.setAccountType("savings");
        account.setBalance(100.0);

        when(accountPool.findById(1)).thenReturn(Optional.of(account));

        Optional<Account> result = bankingService.getAccount(1);

        assertTrue(result.isPresent());
        assertEquals(account, result.get());
    }

    /**
     * Tests depositing cash into an account and verifies that the balance is updated correctly.
     */
    @Test
    void testDepositCash() {
        Account account = new Account();
        account.setId(1);
        account.setAccountHolderName("John Doe");
        account.setAccountType("savings");
        account.setBalance(100.0);

        when(accountPool.findById(1)).thenReturn(Optional.of(account));
        when(accountPool.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = bankingService.depositCash(1, 50.0);

        assertEquals(150.0, result.getBalance());
        verify(accountPool, times(1)).save(result);
    }

    /**
     * Tests withdrawing cash from a savings account with sufficient balance.
     * Ensures the withdrawal fee is applied and the balance is updated.
     */
    @Test
    void testWithdrawCash_SavingsAccount_WithSufficientBalance() {
        Account account = new Account();
        account.setId(1);
        account.setAccountHolderName("John Doe");
        account.setAccountType("savings");
        account.setBalance(105.0); // Enough to cover amount + fee

        when(accountPool.findById(1)).thenReturn(Optional.of(account));
        when(accountPool.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = bankingService.withdrawCash(1, 100.0);

        assertEquals(0.0, result.getBalance()); // 100 + 5 fee deducted
        verify(accountPool, times(1)).save(result);
    }

    /**
     * Tests withdrawing cash from a savings account with insufficient balance to cover the fee.
     */
    @Test
    void testWithdrawCash_SavingsAccount_InsufficientBalanceForFee() {
        Account account = new Account();
        account.setId(1);
        account.setAccountHolderName("John Doe");
        account.setAccountType("savings");
        account.setBalance(104.0); // Not enough for amount + fee

        when(accountPool.findById(1)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> bankingService.withdrawCash(1, 100.0));
        assertEquals("Account balance cannot cover savings account cash withdrawal fees", exception.getMessage());
    }

    /**
     * Tests withdrawing cash from a chequings account.
     * Ensures the withdrawal is successful without additional fees.
     */
    @Test
    void testWithdrawCash_ChequingsAccount() {
        Account account = new Account();
        account.setId(2);
        account.setAccountHolderName("Jane Doe");
        account.setAccountType("chequings");
        account.setBalance(100.0); // Exactly enough for the withdrawal

        when(accountPool.findById(2)).thenReturn(Optional.of(account));
        when(accountPool.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = bankingService.withdrawCash(2, 100.0);

        assertEquals(0.0, result.getBalance()); // No additional fee for chequings
        verify(accountPool, times(1)).save(result);
    }

    /**
     * Tests transferring cash from a savings account to a chequings account.
     * Verifies that the savings account balance accounts for the withdrawal fee and the chequings account receives the correct amount.
     */
    @Test
    void testTransferCash_SavingsToChequings() {
        Account savingsAccount = new Account();
        savingsAccount.setId(1);
        savingsAccount.setAccountHolderName("John Doe");
        savingsAccount.setAccountType("savings");
        savingsAccount.setBalance(100.0);

        Account chequingsAccount = new Account();
        chequingsAccount.setId(2);
        chequingsAccount.setAccountHolderName("John Doe");
        chequingsAccount.setAccountType("chequings");
        chequingsAccount.setBalance(50.0);

        when(accountPool.findById(1)).thenReturn(Optional.of(savingsAccount));
        when(accountPool.findById(2)).thenReturn(Optional.of(chequingsAccount));
        when(accountPool.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bankingService.transferCash(1, 2, 100.0);

        assertEquals(0.0, savingsAccount.getBalance()); // 100 + 5 fee deducted
        assertEquals(150.0, chequingsAccount.getBalance()); // 100 added
        verify(accountPool, times(2)).save(any(Account.class));
    }

    /**
     * Tests transferring cash from a chequings account to a savings account.
     * Ensures balances are updated correctly with no additional fees for chequings.
     */
    @Test
    void testTransferCash_ChequingsToSavings() {
        Account chequingsAccount = new Account();
        chequingsAccount.setId(1);
        chequingsAccount.setAccountHolderName("Jane Doe");
        chequingsAccount.setAccountType("chequings");
        chequingsAccount.setBalance(200.0);

        Account savingsAccount = new Account();
        savingsAccount.setId(2);
        savingsAccount.setAccountHolderName("Jane Doe");
        savingsAccount.setAccountType("savings");
        savingsAccount.setBalance(50.0);

        when(accountPool.findById(1)).thenReturn(Optional.of(chequingsAccount));
        when(accountPool.findById(2)).thenReturn(Optional.of(savingsAccount));
        when(accountPool.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bankingService.transferCash(1, 2, 150.0);

        assertEquals(50.0, chequingsAccount.getBalance()); // 150 deducted
        assertEquals(200.0, savingsAccount.getBalance()); // 150 added
        verify(accountPool, times(2)).save(any(Account.class));
    }
}
