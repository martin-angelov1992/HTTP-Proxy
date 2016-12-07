package tests;

import java.net.Socket;

public class SocketMock extends Socket {

	private boolean closed = false;

	@Override
	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}
}