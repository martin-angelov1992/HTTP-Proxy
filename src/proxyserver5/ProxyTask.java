package proxyserver5;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

public class ProxyTask {
	static Logger logger = LoggerFactory.getLogger(ProxyTask.class.getName());
	private Injector injector;
    private static final int START_THREADS = 10;

    public ProxyTask(Injector injector) {
    	this.injector = injector;
    }

	public void start(int port) throws IOException {
        Map<String, Integer> requests = new HashMap<>();
        Runtime.getRuntime().addShutdownHook(new OnShutdown(requests));
        ExecutorService executorService = Executors.newFixedThreadPool(START_THREADS);
        ServerSocket sock = null;

        try {
            sock = new ServerSocket(port);
            while(true) {
                Socket socket = sock.accept();
                logger.info("Got connection");
                executorService.submit(new Connection(socket, requests, injector));
            }
        } catch (IOException e) {
            die(String.format("Port %d is already taken\n", port));
        } finally {
            sock.close();
            executorService.shutdownNow();
        }
	}

	public static void die(String error) {
		logger.error(error);
        System.exit(-1);
    }
}