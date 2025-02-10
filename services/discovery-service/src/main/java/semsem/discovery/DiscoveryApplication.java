package semsem.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryApplication {
    //log
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DiscoveryApplication.class);
    public static void main(String[] args) {
        log.info("Discovery service started");
        SpringApplication.run(DiscoveryApplication.class, args);
    }

}
