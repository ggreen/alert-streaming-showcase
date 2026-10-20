package io.cloudNativeData.showcase.streaming.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ActivitySourceApp {
    public static void main(String[] args) {
        SpringApplication.run(ActivitySourceApp.class,args);
    }
}
