package com.atm.machine.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atm.machine.entity.ATM;

public interface ATMRepository extends JpaRepository<ATM, Integer> {

}
