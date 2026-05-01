package semsem.chatservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;

/**
 * Assigns a unique machine ID (0–31) to this Chat Service instance
 * by creating an ephemeral sequential ZNode under:
 *   /chat/snowflake/{datacenterId}/workers/machine-
 *
 * The ZNode is automatically deleted when the instance dies,
 * freeing the machine ID for reuse.
 *
 * Falls back to a configured value if Zookeeper is unreachable.
 */
@Slf4j
public class ZookeeperMachineIdProvider implements AutoCloseable {

    private final CuratorFramework curator;
    private final String workerBasePath;
    private String assignedNodePath;
    private long machineId;

    public ZookeeperMachineIdProvider(String connectString, int sessionTimeoutMs,
                                      int connectionTimeoutMs, String workerBasePath) {
        this.workerBasePath = workerBasePath;
        this.curator = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connectionTimeoutMs)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
    }

    public long acquireMachineId(long datacenterId, long fallbackMachineId) {
        try {
            curator.start();
            curator.blockUntilConnected();

            String dcPath = workerBasePath + "/" + datacenterId;
            ensurePathExists(dcPath);

            assignedNodePath = curator.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(dcPath + "/machine-", new byte[0]);

            String sequenceStr = assignedNodePath.substring(assignedNodePath.lastIndexOf("machine-") + 8);
            long sequenceNumber = Long.parseLong(sequenceStr);
            machineId = sequenceNumber % (SnowflakeIdGenerator.MAX_MACHINE_ID + 1);

            log.info("Zookeeper assigned machineId={} (node={})", machineId, assignedNodePath);
            return machineId;

        } catch (Exception e) {
            log.warn("Zookeeper unavailable — falling back to configured machineId={}: {}", fallbackMachineId, e.getMessage());
            return fallbackMachineId;
        }
    }

    private void ensurePathExists(String path) throws Exception {
        if (curator.checkExists().forPath(path) == null) {
            curator.create().creatingParentsIfNeeded().forPath(path, new byte[0]);
        }
    }

    @Override
    public void close() {
        if (assignedNodePath != null) {
            try {
                curator.delete().forPath(assignedNodePath);
                log.info("Released Zookeeper machine ID node: {}", assignedNodePath);
            } catch (Exception e) {
                log.warn("Could not delete Zookeeper node on shutdown: {}", e.getMessage());
            }
        }
        curator.close();
    }
}