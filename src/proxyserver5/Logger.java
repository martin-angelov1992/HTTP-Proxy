/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxyserver5;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author ASUS
 */
public class Logger {
    private PrintWriter writer;
    private static final String DEFAULT_FILENAME = "log.txt";
    public Logger(String fileName) {
//        if(fileName == null) {
//            throw new IllegalArgumentException("File name can't be null.");
//        }
//        try {
//            writer = new PrintWriter(fileName);
//        } catch (FileNotFoundException e) {
//            
//        } finally {
//            finish();
//        }
    }
    public Logger() {
        this(DEFAULT_FILENAME);
    }
    public static void log(String text, Object... args) {
        System.out.printf(text, args);
    }
    public static void logError(String text, Object... args) {
        log(text+"\n", args);
        //writer.write(text);
    }
    public static void logInfo(String text, Object... args) {
        if(Global.logInfo) {
            log(text+"\n", args);
        }
    }
    public static void logDebug(char c) {
        logDebug(Character.toString(c));
    }
    public static void logDebug(String text, Object... args) {
        if(Global.isDebug) {
            log(text, args);
        }
    }
    public void finish() {
        writer.close();
    }
}
