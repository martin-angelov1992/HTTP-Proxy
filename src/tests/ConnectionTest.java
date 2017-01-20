package tests;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.inject.Injector;

import proxyserver5.Connection;
import proxyserver5.ServerResponse;
import proxyserver5.UserRequest;
import proxyserver5.UserResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Connection.class, UserRequest.class})
public class ConnectionTest {
	@Mock
	private Socket socketMock;
	private Map<String, Integer> requests = new HashMap<>();

	@Mock
	private Scanner scannerMock;

	@Mock
	private UserRequest userRequest;

	@Mock
	private OutputStream outputStreamMock;

	private static Injector injector = PowerMockito.mock(Injector.class);

	private static final String SITE_1 = "google.com";
	private static final String SITE_2 = "youtube.com";
	private static final int SITE_1_REQUESTS_COUNT = 1;
	private static final int SITE_2_REQUESTS_COUNT = 2;

	private static final int CONNECTION_COUNT = 3;

	@Test
	public void test() throws Exception {
		PowerMockito.whenNew(UserRequest.class).withArguments(scannerMock, outputStreamMock, null, injector).then(e -> {
			return new UserRequestMock(scannerMock, outputStreamMock, null);
		});
		PowerMockito.whenNew(UserResponse.class).withAnyArguments().thenReturn(new UserResponseMock(null, null));
		PowerMockito.whenNew(Scanner.class).withParameterTypes(InputStream.class).withArguments(null)
				.thenReturn(scannerMock);
		PowerMockito.when(socketMock.isClosed()).then(new TimeoutInASecond());
		PowerMockito.when(socketMock.getOutputStream()).thenReturn(outputStreamMock);

		// Test with specific number of connections
		for (int i = 0; i < CONNECTION_COUNT; ++i) {
			Connection toTest = new Connection(socketMock, requests, injector);
			toTest.setSocket(socketMock);
			toTest.call();
		}

		verifyRequestsMap();
	}

	private void verifyRequestsMap() {
		Assert.assertEquals(requests.get(SITE_1).intValue(), SITE_1_REQUESTS_COUNT);
		Assert.assertEquals(requests.get(SITE_2).intValue(), SITE_2_REQUESTS_COUNT);
	}

	private static class TimeoutInASecond implements Answer<Boolean> {

		private long timeout;

		public TimeoutInASecond() {
			timeout = System.currentTimeMillis() + 1000;
		}

		@Override
		public Boolean answer(InvocationOnMock invocation) throws Throwable {
			return System.currentTimeMillis() > timeout;
		}
	}

	private static class UserRequestMock extends UserRequest {

		private static final Queue<String> hosts = new LinkedList<>();
		static {
			hosts.add(SITE_2);
			hosts.add(SITE_1);
			hosts.add(SITE_2);
		}

		private String host = null;

		public UserRequestMock(Scanner in, OutputStream out, Socket serverSocket) {
			super(in, out, serverSocket, injector);
			host = hosts.poll();
		}

		@Override
		public String getHost() {
			return host;
		}

		@Override
		public ServerResponse send() {
			// Do not send anything, return dummy response
			return new ServerResponse("", new HashMap<>(), "HTTP/1.1 200 OK");
		}

		@Override
		public void readRequest() {
		}
	}

	private class UserResponseMock extends UserResponse {

		public UserResponseMock(ServerResponse serverResponse, OutputStream out) {
			super(serverResponse, out);
		}

		@Override
		public void send() {
			// Do not send anything
		}
	}
}