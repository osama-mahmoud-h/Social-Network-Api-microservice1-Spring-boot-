package semsem.discovery;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryApplication {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(DiscoveryApplication.class);
    public static void main(String[] args) {
        log.info("Discovery service started");
        SpringApplication.run(DiscoveryApplication.class, args);
    }

}
