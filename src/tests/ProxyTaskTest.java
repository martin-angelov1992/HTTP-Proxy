package tests;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.inject.Injector;

import proxyserver5.Connection;
import proxyserver5.ProxyTask;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServerSocket.class, Executors.class, ProxyTask.class, ExecutorService.class})
public class ProxyTaskTest {

	private static final int PORT = 1234;

	@Mock
	private Injector injector;
	private final ProxyTask toTest = new ProxyTask(injector);

	@Mock
	private Socket socketMock;

	@Mock
	private ThreadPoolExecutor executorServiceMock;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSubmittingConnections() throws Exception {
		mockStatic(Executors.class);
		Mockito.when(Executors.newFixedThreadPool(any(Integer.class)))
		.thenReturn(executorServiceMock);
		ServerSocketMock serverSocketMock = new ServerSocketMock();
		whenNew(ServerSocket.class).withAnyArguments().thenReturn(serverSocketMock);

		StarterThread starterThread = new StarterThread();
		starterThread.start();

		while (serverSocketMock.getAcceptedCount() != 3) {
			Thread.sleep(100);
		}

		starterThread.interrupt();
		Exception e = starterThread.getException();

		if (e != null) {
			throw e;
		}

		verify(executorServiceMock, times(3)).submit(any(Connection.class));
	}

	private class StarterThread extends Thread {

		private volatile Exception exception;

		@Override
		public void run() {
			try {
				toTest.start(PORT);
			} catch (Exception e) {
				exception = e;
			}
		}

		public Exception getException() {
			return exception;
		}
	}

	private class ServerSocketMock extends ServerSocket {
		private volatile int acceptedCount = 0;

		public ServerSocketMock() throws IOException {
			super();
		}

		public int getAcceptedCount() {
			return acceptedCount;
		}

		@Override
		public Socket accept() throws IOException {
			// Test with 3 connections only
			if (acceptedCount == 3) {
				try {
					synchronized(this) {
						wait();
					}
				} catch (InterruptedException e) {
					throw new RuntimeException("This thread got unexpectedly interrupted.");
				}
			}
			++acceptedCount;
			return socketMock;
		}
	}
}