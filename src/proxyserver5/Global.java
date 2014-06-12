/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proxyserver5;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author ASUS
 */
public class Global {
    public static boolean isDebug = false;
    public static boolean logInfo = true;
    public static final void readHeaders(StringBuilder sb, InputStream in, HashMap<String, String> headers) {
        int c;
        StringBuilder line = new StringBuilder();
        String[] parts;
        try {
            //System.out.println("<reading headers>");
            while((c = in.read()) != -1) {
                line.append((char)c);
                //System.out.print((char)c);
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
            //System.out.println("<read headers>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static final void readHeaders(StringBuilder sb, Scanner in, HashMap<String, String> headers) {
        String line;
        String[] parts;
        try {
            Logger.logDebug("<reading headers>");
            while(true) {
                line = in.nextLine();
                //System.out.println(line);
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
            Logger.logDebug("<read headers>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String readLineFromStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        int c;
        try {
            //System.out.println("<reading line>");
            while(!sb.toString().endsWith("\r\n") && (c = in.read()) != -1) {
                //System.out.print((char)c);
                sb.append((char)c);
            }
            //System.out.println("<read line>");
        } catch (Exception e) {
            
        }
        return sb.toString();
    }
}
