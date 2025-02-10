package com.app.server;

import com.app.server.dto.notification.NotificationEvent;
import com.app.server.enums.NotificationType;
import com.app.server.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
//@EnableDiscoveryClient
public class ServerApplication {
	private static final Logger log = LoggerFactory.getLogger(ServerApplication.class);
	public static void main(String[] args) {
    	SpringApplication.run(ServerApplication.class, args);
	}

}
