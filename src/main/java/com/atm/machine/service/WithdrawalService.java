package com.atm.machine.service;

import static com.atm.machine.util.ATMUtils.getCountByDenomination;

import java.math.BigDecimal;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atm.machine.entity.ATM;
import com.atm.machine.entity.Account;
import com.atm.machine.entity.Withdrawal;

@Service
public class WithdrawalService {

	Logger logger = LoggerFactory.getLogger(WithdrawalService.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private ATMService atmService;

	public Withdrawal withDraw(Withdrawal withdrawal) {

		int accountNumber = withdrawal.getAccount().getAccountNumber();
		int amount = withdrawal.getAmount();

		Account account = accountService.getAccountById(accountNumber);
		withdrawal.setAccount(account);

		String responseMessage = null;

		// validate amount
		if (amount < 100 || amount % 100 != 0) {
			responseMessage = "Amount should be a multiple of 100 | Invalid amount requested = " + amount;
			logger.info(responseMessage);
			withdrawal.setResponseMessage(responseMessage);
			return withdrawal;
		} else if (amount > 50000) {
			responseMessage = "Amount should be less than or equal to 50000 | amount requested = " + amount;
			logger.info(responseMessage);
			withdrawal.setResponseMessage(responseMessage);
			return withdrawal;
		}

		// validate accountNumber
		if (account == null) {
			responseMessage = "Account not found | accountNumber = " + accountNumber;
			logger.info(responseMessage);
			withdrawal.setResponseMessage(responseMessage);
			return withdrawal;
		}

		// validate account balance
		else if (account.getBalance().compareTo(new BigDecimal(amount)) < 0) {
			responseMessage = "Account balance is low | accountBalance = " + account.getBalance();
			logger.info(responseMessage);
			withdrawal.setResponseMessage(responseMessage);
			return withdrawal;
		}

		// validate ATM balance
		ATM atm = atmService.getATM();
		long atmBalance = (long) (atm.getDenominationHundreds() * 100 + atm.getDenominationTwoHundreds() * 200
				+ atm.getDenominationFiveHundreds() * 500 + atm.getDenominationTwoThousands() * 2000);

		if (atmBalance < amount) {
			responseMessage = "ATM balance is low | atmBalance = " + atmBalance;
			logger.info(responseMessage);
			withdrawal.setResponseMessage(responseMessage);
			return withdrawal;
		}

		else {
			// calculate denominations
			int updatedAmount = amount;
			HashMap<Integer, Integer> denominationMap = new HashMap<Integer, Integer>();
			
			if (atm.getDenominationTwoThousands() > 0) 
				updatedAmount = getCountByDenomination(updatedAmount, Integer.valueOf(2000), denominationMap);
			
			if (atm.getDenominationFiveHundreds() > 0) 
				updatedAmount = getCountByDenomination(updatedAmount, Integer.valueOf(500), denominationMap);
			
			if (atm.getDenominationTwoHundreds() > 0) 
				updatedAmount = getCountByDenomination(updatedAmount, Integer.valueOf(200), denominationMap);
			
			if (atm.getDenominationHundreds() > 0) 
				updatedAmount = getCountByDenomination(updatedAmount, Integer.valueOf(100), denominationMap);
			
			int finalAmount = (denominationMap.get(2000) != null ? denominationMap.get(2000) : 0)  * 2000 
							+ (denominationMap.get(500) != null ? denominationMap.get(500) : 0)  * 500
							+ (denominationMap.get(200) != null ? denominationMap.get(200) : 0)  * 200
							+ (denominationMap.get(100) != null ? denominationMap.get(100) : 0)  * 100;

			if(finalAmount != amount) {
				responseMessage = "ATM currency is low | atmBalance = " + atmBalance;
				logger.info(responseMessage);
				withdrawal.setResponseMessage(responseMessage);
				withdrawal.setAtm(atm);
				return withdrawal;
			}
			
			// update ATM
			atm.setDenominationTwoThousands(atm.getDenominationTwoThousands() - (denominationMap.get(2000) != null ? denominationMap.get(2000) : 0));
			atm.setDenominationFiveHundreds(atm.getDenominationFiveHundreds() - (denominationMap.get(500) != null ? denominationMap.get(500) : 0));
			atm.setDenominationTwoHundreds(atm.getDenominationTwoHundreds() - (denominationMap.get(200) != null ? denominationMap.get(200) : 0));
			atm.setDenominationHundreds(atm.getDenominationHundreds() - (denominationMap.get(100) != null ? denominationMap.get(100) : 0));
			atm = atmService.updateATM(atm);

			// update new account balance
			account.setBalance(account.getBalance().subtract(new BigDecimal(finalAmount)));
			account = accountService.updateAccount(account);

			// prepare final response
			withdrawal.setDenominationMap(denominationMap);
			withdrawal.setAmount(finalAmount);
			withdrawal.setAccount(account);
			withdrawal.setAtm(atm);
			withdrawal.setResponseMessage("SUCCESS");

			logger.info("SUCCESS | " + withdrawal.getAccount().getAccountNumber() + " | "
					+ withdrawal.getDenominationMap().toString() + " | " + withdrawal.getAmount());

		}

		return withdrawal;
	}

}
