package com.atm.machine.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.atm.machine.entity.Account;
import com.atm.machine.service.AccountService;

@RestController
public class AccountController {
	@Autowired
	private AccountService accountService;

	@PostMapping("/addAccount")
	public Account addAccount(@RequestBody Account account) {
		return accountService.createAccount(account);
	}

	@PostMapping("/addAccounts")
	public List<Account> addAccounts(@RequestBody List<Account> accounts) {
		return accountService.createAccounts(accounts);
	}

	@GetMapping("/account/{id}")
	public Account getAccountById(@PathVariable int id) {
		return accountService.getAccountById(id);
	}

	@GetMapping("/accounts")
	public List<Account> getAllAccounts() {
		return accountService.getAllAccounts();
	}

	@PutMapping("/updateaccount")
	public Account updateAccount(@RequestBody Account account) {
		return accountService.updateAccount(account);
	}

	@DeleteMapping("/account/{id}")
	public String deleteAccount(@PathVariable int id) {
		return accountService.deleteAccountById(id);
	}

	@PostMapping("/initAccounts")
	public List<Account> initAccounts() {
		return accountService.initAccounts();
	}
}
