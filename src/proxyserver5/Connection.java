/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proxyserver5;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ASUS
 */
public class Connection implements Callable<Object> {
	private Socket socket;
	private Map<String, Integer> requests;
	private final int id;
	private static int counter = 0;

	static Logger logger = LoggerFactory.getLogger(Connection.class.getName());

	public Connection(Socket socket, Map<String, Integer> requests) {
		this.id = ++counter;
		setRequests(requests);
		setSocket(socket);
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public Object call() {
		OutputStream out;
		Scanner in;
		Socket serverSocket = null;
		try {
			if (!socket.isClosed()) {
				out = socket.getOutputStream();
				in = new Scanner(socket.getInputStream());
				logger.info("<parsing now {}>", id);
				UserRequest userRequest = new UserRequest(in, out, serverSocket);
				userRequest.readRequest();
		        logger.debug("<read user request>");
				String host = userRequest.getHost();
				synchronized (requests) {
					if (host != null) {
						Integer val = requests.get(host);
						if (val == null) {
							requests.put(host, 1);
						} else {
							requests.put(host, ++val);
						}
					}
				}
				ServerResponse serverResponse = userRequest.send();
				if (serverResponse == null) {
					in.close();
					out.close();
					socket.close();
					logger.error("<returning null {}>", id);
					return null;
				}
				UserResponse userResponse = new UserResponse(serverResponse, out);
				userResponse.send();
				socket.shutdownOutput();
				logger.info("<out {}>", id);
			}
			logger.info("<out of while {}>", id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Integer> getRequests() {
		return requests;
	}

	public void setRequests(Map<String, Integer> requests) {
		this.requests = requests;
	}
}
