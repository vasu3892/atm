package com.atm.machine.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class ATM {

	@Id
	@GeneratedValue
	private int atmId;

	private int denominationHundreds;

	private int denominationTwoHundreds;

	private int denominationFiveHundreds;

	private int denominationTwoThousands;

	public int getAtmId() {
		return atmId;
	}

	public void setAtmId(int atmId) {
		this.atmId = atmId;
	}

	public int getDenominationHundreds() {
		return denominationHundreds;
	}

	public void setDenominationHundreds(int denominationHundreds) {
		this.denominationHundreds = denominationHundreds;
	}

	public int getDenominationTwoHundreds() {
		return denominationTwoHundreds;
	}

	public void setDenominationTwoHundreds(int denominationTwoHundreds) {
		this.denominationTwoHundreds = denominationTwoHundreds;
	}

	public int getDenominationFiveHundreds() {
		return denominationFiveHundreds;
	}

	public void setDenominationFiveHundreds(int denominationFiveHundreds) {
		this.denominationFiveHundreds = denominationFiveHundreds;
	}

	public int getDenominationTwoThousands() {
		return denominationTwoThousands;
	}

	public void setDenominationTwoThousands(int denominationTwoThousands) {
		this.denominationTwoThousands = denominationTwoThousands;
	}

}
