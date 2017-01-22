package com.martin.httpproxy.tests;

import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.inject.Injector;
import com.martin.httpproxy.ReadingUtil;
import com.martin.httpproxy.UserRequest;

import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Scanner.class, UserRequest.class, ReadingUtil.class})
public class UserRequestTest {
	private static final String TEST_REQUEST = 
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

	public static final String EXPECTED_REQUEST_RAW = "GET /tutorials/other/top-20-mysql-best-practices/ HTTP/1.1\r\n"+
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

	@Mock
	private Scanner in;

	@Mock
	private OutputStream out;

	@Mock
	private Socket serverSocket;

	@Mock
	private Injector injector;

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
	public void testRead() throws IllegalArgumentException, IllegalAccessException {
		when(in.hasNextLine()).thenReturn(true);
		when(in.nextLine()).then(new DataLineByLine(TEST_REQUEST));

		UserRequest request = new UserRequest(in, out, serverSocket, injector);

		MemberModifier.field(UserRequest.class, "readingUtil").set(
				request, new ReadingUtil());

		request.readRequest();

		Assert.assertEquals("net.tutsplus.com", request.getHost());
		Assert.assertEquals("GET", request.getMethod());
		Assert.assertEquals("/tutorials/other/top-20-mysql-best-practices/", request.getQuery());
		Assert.assertEquals(EXPECTED_REQUEST_RAW, request.getRequestRaw());
	}
}