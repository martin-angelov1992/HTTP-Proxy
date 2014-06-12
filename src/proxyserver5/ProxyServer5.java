/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proxyserver5;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author ASUS
 */
public class ProxyServer5 {

    /**
     * @param args the command line arguments
     */
    private static final int DEFAULT_PORT = 12345;
    private static final int START_THREADS = 10;
    public static void main(String[] args) throws IOException {
        ServerSocket sock = null;
        HashMap<String, Integer> requests = new HashMap();
        Runtime.getRuntime().addShutdownHook(new OnShutdown(requests));
        int port = 0;
        Logger.logInfo("Startting...");
        ExecutorService executorService = Executors.newFixedThreadPool(START_THREADS);
        if(args.length == 0) {
            Logger.logInfo("Using default port: %d\n", DEFAULT_PORT);
            port = DEFAULT_PORT;
        } else {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                die("Invalid port.");
            }
            if(port < 0) {
                die("Port can't be negative");
            }
        }
        try {
            sock = new ServerSocket(port);
            while(true) {
                Socket socket = sock.accept();
                Logger.logInfo("Got connection");
                executorService.submit(new Connection(socket, requests));
            }
        } catch (IOException e) {
            die(String.format("Port %d is already taken\n", port));
        } finally {
            sock.close();
            executorService.shutdownNow();
            //logger.finish();
        }
    }
    public static void die(String error) {
        System.out.println(error);
        System.exit(-1);
    }
    
}

