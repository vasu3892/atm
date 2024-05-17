package com.atm.machine.service;

import static com.atm.machine.util.ATMUtils.getCountByDenomination;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atm.machine.entity.ATM;
import com.atm.machine.entity.Account;
import com.atm.machine.entity.Withdrawal;

@Service
public class WithdrawalService {

	private static final int DENOMINATION_TWO_THOUSAND = 2000;
	private static final int DENOMINATION_FIVE_HUNDRED = 500;
	private static final int DENOMINATION_TWO_HUNDRED = 200;
	private static final int DENOMINATION_HUNDRED = 100;
	private static final int MAX_WITHDRAWAL_LIMIT = 50000;

	Logger logger = LoggerFactory.getLogger(WithdrawalService.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private ATMService atmService;

	public synchronized Withdrawal withDraw(Withdrawal withdrawal) {

		try {
			int accountNumber = withdrawal.getAccount().getAccountNumber();
			int withdrawalAmount = withdrawal.getAmount();
			String responseMessage = null;

			Account account = accountService.getAccountById(accountNumber);
			withdrawal.setAccount(account);

			if (!validateAmount(withdrawalAmount, withdrawal, responseMessage)) {
				return withdrawal;
			}

			if (!validateAccount(accountNumber, withdrawalAmount, account, withdrawal, responseMessage)) {
				return withdrawal;
			}

			ATM atm = atmService.getATM();
			withdrawal.setAtm(atm);
			if (!validateATM(withdrawalAmount, withdrawal, responseMessage, atm)) {
				return withdrawal;
			}

			if (!validateDenominations(withdrawalAmount, withdrawal, responseMessage, atm)) {
				return withdrawal;
			}

			updateATM(atm, withdrawal);

			updateAccountBalance(account, withdrawal, withdrawalAmount);

			withdrawal.setResponseMessage("SUCCESS");

			logger.info("SUCCESS | " + withdrawal.getAccount().getAccountNumber() + " | "
					+ withdrawal.getDenominationMap().toString() + " | " + withdrawal.getAmount());
		} catch (Exception e) {
			logger.info(e.getMessage());
			withdrawal.setResponseMessage("FAILED | " + e.getMessage());
		}

		return withdrawal;
	}

	private void updateAccountBalance(Account account, Withdrawal withdrawal, int withdrawalAmount) {
		account.setBalance(account.getBalance().subtract(new BigDecimal(withdrawalAmount)));
		account = accountService.updateAccount(account);
		withdrawal.setAccount(account);
	}

	private void updateATM(ATM atm, Withdrawal withdrawal) {

		ConcurrentHashMap<Integer, Integer> denominationMap = withdrawal.getDenominationMap();

		atm.setDenominationTwoThousands(
				atm.getDenominationTwoThousands() - (denominationMap.get(DENOMINATION_TWO_THOUSAND) != null
						? denominationMap.get(DENOMINATION_TWO_THOUSAND)
						: 0));
		atm.setDenominationFiveHundreds(
				atm.getDenominationFiveHundreds() - (denominationMap.get(DENOMINATION_FIVE_HUNDRED) != null
						? denominationMap.get(DENOMINATION_FIVE_HUNDRED)
						: 0));
		atm.setDenominationTwoHundreds(atm.getDenominationTwoHundreds()
				- (denominationMap.get(DENOMINATION_TWO_HUNDRED) != null ? denominationMap.get(DENOMINATION_TWO_HUNDRED)
						: 0));
		atm.setDenominationHundreds(atm.getDenominationHundreds()
				- (denominationMap.get(DENOMINATION_HUNDRED) != null ? denominationMap.get(DENOMINATION_HUNDRED) : 0));
		atm = atmService.updateATM(atm);

		withdrawal.setAtm(atm);
	}

	private boolean validateDenominations(int amount, Withdrawal withdrawal, String responseMessage, ATM atm) {
		int updatedAmount = amount;
		ConcurrentHashMap<Integer, Integer> denominationMap = new ConcurrentHashMap<Integer, Integer>();

		if (atm.getDenominationTwoThousands() > 0)
			updatedAmount = getCountByDenomination(updatedAmount, Integer.valueOf(DENOMINATION_TWO_THOUSAND),
					denominationMap, atm.getDenominationTwoThousands());

		if (updatedAmount > 0 && atm.getDenominationFiveHundreds() > 0)
			updatedAmount = getCountByDenomination(updatedAmount, Integer.valueOf(DENOMINATION_FIVE_HUNDRED),
					denominationMap, atm.getDenominationFiveHundreds());

		if (updatedAmount > 0 && atm.getDenominationTwoHundreds() > 0)
			updatedAmount = getCountByDenomination(updatedAmount, Integer.valueOf(DENOMINATION_TWO_HUNDRED),
					denominationMap, atm.getDenominationTwoHundreds());

		if (updatedAmount > 0 && atm.getDenominationHundreds() > 0)
			updatedAmount = getCountByDenomination(updatedAmount, Integer.valueOf(DENOMINATION_HUNDRED),
					denominationMap, atm.getDenominationHundreds());

		withdrawal.setDenominationMap(denominationMap);

		int finalAmount = (denominationMap.get(DENOMINATION_TWO_THOUSAND) != null
				? denominationMap.get(DENOMINATION_TWO_THOUSAND)
				: 0) * DENOMINATION_TWO_THOUSAND

				+ (denominationMap.get(DENOMINATION_FIVE_HUNDRED) != null
						? denominationMap.get(DENOMINATION_FIVE_HUNDRED)
						: 0) * DENOMINATION_FIVE_HUNDRED

				+ (denominationMap.get(DENOMINATION_TWO_HUNDRED) != null ? denominationMap.get(DENOMINATION_TWO_HUNDRED)
						: 0) * DENOMINATION_TWO_HUNDRED

				+ (denominationMap.get(DENOMINATION_HUNDRED) != null ? denominationMap.get(DENOMINATION_HUNDRED) : 0)
						* DENOMINATION_HUNDRED;

		if (finalAmount != amount) {
			responseMessage = "ATM currency is low";
			logger.info(responseMessage);
			withdrawal.setResponseMessage(responseMessage);
			return false;
		}

		return true;
	}

	private boolean validateATM(int amount, Withdrawal withdrawal, String responseMessage, ATM atm) {
		long atmBalance = (long) (atm.getDenominationHundreds() * DENOMINATION_HUNDRED
				+ atm.getDenominationTwoHundreds() * DENOMINATION_TWO_HUNDRED
				+ atm.getDenominationFiveHundreds() * DENOMINATION_FIVE_HUNDRED
				+ atm.getDenominationTwoThousands() * DENOMINATION_TWO_THOUSAND);

		if (atmBalance < amount) {
			responseMessage = "ATM balance is low | atmBalance = " + atmBalance;
			logger.info(responseMessage);
			withdrawal.setResponseMessage(responseMessage);
			return false;
		}

		return true;
	}

	private boolean validateAccount(int accountNumber, int amount, Account account, Withdrawal withdrawal,
			String responseMessage) {
		if (account == null) {
			responseMessage = "Account not found | accountNumber = " + accountNumber;
			logger.info(responseMessage);
			withdrawal.setResponseMessage(responseMessage);
			return false;
		} else if (account.getBalance().compareTo(new BigDecimal(amount)) < 0) {
			responseMessage = "Account balance is low | accountBalance = " + account.getBalance();
			logger.info(responseMessage);
			withdrawal.setResponseMessage(responseMessage);
			return false;
		}

		return true;
	}

	private boolean validateAmount(int amount, Withdrawal withdrawal, String responseMessage) {
		if (amount < DENOMINATION_HUNDRED || amount % DENOMINATION_HUNDRED != 0) {
			responseMessage = "Amount should be a multiple of 100 | Invalid amount requested = " + amount;
			logger.info(responseMessage);
			withdrawal.setResponseMessage(responseMessage);
			return false;
		} else if (amount > MAX_WITHDRAWAL_LIMIT) {
			responseMessage = "Amount should be less than or equal to " + MAX_WITHDRAWAL_LIMIT
					+ " | amount requested = " + amount;
			logger.info(responseMessage);
			withdrawal.setResponseMessage(responseMessage);
			return false;
		}

		return true;
	}
}
