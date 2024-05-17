package com.atm.machine.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.expression.ParseException;
import org.springframework.util.ResourceUtils;

import com.atm.machine.entity.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class ATMUtils {

	public static String readJsonFromFile(String filePath) throws IOException, ParseException {

		File file = ResourceUtils.getFile(filePath);
		return new String(Files.readAllBytes(file.toPath()));
	}

	public static List<Account> convertJsonToAccounts(String JsonString)
			throws JsonMappingException, JsonProcessingException {
		ObjectMapper om = new ObjectMapper();
		CollectionType typeReference = TypeFactory.defaultInstance().constructCollectionType(List.class, Account.class);
		List<Account> accounts = om.readValue(JsonString, typeReference);
		return accounts;
	}

	public static int getCountByDenomination(int updatedAmount, Integer denomination,
			ConcurrentHashMap<Integer, Integer> denominationMap, int atmCount) {
		int count = updatedAmount >= denomination ? updatedAmount / denomination : 0;
		if (atmCount <= 0) {
			return updatedAmount;
		} else if (atmCount < count) {
			denominationMap.put(denomination, atmCount);
			return updatedAmount - atmCount * denomination;
		} else {
			denominationMap.put(denomination, count);
			return updatedAmount - count * denomination;
		}
	}
}
