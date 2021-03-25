package com.freetonleague.core;

import com.freetonleague.core.restclient.SessionCloudClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {SessionCloudClient.class})
public class LeagueCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeagueCoreApplication.class, args);
    }

}
