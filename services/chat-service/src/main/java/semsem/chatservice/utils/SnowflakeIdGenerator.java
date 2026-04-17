package semsem.chatservice.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Twitter-style Snowflake ID: 64 bits
 *
 * [ 1 sign ] [ 41 ms timestamp ] [ 5 datacenter ] [ 5 machine ] [ 12 sequence ]
 *
 * 32 datacenters × 32 machines = 1024 unique instances
 * 4096 IDs/ms/instance — good for ~69 years from custom epoch
 */
@Slf4j
public class SnowflakeIdGenerator {

    private static final int  DATACENTER_ID_BITS = 5;
    private static final int  MACHINE_ID_BITS    = 5;
    private static final int  SEQUENCE_BITS      = 12;

    public static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS); // 31
    public static final long MAX_MACHINE_ID    = ~(-1L << MACHINE_ID_BITS);    // 31
    private static final long SEQUENCE_MASK    = ~(-1L << SEQUENCE_BITS);       // 4095

    private static final int MACHINE_ID_SHIFT    = SEQUENCE_BITS;
    private static final int DATACENTER_ID_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;
    private static final int TIMESTAMP_SHIFT     = SEQUENCE_BITS + MACHINE_ID_BITS + DATACENTER_ID_BITS;

    private final long epoch;
    private final long datacenterId;
    private final long machineId;

    private long lastTimestamp = -1L;
    private long sequence      = 0L;

    public SnowflakeIdGenerator(long datacenterId, long machineId, long epoch) {
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID)
            throw new IllegalArgumentException("Datacenter ID must be 0–" + MAX_DATACENTER_ID + ", got: " + datacenterId);
        if (machineId < 0 || machineId > MAX_MACHINE_ID)
            throw new IllegalArgumentException("Machine ID must be 0–" + MAX_MACHINE_ID + ", got: " + machineId);

        this.epoch        = epoch;
        this.datacenterId = datacenterId;
        this.machineId    = machineId;
        log.info("SnowflakeIdGenerator ready: datacenterId={}, machineId={}", datacenterId, machineId);
    }

    public synchronized long nextId() {
        long now = currentTimeMillis();

        if (now < lastTimestamp) {
            log.warn("Clock moved backwards {}ms — waiting to recover", lastTimestamp - now);
            now = waitForNextMillis(lastTimestamp);
        }

        if (now == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                now = waitForNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = now;

        return ((now - epoch)    << TIMESTAMP_SHIFT)
                | (datacenterId  << DATACENTER_ID_SHIFT)
                | (machineId     << MACHINE_ID_SHIFT)
                | sequence;
    }

    public long extractTimestamp(long snowflakeId) {
        return (snowflakeId >> TIMESTAMP_SHIFT) + epoch;
    }

    private long waitForNextMillis(long lastMs) {
        long ms = currentTimeMillis();
        while (ms <= lastMs) ms = currentTimeMillis();
        return ms;
    }

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}