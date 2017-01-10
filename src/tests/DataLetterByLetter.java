package tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class DataLetterByLetter implements Answer<Integer> {

	private List<Integer> characters;
	private Iterator<Integer> it;

	public DataLetterByLetter(String data) {
		String[] splitted = data.split("");
		characters = new ArrayList<>(splitted.length);

		for (String characterStr : splitted) {
			characters.add(Integer.valueOf(characterStr.charAt(0)));
		}

		it = characters.iterator();
	}

	@Override
	public Integer answer(InvocationOnMock invocation) throws Throwable {
		if (it.hasNext()) {
			return it.next();
		}

		return -1;
	}
}