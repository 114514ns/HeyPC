package cn.pprocket;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    String className;
    public Logger(Object o) {
        className = o.getClass().getCanonicalName();
    }
    public Logger(String className) {
        this.className = className;
    }
    private String time() {
        LocalTime now = LocalTime.now();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");


        return now.format(formatter);

    }

    public void info(String message) {
        System.out.printf("[INFO] [%s] [%s] %s%n", time(), className, message);
    }
    public void error(String message) {
        System.out.printf("\033[31m"+ "[ERROR] [%s] [%s] %s%n", time(), className, message);
    }
}
