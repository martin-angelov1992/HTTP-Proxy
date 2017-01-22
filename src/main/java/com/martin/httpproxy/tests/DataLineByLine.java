package com.martin.httpproxy.tests;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class DataLineByLine implements Answer<String> {
	private List<String> lines;
	private Iterator<String> it;

	public DataLineByLine(String data) {
		String[] splitted = data.split("\\r\\n", -1);
		lines = Arrays.asList(splitted);
		it = lines.iterator();
	}

	@Override
	public String answer(InvocationOnMock invocation) throws Throwable {
		return it.next();
	}
}