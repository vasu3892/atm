package com.atm.machine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.atm.machine.entity.ATM;
import com.atm.machine.service.ATMService;

@RestController
public class ATMController {
	@Autowired
	private ATMService atmService;

	@GetMapping("/getATM")
	public ATM getATM() {
		return atmService.getATM();
	}

	@PutMapping("/updateATM")
	public ATM updateAccount(@RequestBody ATM atm) {
		return atmService.updateATM(atm);
	}

	@PostMapping("/initATM")
	public ATM initATM() {
		return atmService.initATM();
	}
}
