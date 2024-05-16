package com.atm.machine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.atm.machine.entity.Withdrawal;
import com.atm.machine.service.WithdrawalService;

@RestController
public class WithdrawalController {

	@Autowired
	private WithdrawalService withdrawalService;

	@PutMapping("/withDraw")
	public Withdrawal updateAccount(@RequestBody Withdrawal withdrawal) {
		return withdrawalService.withDraw(withdrawal);
	}
}
