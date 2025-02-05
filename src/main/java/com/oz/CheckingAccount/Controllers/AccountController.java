package com.oz.CheckingAccount.Controllers;

import com.oz.CheckingAccount.Accounts.Account;
import com.oz.CheckingAccount.Accounts.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountRepository accountRepository;

    private AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/{id}")
    private ResponseEntity<Account> getAccountByName(@PathVariable Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if(account.isPresent()) {
            return ResponseEntity.ok(account.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    private ResponseEntity<Void> createAccount(@RequestBody Account account, UriComponentsBuilder ucb) {
        Account newAccount = accountRepository.save(account);
        URI location = ucb
                .path("accounts/{id}")
                .buildAndExpand(newAccount.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    private ResponseEntity<Void> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        Optional<Account> updatedAccount = accountRepository.findById(id);

        if(updatedAccount.isPresent()) {
            updatedAccount.get().setBalance(updatedAccount.get().getBalance().add(account.getBalance()));
            accountRepository.save(updatedAccount.get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
