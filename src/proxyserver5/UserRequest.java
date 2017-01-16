/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proxyserver5;

import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import proxyserver5.cache.ProxyCache;

/**
 *
 * @author ASUS
 */
public class UserRequest {
	static Logger logger = LoggerFactory.getLogger(UserRequest.class.getName());

    private Scanner in;
    private OutputStream out;
    private String requestRaw;
    private String host;
    private String method;
    private String query;
    private Map<String, String> headers;
    private Socket serverSocket;
    @Inject
    private ReadingUtil readingUtil;
    @Inject
    private ProxyCache cache;

    public UserRequest(Scanner in, OutputStream out, Socket serverSocket) {
        setOut(out);
        setIn(in);
        setServerSocket(serverSocket);
        headers = new HashMap<>();
        requestRaw = "";
        logger.debug("<reading user request>");
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Socket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Scanner getIn() {
        return in;
    }

    public void setIn(Scanner in) {
        this.in = in;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void readRequest() {
        logger.debug("<checking for next line>");
        if(!in.hasNextLine()) {
            logger.debug("<no next line>");
            return;
        }
        logger.debug("<reading first line>");
        String firstLine = in.nextLine();
        logger.debug("<read first line>");
        requestRaw += firstLine+"\r\n";
        String[] parts = firstLine.split(" ");
        if(parts.length < 2) {
            return;
        }
        method = parts[0];
        query = parts[1];
        StringBuilder sb = new StringBuilder();
        sb.append(requestRaw);
        readingUtil.readHeaders(sb, in, headers);
        requestRaw = sb.toString();
        if(headers.get("Host") != null) {
            host = headers.get("Host");
        }
    }

    public String getRequestRaw() {
        return requestRaw;
    }

    public void setRequestRaw(String requestRaw) {
        this.requestRaw = requestRaw;
    }
    public ServerResponse send() {
        ServerResponse cachedResponse = cache.tryAnswerFromCache(this);

        if (cachedResponse != null) {
        	return cachedResponse;
        }

        ServerRequest serverRequest = new ServerRequest(this);

        ServerResponse response = serverRequest.send();

        if ("GET".equals(getMethod())) {
        	cache.tryCache(response, getQuery());
        }

        return response;
    }
}