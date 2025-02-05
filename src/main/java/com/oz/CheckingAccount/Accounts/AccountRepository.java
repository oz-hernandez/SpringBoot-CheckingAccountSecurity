package com.oz.CheckingAccount.Accounts;

import org.springframework.data.repository.CrudRepository;


public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByName(String name);
}
