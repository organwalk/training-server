package com.training.progress.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ToolConfig {
    public static String getTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
}
