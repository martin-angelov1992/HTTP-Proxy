package tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import proxyserver5.ReadingUtil;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ReadingUtil.class)
public class ReadingUtilTest {

	private ReadingUtil toTest = new ReadingUtil();

	private static final String TEST_HEADER = 
"GET /tutorials/other/top-20-mysql-best-practices/ HTTP/1.1\r\n"+
"Host: net.tutsplus.com\r\n"+
"User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)\r\n"+
"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n"+
"Accept-Language: en-us,en;q=0.5\r\n"+
"Accept-Encoding: gzip,deflate\r\n"+
"Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n"+
"Keep-Alive: 300\r\n"+
"Connection: keep-alive\r\n"+
"Cookie: PHPSESSID=r2t5uvjq435r4q7ib3vtdjq120\r\n"+
"Pragma: no-cache\r\n"+
"Cache-Control: no-cache\r\n\r\n";

	private static final String EXPECTED_HEADER_STR = "GET /tutorials/other/top-20-mysql-best-practices/ HTTP/1.1\r\n"+
"Host: net.tutsplus.com\r\n"+
"User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)\r\n"+
"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n"+
"Accept-Language: en-us,en;q=0.5\r\n"+
"Accept-Encoding: gzip,deflate\r\n"+
"Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n"+
"Keep-Alive: 300\r\n"+
"Cookie: PHPSESSID=r2t5uvjq435r4q7ib3vtdjq120\r\n"+
"Pragma: no-cache\r\n"+
"Cache-Control: no-cache\r\n\r\n";

	private static final Map<String, String> EXPECTED_HEADERS = new HashMap<>();

	static {
		EXPECTED_HEADERS.put("Host", "net.tutsplus.com");
		EXPECTED_HEADERS.put("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)");
		EXPECTED_HEADERS.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		EXPECTED_HEADERS.put("Accept-Language", "en-us,en;q=0.5");
		EXPECTED_HEADERS.put("Accept-Encoding", "gzip,deflate");
		EXPECTED_HEADERS.put("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		EXPECTED_HEADERS.put("Keep-Alive", "300");
		EXPECTED_HEADERS.put("Cookie", "PHPSESSID=r2t5uvjq435r4q7ib3vtdjq120");
		EXPECTED_HEADERS.put("Pragma", "no-cache");
		EXPECTED_HEADERS.put("Cache-Control", "no-cache");
	}

	@Test
	public void testReadHeadersWithInputStream() throws IOException {
		Map<String, String> headers = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		InputStream in = mock(InputStream.class);
		when(in.read()).then(new DataLetterByLetter(TEST_HEADER));
		toTest.readHeaders(sb, in, headers);
		assertEquals(EXPECTED_HEADER_STR, sb.toString());
		assertEquals(EXPECTED_HEADERS, headers);
	}

	@Test
	public void testReadHeadersWithScanner() {
		Map<String, String> headers = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		Scanner in = mock(Scanner.class);
		when(in.nextLine()).then(new HeaderLineByLine());
		toTest.readHeaders(sb, in, headers);
		assertEquals(EXPECTED_HEADER_STR, sb.toString());
		assertEquals(EXPECTED_HEADERS, headers);
	}

	@Test
	public void testReadLineFromStream() throws IOException {
		InputStream in = mock(InputStream.class);
		when(in.read()).then(new DataLetterByLetter(TEST_HEADER));
		String line = toTest.readLineFromStream(in);
		assertEquals("GET /tutorials/other/top-20-mysql-best-practices/ HTTP/1.1\r\n", line);
	}

	private class HeaderLineByLine implements Answer<String> {
		private List<String> lines;
		private Iterator<String> it;

		public HeaderLineByLine() {
			String[] splitted = TEST_HEADER.split("\\r\\n", -1);
			lines = Arrays.asList(splitted);
			it = lines.iterator();
		}

		@Override
		public String answer(InvocationOnMock invocation) throws Throwable {
			return it.next();
		}
	}
}