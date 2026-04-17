package semsem.chatservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import semsem.chatservice.utils.SnowflakeIdGenerator;
import semsem.chatservice.utils.ZookeeperMachineIdProvider;

@Configuration
@Slf4j
public class SnowflakeConfig {

    @Value("${zookeeper.connect-string:localhost:2181}")
    private String zookeeperConnectString;

    @Value("${zookeeper.session-timeout-ms:5000}")
    private int sessionTimeoutMs;

    @Value("${zookeeper.connection-timeout-ms:3000}")
    private int connectionTimeoutMs;

    @Value("${zookeeper.snowflake.worker-path:/chat/snowflake/workers}")
    private String workerBasePath;

    @Value("${snowflake.epoch:1704067200000}")
    private long epoch;

    // Datacenter ID is set per deployment environment (0–31)
    @Value("${snowflake.datacenter-id:0}")
    private long datacenterId;

    // Fallback machine ID if Zookeeper is unreachable
    @Value("${snowflake.fallback-machine-id:0}")
    private long fallbackMachineId;

    @Bean(destroyMethod = "close")
    public ZookeeperMachineIdProvider zookeeperMachineIdProvider() {
        return new ZookeeperMachineIdProvider(
                zookeeperConnectString, sessionTimeoutMs, connectionTimeoutMs, workerBasePath);
    }

    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator(ZookeeperMachineIdProvider zookeeperMachineIdProvider) {
        long machineId = zookeeperMachineIdProvider.acquireMachineId(datacenterId, fallbackMachineId);
        log.info("Creating SnowflakeIdGenerator: datacenterId={}, machineId={}", datacenterId, machineId);
        return new SnowflakeIdGenerator(datacenterId, machineId, epoch);
    }
}