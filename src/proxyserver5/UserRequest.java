/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proxyserver5;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author ASUS
 */
public class UserRequest {
    private Scanner in;
    private OutputStream out;
    private String requestRaw;
    private String host;
    private String method;
    private String query;
    private HashMap<String, String> headers;
    private Socket serverSocket;

    public UserRequest(Scanner in, OutputStream out, Socket serverSocket) {
        setOut(out);
        setIn(in);
        setServerSocket(serverSocket);
        headers = new HashMap();
        requestRaw = "";
        Logger.logDebug("<reading user request>");
        readRequest();
        Logger.logDebug("<read user request>");
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
        Logger.logDebug("<checking for next line>");
        if(!in.hasNextLine()) {
            Logger.logDebug("<no next line>");
            return;
        }
        Logger.logDebug("<reading first line>");
        String firstLine = in.nextLine();
        Logger.logDebug("<read first line>");
        requestRaw += firstLine+"\r\n";
        String[] parts = firstLine.split(" ");
        if(parts.length < 2) {
            return;
        }
        method = parts[0];
        query = parts[1];
//        System.out.println("Reading user headers");
        StringBuilder sb = new StringBuilder();
        sb.append(requestRaw);
        Global.readHeaders(sb, in, headers);
        requestRaw = sb.toString();
//        System.out.println("Read user headers");
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
        ServerRequest serverRequest = new ServerRequest(this);
        return serverRequest.send();
    }
}
