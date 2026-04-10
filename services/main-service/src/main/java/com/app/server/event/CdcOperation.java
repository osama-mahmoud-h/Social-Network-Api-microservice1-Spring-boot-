package com.app.server.event;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Debezium CDC operation codes produced by ExtractNewRecordState SMT (add.fields=op).
 */
public enum CdcOperation {
    INSERT,   // op = "c" (create)
    UPDATE,   // op = "u"
    DELETE,   // op = "d"
    SNAPSHOT; // op = "r" (initial snapshot read)

    @JsonCreator
    public static CdcOperation fromCode(String code) {
        return switch (code) {
            case "c" -> INSERT;
            case "u" -> UPDATE;
            case "d" -> DELETE;
            case "r" -> SNAPSHOT;
            default -> throw new IllegalArgumentException("Unknown CDC operation code: " + code);
        };
    }
}