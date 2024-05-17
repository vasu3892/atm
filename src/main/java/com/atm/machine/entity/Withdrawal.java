package com.atm.machine.entity;

import java.util.concurrent.ConcurrentHashMap;

public class Withdrawal {

	private ATM atm;
	private Account account;
	private int amount;
	private ConcurrentHashMap<Integer, Integer> denominationMap;
	private String responseMessage;

	public ATM getAtm() {
		return atm;
	}

	public void setAtm(ATM atm) {
		this.atm = atm;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public ConcurrentHashMap<Integer, Integer> getDenominationMap() {
		return denominationMap;
	}

	public void setDenominationMap(ConcurrentHashMap<Integer, Integer> denominationMap) {
		this.denominationMap = denominationMap;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
}
