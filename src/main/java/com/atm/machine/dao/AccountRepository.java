package com.atm.machine.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atm.machine.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {

}
