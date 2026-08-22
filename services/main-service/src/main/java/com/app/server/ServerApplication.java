package com.app.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.app.shared.security.client", "com.app.server.client"})
@EnableDiscoveryClient
public class ServerApplication {
	private static final Logger log = LoggerFactory.getLogger(ServerApplication.class);
	public static void main(String[] args) {
    	SpringApplication.run(ServerApplication.class, args);
	}

}
