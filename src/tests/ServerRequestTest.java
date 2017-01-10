package tests;

import static org.powermock.api.mockito.PowerMockito.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import proxyserver5.ReadingUtil;
import proxyserver5.ServerRequest;
import proxyserver5.ServerResponse;
import proxyserver5.UserRequest;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Socket.class, UserRequest.class, ServerRequest.class, Formatter.class})
public class ServerRequestTest {

	private static final String TEST_HOST = "google.com";

	private static final String TEST_REQUEST = "GET /hello.htm HTTP/1.1\\r\\n"
			+ "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\\r\\n" + "Host: google.com\\r\\n"
			+ "Accept-Language: en-us" + "Accept-Encoding: gzip, deflate\\r\\n" + "Connection: Keep-Alive";

	private static final String TEST_RESPONSE = "HTTP/1.1 200 OK\r\n"+
"Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n"+
"Server: Apache/2.2.14 (Win32)\r\n"+
"Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n"+
"Content-Length: 88\r\n"+
"Content-Type: text/html\r\n"+
"Connection: Closed\r\n\r\n"+
"<html>"+
"<body>"+
"<h1>Hello, World!</h1>"+
"</body>"+
"</html>";

	private static final String TEST_RESPONSE_EXPECTED = "HTTP/1.1 200 OK\r\n"+
"Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n"+
"Server: Apache/2.2.14 (Win32)\r\n"+
"Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n"+
"Content-Length: 88\r\n"+
"Content-Type: text/html\r\n\r\n"+
"<html>"+
"<body>"+
"<h1>Hello, World!</h1>"+
"</body>"+
"</html>";

	private static final int HTTP_PORT = 80;

	@Mock
	private UserRequest userRequestMock;

	@Mock
	private Socket socketMock;

	@Mock
	private Formatter formatterMock;

	@Mock
	private InputStream inputStreamMock;

	private ReadingUtil readingUtil = new ReadingUtil();

	@Test
	public void testSend() throws Exception {
		when(userRequestMock.getHost()).thenReturn(TEST_HOST);
		when(userRequestMock.getRequestRaw()).thenReturn(TEST_REQUEST);
		when(socketMock.getInputStream()).thenReturn(inputStreamMock);
		when(inputStreamMock.read()).then(new DataLetterByLetter(TEST_RESPONSE));

		PowerMockito.whenNew(Socket.class).withArguments(TEST_HOST, HTTP_PORT).thenReturn(socketMock);
		PowerMockito.whenNew(Formatter.class).withParameterTypes(OutputStream.class)
		.withArguments(null).thenReturn(formatterMock);

		ServerRequest toTest = new ServerRequest(userRequestMock);

		MemberModifier.field(ServerRequest.class, "readingUtil").set(
				toTest , readingUtil);

		ServerResponse response = toTest.send();

		Assert.assertEquals(response.getResponseRaw(), TEST_RESPONSE_EXPECTED);
	}
}