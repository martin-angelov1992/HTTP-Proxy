/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.martin.httpproxy;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ASUS
 */
public class ReadingUtil {
	static Logger logger = LoggerFactory.getLogger(ReadingUtil.class.getName());

    public void readHeaders(StringBuilder sb, InputStream in, Map<String, String> headers) {
        int c;
        StringBuilder line = new StringBuilder();
        String[] parts;
        try {
            while((c = in.read()) != -1) {
                line.append((char)c);
                if(line.toString().endsWith("\r\n")) {
                    parts = line.toString().split(": ", 2);
                    if(parts.length < 2) {
                        sb.append(line);
                        line.setLength(0);
                        if(sb.toString().endsWith("\r\n\r\n")) {
                            break;
                        }
                        continue;
                    }
                    String name = parts[0].trim();
                    String value = parts[1].trim();
                    if(name.equals("Connection")) { // Keep-alive not supported
                        line.setLength(0);
                        continue;
                    }
                    sb.append(line);
                    line.setLength(0);
                    headers.put(name, value);
                }
                if(sb.toString().endsWith("\r\n\r\n")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readHeaders(StringBuilder sb, Scanner in, Map<String, String> headers) {
        String line;
        String[] parts;
        try {
        	logger.debug("<reading headers>");
            while(true) {
                line = in.nextLine();
                parts = line.split(": ");
                if(parts[0].equals("Connection")) { // Keep-alive not supported
                    continue;
                }
                sb.append(line).append("\r\n");
                if(sb.toString().endsWith("\r\n\r\n")) {
                    break;
                }
                if(parts.length < 2) {
                    continue;
                }
                headers.put(parts[0], parts[1]);
            }
            logger.debug("<read headers>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readLineFromStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        int c;
        try {
            while(!sb.toString().endsWith("\r\n") && (c = in.read()) != -1) {
                sb.append((char)c);
            }
        } catch (Exception e) {
            
        }
        return sb.toString();
    }
}
