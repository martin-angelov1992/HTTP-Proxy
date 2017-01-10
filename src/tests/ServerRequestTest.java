package tests;

import static org.powermock.api.mockito.PowerMockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Formatter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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

	private static final String TEST_RESPONSE_CHUNKED = "HTTP/1.1 200 OK\r\n"+
"Date: Mon, 22 Mar 2004 11:15:03 GMT\r\n"+
"Content-Type: text/html\r\n"+
"Transfer-Encoding: chunked\r\n"+
"Trailer: Expires\r\n\r\n"+

"4\r\n"+
"Wiki\r\n"+
"5\r\n"+
"pedia\r\n"+
"E\r\n"+
" in\r\n"+
"\r\n"+
"chunks.\r\n"+
"0\r\n"+
"\r\n";

	private static final String TEST_RESPONSE_LENGHTED = "HTTP/1.1 200 OK\r\n"+
"Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n"+
"Server: Apache/2.2.14 (Win32)\r\n"+
"Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n"+
"Content-Length: 88\r\n"+
"Content-Type: text/html\r\n\r\n"+
"<html>\r\n"+
"   <body>\r\n"+
"\r\n"+
"   <h1>Hello, World!</h1>\r\n"+
"\r\n"+
"   </body>\r\n"+
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

	@Before
	public void prepare() throws Exception {
		when(userRequestMock.getHost()).thenReturn(TEST_HOST);
		when(userRequestMock.getRequestRaw()).thenReturn(TEST_REQUEST);
		PowerMockito.whenNew(Socket.class).withArguments(TEST_HOST, HTTP_PORT).thenReturn(socketMock);
		PowerMockito.whenNew(Formatter.class).withParameterTypes(OutputStream.class)
		.withArguments(null).thenReturn(formatterMock);
		when(socketMock.getInputStream()).thenReturn(inputStreamMock);
	}

	@Test
	public void testSend() throws Exception {
		when(inputStreamMock.read()).then(new DataLetterByLetter(TEST_RESPONSE));

		ServerRequest toTest = new ServerRequest(userRequestMock);

		MemberModifier.field(ServerRequest.class, "readingUtil").set(
				toTest , readingUtil);

		ServerResponse response = toTest.send();

		Assert.assertEquals(response.getResponseRaw(), TEST_RESPONSE_EXPECTED);
	}

	@Test
	public void testSendWithChunkedResponse() throws IOException, IllegalArgumentException, IllegalAccessException {
		when(inputStreamMock.read()).then(new DataLetterByLetter(TEST_RESPONSE_CHUNKED));

		ServerRequest toTest = new ServerRequest(userRequestMock);

		MemberModifier.field(ServerRequest.class, "readingUtil").set(
				toTest , readingUtil);

		ServerResponse response = toTest.send();

		Assert.assertEquals(response.getResponseRaw(), TEST_RESPONSE_CHUNKED);
	}

	@Test
	public void testSendWithLenghtedResponse() throws IOException, IllegalArgumentException, IllegalAccessException {
		when(inputStreamMock.read()).then(new DataLetterByLetter(TEST_RESPONSE_LENGHTED));

		ServerRequest toTest = new ServerRequest(userRequestMock);

		MemberModifier.field(ServerRequest.class, "readingUtil").set(
				toTest , readingUtil);

		ServerResponse response = toTest.send();

		Assert.assertEquals(response.getResponseRaw(), TEST_RESPONSE_LENGHTED);
	}
}