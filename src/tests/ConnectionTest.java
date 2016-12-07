package tests;

import static org.mockito.Mockito.mock;

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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import maventest.com.martin.SomeClass;
import maventest.com.martin.SomeClassExt;
import proxyserver5.Connection;
import proxyserver5.ServerResponse;
import proxyserver5.UserRequest;
import proxyserver5.UserResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Connection.class})
public class ConnectionTest {
	private SocketMock socketMock = new SocketMock();
    private Map<String, Integer> requests = new HashMap<>();

	private Connection toTest = new Connection(socketMock, requests);

	private boolean doneFilling = false;

	private static final String SITE_1 = "google.com";
	private static final String SITE_2 = "youtube.com";
	private static final int SITE_1_REQUESTS_COUNT = 1;
	private static final int SITE_2_REQUESTS_COUNT = 2;

	@Test
	public void test() {
		// Close the socket in 1 second
		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(1000);
				// just in case
				while (!doneFilling) {}
				socketMock.setClosed(true);
			} catch (InterruptedException e) {}
		});

		mock(UserRequest.class);
		PowerMockito.whenNew(UserRequest.class).withNoArguments().thenReturn(UserRequestMock);

		thread.start();
		fillSocket();
		verifyRequestsMap();
		toTest.call();
		doneFilling = true;
	}

	private void verifyRequestsMap() {
		Assert.assertEquals(requests.get(SITE_1).intValue(), SITE_1_REQUESTS_COUNT);
		Assert.assertEquals(requests.get(SITE_2).intValue(), SITE_2_REQUESTS_COUNT);
	}

	private void fillSocket() {
		
	}

	private static class UserRequestMock extends UserRequest {

		private static final Queue<String> hosts = new LinkedList<>();
		static {
			hosts.add(SITE_2);
			hosts.add(SITE_1);
			hosts.add(SITE_2);
		}

		public UserRequestMock(Scanner in, OutputStream out, Socket serverSocket) {
			super(in, out, serverSocket);
			if (hosts.isEmpty()) {
				try {
					// just block as if they are no new requests from user
					this.wait();
				} catch (InterruptedException e) {}
			}
		}

		@Override
		public String getHost() {
			return hosts.poll();
		}

		@Override
		public ServerResponse send() {
			// Do not send anything, return dummy response
			return new ServerResponse("");
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