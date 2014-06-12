/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proxyserver5;

import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 *
 * @author ASUS
 */
public class Connection implements Callable{
    private Socket socket;
    private HashMap<String, Integer> requests;
    private final int id;
    private static int counter = 0;

    public Connection(Socket socket, HashMap<String, Integer> requests) {
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
    @Override
    public Object call() {
        OutputStream out;
        Scanner in;
        Socket serverSocket = null;
        try {
            while(!socket.isClosed()) {
//                f.format("\n<begin>");
                out = socket.getOutputStream();
                in = new Scanner(socket.getInputStream());
                Logger.logInfo("<parsing now %d>", id);
                UserRequest userRequest = new UserRequest(in, out, serverSocket);
                String host = userRequest.getHost();
                synchronized(requests) {
                    if(host != null) {
                        Integer val = requests.get(host);
                        if(val == null) {
                            requests.put(host, 1);
                        } else {
                            requests.put(host, ++val);
                        }
                    }
                }
                ServerResponse serverResponse = userRequest.send();
                //System.out.println(HexDump.dumpHexString(serverResponse.getResponseRaw()));
                if(serverResponse == null) {
                    in.close();
                    out.close();
                    socket.close();
                    Logger.logError("<returning null %d>", id);
                    return null;
                }
                UserResponse userResponse = new UserResponse(serverResponse, out);
                userResponse.send();
                socket.shutdownOutput();
                Logger.logInfo("<out %d>",id);
                break;
//                System.exit(0);
            }
            Logger.logInfo("<out of while "+id+">");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, Integer> getRequests() {
        return requests;
    }

    public void setRequests(HashMap<String, Integer> requests) {
        this.requests = requests;
    }
}
