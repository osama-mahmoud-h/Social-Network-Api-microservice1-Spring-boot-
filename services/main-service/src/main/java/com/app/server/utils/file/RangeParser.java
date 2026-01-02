package com.app.server.utils.file;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RangeParser {

    public static Range parse(String rangeHeader, long fileSize) {
        String range = rangeHeader.replace("bytes=", "").trim();
        if (range.isEmpty()) {
            throw new IllegalArgumentException("Empty range header");
        }

        String[] ranges = range.split("-", -1);
        long start;
        long end;

        if (ranges[0].isEmpty()) {
            // Suffix-range: "bytes=-500"
            if (ranges.length < 2 || ranges[1].isEmpty()) {
                throw new NumberFormatException("Invalid suffix-range format");
            }
            long suffix = Long.parseLong(ranges[1]);
            start = Math.max(0, fileSize - suffix);
            end = fileSize - 1;
        } else {
            // Regular range: "bytes=0-1023" or "bytes=1000-"
            start = Long.parseLong(ranges[0]);
            end = (ranges.length > 1 && !ranges[1].isEmpty())
                    ? Long.parseLong(ranges[1])
                    : fileSize - 1;
        }

        return new Range(start, end);
    }
}
