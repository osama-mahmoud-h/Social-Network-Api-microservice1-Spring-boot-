package semsem.apigetway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "gateway.rate-limiter")
public class RateLimiterProperties {

    private RouteLimit auth    = new RouteLimit(5,  TimeUnit.MINUTE, 10);
    private RouteLimit general = new RouteLimit(30, TimeUnit.SECOND, 60);
    private RouteLimit search  = new RouteLimit(15, TimeUnit.SECOND, 30);
    private RouteLimit chat    = new RouteLimit(20, TimeUnit.SECOND, 40);

    @Data
    public static class RouteLimit {
        private int rate;
        private TimeUnit per;
        private int burst;

        public RouteLimit() {}

        public RouteLimit(int rate, TimeUnit per, int burst) {
            this.rate  = rate;
            this.per   = per;
            this.burst = burst;
        }

        // tokens added per second
        public int replenishRate() {
            return rate;
        }

        // tokens each request costs = seconds in the chosen time unit
        public int requestedTokens() {
            return per.getSeconds();
        }

        // max tokens in bucket = burst requests worth of tokens
        public int burstCapacity() {
            return burst * per.getSeconds();
        }
    }

    public enum TimeUnit {
        SECOND(1),
        MINUTE(60),
        HOUR(3600),
        DAY(86400);

        private final int seconds;

        TimeUnit(int seconds) {
            this.seconds = seconds;
        }

        public int getSeconds() {
            return seconds;
        }
    }
}