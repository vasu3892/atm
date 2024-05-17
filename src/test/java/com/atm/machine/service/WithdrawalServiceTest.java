package com.atm.machine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atm.machine.entity.ATM;
import com.atm.machine.entity.Account;
import com.atm.machine.entity.Withdrawal;

@ExtendWith(MockitoExtension.class)
public class WithdrawalServiceTest {

	@InjectMocks
	private WithdrawalService withdrawalService = new WithdrawalService();

	@Mock
	private AccountService accountService;

	@Mock
	private ATMService atmService;

	private Withdrawal withdrawal;

	private Account account;

	private ATM atm;

	@BeforeEach
	void setup() {
		atm = new ATM();
		atm.setAtmId(101);
		atm.setDenominationTwoThousands(10);
		atm.setDenominationFiveHundreds(10);
		atm.setDenominationTwoHundreds(10);
		atm.setDenominationHundreds(10);

		account = new Account();
		account.setAccountNumber(123);
		account.setBalance(new BigDecimal(70000));
		account.setHolderName("John Doe");

		withdrawal = new Withdrawal();
		withdrawal.setAmount(1545);
		withdrawal.setAccount(account);
	}

	@Test
	void testWithdrawalSuccess1() {

		when(accountService.getAccountById(anyInt())).thenReturn(account);
		when(atmService.getATM()).thenReturn(atm);
		when(atmService.updateATM(any())).thenReturn(atm);
		when(accountService.updateAccount(any())).thenReturn(account);

		withdrawal.setAmount(7000);

		withdrawal = withdrawalService.withDraw(withdrawal);

		assertEquals("SUCCESS", withdrawal.getResponseMessage());

		assertEquals(3, withdrawal.getDenominationMap().get(2000));
		assertEquals(2, withdrawal.getDenominationMap().get(500));
		assertEquals(null, withdrawal.getDenominationMap().get(200));
		assertEquals(null, withdrawal.getDenominationMap().get(100));

		assertEquals(7000, withdrawal.getAmount());

		assertEquals(123, withdrawal.getAccount().getAccountNumber());
		assertEquals("John Doe", withdrawal.getAccount().getHolderName());
		assertEquals("63000", withdrawal.getAccount().getBalance().toString());
	}

	@Test
	void testWithdrawalLowAccountBalance() {

		when(accountService.getAccountById(anyInt())).thenReturn(account);

		withdrawal.setAmount(7000);
		withdrawal.getAccount().setBalance(new BigDecimal(6999.99));

		withdrawal = withdrawalService.withDraw(withdrawal);

		assertEquals("Account balance is low | accountBalance = 6999.989999999999781721271574497222900390625",
				withdrawal.getResponseMessage());

		assertEquals(null, withdrawal.getDenominationMap());

		assertEquals(7000, withdrawal.getAmount());

		assertEquals(123, withdrawal.getAccount().getAccountNumber());
		assertEquals("John Doe", withdrawal.getAccount().getHolderName());
		assertEquals("6999.989999999999781721271574497222900390625", withdrawal.getAccount().getBalance().toString());
	}

	@Test
	void testWithdrawalInsufficientCurrencyATM() {

		when(accountService.getAccountById(anyInt())).thenReturn(account);
		when(atmService.getATM()).thenReturn(atm);

		withdrawal.setAmount(7000);
		atm.setDenominationTwoThousands(10);
		atm.setDenominationFiveHundreds(1);
		atm.setDenominationTwoHundreds(1);
		atm.setDenominationHundreds(1);

		withdrawal = withdrawalService.withDraw(withdrawal);

		assertEquals("ATM currency is low", withdrawal.getResponseMessage());

		assertEquals(3, withdrawal.getDenominationMap().get(2000));
		assertEquals(1, withdrawal.getDenominationMap().get(500));
		assertEquals(1, withdrawal.getDenominationMap().get(200));
		assertEquals(1, withdrawal.getDenominationMap().get(100));

		assertEquals(7000, withdrawal.getAmount());

		assertEquals(123, withdrawal.getAccount().getAccountNumber());
		assertEquals("John Doe", withdrawal.getAccount().getHolderName());
		assertEquals("70000", withdrawal.getAccount().getBalance().toString());
	}

	@Test
	void testWithdrawalInvalidAmount() {

		withdrawal = withdrawalService.withDraw(withdrawal);

		assertEquals("Amount should be a multiple of 100 | Invalid amount requested = 1545",
				withdrawal.getResponseMessage());
	}

	@Test
	void testWithdrawalOverLimitAmount() {

		withdrawal.setAmount(90000);
		withdrawal = withdrawalService.withDraw(withdrawal);

		assertEquals("Amount should be less than or equal to 50000 | amount requested = 90000",
				withdrawal.getResponseMessage());
	}

	@Test
	void testWithdrawalLowATMBalance() {

		when(accountService.getAccountById(anyInt())).thenReturn(account);
		when(atmService.getATM()).thenReturn(atm);

		withdrawal.setAmount(50000);

		withdrawal = withdrawalService.withDraw(withdrawal);

		assertEquals("ATM balance is low | atmBalance = 28000", withdrawal.getResponseMessage());

		assertEquals(null, withdrawal.getDenominationMap());

		assertEquals(50000, withdrawal.getAmount());

		assertEquals(123, withdrawal.getAccount().getAccountNumber());
		assertEquals("John Doe", withdrawal.getAccount().getHolderName());
		assertEquals("70000", withdrawal.getAccount().getBalance().toString());
	}

}
