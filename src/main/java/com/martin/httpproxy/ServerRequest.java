/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.martin.httpproxy;

import java.io.InputStream;
import java.net.Socket;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ASUS
 */
public class ServerRequest {
	static Logger logger = LoggerFactory.getLogger(ServerRequest.class.getName());

	@Inject
	private ReadingUtil readingUtil;
    private UserRequest userRequest;
    private Socket serverSocket;
    private char code;

    public ServerRequest(UserRequest userRequest) {
        setUserRequest(userRequest);
        this.serverSocket = userRequest.getServerSocket();
    }

    public UserRequest getUserRequest() {
        return userRequest;
    }

    public void setUserRequest(UserRequest userRequest) {
        this.userRequest = userRequest;
    }
    
    public ServerResponse send() {
    	Formatter out = null;
        try {
            if(serverSocket == null) {
                try {
                    serverSocket = new Socket(userRequest.getHost(), 80);
                } catch (Exception e) {
                    return null;
                }
            }
            out = new Formatter(serverSocket.getOutputStream());
            out.format("%s", userRequest.getRequestRaw());
            out.flush();
            InputStream in = serverSocket.getInputStream();
            HashMap<String, String> headers = new LinkedHashMap<>();
            String line = readingUtil.readLineFromStream(in);
            String parts[] = line.trim().split(" ");
            if(parts.length < 2) {
                return null;
            }

            try {
            	code = (char)Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
            	logger.error("Unable to parse code", e);
            }

            logger.debug("<Reading server headers>");
            StringBuilder sb = new StringBuilder();
            sb.append(line);
            readingUtil.readHeaders(sb, in, headers);
            if(code == 304) { // not modified
                return new ServerResponse(code, sb.toString(), headers, line);
            }
            if("chunked".equals(headers.get("Transfer-Encoding"))) {
                sb.append(readChunkedData(in));
            } else if(headers.get("Content-Length") != null) {
                sb.append(readData(in, Integer.parseInt(headers.get("Content-Length"))));
            } else {
                sb.append(readData(in));
            }
            logger.debug("<Returning server response>");
            return new ServerResponse(code ,sb.toString(), headers, line);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	if (out != null) {
        		out.close();
        	}
        }

        return null;
    }

    private String readData(InputStream in, int length) {
        StringBuilder sb = new StringBuilder();
        if(length == 0) { // Happened on youtube.com
            return sb.toString();
        }
        int chars = 0;
        int c = -2;
        try {
            logger.debug("<Reading lengthed data>");
            while(chars < length && (c = in.read()) != -1) {
                sb.append((char)c);
                ++chars;
            }
            if(c == -1) {
                logger.debug("<got -1>");
            }
            logger.debug("\n<Read lengthed data>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String readData(InputStream in) {
        StringBuilder sb = new StringBuilder();
        int c = -2;
        try {
            logger.debug("<reading data from server>");
            while((c = in.read()) != -1) {
                sb.append((char)c);
            }
            if(c == -1) {
                logger.debug("<got -1>");
            }
            logger.debug("<read data from server>");
        } catch(Exception e) {
            
        }
        return sb.toString();
    }

    private String readChunkedData(InputStream in) {
        StringBuilder sb = new StringBuilder();
        String line = readingUtil.readLineFromStream(in);
        sb.append(line);
        int charsLeft = Integer.parseInt(line.trim(), 16);
        try {
            logger.debug("<Reading chunked data>");
            int c = -2;
            while((c = in.read()) != -1) {
                sb.append((char)c);
                if(charsLeft == 0) {
                    line = readingUtil.readLineFromStream(in);
                    sb.append(line);
                    charsLeft = Integer.parseInt(line.trim(), 16);
                    if(charsLeft == 0) {
                        sb.append("\r\n");
                        break;
                    }
                } else {
                    --charsLeft;
                }        
            }
            if(c == -1) {
                logger.debug("<got -1>");
            }
            logger.debug("<Read chunked data>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}