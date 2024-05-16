package com.atm.machine.service;

import static com.atm.machine.util.AccountUtils.convertJsonToAccounts;
import static com.atm.machine.util.AccountUtils.readJsonFromFile;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.atm.machine.dao.AccountRepository;
import com.atm.machine.entity.ATM;
import com.atm.machine.entity.Account;

import jakarta.persistence.EntityManager;

@Service
public class AccountService {

	Logger logger = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	EntityManager entityManager;

	public Account createAccount(Account account) {
		return accountRepository.save(account);
	}

	public List<Account> createAccounts(List<Account> accounts) {
		return accountRepository.saveAll(accounts);
	}

	public Account getAccountById(int id) {
		return accountRepository.findById(id).orElse(null);
	}

	public List<Account> getAllAccounts() {
		return accountRepository.findAll();
	}

	public Account updateAccount(Account newAccount) {

		Optional<Account> optionalAccount = accountRepository.findById(newAccount.getAccountNumber());
		if (optionalAccount.isPresent()) {
			Account oldAccount = optionalAccount.get();
			accountMapper(oldAccount, newAccount);
			return accountRepository.save(oldAccount);
		} else {
			return new Account();
		}
	}

	private static void accountMapper(Account oldAccount, Account newAccount) {
		oldAccount.setBalance(newAccount.getBalance());
		oldAccount.setHolderName(newAccount.getHolderName());
	}

	public String deleteAccountById(int id) {
		accountRepository.deleteById(id);
		return "Account successfully deleted with id = " + id;
	}

	/**
	 * this method initializes the accounts at the start of application
	 * 
	 * @return List<Account>
	 * 
	 */
	@EventListener(ApplicationReadyEvent.class)
	public List<Account> initAccounts() {

		List<Account> savedAccounts = null;

		try {
			entityManager.clear();
			accountRepository.deleteAll();

			savedAccounts = accountRepository
					.saveAll(convertJsonToAccounts(readJsonFromFile("classpath:input/init_accounts.json")));

			savedAccounts.forEach(p -> logger.info("added to Database | AccountNumber ::: " + p.getAccountNumber()
					+ " | Name :: " + p.getHolderName()));

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return savedAccounts;
	}

	/**
	 * this method initializes the ATM at the start of application
	 * 
	 * @return List<Account>
	 * 
	 */
	@EventListener(ApplicationReadyEvent.class)
	public List<ATM> initATM() {
		return null;
	}
}
