package com.app.server.utils.file;

import lombok.Value;

@Value
public class Range {
    private final long start;
    private final long end;

    public boolean isValid(long fileSize) {
        return start >= 0 && start < fileSize && start <= end;
    }

    public long getLength() {
        return end - start + 1;
    }

    public String toContentRangeHeader(long fileSize) {
        return String.format("bytes %d-%d/%d", start, end, fileSize);
    }
}