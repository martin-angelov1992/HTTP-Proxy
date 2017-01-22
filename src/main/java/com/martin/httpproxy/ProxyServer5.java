/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.martin.httpproxy;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.martin.httpproxy.cache.Evictor;

/**
 *
 * @author ASUS
 */
public class ProxyServer5 {
	static Logger logger = LoggerFactory.getLogger(ProxyServer5.class.getName());

    /**
     * @param args the command line arguments
     */
    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) throws IOException {
        int port = 0;
        logger.info("Starting...");
		Injector injector = Guice.createInjector(new ProxyModule());		
		ProxyTask task = new ProxyTask(injector);
        if(args.length == 0) {
            logger.info("Using default port: {}\n", DEFAULT_PORT);
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
		Evictor evictor = injector.getInstance(Evictor.class);
		evictor.start();
        task.start(port);
    }

    public static void die(String error) {
    	logger.error(error);
        System.exit(-1);
    }   
}